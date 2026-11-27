package com.velorise.teyvatbosses.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.velorise.teyvatbosses.TeyvatBossesConstants;
import com.velorise.teyvatbosses.client.model.CryoRegisvineAnimations;
import com.velorise.teyvatbosses.client.model.CryoRegisvineModel;
import com.velorise.teyvatbosses.entity.CryoTurretEntity;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.util.HashMap;
import java.util.Map;

/**
 * Renders the 3x6x3 miniature Cryo Regisvine Turret using {@link CryoRegisvineModel} scaled to 0.86x,
 * toggling Root / Stem / Corolla bone groups dynamically during strict 3-part assembly and
 * applying shortest-arc pose-snapshot cross-fading so transitions never snap.
 */
public class CryoTurretRenderer extends EntityRenderer<CryoTurretEntity> {
    private static final ResourceLocation TEXTURE = TeyvatBossesConstants.id("textures/entity/cryo_regisvine_boss.png");
    private static final float TURRET_SCALE = 0.86F;
    private static final float BLEND_TICKS = 10.0F;

    private static final String[] BLEND_BONES = {
            "root", "root_base", "root_core",
            "stem_1", "stem_2", "stem_3", "neck",
            "corolla_head", "corolla_core",
            "petal_ur", "petal_ul", "petal_lr", "petal_ll",
            "back_sepal_r", "back_sepal_l",
            "lash_r_base", "lash_r_mid", "lash_r_tip",
            "lash_l_base", "lash_l_mid", "lash_l_tip",
            "frost_ring"
    };

    private static final class BonePose {
        float x, y, z, xRot, yRot, zRot, xScale, yScale, zScale;
        void capture(ModelPart p) {
            x = p.x; y = p.y; z = p.z;
            xRot = p.xRot; yRot = p.yRot; zRot = p.zRot;
            xScale = p.xScale; yScale = p.yScale; zScale = p.zScale;
        }
        void copyFrom(BonePose o) {
            x = o.x; y = o.y; z = o.z;
            xRot = o.xRot; yRot = o.yRot; zRot = o.zRot;
            xScale = o.xScale; yScale = o.yScale; zScale = o.zScale;
        }
    }

    private static final class SmoothState {
        String lastClip = null;
        int lastEpoch = -1;
        float blendStartTick = -100.0F;
        boolean hasLastPose = false;
        final Map<String, BonePose> fromPose = new HashMap<>();
        final Map<String, BonePose> lastPose = new HashMap<>();
    }

    private final CryoRegisvineModel<CryoTurretEntity> model;
    private final Map<Integer, SmoothState> smoothStates = new HashMap<>();
    private final PartInstallAnimator installAnimator = new PartInstallAnimator();

    public CryoTurretRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 1.25F;
        this.model = new CryoRegisvineModel<>(context.bakeLayer(CryoRegisvineModel.LAYER_LOCATION));
    }

    private static float lerpAngleRad(float from, float to, float alpha) {
        float diff = (to - from) % Mth.TWO_PI;
        if (diff > Mth.PI) diff -= Mth.TWO_PI;
        if (diff < -Mth.PI) diff += Mth.TWO_PI;
        return from + diff * alpha;
    }

    @Override
    public void render(CryoTurretEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        float rootYaw = entity.getRootYaw();
        float stemWorldYaw = Mth.rotLerp(partialTicks, entity.yBodyRotO, entity.yBodyRot);
        float stemDeltaYawRad = Mth.wrapDegrees(stemWorldYaw - rootYaw) * Mth.DEG_TO_RAD;
        float liveTick = entity.tickCount + partialTicks;
        float sec = (entity.getAnimTick() + partialTicks) / 20.0F;
        String currClip = entity.getBaseClipName();
        int currEpoch = entity.getAnimEpoch();

        SmoothState sm = smoothStates.computeIfAbsent(entity.getId(), k -> new SmoothState());
        if (sm.lastClip != null && (!sm.lastClip.equals(currClip) || sm.lastEpoch != currEpoch) && sm.hasLastPose) {
            for (Map.Entry<String, BonePose> e : sm.lastPose.entrySet()) {
                sm.fromPose.computeIfAbsent(e.getKey(), k -> new BonePose()).copyFrom(e.getValue());
            }
            sm.blendStartTick = liveTick;
        }
        sm.lastClip = currClip;
        sm.lastEpoch = currEpoch;

        poseStack.pushPose();
        // Root base stays permanently anchored at spawn rootYaw; only stem_1 rotates to track targets
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - rootYaw));
        poseStack.scale(-TURRET_SCALE, -TURRET_SCALE, TURRET_SCALE);
        poseStack.translate(0.0D, -1.501D, 0.0D);

        CryoRegisvineAnimations.apply(model, currClip, sec);

        ModelPart stem1 = model.getBone("stem_1");
        if (stem1 != null) {
            stem1.yRot += stemDeltaYawRad;
        }

        // Shortest-arc 10-tick pose-snapshot cross-fade so turret animations never snap on reset
        float elapsed = liveTick - sm.blendStartTick;
        if (sm.hasLastPose && elapsed >= 0.0F && elapsed < BLEND_TICKS) {
            float rawA = Mth.clamp(elapsed / BLEND_TICKS, 0.0F, 1.0F);
            float alpha = rawA * rawA * (3.0F - 2.0F * rawA);
            for (String bName : BLEND_BONES) {
                ModelPart part = model.getBone(bName);
                BonePose prev = sm.fromPose.get(bName);
                if (part == null || prev == null) continue;
                part.x = Mth.lerp(alpha, prev.x, part.x);
                part.y = Mth.lerp(alpha, prev.y, part.y);
                part.z = Mth.lerp(alpha, prev.z, part.z);
                part.xRot = lerpAngleRad(prev.xRot, part.xRot, alpha);
                part.yRot = lerpAngleRad(prev.yRot, part.yRot, alpha);
                part.zRot = lerpAngleRad(prev.zRot, part.zRot, alpha);
                part.xScale = Mth.lerp(alpha, prev.xScale, part.xScale);
                part.yScale = Mth.lerp(alpha, prev.yScale, part.yScale);
                part.zScale = Mth.lerp(alpha, prev.zScale, part.zScale);
            }
        }
        for (String bName : BLEND_BONES) {
            ModelPart part = model.getBone(bName);
            if (part != null) sm.lastPose.computeIfAbsent(bName, k -> new BonePose()).capture(part);
        }
        sm.hasLastPose = true;

        // Toggle 3 modular assembly bone groups (Root -> Stem -> Corolla)
        boolean hasStem = entity.hasPart(CryoTurretEntity.PART_STEM);
        boolean hasCorolla = entity.hasPart(CryoTurretEntity.PART_COROLLA);
        setBoneVisible("stem_1", hasStem);
        setBoneVisible("corolla_head", hasCorolla);

        installAnimator.update(entity, entity.getPartsMask(), partialTicks);
        if (hasStem) PartInstallAnimator.applyPush(model.getBone("stem_1"), installAnimator.getRemaining(entity, CryoTurretEntity.PART_STEM, partialTicks), 0.0F, -10.0F, 0.0F);
        if (hasCorolla) PartInstallAnimator.applyPush(model.getBone("corolla_head"), installAnimator.getRemaining(entity, CryoTurretEntity.PART_COROLLA, partialTicks), 0.0F, -9.0F, -1.5F);
        setBoneVisible("frost_ring", entity.getAnimId() == CryoTurretEntity.ANIM_FROST_NOVA);
        setBoneVisible("lash_beam_l", false);
        setBoneVisible("lash_beam_r", false);
        setBoneVisible("rotary_beam", false);

        int overlay = OverlayTexture.pack(OverlayTexture.u(0.0F), OverlayTexture.v(entity.hurtTime > 0 || entity.deathTime > 0));
        VertexConsumer vc = bufferSource.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        model.renderToBuffer(poseStack, vc, packedLight, overlay, 0xFFFFFFFF);

        // Restore visibility flags
        setBoneVisible("stem_1", true);
        setBoneVisible("corolla_head", true);
        setBoneVisible("frost_ring", true);
        setBoneVisible("lash_beam_l", true);
        setBoneVisible("lash_beam_r", true);
        setBoneVisible("rotary_beam", true);

        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
    }

    private void setBoneVisible(String name, boolean vis) {
        ModelPart part = model.getBone(name);
        if (part != null) part.visible = vis;
    }

    @Override
    public ResourceLocation getTextureLocation(CryoTurretEntity entity) {
        return TEXTURE;
    }
}

package com.velorise.teyvatbosses.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.velorise.teyvatbosses.TeyvatBossesConstants;
import com.velorise.teyvatbosses.client.model.RuinNautilusAnimations;
import com.velorise.teyvatbosses.client.model.RuinNautilusModel;
import com.velorise.teyvatbosses.entity.RuinNautilusEntity;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

import java.util.HashMap;
import java.util.Map;

/**
 * Renders {@link RuinNautilusEntity} (scale 1.45x) with 26-bone shortest-arc pose cross-fading,
 * glowing Hydro emissive eye-core layer, synced 6-torpedo warheads, 3D opaque Abyssal Hydro Beam VFX,
 * and combat-only boss music playback.
 */
public class RuinNautilusRenderer extends EntityRenderer<RuinNautilusEntity> {
    private static final ResourceLocation TEXTURE = TeyvatBossesConstants.id("textures/entity/ruin_nautilus_boss.png");
    private static final ResourceLocation EMISSIVE = TeyvatBossesConstants.id("textures/entity/ruin_nautilus_boss_emissive.png");
    private static final ResourceLocation MUSIC_ID = TeyvatBossesConstants.id("music.ruin_nautilus_theme");
    private static final float BOSS_SCALE = 2.35F;
    private static final float BLEND_TICKS = 8.0F;

    private static final class BonePose {
        float x, y, z, xRot, yRot, zRot;
        void capture(ModelPart p) { x = p.x; y = p.y; z = p.z; xRot = p.xRot; yRot = p.yRot; zRot = p.zRot; }
        void copyFrom(BonePose o) { x = o.x; y = o.y; z = o.z; xRot = o.xRot; yRot = o.yRot; zRot = o.zRot; }
    }

    private static final class SmoothState {
        String lastClip = null;
        int lastEpoch = -1;
        float blendStart = -100.0F;
        boolean hasLast = false;
        final Map<String, BonePose> from = new HashMap<>();
        final Map<String, BonePose> last = new HashMap<>();
    }

    private final RuinNautilusModel<RuinNautilusEntity> model;
    private final Map<Integer, SmoothState> smoothMap = new HashMap<>();

    public RuinNautilusRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
        this.shadowRadius = 3.0F;
        this.model = new RuinNautilusModel<>(ctx.bakeLayer(RuinNautilusModel.LAYER_LOCATION));
    }

    private static float lerpAngle(float from, float to, float a) {
        float d = (to - from) % Mth.TWO_PI;
        if (d > Mth.PI) d -= Mth.TWO_PI;
        if (d < -Mth.PI) d += Mth.TWO_PI;
        return from + d * a;
    }

    @Override
    public void render(RuinNautilusEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int packedLight) {
        float yaw = Mth.rotLerp(partialTicks, entity.yBodyRotO, entity.yBodyRot);
        float liveTick = entity.tickCount + partialTicks;
        float sec = (entity.getAnimTick() + partialTicks) / 20.0F;
        String clip = entity.getCurrentClipName();
        int epoch = entity.getAnimEpoch();

        boolean musicActive = entity.isAlive()
                && (entity.getBossState() == RuinNautilusEntity.STATE_COMBAT || entity.getBossState() == RuinNautilusEntity.STATE_STUNNED);
        BossMusicManager.update(entity, MUSIC_ID, musicActive);

        SmoothState sm = smoothMap.computeIfAbsent(entity.getId(), k -> new SmoothState());
        if (sm.lastClip != null && (!sm.lastClip.equals(clip) || sm.lastEpoch != epoch) && sm.hasLast) {
            for (Map.Entry<String, BonePose> e : sm.last.entrySet()) {
                sm.from.computeIfAbsent(e.getKey(), k -> new BonePose()).copyFrom(e.getValue());
            }
            sm.blendStart = liveTick;
        }
        sm.lastClip = clip;
        sm.lastEpoch = epoch;

        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - yaw));
        poseStack.scale(-BOSS_SCALE, -BOSS_SCALE, BOSS_SCALE);
        poseStack.translate(0.0D, -1.501D, 0.0D);

        RuinNautilusAnimations.apply(model, clip, sec);
        ModelPart hullPitch = model.getBone("hull_pitch");
        if (hullPitch != null) {
            hullPitch.xRot += entity.getHullPitchDeg() * Mth.DEG_TO_RAD;
        }

        // Smooth shortest-arc 8-tick cross-fade
        float elapsed = liveTick - sm.blendStart;
        if (sm.hasLast && elapsed >= 0.0F && elapsed < BLEND_TICKS) {
            float raw = Mth.clamp(elapsed / BLEND_TICKS, 0.0F, 1.0F);
            float a = raw * raw * (3.0F - 2.0F * raw);
            for (String b : RuinNautilusModel.ALL_BONES) {
                ModelPart p = model.getBone(b);
                BonePose prev = sm.from.get(b);
                if (p == null || prev == null) continue;
                p.x = Mth.lerp(a, prev.x, p.x);
                p.y = Mth.lerp(a, prev.y, p.y);
                p.z = Mth.lerp(a, prev.z, p.z);
                p.xRot = lerpAngle(prev.xRot, p.xRot, a);
                p.yRot = lerpAngle(prev.yRot, p.yRot, a);
                p.zRot = lerpAngle(prev.zRot, p.zRot, a);
            }
        }
        for (String b : RuinNautilusModel.ALL_BONES) {
            ModelPart p = model.getBone(b);
            if (p != null) sm.last.computeIfAbsent(b, k -> new BonePose()).capture(p);
        }
        sm.hasLast = true;

        // Sync 6 dynamic torpedo tips with fired mask
        int firedMask = entity.getTorpedoFiredMask();
        for (int i = 0; i < 6; i++) {
            ModelPart tip = model.getBone("torpedo_tip_" + i);
            if (tip != null) tip.visible = ((firedMask & (1 << i)) == 0);
        }

        int overlay = OverlayTexture.pack(OverlayTexture.u(0.0F), OverlayTexture.v(entity.hurtTime > 0 || entity.deathTime > 0));
        VertexConsumer baseVc = buffers.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        model.renderToBuffer(poseStack, baseVc, packedLight, overlay, 0xFFFFFFFF);

        // Emissive Hydro eye-core & torpedo glow (dimmed when dormant)
        if (entity.getBossState() != RuinNautilusEntity.STATE_DORMANT) {
            VertexConsumer emVc = buffers.getBuffer(RenderType.eyes(EMISSIVE));
            model.renderToBuffer(poseStack, emVc, 0x00F000F0, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);
        }

        for (int i = 0; i < 6; i++) {
            ModelPart tip = model.getBone("torpedo_tip_" + i);
            if (tip != null) tip.visible = true;
        }
        poseStack.popPose();

        // 3D Sweeping Abyssal Hydro Beam VFX during ANIM_BEAM (t=24..58)
        if (entity.getAnimId() == RuinNautilusEntity.ANIM_BEAM) {
            float t = entity.getAnimTick() + partialTicks;
            if (t >= 24.0F && t <= 58.0F) {
                float prog = Mth.clamp((t - 24.0F) / 34.0F, 0.0F, 1.0F);
                float beamYaw = yaw + Mth.lerp(prog, -45.0F, 45.0F);
                renderHydroBeam(poseStack, buffers, beamYaw, entity.getHullPitchDeg());
            }
        }

        super.render(entity, entityYaw, partialTicks, poseStack, buffers, packedLight);
    }

    private static void renderHydroBeam(PoseStack poseStack, MultiBufferSource buffers, float yawDeg, float pitchDeg) {
        poseStack.pushPose();
        poseStack.translate(0.0D, 1.6D, 0.0D);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - yawDeg));
        poseStack.mulPose(Axis.XP.rotationDegrees(pitchDeg));
        Matrix4f m = poseStack.last().pose();
        VertexConsumer vc = buffers.getBuffer(RenderType.lightning());
        float r = 0.28F;
        float len = 18.0F;
        // Crossed opaque cyan-white energy ribbons
        vc.addVertex(m, -r, 0, -2.0F).setColor(80, 240, 255, 220);
        vc.addVertex(m,  r, 0, -2.0F).setColor(80, 240, 255, 220);
        vc.addVertex(m,  r, 0, -2.0F - len).setColor(200, 255, 255, 190);
        vc.addVertex(m, -r, 0, -2.0F - len).setColor(200, 255, 255, 190);

        vc.addVertex(m, 0, -r, -2.0F).setColor(80, 240, 255, 220);
        vc.addVertex(m, 0,  r, -2.0F).setColor(80, 240, 255, 220);
        vc.addVertex(m, 0,  r, -2.0F - len).setColor(200, 255, 255, 190);
        vc.addVertex(m, 0, -r, -2.0F - len).setColor(200, 255, 255, 190);
        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(RuinNautilusEntity entity) {
        return TEXTURE;
    }
}

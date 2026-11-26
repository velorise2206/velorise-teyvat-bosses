package com.velorise.teyvatbosses.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.velorise.teyvatbosses.TeyvatBossesConstants;
import com.velorise.teyvatbosses.client.model.RuinNautilusModel;
import com.velorise.teyvatbosses.entity.RuinSubmersibleEntity;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/**
 * Renders the 4-part player-piloted {@link RuinSubmersibleEntity} (scale 0.88x) using {@link RuinNautilusModel},
 * toggling Hull / Cockpit / Propeller / Torpedo Pods during assembly and syncing all 6 torpedo tips live.
 */
public class RuinSubmersibleRenderer extends EntityRenderer<RuinSubmersibleEntity> {
    private static final ResourceLocation TEXTURE = TeyvatBossesConstants.id("textures/entity/ruin_nautilus_boss.png");
    private static final ResourceLocation EMISSIVE = TeyvatBossesConstants.id("textures/entity/ruin_nautilus_boss_emissive.png");
    private static final float SUB_SCALE = 1.65F;

    private final RuinNautilusModel<RuinSubmersibleEntity> model;

    public RuinSubmersibleRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
        this.shadowRadius = 2.1F;
        this.model = new RuinNautilusModel<>(ctx.bakeLayer(RuinNautilusModel.LAYER_LOCATION));
    }

    @Override
    public void render(RuinSubmersibleEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int packedLight) {
        float yaw = Mth.rotLerp(partialTicks, entity.yBodyRotO, entity.yBodyRot);

        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - yaw));
        poseStack.scale(-SUB_SCALE, -SUB_SCALE, SUB_SCALE);
        poseStack.translate(0.0D, -1.501D, 0.0D);

        model.resetPose();
        ModelPart hullPitch = model.getBone("hull_pitch");
        if (hullPitch != null) {
            hullPitch.xRot += entity.getHullPitchDeg() * Mth.DEG_TO_RAD;
            hullPitch.zRot += entity.getHullRollDeg() * Mth.DEG_TO_RAD;
        }
        ModelPart prop = model.getBone("propeller_shaft");
        if (prop != null) {
            prop.zRot += entity.getPropSpinAngle();
        }
        ModelPart iris = model.getBone("eye_iris_ring");
        if (iris != null) {
            iris.zRot += (entity.tickCount + partialTicks) * 0.05F;
        }

        // Toggle 4 modular assembly bone groups
        setVis("bow_dome", entity.hasPart(RuinSubmersibleEntity.PART_COCKPIT));
        setVis("tail_cone", entity.hasPart(RuinSubmersibleEntity.PART_PROPELLER));
        boolean hasPods = entity.hasPart(RuinSubmersibleEntity.PART_TORPEDO_POD);
        setVis("pod_left", hasPods);
        setVis("pod_right", hasPods);

        int ammo = entity.getTorpedoAmmo();
        for (int i = 0; i < 6; i++) {
            setVis("torpedo_tip_" + i, hasPods && i < ammo);
        }

        int overlay = OverlayTexture.pack(OverlayTexture.u(0.0F), OverlayTexture.v(entity.hurtTime > 0 || entity.deathTime > 0));
        VertexConsumer vc = buffers.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        model.renderToBuffer(poseStack, vc, packedLight, overlay, 0xFFFFFFFF);

        if (entity.isFullyAssembled() && entity.getEnergy() > 0.5F) {
            VertexConsumer emVc = buffers.getBuffer(RenderType.eyes(EMISSIVE));
            model.renderToBuffer(poseStack, emVc, 0x00F000F0, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);
        }

        // Restore visibility flags
        setVis("bow_dome", true);
        setVis("tail_cone", true);
        setVis("pod_left", true);
        setVis("pod_right", true);
        for (int i = 0; i < 6; i++) setVis("torpedo_tip_" + i, true);

        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffers, packedLight);
    }

    private void setVis(String bone, boolean v) {
        ModelPart p = model.getBone(bone);
        if (p != null) p.visible = v;
    }

    @Override
    public ResourceLocation getTextureLocation(RuinSubmersibleEntity entity) {
        return TEXTURE;
    }
}

package com.velorise.teyvatbosses.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.velorise.teyvatbosses.TeyvatBossesConstants;
import com.velorise.teyvatbosses.client.model.RuinNautilusModel;
import com.velorise.teyvatbosses.entity.RuinSubmersibleEntity;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/**
 * Renders the 4-part player-piloted {@link RuinSubmersibleEntity} with an Alex's Caves-style
 * panoramic crystal glass canopy dome ({@code sub_cockpit_glass}) rendered via {@link RenderType#entityTranslucent}.
 */
public class RuinSubmersibleRenderer extends EntityRenderer<RuinSubmersibleEntity> {
    private static final ResourceLocation TEXTURE = TeyvatBossesConstants.id("textures/entity/ruin_nautilus_boss.png");
    private static final ResourceLocation EMISSIVE = TeyvatBossesConstants.id("textures/entity/ruin_nautilus_boss_emissive.png");
    private static final ResourceLocation GLASS_TEX = TeyvatBossesConstants.id("textures/entity/ruin_submersible_glass.png");
    private static final float SUB_SCALE = 1.65F;

    private final RuinNautilusModel<RuinSubmersibleEntity> model;
    private final PartInstallAnimator installAnimator = new PartInstallAnimator();

    public RuinSubmersibleRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
        this.shadowRadius = 2.2F;
        this.model = new RuinNautilusModel<>(ctx.bakeLayer(RuinNautilusModel.LAYER_LOCATION));
    }

    @Override
    public void render(RuinSubmersibleEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int packedLight) {
        Minecraft mc = Minecraft.getInstance();
        boolean isLocalPilotFirstPerson = mc.player != null
                && entity.hasPassenger(mc.player)
                && mc.options.getCameraType() == CameraType.FIRST_PERSON;

        // 1st-person uses the clean 2D Fontaine brass/glass canopy visor overlay to avoid water Z-fighting
        if (isLocalPilotFirstPerson) {
            return;
        }

        float yaw = Mth.rotLerp(partialTicks, entity.yBodyRotO, entity.yBodyRot);
        float pitchDeg = entity.getHullPitchDeg(partialTicks);
        float rollDeg = entity.getHullRollDeg(partialTicks);
        float spin = entity.getPropSpinAngle(partialTicks);
        float rudderRad = entity.getRudderDeg(partialTicks) * Mth.DEG_TO_RAD;
        float finPitchRad = entity.getFinPitchDeg(partialTicks) * Mth.DEG_TO_RAD;

        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - yaw));
        poseStack.scale(-SUB_SCALE, -SUB_SCALE, SUB_SCALE);
        poseStack.translate(0.0D, -1.501D, 0.0D);

        model.resetPose();
        ModelPart hullPitch = model.getBone("hull_pitch");
        if (hullPitch != null) {
            hullPitch.xRot += pitchDeg * Mth.DEG_TO_RAD;
            hullPitch.zRot += rollDeg * Mth.DEG_TO_RAD;
        }

        ModelPart prop = model.getBone("propeller_shaft");
        if (prop != null) prop.zRot += spin;
        ModelPart gearL = model.getBone("clockwork_gear_l");
        ModelPart gearR = model.getBone("clockwork_gear_r");
        ModelPart bellyGear = model.getBone("belly_gear");
        ModelPart bellyGear2 = model.getBone("belly_gear_2");
        if (gearL != null) gearL.xRot += spin * 0.45F;
        if (gearR != null) gearR.xRot -= spin * 0.45F;
        if (bellyGear != null) bellyGear.xRot += spin * 0.35F;
        if (bellyGear2 != null) bellyGear2.xRot -= spin * 0.50F;

        ModelPart rudderTop = model.getBone("rudder_top");
        ModelPart rudderBot = model.getBone("rudder_bot");
        if (rudderTop != null) rudderTop.yRot += rudderRad;
        if (rudderBot != null) rudderBot.yRot += rudderRad;
        ModelPart finL = model.getBone("fin_pectoral_l");
        ModelPart finR = model.getBone("fin_pectoral_r");
        if (finL != null) finL.xRot += finPitchRad;
        if (finR != null) finR.xRot += finPitchRad;

        ModelPart leverL = model.getBone("control_lever_l");
        ModelPart leverR = model.getBone("control_lever_r");
        float pitchRad = pitchDeg * Mth.DEG_TO_RAD;
        float rollRad = rollDeg * Mth.DEG_TO_RAD;
        if (leverL != null) leverL.xRot += Mth.clamp(pitchRad * 0.85F - rollRad * 0.65F, -0.55F, 0.55F);
        if (leverR != null) leverR.xRot += Mth.clamp(pitchRad * 0.85F + rollRad * 0.65F, -0.55F, 0.55F);

        setVis("drowned_pilot", false);
        setVis("bow_dome", false);
        setVis("dorsal_railgun_base", false);
        setVis("harpoon_arm_l", false);
        setVis("harpoon_arm_r", false);

        boolean hasCockpit = entity.hasPart(RuinSubmersibleEntity.PART_COCKPIT);
        boolean hasProp = entity.hasPart(RuinSubmersibleEntity.PART_PROPELLER);
        boolean hasPods = entity.hasPart(RuinSubmersibleEntity.PART_TORPEDO_POD);

        installAnimator.update(entity, entity.getPartsMask(), partialTicks);
        float cockpitRem = hasCockpit ? installAnimator.getRemaining(entity, RuinSubmersibleEntity.PART_COCKPIT, partialTicks) : 0.0F;
        float propRem = hasProp ? installAnimator.getRemaining(entity, RuinSubmersibleEntity.PART_PROPELLER, partialTicks) : 0.0F;
        float podRem = hasPods ? installAnimator.getRemaining(entity, RuinSubmersibleEntity.PART_TORPEDO_POD, partialTicks) : 0.0F;

        PartInstallAnimator.applyPush(model.getBone("sub_cockpit_frame"), cockpitRem, 0.0F, 0.0F, -8.5F);
        PartInstallAnimator.applyPush(model.getBone("sub_cockpit_glass"), cockpitRem, 0.0F, 0.0F, -8.5F);
        PartInstallAnimator.applyPush(model.getBone("tail_cone"), propRem, 0.0F, 0.0F, 9.5F);
        if (prop != null && propRem > 0.0001F) prop.zRot += propRem * 0.80F;
        PartInstallAnimator.applyPush(model.getBone("pod_left"), podRem, -7.5F, 0.0F, 0.0F);
        PartInstallAnimator.applyPush(model.getBone("pod_right"), podRem, 7.5F, 0.0F, 0.0F);

        // Pass 1: Opaque metal hull + brass frame
        setVis("sub_cockpit_frame", hasCockpit);
        setVis("sub_cockpit_glass", false);
        setVis("tail_cone", hasProp);
        setVis("pod_left", hasPods);
        setVis("pod_right", hasPods);

        int ammo = entity.getTorpedoAmmo();
        for (int i = 0; i < 6; i++) {
            setVis("torpedo_tip_" + i, hasPods && i < ammo);
        }

        int overlay = OverlayTexture.pack(OverlayTexture.u(0.0F), OverlayTexture.v(entity.hurtTime > 0 || entity.deathTime > 0));
        VertexConsumer vc = buffers.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        model.renderToBuffer(poseStack, vc, packedLight, overlay, 0xFFFFFFFF);

        // Pass 2: Emissive glow with slight 1.003x scale bias to completely eliminate depth-buffer Z-fighting,
        // and visibly dims when Headlight [F] is OFF!
        if (entity.isFullyAssembled() && entity.getEnergy() > 0.5F && entity.isHeadlightOn()) {
            poseStack.pushPose();
            poseStack.scale(1.003F, 1.003F, 1.003F);
            VertexConsumer emVc = buffers.getBuffer(RenderType.eyes(EMISSIVE));
            model.renderToBuffer(poseStack, emVc, 0x00F000F0, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);
            poseStack.popPose();
        }

        // Pass 3: Panoramic Translucent Crystal Glass Canopy Dome (3rd-person)
        if (hasCockpit) {
            ModelPart glassBone = model.getBone("sub_cockpit_glass");
            if (glassBone != null && hullPitch != null) {
                poseStack.pushPose();
                ModelPart rootPart = model.getBone("root");
                if (rootPart != null) rootPart.translateAndRotate(poseStack);
                hullPitch.translateAndRotate(poseStack);
                ModelPart hullMain = model.getBone("hull_main");
                if (hullMain != null) hullMain.translateAndRotate(poseStack);
                glassBone.visible = true;
                VertexConsumer glassVc = buffers.getBuffer(RenderType.entityTranslucent(GLASS_TEX));
                glassBone.render(poseStack, glassVc, packedLight, OverlayTexture.NO_OVERLAY, 0xCCFFFFFF);
                poseStack.popPose();
            }
        }

        setVis("drowned_pilot", true);
        setVis("bow_dome", true);
        setVis("dorsal_railgun_base", true);
        setVis("harpoon_arm_l", true);
        setVis("harpoon_arm_r", true);
        setVis("sub_cockpit_frame", true);
        setVis("sub_cockpit_glass", true);
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

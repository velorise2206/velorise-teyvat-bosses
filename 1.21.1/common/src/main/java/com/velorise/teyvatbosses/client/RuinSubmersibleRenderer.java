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
 *
 * <p>In 3rd-person, the player is clearly visible seated inside the illuminated glass dome.
 * In 1st-person for the local pilot, opaque rear hull cubes are hidden so the view through the
 * crystal glass canopy is 100% unobstructed.
 */
public class RuinSubmersibleRenderer extends EntityRenderer<RuinSubmersibleEntity> {
    private static final ResourceLocation TEXTURE = TeyvatBossesConstants.id("textures/entity/ruin_nautilus_boss.png");
    private static final ResourceLocation EMISSIVE = TeyvatBossesConstants.id("textures/entity/ruin_nautilus_boss_emissive.png");
    private static final ResourceLocation GLASS_TEX = TeyvatBossesConstants.id("textures/entity/ruin_submersible_glass.png");
    private static final float SUB_SCALE = 1.65F;

    private final RuinNautilusModel<RuinSubmersibleEntity> model;

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
        float spin = entity.getPropSpinAngle();
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

        // Animate twin cockpit control levers live with pilot pitch/roll
        ModelPart leverL = model.getBone("control_lever_l");
        ModelPart leverR = model.getBone("control_lever_r");
        float pitchRad = entity.getHullPitchDeg() * Mth.DEG_TO_RAD;
        float rollRad = entity.getHullRollDeg() * Mth.DEG_TO_RAD;
        if (leverL != null) leverL.xRot += Mth.clamp(pitchRad * 0.85F - rollRad * 0.65F, -0.55F, 0.55F);
        if (leverR != null) leverR.xRot += Mth.clamp(pitchRad * 0.85F + rollRad * 0.65F, -0.55F, 0.55F);

        // Always hide Boss-only weapons/nose & Drowned Captain on the player submarine
        setVis("drowned_pilot", false);
        setVis("bow_dome", false);
        setVis("dorsal_railgun_base", false);
        setVis("harpoon_arm_l", false);
        setVis("harpoon_arm_r", false);

        boolean hasCockpit = entity.hasPart(RuinSubmersibleEntity.PART_COCKPIT);
        boolean hasProp = entity.hasPart(RuinSubmersibleEntity.PART_PROPELLER);
        boolean hasPods = entity.hasPart(RuinSubmersibleEntity.PART_TORPEDO_POD);

        // Pass 1 & 2: Opaque metal hull + brass frame + emissive glow
        // In 1st-person for the local pilot, skip opaque body so ZERO opaque walls block the camera!
        if (!isLocalPilotFirstPerson) {
            setVis("sub_cockpit_frame", hasCockpit);
            setVis("sub_cockpit_glass", false); // Rendered in Pass 3 translucent pass
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

            if (entity.isFullyAssembled() && entity.getEnergy() > 0.5F) {
                VertexConsumer emVc = buffers.getBuffer(RenderType.eyes(EMISSIVE));
                model.renderToBuffer(poseStack, emVc, 0x00F000F0, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);
            }
        }

        // Pass 3: Alex's Caves-style Panoramic Translucent Crystal Glass Canopy Dome!
        if (hasCockpit) {
            // Render ONLY sub_cockpit_glass with entityTranslucent so the pilot is visible inside
            // and 1st-person sees cleanly through the subtle crystal-tinted canopy!
            ModelPart glassBone = model.getBone("sub_cockpit_glass");
            if (glassBone != null && hullPitch != null) {
                poseStack.pushPose();
                ModelPart rootPart = model.getBone("root");
                if (rootPart != null) rootPart.translateAndRotate(poseStack);
                hullPitch.translateAndRotate(poseStack);
                ModelPart hullMain = model.getBone("hull_main");
                if (hullMain != null) hullMain.translateAndRotate(poseStack);
                glassBone.visible = true;
                int glassAlpha = isLocalPilotFirstPerson ? 0x66FFFFFF : 0xCCFFFFFF;
                VertexConsumer glassVc = buffers.getBuffer(RenderType.entityTranslucent(GLASS_TEX));
                glassBone.render(poseStack, glassVc, packedLight, OverlayTexture.NO_OVERLAY, glassAlpha);
                poseStack.popPose();
            }
        }

        // Restore all visibility flags cleanly for shared model safety
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

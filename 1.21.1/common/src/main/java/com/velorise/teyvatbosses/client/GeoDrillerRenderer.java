package com.velorise.teyvatbosses.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.velorise.teyvatbosses.TeyvatBossesConstants;
import com.velorise.teyvatbosses.client.model.GeoDrillerModel;
import com.velorise.teyvatbosses.entity.GeoDrillerEntity;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/**
 * Renders {@link GeoDrillerEntity} with smooth partial-tick interpolated procedural animations:
 * spinning drill head, true-travel wheel &amp; tread rotation, dual independent drive/drill levers,
 * engine spool-up shudder, and a true merged Vanilla Single / Double Chest on the rear deck.
 */
public class GeoDrillerRenderer extends EntityRenderer<GeoDrillerEntity> {
    private static final ResourceLocation TEXTURE = TeyvatBossesConstants.id("textures/entity/geo_driller.png");
    private static final ResourceLocation EMISSIVE = TeyvatBossesConstants.id("textures/entity/geo_driller_emissive.png");

    private final GeoDrillerModel<GeoDrillerEntity> model;
    private final ModelPart singleChestModel;
    private final ModelPart doubleChestLeftModel;
    private final ModelPart doubleChestRightModel;

    public GeoDrillerRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
        this.model = new GeoDrillerModel<>(ctx.bakeLayer(GeoDrillerModel.LAYER_LOCATION));
        this.singleChestModel = ctx.bakeLayer(ModelLayers.CHEST);
        this.doubleChestLeftModel = ctx.bakeLayer(ModelLayers.DOUBLE_CHEST_LEFT);
        this.doubleChestRightModel = ctx.bakeLayer(ModelLayers.DOUBLE_CHEST_RIGHT);
        this.shadowRadius = 1.6F;
    }

    @Override
    public void render(GeoDrillerEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buf, int packedLight) {
        model.resetPose();
        int parts = entity.getPartMask();
        boolean hasDrill = (parts & GeoDrillerEntity.PART_DRILL) != 0;
        boolean hasEngine = (parts & GeoDrillerEntity.PART_ENGINE) != 0;

        setVisible("drill_assembly", hasDrill);
        setVisible("engine_unit", hasEngine);

        // Smooth partialTick interpolation for 144fps buttery motion
        float age = entity.tickCount + partialTicks;
        float smoothWheelL = Mth.lerp(partialTicks, entity.prevWheelSpinL, entity.wheelSpinL);
        float smoothWheelR = Mth.lerp(partialTicks, entity.prevWheelSpinR, entity.wheelSpinR);
        float smoothDrill = Mth.lerp(partialTicks, entity.prevDrillSpin, entity.drillSpin);
        float smoothDriveSpool = Mth.lerp(partialTicks, entity.prevDriveSpool, entity.driveSpool);
        float smoothDrillSpool = Mth.lerp(partialTicks, entity.prevDrillSpool, entity.drillSpool);
        float smoothLeverL = Mth.lerp(partialTicks, entity.prevLeverLAngle, entity.leverLAngle);
        float smoothLeverR = Mth.lerp(partialTicks, entity.prevLeverRAngle, entity.leverRAngle);
        float smoothFlapper = Mth.lerp(partialTicks, entity.prevFlapperAngle, entity.flapperAngle);

        // 1. Drill assembly pitch tilt (Level / Down / Up) + smooth high-speed spin
        ModelPart drillAsm = model.getBone("drill_assembly");
        if (drillAsm != null) {
            float pitchTilt = switch (entity.getPitchMode()) {
                case GeoDrillerEntity.PITCH_DOWN -> -0.22F;
                case GeoDrillerEntity.PITCH_UP -> 0.22F;
                default -> 0.0F;
            };
            drillAsm.xRot += pitchTilt;
        }
        ModelPart drillHead = model.getBone("drill_head");
        if (drillHead != null) {
            drillHead.zRot += smoothDrill;
        }

        // 2. Twin industrial smokestack rain-flapper caps lifting under exhaust pressure
        if (hasEngine) {
            ModelPart flapL = model.getBone("flapper_l");
            ModelPart flapR = model.getBone("flapper_r");
            if (flapL != null) flapL.xRot += smoothFlapper;
            if (flapR != null) flapR.xRot += smoothFlapper;
        }

        // 3. All 6 interlocking 8-tooth sprocket gears synced to true left/right tank-steer travel
        for (int i = 0; i < 3; i++) {
            ModelPart wl = model.getBone("wheel_l_" + i);
            ModelPart wr = model.getBone("wheel_r_" + i);
            if (wl != null) wl.xRot += smoothWheelL;
            if (wr != null) wr.xRot += smoothWheelR;
        }

        // 4. Independent control levers: Left = Drive Forward/Reverse, Right = Drill Engage & Pitch
        ModelPart levL = model.getBone("lever_l");
        ModelPart levR = model.getBone("lever_r");
        if (levL != null) levL.xRot += smoothLeverL;
        if (levR != null) levR.xRot += smoothLeverR;

        // 5. Heavy engine spool-up rumble + drilling vibration
        float rumble = Math.max(smoothDriveSpool * 0.65F, smoothDrillSpool);
        float vibY = rumble > 0.05F ? Mth.sin(age * 2.8F) * 0.022F * rumble : 0.0F;
        float bodyYaw = Mth.rotLerp(partialTicks, entity.yBodyRotO, entity.yBodyRot);

        poseStack.pushPose();
        poseStack.translate(0.0D, vibY, 0.0D);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - bodyYaw));

        poseStack.pushPose();
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.translate(0.0D, -1.501D, 0.0D);
        model.renderToBuffer(poseStack, buf.getBuffer(RenderType.entityCutoutNoCull(TEXTURE)), packedLight, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);
        model.renderToBuffer(poseStack, buf.getBuffer(RenderType.eyes(EMISSIVE)), LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);
        poseStack.popPose();

        // 6. Render true Vanilla Single Chest (1x) or seamlessly merged Double Chest (2x, single centered latch)
        int chestCount = entity.getChestCount();
        if (chestCount > 0) {
            poseStack.pushPose();
            poseStack.translate(0.0D, 0.82D, 0.96D);
            float scale = 0.62F;
            poseStack.scale(scale, scale, scale);
            if (chestCount == 1) {
                renderChestHalf(singleChestModel, Sheets.CHEST_LOCATION, -0.5D, poseStack, buf, packedLight);
            } else {
                // Facing SOUTH (+Z): DOUBLE_CHEST_RIGHT sits on -X and DOUBLE_CHEST_LEFT on +X so their
                // inner seam & half-latches fuse seamlessly into one true Vanilla Double Chest!
                renderChestHalf(doubleChestRightModel, Sheets.CHEST_LOCATION_RIGHT, -1.0D, poseStack, buf, packedLight);
                renderChestHalf(doubleChestLeftModel, Sheets.CHEST_LOCATION_LEFT, 0.0D, poseStack, buf, packedLight);
            }
            poseStack.popPose();
        }

        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buf, packedLight);
    }

    private static void renderChestHalf(ModelPart chestPart, Material material, double offsetX, PoseStack poseStack, MultiBufferSource buf, int packedLight) {
        poseStack.pushPose();
        poseStack.translate(offsetX + 0.5D, 0.5D, 0.0D);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
        poseStack.translate(-0.5D, -0.5D, -0.5D);
        VertexConsumer vc = material.buffer(buf, RenderType::entityCutout);
        chestPart.render(poseStack, vc, packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
    }

    private void setVisible(String bone, boolean vis) {
        ModelPart p = model.getBone(bone);
        if (p != null) p.visible = vis;
    }

    @Override
    public ResourceLocation getTextureLocation(GeoDrillerEntity entity) {
        return TEXTURE;
    }
}

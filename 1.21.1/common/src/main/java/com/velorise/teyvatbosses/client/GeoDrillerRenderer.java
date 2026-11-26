package com.velorise.teyvatbosses.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.velorise.teyvatbosses.TeyvatBossesConstants;
import com.velorise.teyvatbosses.client.model.GeoDrillerModel;
import com.velorise.teyvatbosses.entity.GeoDrillerEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;

/**
 * Renders {@link GeoDrillerEntity} with smooth partial-tick interpolated procedural animations:
 * spinning drill head, true-travel wheel &amp; tread rotation, dual independent drive/drill levers,
 * engine spool-up shudder, and 1x/2x Vanilla Chest mounted on the rear deck.
 */
public class GeoDrillerRenderer extends EntityRenderer<GeoDrillerEntity> {
    private static final ResourceLocation TEXTURE = TeyvatBossesConstants.id("textures/entity/geo_driller.png");
    private static final ResourceLocation EMISSIVE = TeyvatBossesConstants.id("textures/entity/geo_driller_emissive.png");

    private final GeoDrillerModel<GeoDrillerEntity> model;

    public GeoDrillerRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
        this.model = new GeoDrillerModel<>(ctx.bakeLayer(GeoDrillerModel.LAYER_LOCATION));
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
        float smoothWheel = Mth.lerp(partialTicks, entity.prevWheelSpin, entity.wheelSpin);
        float smoothDrill = Mth.lerp(partialTicks, entity.prevDrillSpin, entity.drillSpin);
        float smoothDriveSpool = Mth.lerp(partialTicks, entity.prevDriveSpool, entity.driveSpool);
        float smoothDrillSpool = Mth.lerp(partialTicks, entity.prevDrillSpool, entity.drillSpool);
        float smoothLeverL = Mth.lerp(partialTicks, entity.prevLeverLAngle, entity.leverLAngle);
        float smoothLeverR = Mth.lerp(partialTicks, entity.prevLeverRAngle, entity.leverRAngle);

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

        // 2. Reactor core rotation (faster under load)
        ModelPart reactor = model.getBone("reactor_core");
        if (reactor != null && hasEngine) {
            reactor.yRot += age * (0.07F + 0.14F * Math.max(smoothDriveSpool, smoothDrillSpool));
        }

        // 3. All 6 track wheels + rolling tread micro-oscillation synced to smoothWheel
        for (int i = 0; i < 3; i++) {
            ModelPart wl = model.getBone("wheel_l_" + i);
            ModelPart wr = model.getBone("wheel_r_" + i);
            if (wl != null) wl.xRot += smoothWheel;
            if (wr != null) wr.xRot += smoothWheel;
        }
        ModelPart treadL = model.getBone("tread_l");
        ModelPart treadR = model.getBone("tread_r");
        float trackPulse = Mth.sin(smoothWheel * 3.0F) * 0.25F;
        if (treadL != null) treadL.y += trackPulse;
        if (treadR != null) treadR.y -= trackPulse;

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

        // 6. Render 1x Single or 2x Double Vanilla Chest on rear deck
        int chestCount = entity.getChestCount();
        if (chestCount > 0) {
            BlockRenderDispatcher brd = Minecraft.getInstance().getBlockRenderer();
            poseStack.pushPose();
            poseStack.translate(0.0D, 0.82D, 0.96D);
            float scale = 0.62F;
            poseStack.scale(scale, scale, scale);
            if (chestCount == 1) {
                BlockState single = Blocks.CHEST.defaultBlockState()
                        .setValue(ChestBlock.FACING, Direction.SOUTH)
                        .setValue(ChestBlock.TYPE, ChestType.SINGLE);
                poseStack.translate(-0.5D, 0.0D, -0.5D);
                brd.renderSingleBlock(single, poseStack, buf, packedLight, OverlayTexture.NO_OVERLAY);
            } else {
                BlockState left = Blocks.CHEST.defaultBlockState()
                        .setValue(ChestBlock.FACING, Direction.SOUTH)
                        .setValue(ChestBlock.TYPE, ChestType.LEFT);
                BlockState right = Blocks.CHEST.defaultBlockState()
                        .setValue(ChestBlock.FACING, Direction.SOUTH)
                        .setValue(ChestBlock.TYPE, ChestType.RIGHT);
                poseStack.pushPose();
                poseStack.translate(-1.0D, 0.0D, -0.5D);
                brd.renderSingleBlock(right, poseStack, buf, packedLight, OverlayTexture.NO_OVERLAY);
                poseStack.popPose();
                poseStack.pushPose();
                poseStack.translate(0.0D, 0.0D, -0.5D);
                brd.renderSingleBlock(left, poseStack, buf, packedLight, OverlayTexture.NO_OVERLAY);
                poseStack.popPose();
            }
            poseStack.popPose();
        }

        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buf, packedLight);
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

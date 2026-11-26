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
 * Renders {@link GeoDrillerEntity} with procedural mechanical animations (spinning drill bit,
 * velocity-synced tread wheels, live WASD control levers, reactor rotation, and drilling vibration)
 * plus 1x Single or 2x Double Vanilla Chest mounted on the rear deck.
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

        // Procedural mechanical animations driven directly by vehicle physics & inputs
        float age = entity.tickCount + partialTicks;
        ModelPart drillHead = model.getBone("drill_head");
        if (drillHead != null) {
            drillHead.zRot += entity.drillSpin + (entity.isDrilling() ? partialTicks * 0.62F : 0.0F);
        }
        ModelPart reactor = model.getBone("reactor_core");
        if (reactor != null && hasEngine) {
            reactor.yRot += age * 0.08F;
        }
        float wSpin = entity.wheelSpin;
        for (int i = 0; i < 3; i++) {
            ModelPart wl = model.getBone("wheel_l_" + i);
            ModelPart wr = model.getBone("wheel_r_" + i);
            if (wl != null) wl.xRot += wSpin;
            if (wr != null) wr.xRot += wSpin;
        }
        float thr = entity.getThrottleInput();
        float steer = entity.getSteerInput();
        ModelPart levL = model.getBone("lever_l");
        ModelPart levR = model.getBone("lever_r");
        if (levL != null) levL.xRot += (thr + steer * 0.5F) * 0.35F;
        if (levR != null) levR.xRot += (thr - steer * 0.5F) * 0.35F;

        float vibY = entity.isDrilling() ? Mth.sin(age * 2.4F) * 0.025F : 0.0F;
        float bodyYaw = Mth.rotLerp(partialTicks, entity.yBodyRotO, entity.yBodyRot);

        poseStack.pushPose();
        poseStack.translate(0.0D, vibY, 0.0D);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - bodyYaw));

        // 1. Render Driller body + emissive lights/reactor
        poseStack.pushPose();
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.translate(0.0D, -1.501D, 0.0D);
        model.renderToBuffer(poseStack, buf.getBuffer(RenderType.entityCutoutNoCull(TEXTURE)), packedLight, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);
        model.renderToBuffer(poseStack, buf.getBuffer(RenderType.eyes(EMISSIVE)), LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);
        poseStack.popPose();

        // 2. Render 1x Single or 2x Double Vanilla Chest right on rear deck (chest_mount @ y=13/16, z=15.5/16)
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

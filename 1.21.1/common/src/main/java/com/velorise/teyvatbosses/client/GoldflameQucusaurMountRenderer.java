package com.velorise.teyvatbosses.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.velorise.teyvatbosses.client.model.BedrockAnimationLoader;
import com.velorise.teyvatbosses.client.model.GoldflameQucusaurModel;
import com.velorise.teyvatbosses.entity.GoldflameQucusaurMountEntity;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

/**
 * Renderer for {@link GoldflameQucusaurMountEntity} sharing the exact same
 * {@link GoldflameQucusaurModel} and textures at {@code 0.62x} scale with dynamic {@code saddle} visibility.
 */
public class GoldflameQucusaurMountRenderer extends EntityRenderer<GoldflameQucusaurMountEntity> {
    private static final float MOUNT_SCALE = 0.62F;

    private final GoldflameQucusaurModel<GoldflameQucusaurMountEntity> bodyModel;
    private final GoldflameQucusaurModel<GoldflameQucusaurMountEntity> emissiveModel;

    public GoldflameQucusaurMountRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
        this.bodyModel = new GoldflameQucusaurModel<>(ctx.bakeLayer(GoldflameQucusaurModel.LAYER_LOCATION));
        this.emissiveModel = new GoldflameQucusaurModel<>(ctx.bakeLayer(GoldflameQucusaurModel.LAYER_LOCATION_EMISSIVE));
        this.shadowRadius = 1.45F;
    }

    @Override
    public ResourceLocation getTextureLocation(GoldflameQucusaurMountEntity entity) {
        return GoldflameQucusaurRenderer.TEXTURE;
    }

    @Override
    public void render(GoldflameQucusaurMountEntity entity, float entityYaw, float partialTicks,
                       PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        float bodyYaw = Mth.rotLerp(partialTicks, entity.yBodyRotO, entity.yBodyRot);
        float animSec = (entity.getActionState() != GoldflameQucusaurMountEntity.ACTION_NONE
                ? entity.getActionTicks() + partialTicks
                : entity.tickCount + partialTicks) / 20.0F;
        float clientSec = (entity.tickCount + partialTicks) / 20.0F;

        bodyModel.resetPose(entity.isSaddled());
        BedrockAnimationLoader.Clip clip = BedrockAnimationLoader.getClip("goldflame_qucusaur", entity.getActiveAnimationClip());
        if (clip != null) {
            BedrockAnimationLoader.applyWeighted(bodyModel::getBone, clip, animSec, 1.0F, false);
        }
        emissiveModel.copyPoseFrom(bodyModel);

        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - bodyYaw));
        if (entity.isFlying()) {
            poseStack.mulPose(Axis.ZP.rotationDegrees(entity.getBankRoll()));
        }
        poseStack.scale(-MOUNT_SCALE, -MOUNT_SCALE, MOUNT_SCALE);
        poseStack.translate(0.0D, -1.501D, 0.0D);

        int overlay = OverlayTexture.pack(OverlayTexture.u(0.0F), OverlayTexture.v(entity.hurtTime > 0));
        VertexConsumer bodyVc = buffer.getBuffer(RenderType.entityCutoutNoCull(GoldflameQucusaurRenderer.TEXTURE));
        bodyModel.renderToBuffer(poseStack, bodyVc, packedLight, overlay, 0xFFFFFFFF);

        VertexConsumer emVc = buffer.getBuffer(RenderType.entityTranslucentEmissive(GoldflameQucusaurRenderer.EMISSIVE_TEXTURE));
        emissiveModel.renderToBuffer(poseStack, emVc, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 0xD8FFFFFF);
        poseStack.popPose();

        // Volumetric Goldflame Breath Torrent aimed along rider look direction!
        if (entity.isBreathing()) {
            LivingEntity rider = entity.getControllingPassenger();
            float aimYaw = rider != null ? Mth.rotLerp(partialTicks, rider.yRotO, rider.getYRot()) : bodyYaw;
            float aimPitch = rider != null ? Mth.lerp(partialTicks, rider.xRotO, rider.getXRot()) : 0.0F;
            VertexConsumer lvc = buffer.getBuffer(RenderType.lightning());
            GoldflameQucusaurRenderer.renderGoldflameBreathVfx(
                    poseStack, lvc, aimYaw, aimPitch, 1.65D, 1.55D, 16.5F, clientSec, 0.95F);
        }

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }
}

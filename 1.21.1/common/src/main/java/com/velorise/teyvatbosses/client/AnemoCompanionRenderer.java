package com.velorise.teyvatbosses.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.velorise.teyvatbosses.TeyvatBossesConstants;
import com.velorise.teyvatbosses.client.model.AnemoHypostasisModel;
import com.velorise.teyvatbosses.entity.AnemoCompanionEntity;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/**
 * Renders the compact 1-block 8-cube {@link AnemoCompanionEntity} using {@link AnemoHypostasisModel}.
 * Builds strictly from the bottom layer up ({@code shell_4..7} bottom tier first, then {@code shell_0..3} top tier)
 * when assembling on the ground, and mirrors live ammo/recharge cubes during combat.
 */
public class AnemoCompanionRenderer extends EntityRenderer<AnemoCompanionEntity> {
    private static final ResourceLocation TEXTURE = TeyvatBossesConstants.id("textures/entity/anemo_hypostasis.png");
    private static final ResourceLocation EMISSIVE = TeyvatBossesConstants.id("textures/entity/anemo_hypostasis_emissive.png");
    private static final float PET_SCALE = 0.15F;
    // Bottom-up shell order: bottom 4 cubes (y = +9 in MC model space) first, then top 4 cubes (y = -9)
    private static final int[] BOTTOM_UP_ORDER = {4, 5, 6, 7, 0, 1, 2, 3};

    private final AnemoHypostasisModel<AnemoCompanionEntity> model;

    public AnemoCompanionRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
        this.shadowRadius = 0.14F;
        this.model = new AnemoHypostasisModel<>(ctx.bakeLayer(AnemoHypostasisModel.LAYER_LOCATION));
    }

    @Override
    public void render(AnemoCompanionEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int packedLight) {
        float t = entity.tickCount + partialTicks;
        boolean assembled = entity.isFullyAssembled();
        int visibleCubes = assembled ? entity.getActiveShells() : entity.getAssembledCount();
        boolean recharging = entity.isRecharging();

        poseStack.pushPose();
        float yaw = Mth.rotLerp(partialTicks, entity.yBodyRotO, entity.yBodyRot);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - yaw));
        poseStack.scale(-PET_SCALE, -PET_SCALE, PET_SCALE);
        // Sit flush on the ground block while assembling; hover higher once awakened
        poseStack.translate(0.0D, assembled ? -1.20D : -0.92D, 0.0D);

        model.resetPose();

        ModelPart core = model.getBone("core_group");
        ModelPart frame = model.getBone("core_frame");
        ModelPart shellRing = model.getBone("shell_ring");

        if (core != null) {
            core.visible = assembled;
            core.yRot = t * (recharging ? 0.22F : 0.10F);
            core.xRot = 0.7853982F + Mth.sin(t * 0.08F) * 0.25F;
            float pulse = recharging ? (1.12F + Mth.sin(t * 0.35F) * 0.14F) : 1.0F;
            core.xScale = pulse;
            core.yScale = pulse;
            core.zScale = pulse;
        }
        if (frame != null) {
            frame.visible = assembled;
            frame.yRot = -t * (recharging ? 0.18F : 0.07F);
            frame.zRot = Mth.sin(t * 0.06F) * 0.18F;
        }
        if (shellRing != null) {
            // Stay steady & aligned on the block while assembling; spin smoothly once awakened
            shellRing.yRot = assembled ? (t * (recharging ? 0.14F : 0.045F)) : 0.0F;
            shellRing.xRot = assembled ? Mth.sin(t * 0.05F) * 0.12F : 0.0F;
        }

        // Reveal cubes strictly bottom-up (bottom 4 cubes first, then top 4 cubes)
        float expand = assembled ? (recharging ? 1.16F : 1.06F) : 1.00F;
        for (int step = 0; step < 8; step++) {
            int boneIdx = BOTTOM_UP_ORDER[step];
            ModelPart sh = model.getBone("shell_" + boneIdx);
            if (sh != null) {
                sh.visible = step < visibleCubes;
                if (sh.visible) {
                    sh.x *= expand;
                    sh.y *= expand;
                    sh.z *= expand;
                }
            }
        }
        for (int i = 0; i < 4; i++) {
            ModelPart mini = model.getBone("mini_" + i);
            if (mini != null) {
                mini.visible = assembled;
                float ang = t * 0.12F + i * Mth.HALF_PI;
                mini.x = Mth.cos(ang) * 15.0F;
                mini.z = Mth.sin(ang) * 15.0F;
                mini.y = Mth.sin(t * 0.15F + i) * 4.0F;
            }
        }

        int overlay = OverlayTexture.pack(OverlayTexture.u(0.0F), OverlayTexture.v(entity.hurtTime > 0));
        VertexConsumer vc = buffers.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        model.renderToBuffer(poseStack, vc, packedLight, overlay, 0xFFFFFFFF);

        VertexConsumer emVc = buffers.getBuffer(RenderType.eyes(EMISSIVE));
        model.renderToBuffer(poseStack, emVc, 0x00F000F0, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);

        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffers, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(AnemoCompanionEntity entity) {
        return TEXTURE;
    }
}

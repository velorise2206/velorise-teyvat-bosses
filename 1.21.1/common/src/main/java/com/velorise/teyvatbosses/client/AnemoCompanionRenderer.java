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
 * Renders the miniature 8-cube {@link AnemoCompanionEntity} using {@link AnemoHypostasisModel}.
 * Dynamically shows/hides each of the 8 outer shell cubes ({@code shell_0..shell_7}) to match
 * assembly progress ({@code 1/8..8/8}), live ammo remaining as cubes fire, and recharge regeneration.
 */
public class AnemoCompanionRenderer extends EntityRenderer<AnemoCompanionEntity> {
    private static final ResourceLocation TEXTURE = TeyvatBossesConstants.id("textures/entity/anemo_hypostasis.png");
    private static final ResourceLocation EMISSIVE = TeyvatBossesConstants.id("textures/entity/anemo_hypostasis_emissive.png");
    private static final float PET_SCALE = 0.36F;

    private final AnemoHypostasisModel<AnemoCompanionEntity> model;

    public AnemoCompanionRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
        this.shadowRadius = 0.35F;
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
        poseStack.translate(0.0D, -1.35D, 0.0D);

        model.resetPose();

        // Hide the core & wireframe until fully assembled (or pulse brightly when recharging!)
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
            shellRing.yRot = assembled ? (t * (recharging ? 0.14F : 0.045F)) : (t * 0.025F);
            shellRing.xRot = assembled ? Mth.sin(t * 0.05F) * 0.12F : 0.0F;
        }

        // Show exact count of remaining/assembled outer shell cubes (shell_0..shell_7)
        float expand = assembled ? (recharging ? 1.24F : 1.10F) : 1.02F;
        for (int i = 0; i < 8; i++) {
            ModelPart sh = model.getBone("shell_" + i);
            if (sh != null) {
                sh.visible = i < visibleCubes;
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
                float ang = t * 0.12F + i * (Mth.HALF_PI);
                mini.x = Mth.cos(ang) * 18.0F;
                mini.z = Mth.sin(ang) * 18.0F;
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

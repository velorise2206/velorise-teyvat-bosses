package com.velorise.teyvatbosses.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.velorise.teyvatbosses.TeyvatBossesConstants;
import com.velorise.teyvatbosses.client.model.PrimoGeovishapAnimations;
import com.velorise.teyvatbosses.client.model.PrimoGeovishapModel;
import com.velorise.teyvatbosses.entity.PrimoGeovishapEntity;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

/**
 * Renders {@link PrimoGeovishapEntity} using the AAA 881-cube articulated model,
 * 1:1 Catmull-Rom animations, elemental-tinted emissive crystals, and ground telegraph rings.
 */
public class PrimoGeovishapRenderer extends EntityRenderer<PrimoGeovishapEntity> {
    private static final ResourceLocation TEXTURE = TeyvatBossesConstants.id("textures/entity/primo_geovishap_boss.png");
    private static final ResourceLocation EMISSIVE = TeyvatBossesConstants.id("textures/entity/primo_geovishap_boss_emissive.png");

    private final PrimoGeovishapModel<PrimoGeovishapEntity> model;

    public PrimoGeovishapRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
        this.model = new PrimoGeovishapModel<>(ctx.bakeLayer(PrimoGeovishapModel.LAYER_LOCATION));
        this.shadowRadius = 2.4F;
    }

    @Override
    public void render(PrimoGeovishapEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buf, int packedLight) {
        float sec = (entity.getAnimTick() + partialTicks) / 20.0F;
        String clip = PrimoGeovishapEntity.clipNameForAnim(entity.getAnimId());
        PrimoGeovishapAnimations.apply(model, clip, sec);

        float bodyYaw = Mth.rotLerp(partialTicks, entity.yBodyRotO, entity.yBodyRot);

        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - bodyYaw));
        poseStack.scale(-1.05F, -1.05F, 1.05F);
        poseStack.translate(0.0D, -1.501D, 0.0D);

        int overlay = entity.hurtTime > 0 ? OverlayTexture.pack(OverlayTexture.u(0.0F), OverlayTexture.v(true)) : OverlayTexture.NO_OVERLAY;
        model.renderToBuffer(poseStack, buf.getBuffer(RenderType.entityCutoutNoCull(TEXTURE)), packedLight, overlay, 0xFFFFFFFF);

        int[] rgb = entity.getInfusionRgb();
        int emColor = 0xFF000000 | (rgb[0] << 16) | (rgb[1] << 8) | rgb[2];
        model.renderToBuffer(poseStack, buf.getBuffer(RenderType.eyes(EMISSIVE)), LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, emColor);
        poseStack.popPose();

        // Telegraph ring during Primordial Shower / Burrow emergence
        float teleRadius = entity.getTelegraphRadius();
        if (teleRadius > 0.5F) {
            VertexConsumer lvc = buf.getBuffer(RenderType.lightning());
            poseStack.pushPose();
            Matrix4f mat = poseStack.last().pose();
            drawRing(lvc, mat, teleRadius - 0.35F, teleRadius, 40, rgb[0], rgb[1], rgb[2], 185);
            poseStack.popPose();
        }

        super.render(entity, entityYaw, partialTicks, poseStack, buf, packedLight);
    }

    private static void drawRing(VertexConsumer vc, Matrix4f mat, float rIn, float rOut, int segs, int r, int g, int b, int a) {
        for (int i = 0; i < segs; i++) {
            float a0 = (float) (Math.PI * 2.0D * i / segs);
            float a1 = (float) (Math.PI * 2.0D * (i + 1) / segs);
            float c0 = Mth.cos(a0), s0 = Mth.sin(a0);
            float c1 = Mth.cos(a1), s1 = Mth.sin(a1);
            vc.addVertex(mat, c0 * rIn, 0.08F, s0 * rIn).setColor(r, g, b, a);
            vc.addVertex(mat, c0 * rOut, 0.08F, s0 * rOut).setColor(r, g, b, a);
            vc.addVertex(mat, c1 * rOut, 0.08F, s1 * rOut).setColor(r, g, b, a);
            vc.addVertex(mat, c1 * rIn, 0.08F, s1 * rIn).setColor(r, g, b, a);
        }
    }

    @Override
    public ResourceLocation getTextureLocation(PrimoGeovishapEntity entity) {
        return TEXTURE;
    }
}

package com.velorise.teyvatbosses.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.velorise.teyvatbosses.TeyvatBossesConstants;
import com.velorise.teyvatbosses.client.model.TrounceBlossomModel;
import com.velorise.teyvatbosses.entity.TrounceBlossomEntity;
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
 * Renders the 3D Trounce Blossom (Ley Line Reward Flower) with diffuse + emissive petals,
 * smooth emerge & bloom animations, and a glowing Ley Line ground ring.
 */
public class TrounceBlossomRenderer extends EntityRenderer<TrounceBlossomEntity> {
    private static final ResourceLocation TEXTURE = TeyvatBossesConstants.id("textures/entity/trounce_blossom.png");
    private static final ResourceLocation EMISSIVE = TeyvatBossesConstants.id("textures/entity/trounce_blossom_emissive.png");

    private final TrounceBlossomModel<TrounceBlossomEntity> model;

    public TrounceBlossomRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.75F;
        this.model = new TrounceBlossomModel<>(context.bakeLayer(TrounceBlossomModel.LAYER_LOCATION));
    }

    @Override
    public void render(
            TrounceBlossomEntity entity,
            float entityYaw,
            float partialTicks,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight
    ) {
        float ageSec = (entity.tickCount + partialTicks) / 20.0F;
        float emerge = Mth.clamp((entity.tickCount + partialTicks) / 24.0F, 0.0F, 1.0F);
        float bloom = entity.isClaimed()
                ? Mth.clamp((entity.getBloomTicks() + partialTicks) / 22.0F, 0.0F, 1.0F)
                : 0.0F;

        // 1. Ground Ley Line sigil halo
        VertexConsumer lvc = bufferSource.getBuffer(RenderType.lightning());
        poseStack.pushPose();
        Matrix4f m = poseStack.last().pose();
        float rOuter = 1.25F + bloom * 0.65F;
        drawRing(lvc, m, rOuter - 0.22F, rOuter, 24, 125, 225, 255, (int) (165 * emerge));
        poseStack.popPose();

        // 2. 3D Trounce Blossom model (Diffuse + Emissive)
        poseStack.pushPose();
        poseStack.scale(-1.18F, -1.18F, 1.18F);
        poseStack.translate(0.0D, -1.501D, 0.0D);

        model.applyBlossomPose(ageSec, emerge, bloom);

        VertexConsumer vc = bufferSource.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        model.renderToBuffer(poseStack, vc, packedLight, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);

        VertexConsumer evc = bufferSource.getBuffer(RenderType.eyes(EMISSIVE));
        model.renderToBuffer(poseStack, evc, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);

        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
    }

    private static void drawRing(VertexConsumer vc, Matrix4f mat, float rIn, float rOut, int segs, int r, int g, int b, int a) {
        for (int i = 0; i < segs; i++) {
            float a0 = (float) (Math.PI * 2.0D * i / segs);
            float a1 = (float) (Math.PI * 2.0D * (i + 1) / segs);
            float c0 = Mth.cos(a0), s0 = Mth.sin(a0);
            float c1 = Mth.cos(a1), s1 = Mth.sin(a1);
            vc.addVertex(mat, c0 * rIn, 0.07F, s0 * rIn).setColor(r, g, b, a);
            vc.addVertex(mat, c0 * rOut, 0.07F, s0 * rOut).setColor(r, g, b, a);
            vc.addVertex(mat, c1 * rOut, 0.07F, s1 * rOut).setColor(r, g, b, a);
            vc.addVertex(mat, c1 * rIn, 0.07F, s1 * rIn).setColor(r, g, b, a);
            vc.addVertex(mat, c1 * rIn, 0.07F, s1 * rIn).setColor(r, g, b, a);
            vc.addVertex(mat, c1 * rOut, 0.07F, s1 * rOut).setColor(r, g, b, a);
            vc.addVertex(mat, c0 * rOut, 0.07F, s0 * rOut).setColor(r, g, b, a);
            vc.addVertex(mat, c0 * rIn, 0.07F, s0 * rIn).setColor(r, g, b, a);
        }
    }

    @Override
    public ResourceLocation getTextureLocation(TrounceBlossomEntity entity) {
        return TEXTURE;
    }
}

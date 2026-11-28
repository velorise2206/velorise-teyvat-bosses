package com.velorise.teyvatbosses.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.velorise.teyvatbosses.entity.GoldflameQucusaurEggEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

/**
 * Renders the living {@link GoldflameQucusaurEggEntity} as a sculpted crimson-gold volcanic dragon egg
 * that wobbles faster and glows hotter as incubation progresses toward hatching.
 */
public class GoldflameQucusaurEggRenderer extends EntityRenderer<GoldflameQucusaurEggEntity> {
    public GoldflameQucusaurEggRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
        this.shadowRadius = 0.45F;
    }

    @Override
    public ResourceLocation getTextureLocation(GoldflameQucusaurEggEntity entity) {
        return GoldflameQucusaurRenderer.TEXTURE;
    }

    @Override
    public void render(GoldflameQucusaurEggEntity entity, float entityYaw, float partialTicks,
                       PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        float t = (entity.tickCount + partialTicks) / 20.0F;
        float prog = entity.getHatchProgress();

        poseStack.pushPose();
        if (entity.isHeated()) {
            float wobble = (2.5F + prog * 9.0F) * Mth.sin(t * (6.0F + prog * 14.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(wobble));
        }

        VertexConsumer vc = buffer.getBuffer(RenderType.lightning());
        Matrix4f mat = poseStack.last().pose();
        // Multi-tiered crimson-gold egg shell
        drawOctaTier(vc, mat, 0.02F, 0.28F, 0.24F, 0.36F, 148, 22, 16, 240);
        drawOctaTier(vc, mat, 0.28F, 0.62F, 0.36F, 0.28F, 198, 46, 22, 245);
        drawOctaTier(vc, mat, 0.62F, 0.88F, 0.28F, 0.08F, 242, 132, 30, 250);

        if (entity.isHeated()) {
            int glowA = (int) (120 + 115 * prog);
            drawOctaTier(vc, mat, 0.20F, 0.70F, 0.40F, 0.34F, 255, 206, 68, glowA);
        }
        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    private static void drawOctaTier(VertexConsumer vc, Matrix4f mat, float y0, float y1, float r0, float r1,
                                     int r, int g, int b, int a) {
        int segs = 10;
        for (int i = 0; i < segs; i++) {
            float a0 = (float) (Math.PI * 2.0D * i / segs);
            float a1 = (float) (Math.PI * 2.0D * (i + 1) / segs);
            float c0 = Mth.cos(a0), s0 = Mth.sin(a0);
            float c1 = Mth.cos(a1), s1 = Mth.sin(a1);
            vc.addVertex(mat, c0 * r0, y0, s0 * r0).setColor(r, g, b, a);
            vc.addVertex(mat, c1 * r0, y0, s1 * r0).setColor(r, g, b, a);
            vc.addVertex(mat, c1 * r1, y1, s1 * r1).setColor(r, g, b, a);
            vc.addVertex(mat, c0 * r1, y1, s0 * r1).setColor(r, g, b, a);
        }
    }
}

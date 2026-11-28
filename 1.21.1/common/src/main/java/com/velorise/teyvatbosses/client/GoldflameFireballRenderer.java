package com.velorise.teyvatbosses.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.velorise.teyvatbosses.entity.GoldflameFireballEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

/**
 * Renders {@link GoldflameFireballEntity} as a blazing two-shell golden Phlogiston fireball.
 */
public class GoldflameFireballRenderer extends EntityRenderer<GoldflameFireballEntity> {
    public GoldflameFireballRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
        this.shadowRadius = 0.25F;
    }

    @Override
    public ResourceLocation getTextureLocation(GoldflameFireballEntity entity) {
        return GoldflameQucusaurRenderer.TEXTURE;
    }

    @Override
    public void render(GoldflameFireballEntity entity, float entityYaw, float partialTicks,
                       PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        VertexConsumer vc = buffer.getBuffer(RenderType.lightning());
        Matrix4f mat = poseStack.last().pose();
        drawShell(vc, mat, 0.42F, 255, 86, 22, 165);
        drawShell(vc, mat, 0.24F, 255, 236, 128, 240);
        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    private static void drawShell(VertexConsumer vc, Matrix4f mat, float rad, int r, int g, int b, int a) {
        int segs = 8;
        for (int i = 0; i < segs; i++) {
            float a0 = (float) (Math.PI * 2.0D * i / segs);
            float a1 = (float) (Math.PI * 2.0D * (i + 1) / segs);
            float c0 = Mth.cos(a0) * rad, s0 = Mth.sin(a0) * rad;
            float c1 = Mth.cos(a1) * rad, s1 = Mth.sin(a1) * rad;
            vc.addVertex(mat, c0, -rad * 0.5F, s0).setColor(r, g, b, a);
            vc.addVertex(mat, c1, -rad * 0.5F, s1).setColor(r, g, b, a);
            vc.addVertex(mat, c1 * 0.6F, rad * 0.8F, s1 * 0.6F).setColor(r, g, b, a);
            vc.addVertex(mat, c0 * 0.6F, rad * 0.8F, s0 * 0.6F).setColor(r, g, b, a);
        }
    }
}

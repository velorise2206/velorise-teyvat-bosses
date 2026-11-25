package com.velorise.teyvatbosses.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.velorise.teyvatbosses.TeyvatBossesConstants;
import com.velorise.teyvatbosses.entity.AnemoTornadoEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

/**
 * Renders a towering 3D swirling Hurricane Tornado funnel with expanding helical wind ribbons.
 */
public class AnemoTornadoRenderer extends EntityRenderer<AnemoTornadoEntity> {
    private static final ResourceLocation TEX = TeyvatBossesConstants.id("textures/entity/anemo_hypostasis.png");

    public AnemoTornadoRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(AnemoTornadoEntity entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        float t = entity.tickCount + partialTicks;
        float grow = Mth.clamp(t / 16.0F, 0.0F, 1.0F);
        VertexConsumer vc = bufferSource.getBuffer(RenderType.lightning());
        Matrix4f m = poseStack.last().pose();

        int rings = 9;
        for (int i = 0; i < rings; i++) {
            float frac = (float) i / (rings - 1);
            float y = frac * 6.0F * grow;
            float rad = (0.55F + frac * frac * 1.95F) * grow;
            float wobbleX = Mth.sin(t * 0.25F + frac * 4.5F) * 0.22F * frac;
            float wobbleZ = Mth.cos(t * 0.22F + frac * 4.5F) * 0.22F * frac;
            int al = (int) ((155 - frac * 55) * grow);
            drawTiltedRing(vc, m, wobbleX, y, wobbleZ, rad - 0.26F, rad, 18, 175, 248, 236, al);
        }

        // Fast inner cyan wind core helix
        drawHelix(vc, m, t * 0.42F, 6.0F * grow, 110, 252, 224, (int) (185 * grow));
        drawHelix(vc, m, t * 0.42F + Mth.PI, 6.0F * grow, 225, 255, 248, (int) (155 * grow));

        super.render(entity, yaw, partialTicks, poseStack, bufferSource, packedLight);
    }

    private static void drawTiltedRing(VertexConsumer vc, Matrix4f mat, float cx, float cy, float cz,
                                       float rIn, float rOut, int segs, int r, int g, int b, int a) {
        for (int i = 0; i < segs; i++) {
            float a0 = (float) (Math.PI * 2.0D * i / segs);
            float a1 = (float) (Math.PI * 2.0D * (i + 1) / segs);
            float c0 = Mth.cos(a0), s0 = Mth.sin(a0);
            float c1 = Mth.cos(a1), s1 = Mth.sin(a1);
            vc.addVertex(mat, cx + c0 * rIn, cy, cz + s0 * rIn).setColor(r, g, b, a);
            vc.addVertex(mat, cx + c0 * rOut, cy + 0.14F, cz + s0 * rOut).setColor(r, g, b, a);
            vc.addVertex(mat, cx + c1 * rOut, cy + 0.14F, cz + s1 * rOut).setColor(r, g, b, a);
            vc.addVertex(mat, cx + c1 * rIn, cy, cz + s1 * rIn).setColor(r, g, b, a);
            vc.addVertex(mat, cx + c1 * rIn, cy, cz + s1 * rIn).setColor(r, g, b, a);
            vc.addVertex(mat, cx + c1 * rOut, cy + 0.14F, cz + s1 * rOut).setColor(r, g, b, a);
            vc.addVertex(mat, cx + c0 * rOut, cy + 0.14F, cz + s0 * rOut).setColor(r, g, b, a);
            vc.addVertex(mat, cx + c0 * rIn, cy, cz + s0 * rIn).setColor(r, g, b, a);
        }
    }

    private static void drawHelix(VertexConsumer vc, Matrix4f mat, float phase, float maxH, int r, int g, int b, int a) {
        int steps = 22;
        float px0 = 0, py0 = 0, pz0 = 0;
        for (int i = 0; i <= steps; i++) {
            float f = (float) i / steps;
            float y = f * maxH;
            float rad = 0.45F + f * f * 1.85F;
            float ang = phase + f * Mth.TWO_PI * 3.0F;
            float x = Mth.cos(ang) * rad;
            float z = Mth.sin(ang) * rad;
            if (i > 0) {
                float w = 0.24F;
                vc.addVertex(mat, px0, py0, pz0).setColor(r, g, b, a);
                vc.addVertex(mat, px0, py0 + w, pz0).setColor(r, g, b, a);
                vc.addVertex(mat, x, y + w, z).setColor(r, g, b, a);
                vc.addVertex(mat, x, y, z).setColor(r, g, b, a);
                vc.addVertex(mat, x, y, z).setColor(r, g, b, a);
                vc.addVertex(mat, x, y + w, z).setColor(r, g, b, a);
                vc.addVertex(mat, px0, py0 + w, pz0).setColor(r, g, b, a);
                vc.addVertex(mat, px0, py0, pz0).setColor(r, g, b, a);
            }
            px0 = x; py0 = y; pz0 = z;
        }
    }

    @Override
    public ResourceLocation getTextureLocation(AnemoTornadoEntity entity) {
        return TEX;
    }
}

package com.velorise.teyvatbosses.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.velorise.teyvatbosses.TeyvatBossesConstants;
import com.velorise.teyvatbosses.entity.WindCurrentEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

/**
 * Renders Genshin-style Updraft Wind Currents as rising translucent turquoise wind streams
 * with ascending glowing rings and a swirling ground vortex ring.
 */
public class WindCurrentRenderer extends EntityRenderer<WindCurrentEntity> {
    private static final ResourceLocation TEX = TeyvatBossesConstants.id("textures/entity/anemo_hypostasis.png");

    public WindCurrentRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(WindCurrentEntity entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        float t = entity.tickCount + partialTicks;
        float r = entity.getRadius();
        float h = entity.getColumnHeight();
        int maxLife = entity.getMaxLife();
        float fade = t < 15.0F ? t / 15.0F : (t > maxLife - 20.0F ? Math.max(0.0F, (maxLife - t) / 20.0F) : 1.0F);

        VertexConsumer vc = bufferSource.getBuffer(RenderType.lightning());
        Matrix4f m = poseStack.last().pose();

        // 1. Ground updraft base ring
        drawRing(vc, m, 0.08F, r * 0.78F, r * 0.96F, 24, 125, 250, 224, (int) (145 * fade));

        // 2. 4 Ascending wind rings traveling smoothly from floor to top
        for (int i = 0; i < 4; i++) {
            float frac = ((t * 0.045F) + i * 0.25F) % 1.0F;
            float y = frac * h;
            float rr = r * (0.65F + 0.25F * Mth.sin(frac * Mth.PI));
            int al = (int) (155 * fade * Mth.sin(frac * Mth.PI));
            drawRing(vc, m, y, rr - 0.14F, rr, 20, 185, 255, 242, al);
        }

        // 3. 5 Vertical rising wind stream ribbons spiraling gently upward
        for (int s = 0; s < 5; s++) {
            float baseAng = s * (Mth.TWO_PI / 5.0F) + t * 0.08F;
            float segOffset = ((t * 0.07F) + s * 0.21F) % 1.0F;
            float y0 = segOffset * (h * 0.72F);
            float y1 = y0 + 2.4F;
            float a0 = baseAng + y0 * 0.35F;
            float a1 = baseAng + y1 * 0.35F;
            float rad = r * 0.72F;
            float x0 = Mth.cos(a0) * rad, z0 = Mth.sin(a0) * rad;
            float x1 = Mth.cos(a1) * rad, z1 = Mth.sin(a1) * rad;
            float tx = -Mth.sin(a0) * 0.10F, tz = Mth.cos(a0) * 0.10F;
            int al = (int) (165 * fade * Mth.sin(segOffset * Mth.PI));
            if (al > 4) {
                vc.addVertex(m, x0 - tx, y0, z0 - tz).setColor(150, 255, 236, al / 3);
                vc.addVertex(m, x0 + tx, y0, z0 + tz).setColor(150, 255, 236, al / 3);
                vc.addVertex(m, x1 + tx, y1, z1 + tz).setColor(235, 255, 252, al);
                vc.addVertex(m, x1 - tx, y1, z1 - tz).setColor(235, 255, 252, al);
                vc.addVertex(m, x1 - tx, y1, z1 - tz).setColor(235, 255, 252, al);
                vc.addVertex(m, x1 + tx, y1, z1 + tz).setColor(235, 255, 252, al);
                vc.addVertex(m, x0 + tx, y0, z0 + tz).setColor(150, 255, 236, al / 3);
                vc.addVertex(m, x0 - tx, y0, z0 - tz).setColor(150, 255, 236, al / 3);
            }
        }

        super.render(entity, yaw, partialTicks, poseStack, bufferSource, packedLight);
    }

    private static void drawRing(VertexConsumer vc, Matrix4f mat, float y, float rIn, float rOut, int segs, int r, int g, int b, int a) {
        if (a <= 3) return;
        for (int i = 0; i < segs; i++) {
            float a0 = (float) (Math.PI * 2.0D * i / segs);
            float a1 = (float) (Math.PI * 2.0D * (i + 1) / segs);
            float c0 = Mth.cos(a0), s0 = Mth.sin(a0);
            float c1 = Mth.cos(a1), s1 = Mth.sin(a1);
            vc.addVertex(mat, c0 * rIn, y, s0 * rIn).setColor(r, g, b, a);
            vc.addVertex(mat, c0 * rOut, y, s0 * rOut).setColor(r, g, b, a);
            vc.addVertex(mat, c1 * rOut, y, s1 * rOut).setColor(r, g, b, a);
            vc.addVertex(mat, c1 * rIn, y, s1 * rIn).setColor(r, g, b, a);
            vc.addVertex(mat, c1 * rIn, y, s1 * rIn).setColor(r, g, b, a);
            vc.addVertex(mat, c1 * rOut, y, s1 * rOut).setColor(r, g, b, a);
            vc.addVertex(mat, c0 * rOut, y, s0 * rOut).setColor(r, g, b, a);
            vc.addVertex(mat, c0 * rIn, y, s0 * rIn).setColor(r, g, b, a);
        }
    }

    @Override
    public ResourceLocation getTextureLocation(WindCurrentEntity entity) {
        return TEX;
    }
}

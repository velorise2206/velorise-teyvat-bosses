package com.velorise.teyvatbosses.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.velorise.teyvatbosses.TeyvatBossesConstants;
import com.velorise.teyvatbosses.entity.AnemoWindOrbEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

/**
 * Renders floating Anemo Orbs and Revival Wind Crystals as glowing turquoise
 * crystal octahedrons surrounded by twin orbiting Anemo energy halos.
 */
public class AnemoWindOrbRenderer extends EntityRenderer<AnemoWindOrbEntity> {
    private static final ResourceLocation TEX = TeyvatBossesConstants.id("textures/entity/anemo_hypostasis.png");

    public AnemoWindOrbRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(AnemoWindOrbEntity entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        float t = entity.tickCount + partialTicks;
        boolean revival = entity.isRevivalCrystal();
        float scale = (revival ? 1.15F : 0.85F) * (1.0F + 0.08F * Mth.sin(t * 0.25F));

        VertexConsumer vc = bufferSource.getBuffer(RenderType.lightning());
        poseStack.pushPose();
        poseStack.translate(0.0D, Mth.sin(t * 0.14F) * 0.18D, 0.0D);

        // Inner bright white-mint crystal
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(t * 3.2F));
        drawOctahedron(vc, poseStack.last().pose(), 0.42F * scale, 225, 255, 248, 240);
        drawOctahedron(vc, poseStack.last().pose(), 0.68F * scale, 80, 245, 212, 130);
        poseStack.popPose();

        // Twin orbiting turquoise energy rings
        for (int ring = 0; ring < 2; ring++) {
            poseStack.pushPose();
            poseStack.mulPose(Axis.YP.rotationDegrees(t * (ring == 0 ? 4.0F : -3.2F)));
            poseStack.mulPose(Axis.XP.rotationDegrees(ring == 0 ? 58.0F : -58.0F));
            drawFlatRing(vc, poseStack.last().pose(), 0.76F * scale, 0.90F * scale, 20, 135, 255, 232, 195);
            poseStack.popPose();
        }

        poseStack.popPose();
        super.render(entity, yaw, partialTicks, poseStack, bufferSource, packedLight);
    }

    private static void drawOctahedron(VertexConsumer vc, Matrix4f mat, float r, int cr, int cg, int cb, int ca) {
        float[][] pts = {{r, 0}, {0, r}, {-r, 0}, {0, -r}};
        float tipY = r * 1.35F;
        for (int i = 0; i < 4; i++) {
            float[] a = pts[i];
            float[] b = pts[(i + 1) % 4];
            // Upper pyramid face (RenderType.lightning() uses QUADS, so emit 1 quad per triangle)
            vc.addVertex(mat, 0.0F, tipY, 0.0F).setColor(cr, cg, cb, ca);
            vc.addVertex(mat, a[0], 0.0F, a[1]).setColor(cr, cg, cb, ca);
            vc.addVertex(mat, b[0], 0.0F, b[1]).setColor(cr, cg, cb, ca);
            vc.addVertex(mat, b[0], 0.0F, b[1]).setColor(cr, cg, cb, ca);

            // Lower pyramid face
            vc.addVertex(mat, 0.0F, -tipY, 0.0F).setColor(cr, cg, cb, ca);
            vc.addVertex(mat, b[0], 0.0F, b[1]).setColor(cr, cg, cb, ca);
            vc.addVertex(mat, a[0], 0.0F, a[1]).setColor(cr, cg, cb, ca);
            vc.addVertex(mat, a[0], 0.0F, a[1]).setColor(cr, cg, cb, ca);
        }
    }

    private static void drawFlatRing(VertexConsumer vc, Matrix4f mat, float rIn, float rOut, int segs, int r, int g, int b, int a) {
        for (int i = 0; i < segs; i++) {
            float a0 = (float) (Math.PI * 2.0D * i / segs);
            float a1 = (float) (Math.PI * 2.0D * (i + 1) / segs);
            float c0 = Mth.cos(a0), s0 = Mth.sin(a0);
            float c1 = Mth.cos(a1), s1 = Mth.sin(a1);
            vc.addVertex(mat, c0 * rIn, 0.0F, s0 * rIn).setColor(r, g, b, a);
            vc.addVertex(mat, c0 * rOut, 0.0F, s0 * rOut).setColor(r, g, b, a);
            vc.addVertex(mat, c1 * rOut, 0.0F, s1 * rOut).setColor(r, g, b, a);
            vc.addVertex(mat, c1 * rIn, 0.0F, s1 * rIn).setColor(r, g, b, a);
            vc.addVertex(mat, c1 * rIn, 0.0F, s1 * rIn).setColor(r, g, b, a);
            vc.addVertex(mat, c1 * rOut, 0.0F, s1 * rOut).setColor(r, g, b, a);
            vc.addVertex(mat, c0 * rOut, 0.0F, s0 * rOut).setColor(r, g, b, a);
            vc.addVertex(mat, c0 * rIn, 0.0F, s0 * rIn).setColor(r, g, b, a);
        }
    }

    @Override
    public ResourceLocation getTextureLocation(AnemoWindOrbEntity entity) {
        return TEX;
    }
}

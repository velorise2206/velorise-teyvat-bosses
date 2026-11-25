package com.velorise.teyvatbosses.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.velorise.teyvatbosses.TeyvatBossesConstants;
import com.velorise.teyvatbosses.entity.CryoIceSpikeEntity;
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
 * Renders CryoIceSpikeEntity with:
 * 1) A rotating glowing cyan frost sigil ring on the ground during telegraph phase.
 * 2) A fullbright 3D multi-crystal glacial spire cluster (central tapered hex crystal + 4 angled side crystals)
 *    with inner glowing emissive core on eruption.
 */
public class CryoIceSpikeRenderer extends EntityRenderer<CryoIceSpikeEntity> {
    private static final ResourceLocation TEXTURE = TeyvatBossesConstants.id("textures/entity/cryo_ice_spike.png");

    public CryoIceSpikeRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(
            CryoIceSpikeEntity entity,
            float entityYaw,
            float partialTicks,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight
    ) {
        float age = entity.tickCount + partialTicks;
        int mode = entity.getMode();
        float sc = entity.getSpikeScale();
        int eruptTick = (mode == CryoIceSpikeEntity.MODE_PILLAR) ? CryoIceSpikeEntity.PILLAR_TELEGRAPH_TICKS : 2;
        int maxTicks = (mode == CryoIceSpikeEntity.MODE_PILLAR) ? CryoIceSpikeEntity.PILLAR_LIFETIME_TICKS : CryoIceSpikeEntity.SLAM_LIFETIME_TICKS;

        poseStack.pushPose();

        // 1. Rotating ground telegraph sigil ring (MODE_PILLAR before & slightly after eruption)
        if (mode == CryoIceSpikeEntity.MODE_PILLAR && age < eruptTick + 6.0F) {
            float progress = Mth.clamp(age / (float) eruptTick, 0.0F, 1.0F);
            float fade = age <= eruptTick ? (0.45F + 0.55F * progress) : Math.max(0.0F, 1.0F - (age - eruptTick) / 6.0F);
            VertexConsumer lvc = bufferSource.getBuffer(RenderType.lightning());
            Matrix4f lm = poseStack.last().pose();
            float outerR = (1.65F * sc) * (1.08F - 0.08F * progress);
            float innerR = outerR * (0.25F + 0.65F * progress);
            drawGroundRing(lvc, lm, outerR - 0.12F, outerR, 24, 150, 238, 255, (int) (210 * fade));
            drawGroundRing(lvc, lm, innerR - 0.07F, innerR, 20, 225, 252, 255, (int) (175 * fade));

            // Rotating 6-point snowflake star rays inside sigil
            poseStack.pushPose();
            poseStack.mulPose(Axis.YP.rotationDegrees(age * 9.0F));
            Matrix4f rm = poseStack.last().pose();
            for (int i = 0; i < 6; i++) {
                float a = (float) (i * Math.PI / 3.0D);
                float c = Mth.cos(a), s = Mth.sin(a);
                float px = -s * 0.055F, pz = c * 0.055F;
                float len = outerR * 0.92F;
                int al = (int) (165 * fade);
                lvc.addVertex(rm, px, 0.055F, pz).setColor(210, 248, 255, al);
                lvc.addVertex(rm, -px, 0.055F, -pz).setColor(210, 248, 255, al);
                lvc.addVertex(rm, c * len - px, 0.055F, s * len - pz).setColor(105, 215, 255, al / 2);
                lvc.addVertex(rm, c * len + px, 0.055F, s * len + pz).setColor(105, 215, 255, al / 2);
            }
            poseStack.popPose();
        }

        // 2. 3D Crystalline Spire Cluster after eruption
        if (age >= eruptTick - 0.5F) {
            float since = Math.max(0.0F, age - (eruptTick - 0.5F));
            float growT = Mth.clamp(since / 3.0F, 0.0F, 1.0F);
            // Springy easeOutBack pop-up
            float pop = 1.0F + 2.1F * (float) Math.pow(growT - 1.0F, 3.0D) + 1.1F * (float) Math.pow(growT - 1.0F, 2.0D);
            float shrink = age > maxTicks - 5.0F ? Math.max(0.0F, (maxTicks - age) / 5.0F) : 1.0F;
            float heightScale = sc * Mth.clamp(pop, 0.05F, 1.12F) * shrink;
            float widthScale = sc * (0.75F + 0.25F * growT) * shrink;

            poseStack.mulPose(Axis.YP.rotationDegrees(entity.getSpinYaw()));

            // Textured translucent fullbright crystalline outer spikes
            VertexConsumer tvc = bufferSource.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
            float mainH = (mode == CryoIceSpikeEntity.MODE_PILLAR ? 3.55F : 2.35F) * heightScale;
            float mainR = (mode == CryoIceSpikeEntity.MODE_PILLAR ? 0.62F : 0.44F) * widthScale;

            // Central towering 6-sided spire
            renderCrystalSpire(poseStack, tvc, mainR, mainH, 6, 0xFFFFFFFF);

            // 4 surrounding angled smaller crystal shards
            float[] angles = { 18.0F, 108.0F, 202.0F, 294.0F };
            float[] tilts  = { 24.0F, 30.0F, 22.0F, 28.0F };
            float[] hMults = { 0.62F, 0.52F, 0.68F, 0.48F };
            for (int i = 0; i < 4; i++) {
                poseStack.pushPose();
                poseStack.mulPose(Axis.YP.rotationDegrees(angles[i]));
                poseStack.translate(mainR * 0.65F, -0.05F, 0.0F);
                poseStack.mulPose(Axis.ZP.rotationDegrees(-tilts[i]));
                renderCrystalSpire(poseStack, tvc, mainR * 0.56F, mainH * hMults[i], 5, 0xFFDDF8FF);
                poseStack.popPose();
            }

            // Inner glowing emissive cyan-white frost core
            VertexConsumer lvc = bufferSource.getBuffer(RenderType.lightning());
            renderGlowCore(poseStack.last().pose(), lvc, mainR * 0.46F, mainH * 0.88F);
        }

        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
    }

    private static void renderCrystalSpire(PoseStack poseStack, VertexConsumer vc, float radius, float height, int sides, int argb) {
        PoseStack.Pose pose = poseStack.last();
        Matrix4f m = pose.pose();
        float midY = height * 0.28F;
        float midR = radius * 1.12F;

        for (int i = 0; i < sides; i++) {
            double a0 = (Math.PI * 2.0D * i) / sides;
            double a1 = (Math.PI * 2.0D * (i + 1)) / sides;
            float c0 = (float) Math.cos(a0), s0 = (float) Math.sin(a0);
            float c1 = (float) Math.cos(a1), s1 = (float) Math.sin(a1);
            float nx = (c0 + c1) * 0.5F;
            float nz = (s0 + s1) * 0.5F;
            float u0 = (float) i / sides;
            float u1 = (float) (i + 1) / sides;

            // Lower flared base facet
            addTexVert(vc, pose, m, c0 * radius, 0.0F, s0 * radius, u0, 1.0F, nx, 0.1F, nz, argb);
            addTexVert(vc, pose, m, c1 * radius, 0.0F, s1 * radius, u1, 1.0F, nx, 0.1F, nz, argb);
            addTexVert(vc, pose, m, c1 * midR, midY, s1 * midR, u1, 0.65F, nx, 0.1F, nz, argb);
            addTexVert(vc, pose, m, c0 * midR, midY, s0 * midR, u0, 0.65F, nx, 0.1F, nz, argb);

            // Upper tapered sharp tip facet (degenerate quad -> clean triangle)
            addTexVert(vc, pose, m, c0 * midR, midY, s0 * midR, u0, 0.65F, nx, 0.35F, nz, argb);
            addTexVert(vc, pose, m, c1 * midR, midY, s1 * midR, u1, 0.65F, nx, 0.35F, nz, argb);
            addTexVert(vc, pose, m, 0.0F, height, 0.0F, (u0 + u1) * 0.5F, 0.0F, nx, 0.5F, nz, argb);
            addTexVert(vc, pose, m, 0.0F, height, 0.0F, (u0 + u1) * 0.5F, 0.0F, nx, 0.5F, nz, argb);
        }
    }

    private static void addTexVert(VertexConsumer vc, PoseStack.Pose pose, Matrix4f m,
                                   float x, float y, float z, float u, float v,
                                   float nx, float ny, float nz, int argb) {
        vc.addVertex(m, x, y, z)
                .setColor(argb)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(LightTexture.FULL_BRIGHT)
                .setNormal(pose, nx, ny, nz);
    }

    private static void renderGlowCore(Matrix4f m, VertexConsumer vc, float r, float h) {
        float[][] ring = { {0, r}, {r, 0}, {0, -r}, {-r, 0} };
        for (int i = 0; i < 4; i++) {
            float[] a = ring[i];
            float[] b = ring[(i + 1) % 4];
            vc.addVertex(m, a[0], 0.1F, a[1]).setColor(90, 215, 255, 130);
            vc.addVertex(m, b[0], 0.1F, b[1]).setColor(90, 215, 255, 130);
            vc.addVertex(m, 0.0F, h, 0.0F).setColor(235, 253, 255, 195);
            vc.addVertex(m, 0.0F, h, 0.0F).setColor(235, 253, 255, 195);
        }
    }

    private static void drawGroundRing(VertexConsumer vc, Matrix4f mat, float rIn, float rOut, int segs, int r, int g, int b, int a) {
        for (int i = 0; i < segs; i++) {
            double a0 = (Math.PI * 2.0D * i) / segs;
            double a1 = (Math.PI * 2.0D * (i + 1)) / segs;
            float c0 = (float) Math.cos(a0), s0 = (float) Math.sin(a0);
            float c1 = (float) Math.cos(a1), s1 = (float) Math.sin(a1);
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
    public ResourceLocation getTextureLocation(CryoIceSpikeEntity entity) {
        return TEXTURE;
    }
}

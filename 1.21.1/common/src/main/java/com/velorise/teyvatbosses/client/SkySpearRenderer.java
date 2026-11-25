package com.velorise.teyvatbosses.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.velorise.teyvatbosses.TeyvatBossesConstants;
import com.velorise.teyvatbosses.entity.ElementalStarEntity;
import com.velorise.teyvatbosses.entity.SkySpearEntity;
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
 * Renders SkySpearEntity with:
 * 1) Crimson-red & elemental 4-point Celestial Star ground telegraph sigil (eye.mp4 01:27).
 * 2) Towering 9-block textured fullbright 3D Celestial Lance plunging from the sky (eye.mp4 01:28-01:29).
 * 3) Expanding luminous shockwave ring + pillar of heavenly light on impact.
 */
public class SkySpearRenderer extends EntityRenderer<SkySpearEntity> {
    private static final ResourceLocation TEXTURE = TeyvatBossesConstants.id("textures/entity/sky_spear.png");

    public SkySpearRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(SkySpearEntity entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        float age = entity.tickCount + partialTicks;
        float r = entity.getBlastRadius();
        int[] rgb = colorForElement(entity.getElementId());

        poseStack.pushPose();
        // 1. Crimson & Elemental 4-Point Star Ground Telegraph Sigil (ticks 0..24)
        if (age < 24.0F) {
            VertexConsumer lvc = bufferSource.getBuffer(RenderType.lightning());
            float fade = age <= SkySpearEntity.IMPACT_TICK
                    ? (0.45F + 0.55F * Mth.clamp(age / (float) SkySpearEntity.IMPACT_TICK, 0.0F, 1.0F))
                    : Math.max(0.0F, 1.0F - (age - SkySpearEntity.IMPACT_TICK) / 8.0F);
            Matrix4f m = poseStack.last().pose();
            drawGroundRing(lvc, m, r - 0.22F, r, 32, 235, 45, 55, (int) (215 * fade));
            drawGroundRing(lvc, m, r * 0.55F - 0.14F, r * 0.55F, 24, rgb[0], rgb[1], rgb[2], (int) (185 * fade));

            // Iconic 4-point curved crimson star inside the circle (eye.mp4 01:27)
            poseStack.pushPose();
            poseStack.mulPose(Axis.YP.rotationDegrees(age * 2.5F));
            Matrix4f sm = poseStack.last().pose();
            for (int i = 0; i < 4; i++) {
                float ang = (float) (i * Math.PI * 0.5D);
                float c = Mth.cos(ang), s = Mth.sin(ang);
                float px = -s * 0.42F, pz = c * 0.42F;
                int al = (int) (200 * fade);
                lvc.addVertex(sm, px, 0.08F, pz).setColor(215, 25, 35, al);
                lvc.addVertex(sm, -px, 0.08F, -pz).setColor(215, 25, 35, al);
                lvc.addVertex(sm, c * r * 0.96F, 0.08F, s * r * 0.96F).setColor(255, 90, 65, al);
                lvc.addVertex(sm, c * r * 0.96F, 0.08F, s * r * 0.96F).setColor(255, 90, 65, al);
                lvc.addVertex(sm, c * r * 0.96F, 0.08F, s * r * 0.96F).setColor(255, 90, 65, al);
                lvc.addVertex(sm, c * r * 0.96F, 0.08F, s * r * 0.96F).setColor(255, 90, 65, al);
                lvc.addVertex(sm, -px, 0.08F, -pz).setColor(215, 25, 35, al);
                lvc.addVertex(sm, px, 0.08F, pz).setColor(215, 25, 35, al);
            }
            poseStack.popPose();
        }

        // 2. Plunging Towering 3D Celestial Spear (ticks 11..38)
        if (age >= SkySpearEntity.TELEGRAPH_TICKS - 1.0F) {
            float dropT = Mth.clamp((age - (SkySpearEntity.TELEGRAPH_TICKS - 1.0F)) / 5.0F, 0.0F, 1.0F);
            // Accelerating plunge from Y+18 down to Y=0
            float yOffset = (1.0F - dropT * dropT) * 18.0F;
            float fadeOut = age > SkySpearEntity.LIFETIME_TICKS - 6.0F
                    ? Math.max(0.0F, (SkySpearEntity.LIFETIME_TICKS - age) / 6.0F)
                    : 1.0F;

            poseStack.pushPose();
            poseStack.translate(0.0D, yOffset, 0.0D);
            poseStack.scale(fadeOut, 1.0F, fadeOut);
            poseStack.mulPose(Axis.YP.rotationDegrees(age * 3.0F));

            // Fullbright textured 3D lance blade & shaft
            VertexConsumer tvc = bufferSource.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
            int tint = (0xFF << 24) | ((Math.min(255, (rgb[0] + 255) / 2)) << 16)
                    | ((Math.min(255, (rgb[1] + 255) / 2)) << 8)
                    | (Math.min(255, (rgb[2] + 255) / 2));
            renderSpearMesh(poseStack, tvc, tint);

            // Luminous outer energy sheath & sky beam (fresh lightning buffer after entityTranslucent!)
            Matrix4f bm = poseStack.last().pose();
            VertexConsumer beamVc = bufferSource.getBuffer(RenderType.lightning());
            drawLightShaft(beamVc, bm, 0.32F, 10.5F, rgb[0], rgb[1], rgb[2], (int) (135 * fadeOut));
            poseStack.popPose();
        }

        // 3. Impact Expanding Shockwave Ring (ticks 16..28)
        if (age >= SkySpearEntity.IMPACT_TICK && age < SkySpearEntity.IMPACT_TICK + 12.0F) {
            VertexConsumer lvc = bufferSource.getBuffer(RenderType.lightning());
            float p = (age - SkySpearEntity.IMPACT_TICK) / 12.0F;
            float waveR = 0.8F + r * 1.15F * p;
            int al = (int) (220 * (1.0F - p));
            drawGroundRing(lvc, poseStack.last().pose(), Math.max(0.1F, waveR - 0.55F), waveR, 32, rgb[0], rgb[1], rgb[2], al);
        }

        poseStack.popPose();
        super.render(entity, yaw, partialTicks, poseStack, bufferSource, packedLight);
    }

    private static void renderSpearMesh(PoseStack poseStack, VertexConsumer vc, int argb) {
        PoseStack.Pose pose = poseStack.last();
        Matrix4f m = pose.pose();
        // Sharp 4-sided blade tip (y = 0 -> 3.0), broad crossguard diamond (y = 3.0 -> 4.2), tall shaft (y = 4.2 -> 9.2)
        float[][] bRing = { {0.34F, 0}, {0, 0.34F}, {-0.34F, 0}, {0, -0.34F} };
        float[][] gRing = { {1.25F, 0}, {0, 0.28F}, {-1.25F, 0}, {0, -0.28F} };
        float[][] sRing = { {0.22F, 0}, {0, 0.22F}, {-0.22F, 0}, {0, -0.22F} };
        for (int i = 0; i < 4; i++) {
            float[] a = bRing[i], b = bRing[(i + 1) % 4];
            // Tip cone pointing down to y=0
            addVert(vc, pose, m, a[0], 2.8F, a[1], 0, 0.4F, argb);
            addVert(vc, pose, m, b[0], 2.8F, b[1], 1, 0.4F, argb);
            addVert(vc, pose, m, 0, 0.0F, 0, 0.5F, 0.0F, argb);
            addVert(vc, pose, m, 0, 0.0F, 0, 0.5F, 0.0F, argb);

            // Crossguard flare (2.8 -> 3.8)
            float[] ga = gRing[i], gb = gRing[(i + 1) % 4];
            addVert(vc, pose, m, a[0], 2.8F, a[1], 0, 0.5F, argb);
            addVert(vc, pose, m, b[0], 2.8F, b[1], 1, 0.5F, argb);
            addVert(vc, pose, m, gb[0], 3.8F, gb[1], 1, 0.7F, argb);
            addVert(vc, pose, m, ga[0], 3.8F, ga[1], 0, 0.7F, argb);

            // Upper shaft (3.8 -> 9.0)
            float[] sa = sRing[i], sb = sRing[(i + 1) % 4];
            addVert(vc, pose, m, sa[0], 3.8F, sa[1], 0, 0.7F, argb);
            addVert(vc, pose, m, sb[0], 3.8F, sb[1], 1, 0.7F, argb);
            addVert(vc, pose, m, sb[0], 9.0F, sb[1], 1, 1.0F, argb);
            addVert(vc, pose, m, sa[0], 9.0F, sa[1], 0, 1.0F, argb);
        }
    }

    private static void addVert(VertexConsumer vc, PoseStack.Pose pose, Matrix4f m,
                                float x, float y, float z, float u, float v, int argb) {
        vc.addVertex(m, x, y, z)
                .setColor(argb)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(LightTexture.FULL_BRIGHT)
                .setNormal(pose, 0, 1, 0);
    }

    private static void drawLightShaft(VertexConsumer vc, Matrix4f m, float rad, float h, int r, int g, int b, int a) {
        int segs = 8;
        for (int i = 0; i < segs; i++) {
            double a0 = (Math.PI * 2.0D * i) / segs;
            double a1 = (Math.PI * 2.0D * (i + 1)) / segs;
            float x0 = (float) Math.cos(a0) * rad, z0 = (float) Math.sin(a0) * rad;
            float x1 = (float) Math.cos(a1) * rad, z1 = (float) Math.sin(a1) * rad;
            vc.addVertex(m, x0, 0.0F, z0).setColor(255, 250, 235, a);
            vc.addVertex(m, x1, 0.0F, z1).setColor(255, 250, 235, a);
            vc.addVertex(m, x1 * 1.4F, h, z1 * 1.4F).setColor(r, g, b, a / 3);
            vc.addVertex(m, x0 * 1.4F, h, z0 * 1.4F).setColor(r, g, b, a / 3);
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

    private static int[] colorForElement(int id) {
        return switch (id) {
            case ElementalStarEntity.ELEM_PYRO    -> new int[]{255, 105, 45};
            case ElementalStarEntity.ELEM_HYDRO   -> new int[]{60, 180, 255};
            case ElementalStarEntity.ELEM_ELECTRO -> new int[]{195, 105, 255};
            case ElementalStarEntity.ELEM_CRYO    -> new int[]{140, 238, 255};
            default                               -> new int[]{255, 205, 85};
        };
    }

    @Override
    public ResourceLocation getTextureLocation(SkySpearEntity entity) {
        return TEXTURE;
    }
}

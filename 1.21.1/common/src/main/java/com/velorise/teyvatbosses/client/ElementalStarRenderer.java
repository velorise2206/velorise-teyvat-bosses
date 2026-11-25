package com.velorise.teyvatbosses.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.velorise.teyvatbosses.TeyvatBossesConstants;
import com.velorise.teyvatbosses.entity.ElementalStarEntity;
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
 * Solid, fully opaque 3D 4-Point Celestial Star renderer (entityCutoutNoCull + opaque texture, zero see-through).
 */
public class ElementalStarRenderer extends EntityRenderer<ElementalStarEntity> {
    private static final ResourceLocation TEXTURE = TeyvatBossesConstants.id("textures/entity/elemental_star.png");

    public ElementalStarRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(ElementalStarEntity entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        float t = entity.tickCount + partialTicks;
        int[] rgb = colorForElement(entity.getElementId());
        int argb = 0xFF000000 | (rgb[0] << 16) | (rgb[1] << 8) | rgb[2];
        float pulse = 1.0F + 0.08F * Mth.sin(t * 0.22F);

        // 1. Solid Opaque 3D 4-Point Faceted Star (100% opaque depth-writing mesh — never see-through!)
        poseStack.pushPose();
        poseStack.translate(0.0D, 1.15D + Mth.sin(t * 0.12F) * 0.12D, 0.0D);
        poseStack.mulPose(Axis.YP.rotationDegrees(t * 2.8F));

        VertexConsumer tvc = bufferSource.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        renderSolid3DStar(poseStack, tvc, 1.08F * pulse, 0.78F * pulse, 0.28F * pulse, argb);
        poseStack.popPose();

        // 2. Subtle outer ground marker & tilted halo ring (fresh lightning buffer)
        VertexConsumer lvc = bufferSource.getBuffer(RenderType.lightning());
        poseStack.pushPose();
        drawHorizontalRingDS(lvc, poseStack.last().pose(), 0.85F, 1.02F, 20, rgb[0], rgb[1], rgb[2], 145);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(0.0D, 1.15D + Mth.sin(t * 0.12F) * 0.12D, 0.0D);
        poseStack.mulPose(Axis.XP.rotationDegrees(62.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(t * 4.5F));
        drawHorizontalRingDS(lvc, poseStack.last().pose(), 0.86F, 0.96F, 20, rgb[0], rgb[1], rgb[2], 165);
        poseStack.popPose();

        super.render(entity, yaw, partialTicks, poseStack, bufferSource, packedLight);
    }

    /**
     * Renders a watertight, solid 3D 6-pointed / 4-pointed celestial star crystal with 6 pyramids
     * (+Y, -Y, +X, -X, +Z, -Z) joined cleanly at a solid inner octahedral core.
     */
    public static void renderSolid3DStar(PoseStack poseStack, VertexConsumer vc, float vLen, float hLen, float coreW, int argb) {
        PoseStack.Pose pose = poseStack.last();
        Matrix4f m = pose.pose();
        float[][] tips = {
                {0, vLen, 0}, {0, -vLen, 0},
                {hLen, 0, 0}, {-hLen, 0, 0},
                {0, 0, hLen}, {0, 0, -hLen}
        };
        float[][][] bases = {
                {{coreW,0,0}, {0,0,coreW}, {-coreW,0,0}, {0,0,-coreW}},
                {{coreW,0,0}, {0,0,-coreW}, {-coreW,0,0}, {0,0,coreW}},
                {{0,coreW,0}, {0,0,-coreW}, {0,-coreW,0}, {0,0,coreW}},
                {{0,coreW,0}, {0,0,coreW}, {0,-coreW,0}, {0,0,-coreW}},
                {{0,coreW,0}, {coreW,0,0}, {0,-coreW,0}, {-coreW,0,0}},
                {{0,coreW,0}, {-coreW,0,0}, {0,-coreW,0}, {coreW,0,0}}
        };
        for (int s = 0; s < 6; s++) {
            float[] tip = tips[s];
            float[][] ring = bases[s];
            for (int i = 0; i < 4; i++) {
                float[] a = ring[i];
                float[] b = ring[(i + 1) % 4];
                addOpaqueVert(vc, pose, m, a[0], a[1], a[2], 0.0F, 1.0F, argb);
                addOpaqueVert(vc, pose, m, b[0], b[1], b[2], 1.0F, 1.0F, argb);
                addOpaqueVert(vc, pose, m, tip[0], tip[1], tip[2], 0.5F, 0.0F, 0xFFFFFFFF);
                addOpaqueVert(vc, pose, m, tip[0], tip[1], tip[2], 0.5F, 0.0F, 0xFFFFFFFF);
            }
        }
    }

    private static void addOpaqueVert(VertexConsumer vc, PoseStack.Pose pose, Matrix4f m,
                                      float x, float y, float z, float u, float v, int argb) {
        vc.addVertex(m, x, y, z)
                .setColor(argb)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(LightTexture.FULL_BRIGHT)
                .setNormal(pose, 0, 1, 0);
    }

    private static void drawHorizontalRingDS(VertexConsumer vc, Matrix4f mat, float rIn, float rOut, int segs, int r, int g, int b, int a) {
        for (int i = 0; i < segs; i++) {
            float a0 = (float) (Math.PI * 2.0D * i / segs);
            float a1 = (float) (Math.PI * 2.0D * (i + 1) / segs);
            float c0 = Mth.cos(a0), s0 = Mth.sin(a0);
            float c1 = Mth.cos(a1), s1 = Mth.sin(a1);
            vc.addVertex(mat, c0 * rIn, 0.06F, s0 * rIn).setColor(r, g, b, a);
            vc.addVertex(mat, c0 * rOut, 0.06F, s0 * rOut).setColor(r, g, b, a);
            vc.addVertex(mat, c1 * rOut, 0.06F, s1 * rOut).setColor(r, g, b, a);
            vc.addVertex(mat, c1 * rIn, 0.06F, s1 * rIn).setColor(r, g, b, a);

            vc.addVertex(mat, c1 * rIn, 0.06F, s1 * rIn).setColor(r, g, b, a);
            vc.addVertex(mat, c1 * rOut, 0.06F, s1 * rOut).setColor(r, g, b, a);
            vc.addVertex(mat, c0 * rOut, 0.06F, s0 * rOut).setColor(r, g, b, a);
            vc.addVertex(mat, c0 * rIn, 0.06F, s0 * rIn).setColor(r, g, b, a);
        }
    }

    private static int[] colorForElement(int id) {
        return switch (id) {
            case ElementalStarEntity.ELEM_PYRO    -> new int[]{255, 110, 55};
            case ElementalStarEntity.ELEM_HYDRO   -> new int[]{65, 185, 255};
            case ElementalStarEntity.ELEM_ELECTRO -> new int[]{200, 110, 255};
            case ElementalStarEntity.ELEM_CRYO    -> new int[]{140, 240, 255};
            default                               -> new int[]{245, 205, 95};
        };
    }

    @Override
    public ResourceLocation getTextureLocation(ElementalStarEntity entity) {
        return TEXTURE;
    }
}

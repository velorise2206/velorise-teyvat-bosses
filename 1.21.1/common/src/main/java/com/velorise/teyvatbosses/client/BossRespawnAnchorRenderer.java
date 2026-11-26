package com.velorise.teyvatbosses.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.velorise.teyvatbosses.TeyvatBossesConstants;
import com.velorise.teyvatbosses.entity.BossRespawnAnchorEntity;
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
 * Renders BossRespawnAnchorEntity as a solid, fully opaque 3D Genshin Ley Line Blossom Crystal
 * with a glowing ground sigil ring.
 */
public class BossRespawnAnchorRenderer extends EntityRenderer<BossRespawnAnchorEntity> {
    private static final ResourceLocation TEXTURE = TeyvatBossesConstants.id("textures/entity/boss_respawn_anchor.png");

    public BossRespawnAnchorRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(BossRespawnAnchorEntity entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        float t = entity.tickCount + partialTicks;
        int kind = entity.getBossKind();
        int r, g, b;
        if (kind == BossRespawnAnchorEntity.BOSS_CRYO_REGISVINE) {
            r = 110; g = 230; b = 255;
        } else if (kind == BossRespawnAnchorEntity.BOSS_ANEMO_HYPOSTASIS) {
            r = 107; g = 245; b = 212;
        } else if (kind == BossRespawnAnchorEntity.BOSS_RUIN_NAUTILUS) {
            r = 70; g = 195; b = 255;
        } else {
            r = 255; g = 205; b = 85;
        }
        int argb = 0xFF000000 | (r << 16) | (g << 8) | b;

        poseStack.pushPose();
        poseStack.translate(0.0D, 1.35D + Mth.sin(t * 0.1F) * 0.16D, 0.0D);
        poseStack.mulPose(Axis.YP.rotationDegrees(t * 2.5F));
        VertexConsumer tvc = bufferSource.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        renderSolid3DStar(poseStack, tvc, 0.92F, 0.66F, 0.25F, argb);
        poseStack.popPose();

        VertexConsumer lvc = bufferSource.getBuffer(RenderType.lightning());
        poseStack.pushPose();
        drawRingDS(lvc, poseStack.last().pose(), 1.05F, 1.25F, 24, r, g, b, 155);
        poseStack.popPose();

        super.render(entity, yaw, partialTicks, poseStack, bufferSource, packedLight);
    }

    private static void renderSolid3DStar(PoseStack poseStack, VertexConsumer vc, float vLen, float hLen, float coreW, int argb) {
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
                addOpaqueVert(vc, pose, m, b[0], b[1], b[2], 0.0F, 1.0F, argb);
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

    private static void drawRingDS(VertexConsumer vc, Matrix4f mat, float rIn, float rOut, int segs, int r, int g, int b, int a) {
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

    @Override
    public ResourceLocation getTextureLocation(BossRespawnAnchorEntity entity) {
        return TEXTURE;
    }
}

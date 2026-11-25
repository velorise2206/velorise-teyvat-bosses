package com.velorise.teyvatbosses.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.velorise.teyvatbosses.TeyvatBossesConstants;
import com.velorise.teyvatbosses.entity.BossRespawnAnchorEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

/**
 * Renders BossRespawnAnchorEntity as a solid, fully opaque 3D Genshin Ley Line Blossom Crystal
 * (never see-through) with a glowing ground sigil ring.
 */
public class BossRespawnAnchorRenderer extends EntityRenderer<BossRespawnAnchorEntity> {
    private static final ResourceLocation TEXTURE = TeyvatBossesConstants.id("textures/entity/boss_respawn_anchor.png");

    public BossRespawnAnchorRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(BossRespawnAnchorEntity entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        float t = entity.tickCount + partialTicks;
        boolean cryo = entity.getBossKind() == BossRespawnAnchorEntity.BOSS_CRYO_REGISVINE;
        int r = cryo ? 110 : 255;
        int g = cryo ? 230 : 210;
        int b = cryo ? 255 : 100;
        int argb = 0xFF000000 | (r << 16) | (g << 8) | b;

        // 1. Solid Opaque 3D Ley Line Blossom Crystal (entityCutoutNoCull, zero see-through!)
        poseStack.pushPose();
        poseStack.translate(0.0D, 1.35D + Mth.sin(t * 0.1F) * 0.16D, 0.0D);
        poseStack.mulPose(Axis.YP.rotationDegrees(t * 2.5F));
        VertexConsumer tvc = bufferSource.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        ElementalStarRenderer.renderSolid3DStar(poseStack, tvc, 0.92F, 0.66F, 0.25F, argb);
        poseStack.popPose();

        // 2. Ground double-sided sigil ring
        VertexConsumer lvc = bufferSource.getBuffer(RenderType.lightning());
        poseStack.pushPose();
        drawRingDS(lvc, poseStack.last().pose(), 1.05F, 1.25F, 24, r, g, b, 155);
        poseStack.popPose();

        super.render(entity, yaw, partialTicks, poseStack, bufferSource, packedLight);
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

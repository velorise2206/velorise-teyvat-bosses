package com.velorise.teyvatbosses.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.velorise.teyvatbosses.TeyvatBossesConstants;
import com.velorise.teyvatbosses.entity.AnemoMissileProjectile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

/**
 * Renders Anemo Hypostasis projectiles:
 * - KIND_ORB_BOLT (0): Glowing cyan Anemo energy sphere with trailing ring
 * - KIND_WIND_BLADE (1): Wide glowing turquoise crescent gust slash from Butterfly Sweep
 */
public class AnemoMissileRenderer extends EntityRenderer<AnemoMissileProjectile> {
    private static final ResourceLocation TEX = TeyvatBossesConstants.id("textures/entity/anemo_hypostasis.png");

    public AnemoMissileRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(AnemoMissileProjectile entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        float t = entity.tickCount + partialTicks;
        VertexConsumer vc = bufferSource.getBuffer(RenderType.lightning());
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - entity.getYRot()));

        if (entity.getKind() == AnemoMissileProjectile.KIND_WIND_BLADE) {
            // Wide crescent wind slash arc
            Matrix4f m = poseStack.last().pose();
            int segs = 14;
            for (int i = 0; i < segs; i++) {
                float f0 = (float) i / segs;
                float f1 = (float) (i + 1) / segs;
                float a0 = (f0 - 0.5F) * 2.1F;
                float a1 = (f1 - 0.5F) * 2.1F;
                float rOut = 2.25F;
                float rIn = 1.35F + 0.55F * Math.abs(f0 - 0.5F);
                float x0 = Mth.sin(a0) * rOut, z0 = Mth.cos(a0) * rOut - rOut;
                float x1 = Mth.sin(a1) * rOut, z1 = Mth.cos(a1) * rOut - rOut;
                float ix0 = Mth.sin(a0) * rIn, iz0 = Mth.cos(a0) * rIn - rOut;
                float ix1 = Mth.sin(a1) * rIn, iz1 = Mth.cos(a1) * rIn - rOut;
                vc.addVertex(m, ix0, 0.0F, iz0).setColor(85, 245, 215, 75);
                vc.addVertex(m, x0, 0.1F, z0).setColor(235, 255, 250, 235);
                vc.addVertex(m, x1, 0.1F, z1).setColor(235, 255, 250, 235);
                vc.addVertex(m, ix1, 0.0F, iz1).setColor(85, 245, 215, 75);
                vc.addVertex(m, ix1, 0.0F, iz1).setColor(85, 245, 215, 75);
                vc.addVertex(m, x1, 0.1F, z1).setColor(235, 255, 250, 235);
                vc.addVertex(m, x0, 0.1F, z0).setColor(235, 255, 250, 235);
                vc.addVertex(m, ix0, 0.0F, iz0).setColor(85, 245, 215, 75);
            }
        } else {
            // Magic Circle glowing energy bolt
            poseStack.mulPose(Axis.ZP.rotationDegrees(t * 14.0F));
            Matrix4f m = poseStack.last().pose();
            float r = 0.42F;
            float[][] pts = {{r, 0}, {0, r}, {-r, 0}, {0, -r}};
            for (int i = 0; i < 4; i++) {
                float[] a = pts[i];
                float[] b = pts[(i + 1) % 4];
                vc.addVertex(m, 0.0F, 0.0F, -r * 1.8F).setColor(235, 255, 250, 245);
                vc.addVertex(m, a[0], a[1], 0.0F).setColor(95, 248, 220, 215);
                vc.addVertex(m, b[0], b[1], 0.0F).setColor(95, 248, 220, 215);
                vc.addVertex(m, 0.0F, 0.0F, r * 1.8F).setColor(60, 220, 195, 165);
            }
        }

        poseStack.popPose();
        super.render(entity, yaw, partialTicks, poseStack, bufferSource, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(AnemoMissileProjectile entity) {
        return TEX;
    }
}

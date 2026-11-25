package com.velorise.teyvatbosses.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.velorise.teyvatbosses.TeyvatBossesConstants;
import com.velorise.teyvatbosses.entity.CryoIcicleProjectile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

/**
 * Procedural crystalline 3D Cryo icicle renderer.
 */
public class CryoIcicleRenderer extends EntityRenderer<CryoIcicleProjectile> {
    private static final ResourceLocation TEXTURE = TeyvatBossesConstants.id("textures/entity/cryo_regisvine_boss.png");

    public CryoIcicleRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(CryoIcicleProjectile entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        float yRot = Mth.lerp(partialTicks, entity.yRotO, entity.getYRot());
        float xRot = Mth.lerp(partialTicks, entity.xRotO, entity.getXRot());
        poseStack.mulPose(Axis.YP.rotationDegrees(yRot - 90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(xRot));
        poseStack.mulPose(Axis.XP.rotationDegrees((entity.tickCount + partialTicks) * 18.0F));

        VertexConsumer vc = bufferSource.getBuffer(RenderType.lightning());
        Matrix4f m = poseStack.last().pose();
        float r = 0.16F;
        float front = 0.55F;
        float back = -0.35F;

        // 4 crystal facets tapering to front & back points
        float[][] ring = { {0, r}, {r, 0}, {0, -r}, {-r, 0} };
        for (int i = 0; i < 4; i++) {
            float[] a = ring[i];
            float[] b = ring[(i + 1) % 4];
            vc.addVertex(m, front, 0, 0).setColor(225, 250, 255, 240);
            vc.addVertex(m, 0, a[0], a[1]).setColor(115, 215, 255, 215);
            vc.addVertex(m, 0, b[0], b[1]).setColor(115, 215, 255, 215);
            vc.addVertex(m, back, 0, 0).setColor(70, 170, 255, 190);
        }

        poseStack.popPose();
        super.render(entity, yaw, partialTicks, poseStack, bufferSource, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(CryoIcicleProjectile entity) {
        return TEXTURE;
    }
}

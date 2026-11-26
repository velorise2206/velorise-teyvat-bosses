package com.velorise.teyvatbosses.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.velorise.teyvatbosses.TeyvatBossesConstants;
import com.velorise.teyvatbosses.entity.RuinTorpedoProjectile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

/**
 * Renders the sleek bronze-and-cyan Hydro torpedo with smooth velocity-aligned pitch/yaw.
 */
public class RuinTorpedoRenderer extends EntityRenderer<RuinTorpedoProjectile> {
    private static final ResourceLocation TEXTURE = TeyvatBossesConstants.id("textures/entity/ruin_nautilus_boss.png");

    public RuinTorpedoRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    public void render(RuinTorpedoProjectile entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int packedLight) {
        float yaw = Mth.rotLerp(partialTicks, entity.yRotO, entity.getYRot());
        float pitch = Mth.lerp(partialTicks, entity.xRotO, entity.getXRot());

        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(yaw));
        poseStack.mulPose(Axis.XP.rotationDegrees(-pitch));

        Matrix4f m = poseStack.last().pose();
        VertexConsumer vc = buffers.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        // Sleek elongated torpedo body (0.16 x 0.16 x 0.72) with glowing cyan nose
        drawBox(vc, m, -0.10F, -0.10F, -0.34F, 0.10F, 0.10F, 0.24F, 0xFFB89458, packedLight);
        drawBox(vc, m, -0.08F, -0.08F,  0.24F, 0.08F, 0.08F, 0.42F, 0xFF66F4FF, 0x00F000F0);

        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffers, packedLight);
    }

    private static void drawBox(VertexConsumer vc, Matrix4f m, float x0, float y0, float z0, float x1, float y1, float z1, int argb, int light) {
        int a = (argb >>> 24) & 0xFF, r = (argb >>> 16) & 0xFF, g = (argb >>> 8) & 0xFF, b = argb & 0xFF;
        // 6 faces using solid UV region
        quad(vc, m, x0, y0, z1, x1, y0, z1, x1, y1, z1, x0, y1, z1, r, g, b, a, light, 0, 0, 1);
        quad(vc, m, x1, y0, z0, x0, y0, z0, x0, y1, z0, x1, y1, z0, r, g, b, a, light, 0, 0, -1);
        quad(vc, m, x0, y1, z0, x0, y1, z1, x1, y1, z1, x1, y1, z0, r, g, b, a, light, 0, 1, 0);
        quad(vc, m, x0, y0, z1, x0, y0, z0, x1, y0, z0, x1, y0, z1, r, g, b, a, light, 0, -1, 0);
        quad(vc, m, x1, y0, z1, x1, y0, z0, x1, y1, z0, x1, y1, z1, r, g, b, a, light, 1, 0, 0);
        quad(vc, m, x0, y0, z0, x0, y0, z1, x0, y1, z1, x0, y1, z0, r, g, b, a, light, -1, 0, 0);
    }

    private static void quad(VertexConsumer vc, Matrix4f m,
                             float x0, float y0, float z0, float x1, float y1, float z1,
                             float x2, float y2, float z2, float x3, float y3, float z3,
                             int r, int g, int b, int a, int light, float nx, float ny, float nz) {
        vc.addVertex(m, x0, y0, z0).setColor(r, g, b, a).setUv(0.8F, 0.55F).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(nx, ny, nz);
        vc.addVertex(m, x1, y1, z1).setColor(r, g, b, a).setUv(0.85F, 0.55F).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(nx, ny, nz);
        vc.addVertex(m, x2, y2, z2).setColor(r, g, b, a).setUv(0.85F, 0.60F).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(nx, ny, nz);
        vc.addVertex(m, x3, y3, z3).setColor(r, g, b, a).setUv(0.8F, 0.60F).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(nx, ny, nz);
    }

    @Override
    public ResourceLocation getTextureLocation(RuinTorpedoProjectile entity) {
        return TEXTURE;
    }
}

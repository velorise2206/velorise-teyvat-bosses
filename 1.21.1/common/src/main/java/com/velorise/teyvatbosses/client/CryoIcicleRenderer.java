package com.velorise.teyvatbosses.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.velorise.teyvatbosses.TeyvatBossesConstants;
import com.velorise.teyvatbosses.entity.CryoIcicleProjectile;
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
 * Multi-layered 3D Cryo Crystal Lance renderer with custom crystalline texture,
 * fullbright faceted outer shell, inner white-cyan emissive core, and spinning frost stabilizer fins.
 */
public class CryoIcicleRenderer extends EntityRenderer<CryoIcicleProjectile> {
    private static final ResourceLocation TEXTURE = TeyvatBossesConstants.id("textures/entity/cryo_icicle.png");

    public CryoIcicleRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(
            CryoIcicleProjectile entity,
            float yaw,
            float partialTicks,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight
    ) {
        poseStack.pushPose();
        float yRot = Mth.lerp(partialTicks, entity.yRotO, entity.getYRot());
        float xRot = Mth.lerp(partialTicks, entity.xRotO, entity.getXRot());
        float spin = (entity.tickCount + partialTicks) * 22.0F;

        poseStack.mulPose(Axis.YP.rotationDegrees(yRot - 90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(xRot));
        poseStack.mulPose(Axis.XP.rotationDegrees(spin));

        // 1. Textured 6-sided crystalline lance body (fullbright translucent ice)
        VertexConsumer tvc = bufferSource.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        PoseStack.Pose pose = poseStack.last();
        Matrix4f m = pose.pose();

        float front = 0.78F;
        float midX = 0.08F;
        float back = -0.52F;
        float r = 0.18F;
        int sides = 6;

        for (int i = 0; i < sides; i++) {
            double a0 = (Math.PI * 2.0D * i) / sides;
            double a1 = (Math.PI * 2.0D * (i + 1)) / sides;
            float y0 = (float) Math.cos(a0) * r, z0 = (float) Math.sin(a0) * r;
            float y1 = (float) Math.cos(a1) * r, z1 = (float) Math.sin(a1) * r;
            float u0 = (float) i / sides;
            float u1 = (float) (i + 1) / sides;

            // Front razor tip
            addVert(tvc, pose, m, midX, y0, z0, u0, 0.55F, 0.3F, y0, z0, 0xFFE6FAFF);
            addVert(tvc, pose, m, midX, y1, z1, u1, 0.55F, 0.3F, y1, z1, 0xFFE6FAFF);
            addVert(tvc, pose, m, front, 0.0F, 0.0F, (u0 + u1) * 0.5F, 0.0F, 1.0F, 0.0F, 0.0F, 0xFFFFFFFF);
            addVert(tvc, pose, m, front, 0.0F, 0.0F, (u0 + u1) * 0.5F, 0.0F, 1.0F, 0.0F, 0.0F, 0xFFFFFFFF);

            // Rear tapered tail
            addVert(tvc, pose, m, midX, y1, z1, u1, 0.55F, -0.3F, y1, z1, 0xFFB4E8FF);
            addVert(tvc, pose, m, midX, y0, z0, u0, 0.55F, -0.3F, y0, z0, 0xFFB4E8FF);
            addVert(tvc, pose, m, back, 0.0F, 0.0F, (u0 + u1) * 0.5F, 1.0F, -1.0F, 0.0F, 0.0F, 0xFF70C8FF);
            addVert(tvc, pose, m, back, 0.0F, 0.0F, (u0 + u1) * 0.5F, 1.0F, -1.0F, 0.0F, 0.0F, 0xFF70C8FF);
        }

        // 2. Glowing emissive core + 2 crossing frost blade fins + trailing energy ring
        VertexConsumer lvc = bufferSource.getBuffer(RenderType.lightning());
        float cr = 0.095F;
        float[][] coreRing = { {0, cr}, {cr, 0}, {0, -cr}, {-cr, 0} };
        for (int i = 0; i < 4; i++) {
            float[] a = coreRing[i];
            float[] b = coreRing[(i + 1) % 4];
            lvc.addVertex(m, front * 0.92F, 0, 0).setColor(240, 253, 255, 240);
            lvc.addVertex(m, midX, a[0], a[1]).setColor(150, 235, 255, 210);
            lvc.addVertex(m, midX, b[0], b[1]).setColor(150, 235, 255, 210);
            lvc.addVertex(m, back * 0.85F, 0, 0).setColor(70, 185, 255, 165);
        }

        // Crossed frost stabilizer fins at rear
        float finSpan = 0.34F;
        lvc.addVertex(m, -0.05F, 0.0F, 0.0F).setColor(215, 248, 255, 210);
        lvc.addVertex(m, -0.45F, finSpan, 0.0F).setColor(95, 210, 255, 170);
        lvc.addVertex(m, -0.56F, 0.0F, 0.0F).setColor(60, 175, 255, 130);
        lvc.addVertex(m, -0.45F, -finSpan, 0.0F).setColor(95, 210, 255, 170);

        lvc.addVertex(m, -0.05F, 0.0F, 0.0F).setColor(215, 248, 255, 210);
        lvc.addVertex(m, -0.45F, 0.0F, finSpan).setColor(95, 210, 255, 170);
        lvc.addVertex(m, -0.56F, 0.0F, 0.0F).setColor(60, 175, 255, 130);
        lvc.addVertex(m, -0.45F, 0.0F, -finSpan).setColor(95, 210, 255, 170);

        poseStack.popPose();
        super.render(entity, yaw, partialTicks, poseStack, bufferSource, packedLight);
    }

    private static void addVert(VertexConsumer vc, PoseStack.Pose pose, Matrix4f m,
                                float x, float y, float z, float u, float v,
                                float nx, float ny, float nz, int argb) {
        vc.addVertex(m, x, y, z)
                .setColor(argb)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(LightTexture.FULL_BRIGHT)
                .setNormal(pose, nx, ny, nz);
    }

    @Override
    public ResourceLocation getTextureLocation(CryoIcicleProjectile entity) {
        return TEXTURE;
    }
}

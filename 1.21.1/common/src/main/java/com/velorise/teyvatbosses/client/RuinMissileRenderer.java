package com.velorise.teyvatbosses.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.velorise.teyvatbosses.TeyvatBossesConstants;
import com.velorise.teyvatbosses.entity.RuinMissileProjectile;
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
 * Renders RuinMissileProjectile as a sleek 3D bronze-iron mechanical warhead with a
 * glowing amber thruster flare aligned straight along its velocity vector.
 */
public class RuinMissileRenderer extends EntityRenderer<RuinMissileProjectile> {
    private static final ResourceLocation TEXTURE = TeyvatBossesConstants.id("textures/entity/ruin_guard_missile.png");

    public RuinMissileRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(RuinMissileProjectile entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        float interpYaw = Mth.rotLerp(partialTicks, entity.yRotO, entity.getYRot());
        float interpPitch = Mth.lerp(partialTicks, entity.xRotO, entity.getXRot());

        poseStack.pushPose();
        // Warhead nose is along local -Z; rotating by (180 - yaw) aligns -Z with world forward (-sin(yaw), cos(yaw)),
        // and rotating by -pitch pitches local -Z UP when climbing (xRot < 0) and DOWN when diving (xRot > 0).
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - interpYaw));
        poseStack.mulPose(Axis.XP.rotationDegrees(-interpPitch));

        // Textured warhead body
        VertexConsumer vc = bufferSource.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        Matrix4f m = poseStack.last().pose();
        drawBox(vc, poseStack.last(), -0.14F, -0.14F, -0.45F, 0.14F, 0.14F, 0.35F, packedLight);

        // Glowing amber thruster plume & nose core
        VertexConsumer lvc = bufferSource.getBuffer(RenderType.lightning());
        float r = 0.22F;
        float[][] pts = {{r, 0}, {0, r}, {-r, 0}, {0, -r}};
        for (int i = 0; i < 4; i++) {
            float[] a = pts[i];
            float[] b = pts[(i + 1) % 4];
            lvc.addVertex(m, 0.0F, 0.0F, -0.58F).setColor(255, 215, 110, 240);
            lvc.addVertex(m, a[0] * 0.7F, a[1] * 0.7F, -0.22F).setColor(255, 145, 35, 180);
            lvc.addVertex(m, b[0] * 0.7F, b[1] * 0.7F, -0.22F).setColor(255, 145, 35, 180);
            lvc.addVertex(m, 0.0F, 0.0F, 0.85F).setColor(255, 95, 20, 195);
        }

        poseStack.popPose();
        super.render(entity, yaw, partialTicks, poseStack, bufferSource, packedLight);
    }

    private static void drawBox(VertexConsumer vc, PoseStack.Pose pose,
                                float x0, float y0, float z0, float x1, float y1, float z1, int light) {
        quad(vc, pose, x0, y0, z0, x1, y0, z0, x1, y1, z0, x0, y1, z0, 0, 0, -1, light);
        quad(vc, pose, x1, y0, z1, x0, y0, z1, x0, y1, z1, x1, y1, z1, 0, 0, 1, light);
        quad(vc, pose, x0, y1, z0, x1, y1, z0, x1, y1, z1, x0, y1, z1, 0, 1, 0, light);
        quad(vc, pose, x0, y0, z1, x1, y0, z1, x1, y0, z0, x0, y0, z0, 0, -1, 0, light);
        quad(vc, pose, x0, y0, z1, x0, y0, z0, x0, y1, z0, x0, y1, z1, -1, 0, 0, light);
        quad(vc, pose, x1, y0, z0, x1, y0, z1, x1, y1, z1, x1, y1, z0, 1, 0, 0, light);
    }

    private static void quad(VertexConsumer vc, PoseStack.Pose pose,
                             float x0, float y0, float z0, float x1, float y1, float z1,
                             float x2, float y2, float z2, float x3, float y3, float z3,
                             float nx, float ny, float nz, int light) {
        vc.addVertex(pose, x0, y0, z0).setColor(255, 255, 255, 255).setUv(0, 0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(pose, nx, ny, nz);
        vc.addVertex(pose, x1, y1, z1).setColor(255, 255, 255, 255).setUv(1, 0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(pose, nx, ny, nz);
        vc.addVertex(pose, x2, y2, z2).setColor(255, 255, 255, 255).setUv(1, 1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(pose, nx, ny, nz);
        vc.addVertex(pose, x3, y3, z3).setColor(255, 255, 255, 255).setUv(0, 1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(pose, nx, ny, nz);
    }

    @Override
    public ResourceLocation getTextureLocation(RuinMissileProjectile entity) {
        return TEXTURE;
    }
}

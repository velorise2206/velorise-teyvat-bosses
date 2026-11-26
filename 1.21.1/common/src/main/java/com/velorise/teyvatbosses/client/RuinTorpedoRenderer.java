package com.velorise.teyvatbosses.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.velorise.teyvatbosses.TeyvatBossesConstants;
import com.velorise.teyvatbosses.entity.RuinTorpedoProjectile;
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
 * AAA 3D Fontaine Clockwork Torpedo Renderer:
 * - Multi-tier verdigris &amp; brass hull casing with twin riveted clamp bands
 * - Blazing full-bright Hydro cyan warhead nosecone
 * - 4 cruciform stabilizing tail fins
 * - High-speed spinning 4-blade bronze tail propeller
 * - Additive cyan Hydro energy wake &amp; warhead glow halo
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
        float age = entity.tickCount + partialTicks;

        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(yaw));
        poseStack.mulPose(Axis.XP.rotationDegrees(-pitch));

        Matrix4f m = poseStack.last().pose();
        VertexConsumer vc = buffers.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));

        // 1. Main verdigris torpedo casing (-0.36 .. +0.26)
        drawBox(vc, m, -0.11F, -0.11F, -0.36F, 0.11F, 0.11F, 0.26F, 0xFF3E786E, packedLight);
        // 2. Twin antique brass structural clamp rings
        drawBox(vc, m, -0.125F, -0.125F, -0.22F, 0.125F, 0.125F, -0.14F, 0xFFC29A52, packedLight);
        drawBox(vc, m, -0.125F, -0.125F,  0.10F, 0.125F, 0.125F,  0.18F, 0xFFC29A52, packedLight);
        // 3. Tapered glowing Hydro-cyan warhead nosecone (FULL_BRIGHT)
        drawBox(vc, m, -0.095F, -0.095F, 0.26F, 0.095F, 0.095F, 0.42F, 0xFF5CF2FF, LightTexture.FULL_BRIGHT);
        drawBox(vc, m, -0.060F, -0.060F, 0.42F, 0.060F, 0.060F, 0.52F, 0xFFD2FFFF, LightTexture.FULL_BRIGHT);
        // 4. Tapered dark-iron stern tail cone
        drawBox(vc, m, -0.085F, -0.085F, -0.48F, 0.085F, 0.085F, -0.36F, 0xFF323842, packedLight);
        // 5. 4 Cruciform bronze stabilizing tail fins
        drawBox(vc, m, -0.018F, -0.22F, -0.46F, 0.018F, 0.22F, -0.28F, 0xFFB08846, packedLight);
        drawBox(vc, m, -0.22F, -0.018F, -0.46F, 0.22F, 0.018F, -0.28F, 0xFFB08846, packedLight);

        // 6. Spinning 4-blade miniature bronze tail propeller
        poseStack.pushPose();
        poseStack.translate(0.0F, 0.0F, -0.52F);
        poseStack.mulPose(Axis.ZP.rotationDegrees(age * 42.0F));
        Matrix4f pm = poseStack.last().pose();
        drawBox(vc, pm, -0.03F, -0.16F, -0.02F, 0.03F, 0.16F, 0.02F, 0xFFD4AA62, packedLight);
        drawBox(vc, pm, -0.16F, -0.03F, -0.02F, 0.16F, 0.03F, 0.02F, 0xFFD4AA62, packedLight);
        poseStack.popPose();

        // 7. Additive Hydro plasma wake ribbon & nose halo
        VertexConsumer lvc = buffers.getBuffer(RenderType.lightning());
        float pulse = 0.13F + 0.03F * Mth.sin(age * 0.8F);
        lvc.addVertex(m, -pulse, 0,  0.48F).setColor(90, 245, 255, 175);
        lvc.addVertex(m,  pulse, 0,  0.48F).setColor(90, 245, 255, 175);
        lvc.addVertex(m,  0.04F, 0, -1.25F).setColor(60, 220, 255, 20);
        lvc.addVertex(m, -0.04F, 0, -1.25F).setColor(60, 220, 255, 20);

        lvc.addVertex(m, 0, -pulse,  0.48F).setColor(90, 245, 255, 175);
        lvc.addVertex(m, 0,  pulse,  0.48F).setColor(90, 245, 255, 175);
        lvc.addVertex(m, 0,  0.04F, -1.25F).setColor(60, 220, 255, 20);
        lvc.addVertex(m, 0, -0.04F, -1.25F).setColor(60, 220, 255, 20);

        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffers, packedLight);
    }

    private static void drawBox(VertexConsumer vc, Matrix4f m, float x0, float y0, float z0, float x1, float y1, float z1, int argb, int light) {
        int a = (argb >>> 24) & 0xFF, r = (argb >>> 16) & 0xFF, g = (argb >>> 8) & 0xFF, b = argb & 0xFF;
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
        vc.addVertex(m, x0, y0, z0).setColor(r, g, b, a).setUv(0.02F, 0.96F).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(nx, ny, nz);
        vc.addVertex(m, x1, y1, z1).setColor(r, g, b, a).setUv(0.03F, 0.96F).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(nx, ny, nz);
        vc.addVertex(m, x2, y2, z2).setColor(r, g, b, a).setUv(0.03F, 0.97F).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(nx, ny, nz);
        vc.addVertex(m, x3, y3, z3).setColor(r, g, b, a).setUv(0.02F, 0.97F).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(nx, ny, nz);
    }

    @Override
    public ResourceLocation getTextureLocation(RuinTorpedoProjectile entity) {
        return TEXTURE;
    }
}

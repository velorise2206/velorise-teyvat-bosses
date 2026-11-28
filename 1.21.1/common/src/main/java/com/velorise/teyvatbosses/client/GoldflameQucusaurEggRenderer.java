package com.velorise.teyvatbosses.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.velorise.teyvatbosses.TeyvatBossesConstants;
import com.velorise.teyvatbosses.entity.GoldflameQucusaurEggEntity;
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
 * Renders the living {@link GoldflameQucusaurEggEntity} as a solid, opaque, textured crimson-gold
 * volcanic dragon egg (with closed bottom &amp; top caps, proper lighting, and a subtle pulsing
 * Phlogiston aura when heated).
 */
public class GoldflameQucusaurEggRenderer extends EntityRenderer<GoldflameQucusaurEggEntity> {
    private static final ResourceLocation EGG_TEXTURE =
            TeyvatBossesConstants.id("textures/entity/goldflame_qucusaur_egg.png");

    public GoldflameQucusaurEggRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
        this.shadowRadius = 0.42F;
    }

    @Override
    public ResourceLocation getTextureLocation(GoldflameQucusaurEggEntity entity) {
        return EGG_TEXTURE;
    }

    @Override
    public void render(GoldflameQucusaurEggEntity entity, float entityYaw, float partialTicks,
                       PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        float t = (entity.tickCount + partialTicks) / 20.0F;
        float prog = entity.getHatchProgress();

        poseStack.pushPose();
        if (entity.isHeated()) {
            float wobble = (2.5F + prog * 9.0F) * Mth.sin(t * (6.0F + prog * 14.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(wobble));
        }

        // Solid opaque textured dragon egg shell (closed bottom & closed pointed apex)
        VertexConsumer tvc = buffer.getBuffer(RenderType.entityCutoutNoCull(EGG_TEXTURE));
        PoseStack.Pose pose = poseStack.last();
        Matrix4f mat = pose.pose();
        int light = entity.isHeated() ? LightTexture.FULL_BRIGHT : packedLight;

        // 5 smooth closed ovoid tiers from y=0.00 (closed base) to y=0.92 (closed apex)
        drawTexturedCap(tvc, pose, mat, 0.00F, 0.24F, 12, false, light);
        drawTexturedTier(tvc, pose, mat, 0.00F, 0.14F, 0.24F, 0.35F, 0.84F, 1.00F, 12, light);
        drawTexturedTier(tvc, pose, mat, 0.14F, 0.38F, 0.35F, 0.38F, 0.56F, 0.84F, 12, light);
        drawTexturedTier(tvc, pose, mat, 0.38F, 0.66F, 0.38F, 0.28F, 0.26F, 0.56F, 12, light);
        drawTexturedTier(tvc, pose, mat, 0.66F, 0.86F, 0.28F, 0.12F, 0.06F, 0.26F, 12, light);
        drawTexturedTier(tvc, pose, mat, 0.86F, 0.93F, 0.12F, 0.00F, 0.00F, 0.06F, 12, light);

        // Subtle pulsing golden Phlogiston heat aura ONLY when heated
        if (entity.isHeated()) {
            VertexConsumer lvc = buffer.getBuffer(RenderType.lightning());
            float pulse = 0.5F + 0.5F * Mth.sin(t * 5.0F);
            int glowA = (int) (55 + 65 * prog + 25 * pulse);
            drawGlowShell(lvc, mat, 0.16F, 0.72F, 0.41F, 0.32F, 255, 196, 54, glowA);
        }

        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    private static void drawTexturedTier(VertexConsumer vc, PoseStack.Pose pose, Matrix4f mat,
                                         float y0, float y1, float r0, float r1,
                                         float vTop, float vBot, int segs, int light) {
        for (int i = 0; i < segs; i++) {
            float a0 = (float) (Math.PI * 2.0D * i / segs);
            float a1 = (float) (Math.PI * 2.0D * (i + 1) / segs);
            float c0 = Mth.cos(a0), s0 = Mth.sin(a0);
            float c1 = Mth.cos(a1), s1 = Mth.sin(a1);
            float nx = (c0 + c1) * 0.5F;
            float nz = (s0 + s1) * 0.5F;
            float ny = (r0 - r1) * 0.6F;
            float u0 = (float) i / segs;
            float u1 = (float) (i + 1) / segs;

            addVert(vc, pose, mat, c0 * r0, y0, s0 * r0, u0, vBot, nx, ny, nz, light);
            addVert(vc, pose, mat, c1 * r0, y0, s1 * r0, u1, vBot, nx, ny, nz, light);
            addVert(vc, pose, mat, c1 * r1, y1, s1 * r1, u1, vTop, nx, ny, nz, light);
            addVert(vc, pose, mat, c0 * r1, y1, s0 * r1, u0, vTop, nx, ny, nz, light);
        }
    }

    private static void drawTexturedCap(VertexConsumer vc, PoseStack.Pose pose, Matrix4f mat,
                                        float y, float r, int segs, boolean top, int light) {
        float ny = top ? 1.0F : -1.0F;
        for (int i = 0; i < segs; i++) {
            float a0 = (float) (Math.PI * 2.0D * i / segs);
            float a1 = (float) (Math.PI * 2.0D * (i + 1) / segs);
            float c0 = Mth.cos(a0), s0 = Mth.sin(a0);
            float c1 = Mth.cos(a1), s1 = Mth.sin(a1);
            addVert(vc, pose, mat, 0.0F, y, 0.0F, 0.5F, 0.95F, 0.0F, ny, 0.0F, light);
            addVert(vc, pose, mat, c1 * r, y, s1 * r, 0.5F + c1 * 0.2F, 0.95F, 0.0F, ny, 0.0F, light);
            addVert(vc, pose, mat, c0 * r, y, s0 * r, 0.5F + c0 * 0.2F, 0.95F, 0.0F, ny, 0.0F, light);
            addVert(vc, pose, mat, 0.0F, y, 0.0F, 0.5F, 0.95F, 0.0F, ny, 0.0F, light);
        }
    }

    private static void addVert(VertexConsumer vc, PoseStack.Pose pose, Matrix4f mat,
                                float x, float y, float z, float u, float v,
                                float nx, float ny, float nz, int light) {
        vc.addVertex(mat, x, y, z)
                .setColor(0xFFFFFFFF)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(pose, nx, ny, nz);
    }

    private static void drawGlowShell(VertexConsumer vc, Matrix4f mat, float y0, float y1, float r0, float r1,
                                      int r, int g, int b, int a) {
        int segs = 10;
        for (int i = 0; i < segs; i++) {
            float a0 = (float) (Math.PI * 2.0D * i / segs);
            float a1 = (float) (Math.PI * 2.0D * (i + 1) / segs);
            float c0 = Mth.cos(a0), s0 = Mth.sin(a0);
            float c1 = Mth.cos(a1), s1 = Mth.sin(a1);
            vc.addVertex(mat, c0 * r0, y0, s0 * r0).setColor(r, g, b, a);
            vc.addVertex(mat, c1 * r0, y0, s1 * r0).setColor(r, g, b, a);
            vc.addVertex(mat, c1 * r1, y1, s1 * r1).setColor(r, g, b, a / 2);
            vc.addVertex(mat, c0 * r1, y1, s0 * r1).setColor(r, g, b, a / 2);
        }
    }
}

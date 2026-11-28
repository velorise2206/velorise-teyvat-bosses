package com.velorise.teyvatbosses.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.velorise.teyvatbosses.TeyvatBossesConstants;
import com.velorise.teyvatbosses.client.model.BedrockAnimationLoader;
import com.velorise.teyvatbosses.client.model.GoldflameQucusaurModel;
import com.velorise.teyvatbosses.entity.GoldflameQucusaurEntity;
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
 * Renderer for Boss #6 {@link GoldflameQucusaurEntity}:
 * <ul>
 *   <li>Uses the shared {@link GoldflameQucusaurModel} ({@code main} + {@code emissive} layers) with {@code saddle} hidden.</li>
 *   <li>Drives all 15 Boss clips directly from {@code animations/goldflame_qucusaur.bbmodel} via {@link BedrockAnimationLoader}.</li>
 *   <li>Renders a 4-layer undulating volumetric Goldflame breath torrent + dual helical vortex ribbons
 *       (matching {@link PrimoGeovishapRenderer}) during {@code breath_sweep}.</li>
 * </ul>
 */
public class GoldflameQucusaurRenderer extends EntityRenderer<GoldflameQucusaurEntity> {
    public static final ResourceLocation TEXTURE =
            TeyvatBossesConstants.id("textures/entity/goldflame_qucusaur.png");
    public static final ResourceLocation EMISSIVE_TEXTURE =
            TeyvatBossesConstants.id("textures/entity/goldflame_qucusaur_emissive.png");
    private static final ResourceLocation BOSS_MUSIC_ID =
            TeyvatBossesConstants.id("music.goldflame_qucusaur_theme");

    private final GoldflameQucusaurModel<GoldflameQucusaurEntity> bodyModel;
    private final GoldflameQucusaurModel<GoldflameQucusaurEntity> emissiveModel;

    public GoldflameQucusaurRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
        this.bodyModel = new GoldflameQucusaurModel<>(ctx.bakeLayer(GoldflameQucusaurModel.LAYER_LOCATION));
        this.emissiveModel = new GoldflameQucusaurModel<>(ctx.bakeLayer(GoldflameQucusaurModel.LAYER_LOCATION_EMISSIVE));
        this.shadowRadius = 2.4F;
    }

    @Override
    public ResourceLocation getTextureLocation(GoldflameQucusaurEntity entity) {
        return TEXTURE;
    }

    @Override
    public void render(GoldflameQucusaurEntity entity, float entityYaw, float partialTicks,
                       PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        boolean musicActive = entity.isAlive() && entity.getAnimState() != GoldflameQucusaurEntity.STATE_DEATH_COLLAPSE;
        BossMusicManager.update(entity, BOSS_MUSIC_ID, musicActive,
                () -> entity.isAlive() && entity.getAnimState() != GoldflameQucusaurEntity.STATE_DEATH_COLLAPSE);
        float bodyYaw = Mth.rotLerp(partialTicks, entity.yBodyRotO, entity.yBodyRot);
        float stateSec = (entity.getStateTicks() + partialTicks) / 20.0F;
        float clientSec = (entity.tickCount + partialTicks) / 20.0F;

        bodyModel.resetPose(false);
        BedrockAnimationLoader.Clip clip = BedrockAnimationLoader.getClip("goldflame_qucusaur", entity.getActiveAnimationClip());
        if (clip != null) {
            BedrockAnimationLoader.applyWeighted(bodyModel::getBone, clip, stateSec, 1.0F, false);
        }
        emissiveModel.copyPoseFrom(bodyModel);

        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - bodyYaw));
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.translate(0.0D, -1.501D, 0.0D);

        int overlay = OverlayTexture.pack(OverlayTexture.u(0.0F), OverlayTexture.v(entity.hurtTime > 0));
        VertexConsumer bodyVc = buffer.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        bodyModel.renderToBuffer(poseStack, bodyVc, packedLight, overlay, 0xFFFFFFFF);

        // Pulsing molten-gold emissive layer (brightens during Golden Flame form)
        float pulse = entity.isGoldenFlameActive()
                ? 0.85F + 0.15F * Mth.sin(clientSec * 8.0F)
                : 0.65F + 0.20F * Mth.sin(clientSec * 3.5F);
        int alpha = Mth.clamp((int) (pulse * 255.0F), 90, 255);
        int emissiveColor = (alpha << 24) | 0x00FFFFFF;
        VertexConsumer emVc = buffer.getBuffer(RenderType.entityTranslucentEmissive(EMISSIVE_TEXTURE));
        emissiveModel.renderToBuffer(poseStack, emVc, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, emissiveColor);
        poseStack.popPose();

        // Volumetric Goldflame Breath Torrent synced 1:1 with breath_sweep (ticks 16..48)
        if (entity.getAnimState() == GoldflameQucusaurEntity.STATE_BREATH_SWEEP
                && entity.getStateTicks() >= 15 && entity.getStateTicks() <= 50) {
            float progress = Mth.clamp((entity.getStateTicks() - 16 + partialTicks) / 32.0F, 0.0F, 1.0F);
            float sweepYaw = bodyYaw + Mth.lerp(progress, -38.0F, 38.0F);
            float fade = (progress < 0.08F) ? (progress / 0.08F) : (progress > 0.90F ? (1.0F - progress) / 0.10F : 1.0F);
            VertexConsumer lvc = buffer.getBuffer(RenderType.lightning());
            renderGoldflameBreathVfx(poseStack, lvc, sweepYaw, 8.0F, 2.55D, 2.25D, 16.0F, clientSec, fade);
        }

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    /**
     * Shared 4-layer undulating volumetric Goldflame breath torrent + dual helical vortex ribbons
     * (reusing PrimoGeovishapRenderer's volumetric shaderless VertexConsumer architecture).
     */
    public static void renderGoldflameBreathVfx(PoseStack poseStack, VertexConsumer lvc, float yawDeg, float pitchDeg,
                                                double mouthHeight, double forwardOffset, float length,
                                                float clientSec, float fade) {
        if (fade <= 0.02F) return;
        poseStack.pushPose();
        float rad = (float) Math.toRadians(yawDeg);
        poseStack.translate(-Math.sin(rad) * forwardOffset, mouthHeight, Math.cos(rad) * forwardOffset);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - yawDeg));
        poseStack.mulPose(Axis.XP.rotationDegrees(-pitchDeg));
        Matrix4f mat = poseStack.last().pose();

        // 4-layer Pyro Goldflame torrent: crimson-orange -> molten gold -> sun gold -> white-gold core
        drawUndulatingStream(lvc, mat, 0.05F, length, 0.36F, 2.45F, 10, 18, clientSec, 0.15F, 255, 75, 31, (int) (88 * fade));
        drawUndulatingStream(lvc, mat, 0.08F, length * 0.95F, 0.24F, 1.65F, 10, 16, clientSec * 1.15F, 0.12F, 255, 174, 25, (int) (145 * fade));
        drawUndulatingStream(lvc, mat, 0.12F, length * 0.88F, 0.15F, 0.90F, 9, 14, clientSec * 1.32F, 0.09F, 255, 240, 138, (int) (185 * fade));
        drawUndulatingStream(lvc, mat, 0.16F, length * 0.78F, 0.08F, 0.42F, 8, 12, clientSec * 1.50F, 0.07F, 255, 255, 245, (int) (225 * fade));

        // Dual counter-rotating helical fire ribbons
        drawHelicalRibbons(lvc, mat, 0.10F, length * 0.94F, 0.28F, 2.05F, 20, clientSec, 255, 110, 28, (int) (155 * fade));
        drawHelicalRibbons(lvc, mat, 0.15F, length * 0.86F, 0.18F, 1.30F, 18, -clientSec * 0.85F, 255, 218, 86, (int) (135 * fade));
        poseStack.popPose();
    }

    private static void drawUndulatingStream(VertexConsumer vc, Matrix4f mat, float zStart, float zEnd,
                                             float rStart, float rEnd, int lenSegs, int radSegs,
                                             float timeSec, float rippleAmp, int r, int g, int b, int maxAlpha) {
        if (maxAlpha <= 5) return;
        for (int s = 0; s < lenSegs; s++) {
            float u0 = s / (float) lenSegs;
            float u1 = (s + 1) / (float) lenSegs;
            float z0 = -Mth.lerp(u0, zStart, zEnd);
            float z1 = -Mth.lerp(u1, zStart, zEnd);
            float wave0 = 1.0F + rippleAmp * Mth.sin(u0 * 11.0F - timeSec * 22.0F);
            float wave1 = 1.0F + rippleAmp * Mth.sin(u1 * 11.0F - timeSec * 22.0F);
            float r0 = Mth.lerp(u0 * u0 * 0.35F + u0 * 0.65F, rStart, rEnd) * wave0;
            float r1 = Mth.lerp(u1 * u1 * 0.35F + u1 * 0.65F, rStart, rEnd) * wave1;
            float env0 = (u0 < 0.12F ? (u0 / 0.12F) : 1.0F) * (1.0F - u0 * u0);
            float env1 = (u1 < 0.12F ? (u1 / 0.12F) : 1.0F) * (1.0F - u1 * u1);
            int a0 = Mth.clamp((int) (maxAlpha * env0), 0, 255);
            int a1 = Mth.clamp((int) (maxAlpha * env1), 0, 255);
            for (int i = 0; i < radSegs; i++) {
                float ang0 = (float) (Math.PI * 2.0D * i / radSegs);
                float ang1 = (float) (Math.PI * 2.0D * (i + 1) / radSegs);
                float c0 = Mth.cos(ang0), s0 = Mth.sin(ang0);
                float c1 = Mth.cos(ang1), s1 = Mth.sin(ang1);
                vc.addVertex(mat, c0 * r0, s0 * r0, z0).setColor(r, g, b, a0);
                vc.addVertex(mat, c1 * r0, s1 * r0, z0).setColor(r, g, b, a0);
                vc.addVertex(mat, c1 * r1, s1 * r1, z1).setColor(r, g, b, a1);
                vc.addVertex(mat, c0 * r1, s0 * r1, z1).setColor(r, g, b, a1);
            }
        }
    }

    private static void drawHelicalRibbons(VertexConsumer vc, Matrix4f mat, float zStart, float zEnd,
                                           float rStart, float rEnd, int segs, float timeSec,
                                           int r, int g, int b, int maxAlpha) {
        if (maxAlpha <= 5) return;
        for (int strand = 0; strand < 3; strand++) {
            float phase = strand * (Mth.TWO_PI / 3.0F);
            for (int i = 0; i < segs; i++) {
                float u0 = i / (float) segs;
                float u1 = (i + 1) / (float) segs;
                float z0 = -Mth.lerp(u0, zStart, zEnd);
                float z1 = -Mth.lerp(u1, zStart, zEnd);
                float rad0 = Mth.lerp(u0, rStart, rEnd);
                float rad1 = Mth.lerp(u1, rStart, rEnd);
                float ang0 = phase + u0 * 9.5F - timeSec * 16.0F;
                float ang1 = phase + u1 * 9.5F - timeSec * 16.0F;
                float w0 = 0.08F + u0 * 0.16F;
                float w1 = 0.08F + u1 * 0.16F;
                int a0 = Mth.clamp((int) (maxAlpha * (1.0F - u0)), 0, 255);
                int a1 = Mth.clamp((int) (maxAlpha * (1.0F - u1)), 0, 255);
                float x0a = Mth.cos(ang0) * (rad0 - w0), y0a = Mth.sin(ang0) * (rad0 - w0);
                float x0b = Mth.cos(ang0) * (rad0 + w0), y0b = Mth.sin(ang0) * (rad0 + w0);
                float x1a = Mth.cos(ang1) * (rad1 - w1), y1a = Mth.sin(ang1) * (rad1 - w1);
                float x1b = Mth.cos(ang1) * (rad1 + w1), y1b = Mth.sin(ang1) * (rad1 + w1);
                vc.addVertex(mat, x0a, y0a, z0).setColor(r, g, b, a0);
                vc.addVertex(mat, x0b, y0b, z0).setColor(r, g, b, a0);
                vc.addVertex(mat, x1b, y1b, z1).setColor(r, g, b, a1);
                vc.addVertex(mat, x1a, y1a, z1).setColor(r, g, b, a1);
            }
        }
    }
}

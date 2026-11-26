package com.velorise.teyvatbosses.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.velorise.teyvatbosses.TeyvatBossesConstants;
import com.velorise.teyvatbosses.client.model.PrimoGeovishapAnimations;
import com.velorise.teyvatbosses.client.model.PrimoGeovishapModel;
import com.velorise.teyvatbosses.entity.PrimoGeovishapEntity;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * Renders {@link PrimoGeovishapEntity} using the optimized 338-cube articulated model at canonical
 * Genshin Impact boss scale (~5.1 blocks tall on all fours, ~15.0 blocks long), smooth cross-faded
 * Hermite animations, elemental-tinted emissive crystals, and pulsing ground telegraph rings.
 */
public class PrimoGeovishapRenderer extends EntityRenderer<PrimoGeovishapEntity> {
    /** Canonical Genshin Impact scale relative to 1.8-block player & 4.0-block Ruin Guard. */
    public static final float BOSS_SCALE = 1.60F;

    private static final ResourceLocation TEXTURE = TeyvatBossesConstants.id("textures/entity/primo_geovishap_boss.png");
    private static final ResourceLocation EMISSIVE = TeyvatBossesConstants.id("textures/entity/primo_geovishap_boss_emissive.png");

    private final PrimoGeovishapModel<PrimoGeovishapEntity> model;
    private final Map<PrimoGeovishapEntity, PrimoGeovishapAnimations.BlendState> blendStates = new WeakHashMap<>();

    public PrimoGeovishapRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
        this.model = new PrimoGeovishapModel<>(ctx.bakeLayer(PrimoGeovishapModel.LAYER_LOCATION));
        this.shadowRadius = 3.6F;
    }

    @Override
    public void render(PrimoGeovishapEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buf, int packedLight) {
        float clipSec = (entity.getAnimTick() + partialTicks) / 20.0F;
        float clientSec = (entity.tickCount + partialTicks) / 20.0F;
        String clip = PrimoGeovishapEntity.clipNameForAnim(entity.getAnimId());

        float bodyYaw = Mth.rotLerp(partialTicks, entity.yBodyRotO, entity.yBodyRot);
        float headYaw = Mth.rotLerp(partialTicks, entity.yHeadRotO, entity.yHeadRot);
        float headLookYaw = Mth.wrapDegrees(headYaw - bodyYaw);
        float headLookPitch = Mth.lerp(partialTicks, entity.xRotO, entity.getXRot());

        PrimoGeovishapAnimations.BlendState blend = blendStates.computeIfAbsent(
                entity, k -> new PrimoGeovishapAnimations.BlendState());
        PrimoGeovishapAnimations.applyBlended(model, blend, clip, clipSec, clientSec, headLookYaw, headLookPitch);

        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - bodyYaw));
        poseStack.scale(-BOSS_SCALE, -BOSS_SCALE, BOSS_SCALE);
        poseStack.translate(0.0D, -1.501D, 0.0D);

        int overlay = entity.hurtTime > 0
                ? OverlayTexture.pack(OverlayTexture.u(0.0F), OverlayTexture.v(true))
                : OverlayTexture.NO_OVERLAY;
        model.renderToBuffer(poseStack, buf.getBuffer(RenderType.entityCutoutNoCull(TEXTURE)), packedLight, overlay, 0xFFFFFFFF);

        int[] rgb = entity.getInfusionRgb();
        // Subtle breathing luminescence on emissive crystals, brighter during Primordial Shower / Elemental Breath
        float glowPulse = (entity.getAnimId() == PrimoGeovishapEntity.ANIM_PRIMORDIAL_SHOWER
                || entity.getAnimId() == PrimoGeovishapEntity.ANIM_ELEMENTAL_BREATH)
                ? 1.0F
                : 0.84F + 0.16F * Mth.sin(clientSec * 2.6F);
        int er = Mth.clamp((int) (rgb[0] * glowPulse), 0, 255);
        int eg = Mth.clamp((int) (rgb[1] * glowPulse), 0, 255);
        int eb = Mth.clamp((int) (rgb[2] * glowPulse), 0, 255);
        int emColor = 0xFF000000 | (er << 16) | (eg << 8) | eb;
        model.renderToBuffer(poseStack, buf.getBuffer(RenderType.eyes(EMISSIVE)), LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, emColor);
        poseStack.popPose();

        // Dual-ring pulsing telegraph during Primordial Shower / Burrow emergence
        float teleRadius = entity.getTelegraphRadius();
        if (teleRadius > 0.5F) {
            VertexConsumer lvc = buf.getBuffer(RenderType.lightning());
            poseStack.pushPose();
            Matrix4f mat = poseStack.last().pose();
            drawRing(lvc, mat, teleRadius - 0.45F, teleRadius, 48, rgb[0], rgb[1], rgb[2], 195);
            float pulseFrac = (clientSec * 1.4F) % 1.0F;
            float innerR = Math.max(0.6F, teleRadius * pulseFrac);
            int innerA = (int) (140 * (1.0F - pulseFrac));
            if (innerA > 15) {
                drawRing(lvc, mat, Math.max(0.2F, innerR - 0.30F), innerR, 40, rgb[0], rgb[1], rgb[2], innerA);
            }
            poseStack.popPose();
        }

        super.render(entity, entityYaw, partialTicks, poseStack, buf, packedLight);
    }

    private static void drawRing(VertexConsumer vc, Matrix4f mat, float rIn, float rOut, int segs, int r, int g, int b, int a) {
        for (int i = 0; i < segs; i++) {
            float a0 = (float) (Math.PI * 2.0D * i / segs);
            float a1 = (float) (Math.PI * 2.0D * (i + 1) / segs);
            float c0 = Mth.cos(a0), s0 = Mth.sin(a0);
            float c1 = Mth.cos(a1), s1 = Mth.sin(a1);
            vc.addVertex(mat, c0 * rIn, 0.08F, s0 * rIn).setColor(r, g, b, a);
            vc.addVertex(mat, c0 * rOut, 0.08F, s0 * rOut).setColor(r, g, b, a);
            vc.addVertex(mat, c1 * rOut, 0.08F, s1 * rOut).setColor(r, g, b, a);
            vc.addVertex(mat, c1 * rIn, 0.08F, s1 * rIn).setColor(r, g, b, a);
        }
    }

    @Override
    public ResourceLocation getTextureLocation(PrimoGeovishapEntity entity) {
        return TEXTURE;
    }
}

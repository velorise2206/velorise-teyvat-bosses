package com.velorise.teyvatbosses.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.velorise.teyvatbosses.TeyvatBossesConstants;
import com.velorise.teyvatbosses.client.model.RuinGuardAnimations;
import com.velorise.teyvatbosses.client.model.RuinGuardModel;
import com.velorise.teyvatbosses.entity.RuinGuardEntity;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * High-impact renderer for Ruin Guard (GN/Hu-24601) with world-space locked telegraph rings
 * (fixes Leap Slam ring following the boss's feet in mid-air), crescent melee slashes,
 * expanding ground shockwaves, impact micro-shudder, and timestamp-preserving clip blending.
 */
public class RuinGuardRenderer extends EntityRenderer<RuinGuardEntity> {
    private static final ResourceLocation TEXTURE = TeyvatBossesConstants.id("textures/entity/ruin_guard_boss.png");
    private static final ResourceLocation EMISSIVE = TeyvatBossesConstants.id("textures/entity/ruin_guard_boss_emissive.png");
    private static final ResourceLocation MUSIC_ID = TeyvatBossesConstants.id("music.ruin_guard_theme");

    private static final float BLEND_TICKS = 5.0F;

    private static final class AnimSM {
        String prevClip = null;
        float prevTimeSec = 0.0F;
        String currClip = null;
        float lastTimeSec = 0.0F;
        int lastEpoch = -1;
        float blendStartTick = -100.0F;
        BossMusicInstance music = null;
    }

    private static final class BossMusicInstance extends AbstractTickableSoundInstance {
        private final RuinGuardEntity boss;
        BossMusicInstance(SoundEvent ev, RuinGuardEntity boss) {
            super(ev, SoundSource.MUSIC, SoundInstance.createUnseededRandom());
            this.boss = boss;
            this.looping = true;
            this.volume = 0.72F;
            this.pitch = 1.0F;
            this.relative = true;
        }
        @Override
        public void tick() {
            int st = boss.getBossState();
            if (boss.isRemoved() || !boss.isAlive() || st == RuinGuardEntity.STATE_DORMANT || st == RuinGuardEntity.STATE_DEATH) {
                this.stop();
            }
        }
    }

    private final RuinGuardModel<RuinGuardEntity> model;
    private final Map<RuinGuardEntity, AnimSM> stateMachines = new WeakHashMap<>();

    public RuinGuardRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 1.55F;
        this.model = new RuinGuardModel<>(context.bakeLayer(RuinGuardModel.LAYER_LOCATION));
    }

    @Override
    public void render(
            RuinGuardEntity entity,
            float entityYaw,
            float partialTicks,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight
    ) {
        float liveTick = entity.tickCount + partialTicks;
        float currSec = (entity.getAnimTick() + partialTicks) / 20.0F;
        String clip = entity.getCurrentAnimName();
        int epoch = entity.getAnimEpoch();

        AnimSM sm = stateMachines.computeIfAbsent(entity, k -> new AnimSM());
        updateBossMusic(entity, sm);

        if (sm.currClip != null && (!sm.currClip.equals(clip) || sm.lastEpoch != epoch)) {
            sm.prevClip = sm.currClip;
            sm.prevTimeSec = Math.max(0.0F, sm.lastTimeSec);
            sm.blendStartTick = liveTick;
        }
        sm.currClip = clip;
        sm.lastEpoch = epoch;
        sm.lastTimeSec = currSec;

        float bodyYaw = Mth.rotLerp(partialTicks, entity.yBodyRotO, entity.yBodyRot);

        // World-space combat VFX (world-locked telegraphs, melee crescent slashes, expanding shockwaves, Cyclone Spin)
        renderCombatVFX(entity, bodyYaw, partialTicks, liveTick, currSec, poseStack, bufferSource);

        poseStack.pushPose();
        float shudder = computeImpactShudder(entity.getAnimId(), currSec);
        if (shudder > 0.001F) {
            float sx = Mth.sin(liveTick * 31.0F) * shudder;
            float sz = Mth.cos(liveTick * 27.0F) * shudder;
            poseStack.translate(sx, 0.0D, sz);
        }

        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - bodyYaw));
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.translate(0.0D, -1.501D, 0.0D);

        float blendAlpha = Mth.clamp((liveTick - sm.blendStartTick) / BLEND_TICKS, 0.0F, 1.0F);
        if (sm.prevClip != null && blendAlpha < 0.99F) {
            RuinGuardAnimations.applyBlended(model, sm.prevClip, sm.prevTimeSec, clip, currSec, blendAlpha);
        } else {
            RuinGuardAnimations.apply(model, clip, currSec);
        }

        int overlay = OverlayTexture.pack(OverlayTexture.u(0.0F), OverlayTexture.v(entity.hurtTime > 0 || entity.deathTime > 0));
        VertexConsumer vc = bufferSource.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        model.renderToBuffer(poseStack, vc, packedLight, overlay, 0xFFFFFFFF);

        if (entity.getBossState() != RuinGuardEntity.STATE_DORMANT && entity.getBossState() != RuinGuardEntity.STATE_DEATH) {
            VertexConsumer evc = bufferSource.getBuffer(RenderType.eyes(EMISSIVE));
            model.renderToBuffer(poseStack, evc, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);
        }

        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
    }

    private static float computeImpactShudder(int animId, float sec) {
        if (animId == RuinGuardEntity.ANIM_MELEE_3HIT && sec >= 2.48F && sec <= 2.66F) {
            return 0.09F * (1.0F - (sec - 2.48F) / 0.18F);
        }
        if (animId == RuinGuardEntity.ANIM_LEAP_SLAM && sec >= 1.33F && sec <= 1.56F) {
            return 0.12F * (1.0F - (sec - 1.33F) / 0.23F);
        }
        if (animId == RuinGuardEntity.ANIM_STOMP_SHOCKWAVE && sec >= 0.88F && sec <= 1.08F) {
            return 0.08F * (1.0F - (sec - 0.88F) / 0.20F);
        }
        if (animId == RuinGuardEntity.ANIM_DOUBLE_CLAP && sec >= 1.03F && sec <= 1.20F) {
            return 0.07F * (1.0F - (sec - 1.03F) / 0.17F);
        }
        return 0.0F;
    }

    private static void updateBossMusic(RuinGuardEntity entity, AnimSM sm) {
        int st = entity.getBossState();
        boolean active = entity.isAlive() && st != RuinGuardEntity.STATE_DORMANT && st != RuinGuardEntity.STATE_DEATH;
        BossMusicManager.update(entity, MUSIC_ID, active);
    }

    private static void renderCombatVFX(
            RuinGuardEntity entity,
            float bodyYaw,
            float partialTicks,
            float liveTick,
            float currSec,
            PoseStack poseStack,
            MultiBufferSource bufferSource
    ) {
        VertexConsumer vc = bufferSource.getBuffer(RenderType.lightning());
        float teleR = entity.getTelegraphRadius();
        if (teleR > 0.5F) {
            // Render telegraph locked at world-space landing position (telePos), NOT stuck to the boss's feet!
            Vec3 telePos = entity.getTelegraphPos();
            double lerpX = Mth.lerp(partialTicks, entity.xo, entity.getX());
            double lerpY = Mth.lerp(partialTicks, entity.yo, entity.getY());
            double lerpZ = Mth.lerp(partialTicks, entity.zo, entity.getZ());

            poseStack.pushPose();
            poseStack.translate(telePos.x - lerpX, telePos.y - lerpY + 0.12D, telePos.z - lerpZ);
            Matrix4f m = poseStack.last().pose();
            float pulse = 0.85F + 0.15F * Mth.sin(liveTick * 0.7F);
            drawHorizontalRing(vc, m, teleR - 0.35F, teleR, 36, 255, 110, 35, (int) (200 * pulse));
            drawHorizontalRing(vc, m, 0.4F, teleR * pulse, 28, 255, 165, 55, 55);
            poseStack.popPose();
        }

        int anim = entity.getAnimId();

        if (anim == RuinGuardEntity.ANIM_CYCLONE_ACTIVE) {
            poseStack.pushPose();
            poseStack.translate(0.0D, 2.05D, 0.0D);
            poseStack.mulPose(Axis.YP.rotationDegrees(liveTick * 34.6F));
            Matrix4f m = poseStack.last().pose();
            drawHorizontalRing(vc, m, 3.25F, 4.75F, 32, 255, 220, 145, 145);
            drawHorizontalRing(vc, m, 4.35F, 4.85F, 32, 255, 140, 45, 195);
            poseStack.popPose();
        }

        if (anim == RuinGuardEntity.ANIM_MELEE_3HIT) {
            if (currSec >= 0.76F && currSec <= 1.04F) {
                drawMeleeCrescentSlash(vc, poseStack, bodyYaw, 1.85D, (currSec - 0.76F) / 0.28F, false);
            } else if (currSec >= 1.46F && currSec <= 1.74F) {
                drawMeleeCrescentSlash(vc, poseStack, bodyYaw, 1.85D, (currSec - 1.46F) / 0.28F, true);
            } else if (currSec >= 2.48F && currSec <= 2.92F) {
                drawExpandingShockwave(vc, poseStack, 5.4F, (currSec - 2.48F) / 0.44F);
            }
        }

        if (anim == RuinGuardEntity.ANIM_DOUBLE_CLAP && currSec >= 1.02F && currSec <= 1.38F) {
            float p = (currSec - 1.02F) / 0.36F;
            poseStack.pushPose();
            poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - bodyYaw));
            poseStack.translate(0.0D, 1.85D, -2.2D - p * 1.4D);
            poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
            Matrix4f m = poseStack.last().pose();
            float rOut = 0.8F + p * 2.6F;
            float rIn = Math.max(0.1F, rOut - 0.65F * (1.0F - p * 0.6F));
            int alpha = (int) (220 * (1.0F - p));
            drawHorizontalRing(vc, m, rIn, rOut, 24, 255, 230, 165, alpha);
            poseStack.popPose();
        }

        if (anim == RuinGuardEntity.ANIM_STOMP_SHOCKWAVE && currSec >= 0.88F && currSec <= 1.32F) {
            drawExpandingShockwave(vc, poseStack, 5.2F, (currSec - 0.88F) / 0.44F);
        } else if (anim == RuinGuardEntity.ANIM_LEAP_SLAM && currSec >= 1.33F && currSec <= 1.85F) {
            drawExpandingShockwave(vc, poseStack, 6.6F, (currSec - 1.33F) / 0.52F);
        }
    }

    private static void drawMeleeCrescentSlash(
            VertexConsumer vc,
            PoseStack poseStack,
            float bodyYaw,
            double heightY,
            float progress,
            boolean leftToRight
    ) {
        poseStack.pushPose();
        poseStack.translate(0.0D, heightY, 0.0D);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - bodyYaw));
        poseStack.mulPose(Axis.ZP.rotationDegrees(leftToRight ? -9.0F : 9.0F));
        Matrix4f mat = poseStack.last().pose();

        float alphaF = Mth.sin(Mth.clamp(progress, 0.0F, 1.0F) * (float) Math.PI);
        int alpha = (int) (225 * alphaF);
        float sweepOffset = (leftToRight ? -1.0F : 1.0F) * (progress - 0.45F) * 1.15F;
        float startAng = (float) (-Math.PI * 0.5D - 1.25D) + sweepOffset;
        float endAng = (float) (-Math.PI * 0.5D + 1.25D) + sweepOffset;
        int segs = 22;

        for (int i = 0; i < segs; i++) {
            float f0 = (float) i / segs;
            float f1 = (float) (i + 1) / segs;
            float a0 = Mth.lerp(f0, startAng, endAng);
            float a1 = Mth.lerp(f1, startAng, endAng);
            float w0 = Mth.sin(f0 * (float) Math.PI) * 1.65F;
            float w1 = Mth.sin(f1 * (float) Math.PI) * 1.65F;
            float rOut = 4.65F;
            float rIn0 = rOut - w0;
            float rIn1 = rOut - w1;
            float c0 = Mth.cos(a0), s0 = Mth.sin(a0);
            float c1 = Mth.cos(a1), s1 = Mth.sin(a1);
            int aInner = (int) (alpha * 0.25F);
            vc.addVertex(mat, c0 * rIn0, 0.0F, s0 * rIn0).setColor(255, 210, 120, aInner);
            vc.addVertex(mat, c0 * rOut, 0.0F, s0 * rOut).setColor(255, 245, 210, alpha);
            vc.addVertex(mat, c1 * rOut, 0.0F, s1 * rOut).setColor(255, 245, 210, alpha);
            vc.addVertex(mat, c1 * rIn1, 0.0F, s1 * rIn1).setColor(255, 210, 120, aInner);

            vc.addVertex(mat, c1 * rIn1, 0.0F, s1 * rIn1).setColor(255, 210, 120, aInner);
            vc.addVertex(mat, c1 * rOut, 0.0F, s1 * rOut).setColor(255, 245, 210, alpha);
            vc.addVertex(mat, c0 * rOut, 0.0F, s0 * rOut).setColor(255, 245, 210, alpha);
            vc.addVertex(mat, c0 * rIn0, 0.0F, s0 * rIn0).setColor(255, 210, 120, aInner);
        }
        poseStack.popPose();
    }

    private static void drawExpandingShockwave(VertexConsumer vc, PoseStack poseStack, float maxRadius, float progress) {
        float p = Mth.clamp(progress, 0.0F, 1.0F);
        float ease = 1.0F - (1.0F - p) * (1.0F - p);
        float rOut = 0.9F + ease * maxRadius;
        float rIn = Math.max(0.2F, rOut - (1.15F * (1.0F - p * 0.55F)));
        int alpha = (int) (235 * (1.0F - p));
        poseStack.pushPose();
        poseStack.translate(0.0D, 0.16D, 0.0D);
        Matrix4f m = poseStack.last().pose();
        drawHorizontalRing(vc, m, rIn, rOut, 36, 255, 200, 105, alpha);
        drawHorizontalRing(vc, m, Math.max(0.1F, rOut - 0.25F), rOut, 36, 255, 245, 210, alpha);
        poseStack.popPose();
    }

    private static void drawHorizontalRing(VertexConsumer vc, Matrix4f mat, float rIn, float rOut, int segs, int r, int g, int b, int a) {
        for (int i = 0; i < segs; i++) {
            float a0 = (float) (Math.PI * 2.0D * i / segs);
            float a1 = (float) (Math.PI * 2.0D * (i + 1) / segs);
            float c0 = Mth.cos(a0), s0 = Mth.sin(a0);
            float c1 = Mth.cos(a1), s1 = Mth.sin(a1);
            vc.addVertex(mat, c0 * rIn, 0.0F, s0 * rIn).setColor(r, g, b, a);
            vc.addVertex(mat, c0 * rOut, 0.0F, s0 * rOut).setColor(r, g, b, a);
            vc.addVertex(mat, c1 * rOut, 0.0F, s1 * rOut).setColor(r, g, b, a);
            vc.addVertex(mat, c1 * rIn, 0.0F, s1 * rIn).setColor(r, g, b, a);
            vc.addVertex(mat, c1 * rIn, 0.0F, s1 * rIn).setColor(r, g, b, a);
            vc.addVertex(mat, c1 * rOut, 0.0F, s1 * rOut).setColor(r, g, b, a);
            vc.addVertex(mat, c0 * rOut, 0.0F, s0 * rOut).setColor(r, g, b, a);
            vc.addVertex(mat, c0 * rIn, 0.0F, s0 * rIn).setColor(r, g, b, a);
        }
    }

    @Override
    public ResourceLocation getTextureLocation(RuinGuardEntity entity) {
        return TEXTURE;
    }
}

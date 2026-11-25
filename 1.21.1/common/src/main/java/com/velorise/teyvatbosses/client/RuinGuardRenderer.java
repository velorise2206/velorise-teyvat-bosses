package com.velorise.teyvatbosses.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.velorise.teyvatbosses.TeyvatBossesConstants;
import com.velorise.teyvatbosses.client.model.RuinGuardAnimations;
import com.velorise.teyvatbosses.client.model.RuinGuardModel;
import com.velorise.teyvatbosses.entity.RuinGuardEntity;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * High-impact renderer for Ruin Guard (GN/Hu-24601) featuring:
 * <ul>
 *   <li><b>Adaptive Shortest-Arc Pose-Snapshot State Machine:</b> Snapshots all 28 bones on every clip/epoch
 *       transition and cross-fades via wrapped shortest-arc angle interpolation (prevents 1080-deg Cyclone waist unwind).</li>
 *   <li><b>Procedural Secondary Motion Layer:</b> Stunned electrical micro-shudder, Phase 2 overdrive core pulse,
 *       and speed-scaled walk inertia.</li>
 *   <li><b>World-Locked Telegraphs & Finisher VFX:</b> Ground-locked rings, crescent slashes, expanding shockwaves,
 *       and Phase 2 Cataclysmic Overdrive Finisher dual-ring eruption.</li>
 * </ul>
 */
public class RuinGuardRenderer extends EntityRenderer<RuinGuardEntity> {
    private static final ResourceLocation TEXTURE = TeyvatBossesConstants.id("textures/entity/ruin_guard_boss.png");
    private static final ResourceLocation EMISSIVE = TeyvatBossesConstants.id("textures/entity/ruin_guard_boss_emissive.png");
    private static final ResourceLocation MUSIC_ID = TeyvatBossesConstants.id("music.ruin_guard_theme");

    private static final class BonePose {
        float x, y, z, xRot, yRot, zRot, xScale, yScale, zScale;

        void capture(ModelPart p) {
            x = p.x; y = p.y; z = p.z;
            xRot = p.xRot; yRot = p.yRot; zRot = p.zRot;
            xScale = p.xScale; yScale = p.yScale; zScale = p.zScale;
        }

        void copyFrom(BonePose o) {
            x = o.x; y = o.y; z = o.z;
            xRot = o.xRot; yRot = o.yRot; zRot = o.zRot;
            xScale = o.xScale; yScale = o.yScale; zScale = o.zScale;
        }
    }

    private static final class AnimSM {
        String currClip = null;
        int lastEpoch = -1;
        float blendStartTick = -100.0F;
        float blendDurationTicks = 7.0F;
        boolean hasLastPose = false;
        final Map<String, BonePose> fromPose = new HashMap<>();
        final Map<String, BonePose> lastPose = new HashMap<>();

        void reset() {
            currClip = null;
            lastEpoch = -1;
            blendStartTick = -100.0F;
            hasLastPose = false;
            fromPose.clear();
            lastPose.clear();
        }
    }

    private final RuinGuardModel<RuinGuardEntity> model;
    private final Map<RuinGuardEntity, AnimSM> stateMachines = new WeakHashMap<>();

    public RuinGuardRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 1.55F;
        this.model = new RuinGuardModel<>(context.bakeLayer(RuinGuardModel.LAYER_LOCATION));
    }

    private static float wrapRad(float rad) {
        float twoPi = (float) (Math.PI * 2.0D);
        float r = rad % twoPi;
        if (r > Math.PI) r -= twoPi;
        if (r < -Math.PI) r += twoPi;
        return r;
    }

    private static float lerpAngleRad(float from, float to, float alpha) {
        return from + wrapRad(to - from) * alpha;
    }

    private static float blendDurationFor(int animId) {
        return switch (animId) {
            case RuinGuardEntity.ANIM_STUNNED,
                 RuinGuardEntity.ANIM_DEATH,
                 RuinGuardEntity.ANIM_OVERDRIVE_FINISHER,
                 RuinGuardEntity.ANIM_MELEE_3HIT,
                 RuinGuardEntity.ANIM_DOUBLE_CLAP,
                 RuinGuardEntity.ANIM_STOMP_SHOCKWAVE,
                 RuinGuardEntity.ANIM_LEAP_SLAM -> 3.5F;
            case RuinGuardEntity.ANIM_CYCLONE_ACTIVE,
                 RuinGuardEntity.ANIM_CYCLONE_RECOVER -> 5.0F;
            default -> 7.0F;
        };
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
        int animId = entity.getAnimId();
        int epoch = entity.getAnimEpoch();

        AnimSM sm = stateMachines.computeIfAbsent(entity, k -> new AnimSM());
        updateBossMusic(entity);

        if (sm.currClip != null && (!sm.currClip.equals(clip) || sm.lastEpoch != epoch)) {
            if (sm.hasLastPose) {
                for (Map.Entry<String, BonePose> e : sm.lastPose.entrySet()) {
                    sm.fromPose.computeIfAbsent(e.getKey(), k -> new BonePose()).copyFrom(e.getValue());
                }
                sm.blendStartTick = liveTick;
                sm.blendDurationTicks = blendDurationFor(animId);
            }
        }
        sm.currClip = clip;
        sm.lastEpoch = epoch;

        float bodyYaw = Mth.rotLerp(partialTicks, entity.yBodyRotO, entity.yBodyRot);

        renderCombatVFX(entity, bodyYaw, partialTicks, liveTick, currSec, poseStack, bufferSource);

        poseStack.pushPose();
        float shudder = computeImpactShudder(animId, currSec);
        if (shudder > 0.001F) {
            float sx = Mth.sin(liveTick * 31.0F) * shudder;
            float sz = Mth.cos(liveTick * 27.0F) * shudder;
            poseStack.translate(sx, 0.0D, sz);
        }

        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - bodyYaw));
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.translate(0.0D, -1.501D, 0.0D);

        // 1. Evaluate target keyframe pose (always calls full resetPose including scales)
        RuinGuardAnimations.apply(model, clip, currSec);

        // 2. Procedural secondary motion layer
        applyProceduralSecondaryMotion(entity, animId, liveTick, currSec);

        // 3. Shortest-arc pose-snapshot cross-fade across all 28 bones
        float blendElapsed = liveTick - sm.blendStartTick;
        if (sm.hasLastPose && blendElapsed >= 0.0F && blendElapsed < sm.blendDurationTicks) {
            float rawA = Mth.clamp(blendElapsed / sm.blendDurationTicks, 0.0F, 1.0F);
            float alpha = rawA * rawA * (3.0F - 2.0F * rawA);
            for (String bName : model.getBoneNames()) {
                ModelPart part = model.getBone(bName);
                BonePose prev = sm.fromPose.get(bName);
                if (part == null || prev == null) continue;
                part.x = Mth.lerp(alpha, prev.x, part.x);
                part.y = Mth.lerp(alpha, prev.y, part.y);
                part.z = Mth.lerp(alpha, prev.z, part.z);
                part.xRot = lerpAngleRad(prev.xRot, part.xRot, alpha);
                part.yRot = lerpAngleRad(prev.yRot, part.yRot, alpha);
                part.zRot = lerpAngleRad(prev.zRot, part.zRot, alpha);
                part.xScale = Mth.lerp(alpha, prev.xScale, part.xScale);
                part.yScale = Mth.lerp(alpha, prev.yScale, part.yScale);
                part.zScale = Mth.lerp(alpha, prev.zScale, part.zScale);
            }
        }

        // 4. Snapshot final rendered pose for seamless future transitions
        for (String bName : model.getBoneNames()) {
            ModelPart part = model.getBone(bName);
            if (part != null) {
                sm.lastPose.computeIfAbsent(bName, k -> new BonePose()).capture(part);
            }
        }
        sm.hasLastPose = true;

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

    private void applyProceduralSecondaryMotion(RuinGuardEntity entity, int animId, float liveTick, float currSec) {
        // Stunned electrical short-circuit twitching after initial collapse
        if (animId == RuinGuardEntity.ANIM_STUNNED && currSec > 1.25F) {
            float twitchGate = Math.max(0.0F, Mth.sin(liveTick * 0.42F) - 0.72F) * 3.5F;
            if (twitchGate > 0.01F) {
                ModelPart torso = model.getBone("torso");
                ModelPart eye = model.getBone("eye_core");
                if (torso != null) {
                    torso.zRot += Mth.sin(liveTick * 2.9F) * 0.055F * twitchGate;
                    torso.xRot += Mth.cos(liveTick * 2.3F) * 0.035F * twitchGate;
                }
                if (eye != null) {
                    float flick = 0.18F + 0.55F * twitchGate;
                    eye.xScale = flick;
                    eye.yScale = flick;
                }
            }
        }

        // Phase 2 Overdrive: pulsing eye_core & back_reactor
        if (entity.isPhase2() && entity.getBossState() == RuinGuardEntity.STATE_COMBAT) {
            float pulse = 1.08F + 0.10F * Mth.sin(liveTick * 0.48F);
            ModelPart eye = model.getBone("eye_core");
            ModelPart reactor = model.getBone("back_reactor");
            if (eye != null) {
                eye.xScale *= pulse;
                eye.yScale *= pulse;
            }
            if (reactor != null) {
                reactor.xScale *= pulse;
                reactor.yScale *= pulse;
            }
        }
    }

    private static float computeImpactShudder(int animId, float sec) {
        if (animId == RuinGuardEntity.ANIM_MELEE_3HIT && sec >= 2.48F && sec <= 2.66F) {
            return 0.09F * (1.0F - (sec - 2.48F) / 0.18F);
        }
        if (animId == RuinGuardEntity.ANIM_LEAP_SLAM && sec >= 1.33F && sec <= 1.56F) {
            return 0.12F * (1.0F - (sec - 1.33F) / 0.23F);
        }
        if (animId == RuinGuardEntity.ANIM_OVERDRIVE_FINISHER && sec >= 1.23F && sec <= 1.55F) {
            return 0.15F * (1.0F - (sec - 1.23F) / 0.32F);
        }
        if (animId == RuinGuardEntity.ANIM_STOMP_SHOCKWAVE && sec >= 0.88F && sec <= 1.08F) {
            return 0.08F * (1.0F - (sec - 0.88F) / 0.20F);
        }
        if (animId == RuinGuardEntity.ANIM_DOUBLE_CLAP && sec >= 1.03F && sec <= 1.20F) {
            return 0.07F * (1.0F - (sec - 1.03F) / 0.17F);
        }
        return 0.0F;
    }

    private static void updateBossMusic(RuinGuardEntity entity) {
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
            Vec3 telePos = entity.getTelegraphPos();
            double lerpX = Mth.lerp(partialTicks, entity.xo, entity.getX());
            double lerpY = Mth.lerp(partialTicks, entity.yo, entity.getY());
            double lerpZ = Mth.lerp(partialTicks, entity.zo, entity.getZ());

            poseStack.pushPose();
            poseStack.translate(telePos.x - lerpX, telePos.y - lerpY + 0.12D, telePos.z - lerpZ);
            Matrix4f m = poseStack.last().pose();
            float pulse = 0.85F + 0.15F * Mth.sin(liveTick * 0.7F);
            int rCol = entity.getAnimId() == RuinGuardEntity.ANIM_OVERDRIVE_FINISHER ? 255 : 255;
            int gCol = entity.getAnimId() == RuinGuardEntity.ANIM_OVERDRIVE_FINISHER ? 65 : 110;
            drawHorizontalRing(vc, m, teleR - 0.35F, teleR, 36, rCol, gCol, 35, (int) (200 * pulse));
            drawHorizontalRing(vc, m, 0.4F, teleR * pulse, 28, 255, 165, 55, 55);
            poseStack.popPose();
        }

        int anim = entity.getAnimId();

        if (anim == RuinGuardEntity.ANIM_CYCLONE_ACTIVE) {
            float spinYaw = liveTick * 50.0F;
            drawSpinWindArc(vc, poseStack, spinYaw, 2.05D, 3.4F, 4.55F, 155, 255, 238, 195);
            drawSpinWindArc(vc, poseStack, spinYaw + 180.0F, 2.05D, 3.4F, 4.55F, 155, 255, 238, 195);
            drawSpinWindArc(vc, poseStack, spinYaw + 90.0F, 1.75D, 3.9F, 4.70F, 95, 255, 190, 95);
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
        } else if (anim == RuinGuardEntity.ANIM_OVERDRIVE_FINISHER && currSec >= 1.23F && currSec <= 1.95F) {
            float p = (currSec - 1.23F) / 0.72F;
            drawExpandingShockwave(vc, poseStack, 8.2F, p);
            if (p < 0.75F) {
                float p2 = p / 0.75F;
                poseStack.pushPose();
                poseStack.translate(0.0D, 0.35D + p2 * 0.8D, 0.0D);
                float rOut = 1.4F + p2 * 6.4F;
                drawHorizontalRing(vc, poseStack.last().pose(), Math.max(0.2F, rOut - 0.9F), rOut, 36, 255, 85, 35, (int) (215 * (1.0F - p2)));
                poseStack.popPose();
            }
        }
    }

    private static void drawSpinWindArc(
            VertexConsumer vc, PoseStack poseStack, float yawDeg, double heightY,
            float rInBase, float rOut, int maxAlpha, int cr, int cg, int cb
    ) {
        poseStack.pushPose();
        poseStack.translate(0.0D, heightY, 0.0D);
        poseStack.mulPose(Axis.YP.rotationDegrees(-yawDeg));
        Matrix4f mat = poseStack.last().pose();
        int segs = 16;
        float arcRad = (float) (Math.PI * 0.72D);
        for (int i = 0; i < segs; i++) {
            float f0 = (float) i / segs;
            float f1 = (float) (i + 1) / segs;
            float a0 = f0 * arcRad;
            float a1 = f1 * arcRad;
            float w0 = (rOut - rInBase) * Mth.sin(f0 * (float) Math.PI);
            float w1 = (rOut - rInBase) * Mth.sin(f1 * (float) Math.PI);
            float rIn0 = rOut - Math.max(0.08F, w0);
            float rIn1 = rOut - Math.max(0.08F, w1);
            int al0 = (int) (maxAlpha * f0);
            int al1 = (int) (maxAlpha * f1);
            float c0 = Mth.cos(a0), s0 = Mth.sin(a0);
            float c1 = Mth.cos(a1), s1 = Mth.sin(a1);
            vc.addVertex(mat, c0 * rIn0, 0.0F, s0 * rIn0).setColor(cr, cg, cb, al0 / 4);
            vc.addVertex(mat, c0 * rOut, 0.0F, s0 * rOut).setColor(cr, cg, cb, al0);
            vc.addVertex(mat, c1 * rOut, 0.0F, s1 * rOut).setColor(cr, cg, cb, al1);
            vc.addVertex(mat, c1 * rIn1, 0.0F, s1 * rIn1).setColor(cr, cg, cb, al1 / 4);

            vc.addVertex(mat, c1 * rIn1, 0.0F, s1 * rIn1).setColor(cr, cg, cb, al1 / 4);
            vc.addVertex(mat, c1 * rOut, 0.0F, s1 * rOut).setColor(cr, cg, cb, al1);
            vc.addVertex(mat, c0 * rOut, 0.0F, s0 * rOut).setColor(cr, cg, cb, al0);
            vc.addVertex(mat, c0 * rIn0, 0.0F, s0 * rIn0).setColor(cr, cg, cb, al0 / 4);
        }
        poseStack.popPose();
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
        poseStack.mulPose(Axis.ZP.rotationDegrees(leftToRight ? -28.0F : 28.0F));
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

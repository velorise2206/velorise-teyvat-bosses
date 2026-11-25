package com.velorise.teyvatbosses.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.velorise.teyvatbosses.TeyvatBossesConstants;
import com.velorise.teyvatbosses.client.model.TheOpenEyedAnimations;
import com.velorise.teyvatbosses.client.model.TheOpenEyedModel;
import com.velorise.teyvatbosses.entity.ElementalStarEntity;
import com.velorise.teyvatbosses.entity.TheOpenEyedEntity;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Overhauled Genshin-accurate AAA renderer for TheOpenEyedEntity (World-Watch Sentry):
 * <ul>
 *   <li><b>True 8-tick Shortest-Angle Pose-Snapshot State Machine:</b> Smoothly cross-fades all 18 bones between
 *       every combat/ritual/stun state with zero snap glitches.</li>
 *   <li><b>Double-Sided Additive VFX Geometry:</b> Every golden slash crescent, lance beam, ground telegraph ring,
 *       Supreme Overseer vertical clock-ring, and Delirium crimson 4-point eye renders double-sided so OpenGL backface
 *       culling never hides them from any camera angle.</li>
 *   <li><b>Authentic Elevation & Saturated Crimson Palette:</b> Floats cleanly above marble floor in combat and
 *       ascends gracefully (+2.2 blocks) during Phenomena's Delirium beneath a vivid blood-crimson 4-point eye.</li>
 * </ul>
 */
public class TheOpenEyedRenderer extends EntityRenderer<TheOpenEyedEntity> {
    private static final ResourceLocation TEXTURE = TeyvatBossesConstants.id("textures/entity/the_open_eyed.png");
    private static final float BOSS_RENDER_SCALE = 1.18F;
    private static final float BLEND_DURATION_TICKS = 8.0F;

    private static final String[] ALL_BONES = {
            "root", "body", "torso", "chest_eye", "head", "halo_inner", "halo_outer",
            "giant_wing_left", "giant_wing_left_lower", "broken_wing_right",
            "arm_left", "forearm_left", "arm_right", "corrupt_cubes_right", "forearm_right", "celestial_spear",
            "skirt_front", "skirt_back", "lower_wing_left", "lower_wing_right", "cube_tail"
    };

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

    private static final class AnimStateMachine {
        String lastClip = null;
        int lastEpoch = -1;
        float blendStartLiveTick = -100.0F;
        boolean hasLastPose = false;
        float currentAscendY = 0.0F;
        final Map<String, BonePose> fromPose = new HashMap<>();
        final Map<String, BonePose> lastPose = new HashMap<>();
    }

    private final TheOpenEyedModel<TheOpenEyedEntity> model;
    private final Map<TheOpenEyedEntity, AnimStateMachine> stateMachines = new WeakHashMap<>();

    public TheOpenEyedRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 1.45F;
        this.model = new TheOpenEyedModel<>(context.bakeLayer(TheOpenEyedModel.LAYER_LOCATION));
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

    @Override
    public void render(
            TheOpenEyedEntity entity,
            float entityYaw,
            float partialTicks,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight
    ) {
        float yaw = Mth.rotLerp(partialTicks, entity.yBodyRotO, entity.yBodyRot);
        int holdAt = entity.getAnimHoldAt();
        int rawTick = entity.getAnimTick();
        boolean holding = (holdAt >= 0 && rawTick >= holdAt);
        float animTicksF = holding ? (float) holdAt : (rawTick + partialTicks);
        float currSec = animTicksF / 20.0F;
        String currClip = entity.getCurrentAnimName();
        int currEpoch = entity.getAnimEpoch();
        float liveT = entity.tickCount + partialTicks;

        AnimStateMachine sm = stateMachines.computeIfAbsent(entity, k -> new AnimStateMachine());
        if (sm.lastClip != null && (!sm.lastClip.equals(currClip) || sm.lastEpoch != currEpoch)) {
            if (sm.hasLastPose) {
                for (Map.Entry<String, BonePose> e : sm.lastPose.entrySet()) {
                    sm.fromPose.computeIfAbsent(e.getKey(), k -> new BonePose()).copyFrom(e.getValue());
                }
                sm.blendStartLiveTick = liveT;
            }
        }
        sm.lastClip = currClip;
        sm.lastEpoch = currEpoch;

        // Smooth visual elevation: +0.38 hover in combat, +2.25 regal ascension in Phenomena's Delirium (eye.mp4 01:08-01:25)
        float targetAscend = switch (entity.getBossState()) {
            case TheOpenEyedEntity.STATE_DELIRIUM -> 2.25F;
            case TheOpenEyedEntity.STATE_OVERSEER -> 0.85F;
            case TheOpenEyedEntity.STATE_STUNNED, TheOpenEyedEntity.STATE_DEATH -> 0.0F;
            default -> 0.38F + 0.12F * Mth.sin(liveT * 0.09F);
        };
        sm.currentAscendY += (targetAscend - sm.currentAscendY) * 0.08F;

        // 1. Render model first (entityCutoutNoCull)
        poseStack.pushPose();
        poseStack.translate(0.0D, sm.currentAscendY, 0.0D);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - yaw));
        poseStack.scale(-BOSS_RENDER_SCALE, -BOSS_RENDER_SCALE, BOSS_RENDER_SCALE);
        poseStack.translate(0.0D, -1.501D, 0.0D);

        TheOpenEyedAnimations.apply(model, currClip, currSec);

        // Continuous organic micro-motion during hold states (Supreme Overseer / Phenomena's Delirium)
        if (holding) {
            var bodyBone = model.getBone("body");
            if (bodyBone != null) bodyBone.y += Mth.sin(liveT * 0.14F) * 1.4F;
            var wingL = model.getBone("giant_wing_left");
            if (wingL != null) wingL.yRot += Mth.sin(liveT * 0.16F) * 0.18F;
            var wingLL = model.getBone("giant_wing_left_lower");
            if (wingLL != null) wingLL.zRot += Mth.cos(liveT * 0.16F) * 0.14F;
            var hIn = model.getBone("halo_inner");
            if (hIn != null) hIn.zRot += liveT * 0.24F;
            var hOut = model.getBone("halo_outer");
            if (hOut != null) hOut.zRot -= liveT * 0.16F;
            var cubes = model.getBone("corrupt_cubes_right");
            if (cubes != null) cubes.yRot += liveT * 0.22F;
            var eye = model.getBone("chest_eye");
            if (eye != null) {
                float p = 1.0F + 0.16F * Mth.sin(liveT * 0.35F);
                eye.xScale *= p; eye.yScale *= p; eye.zScale *= p;
            }
        }

        // Smooth 8-tick shortest-arc Pose-Snapshot cross-fade
        float blendElapsed = liveT - sm.blendStartLiveTick;
        if (sm.hasLastPose && blendElapsed >= 0.0F && blendElapsed < BLEND_DURATION_TICKS) {
            float rawA = Mth.clamp(blendElapsed / BLEND_DURATION_TICKS, 0.0F, 1.0F);
            float alpha = rawA * rawA * (3.0F - 2.0F * rawA);
            for (String bName : ALL_BONES) {
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

        for (String bName : ALL_BONES) {
            ModelPart part = model.getBone(bName);
            if (part != null) {
                sm.lastPose.computeIfAbsent(bName, k -> new BonePose()).capture(part);
            }
        }
        sm.hasLastPose = true;

        VertexConsumer vc = bufferSource.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        int overlay = OverlayTexture.pack(OverlayTexture.u(0.0F), OverlayTexture.v(entity.hurtTime > 0 || entity.deathTime > 0));
        model.renderToBuffer(poseStack, vc, packedLight, overlay, 0xFFFFFFFF);
        poseStack.popPose();

        // 2. Render all double-sided world-space combat & ritual VFX (fresh lightning buffer!)
        renderCombatAndRitualVFX(entity, yaw, animTicksF, liveT, sm.currentAscendY, poseStack, bufferSource);

        super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
    }

    private static void renderCombatAndRitualVFX(
            TheOpenEyedEntity entity, float yaw, float t, float liveT, float ascendY,
            PoseStack poseStack, MultiBufferSource bufferSource
    ) {
        VertexConsumer vc = bufferSource.getBuffer(RenderType.lightning());
        int anim = entity.getAnimId();
        int st = entity.getBossState();

        // 1. Descend Blood-Crimson 4-Point Celestial Star Rift (eye.mp4 00:13)
        if (anim == TheOpenEyedEntity.ANIM_DESCEND && t < 30.0F) {
            float fade = t < 8.0F ? t / 8.0F : (t > 22.0F ? Math.max(0.0F, (30.0F - t) / 8.0F) : 1.0F);
            poseStack.pushPose();
            poseStack.translate(0.0D, 3.8D, 0.0D);
            poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - yaw));
            drawDoubleSidedStar(vc, poseStack.last().pose(), 3.6F * fade, 2.7F * fade, 0.50F, 195, 12, 22, (int) (210 * fade));
            drawDoubleSidedStar(vc, poseStack.last().pose(), 2.1F * fade, 1.4F * fade, 0.22F, 255, 55, 55, (int) (190 * fade));
            poseStack.popPose();
        }

        // 2. Double Slash Blazing Double-Sided Golden Crescents (eye.mp4 00:21, 02:50, 03:04)
        if (anim == TheOpenEyedEntity.ANIM_DOUBLE_SLASH) {
            if (t >= 11.0F && t <= 22.0F) {
                float p = (t - 11.0F) / 11.0F;
                float fade = 1.0F - Math.abs(p - 0.5F) * 1.6F;
                drawSlashCrescent(poseStack, vc, yaw - 85.0F + p * 175.0F, 125.0F, -1.0F, 2.6F, 6.8F, 1.65F + ascendY,
                        255, 228, 105, 255, 150, 28, Mth.clamp(fade, 0.0F, 1.0F));
            }
            if (t >= 26.0F && t <= 38.0F) {
                float p = (t - 26.0F) / 12.0F;
                float fade = 1.0F - Math.abs(p - 0.5F) * 1.6F;
                drawSlashCrescent(poseStack, vc, yaw + 90.0F - p * 185.0F, 135.0F, 1.0F, 2.8F, 7.2F, 1.85F + ascendY,
                        255, 238, 125, 255, 140, 22, Mth.clamp(fade, 0.0F, 1.0F));
            }
        }

        // 3. Double Lunge & Spear Thrust Blazing Golden Lance Beam (eye.mp4 00:43, 01:41, 02:17)
        if (anim == TheOpenEyedEntity.ANIM_DOUBLE_LUNGE) {
            if (t >= 13.0F && t <= 23.0F) {
                float fade = 1.0F - Math.abs((t - 13.0F) / 10.0F - 0.35F) * 1.4F;
                drawLanceBeam(poseStack, vc, yaw, 8.2F, 0.65F, 1.75F + ascendY, Mth.clamp(fade, 0.0F, 1.0F));
            }
            if (t >= 31.0F && t <= 43.0F) {
                float fade = 1.0F - Math.abs((t - 31.0F) / 12.0F - 0.35F) * 1.4F;
                drawLanceBeam(poseStack, vc, yaw, 10.2F, 0.80F, 1.75F + ascendY, Mth.clamp(fade, 0.0F, 1.0F));
            }
        } else if (anim == TheOpenEyedEntity.ANIM_SPEAR_THRUST && t >= 20.0F && t <= 34.0F) {
            float fade = 1.0F - Math.abs((t - 20.0F) / 14.0F - 0.35F) * 1.4F;
            drawLanceBeam(poseStack, vc, yaw, 11.5F, 0.82F, 1.75F + ascendY, Mth.clamp(fade, 0.0F, 1.0F));
        }

        // 4. Spear Slam & Leaping Slam Double-Sided Golden Ground Ring + Shockwave (eye.mp4 02:52-02:53)
        if (anim == TheOpenEyedEntity.ANIM_SPEAR_SLAM && t >= 10.0F && t <= 32.0F) {
            float rad = yaw * Mth.DEG_TO_RAD;
            double fx = -Math.sin(rad) * 4.0D, fz = Math.cos(rad) * 4.0D;
            poseStack.pushPose();
            poseStack.translate(fx, 0.08D, fz);
            Matrix4f gm = poseStack.last().pose();
            if (t < 21.0F) {
                float p = (t - 10.0F) / 11.0F;
                drawHorizontalRing(vc, gm, 3.75F, 4.15F, 30, 255, 205, 85, (int) (195 * p));
            } else {
                float p = (t - 21.0F) / 11.0F;
                float rWave = 1.2F + 4.6F * p;
                drawHorizontalRing(vc, gm, Math.max(0.2F, rWave - 0.65F), rWave, 30, 255, 225, 110, (int) (230 * (1.0F - p)));
            }
            poseStack.popPose();
        }

        // 5. Forced Teleport Crimson Cube Vortex (eye.mp4 00:46, 01:33, 02:07)
        if (anim == TheOpenEyedEntity.ANIM_FORCED_TELEPORT && t >= 8.0F && t <= 36.0F) {
            drawOrbitingCorruptionCubes(poseStack, vc, liveT, 2.2F + ascendY, 2.3F, 10, 1.0F);
        }

        // 6. Supreme Overseer Double-Sided Golden Clock-Ring + Floor Boundary + Colored Elemental Sweep
        if (st == TheOpenEyedEntity.STATE_OVERSEER) {
            int[] rgb = colorForElement(entity.getOverseerElement());
            if (t < 43.0F) {
                poseStack.pushPose();
                float rad = yaw * Mth.DEG_TO_RAD;
                poseStack.translate(-Math.sin(rad) * 1.45D, 2.9D + ascendY, Math.cos(rad) * 1.45D);
                poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - yaw));
                poseStack.mulPose(Axis.ZP.rotationDegrees(liveT * 4.5F));
                Matrix4f cm = poseStack.last().pose();
                drawVerticalRing(vc, cm, 1.85F, 2.15F, 30, 255, 210, 85, 215);
                drawVerticalRing(vc, cm, 1.25F, 1.42F, 22, rgb[0], rgb[1], rgb[2], 185);
                poseStack.popPose();

                // Wide golden floor monitoring boundary ring (double-sided!)
                poseStack.pushPose();
                poseStack.translate(0.0D, 0.08D, 0.0D);
                drawHorizontalRing(vc, poseStack.last().pose(), 8.6F, 9.2F, 42, 255, 210, 95, 175);
                poseStack.popPose();
            } else if (t >= 48.0F && t <= 60.0F) {
                float p = (t - 48.0F) / 12.0F;
                float fade = 1.0F - p * 0.85F;
                drawSlashCrescent(poseStack, vc, yaw - p * 360.0F, 230.0F, 1.0F, 3.8F, 10.0F, 1.55F + ascendY,
                        255, 248, 225, rgb[0], rgb[1], rgb[2], Mth.clamp(fade, 0.0F, 1.0F));
            }
        }

        // 7. Phenomena's Delirium Vivid Blood-Crimson 4-Point Eye + 12 Orbiting Corruption Cubes (eye.mp4 01:08-01:26)
        if (st == TheOpenEyedEntity.STATE_DELIRIUM) {
            float prog = entity.getFuryProgress();
            float eyeScale = 1.35F + 1.05F * prog + 0.12F * Mth.sin(liveT * 0.25F);
            poseStack.pushPose();
            poseStack.translate(0.0D, 5.4D + ascendY, 0.0D);
            poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - yaw));
            // Saturated blood-crimson outer + bright scarlet core (NO white additive blowout!)
            drawDoubleSidedStar(vc, poseStack.last().pose(), 2.3F * eyeScale, 1.65F * eyeScale, 0.38F, 190, 10, 18, 215);
            drawDoubleSidedStar(vc, poseStack.last().pose(), 1.25F * eyeScale, 0.85F * eyeScale, 0.17F, 255, 42, 48, 195);
            poseStack.popPose();

            drawOrbitingCorruptionCubes(poseStack, vc, liveT, 2.8F + ascendY, 3.5F, 12, 1.0F);
        }
    }

    private static void quadDS(VertexConsumer vc, Matrix4f m,
                               float x0, float y0, float z0, int r0, int g0, int b0, int a0,
                               float x1, float y1, float z1, int r1, int g1, int b1, int a1,
                               float x2, float y2, float z2, int r2, int g2, int b2, int a2,
                               float x3, float y3, float z3, int r3, int g3, int b3, int a3) {
        vc.addVertex(m, x0, y0, z0).setColor(r0, g0, b0, a0);
        vc.addVertex(m, x1, y1, z1).setColor(r1, g1, b1, a1);
        vc.addVertex(m, x2, y2, z2).setColor(r2, g2, b2, a2);
        vc.addVertex(m, x3, y3, z3).setColor(r3, g3, b3, a3);

        vc.addVertex(m, x3, y3, z3).setColor(r3, g3, b3, a3);
        vc.addVertex(m, x2, y2, z2).setColor(r2, g2, b2, a2);
        vc.addVertex(m, x1, y1, z1).setColor(r1, g1, b1, a1);
        vc.addVertex(m, x0, y0, z0).setColor(r0, g0, b0, a0);
    }

    private static void drawOrbitingCorruptionCubes(PoseStack poseStack, VertexConsumer vc, float liveT,
                                                    float yHeight, float radius, int count, float alphaMul) {
        poseStack.pushPose();
        poseStack.translate(0.0D, yHeight, 0.0D);
        for (int i = 0; i < count; i++) {
            float ang = liveT * 0.075F + (float) (Math.PI * 2.0D * i / count);
            float cx = Mth.cos(ang) * radius;
            float cz = Mth.sin(ang) * radius;
            float cy = Mth.sin(liveT * 0.14F + i * 1.1F) * 0.55F;
            poseStack.pushPose();
            poseStack.translate(cx, cy, cz);
            poseStack.mulPose(Axis.YP.rotationDegrees(liveT * 5.0F + i * 37.0F));
            poseStack.mulPose(Axis.XP.rotationDegrees(liveT * 3.5F + i * 23.0F));
            drawSmallCube(vc, poseStack.last().pose(), 0.22F, 195, 16, 24, (int) (200 * alphaMul));
            poseStack.popPose();
        }
        poseStack.popPose();
    }

    private static void drawSmallCube(VertexConsumer vc, Matrix4f m, float s, int r, int g, int b, int a) {
        float[][] faces = {
                {-s,-s, s,  s,-s, s,  s, s, s, -s, s, s},
                { s,-s,-s, -s,-s,-s, -s, s,-s,  s, s,-s},
                {-s, s,-s, -s, s, s,  s, s, s,  s, s,-s},
                {-s,-s, s, -s,-s,-s,  s,-s,-s,  s,-s, s},
                { s,-s, s,  s,-s,-s,  s, s,-s,  s, s, s},
                {-s,-s,-s, -s,-s, s, -s, s, s, -s, s,-s}
        };
        for (float[] f : faces) {
            vc.addVertex(m, f[0], f[1], f[2]).setColor(r, g, b, a);
            vc.addVertex(m, f[3], f[4], f[5]).setColor(r, g, b, a);
            vc.addVertex(m, f[6], f[7], f[8]).setColor(r, g, b, a);
            vc.addVertex(m, f[9], f[10], f[11]).setColor(r, g, b, a);
        }
    }

    private static void drawLanceBeam(PoseStack poseStack, VertexConsumer vc, float yaw, float len, float rad, float yH, float alphaMul) {
        if (alphaMul <= 0.02F) return;
        poseStack.pushPose();
        poseStack.translate(0.0D, yH, 0.0D);
        poseStack.mulPose(Axis.YP.rotationDegrees(-yaw));
        Matrix4f m = poseStack.last().pose();
        int aCore = (int) (235 * alphaMul);
        int aEdge = (int) (165 * alphaMul);
        quadDS(vc, m,
                -rad, 0, 0.5F, 255, 195, 60, aEdge,
                rad, 0, 0.5F, 255, 195, 60, aEdge,
                rad * 0.22F, 0, len, 255, 248, 200, aCore,
                -rad * 0.22F, 0, len, 255, 248, 200, aCore);
        quadDS(vc, m,
                0, -rad, 0.5F, 255, 195, 60, aEdge,
                0, rad, 0.5F, 255, 195, 60, aEdge,
                0, rad * 0.22F, len, 255, 248, 200, aCore,
                0, -rad * 0.22F, len, 255, 248, 200, aCore);
        poseStack.popPose();
    }

    private static void drawSlashCrescent(PoseStack poseStack, VertexConsumer vc, float leadDeg, float arcDeg, float trailSign,
                                          float rIn, float rOut, float yH,
                                          int r1, int g1, int b1, int r2, int g2, int b2, float alphaMul) {
        if (alphaMul <= 0.02F) return;
        poseStack.pushPose();
        poseStack.translate(0.0D, yH, 0.0D);
        Matrix4f m = poseStack.last().pose();
        int segs = 24;
        for (int i = 0; i < segs; i++) {
            float f0 = (float) i / segs;
            float f1 = (float) (i + 1) / segs;
            float a0 = (leadDeg + trailSign * f0 * arcDeg) * Mth.DEG_TO_RAD;
            float a1 = (leadDeg + trailSign * f1 * arcDeg) * Mth.DEG_TO_RAD;
            float dx0 = -Mth.sin(a0), dz0 = Mth.cos(a0);
            float dx1 = -Mth.sin(a1), dz1 = Mth.cos(a1);
            int al0 = (int) (230 * alphaMul * (1.0F - f0));
            int al1 = (int) (230 * alphaMul * (1.0F - f1));
            float w0 = rIn + (rOut - rIn) * (1.0F - f0 * 0.42F);
            float w1 = rIn + (rOut - rIn) * (1.0F - f1 * 0.42F);
            quadDS(vc, m,
                    dx0 * rIn, 0.0F, dz0 * rIn, r2, g2, b2, al0 / 3,
                    dx0 * w0, 0.08F, dz0 * w0, r1, g1, b1, al0,
                    dx1 * w1, 0.08F, dz1 * w1, r1, g1, b1, al1,
                    dx1 * rIn, 0.0F, dz1 * rIn, r2, g2, b2, al1 / 3);
        }
        poseStack.popPose();
    }

    private static void drawDoubleSidedStar(VertexConsumer vc, Matrix4f m, float vLen, float hLen, float midW,
                                            int r, int g, int b, int a) {
        quadDS(vc, m,
                0, vLen, 0, r, g, b, a,
                midW, 0, 0, r, g, b, a,
                0, -vLen, 0, r, g, b, a,
                -midW, 0, 0, r, g, b, a);
        quadDS(vc, m,
                hLen, 0, 0, r, g, b, a,
                0, -midW, 0, r, g, b, a,
                -hLen, 0, 0, r, g, b, a,
                0, midW, 0, r, g, b, a);
    }

    private static void drawHorizontalRing(VertexConsumer vc, Matrix4f mat, float rIn, float rOut, int segs, int r, int g, int b, int a) {
        for (int i = 0; i < segs; i++) {
            float a0 = (float) (Math.PI * 2.0D * i / segs);
            float a1 = (float) (Math.PI * 2.0D * (i + 1) / segs);
            float c0 = Mth.cos(a0), s0 = Mth.sin(a0);
            float c1 = Mth.cos(a1), s1 = Mth.sin(a1);
            quadDS(vc, mat,
                    c0 * rIn, 0, s0 * rIn, r, g, b, a,
                    c0 * rOut, 0, s0 * rOut, r, g, b, a,
                    c1 * rOut, 0, s1 * rOut, r, g, b, a,
                    c1 * rIn, 0, s1 * rIn, r, g, b, a);
        }
    }

    private static void drawVerticalRing(VertexConsumer vc, Matrix4f mat, float rIn, float rOut, int segs, int r, int g, int b, int a) {
        for (int i = 0; i < segs; i++) {
            float a0 = (float) (Math.PI * 2.0D * i / segs);
            float a1 = (float) (Math.PI * 2.0D * (i + 1) / segs);
            float c0 = Mth.cos(a0), s0 = Mth.sin(a0);
            float c1 = Mth.cos(a1), s1 = Mth.sin(a1);
            quadDS(vc, mat,
                    c0 * rIn, s0 * rIn, 0, r, g, b, a,
                    c0 * rOut, s0 * rOut, 0, r, g, b, a,
                    c1 * rOut, s1 * rOut, 0, r, g, b, a,
                    c1 * rIn, s1 * rIn, 0, r, g, b, a);
        }
    }

    private static int[] colorForElement(int id) {
        return switch (id) {
            case ElementalStarEntity.ELEM_PYRO    -> new int[]{255, 105, 45};
            case ElementalStarEntity.ELEM_HYDRO   -> new int[]{60, 180, 255};
            case ElementalStarEntity.ELEM_ELECTRO -> new int[]{195, 105, 255};
            case ElementalStarEntity.ELEM_CRYO    -> new int[]{135, 238, 255};
            default                               -> new int[]{255, 210, 95};
        };
    }

    @Override
    public ResourceLocation getTextureLocation(TheOpenEyedEntity entity) {
        return TEXTURE;
    }
}

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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * Phase-3 AAA Genshin-grade 3D VFX renderer for {@link PrimoGeovishapEntity}:
 * <ul>
 *   <li>Dedicated normal-inflated emissive model layer ({@code LAYER_LOCATION_EMISSIVE}) for 100% zero Z-fighting.</li>
 *   <li>Canonical 1:1 scale ({@code 1.35F}) with smooth cross-faded Hermite animations.</li>
 *   <li>3D Golden Geo Crescent Slash Arcs for Left/Right Claw, Combo Slash, and 360° Whirlwind.</li>
 *   <li>3D Elemental-Infused Stone Spires + rotating 6-pointed star ground runes + elemental detonation bursts.</li>
 *   <li>Primordial Shower mouth charge orb, 18-block floor wave, and Shield-Reflect Counter Beam.</li>
 *   <li>Mouth-anchored volumetric Elemental Breath cone with pulsing plasma rings + golden Geo slam shockwaves.</li>
 * </ul>
 */
public class PrimoGeovishapRenderer extends EntityRenderer<PrimoGeovishapEntity> {
    public static final float BOSS_SCALE = 1.35F;

    private static final ResourceLocation TEXTURE = TeyvatBossesConstants.id("textures/entity/primo_geovishap_boss.png");
    private static final ResourceLocation EMISSIVE = TeyvatBossesConstants.id("textures/entity/primo_geovishap_boss_emissive.png");
    private static final ResourceLocation BOSS_MUSIC_ID = TeyvatBossesConstants.id("music.primo_geovishap_theme");

    private final PrimoGeovishapModel<PrimoGeovishapEntity> model;
    private final PrimoGeovishapModel<PrimoGeovishapEntity> emissiveModel;
    private final Map<PrimoGeovishapEntity, PrimoGeovishapAnimations.BlendState> blendStates = new WeakHashMap<>();
    private final Map<PrimoGeovishapEntity, WalkStrideState> walkStates = new WeakHashMap<>();

    private static final class WalkStrideState {
        float walkPhase = 0.0F;
        float walkAmount = 0.0F;
        float lastClientSec = -1.0F;
        double lastX = 0.0D;
        double lastZ = 0.0D;
        float lastBodyYaw = 0.0F;
        boolean hasPos = false;
        float ikFL = 0.0F, ikFR = 0.0F, ikBL = 0.0F, ikBR = 0.0F;
        float ikBodyDrop = 0.0F;
        float ikPitch = 0.0F;
        float ikRoll = 0.0F;
        int lastStaggerStartTick = -1;
        int lastStepQuarter = -1;
        final float[] tailLag = new float[4];
        float frozenClipSec = 0.0F;
        boolean wasHitstop = false;
    }

    public PrimoGeovishapRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
        this.model = new PrimoGeovishapModel<>(ctx.bakeLayer(PrimoGeovishapModel.LAYER_LOCATION));
        this.emissiveModel = new PrimoGeovishapModel<>(ctx.bakeLayer(PrimoGeovishapModel.LAYER_LOCATION_EMISSIVE));
        this.shadowRadius = 3.1F;
    }

    @Override
    public void render(PrimoGeovishapEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buf, int packedLight) {
        int bossState = entity.getBossState();
        BossMusicManager.update(entity, BOSS_MUSIC_ID,
                bossState == PrimoGeovishapEntity.STATE_AWAKEN
                        || bossState == PrimoGeovishapEntity.STATE_COMBAT
                        || bossState == PrimoGeovishapEntity.STATE_STAGGER);
        int animId = entity.getAnimId();
        int animTick = entity.getAnimTick();
        float clipSec = (animTick + partialTicks) / 20.0F;
        float clientSec = (entity.tickCount + partialTicks) / 20.0F;
        String clip = PrimoGeovishapEntity.clipNameForAnim(animId);

        float bodyYaw = Mth.rotLerp(partialTicks, entity.yBodyRotO, entity.yBodyRot);
        float headYaw = Mth.rotLerp(partialTicks, entity.yHeadRotO, entity.yHeadRot);
        float headLookYaw = Mth.wrapDegrees(headYaw - bodyYaw);
        float headLookPitch = Mth.lerp(partialTicks, entity.xRotO, entity.getXRot());

        float evalClipSec = clipSec;
        WalkStrideState ws = walkStates.computeIfAbsent(entity, k -> new WalkStrideState());
        // Trigger impact hitstop on Shield-Reflect Counter Beam stagger
        if (bossState == PrimoGeovishapEntity.STATE_STAGGER && animTick >= 1 && animTick <= 3) {
            int startTick = entity.tickCount - animTick;
            if (ws.lastStaggerStartTick != startTick) {
                ws.lastStaggerStartTick = startTick;
                BossCameraShake.triggerHitstop(entity.position().add(0.0D, 2.5D, 0.0D), 4, 0.68F, 48.0D);
            }
        }
        boolean isLocomotion = (animId == PrimoGeovishapEntity.ANIM_IDLE || animId == PrimoGeovishapEntity.ANIM_WALK);
        if (isLocomotion) {
            double ex = Mth.lerp(partialTicks, entity.xo, entity.getX());
            double ez = Mth.lerp(partialTicks, entity.zo, entity.getZ());
            if (!ws.hasPos || ws.lastClientSec < 0.0F || clientSec < ws.lastClientSec || (clientSec - ws.lastClientSec) > 0.35F) {
                ws.lastX = ex;
                ws.lastZ = ez;
                ws.lastBodyYaw = bodyYaw;
                ws.lastClientSec = clientSec;
                ws.hasPos = true;
            } else {
                float dtSec = Math.max(1.0E-4F, clientSec - ws.lastClientSec);
                double distMoved = Math.hypot(ex - ws.lastX, ez - ws.lastZ);
                float yawDelta = Math.abs(Mth.wrapDegrees(bodyYaw - ws.lastBodyYaw));
                float speedBlocksPerSec = (float) (distMoved / dtSec);
                float turnStepDrive = Mth.clamp((yawDelta / dtSec) / 55.0F, 0.0F, 0.72F);
                float targetWalkAmt = Mth.clamp(Math.max(speedBlocksPerSec / 3.4F, turnStepDrive), 0.0F, 1.0F);
                if (animId == PrimoGeovishapEntity.ANIM_WALK) {
                    targetWalkAmt = Math.max(targetWalkAmt, 0.45F);
                }
                float damp = 1.0F - (float) Math.exp(-dtSec * 8.5D);
                ws.walkAmount = Mth.lerp(damp, ws.walkAmount, targetWalkAmt);
                // Advance continuous gait phase strictly proportional to real ground distance + pivot turning
                float phaseStep = (float) (distMoved * 1.95D) + (turnStepDrive * dtSec * 4.4F);
                ws.walkPhase += phaseStep;
                // Trigger weighty footstep camera thud on each of the 4 staggered paw plants
                int stepQuarter = (int) Math.floor(ws.walkPhase / (Mth.HALF_PI));
                if (stepQuarter != ws.lastStepQuarter && entity.onGround() && ws.walkAmount > 0.25F) {
                    ws.lastStepQuarter = stepQuarter;
                    BossCameraShake.triggerAt(entity.position(), 0.13F * ws.walkAmount, 18.0D);
                }
                ws.lastX = ex;
                ws.lastZ = ez;
                ws.lastBodyYaw = bodyYaw;
                ws.lastClientSec = clientSec;
            }
            // Seamless Alex's Caves locomotion: always use living "idle" base + continuous procedural gait blend
            clip = "idle";
            evalClipSec = clientSec;
        } else {
            ws.hasPos = false;
            ws.walkAmount = Mth.lerp(0.25F, ws.walkAmount, 0.0F);
        }

        if (BossCameraShake.isHitstopActive()) {
            if (!ws.wasHitstop) {
                ws.frozenClipSec = evalClipSec;
                ws.wasHitstop = true;
            }
            evalClipSec = ws.frozenClipSec;
        } else {
            ws.wasHitstop = false;
        }
        PrimoGeovishapAnimations.BlendState blend = blendStates.computeIfAbsent(
                entity, k -> new PrimoGeovishapAnimations.BlendState());
        PrimoGeovishapAnimations.applyBlended(
                model, blend, clip, evalClipSec, clientSec, headLookYaw, headLookPitch,
                ws.walkPhase, isLocomotion ? ws.walkAmount : 0.0F);
        applyQuadrupedFootIK(entity, ws, animId, bodyYaw, partialTicks);
        applyTailYawLag(entity, ws, animId);
        emissiveModel.copyPoseFrom(model);

        int[] rgb = entity.getInfusionRgb();

        poseStack.pushPose();
        if (BossCameraShake.isHitstopActive()) {
            float sh = 0.095F * BossCameraShake.getHitstopIntensity();
            poseStack.translate(Mth.sin((entity.tickCount + partialTicks) * 33.0F) * sh, 0.0D, Mth.cos((entity.tickCount + partialTicks) * 29.0F) * sh);
        }
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - bodyYaw));
        poseStack.scale(-BOSS_SCALE, -BOSS_SCALE, BOSS_SCALE);
        poseStack.translate(0.0D, -1.501D, 0.0D);

        int overlay = entity.hurtTime > 0
                ? OverlayTexture.pack(OverlayTexture.u(0.0F), OverlayTexture.v(true))
                : OverlayTexture.NO_OVERLAY;
        model.renderToBuffer(poseStack, buf.getBuffer(RenderType.entityCutoutNoCull(TEXTURE)), packedLight, overlay, 0xFFFFFFFF);
        boolean sleeping = (bossState == PrimoGeovishapEntity.STATE_DORMANT);
        if (!sleeping) {
            // Full-bright fierce golden-amber eyes when awake (dimmed when sleeping dormant!)
            model.renderGoldenEyes(poseStack, buf.getBuffer(RenderType.entityCutoutNoCull(TEXTURE)), LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);
        }

        boolean chargingUltimate = (animId == PrimoGeovishapEntity.ANIM_PRIMORDIAL_SHOWER
                || animId == PrimoGeovishapEntity.ANIM_ELEMENTAL_BREATH
                || animId == PrimoGeovishapEntity.ANIM_PRIMEVAL_SPRING
                || animId == PrimoGeovishapEntity.ANIM_SCATTERED_ROCKS);
        float glowPulse = sleeping
                ? (0.34F + 0.08F * Mth.sin(clientSec * 1.4F))
                : (chargingUltimate
                    ? (0.90F + 0.10F * Mth.sin(clientSec * 14.0F))
                    : (0.80F + 0.20F * Mth.sin(clientSec * 2.8F)));
        int er = Mth.clamp((int) (rgb[0] * glowPulse), 0, 255);
        int eg = Mth.clamp((int) (rgb[1] * glowPulse), 0, 255);
        int eb = Mth.clamp((int) (rgb[2] * glowPulse), 0, 255);
        int emColor = 0xFF000000 | (er << 16) | (eg << 8) | eb;
        emissiveModel.renderToBuffer(poseStack, buf.getBuffer(RenderType.eyes(EMISSIVE)), LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, emColor);
        poseStack.popPose();

        // =====================================================================
        // AAA 3D GENSHIN-FAITHFUL COMBAT VFX PASS
        // =====================================================================
        VertexConsumer lvc = buf.getBuffer(RenderType.lightning());

        // 1. Golden Geo Crescent Slash Arcs (Left/Right Claw, Combo Slash 1 & 2, Whirlwind 360°)
        renderGoldenSlashVfx(poseStack, lvc, animId, animTick, partialTicks, bodyYaw);

        // 2. Ruin-Guard-style crisp red expanding warning zone during Primordial Shower / Burrow emergence
        float teleRadius = entity.getTelegraphRadius();
        if (teleRadius > 0.5F) {
            poseStack.pushPose();
            Matrix4f mat = poseStack.last().pose();
            float tF = animTick + partialTicks;
            float prog = switch (animId) {
                case PrimoGeovishapEntity.ANIM_PRIMORDIAL_SHOWER -> Mth.clamp(tF / 47.0F, 0.0F, 1.0F);
                case PrimoGeovishapEntity.ANIM_BURROW -> Mth.clamp((tF - 46.0F) / 18.0F, 0.0F, 1.0F);
                default -> Mth.clamp(tF / 24.0F, 0.0F, 1.0F);
            };
            float fillR = Math.max(0.25F, teleRadius * prog);
            drawRing(lvc, mat, teleRadius - 0.36F, teleRadius, 52, 0.11F, 235, 28, 36, 225);
            drawRing(lvc, mat, 0.10F, Math.max(0.12F, teleRadius - 0.36F), 44, 0.09F, 200, 18, 24, 42);
            drawRing(lvc, mat, 0.10F, fillR, 44, 0.10F, 255, 42, 48, 86);
            drawRing(lvc, mat, Math.max(0.10F, fillR - 0.28F), fillR, 52, 0.12F, 255, 85, 78, 210);
            poseStack.popPose();
        }

        // 2b. Crisp red expanding ground warning zones for Rear Tail Slam (behind boss) & Vertical Tail Cleave (forward leap slam)
        if (animId == PrimoGeovishapEntity.ANIM_REAR_TAIL_SLAM && animTick < 12) {
            float prog = Mth.clamp((animTick + partialTicks) / 12.0F, 0.0F, 1.0F);
            float yawRad = bodyYaw * Mth.DEG_TO_RAD;
            // Impact center is 5.2 blocks directly behind the boss
            double bx = Math.sin(yawRad) * 5.2D;
            double bz = -Math.cos(yawRad) * 5.2D;
            float rMax = 5.6F;
            float fillR = Math.max(0.25F, rMax * prog);
            poseStack.pushPose();
            poseStack.translate(bx, 0.0D, bz);
            Matrix4f mat = poseStack.last().pose();
            drawRing(lvc, mat, rMax - 0.34F, rMax, 44, 0.11F, 235, 28, 36, 230);
            drawRing(lvc, mat, 0.10F, Math.max(0.12F, rMax - 0.34F), 40, 0.09F, 200, 18, 24, 48);
            drawRing(lvc, mat, 0.10F, fillR, 40, 0.10F, 255, 42, 48, 92);
            drawRing(lvc, mat, Math.max(0.10F, fillR - 0.26F), fillR, 44, 0.12F, 255, 95, 82, 220);
            poseStack.popPose();
        } else if (animId == PrimoGeovishapEntity.ANIM_VERTICAL_TAIL_CLEAVE && animTick < 27) {
            // World-pinned fixed red warning circle at exact locked impact point (never slides with jumping boss!)
            float prog = Mth.clamp((animTick + partialTicks) / 27.0F, 0.0F, 1.0F);
            double ex = Mth.lerp(partialTicks, entity.xo, entity.getX());
            double ey = Mth.lerp(partialTicks, entity.yo, entity.getY());
            double ez = Mth.lerp(partialTicks, entity.zo, entity.getZ());
            double ox = entity.getCleaveTargetX() - ex;
            double oy = entity.getCleaveTargetY() - ey;
            double oz = entity.getCleaveTargetZ() - ez;
            float rMax = 5.2F;
            float fillR = Math.max(0.25F, rMax * prog);
            poseStack.pushPose();
            poseStack.translate(ox, oy, oz);
            Matrix4f cMat = poseStack.last().pose();
            drawRing(lvc, cMat, rMax - 0.34F, rMax, 48, 0.11F, 235, 28, 36, 230);
            drawRing(lvc, cMat, 0.10F, Math.max(0.12F, rMax - 0.34F), 44, 0.09F, 200, 18, 24, 46);
            drawRing(lvc, cMat, 0.10F, fillR, 44, 0.10F, 255, 42, 48, 92);
            drawRing(lvc, cMat, Math.max(0.10F, fillR - 0.26F), fillR, 48, 0.12F, 255, 95, 82, 220);
            poseStack.popPose();
        }

        // 3. Primordial Shower: Pre-Roar Eye Glint + Mouth Charge Orb + Massive 18-Block Floor Wave + Sky Pillar
        if (animId == PrimoGeovishapEntity.ANIM_PRIMORDIAL_SHOWER) {
            if (animTick >= 7 && animTick <= 16) {
                float gp = Mth.clamp((animTick - 7 + partialTicks) / 9.0F, 0.0F, 1.0F);
                renderPrimoEyeGlint(poseStack, lvc, bodyYaw, gp, rgb);
            }
            renderPrimordialShowerVfx(poseStack, lvc, animTick, partialTicks, clientSec, bodyYaw, rgb);
        }

        // 4. Shield-Reflect Counter Beam (shoots straight back from countering player into boss face!)
        if (bossState == PrimoGeovishapEntity.STATE_STAGGER && animTick >= 1 && animTick <= 16) {
            renderShieldCounterBeam(entity, poseStack, lvc, animTick, partialTicks, rgb);
        }

        // 5. Elemental Breath: Mouth-anchored 3D volumetric sweeping beam + pulsing rings
        if (animId == PrimoGeovishapEntity.ANIM_ELEMENTAL_BREATH && animTick >= 22 && animTick <= 63) {
            renderElementalBreathVfx(poseStack, lvc, animTick, partialTicks, clientSec, bodyYaw, rgb);
        }

        // 6. Golden Geo Ground Shockwaves on heavy slams (Combo Hit 3, Burrow emerge, Primeval Spring)
        renderImpactShockwave(poseStack, lvc, animId, animTick, partialTicks, bodyYaw, rgb);

        // 7. Infused Stones are now real independent 3D PrimoInfusedStoneEntity instances

        super.render(entity, entityYaw, partialTicks, poseStack, buf, packedLight);
    }

    /**
     * 4-Limb Quadruped Terrain Pitch/Roll + 2-Bone Residual Foot IK (Alex's Caves style):
     * Tilts the entire torso smoothly to hug 1–2 block hills and side-slopes, counter-levels the neck
     * slightly so the gaze stays natural, and solves residual 2-bone limb flexion per paw.
     */
    private void applyQuadrupedFootIK(PrimoGeovishapEntity entity, WalkStrideState ws, int animId, float bodyYaw, float partialTicks) {
        boolean allowIK = entity.onGround()
                && animId != PrimoGeovishapEntity.ANIM_BURROW
                && animId != PrimoGeovishapEntity.ANIM_SCATTERED_ROCKS
                && animId != PrimoGeovishapEntity.ANIM_VERTICAL_TAIL_CLEAVE
                && animId != PrimoGeovishapEntity.ANIM_DORMANT
                && animId != PrimoGeovishapEntity.ANIM_DEATH;
        float tFL = 0.0F, tFR = 0.0F, tBL = 0.0F, tBR = 0.0F;
        if (allowIK) {
            double ex = Mth.lerp(partialTicks, entity.xo, entity.getX());
            double ey = Mth.lerp(partialTicks, entity.yo, entity.getY());
            double ez = Mth.lerp(partialTicks, entity.zo, entity.getZ());
            tFL = BossFootIK.sampleFootFootprint(entity.level(), ex, ey, ez, bodyYaw, -1.15D,  1.25D, 0.42D, 1.35F, 1.25F);
            tFR = BossFootIK.sampleFootFootprint(entity.level(), ex, ey, ez, bodyYaw,  1.15D,  1.25D, 0.42D, 1.35F, 1.25F);
            tBL = BossFootIK.sampleFootFootprint(entity.level(), ex, ey, ez, bodyYaw, -1.05D, -1.25D, 0.42D, 1.35F, 1.25F);
            tBR = BossFootIK.sampleFootFootprint(entity.level(), ex, ey, ez, bodyYaw,  1.05D, -1.25D, 0.42D, 1.35F, 1.25F);
        }
        ws.ikFL = Mth.lerp(0.24F, ws.ikFL, tFL);
        ws.ikFR = Mth.lerp(0.24F, ws.ikFR, tFR);
        ws.ikBL = Mth.lerp(0.24F, ws.ikBL, tBL);
        ws.ikBR = Mth.lerp(0.24F, ws.ikBR, tBR);

        float frontAvg = 0.5F * (ws.ikFL + ws.ikFR);
        float backAvg  = 0.5F * (ws.ikBL + ws.ikBR);
        float leftAvg  = 0.5F * (ws.ikFL + ws.ikBL);
        float rightAvg = 0.5F * (ws.ikFR + ws.ikBR);
        float avgFoot  = 0.25F * (ws.ikFL + ws.ikFR + ws.ikBL + ws.ikBR);
        float minFoot  = Math.min(Math.min(ws.ikFL, ws.ikFR), Math.min(ws.ikBL, ws.ikBR));

        // Model space: -Z = forward, +X = right, +Y = down -> negative xRot lifts front UP; negative zRot lifts right UP
        float targetPitch = allowIK ? Mth.clamp((float) Math.atan2(backAvg - frontAvg, 2.50D), -0.44F, 0.44F) : 0.0F;
        float targetRoll  = allowIK ? Mth.clamp((float) Math.atan2(leftAvg - rightAvg, 2.20D), -0.32F, 0.32F) : 0.0F;
        float targetDrop  = allowIK ? Mth.clamp(avgFoot * 0.55F + Math.min(0.0F, minFoot) * 0.30F, -0.75F, 0.55F) : 0.0F;

        ws.ikPitch = Mth.lerp(0.20F, ws.ikPitch, targetPitch);
        ws.ikRoll  = Mth.lerp(0.20F, ws.ikRoll, targetRoll);
        ws.ikBodyDrop = Mth.lerp(0.24F, ws.ikBodyDrop, targetDrop);

        if (Math.abs(ws.ikFL) < 0.01F && Math.abs(ws.ikFR) < 0.01F
                && Math.abs(ws.ikBL) < 0.01F && Math.abs(ws.ikBR) < 0.01F
                && Math.abs(ws.ikPitch) < 0.005F && Math.abs(ws.ikRoll) < 0.005F) {
            return;
        }

        net.minecraft.client.model.geom.ModelPart body = model.getBone("body");
        if (body != null) {
            body.y += (-ws.ikBodyDrop) * (16.0F / BOSS_SCALE);
            body.xRot += ws.ikPitch;
            body.zRot += ws.ikRoll;
        }
        // Subtle neck counter-leveling so head maintains natural gaze on hills
        net.minecraft.client.model.geom.ModelPart neck = model.getBone("neck");
        if (neck != null) {
            neck.xRot -= ws.ikPitch * 0.35F;
        }

        float sinP = Mth.sin(ws.ikPitch);
        float sinR = Mth.sin(ws.ikRoll);
        float planeFL = ws.ikBodyDrop + (-1.25F * sinP) + ( 1.15F * sinR);
        float planeFR = ws.ikBodyDrop + (-1.25F * sinP) + (-1.15F * sinR);
        float planeBL = ws.ikBodyDrop + ( 1.25F * sinP) + ( 1.05F * sinR);
        float planeBR = ws.ikBodyDrop + ( 1.25F * sinP) + (-1.05F * sinR);

        float[] solve = new float[2];
        applyLimbIK("arm_left", "forearm_left", Math.max(0.0F, ws.ikFL - planeFL), 0.92F, 0.85F, solve);
        applyLimbIK("arm_right", "forearm_right", Math.max(0.0F, ws.ikFR - planeFR), 0.92F, 0.85F, solve);
        applyLimbIK("leg_left", "shin_left", Math.max(0.0F, ws.ikBL - planeBL), 0.84F, 0.78F, solve);
        applyLimbIK("leg_right", "shin_right", Math.max(0.0F, ws.ikBR - planeBR), 0.84F, 0.78F, solve);
    }

    /**
     * Alex's Caves Sauropod-style cascading multi-segment inertial yaw chain:
     * each tail segment follows the previous segment with progressive damping so turning sends a
     * fluid traveling S-wave down tail_1 -> tail_2 -> tail_3 -> tail_4.
     */
    private void applyTailYawLag(PrimoGeovishapEntity entity, WalkStrideState ws, int animId) {
        boolean active = animId != PrimoGeovishapEntity.ANIM_DORMANT && animId != PrimoGeovishapEntity.ANIM_DEATH;
        float yawDeltaDeg = active ? Mth.wrapDegrees(entity.yBodyRot - entity.yBodyRotO) : 0.0F;
        float baseTarget = Mth.clamp(-yawDeltaDeg * 0.036F, -0.38F, 0.38F);
        ws.tailLag[0] = Mth.lerp(0.24F, ws.tailLag[0], baseTarget);
        ws.tailLag[1] = Mth.lerp(0.19F, ws.tailLag[1], ws.tailLag[0] * 1.15F);
        ws.tailLag[2] = Mth.lerp(0.15F, ws.tailLag[2], ws.tailLag[1] * 1.12F);
        ws.tailLag[3] = Mth.lerp(0.12F, ws.tailLag[3], ws.tailLag[2] * 1.10F);

        String[] tails = {"tail_1", "tail_2", "tail_3", "tail_4"};
        float[] weights = {0.42F, 0.56F, 0.68F, 0.80F};
        for (int i = 0; i < tails.length; i++) {
            if (Math.abs(ws.tailLag[i]) < 0.002F) continue;
            net.minecraft.client.model.geom.ModelPart t = model.getBone(tails[i]);
            if (t != null) t.yRot += ws.tailLag[i] * weights[i];
        }
        // Subtle counter-leading neck anticipation when turning
        if (active && Math.abs(ws.tailLag[0]) > 0.003F) {
            net.minecraft.client.model.geom.ModelPart neck = model.getBone("neck");
            if (neck != null) neck.yRot -= ws.tailLag[0] * 0.28F;
        }
    }

    private static void renderPrimoEyeGlint(PoseStack poseStack, VertexConsumer lvc, float bodyYaw, float progress, int[] rgb) {
        float env = Mth.sin(progress * Mth.PI);
        if (env <= 0.02F) return;
        float rad = bodyYaw * Mth.DEG_TO_RAD;
        double fx = -Mth.sin(rad) * 2.45D;
        double fz = Mth.cos(rad) * 2.45D;
        poseStack.pushPose();
        poseStack.translate(fx, 2.75D, fz);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - bodyYaw));
        Matrix4f m = poseStack.last().pose();
        float armH = 0.45F + 1.85F * env;
        float armV = 0.30F + 1.05F * env;
        float thick = 0.12F * env;
        int aCore = (int) (245 * env);
        int aTip = (int) (45 * env);
        lvc.addVertex(m, -armH, 0.0F, 0.0F).setColor(rgb[0], rgb[1], rgb[2], aTip);
        lvc.addVertex(m, 0.0F, thick, 0.0F).setColor(255, 250, 225, aCore);
        lvc.addVertex(m, armH, 0.0F, 0.0F).setColor(rgb[0], rgb[1], rgb[2], aTip);
        lvc.addVertex(m, 0.0F, -thick, 0.0F).setColor(255, 250, 225, aCore);
        lvc.addVertex(m, 0.0F, -thick, 0.0F).setColor(255, 250, 225, aCore);
        lvc.addVertex(m, armH, 0.0F, 0.0F).setColor(rgb[0], rgb[1], rgb[2], aTip);
        lvc.addVertex(m, 0.0F, thick, 0.0F).setColor(255, 250, 225, aCore);
        lvc.addVertex(m, -armH, 0.0F, 0.0F).setColor(rgb[0], rgb[1], rgb[2], aTip);
        lvc.addVertex(m, 0.0F, -armV, 0.0F).setColor(rgb[0], rgb[1], rgb[2], aTip);
        lvc.addVertex(m, thick, 0.0F, 0.0F).setColor(255, 250, 225, aCore);
        lvc.addVertex(m, 0.0F, armV, 0.0F).setColor(rgb[0], rgb[1], rgb[2], aTip);
        lvc.addVertex(m, -thick, 0.0F, 0.0F).setColor(255, 250, 225, aCore);
        lvc.addVertex(m, -thick, 0.0F, 0.0F).setColor(255, 250, 225, aCore);
        lvc.addVertex(m, 0.0F, armV, 0.0F).setColor(rgb[0], rgb[1], rgb[2], aTip);
        lvc.addVertex(m, thick, 0.0F, 0.0F).setColor(255, 250, 225, aCore);
        lvc.addVertex(m, 0.0F, -armV, 0.0F).setColor(rgb[0], rgb[1], rgb[2], aTip);
        poseStack.popPose();
    }

    private void applyLimbIK(String upperName, String lowerName, float liftBlocks, float upperLen, float lowerLen, float[] solve) {
        if (liftBlocks <= 0.015F) return;
        BossFootIK.solveTwoBoneLift(liftBlocks, upperLen, lowerLen, solve);
        net.minecraft.client.model.geom.ModelPart upper = model.getBone(upperName);
        net.minecraft.client.model.geom.ModelPart lower = model.getBone(lowerName);
        if (upper != null) upper.xRot -= solve[0];
        if (lower != null) lower.xRot += solve[1];
    }

    private static void renderGoldenSlashVfx(PoseStack poseStack, VertexConsumer lvc, int animId, int animTick, float partialTicks, float bodyYaw) {
        if (animId == PrimoGeovishapEntity.ANIM_RIGHT_CLAW && animTick >= 10 && animTick <= 17) {
            float raw = Mth.clamp((animTick - 10 + partialTicks) / 7.0F, 0.0F, 1.0F);
            float p = 1.0F - (1.0F - raw) * (1.0F - raw);
            drawSlashArc(poseStack, lvc, bodyYaw, 1.95F, 6.6F, -92.0F, 72.0F, p, 24.0F);
        } else if (animId == PrimoGeovishapEntity.ANIM_LEFT_CLAW) {
            if (animTick >= 10 && animTick <= 17) {
                float raw = Mth.clamp((animTick - 10 + partialTicks) / 7.0F, 0.0F, 1.0F);
                float p = 1.0F - (1.0F - raw) * (1.0F - raw);
                drawSlashArc(poseStack, lvc, bodyYaw, 1.95F, 6.6F, 92.0F, -72.0F, p, -24.0F);
            } else if (animTick >= 24 && animTick <= 31) {
                // Hit 2: upward-diagonal rip-up slash synced to 1.42s (tick 28) ground rupture!
                float raw = Mth.clamp((animTick - 24 + partialTicks) / 7.0F, 0.0F, 1.0F);
                float p = 1.0F - (1.0F - raw) * (1.0F - raw);
                drawSlashArc(poseStack, lvc, bodyYaw, 1.55F, 6.2F, -64.0F, 82.0F, p, -42.0F);
            }
        } else if (animId == PrimoGeovishapEntity.ANIM_COMBO_SLASH) {
            if (animTick >= 10 && animTick <= 17) {
                float raw = Mth.clamp((animTick - 10 + partialTicks) / 7.0F, 0.0F, 1.0F);
                float p = 1.0F - (1.0F - raw) * (1.0F - raw);
                drawSlashArc(poseStack, lvc, bodyYaw, 1.95F, 6.9F, 96.0F, -76.0F, p, -22.0F);
            } else if (animTick >= 24 && animTick <= 31) {
                float raw = Mth.clamp((animTick - 24 + partialTicks) / 7.0F, 0.0F, 1.0F);
                float p = 1.0F - (1.0F - raw) * (1.0F - raw);
                drawSlashArc(poseStack, lvc, bodyYaw, 2.05F, 7.1F, -96.0F, 76.0F, p, 22.0F);
            }
        } else if (animId == PrimoGeovishapEntity.ANIM_WHIRLWIND && animTick >= 11 && animTick <= 24) {
            // Synced 1:1 with body.yRot 360-deg spin (0.52s..1.16s = tick 11..24)
            float p = Mth.clamp((animTick - 11 + partialTicks) / 13.0F, 0.0F, 1.0F);
            float fade = (p < 0.15F) ? (p / 0.15F) : (1.0F - (p - 0.15F) / 0.85F * 0.78F);
            poseStack.pushPose();
            poseStack.translate(0.0D, 1.35D, 0.0D);
            poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - bodyYaw + p * 410.0F));
            poseStack.mulPose(Axis.XP.rotationDegrees(10.0F));
            Matrix4f mat = poseStack.last().pose();
            drawArcRibbon(lvc, mat, 3.4F, 7.8F, 0.0F, 265.0F, 34, 255, 175, 45, (int) (195 * fade));
            drawArcRibbon(lvc, mat, 6.2F, 8.0F, 12.0F, 265.0F, 34, 255, 245, 195, (int) (235 * fade));
            drawVerticalArcBevel(lvc, mat, 7.4F, 0.55F, 12.0F, 265.0F, 28, 255, 220, 120, (int) (170 * fade));
            poseStack.popPose();
        }
    }

    /**
     * 3D Tilted, Double-Sided, Multi-Layer Golden Geo Crescent Slash (matches Genshin's bold golden-amber arcs
     * and stays clearly visible at player eye level instead of collapsing to a flat 1px horizontal line).
     */
    private static void drawSlashArc(PoseStack poseStack, VertexConsumer lvc, float bodyYaw, float yHeight, float radius,
                                     float startDeg, float endDeg, float progress, float tiltDeg) {
        float fade = (progress < 0.22F) ? (progress / 0.22F) : (1.0F - (progress - 0.22F) / 0.78F);
        int alpha = Mth.clamp((int) (230 * fade), 0, 255);
        if (alpha <= 10) return;
        float curEnd = Mth.lerp(progress, startDeg, endDeg);
        float curStart = Mth.lerp(Math.max(0.0F, progress - 0.52F), startDeg, endDeg);
        poseStack.pushPose();
        poseStack.translate(0.0D, yHeight, 0.0D);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - bodyYaw));
        // Pitch forward 20 deg + diagonal roll so the crescent face is always boldly visible from player eye-level!
        poseStack.mulPose(Axis.XP.rotationDegrees(20.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(tiltDeg));
        Matrix4f mat = poseStack.last().pose();
        // Layer 1: Wide deep-amber Geo aura trail
        drawArcRibbon(lvc, mat, radius * 0.42F, radius * 1.02F, curStart, curEnd, 24, 255, 155, 25, (int) (alpha * 0.85F));
        // Layer 2: Mid vivid golden Geo crescent body
        drawArcRibbon(lvc, mat, radius * 0.64F, radius * 1.05F, curStart, curEnd, 24, 255, 210, 70, alpha);
        // Layer 3: Blazing cream-white leading razor edge
        drawArcRibbon(lvc, mat, radius * 0.88F, radius * 1.08F, curStart, curEnd, 24, 255, 252, 220, Math.min(255, alpha + 25));
        // Layer 4: Vertical 3D bevel wall along outer edge for true volumetric slash thickness
        drawVerticalArcBevel(lvc, mat, radius * 0.98F, 0.52F, curStart, curEnd, 20, 255, 225, 115, (int) (alpha * 0.75F));
        poseStack.popPose();
    }

    private static void drawArcRibbon(VertexConsumer vc, Matrix4f mat, float rIn, float rOut, float deg0, float deg1,
                                      int segs, int r, int g, int b, int a) {
        for (int i = 0; i < segs; i++) {
            float f0 = i / (float) segs;
            float f1 = (i + 1) / (float) segs;
            float a0 = Mth.lerp(f0, deg0, deg1) * Mth.DEG_TO_RAD;
            float a1 = Mth.lerp(f1, deg0, deg1) * Mth.DEG_TO_RAD;
            int al0 = (int) (a * Mth.sin(f0 * Mth.PI));
            int al1 = (int) (a * Mth.sin(f1 * Mth.PI));
            float x0 = Mth.sin(a0), z0 = -Mth.cos(a0);
            float x1 = Mth.sin(a1), z1 = -Mth.cos(a1);
            // Front face
            vc.addVertex(mat, x0 * rIn, 0.0F, z0 * rIn).setColor(r, g, b, 0);
            vc.addVertex(mat, x0 * rOut, 0.0F, z0 * rOut).setColor(r, g, b, al0);
            vc.addVertex(mat, x1 * rOut, 0.0F, z1 * rOut).setColor(r, g, b, al1);
            vc.addVertex(mat, x1 * rIn, 0.0F, z1 * rIn).setColor(r, g, b, 0);
            // Back face (double-sided so slash never vanishes from below/above!)
            vc.addVertex(mat, x1 * rIn, 0.0F, z1 * rIn).setColor(r, g, b, 0);
            vc.addVertex(mat, x1 * rOut, 0.0F, z1 * rOut).setColor(r, g, b, al1);
            vc.addVertex(mat, x0 * rOut, 0.0F, z0 * rOut).setColor(r, g, b, al0);
            vc.addVertex(mat, x0 * rIn, 0.0F, z0 * rIn).setColor(r, g, b, 0);
        }
    }

    private static void drawVerticalArcBevel(VertexConsumer vc, Matrix4f mat, float radius, float halfHeight,
                                             float deg0, float deg1, int segs, int r, int g, int b, int a) {
        if (a <= 5) return;
        for (int i = 0; i < segs; i++) {
            float f0 = i / (float) segs;
            float f1 = (i + 1) / (float) segs;
            float a0 = Mth.lerp(f0, deg0, deg1) * Mth.DEG_TO_RAD;
            float a1 = Mth.lerp(f1, deg0, deg1) * Mth.DEG_TO_RAD;
            int al0 = (int) (a * Mth.sin(f0 * Mth.PI));
            int al1 = (int) (a * Mth.sin(f1 * Mth.PI));
            float x0 = Mth.sin(a0) * radius, z0 = -Mth.cos(a0) * radius;
            float x1 = Mth.sin(a1) * radius, z1 = -Mth.cos(a1) * radius;
            // Outer face
            vc.addVertex(mat, x0, -halfHeight, z0).setColor(r, g, b, 0);
            vc.addVertex(mat, x0, 0.0F, z0).setColor(r, g, b, al0);
            vc.addVertex(mat, x1, 0.0F, z1).setColor(r, g, b, al1);
            vc.addVertex(mat, x1, -halfHeight, z1).setColor(r, g, b, 0);
            vc.addVertex(mat, x0, 0.0F, z0).setColor(r, g, b, al0);
            vc.addVertex(mat, x0, halfHeight, z0).setColor(r, g, b, 0);
            vc.addVertex(mat, x1, halfHeight, z1).setColor(r, g, b, 0);
            vc.addVertex(mat, x1, 0.0F, z1).setColor(r, g, b, al1);
            // Inner face (double-sided)
            vc.addVertex(mat, x1, -halfHeight, z1).setColor(r, g, b, 0);
            vc.addVertex(mat, x1, 0.0F, z1).setColor(r, g, b, al1);
            vc.addVertex(mat, x0, 0.0F, z0).setColor(r, g, b, al0);
            vc.addVertex(mat, x0, -halfHeight, z0).setColor(r, g, b, 0);
            vc.addVertex(mat, x1, 0.0F, z1).setColor(r, g, b, al1);
            vc.addVertex(mat, x1, halfHeight, z1).setColor(r, g, b, 0);
            vc.addVertex(mat, x0, halfHeight, z0).setColor(r, g, b, 0);
            vc.addVertex(mat, x0, 0.0F, z0).setColor(r, g, b, al0);
        }
    }

    /**
     * Genshin-faithful Primordial Shower VFX (1.mp4 00:35–00:38 &amp; 2.mp4 00:20–00:23):
     * <ul>
     *   <li>Charge (tick 6..46): 3 contracting elemental suction rings pulling energy INWARD along the floor
     *       + soft glowing ground aura (ZERO giant blocky octahedron or sky beacon pillar!).</li>
     *   <li>Release (tick 47..68): Massive multi-layer flat elemental tsunami floor wave + 12 radial ground
     *       surge blades tearing outward across the 18m arena floor!</li>
     * </ul>
     */
    private static void renderPrimordialShowerVfx(PoseStack poseStack, VertexConsumer lvc, int animTick,
                                                  float partialTicks, float clientSec, float bodyYaw, int[] rgb) {
        int lr = Math.min(255, rgb[0] + 65);
        int lg = Math.min(255, rgb[1] + 65);
        int lb = Math.min(255, rgb[2] + 65);
        poseStack.pushPose();
        Matrix4f mat = poseStack.last().pose();
        if (animTick >= 47 && animTick <= 68) {
            float p = Mth.clamp((animTick - 47 + partialTicks) / 21.0F, 0.0F, 1.0F);
            float ease = 1.0F - (1.0F - p) * (1.0F - p);
            float waveR = 2.2F + ease * 16.5F;
            int alpha = (int) (235 * (1.0F - p));
            // Massive 3-layer flat elemental tsunami floor wave (matches 1.mp4 00:37 & 2.mp4 00:22!)
            drawRing(lvc, mat, Math.max(0.4F, waveR - 5.2F), waveR, 64, 0.14F, rgb[0], rgb[1], rgb[2], (int) (alpha * 0.78F));
            drawRing(lvc, mat, Math.max(0.4F, waveR - 2.4F), waveR + 0.2F, 64, 0.22F, lr, lg, lb, alpha);
            drawRing(lvc, mat, Math.max(0.4F, waveR - 0.85F), waveR + 0.35F, 64, 0.30F, 255, 255, 255, Math.min(255, alpha + 20));
            // Low wide ground-hugging elemental shockwave crest wall (height 1.8m, NOT a 16m sky beacon!)
            drawEnergyColumn(lvc, mat, waveR * 0.94F, 0.10F, 1.85F * (1.0F - p * 0.5F), 32, lr, lg, lb, (int) (120 * (1.0F - p)));
        }
        poseStack.popPose();
    }

    private static void renderShieldCounterBeam(PrimoGeovishapEntity entity, PoseStack poseStack, VertexConsumer lvc,
                                                int animTick, float partialTicks, int[] rgb) {
        CompoundTag cTag = entity.getCounterBeamTag();
        if (!cTag.contains("dx")) return;
        float dx = cTag.getFloat("dx");
        float dy = cTag.getFloat("dy");
        float dz = cTag.getFloat("dz");
        float p = Mth.clamp((animTick - 1 + partialTicks) / 15.0F, 0.0F, 1.0F);
        int alpha = (int) (235 * (1.0F - p));
        if (alpha <= 10) return;

        poseStack.pushPose();
        Matrix4f mat = poseStack.last().pose();
        // Thick glowing counter-reflection beam from player's shield (dx,dy,dz) straight into boss face (0, 2.2, 0)
        float w = 0.55F * (1.0F - p * 0.5F);
        drawRibbonSegment(lvc, mat, dx, dy + 1.0F, dz, 0.0F, 2.2F, 0.0F, w, rgb[0], rgb[1], rgb[2], alpha);
        drawRibbonSegment(lvc, mat, dx, dy + 1.0F, dz, 0.0F, 2.2F, 0.0F, w * 0.45F, 255, 255, 255, Math.min(255, alpha + 20));
        drawGlowingOctahedron(lvc, mat, 0.0F, 2.2F, 0.0F, 1.4F + p * 2.2F, 255, 240, 180, (int) (190 * (1.0F - p)));
        poseStack.popPose();
    }

    /**
     * Genshin-faithful Elemental Breath torrent (1.mp4 00:40-00:46 &amp; 2.mp4 00:00-00:02):
     * <ul>
     *   <li>8-segment undulating volumetric elemental stream (3 layered depth shells with traveling fluid ripples).</li>
     *   <li>3 twisting helical vortex energy ribbons spiraling rapidly from open jaws to ground-impact mist.</li>
     *   <li>Soft radial mouth-throat swirl + billowing ground-contact splash flare (zero blocky octahedron!).</li>
     * </ul>
     */
    private static void renderElementalBreathVfx(PoseStack poseStack, VertexConsumer lvc, int animTick,
                                                 float partialTicks, float clientSec, float bodyYaw, int[] rgb) {
        float progress = Mth.clamp((animTick - 22 + partialTicks) / 40.0F, 0.0F, 1.0F);
        // 1:1 synchronized with build_attack_elemental_breath (-46 -> +46) & server tickElementalBreath!
        float sweepYaw = bodyYaw + Mth.lerp(progress, 46.0F, -46.0F);
        float fade = (progress < 0.08F) ? (progress / 0.08F) : (progress > 0.90F ? (1.0F - progress) / 0.10F : 1.0F);
        poseStack.pushPose();
        // Offset beam origin slightly along swung neck/snout axis so torrent emerges dead-center from open jaws
        float sweepRelDeg = Mth.lerp(progress, 46.0F, -46.0F);
        // Anchor directly inside the open snout tip (follows neck+head sweep yaw with zero floating gap!)
        float pivotYawRad = (float) Math.toRadians(bodyYaw + sweepRelDeg * 0.68F);
        double mouthAnchorX = -Math.sin(pivotYawRad) * 2.05D;
        double mouthAnchorZ = Math.cos(pivotYawRad) * 2.05D;
        poseStack.translate(mouthAnchorX, 1.82D, mouthAnchorZ);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - sweepYaw));
        poseStack.mulPose(Axis.XP.rotationDegrees(-11.0F));
        Matrix4f mat = poseStack.last().pose();

        int lr = Math.min(255, rgb[0] + 65);
        int lg = Math.min(255, rgb[1] + 65);
        int lb = Math.min(255, rgb[2] + 65);

        // 1. Silky 4-layer undulating volumetric torrent starting directly at the open jaws (zStart ~ 0.05..0.15)
        drawUndulatingBreathStream(lvc, mat, 0.05F, 15.2F, 0.42F, 2.95F, 10, 20, clientSec, 0.15F, rgb[0], rgb[1], rgb[2], (int) (84 * fade));
        drawUndulatingBreathStream(lvc, mat, 0.08F, 14.6F, 0.28F, 1.90F, 10, 18, clientSec * 1.15F, 0.12F, lr, lg, lb, (int) (148 * fade));
        drawUndulatingBreathStream(lvc, mat, 0.12F, 13.8F, 0.18F, 1.02F, 10, 16, clientSec * 1.32F, 0.09F, 230, 248, 255, (int) (185 * fade));
        drawUndulatingBreathStream(lvc, mat, 0.16F, 12.6F, 0.10F, 0.46F, 8, 12, clientSec * 1.50F, 0.07F, 255, 255, 255, (int) (225 * fade));

        // 2. Dual counter-flowing twisting helical vortex ribbons emerging directly from the mouth
        drawHelicalBreathRibbons(lvc, mat, 0.10F, 14.5F, 0.34F, 2.35F, 22, clientSec, rgb[0], rgb[1], rgb[2], (int) (155 * fade));
        drawHelicalBreathRibbons(lvc, mat, 0.15F, 13.8F, 0.20F, 1.48F, 20, -clientSec * 0.85F, lr, lg, lb, (int) (135 * fade));
        poseStack.popPose();
    }

    private static void drawUndulatingBreathStream(VertexConsumer vc, Matrix4f mat, float zStart, float zEnd,
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
            // Smooth alpha ramp-in at mouth & soft mist fade-out at far tip
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

    private static void drawHelicalBreathRibbons(VertexConsumer vc, Matrix4f mat, float zStart, float zEnd,
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
                float w0 = 0.24F + u0 * 0.45F;
                float w1 = 0.24F + u1 * 0.45F;
                int a0 = Mth.clamp((int) (maxAlpha * Mth.sin(u0 * Mth.PI)), 0, 255);
                int a1 = Mth.clamp((int) (maxAlpha * Mth.sin(u1 * Mth.PI)), 0, 255);

                float x0a = Mth.cos(ang0) * rad0, y0a = Mth.sin(ang0) * rad0;
                float x0b = Mth.cos(ang0 + w0) * rad0, y0b = Mth.sin(ang0 + w0) * rad0;
                float x1a = Mth.cos(ang1) * rad1, y1a = Mth.sin(ang1) * rad1;
                float x1b = Mth.cos(ang1 + w1) * rad1, y1b = Mth.sin(ang1 + w1) * rad1;

                // Double-sided helical ribbon
                vc.addVertex(mat, x0a, y0a, z0).setColor(r, g, b, a0);
                vc.addVertex(mat, x0b, y0b, z0).setColor(255, 255, 255, a0);
                vc.addVertex(mat, x1b, y1b, z1).setColor(255, 255, 255, a1);
                vc.addVertex(mat, x1a, y1a, z1).setColor(r, g, b, a1);
                vc.addVertex(mat, x1a, y1a, z1).setColor(r, g, b, a1);
                vc.addVertex(mat, x1b, y1b, z1).setColor(255, 255, 255, a1);
                vc.addVertex(mat, x0b, y0b, z0).setColor(255, 255, 255, a0);
                vc.addVertex(mat, x0a, y0a, z0).setColor(r, g, b, a0);
            }
        }
    }

    private static void renderImpactShockwave(PoseStack poseStack, VertexConsumer lvc, int animId, int animTick, float partialTicks, float bodyYaw, int[] rgb) {
        int start = -1;
        float maxR = 9.0F;
        boolean goldenGeo = true;
        double fwdOffset = 0.0D;
        if (animId == PrimoGeovishapEntity.ANIM_COMBO_SLASH && animTick >= 47 && animTick <= 62) {
            start = 47; maxR = 9.8F;
        } else if (animId == PrimoGeovishapEntity.ANIM_BURROW && animTick >= 64 && animTick <= 79) {
            start = 64; maxR = 10.2F;
        } else if (animId == PrimoGeovishapEntity.ANIM_PRIMEVAL_SPRING && animTick >= 24 && animTick <= 39) {
            start = 24; maxR = 10.5F; goldenGeo = false;
        } else if (animId == PrimoGeovishapEntity.ANIM_REAR_TAIL_SLAM && animTick >= 12 && animTick <= 26) {
            start = 18; maxR = 6.2F; fwdOffset = -5.2D;
        } else if (animId == PrimoGeovishapEntity.ANIM_VERTICAL_TAIL_CLEAVE && animTick >= 27 && animTick <= 42) {
            start = 25; maxR = 7.4F; fwdOffset = 5.6D;
        }
        if (start < 0) return;
        float p = Mth.clamp((animTick - start + partialTicks) / 15.0F, 0.0F, 1.0F);
        float ease = 1.0F - (1.0F - p) * (1.0F - p);
        float r = 1.4F + ease * (maxR - 1.4F);
        int a = (int) (225 * (1.0F - p));
        if (a <= 10) return;
        int cr = goldenGeo ? 255 : rgb[0];
        int cg = goldenGeo ? 205 : rgb[1];
        int cb = goldenGeo ? 75  : rgb[2];
        poseStack.pushPose();
        if (Math.abs(fwdOffset) > 0.01D) {
            double rad = Math.toRadians(bodyYaw);
            poseStack.translate(-Math.sin(rad) * fwdOffset, 0.0D, Math.cos(rad) * fwdOffset);
        }
        Matrix4f mat = poseStack.last().pose();
        drawRing(lvc, mat, Math.max(0.4F, r - 1.5F), r, 48, 0.14F, cr, cg, cb, a);
        drawRing(lvc, mat, Math.max(0.4F, r - 0.45F), r, 48, 0.20F, 255, 250, 220, Math.min(255, a + 20));
        poseStack.popPose();
    }

    /**
     * Renders every synced Elemental-Infused Stone as a real 3D crystalline monolith obelisk
     * with a rotating 6-pointed star ground rune (1.mp4 01:11, 2.mp4 00:28-00:30, 01:11-01:14)!
     */
    private static void renderSyncedInfusedStones(PrimoGeovishapEntity entity, PoseStack poseStack, VertexConsumer lvc,
                                                  float partialTicks, float clientSec, int[] rgb) {
        CompoundTag tag = entity.getStonesTag();
        if (!tag.contains("stones", Tag.TAG_LIST)) return;
        ListTag list = tag.getList("stones", Tag.TAG_COMPOUND);
        if (list.isEmpty()) return;

        double ex = Mth.lerp(partialTicks, entity.xo, entity.getX());
        double ey = Mth.lerp(partialTicks, entity.yo, entity.getY());
        double ez = Mth.lerp(partialTicks, entity.zo, entity.getZ());

        for (int i = 0; i < list.size(); i++) {
            CompoundTag st = list.getCompound(i);
            float sx = (float) (st.getDouble("x") - ex);
            float sy = (float) (st.getDouble("y") - ey);
            float sz = (float) (st.getDouble("z") - ez);
            int fuse = st.getInt("f");
            float urgency = Mth.clamp(1.0F - (fuse / 48.0F), 0.0F, 1.0F);
            float pulse = 0.85F + 0.15F * Mth.sin(clientSec * (8.0F + urgency * 24.0F));

            poseStack.pushPose();
            poseStack.translate(sx, sy, sz);
            Matrix4f mat = poseStack.last().pose();

            // 1. Rotating 6-pointed star rune circle flat on the ground
            drawStarRune(lvc, mat, 1.45F + urgency * 0.35F, clientSec * (1.2F + urgency * 2.5F),
                    rgb[0], rgb[1], rgb[2], (int) (185 * pulse));
            drawRing(lvc, mat, 1.35F, 1.50F, 24, 0.06F, 255, 255, 255, (int) (145 * pulse));

            // 2. 3D Crystalline Monolith Obelisk spire (dark outer sheath + glowing inner crystal core)
            float h = 1.95F + urgency * 0.30F;
            drawCrystalSpire(lvc, mat, 0.42F, h, 45, 50, 62, 235);
            drawCrystalSpire(lvc, mat, 0.28F, h * 1.06F, rgb[0], rgb[1], rgb[2], (int) (220 * pulse));
            drawCrystalSpire(lvc, mat, 0.14F, h * 0.92F, 255, 255, 255, (int) (240 * pulse));

            poseStack.popPose();
        }
    }

    private static void drawCrystalSpire(VertexConsumer vc, Matrix4f mat, float r, float h, int cr, int cg, int cb, int a) {
        // 4-sided tapered crystal obelisk
        float[][] pts = {{r, 0}, {0, r}, {-r, 0}, {0, -r}};
        for (int i = 0; i < 4; i++) {
            int j = (i + 1) % 4;
            vc.addVertex(mat, pts[i][0], 0.02F, pts[i][1]).setColor(cr, cg, cb, a);
            vc.addVertex(mat, pts[j][0], 0.02F, pts[j][1]).setColor(cr, cg, cb, a);
            vc.addVertex(mat, pts[j][0] * 0.22F, h, pts[j][1] * 0.22F).setColor(cr, cg, cb, a);
            vc.addVertex(mat, pts[i][0] * 0.22F, h, pts[i][1] * 0.22F).setColor(cr, cg, cb, a);
        }
    }

    private static void drawStarRune(VertexConsumer vc, Matrix4f mat, float radius, float rotRad, int r, int g, int b, int a) {
        // Two overlapping equilateral triangles forming a 6-pointed elemental hexagram rune on the floor
        for (int tri = 0; tri < 2; tri++) {
            float baseAng = rotRad + tri * (Mth.PI / 3.0F);
            for (int edge = 0; edge < 3; edge++) {
                float a0 = baseAng + edge * (Mth.TWO_PI / 3.0F);
                float a1 = baseAng + (edge + 1) * (Mth.TWO_PI / 3.0F);
                float x0 = Mth.cos(a0) * radius, z0 = Mth.sin(a0) * radius;
                float x1 = Mth.cos(a1) * radius, z1 = Mth.sin(a1) * radius;
                float dx = x1 - x0, dz = z1 - z0;
                float len = Mth.sqrt(dx * dx + dz * dz);
                if (len < 1.0E-4F) continue;
                float nx = -dz / len * 0.075F, nz = dx / len * 0.075F;
                vc.addVertex(mat, x0 - nx, 0.07F, z0 - nz).setColor(r, g, b, a);
                vc.addVertex(mat, x0 + nx, 0.07F, z0 + nz).setColor(r, g, b, a);
                vc.addVertex(mat, x1 + nx, 0.07F, z1 + nz).setColor(r, g, b, a);
                vc.addVertex(mat, x1 - nx, 0.07F, z1 - nz).setColor(r, g, b, a);
            }
        }
    }

    private static void drawGlowingOctahedron(VertexConsumer vc, Matrix4f mat, float cx, float cy, float cz, float r,
                                              int cr, int cg, int cb, int a) {
        if (a <= 5) return;
        float[][] eq = {{r, 0}, {0, r}, {-r, 0}, {0, -r}};
        for (int i = 0; i < 4; i++) {
            int j = (i + 1) % 4;
            // Upper pyramid
            vc.addVertex(mat, cx + eq[i][0], cy, cz + eq[i][1]).setColor(cr, cg, cb, a);
            vc.addVertex(mat, cx + eq[j][0], cy, cz + eq[j][1]).setColor(cr, cg, cb, a);
            vc.addVertex(mat, cx, cy + r, cz).setColor(cr, cg, cb, a);
            vc.addVertex(mat, cx, cy + r, cz).setColor(cr, cg, cb, a);
            // Lower pyramid
            vc.addVertex(mat, cx + eq[j][0], cy, cz + eq[j][1]).setColor(cr, cg, cb, a);
            vc.addVertex(mat, cx + eq[i][0], cy, cz + eq[i][1]).setColor(cr, cg, cb, a);
            vc.addVertex(mat, cx, cy - r, cz).setColor(cr, cg, cb, a);
            vc.addVertex(mat, cx, cy - r, cz).setColor(cr, cg, cb, a);
        }
    }

    private static void drawRibbonSegment(VertexConsumer vc, Matrix4f mat, float x0, float y0, float z0,
                                          float x1, float y1, float z1, float halfW, int r, int g, int b, int a) {
        vc.addVertex(mat, x0, y0 - halfW, z0).setColor(r, g, b, a);
        vc.addVertex(mat, x0, y0 + halfW, z0).setColor(r, g, b, a);
        vc.addVertex(mat, x1, y1 + halfW, z1).setColor(r, g, b, a);
        vc.addVertex(mat, x1, y1 - halfW, z1).setColor(r, g, b, a);
        vc.addVertex(mat, x0 - halfW, y0, z0).setColor(r, g, b, a);
        vc.addVertex(mat, x0 + halfW, y0, z0).setColor(r, g, b, a);
        vc.addVertex(mat, x1 + halfW, y1, z1).setColor(r, g, b, a);
        vc.addVertex(mat, x1 - halfW, y1, z1).setColor(r, g, b, a);
    }

    private static void drawGroundQuad(VertexConsumer vc, Matrix4f mat, float x0, float x1, float z0, float z1, float y, int r, int g, int b, int a) {
        vc.addVertex(mat, x0, y, z0).setColor(r, g, b, a);
        vc.addVertex(mat, x0, y, z1).setColor(r, g, b, a);
        vc.addVertex(mat, x1, y, z1).setColor(r, g, b, a);
        vc.addVertex(mat, x1, y, z0).setColor(r, g, b, a);
        vc.addVertex(mat, x1, y, z0).setColor(r, g, b, a);
        vc.addVertex(mat, x1, y, z1).setColor(r, g, b, a);
        vc.addVertex(mat, x0, y, z1).setColor(r, g, b, a);
        vc.addVertex(mat, x0, y, z0).setColor(r, g, b, a);
    }

    private static void drawRing(VertexConsumer vc, Matrix4f mat, float rIn, float rOut, int segs, float y, int r, int g, int b, int a) {
        for (int i = 0; i < segs; i++) {
            float a0 = (float) (Math.PI * 2.0D * i / segs);
            float a1 = (float) (Math.PI * 2.0D * (i + 1) / segs);
            float c0 = Mth.cos(a0), s0 = Mth.sin(a0);
            float c1 = Mth.cos(a1), s1 = Mth.sin(a1);
            // Double-sided so ground rings & waves render cleanly from every camera angle
            vc.addVertex(mat, c0 * rIn, y, s0 * rIn).setColor(r, g, b, a);
            vc.addVertex(mat, c0 * rOut, y, s0 * rOut).setColor(r, g, b, a);
            vc.addVertex(mat, c1 * rOut, y, s1 * rOut).setColor(r, g, b, a);
            vc.addVertex(mat, c1 * rIn, y, s1 * rIn).setColor(r, g, b, a);
            vc.addVertex(mat, c1 * rIn, y, s1 * rIn).setColor(r, g, b, a);
            vc.addVertex(mat, c1 * rOut, y, s1 * rOut).setColor(r, g, b, a);
            vc.addVertex(mat, c0 * rOut, y, s0 * rOut).setColor(r, g, b, a);
            vc.addVertex(mat, c0 * rIn, y, s0 * rIn).setColor(r, g, b, a);
        }
    }

    private static void drawBeamRing(VertexConsumer vc, Matrix4f mat, float zDist, float rIn, float rOut, int segs, int r, int g, int b, int a) {
        if (a <= 5) return;
        float z = -zDist;
        for (int i = 0; i < segs; i++) {
            float a0 = (float) (Math.PI * 2.0D * i / segs);
            float a1 = (float) (Math.PI * 2.0D * (i + 1) / segs);
            float c0 = Mth.cos(a0), s0 = Mth.sin(a0);
            float c1 = Mth.cos(a1), s1 = Mth.sin(a1);
            vc.addVertex(mat, c0 * rIn, s0 * rIn, z).setColor(r, g, b, 0);
            vc.addVertex(mat, c0 * rOut, s0 * rOut, z).setColor(r, g, b, a);
            vc.addVertex(mat, c1 * rOut, s1 * rOut, z).setColor(r, g, b, a);
            vc.addVertex(mat, c1 * rIn, s1 * rIn, z).setColor(r, g, b, 0);
            vc.addVertex(mat, c1 * rIn, s1 * rIn, z).setColor(r, g, b, 0);
            vc.addVertex(mat, c1 * rOut, s1 * rOut, z).setColor(r, g, b, a);
            vc.addVertex(mat, c0 * rOut, s0 * rOut, z).setColor(r, g, b, a);
            vc.addVertex(mat, c0 * rIn, s0 * rIn, z).setColor(r, g, b, 0);
        }
    }

    private static void drawEnergyColumn(VertexConsumer vc, Matrix4f mat, float radius, float y0, float y1, int segs, int r, int g, int b, int a) {
        if (a <= 5) return;
        for (int i = 0; i < segs; i++) {
            float a0 = (float) (Math.PI * 2.0D * i / segs);
            float a1 = (float) (Math.PI * 2.0D * (i + 1) / segs);
            float c0 = Mth.cos(a0) * radius, s0 = Mth.sin(a0) * radius;
            float c1 = Mth.cos(a1) * radius, s1 = Mth.sin(a1) * radius;
            vc.addVertex(mat, c0, y0, s0).setColor(r, g, b, a);
            vc.addVertex(mat, c1, y0, s1).setColor(r, g, b, a);
            vc.addVertex(mat, c1 * 1.35F, y1, s1 * 1.35F).setColor(r, g, b, 0);
            vc.addVertex(mat, c0 * 1.35F, y1, s0 * 1.35F).setColor(r, g, b, 0);
        }
    }

    private static void drawBeamCone(VertexConsumer vc, Matrix4f mat, float zStart, float zEnd, float rStart, float rEnd, int segs, int r, int g, int b, int a) {
        if (a <= 5) return;
        float z0 = -zStart;
        float z1 = -zEnd;
        for (int i = 0; i < segs; i++) {
            float a0 = (float) (Math.PI * 2.0D * i / segs);
            float a1 = (float) (Math.PI * 2.0D * (i + 1) / segs);
            float c0 = Mth.cos(a0), s0 = Mth.sin(a0);
            float c1 = Mth.cos(a1), s1 = Mth.sin(a1);
            vc.addVertex(mat, c0 * rStart, s0 * rStart, z0).setColor(r, g, b, a);
            vc.addVertex(mat, c1 * rStart, s1 * rStart, z0).setColor(r, g, b, a);
            vc.addVertex(mat, c1 * rEnd, s1 * rEnd, z1).setColor(r, g, b, 0);
            vc.addVertex(mat, c0 * rEnd, s0 * rEnd, z1).setColor(r, g, b, 0);
        }
    }

    @Override
    public ResourceLocation getTextureLocation(PrimoGeovishapEntity entity) {
        return TEXTURE;
    }
}

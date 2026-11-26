package com.velorise.teyvatbosses.client;

import com.velorise.teyvatbosses.client.model.RuinGuardAnimations;
import com.velorise.teyvatbosses.entity.RuinMechEntity;
import com.velorise.teyvatbosses.registry.TeyvatBossesSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;

/**
 * Client-side procedural camera & tactical audio controller for the Pilotable Ruin Mech:
 * <ul>
 *   <li>Extends 3rd-person camera distance to {@link #THIRD_PERSON_ZOOM} blocks.</li>
 *   <li>1st-person cockpit camera physically tracks the animated eye-core torso pitch/yaw/roll during combat skills & movement.</li>
 *   <li>Amplified impact camera shake in 1st-person synced to every strike, stomp, slam, and missile launch.</li>
 *   <li>Tactical radar lock-on beep when acquiring targets or charging {@code R+} missiles.</li>
 * </ul>
 */
public final class RuinMechCameraController {
    public static final float THIRD_PERSON_ZOOM = 7.2F;

    private static float shakeTrauma = 0.0F;
    private static int lastTriggeredEpoch = -1;
    private static int lastTriggeredTick = -1;
    private static int lastHurtTime = 0;
    private static int lastLockedId = -1;
    private static int chargeBeepTick = 0;

    private RuinMechCameraController() {}

    public static RuinMechEntity getPilotedMech() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.player.getVehicle() instanceof RuinMechEntity mech) {
            return mech;
        }
        return null;
    }

    public static void addTrauma(float amount) {
        shakeTrauma = Mth.clamp(shakeTrauma + amount, 0.0F, 1.0F);
    }

    public static void clientTick() {
        Minecraft mc = Minecraft.getInstance();
        RuinMechEntity mech = getPilotedMech();
        if (mech == null || mc.player == null) {
            shakeTrauma = 0.0F;
            lastTriggeredEpoch = -1;
            lastTriggeredTick = -1;
            lastHurtTime = 0;
            lastLockedId = -1;
            chargeBeepTick = 0;
            return;
        }

        // Decay shake trauma smoothly
        shakeTrauma = Math.max(0.0F, shakeTrauma - 0.062F);

        // Tactical radar lock-on beep on new target acquisition or while charging R+ missiles
        int lockedId = mech.getLockedTargetId();
        SoundEvent beep = TeyvatBossesSounds.mechLockonBeep();
        if (lockedId != -1 && lockedId != lastLockedId && beep != null) {
            mc.player.playSound(beep, 0.48F, 1.05F);
        }
        lastLockedId = lockedId;

        float rmbCharge = RuinMechInputController.getRmbHoldProgress();
        if (rmbCharge > 0.12F && lockedId != -1 && beep != null) {
            if (++chargeBeepTick % 3 == 1) {
                mc.player.playSound(beep, 0.55F, 1.10F + rmbCharge * 0.45F);
            }
        } else {
            chargeBeepTick = 0;
        }

        int anim = mech.getAnimId();
        int tick = mech.getAnimTick();
        int epoch = mech.getAnimEpoch();

        if (mech.hurtTime > lastHurtTime) {
            addTrauma(0.60F);
        }
        lastHurtTime = mech.hurtTime;

        boolean newStep = (epoch != lastTriggeredEpoch || tick != lastTriggeredTick);
        if (newStep) {
            lastTriggeredEpoch = epoch;
            lastTriggeredTick = tick;

            if (anim == RuinMechEntity.ANIM_WALK && tick % 24 == 12) {
                addTrauma(0.34F);
            } else if (anim == RuinMechEntity.ANIM_DOUBLE_CLAP) {
                if (tick == 10) addTrauma(0.22F);
                else if (tick == 23) addTrauma(0.82F);
            } else if (anim == RuinMechEntity.ANIM_MELEE_3HIT) {
                if (tick == 24) addTrauma(0.55F);
                else if (tick == 58) addTrauma(0.68F);
                else if (tick == 98) addTrauma(0.98F);
            } else if (anim == RuinMechEntity.ANIM_STOMP) {
                if (tick == 8) addTrauma(0.20F);
                else if (tick == 20) addTrauma(0.86F);
            } else if (anim == RuinMechEntity.ANIM_CYCLONE_WINDUP && tick == 14) {
                addTrauma(0.32F);
            } else if (anim == RuinMechEntity.ANIM_CYCLONE_ACTIVE && tick % 10 == 0) {
                addTrauma(0.45F);
            } else if (anim == RuinMechEntity.ANIM_LEAP_SLAM) {
                if (tick == 17) addTrauma(0.42F);
                else if (tick == 36) addTrauma(1.00F);
            } else if (anim == RuinMechEntity.ANIM_BOOST_LEAP) {
                if (tick == 6) addTrauma(0.32F);
                else if (tick == 23) addTrauma(0.58F);
            } else if (anim == RuinMechEntity.ANIM_SUPER_LEAP) {
                if (tick == 6) addTrauma(0.52F);
                else if (tick == 31) addTrauma(0.82F);
            } else if (anim == RuinMechEntity.ANIM_MISSILE_SALVO && tick >= 16 && tick < 34 && (tick - 16) % 3 == 0) {
                addTrauma(0.38F);
            } else if (anim == RuinMechEntity.ANIM_ENTER_COCKPIT && tick == 13) {
                addTrauma(0.45F);
            }
        }
    }

    /**
     * Computes additive camera angle offsets {@code [yawOffsetDeg, pitchOffsetDeg, rollOffsetDeg]} for the current frame.
     */
    public static float[] computeOffsets(float partialTicks) {
        Minecraft mc = Minecraft.getInstance();
        RuinMechEntity mech = getPilotedMech();
        if (mech == null) {
            return new float[]{0.0F, 0.0F, 0.0F};
        }

        boolean firstPerson = mc.options.getCameraType().isFirstPerson();
        float time = mech.tickCount + partialTicks;
        float animT = mech.getAnimTick() + partialTicks;
        int anim = mech.getAnimId();

        float yawOff = 0.0F;
        float pitchOff = 0.0F;
        float rollOff = 0.0F;

        // 1st-person cockpit camera physically tracks the animated torso/waist/core orientation during combat & movement!
        if (firstPerson && anim != RuinMechEntity.ANIM_IDLE && anim != RuinMechEntity.ANIM_DORMANT) {
            float[] core = new float[6];
            RuinGuardAnimations.sampleCoreMotion(mech.getBaseClipName(), animT / 20.0F, core);
            // core = [pelvisX, pelvisY, pelvisZ, torsoPitchDeg, waistYawDeg, torsoRollDeg]
            // Subtract baseline combat stance pitch (~7 deg) so idle/neutral stays level
            float relPitch = core[3] - 7.0F;
            float relYaw = Mth.clamp(core[4], -26.0F, 26.0F);
            float relRoll = Mth.clamp(core[5], -16.0F, 16.0F);
            if (anim == RuinMechEntity.ANIM_WALK) {
                pitchOff += relPitch * 0.42F;
                rollOff  += relRoll * 0.55F;
            } else {
                // Combat skills & leaps: strong core-coupled pitch/yaw/roll so 1st-person feels seated inside the chest core!
                pitchOff += Mth.clamp(relPitch * 0.52F, -18.0F, 24.0F);
                yawOff   += relYaw * 0.35F;
                rollOff  += relRoll * 0.65F;
            }
        } else if (anim == RuinMechEntity.ANIM_WALK) {
            float cycle = (animT % 48.0F) / 48.0F;
            float angle = cycle * Mth.TWO_PI;
            pitchOff += Mth.sin(angle * 2.0F - 0.6F) * 1.05F;
            rollOff  += Mth.sin(angle) * 0.85F;
        }

        if (anim == RuinMechEntity.ANIM_ENTER_COCKPIT) {
            float p = Mth.clamp(animT / 26.0F, 0.0F, 1.0F);
            float ease = 1.0F - (float) Math.pow(1.0F - p, 3.0F);
            pitchOff += (1.0F - ease) * 13.0F;
            rollOff  += Mth.sin(p * Mth.PI) * 2.4F;
        } else if (anim == RuinMechEntity.ANIM_CYCLONE_ACTIVE) {
            rollOff += Mth.sin(time * 0.9F) * (firstPerson ? 4.2F : 2.6F);
            if (firstPerson) {
                yawOff += Mth.cos(time * 1.15F) * 3.8F;
            }
        }

        if (shakeTrauma > 0.001F) {
            float s = shakeTrauma * shakeTrauma * (firstPerson ? 1.55F : 1.0F);
            yawOff   += Mth.sin(time * 3.9F) * 3.0F * s;
            pitchOff += Mth.cos(time * 4.5F) * 2.8F * s;
            rollOff  += Mth.sin(time * 5.3F + 1.1F) * 3.8F * s;
        }

        return new float[]{yawOff, pitchOff, rollOff};
    }
}

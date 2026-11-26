package com.velorise.teyvatbosses.client;

import com.velorise.teyvatbosses.entity.RuinMechEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;

/**
 * Client-side procedural camera controller for the Pilotable Ruin Mech:
 * <ul>
 *   <li>Extends 3rd-person camera distance to {@link #THIRD_PERSON_ZOOM} blocks so the full 4m chassis fits cleanly.</li>
 *   <li>Applies heavy 48-tick step bobbing (pitch dip + alternating left/right roll sway) synced to {@code walk_combat}.</li>
 *   <li>Applies impact camera shake on Double Clap, 3-Hit Combo, Stomp, Cyclone Spin, Leap Slam, Missile launch, and damage.</li>
 * </ul>
 */
public final class RuinMechCameraController {
    public static final float THIRD_PERSON_ZOOM = 7.2F;

    private static float shakeTrauma = 0.0F;
    private static int lastTriggeredEpoch = -1;
    private static int lastTriggeredTick = -1;
    private static int lastHurtTime = 0;

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
        RuinMechEntity mech = getPilotedMech();
        if (mech == null) {
            shakeTrauma = 0.0F;
            lastTriggeredEpoch = -1;
            lastTriggeredTick = -1;
            lastHurtTime = 0;
            return;
        }

        // Decay shake trauma smoothly
        shakeTrauma = Math.max(0.0F, shakeTrauma - 0.065F);

        int anim = mech.getAnimId();
        int tick = mech.getAnimTick();
        int epoch = mech.getAnimEpoch();

        if (mech.hurtTime > lastHurtTime) {
            addTrauma(0.55F);
        }
        lastHurtTime = mech.hurtTime;

        boolean newStep = (epoch != lastTriggeredEpoch || tick != lastTriggeredTick);
        if (newStep) {
            lastTriggeredEpoch = epoch;
            lastTriggeredTick = tick;

            if (anim == RuinMechEntity.ANIM_WALK && tick % 24 == 12) {
                addTrauma(0.24F);
            } else if (anim == RuinMechEntity.ANIM_DOUBLE_CLAP && tick == 23) {
                addTrauma(0.75F);
            } else if (anim == RuinMechEntity.ANIM_MELEE_3HIT) {
                if (tick == 24) addTrauma(0.45F);
                else if (tick == 58) addTrauma(0.55F);
                else if (tick == 98) addTrauma(0.90F);
            } else if (anim == RuinMechEntity.ANIM_STOMP && tick == 20) {
                addTrauma(0.80F);
            } else if (anim == RuinMechEntity.ANIM_CYCLONE_ACTIVE && tick % 10 == 0) {
                addTrauma(0.38F);
            } else if (anim == RuinMechEntity.ANIM_LEAP_SLAM && tick == 29) {
                addTrauma(1.00F);
            } else if (anim == RuinMechEntity.ANIM_MISSILE_SALVO && tick >= 16 && tick < 34 && (tick - 16) % 3 == 0) {
                addTrauma(0.32F);
            } else if (anim == RuinMechEntity.ANIM_ENTER_COCKPIT && tick == 18) {
                addTrauma(0.35F);
            }
        }
    }

    /**
     * Computes additive camera angle offsets {@code [yawOffsetDeg, pitchOffsetDeg, rollOffsetDeg]} for the current frame.
     */
    public static float[] computeOffsets(float partialTicks) {
        RuinMechEntity mech = getPilotedMech();
        if (mech == null) {
            return new float[]{0.0F, 0.0F, 0.0F};
        }

        float time = mech.tickCount + partialTicks;
        float animT = mech.getAnimTick() + partialTicks;
        int anim = mech.getAnimId();

        float yawOff = 0.0F;
        float pitchOff = 0.0F;
        float rollOff = 0.0F;

        // 1. Heavy 48-tick walking gait sway & step dip synced with walk_combat
        if (anim == RuinMechEntity.ANIM_WALK) {
            float cycle = (animT % 48.0F) / 48.0F; // 0..1 per full L+R stride
            float angle = cycle * Mth.TWO_PI;
            // Subtle cockpit pitch nod (2x per cycle) + heavy alternating shoulder roll (1x per cycle)
            pitchOff += Mth.sin(angle * 2.0F - 0.6F) * 1.15F;
            rollOff  += Mth.sin(angle) * 1.65F;
        } else if (anim == RuinMechEntity.ANIM_ENTER_COCKPIT) {
            float p = Mth.clamp(animT / 24.0F, 0.0F, 1.0F);
            pitchOff += Mth.sin(p * Mth.PI) * 5.5F;
        } else if (anim == RuinMechEntity.ANIM_CYCLONE_ACTIVE) {
            rollOff += Mth.sin(time * 0.9F) * 2.8F;
        }

        // 2. Quadratic trauma-based impact camera shake
        if (shakeTrauma > 0.001F) {
            float s = shakeTrauma * shakeTrauma;
            yawOff   += Mth.sin(time * 3.7F) * 2.8F * s;
            pitchOff += Mth.cos(time * 4.3F) * 2.6F * s;
            rollOff  += Mth.sin(time * 5.1F + 1.1F) * 3.6F * s;
        }

        return new float[]{yawOff, pitchOff, rollOff};
    }
}

package com.velorise.teyvatbosses.client;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

/**
 * Lightweight, distance-attenuated cinematic camera shake & impact hitstop controller
 * for AAA world bosses (Primo Geovishap roars/slams/shield-counters, Ruin Guard weakpoint
 * breaks, paralysis collapses, and heavy finishers).
 */
public final class BossCameraShake {
    private static float trauma = 0.0F;
    private static float prevTrauma = 0.0F;
    private static float phase = 0.0F;

    // Client-side impact hitstop (2–4 tick animation freeze-frame + tactile impact punch)
    private static int hitstopTicks = 0;
    private static int hitstopMaxTicks = 1;

    private BossCameraShake() {}

    /**
     * Triggers a distance-attenuated screen shake centered at {@code origin} with falloff up to {@code maxRange}.
     */
    public static void triggerAt(Vec3 origin, float intensity, double maxRange) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        double dist = mc.player.position().distanceTo(origin);
        if (dist >= maxRange) return;
        float falloff = (float) (1.0D - (dist / maxRange));
        float scaled = intensity * falloff * falloff;
        trauma = Mth.clamp(Math.max(trauma, scaled), 0.0F, 1.0F);
    }

    /**
     * Triggers a crisp client-side impact hitstop (2–4 tick boss pose freeze + camera punch)
     * when the local player is within {@code maxRange} of {@code origin}.
     */
    public static void triggerHitstop(Vec3 origin, int ticks, float shakeIntensity, double maxRange) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        double dist = mc.player.position().distanceTo(origin);
        if (dist >= maxRange) return;
        int clamped = Mth.clamp(ticks, 1, 5);
        if (clamped >= hitstopTicks) {
            hitstopTicks = clamped;
            hitstopMaxTicks = Math.max(1, clamped);
        }
        if (shakeIntensity > 0.0F) {
            triggerAt(origin, shakeIntensity, maxRange);
        }
    }

    public static boolean isHitstopActive() {
        return hitstopTicks > 0;
    }

    public static float getHitstopIntensity() {
        if (hitstopTicks <= 0) return 0.0F;
        return Mth.clamp((float) hitstopTicks / (float) Math.max(1, hitstopMaxTicks), 0.0F, 1.0F);
    }

    public static void clientTick() {
        prevTrauma = trauma;
        if (trauma > 0.0F) {
            trauma = Math.max(0.0F, trauma - 0.055F);
            phase += 1.0F;
        }
        if (hitstopTicks > 0) {
            hitstopTicks--;
        }
    }

    public static boolean isActive() {
        return trauma > 0.002F || prevTrauma > 0.002F;
    }

    /** Returns {yawOffsetDeg, pitchOffsetDeg, rollOffsetDeg}. */
    public static float[] computeOffsets(float partialTick) {
        float t = Mth.lerp(partialTick, prevTrauma, trauma);
        if (t <= 0.002F) return new float[]{0.0F, 0.0F, 0.0F};
        float power = t * t;
        float time = (phase + partialTick) * 1.85F;
        float yaw = Mth.sin(time * 2.3F) * 2.2F * power;
        float pitch = Mth.cos(time * 2.9F) * 1.9F * power;
        float roll = Mth.sin(time * 1.7F + 0.8F) * 1.6F * power;
        return new float[]{yaw, pitch, roll};
    }
}

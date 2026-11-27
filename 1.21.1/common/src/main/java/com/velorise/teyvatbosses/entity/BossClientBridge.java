package com.velorise.teyvatbosses.entity;

import net.minecraft.world.phys.Vec3;

/**
 * Dedicated-server-safe bridge for triggering client-only boss visual effects (such as camera shake)
 * without importing {@code net.minecraft.client.*} classes inside common entity code.
 */
public final class BossClientBridge {

    @FunctionalInterface
    public interface ShakeHandler {
        void triggerAt(Vec3 origin, float intensity, double maxRange);
    }

    private static volatile ShakeHandler shakeHandler = (origin, intensity, maxRange) -> {};

    private BossClientBridge() {}

    public static void setShakeHandler(ShakeHandler handler) {
        shakeHandler = handler != null ? handler : (origin, intensity, maxRange) -> {};
    }

    public static void triggerCameraShake(Vec3 origin, float intensity, double maxRange) {
        shakeHandler.triggerAt(origin, intensity, maxRange);
    }
}

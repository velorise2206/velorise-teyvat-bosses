package com.velorise.teyvat.portal.client;

import com.velorise.teyvat.portal.CelestiaTransitionPackets;
import net.minecraft.client.Minecraft;

/**
 * Client-side packet handlers for {@link CelestiaTransitionScreen}.
 * Call these from your NeoForge / Fabric client payload receivers.
 */
public final class CelestiaTransitionClient {
    private CelestiaTransitionClient() {}

    public static void handleStartTransition(CelestiaTransitionPackets.StartTransitionPayload payload) {
        Minecraft mc = Minecraft.getInstance();
        mc.execute(() -> mc.setScreen(
                new CelestiaTransitionScreen(payload.targetDimension(), payload.fullBridgeCutscene())
        ));
    }

    public static void handlePreloadProgress(CelestiaTransitionPackets.PreloadProgressPayload payload) {
        Minecraft mc = Minecraft.getInstance();
        mc.execute(() -> {
            if (mc.screen instanceof CelestiaTransitionScreen screen) {
                screen.updatePreloadProgress(payload.progress(), payload.ready());
            }
        });
    }
}

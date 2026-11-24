package com.velorise.teyvat.fabric.network;

import com.velorise.teyvat.portal.CelestiaTeleportHandler;
import com.velorise.teyvat.portal.CelestiaTransitionPackets;
import com.velorise.teyvat.portal.client.CelestiaTransitionClient;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

/**
 * Fabric 1.21.1 registration & bridge for Celestia Portal transition packets.
 * Call {@link #registerCommon()} in {@code onInitialize} and {@link #registerClient()} in {@code onInitializeClient}.
 */
public final class FabricCelestiaNetwork {
    private FabricCelestiaNetwork() {}

    public static void registerCommon() {
        PayloadTypeRegistry.playS2C().register(
                CelestiaTransitionPackets.StartTransitionPayload.TYPE,
                CelestiaTransitionPackets.StartTransitionPayload.STREAM_CODEC
        );
        PayloadTypeRegistry.playS2C().register(
                CelestiaTransitionPackets.PreloadProgressPayload.TYPE,
                CelestiaTransitionPackets.PreloadProgressPayload.STREAM_CODEC
        );
        PayloadTypeRegistry.playC2S().register(
                CelestiaTransitionPackets.CommitTeleportPayload.TYPE,
                CelestiaTransitionPackets.CommitTeleportPayload.STREAM_CODEC
        );

        ServerPlayNetworking.registerGlobalReceiver(
                CelestiaTransitionPackets.CommitTeleportPayload.TYPE,
                (payload, ctx) -> ctx.server().execute(() ->
                        CelestiaTeleportHandler.handleCommitTeleport(ctx.player(), payload.targetDimension())
                )
        );

        CelestiaTransitionPackets.bindBridge(new CelestiaTransitionPackets.NetworkBridge() {
            @Override
            public void sendToPlayer(ServerPlayer player, CustomPacketPayload payload) {
                ServerPlayNetworking.send(player, payload);
            }

            @Override
            public void sendToServer(CustomPacketPayload payload) {
                ClientPlayNetworking.send(payload);
            }
        });
    }

    public static void registerClient() {
        ClientPlayNetworking.registerGlobalReceiver(
                CelestiaTransitionPackets.StartTransitionPayload.TYPE,
                (payload, ctx) -> CelestiaTransitionClient.handleStartTransition(payload)
        );
        ClientPlayNetworking.registerGlobalReceiver(
                CelestiaTransitionPackets.PreloadProgressPayload.TYPE,
                (payload, ctx) -> CelestiaTransitionClient.handlePreloadProgress(payload)
        );
    }
}

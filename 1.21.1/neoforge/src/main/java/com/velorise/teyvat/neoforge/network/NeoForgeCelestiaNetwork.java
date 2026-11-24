package com.velorise.teyvat.neoforge.network;

import com.velorise.teyvat.portal.CelestiaTeleportHandler;
import com.velorise.teyvat.portal.CelestiaTransitionPackets;
import com.velorise.teyvat.portal.client.CelestiaTransitionClient;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

/**
 * NeoForge 1.21.1 registration & bridge for Celestia Portal transition packets.
 * Subscribe {@link #onRegisterPayloads} on your mod event bus and call {@link #initBridge()} during mod init.
 */
public final class NeoForgeCelestiaNetwork {
    private NeoForgeCelestiaNetwork() {}

    public static void initBridge() {
        CelestiaTransitionPackets.bindBridge(new CelestiaTransitionPackets.NetworkBridge() {
            @Override
            public void sendToPlayer(ServerPlayer player, CustomPacketPayload payload) {
                PacketDistributor.sendToPlayer(player, payload);
            }

            @Override
            public void sendToServer(CustomPacketPayload payload) {
                PacketDistributor.sendToServer(payload);
            }
        });
    }

    public static void onRegisterPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");

        registrar.playToClient(
                CelestiaTransitionPackets.StartTransitionPayload.TYPE,
                CelestiaTransitionPackets.StartTransitionPayload.STREAM_CODEC,
                (payload, ctx) -> CelestiaTransitionClient.handleStartTransition(payload)
        );
        registrar.playToClient(
                CelestiaTransitionPackets.PreloadProgressPayload.TYPE,
                CelestiaTransitionPackets.PreloadProgressPayload.STREAM_CODEC,
                (payload, ctx) -> CelestiaTransitionClient.handlePreloadProgress(payload)
        );
        registrar.playToServer(
                CelestiaTransitionPackets.CommitTeleportPayload.TYPE,
                CelestiaTransitionPackets.CommitTeleportPayload.STREAM_CODEC,
                (payload, ctx) -> ctx.enqueueWork(() -> {
                    if (ctx.player() instanceof ServerPlayer sp) {
                        CelestiaTeleportHandler.handleCommitTeleport(sp, payload.targetDimension());
                    }
                })
        );
    }
}

package com.velorise.teyvat.portal;

import com.velorise.teyvat.worldgen.TeyvatDimensions;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

/**
 * MultiLoader CustomPacketPayload definitions for the 4-phase Celestia portal transition.
 * Both NeoForge 1.21.1 and Fabric 1.21.1 register these payloads directly.
 */
public final class CelestiaTransitionPackets {
    private CelestiaTransitionPackets() {}

    /**
     * S2C: Starts the client-side 3D Celestia bridge cutscene while the server preloads destination chunks.
     */
    public record StartTransitionPayload(
            ResourceLocation targetDimension,
            boolean fullBridgeCutscene,
            int targetX,
            int targetY,
            int targetZ
    ) implements CustomPacketPayload {
        public static final Type<StartTransitionPayload> TYPE =
                new Type<>(TeyvatDimensions.id("celestia_start_transition"));

        public static final StreamCodec<FriendlyByteBuf, StartTransitionPayload> STREAM_CODEC =
                StreamCodec.of(
                        (buf, msg) -> {
                            buf.writeResourceLocation(msg.targetDimension);
                            buf.writeBoolean(msg.fullBridgeCutscene);
                            buf.writeVarInt(msg.targetX);
                            buf.writeVarInt(msg.targetY);
                            buf.writeVarInt(msg.targetZ);
                        },
                        buf -> new StartTransitionPayload(
                                buf.readResourceLocation(),
                                buf.readBoolean(),
                                buf.readVarInt(),
                                buf.readVarInt(),
                                buf.readVarInt()
                        )
                );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    /**
     * S2C: Streams destination chunk preload progress (0.0..1.0) so the bridge cutscene knows when to raise the gate.
     */
    public record PreloadProgressPayload(float progress, boolean ready) implements CustomPacketPayload {
        public static final Type<PreloadProgressPayload> TYPE =
                new Type<>(TeyvatDimensions.id("celestia_preload_progress"));

        public static final StreamCodec<FriendlyByteBuf, PreloadProgressPayload> STREAM_CODEC =
                StreamCodec.of(
                        (buf, msg) -> {
                            buf.writeFloat(msg.progress);
                            buf.writeBoolean(msg.ready);
                        },
                        buf -> new PreloadProgressPayload(buf.readFloat(), buf.readBoolean())
                );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    /**
     * C2S: Sent by the client right as the white flash peaks (alpha = 1.0) to execute {@code teleportTo}
     * invisibly behind the 7-element white loading screen.
     */
    public record CommitTeleportPayload(ResourceLocation targetDimension) implements CustomPacketPayload {
        public static final Type<CommitTeleportPayload> TYPE =
                new Type<>(TeyvatDimensions.id("celestia_commit_teleport"));

        public static final StreamCodec<FriendlyByteBuf, CommitTeleportPayload> STREAM_CODEC =
                StreamCodec.of(
                        (buf, msg) -> buf.writeResourceLocation(msg.targetDimension),
                        buf -> new CommitTeleportPayload(buf.readResourceLocation())
                );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    /**
     * Loader bridge implemented once in NeoForge and once in Fabric.
     */
    public interface NetworkBridge {
        void sendToPlayer(ServerPlayer player, CustomPacketPayload payload);
        void sendToServer(CustomPacketPayload payload);
    }

    private static NetworkBridge bridge;

    public static void bindBridge(NetworkBridge impl) {
        bridge = impl;
    }

    public static NetworkBridge bridge() {
        if (bridge == null) {
            throw new IllegalStateException("CelestiaTransitionPackets.NetworkBridge not bound by loader");
        }
        return bridge;
    }
}

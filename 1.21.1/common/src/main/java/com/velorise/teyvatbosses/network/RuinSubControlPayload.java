package com.velorise.teyvatbosses.network;

import com.velorise.teyvatbosses.TeyvatBossesConstants;
import com.velorise.teyvatbosses.entity.RuinSubmersibleEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

/**
 * C2S network payload carrying pilot depth controls, combat/utility actions, and aim vector
 * for {@link RuinSubmersibleEntity}.
 */
public record RuinSubControlPayload(int verticalInput, int action, float aimX, float aimY, float aimZ) implements CustomPacketPayload {
    public static final Type<RuinSubControlPayload> TYPE =
            new Type<>(TeyvatBossesConstants.id("ruin_sub_control"));

    public static final StreamCodec<FriendlyByteBuf, RuinSubControlPayload> STREAM_CODEC = StreamCodec.of(
            (buf, msg) -> {
                buf.writeVarInt(msg.verticalInput);
                buf.writeVarInt(msg.action);
                buf.writeFloat(msg.aimX);
                buf.writeFloat(msg.aimY);
                buf.writeFloat(msg.aimZ);
            },
            buf -> new RuinSubControlPayload(
                    buf.readVarInt(),
                    buf.readVarInt(),
                    buf.readFloat(),
                    buf.readFloat(),
                    buf.readFloat()
            )
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handleOnServer(RuinSubControlPayload payload, ServerPlayer player) {
        if (player != null && player.getVehicle() instanceof RuinSubmersibleEntity sub
                && player.level() instanceof ServerLevel sl) {
            sub.handlePilotControl(sl, player, payload.verticalInput, payload.action,
                    new Vec3(payload.aimX, payload.aimY, payload.aimZ));
        }
    }
}

package com.velorise.teyvatbosses.network;

import com.velorise.teyvatbosses.TeyvatBossesConstants;
import com.velorise.teyvatbosses.entity.GeoDrillerEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

/**
 * C2S control packet for {@link GeoDrillerEntity}:
 * toggle drill (LMB), forward drill surge (RMB), cycle pitch mode Level/Down/Up (Space),
 * toggle headlight (F), toggle auto-pave (B).
 */
public record GeoDrillerControlPayload(int action) implements CustomPacketPayload {
    public static final int ACTION_TOGGLE_DRILL = 1;
    public static final int ACTION_CYCLE_PITCH  = 2;
    public static final int ACTION_TOGGLE_LIGHT = 3;
    public static final int ACTION_TOGGLE_PAVE  = 4;
    public static final int ACTION_DRILL_SURGE  = 5;

    public static final Type<GeoDrillerControlPayload> TYPE =
            new Type<>(TeyvatBossesConstants.id("geo_driller_control"));

    public static final StreamCodec<ByteBuf, GeoDrillerControlPayload> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.VAR_INT, GeoDrillerControlPayload::action, GeoDrillerControlPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handleOnServer(GeoDrillerControlPayload payload, ServerPlayer player) {
        if (player.getVehicle() instanceof GeoDrillerEntity driller) {
            driller.handleControlAction(payload.action(), player);
        }
    }
}

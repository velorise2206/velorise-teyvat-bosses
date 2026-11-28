package com.velorise.teyvatbosses.network;

import com.velorise.teyvatbosses.TeyvatBossesConstants;
import com.velorise.teyvatbosses.entity.GoldflameQucusaurMountEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

/**
 * C2S control packet for {@link GoldflameQucusaurMountEntity}:
 * ground walk + Space takeoff, vertical ascend/descend, glide boost,
 * crosshair-aimed fireball spit (LMB tap), continuous golden flame breath (LMB hold),
 * and RMB aerial dive / ground heatwave takeoff.
 */
public record GoldflameQucusaurControlPayload(int action) implements CustomPacketPayload {
    public static final int ACTION_SPACE_TAKEOFF = 1;
    public static final int ACTION_FIREBALL_SPIT = 2;
    public static final int ACTION_BREATH_START  = 3;
    public static final int ACTION_BREATH_STOP   = 4;
    public static final int ACTION_RMB_SKILL     = 5;
    public static final int ACTION_BOOST_START   = 6;
    public static final int ACTION_BOOST_STOP    = 7;
    public static final int ACTION_ASCEND_START  = 8;
    public static final int ACTION_ASCEND_STOP   = 9;
    public static final int ACTION_DESCEND_START = 10;
    public static final int ACTION_DESCEND_STOP  = 11;

    public static final Type<GoldflameQucusaurControlPayload> TYPE =
            new Type<>(TeyvatBossesConstants.id("goldflame_qucusaur_control"));

    public static final StreamCodec<ByteBuf, GoldflameQucusaurControlPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT,
                    GoldflameQucusaurControlPayload::action,
                    GoldflameQucusaurControlPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handleOnServer(GoldflameQucusaurControlPayload payload, ServerPlayer player) {
        if (player.getVehicle() instanceof GoldflameQucusaurMountEntity mount) {
            mount.handleControlAction(payload.action(), player);
        }
    }
}

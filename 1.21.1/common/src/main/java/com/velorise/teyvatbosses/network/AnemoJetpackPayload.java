package com.velorise.teyvatbosses.network;

import com.velorise.teyvatbosses.TeyvatBossesConstants;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * C2S payload sent when a player wearing the Anemo Jet-Wing starts or stops boosting mid-air with Space.
 */
public record AnemoJetpackPayload(boolean boosting) implements CustomPacketPayload {
    public static final Type<AnemoJetpackPayload> TYPE =
            new Type<>(TeyvatBossesConstants.id("anemo_jetpack_boost"));

    public static final StreamCodec<FriendlyByteBuf, AnemoJetpackPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.BOOL, AnemoJetpackPayload::boosting,
                    AnemoJetpackPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

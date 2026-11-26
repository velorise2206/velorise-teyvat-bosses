package com.velorise.teyvatbosses.network;

import com.velorise.teyvatbosses.TeyvatBossesConstants;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * C2S payload sent when a pilot triggers one of the 6 Ruin Mech combat skills via dedicated keybinds.
 */
public record RuinMechSkillPayload(int skillId) implements CustomPacketPayload {
    public static final Type<RuinMechSkillPayload> TYPE =
            new Type<>(TeyvatBossesConstants.id("ruin_mech_skill"));

    public static final StreamCodec<FriendlyByteBuf, RuinMechSkillPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT, RuinMechSkillPayload::skillId,
                    RuinMechSkillPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

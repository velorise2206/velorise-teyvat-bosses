package com.velorise.teyvatbosses.network;

import com.velorise.teyvatbosses.TeyvatBossesConstants;
import com.velorise.teyvatbosses.item.AnemoBootsItem;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.phys.Vec3;

/**
 * C2S notification sent when a player triggers a mid-air double jump wearing {@link AnemoBootsItem}.
 */
public record AnemoDoubleJumpPayload() implements CustomPacketPayload {

    public static final Type<AnemoDoubleJumpPayload> TYPE =
            new Type<>(TeyvatBossesConstants.id("anemo_double_jump"));

    public static final StreamCodec<ByteBuf, AnemoDoubleJumpPayload> STREAM_CODEC =
            StreamCodec.unit(new AnemoDoubleJumpPayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handleOnServer(AnemoDoubleJumpPayload payload, ServerPlayer player) {
        if (!AnemoBootsItem.isWearing(player)) return;
        if (player.onGround() || player.isInWater() || player.isInLava()
                || player.onClimbable() || player.isFallFlying()
                || player.getAbilities().flying || player.isPassenger()) {
            return;
        }
        if (!AnemoBootsItem.tryConsumeAirJump(player.getUUID())) {
            return;
        }

        player.fallDistance = 0.0F;
        Vec3 v = player.getDeltaMovement();
        player.setDeltaMovement(v.x, Math.max(v.y, 0.56D), v.z);

        ServerLevel sl = player.serverLevel();
        sl.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.BREEZE_JUMP, SoundSource.PLAYERS, 0.65F, 1.25F);

        double baseY = player.getY() + 0.1D;
        for (int i = 0; i < 12; i++) {
            double ang = (Math.PI * 2.0D * i) / 12.0D;
            double ox = Math.cos(ang) * 0.55D;
            double oz = Math.sin(ang) * 0.55D;
            sl.sendParticles(ParticleTypes.CLOUD,
                    player.getX() + ox * 0.4D, baseY, player.getZ() + oz * 0.4D,
                    1, ox * 0.08D, 0.01D, oz * 0.08D, 0.04D);
        }
        sl.sendParticles(ParticleTypes.END_ROD,
                player.getX(), baseY, player.getZ(),
                4, 0.25D, 0.05D, 0.25D, 0.03D);

        if (player.getRandom().nextFloat() < 0.25F) {
            player.getItemBySlot(EquipmentSlot.FEET).hurtAndBreak(1, player, EquipmentSlot.FEET);
        }
    }
}

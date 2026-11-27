package com.velorise.teyvatbosses.client;

import com.velorise.teyvatbosses.item.AnemoBootsItem;
import com.velorise.teyvatbosses.network.AnemoDoubleJumpPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.phys.Vec3;

import java.util.function.Consumer;

/**
 * Client-side controller detecting mid-air jump presses while wearing {@link AnemoBootsItem}
 * and applying an immediate responsive wind boost before notifying the server.
 */
public final class AnemoBootsClientController {

    private static Consumer<CustomPacketPayload> packetSender;
    private static boolean wasJumpDown = false;
    private static int airJumpsRemaining = 1;
    private static int airborneTicks = 0;

    private AnemoBootsClientController() {}

    public static void setPacketSender(Consumer<CustomPacketPayload> sender) {
        packetSender = sender;
    }

    public static void tick(Minecraft mc) {
        if (mc.player == null || mc.level == null) {
            wasJumpDown = false;
            airJumpsRemaining = 1;
            airborneTicks = 0;
            return;
        }

        LocalPlayer player = mc.player;
        boolean jumpDown = mc.options.keyJump.isDown();
        if (player.onGround() || player.isInWater() || player.isInLava()
                || player.onClimbable() || player.getAbilities().flying || player.isPassenger()) {
            airJumpsRemaining = 1;
            airborneTicks = 0;
            wasJumpDown = jumpDown;
            return;
        }

        airborneTicks++;
        if (jumpDown && !wasJumpDown && airborneTicks >= 2 && airJumpsRemaining > 0
                && !player.isFallFlying() && AnemoBootsItem.isWearing(player) && mc.screen == null) {
            airJumpsRemaining--;
            Vec3 v = player.getDeltaMovement();
            player.setDeltaMovement(v.x * 1.12D, 0.56D, v.z * 1.12D);
            player.hasImpulse = true;
            player.fallDistance = 0.0F;

            double baseY = player.getY() + 0.1D;
            for (int i = 0; i < 10; i++) {
                double ang = (Math.PI * 2.0D * i) / 10.0D;
                mc.level.addParticle(ParticleTypes.CLOUD,
                        player.getX() + Math.cos(ang) * 0.35D, baseY, player.getZ() + Math.sin(ang) * 0.35D,
                        Math.cos(ang) * 0.06D, 0.01D, Math.sin(ang) * 0.06D);
            }

            if (packetSender != null) {
                packetSender.accept(new AnemoDoubleJumpPayload());
            }
        }
        wasJumpDown = jumpDown;
    }
}

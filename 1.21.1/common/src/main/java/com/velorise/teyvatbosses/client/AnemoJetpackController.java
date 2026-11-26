package com.velorise.teyvatbosses.client;

import com.velorise.teyvatbosses.item.AnemoJetpackItem;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

/**
 * Client-side input tracker for the Anemo Jet-Wing Jetpack:
 * holding Space while airborne (and not riding a Ruin Mech or flying in Creative) engages Anemo thrusters.
 */
public final class AnemoJetpackController {
    private static boolean lastBoosting = false;

    private AnemoJetpackController() {}

    public static void clientTick(Consumer<Boolean> boostSender) {
        Minecraft mc = Minecraft.getInstance();
        boolean shouldBoost = false;
        if (mc.player != null && mc.screen == null && !mc.player.isPassenger()
                && !mc.player.getAbilities().flying && !mc.player.onGround()) {
            ItemStack chest = mc.player.getItemBySlot(EquipmentSlot.CHEST);
            if (chest.getItem() instanceof AnemoJetpackItem) {
                int remaining = AnemoJetpackItem.MAX_PRESSURE - chest.getDamageValue();
                if (remaining > 1 && mc.options.keyJump.isDown()) {
                    shouldBoost = true;
                }
            }
        }
        if (mc.player != null) {
            AnemoJetpackItem.setBoosting(mc.player.getUUID(), shouldBoost);
        }
        if (shouldBoost != lastBoosting) {
            lastBoosting = shouldBoost;
            boostSender.accept(shouldBoost);
        }
    }
}

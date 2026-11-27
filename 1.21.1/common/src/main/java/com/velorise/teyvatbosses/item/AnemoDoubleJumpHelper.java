package com.velorise.teyvatbosses.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemLore;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Shared helper for boots infused with two {@link AnemoPrismCubeItem}s in a Crafting Table,
 * granting a mid-air double jump on any vanilla or modded boots via {@link DataComponents#CUSTOM_DATA}.
 */
public final class AnemoDoubleJumpHelper {

    public static final String TAG_ANEMO_DOUBLE_JUMP = "AnemoDoubleJump";
    private static final Set<UUID> USED_AIR_JUMP = ConcurrentHashMap.newKeySet();

    private AnemoDoubleJumpHelper() {}

    public static boolean hasAnemoDoubleJump(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        CustomData cd = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        return cd.copyTag().getBoolean(TAG_ANEMO_DOUBLE_JUMP);
    }

    public static boolean isWearingInfusedBoots(LivingEntity entity) {
        return entity != null && hasAnemoDoubleJump(entity.getItemBySlot(EquipmentSlot.FEET));
    }

    public static ItemStack infuseBoots(ItemStack boots) {
        ItemStack out = boots.copyWithCount(1);
        CustomData.update(DataComponents.CUSTOM_DATA, out, tag -> tag.putBoolean(TAG_ANEMO_DOUBLE_JUMP, true));
        ItemLore currentLore = out.getOrDefault(DataComponents.LORE, ItemLore.EMPTY);
        Component line = Component.translatable("tooltip.velorise_teyvat_bosses.anemo_double_jump")
                .withStyle(style -> style.withColor(ChatFormatting.AQUA).withItalic(false));
        out.set(DataComponents.LORE, currentLore.withLineAdded(line));
        return out;
    }

    public static void resetAirJump(UUID playerId) {
        USED_AIR_JUMP.remove(playerId);
    }

    public static boolean tryConsumeAirJump(UUID playerId) {
        return USED_AIR_JUMP.add(playerId);
    }

    public static void tickServerPlayer(ServerPlayer player) {
        if (player.onGround() || player.isInWater() || player.isInLava()
                || player.onClimbable() || player.getAbilities().flying) {
            resetAirJump(player.getUUID());
        }
    }
}

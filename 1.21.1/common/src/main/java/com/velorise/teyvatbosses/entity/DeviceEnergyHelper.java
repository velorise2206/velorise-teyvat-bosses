package com.velorise.teyvatbosses.entity;

import com.velorise.teyvatbosses.TeyvatBossesConstants;
import com.velorise.teyvatbosses.registry.TeyvatBossesItems;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;

import java.util.Optional;

/**
 * Unified Redstone-only Energy & Core Enchantment helper shared by Ruin Mech, Ruin Submersible, and Geo Driller:
 * <ul>
 *   <li>All 3 devices start at {@code 0} energy with base capacity {@code 900}.</li>
 *   <li>Redstone-only fuel values: Redstone Block = {@code 100},
 *       Condensed Redstone Block (9x Block) = {@code 900}.</li>
 *   <li>{@code core_capacity} (Lv I–III): {@code +900} max energy per level (up to {@code 3600}).</li>
 *   <li>{@code energy_conservation} (Lv I–III): {@code -15% / -30% / -45%} energy consumption.</li>
 * </ul>
 */
public final class DeviceEnergyHelper {
    public static final float BASE_MAX_ENERGY = 900.0F;
    public static final float CAPACITY_BONUS_PER_LEVEL = 900.0F;
    public static final float CONSERVATION_REDUCTION_PER_LEVEL = 0.15F;

    public static final float ENERGY_REDSTONE_BLOCK = 100.0F;
    public static final float ENERGY_CONDENSED_REDSTONE_BLOCK = 900.0F;

    public static final ResourceKey<Enchantment> ENCH_CORE_CAPACITY =
            ResourceKey.create(Registries.ENCHANTMENT, TeyvatBossesConstants.id("core_capacity"));
    public static final ResourceKey<Enchantment> ENCH_ENERGY_CONSERVATION =
            ResourceKey.create(Registries.ENCHANTMENT, TeyvatBossesConstants.id("energy_conservation"));

    private DeviceEnergyHelper() {}

    public static float maxEnergyForLevel(int capacityLevel) {
        return BASE_MAX_ENERGY + Mth.clamp(capacityLevel, 0, 5) * CAPACITY_BONUS_PER_LEVEL;
    }

    public static int maxIntFuelForLevel(int capacityLevel) {
        return Math.round(maxEnergyForLevel(capacityLevel));
    }

    /** Scales energy cost down by -15% per conservation level (min 25% cost). */
    public static float scaleCost(float rawCost, int conservationLevel) {
        if (rawCost <= 0.0F) return 0.0F;
        float mult = Math.max(0.25F, 1.0F - Mth.clamp(conservationLevel, 0, 5) * CONSERVATION_REDUCTION_PER_LEVEL);
        return rawCost * mult;
    }

    /** Integer Unbreaking-style fuel cost scaling for Geo Driller using stochastic rounding for small costs. */
    public static int scaleIntCost(int rawCost, int conservationLevel, net.minecraft.util.RandomSource random) {
        if (rawCost <= 0 || conservationLevel <= 0) return rawCost;
        float exact = scaleCost(rawCost, conservationLevel);
        int base = (int) Math.floor(exact);
        float frac = exact - base;
        return base + (random.nextFloat() < frac ? 1 : 0);
    }

    public static boolean isCondensedRedstone(ItemStack stack) {
        Item condensed = TeyvatBossesItems.condensedRedstoneBlock();
        return condensed != null && stack.is(condensed);
    }

    /** Unified Redstone-only recharge value across all devices. */
    public static float getRedstoneFuelValue(ItemStack stack) {
        if (isCondensedRedstone(stack)) return ENERGY_CONDENSED_REDSTONE_BLOCK;
        if (stack.is(Items.REDSTONE_BLOCK)) return ENERGY_REDSTONE_BLOCK;
        return 0.0F;
    }

    public static int getRedstoneIntFuelValue(ItemStack stack) {
        if (isCondensedRedstone(stack)) return 900;
        if (stack.is(Items.REDSTONE_BLOCK)) return 100;
        return 0;
    }

    public static int getCapacityLevel(Level level, ItemStack stack) {
        return getEnchantLevel(level, stack, ENCH_CORE_CAPACITY);
    }

    public static int getConservationLevel(Level level, ItemStack stack) {
        return getEnchantLevel(level, stack, ENCH_ENERGY_CONSERVATION);
    }

    private static int getEnchantLevel(Level level, ItemStack stack, ResourceKey<Enchantment> key) {
        if (level == null || stack.isEmpty()) return 0;
        Optional<Holder.Reference<Enchantment>> holder = level.registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT)
                .getHolder(key);
        return holder.map(h -> EnchantmentHelper.getItemEnchantmentLevel(h, stack)).orElse(0);
    }

    /** Re-applies saved core enchantments onto the dropped core ItemStack when dismantling a device. */
    public static void applyCoreEnchants(Level level, ItemStack stack, int capacityLevel, int conservationLevel) {
        if (level == null || stack.isEmpty()) return;
        var reg = level.registryAccess().registryOrThrow(Registries.ENCHANTMENT);
        if (capacityLevel > 0) {
            reg.getHolder(ENCH_CORE_CAPACITY).ifPresent(h -> stack.enchant(h, capacityLevel));
        }
        if (conservationLevel > 0) {
            reg.getHolder(ENCH_ENERGY_CONSERVATION).ifPresent(h -> stack.enchant(h, conservationLevel));
        }
    }
}

package com.velorise.teyvatbosses.registry;

import net.minecraft.world.item.Item;

import java.util.function.Supplier;

/**
 * Loader-agnostic holder for registered boss loot & music disc items.
 */
public final class TeyvatBossesItems {
    private static Supplier<Item> cryoDisc;
    private static Supplier<Item> openEyedDisc;

    private TeyvatBossesItems() {}

    public static void bind(Supplier<Item> cryoDiscSupplier, Supplier<Item> openEyedDiscSupplier) {
        cryoDisc = cryoDiscSupplier;
        openEyedDisc = openEyedDiscSupplier;
    }

    public static Item cryoDisc() { return cryoDisc != null ? cryoDisc.get() : null; }
    public static Item openEyedDisc() { return openEyedDisc != null ? openEyedDisc.get() : null; }
}

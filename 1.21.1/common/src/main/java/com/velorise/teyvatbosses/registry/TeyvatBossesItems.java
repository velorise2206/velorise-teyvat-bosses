package com.velorise.teyvatbosses.registry;

import net.minecraft.world.item.Item;

import java.util.function.Supplier;

/**
 * Loader-agnostic holder for registered boss loot & music disc items.
 */
public final class TeyvatBossesItems {
    private static Supplier<Item> cryoDisc;
    private static Supplier<Item> openEyedDisc;
    private static Supplier<Item> anemoDisc;
    private static Supplier<Item> ruinGuardDisc;

    private TeyvatBossesItems() {}

    public static void bind(
            Supplier<Item> cryoDiscSupplier,
            Supplier<Item> openEyedDiscSupplier,
            Supplier<Item> anemoDiscSupplier,
            Supplier<Item> ruinGuardDiscSupplier
    ) {
        cryoDisc = cryoDiscSupplier;
        openEyedDisc = openEyedDiscSupplier;
        anemoDisc = anemoDiscSupplier;
        ruinGuardDisc = ruinGuardDiscSupplier;
    }

    public static Item cryoDisc() { return cryoDisc != null ? cryoDisc.get() : null; }
    public static Item openEyedDisc() { return openEyedDisc != null ? openEyedDisc.get() : null; }
    public static Item anemoDisc() { return anemoDisc != null ? anemoDisc.get() : null; }
    public static Item ruinGuardDisc() { return ruinGuardDisc != null ? ruinGuardDisc.get() : null; }
}

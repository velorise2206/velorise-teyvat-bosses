package com.velorise.teyvatbosses.registry;

import net.minecraft.world.item.Item;

import java.util.function.Supplier;

/**
 * Loader-agnostic holder for registered boss loot, music discs, and modular vehicle/turret salvage parts.
 */
public final class TeyvatBossesItems {
    private static Supplier<Item> cryoDisc;
    private static Supplier<Item> anemoDisc;
    private static Supplier<Item> ruinGuardDisc;
    private static Supplier<Item> ruinNautilusDisc;
    private static Supplier<Item> primoGeovishapDisc;

    private static Supplier<Item> mechCore;
    private static Supplier<Item> mechLeftArm;
    private static Supplier<Item> mechRightArm;
    private static Supplier<Item> mechLeftLeg;
    private static Supplier<Item> mechRightLeg;
    private static Supplier<Item> cryoTurretRoot;
    private static Supplier<Item> cryoTurretStem;
    private static Supplier<Item> cryoTurretCorolla;
    private static Supplier<Item> anemoPrismCube;
    private static Supplier<Item> ruinSubHull;
    private static Supplier<Item> ruinSubPropeller;
    private static Supplier<Item> ruinSubTorpedoPod;
    private static Supplier<Item> geoDrillerChassis;
    private static Supplier<Item> geoDrillerDrill;
    private static Supplier<Item> geoDrillerEngine;
    private static Supplier<Item> condensedRedstoneBlock;
    private static Supplier<Item> primordialJade;
    private static Supplier<Item> primordialGeoIngot;
    private static Supplier<Item> geovishapHelmet;
    private static Supplier<Item> geovishapChestplate;
    private static Supplier<Item> geovishapLeggings;
    private static Supplier<Item> geovishapBoots;

    private TeyvatBossesItems() {}

    public static void bind(
            Supplier<Item> cryoDiscSupplier,
            Supplier<Item> anemoDiscSupplier,
            Supplier<Item> ruinGuardDiscSupplier,
            Supplier<Item> ruinNautilusDiscSupplier,
            Supplier<Item> primoGeovishapDiscSupplier,
            Supplier<Item> mechCoreSupplier,
            Supplier<Item> mechLeftArmSupplier,
            Supplier<Item> mechRightArmSupplier,
            Supplier<Item> mechLeftLegSupplier,
            Supplier<Item> mechRightLegSupplier,
            Supplier<Item> cryoTurretRootSupplier,
            Supplier<Item> cryoTurretStemSupplier,
            Supplier<Item> cryoTurretCorollaSupplier,
            Supplier<Item> anemoPrismCubeSupplier,
            Supplier<Item> ruinSubHullSupplier,
            Supplier<Item> ruinSubPropellerSupplier,
            Supplier<Item> ruinSubTorpedoPodSupplier,
            Supplier<Item> geoDrillerChassisSupplier,
            Supplier<Item> geoDrillerDrillSupplier,
            Supplier<Item> geoDrillerEngineSupplier
    ) {
        cryoDisc = cryoDiscSupplier;
        anemoDisc = anemoDiscSupplier;
        ruinGuardDisc = ruinGuardDiscSupplier;
        ruinNautilusDisc = ruinNautilusDiscSupplier;
        primoGeovishapDisc = primoGeovishapDiscSupplier;
        mechCore = mechCoreSupplier;
        mechLeftArm = mechLeftArmSupplier;
        mechRightArm = mechRightArmSupplier;
        mechLeftLeg = mechLeftLegSupplier;
        mechRightLeg = mechRightLegSupplier;
        cryoTurretRoot = cryoTurretRootSupplier;
        cryoTurretStem = cryoTurretStemSupplier;
        cryoTurretCorolla = cryoTurretCorollaSupplier;
        anemoPrismCube = anemoPrismCubeSupplier;
        ruinSubHull = ruinSubHullSupplier;
        ruinSubPropeller = ruinSubPropellerSupplier;
        ruinSubTorpedoPod = ruinSubTorpedoPodSupplier;
        geoDrillerChassis = geoDrillerChassisSupplier;
        geoDrillerDrill = geoDrillerDrillSupplier;
        geoDrillerEngine = geoDrillerEngineSupplier;
    }

    public static Item cryoDisc() { return cryoDisc != null ? cryoDisc.get() : null; }
    public static Item anemoDisc() { return anemoDisc != null ? anemoDisc.get() : null; }
    public static Item ruinGuardDisc() { return ruinGuardDisc != null ? ruinGuardDisc.get() : null; }
    public static Item ruinNautilusDisc() { return ruinNautilusDisc != null ? ruinNautilusDisc.get() : null; }
    public static Item primoGeovishapDisc() { return primoGeovishapDisc != null ? primoGeovishapDisc.get() : null; }

    public static Item mechCore() { return mechCore != null ? mechCore.get() : null; }
    public static Item mechLeftArm() { return mechLeftArm != null ? mechLeftArm.get() : null; }
    public static Item mechRightArm() { return mechRightArm != null ? mechRightArm.get() : null; }
    public static Item mechLeftLeg() { return mechLeftLeg != null ? mechLeftLeg.get() : null; }
    public static Item mechRightLeg() { return mechRightLeg != null ? mechRightLeg.get() : null; }
    public static Item cryoTurretRoot() { return cryoTurretRoot != null ? cryoTurretRoot.get() : null; }
    public static Item cryoTurretStem() { return cryoTurretStem != null ? cryoTurretStem.get() : null; }
    public static Item cryoTurretCorolla() { return cryoTurretCorolla != null ? cryoTurretCorolla.get() : null; }
    public static Item anemoPrismCube() { return anemoPrismCube != null ? anemoPrismCube.get() : null; }
    public static Item ruinSubHull() { return ruinSubHull != null ? ruinSubHull.get() : null; }
    public static Item ruinSubPropeller() { return ruinSubPropeller != null ? ruinSubPropeller.get() : null; }
    public static Item ruinSubTorpedoPod() { return ruinSubTorpedoPod != null ? ruinSubTorpedoPod.get() : null; }
    public static Item geoDrillerChassis() { return geoDrillerChassis != null ? geoDrillerChassis.get() : null; }
    public static Item geoDrillerDrill() { return geoDrillerDrill != null ? geoDrillerDrill.get() : null; }
    public static Item geoDrillerEngine() { return geoDrillerEngine != null ? geoDrillerEngine.get() : null; }

    public static Item[] mechPartPool() { return new Item[]{mechCore(), mechLeftArm(), mechRightArm(), mechLeftLeg(), mechRightLeg()}; }
    public static Item[] subPartPool() { return new Item[]{ruinSubHull(), ruinSubPropeller(), ruinSubTorpedoPod()}; }
    public static Item[] cryoTurretPartPool() { return new Item[]{cryoTurretRoot(), cryoTurretStem(), cryoTurretCorolla()}; }
    public static Item[] geoDrillerPartPool() { return new Item[]{geoDrillerChassis(), geoDrillerDrill(), geoDrillerEngine()}; }
    public static void bindCondensedRedstone(Supplier<Item> supplier) { condensedRedstoneBlock = supplier; }
    public static Item condensedRedstoneBlock() { return condensedRedstoneBlock != null ? condensedRedstoneBlock.get() : null; }

    public static void bindGeovishapGear(
            Supplier<Item> jadeSupplier,
            Supplier<Item> ingotSupplier,
            Supplier<Item> helmetSupplier,
            Supplier<Item> chestSupplier,
            Supplier<Item> legsSupplier,
            Supplier<Item> bootsSupplier
    ) {
        primordialJade = jadeSupplier;
        primordialGeoIngot = ingotSupplier;
        geovishapHelmet = helmetSupplier;
        geovishapChestplate = chestSupplier;
        geovishapLeggings = legsSupplier;
        geovishapBoots = bootsSupplier;
    }

    public static Item primordialJade() { return primordialJade != null ? primordialJade.get() : null; }
    public static Item primordialGeoIngot() { return primordialGeoIngot != null ? primordialGeoIngot.get() : null; }
    public static Item geovishapHelmet() { return geovishapHelmet != null ? geovishapHelmet.get() : null; }
    public static Item geovishapChestplate() { return geovishapChestplate != null ? geovishapChestplate.get() : null; }
    public static Item geovishapLeggings() { return geovishapLeggings != null ? geovishapLeggings.get() : null; }
    public static Item geovishapBoots() { return geovishapBoots != null ? geovishapBoots.get() : null; }
}

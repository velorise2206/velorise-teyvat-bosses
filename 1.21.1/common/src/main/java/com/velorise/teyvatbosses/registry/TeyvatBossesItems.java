package com.velorise.teyvatbosses.registry;

import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

/**
 * Loader-agnostic holder for registered boss loot, music discs, and Ruin Mech salvage parts.
 */
public final class TeyvatBossesItems {
    private static Supplier<Item> cryoDisc;
    private static Supplier<Item> openEyedDisc;
    private static Supplier<Item> anemoDisc;
    private static Supplier<Item> ruinGuardDisc;
    private static Supplier<Item> astralionDisc;

    private static Supplier<Item> mechCore;
    private static Supplier<Item> mechLeftArm;
    private static Supplier<Item> mechRightArm;
    private static Supplier<Item> mechLeftLeg;
    private static Supplier<Item> mechRightLeg;
    private static Supplier<Item> cryoTurretRoot;
    private static Supplier<Item> cryoTurretStem;
    private static Supplier<Item> cryoTurretCorolla;
    private static Supplier<Item> anemoPrismCube;
    private static Supplier<Item> anemoJetpack;

    private TeyvatBossesItems() {}

    public static void bind(
            Supplier<Item> cryoDiscSupplier,
            Supplier<Item> openEyedDiscSupplier,
            Supplier<Item> anemoDiscSupplier,
            Supplier<Item> ruinGuardDiscSupplier,
            Supplier<Item> astralionDiscSupplier,
            Supplier<Item> mechCoreSupplier,
            Supplier<Item> mechLeftArmSupplier,
            Supplier<Item> mechRightArmSupplier,
            Supplier<Item> mechLeftLegSupplier,
            Supplier<Item> mechRightLegSupplier,
            Supplier<Item> cryoTurretRootSupplier,
            Supplier<Item> cryoTurretStemSupplier,
            Supplier<Item> cryoTurretCorollaSupplier,
            Supplier<Item> anemoPrismCubeSupplier,
            Supplier<Item> anemoJetpackSupplier
    ) {
        cryoDisc = cryoDiscSupplier;
        openEyedDisc = openEyedDiscSupplier;
        anemoDisc = anemoDiscSupplier;
        ruinGuardDisc = ruinGuardDiscSupplier;
        astralionDisc = astralionDiscSupplier;
        mechCore = mechCoreSupplier;
        mechLeftArm = mechLeftArmSupplier;
        mechRightArm = mechRightArmSupplier;
        mechLeftLeg = mechLeftLegSupplier;
        mechRightLeg = mechRightLegSupplier;
        cryoTurretRoot = cryoTurretRootSupplier;
        cryoTurretStem = cryoTurretStemSupplier;
        cryoTurretCorolla = cryoTurretCorollaSupplier;
        anemoPrismCube = anemoPrismCubeSupplier;
        anemoJetpack = anemoJetpackSupplier;
    }

    public static Item cryoDisc() { return cryoDisc != null ? cryoDisc.get() : null; }
    public static Item openEyedDisc() { return openEyedDisc != null ? openEyedDisc.get() : null; }
    public static Item anemoDisc() { return anemoDisc != null ? anemoDisc.get() : null; }
    public static Item ruinGuardDisc() { return ruinGuardDisc != null ? ruinGuardDisc.get() : null; }
    public static Item astralionDisc() { return astralionDisc != null ? astralionDisc.get() : null; }

    public static Item mechCore() { return mechCore != null ? mechCore.get() : null; }
    public static Item mechLeftArm() { return mechLeftArm != null ? mechLeftArm.get() : null; }
    public static Item mechRightArm() { return mechRightArm != null ? mechRightArm.get() : null; }
    public static Item mechLeftLeg() { return mechLeftLeg != null ? mechLeftLeg.get() : null; }
    public static Item mechRightLeg() { return mechRightLeg != null ? mechRightLeg.get() : null; }
    public static Item cryoTurretRoot() { return cryoTurretRoot != null ? cryoTurretRoot.get() : null; }
    public static Item cryoTurretStem() { return cryoTurretStem != null ? cryoTurretStem.get() : null; }
    public static Item cryoTurretCorolla() { return cryoTurretCorolla != null ? cryoTurretCorolla.get() : null; }
    public static Item anemoPrismCube() { return anemoPrismCube != null ? anemoPrismCube.get() : null; }
    public static Item anemoJetpack() { return anemoJetpack != null ? anemoJetpack.get() : null; }

    public static Item randomCryoTurretPart(RandomSource random) {
        return switch (random.nextInt(3)) {
            case 0 -> cryoTurretRoot();
            case 1 -> cryoTurretStem();
            default -> cryoTurretCorolla();
        };
    }

    public static Item randomMechPart(RandomSource random) {
        return switch (random.nextInt(5)) {
            case 0 -> mechCore();
            case 1 -> mechLeftArm();
            case 2 -> mechRightArm();
            case 3 -> mechLeftLeg();
            default -> mechRightLeg();
        };
    }
}

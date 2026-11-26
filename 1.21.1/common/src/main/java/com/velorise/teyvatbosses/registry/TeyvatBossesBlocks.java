package com.velorise.teyvatbosses.registry;

import com.velorise.teyvatbosses.block.BossTrophyBlock;
import com.velorise.teyvatbosses.block.BossTrophyBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Supplier;

/**
 * Loader-agnostic holder for registered Boss Trophy blocks and block entities.
 */
public final class TeyvatBossesBlocks {
    private static Supplier<BossTrophyBlock> cryoTrophy;
    private static Supplier<BossTrophyBlock> openEyedTrophy;
    private static Supplier<BossTrophyBlock> anemoTrophy;
    private static Supplier<BossTrophyBlock> ruinGuardTrophy;
    private static Supplier<BossTrophyBlock> astralionTrophy;
    private static Supplier<BossTrophyBlock> ruinNautilusTrophy;
    private static Supplier<BlockEntityType<BossTrophyBlockEntity>> bossTrophyBe;

    private TeyvatBossesBlocks() {}

    public static void bind(
            Supplier<BossTrophyBlock> cryoTrophySupplier,
            Supplier<BossTrophyBlock> openEyedTrophySupplier,
            Supplier<BossTrophyBlock> anemoTrophySupplier,
            Supplier<BossTrophyBlock> ruinGuardTrophySupplier,
            Supplier<BossTrophyBlock> astralionTrophySupplier,
            Supplier<BossTrophyBlock> ruinNautilusTrophySupplier,
            Supplier<BlockEntityType<BossTrophyBlockEntity>> beSupplier
    ) {
        cryoTrophy = cryoTrophySupplier;
        openEyedTrophy = openEyedTrophySupplier;
        anemoTrophy = anemoTrophySupplier;
        ruinGuardTrophy = ruinGuardTrophySupplier;
        astralionTrophy = astralionTrophySupplier;
        ruinNautilusTrophy = ruinNautilusTrophySupplier;
        bossTrophyBe = beSupplier;
    }

    public static BossTrophyBlock cryoTrophy() { return cryoTrophy.get(); }
    public static BossTrophyBlock openEyedTrophy() { return openEyedTrophy.get(); }
    public static BossTrophyBlock anemoTrophy() { return anemoTrophy.get(); }
    public static BossTrophyBlock ruinGuardTrophy() { return ruinGuardTrophy.get(); }
    public static BossTrophyBlock astralionTrophy() { return astralionTrophy.get(); }
    public static BossTrophyBlock ruinNautilusTrophy() { return ruinNautilusTrophy.get(); }
    public static BlockEntityType<BossTrophyBlockEntity> bossTrophyBe() { return bossTrophyBe.get(); }

    public static BossTrophyBlock trophyForBossKind(int bossKind) {
        return switch (bossKind) {
            case 0 -> cryoTrophy();
            case 1 -> openEyedTrophy();
            case 2 -> anemoTrophy();
            case 3 -> ruinGuardTrophy();
            case 4 -> astralionTrophy();
            case 5 -> ruinNautilusTrophy();
            default -> ruinGuardTrophy();
        };
    }
}

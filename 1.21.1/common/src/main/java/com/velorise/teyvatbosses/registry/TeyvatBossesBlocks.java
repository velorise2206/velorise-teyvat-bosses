package com.velorise.teyvatbosses.registry;

import com.velorise.teyvatbosses.block.BossTrophyBlock;
import com.velorise.teyvatbosses.block.BossTrophyBlockEntity;
import com.velorise.teyvatbosses.entity.BossRespawnAnchorEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Supplier;

/**
 * Loader-agnostic holder for registered Boss Trophy blocks and block entities.
 */
public final class TeyvatBossesBlocks {
    private static Supplier<BossTrophyBlock> cryoTrophy;
    private static Supplier<BossTrophyBlock> anemoTrophy;
    private static Supplier<BossTrophyBlock> ruinGuardTrophy;
    private static Supplier<BossTrophyBlock> ruinNautilusTrophy;
    private static Supplier<BossTrophyBlock> primoGeovishapTrophy;
    private static Supplier<BlockEntityType<BossTrophyBlockEntity>> bossTrophyBe;

    private TeyvatBossesBlocks() {}

    public static void bind(
            Supplier<BossTrophyBlock> cryoTrophySupplier,
            Supplier<BossTrophyBlock> anemoTrophySupplier,
            Supplier<BossTrophyBlock> ruinGuardTrophySupplier,
            Supplier<BossTrophyBlock> ruinNautilusTrophySupplier,
            Supplier<BossTrophyBlock> primoGeovishapTrophySupplier,
            Supplier<BlockEntityType<BossTrophyBlockEntity>> beSupplier
    ) {
        cryoTrophy = cryoTrophySupplier;
        anemoTrophy = anemoTrophySupplier;
        ruinGuardTrophy = ruinGuardTrophySupplier;
        ruinNautilusTrophy = ruinNautilusTrophySupplier;
        primoGeovishapTrophy = primoGeovishapTrophySupplier;
        bossTrophyBe = beSupplier;
    }

    public static BossTrophyBlock cryoTrophy() { return cryoTrophy.get(); }
    public static BossTrophyBlock anemoTrophy() { return anemoTrophy.get(); }
    public static BossTrophyBlock ruinGuardTrophy() { return ruinGuardTrophy.get(); }
    public static BossTrophyBlock ruinNautilusTrophy() { return ruinNautilusTrophy.get(); }
    public static BossTrophyBlock primoGeovishapTrophy() { return primoGeovishapTrophy.get(); }
    public static BlockEntityType<BossTrophyBlockEntity> bossTrophyBe() { return bossTrophyBe.get(); }

    public static BossTrophyBlock trophyForBossKind(int bossKind) {
        return switch (bossKind) {
            case BossRespawnAnchorEntity.BOSS_CRYO_REGISVINE -> cryoTrophy();
            case BossRespawnAnchorEntity.BOSS_ANEMO_HYPOSTASIS -> anemoTrophy();
            case BossRespawnAnchorEntity.BOSS_RUIN_GUARD -> ruinGuardTrophy();
            case BossRespawnAnchorEntity.BOSS_RUIN_NAUTILUS -> ruinNautilusTrophy();
            case BossRespawnAnchorEntity.BOSS_PRIMO_GEOVISHAP -> primoGeovishapTrophy();
            default -> ruinGuardTrophy();
        };
    }
}

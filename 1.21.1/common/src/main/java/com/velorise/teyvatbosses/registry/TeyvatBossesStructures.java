package com.velorise.teyvatbosses.registry;

import com.velorise.teyvatbosses.worldgen.FlatArenaStructure;
import net.minecraft.world.level.levelgen.structure.StructureType;

import java.util.function.Supplier;

/**
 * Loader-agnostic holder for custom worldgen structure types.
 */
public final class TeyvatBossesStructures {
    private static Supplier<StructureType<FlatArenaStructure>> flatArena;

    private TeyvatBossesStructures() {}

    public static void bind(Supplier<StructureType<FlatArenaStructure>> flatArenaSupplier) {
        flatArena = flatArenaSupplier;
    }

    public static StructureType<FlatArenaStructure> flatArena() {
        return flatArena.get();
    }
}

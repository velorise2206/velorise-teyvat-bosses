package com.velorise.teyvat.worldgen;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;

/**
 * Canonical ResourceKeys for {@code veloriseteyvat:teyvat} at default 1:1 scale (4096x4096 Mondstadt).
 * All world coordinates scale automatically by the active world-save {@code worldScale}.
 */
public final class TeyvatDimensions {
    public static final String MOD_ID = "velorise_teyvat";
    public static final String NAMESPACE = "veloriseteyvat";

    public static final ResourceLocation TEYVAT_ID = id("teyvat");
    public static final ResourceLocation MACRO_MAP_LOCATION = id("worldgen/teyvat_macro.bin");
    public static final ResourceLocation LANDMARKS_LOCATION = id("worldgen/mondstadt_landmarks.json");

    public static final ResourceKey<Level> TEYVAT_LEVEL =
            ResourceKey.create(Registries.DIMENSION, TEYVAT_ID);
    public static final ResourceKey<DimensionType> TEYVAT_TYPE =
            ResourceKey.create(Registries.DIMENSION_TYPE, TEYVAT_ID);
    public static final ResourceKey<NoiseGeneratorSettings> TEYVAT_NOISE =
            ResourceKey.create(Registries.NOISE_SETTINGS, id("teyvat_noise"));

    // Mondstadt v1.0 + Border Biomes
    public static final ResourceKey<Biome> BIOME_STARFELL_VALLEY = biome("starfell_valley");
    public static final ResourceKey<Biome> BIOME_CIDER_LAKE = biome("cider_lake");
    public static final ResourceKey<Biome> BIOME_WINDRISE = biome("windrise");
    public static final ResourceKey<Biome> BIOME_WOLVENDOM = biome("wolvendom");
    public static final ResourceKey<Biome> BIOME_STORMTERRORS_LAIR = biome("stormterrors_lair");
    public static final ResourceKey<Biome> BIOME_DRAGONSPINE = biome("dragonspine");
    public static final ResourceKey<Biome> BIOME_DARK_SEA = biome("dark_sea");

    // Canon Starfell Beach Spawn at 1:1 (BLOCKS_PER_PIXEL = 8, WORLD_MIN = -2048)
    public static final int CANON_SPAWN_X = 922;
    public static final int CANON_SPAWN_Y = 65;
    public static final int CANON_SPAWN_Z = -90;

    // Mondstadt City Island center at 1:1
    public static final double MONDSTADT_CENTER_X = -32.0;
    public static final double MONDSTADT_CENTER_Z = 376.0;

    private TeyvatDimensions() {}

    public static int scaledSpawnX(double worldScale) {
        return (int) Math.round(CANON_SPAWN_X * worldScale);
    }

    public static int scaledSpawnZ(double worldScale) {
        return (int) Math.round(CANON_SPAWN_Z * worldScale);
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(NAMESPACE, path);
    }

    private static ResourceKey<Biome> biome(String path) {
        return ResourceKey.create(Registries.BIOME, id(path));
    }
}

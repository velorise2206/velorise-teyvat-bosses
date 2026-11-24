package com.velorise.teyvat.worldgen;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.velorise.teyvat.worldgen.region.RegionMacroMap;
import com.velorise.teyvat.worldgen.region.TeyvatRegion;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;

import java.util.stream.Stream;

/**
 * Deterministic 3D {@link BiomeSource} for {@code veloriseteyvat:teyvat}.
 * Reads {@link RegionMacroMap} (region + sub-biome channel G) and vertical height {@code y}
 * so Mondstadt, Dragonspine, Cider Lake, Wolvendom, Stormterror's Lair, and Dark Sea
 * align 1:1 with the extracted Teyvat map.
 */
public final class TeyvatBiomeSource extends BiomeSource {
    // Sub-biome IDs matching tools/build_teyvat_macro.py
    private static final int SUB_STARFELL = 0;
    private static final int SUB_CIDER_LAKE = 1;
    private static final int SUB_WINDRISE = 2;
    private static final int SUB_WOLVENDOM = 3;
    private static final int SUB_STORMTERROR = 4;

    public static final MapCodec<TeyvatBiomeSource> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    RegistryOps.retrieveElement(TeyvatDimensions.BIOME_STARFELL_VALLEY),
                    RegistryOps.retrieveElement(TeyvatDimensions.BIOME_CIDER_LAKE),
                    RegistryOps.retrieveElement(TeyvatDimensions.BIOME_WINDRISE),
                    RegistryOps.retrieveElement(TeyvatDimensions.BIOME_WOLVENDOM),
                    RegistryOps.retrieveElement(TeyvatDimensions.BIOME_STORMTERRORS_LAIR),
                    RegistryOps.retrieveElement(TeyvatDimensions.BIOME_DRAGONSPINE),
                    RegistryOps.retrieveElement(TeyvatDimensions.BIOME_DARK_SEA)
            ).apply(instance, TeyvatBiomeSource::new)
    );

    private final Holder<Biome> starfellValley;
    private final Holder<Biome> ciderLake;
    private final Holder<Biome> windrise;
    private final Holder<Biome> wolvendom;
    private final Holder<Biome> stormterrorsLair;
    private final Holder<Biome> dragonspine;
    private final Holder<Biome> darkSea;

    public TeyvatBiomeSource(
            Holder<Biome> starfellValley,
            Holder<Biome> ciderLake,
            Holder<Biome> windrise,
            Holder<Biome> wolvendom,
            Holder<Biome> stormterrorsLair,
            Holder<Biome> dragonspine,
            Holder<Biome> darkSea
    ) {
        this.starfellValley = starfellValley;
        this.ciderLake = ciderLake;
        this.windrise = windrise;
        this.wolvendom = wolvendom;
        this.stormterrorsLair = stormterrorsLair;
        this.dragonspine = dragonspine;
        this.darkSea = darkSea;
    }

    @Override
    protected MapCodec<? extends BiomeSource> codec() {
        return CODEC;
    }

    @Override
    protected Stream<Holder<Biome>> collectPossibleBiomes() {
        return Stream.of(
                starfellValley,
                ciderLake,
                windrise,
                wolvendom,
                stormterrorsLair,
                dragonspine,
                darkSea
        );
    }

    @Override
    public Holder<Biome> getNoiseBiome(int quartX, int quartY, int quartZ, Climate.Sampler sampler) {
        int blockX = QuartPos.toBlock(quartX);
        int blockZ = QuartPos.toBlock(quartZ);

        RegionMacroMap map = RegionMacroMap.getInstance();
        TeyvatRegion region = map.sampleRegion(blockX, blockZ);

        return switch (region) {
            case DARK_SEA -> darkSea;
            case DRAGONSPINE -> dragonspine;
            case MONDSTADT -> {
                int sub = map.sampleSubBiomeId(blockX, blockZ);
                yield switch (sub) {
                    case SUB_CIDER_LAKE -> ciderLake;
                    case SUB_WINDRISE -> windrise;
                    case SUB_WOLVENDOM -> wolvendom;
                    case SUB_STORMTERROR -> stormterrorsLair;
                    default -> starfellValley;
                };
            }
            // Locked future nations fall back cleanly until v1.1+ nation biomes are added
            default -> starfellValley;
        };
    }
}

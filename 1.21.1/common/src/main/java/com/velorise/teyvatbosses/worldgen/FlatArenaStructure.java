package com.velorise.teyvatbosses.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.velorise.teyvatbosses.registry.TeyvatBossesStructures;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasLookup;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;

import java.util.Optional;

/**
 * Terrain-aware Boss Arena Structure that validates a circular flat footprint before spawning.
 *
 * <p>Samples 17 terrain points (center + 8-point inner ring + 8-point outer ring across {@code sample_radius}):
 * <ul>
 *   <li>Rejects water/rivers/oceans (where {@code WORLD_SURFACE_WG > OCEAN_FLOOR_WG}) and slopes exceeding {@code max_height_variance}.</li>
 *   <li>Anchors placement to {@code minY + y_offset} so surface arenas never expose floating undersides,
 *       and underground caves (Cryo Regisvine) remain 100% submerged beneath the flat terrain with zero exposed outer shell.</li>
 * </ul>
 */
public class FlatArenaStructure extends Structure {

    public static final MapCodec<FlatArenaStructure> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    settingsCodec(instance),
                    StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter(s -> s.startPool),
                    Codec.intRange(4, 32).fieldOf("sample_radius").forGetter(s -> s.sampleRadius),
                    Codec.intRange(1, 16).fieldOf("max_height_variance").forGetter(s -> s.maxHeightVariance),
                    Codec.intRange(-64, 16).fieldOf("y_offset").forGetter(s -> s.yOffset),
                    Codec.intRange(-64, 256).optionalFieldOf("min_surface_y", 62).forGetter(s -> s.minSurfaceY)
            ).apply(instance, FlatArenaStructure::new)
    );

    private final Holder<StructureTemplatePool> startPool;
    private final int sampleRadius;
    private final int maxHeightVariance;
    private final int yOffset;
    private final int minSurfaceY;

    public FlatArenaStructure(
            StructureSettings settings,
            Holder<StructureTemplatePool> startPool,
            int sampleRadius,
            int maxHeightVariance,
            int yOffset,
            int minSurfaceY
    ) {
        super(settings);
        this.startPool = startPool;
        this.sampleRadius = sampleRadius;
        this.maxHeightVariance = maxHeightVariance;
        this.yOffset = yOffset;
        this.minSurfaceY = minSurfaceY;
    }

    @Override
    protected Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        int cx = context.chunkPos().getMiddleBlockX();
        int cz = context.chunkPos().getMiddleBlockZ();
        ChunkGenerator gen = context.chunkGenerator();

        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;

        // Sample 17 points: center + 8 points at 55% radius + 8 points at 100% radius
        int[] rings = {0, (int) Math.round(sampleRadius * 0.55D), sampleRadius};
        for (int rIdx = 0; rIdx < rings.length; rIdx++) {
            int r = rings[rIdx];
            int count = (r == 0) ? 1 : 8;
            for (int i = 0; i < count; i++) {
                double ang = (Math.PI * 2.0D * i) / count;
                int sx = cx + (int) Math.round(Math.cos(ang) * r);
                int sz = cz + (int) Math.round(Math.sin(ang) * r);

                int surfaceY = gen.getFirstOccupiedHeight(
                        sx, sz, Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor(), context.randomState());
                int floorY = gen.getFirstOccupiedHeight(
                        sx, sz, Heightmap.Types.OCEAN_FLOOR_WG, context.heightAccessor(), context.randomState());

                // Reject water bodies / rivers / coastlines
                if (surfaceY > floorY + 1 || surfaceY < minSurfaceY) {
                    return Optional.empty();
                }

                if (surfaceY < minY) minY = surfaceY;
                if (surfaceY > maxY) maxY = surfaceY;

                // Early exit if terrain slope already exceeds flatness threshold
                if (maxY - minY > maxHeightVariance) {
                    return Optional.empty();
                }
            }
        }

        // Anchor to minY (lowest perimeter elevation) + yOffset so foundation/cave shell is 100% embedded
        int placeY = minY + yOffset;
        if (placeY <= context.heightAccessor().getMinBuildHeight() + 4) {
            return Optional.empty();
        }

        BlockPos origin = new BlockPos(cx, placeY, cz);
        return JigsawPlacement.addPieces(
                context,
                this.startPool,
                Optional.empty(),
                1,
                origin,
                false,
                Optional.empty(),
                64,
                PoolAliasLookup.EMPTY,
                JigsawStructure.DEFAULT_DIMENSION_PADDING,
                JigsawStructure.DEFAULT_LIQUID_SETTINGS
        );
    }

    @Override
    public StructureType<?> type() {
        return TeyvatBossesStructures.flatArena();
    }
}

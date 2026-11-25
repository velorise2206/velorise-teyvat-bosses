package com.velorise.teyvatbosses.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.velorise.teyvatbosses.registry.TeyvatBossesStructures;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.PiecesContainer;
import net.minecraft.world.level.levelgen.structure.pools.EmptyPoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;

import java.util.Arrays;
import java.util.Optional;

/**
 * Fast, terrain-aware Boss Arena Structure with exact centered placement, fast biome-first
 * 25-point terrain validation (deferring 3D {@code getBaseColumn} checks to only 5 points after
 * all heightmap checks pass), and procedural radial terrain blending in {@link #afterPlace}.
 */
public class FlatArenaStructure extends Structure {

    public static final MapCodec<FlatArenaStructure> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    settingsCodec(instance),
                    StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter(s -> s.startPool),
                    Codec.intRange(8, 32).fieldOf("sample_radius").forGetter(s -> s.sampleRadius),
                    Codec.intRange(1, 20).fieldOf("max_height_variance").forGetter(s -> s.maxHeightVariance),
                    Codec.intRange(-64, 16).fieldOf("y_offset").forGetter(s -> s.yOffset),
                    Codec.intRange(-64, 256).optionalFieldOf("min_surface_y", 63).forGetter(s -> s.minSurfaceY),
                    Codec.intRange(4, 20).optionalFieldOf("blend_radius", 12).forGetter(s -> s.blendRadius),
                    Codec.BOOL.optionalFieldOf("underground_cave", false).forGetter(s -> s.undergroundCave),
                    Codec.intRange(0, 24).optionalFieldOf("clear_interior_radius", 0).forGetter(s -> s.clearInteriorRadius)
            ).apply(instance, FlatArenaStructure::new)
    );

    private final Holder<StructureTemplatePool> startPool;
    private final int sampleRadius;
    private final int maxHeightVariance;
    private final int yOffset;
    private final int minSurfaceY;
    private final int blendRadius;
    private final boolean undergroundCave;
    private final int clearInteriorRadius;

    public FlatArenaStructure(
            StructureSettings settings,
            Holder<StructureTemplatePool> startPool,
            int sampleRadius,
            int maxHeightVariance,
            int yOffset,
            int minSurfaceY,
            int blendRadius,
            boolean undergroundCave,
            int clearInteriorRadius
    ) {
        super(settings);
        this.startPool = startPool;
        this.sampleRadius = sampleRadius;
        this.maxHeightVariance = maxHeightVariance;
        this.yOffset = yOffset;
        this.minSurfaceY = minSurfaceY;
        this.blendRadius = blendRadius;
        this.undergroundCave = undergroundCave;
        this.clearInteriorRadius = clearInteriorRadius;
    }

    @Override
    public BoundingBox adjustBoundingBox(BoundingBox boundingBox) {
        return boundingBox.inflatedBy(this.blendRadius + 2);
    }

    @Override
    protected Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        int cx = context.chunkPos().getMiddleBlockX();
        int cz = context.chunkPos().getMiddleBlockZ();
        ChunkGenerator gen = context.chunkGenerator();
        LevelHeightAccessor heightAccessor = context.heightAccessor();
        RandomState randomState = context.randomState();
        int seaFloorLimit = Math.max(this.minSurfaceY, gen.getSeaLevel() + 1);

        // 1. Fast center check (2 heightmap queries + 1 biome check) — rejects >85% of chunks immediately
        int centerSurfaceY = gen.getFirstOccupiedHeight(
                cx, cz, Heightmap.Types.WORLD_SURFACE_WG, heightAccessor, randomState);
        int centerFloorY = gen.getFirstOccupiedHeight(
                cx, cz, Heightmap.Types.OCEAN_FLOOR_WG, heightAccessor, randomState);
        if (centerSurfaceY > centerFloorY || centerSurfaceY < seaFloorLimit) {
            return Optional.empty();
        }

        Holder<Biome> centerBiome = context.biomeSource().getNoiseBiome(
                QuartPos.fromBlock(cx), QuartPos.fromBlock(centerSurfaceY), QuartPos.fromBlock(cz),
                randomState.sampler());
        if (!context.validBiome().test(centerBiome)) {
            return Optional.empty();
        }

        // 2. Fast 25-point 2D heightmap check (center + 8 mid + 8 perimeter + 8 outer skirt) — ZERO getBaseColumn here!
        int[] innerSamples = new int[17];
        innerSamples[0] = centerSurfaceY;
        int innerIdx = 1;
        int innerMinY = centerSurfaceY;
        int innerMaxY = centerSurfaceY;
        int outerMinY = Integer.MAX_VALUE;
        int outerMaxY = Integer.MIN_VALUE;

        int midR = Math.max(4, (int) Math.round(this.sampleRadius * 0.50D));
        int perimR = this.sampleRadius + 1;
        int skirtR = this.sampleRadius + this.blendRadius;
        int[] radii = {midR, perimR, skirtR};

        for (int rIdx = 0; rIdx < radii.length; rIdx++) {
            int r = radii[rIdx];
            boolean isOuterSkirt = (rIdx == 2);
            for (int i = 0; i < 8; i++) {
                double ang = (Math.PI * 0.25D) * i;
                int sx = cx + (int) Math.round(Math.cos(ang) * r);
                int sz = cz + (int) Math.round(Math.sin(ang) * r);

                int surfaceY = gen.getFirstOccupiedHeight(
                        sx, sz, Heightmap.Types.WORLD_SURFACE_WG, heightAccessor, randomState);
                int floorY = gen.getFirstOccupiedHeight(
                        sx, sz, Heightmap.Types.OCEAN_FLOOR_WG, heightAccessor, randomState);

                if (surfaceY > floorY || surfaceY < seaFloorLimit) {
                    return Optional.empty();
                }

                if (!isOuterSkirt) {
                    innerSamples[innerIdx++] = surfaceY;
                    if (surfaceY < innerMinY) innerMinY = surfaceY;
                    if (surfaceY > innerMaxY) innerMaxY = surfaceY;
                    if (innerMaxY - innerMinY > this.maxHeightVariance) {
                        return Optional.empty();
                    }
                } else {
                    if (surfaceY < outerMinY) outerMinY = surfaceY;
                    if (surfaceY > outerMaxY) outerMaxY = surfaceY;
                }
            }
        }

        Arrays.sort(innerSamples, 0, innerIdx);
        int anchorSurfaceY = this.undergroundCave
                ? innerMinY
                : innerSamples[(int) (innerIdx * 0.40D)];

        int maxSkirtDrop = this.undergroundCave ? (this.maxHeightVariance + 4) : (this.maxHeightVariance + 6);
        int maxSkirtRise = this.undergroundCave ? (this.maxHeightVariance + 7) : (this.maxHeightVariance + 8);
        if (anchorSurfaceY - outerMinY > maxSkirtDrop || outerMaxY - anchorSurfaceY > maxSkirtRise) {
            return Optional.empty();
        }
        if (outerMaxY - outerMinY > this.maxHeightVariance + 12) {
            return Optional.empty();
        }

        int placeY = anchorSurfaceY + this.yOffset;
        if (placeY <= heightAccessor.getMinBuildHeight() + 6) {
            return Optional.empty();
        }

        // 3. Deferred 3D NoiseColumn ice/fluid check ONLY on 5 points (center + 4 cardinals) after all fast checks pass!
        int[][] iceCheckOffsets = {{0, 0}, {perimR, 0}, {-perimR, 0}, {0, perimR}, {0, -perimR}};
        for (int[] off : iceCheckOffsets) {
            int ix = cx + off[0];
            int iz = cz + off[1];
            int sy = (off[0] == 0 && off[1] == 0)
                    ? centerSurfaceY
                    : gen.getFirstOccupiedHeight(ix, iz, Heightmap.Types.WORLD_SURFACE_WG, heightAccessor, randomState);
            if (isWaterOrIceColumn(gen, ix, iz, sy, heightAccessor, randomState)) {
                return Optional.empty();
            }
        }

        Rotation rotation = Rotation.getRandom(context.random());
        StructurePoolElement element = this.startPool.value().getRandomTemplate(context.random());
        if (element == EmptyPoolElement.INSTANCE) {
            return Optional.empty();
        }

        BoundingBox rawBox = element.getBoundingBox(context.structureTemplateManager(), BlockPos.ZERO, rotation);
        int rawCenterX = (rawBox.minX() + rawBox.maxX()) / 2;
        int rawCenterZ = (rawBox.minZ() + rawBox.maxZ()) / 2;
        BlockPos adjustedOrigin = new BlockPos(cx - rawCenterX, placeY, cz - rawCenterZ);
        BoundingBox placedBox = element.getBoundingBox(context.structureTemplateManager(), adjustedOrigin, rotation);

        BlockPos stubPos = new BlockPos(cx, placeY, cz);
        return Optional.of(new GenerationStub(stubPos, builder -> builder.addPiece(
                new PoolElementStructurePiece(
                        context.structureTemplateManager(),
                        element,
                        adjustedOrigin,
                        0,
                        rotation,
                        placedBox,
                        JigsawStructure.DEFAULT_LIQUID_SETTINGS
                )
        )));
    }

    private static boolean isWaterOrIceColumn(
            ChunkGenerator gen,
            int x,
            int z,
            int surfaceY,
            LevelHeightAccessor heightAccessor,
            RandomState randomState
    ) {
        NoiseColumn column = gen.getBaseColumn(x, z, heightAccessor, randomState);
        BlockState atSurface = column.getBlock(surfaceY);
        BlockState aboveSurface = column.getBlock(surfaceY + 1);
        BlockState belowSurface = column.getBlock(surfaceY - 1);

        if (!atSurface.getFluidState().isEmpty() || !aboveSurface.getFluidState().isEmpty()
                || !belowSurface.getFluidState().isEmpty()) {
            return true;
        }
        return atSurface.is(Blocks.ICE)
                || atSurface.is(Blocks.FROSTED_ICE)
                || atSurface.is(Blocks.PACKED_ICE)
                || atSurface.is(Blocks.BLUE_ICE);
    }

    @Override
    public void afterPlace(
            WorldGenLevel level,
            StructureManager structureManager,
            ChunkGenerator chunkGenerator,
            RandomSource random,
            BoundingBox chunkBox,
            ChunkPos chunkPos,
            PiecesContainer pieces
    ) {
        BoundingBox pieceBox = pieces.calculateBoundingBox();
        double centerX = (pieceBox.minX() + pieceBox.maxX()) * 0.5D;
        double centerZ = (pieceBox.minZ() + pieceBox.maxZ()) * 0.5D;
        int placeY = pieceBox.minY();
        int groundY = placeY - this.yOffset;

        Rotation rotation = pieces.pieces().isEmpty()
                ? Rotation.NONE
                : pieces.pieces().get(0).getRotation();

        if (this.undergroundCave) {
            blendUndergroundCave(level, random, chunkBox, pieceBox, centerX, centerZ, placeY, groundY, rotation);
        } else {
            blendSurfaceArena(level, random, chunkBox, pieceBox, centerX, centerZ, placeY, groundY);
        }
    }

    private void blendSurfaceArena(
            WorldGenLevel level,
            RandomSource random,
            BoundingBox chunkBox,
            BoundingBox pieceBox,
            double centerX,
            double centerZ,
            int placeY,
            int targetGroundY
    ) {
        int minX = Math.max(chunkBox.minX(), (int) Math.floor(centerX - this.sampleRadius - this.blendRadius - 2));
        int maxX = Math.min(chunkBox.maxX(), (int) Math.ceil(centerX + this.sampleRadius + this.blendRadius + 2));
        int minZ = Math.max(chunkBox.minZ(), (int) Math.floor(centerZ - this.sampleRadius - this.blendRadius - 2));
        int maxZ = Math.min(chunkBox.maxZ(), (int) Math.ceil(centerZ + this.sampleRadius + this.blendRadius + 2));

        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        int minBuildY = level.getMinBuildHeight() + 2;

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                double dx = x - centerX;
                double dz = z - centerZ;
                double dist = Math.sqrt(dx * dx + dz * dz);
                double organicNoise = smoothNoise2D(x * 0.13D, z * 0.13D) * 2.1D;

                if (dist <= this.sampleRadius + 0.5D) {
                    boolean isArenaColumn = hasStructureBlockNearSurface(level, pos, x, z, placeY, targetGroundY);
                    if (isArenaColumn) {
                        pos.set(x, placeY, z);
                        if (!level.getBlockState(pos).isAir()) {
                            for (int y = placeY - 1; y >= Math.max(minBuildY, placeY - 16); y--) {
                                pos.set(x, y, z);
                                BlockState cur = level.getBlockState(pos);
                                if (cur.isSolid() && cur.getFluidState().isEmpty()) {
                                    break;
                                }
                                level.setBlock(pos, Blocks.STONE.defaultBlockState(), Block.UPDATE_CLIENTS);
                            }
                        }

                        if (this.clearInteriorRadius > 0 && dist <= this.clearInteriorRadius) {
                            int topScan = Math.min(level.getMaxBuildHeight() - 1, targetGroundY + 14);
                            for (int y = targetGroundY + 1; y <= topScan; y++) {
                                pos.set(x, y, z);
                                BlockState cur = level.getBlockState(pos);
                                if (isNaturalTerrainOrVegetation(cur)) {
                                    level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
                                }
                            }
                        }

                        if (dist >= this.sampleRadius - 2.5D) {
                            pos.set(x, targetGroundY, z);
                            BlockState rimState = level.getBlockState(pos);
                            if (rimState.is(Blocks.GRASS_BLOCK) && level.getBiome(pos).value().coldEnoughToSnow(pos)) {
                                pos.set(x, targetGroundY + 1, z);
                                if (level.getBlockState(pos).isAir()) {
                                    level.setBlock(pos, Blocks.SNOW.defaultBlockState(), Block.UPDATE_CLIENTS);
                                }
                            }
                        }
                        continue;
                    }
                    // Empty natural-ground notch right against the circular stone rim:
                    // fall through to blend flush to targetGroundY (s = 0.0) so ZERO 1-block slits remain!
                }

                double outerLimit = this.sampleRadius + this.blendRadius + organicNoise;
                if (dist > outerLimit) {
                    continue;
                }

                double t = Mth.clamp((dist - this.sampleRadius) / Math.max(1.0D, outerLimit - this.sampleRadius), 0.0D, 1.0D);
                double s = t * t * t * (t * (t * 6.0D - 15.0D) + 10.0D);

                SurfaceColumn nat = sampleNaturalSurface(level, x, z, targetGroundY);
                if (nat == null) {
                    continue;
                }

                int blendedY = (int) Math.round(targetGroundY + (nat.y - targetGroundY) * s);
                if (blendedY == nat.y) {
                    continue;
                }

                applyBlendedColumn(level, random, pos, x, z, nat, blendedY, minBuildY);
            }
        }
    }

    private void blendUndergroundCave(
            WorldGenLevel level,
            RandomSource random,
            BoundingBox chunkBox,
            BoundingBox pieceBox,
            double centerX,
            double centerZ,
            int placeY,
            int rimY,
            Rotation rotation
    ) {
        int minX = Math.max(chunkBox.minX(), (int) Math.floor(centerX - this.sampleRadius - this.blendRadius - 2));
        int maxX = Math.min(chunkBox.maxX(), (int) Math.ceil(centerX + this.sampleRadius + this.blendRadius + 2));
        int minZ = Math.max(chunkBox.minZ(), (int) Math.floor(centerZ - this.sampleRadius - this.blendRadius - 2));
        int maxZ = Math.min(chunkBox.maxZ(), (int) Math.ceil(centerZ + this.sampleRadius + this.blendRadius + 2));

        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        int minBuildY = level.getMinBuildHeight() + 2;
        double seedPhase = ((pieceBox.minX() * 31L ^ pieceBox.minZ() * 17L) & 0xFF) * 0.025D;

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                double dx = x - centerX;
                double dz = z - centerZ;
                double dist = Math.sqrt(dx * dx + dz * dz);
                double angle = Math.atan2(dz, dx);

                double skylightR = 8.9D
                        + 1.55D * Math.sin(angle * 3.0D + seedPhase)
                        + 0.95D * Math.cos(angle * 5.0D - seedPhase * 1.6D)
                        + 0.65D * Math.sin(angle * 7.0D + seedPhase * 0.7D);

                if (dist < skylightR) {
                    int topScan = Math.min(level.getMaxBuildHeight() - 1,
                            Math.max(level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z), rimY) + 12);
                    for (int y = placeY + 32; y <= topScan; y++) {
                        pos.set(x, y, z);
                        if (!level.getBlockState(pos).isAir()) {
                            level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
                        }
                    }
                    continue;
                }

                SurfaceColumn nat = sampleNaturalSurface(level, x, z, rimY);
                if (nat == null) {
                    continue;
                }
                int surfaceY = Math.max(nat.y, rimY);
                boolean coldBiome = level.getBiome(pos.set(x, surfaceY, z)).value().coldEnoughToSnow(pos);

                if (dist < 16.5D) {
                    double overhangT = Mth.clamp((dist - skylightR) / Math.max(1.0D, 16.5D - skylightR), 0.0D, 1.0D);
                    double relief = smoothNoise2D(x * 0.27D, z * 0.27D) * 2.1D
                            + smoothNoise2D(x * 0.55D + 13.0D, z * 0.55D - 7.0D) * 0.9D;

                    int ceilingBottomY = (int) Math.round(
                            (placeY + 34.5D) - overhangT * 3.8D + relief
                    );
                    ceilingBottomY = Mth.clamp(ceilingBottomY, placeY + 29, surfaceY - 1);

                    for (int y = placeY + 26; y < ceilingBottomY; y++) {
                        pos.set(x, y, z);
                        BlockState cur = level.getBlockState(pos);
                        if (cur.is(Blocks.DIRT) || cur.is(Blocks.GRASS_BLOCK) || cur.is(Blocks.MOSS_BLOCK)) {
                            level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
                        }
                    }

                    for (int y = ceilingBottomY; y < surfaceY; y++) {
                        pos.set(x, y, z);
                        BlockState rock;
                        if (y == surfaceY - 1 && surfaceY - ceilingBottomY >= 2) {
                            rock = nat.fillerBlock;
                        } else {
                            rock = pickCaveRoofBlock(x, y, z, coldBiome, y == ceilingBottomY);
                        }
                        level.setBlock(pos, rock, Block.UPDATE_CLIENTS);
                    }

                    pos.set(x, surfaceY, z);
                    level.setBlock(pos, nat.topBlock, Block.UPDATE_CLIENTS);
                    if (coldBiome || nat.hasSnowLayer) {
                        pos.set(x, surfaceY + 1, z);
                        if (level.getBlockState(pos).isAir()) {
                            level.setBlock(pos, Blocks.SNOW.defaultBlockState(), Block.UPDATE_CLIENTS);
                        }
                    }

                    int hash = positionHash(x, z);
                    if ((hash & 7) < 2 && ceilingBottomY >= placeY + 30) {
                        int hangLen = 1 + ((hash >> 3) % 3);
                        boolean icyTip = coldBiome || overhangT < 0.45D;
                        for (int dy = 1; dy <= hangLen; dy++) {
                            pos.set(x, ceilingBottomY - dy, z);
                            if (!level.getBlockState(pos).isAir()) {
                                break;
                            }
                            BlockState stalactite;
                            if (icyTip && dy == hangLen && (hash & 16) != 0) {
                                stalactite = Blocks.PACKED_ICE.defaultBlockState();
                            } else if (dy == 1) {
                                stalactite = (hash & 32) != 0
                                        ? Blocks.ANDESITE.defaultBlockState()
                                        : Blocks.STONE.defaultBlockState();
                            } else {
                                stalactite = (hash & 64) != 0
                                        ? Blocks.COBBLESTONE.defaultBlockState()
                                        : Blocks.MOSSY_COBBLESTONE.defaultBlockState();
                            }
                            level.setBlock(pos, stalactite, Block.UPDATE_CLIENTS);
                        }
                    }
                    continue;
                }

                if (dist <= this.sampleRadius + 1.5D) {
                    int topY = Math.max(nat.y, rimY);
                    for (int y = Math.max(placeY + 30, topY - 6); y <= topY; y++) {
                        pos.set(x, y, z);
                        BlockState cur = level.getBlockState(pos);
                        if (y == topY && (cur.is(Blocks.GRASS_BLOCK) || cur.is(Blocks.DIRT) || cur.is(Blocks.MOSS_BLOCK))) {
                            level.setBlock(pos, nat.topBlock, Block.UPDATE_CLIENTS);
                        } else if (y >= topY - 2 && (cur.is(Blocks.MOSS_BLOCK) || cur.is(Blocks.DEEPSLATE))) {
                            level.setBlock(pos, nat.fillerBlock, Block.UPDATE_CLIENTS);
                        } else if (cur.is(Blocks.MOSS_BLOCK)) {
                            level.setBlock(pos, Blocks.STONE.defaultBlockState(), Block.UPDATE_CLIENTS);
                        }
                    }
                    if (coldBiome || nat.hasSnowLayer) {
                        pos.set(x, topY + 1, z);
                        if (level.getBlockState(pos).isAir()) {
                            level.setBlock(pos, Blocks.SNOW.defaultBlockState(), Block.UPDATE_CLIENTS);
                        }
                    }
                    continue;
                }

                double organicNoise = smoothNoise2D(x * 0.14D, z * 0.14D) * 1.6D;
                double outerLimit = this.sampleRadius + this.blendRadius + organicNoise;
                if (dist <= outerLimit && nat.y < rimY) {
                    double t = Mth.clamp((dist - this.sampleRadius) / Math.max(1.0D, outerLimit - this.sampleRadius), 0.0D, 1.0D);
                    double s = t * t * t * (t * (t * 6.0D - 15.0D) + 10.0D);
                    int blendedY = (int) Math.round(rimY + (nat.y - rimY) * s);
                    if (blendedY > nat.y) {
                        applyBlendedColumn(level, random, pos, x, z, nat, blendedY, minBuildY);
                    }
                }
            }
        }

        // Carve a walkable curved cavern entrance tunnel from the arched doorway up to the overworld surface
        carveCryoEntranceTunnel(level, chunkBox, centerX, centerZ, placeY, rimY, rotation, minBuildY);
    }

    /**
     * Carves a natural, walkable curved cavern ramp connecting the arched doorway of
     * {@code cryo_regisvine_arena} (local NORTH / -Z face at r ≈ 21..24, Y = placeY + 3..12)
     * smoothly up to the overworld surface within the structure's {@link #adjustBoundingBox} radius.
     */
    private void carveCryoEntranceTunnel(
            WorldGenLevel level,
            BoundingBox chunkBox,
            double centerX,
            double centerZ,
            int placeY,
            int rimY,
            Rotation rotation,
            int minBuildY
    ) {
        double doorAngle = switch (rotation) {
            case NONE -> -Math.PI * 0.5D;             // local -Z (North)
            case CLOCKWISE_90 -> 0.0D;                // +X (East)
            case CLOCKWISE_180 -> Math.PI * 0.5D;     // +Z (South)
            case COUNTERCLOCKWISE_90 -> Math.PI;      // -X (West)
        };

        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        int maxBuildY = level.getMaxBuildHeight() - 2;

        // Parametric centerline:
        //   s in [0, 6]:  straight outward from doorway mouth (r = 20.5 -> 26.5, floorY = placeY+3 -> placeY+5)
        //   s in (6, 54]: curved spiral ramp along r = 26.5 -> 30.5, climbing +0.76 Y/block to surface
        for (double s = 0.0D; s <= 54.0D; s += 0.5D) {
            double r;
            double ang;
            double fy;
            if (s <= 6.0D) {
                r = 20.5D + s;
                ang = doorAngle;
                fy = (placeY + 3.0D) + (s / 6.0D) * 2.0D;
            } else {
                double u = s - 6.0D;
                r = 26.5D + Math.min(4.0D, u * 0.085D);
                ang = doorAngle + (u / 27.5D);
                fy = (placeY + 5.0D) + u * 0.76D;
            }

            double cx = centerX + Math.cos(ang) * r;
            double cz = centerZ + Math.sin(ang) * r;
            int floorY = (int) Math.floor(fy);

            // Fast chunk-box reject
            if (cx + 5.0D < chunkBox.minX() || cx - 5.0D > chunkBox.maxX()
                    || cz + 5.0D < chunkBox.minZ() || cz - 5.0D > chunkBox.maxZ()) {
                continue;
            }

            int ixMin = Math.max(chunkBox.minX(), (int) Math.floor(cx - 4.0D));
            int ixMax = Math.min(chunkBox.maxX(), (int) Math.ceil(cx + 4.0D));
            int izMin = Math.max(chunkBox.minZ(), (int) Math.floor(cz - 4.0D));
            int izMax = Math.min(chunkBox.maxZ(), (int) Math.ceil(cz + 4.0D));

            for (int x = ixMin; x <= ixMax; x++) {
                for (int z = izMin; z <= izMax; z++) {
                    double hd = Math.hypot(x - cx, z - cz);
                    if (hd > 3.85D) {
                        continue;
                    }

                    int localSurfY = Math.max(level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z), rimY - 2);
                    if (floorY > localSurfY + 1) {
                        continue;
                    }

                    boolean coldBiome = level.getBiome(pos.set(x, Math.min(maxBuildY, localSurfY), z))
                            .value().coldEnoughToSnow(pos);

                    if (hd <= 2.75D) {
                        // Natural walkable stone/andesite/mossy floor + solid subfloor
                        pos.set(x, floorY, z);
                        BlockState curFloor = level.getBlockState(pos);
                        if (curFloor.isAir() || !curFloor.getFluidState().isEmpty()
                                || curFloor.is(Blocks.DIRT) || curFloor.is(Blocks.GRASS_BLOCK)
                                || curFloor.is(Blocks.GRAVEL) || curFloor.is(Blocks.SAND)) {
                            level.setBlock(pos, pickTunnelFloorBlock(x, floorY, z), Block.UPDATE_CLIENTS);
                        }
                        for (int sy = floorY - 1; sy >= Math.max(minBuildY, floorY - 3); sy--) {
                            pos.set(x, sy, z);
                            BlockState under = level.getBlockState(pos);
                            if (under.isAir() || !under.getFluidState().isEmpty()) {
                                level.setBlock(pos, Blocks.STONE.defaultBlockState(), Block.UPDATE_CLIENTS);
                            } else {
                                break;
                            }
                        }

                        // Spacious arched cavern headroom (~6 blocks tall at center, >=4 blocks at sides)
                        int archH = (int) Math.round(5.8D * Math.sqrt(Math.max(0.0D, 1.0D - (hd / 3.05D) * (hd / 3.05D))));
                        archH = Math.max(4, archH);
                        int carveTop = floorY + archH;
                        boolean emergingToSurface = (carveTop >= localSurfY - 1);
                        int clearTop = emergingToSurface ? Math.min(maxBuildY, localSurfY + 8) : Math.min(maxBuildY, carveTop);

                        for (int y = floorY + 1; y <= clearTop; y++) {
                            pos.set(x, y, z);
                            BlockState cur = level.getBlockState(pos);
                            if (!cur.isAir()) {
                                level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
                            }
                        }

                        // Seal roof block directly above tunnel if underground & exposed to fluid/cave air
                        if (!emergingToSurface && carveTop + 1 < localSurfY) {
                            pos.set(x, carveTop + 1, z);
                            BlockState roof = level.getBlockState(pos);
                            if (!roof.getFluidState().isEmpty() || roof.is(Blocks.GRAVEL) || roof.is(Blocks.SAND)) {
                                level.setBlock(pos, pickCaveRoofBlock(x, carveTop + 1, z, coldBiome, true), Block.UPDATE_CLIENTS);
                            }
                        }
                    } else if (floorY + 4 < localSurfY) {
                        // Outer stone/ice wall jacket so underground aquifers/caves never flood the tunnel
                        for (int y = floorY; y <= Math.min(localSurfY - 1, floorY + 5); y++) {
                            pos.set(x, y, z);
                            BlockState wall = level.getBlockState(pos);
                            if (!wall.getFluidState().isEmpty() || wall.is(Blocks.GRAVEL) || wall.is(Blocks.SAND)) {
                                level.setBlock(pos, pickCaveRoofBlock(x, y, z, coldBiome, false), Block.UPDATE_CLIENTS);
                            }
                        }
                    }
                }
            }
        }
    }

    private static BlockState pickTunnelFloorBlock(int x, int y, int z) {
        int h = Math.floorMod(positionHash(x * 7 + y * 3, z * 11 - y * 5), 100);
        if (h < 45) {
            return Blocks.STONE.defaultBlockState();
        } else if (h < 70) {
            return Blocks.ANDESITE.defaultBlockState();
        } else if (h < 88) {
            return Blocks.COBBLESTONE.defaultBlockState();
        } else {
            return Blocks.MOSSY_COBBLESTONE.defaultBlockState();
        }
    }

    private static boolean hasStructureBlockNearSurface(
            WorldGenLevel level,
            BlockPos.MutableBlockPos pos,
            int x,
            int z,
            int placeY,
            int targetGroundY
    ) {
        int scanTop = Math.min(level.getMaxBuildHeight() - 1, targetGroundY + 2);
        for (int y = placeY; y <= scanTop; y++) {
            pos.set(x, y, z);
            BlockState state = level.getBlockState(pos);
            if (!state.isAir() && !isNaturalTerrainOrVegetation(state)) {
                return true;
            }
        }
        return false;
    }

    private static BlockState pickCaveRoofBlock(int x, int y, int z, boolean coldBiome, boolean isUnderside) {
        int h = positionHash(x * 3 + y * 13, z * 5 - y * 7);
        int mod = Math.floorMod(h, 100);
        if (isUnderside && coldBiome && mod < 14) {
            return Blocks.PACKED_ICE.defaultBlockState();
        }
        if (mod < 48) {
            return Blocks.STONE.defaultBlockState();
        } else if (mod < 74) {
            return Blocks.ANDESITE.defaultBlockState();
        } else if (mod < 88) {
            return Blocks.COBBLESTONE.defaultBlockState();
        } else if (mod < 95) {
            return Blocks.MOSSY_COBBLESTONE.defaultBlockState();
        } else {
            return Blocks.DEEPSLATE.defaultBlockState();
        }
    }

    private static void applyBlendedColumn(
            WorldGenLevel level,
            RandomSource random,
            BlockPos.MutableBlockPos pos,
            int x,
            int z,
            SurfaceColumn nat,
            int blendedY,
            int minBuildY
    ) {
        if (blendedY > nat.y) {
            for (int y = nat.y; y < blendedY; y++) {
                pos.set(x, y, z);
                BlockState fill = (blendedY - y <= 3) ? nat.fillerBlock : Blocks.STONE.defaultBlockState();
                level.setBlock(pos, fill, Block.UPDATE_CLIENTS);
            }
            pos.set(x, blendedY, z);
            level.setBlock(pos, nat.topBlock, Block.UPDATE_CLIENTS);

            int topClear = Math.min(level.getMaxBuildHeight() - 1, blendedY + 12);
            for (int y = blendedY + 1; y <= topClear; y++) {
                pos.set(x, y, z);
                BlockState cur = level.getBlockState(pos);
                if (isVegetationOrSnow(cur)) {
                    level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
                } else if (!cur.isAir()) {
                    break;
                }
            }
        } else {
            int topClear = Math.min(level.getMaxBuildHeight() - 1, nat.y + 14);
            for (int y = blendedY + 1; y <= topClear; y++) {
                pos.set(x, y, z);
                BlockState cur = level.getBlockState(pos);
                if (!cur.isAir() && isNaturalTerrainOrVegetation(cur)) {
                    level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
                }
            }
            pos.set(x, blendedY, z);
            level.setBlock(pos, nat.topBlock, Block.UPDATE_CLIENTS);
            for (int y = blendedY - 1; y >= Math.max(minBuildY, blendedY - 3); y--) {
                pos.set(x, y, z);
                BlockState cur = level.getBlockState(pos);
                if (cur.isAir() || cur.is(Blocks.GRASS_BLOCK)) {
                    level.setBlock(pos, nat.fillerBlock, Block.UPDATE_CLIENTS);
                }
            }
        }

        if (nat.hasSnowLayer) {
            pos.set(x, blendedY + 1, z);
            if (level.getBlockState(pos).isAir()) {
                level.setBlock(pos, Blocks.SNOW.defaultBlockState(), Block.UPDATE_CLIENTS);
            }
        }
    }

    private static SurfaceColumn sampleNaturalSurface(WorldGenLevel level, int x, int z, int fallbackY) {
        int startY = Math.min(level.getMaxBuildHeight() - 2,
                Math.max(level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z), fallbackY + 12));
        int minY = Math.max(level.getMinBuildHeight() + 2, fallbackY - 24);
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        boolean sawSnow = false;

        for (int y = startY; y >= minY; y--) {
            pos.set(x, y, z);
            BlockState state = level.getBlockState(pos);
            if (state.isAir()) {
                continue;
            }
            if (state.is(Blocks.SNOW)) {
                sawSnow = true;
                continue;
            }
            if (isVegetationOrSnow(state)) {
                continue;
            }
            if (!state.getFluidState().isEmpty()) {
                return null;
            }
            if (isNaturalGroundBlock(state)) {
                BlockState top = state;
                BlockState filler = deriveFiller(top);
                if (!sawSnow && level.getBiome(pos).value().coldEnoughToSnow(pos)) {
                    sawSnow = true;
                }
                return new SurfaceColumn(y, top, filler, sawSnow);
            }
            return null;
        }
        return null;
    }

    private static BlockState deriveFiller(BlockState top) {
        if (top.is(Blocks.GRASS_BLOCK) || top.is(Blocks.PODZOL) || top.is(Blocks.MYCELIUM)
                || top.is(Blocks.COARSE_DIRT) || top.is(Blocks.ROOTED_DIRT) || top.is(Blocks.DIRT)) {
            return Blocks.DIRT.defaultBlockState();
        }
        if (top.is(Blocks.SAND)) {
            return Blocks.SANDSTONE.defaultBlockState();
        }
        if (top.is(Blocks.RED_SAND)) {
            return Blocks.RED_SANDSTONE.defaultBlockState();
        }
        if (top.is(Blocks.SNOW_BLOCK) || top.is(Blocks.POWDER_SNOW)) {
            return Blocks.DIRT.defaultBlockState();
        }
        return Blocks.STONE.defaultBlockState();
    }

    private static boolean isNaturalGroundBlock(BlockState state) {
        return state.is(BlockTags.DIRT)
                || state.is(BlockTags.BASE_STONE_OVERWORLD)
                || state.is(BlockTags.SAND)
                || state.is(BlockTags.TERRACOTTA)
                || state.is(Blocks.GRAVEL)
                || state.is(Blocks.SNOW_BLOCK)
                || state.is(Blocks.POWDER_SNOW)
                || state.is(Blocks.CALCITE)
                || state.is(Blocks.CLAY)
                || state.is(Blocks.MUD);
    }

    private static boolean isVegetationOrSnow(BlockState state) {
        return state.is(BlockTags.LEAVES)
                || state.is(BlockTags.LOGS)
                || state.is(BlockTags.FLOWERS)
                || state.is(BlockTags.SAPLINGS)
                || state.is(BlockTags.REPLACEABLE)
                || state.is(Blocks.SNOW)
                || state.is(Blocks.VINE)
                || state.is(Blocks.SWEET_BERRY_BUSH)
                || state.is(Blocks.BROWN_MUSHROOM)
                || state.is(Blocks.RED_MUSHROOM);
    }

    private static boolean isNaturalTerrainOrVegetation(BlockState state) {
        return isNaturalGroundBlock(state) || isVegetationOrSnow(state);
    }

    private static double smoothNoise2D(double x, double z) {
        return (Math.sin(x) * Math.cos(z * 0.9D)
                + 0.5D * Math.sin(x * 1.7D + z * 1.3D)
                + 0.25D * Math.cos(x * 2.9D - z * 2.1D)) / 1.75D;
    }

    private static int positionHash(int x, int z) {
        int h = x * 374761393 + z * 668265263;
        h = (h ^ (h >> 13)) * 1274126177;
        return h ^ (h >> 16);
    }

    private record SurfaceColumn(int y, BlockState topBlock, BlockState fillerBlock, boolean hasSnowLayer) {}

    @Override
    public StructureType<?> type() {
        return TeyvatBossesStructures.flatArena();
    }
}

package com.velorise.teyvat.landmark;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.velorise.teyvat.worldgen.TeyvatDimensions;
import com.velorise.teyvat.worldgen.region.RegionBarrierManager;
import com.velorise.teyvat.worldgen.region.RegionMacroMap;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Supplier;

/**
 * Deterministic Mondstadt world decorator invoked on chunk load in {@code veloriseteyvat:teyvat}:
 * <ol>
 *   <li>Paints extracted cream roads ({@code RoadMask >= 160}) with full-block {@code packed_mud}/{@code coarse_dirt}.</li>
 *   <li>Places all 4 Statues of The Seven and 10 Teleport Waypoints on 3x3 stone plinths.</li>
 *   <li>Places all 17 Mondstadt {@code .nbt} structures (City Core, Houses, Windmills, 6 Boss Arenas,
 *       Windrise Great Oak, Dornman Port Pier, Thousand Winds Temple, Dadaupa Camp, Stone Gate).</li>
 * </ol>
 */
public final class MondstadtLandmarkPlacer {
    public record LandmarkEntry(String id, String name, String type, int x, int y, int z) {}

    /** {@code fixedY == Integer.MIN_VALUE} means snap automatically to terrain surface Y - 1. */
    private record StructurePlacementSpec(
            String key, String templatePath, int canonicalX, int canonicalZ, int fixedY, Rotation rotation
    ) {
        private static final int SURFACE = Integer.MIN_VALUE;
    }

    // Canonical 1:1 placements (scaled automatically by save's worldScale)
    private static final List<StructurePlacementSpec> MONDSTADT_STRUCTURES = List.of(
            // --- Mondstadt City Core (3-Tier Island: Y=74 Gate/Bridge -> Y=82 Plaza -> Y=92 Cathedral) ---
            new StructurePlacementSpec("mc_cider_bridge_1",   "mondstadt/mondstadt_cider_bridge",       115,  454, 64, Rotation.NONE),
            new StructurePlacementSpec("mc_cider_bridge_2",   "mondstadt/mondstadt_cider_bridge",       160,  454, 64, Rotation.NONE),
            new StructurePlacementSpec("mc_main_gate_wall",   "mondstadt/mondstadt_main_gate_wall",      96,  437, 74, Rotation.NONE),
            new StructurePlacementSpec("mc_barbatos_plaza",   "mondstadt/barbatos_statue_plaza",        -68,  346, 81, Rotation.NONE),
            new StructurePlacementSpec("mc_cathedral",        "mondstadt/favonius_cathedral",          -128,  308, 91, Rotation.NONE),

            // --- Mondstadt City Houses, Stalls & Windmill Towers ---
            new StructurePlacementSpec("mc_windmill_east",    "mondstadt/mondstadt_windmill_tower",      48,  392, StructurePlacementSpec.SURFACE, Rotation.NONE),
            new StructurePlacementSpec("mc_windmill_north",   "mondstadt/mondstadt_windmill_tower",     -12,  326, StructurePlacementSpec.SURFACE, Rotation.CLOCKWISE_90),
            new StructurePlacementSpec("mc_townhouse_1",      "mondstadt/mondstadt_house_tall",          32,  424, StructurePlacementSpec.SURFACE, Rotation.NONE),
            new StructurePlacementSpec("mc_townhouse_2",      "mondstadt/mondstadt_house_tall",           4,  428, StructurePlacementSpec.SURFACE, Rotation.CLOCKWISE_180),
            new StructurePlacementSpec("mc_townhouse_3",      "mondstadt/mondstadt_house_tall",         -42,  356, StructurePlacementSpec.SURFACE, Rotation.CLOCKWISE_90),
            new StructurePlacementSpec("mc_townhouse_4",      "mondstadt/mondstadt_house_tall",         -74,  384, StructurePlacementSpec.SURFACE, Rotation.COUNTERCLOCKWISE_90),
            new StructurePlacementSpec("mc_cottage_1",        "mondstadt/mondstadt_house_small",         64,  434, StructurePlacementSpec.SURFACE, Rotation.CLOCKWISE_90),
            new StructurePlacementSpec("mc_cottage_2",        "mondstadt/mondstadt_house_small",        -64,  412, StructurePlacementSpec.SURFACE, Rotation.NONE),
            new StructurePlacementSpec("mc_stall_1",          "mondstadt/mondstadt_market_stall",        78,  414, StructurePlacementSpec.SURFACE, Rotation.NONE),
            new StructurePlacementSpec("mc_stall_2",          "mondstadt/mondstadt_market_stall",        52,  412, StructurePlacementSpec.SURFACE, Rotation.CLOCKWISE_180),
            new StructurePlacementSpec("mc_stall_3",          "mondstadt/mondstadt_market_stall",        22,  396, StructurePlacementSpec.SURFACE, Rotation.CLOCKWISE_90),

            // --- Springvale & Dawn Winery ---
            new StructurePlacementSpec("sv_cottage_1",        "mondstadt/mondstadt_house_small",         86,  842, StructurePlacementSpec.SURFACE, Rotation.NONE),
            new StructurePlacementSpec("sv_cottage_2",        "mondstadt/mondstadt_house_small",        114,  862, StructurePlacementSpec.SURFACE, Rotation.CLOCKWISE_180),
            new StructurePlacementSpec("dw_manor",            "mondstadt/mondstadt_house_tall",        -326,  926, StructurePlacementSpec.SURFACE, Rotation.NONE),

            // --- 6 v1.0 Boss Arenas (Canonical 1:1 Scale) ---
            new StructurePlacementSpec("arena_andrius",       "mondstadt/boss_arena_andrius",          -589,  664, StructurePlacementSpec.SURFACE, Rotation.NONE),
            new StructurePlacementSpec("arena_dvalin",        "mondstadt/boss_arena_dvalin_spire",     -960,   31, StructurePlacementSpec.SURFACE, Rotation.NONE),
            new StructurePlacementSpec("arena_anemo_hypo",    "mondstadt/boss_arena_hypostasis",        892,  331, StructurePlacementSpec.SURFACE, Rotation.NONE),
            new StructurePlacementSpec("arena_electro_hypo",  "mondstadt/boss_arena_hypostasis",       1105, 1367, StructurePlacementSpec.SURFACE, Rotation.NONE),
            new StructurePlacementSpec("arena_cryo_hypo",     "mondstadt/boss_arena_hypostasis",         85, 1286, StructurePlacementSpec.SURFACE, Rotation.NONE),
            new StructurePlacementSpec("arena_cryo_regis",    "mondstadt/boss_arena_cryo_regisvine",    671,  249, StructurePlacementSpec.SURFACE, Rotation.NONE),

            // --- Windrise Great Oak & Priority-2 Expansion Landmarks ---
            new StructurePlacementSpec("windrise_oak_tree",   "mondstadt/windrise_great_oak",           488,  596, StructurePlacementSpec.SURFACE, Rotation.NONE),
            new StructurePlacementSpec("dornman_pier",        "mondstadt/dornman_port_pier",           -552, -838, 59, Rotation.NONE),
            new StructurePlacementSpec("thousand_winds",      "mondstadt/thousand_winds_temple",        820,  220, StructurePlacementSpec.SURFACE, Rotation.NONE),
            new StructurePlacementSpec("dadaupa_camp",        "mondstadt/dadaupa_hilichurl_camp",       790, 1280, StructurePlacementSpec.SURFACE, Rotation.NONE),
            new StructurePlacementSpec("stone_gate_pass",     "mondstadt/stone_gate_border_pass",      -640, 1080, StructurePlacementSpec.SURFACE, Rotation.NONE)
    );

    private static final int MAX_CHUNKS_PER_TICK = 4;
    private static final Queue<ChunkPos> PENDING_CHUNKS = new ConcurrentLinkedQueue<>();
    private static final Set<Long> QUEUED_CHUNKS = ConcurrentHashMap.newKeySet();

    private static List<LandmarkEntry> cached;
    private static Supplier<Block> waypointBlock;
    private static Supplier<Block> statueBlock;

    private MondstadtLandmarkPlacer() {}

    public static void bindBlocks(Supplier<Block> waypoint, Supplier<Block> statue) {
        waypointBlock = waypoint;
        statueBlock = statue;
    }

    public static List<LandmarkEntry> getEntries() {
        if (cached != null) return cached;
        List<LandmarkEntry> list = new ArrayList<>();
        String cp = "/data/" + TeyvatDimensions.NAMESPACE + "/" + TeyvatDimensions.LANDMARKS_LOCATION.getPath();
        try (InputStream in = MondstadtLandmarkPlacer.class.getResourceAsStream(cp)) {
            if (in != null) {
                JsonObject root = JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject();
                JsonArray arr = root.getAsJsonArray("landmarks");
                for (JsonElement el : arr) {
                    JsonObject o = el.getAsJsonObject();
                    list.add(new LandmarkEntry(
                            o.get("id").getAsString(),
                            o.get("name").getAsString(),
                            o.get("type").getAsString(),
                            o.get("x").getAsInt(),
                            o.get("y").getAsInt(),
                            o.get("z").getAsInt()
                    ));
                }
            }
        } catch (Exception ignored) {}
        cached = List.copyOf(list);
        return cached;
    }

    public static void onChunkLoaded(ServerLevel level, ChunkPos chunkPos) {
        if (!level.dimension().equals(TeyvatDimensions.TEYVAT_LEVEL)) {
            return;
        }
        if (QUEUED_CHUNKS.add(chunkPos.toLong())) {
            PENDING_CHUNKS.add(chunkPos);
        }
    }

    public static void tickPendingChunks(ServerLevel level) {
        if (!level.dimension().equals(TeyvatDimensions.TEYVAT_LEVEL) || PENDING_CHUNKS.isEmpty()) {
            return;
        }
        RegionBarrierManager data = RegionBarrierManager.get(level);
        double scale = data.getWorldScale();
        for (int i = 0; i < MAX_CHUNKS_PER_TICK; i++) {
            ChunkPos pos = PENDING_CHUNKS.poll();
            if (pos == null) {
                break;
            }
            QUEUED_CHUNKS.remove(pos.toLong());
            if (level.getChunkSource().getChunkNow(pos.x, pos.z) == null) {
                continue;
            }
            paintChunkRoads(level, pos);
            if (waypointBlock != null && statueBlock != null) {
                placeWaypointsAndStatues(level, pos, scale);
            }
            placeMondstadtStructures(level, pos, data, scale);
        }
    }

    public static void clearPending() {
        PENDING_CHUNKS.clear();
        QUEUED_CHUNKS.clear();
    }

    private static void paintChunkRoads(ServerLevel level, ChunkPos chunkPos) {
        RegionMacroMap map = RegionMacroMap.getInstance();
        int baseX = chunkPos.getMinBlockX();
        int baseZ = chunkPos.getMinBlockZ();
        for (int dx = 0; dx < 16; dx++) {
            for (int dz = 0; dz < 16; dz++) {
                int wx = baseX + dx;
                int wz = baseZ + dz;
                int mask = map.sampleWaterRoadMask(wx, wz);
                if (mask < 160 || mask >= 200) {
                    continue;
                }
                int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, wx, wz) - 1;
                if (y < 64) {
                    continue;
                }
                BlockPos pos = new BlockPos(wx, y, wz);
                if (level.getBlockState(pos).is(Blocks.GRASS_BLOCK)) {
                    int hash = Math.abs((wx * 73428767) ^ (wz * 912931)) % 10;
                    Block roadBlock = hash < 6 ? Blocks.PACKED_MUD
                            : (hash < 9 ? Blocks.COARSE_DIRT : Blocks.ROOTED_DIRT);
                    level.setBlock(pos, roadBlock.defaultBlockState(), Block.UPDATE_CLIENTS);
                }
            }
        }
    }

    private static void placeWaypointsAndStatues(ServerLevel level, ChunkPos chunkPos, double scale) {
        for (LandmarkEntry entry : getEntries()) {
            int sx = (int) Math.round(entry.x() * scale);
            int sz = (int) Math.round(entry.z() * scale);
            if ((sx >> 4) != chunkPos.x || (sz >> 4) != chunkPos.z) {
                continue;
            }
            boolean isStatue = "statue".equals(entry.type());
            boolean isWaypoint = "waypoint".equals(entry.type()) || "spawn_waypoint".equals(entry.type());
            if (!isStatue && !isWaypoint) {
                continue;
            }
            int surfY = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, sx, sz);
            BlockPos basePos = new BlockPos(sx, Math.max(entry.y(), surfY), sz);

            Block targetBlock = isStatue ? statueBlock.get() : waypointBlock.get();
            if (level.getBlockState(basePos).is(targetBlock)) {
                continue;
            }

            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    BlockPos p = basePos.offset(dx, -1, dz);
                    level.setBlock(p, (dx == 0 && dz == 0) ? Blocks.CHISELED_STONE_BRICKS.defaultBlockState()
                            : Blocks.SMOOTH_STONE.defaultBlockState(), Block.UPDATE_CLIENTS);
                    for (int dy = -2; dy >= -6; dy--) {
                        BlockPos below = basePos.offset(dx, dy, dz);
                        if (level.getBlockState(below).isAir()) {
                            level.setBlock(below, Blocks.STONE_BRICKS.defaultBlockState(), Block.UPDATE_CLIENTS);
                        }
                    }
                }
            }

            level.setBlock(basePos, targetBlock.defaultBlockState(), Block.UPDATE_ALL);
            if (isStatue && level.getBlockEntity(basePos) instanceof StatueOfTheSevenBlockEntity sbe) {
                sbe.configure(entry.id(), entry.name());
            } else if (isWaypoint && level.getBlockEntity(basePos) instanceof TeleportWaypointBlockEntity wbe) {
                wbe.configure(entry.id(), entry.name());
            }
        }
    }

    private static void placeMondstadtStructures(ServerLevel level, ChunkPos chunkPos,
                                                 RegionBarrierManager data, double scale) {
        for (StructurePlacementSpec spec : MONDSTADT_STRUCTURES) {
            if (data.isStructurePlaced(spec.key())) {
                continue;
            }
            int sx = (int) Math.round(spec.canonicalX() * scale);
            int sz = (int) Math.round(spec.canonicalZ() * scale);
            if ((sx >> 4) != chunkPos.x || (sz >> 4) != chunkPos.z) {
                continue;
            }

            ResourceLocation templateId = TeyvatDimensions.id(spec.templatePath());
            Optional<StructureTemplate> opt = level.getStructureManager().get(templateId);
            if (opt.isEmpty()) {
                continue;
            }

            int targetY;
            if (spec.fixedY() != StructurePlacementSpec.SURFACE) {
                targetY = spec.fixedY();
            } else {
                int surfY = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, sx, sz);
                if (surfY < 64) {
                    continue;
                }
                targetY = surfY - 1;
            }
            BlockPos origin = new BlockPos(sx, targetY, sz);
            StructurePlaceSettings settings = new StructurePlaceSettings().setRotation(spec.rotation());
            opt.get().placeInWorld(level, origin, origin, settings, level.random, Block.UPDATE_CLIENTS);
            data.markStructurePlaced(spec.key());
        }
    }
}

package com.velorise.teyvat.portal;

import com.velorise.teyvat.worldgen.TeyvatDimensions;
import com.velorise.teyvat.worldgen.region.RegionBarrierManager;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Server-side orchestrator for entering/exiting {@code veloriseteyvat:teyvat}
 * with per-save {@code worldScale} support, stale-transition recovery, and explicit chat diagnostics.
 */
public final class CelestiaTeleportHandler {
    private static final String TAG_VISITED_TEYVAT = "veloriseteyvat_has_visited";
    private static final Map<UUID, PendingTransition> PENDING = new ConcurrentHashMap<>();

    private record PendingTransition(
            ResourceKey<Level> targetKey,
            int targetX,
            int targetY,
            int targetZ,
            int centerChunkX,
            int centerChunkZ,
            int startedTick,
            int forcedCount,
            boolean readySent
    ) {}

    private CelestiaTeleportHandler() {}

    public static void beginTransition(ServerPlayer player, BlockPos portalPos) {
        MinecraftServer server = player.getServer();
        if (server == null) {
            return;
        }

        // Recover automatically if a previous transition stalled (> 10s)
        PendingTransition existing = PENDING.get(player.getUUID());
        if (existing != null) {
            if (server.getTickCount() - existing.startedTick() < 200) {
                return;
            }
            PENDING.remove(player.getUUID());
            ServerLevel oldLevel = server.getLevel(existing.targetKey());
            if (oldLevel != null) {
                releaseForcedChunks(oldLevel, existing.centerChunkX(), existing.centerChunkZ());
            }
        }

        boolean goingToTeyvat = !player.level().dimension().equals(TeyvatDimensions.TEYVAT_LEVEL);
        ResourceKey<Level> targetKey = goingToTeyvat ? TeyvatDimensions.TEYVAT_LEVEL : Level.OVERWORLD;
        ServerLevel targetLevel = server.getLevel(targetKey);
        if (targetLevel == null) {
            player.sendSystemMessage(Component.literal(
                    "[Velorise's Teyvat] Dimension 'veloriseteyvat:teyvat' is not loaded in this save! "
                            + "Please create a new World (existing saves created before installing the mod do not auto-register new dimensions)."
            ).withStyle(ChatFormatting.RED));
            return;
        }

        int tx;
        int ty;
        int tz;
        if (goingToTeyvat) {
            double scale = RegionBarrierManager.get(targetLevel).getWorldScale();
            tx = TeyvatDimensions.scaledSpawnX(scale);
            ty = TeyvatDimensions.CANON_SPAWN_Y;
            tz = TeyvatDimensions.scaledSpawnZ(scale);
        } else {
            BlockPos spawn = player.getRespawnPosition();
            if (spawn == null) {
                spawn = targetLevel.getSharedSpawnPos();
            }
            tx = spawn.getX();
            ty = spawn.getY();
            tz = spawn.getZ();
        }

        boolean firstVisit = !player.getTags().contains(TAG_VISITED_TEYVAT);
        boolean fullCutscene = goingToTeyvat;

        int cx = tx >> 4;
        int cz = tz >> 4;

        PENDING.put(player.getUUID(), new PendingTransition(
                targetKey, tx, ty, tz, cx, cz, server.getTickCount(), 0, false
        ));

        // Send StartTransition immediately so the Celestia screen opens on click (0ms delay),
        // then preload the 3x3 destination chunks one per server tick below.
        CelestiaTransitionPackets.bridge().sendToPlayer(
                player,
                new CelestiaTransitionPackets.StartTransitionPayload(
                        targetKey.location(), fullCutscene || firstVisit, tx, ty, tz
                )
        );
    }

    public static void onServerTick(MinecraftServer server) {
        if (PENDING.isEmpty()) {
            return;
        }
        Iterator<Map.Entry<UUID, PendingTransition>> it = PENDING.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<UUID, PendingTransition> entry = it.next();
            ServerPlayer player = server.getPlayerList().getPlayer(entry.getKey());
            PendingTransition pt = entry.getValue();
            ServerLevel targetLevel = server.getLevel(pt.targetKey());

            if (player == null || targetLevel == null) {
                if (targetLevel != null) {
                    releaseForcedChunks(targetLevel, pt.centerChunkX(), pt.centerChunkZ());
                }
                it.remove();
                continue;
            }

            if (server.getTickCount() - pt.startedTick() > 600) {
                releaseForcedChunks(targetLevel, pt.centerChunkX(), pt.centerChunkZ());
                it.remove();
                continue;
            }

            if (pt.readySent()) {
                continue;
            }

            if (pt.forcedCount() < 9) {
                int idx = pt.forcedCount();
                int dx = (idx % 3) - 1;
                int dz = (idx / 3) - 1;
                try {
                    targetLevel.setChunkForced(pt.centerChunkX() + dx, pt.centerChunkZ() + dz, true);
                } catch (Throwable t) {
                    t.printStackTrace();
                    player.sendSystemMessage(Component.literal(
                            "[Velorise's Teyvat] Failed to preload destination chunk: " + t.getMessage()
                    ).withStyle(ChatFormatting.RED));
                    releaseForcedChunks(targetLevel, pt.centerChunkX(), pt.centerChunkZ());
                    it.remove();
                    continue;
                }
                int newCount = idx + 1;
                boolean ready = newCount >= 9;
                CelestiaTransitionPackets.bridge().sendToPlayer(
                        player,
                        new CelestiaTransitionPackets.PreloadProgressPayload(newCount / 9.0f, ready)
                );
                entry.setValue(new PendingTransition(
                        pt.targetKey(), pt.targetX(), pt.targetY(), pt.targetZ(),
                        pt.centerChunkX(), pt.centerChunkZ(), pt.startedTick(), newCount, ready
                ));
            }
        }
    }

    public static void handleCommitTeleport(ServerPlayer player, ResourceLocation requestedDim) {
        PendingTransition pt = PENDING.remove(player.getUUID());
        if (pt == null || !pt.targetKey().location().equals(requestedDim)) {
            return;
        }
        MinecraftServer server = player.getServer();
        if (server == null) {
            return;
        }
        ServerLevel targetLevel = server.getLevel(pt.targetKey());
        if (targetLevel == null) {
            return;
        }

        targetLevel.getChunk(pt.centerChunkX(), pt.centerChunkZ(), ChunkStatus.FULL, true);
        int surfaceY = targetLevel.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pt.targetX(), pt.targetZ());
        double safeY = Math.max(pt.targetY(), surfaceY + 0.1);

        float yaw = pt.targetKey().equals(TeyvatDimensions.TEYVAT_LEVEL) ? 115.0F : player.getYRot();
        player.teleportTo(targetLevel, pt.targetX() + 0.5, safeY, pt.targetZ() + 0.5, Set.of(), yaw, 0.0F);
        player.setDeltaMovement(0, 0, 0);
        player.fallDistance = 0.0F;

        if (pt.targetKey().equals(TeyvatDimensions.TEYVAT_LEVEL)) {
            player.addTag(TAG_VISITED_TEYVAT);
        }

        releaseForcedChunks(targetLevel, pt.centerChunkX(), pt.centerChunkZ());
    }

    public static void onPlayerDisconnect(ServerPlayer player) {
        PendingTransition pt = PENDING.remove(player.getUUID());
        if (pt != null && player.getServer() != null) {
            ServerLevel targetLevel = player.getServer().getLevel(pt.targetKey());
            if (targetLevel != null) {
                releaseForcedChunks(targetLevel, pt.centerChunkX(), pt.centerChunkZ());
            }
        }
    }

    public static void onServerStopping(MinecraftServer server) {
        if (PENDING.isEmpty()) {
            return;
        }
        for (PendingTransition pt : PENDING.values()) {
            ServerLevel targetLevel = server.getLevel(pt.targetKey());
            if (targetLevel != null) {
                releaseForcedChunks(targetLevel, pt.centerChunkX(), pt.centerChunkZ());
            }
        }
        PENDING.clear();
    }

    private static void releaseForcedChunks(ServerLevel level, int cx, int cz) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                level.setChunkForced(cx + dx, cz + dz, false);
            }
        }
    }
}

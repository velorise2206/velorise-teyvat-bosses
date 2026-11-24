package com.velorise.teyvat.fabric;

import com.velorise.teyvat.config.TeyvatWorldConfig;
import com.velorise.teyvat.fabric.network.FabricCelestiaNetwork;
import com.velorise.teyvat.landmark.MondstadtLandmarkPlacer;
import com.velorise.teyvat.landmark.StatueOfTheSevenBlock;
import com.velorise.teyvat.landmark.StatueOfTheSevenBlockEntity;
import com.velorise.teyvat.landmark.TeleportWaypointBlock;
import com.velorise.teyvat.landmark.TeleportWaypointBlockEntity;
import com.velorise.teyvat.portal.CelestiaPortalBlock;
import com.velorise.teyvat.portal.CelestiaPortalBlockEntity;
import com.velorise.teyvat.portal.CelestiaTeleportHandler;
import com.velorise.teyvat.worldgen.TeyvatBiomeSource;
import com.velorise.teyvat.worldgen.TeyvatDimensions;
import com.velorise.teyvat.worldgen.density.TeyvatDensityFunction;
import com.velorise.teyvat.worldgen.region.RegionBarrierManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public final class VeloriseTeyvatFabric implements ModInitializer {
    public static CelestiaPortalBlock CELESTIA_PORTAL;
    public static TeleportWaypointBlock TELEPORT_WAYPOINT;
    public static StatueOfTheSevenBlock STATUE_OF_THE_SEVEN;

    public static BlockEntityType<CelestiaPortalBlockEntity> CELESTIA_PORTAL_BE;
    public static BlockEntityType<TeleportWaypointBlockEntity> TELEPORT_WAYPOINT_BE;
    public static BlockEntityType<StatueOfTheSevenBlockEntity> STATUE_OF_THE_SEVEN_BE;

    @Override
    public void onInitialize() {
        TeyvatWorldConfig.loadOrCreate(FabricLoader.getInstance().getConfigDir());

        CELESTIA_PORTAL = Registry.register(BuiltInRegistries.BLOCK, TeyvatDimensions.id("celestia_portal"),
                new CelestiaPortalBlock(BlockBehaviour.Properties.of().strength(50.0F, 1200.0F).sound(SoundType.STONE).noOcclusion()));
        TELEPORT_WAYPOINT = Registry.register(BuiltInRegistries.BLOCK, TeyvatDimensions.id("teleport_waypoint"),
                new TeleportWaypointBlock(BlockBehaviour.Properties.of().strength(-1.0F, 3600000.0F).sound(SoundType.STONE).noOcclusion()));
        STATUE_OF_THE_SEVEN = Registry.register(BuiltInRegistries.BLOCK, TeyvatDimensions.id("statue_of_the_seven_mondstadt"),
                new StatueOfTheSevenBlock(BlockBehaviour.Properties.of().strength(-1.0F, 3600000.0F).sound(SoundType.STONE).noOcclusion()));

        Registry.register(BuiltInRegistries.ITEM, TeyvatDimensions.id("celestia_portal"),
                new BlockItem(CELESTIA_PORTAL, new Item.Properties()));
        Registry.register(BuiltInRegistries.ITEM, TeyvatDimensions.id("teleport_waypoint"),
                new BlockItem(TELEPORT_WAYPOINT, new Item.Properties()));
        Registry.register(BuiltInRegistries.ITEM, TeyvatDimensions.id("statue_of_the_seven_mondstadt"),
                new BlockItem(STATUE_OF_THE_SEVEN, new Item.Properties()));

        CELESTIA_PORTAL_BE = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, TeyvatDimensions.id("celestia_portal"),
                BlockEntityType.Builder.of(CelestiaPortalBlockEntity::new, CELESTIA_PORTAL).build(null));
        TELEPORT_WAYPOINT_BE = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, TeyvatDimensions.id("teleport_waypoint"),
                BlockEntityType.Builder.of(TeleportWaypointBlockEntity::new, TELEPORT_WAYPOINT).build(null));
        STATUE_OF_THE_SEVEN_BE = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, TeyvatDimensions.id("statue_of_the_seven_mondstadt"),
                BlockEntityType.Builder.of(StatueOfTheSevenBlockEntity::new, STATUE_OF_THE_SEVEN).build(null));

        Registry.register(BuiltInRegistries.BIOME_SOURCE, TeyvatDimensions.id("teyvat_biome_source"),
                TeyvatBiomeSource.CODEC);
        Registry.register(BuiltInRegistries.DENSITY_FUNCTION_TYPE, TeyvatDimensions.id("teyvat_density"),
                TeyvatDensityFunction.CODEC);

        CelestiaPortalBlock.bindBlockEntityType(() -> CELESTIA_PORTAL_BE);
        CelestiaPortalBlockEntity.bindType(() -> CELESTIA_PORTAL_BE);
        TeleportWaypointBlockEntity.bindType(() -> TELEPORT_WAYPOINT_BE);
        StatueOfTheSevenBlockEntity.bindType(() -> STATUE_OF_THE_SEVEN_BE);
        MondstadtLandmarkPlacer.bindBlocks(() -> TELEPORT_WAYPOINT, () -> STATUE_OF_THE_SEVEN);

        FabricCelestiaNetwork.registerCommon();

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            CelestiaTeleportHandler.onServerTick(server);
            ServerLevel teyvat = server.getLevel(TeyvatDimensions.TEYVAT_LEVEL);
            if (teyvat != null) {
                MondstadtLandmarkPlacer.tickPendingChunks(teyvat);
                RegionBarrierManager barrier = RegionBarrierManager.get(teyvat);
                for (ServerPlayer sp : teyvat.players()) {
                    barrier.enforceBarrier(sp);
                }
            }
        });

        ServerChunkEvents.CHUNK_LOAD.register((world, chunk) ->
                MondstadtLandmarkPlacer.onChunkLoaded(world, chunk.getPos()));

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) ->
                CelestiaTeleportHandler.onPlayerDisconnect(handler.getPlayer()));

        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            MondstadtLandmarkPlacer.clearPending();
            CelestiaTeleportHandler.onServerStopping(server);
        });
    }
}

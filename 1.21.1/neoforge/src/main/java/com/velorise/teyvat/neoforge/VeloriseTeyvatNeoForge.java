package com.velorise.teyvat.neoforge;

import com.mojang.serialization.MapCodec;
import com.velorise.teyvat.config.TeyvatWorldConfig;
import com.velorise.teyvat.landmark.MondstadtLandmarkPlacer;
import com.velorise.teyvat.landmark.StatueOfTheSevenBlock;
import com.velorise.teyvat.landmark.StatueOfTheSevenBlockEntity;
import com.velorise.teyvat.landmark.TeleportWaypointBlock;
import com.velorise.teyvat.landmark.TeleportWaypointBlockEntity;
import com.velorise.teyvat.neoforge.network.NeoForgeCelestiaNetwork;
import com.velorise.teyvat.portal.CelestiaPortalBlock;
import com.velorise.teyvat.portal.CelestiaPortalBlockEntity;
import com.velorise.teyvat.portal.CelestiaTeleportHandler;
import com.velorise.teyvat.worldgen.TeyvatBiomeSource;
import com.velorise.teyvat.worldgen.TeyvatDimensions;
import com.velorise.teyvat.worldgen.density.TeyvatDensityFunction;
import com.velorise.teyvat.worldgen.region.RegionBarrierManager;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(TeyvatDimensions.MOD_ID)
public final class VeloriseTeyvatNeoForge {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(Registries.BLOCK, TeyvatDimensions.NAMESPACE);
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(Registries.ITEM, TeyvatDimensions.NAMESPACE);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, TeyvatDimensions.NAMESPACE);
    public static final DeferredRegister<MapCodec<? extends BiomeSource>> BIOME_SOURCES =
            DeferredRegister.create(Registries.BIOME_SOURCE, TeyvatDimensions.NAMESPACE);
    public static final DeferredRegister<MapCodec<? extends DensityFunction>> DENSITY_FUNCTIONS =
            DeferredRegister.create(Registries.DENSITY_FUNCTION_TYPE, TeyvatDimensions.NAMESPACE);

    public static final DeferredHolder<Block, CelestiaPortalBlock> CELESTIA_PORTAL =
            BLOCKS.register("celestia_portal", () -> new CelestiaPortalBlock(
                    BlockBehaviour.Properties.of().strength(50.0F, 1200.0F).sound(SoundType.STONE).noOcclusion()));
    public static final DeferredHolder<Block, TeleportWaypointBlock> TELEPORT_WAYPOINT =
            BLOCKS.register("teleport_waypoint", () -> new TeleportWaypointBlock(
                    BlockBehaviour.Properties.of().strength(-1.0F, 3600000.0F).sound(SoundType.STONE).noOcclusion()));
    public static final DeferredHolder<Block, StatueOfTheSevenBlock> STATUE_OF_THE_SEVEN =
            BLOCKS.register("statue_of_the_seven_mondstadt", () -> new StatueOfTheSevenBlock(
                    BlockBehaviour.Properties.of().strength(-1.0F, 3600000.0F).sound(SoundType.STONE).noOcclusion()));

    public static final DeferredHolder<Item, BlockItem> CELESTIA_PORTAL_ITEM =
            ITEMS.register("celestia_portal", () -> new BlockItem(CELESTIA_PORTAL.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> TELEPORT_WAYPOINT_ITEM =
            ITEMS.register("teleport_waypoint", () -> new BlockItem(TELEPORT_WAYPOINT.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> STATUE_OF_THE_SEVEN_ITEM =
            ITEMS.register("statue_of_the_seven_mondstadt", () -> new BlockItem(STATUE_OF_THE_SEVEN.get(), new Item.Properties()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CelestiaPortalBlockEntity>> CELESTIA_PORTAL_BE =
            BLOCK_ENTITIES.register("celestia_portal", () -> BlockEntityType.Builder.of(
                    CelestiaPortalBlockEntity::new, CELESTIA_PORTAL.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TeleportWaypointBlockEntity>> TELEPORT_WAYPOINT_BE =
            BLOCK_ENTITIES.register("teleport_waypoint", () -> BlockEntityType.Builder.of(
                    TeleportWaypointBlockEntity::new, TELEPORT_WAYPOINT.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<StatueOfTheSevenBlockEntity>> STATUE_OF_THE_SEVEN_BE =
            BLOCK_ENTITIES.register("statue_of_the_seven_mondstadt", () -> BlockEntityType.Builder.of(
                    StatueOfTheSevenBlockEntity::new, STATUE_OF_THE_SEVEN.get()).build(null));

    public static final DeferredHolder<MapCodec<? extends BiomeSource>, MapCodec<TeyvatBiomeSource>> TEYVAT_BIOME_SOURCE =
            BIOME_SOURCES.register("teyvat_biome_source", () -> TeyvatBiomeSource.CODEC);
    public static final DeferredHolder<MapCodec<? extends DensityFunction>, MapCodec<TeyvatDensityFunction>> TEYVAT_DENSITY =
            DENSITY_FUNCTIONS.register("teyvat_density", () -> TeyvatDensityFunction.CODEC);

    public VeloriseTeyvatNeoForge(IEventBus modBus) {
        TeyvatWorldConfig.loadOrCreate(FMLPaths.CONFIGDIR.get());

        BLOCKS.register(modBus);
        ITEMS.register(modBus);
        BLOCK_ENTITIES.register(modBus);
        BIOME_SOURCES.register(modBus);
        DENSITY_FUNCTIONS.register(modBus);

        CelestiaPortalBlock.bindBlockEntityType(CELESTIA_PORTAL_BE);
        CelestiaPortalBlockEntity.bindType(CELESTIA_PORTAL_BE);
        TeleportWaypointBlockEntity.bindType(TELEPORT_WAYPOINT_BE);
        StatueOfTheSevenBlockEntity.bindType(STATUE_OF_THE_SEVEN_BE);
        MondstadtLandmarkPlacer.bindBlocks(TELEPORT_WAYPOINT::get, STATUE_OF_THE_SEVEN::get);

        NeoForgeCelestiaNetwork.initBridge();
        modBus.addListener(NeoForgeCelestiaNetwork::onRegisterPayloads);

        NeoForge.EVENT_BUS.addListener(this::onServerTick);
        NeoForge.EVENT_BUS.addListener(this::onChunkLoad);
        NeoForge.EVENT_BUS.addListener(this::onPlayerLogout);
        NeoForge.EVENT_BUS.addListener(this::onServerStopping);
    }

    private void onServerTick(ServerTickEvent.Post event) {
        CelestiaTeleportHandler.onServerTick(event.getServer());
        ServerLevel teyvat = event.getServer().getLevel(TeyvatDimensions.TEYVAT_LEVEL);
        if (teyvat != null) {
            MondstadtLandmarkPlacer.tickPendingChunks(teyvat);
            RegionBarrierManager barrier = RegionBarrierManager.get(teyvat);
            for (ServerPlayer sp : teyvat.players()) {
                barrier.enforceBarrier(sp);
            }
        }
    }

    private void onChunkLoad(ChunkEvent.Load event) {
        if (event.getLevel() instanceof ServerLevel sl) {
            MondstadtLandmarkPlacer.onChunkLoaded(sl, event.getChunk().getPos());
        }
    }

    private void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer sp) {
            CelestiaTeleportHandler.onPlayerDisconnect(sp);
        }
    }

    private void onServerStopping(ServerStoppingEvent event) {
        MondstadtLandmarkPlacer.clearPending();
        CelestiaTeleportHandler.onServerStopping(event.getServer());
    }
}

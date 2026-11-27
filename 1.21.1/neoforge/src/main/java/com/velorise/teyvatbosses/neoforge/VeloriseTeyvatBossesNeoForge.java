package com.velorise.teyvatbosses.neoforge;

import com.velorise.teyvatbosses.TeyvatBossesConstants;
import com.velorise.teyvatbosses.block.BossTrophyBlock;
import com.velorise.teyvatbosses.block.BossTrophyBlockEntity;
import com.velorise.teyvatbosses.entity.AnemoCompanionEntity;
import com.velorise.teyvatbosses.entity.AnemoHypostasisEntity;
import com.velorise.teyvatbosses.entity.AnemoMissileProjectile;
import com.velorise.teyvatbosses.entity.AnemoTornadoEntity;
import com.velorise.teyvatbosses.entity.AnemoWindOrbEntity;
import com.velorise.teyvatbosses.entity.BossRespawnAnchorEntity;
import com.velorise.teyvatbosses.entity.CryoIceSpikeEntity;
import com.velorise.teyvatbosses.entity.CryoIcicleProjectile;
import com.velorise.teyvatbosses.entity.CryoRegisvineEntity;
import com.velorise.teyvatbosses.entity.CryoTurretEntity;
import com.velorise.teyvatbosses.entity.GeoDrillerEntity;
import com.velorise.teyvatbosses.entity.PrimoGeovishapEntity;
import com.velorise.teyvatbosses.entity.PrimoGeovishapPartEntity;
import com.velorise.teyvatbosses.entity.PrimoInfusedStoneEntity;
import com.velorise.teyvatbosses.entity.RuinGuardEntity;
import com.velorise.teyvatbosses.entity.RuinMechEntity;
import com.velorise.teyvatbosses.entity.RuinMissileProjectile;
import com.velorise.teyvatbosses.entity.RuinNautilusEntity;
import com.velorise.teyvatbosses.entity.RuinSubmersibleEntity;
import com.velorise.teyvatbosses.entity.RuinTorpedoProjectile;
import com.velorise.teyvatbosses.entity.TrounceBlossomEntity;
import com.velorise.teyvatbosses.entity.WindCurrentEntity;
import com.velorise.teyvatbosses.item.AnemoBootsItem;
import com.velorise.teyvatbosses.item.AnemoPrismCubeItem;
import com.velorise.teyvatbosses.network.AnemoDoubleJumpPayload;
import com.velorise.teyvatbosses.item.CryoTurretPartItem;
import com.velorise.teyvatbosses.item.GeoDrillerPartItem;
import com.velorise.teyvatbosses.item.GeovishapArmorItem;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import com.velorise.teyvatbosses.item.RuinMechPartItem;
import com.velorise.teyvatbosses.config.TeyvatBossesConfig;
import com.velorise.teyvatbosses.item.RuinSubPartItem;
import com.velorise.teyvatbosses.item.TeyvatBossLocatorItem;
import com.velorise.teyvatbosses.network.GeoDrillerControlPayload;
import com.velorise.teyvatbosses.network.RuinMechSkillPayload;
import com.velorise.teyvatbosses.network.RuinSubControlPayload;
import com.velorise.teyvatbosses.registry.TeyvatBossesBlocks;
import com.velorise.teyvatbosses.registry.TeyvatBossesEntities;
import com.velorise.teyvatbosses.registry.TeyvatBossesItems;
import com.velorise.teyvatbosses.registry.TeyvatBossesSounds;
import com.velorise.teyvatbosses.registry.TeyvatBossesStructures;
import com.velorise.teyvatbosses.worldgen.FlatArenaStructure;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PoweredBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(TeyvatBossesConstants.MOD_ID)
public class VeloriseTeyvatBossesNeoForge {

    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(Registries.ENTITY_TYPE, TeyvatBossesConstants.MOD_ID);
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(Registries.BLOCK, TeyvatBossesConstants.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, TeyvatBossesConstants.MOD_ID);
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(Registries.ITEM, TeyvatBossesConstants.MOD_ID);
    public static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS =
            DeferredRegister.create(Registries.ARMOR_MATERIAL, TeyvatBossesConstants.MOD_ID);
    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(Registries.SOUND_EVENT, TeyvatBossesConstants.MOD_ID);
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, TeyvatBossesConstants.MOD_ID);
    public static final DeferredRegister<StructureType<?>> STRUCTURE_TYPES =
            DeferredRegister.create(Registries.STRUCTURE_TYPE, TeyvatBossesConstants.MOD_ID);

    private static BlockBehaviour.Properties trophyProps() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.METAL)
                .strength(2.0F, 6.0F)
                .sound(SoundType.METAL)
                .noOcclusion()
                .lightLevel(state -> 6);
    }

    public static final DeferredHolder<StructureType<?>, StructureType<FlatArenaStructure>> FLAT_ARENA =
            STRUCTURE_TYPES.register("flat_arena", () -> () -> FlatArenaStructure.CODEC);

    public static final DeferredHolder<EntityType<?>, EntityType<CryoRegisvineEntity>> CRYO_REGISVINE =
            ENTITIES.register("cryo_regisvine", () -> EntityType.Builder.of(CryoRegisvineEntity::new, MobCategory.MONSTER)
                    .sized(3.4F, 6.2F).clientTrackingRange(12).build("cryo_regisvine"));
    public static final DeferredHolder<EntityType<?>, EntityType<CryoIcicleProjectile>> CRYO_ICICLE =
            ENTITIES.register("cryo_icicle", () -> EntityType.Builder.<CryoIcicleProjectile>of(CryoIcicleProjectile::new, MobCategory.MISC)
                    .sized(0.55F, 0.55F).clientTrackingRange(10).updateInterval(2).build("cryo_icicle"));
    public static final DeferredHolder<EntityType<?>, EntityType<CryoIceSpikeEntity>> CRYO_ICE_SPIKE =
            ENTITIES.register("cryo_ice_spike", () -> EntityType.Builder.<CryoIceSpikeEntity>of(CryoIceSpikeEntity::new, MobCategory.MISC)
                    .sized(1.6F, 3.6F).clientTrackingRange(10).updateInterval(2).build("cryo_ice_spike"));
    public static final DeferredHolder<EntityType<?>, EntityType<BossRespawnAnchorEntity>> BOSS_RESPAWN_ANCHOR =
            ENTITIES.register("boss_respawn_anchor", () -> EntityType.Builder.<BossRespawnAnchorEntity>of(BossRespawnAnchorEntity::new, MobCategory.MISC)
                    .sized(1.4F, 2.2F).clientTrackingRange(10).updateInterval(10).build("boss_respawn_anchor"));

    public static final DeferredHolder<EntityType<?>, EntityType<AnemoHypostasisEntity>> ANEMO_HYPOSTASIS =
            ENTITIES.register("anemo_hypostasis", () -> EntityType.Builder.of(AnemoHypostasisEntity::new, MobCategory.MONSTER)
                    .sized(2.6F, 3.2F).clientTrackingRange(14).build("anemo_hypostasis"));
    public static final DeferredHolder<EntityType<?>, EntityType<AnemoWindOrbEntity>> ANEMO_WIND_ORB =
            ENTITIES.register("anemo_wind_orb", () -> EntityType.Builder.<AnemoWindOrbEntity>of(AnemoWindOrbEntity::new, MobCategory.MISC)
                    .sized(1.2F, 1.2F).clientTrackingRange(12).updateInterval(2).build("anemo_wind_orb"));
    public static final DeferredHolder<EntityType<?>, EntityType<AnemoTornadoEntity>> ANEMO_TORNADO =
            ENTITIES.register("anemo_tornado", () -> EntityType.Builder.<AnemoTornadoEntity>of(AnemoTornadoEntity::new, MobCategory.MISC)
                    .sized(2.4F, 6.0F).clientTrackingRange(14).updateInterval(2).build("anemo_tornado"));
    public static final DeferredHolder<EntityType<?>, EntityType<AnemoMissileProjectile>> ANEMO_MISSILE =
            ENTITIES.register("anemo_missile", () -> EntityType.Builder.<AnemoMissileProjectile>of(AnemoMissileProjectile::new, MobCategory.MISC)
                    .sized(0.9F, 0.9F).clientTrackingRange(12).updateInterval(2).build("anemo_missile"));
    public static final DeferredHolder<EntityType<?>, EntityType<WindCurrentEntity>> WIND_CURRENT =
            ENTITIES.register("wind_current", () -> EntityType.Builder.<WindCurrentEntity>of(WindCurrentEntity::new, MobCategory.MISC)
                    .sized(4.5F, 9.5F).clientTrackingRange(14).updateInterval(4).build("wind_current"));
    public static final DeferredHolder<EntityType<?>, EntityType<TrounceBlossomEntity>> TROUNCE_BLOSSOM =
            ENTITIES.register("trounce_blossom", () -> EntityType.Builder.<TrounceBlossomEntity>of(TrounceBlossomEntity::new, MobCategory.MISC)
                    .sized(1.5F, 2.2F).clientTrackingRange(12).updateInterval(4).build("trounce_blossom"));

    public static final DeferredHolder<EntityType<?>, EntityType<RuinGuardEntity>> RUIN_GUARD =
            ENTITIES.register("ruin_guard", () -> EntityType.Builder.of(RuinGuardEntity::new, MobCategory.MONSTER)
                    .sized(2.4F, 4.0F).clientTrackingRange(14).build("ruin_guard"));
    public static final DeferredHolder<EntityType<?>, EntityType<RuinMissileProjectile>> RUIN_MISSILE =
            ENTITIES.register("ruin_missile", () -> EntityType.Builder.<RuinMissileProjectile>of(RuinMissileProjectile::new, MobCategory.MISC)
                    .sized(0.75F, 0.75F).clientTrackingRange(12).updateInterval(2).build("ruin_missile"));
    public static final DeferredHolder<EntityType<?>, EntityType<RuinMechEntity>> RUIN_MECH =
            ENTITIES.register("ruin_mech", () -> EntityType.Builder.of(RuinMechEntity::new, MobCategory.MISC)
                    .sized(2.4F, 4.0F).clientTrackingRange(14).build("ruin_mech"));
    public static final DeferredHolder<EntityType<?>, EntityType<CryoTurretEntity>> CRYO_TURRET =
            ENTITIES.register("cryo_turret", () -> EntityType.Builder.of(CryoTurretEntity::new, MobCategory.MISC)
                    .sized(2.6F, 5.6F).clientTrackingRange(12).build("cryo_turret"));
    public static final DeferredHolder<EntityType<?>, EntityType<RuinNautilusEntity>> RUIN_NAUTILUS =
            ENTITIES.register("ruin_nautilus", () -> EntityType.Builder.of(RuinNautilusEntity::new, MobCategory.MONSTER)
                    .sized(4.6F, 3.8F).clientTrackingRange(16).build("ruin_nautilus"));
    public static final DeferredHolder<EntityType<?>, EntityType<RuinTorpedoProjectile>> RUIN_TORPEDO =
            ENTITIES.register("ruin_torpedo", () -> EntityType.Builder.<RuinTorpedoProjectile>of(RuinTorpedoProjectile::new, MobCategory.MISC)
                    .sized(0.7F, 0.7F).clientTrackingRange(12).updateInterval(2).build("ruin_torpedo"));
    public static final DeferredHolder<EntityType<?>, EntityType<RuinSubmersibleEntity>> RUIN_SUBMERSIBLE =
            ENTITIES.register("ruin_submersible", () -> EntityType.Builder.of(RuinSubmersibleEntity::new, MobCategory.MISC)
                    .sized(3.4F, 2.6F).clientTrackingRange(14).build("ruin_submersible"));
    public static final DeferredHolder<EntityType<?>, EntityType<AnemoCompanionEntity>> ANEMO_COMPANION =
            ENTITIES.register("anemo_companion", () -> EntityType.Builder.of(AnemoCompanionEntity::new, MobCategory.MISC)
                    .sized(0.45F, 0.45F).clientTrackingRange(12).build("anemo_companion"));
    public static final DeferredHolder<EntityType<?>, EntityType<PrimoGeovishapEntity>> PRIMO_GEOVISHAP =
            ENTITIES.register("primo_geovishap", () -> EntityType.Builder.of(PrimoGeovishapEntity::new, MobCategory.MONSTER)
                    .sized(4.4F, 4.0F).clientTrackingRange(16).build("primo_geovishap"));
    public static final DeferredHolder<EntityType<?>, EntityType<GeoDrillerEntity>> GEO_DRILLER =
            ENTITIES.register("geo_driller", () -> EntityType.Builder.of(GeoDrillerEntity::new, MobCategory.MISC)
                    .sized(2.4F, 2.1F).clientTrackingRange(14).build("geo_driller"));
    public static final DeferredHolder<EntityType<?>, EntityType<PrimoInfusedStoneEntity>> PRIMO_INFUSED_STONE =
            ENTITIES.register("primo_infused_stone", () -> EntityType.Builder.<PrimoInfusedStoneEntity>of(PrimoInfusedStoneEntity::new, MobCategory.MISC)
                    .sized(1.3F, 2.2F).clientTrackingRange(14).updateInterval(2).build("primo_infused_stone"));
    public static final DeferredHolder<EntityType<?>, EntityType<PrimoGeovishapPartEntity>> PRIMO_GEOVISHAP_PART =
            ENTITIES.register("primo_geovishap_part", () -> EntityType.Builder.<PrimoGeovishapPartEntity>of(PrimoGeovishapPartEntity::new, MobCategory.MISC)
                    .sized(2.4F, 2.2F).noSave().noSummon().clientTrackingRange(16).updateInterval(1).build("primo_geovishap_part"));

    // 5 3D Miniature Boss Trophy Blocks
    public static final DeferredHolder<Block, BossTrophyBlock> CRYO_REGISVINE_TROPHY =
            BLOCKS.register("cryo_regisvine_trophy", () -> new BossTrophyBlock(BossRespawnAnchorEntity.BOSS_CRYO_REGISVINE, trophyProps()));
    public static final DeferredHolder<Block, BossTrophyBlock> ANEMO_HYPOSTASIS_TROPHY =
            BLOCKS.register("anemo_hypostasis_trophy", () -> new BossTrophyBlock(BossRespawnAnchorEntity.BOSS_ANEMO_HYPOSTASIS, trophyProps()));
    public static final DeferredHolder<Block, BossTrophyBlock> RUIN_GUARD_TROPHY =
            BLOCKS.register("ruin_guard_trophy", () -> new BossTrophyBlock(BossRespawnAnchorEntity.BOSS_RUIN_GUARD, trophyProps()));
    public static final DeferredHolder<Block, BossTrophyBlock> RUIN_NAUTILUS_TROPHY =
            BLOCKS.register("ruin_nautilus_trophy", () -> new BossTrophyBlock(BossRespawnAnchorEntity.BOSS_RUIN_NAUTILUS, trophyProps()));
    public static final DeferredHolder<Block, BossTrophyBlock> PRIMO_GEOVISHAP_TROPHY =
            BLOCKS.register("primo_geovishap_trophy", () -> new BossTrophyBlock(BossRespawnAnchorEntity.BOSS_PRIMO_GEOVISHAP, trophyProps()));
    public static final DeferredHolder<Block, Block> CONDENSED_REDSTONE_BLOCK =
            BLOCKS.register("condensed_redstone_block", () -> new PoweredBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.REDSTONE_BLOCK).strength(7.0F, 9.0F)));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BossTrophyBlockEntity>> BOSS_TROPHY_BE =
            BLOCK_ENTITIES.register("boss_trophy", () -> BlockEntityType.Builder.of(
                    BossTrophyBlockEntity::new,
                    CRYO_REGISVINE_TROPHY.get(), ANEMO_HYPOSTASIS_TROPHY.get(),
                    RUIN_GUARD_TROPHY.get(), RUIN_NAUTILUS_TROPHY.get(), PRIMO_GEOVISHAP_TROPHY.get()
            ).build(null));

    // Boss Trophy Block Items
    public static final DeferredHolder<Item, Item> CRYO_REGISVINE_TROPHY_ITEM =
            ITEMS.register("cryo_regisvine_trophy", () -> new BlockItem(CRYO_REGISVINE_TROPHY.get(), new Item.Properties().rarity(Rarity.EPIC)));
    public static final DeferredHolder<Item, Item> ANEMO_HYPOSTASIS_TROPHY_ITEM =
            ITEMS.register("anemo_hypostasis_trophy", () -> new BlockItem(ANEMO_HYPOSTASIS_TROPHY.get(), new Item.Properties().rarity(Rarity.EPIC)));
    public static final DeferredHolder<Item, Item> RUIN_GUARD_TROPHY_ITEM =
            ITEMS.register("ruin_guard_trophy", () -> new BlockItem(RUIN_GUARD_TROPHY.get(), new Item.Properties().rarity(Rarity.EPIC)));
    public static final DeferredHolder<Item, Item> RUIN_NAUTILUS_TROPHY_ITEM =
            ITEMS.register("ruin_nautilus_trophy", () -> new BlockItem(RUIN_NAUTILUS_TROPHY.get(), new Item.Properties().rarity(Rarity.EPIC)));
    public static final DeferredHolder<Item, Item> PRIMO_GEOVISHAP_TROPHY_ITEM =
            ITEMS.register("primo_geovishap_trophy", () -> new BlockItem(PRIMO_GEOVISHAP_TROPHY.get(), new Item.Properties().rarity(Rarity.EPIC)));
    public static final DeferredHolder<Item, Item> CONDENSED_REDSTONE_BLOCK_ITEM =
            ITEMS.register("condensed_redstone_block", () -> new BlockItem(CONDENSED_REDSTONE_BLOCK.get(), new Item.Properties().rarity(Rarity.UNCOMMON)));

    // Modular Salvage Parts & Gear
    public static final DeferredHolder<Item, Item> RUIN_MECH_CORE =
            ITEMS.register("ruin_mech_core", () -> new RuinMechPartItem(RuinMechEntity.PART_CORE, new Item.Properties().stacksTo(1).rarity(Rarity.EPIC)));
    public static final DeferredHolder<Item, Item> RUIN_MECH_LEFT_ARM =
            ITEMS.register("ruin_mech_left_arm", () -> new RuinMechPartItem(RuinMechEntity.PART_LEFT_ARM, new Item.Properties().stacksTo(1).rarity(Rarity.RARE)));
    public static final DeferredHolder<Item, Item> RUIN_MECH_RIGHT_ARM =
            ITEMS.register("ruin_mech_right_arm", () -> new RuinMechPartItem(RuinMechEntity.PART_RIGHT_ARM, new Item.Properties().stacksTo(1).rarity(Rarity.RARE)));
    public static final DeferredHolder<Item, Item> RUIN_MECH_LEFT_LEG =
            ITEMS.register("ruin_mech_left_leg", () -> new RuinMechPartItem(RuinMechEntity.PART_LEFT_LEG, new Item.Properties().stacksTo(1).rarity(Rarity.RARE)));
    public static final DeferredHolder<Item, Item> RUIN_MECH_RIGHT_LEG =
            ITEMS.register("ruin_mech_right_leg", () -> new RuinMechPartItem(RuinMechEntity.PART_RIGHT_LEG, new Item.Properties().stacksTo(1).rarity(Rarity.RARE)));
    public static final DeferredHolder<Item, Item> CRYO_TURRET_ROOT =
            ITEMS.register("cryo_turret_root", () -> new CryoTurretPartItem(CryoTurretEntity.PART_ROOT, new Item.Properties().stacksTo(1).rarity(Rarity.RARE)));
    public static final DeferredHolder<Item, Item> CRYO_TURRET_STEM =
            ITEMS.register("cryo_turret_stem", () -> new CryoTurretPartItem(CryoTurretEntity.PART_STEM, new Item.Properties().stacksTo(1).rarity(Rarity.RARE)));
    public static final DeferredHolder<Item, Item> CRYO_TURRET_COROLLA =
            ITEMS.register("cryo_turret_corolla", () -> new CryoTurretPartItem(CryoTurretEntity.PART_COROLLA, new Item.Properties().stacksTo(1).rarity(Rarity.RARE)));
    public static final DeferredHolder<Item, Item> ANEMO_PRISM_CUBE =
            ITEMS.register("anemo_prism_cube", () -> new AnemoPrismCubeItem(new Item.Properties().stacksTo(16).rarity(Rarity.RARE)));
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> ANEMO_ARMOR_MATERIAL =
            ARMOR_MATERIALS.register("anemo", AnemoBootsItem::createArmorMaterial);
    public static final DeferredHolder<Item, Item> ANEMO_BOOTS =
            ITEMS.register("anemo_boots", () -> new AnemoBootsItem(ANEMO_ARMOR_MATERIAL, new Item.Properties()));
    public static final DeferredHolder<Item, Item> RUIN_SUB_HULL =
            ITEMS.register("ruin_sub_hull", () -> new RuinSubPartItem(RuinSubmersibleEntity.PART_HULL, new Item.Properties().stacksTo(1).rarity(Rarity.EPIC)));
    public static final DeferredHolder<Item, Item> RUIN_SUB_PROPELLER =
            ITEMS.register("ruin_sub_propeller", () -> new RuinSubPartItem(RuinSubmersibleEntity.PART_PROPELLER, new Item.Properties().stacksTo(1).rarity(Rarity.RARE)));
    public static final DeferredHolder<Item, Item> RUIN_SUB_TORPEDO_POD =
            ITEMS.register("ruin_sub_torpedo_pod", () -> new RuinSubPartItem(RuinSubmersibleEntity.PART_TORPEDO_POD, new Item.Properties().stacksTo(1).rarity(Rarity.RARE)));
    public static final DeferredHolder<Item, Item> GEO_DRILLER_CHASSIS =
            ITEMS.register("geo_driller_chassis", () -> new GeoDrillerPartItem(GeoDrillerEntity.PART_CHASSIS, new Item.Properties().stacksTo(1).rarity(Rarity.EPIC)));
    public static final DeferredHolder<Item, Item> GEO_DRILLER_DRILL =
            ITEMS.register("geo_driller_drill", () -> new GeoDrillerPartItem(GeoDrillerEntity.PART_DRILL, new Item.Properties().stacksTo(1).rarity(Rarity.RARE)));
    public static final DeferredHolder<Item, Item> GEO_DRILLER_ENGINE =
            ITEMS.register("geo_driller_engine", () -> new GeoDrillerPartItem(GeoDrillerEntity.PART_ENGINE, new Item.Properties().stacksTo(1).rarity(Rarity.RARE)));

    // Primo Geovishap Primordial Jade, Geo Scale Ingot & Dragon-Scale Armor
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> GEOVISHAP_ARMOR_MATERIAL =
            ARMOR_MATERIALS.register("geovishap", GeovishapArmorItem::createArmorMaterial);
    public static final DeferredHolder<Item, Item> PRIMORDIAL_JADE =
            ITEMS.register("primordial_jade", () -> new Item(new Item.Properties().fireResistant().rarity(Rarity.EPIC)));
    public static final DeferredHolder<Item, Item> PRIMORDIAL_GEO_INGOT =
            ITEMS.register("primordial_geo_ingot", () -> new Item(new Item.Properties().fireResistant().rarity(Rarity.RARE)));
    public static final DeferredHolder<Item, Item> GEOVISHAP_HELMET =
            ITEMS.register("geovishap_helmet", () -> new GeovishapArmorItem(GEOVISHAP_ARMOR_MATERIAL, ArmorItem.Type.HELMET, new Item.Properties()));
    public static final DeferredHolder<Item, Item> GEOVISHAP_CHESTPLATE =
            ITEMS.register("geovishap_chestplate", () -> new GeovishapArmorItem(GEOVISHAP_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE, new Item.Properties()));
    public static final DeferredHolder<Item, Item> GEOVISHAP_LEGGINGS =
            ITEMS.register("geovishap_leggings", () -> new GeovishapArmorItem(GEOVISHAP_ARMOR_MATERIAL, ArmorItem.Type.LEGGINGS, new Item.Properties()));
    public static final DeferredHolder<Item, Item> GEOVISHAP_BOOTS =
            ITEMS.register("geovishap_boots", () -> new GeovishapArmorItem(GEOVISHAP_ARMOR_MATERIAL, ArmorItem.Type.BOOTS, new Item.Properties()));

    // Teyvat Boss Locator Compass
    public static final DeferredHolder<Item, Item> CRYO_REGISVINE_COMPASS =
            ITEMS.register("cryo_regisvine_compass", () -> new TeyvatBossLocatorItem(new Item.Properties().stacksTo(1).rarity(Rarity.RARE), 0));
    public static final DeferredHolder<Item, Item> ANEMO_HYPOSTASIS_COMPASS =
            ITEMS.register("anemo_hypostasis_compass", () -> new TeyvatBossLocatorItem(new Item.Properties().stacksTo(1).rarity(Rarity.RARE), 1));
    public static final DeferredHolder<Item, Item> RUIN_GUARD_COMPASS =
            ITEMS.register("ruin_guard_compass", () -> new TeyvatBossLocatorItem(new Item.Properties().stacksTo(1).rarity(Rarity.RARE), 2));
    public static final DeferredHolder<Item, Item> RUIN_NAUTILUS_COMPASS =
            ITEMS.register("ruin_nautilus_compass", () -> new TeyvatBossLocatorItem(new Item.Properties().stacksTo(1).rarity(Rarity.RARE), 3));
    public static final DeferredHolder<Item, Item> PRIMO_GEOVISHAP_COMPASS =
            ITEMS.register("primo_geovishap_compass", () -> new TeyvatBossLocatorItem(new Item.Properties().stacksTo(1).rarity(Rarity.RARE), 4));

    // 5 Boss Spawn Eggs
    public static final DeferredHolder<Item, Item> CRYO_REGISVINE_SPAWN_EGG =
            ITEMS.register("cryo_regisvine_spawn_egg", () -> new DeferredSpawnEggItem(
                    CRYO_REGISVINE, 0x1E4B7A, 0x9BE8FF, new Item.Properties()));
    public static final DeferredHolder<Item, Item> ANEMO_HYPOSTASIS_SPAWN_EGG =
            ITEMS.register("anemo_hypostasis_spawn_egg", () -> new DeferredSpawnEggItem(
                    ANEMO_HYPOSTASIS, 0x1E3B42, 0x74F9DF, new Item.Properties()));
    public static final DeferredHolder<Item, Item> RUIN_GUARD_SPAWN_EGG =
            ITEMS.register("ruin_guard_spawn_egg", () -> new DeferredSpawnEggItem(
                    RUIN_GUARD, 0x4E3C2B, 0xFFB338, new Item.Properties()));
    public static final DeferredHolder<Item, Item> RUIN_NAUTILUS_SPAWN_EGG =
            ITEMS.register("ruin_nautilus_spawn_egg", () -> new DeferredSpawnEggItem(
                    RUIN_NAUTILUS, 0x1A3244, 0x22D3EE, new Item.Properties()));
    public static final DeferredHolder<Item, Item> PRIMO_GEOVISHAP_SPAWN_EGG =
            ITEMS.register("primo_geovishap_spawn_egg", () -> new DeferredSpawnEggItem(
                    PRIMO_GEOVISHAP, 0x4A3828, 0xF5B841, new Item.Properties()));

    // Sounds
    public static final DeferredHolder<SoundEvent, SoundEvent> SOUND_CRYO_THEME =
            SOUNDS.register("music.cryo_regisvine_theme", () -> SoundEvent.createVariableRangeEvent(TeyvatBossesConstants.id("music.cryo_regisvine_theme")));
    public static final DeferredHolder<SoundEvent, SoundEvent> SOUND_ANEMO_THEME =
            SOUNDS.register("music.anemo_hypostasis_theme", () -> SoundEvent.createVariableRangeEvent(TeyvatBossesConstants.id("music.anemo_hypostasis_theme")));
    public static final DeferredHolder<SoundEvent, SoundEvent> SOUND_RUIN_GUARD_THEME =
            SOUNDS.register("music.ruin_guard_theme", () -> SoundEvent.createVariableRangeEvent(TeyvatBossesConstants.id("music.ruin_guard_theme")));
    public static final DeferredHolder<SoundEvent, SoundEvent> SOUND_RUIN_NAUTILUS_THEME =
            SOUNDS.register("music.ruin_nautilus_theme", () -> SoundEvent.createVariableRangeEvent(TeyvatBossesConstants.id("music.ruin_nautilus_theme")));
    public static final DeferredHolder<SoundEvent, SoundEvent> SOUND_PRIMO_GEOVISHAP_THEME =
            SOUNDS.register("music.primo_geovishap_theme", () -> SoundEvent.createVariableRangeEvent(TeyvatBossesConstants.id("music.primo_geovishap_theme")));
    public static final DeferredHolder<SoundEvent, SoundEvent> SOUND_MECH_ENGINE_HUM =
            SOUNDS.register("sfx.mech_engine_hum", () -> SoundEvent.createVariableRangeEvent(TeyvatBossesConstants.id("sfx.mech_engine_hum")));
    public static final DeferredHolder<SoundEvent, SoundEvent> SOUND_MECH_LOCKON_BEEP =
            SOUNDS.register("sfx.mech_lockon_beep", () -> SoundEvent.createVariableRangeEvent(TeyvatBossesConstants.id("sfx.mech_lockon_beep")));
    public static final DeferredHolder<SoundEvent, SoundEvent> SOUND_MECH_VISOR_SEAL =
            SOUNDS.register("sfx.mech_visor_seal", () -> SoundEvent.createVariableRangeEvent(TeyvatBossesConstants.id("sfx.mech_visor_seal")));
    public static final DeferredHolder<SoundEvent, SoundEvent> SOUND_MECH_SERVO_WHIR =
            SOUNDS.register("sfx.mech_servo_whir", () -> SoundEvent.createVariableRangeEvent(TeyvatBossesConstants.id("sfx.mech_servo_whir")));
    public static final DeferredHolder<SoundEvent, SoundEvent> SOUND_MECH_JOINT_CREAK =
            SOUNDS.register("sfx.mech_joint_creak", () -> SoundEvent.createVariableRangeEvent(TeyvatBossesConstants.id("sfx.mech_joint_creak")));
    public static final DeferredHolder<SoundEvent, SoundEvent> SOUND_MECH_METAL_CLANK =
            SOUNDS.register("sfx.mech_metal_clank", () -> SoundEvent.createVariableRangeEvent(TeyvatBossesConstants.id("sfx.mech_metal_clank")));
    public static final DeferredHolder<SoundEvent, SoundEvent> SOUND_MECH_HYDRAULIC_HISS =
            SOUNDS.register("sfx.mech_hydraulic_hiss", () -> SoundEvent.createVariableRangeEvent(TeyvatBossesConstants.id("sfx.mech_hydraulic_hiss")));
    public static final DeferredHolder<SoundEvent, SoundEvent> SOUND_SUB_PROPELLER_HUM =
            SOUNDS.register("sfx.sub_propeller_hum", () -> SoundEvent.createVariableRangeEvent(TeyvatBossesConstants.id("sfx.sub_propeller_hum")));
    public static final DeferredHolder<SoundEvent, SoundEvent> SOUND_SUB_SONAR_PING =
            SOUNDS.register("sfx.sub_sonar_ping", () -> SoundEvent.createVariableRangeEvent(TeyvatBossesConstants.id("sfx.sub_sonar_ping")));
    public static final DeferredHolder<SoundEvent, SoundEvent> SOUND_SUB_TORPEDO_LAUNCH =
            SOUNDS.register("sfx.sub_torpedo_launch", () -> SoundEvent.createVariableRangeEvent(TeyvatBossesConstants.id("sfx.sub_torpedo_launch")));
    public static final DeferredHolder<SoundEvent, SoundEvent> SOUND_SUB_BALLAST_HISS =
            SOUNDS.register("sfx.sub_ballast_hiss", () -> SoundEvent.createVariableRangeEvent(TeyvatBossesConstants.id("sfx.sub_ballast_hiss")));
    public static final DeferredHolder<SoundEvent, SoundEvent> SOUND_DRILL_ENGINE_IDLE =
            SOUNDS.register("sfx.drill_engine_idle", () -> SoundEvent.createVariableRangeEvent(TeyvatBossesConstants.id("sfx.drill_engine_idle")));
    public static final DeferredHolder<SoundEvent, SoundEvent> SOUND_DRILL_BORE_SPIN =
            SOUNDS.register("sfx.drill_bore_spin", () -> SoundEvent.createVariableRangeEvent(TeyvatBossesConstants.id("sfx.drill_bore_spin")));
    public static final DeferredHolder<SoundEvent, SoundEvent> SOUND_RUIN_GUARD_SPEECH =
            SOUNDS.register("sfx.ruin_guard_speech", () -> SoundEvent.createVariableRangeEvent(TeyvatBossesConstants.id("sfx.ruin_guard_speech")));
    public static final DeferredHolder<SoundEvent, SoundEvent> SOUND_RUIN_GUARD_POWER_DOWN =
            SOUNDS.register("sfx.ruin_guard_power_down", () -> SoundEvent.createVariableRangeEvent(TeyvatBossesConstants.id("sfx.ruin_guard_power_down")));
    public static final DeferredHolder<SoundEvent, SoundEvent> SOUND_PRIMO_ROAR =
            SOUNDS.register("sfx.primo_geovishap_roar", () -> SoundEvent.createVariableRangeEvent(TeyvatBossesConstants.id("sfx.primo_geovishap_roar")));
    public static final DeferredHolder<SoundEvent, SoundEvent> SOUND_PRIMO_STEP =
            SOUNDS.register("sfx.primo_geovishap_step", () -> SoundEvent.createVariableRangeEvent(TeyvatBossesConstants.id("sfx.primo_geovishap_step")));
    public static final DeferredHolder<SoundEvent, SoundEvent> SOUND_PRIMO_CLAW_SWIPE =
            SOUNDS.register("sfx.primo_geovishap_claw_swipe", () -> SoundEvent.createVariableRangeEvent(TeyvatBossesConstants.id("sfx.primo_geovishap_claw_swipe")));
    public static final DeferredHolder<SoundEvent, SoundEvent> SOUND_PRIMO_SLAM =
            SOUNDS.register("sfx.primo_geovishap_slam", () -> SoundEvent.createVariableRangeEvent(TeyvatBossesConstants.id("sfx.primo_geovishap_slam")));
    public static final DeferredHolder<SoundEvent, SoundEvent> SOUND_PRIMO_BURROW =
            SOUNDS.register("sfx.primo_geovishap_burrow", () -> SoundEvent.createVariableRangeEvent(TeyvatBossesConstants.id("sfx.primo_geovishap_burrow")));
    public static final DeferredHolder<SoundEvent, SoundEvent> SOUND_PRIMO_EMERGE =
            SOUNDS.register("sfx.primo_geovishap_emerge", () -> SoundEvent.createVariableRangeEvent(TeyvatBossesConstants.id("sfx.primo_geovishap_emerge")));
    public static final DeferredHolder<SoundEvent, SoundEvent> SOUND_PRIMO_SHOWER_CHARGE =
            SOUNDS.register("sfx.primo_geovishap_shower_charge", () -> SoundEvent.createVariableRangeEvent(TeyvatBossesConstants.id("sfx.primo_geovishap_shower_charge")));
    public static final DeferredHolder<SoundEvent, SoundEvent> SOUND_PRIMO_SHOWER_BLAST =
            SOUNDS.register("sfx.primo_geovishap_shower_blast", () -> SoundEvent.createVariableRangeEvent(TeyvatBossesConstants.id("sfx.primo_geovishap_shower_blast")));
    public static final DeferredHolder<SoundEvent, SoundEvent> SOUND_PRIMO_SHIELD_REFLECT =
            SOUNDS.register("sfx.primo_geovishap_shield_reflect", () -> SoundEvent.createVariableRangeEvent(TeyvatBossesConstants.id("sfx.primo_geovishap_shield_reflect")));
    public static final DeferredHolder<SoundEvent, SoundEvent> SOUND_PRIMO_DEATH =
            SOUNDS.register("sfx.primo_geovishap_death", () -> SoundEvent.createVariableRangeEvent(TeyvatBossesConstants.id("sfx.primo_geovishap_death")));

    // 5 Music Discs
    public static final DeferredHolder<Item, Item> MUSIC_DISC_CRYO_REGISVINE =
            ITEMS.register("music_disc_cryo_regisvine", () -> new Item(new Item.Properties().stacksTo(1).rarity(Rarity.RARE)
                    .jukeboxPlayable(ResourceKey.create(Registries.JUKEBOX_SONG, TeyvatBossesConstants.id("cryo_regisvine_theme")))));
    public static final DeferredHolder<Item, Item> MUSIC_DISC_ANEMO_HYPOSTASIS =
            ITEMS.register("music_disc_anemo_hypostasis", () -> new Item(new Item.Properties().stacksTo(1).rarity(Rarity.RARE)
                    .jukeboxPlayable(ResourceKey.create(Registries.JUKEBOX_SONG, TeyvatBossesConstants.id("anemo_hypostasis_theme")))));
    public static final DeferredHolder<Item, Item> MUSIC_DISC_RUIN_GUARD =
            ITEMS.register("music_disc_ruin_guard", () -> new Item(new Item.Properties().stacksTo(1).rarity(Rarity.RARE)
                    .jukeboxPlayable(ResourceKey.create(Registries.JUKEBOX_SONG, TeyvatBossesConstants.id("ruin_guard_theme")))));
    public static final DeferredHolder<Item, Item> MUSIC_DISC_RUIN_NAUTILUS =
            ITEMS.register("music_disc_ruin_nautilus", () -> new Item(new Item.Properties().stacksTo(1).rarity(Rarity.RARE)
                    .jukeboxPlayable(ResourceKey.create(Registries.JUKEBOX_SONG, TeyvatBossesConstants.id("ruin_nautilus_theme")))));
    public static final DeferredHolder<Item, Item> MUSIC_DISC_PRIMO_GEOVISHAP =
            ITEMS.register("music_disc_primo_geovishap", () -> new Item(new Item.Properties().stacksTo(1).rarity(Rarity.RARE)
                    .jukeboxPlayable(ResourceKey.create(Registries.JUKEBOX_SONG, TeyvatBossesConstants.id("primo_geovishap_theme")))));

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN_TAB =
            TABS.register("main", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.velorise_teyvat_bosses.main"))
                    .icon(() -> new ItemStack(PRIMO_GEOVISHAP_SPAWN_EGG.get()))
                    .displayItems((params, output) -> {
                        output.accept(CRYO_REGISVINE_COMPASS.get());
                        output.accept(ANEMO_HYPOSTASIS_COMPASS.get());
                        output.accept(RUIN_GUARD_COMPASS.get());
                        output.accept(RUIN_NAUTILUS_COMPASS.get());
                        output.accept(PRIMO_GEOVISHAP_COMPASS.get());
                        output.accept(CRYO_REGISVINE_SPAWN_EGG.get());
                        output.accept(ANEMO_HYPOSTASIS_SPAWN_EGG.get());
                        output.accept(RUIN_GUARD_SPAWN_EGG.get());
                        output.accept(RUIN_NAUTILUS_SPAWN_EGG.get());
                        output.accept(PRIMO_GEOVISHAP_SPAWN_EGG.get());
                        output.accept(CRYO_REGISVINE_TROPHY_ITEM.get());
                        output.accept(ANEMO_HYPOSTASIS_TROPHY_ITEM.get());
                        output.accept(RUIN_GUARD_TROPHY_ITEM.get());
                        output.accept(RUIN_NAUTILUS_TROPHY_ITEM.get());
                        output.accept(PRIMO_GEOVISHAP_TROPHY_ITEM.get());
                        output.accept(CONDENSED_REDSTONE_BLOCK_ITEM.get());
                        output.accept(RUIN_MECH_CORE.get());
                        output.accept(RUIN_MECH_LEFT_ARM.get());
                        output.accept(RUIN_MECH_RIGHT_ARM.get());
                        output.accept(RUIN_MECH_LEFT_LEG.get());
                        output.accept(RUIN_MECH_RIGHT_LEG.get());
                        output.accept(CRYO_TURRET_ROOT.get());
                        output.accept(CRYO_TURRET_STEM.get());
                        output.accept(CRYO_TURRET_COROLLA.get());
                        output.accept(ANEMO_PRISM_CUBE.get());
                        output.accept(ANEMO_BOOTS.get());
                        output.accept(RUIN_SUB_HULL.get());
                        output.accept(RUIN_SUB_PROPELLER.get());
                        output.accept(RUIN_SUB_TORPEDO_POD.get());
                        output.accept(GEO_DRILLER_CHASSIS.get());
                        output.accept(GEO_DRILLER_DRILL.get());
                        output.accept(GEO_DRILLER_ENGINE.get());
                        output.accept(PRIMORDIAL_JADE.get());
                        output.accept(PRIMORDIAL_GEO_INGOT.get());
                        output.accept(GEOVISHAP_HELMET.get());
                        output.accept(GEOVISHAP_CHESTPLATE.get());
                        output.accept(GEOVISHAP_LEGGINGS.get());
                        output.accept(GEOVISHAP_BOOTS.get());
                        output.accept(MUSIC_DISC_CRYO_REGISVINE.get());
                        output.accept(MUSIC_DISC_ANEMO_HYPOSTASIS.get());
                        output.accept(MUSIC_DISC_RUIN_GUARD.get());
                        output.accept(MUSIC_DISC_RUIN_NAUTILUS.get());
                        output.accept(MUSIC_DISC_PRIMO_GEOVISHAP.get());
                    })
                    .build());

    public VeloriseTeyvatBossesNeoForge(IEventBus modEventBus) {
        TeyvatBossesConstants.LOGGER.info("Initializing {} (NeoForge 1.21.1)", TeyvatBossesConstants.MOD_NAME);
        TeyvatBossesConfig.load();
        ENTITIES.register(modEventBus);
        BLOCKS.register(modEventBus);
        BLOCK_ENTITIES.register(modEventBus);
        ARMOR_MATERIALS.register(modEventBus);
        ITEMS.register(modEventBus);
        SOUNDS.register(modEventBus);
        TABS.register(modEventBus);
        STRUCTURE_TYPES.register(modEventBus);
        TeyvatBossesStructures.bind(FLAT_ARENA);
        TeyvatBossesSounds.bind(
                SOUND_MECH_ENGINE_HUM, SOUND_MECH_LOCKON_BEEP, SOUND_MECH_VISOR_SEAL,
                SOUND_MECH_SERVO_WHIR, SOUND_MECH_JOINT_CREAK, SOUND_MECH_METAL_CLANK, SOUND_MECH_HYDRAULIC_HISS,
                SOUND_SUB_PROPELLER_HUM, SOUND_SUB_SONAR_PING, SOUND_SUB_TORPEDO_LAUNCH, SOUND_SUB_BALLAST_HISS,
                SOUND_DRILL_ENGINE_IDLE, SOUND_DRILL_BORE_SPIN,
                SOUND_RUIN_GUARD_SPEECH, SOUND_RUIN_GUARD_POWER_DOWN,
                SOUND_PRIMO_ROAR, SOUND_PRIMO_STEP, SOUND_PRIMO_CLAW_SWIPE, SOUND_PRIMO_SLAM, SOUND_PRIMO_BURROW, SOUND_PRIMO_EMERGE, SOUND_PRIMO_SHOWER_CHARGE, SOUND_PRIMO_SHOWER_BLAST, SOUND_PRIMO_SHIELD_REFLECT, SOUND_PRIMO_DEATH
        );
        TeyvatBossesEntities.bind(
                CRYO_REGISVINE, CRYO_ICICLE, CRYO_ICE_SPIKE, BOSS_RESPAWN_ANCHOR,
                ANEMO_HYPOSTASIS, ANEMO_WIND_ORB, ANEMO_TORNADO, ANEMO_MISSILE, WIND_CURRENT, TROUNCE_BLOSSOM,
                RUIN_GUARD, RUIN_MISSILE, RUIN_MECH, CRYO_TURRET,
                RUIN_NAUTILUS, RUIN_TORPEDO, RUIN_SUBMERSIBLE, ANEMO_COMPANION,
                PRIMO_GEOVISHAP, GEO_DRILLER, PRIMO_INFUSED_STONE
        );
        TeyvatBossesEntities.bindPrimoPart(PRIMO_GEOVISHAP_PART);
        TeyvatBossesBlocks.bind(
                CRYO_REGISVINE_TROPHY, ANEMO_HYPOSTASIS_TROPHY,
                RUIN_GUARD_TROPHY, RUIN_NAUTILUS_TROPHY, PRIMO_GEOVISHAP_TROPHY, BOSS_TROPHY_BE
        );
        TeyvatBossesBlocks.bindCondensedRedstone(CONDENSED_REDSTONE_BLOCK);
        TeyvatBossesItems.bindCondensedRedstone(CONDENSED_REDSTONE_BLOCK_ITEM);
        TeyvatBossesItems.bindGeovishapGear(
                PRIMORDIAL_JADE, PRIMORDIAL_GEO_INGOT,
                GEOVISHAP_HELMET, GEOVISHAP_CHESTPLATE, GEOVISHAP_LEGGINGS, GEOVISHAP_BOOTS);
        TeyvatBossesItems.bindAnemoBoots(ANEMO_BOOTS);
        TeyvatBossesItems.bind(
                MUSIC_DISC_CRYO_REGISVINE, MUSIC_DISC_ANEMO_HYPOSTASIS, MUSIC_DISC_RUIN_GUARD,
                MUSIC_DISC_RUIN_NAUTILUS, MUSIC_DISC_PRIMO_GEOVISHAP,
                RUIN_MECH_CORE, RUIN_MECH_LEFT_ARM, RUIN_MECH_RIGHT_ARM, RUIN_MECH_LEFT_LEG, RUIN_MECH_RIGHT_LEG,
                CRYO_TURRET_ROOT, CRYO_TURRET_STEM, CRYO_TURRET_COROLLA, ANEMO_PRISM_CUBE,
                RUIN_SUB_HULL, RUIN_SUB_PROPELLER, RUIN_SUB_TORPEDO_POD,
                GEO_DRILLER_CHASSIS, GEO_DRILLER_DRILL, GEO_DRILLER_ENGINE
        );
        modEventBus.addListener(this::registerAttributes);
        modEventBus.addListener(this::registerPayloads);
    }

    private void registerPayloads(RegisterPayloadHandlersEvent event) {
        event.registrar("1").playToServer(
                GeoDrillerControlPayload.TYPE,
                GeoDrillerControlPayload.STREAM_CODEC,
                (payload, ctx) -> ctx.enqueueWork(() -> {
                    if (ctx.player() instanceof ServerPlayer sp) GeoDrillerControlPayload.handleOnServer(payload, sp);
                })
        );
        event.registrar("1").playToServer(
                AnemoDoubleJumpPayload.TYPE,
                AnemoDoubleJumpPayload.STREAM_CODEC,
                (payload, ctx) -> ctx.enqueueWork(() -> {
                    if (ctx.player() instanceof ServerPlayer sp) AnemoDoubleJumpPayload.handleOnServer(payload, sp);
                })
        );
        event.registrar("1").playToServer(
                RuinSubControlPayload.TYPE,
                RuinSubControlPayload.STREAM_CODEC,
                (payload, ctx) -> ctx.enqueueWork(() -> {
                    if (ctx.player() instanceof ServerPlayer sp) RuinSubControlPayload.handleOnServer(payload, sp);
                })
        );
        event.registrar("1").playToServer(
                RuinMechSkillPayload.TYPE,
                RuinMechSkillPayload.STREAM_CODEC,
                (payload, ctx) -> ctx.enqueueWork(() -> {
                    if (ctx.player().getVehicle() instanceof RuinMechEntity mech) {
                        mech.tryTriggerSkill(payload.skillId());
                    }
                })
        );
    }

    private void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(CRYO_REGISVINE.get(), CryoRegisvineEntity.createAttributes().build());
        event.put(ANEMO_HYPOSTASIS.get(), AnemoHypostasisEntity.createAttributes().build());
        event.put(RUIN_GUARD.get(), RuinGuardEntity.createAttributes().build());
        event.put(RUIN_MECH.get(), RuinMechEntity.createAttributes().build());
        event.put(CRYO_TURRET.get(), CryoTurretEntity.createAttributes().build());
        event.put(RUIN_NAUTILUS.get(), RuinNautilusEntity.createAttributes().build());
        event.put(RUIN_SUBMERSIBLE.get(), RuinSubmersibleEntity.createAttributes().build());
        event.put(ANEMO_COMPANION.get(), AnemoCompanionEntity.createAttributes().build());
        event.put(PRIMO_GEOVISHAP.get(), PrimoGeovishapEntity.createAttributes().build());
        event.put(GEO_DRILLER.get(), GeoDrillerEntity.createAttributes().build());
    }
}

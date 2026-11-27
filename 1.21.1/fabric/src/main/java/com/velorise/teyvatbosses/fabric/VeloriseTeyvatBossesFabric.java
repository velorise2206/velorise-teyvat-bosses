package com.velorise.teyvatbosses.fabric;

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
import com.velorise.teyvatbosses.item.AnemoPrismCubeItem;
import com.velorise.teyvatbosses.item.CryoTurretPartItem;
import com.velorise.teyvatbosses.item.GeoDrillerPartItem;
import com.velorise.teyvatbosses.item.GeovishapArmorItem;
import net.minecraft.core.Holder;
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
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PoweredBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.material.MapColor;

public class VeloriseTeyvatBossesFabric implements ModInitializer {

    private static BlockBehaviour.Properties trophyProps() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.METAL)
                .strength(2.0F, 6.0F)
                .sound(SoundType.METAL)
                .noOcclusion()
                .lightLevel(state -> 6);
    }

    public static final EntityType<CryoRegisvineEntity> CRYO_REGISVINE = Registry.register(
            BuiltInRegistries.ENTITY_TYPE, TeyvatBossesConstants.id("cryo_regisvine"),
            EntityType.Builder.of(CryoRegisvineEntity::new, MobCategory.MONSTER).sized(3.4F, 6.2F).clientTrackingRange(12).build("cryo_regisvine"));
    public static final EntityType<CryoIcicleProjectile> CRYO_ICICLE = Registry.register(
            BuiltInRegistries.ENTITY_TYPE, TeyvatBossesConstants.id("cryo_icicle"),
            EntityType.Builder.<CryoIcicleProjectile>of(CryoIcicleProjectile::new, MobCategory.MISC).sized(0.55F, 0.55F).clientTrackingRange(10).updateInterval(2).build("cryo_icicle"));
    public static final EntityType<CryoIceSpikeEntity> CRYO_ICE_SPIKE = Registry.register(
            BuiltInRegistries.ENTITY_TYPE, TeyvatBossesConstants.id("cryo_ice_spike"),
            EntityType.Builder.<CryoIceSpikeEntity>of(CryoIceSpikeEntity::new, MobCategory.MISC).sized(1.6F, 3.6F).clientTrackingRange(10).updateInterval(2).build("cryo_ice_spike"));
    public static final EntityType<BossRespawnAnchorEntity> BOSS_RESPAWN_ANCHOR = Registry.register(
            BuiltInRegistries.ENTITY_TYPE, TeyvatBossesConstants.id("boss_respawn_anchor"),
            EntityType.Builder.<BossRespawnAnchorEntity>of(BossRespawnAnchorEntity::new, MobCategory.MISC).sized(1.4F, 2.2F).clientTrackingRange(10).updateInterval(10).build("boss_respawn_anchor"));

    public static final EntityType<AnemoHypostasisEntity> ANEMO_HYPOSTASIS = Registry.register(
            BuiltInRegistries.ENTITY_TYPE, TeyvatBossesConstants.id("anemo_hypostasis"),
            EntityType.Builder.of(AnemoHypostasisEntity::new, MobCategory.MONSTER).sized(2.6F, 3.2F).clientTrackingRange(14).build("anemo_hypostasis"));
    public static final EntityType<AnemoWindOrbEntity> ANEMO_WIND_ORB = Registry.register(
            BuiltInRegistries.ENTITY_TYPE, TeyvatBossesConstants.id("anemo_wind_orb"),
            EntityType.Builder.<AnemoWindOrbEntity>of(AnemoWindOrbEntity::new, MobCategory.MISC).sized(1.2F, 1.2F).clientTrackingRange(12).updateInterval(2).build("anemo_wind_orb"));
    public static final EntityType<AnemoTornadoEntity> ANEMO_TORNADO = Registry.register(
            BuiltInRegistries.ENTITY_TYPE, TeyvatBossesConstants.id("anemo_tornado"),
            EntityType.Builder.<AnemoTornadoEntity>of(AnemoTornadoEntity::new, MobCategory.MISC).sized(2.4F, 6.0F).clientTrackingRange(14).updateInterval(2).build("anemo_tornado"));
    public static final EntityType<AnemoMissileProjectile> ANEMO_MISSILE = Registry.register(
            BuiltInRegistries.ENTITY_TYPE, TeyvatBossesConstants.id("anemo_missile"),
            EntityType.Builder.<AnemoMissileProjectile>of(AnemoMissileProjectile::new, MobCategory.MISC).sized(0.9F, 0.9F).clientTrackingRange(12).updateInterval(2).build("anemo_missile"));
    public static final EntityType<WindCurrentEntity> WIND_CURRENT = Registry.register(
            BuiltInRegistries.ENTITY_TYPE, TeyvatBossesConstants.id("wind_current"),
            EntityType.Builder.<WindCurrentEntity>of(WindCurrentEntity::new, MobCategory.MISC).sized(4.5F, 9.5F).clientTrackingRange(14).updateInterval(4).build("wind_current"));
    public static final EntityType<TrounceBlossomEntity> TROUNCE_BLOSSOM = Registry.register(
            BuiltInRegistries.ENTITY_TYPE, TeyvatBossesConstants.id("trounce_blossom"),
            EntityType.Builder.<TrounceBlossomEntity>of(TrounceBlossomEntity::new, MobCategory.MISC).sized(1.5F, 2.2F).clientTrackingRange(12).updateInterval(4).build("trounce_blossom"));

    public static final EntityType<RuinGuardEntity> RUIN_GUARD = Registry.register(
            BuiltInRegistries.ENTITY_TYPE, TeyvatBossesConstants.id("ruin_guard"),
            EntityType.Builder.of(RuinGuardEntity::new, MobCategory.MONSTER).sized(2.4F, 4.0F).clientTrackingRange(14).build("ruin_guard"));
    public static final EntityType<RuinMissileProjectile> RUIN_MISSILE = Registry.register(
            BuiltInRegistries.ENTITY_TYPE, TeyvatBossesConstants.id("ruin_missile"),
            EntityType.Builder.<RuinMissileProjectile>of(RuinMissileProjectile::new, MobCategory.MISC).sized(0.75F, 0.75F).clientTrackingRange(12).updateInterval(2).build("ruin_missile"));
    public static final EntityType<RuinMechEntity> RUIN_MECH = Registry.register(
            BuiltInRegistries.ENTITY_TYPE, TeyvatBossesConstants.id("ruin_mech"),
            EntityType.Builder.of(RuinMechEntity::new, MobCategory.MISC).sized(2.4F, 4.0F).clientTrackingRange(14).build("ruin_mech"));
    public static final EntityType<CryoTurretEntity> CRYO_TURRET = Registry.register(
            BuiltInRegistries.ENTITY_TYPE, TeyvatBossesConstants.id("cryo_turret"),
            EntityType.Builder.of(CryoTurretEntity::new, MobCategory.MISC).sized(2.6F, 5.6F).clientTrackingRange(12).build("cryo_turret"));
    public static final EntityType<RuinNautilusEntity> RUIN_NAUTILUS = Registry.register(
            BuiltInRegistries.ENTITY_TYPE, TeyvatBossesConstants.id("ruin_nautilus"),
            EntityType.Builder.of(RuinNautilusEntity::new, MobCategory.MONSTER).sized(4.6F, 3.8F).clientTrackingRange(16).build("ruin_nautilus"));
    public static final EntityType<RuinTorpedoProjectile> RUIN_TORPEDO = Registry.register(
            BuiltInRegistries.ENTITY_TYPE, TeyvatBossesConstants.id("ruin_torpedo"),
            EntityType.Builder.<RuinTorpedoProjectile>of(RuinTorpedoProjectile::new, MobCategory.MISC).sized(0.7F, 0.7F).clientTrackingRange(12).updateInterval(2).build("ruin_torpedo"));
    public static final EntityType<RuinSubmersibleEntity> RUIN_SUBMERSIBLE = Registry.register(
            BuiltInRegistries.ENTITY_TYPE, TeyvatBossesConstants.id("ruin_submersible"),
            EntityType.Builder.of(RuinSubmersibleEntity::new, MobCategory.MISC).sized(3.4F, 2.6F).clientTrackingRange(14).build("ruin_submersible"));
    public static final EntityType<AnemoCompanionEntity> ANEMO_COMPANION = Registry.register(
            BuiltInRegistries.ENTITY_TYPE, TeyvatBossesConstants.id("anemo_companion"),
            EntityType.Builder.of(AnemoCompanionEntity::new, MobCategory.MISC).sized(0.45F, 0.45F).clientTrackingRange(12).build("anemo_companion"));
    public static final EntityType<PrimoGeovishapEntity> PRIMO_GEOVISHAP = Registry.register(
            BuiltInRegistries.ENTITY_TYPE, TeyvatBossesConstants.id("primo_geovishap"),
            EntityType.Builder.of(PrimoGeovishapEntity::new, MobCategory.MONSTER).sized(4.4F, 4.0F).clientTrackingRange(16).build("primo_geovishap"));
    public static final EntityType<GeoDrillerEntity> GEO_DRILLER = Registry.register(
            BuiltInRegistries.ENTITY_TYPE, TeyvatBossesConstants.id("geo_driller"),
            EntityType.Builder.of(GeoDrillerEntity::new, MobCategory.MISC).sized(2.4F, 2.1F).clientTrackingRange(14).build("geo_driller"));
    public static final EntityType<PrimoInfusedStoneEntity> PRIMO_INFUSED_STONE = Registry.register(
            BuiltInRegistries.ENTITY_TYPE, TeyvatBossesConstants.id("primo_infused_stone"),
            EntityType.Builder.<PrimoInfusedStoneEntity>of(PrimoInfusedStoneEntity::new, MobCategory.MISC).sized(1.3F, 2.2F).clientTrackingRange(14).updateInterval(2).build("primo_infused_stone"));
    public static final EntityType<PrimoGeovishapPartEntity> PRIMO_GEOVISHAP_PART = Registry.register(
            BuiltInRegistries.ENTITY_TYPE, TeyvatBossesConstants.id("primo_geovishap_part"),
            EntityType.Builder.<PrimoGeovishapPartEntity>of(PrimoGeovishapPartEntity::new, MobCategory.MISC).sized(2.4F, 2.2F).noSave().noSummon().clientTrackingRange(16).updateInterval(1).build("primo_geovishap_part"));

    // 5 Boss Trophy Blocks
    public static final BossTrophyBlock CRYO_REGISVINE_TROPHY = Registry.register(
            BuiltInRegistries.BLOCK, TeyvatBossesConstants.id("cryo_regisvine_trophy"),
            new BossTrophyBlock(BossRespawnAnchorEntity.BOSS_CRYO_REGISVINE, trophyProps()));
    public static final BossTrophyBlock ANEMO_HYPOSTASIS_TROPHY = Registry.register(
            BuiltInRegistries.BLOCK, TeyvatBossesConstants.id("anemo_hypostasis_trophy"),
            new BossTrophyBlock(BossRespawnAnchorEntity.BOSS_ANEMO_HYPOSTASIS, trophyProps()));
    public static final BossTrophyBlock RUIN_GUARD_TROPHY = Registry.register(
            BuiltInRegistries.BLOCK, TeyvatBossesConstants.id("ruin_guard_trophy"),
            new BossTrophyBlock(BossRespawnAnchorEntity.BOSS_RUIN_GUARD, trophyProps()));
    public static final BossTrophyBlock RUIN_NAUTILUS_TROPHY = Registry.register(
            BuiltInRegistries.BLOCK, TeyvatBossesConstants.id("ruin_nautilus_trophy"),
            new BossTrophyBlock(BossRespawnAnchorEntity.BOSS_RUIN_NAUTILUS, trophyProps()));
    public static final BossTrophyBlock PRIMO_GEOVISHAP_TROPHY = Registry.register(
            BuiltInRegistries.BLOCK, TeyvatBossesConstants.id("primo_geovishap_trophy"),
            new BossTrophyBlock(BossRespawnAnchorEntity.BOSS_PRIMO_GEOVISHAP, trophyProps()));
    public static final Block CONDENSED_REDSTONE_BLOCK = Registry.register(
            BuiltInRegistries.BLOCK, TeyvatBossesConstants.id("condensed_redstone_block"),
            new PoweredBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.REDSTONE_BLOCK).strength(7.0F, 9.0F)));

    public static final BlockEntityType<BossTrophyBlockEntity> BOSS_TROPHY_BE = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE, TeyvatBossesConstants.id("boss_trophy"),
            BlockEntityType.Builder.of(BossTrophyBlockEntity::new,
                    CRYO_REGISVINE_TROPHY, ANEMO_HYPOSTASIS_TROPHY,
                    RUIN_GUARD_TROPHY, RUIN_NAUTILUS_TROPHY, PRIMO_GEOVISHAP_TROPHY).build(null));

    // Trophy Block Items
    public static final Item CRYO_REGISVINE_TROPHY_ITEM = Registry.register(
            BuiltInRegistries.ITEM, TeyvatBossesConstants.id("cryo_regisvine_trophy"),
            new BlockItem(CRYO_REGISVINE_TROPHY, new Item.Properties().rarity(Rarity.EPIC)));
    public static final Item ANEMO_HYPOSTASIS_TROPHY_ITEM = Registry.register(
            BuiltInRegistries.ITEM, TeyvatBossesConstants.id("anemo_hypostasis_trophy"),
            new BlockItem(ANEMO_HYPOSTASIS_TROPHY, new Item.Properties().rarity(Rarity.EPIC)));
    public static final Item RUIN_GUARD_TROPHY_ITEM = Registry.register(
            BuiltInRegistries.ITEM, TeyvatBossesConstants.id("ruin_guard_trophy"),
            new BlockItem(RUIN_GUARD_TROPHY, new Item.Properties().rarity(Rarity.EPIC)));
    public static final Item RUIN_NAUTILUS_TROPHY_ITEM = Registry.register(
            BuiltInRegistries.ITEM, TeyvatBossesConstants.id("ruin_nautilus_trophy"),
            new BlockItem(RUIN_NAUTILUS_TROPHY, new Item.Properties().rarity(Rarity.EPIC)));
    public static final Item PRIMO_GEOVISHAP_TROPHY_ITEM = Registry.register(
            BuiltInRegistries.ITEM, TeyvatBossesConstants.id("primo_geovishap_trophy"),
            new BlockItem(PRIMO_GEOVISHAP_TROPHY, new Item.Properties().rarity(Rarity.EPIC)));
    public static final Item CONDENSED_REDSTONE_BLOCK_ITEM = Registry.register(
            BuiltInRegistries.ITEM, TeyvatBossesConstants.id("condensed_redstone_block"),
            new BlockItem(CONDENSED_REDSTONE_BLOCK, new Item.Properties().rarity(Rarity.UNCOMMON)));

    // Modular Salvage Parts & Gear
    public static final Item RUIN_MECH_CORE = Registry.register(
            BuiltInRegistries.ITEM, TeyvatBossesConstants.id("ruin_mech_core"),
            new RuinMechPartItem(RuinMechEntity.PART_CORE, new Item.Properties().stacksTo(1).rarity(Rarity.EPIC)));
    public static final Item RUIN_MECH_LEFT_ARM = Registry.register(
            BuiltInRegistries.ITEM, TeyvatBossesConstants.id("ruin_mech_left_arm"),
            new RuinMechPartItem(RuinMechEntity.PART_LEFT_ARM, new Item.Properties().stacksTo(1).rarity(Rarity.RARE)));
    public static final Item RUIN_MECH_RIGHT_ARM = Registry.register(
            BuiltInRegistries.ITEM, TeyvatBossesConstants.id("ruin_mech_right_arm"),
            new RuinMechPartItem(RuinMechEntity.PART_RIGHT_ARM, new Item.Properties().stacksTo(1).rarity(Rarity.RARE)));
    public static final Item RUIN_MECH_LEFT_LEG = Registry.register(
            BuiltInRegistries.ITEM, TeyvatBossesConstants.id("ruin_mech_left_leg"),
            new RuinMechPartItem(RuinMechEntity.PART_LEFT_LEG, new Item.Properties().stacksTo(1).rarity(Rarity.RARE)));
    public static final Item RUIN_MECH_RIGHT_LEG = Registry.register(
            BuiltInRegistries.ITEM, TeyvatBossesConstants.id("ruin_mech_right_leg"),
            new RuinMechPartItem(RuinMechEntity.PART_RIGHT_LEG, new Item.Properties().stacksTo(1).rarity(Rarity.RARE)));
    public static final Item CRYO_TURRET_ROOT = Registry.register(
            BuiltInRegistries.ITEM, TeyvatBossesConstants.id("cryo_turret_root"),
            new CryoTurretPartItem(CryoTurretEntity.PART_ROOT, new Item.Properties().stacksTo(1).rarity(Rarity.RARE)));
    public static final Item CRYO_TURRET_STEM = Registry.register(
            BuiltInRegistries.ITEM, TeyvatBossesConstants.id("cryo_turret_stem"),
            new CryoTurretPartItem(CryoTurretEntity.PART_STEM, new Item.Properties().stacksTo(1).rarity(Rarity.RARE)));
    public static final Item CRYO_TURRET_COROLLA = Registry.register(
            BuiltInRegistries.ITEM, TeyvatBossesConstants.id("cryo_turret_corolla"),
            new CryoTurretPartItem(CryoTurretEntity.PART_COROLLA, new Item.Properties().stacksTo(1).rarity(Rarity.RARE)));
    public static final Item ANEMO_PRISM_CUBE = Registry.register(
            BuiltInRegistries.ITEM, TeyvatBossesConstants.id("anemo_prism_cube"),
            new AnemoPrismCubeItem(new Item.Properties().stacksTo(16).rarity(Rarity.RARE)));
    public static final Item RUIN_SUB_HULL = Registry.register(
            BuiltInRegistries.ITEM, TeyvatBossesConstants.id("ruin_sub_hull"),
            new RuinSubPartItem(RuinSubmersibleEntity.PART_HULL, new Item.Properties().stacksTo(1).rarity(Rarity.EPIC)));
    public static final Item RUIN_SUB_COCKPIT = Registry.register(
            BuiltInRegistries.ITEM, TeyvatBossesConstants.id("ruin_sub_cockpit"),
            new RuinSubPartItem(RuinSubmersibleEntity.PART_COCKPIT, new Item.Properties().stacksTo(1).rarity(Rarity.RARE)));
    public static final Item RUIN_SUB_PROPELLER = Registry.register(
            BuiltInRegistries.ITEM, TeyvatBossesConstants.id("ruin_sub_propeller"),
            new RuinSubPartItem(RuinSubmersibleEntity.PART_PROPELLER, new Item.Properties().stacksTo(1).rarity(Rarity.RARE)));
    public static final Item RUIN_SUB_TORPEDO_POD = Registry.register(
            BuiltInRegistries.ITEM, TeyvatBossesConstants.id("ruin_sub_torpedo_pod"),
            new RuinSubPartItem(RuinSubmersibleEntity.PART_TORPEDO_POD, new Item.Properties().stacksTo(1).rarity(Rarity.RARE)));
    public static final Item GEO_DRILLER_CHASSIS = Registry.register(
            BuiltInRegistries.ITEM, TeyvatBossesConstants.id("geo_driller_chassis"),
            new GeoDrillerPartItem(GeoDrillerEntity.PART_CHASSIS, new Item.Properties().stacksTo(1).rarity(Rarity.EPIC)));
    public static final Item GEO_DRILLER_DRILL = Registry.register(
            BuiltInRegistries.ITEM, TeyvatBossesConstants.id("geo_driller_drill"),
            new GeoDrillerPartItem(GeoDrillerEntity.PART_DRILL, new Item.Properties().stacksTo(1).rarity(Rarity.RARE)));
    public static final Item GEO_DRILLER_ENGINE = Registry.register(
            BuiltInRegistries.ITEM, TeyvatBossesConstants.id("geo_driller_engine"),
            new GeoDrillerPartItem(GeoDrillerEntity.PART_ENGINE, new Item.Properties().stacksTo(1).rarity(Rarity.RARE)));

    // Primo Geovishap Primordial Jade, Geo Scale Ingot & Dragon-Scale Armor
    public static final Holder<ArmorMaterial> GEOVISHAP_ARMOR_MATERIAL = Registry.registerForHolder(
            BuiltInRegistries.ARMOR_MATERIAL, TeyvatBossesConstants.id("geovishap"),
            GeovishapArmorItem.createArmorMaterial());
    public static final Item PRIMORDIAL_JADE = Registry.register(
            BuiltInRegistries.ITEM, TeyvatBossesConstants.id("primordial_jade"),
            new Item(new Item.Properties().fireResistant().rarity(Rarity.EPIC)));
    public static final Item PRIMORDIAL_GEO_INGOT = Registry.register(
            BuiltInRegistries.ITEM, TeyvatBossesConstants.id("primordial_geo_ingot"),
            new Item(new Item.Properties().fireResistant().rarity(Rarity.RARE)));
    public static final Item GEOVISHAP_HELMET = Registry.register(
            BuiltInRegistries.ITEM, TeyvatBossesConstants.id("geovishap_helmet"),
            new GeovishapArmorItem(GEOVISHAP_ARMOR_MATERIAL, ArmorItem.Type.HELMET, new Item.Properties()));
    public static final Item GEOVISHAP_CHESTPLATE = Registry.register(
            BuiltInRegistries.ITEM, TeyvatBossesConstants.id("geovishap_chestplate"),
            new GeovishapArmorItem(GEOVISHAP_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE, new Item.Properties()));
    public static final Item GEOVISHAP_LEGGINGS = Registry.register(
            BuiltInRegistries.ITEM, TeyvatBossesConstants.id("geovishap_leggings"),
            new GeovishapArmorItem(GEOVISHAP_ARMOR_MATERIAL, ArmorItem.Type.LEGGINGS, new Item.Properties()));
    public static final Item GEOVISHAP_BOOTS = Registry.register(
            BuiltInRegistries.ITEM, TeyvatBossesConstants.id("geovishap_boots"),
            new GeovishapArmorItem(GEOVISHAP_ARMOR_MATERIAL, ArmorItem.Type.BOOTS, new Item.Properties()));

    // Teyvat Boss Locator Compass
    public static final Item CRYO_REGISVINE_COMPASS = Registry.register(
            BuiltInRegistries.ITEM, TeyvatBossesConstants.id("cryo_regisvine_compass"),
            new TeyvatBossLocatorItem(new Item.Properties().stacksTo(1).rarity(Rarity.RARE), 0));
    public static final Item ANEMO_HYPOSTASIS_COMPASS = Registry.register(
            BuiltInRegistries.ITEM, TeyvatBossesConstants.id("anemo_hypostasis_compass"),
            new TeyvatBossLocatorItem(new Item.Properties().stacksTo(1).rarity(Rarity.RARE), 1));
    public static final Item RUIN_GUARD_COMPASS = Registry.register(
            BuiltInRegistries.ITEM, TeyvatBossesConstants.id("ruin_guard_compass"),
            new TeyvatBossLocatorItem(new Item.Properties().stacksTo(1).rarity(Rarity.RARE), 2));
    public static final Item RUIN_NAUTILUS_COMPASS = Registry.register(
            BuiltInRegistries.ITEM, TeyvatBossesConstants.id("ruin_nautilus_compass"),
            new TeyvatBossLocatorItem(new Item.Properties().stacksTo(1).rarity(Rarity.RARE), 3));
    public static final Item PRIMO_GEOVISHAP_COMPASS = Registry.register(
            BuiltInRegistries.ITEM, TeyvatBossesConstants.id("primo_geovishap_compass"),
            new TeyvatBossLocatorItem(new Item.Properties().stacksTo(1).rarity(Rarity.RARE), 4));

    // 5 Boss Spawn Eggs
    public static final Item CRYO_REGISVINE_SPAWN_EGG = Registry.register(
            BuiltInRegistries.ITEM, TeyvatBossesConstants.id("cryo_regisvine_spawn_egg"),
            new SpawnEggItem(CRYO_REGISVINE, 0x1E4B7A, 0x9BE8FF, new Item.Properties()));
    public static final Item ANEMO_HYPOSTASIS_SPAWN_EGG = Registry.register(
            BuiltInRegistries.ITEM, TeyvatBossesConstants.id("anemo_hypostasis_spawn_egg"),
            new SpawnEggItem(ANEMO_HYPOSTASIS, 0x1E3B42, 0x74F9DF, new Item.Properties()));
    public static final Item RUIN_GUARD_SPAWN_EGG = Registry.register(
            BuiltInRegistries.ITEM, TeyvatBossesConstants.id("ruin_guard_spawn_egg"),
            new SpawnEggItem(RUIN_GUARD, 0x4E3C2B, 0xFFB338, new Item.Properties()));
    public static final Item RUIN_NAUTILUS_SPAWN_EGG = Registry.register(
            BuiltInRegistries.ITEM, TeyvatBossesConstants.id("ruin_nautilus_spawn_egg"),
            new SpawnEggItem(RUIN_NAUTILUS, 0x1A3244, 0x22D3EE, new Item.Properties()));
    public static final Item PRIMO_GEOVISHAP_SPAWN_EGG = Registry.register(
            BuiltInRegistries.ITEM, TeyvatBossesConstants.id("primo_geovishap_spawn_egg"),
            new SpawnEggItem(PRIMO_GEOVISHAP, 0x4A3828, 0xF5B841, new Item.Properties()));

    // Sounds
    public static final SoundEvent SOUND_CRYO_THEME = Registry.register(
            BuiltInRegistries.SOUND_EVENT, TeyvatBossesConstants.id("music.cryo_regisvine_theme"),
            SoundEvent.createVariableRangeEvent(TeyvatBossesConstants.id("music.cryo_regisvine_theme")));
    public static final SoundEvent SOUND_ANEMO_THEME = Registry.register(
            BuiltInRegistries.SOUND_EVENT, TeyvatBossesConstants.id("music.anemo_hypostasis_theme"),
            SoundEvent.createVariableRangeEvent(TeyvatBossesConstants.id("music.anemo_hypostasis_theme")));
    public static final SoundEvent SOUND_RUIN_GUARD_THEME = Registry.register(
            BuiltInRegistries.SOUND_EVENT, TeyvatBossesConstants.id("music.ruin_guard_theme"),
            SoundEvent.createVariableRangeEvent(TeyvatBossesConstants.id("music.ruin_guard_theme")));
    public static final SoundEvent SOUND_RUIN_NAUTILUS_THEME = Registry.register(
            BuiltInRegistries.SOUND_EVENT, TeyvatBossesConstants.id("music.ruin_nautilus_theme"),
            SoundEvent.createVariableRangeEvent(TeyvatBossesConstants.id("music.ruin_nautilus_theme")));
    public static final SoundEvent SOUND_PRIMO_GEOVISHAP_THEME = Registry.register(
            BuiltInRegistries.SOUND_EVENT, TeyvatBossesConstants.id("music.primo_geovishap_theme"),
            SoundEvent.createVariableRangeEvent(TeyvatBossesConstants.id("music.primo_geovishap_theme")));
    public static final SoundEvent SOUND_MECH_ENGINE_HUM = Registry.register(
            BuiltInRegistries.SOUND_EVENT, TeyvatBossesConstants.id("sfx.mech_engine_hum"),
            SoundEvent.createVariableRangeEvent(TeyvatBossesConstants.id("sfx.mech_engine_hum")));
    public static final SoundEvent SOUND_MECH_LOCKON_BEEP = Registry.register(
            BuiltInRegistries.SOUND_EVENT, TeyvatBossesConstants.id("sfx.mech_lockon_beep"),
            SoundEvent.createVariableRangeEvent(TeyvatBossesConstants.id("sfx.mech_lockon_beep")));
    public static final SoundEvent SOUND_MECH_VISOR_SEAL = Registry.register(
            BuiltInRegistries.SOUND_EVENT, TeyvatBossesConstants.id("sfx.mech_visor_seal"),
            SoundEvent.createVariableRangeEvent(TeyvatBossesConstants.id("sfx.mech_visor_seal")));
    public static final SoundEvent SOUND_MECH_SERVO_WHIR = Registry.register(
            BuiltInRegistries.SOUND_EVENT, TeyvatBossesConstants.id("sfx.mech_servo_whir"),
            SoundEvent.createVariableRangeEvent(TeyvatBossesConstants.id("sfx.mech_servo_whir")));
    public static final SoundEvent SOUND_MECH_JOINT_CREAK = Registry.register(
            BuiltInRegistries.SOUND_EVENT, TeyvatBossesConstants.id("sfx.mech_joint_creak"),
            SoundEvent.createVariableRangeEvent(TeyvatBossesConstants.id("sfx.mech_joint_creak")));
    public static final SoundEvent SOUND_MECH_METAL_CLANK = Registry.register(
            BuiltInRegistries.SOUND_EVENT, TeyvatBossesConstants.id("sfx.mech_metal_clank"),
            SoundEvent.createVariableRangeEvent(TeyvatBossesConstants.id("sfx.mech_metal_clank")));
    public static final SoundEvent SOUND_MECH_HYDRAULIC_HISS = Registry.register(
            BuiltInRegistries.SOUND_EVENT, TeyvatBossesConstants.id("sfx.mech_hydraulic_hiss"),
            SoundEvent.createVariableRangeEvent(TeyvatBossesConstants.id("sfx.mech_hydraulic_hiss")));
    public static final SoundEvent SOUND_SUB_PROPELLER_HUM = Registry.register(
            BuiltInRegistries.SOUND_EVENT, TeyvatBossesConstants.id("sfx.sub_propeller_hum"),
            SoundEvent.createVariableRangeEvent(TeyvatBossesConstants.id("sfx.sub_propeller_hum")));
    public static final SoundEvent SOUND_SUB_SONAR_PING = Registry.register(
            BuiltInRegistries.SOUND_EVENT, TeyvatBossesConstants.id("sfx.sub_sonar_ping"),
            SoundEvent.createVariableRangeEvent(TeyvatBossesConstants.id("sfx.sub_sonar_ping")));
    public static final SoundEvent SOUND_SUB_TORPEDO_LAUNCH = Registry.register(
            BuiltInRegistries.SOUND_EVENT, TeyvatBossesConstants.id("sfx.sub_torpedo_launch"),
            SoundEvent.createVariableRangeEvent(TeyvatBossesConstants.id("sfx.sub_torpedo_launch")));
    public static final SoundEvent SOUND_SUB_BALLAST_HISS = Registry.register(
            BuiltInRegistries.SOUND_EVENT, TeyvatBossesConstants.id("sfx.sub_ballast_hiss"),
            SoundEvent.createVariableRangeEvent(TeyvatBossesConstants.id("sfx.sub_ballast_hiss")));
    public static final SoundEvent SOUND_DRILL_ENGINE_IDLE = Registry.register(
            BuiltInRegistries.SOUND_EVENT, TeyvatBossesConstants.id("sfx.drill_engine_idle"),
            SoundEvent.createVariableRangeEvent(TeyvatBossesConstants.id("sfx.drill_engine_idle")));
    public static final SoundEvent SOUND_DRILL_BORE_SPIN = Registry.register(
            BuiltInRegistries.SOUND_EVENT, TeyvatBossesConstants.id("sfx.drill_bore_spin"),
            SoundEvent.createVariableRangeEvent(TeyvatBossesConstants.id("sfx.drill_bore_spin")));
    public static final SoundEvent SOUND_RUIN_GUARD_SPEECH = Registry.register(
            BuiltInRegistries.SOUND_EVENT, TeyvatBossesConstants.id("sfx.ruin_guard_speech"),
            SoundEvent.createVariableRangeEvent(TeyvatBossesConstants.id("sfx.ruin_guard_speech")));
    public static final SoundEvent SOUND_RUIN_GUARD_POWER_DOWN = Registry.register(
            BuiltInRegistries.SOUND_EVENT, TeyvatBossesConstants.id("sfx.ruin_guard_power_down"),
            SoundEvent.createVariableRangeEvent(TeyvatBossesConstants.id("sfx.ruin_guard_power_down")));
    public static final SoundEvent SOUND_PRIMO_ROAR = Registry.register(
            BuiltInRegistries.SOUND_EVENT, TeyvatBossesConstants.id("sfx.primo_geovishap_roar"),
            SoundEvent.createVariableRangeEvent(TeyvatBossesConstants.id("sfx.primo_geovishap_roar")));
    public static final SoundEvent SOUND_PRIMO_STEP = Registry.register(
            BuiltInRegistries.SOUND_EVENT, TeyvatBossesConstants.id("sfx.primo_geovishap_step"),
            SoundEvent.createVariableRangeEvent(TeyvatBossesConstants.id("sfx.primo_geovishap_step")));
    public static final SoundEvent SOUND_PRIMO_CLAW_SWIPE = Registry.register(
            BuiltInRegistries.SOUND_EVENT, TeyvatBossesConstants.id("sfx.primo_geovishap_claw_swipe"),
            SoundEvent.createVariableRangeEvent(TeyvatBossesConstants.id("sfx.primo_geovishap_claw_swipe")));
    public static final SoundEvent SOUND_PRIMO_SLAM = Registry.register(
            BuiltInRegistries.SOUND_EVENT, TeyvatBossesConstants.id("sfx.primo_geovishap_slam"),
            SoundEvent.createVariableRangeEvent(TeyvatBossesConstants.id("sfx.primo_geovishap_slam")));
    public static final SoundEvent SOUND_PRIMO_BURROW = Registry.register(
            BuiltInRegistries.SOUND_EVENT, TeyvatBossesConstants.id("sfx.primo_geovishap_burrow"),
            SoundEvent.createVariableRangeEvent(TeyvatBossesConstants.id("sfx.primo_geovishap_burrow")));
    public static final SoundEvent SOUND_PRIMO_EMERGE = Registry.register(
            BuiltInRegistries.SOUND_EVENT, TeyvatBossesConstants.id("sfx.primo_geovishap_emerge"),
            SoundEvent.createVariableRangeEvent(TeyvatBossesConstants.id("sfx.primo_geovishap_emerge")));
    public static final SoundEvent SOUND_PRIMO_SHOWER_CHARGE = Registry.register(
            BuiltInRegistries.SOUND_EVENT, TeyvatBossesConstants.id("sfx.primo_geovishap_shower_charge"),
            SoundEvent.createVariableRangeEvent(TeyvatBossesConstants.id("sfx.primo_geovishap_shower_charge")));
    public static final SoundEvent SOUND_PRIMO_SHOWER_BLAST = Registry.register(
            BuiltInRegistries.SOUND_EVENT, TeyvatBossesConstants.id("sfx.primo_geovishap_shower_blast"),
            SoundEvent.createVariableRangeEvent(TeyvatBossesConstants.id("sfx.primo_geovishap_shower_blast")));
    public static final SoundEvent SOUND_PRIMO_SHIELD_REFLECT = Registry.register(
            BuiltInRegistries.SOUND_EVENT, TeyvatBossesConstants.id("sfx.primo_geovishap_shield_reflect"),
            SoundEvent.createVariableRangeEvent(TeyvatBossesConstants.id("sfx.primo_geovishap_shield_reflect")));
    public static final SoundEvent SOUND_PRIMO_DEATH = Registry.register(
            BuiltInRegistries.SOUND_EVENT, TeyvatBossesConstants.id("sfx.primo_geovishap_death"),
            SoundEvent.createVariableRangeEvent(TeyvatBossesConstants.id("sfx.primo_geovishap_death")));

    // 5 Music Discs
    public static final Item MUSIC_DISC_CRYO_REGISVINE = Registry.register(
            BuiltInRegistries.ITEM, TeyvatBossesConstants.id("music_disc_cryo_regisvine"),
            new Item(new Item.Properties().stacksTo(1).rarity(Rarity.RARE)
                    .jukeboxPlayable(ResourceKey.create(Registries.JUKEBOX_SONG, TeyvatBossesConstants.id("cryo_regisvine_theme")))));
    public static final Item MUSIC_DISC_ANEMO_HYPOSTASIS = Registry.register(
            BuiltInRegistries.ITEM, TeyvatBossesConstants.id("music_disc_anemo_hypostasis"),
            new Item(new Item.Properties().stacksTo(1).rarity(Rarity.RARE)
                    .jukeboxPlayable(ResourceKey.create(Registries.JUKEBOX_SONG, TeyvatBossesConstants.id("anemo_hypostasis_theme")))));
    public static final Item MUSIC_DISC_RUIN_GUARD = Registry.register(
            BuiltInRegistries.ITEM, TeyvatBossesConstants.id("music_disc_ruin_guard"),
            new Item(new Item.Properties().stacksTo(1).rarity(Rarity.RARE)
                    .jukeboxPlayable(ResourceKey.create(Registries.JUKEBOX_SONG, TeyvatBossesConstants.id("ruin_guard_theme")))));
    public static final Item MUSIC_DISC_RUIN_NAUTILUS = Registry.register(
            BuiltInRegistries.ITEM, TeyvatBossesConstants.id("music_disc_ruin_nautilus"),
            new Item(new Item.Properties().stacksTo(1).rarity(Rarity.RARE)
                    .jukeboxPlayable(ResourceKey.create(Registries.JUKEBOX_SONG, TeyvatBossesConstants.id("ruin_nautilus_theme")))));
    public static final Item MUSIC_DISC_PRIMO_GEOVISHAP = Registry.register(
            BuiltInRegistries.ITEM, TeyvatBossesConstants.id("music_disc_primo_geovishap"),
            new Item(new Item.Properties().stacksTo(1).rarity(Rarity.RARE)
                    .jukeboxPlayable(ResourceKey.create(Registries.JUKEBOX_SONG, TeyvatBossesConstants.id("primo_geovishap_theme")))));

    public static final StructureType<FlatArenaStructure> FLAT_ARENA = Registry.register(
            BuiltInRegistries.STRUCTURE_TYPE, TeyvatBossesConstants.id("flat_arena"),
            () -> FlatArenaStructure.CODEC);

    public static final CreativeModeTab MAIN_TAB = Registry.register(
            BuiltInRegistries.CREATIVE_MODE_TAB,
            TeyvatBossesConstants.id("main"),
            FabricItemGroup.builder()
                    .title(Component.translatable("itemGroup.velorise_teyvat_bosses.main"))
                    .icon(() -> new ItemStack(PRIMO_GEOVISHAP_SPAWN_EGG))
                    .displayItems((params, output) -> {
                        output.accept(CRYO_REGISVINE_COMPASS);
                        output.accept(ANEMO_HYPOSTASIS_COMPASS);
                        output.accept(RUIN_GUARD_COMPASS);
                        output.accept(RUIN_NAUTILUS_COMPASS);
                        output.accept(PRIMO_GEOVISHAP_COMPASS);
                        output.accept(CRYO_REGISVINE_SPAWN_EGG);
                        output.accept(ANEMO_HYPOSTASIS_SPAWN_EGG);
                        output.accept(RUIN_GUARD_SPAWN_EGG);
                        output.accept(RUIN_NAUTILUS_SPAWN_EGG);
                        output.accept(PRIMO_GEOVISHAP_SPAWN_EGG);
                        output.accept(CRYO_REGISVINE_TROPHY_ITEM);
                        output.accept(ANEMO_HYPOSTASIS_TROPHY_ITEM);
                        output.accept(RUIN_GUARD_TROPHY_ITEM);
                        output.accept(RUIN_NAUTILUS_TROPHY_ITEM);
                        output.accept(PRIMO_GEOVISHAP_TROPHY_ITEM);
                        output.accept(CONDENSED_REDSTONE_BLOCK_ITEM);
                        output.accept(RUIN_MECH_CORE);
                        output.accept(RUIN_MECH_LEFT_ARM);
                        output.accept(RUIN_MECH_RIGHT_ARM);
                        output.accept(RUIN_MECH_LEFT_LEG);
                        output.accept(RUIN_MECH_RIGHT_LEG);
                        output.accept(CRYO_TURRET_ROOT);
                        output.accept(CRYO_TURRET_STEM);
                        output.accept(CRYO_TURRET_COROLLA);
                        output.accept(ANEMO_PRISM_CUBE);
                        output.accept(RUIN_SUB_HULL);
                        output.accept(RUIN_SUB_COCKPIT);
                        output.accept(RUIN_SUB_PROPELLER);
                        output.accept(RUIN_SUB_TORPEDO_POD);
                        output.accept(GEO_DRILLER_CHASSIS);
                        output.accept(GEO_DRILLER_DRILL);
                        output.accept(GEO_DRILLER_ENGINE);
                        output.accept(PRIMORDIAL_JADE);
                        output.accept(PRIMORDIAL_GEO_INGOT);
                        output.accept(GEOVISHAP_HELMET);
                        output.accept(GEOVISHAP_CHESTPLATE);
                        output.accept(GEOVISHAP_LEGGINGS);
                        output.accept(GEOVISHAP_BOOTS);
                        output.accept(MUSIC_DISC_CRYO_REGISVINE);
                        output.accept(MUSIC_DISC_ANEMO_HYPOSTASIS);
                        output.accept(MUSIC_DISC_RUIN_GUARD);
                        output.accept(MUSIC_DISC_RUIN_NAUTILUS);
                        output.accept(MUSIC_DISC_PRIMO_GEOVISHAP);
                    })
                    .build()
    );

    @Override
    public void onInitialize() {
        TeyvatBossesConstants.LOGGER.info("Initializing {} (Fabric 1.21.1)", TeyvatBossesConstants.MOD_NAME);
        TeyvatBossesConfig.load();
        TeyvatBossesEntities.bind(
                () -> CRYO_REGISVINE, () -> CRYO_ICICLE, () -> CRYO_ICE_SPIKE, () -> BOSS_RESPAWN_ANCHOR,
                () -> ANEMO_HYPOSTASIS, () -> ANEMO_WIND_ORB, () -> ANEMO_TORNADO, () -> ANEMO_MISSILE, () -> WIND_CURRENT, () -> TROUNCE_BLOSSOM,
                () -> RUIN_GUARD, () -> RUIN_MISSILE, () -> RUIN_MECH, () -> CRYO_TURRET,
                () -> RUIN_NAUTILUS, () -> RUIN_TORPEDO, () -> RUIN_SUBMERSIBLE, () -> ANEMO_COMPANION,
                () -> PRIMO_GEOVISHAP, () -> GEO_DRILLER, () -> PRIMO_INFUSED_STONE
        );
        TeyvatBossesEntities.bindPrimoPart(() -> PRIMO_GEOVISHAP_PART);
        TeyvatBossesBlocks.bind(
                () -> CRYO_REGISVINE_TROPHY, () -> ANEMO_HYPOSTASIS_TROPHY,
                () -> RUIN_GUARD_TROPHY, () -> RUIN_NAUTILUS_TROPHY, () -> PRIMO_GEOVISHAP_TROPHY, () -> BOSS_TROPHY_BE
        );
        TeyvatBossesBlocks.bindCondensedRedstone(() -> CONDENSED_REDSTONE_BLOCK);
        TeyvatBossesItems.bindCondensedRedstone(() -> CONDENSED_REDSTONE_BLOCK_ITEM);
        TeyvatBossesItems.bindGeovishapGear(
                () -> PRIMORDIAL_JADE, () -> PRIMORDIAL_GEO_INGOT,
                () -> GEOVISHAP_HELMET, () -> GEOVISHAP_CHESTPLATE, () -> GEOVISHAP_LEGGINGS, () -> GEOVISHAP_BOOTS);
        TeyvatBossesStructures.bind(() -> FLAT_ARENA);
        TeyvatBossesSounds.bind(
                () -> SOUND_MECH_ENGINE_HUM, () -> SOUND_MECH_LOCKON_BEEP, () -> SOUND_MECH_VISOR_SEAL,
                () -> SOUND_MECH_SERVO_WHIR, () -> SOUND_MECH_JOINT_CREAK, () -> SOUND_MECH_METAL_CLANK, () -> SOUND_MECH_HYDRAULIC_HISS,
                () -> SOUND_SUB_PROPELLER_HUM, () -> SOUND_SUB_SONAR_PING, () -> SOUND_SUB_TORPEDO_LAUNCH, () -> SOUND_SUB_BALLAST_HISS,
                () -> SOUND_DRILL_ENGINE_IDLE, () -> SOUND_DRILL_BORE_SPIN,
                () -> SOUND_RUIN_GUARD_SPEECH, () -> SOUND_RUIN_GUARD_POWER_DOWN,
                () -> SOUND_PRIMO_ROAR, () -> SOUND_PRIMO_STEP, () -> SOUND_PRIMO_CLAW_SWIPE, () -> SOUND_PRIMO_SLAM, () -> SOUND_PRIMO_BURROW, () -> SOUND_PRIMO_EMERGE, () -> SOUND_PRIMO_SHOWER_CHARGE, () -> SOUND_PRIMO_SHOWER_BLAST, () -> SOUND_PRIMO_SHIELD_REFLECT, () -> SOUND_PRIMO_DEATH
        );
        TeyvatBossesItems.bind(
                () -> MUSIC_DISC_CRYO_REGISVINE, () -> MUSIC_DISC_ANEMO_HYPOSTASIS, () -> MUSIC_DISC_RUIN_GUARD,
                () -> MUSIC_DISC_RUIN_NAUTILUS, () -> MUSIC_DISC_PRIMO_GEOVISHAP,
                () -> RUIN_MECH_CORE, () -> RUIN_MECH_LEFT_ARM, () -> RUIN_MECH_RIGHT_ARM, () -> RUIN_MECH_LEFT_LEG, () -> RUIN_MECH_RIGHT_LEG,
                () -> CRYO_TURRET_ROOT, () -> CRYO_TURRET_STEM, () -> CRYO_TURRET_COROLLA, () -> ANEMO_PRISM_CUBE,
                () -> RUIN_SUB_HULL, () -> RUIN_SUB_COCKPIT, () -> RUIN_SUB_PROPELLER, () -> RUIN_SUB_TORPEDO_POD,
                () -> GEO_DRILLER_CHASSIS, () -> GEO_DRILLER_DRILL, () -> GEO_DRILLER_ENGINE
        );
        FabricDefaultAttributeRegistry.register(CRYO_REGISVINE, CryoRegisvineEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(ANEMO_HYPOSTASIS, AnemoHypostasisEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(RUIN_GUARD, RuinGuardEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(RUIN_MECH, RuinMechEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(CRYO_TURRET, CryoTurretEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(RUIN_NAUTILUS, RuinNautilusEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(RUIN_SUBMERSIBLE, RuinSubmersibleEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(ANEMO_COMPANION, AnemoCompanionEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(PRIMO_GEOVISHAP, PrimoGeovishapEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(GEO_DRILLER, GeoDrillerEntity.createAttributes());

        PayloadTypeRegistry.playC2S().register(RuinMechSkillPayload.TYPE, RuinMechSkillPayload.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(RuinSubControlPayload.TYPE, RuinSubControlPayload.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(GeoDrillerControlPayload.TYPE, GeoDrillerControlPayload.STREAM_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(RuinSubControlPayload.TYPE, (payload, ctx) ->
                ctx.server().execute(() -> RuinSubControlPayload.handleOnServer(payload, ctx.player())));
        ServerPlayNetworking.registerGlobalReceiver(GeoDrillerControlPayload.TYPE, (payload, ctx) ->
                ctx.server().execute(() -> GeoDrillerControlPayload.handleOnServer(payload, ctx.player())));
        ServerPlayNetworking.registerGlobalReceiver(RuinMechSkillPayload.TYPE, (payload, ctx) ->
                ctx.server().execute(() -> {
                    if (ctx.player().getVehicle() instanceof RuinMechEntity mech) {
                        mech.tryTriggerSkill(payload.skillId());
                    }
                }));
    }
}

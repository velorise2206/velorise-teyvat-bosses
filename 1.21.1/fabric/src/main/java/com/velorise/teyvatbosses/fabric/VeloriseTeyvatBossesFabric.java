package com.velorise.teyvatbosses.fabric;

import com.velorise.teyvatbosses.TeyvatBossesConstants;
import com.velorise.teyvatbosses.entity.AnemoHypostasisEntity;
import com.velorise.teyvatbosses.entity.AstralionEntity;
import com.velorise.teyvatbosses.entity.AnemoMissileProjectile;
import com.velorise.teyvatbosses.entity.AnemoTornadoEntity;
import com.velorise.teyvatbosses.entity.AnemoWindOrbEntity;
import com.velorise.teyvatbosses.entity.BossRespawnAnchorEntity;
import com.velorise.teyvatbosses.entity.CryoIceSpikeEntity;
import com.velorise.teyvatbosses.entity.CryoIcicleProjectile;
import com.velorise.teyvatbosses.entity.CryoRegisvineEntity;
import com.velorise.teyvatbosses.entity.ElementalStarEntity;
import com.velorise.teyvatbosses.entity.RuinGuardEntity;
import com.velorise.teyvatbosses.entity.RuinMissileProjectile;
import com.velorise.teyvatbosses.entity.SkySpearEntity;
import com.velorise.teyvatbosses.entity.TheOpenEyedEntity;
import com.velorise.teyvatbosses.entity.TrounceBlossomEntity;
import com.velorise.teyvatbosses.entity.WindCurrentEntity;
import com.velorise.teyvatbosses.registry.TeyvatBossesEntities;
import com.velorise.teyvatbosses.registry.TeyvatBossesItems;
import com.velorise.teyvatbosses.registry.TeyvatBossesStructures;
import com.velorise.teyvatbosses.worldgen.FlatArenaStructure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SpawnEggItem;

public class VeloriseTeyvatBossesFabric implements ModInitializer {

    public static final EntityType<CryoRegisvineEntity> CRYO_REGISVINE = Registry.register(
            BuiltInRegistries.ENTITY_TYPE, TeyvatBossesConstants.id("cryo_regisvine"),
            EntityType.Builder.of(CryoRegisvineEntity::new, MobCategory.MONSTER).sized(3.4F, 6.2F).clientTrackingRange(12).build());

    public static final EntityType<CryoIcicleProjectile> CRYO_ICICLE = Registry.register(
            BuiltInRegistries.ENTITY_TYPE, TeyvatBossesConstants.id("cryo_icicle"),
            EntityType.Builder.<CryoIcicleProjectile>of(CryoIcicleProjectile::new, MobCategory.MISC).sized(0.55F, 0.55F).clientTrackingRange(10).updateInterval(2).build());

    public static final EntityType<CryoIceSpikeEntity> CRYO_ICE_SPIKE = Registry.register(
            BuiltInRegistries.ENTITY_TYPE, TeyvatBossesConstants.id("cryo_ice_spike"),
            EntityType.Builder.<CryoIceSpikeEntity>of(CryoIceSpikeEntity::new, MobCategory.MISC).sized(1.6F, 3.6F).clientTrackingRange(10).updateInterval(2).build());

    public static final EntityType<TheOpenEyedEntity> THE_OPEN_EYED = Registry.register(
            BuiltInRegistries.ENTITY_TYPE, TeyvatBossesConstants.id("the_open_eyed"),
            EntityType.Builder.of(TheOpenEyedEntity::new, MobCategory.MONSTER).sized(2.2F, 5.2F).clientTrackingRange(14).build());

    public static final EntityType<ElementalStarEntity> ELEMENTAL_STAR = Registry.register(
            BuiltInRegistries.ENTITY_TYPE, TeyvatBossesConstants.id("elemental_star"),
            EntityType.Builder.of(ElementalStarEntity::new, MobCategory.MONSTER).sized(1.8F, 2.2F).clientTrackingRange(12).build());

    public static final EntityType<SkySpearEntity> SKY_SPEAR = Registry.register(
            BuiltInRegistries.ENTITY_TYPE, TeyvatBossesConstants.id("sky_spear"),
            EntityType.Builder.<SkySpearEntity>of(SkySpearEntity::new, MobCategory.MISC).sized(2.2F, 9.0F).clientTrackingRange(14).updateInterval(2).build());

    public static final EntityType<BossRespawnAnchorEntity> BOSS_RESPAWN_ANCHOR = Registry.register(
            BuiltInRegistries.ENTITY_TYPE, TeyvatBossesConstants.id("boss_respawn_anchor"),
            EntityType.Builder.<BossRespawnAnchorEntity>of(BossRespawnAnchorEntity::new, MobCategory.MISC).sized(1.4F, 2.2F).clientTrackingRange(10).updateInterval(10).build());

    public static final EntityType<AnemoHypostasisEntity> ANEMO_HYPOSTASIS = Registry.register(
            BuiltInRegistries.ENTITY_TYPE, TeyvatBossesConstants.id("anemo_hypostasis"),
            EntityType.Builder.of(AnemoHypostasisEntity::new, MobCategory.MONSTER).sized(2.6F, 3.2F).clientTrackingRange(14).build());

    public static final EntityType<AnemoWindOrbEntity> ANEMO_WIND_ORB = Registry.register(
            BuiltInRegistries.ENTITY_TYPE, TeyvatBossesConstants.id("anemo_wind_orb"),
            EntityType.Builder.<AnemoWindOrbEntity>of(AnemoWindOrbEntity::new, MobCategory.MISC).sized(1.2F, 1.2F).clientTrackingRange(12).updateInterval(2).build());

    public static final EntityType<AnemoTornadoEntity> ANEMO_TORNADO = Registry.register(
            BuiltInRegistries.ENTITY_TYPE, TeyvatBossesConstants.id("anemo_tornado"),
            EntityType.Builder.<AnemoTornadoEntity>of(AnemoTornadoEntity::new, MobCategory.MISC).sized(2.4F, 6.0F).clientTrackingRange(14).updateInterval(2).build());

    public static final EntityType<AnemoMissileProjectile> ANEMO_MISSILE = Registry.register(
            BuiltInRegistries.ENTITY_TYPE, TeyvatBossesConstants.id("anemo_missile"),
            EntityType.Builder.<AnemoMissileProjectile>of(AnemoMissileProjectile::new, MobCategory.MISC).sized(0.9F, 0.9F).clientTrackingRange(12).updateInterval(2).build());

    public static final EntityType<WindCurrentEntity> WIND_CURRENT = Registry.register(
            BuiltInRegistries.ENTITY_TYPE, TeyvatBossesConstants.id("wind_current"),
            EntityType.Builder.<WindCurrentEntity>of(WindCurrentEntity::new, MobCategory.MISC).sized(4.5F, 9.5F).clientTrackingRange(14).updateInterval(4).build());

    public static final EntityType<TrounceBlossomEntity> TROUNCE_BLOSSOM = Registry.register(
            BuiltInRegistries.ENTITY_TYPE, TeyvatBossesConstants.id("trounce_blossom"),
            EntityType.Builder.<TrounceBlossomEntity>of(TrounceBlossomEntity::new, MobCategory.MISC).sized(1.5F, 2.2F).clientTrackingRange(12).updateInterval(4).build());

    public static final EntityType<RuinGuardEntity> RUIN_GUARD = Registry.register(
            BuiltInRegistries.ENTITY_TYPE, TeyvatBossesConstants.id("ruin_guard"),
            EntityType.Builder.of(RuinGuardEntity::new, MobCategory.MONSTER).sized(2.4F, 4.0F).clientTrackingRange(14).build());

    public static final EntityType<RuinMissileProjectile> RUIN_MISSILE = Registry.register(
            BuiltInRegistries.ENTITY_TYPE, TeyvatBossesConstants.id("ruin_missile"),
            EntityType.Builder.<RuinMissileProjectile>of(RuinMissileProjectile::new, MobCategory.MISC).sized(0.75F, 0.75F).clientTrackingRange(12).updateInterval(2).build());

    public static final EntityType<AstralionEntity> ASTRALION = Registry.register(
            BuiltInRegistries.ENTITY_TYPE, TeyvatBossesConstants.id("astralion"),
            EntityType.Builder.of(AstralionEntity::new, MobCategory.MONSTER).sized(2.6F, 5.4F).clientTrackingRange(16).build());

    public static final Item CRYO_REGISVINE_SPAWN_EGG = Registry.register(
            BuiltInRegistries.ITEM, TeyvatBossesConstants.id("cryo_regisvine_spawn_egg"),
            new SpawnEggItem(CRYO_REGISVINE, 0x1E4B7A, 0x9BE8FF, new Item.Properties()));

    public static final Item THE_OPEN_EYED_SPAWN_EGG = Registry.register(
            BuiltInRegistries.ITEM, TeyvatBossesConstants.id("the_open_eyed_spawn_egg"),
            new SpawnEggItem(THE_OPEN_EYED, 0x2B2438, 0xF5D27A, new Item.Properties()));

    public static final Item ANEMO_HYPOSTASIS_SPAWN_EGG = Registry.register(
            BuiltInRegistries.ITEM, TeyvatBossesConstants.id("anemo_hypostasis_spawn_egg"),
            new SpawnEggItem(ANEMO_HYPOSTASIS, 0x1E3B42, 0x74F9DF, new Item.Properties()));

    public static final Item RUIN_GUARD_SPAWN_EGG = Registry.register(
            BuiltInRegistries.ITEM, TeyvatBossesConstants.id("ruin_guard_spawn_egg"),
            new SpawnEggItem(RUIN_GUARD, 0x4E3C2B, 0xFFB338, new Item.Properties()));

    public static final Item ASTRALION_SPAWN_EGG = Registry.register(
            BuiltInRegistries.ITEM, TeyvatBossesConstants.id("astralion_spawn_egg"),
            new SpawnEggItem(ASTRALION, 0x1D1135, 0xE5A4FF, new Item.Properties()));

    public static final SoundEvent SOUND_CRYO_THEME = Registry.register(
            BuiltInRegistries.SOUND_EVENT, TeyvatBossesConstants.id("music.cryo_regisvine_theme"),
            SoundEvent.createVariableRangeEvent(TeyvatBossesConstants.id("music.cryo_regisvine_theme")));
    public static final SoundEvent SOUND_OPEN_EYED_THEME = Registry.register(
            BuiltInRegistries.SOUND_EVENT, TeyvatBossesConstants.id("music.the_open_eyed_theme"),
            SoundEvent.createVariableRangeEvent(TeyvatBossesConstants.id("music.the_open_eyed_theme")));
    public static final SoundEvent SOUND_ANEMO_THEME = Registry.register(
            BuiltInRegistries.SOUND_EVENT, TeyvatBossesConstants.id("music.anemo_hypostasis_theme"),
            SoundEvent.createVariableRangeEvent(TeyvatBossesConstants.id("music.anemo_hypostasis_theme")));
    public static final SoundEvent SOUND_RUIN_GUARD_THEME = Registry.register(
            BuiltInRegistries.SOUND_EVENT, TeyvatBossesConstants.id("music.ruin_guard_theme"),
            SoundEvent.createVariableRangeEvent(TeyvatBossesConstants.id("music.ruin_guard_theme")));

    public static final Item MUSIC_DISC_CRYO_REGISVINE = Registry.register(
            BuiltInRegistries.ITEM, TeyvatBossesConstants.id("music_disc_cryo_regisvine"),
            new Item(new Item.Properties().stacksTo(1).rarity(Rarity.RARE)
                    .jukeboxPlayable(ResourceKey.create(Registries.JUKEBOX_SONG, TeyvatBossesConstants.id("cryo_regisvine_theme")))));
    public static final Item MUSIC_DISC_THE_OPEN_EYED = Registry.register(
            BuiltInRegistries.ITEM, TeyvatBossesConstants.id("music_disc_the_open_eyed"),
            new Item(new Item.Properties().stacksTo(1).rarity(Rarity.RARE)
                    .jukeboxPlayable(ResourceKey.create(Registries.JUKEBOX_SONG, TeyvatBossesConstants.id("the_open_eyed_theme")))));
    public static final Item MUSIC_DISC_ANEMO_HYPOSTASIS = Registry.register(
            BuiltInRegistries.ITEM, TeyvatBossesConstants.id("music_disc_anemo_hypostasis"),
            new Item(new Item.Properties().stacksTo(1).rarity(Rarity.RARE)
                    .jukeboxPlayable(ResourceKey.create(Registries.JUKEBOX_SONG, TeyvatBossesConstants.id("anemo_hypostasis_theme")))));
    public static final Item MUSIC_DISC_RUIN_GUARD = Registry.register(
            BuiltInRegistries.ITEM, TeyvatBossesConstants.id("music_disc_ruin_guard"),
            new Item(new Item.Properties().stacksTo(1).rarity(Rarity.RARE)
                    .jukeboxPlayable(ResourceKey.create(Registries.JUKEBOX_SONG, TeyvatBossesConstants.id("ruin_guard_theme")))));

    public static final StructureType<FlatArenaStructure> FLAT_ARENA = Registry.register(
            BuiltInRegistries.STRUCTURE_TYPE, TeyvatBossesConstants.id("flat_arena"),
            () -> FlatArenaStructure.CODEC);

    public static final CreativeModeTab MAIN_TAB = Registry.register(
            BuiltInRegistries.CREATIVE_MODE_TAB,
            TeyvatBossesConstants.id("main"),
            FabricItemGroup.builder()
                    .title(Component.translatable("itemGroup.velorise_teyvat_bosses.main"))
                    .icon(() -> new ItemStack(ANEMO_HYPOSTASIS_SPAWN_EGG))
                    .displayItems((params, output) -> {
                        output.accept(CRYO_REGISVINE_SPAWN_EGG);
                        output.accept(THE_OPEN_EYED_SPAWN_EGG);
                        output.accept(ANEMO_HYPOSTASIS_SPAWN_EGG);
                        output.accept(RUIN_GUARD_SPAWN_EGG);
                        output.accept(ASTRALION_SPAWN_EGG);
                        output.accept(MUSIC_DISC_CRYO_REGISVINE);
                        output.accept(MUSIC_DISC_THE_OPEN_EYED);
                        output.accept(MUSIC_DISC_ANEMO_HYPOSTASIS);
                        output.accept(MUSIC_DISC_RUIN_GUARD);
                    })
                    .build()
    );

    @Override
    public void onInitialize() {
        TeyvatBossesConstants.LOGGER.info("Initializing {} (Fabric 1.21.1)", TeyvatBossesConstants.MOD_NAME);
        TeyvatBossesEntities.bind(
                () -> CRYO_REGISVINE,
                () -> CRYO_ICICLE,
                () -> CRYO_ICE_SPIKE,
                () -> THE_OPEN_EYED,
                () -> ELEMENTAL_STAR,
                () -> SKY_SPEAR,
                () -> BOSS_RESPAWN_ANCHOR,
                () -> ANEMO_HYPOSTASIS,
                () -> ANEMO_WIND_ORB,
                () -> ANEMO_TORNADO,
                () -> ANEMO_MISSILE,
                () -> WIND_CURRENT,
                () -> TROUNCE_BLOSSOM,
                () -> RUIN_GUARD,
                () -> RUIN_MISSILE,
                () -> ASTRALION
        );

        TeyvatBossesStructures.bind(() -> FLAT_ARENA);
        TeyvatBossesItems.bind(
                () -> MUSIC_DISC_CRYO_REGISVINE,
                () -> MUSIC_DISC_THE_OPEN_EYED,
                () -> MUSIC_DISC_ANEMO_HYPOSTASIS,
                () -> MUSIC_DISC_RUIN_GUARD
        );
        FabricDefaultAttributeRegistry.register(CRYO_REGISVINE, CryoRegisvineEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(THE_OPEN_EYED, TheOpenEyedEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(ELEMENTAL_STAR, ElementalStarEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(ANEMO_HYPOSTASIS, AnemoHypostasisEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(RUIN_GUARD, RuinGuardEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(ASTRALION, AstralionEntity.createAttributes());
    }
}

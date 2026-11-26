package com.velorise.teyvatbosses.neoforge;

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
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(TeyvatBossesConstants.MOD_ID)
public class VeloriseTeyvatBossesNeoForge {

    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(Registries.ENTITY_TYPE, TeyvatBossesConstants.MOD_ID);
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(Registries.ITEM, TeyvatBossesConstants.MOD_ID);
    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(Registries.SOUND_EVENT, TeyvatBossesConstants.MOD_ID);
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, TeyvatBossesConstants.MOD_ID);
    public static final DeferredRegister<StructureType<?>> STRUCTURE_TYPES =
            DeferredRegister.create(Registries.STRUCTURE_TYPE, TeyvatBossesConstants.MOD_ID);

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

    public static final DeferredHolder<EntityType<?>, EntityType<TheOpenEyedEntity>> THE_OPEN_EYED =
            ENTITIES.register("the_open_eyed", () -> EntityType.Builder.of(TheOpenEyedEntity::new, MobCategory.MONSTER)
                    .sized(2.2F, 5.2F).clientTrackingRange(14).build("the_open_eyed"));

    public static final DeferredHolder<EntityType<?>, EntityType<ElementalStarEntity>> ELEMENTAL_STAR =
            ENTITIES.register("elemental_star", () -> EntityType.Builder.of(ElementalStarEntity::new, MobCategory.MONSTER)
                    .sized(1.8F, 2.2F).clientTrackingRange(12).build("elemental_star"));

    public static final DeferredHolder<EntityType<?>, EntityType<SkySpearEntity>> SKY_SPEAR =
            ENTITIES.register("sky_spear", () -> EntityType.Builder.<SkySpearEntity>of(SkySpearEntity::new, MobCategory.MISC)
                    .sized(2.2F, 9.0F).clientTrackingRange(14).updateInterval(2).build("sky_spear"));

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

    public static final DeferredHolder<EntityType<?>, EntityType<AstralionEntity>> ASTRALION =
            ENTITIES.register("astralion", () -> EntityType.Builder.of(AstralionEntity::new, MobCategory.MONSTER)
                    .sized(2.6F, 5.4F).clientTrackingRange(16).build("astralion"));

    public static final DeferredHolder<Item, Item> CRYO_REGISVINE_SPAWN_EGG =
            ITEMS.register("cryo_regisvine_spawn_egg", () -> new DeferredSpawnEggItem(
                    CRYO_REGISVINE, 0x1E4B7A, 0x9BE8FF, new Item.Properties()));

    public static final DeferredHolder<Item, Item> THE_OPEN_EYED_SPAWN_EGG =
            ITEMS.register("the_open_eyed_spawn_egg", () -> new DeferredSpawnEggItem(
                    THE_OPEN_EYED, 0x2B2438, 0xF5D27A, new Item.Properties()));

    public static final DeferredHolder<Item, Item> ANEMO_HYPOSTASIS_SPAWN_EGG =
            ITEMS.register("anemo_hypostasis_spawn_egg", () -> new DeferredSpawnEggItem(
                    ANEMO_HYPOSTASIS, 0x1E3B42, 0x74F9DF, new Item.Properties()));

    public static final DeferredHolder<Item, Item> RUIN_GUARD_SPAWN_EGG =
            ITEMS.register("ruin_guard_spawn_egg", () -> new DeferredSpawnEggItem(
                    RUIN_GUARD, 0x4E3C2B, 0xFFB338, new Item.Properties()));

    public static final DeferredHolder<Item, Item> ASTRALION_SPAWN_EGG =
            ITEMS.register("astralion_spawn_egg", () -> new DeferredSpawnEggItem(
                    ASTRALION, 0x1D1135, 0xE5A4FF, new Item.Properties()));

    public static final DeferredHolder<SoundEvent, SoundEvent> SOUND_CRYO_THEME =
            SOUNDS.register("music.cryo_regisvine_theme", () -> SoundEvent.createVariableRangeEvent(TeyvatBossesConstants.id("music.cryo_regisvine_theme")));
    public static final DeferredHolder<SoundEvent, SoundEvent> SOUND_OPEN_EYED_THEME =
            SOUNDS.register("music.the_open_eyed_theme", () -> SoundEvent.createVariableRangeEvent(TeyvatBossesConstants.id("music.the_open_eyed_theme")));
    public static final DeferredHolder<SoundEvent, SoundEvent> SOUND_ANEMO_THEME =
            SOUNDS.register("music.anemo_hypostasis_theme", () -> SoundEvent.createVariableRangeEvent(TeyvatBossesConstants.id("music.anemo_hypostasis_theme")));
    public static final DeferredHolder<SoundEvent, SoundEvent> SOUND_RUIN_GUARD_THEME =
            SOUNDS.register("music.ruin_guard_theme", () -> SoundEvent.createVariableRangeEvent(TeyvatBossesConstants.id("music.ruin_guard_theme")));

    public static final DeferredHolder<Item, Item> MUSIC_DISC_CRYO_REGISVINE =
            ITEMS.register("music_disc_cryo_regisvine", () -> new Item(new Item.Properties().stacksTo(1).rarity(Rarity.RARE)
                    .jukeboxPlayable(ResourceKey.create(Registries.JUKEBOX_SONG, TeyvatBossesConstants.id("cryo_regisvine_theme")))));
    public static final DeferredHolder<Item, Item> MUSIC_DISC_THE_OPEN_EYED =
            ITEMS.register("music_disc_the_open_eyed", () -> new Item(new Item.Properties().stacksTo(1).rarity(Rarity.RARE)
                    .jukeboxPlayable(ResourceKey.create(Registries.JUKEBOX_SONG, TeyvatBossesConstants.id("the_open_eyed_theme")))));
    public static final DeferredHolder<Item, Item> MUSIC_DISC_ANEMO_HYPOSTASIS =
            ITEMS.register("music_disc_anemo_hypostasis", () -> new Item(new Item.Properties().stacksTo(1).rarity(Rarity.RARE)
                    .jukeboxPlayable(ResourceKey.create(Registries.JUKEBOX_SONG, TeyvatBossesConstants.id("anemo_hypostasis_theme")))));
    public static final DeferredHolder<Item, Item> MUSIC_DISC_RUIN_GUARD =
            ITEMS.register("music_disc_ruin_guard", () -> new Item(new Item.Properties().stacksTo(1).rarity(Rarity.RARE)
                    .jukeboxPlayable(ResourceKey.create(Registries.JUKEBOX_SONG, TeyvatBossesConstants.id("ruin_guard_theme")))));

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN_TAB =
            TABS.register("main", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.velorise_teyvat_bosses.main"))
                    .icon(() -> new ItemStack(ANEMO_HYPOSTASIS_SPAWN_EGG.get()))
                    .displayItems((params, output) -> {
                        output.accept(CRYO_REGISVINE_SPAWN_EGG.get());
                        output.accept(THE_OPEN_EYED_SPAWN_EGG.get());
                        output.accept(ANEMO_HYPOSTASIS_SPAWN_EGG.get());
                        output.accept(RUIN_GUARD_SPAWN_EGG.get());
                        output.accept(ASTRALION_SPAWN_EGG.get());
                        output.accept(MUSIC_DISC_CRYO_REGISVINE.get());
                        output.accept(MUSIC_DISC_THE_OPEN_EYED.get());
                        output.accept(MUSIC_DISC_ANEMO_HYPOSTASIS.get());
                        output.accept(MUSIC_DISC_RUIN_GUARD.get());
                    })
                    .build());

    public VeloriseTeyvatBossesNeoForge(IEventBus modEventBus) {
        TeyvatBossesConstants.LOGGER.info("Initializing {} (NeoForge 1.21.1)", TeyvatBossesConstants.MOD_NAME);
        ENTITIES.register(modEventBus);
        ITEMS.register(modEventBus);
        SOUNDS.register(modEventBus);
        TABS.register(modEventBus);
        STRUCTURE_TYPES.register(modEventBus);
        TeyvatBossesStructures.bind(FLAT_ARENA);

        TeyvatBossesEntities.bind(
                CRYO_REGISVINE, CRYO_ICICLE, CRYO_ICE_SPIKE, THE_OPEN_EYED, ELEMENTAL_STAR, SKY_SPEAR, BOSS_RESPAWN_ANCHOR,
                ANEMO_HYPOSTASIS, ANEMO_WIND_ORB, ANEMO_TORNADO, ANEMO_MISSILE, WIND_CURRENT, TROUNCE_BLOSSOM,
                RUIN_GUARD, RUIN_MISSILE, ASTRALION
        );

        TeyvatBossesItems.bind(MUSIC_DISC_CRYO_REGISVINE, MUSIC_DISC_THE_OPEN_EYED, MUSIC_DISC_ANEMO_HYPOSTASIS, MUSIC_DISC_RUIN_GUARD);
        modEventBus.addListener(this::registerAttributes);
    }

    private void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(CRYO_REGISVINE.get(), CryoRegisvineEntity.createAttributes().build());
        event.put(THE_OPEN_EYED.get(), TheOpenEyedEntity.createAttributes().build());
        event.put(ELEMENTAL_STAR.get(), ElementalStarEntity.createAttributes().build());
        event.put(ANEMO_HYPOSTASIS.get(), AnemoHypostasisEntity.createAttributes().build());
        event.put(RUIN_GUARD.get(), RuinGuardEntity.createAttributes().build());
        event.put(ASTRALION.get(), AstralionEntity.createAttributes().build());
    }
}

package com.velorise.teyvatbosses.neoforge;

import com.velorise.teyvatbosses.TeyvatBossesConstants;
import com.velorise.teyvatbosses.entity.CryoIcicleProjectile;
import com.velorise.teyvatbosses.entity.CryoRegisvineEntity;
import com.velorise.teyvatbosses.entity.ElementalStarEntity;
import com.velorise.teyvatbosses.entity.TheOpenEyedEntity;
import com.velorise.teyvatbosses.registry.TeyvatBossesEntities;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
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
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, TeyvatBossesConstants.MOD_ID);

    public static final DeferredHolder<EntityType<?>, EntityType<CryoRegisvineEntity>> CRYO_REGISVINE =
            ENTITIES.register("cryo_regisvine", () -> EntityType.Builder.of(CryoRegisvineEntity::new, MobCategory.MONSTER)
                    .sized(3.4F, 6.2F)
                    .clientTrackingRange(12)
                    .build("cryo_regisvine"));

    public static final DeferredHolder<EntityType<?>, EntityType<CryoIcicleProjectile>> CRYO_ICICLE =
            ENTITIES.register("cryo_icicle", () -> EntityType.Builder.<CryoIcicleProjectile>of(CryoIcicleProjectile::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(8)
                    .updateInterval(2)
                    .build("cryo_icicle"));

    public static final DeferredHolder<EntityType<?>, EntityType<TheOpenEyedEntity>> THE_OPEN_EYED =
            ENTITIES.register("the_open_eyed", () -> EntityType.Builder.of(TheOpenEyedEntity::new, MobCategory.MONSTER)
                    .sized(2.2F, 5.2F)
                    .clientTrackingRange(14)
                    .build("the_open_eyed"));

    public static final DeferredHolder<EntityType<?>, EntityType<ElementalStarEntity>> ELEMENTAL_STAR =
            ENTITIES.register("elemental_star", () -> EntityType.Builder.of(ElementalStarEntity::new, MobCategory.MONSTER)
                    .sized(1.6F, 1.6F)
                    .clientTrackingRange(12)
                    .build("elemental_star"));

    public static final DeferredHolder<Item, Item> CRYO_REGISVINE_SPAWN_EGG =
            ITEMS.register("cryo_regisvine_spawn_egg", () -> new DeferredSpawnEggItem(
                    CRYO_REGISVINE, 0x1E4B7A, 0x9BE8FF, new Item.Properties()));

    public static final DeferredHolder<Item, Item> THE_OPEN_EYED_SPAWN_EGG =
            ITEMS.register("the_open_eyed_spawn_egg", () -> new DeferredSpawnEggItem(
                    THE_OPEN_EYED, 0x2B2438, 0xF5D27A, new Item.Properties()));

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN_TAB =
            TABS.register("main", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.velorise_teyvat_bosses.main"))
                    .icon(() -> new ItemStack(THE_OPEN_EYED_SPAWN_EGG.get()))
                    .displayItems((params, output) -> {
                        output.accept(CRYO_REGISVINE_SPAWN_EGG.get());
                        output.accept(THE_OPEN_EYED_SPAWN_EGG.get());
                    })
                    .build());

    public VeloriseTeyvatBossesNeoForge(IEventBus modEventBus) {
        TeyvatBossesConstants.LOGGER.info("Initializing {} (NeoForge 1.21.1)", TeyvatBossesConstants.MOD_NAME);
        ENTITIES.register(modEventBus);
        ITEMS.register(modEventBus);
        TABS.register(modEventBus);

        TeyvatBossesEntities.bind(CRYO_REGISVINE, CRYO_ICICLE, THE_OPEN_EYED, ELEMENTAL_STAR);

        modEventBus.addListener(this::registerAttributes);
    }

    private void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(CRYO_REGISVINE.get(), CryoRegisvineEntity.createAttributes().build());
        event.put(THE_OPEN_EYED.get(), TheOpenEyedEntity.createAttributes().build());
        event.put(ELEMENTAL_STAR.get(), ElementalStarEntity.createAttributes().build());
    }
}

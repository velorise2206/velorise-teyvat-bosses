package com.velorise.teyvatbosses.fabric;

import com.velorise.teyvatbosses.TeyvatBossesConstants;
import com.velorise.teyvatbosses.entity.CryoIcicleProjectile;
import com.velorise.teyvatbosses.entity.CryoRegisvineEntity;
import com.velorise.teyvatbosses.entity.ElementalStarEntity;
import com.velorise.teyvatbosses.entity.TheOpenEyedEntity;
import com.velorise.teyvatbosses.registry.TeyvatBossesEntities;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;

public class VeloriseTeyvatBossesFabric implements ModInitializer {

    public static final EntityType<CryoRegisvineEntity> CRYO_REGISVINE = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            TeyvatBossesConstants.id("cryo_regisvine"),
            EntityType.Builder.of(CryoRegisvineEntity::new, MobCategory.MONSTER)
                    .sized(3.4F, 6.2F)
                    .clientTrackingRange(12)
                    .build()
    );

    public static final EntityType<CryoIcicleProjectile> CRYO_ICICLE = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            TeyvatBossesConstants.id("cryo_icicle"),
            EntityType.Builder.<CryoIcicleProjectile>of(CryoIcicleProjectile::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(8)
                    .updateInterval(2)
                    .build()
    );

    public static final EntityType<TheOpenEyedEntity> THE_OPEN_EYED = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            TeyvatBossesConstants.id("the_open_eyed"),
            EntityType.Builder.of(TheOpenEyedEntity::new, MobCategory.MONSTER)
                    .sized(2.2F, 5.2F)
                    .clientTrackingRange(14)
                    .build()
    );

    public static final EntityType<ElementalStarEntity> ELEMENTAL_STAR = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            TeyvatBossesConstants.id("elemental_star"),
            EntityType.Builder.of(ElementalStarEntity::new, MobCategory.MONSTER)
                    .sized(1.6F, 1.6F)
                    .clientTrackingRange(12)
                    .build()
    );

    public static final Item CRYO_REGISVINE_SPAWN_EGG = Registry.register(
            BuiltInRegistries.ITEM,
            TeyvatBossesConstants.id("cryo_regisvine_spawn_egg"),
            new SpawnEggItem(CRYO_REGISVINE, 0x1E4B7A, 0x9BE8FF, new Item.Properties())
    );

    public static final Item THE_OPEN_EYED_SPAWN_EGG = Registry.register(
            BuiltInRegistries.ITEM,
            TeyvatBossesConstants.id("the_open_eyed_spawn_egg"),
            new SpawnEggItem(THE_OPEN_EYED, 0x2B2438, 0xF5D27A, new Item.Properties())
    );

    public static final CreativeModeTab MAIN_TAB = Registry.register(
            BuiltInRegistries.CREATIVE_MODE_TAB,
            TeyvatBossesConstants.id("main"),
            FabricItemGroup.builder()
                    .title(Component.translatable("itemGroup.velorise_teyvat_bosses.main"))
                    .icon(() -> new ItemStack(THE_OPEN_EYED_SPAWN_EGG))
                    .displayItems((params, output) -> {
                        output.accept(CRYO_REGISVINE_SPAWN_EGG);
                        output.accept(THE_OPEN_EYED_SPAWN_EGG);
                    })
                    .build()
    );

    @Override
    public void onInitialize() {
        TeyvatBossesConstants.LOGGER.info("Initializing {} (Fabric 1.21.1)", TeyvatBossesConstants.MOD_NAME);
        TeyvatBossesEntities.bind(() -> CRYO_REGISVINE, () -> CRYO_ICICLE, () -> THE_OPEN_EYED, () -> ELEMENTAL_STAR);

        FabricDefaultAttributeRegistry.register(CRYO_REGISVINE, CryoRegisvineEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(THE_OPEN_EYED, TheOpenEyedEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(ELEMENTAL_STAR, ElementalStarEntity.createAttributes());
    }
}

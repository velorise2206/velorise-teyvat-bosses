package com.velorise.teyvatbosses.registry;

import com.velorise.teyvatbosses.entity.CryoIcicleProjectile;
import com.velorise.teyvatbosses.entity.CryoRegisvineEntity;
import com.velorise.teyvatbosses.entity.ElementalStarEntity;
import com.velorise.teyvatbosses.entity.TheOpenEyedEntity;
import net.minecraft.world.entity.EntityType;

import java.util.function.Supplier;

/**
 * Loader-agnostic holder for registered entity types, populated during NeoForge/Fabric init.
 */
public final class TeyvatBossesEntities {
    private static Supplier<EntityType<CryoRegisvineEntity>> cryoRegisvine;
    private static Supplier<EntityType<CryoIcicleProjectile>> cryoIcicle;
    private static Supplier<EntityType<TheOpenEyedEntity>> theOpenEyed;
    private static Supplier<EntityType<ElementalStarEntity>> elementalStar;

    private TeyvatBossesEntities() {}

    public static void bind(
            Supplier<EntityType<CryoRegisvineEntity>> cryoRegisvineSupplier,
            Supplier<EntityType<CryoIcicleProjectile>> cryoIcicleSupplier,
            Supplier<EntityType<TheOpenEyedEntity>> theOpenEyedSupplier,
            Supplier<EntityType<ElementalStarEntity>> elementalStarSupplier
    ) {
        cryoRegisvine = cryoRegisvineSupplier;
        cryoIcicle = cryoIcicleSupplier;
        theOpenEyed = theOpenEyedSupplier;
        elementalStar = elementalStarSupplier;
    }

    public static EntityType<CryoRegisvineEntity> cryoRegisvine() { return cryoRegisvine.get(); }
    public static EntityType<CryoIcicleProjectile> cryoIcicle() { return cryoIcicle.get(); }
    public static EntityType<TheOpenEyedEntity> theOpenEyed() { return theOpenEyed.get(); }
    public static EntityType<ElementalStarEntity> elementalStar() { return elementalStar.get(); }
}

package com.velorise.teyvatbosses.registry;

import com.velorise.teyvatbosses.entity.BossRespawnAnchorEntity;
import com.velorise.teyvatbosses.entity.CryoIceSpikeEntity;
import com.velorise.teyvatbosses.entity.CryoIcicleProjectile;
import com.velorise.teyvatbosses.entity.CryoRegisvineEntity;
import com.velorise.teyvatbosses.entity.ElementalStarEntity;
import com.velorise.teyvatbosses.entity.SkySpearEntity;
import com.velorise.teyvatbosses.entity.TheOpenEyedEntity;
import net.minecraft.world.entity.EntityType;

import java.util.function.Supplier;

/**
 * Loader-agnostic holder for registered entity types, populated during NeoForge/Fabric init.
 */
public final class TeyvatBossesEntities {
    private static Supplier<EntityType<CryoRegisvineEntity>> cryoRegisvine;
    private static Supplier<EntityType<CryoIcicleProjectile>> cryoIcicle;
    private static Supplier<EntityType<CryoIceSpikeEntity>> cryoIceSpike;
    private static Supplier<EntityType<TheOpenEyedEntity>> theOpenEyed;
    private static Supplier<EntityType<ElementalStarEntity>> elementalStar;
    private static Supplier<EntityType<SkySpearEntity>> skySpear;
    private static Supplier<EntityType<BossRespawnAnchorEntity>> bossRespawnAnchor;

    private TeyvatBossesEntities() {}

    public static void bind(
            Supplier<EntityType<CryoRegisvineEntity>> cryoRegisvineSupplier,
            Supplier<EntityType<CryoIcicleProjectile>> cryoIcicleSupplier,
            Supplier<EntityType<CryoIceSpikeEntity>> cryoIceSpikeSupplier,
            Supplier<EntityType<TheOpenEyedEntity>> theOpenEyedSupplier,
            Supplier<EntityType<ElementalStarEntity>> elementalStarSupplier,
            Supplier<EntityType<SkySpearEntity>> skySpearSupplier,
            Supplier<EntityType<BossRespawnAnchorEntity>> bossRespawnAnchorSupplier
    ) {
        cryoRegisvine = cryoRegisvineSupplier;
        cryoIcicle = cryoIcicleSupplier;
        cryoIceSpike = cryoIceSpikeSupplier;
        theOpenEyed = theOpenEyedSupplier;
        elementalStar = elementalStarSupplier;
        skySpear = skySpearSupplier;
        bossRespawnAnchor = bossRespawnAnchorSupplier;
    }

    public static EntityType<CryoRegisvineEntity> cryoRegisvine() { return cryoRegisvine.get(); }
    public static EntityType<CryoIcicleProjectile> cryoIcicle() { return cryoIcicle.get(); }
    public static EntityType<CryoIceSpikeEntity> cryoIceSpike() { return cryoIceSpike.get(); }
    public static EntityType<TheOpenEyedEntity> theOpenEyed() { return theOpenEyed.get(); }
    public static EntityType<ElementalStarEntity> elementalStar() { return elementalStar.get(); }
    public static EntityType<SkySpearEntity> skySpear() { return skySpear.get(); }
    public static EntityType<BossRespawnAnchorEntity> bossRespawnAnchor() { return bossRespawnAnchor.get(); }
}

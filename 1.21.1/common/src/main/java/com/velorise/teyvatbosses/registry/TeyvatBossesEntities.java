package com.velorise.teyvatbosses.registry;

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
import com.velorise.teyvatbosses.entity.CryoTurretEntity;
import com.velorise.teyvatbosses.entity.RuinMechEntity;
import com.velorise.teyvatbosses.entity.RuinMissileProjectile;
import com.velorise.teyvatbosses.entity.RuinNautilusEntity;
import com.velorise.teyvatbosses.entity.RuinSubmersibleEntity;
import com.velorise.teyvatbosses.entity.RuinTorpedoProjectile;
import com.velorise.teyvatbosses.entity.SkySpearEntity;
import com.velorise.teyvatbosses.entity.TheOpenEyedEntity;
import com.velorise.teyvatbosses.entity.TrounceBlossomEntity;
import com.velorise.teyvatbosses.entity.WindCurrentEntity;
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

    private static Supplier<EntityType<AnemoHypostasisEntity>> anemoHypostasis;
    private static Supplier<EntityType<AnemoWindOrbEntity>> anemoWindOrb;
    private static Supplier<EntityType<AnemoTornadoEntity>> anemoTornado;
    private static Supplier<EntityType<AnemoMissileProjectile>> anemoMissile;
    private static Supplier<EntityType<WindCurrentEntity>> windCurrent;
    private static Supplier<EntityType<TrounceBlossomEntity>> trounceBlossom;

    private static Supplier<EntityType<RuinGuardEntity>> ruinGuard;
    private static Supplier<EntityType<RuinMissileProjectile>> ruinMissile;
    private static Supplier<EntityType<AstralionEntity>> astralion;
    private static Supplier<EntityType<RuinMechEntity>> ruinMech;
    private static Supplier<EntityType<CryoTurretEntity>> cryoTurret;
    private static Supplier<EntityType<RuinNautilusEntity>> ruinNautilus;
    private static Supplier<EntityType<RuinTorpedoProjectile>> ruinTorpedo;
    private static Supplier<EntityType<RuinSubmersibleEntity>> ruinSubmersible;

    private TeyvatBossesEntities() {}

    public static void bind(
            Supplier<EntityType<CryoRegisvineEntity>> cryoRegisvineSupplier,
            Supplier<EntityType<CryoIcicleProjectile>> cryoIcicleSupplier,
            Supplier<EntityType<CryoIceSpikeEntity>> cryoIceSpikeSupplier,
            Supplier<EntityType<TheOpenEyedEntity>> theOpenEyedSupplier,
            Supplier<EntityType<ElementalStarEntity>> elementalStarSupplier,
            Supplier<EntityType<SkySpearEntity>> skySpearSupplier,
            Supplier<EntityType<BossRespawnAnchorEntity>> bossRespawnAnchorSupplier,
            Supplier<EntityType<AnemoHypostasisEntity>> anemoHypostasisSupplier,
            Supplier<EntityType<AnemoWindOrbEntity>> anemoWindOrbSupplier,
            Supplier<EntityType<AnemoTornadoEntity>> anemoTornadoSupplier,
            Supplier<EntityType<AnemoMissileProjectile>> anemoMissileSupplier,
            Supplier<EntityType<WindCurrentEntity>> windCurrentSupplier,
            Supplier<EntityType<TrounceBlossomEntity>> trounceBlossomSupplier,
            Supplier<EntityType<RuinGuardEntity>> ruinGuardSupplier,
            Supplier<EntityType<RuinMissileProjectile>> ruinMissileSupplier,
            Supplier<EntityType<AstralionEntity>> astralionSupplier,
            Supplier<EntityType<RuinMechEntity>> ruinMechSupplier,
            Supplier<EntityType<CryoTurretEntity>> cryoTurretSupplier,
            Supplier<EntityType<RuinNautilusEntity>> ruinNautilusSupplier,
            Supplier<EntityType<RuinTorpedoProjectile>> ruinTorpedoSupplier,
            Supplier<EntityType<RuinSubmersibleEntity>> ruinSubmersibleSupplier
    ) {
        cryoRegisvine = cryoRegisvineSupplier;
        cryoIcicle = cryoIcicleSupplier;
        cryoIceSpike = cryoIceSpikeSupplier;
        theOpenEyed = theOpenEyedSupplier;
        elementalStar = elementalStarSupplier;
        skySpear = skySpearSupplier;
        bossRespawnAnchor = bossRespawnAnchorSupplier;
        anemoHypostasis = anemoHypostasisSupplier;
        anemoWindOrb = anemoWindOrbSupplier;
        anemoTornado = anemoTornadoSupplier;
        anemoMissile = anemoMissileSupplier;
        windCurrent = windCurrentSupplier;
        trounceBlossom = trounceBlossomSupplier;
        ruinGuard = ruinGuardSupplier;
        ruinMissile = ruinMissileSupplier;
        astralion = astralionSupplier;
        ruinMech = ruinMechSupplier;
        cryoTurret = cryoTurretSupplier;
        ruinNautilus = ruinNautilusSupplier;
        ruinTorpedo = ruinTorpedoSupplier;
        ruinSubmersible = ruinSubmersibleSupplier;
    }

    public static EntityType<CryoRegisvineEntity> cryoRegisvine() { return cryoRegisvine.get(); }
    public static EntityType<CryoIcicleProjectile> cryoIcicle() { return cryoIcicle.get(); }
    public static EntityType<CryoIceSpikeEntity> cryoIceSpike() { return cryoIceSpike.get(); }
    public static EntityType<TheOpenEyedEntity> theOpenEyed() { return theOpenEyed.get(); }
    public static EntityType<ElementalStarEntity> elementalStar() { return elementalStar.get(); }
    public static EntityType<SkySpearEntity> skySpear() { return skySpear.get(); }
    public static EntityType<BossRespawnAnchorEntity> bossRespawnAnchor() { return bossRespawnAnchor.get(); }
    public static EntityType<AnemoHypostasisEntity> anemoHypostasis() { return anemoHypostasis.get(); }
    public static EntityType<AnemoWindOrbEntity> anemoWindOrb() { return anemoWindOrb.get(); }
    public static EntityType<AnemoTornadoEntity> anemoTornado() { return anemoTornado.get(); }
    public static EntityType<AnemoMissileProjectile> anemoMissile() { return anemoMissile.get(); }
    public static EntityType<WindCurrentEntity> windCurrent() { return windCurrent.get(); }
    public static EntityType<TrounceBlossomEntity> trounceBlossom() { return trounceBlossom.get(); }
    public static EntityType<RuinGuardEntity> ruinGuard() { return ruinGuard.get(); }
    public static EntityType<RuinMissileProjectile> ruinMissile() { return ruinMissile.get(); }
    public static EntityType<AstralionEntity> astralion() { return astralion.get(); }
    public static EntityType<RuinMechEntity> ruinMech() { return ruinMech.get(); }
    public static EntityType<CryoTurretEntity> cryoTurret() { return cryoTurret.get(); }
    public static EntityType<RuinNautilusEntity> ruinNautilus() { return ruinNautilus.get(); }
    public static EntityType<RuinTorpedoProjectile> ruinTorpedo() { return ruinTorpedo.get(); }
    public static EntityType<RuinSubmersibleEntity> ruinSubmersible() { return ruinSubmersible.get(); }
}

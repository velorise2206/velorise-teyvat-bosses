package com.velorise.teyvatbosses.entity;

import com.velorise.teyvatbosses.compat.StavesElementBridge;
import com.velorise.teyvatbosses.registry.TeyvatBossesEntities;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Summoned by The Open-Eyed during Phenomena's Delirium.
 *
 * <p>Official Genshin mechanics:
 * <ul>
 *   <li>Inherits 1/6 of The Open-Eyed's Max HP.</li>
 *   <li><b>Active Elemental Star</b> (Pyro/Hydro/Electro/Cryo): immutable innate aura of its element
 *       and immune to damage of its own element; easily shattered via opposing elemental reactions.</li>
 *   <li><b>Inert Elemental Star</b> (Geo, when no element was recorded): +70% extra All RES
 *       (80% total RES, takes 20% damage).</li>
 *   <li>When destroyed, notifies owner {@link TheOpenEyedEntity} to deal True DMG + True CRIT DMG
 *       (3% / 9% Max HP per instance for 1st / 2nd Star destroyed).</li>
 * </ul>
 */
public class ElementalStarEntity extends Monster {

    public static final int ELEM_INERT_GEO = 0;
    public static final int ELEM_PYRO      = 1;
    public static final int ELEM_HYDRO     = 2;
    public static final int ELEM_ELECTRO   = 3;
    public static final int ELEM_CRYO      = 4;

    private static final EntityDataAccessor<Integer> DATA_ELEMENT =
            SynchedEntityData.defineId(ElementalStarEntity.class, EntityDataSerializers.INT);

    @Nullable
    private UUID ownerUuid;
    private Vec3 hoverPos = Vec3.ZERO;
    private boolean hoverInit = false;
    private int lifeTicks = 0;

    public ElementalStarEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.setNoGravity(true);
        this.addTag("VeloriseInnateChecked");
        this.xpReward = 0;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 80.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.ARMOR, 0.0D);
    }

    public static ElementalStarEntity spawnForBoss(
            ServerLevel level,
            TheOpenEyedEntity owner,
            Vec3 pos,
            int elementId,
            float maxHp
    ) {
        ElementalStarEntity star = new ElementalStarEntity(TeyvatBossesEntities.elementalStar(), level);
        star.ownerUuid = owner.getUUID();
        star.hoverPos = pos;
        star.hoverInit = true;
        star.setPos(pos.x, pos.y, pos.z);
        star.entityData.set(DATA_ELEMENT, elementId);
        if (star.getAttribute(Attributes.MAX_HEALTH) != null) {
            star.getAttribute(Attributes.MAX_HEALTH).setBaseValue(Math.max(20.0D, maxHp));
        }
        star.setHealth(star.getMaxHealth());

        // Official mechanic: Active Elemental Star has an immutable innate aura of its element
        String elemName = elementName(elementId);
        if (elementId != ELEM_INERT_GEO) {
            StavesElementBridge.setInnateElement(star, elemName);
        }
        level.addFreshEntity(star);
        return star;
    }

    public static String elementName(int id) {
        return switch (id) {
            case ELEM_PYRO -> "PYRO";
            case ELEM_HYDRO -> "HYDRO";
            case ELEM_ELECTRO -> "ELECTRO";
            case ELEM_CRYO -> "CRYO";
            default -> "GEO";
        };
    }

    public int getElementId() { return entityData.get(DATA_ELEMENT); }
    public boolean isInert() { return getElementId() == ELEM_INERT_GEO; }
    public @Nullable UUID getOwnerUuid() { return ownerUuid; }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_ELEMENT, ELEM_INERT_GEO);
    }

    @Override
    protected void registerGoals() {}

    @Override
    public boolean isPushable() { return false; }

    @Override
    protected void doPush(Entity entity) {}

    @Override
    public void knockback(double strength, double x, double z) {}

    @Override
    public void tick() {
        super.tick();
        lifeTicks++;
        if (!hoverInit) {
            hoverPos = position();
            hoverInit = true;
        }
        double bob = Math.sin(lifeTicks * 0.10D) * 0.10D;
        setDeltaMovement(Vec3.ZERO);
        setPos(hoverPos.x, hoverPos.y + bob, hoverPos.z);

        if (!level().isClientSide() && level() instanceof ServerLevel sl) {
            if (lifeTicks % 5 == 0) {
                sl.sendParticles(particleForElement(getElementId()), getX(), getY() + 0.8D, getZ(), 4, 0.45D, 0.45D, 0.45D, 0.02D);
            }
            if (ownerUuid != null) {
                Entity e = sl.getEntity(ownerUuid);
                if (!(e instanceof TheOpenEyedEntity boss) || !boss.isAlive()) {
                    discard();
                }
            }
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (isInert()) {
            // Official Inert Elemental Star: 80% total RES (10% base + 70% bonus) -> takes 20% damage
            amount *= 0.45F;
        } else {
            // Active Elemental Star: 10% base RES; vanilla fire immunity for Pyro star, freeze immunity for Cryo star
            if (getElementId() == ELEM_PYRO && source.is(net.minecraft.tags.DamageTypeTags.IS_FIRE)) return false;
            if (getElementId() == ELEM_CRYO && source.is(net.minecraft.tags.DamageTypeTags.IS_FREEZING)) return false;
            if (getElementId() == ELEM_ELECTRO && source.is(net.minecraft.tags.DamageTypeTags.IS_LIGHTNING)) return false;
            amount *= 0.90F;
        }
        return super.hurt(source, amount);
    }

    @Override
    public void die(DamageSource cause) {
        if (!level().isClientSide() && level() instanceof ServerLevel sl) {
            sl.playSound(null, getX(), getY(), getZ(), SoundEvents.GLASS_BREAK, SoundSource.HOSTILE, 1.5F, 0.9F);
            sl.sendParticles(particleForElement(getElementId()), getX(), getY() + 0.8D, getZ(), 32, 0.7D, 0.7D, 0.7D, 0.12D);
            if (ownerUuid != null && sl.getEntity(ownerUuid) instanceof TheOpenEyedEntity boss && boss.isAlive()) {
                boss.onElementalStarDestroyed(sl, this);
            }
        }
        super.die(cause);
    }

    public static ParticleOptions particleForElement(int elementId) {
        return switch (elementId) {
            case ELEM_PYRO -> ParticleTypes.FLAME;
            case ELEM_HYDRO -> ParticleTypes.SPLASH;
            case ELEM_ELECTRO -> ParticleTypes.ELECTRIC_SPARK;
            case ELEM_CRYO -> ParticleTypes.SNOWFLAKE;
            default -> ParticleTypes.END_ROD;
        };
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("ElementId", getElementId());
        if (ownerUuid != null) tag.putUUID("OwnerUUID", ownerUuid);
        tag.putDouble("HoverX", hoverPos.x);
        tag.putDouble("HoverY", hoverPos.y);
        tag.putDouble("HoverZ", hoverPos.z);
        tag.putBoolean("HoverInit", hoverInit);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        entityData.set(DATA_ELEMENT, tag.getInt("ElementId"));
        if (tag.hasUUID("OwnerUUID")) ownerUuid = tag.getUUID("OwnerUUID");
        if (tag.getBoolean("HoverInit")) {
            hoverPos = new Vec3(tag.getDouble("HoverX"), tag.getDouble("HoverY"), tag.getDouble("HoverZ"));
            hoverInit = true;
        }
    }
}

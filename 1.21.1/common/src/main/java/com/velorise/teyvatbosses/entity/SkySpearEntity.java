package com.velorise.teyvatbosses.entity;

import com.velorise.teyvatbosses.compat.StavesElementBridge;
import com.velorise.teyvatbosses.registry.TeyvatBossesEntities;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Giant Celestial Sky-Spear summoned by The Open-Eyed during Phenomena's Delirium judgement (eye.mp4 01:26-01:30).
 *
 * <p>Lifecycle:
 * <ul>
 *   <li>Ticks 0..12: Rotating crimson-gold 4-point celestial star sigil telegraph on the ground.</li>
 *   <li>Ticks 12..16: Towering 9-block 3D Celestial Spear plunges vertically from the sky.</li>
 *   <li>Tick 16: Massive elemental AoE impact (100% ATK + 15% Max HP bonus + 35% pierce) & expanding shockwave ring.</li>
 *   <li>Ticks 16..36: Spear remains embedded glowing in the marble floor before dissolving in light sparks.</li>
 * </ul>
 */
public class SkySpearEntity extends Entity {

    public static final int TELEGRAPH_TICKS = 12;
    public static final int IMPACT_TICK = 16;
    public static final int LIFETIME_TICKS = 38;

    private static final EntityDataAccessor<Integer> DATA_ELEMENT =
            SynchedEntityData.defineId(SkySpearEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DATA_RADIUS =
            SynchedEntityData.defineId(SkySpearEntity.class, EntityDataSerializers.FLOAT);

    @Nullable
    private UUID ownerUuid;
    private float damage = 34.0F;
    private boolean impacted = false;

    public SkySpearEntity(EntityType<? extends SkySpearEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public static SkySpearEntity spawn(ServerLevel level, LivingEntity owner, double x, double y, double z, int elementId, float damage, float radius) {
        SkySpearEntity spear = new SkySpearEntity(TeyvatBossesEntities.skySpear(), level);
        spear.setPos(x, y, z);
        spear.ownerUuid = owner.getUUID();
        spear.damage = damage;
        spear.entityData.set(DATA_ELEMENT, elementId);
        spear.entityData.set(DATA_RADIUS, radius);
        level.addFreshEntity(spear);
        level.playSound(null, x, y, z, SoundEvents.BEACON_ACTIVATE, SoundSource.HOSTILE, 1.15F, 1.35F);
        return spear;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_ELEMENT, ElementalStarEntity.ELEM_INERT_GEO);
        builder.define(DATA_RADIUS, 5.4F);
    }

    public int getElementId() { return entityData.get(DATA_ELEMENT); }
    public float getBlastRadius() { return entityData.get(DATA_RADIUS); }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide()) {
            if (tickCount < IMPACT_TICK && tickCount % 2 == 0) {
                double ang = tickCount * 0.55D;
                double r = getBlastRadius() * 0.85D;
                level().addParticle(ElementalStarEntity.particleForElement(getElementId()),
                        getX() + Math.cos(ang) * r, getY() + 0.15D, getZ() + Math.sin(ang) * r,
                        0.0D, 0.08D, 0.0D);
            } else if (tickCount == IMPACT_TICK) {
                for (int i = 0; i < 24; i++) {
                    double a = (Math.PI * 2.0D * i) / 24.0D;
                    level().addParticle(ParticleTypes.END_ROD, getX(), getY() + 0.5D, getZ(),
                            Math.cos(a) * 0.42D, 0.18D, Math.sin(a) * 0.42D);
                }
            }
            return;
        }

        ServerLevel sl = (ServerLevel) level();
        if (!impacted && tickCount >= IMPACT_TICK) {
            impacted = true;
            triggerImpact(sl);
        }
        if (tickCount >= LIFETIME_TICKS) {
            sl.sendParticles(ParticleTypes.END_ROD, getX(), getY() + 2.5D, getZ(), 20, 0.5D, 2.5D, 0.5D, 0.06D);
            discard();
        }
    }

    private void triggerImpact(ServerLevel sl) {
        float r = getBlastRadius();
        int elemId = getElementId();
        String elem = ElementalStarEntity.elementName(elemId);

        sl.playSound(null, getX(), getY(), getZ(), SoundEvents.TRIDENT_THUNDER.value(), SoundSource.HOSTILE, 1.65F, 0.82F);
        sl.playSound(null, getX(), getY(), getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.HOSTILE, 1.4F, 0.72F);
        sl.sendParticles(ElementalStarEntity.particleForElement(elemId), getX(), getY() + 1.8D, getZ(), 54, r * 0.45D, 2.6D, r * 0.45D, 0.15D);
        sl.sendParticles(ParticleTypes.EXPLOSION, getX(), getY() + 0.6D, getZ(), 5, 1.2D, 0.4D, 1.2D, 0.02D);

        LivingEntity owner = null;
        if (ownerUuid != null && sl.getEntity(ownerUuid) instanceof LivingEntity le) {
            owner = le;
        }

        AABB box = new AABB(getX() - r, getY() - 1.5D, getZ() - r, getX() + r, getY() + 6.0D, getZ() + r);
        for (LivingEntity e : sl.getEntitiesOfClass(LivingEntity.class, box,
                le -> le.isAlive() && !(le instanceof TheOpenEyedEntity) && !(le instanceof ElementalStarEntity))) {
            double dx = e.getX() - getX();
            double dz = e.getZ() - getZ();
            if (dx * dx + dz * dz <= r * r) {
                float totalDmg = damage + e.getMaxHealth() * 0.15F;
                StavesElementBridge.dealElementalDamage(e, owner, elem, totalDmg, 1.0F, false, 0.25F);
                Vec3 push = new Vec3(dx, 0.0D, dz);
                if (push.lengthSqr() > 1e-4D) {
                    push = push.normalize().scale(1.05D);
                    e.push(push.x, 0.48D, push.z);
                }
            }
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.contains("Element")) entityData.set(DATA_ELEMENT, tag.getInt("Element"));
        if (tag.contains("Radius")) entityData.set(DATA_RADIUS, tag.getFloat("Radius"));
        if (tag.contains("Damage")) damage = tag.getFloat("Damage");
        if (tag.hasUUID("OwnerUUID")) ownerUuid = tag.getUUID("OwnerUUID");
        impacted = tag.getBoolean("Impacted");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("Element", getElementId());
        tag.putFloat("Radius", getBlastRadius());
        tag.putFloat("Damage", damage);
        if (ownerUuid != null) tag.putUUID("OwnerUUID", ownerUuid);
        tag.putBoolean("Impacted", impacted);
    }
}

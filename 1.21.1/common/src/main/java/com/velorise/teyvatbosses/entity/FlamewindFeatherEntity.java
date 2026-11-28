package com.velorise.teyvatbosses.entity;

import com.velorise.teyvatbosses.registry.TeyvatBossesEntities;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

/**
 * Flamewind Feather planted by {@link GoldflameQucusaurEntity} during its Golden Flame aerial phase.
 * Destroying it (via Hydro/Cryo/Electro/Anemo/Geo or 3 weapon hits) weakens the Boss's Dive Bomb
 * from 100% to 65% ATK and drains a large chunk of the Boss's Golden Flame Pyro Shield.
 */
public class FlamewindFeatherEntity extends Entity {
    private static final EntityDataAccessor<Float> DATA_SHIELD =
            SynchedEntityData.defineId(FlamewindFeatherEntity.class, EntityDataSerializers.FLOAT);

    private static final float MAX_SHIELD = 24.0F;
    private UUID bossUuid;
    private int lifeTicks = 0;

    public FlamewindFeatherEntity(EntityType<? extends FlamewindFeatherEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public static FlamewindFeatherEntity spawn(ServerLevel level, GoldflameQucusaurEntity boss, Vec3 pos) {
        FlamewindFeatherEntity f = new FlamewindFeatherEntity(TeyvatBossesEntities.flamewindFeather(), level);
        f.setPos(pos.x, pos.y, pos.z);
        f.bossUuid = boss.getUUID();
        f.entityData.set(DATA_SHIELD, MAX_SHIELD);
        level.addFreshEntity(f);
        level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.BLAZE_SHOOT, SoundSource.HOSTILE, 1.1F, 1.35F);
        return f;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_SHIELD, MAX_SHIELD);
    }

    public float getShieldProgress() {
        return Math.max(0.0F, Math.min(1.0F, entityData.get(DATA_SHIELD) / MAX_SHIELD));
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (level().isClientSide() || !isAlive()) return false;
        if (source.is(DamageTypeTags.IS_FIRE)) return false;
        float mult = source.is(DamageTypeTags.IS_FREEZING) || source.is(DamageTypeTags.IS_DROWNING) ? 2.5F : 1.0F;
        float next = entityData.get(DATA_SHIELD) - Math.max(6.0F, amount * mult);
        entityData.set(DATA_SHIELD, Math.max(0.0F, next));
        ServerLevel sl = (ServerLevel) level();
        sl.playSound(null, getX(), getY() + 0.8D, getZ(), SoundEvents.GLASS_BREAK, SoundSource.HOSTILE, 0.9F, 1.35F);
        sl.sendParticles(ParticleTypes.FLAME, getX(), getY() + 0.9D, getZ(), 10, 0.35D, 0.55D, 0.35D, 0.05D);
        if (next <= 0.0F) {
            if (bossUuid != null && sl.getEntity(bossUuid) instanceof GoldflameQucusaurEntity boss) {
                boss.onFlamewindFeatherDestroyed(position());
            }
            sl.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, getX(), getY() + 1.0D, getZ(), 24, 0.5D, 0.8D, 0.5D, 0.08D);
            discard();
        }
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        lifeTicks++;
        if (level().isClientSide()) {
            if (tickCount % 2 == 0) {
                level().addParticle(ParticleTypes.FLAME, getX(), getY() + 0.3D + (tickCount % 10) * 0.14D, getZ(),
                        0.0D, 0.08D, 0.0D);
            }
            return;
        }
        if (lifeTicks > 320) {
            discard();
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.hasUUID("BossUuid")) bossUuid = tag.getUUID("BossUuid");
        if (tag.contains("Shield")) entityData.set(DATA_SHIELD, tag.getFloat("Shield"));
        lifeTicks = tag.getInt("LifeTicks");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        if (bossUuid != null) tag.putUUID("BossUuid", bossUuid);
        tag.putFloat("Shield", entityData.get(DATA_SHIELD));
        tag.putInt("LifeTicks", lifeTicks);
    }
}

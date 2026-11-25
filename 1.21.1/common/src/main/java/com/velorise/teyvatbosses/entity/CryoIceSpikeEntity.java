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
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * 3D Erupting Cryo Ice Crystal Pillar / Slam Spike summoned by Cryo Regisvine.
 *
 * <ul>
 *   <li>{@link #MODE_PILLAR}: 14-tick rotating ground frost sigil telegraph -> rapid 3-tick crystal eruption ->
 *       18-tick hold -> crystalline shatter (matches Genshin Cryo Regisvine's Frost Field pillar barrage).</li>
 *   <li>{@link #MODE_SLAM_SPIKE}: Instant 2-tick jagged ice spike cluster erupting around head-slam impacts.</li>
 * </ul>
 */
public class CryoIceSpikeEntity extends Entity {

    public static final int MODE_PILLAR = 0;
    public static final int MODE_SLAM_SPIKE = 1;

    public static final int PILLAR_TELEGRAPH_TICKS = 14;
    public static final int PILLAR_LIFETIME_TICKS = 38;
    public static final int SLAM_LIFETIME_TICKS = 24;

    private static final EntityDataAccessor<Integer> DATA_MODE =
            SynchedEntityData.defineId(CryoIceSpikeEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DATA_SCALE =
            SynchedEntityData.defineId(CryoIceSpikeEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_SPIN_YAW =
            SynchedEntityData.defineId(CryoIceSpikeEntity.class, EntityDataSerializers.FLOAT);

    @Nullable
    private UUID ownerUuid;
    private float damage = 15.0F;
    private boolean hasErupted = false;

    public CryoIceSpikeEntity(EntityType<? extends CryoIceSpikeEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public static CryoIceSpikeEntity spawnPillar(ServerLevel level, LivingEntity owner, double x, double y, double z, float damage, float scale) {
        CryoIceSpikeEntity spike = new CryoIceSpikeEntity(TeyvatBossesEntities.cryoIceSpike(), level);
        spike.setPos(x, y, z);
        spike.ownerUuid = owner.getUUID();
        spike.damage = damage;
        spike.entityData.set(DATA_MODE, MODE_PILLAR);
        spike.entityData.set(DATA_SCALE, scale);
        spike.entityData.set(DATA_SPIN_YAW, level.random.nextFloat() * 360.0F);
        level.addFreshEntity(spike);
        level.playSound(null, x, y, z, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.HOSTILE, 0.85F, 1.45F);
        return spike;
    }

    public static CryoIceSpikeEntity spawnSlamSpike(ServerLevel level, LivingEntity owner, double x, double y, double z, float damage, float scale, float yawDeg) {
        CryoIceSpikeEntity spike = new CryoIceSpikeEntity(TeyvatBossesEntities.cryoIceSpike(), level);
        spike.setPos(x, y, z);
        spike.ownerUuid = owner.getUUID();
        spike.damage = damage;
        spike.entityData.set(DATA_MODE, MODE_SLAM_SPIKE);
        spike.entityData.set(DATA_SCALE, scale);
        spike.entityData.set(DATA_SPIN_YAW, yawDeg);
        level.addFreshEntity(spike);
        return spike;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_MODE, MODE_PILLAR);
        builder.define(DATA_SCALE, 1.0F);
        builder.define(DATA_SPIN_YAW, 0.0F);
    }

    public int getMode() { return entityData.get(DATA_MODE); }
    public float getSpikeScale() { return entityData.get(DATA_SCALE); }
    public float getSpinYaw() { return entityData.get(DATA_SPIN_YAW); }

    @Override
    public void tick() {
        super.tick();
        int mode = getMode();
        int eruptTick = (mode == MODE_PILLAR) ? PILLAR_TELEGRAPH_TICKS : 2;
        int maxTicks = (mode == MODE_PILLAR) ? PILLAR_LIFETIME_TICKS : SLAM_LIFETIME_TICKS;

        if (level().isClientSide()) {
            if (mode == MODE_PILLAR && tickCount < eruptTick && tickCount % 2 == 0) {
                double ang = (tickCount * 0.65D);
                double rad = 1.45D * getSpikeScale();
                level().addParticle(ParticleTypes.SNOWFLAKE,
                        getX() + Math.cos(ang) * rad, getY() + 0.12D, getZ() + Math.sin(ang) * rad,
                        0.0D, 0.04D, 0.0D);
                level().addParticle(ParticleTypes.SNOWFLAKE,
                        getX() - Math.cos(ang) * rad, getY() + 0.12D, getZ() - Math.sin(ang) * rad,
                        0.0D, 0.04D, 0.0D);
            } else if (tickCount == eruptTick) {
                for (int i = 0; i < 14; i++) {
                    double ox = (random.nextDouble() - 0.5D) * 1.4D;
                    double oz = (random.nextDouble() - 0.5D) * 1.4D;
                    level().addParticle(ParticleTypes.SNOWFLAKE, getX() + ox, getY() + 0.3D, getZ() + oz,
                            ox * 0.15D, 0.18D + random.nextDouble() * 0.14D, oz * 0.15D);
                }
            }
            return;
        }

        ServerLevel sl = (ServerLevel) level();
        if (!hasErupted && tickCount >= eruptTick) {
            hasErupted = true;
            triggerEruption(sl, mode);
        }

        if (tickCount >= maxTicks) {
            sl.sendParticles(ParticleTypes.SNOWFLAKE, getX(), getY() + 1.1D * getSpikeScale(), getZ(),
                    18, 0.65D, 0.9D, 0.65D, 0.08D);
            sl.playSound(null, getX(), getY(), getZ(), SoundEvents.GLASS_BREAK, SoundSource.HOSTILE, 0.55F, 1.45F);
            discard();
        }
    }

    private void triggerEruption(ServerLevel sl, int mode) {
        float sc = getSpikeScale();
        double hitR = (mode == MODE_PILLAR ? 1.85D : 1.45D) * sc;
        double hitH = (mode == MODE_PILLAR ? 3.8D : 2.6D) * sc;

        sl.sendParticles(ParticleTypes.SNOWFLAKE, getX(), getY() + 0.8D, getZ(), 28, hitR * 0.45D, 1.0D, hitR * 0.45D, 0.12D);
        sl.sendParticles(ParticleTypes.ITEM_SNOWBALL, getX(), getY() + 0.4D, getZ(), 16, hitR * 0.4D, 0.5D, hitR * 0.4D, 0.16D);
        sl.playSound(null, getX(), getY(), getZ(), SoundEvents.GLASS_BREAK, SoundSource.HOSTILE,
                mode == MODE_PILLAR ? 1.05F : 0.75F, mode == MODE_PILLAR ? 0.95F : 1.2F);
        sl.playSound(null, getX(), getY(), getZ(), SoundEvents.AMETHYST_CLUSTER_BREAK, SoundSource.HOSTILE,
                0.9F, 0.85F);

        if (damage <= 0.0F) return;

        LivingEntity owner = null;
        if (ownerUuid != null && sl.getEntity(ownerUuid) instanceof LivingEntity le) {
            owner = le;
        }

        AABB box = new AABB(getX() - hitR, getY() - 0.5D, getZ() - hitR, getX() + hitR, getY() + hitH, getZ() + hitR);
        for (LivingEntity e : sl.getEntitiesOfClass(LivingEntity.class, box,
                le -> le.isAlive() && !(le instanceof CryoRegisvineEntity))) {
            double dx = e.getX() - getX();
            double dz = e.getZ() - getZ();
            if (dx * dx + dz * dz <= hitR * hitR) {
                StavesElementBridge.dealElementalDamage(e, owner, "CRYO", damage, 1.0F, false, 0.28F);
                if (mode == MODE_PILLAR) {
                    e.setDeltaMovement(e.getDeltaMovement().x * 0.4D, 0.44D, e.getDeltaMovement().z * 0.4D);
                    e.hurtMarked = true;
                }
            }
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.contains("Mode")) entityData.set(DATA_MODE, tag.getInt("Mode"));
        if (tag.contains("Scale")) entityData.set(DATA_SCALE, tag.getFloat("Scale"));
        if (tag.contains("SpinYaw")) entityData.set(DATA_SPIN_YAW, tag.getFloat("SpinYaw"));
        if (tag.contains("Damage")) damage = tag.getFloat("Damage");
        if (tag.hasUUID("OwnerUUID")) ownerUuid = tag.getUUID("OwnerUUID");
        hasErupted = tag.getBoolean("HasErupted");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("Mode", getMode());
        tag.putFloat("Scale", getSpikeScale());
        tag.putFloat("SpinYaw", getSpinYaw());
        tag.putFloat("Damage", damage);
        if (ownerUuid != null) tag.putUUID("OwnerUUID", ownerUuid);
        tag.putBoolean("HasErupted", hasErupted);
    }
}

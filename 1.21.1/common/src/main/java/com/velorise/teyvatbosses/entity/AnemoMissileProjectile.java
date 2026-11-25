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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.UUID;

/**
 * Glowing Anemo energy projectile fired during Magic Circle Volley (kind=0)
 * and Butterfly Winged Gust Sweep (kind=1, wide crescent wind blade).
 */
public class AnemoMissileProjectile extends Entity {

    public static final int KIND_ORB_BOLT = 0;
    public static final int KIND_WIND_BLADE = 1;

    private static final EntityDataAccessor<Integer> DATA_KIND =
            SynchedEntityData.defineId(AnemoMissileProjectile.class, EntityDataSerializers.INT);

    private UUID ownerUuid;
    private float damage = 11.0F;
    private int maxLife = 85;

    public AnemoMissileProjectile(EntityType<? extends AnemoMissileProjectile> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public static AnemoMissileProjectile launch(
            ServerLevel level,
            LivingEntity owner,
            Vec3 origin,
            Vec3 velocity,
            float damage,
            int kind
    ) {
        AnemoMissileProjectile p = new AnemoMissileProjectile(TeyvatBossesEntities.anemoMissile(), level);
        p.setPos(origin.x, origin.y, origin.z);
        p.setDeltaMovement(velocity);
        p.ownerUuid = owner != null ? owner.getUUID() : null;
        p.damage = damage;
        p.entityData.set(DATA_KIND, kind);
        p.setYRot((float) (Math.atan2(-velocity.x, velocity.z) * (180.0D / Math.PI)));
        level.addFreshEntity(p);
        return p;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_KIND, KIND_ORB_BOLT);
    }

    public int getKind() { return entityData.get(DATA_KIND); }

    @Override
    public void tick() {
        super.tick();
        Vec3 vel = getDeltaMovement();
        setPos(getX() + vel.x, getY() + vel.y, getZ() + vel.z);

        if (level().isClientSide()) {
            level().addParticle(ParticleTypes.CLOUD, getX(), getY(), getZ(), -vel.x * 0.15D, -vel.y * 0.15D, -vel.z * 0.15D);
            if (tickCount % 2 == 0) {
                level().addParticle(ParticleTypes.GLOW, getX(), getY(), getZ(), 0.0D, 0.02D, 0.0D);
            }
            return;
        }

        ServerLevel sl = (ServerLevel) level();
        double hitRadius = (getKind() == KIND_WIND_BLADE) ? 2.15D : 1.05D;
        AABB box = new AABB(getX() - hitRadius, getY() - 1.1D, getZ() - hitRadius,
                getX() + hitRadius, getY() + 1.1D, getZ() + hitRadius);
        List<LivingEntity> hits = sl.getEntitiesOfClass(LivingEntity.class, box,
                e -> e.isAlive() && !(e instanceof AnemoHypostasisEntity)
                        && !(e instanceof Player pl && (pl.isCreative() || pl.isSpectator())));

        boolean impacted = false;
        LivingEntity owner = (ownerUuid != null && sl.getEntity(ownerUuid) instanceof LivingEntity le) ? le : null;
        if (!hits.isEmpty()) {
            for (LivingEntity target : hits) {
                StavesElementBridge.dealElementalDamage(target, owner, "ANEMO", damage, 1.0F, false, 0.30F);
                Vec3 push = vel.normalize().scale(getKind() == KIND_WIND_BLADE ? 0.95D : 0.45D);
                target.setDeltaMovement(target.getDeltaMovement().add(push.x, 0.28D, push.z));
                target.hurtMarked = true;
            }
            impacted = true;
        } else if (!sl.getBlockState(blockPosition()).isAir() && sl.getBlockState(blockPosition()).blocksMotion()) {
            impacted = true;
        }

        if (impacted) {
            sl.playSound(null, getX(), getY(), getZ(), SoundEvents.BREEZE_WIND_CHARGE_BURST.value(), SoundSource.HOSTILE, 0.95F, 1.25F);
            sl.sendParticles(ParticleTypes.GUST_EMITTER_SMALL, getX(), getY(), getZ(), 1, 0.1D, 0.1D, 0.1D, 0.0D);
            sl.sendParticles(ParticleTypes.END_ROD, getX(), getY(), getZ(), 14, 0.45D, 0.45D, 0.45D, 0.08D);
            discard();
            return;
        }

        if (tickCount >= maxLife) {
            discard();
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.contains("Kind")) entityData.set(DATA_KIND, tag.getInt("Kind"));
        if (tag.contains("Damage")) damage = tag.getFloat("Damage");
        if (tag.hasUUID("Owner")) ownerUuid = tag.getUUID("Owner");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("Kind", getKind());
        tag.putFloat("Damage", damage);
        if (ownerUuid != null) tag.putUUID("Owner", ownerUuid);
    }
}

package com.velorise.teyvatbosses.entity;

import com.velorise.teyvatbosses.compat.StavesElementBridge;
import com.velorise.teyvatbosses.registry.TeyvatBossesEntities;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Homing Cryo icicle launched by Cryo Regisvine's corolla lashes.
 */
public class CryoIcicleProjectile extends Projectile {
    private static final int MAX_AGE_TICKS = 90;
    @Nullable
    private UUID targetUuid;
    private float damage = 7.0F;
    private int ageTicks = 0;

    public CryoIcicleProjectile(EntityType<? extends CryoIcicleProjectile> type, Level level) {
        super(type, level);
        this.noPhysics = false;
    }

    public static CryoIcicleProjectile spawn(
            ServerLevel level,
            LivingEntity owner,
            @Nullable LivingEntity target,
            Vec3 origin,
            Vec3 initialVel,
            float damage
    ) {
        CryoIcicleProjectile proj = new CryoIcicleProjectile(TeyvatBossesEntities.cryoIcicle(), level);
        proj.setOwner(owner);
        proj.setPos(origin.x, origin.y, origin.z);
        proj.setDeltaMovement(initialVel);
        proj.damage = damage;
        if (target != null) proj.targetUuid = target.getUUID();
        level.addFreshEntity(proj);
        return proj;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {}

    @Override
    public void tick() {
        super.tick();
        ageTicks++;
        if (ageTicks > MAX_AGE_TICKS) {
            discard();
            return;
        }

        Vec3 vel = getDeltaMovement();
        if (!level().isClientSide() && level() instanceof ServerLevel sl && targetUuid != null && ageTicks <= 42) {
            Entity target = sl.getEntity(targetUuid);
            if (target instanceof LivingEntity living && living.isAlive()) {
                Vec3 desired = living.position().add(0.0D, living.getBbHeight() * 0.55D, 0.0D)
                        .subtract(position()).normalize().scale(0.68D);
                vel = vel.lerp(desired, 0.14D);
                setDeltaMovement(vel);
            }
        }

        HitResult hit = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        if (hit.getType() != HitResult.Type.MISS) {
            onHit(hit);
        }

        Vec3 next = position().add(vel);
        setPos(next.x, next.y, next.z);
        updateRotation();

        if (level().isClientSide()) {
            level().addParticle(ParticleTypes.SNOWFLAKE, getX(), getY(), getZ(), 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        if (!super.canHitEntity(entity)) return false;
        return entity != getOwner() && !(entity instanceof CryoRegisvineEntity);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (!level().isClientSide() && result.getEntity() instanceof LivingEntity living) {
            LivingEntity owner = getOwner() instanceof LivingEntity le ? le : null;
            StavesElementBridge.dealElementalDamage(living, owner, "CRYO", damage, 1.0F, false);
            if (level() instanceof ServerLevel sl) {
                sl.sendParticles(ParticleTypes.SNOWFLAKE, getX(), getY(), getZ(), 10, 0.25D, 0.25D, 0.25D, 0.06D);
                sl.playSound(null, getX(), getY(), getZ(), SoundEvents.GLASS_BREAK, SoundSource.HOSTILE, 0.65F, 1.55F);
            }
        }
        discard();
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (!level().isClientSide() && level() instanceof ServerLevel sl) {
            sl.sendParticles(ParticleTypes.SNOWFLAKE, getX(), getY(), getZ(), 8, 0.2D, 0.2D, 0.2D, 0.05D);
            sl.playSound(null, getX(), getY(), getZ(), SoundEvents.GLASS_BREAK, SoundSource.HOSTILE, 0.5F, 1.6F);
        }
        discard();
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putFloat("Damage", damage);
        tag.putInt("AgeTicks", ageTicks);
        if (targetUuid != null) tag.putUUID("TargetUUID", targetUuid);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Damage")) damage = tag.getFloat("Damage");
        if (tag.contains("AgeTicks")) ageTicks = tag.getInt("AgeTicks");
        if (tag.hasUUID("TargetUUID")) targetUuid = tag.getUUID("TargetUUID");
    }
}

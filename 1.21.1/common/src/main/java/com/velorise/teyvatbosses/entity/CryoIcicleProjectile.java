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
 * Curving homing Cryo Crystal Dart launched in alternating volleys by Cryo Regisvine's floating stamen pods.
 * Launches outward in a dramatic arc for 4 ticks before smoothly curving onto the target like Genshin Impact.
 */
public class CryoIcicleProjectile extends Projectile {
    private static final int MAX_AGE_TICKS = 96;
    private static final int ARC_DELAY_TICKS = 4;
    @Nullable
    private UUID targetUuid;
    private float damage = 12.0F;
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
        if (!level().isClientSide() && level() instanceof ServerLevel sl && targetUuid != null
                && ageTicks >= ARC_DELAY_TICKS && ageTicks <= 48) {
            Entity target = sl.getEntity(targetUuid);
            if (target instanceof LivingEntity living && living.isAlive()) {
                double speed = Math.min(0.92D, 0.56D + (ageTicks - ARC_DELAY_TICKS) * 0.015D);
                Vec3 desired = living.position().add(0.0D, living.getBbHeight() * 0.55D, 0.0D)
                        .subtract(position()).normalize().scale(speed);
                vel = vel.lerp(desired, 0.16D);
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
            level().addParticle(ParticleTypes.SNOWFLAKE, getX(), getY(), getZ(),
                    -vel.x * 0.08D, -vel.y * 0.08D, -vel.z * 0.08D);
            if (tickCount % 2 == 0) {
                level().addParticle(ParticleTypes.END_ROD, getX(), getY(), getZ(), 0.0D, 0.0D, 0.0D);
            }
        }
    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        if (!super.canHitEntity(entity)) return false;
        return entity != getOwner()
                && !(entity instanceof CryoRegisvineEntity)
                && !(entity instanceof CryoIceSpikeEntity);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (!level().isClientSide() && result.getEntity() instanceof LivingEntity living) {
            LivingEntity owner = getOwner() instanceof LivingEntity le ? le : null;
            StavesElementBridge.dealElementalDamage(living, owner, "CRYO", damage, 1.0F, false, 0.25F);
            if (level() instanceof ServerLevel sl) {
                sl.sendParticles(ParticleTypes.SNOWFLAKE, getX(), getY(), getZ(), 16, 0.32D, 0.32D, 0.32D, 0.08D);
                sl.sendParticles(ParticleTypes.ITEM_SNOWBALL, getX(), getY(), getZ(), 8, 0.25D, 0.25D, 0.25D, 0.1D);
                sl.playSound(null, getX(), getY(), getZ(), SoundEvents.GLASS_BREAK, SoundSource.HOSTILE, 0.72F, 1.55F);
            }
        }
        discard();
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (!level().isClientSide() && level() instanceof ServerLevel sl) {
            sl.sendParticles(ParticleTypes.SNOWFLAKE, getX(), getY(), getZ(), 12, 0.28D, 0.28D, 0.28D, 0.07D);
            sl.sendParticles(ParticleTypes.ITEM_SNOWBALL, getX(), getY(), getZ(), 6, 0.2D, 0.2D, 0.2D, 0.08D);
            sl.playSound(null, getX(), getY(), getZ(), SoundEvents.GLASS_BREAK, SoundSource.HOSTILE, 0.55F, 1.65F);
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

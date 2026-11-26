package com.velorise.teyvatbosses.entity;

import com.velorise.teyvatbosses.compat.StavesElementBridge;
import com.velorise.teyvatbosses.registry.TeyvatBossesEntities;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

/**
 * Hydro homing or free-aim torpedo fired by {@link RuinNautilusEntity} and {@link RuinSubmersibleEntity}.
 * Zero water drag, crisp straight bubble contrail, and Hydro AoE detonation on impact.
 */
public class RuinTorpedoProjectile extends Projectile {
    private static final int MAX_LIFETIME_TICKS = 110;
    private UUID targetUuid;
    private float damage = 18.0F;
    private boolean friendlyToPlayers = false;

    public RuinTorpedoProjectile(EntityType<? extends Projectile> type, Level level) {
        super(type, level);
        this.noPhysics = false;
    }

    public static RuinTorpedoProjectile launchHoming(
            ServerLevel level, LivingEntity owner, LivingEntity target,
            Vec3 origin, Vec3 initVel, float damage, boolean friendlyToPlayers
    ) {
        RuinTorpedoProjectile p = new RuinTorpedoProjectile(TeyvatBossesEntities.ruinTorpedo(), level);
        p.setOwner(owner);
        p.setPos(origin.x, origin.y, origin.z);
        p.setDeltaMovement(initVel);
        p.targetUuid = target != null ? target.getUUID() : null;
        p.damage = damage;
        p.friendlyToPlayers = friendlyToPlayers;
        p.orientFromVelocity(initVel);
        level.addFreshEntity(p);
        return p;
    }

    public static RuinTorpedoProjectile launchFreeAim(
            ServerLevel level, LivingEntity owner,
            Vec3 origin, Vec3 dir, float damage, boolean friendlyToPlayers
    ) {
        Vec3 vel = dir.normalize().scale(0.95D);
        return launchHoming(level, owner, null, origin, vel, damage, friendlyToPlayers);
    }

    private void orientFromVelocity(Vec3 vel) {
        double horiz = vel.horizontalDistance();
        if (horiz > 1e-4D || Math.abs(vel.y) > 1e-4D) {
            float yaw = (float) (Mth.atan2(vel.x, vel.z) * Mth.RAD_TO_DEG);
            float pitch = (float) (Mth.atan2(vel.y, horiz) * Mth.RAD_TO_DEG);
            setYRot(yaw);
            setXRot(pitch);
            this.yRotO = yaw;
            this.xRotO = pitch;
        }
    }

    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) {}
    @Override public boolean isPushedByFluid() { return false; }

    @Override
    public void tick() {
        super.tick();
        if (this.tickCount >= MAX_LIFETIME_TICKS) {
            if (level() instanceof ServerLevel sl) detonate(sl);
            else discard();
            return;
        }

        Vec3 vel = getDeltaMovement();
        if (!level().isClientSide() && level() instanceof ServerLevel sl) {
            // Smooth homing steering after 4 ticks
            if (targetUuid != null && tickCount >= 4) {
                Entity t = sl.getEntity(targetUuid);
                if (t instanceof LivingEntity le && le.isAlive()) {
                    Vec3 aim = le.position().add(0.0D, le.getBbHeight() * 0.5D, 0.0D).subtract(position());
                    if (aim.lengthSqr() > 1e-4D) {
                        double speed = Mth.clamp(vel.length() + 0.035D, 0.65D, 1.05D);
                        Vec3 desired = aim.normalize().scale(speed);
                        vel = vel.lerp(desired, 0.16D).normalize().scale(speed);
                        setDeltaMovement(vel);
                    }
                }
            }
            // Straight tight bubble contrail behind torpedo
            if (tickCount % 2 == 0) {
                sl.sendParticles(isInWater() ? ParticleTypes.BUBBLE : ParticleTypes.CLOUD,
                        getX(), getY(), getZ(), 0, -vel.x * 0.12D, -vel.y * 0.12D, -vel.z * 0.12D, 1.0D);
            }
        }

        HitResult hit = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        if (hit.getType() != HitResult.Type.MISS) {
            onHit(hit);
            return;
        }

        Vec3 next = position().add(vel);
        setPos(next.x, next.y, next.z);
        double horiz = vel.horizontalDistance();
        if (horiz > 1e-4D || Math.abs(vel.y) > 1e-4D) {
            float targetYaw = (float) (Mth.atan2(vel.x, vel.z) * Mth.RAD_TO_DEG);
            float targetPitch = (float) (Mth.atan2(vel.y, horiz) * Mth.RAD_TO_DEG);
            setYRot(Mth.rotLerp(0.35F, getYRot(), targetYaw));
            setXRot(Mth.lerp(0.35F, getXRot(), targetPitch));
        }
    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        if (!super.canHitEntity(entity)) return false;
        Entity owner = getOwner();
        if (entity == owner) return false;
        if (owner != null && (entity.isPassengerOfSameVehicle(owner) || owner.hasPassenger(entity))) return false;
        if (friendlyToPlayers && (entity instanceof Player || entity instanceof RuinSubmersibleEntity || entity instanceof RuinMechEntity)) {
            return false;
        }
        return entity instanceof LivingEntity;
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        if (level() instanceof ServerLevel sl) detonate(sl);
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        if (level() instanceof ServerLevel sl) detonate(sl);
    }

    private void detonate(ServerLevel sl) {
        Entity owner = getOwner();
        LivingEntity attacker = owner instanceof LivingEntity le ? le : null;
        AABB aoe = getBoundingBox().inflate(3.2D);
        for (LivingEntity e : sl.getEntitiesOfClass(LivingEntity.class, aoe, LivingEntity::isAlive)) {
            if (e == attacker) continue;
            if (attacker != null && attacker.hasPassenger(e)) continue;
            if (friendlyToPlayers && (e instanceof Player || e instanceof RuinSubmersibleEntity || e instanceof RuinMechEntity)) continue;
            if (e.distanceToSqr(this) <= 3.4D * 3.4D) {
                StavesElementBridge.dealElementalDamage(e, attacker, "HYDRO", damage, 1.0F, false, 0.20F);
            }
        }
        sl.playSound(null, getX(), getY(), getZ(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 0.9F, 1.25F);
        sl.sendParticles(ParticleTypes.SPLASH, getX(), getY(), getZ(), 24, 0.9D, 0.9D, 0.9D, 0.15D);
        sl.sendParticles(ParticleTypes.BUBBLE_POP, getX(), getY(), getZ(), 16, 0.8D, 0.8D, 0.8D, 0.08D);
        discard();
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putFloat("Damage", damage);
        tag.putBoolean("Friendly", friendlyToPlayers);
        if (targetUuid != null) tag.putUUID("Target", targetUuid);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Damage")) damage = tag.getFloat("Damage");
        friendlyToPlayers = tag.getBoolean("Friendly");
        if (tag.hasUUID("Target")) targetUuid = tag.getUUID("Target");
    }
}

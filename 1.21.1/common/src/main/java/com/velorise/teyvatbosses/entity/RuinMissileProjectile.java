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
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.UUID;

/**
 * Genshin-accurate homing missile fired from the Ruin Guard's back shoulder pods.
 * Launches upward in a dramatic arc for 12 ticks, then smoothly steers toward its locked target,
 * detonating in a fiery mechanical blast on impact.
 */
public class RuinMissileProjectile extends Entity {

    private UUID ownerUuid;
    private UUID targetUuid;
    private float damage = 11.5F;
    private int maxLife = 110;

    public RuinMissileProjectile(EntityType<? extends RuinMissileProjectile> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public static RuinMissileProjectile launch(
            ServerLevel level,
            LivingEntity owner,
            LivingEntity target,
            Vec3 origin,
            Vec3 initialVel,
            float damage
    ) {
        RuinMissileProjectile m = new RuinMissileProjectile(TeyvatBossesEntities.ruinMissile(), level);
        m.setPos(origin.x, origin.y, origin.z);
        m.setDeltaMovement(initialVel);
        m.ownerUuid = owner != null ? owner.getUUID() : null;
        m.targetUuid = target != null ? target.getUUID() : null;
        m.damage = damage;
        m.updateRotationFromVelocity(initialVel);
        level.addFreshEntity(m);
        level.playSound(null, origin.x, origin.y, origin.z, SoundEvents.FIREWORK_ROCKET_LAUNCH, SoundSource.HOSTILE, 0.9F, 0.85F);
        return m;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {}

    private void updateRotationFromVelocity(Vec3 vel) {
        double horiz = Math.sqrt(vel.x * vel.x + vel.z * vel.z);
        setYRot((float) (Math.atan2(-vel.x, vel.z) * (180.0D / Math.PI)));
        setXRot((float) (Math.atan2(-vel.y, horiz) * (180.0D / Math.PI)));
    }

    @Override
    public void tick() {
        super.tick();
        Vec3 vel = getDeltaMovement();

        if (!level().isClientSide() && level() instanceof ServerLevel sl) {
            // Homing guidance after initial 10-tick upward launch arc
            if (tickCount >= 10 && tickCount <= 80 && targetUuid != null) {
                Entity te = sl.getEntity(targetUuid);
                if (te instanceof LivingEntity target && target.isAlive()) {
                    Vec3 aimPos = target.position().add(0.0D, target.getBbHeight() * 0.5D, 0.0D);
                    Vec3 desired = aimPos.subtract(position()).normalize().scale(0.86D);
                    double turnRate = (tickCount < 28) ? 0.16D : 0.11D;
                    vel = vel.lerp(desired, turnRate);
                    if (vel.lengthSqr() > 1e-4D) {
                        vel = vel.normalize().scale(0.88D);
                    }
                    setDeltaMovement(vel);
                }
            }
        }

        setPos(getX() + vel.x, getY() + vel.y, getZ() + vel.z);
        updateRotationFromVelocity(vel);

        if (level().isClientSide()) {
            level().addParticle(ParticleTypes.FLAME, getX(), getY(), getZ(), -vel.x * 0.15D, -vel.y * 0.15D, -vel.z * 0.15D);
            level().addParticle(ParticleTypes.SMOKE, getX(), getY(), getZ(), 0.0D, 0.02D, 0.0D);
            return;
        }

        ServerLevel sl = (ServerLevel) level();
        boolean impacted = false;

        // Check entity hit
        AABB box = new AABB(getX() - 0.95D, getY() - 0.95D, getZ() - 0.95D,
                getX() + 0.95D, getY() + 0.95D, getZ() + 0.95D);
        List<LivingEntity> hits = sl.getEntitiesOfClass(LivingEntity.class, box,
                e -> e.isAlive() && !(e instanceof RuinGuardEntity)
                        && !(e instanceof Player p && (p.isCreative() || p.isSpectator())));
        if (!hits.isEmpty()) {
            impacted = true;
        } else if (tickCount > 6 && !sl.getBlockState(blockPosition()).isAir() && sl.getBlockState(blockPosition()).blocksMotion()) {
            impacted = true;
        }

        if (impacted || tickCount >= maxLife) {
            detonate(sl);
            discard();
        }
    }

    private void detonate(ServerLevel sl) {
        LivingEntity owner = (ownerUuid != null && sl.getEntity(ownerUuid) instanceof LivingEntity le) ? le : null;
        sl.playSound(null, getX(), getY(), getZ(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 1.05F, 1.12F);
        sl.sendParticles(ParticleTypes.EXPLOSION, getX(), getY(), getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
        sl.sendParticles(ParticleTypes.FLAME, getX(), getY(), getZ(), 16, 0.55D, 0.45D, 0.55D, 0.09D);

        AABB blast = new AABB(getX() - 2.4D, getY() - 2.0D, getZ() - 2.4D,
                getX() + 2.4D, getY() + 2.4D, getZ() + 2.4D);
        for (LivingEntity e : sl.getEntitiesOfClass(LivingEntity.class, blast,
                ent -> ent.isAlive() && !(ent instanceof RuinGuardEntity)
                        && !(ent instanceof Player p && (p.isCreative() || p.isSpectator())))) {
            StavesElementBridge.dealElementalDamage(e, owner, "PYRO", damage, 0.5F, false, 0.25F);
            Vec3 push = e.position().subtract(position()).normalize().scale(0.55D);
            e.setDeltaMovement(e.getDeltaMovement().add(push.x, 0.32D, push.z));
            e.hurtMarked = true;
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.hasUUID("Owner")) ownerUuid = tag.getUUID("Owner");
        if (tag.hasUUID("Target")) targetUuid = tag.getUUID("Target");
        if (tag.contains("Damage")) damage = tag.getFloat("Damage");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        if (ownerUuid != null) tag.putUUID("Owner", ownerUuid);
        if (targetUuid != null) tag.putUUID("Target", targetUuid);
        tag.putFloat("Damage", damage);
    }
}

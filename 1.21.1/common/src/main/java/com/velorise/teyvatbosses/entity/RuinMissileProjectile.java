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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.UUID;

/**
 * Genshin-accurate homing missile fired from the Ruin Guard's back shoulder pods.
 * <ul>
 *   <li>Ticks 0..7: Crisp upward/outward launch pop from shoulder pods.</li>
 *   <li>Ticks 8..26: Adaptive high-authority homing dive toward target.</li>
 *   <li>Terminal Lock-In (tick > 26 or within 5.5 blocks): locks straight vector into the ground/target
 *       so missiles NEVER orbit in circles and remain cleanly dodgeable.</li>
 * </ul>
 */
public class RuinMissileProjectile extends Entity {

    private UUID ownerUuid;
    private UUID targetUuid;
    private float damage = 8.5F;
    private int maxLife = 90;
    private boolean terminalLocked = false;

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
            if (tickCount < 8) {
                // Phase 1: Brief upward launch arc with gentle drag so apex stays compact
                vel = vel.scale(0.95D).add(0.0D, -0.02D, 0.0D);
                setDeltaMovement(vel);
            } else if (!terminalLocked && tickCount <= 26 && targetUuid != null) {
                // Phase 2: Tight responsive homing dive
                Entity te = sl.getEntity(targetUuid);
                if (te instanceof LivingEntity target && target.isAlive()) {
                    Vec3 aimPos = target.position().add(0.0D, target.getBbHeight() * 0.45D, 0.0D);
                    Vec3 toTarget = aimPos.subtract(position());
                    double dist = toTarget.length();
                    if (dist < 5.5D) {
                        // Phase 3: Terminal lock-in — fly straight through impact, zero orbiting!
                        vel = toTarget.normalize().scale(0.92D);
                        terminalLocked = true;
                    } else {
                        double speed = Math.min(0.94D, 0.58D + (tickCount - 8) * 0.024D);
                        Vec3 desired = toTarget.normalize().scale(speed);
                        vel = vel.lerp(desired, 0.28D);
                        if (vel.lengthSqr() > 1e-4D) {
                            vel = vel.normalize().scale(speed);
                        }
                    }
                    setDeltaMovement(vel);
                } else {
                    terminalLocked = true;
                }
            } else {
                // Terminal flight: maintain crisp straight dive with slight gravity so it always hits ground
                terminalLocked = true;
                vel = vel.add(0.0D, -0.015D, 0.0D);
                setDeltaMovement(vel);
            }
            hasImpulse = true;
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

        LivingEntity owner = (ownerUuid != null && sl.getEntity(ownerUuid) instanceof LivingEntity le) ? le : null;
        if (tickCount > 3) {
            AABB box = new AABB(getX() - 0.95D, getY() - 0.95D, getZ() - 0.95D,
                    getX() + 0.95D, getY() + 0.95D, getZ() + 0.95D);
            List<LivingEntity> hits = sl.getEntitiesOfClass(LivingEntity.class, box,
                    e -> e.isAlive() && !isFriendlyToOwner(e, owner)
                            && !(e instanceof Player p && (p.isCreative() || p.isSpectator())));
            if (!hits.isEmpty()) {
                impacted = true;
            }
        }
        if (!impacted && tickCount > 6 && !sl.getBlockState(blockPosition()).isAir() && sl.getBlockState(blockPosition()).blocksMotion()) {
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
                ent -> ent.isAlive() && !isFriendlyToOwner(ent, owner)
                        && !(ent instanceof Player p && (p.isCreative() || p.isSpectator())))) {
            StavesElementBridge.dealElementalDamage(e, owner, "PYRO", damage, 0.5F, false, 0.20F);
            Vec3 push = e.position().subtract(position()).normalize().scale(0.55D);
            e.setDeltaMovement(e.getDeltaMovement().add(push.x, 0.32D, push.z));
            e.hurtMarked = true;
        }
    }


    private boolean isFriendlyToOwner(LivingEntity candidate, LivingEntity owner) {
        if (owner != null) {
            if (candidate == owner || owner.hasPassenger(candidate) || candidate.getVehicle() == owner) {
                return true;
            }
            if (owner instanceof RuinMechEntity && candidate instanceof RuinMechEntity) {
                return true;
            }
            if (owner instanceof RuinGuardEntity && candidate instanceof RuinGuardEntity) {
                return true;
            }
        } else if (candidate instanceof RuinGuardEntity) {
            return true;
        }
        return false;
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

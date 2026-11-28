package com.velorise.teyvatbosses.entity;

import com.velorise.teyvatbosses.compat.StavesElementBridge;
import com.velorise.teyvatbosses.config.TeyvatBossesConfig;
import com.velorise.teyvatbosses.registry.TeyvatBossesEntities;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * Phlogiston Fireball projectile spat by {@link GoldflameQucusaurMountEntity} (crosshair-aimed, zero auto-aim)
 * or {@link GoldflameQucusaurEntity}. Explodes on impact into a 2.5-block Pyro AoE without breaking blocks
 * (optional fire ignition gated behind {@code enableMountFireBlockIgnition}, default false).
 */
public class GoldflameFireballEntity extends Projectile {
    private float damage = 12.0F;
    private float aoeRadius = 2.5F;
    private boolean fromMount = true;
    private int lifeTicks = 0;

    public GoldflameFireballEntity(EntityType<? extends GoldflameFireballEntity> type, Level level) {
        super(type, level);
    }

    public static GoldflameFireballEntity spawn(Level level, LivingEntity owner, Vec3 origin, Vec3 direction,
                                                float speed, float damage, float aoeRadius, boolean fromMount) {
        GoldflameFireballEntity fb = new GoldflameFireballEntity(TeyvatBossesEntities.goldflameFireball(), level);
        fb.setOwner(owner);
        fb.setPos(origin.x, origin.y, origin.z);
        fb.setDeltaMovement(direction.normalize().scale(speed));
        fb.damage = damage;
        fb.aoeRadius = aoeRadius;
        fb.fromMount = fromMount;
        level.addFreshEntity(fb);
        return fb;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {}

    @Override
    public void tick() {
        super.tick();
        lifeTicks++;
        if (lifeTicks > 100) {
            discard();
            return;
        }

        HitResult hit = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        if (hit.getType() != HitResult.Type.MISS) {
            onHit(hit);
            return;
        }

        Vec3 vel = getDeltaMovement();
        setPos(getX() + vel.x, getY() + vel.y, getZ() + vel.z);
        setDeltaMovement(vel.add(0.0D, -0.012D, 0.0D));

        if (level().isClientSide()) {
            level().addParticle(ParticleTypes.FLAME, getX(), getY(), getZ(), 0.0D, 0.01D, 0.0D);
            if (lifeTicks % 2 == 0) {
                level().addParticle(ParticleTypes.LAVA, getX(), getY(), getZ(), 0.0D, 0.0D, 0.0D);
            }
        }
    }

    @Override
    protected boolean canHitEntity(Entity target) {
        if (!super.canHitEntity(target)) return false;
        Entity owner = getOwner();
        if (target == owner) return false;
        if (owner != null && (target.isPassengerOfSameVehicle(owner) || owner.isPassengerOfSameVehicle(target))) {
            return false;
        }
        return true;
    }

    @Override
    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
        if (level().isClientSide()) return;
        ServerLevel sl = (ServerLevel) level();
        Vec3 pos = hitResult.getLocation();

        Entity owner = getOwner();
        LivingEntity livingOwner = owner instanceof LivingEntity le ? le : null;

        AABB box = new AABB(pos.x - aoeRadius, pos.y - aoeRadius, pos.z - aoeRadius,
                pos.x + aoeRadius, pos.y + aoeRadius, pos.z + aoeRadius);
        List<LivingEntity> targets = sl.getEntitiesOfClass(LivingEntity.class, box,
                e -> e.isAlive() && canHitEntity(e) && e.position().distanceToSqr(pos) <= (aoeRadius + 0.8D) * (aoeRadius + 0.8D));
        for (LivingEntity t : targets) {
            StavesElementBridge.dealElementalDamage(t, livingOwner, "PYRO", damage, 1.0F, false, 0.25F);
            t.igniteForSeconds(4.0F);
        }

        if (fromMount && TeyvatBossesConfig.get().enableMountFireBlockIgnition) {
            BlockPos bp = BlockPos.containing(pos);
            if (sl.isEmptyBlock(bp) && BaseFireBlock.canBePlacedAt(sl, bp, getDirection())) {
                sl.setBlockAndUpdate(bp, BaseFireBlock.getState(sl, bp));
            }
        }

        sl.sendParticles(ParticleTypes.EXPLOSION, pos.x, pos.y + 0.2D, pos.z, 2, 0.3D, 0.3D, 0.3D, 0.0D);
        sl.sendParticles(ParticleTypes.FLAME, pos.x, pos.y + 0.2D, pos.z, 22, 0.65D, 0.55D, 0.65D, 0.12D);
        sl.playSound(null, pos.x, pos.y, pos.z, SoundEvents.GENERIC_EXPLODE.value(), SoundSource.NEUTRAL, 0.85F, 1.25F);
        discard();
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putFloat("Damage", damage);
        tag.putFloat("AoeRadius", aoeRadius);
        tag.putBoolean("FromMount", fromMount);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Damage")) damage = tag.getFloat("Damage");
        if (tag.contains("AoeRadius")) aoeRadius = tag.getFloat("AoeRadius");
        fromMount = tag.getBoolean("FromMount");
    }
}

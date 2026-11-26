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
 * Glowing Anemo energy projectile fired during:
 * <ul>
 *   <li>{@link #KIND_ORB_BOLT} (0): Anemo Hypostasis Magic Circle Volley</li>
 *   <li>{@link #KIND_WIND_BLADE} (1): Anemo Hypostasis Butterfly Winged Gust Sweep</li>
 *   <li>{@link #KIND_PET_CUBE} (2): Launched physical shell cube from {@link AnemoCompanionEntity}</li>
 * </ul>
 */
public class AnemoMissileProjectile extends Entity {

    public static final int KIND_ORB_BOLT = 0;
    public static final int KIND_WIND_BLADE = 1;
    public static final int KIND_PET_CUBE = 2;

    private static final EntityDataAccessor<Integer> DATA_KIND =
            SynchedEntityData.defineId(AnemoMissileProjectile.class, EntityDataSerializers.INT);

    private UUID ownerUuid;
    private float damage = 11.0F;
    private int maxLife = 85;
    private boolean friendlyToPlayers = false;

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
        p.friendlyToPlayers = (kind == KIND_PET_CUBE);
        p.entityData.set(DATA_KIND, kind);
        p.setYRot((float) (Math.atan2(-velocity.x, velocity.z) * (180.0D / Math.PI)));
        level.addFreshEntity(p);
        return p;
    }

    public static AnemoMissileProjectile launchPetCube(
            ServerLevel level,
            LivingEntity owner,
            Vec3 origin,
            Vec3 velocity,
            float damage
    ) {
        return launch(level, owner, origin, velocity, damage, KIND_PET_CUBE);
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
        int kind = getKind();
        boolean petMode = (kind == KIND_PET_CUBE) || friendlyToPlayers;
        double hitRadius = (kind == KIND_WIND_BLADE) ? 2.15D : (petMode ? 0.85D : 1.05D);
        AABB box = new AABB(getX() - hitRadius, getY() - 0.9D, getZ() - hitRadius,
                getX() + hitRadius, getY() + 0.9D, getZ() + hitRadius);

        List<LivingEntity> hits = sl.getEntitiesOfClass(LivingEntity.class, box, e -> {
            if (!e.isAlive()) return false;
            if (petMode) {
                if (e instanceof Player || e instanceof AnemoCompanionEntity
                        || e instanceof RuinSubmersibleEntity || e instanceof RuinMechEntity
                        || e instanceof CryoTurretEntity) {
                    return false;
                }
                return ownerUuid == null || !ownerUuid.equals(e.getUUID());
            }
            return !(e instanceof AnemoHypostasisEntity)
                    && !(e instanceof Player pl && (pl.isCreative() || pl.isSpectator()));
        });

        boolean impacted = false;
        LivingEntity owner = (ownerUuid != null && sl.getEntity(ownerUuid) instanceof LivingEntity le) ? le : null;
        if (!hits.isEmpty()) {
            Vec3 pushDir = vel.lengthSqr() > 1.0e-4D ? vel.normalize() : Vec3.ZERO;
            double kbScale = (kind == KIND_WIND_BLADE) ? 0.95D : (petMode ? 0.88D : 0.45D);
            double kbUp = petMode ? 0.32D : 0.28D;
            for (LivingEntity target : hits) {
                // Uses Velorise's Staves ANEMO elemental damage (+Swirl) when installed, or vanilla fallback otherwise
                StavesElementBridge.dealElementalDamage(target, owner, "ANEMO", damage, 1.0F, false, petMode ? 0.20F : 0.30F);
                target.setDeltaMovement(target.getDeltaMovement().add(pushDir.x * kbScale, kbUp, pushDir.z * kbScale));
                target.hurtMarked = true;
                if (petMode) break; // Single-cube direct impact
            }
            impacted = true;
        } else if (!sl.getBlockState(blockPosition()).isAir() && sl.getBlockState(blockPosition()).blocksMotion()) {
            impacted = true;
        }

        if (impacted) {
            SoundSource src = petMode ? SoundSource.PLAYERS : SoundSource.HOSTILE;
            sl.playSound(null, getX(), getY(), getZ(), SoundEvents.BREEZE_WIND_CHARGE_BURST.value(), src, petMode ? 0.75F : 0.95F, petMode ? 1.35F : 1.25F);
            sl.sendParticles(ParticleTypes.GUST_EMITTER_SMALL, getX(), getY(), getZ(), 1, 0.1D, 0.1D, 0.1D, 0.0D);
            sl.sendParticles(ParticleTypes.END_ROD, getX(), getY(), getZ(), petMode ? 10 : 14, 0.35D, 0.35D, 0.35D, 0.07D);
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
        if (tag.contains("Friendly")) friendlyToPlayers = tag.getBoolean("Friendly");
        if (tag.hasUUID("Owner")) ownerUuid = tag.getUUID("Owner");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("Kind", getKind());
        tag.putFloat("Damage", damage);
        tag.putBoolean("Friendly", friendlyToPlayers);
        if (ownerUuid != null) tag.putUUID("Owner", ownerUuid);
    }
}

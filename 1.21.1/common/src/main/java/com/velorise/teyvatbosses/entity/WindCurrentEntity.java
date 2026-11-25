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
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * Genshin-accurate Anemo Wind Current (Updraft) entity.
 * Creates a tall rising air column that smoothly lifts players skyward and grants
 * responsive slow-fall gliding momentum so players can dodge Hurricane Tornadoes
 * and reach airborne Wind Crystals during the Anemo Hypostasis Revival Phase.
 */
public class WindCurrentEntity extends Entity {

    private static final EntityDataAccessor<Float> DATA_RADIUS =
            SynchedEntityData.defineId(WindCurrentEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_HEIGHT =
            SynchedEntityData.defineId(WindCurrentEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> DATA_MAX_LIFE =
            SynchedEntityData.defineId(WindCurrentEntity.class, EntityDataSerializers.INT);

    public WindCurrentEntity(EntityType<? extends WindCurrentEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public static WindCurrentEntity spawn(ServerLevel level, double x, double y, double z, float radius, float height, int durationTicks) {
        WindCurrentEntity wc = new WindCurrentEntity(TeyvatBossesEntities.windCurrent(), level);
        wc.setPos(x, y, z);
        wc.entityData.set(DATA_RADIUS, radius);
        wc.entityData.set(DATA_HEIGHT, height);
        wc.entityData.set(DATA_MAX_LIFE, Math.max(40, durationTicks));
        level.addFreshEntity(wc);
        level.playSound(null, x, y, z, SoundEvents.BREEZE_WIND_CHARGE_BURST.value(), SoundSource.AMBIENT, 0.85F, 1.35F);
        return wc;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_RADIUS, 2.6F);
        builder.define(DATA_HEIGHT, 9.5F);
        builder.define(DATA_MAX_LIFE, 320);
    }

    public float getRadius() { return entityData.get(DATA_RADIUS); }
    public float getColumnHeight() { return entityData.get(DATA_HEIGHT); }
    public int getMaxLife() { return entityData.get(DATA_MAX_LIFE); }

    @Override
    public void tick() {
        super.tick();
        float r = getRadius();
        float h = getColumnHeight();

        // Client-side rising wind vortex particles & smooth local player updraft feel
        if (level().isClientSide()) {
            if (tickCount % 2 == 0) {
                for (int i = 0; i < 3; i++) {
                    double ang = (tickCount * 0.35D) + i * (Math.PI * 2.0D / 3.0D);
                    double rad = r * (0.35D + random.nextDouble() * 0.55D);
                    double px = getX() + Math.cos(ang) * rad;
                    double pz = getZ() + Math.sin(ang) * rad;
                    double py = getY() + random.nextDouble() * (h * 0.45D);
                    level().addParticle(ParticleTypes.CLOUD, px, py, pz, -Math.sin(ang) * 0.08D, 0.42D, Math.cos(ang) * 0.08D);
                }
            }
        }

        // Apply smooth Genshin-style updraft lift to all players inside the cylinder (both server & client for zero rubberbanding)
        AABB box = new AABB(getX() - r, getY() - 0.5D, getZ() - r, getX() + r, getY() + h, getZ() + r);
        List<Player> players = level().getEntitiesOfClass(Player.class, box, p -> p.isAlive() && !p.isSpectator());
        for (Player p : players) {
            double dx = p.getX() - getX();
            double dz = p.getZ() - getZ();
            if (dx * dx + dz * dz <= (double) (r * r)) {
                double relY = p.getY() - getY();
                double targetVy = relY < h - 1.5D ? 0.52D : 0.14D;
                Vec3 vel = p.getDeltaMovement();
                double newVy = Math.min(targetVy, Math.max(vel.y + 0.16D, 0.28D));
                p.setDeltaMovement(vel.x * 1.03D, newVy, vel.z * 1.03D);
                p.resetFallDistance();
                if (!level().isClientSide()) {
                    p.hurtMarked = true;
                    // Grant brief Slow Falling (Wind Glider feel!) so player can glide between Wind Crystals
                    p.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 50, 0, false, false, true));
                }
            }
        }

        if (!level().isClientSide()) {
            if (tickCount % 35 == 0) {
                level().playSound(null, getX(), getY() + 2.0D, getZ(), SoundEvents.ELYTRA_FLYING, SoundSource.AMBIENT, 0.35F, 1.4F);
            }
            if (tickCount >= getMaxLife()) {
                discard();
            }
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.contains("Radius")) entityData.set(DATA_RADIUS, tag.getFloat("Radius"));
        if (tag.contains("Height")) entityData.set(DATA_HEIGHT, tag.getFloat("Height"));
        if (tag.contains("MaxLife")) entityData.set(DATA_MAX_LIFE, tag.getInt("MaxLife"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putFloat("Radius", getRadius());
        tag.putFloat("Height", getColumnHeight());
        tag.putInt("MaxLife", getMaxLife());
    }
}

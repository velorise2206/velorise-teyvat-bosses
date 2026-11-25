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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.UUID;

/**
 * Floating Anemo Energy Orb / Revival Wind Crystal.
 * Players can glide through these orbs using Wind Currents to absorb them before
 * the Anemo Hypostasis recalls them for shockwaves or HP regeneration.
 */
public class AnemoWindOrbEntity extends Entity {

    private static final EntityDataAccessor<Boolean> DATA_IS_REVIVAL =
            SynchedEntityData.defineId(AnemoWindOrbEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_RECALLING =
            SynchedEntityData.defineId(AnemoWindOrbEntity.class, EntityDataSerializers.BOOLEAN);

    private UUID ownerUuid;
    private int maxLife = 340;
    private Vec3 recallTarget = null;

    public AnemoWindOrbEntity(EntityType<? extends AnemoWindOrbEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public static AnemoWindOrbEntity spawn(ServerLevel level, double x, double y, double z, UUID ownerUuid, boolean isRevival, int maxLife) {
        AnemoWindOrbEntity orb = new AnemoWindOrbEntity(TeyvatBossesEntities.anemoWindOrb(), level);
        orb.setPos(x, y, z);
        orb.ownerUuid = ownerUuid;
        orb.maxLife = Math.max(60, maxLife);
        orb.entityData.set(DATA_IS_REVIVAL, isRevival);
        orb.entityData.set(DATA_RECALLING, false);
        level.addFreshEntity(orb);
        return orb;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_IS_REVIVAL, false);
        builder.define(DATA_RECALLING, false);
    }

    public boolean isRevivalCrystal() { return entityData.get(DATA_IS_REVIVAL); }
    public boolean isRecalling() { return entityData.get(DATA_RECALLING); }
    public UUID getOwnerUuid() { return ownerUuid; }

    public void startRecall(Vec3 bossPos) {
        this.recallTarget = bossPos;
        this.entityData.set(DATA_RECALLING, true);
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide()) {
            if (tickCount % 3 == 0) {
                level().addParticle(ParticleTypes.GLOW,
                        getX() + (random.nextDouble() - 0.5D) * 0.7D,
                        getY() + (random.nextDouble() - 0.5D) * 0.7D,
                        getZ() + (random.nextDouble() - 0.5D) * 0.7D,
                        0.0D, 0.03D, 0.0D);
            }
            return;
        }

        ServerLevel sl = (ServerLevel) level();

        // Check player absorption (generous 1.85-block touch radius while gliding!)
        AABB box = new AABB(getX() - 1.85D, getY() - 1.85D, getZ() - 1.85D,
                getX() + 1.85D, getY() + 1.85D, getZ() + 1.85D);
        List<Player> players = sl.getEntitiesOfClass(Player.class, box, p -> p.isAlive() && !p.isSpectator());
        if (!players.isEmpty()) {
            sl.playSound(null, getX(), getY(), getZ(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 1.25F, 1.55F);
            sl.playSound(null, getX(), getY(), getZ(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.9F, 1.35F);
            sl.sendParticles(ParticleTypes.END_ROD, getX(), getY(), getZ(), 22, 0.45D, 0.45D, 0.45D, 0.09D);
            sl.sendParticles(ParticleTypes.CLOUD, getX(), getY(), getZ(), 10, 0.35D, 0.35D, 0.35D, 0.06D);
            // Notify owner boss if this was a revival crystal
            if (ownerUuid != null && sl.getEntity(ownerUuid) instanceof AnemoHypostasisEntity boss) {
                boss.onOrbAbsorbedByPlayer(this);
            }
            discard();
            return;
        }

        // Recall flight toward boss
        if (isRecalling() && recallTarget != null) {
            Vec3 toBoss = recallTarget.subtract(position());
            double dist = toBoss.length();
            if (dist < 1.35D) {
                if (ownerUuid != null && sl.getEntity(ownerUuid) instanceof AnemoHypostasisEntity boss) {
                    boss.onOrbRecalledToBoss(this);
                }
                sl.sendParticles(ParticleTypes.CLOUD, getX(), getY(), getZ(), 14, 0.4D, 0.4D, 0.4D, 0.08D);
                discard();
                return;
            }
            Vec3 step = toBoss.scale(Math.min(0.85D, dist) / dist);
            setPos(getX() + step.x, getY() + step.y, getZ() + step.z);
        }

        if (tickCount >= maxLife) {
            discard();
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.contains("IsRevival")) entityData.set(DATA_IS_REVIVAL, tag.getBoolean("IsRevival"));
        if (tag.hasUUID("Owner")) ownerUuid = tag.getUUID("Owner");
        if (tag.contains("MaxLife")) maxLife = tag.getInt("MaxLife");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putBoolean("IsRevival", isRevivalCrystal());
        if (ownerUuid != null) tag.putUUID("Owner", ownerUuid);
        tag.putInt("MaxLife", maxLife);
    }
}

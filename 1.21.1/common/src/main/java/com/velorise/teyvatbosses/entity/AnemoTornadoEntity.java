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
 * Roaming 3D Hurricane Tornado spawned by the Anemo Hypostasis during its Tornado Storm attack.
 * Spirals across the arena, deals Anemo damage + upward knockback on contact,
 * and leaves an absorbable AnemoWindOrbEntity behind when it dissipates.
 */
public class AnemoTornadoEntity extends Entity {

    private UUID ownerUuid;
    private Vec3 arenaCenter = Vec3.ZERO;
    private float orbitRadius = 8.0F;
    private float orbitAngle = 0.0F;
    private float angularSpeed = 0.045F;
    private float radialWobble = 2.5F;
    private int maxLife = 220;
    private boolean spawnOrbOnEnd = true;

    public AnemoTornadoEntity(EntityType<? extends AnemoTornadoEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public static AnemoTornadoEntity spawn(
            ServerLevel level,
            Vec3 arenaCenter,
            UUID ownerUuid,
            float orbitRadius,
            float startAngle,
            float angularSpeed,
            int maxLife,
            boolean spawnOrbOnEnd
    ) {
        AnemoTornadoEntity t = new AnemoTornadoEntity(TeyvatBossesEntities.anemoTornado(), level);
        t.arenaCenter = arenaCenter;
        t.ownerUuid = ownerUuid;
        t.orbitRadius = orbitRadius;
        t.orbitAngle = startAngle;
        t.angularSpeed = angularSpeed;
        t.maxLife = Math.max(60, maxLife);
        t.spawnOrbOnEnd = spawnOrbOnEnd;
        double sx = arenaCenter.x + Math.cos(startAngle) * orbitRadius;
        double sz = arenaCenter.z + Math.sin(startAngle) * orbitRadius;
        t.setPos(sx, arenaCenter.y, sz);
        level.addFreshEntity(t);
        return t;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {}

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide()) {
            if (tickCount % 2 == 0) {
                for (int i = 0; i < 3; i++) {
                    double h = random.nextDouble() * 5.8D;
                    double rad = 0.7D + h * 0.28D;
                    double a = tickCount * 0.45D + h * 1.1D + i * 2.1D;
                    level().addParticle(ParticleTypes.CLOUD,
                            getX() + Math.cos(a) * rad,
                            getY() + h,
                            getZ() + Math.sin(a) * rad,
                            -Math.sin(a) * 0.14D, 0.14D, Math.cos(a) * 0.14D);
                }
            }
            return;
        }

        ServerLevel sl = (ServerLevel) level();
        orbitAngle += angularSpeed;
        float r = orbitRadius + Mth.sin(tickCount * 0.06F + orbitAngle) * radialWobble;
        double nx = arenaCenter.x + Math.cos(orbitAngle) * r;
        double nz = arenaCenter.z + Math.sin(orbitAngle) * r;
        setPos(nx, arenaCenter.y, nz);

        // Damage & launch players who touch the tornado funnel
        if (tickCount > 12 && tickCount % 10 == 0) {
            LivingEntity owner = (ownerUuid != null && sl.getEntity(ownerUuid) instanceof LivingEntity le) ? le : null;
            AABB box = new AABB(getX() - 1.95D, getY(), getZ() - 1.95D, getX() + 1.95D, getY() + 6.0D, getZ() + 1.95D);
            List<Player> hits = sl.getEntitiesOfClass(Player.class, box, p -> p.isAlive() && !p.isCreative() && !p.isSpectator());
            for (Player p : hits) {
                StavesElementBridge.dealElementalDamage(p, owner, "ANEMO", 8.5F, 1.0F, false, 0.25F);
                Vec3 push = p.position().subtract(position()).normalize().scale(0.65D);
                p.setDeltaMovement(push.x, 0.62D, push.z);
                p.hurtMarked = true;
            }
        }

        if (tickCount % 30 == 0) {
            sl.playSound(null, getX(), getY() + 2.0D, getZ(), SoundEvents.BREEZE_IDLE_AIR, SoundSource.HOSTILE, 0.65F, 0.85F);
        }

        if (tickCount >= maxLife) {
            if (spawnOrbOnEnd) {
                AnemoWindOrbEntity.spawn(sl, getX(), getY() + 3.2D, getZ(), ownerUuid, false, 360);
            }
            sl.sendParticles(ParticleTypes.CLOUD, getX(), getY() + 2.5D, getZ(), 18, 0.9D, 1.5D, 0.9D, 0.08D);
            discard();
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.hasUUID("Owner")) ownerUuid = tag.getUUID("Owner");
        arenaCenter = new Vec3(tag.getDouble("CX"), tag.getDouble("CY"), tag.getDouble("CZ"));
        orbitRadius = tag.getFloat("ORad");
        orbitAngle = tag.getFloat("OAng");
        maxLife = tag.getInt("MaxLife");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        if (ownerUuid != null) tag.putUUID("Owner", ownerUuid);
        tag.putDouble("CX", arenaCenter.x);
        tag.putDouble("CY", arenaCenter.y);
        tag.putDouble("CZ", arenaCenter.z);
        tag.putFloat("ORad", orbitRadius);
        tag.putFloat("OAng", orbitAngle);
        tag.putInt("MaxLife", maxLife);
    }
}

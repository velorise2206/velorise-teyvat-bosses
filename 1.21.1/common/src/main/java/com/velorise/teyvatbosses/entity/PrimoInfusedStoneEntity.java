package com.velorise.teyvatbosses.entity;

import com.velorise.teyvatbosses.compat.StavesElementBridge;
import com.velorise.teyvatbosses.registry.TeyvatBossesEntities;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Real 3D Hollowed-Rock Elemental-Infused Stone entity summoned by {@link PrimoGeovishapEntity}
 * during Scattered Rocks, Burrow, and Primeval Spring.
 *
 * <p>Features a sculpted hollowed-out basalt monolith shell with a levitating, spinning elemental
 * crystal core suspended inside, rotating 6-pointed star ground rune, and all 4 canonical Genshin
 * elemental detonation patterns (Pyro explosion + 3.5s lava pool DoT, Hydro 7-geyser splash,
 * Cryo 3-ring ice spike burst, Electro X-cross lightning detonation).
 */
public class PrimoInfusedStoneEntity extends Entity {

    public static final int PHASE_ARMING = 0;
    public static final int PHASE_LAVA_POOL = 1;

    private static final EntityDataAccessor<Integer> DATA_INFUSION =
            SynchedEntityData.defineId(PrimoInfusedStoneEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_FUSE_MAX =
            SynchedEntityData.defineId(PrimoInfusedStoneEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_FUSE_LEFT =
            SynchedEntityData.defineId(PrimoInfusedStoneEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_PHASE =
            SynchedEntityData.defineId(PrimoInfusedStoneEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DATA_SPIN_YAW =
            SynchedEntityData.defineId(PrimoInfusedStoneEntity.class, EntityDataSerializers.FLOAT);

    @Nullable
    private UUID ownerUuid;
    private int lavaTicksLeft = 0;

    public PrimoInfusedStoneEntity(EntityType<? extends PrimoInfusedStoneEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public static PrimoInfusedStoneEntity spawn(
            ServerLevel level,
            LivingEntity owner,
            double x,
            double y,
            double z,
            int infusion,
            int fuseTicks
    ) {
        PrimoInfusedStoneEntity stone = new PrimoInfusedStoneEntity(TeyvatBossesEntities.primoInfusedStone(), level);
        stone.setPos(x, y, z);
        stone.ownerUuid = owner.getUUID();
        stone.entityData.set(DATA_INFUSION, infusion);
        stone.entityData.set(DATA_FUSE_MAX, Math.max(10, fuseTicks));
        stone.entityData.set(DATA_FUSE_LEFT, Math.max(10, fuseTicks));
        stone.entityData.set(DATA_PHASE, PHASE_ARMING);
        stone.entityData.set(DATA_SPIN_YAW, level.random.nextFloat() * 360.0F);
        level.addFreshEntity(stone);
        level.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.DRIPSTONE_BLOCK.defaultBlockState()),
                x, y + 0.4D, z, 12, 0.45D, 0.35D, 0.45D, 0.09D);
        level.playSound(null, x, y, z, SoundEvents.STONE_PLACE, SoundSource.HOSTILE, 1.1F, 0.72F);
        return stone;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_INFUSION, PrimoGeovishapEntity.INFUSION_PYRO);
        builder.define(DATA_FUSE_MAX, 48);
        builder.define(DATA_FUSE_LEFT, 48);
        builder.define(DATA_PHASE, PHASE_ARMING);
        builder.define(DATA_SPIN_YAW, 0.0F);
    }

    public int getInfusion() { return entityData.get(DATA_INFUSION); }
    public int getFuseMax() { return Math.max(1, entityData.get(DATA_FUSE_MAX)); }
    public int getFuseLeft() { return entityData.get(DATA_FUSE_LEFT); }
    public int getPhase() { return entityData.get(DATA_PHASE); }
    public float getSpinYaw() { return entityData.get(DATA_SPIN_YAW); }

    public int[] getInfusionRgb() {
        return switch (getInfusion()) {
            case PrimoGeovishapEntity.INFUSION_HYDRO -> new int[]{40, 190, 255};
            case PrimoGeovishapEntity.INFUSION_ELECTRO -> new int[]{200, 75, 255};
            case PrimoGeovishapEntity.INFUSION_CRYO -> new int[]{135, 238, 255};
            default -> new int[]{255, 115, 35};
        };
    }

    public String getInfusionElementName() {
        return switch (getInfusion()) {
            case PrimoGeovishapEntity.INFUSION_HYDRO -> "HYDRO";
            case PrimoGeovishapEntity.INFUSION_ELECTRO -> "ELECTRO";
            case PrimoGeovishapEntity.INFUSION_CRYO -> "CRYO";
            default -> "PYRO";
        };
    }

    private ParticleOptions infusionParticle() {
        return switch (getInfusion()) {
            case PrimoGeovishapEntity.INFUSION_HYDRO -> ParticleTypes.SPLASH;
            case PrimoGeovishapEntity.INFUSION_ELECTRO -> ParticleTypes.ELECTRIC_SPARK;
            case PrimoGeovishapEntity.INFUSION_CRYO -> ParticleTypes.SNOWFLAKE;
            default -> ParticleTypes.FLAME;
        };
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide()) {
            if (getPhase() == PHASE_ARMING && tickCount % 4 == 0) {
                double ang = tickCount * 0.45D;
                level().addParticle(infusionParticle(),
                        getX() + Math.cos(ang) * 0.55D, getY() + 1.05D, getZ() + Math.sin(ang) * 0.55D,
                        0.0D, 0.04D, 0.0D);
            } else if (getPhase() == PHASE_LAVA_POOL && tickCount % 2 == 0) {
                double rx = (random.nextDouble() - 0.5D) * 4.4D;
                double rz = (random.nextDouble() - 0.5D) * 4.4D;
                level().addParticle(ParticleTypes.LAVA, getX() + rx, getY() + 0.15D, getZ() + rz, 0.0D, 0.04D, 0.0D);
                level().addParticle(ParticleTypes.FLAME, getX() + rx, getY() + 0.25D, getZ() + rz, 0.0D, 0.03D, 0.0D);
            }
            return;
        }

        ServerLevel sl = (ServerLevel) level();
        if (getPhase() == PHASE_ARMING) {
            int left = getFuseLeft() - 1;
            entityData.set(DATA_FUSE_LEFT, Math.max(0, left));
            if (left % 8 == 0 && left > 0) {
                sl.sendParticles(infusionParticle(), getX(), getY() + 1.15D, getZ(), 4, 0.35D, 0.45D, 0.35D, 0.03D);
            }
            if (left <= 0) {
                detonate(sl);
            }
        } else {
            lavaTicksLeft--;
            if (lavaTicksLeft % 10 == 0) {
                dealRadialDamage(sl, 2.7D, 4.5F, 0.18D, 0.12D);
            }
            if (lavaTicksLeft <= 0) {
                discard();
            }
        }
    }

    private void detonate(ServerLevel sl) {
        Vec3 p = position();
        ParticleOptions part = infusionParticle();
        sl.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.DEEPSLATE.defaultBlockState()),
                p.x, p.y + 1.0D, p.z, 24, 0.8D, 0.8D, 0.8D, 0.18D);

        switch (getInfusion()) {
            case PrimoGeovishapEntity.INFUSION_PYRO -> {
                sl.playSound(null, p.x, p.y, p.z, SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 0.95F, 0.85F);
                sl.sendParticles(ParticleTypes.EXPLOSION, p.x, p.y + 0.4D, p.z, 2, 0.6D, 0.3D, 0.6D, 0.0D);
                sl.sendParticles(ParticleTypes.FLAME, p.x, p.y + 0.5D, p.z, 22, 1.3D, 0.6D, 1.3D, 0.09D);
                dealRadialDamage(sl, 3.2D, 11.0F, 0.7D, 0.35D);
                entityData.set(DATA_PHASE, PHASE_LAVA_POOL);
                lavaTicksLeft = 70; // 3.5s Pyro lava pool DoT
            }
            case PrimoGeovishapEntity.INFUSION_HYDRO -> {
                sl.playSound(null, p.x, p.y, p.z, SoundEvents.PLAYER_SPLASH_HIGH_SPEED, SoundSource.HOSTILE, 1.25F, 0.8F);
                for (int i = 0; i < 7; i++) {
                    double ang = (Math.PI * 2.0D * i) / 7.0D;
                    double rad = (i == 0) ? 0.0D : 2.2D;
                    Vec3 gPos = p.add(Math.cos(ang) * rad, 0.0D, Math.sin(ang) * rad);
                    sl.sendParticles(ParticleTypes.SPLASH, gPos.x, gPos.y + 0.8D, gPos.z, 12, 0.3D, 0.95D, 0.3D, 0.12D);
                    sl.sendParticles(ParticleTypes.BUBBLE_POP, gPos.x, gPos.y + 1.0D, gPos.z, 5, 0.3D, 0.5D, 0.3D, 0.05D);
                }
                dealRadialDamage(sl, 3.6D, 10.5F, 0.65D, 0.45D);
                discard();
            }
            case PrimoGeovishapEntity.INFUSION_CRYO -> {
                sl.playSound(null, p.x, p.y, p.z, SoundEvents.GLASS_BREAK, SoundSource.HOSTILE, 1.25F, 0.75F);
                for (double r : new double[]{1.4D, 2.6D, 3.8D}) {
                    for (int i = 0; i < 12; i++) {
                        double a = (Math.PI * 2.0D * i) / 12.0D;
                        sl.sendParticles(ParticleTypes.SNOWFLAKE, p.x + Math.cos(a) * r, p.y + 0.5D, p.z + Math.sin(a) * r,
                                2, 0.1D, 0.4D, 0.1D, 0.03D);
                    }
                }
                dealRadialDamage(sl, 4.0D, 11.5F, 0.60D, 0.32D);
                discard();
            }
            default -> { // ELECTRO X-pattern detonation
                sl.playSound(null, p.x, p.y, p.z, SoundEvents.LIGHTNING_BOLT_IMPACT, SoundSource.HOSTILE, 0.95F, 1.35F);
                for (int arm = 0; arm < 4; arm++) {
                    double ang = Math.PI * 0.25D + arm * (Math.PI * 0.5D);
                    for (double d = 0.6D; d <= 3.6D; d += 0.75D) {
                        sl.sendParticles(part, p.x + Math.cos(ang) * d, p.y + 0.5D, p.z + Math.sin(ang) * d,
                                3, 0.15D, 0.35D, 0.15D, 0.04D);
                    }
                }
                dealRadialDamage(sl, 3.8D, 10.5F, 0.75D, 0.30D);
                discard();
            }
        }
    }

    private void dealRadialDamage(ServerLevel sl, double radius, float dmg, double kb, double lift) {
        LivingEntity owner = null;
        if (ownerUuid != null && sl.getEntity(ownerUuid) instanceof LivingEntity le) {
            owner = le;
        }
        AABB box = new AABB(getX() - radius, getY() - 1.5D, getZ() - radius,
                getX() + radius, getY() + 4.0D, getZ() + radius);
        for (LivingEntity e : sl.getEntitiesOfClass(LivingEntity.class, box,
                le -> le.isAlive() && !(le instanceof PrimoGeovishapEntity))) {
            if (e.position().distanceToSqr(position()) > radius * radius) continue;
            if (StavesElementBridge.isAvailable()) {
                StavesElementBridge.dealElementalDamage(e, owner, getInfusionElementName(), dmg, 1.0F, false, 0.25F);
            } else {
                e.hurt(owner != null ? sl.damageSources().mobAttack(owner) : sl.damageSources().magic(), dmg);
            }
            Vec3 push = e.position().subtract(position()).multiply(1.0D, 0.0D, 1.0D);
            if (push.lengthSqr() > 1.0E-4D) push = push.normalize().scale(kb);
            e.setDeltaMovement(push.x, lift, push.z);
            e.hurtMarked = true;
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.contains("Infusion")) entityData.set(DATA_INFUSION, tag.getInt("Infusion"));
        if (tag.contains("FuseMax")) entityData.set(DATA_FUSE_MAX, tag.getInt("FuseMax"));
        if (tag.contains("FuseLeft")) entityData.set(DATA_FUSE_LEFT, tag.getInt("FuseLeft"));
        if (tag.contains("Phase")) entityData.set(DATA_PHASE, tag.getInt("Phase"));
        if (tag.contains("SpinYaw")) entityData.set(DATA_SPIN_YAW, tag.getFloat("SpinYaw"));
        if (tag.hasUUID("OwnerUUID")) ownerUuid = tag.getUUID("OwnerUUID");
        lavaTicksLeft = tag.getInt("LavaTicksLeft");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("Infusion", getInfusion());
        tag.putInt("FuseMax", getFuseMax());
        tag.putInt("FuseLeft", getFuseLeft());
        tag.putInt("Phase", getPhase());
        tag.putFloat("SpinYaw", getSpinYaw());
        if (ownerUuid != null) tag.putUUID("OwnerUUID", ownerUuid);
        tag.putInt("LavaTicksLeft", lavaTicksLeft);
    }
}

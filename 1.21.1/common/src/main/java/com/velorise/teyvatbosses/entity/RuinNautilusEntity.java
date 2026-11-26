package com.velorise.teyvatbosses.entity;

import com.velorise.teyvatbosses.compat.StavesElementBridge;
import com.velorise.teyvatbosses.registry.TeyvatBossesSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * Hydro Underwater Boss Counterpart to {@link RuinGuardEntity}:
 * <b>Ruin Nautilus</b> — an ancient Khaenri'ah deep-sea combat submersible guarding ocean trenches.
 *
 * <p>Features 12 synced underwater combat animations, 6-torpedo homing salvos, hydro-jet ramming,
 * sonar shockwaves, whirlpool vortex pull, sweeping abyssal hydro beam, and a core-overheat stun phase.
 * Defeating it spawns a {@link TrounceBlossomEntity} dropping modular submarine salvage parts.
 */
public class RuinNautilusEntity extends Mob {

    public static final int STATE_DORMANT = 0;
    public static final int STATE_AWAKEN  = 1;
    public static final int STATE_COMBAT  = 2;
    public static final int STATE_STUNNED = 3;
    public static final int STATE_DEATH   = 4;

    public static final int ANIM_DORMANT    = 0;
    public static final int ANIM_AWAKEN     = 1;
    public static final int ANIM_IDLE       = 2;
    public static final int ANIM_SWIM       = 3;
    public static final int ANIM_TORPEDO    = 4;
    public static final int ANIM_JET_RAM    = 5;
    public static final int ANIM_SONAR      = 6;
    public static final int ANIM_WHIRLPOOL  = 7;
    public static final int ANIM_BEAM       = 8;
    public static final int ANIM_STUNNED    = 9;
    public static final int ANIM_RECOVER    = 10;
    public static final int ANIM_DEATH      = 11;

    private static final EntityDataAccessor<Integer> DATA_STATE =
            SynchedEntityData.defineId(RuinNautilusEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_ANIM_ID =
            SynchedEntityData.defineId(RuinNautilusEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_ANIM_EPOCH =
            SynchedEntityData.defineId(RuinNautilusEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_TORPEDO_FIRED_MASK =
            SynchedEntityData.defineId(RuinNautilusEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DATA_HULL_PITCH =
            SynchedEntityData.defineId(RuinNautilusEntity.class, EntityDataSerializers.FLOAT);

    private final ServerBossEvent bossEvent = new ServerBossEvent(
            Component.translatable("entity.velorise_teyvat_bosses.ruin_nautilus"),
            BossEvent.BossBarColor.BLUE,
            BossEvent.BossBarOverlay.NOTCHED_10
    );

    private int serverAnimTick = 0;
    private int clientAnimTick = 0;
    private int lastSyncedAnimId = -1;
    private int lastSyncedEpoch = -1;
    private int attackCooldown = 36;
    private int attackCycle = 0;
    private int eyeHitsAccumulated = 0;
    private boolean blossomSpawned = false;
    private Vec3 ramDir = Vec3.ZERO;

    public RuinNautilusEntity(EntityType<? extends Mob> type, Level level) {
        super(type, level);
        this.xpReward = 140;
        this.setPersistenceRequired();
        this.setNoGravity(true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 460.0D)
                .add(Attributes.ARMOR, 10.0D)
                .add(Attributes.ARMOR_TOUGHNESS, 4.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.32D)
                .add(Attributes.FOLLOW_RANGE, 44.0D);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_STATE, STATE_DORMANT);
        builder.define(DATA_ANIM_ID, ANIM_DORMANT);
        builder.define(DATA_ANIM_EPOCH, 0);
        builder.define(DATA_TORPEDO_FIRED_MASK, 0);
        builder.define(DATA_HULL_PITCH, 0.0F);
    }

    public int getBossState() { return entityData.get(DATA_STATE); }
    public int getAnimId() { return entityData.get(DATA_ANIM_ID); }
    public int getAnimEpoch() { return entityData.get(DATA_ANIM_EPOCH); }
    public int getTorpedoFiredMask() { return entityData.get(DATA_TORPEDO_FIRED_MASK); }
    public float getHullPitchDeg() { return entityData.get(DATA_HULL_PITCH); }
    public int getAnimTick() { return level().isClientSide() ? clientAnimTick : serverAnimTick; }

    public String getCurrentClipName() {
        return switch (getAnimId()) {
            case ANIM_DORMANT   -> "dormant_sunken";
            case ANIM_AWAKEN    -> "awaken_surface";
            case ANIM_SWIM      -> "swim_patrol";
            case ANIM_TORPEDO   -> "torpedo_salvo";
            case ANIM_JET_RAM   -> "hydro_jet_ram";
            case ANIM_SONAR     -> "sonar_shockwave";
            case ANIM_WHIRLPOOL -> "whirlpool_vortex";
            case ANIM_BEAM      -> "abyssal_beam";
            case ANIM_STUNNED   -> "core_overheat_stun";
            case ANIM_RECOVER   -> "recover_repressurize";
            case ANIM_DEATH     -> "death_sink";
            default             -> "idle_hover";
        };
    }

    private void setAnim(int animId) {
        entityData.set(DATA_ANIM_ID, animId);
        entityData.set(DATA_ANIM_EPOCH, entityData.get(DATA_ANIM_EPOCH) + 1);
        serverAnimTick = 0;
    }

    private void transitionTo(int state, int animId) {
        entityData.set(DATA_STATE, state);
        setAnim(animId);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (level().isClientSide() && (DATA_ANIM_EPOCH.equals(key) || DATA_ANIM_ID.equals(key))) {
            lastSyncedAnimId = getAnimId();
            lastSyncedEpoch = getAnimEpoch();
            clientAnimTick = 0;
        }
    }

    @Override protected void registerGoals() {}
    @Override public boolean removeWhenFarAway(double dist) { return false; }
    @Override public boolean isPushable() { return false; }
    @Override public boolean isPushedByFluid() { return false; }
    @Override public void push(Entity e) {}
    @Override protected void doPush(Entity e) {}
    @Override public void push(double x, double y, double z) {}
    @Override public void knockback(double s, double x, double z) {}
    @Override protected int decreaseAirSupply(int currentAir) { return currentAir; }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        if (getBossState() != STATE_DORMANT && getBossState() != STATE_DEATH) {
            bossEvent.addPlayer(player);
        }
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        bossEvent.removePlayer(player);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypes.DROWN) || source.is(DamageTypes.IN_WALL) || source.is(DamageTypes.CRAMMING) || source.is(DamageTypes.FALL)) {
            return false;
        }
        int st = getBossState();
        if (st == STATE_DEATH || st == STATE_AWAKEN) return false;
        if (st == STATE_DORMANT) {
            if (source.getEntity() instanceof Player) {
                startAwaken();
            }
            return false;
        }
        // Stunned overheat takes 1.5x damage
        float finalAmt = (st == STATE_STUNNED) ? amount * 1.5F : amount;
        // Projectile hit to eye-core while charging beam or attacking can stun
        if (st == STATE_COMBAT && !source.isDirect()) {
            eyeHitsAccumulated++;
            if (eyeHitsAccumulated >= 3 || (getAnimId() == ANIM_BEAM && serverAnimTick < 24)) {
                eyeHitsAccumulated = 0;
                transitionTo(STATE_STUNNED, ANIM_STUNNED);
                playSfx(TeyvatBossesSounds.subBallastHiss(), SoundEvents.GUARDIAN_HURT, 1.4F, 0.72F);
            }
        }
        return super.hurt(source, finalAmt);
    }

    @Override
    public void die(DamageSource cause) {
        if (getBossState() != STATE_DEATH) {
            setHealth(1.0F);
            transitionTo(STATE_DEATH, ANIM_DEATH);
            bossEvent.removeAllPlayers();
            playSfx(TeyvatBossesSounds.subBallastHiss(), SoundEvents.ELDER_GUARDIAN_DEATH, 1.5F, 0.65F);
            return;
        }
        super.die(cause);
    }

    private void startAwaken() {
        transitionTo(STATE_AWAKEN, ANIM_AWAKEN);
        playSfx(TeyvatBossesSounds.subBallastHiss(), SoundEvents.BUBBLE_COLUMN_UPWARDS_INSIDE, 1.5F, 0.80F);
        if (level() instanceof ServerLevel sl) {
            for (ServerPlayer sp : sl.players()) {
                if (sp.distanceToSqr(this) <= 48.0D * 48.0D) bossEvent.addPlayer(sp);
            }
        }
    }

    private void playSfx(SoundEvent primary, SoundEvent fallback, float vol, float pitch) {
        SoundEvent ev = primary != null ? primary : fallback;
        level().playSound(null, getX(), getY() + 1.5D, getZ(), ev, SoundSource.HOSTILE, vol, pitch);
    }

    @Override
    public void tick() {
        super.tick();
        setAirSupply(getMaxAirSupply());

        if (level().isClientSide()) {
            int curId = getAnimId();
            int curEp = getAnimEpoch();
            if (curId != lastSyncedAnimId || curEp != lastSyncedEpoch) {
                lastSyncedAnimId = curId;
                lastSyncedEpoch = curEp;
                clientAnimTick = 0;
            } else {
                clientAnimTick++;
            }
            return;
        }

        ServerLevel sl = (ServerLevel) level();
        serverAnimTick++;
        int st = getBossState();
        int anim = getAnimId();
        int t = serverAnimTick;

        bossEvent.setProgress(Mth.clamp(getHealth() / getMaxHealth(), 0.0F, 1.0F));

        if (st == STATE_DEATH) {
            setDeltaMovement(0.0D, -0.04D, 0.0D);
            if (t % 4 == 0) {
                sl.sendParticles(ParticleTypes.BUBBLE, getX(), getY() + 1.5D, getZ(), 10, 1.4D, 0.8D, 1.4D, 0.08D);
            }
            if (t >= 70) {
                if (!blossomSpawned) {
                    blossomSpawned = true;
                    TrounceBlossomEntity.spawn(sl, BossRespawnAnchorEntity.BOSS_RUIN_NAUTILUS, position().add(0.0D, 0.8D, 0.0D), getYRot());
                }
                discard();
            }
            return;
        }

        if (st == STATE_DORMANT) {
            setDeltaMovement(Vec3.ZERO);
            Player near = sl.getNearestPlayer(this, 16.0D);
            if (near != null && !near.isSpectator() && !near.isCreative()) {
                startAwaken();
            }
            return;
        }

        if (st == STATE_AWAKEN) {
            setDeltaMovement(0.0D, t < 35 ? 0.055D : 0.0D, 0.0D);
            if (t % 3 == 0) {
                sl.sendParticles(ParticleTypes.BUBBLE, getX(), getY() + 0.5D, getZ(), 8, 1.6D, 0.5D, 1.6D, 0.06D);
            }
            if (t >= 56) {
                transitionTo(STATE_COMBAT, ANIM_IDLE);
                attackCooldown = 28;
            }
            return;
        }

        if (st == STATE_STUNNED) {
            setDeltaMovement(getDeltaMovement().scale(0.85D));
            if (anim == ANIM_STUNNED) {
                if (t % 6 == 0) {
                    sl.sendParticles(ParticleTypes.SMOKE, getX(), getY() + 1.8D, getZ(), 6, 1.0D, 0.5D, 1.0D, 0.04D);
                }
                if (t >= 100) {
                    setAnim(ANIM_RECOVER);
                    playSfx(TeyvatBossesSounds.subBallastHiss(), SoundEvents.IRON_DOOR_CLOSE, 1.2F, 0.85F);
                }
            } else if (anim == ANIM_RECOVER && t >= 34) {
                transitionTo(STATE_COMBAT, ANIM_IDLE);
                attackCooldown = 30;
            }
            return;
        }

        // STATE_COMBAT
        Player target = findTarget(sl, 40.0D);
        if (target == null) {
            setDeltaMovement(getDeltaMovement().scale(0.88D));
            if (anim != ANIM_IDLE) setAnim(ANIM_IDLE);
            return;
        }

        // Sync BossBar to nearby players
        if (tickCount % 20 == 0) {
            for (ServerPlayer sp : sl.players()) {
                if (sp.distanceToSqr(this) <= 44.0D * 44.0D) bossEvent.addPlayer(sp);
                else bossEvent.removePlayer(sp);
            }
        }

        // Smooth 3D tracking (yaw + pitch) except during locked ram/beam
        Vec3 toTarget = target.position().add(0.0D, target.getBbHeight() * 0.5D, 0.0D).subtract(position().add(0.0D, 1.6D, 0.0D));
        double dist = toTarget.length();
        if (anim != ANIM_JET_RAM && anim != ANIM_BEAM) {
            float desiredYaw = (float) (Mth.atan2(toTarget.z, toTarget.x) * Mth.RAD_TO_DEG) - 90.0F;
            float nextYaw = getYRot() + Mth.clamp(Mth.wrapDegrees(desiredYaw - getYRot()), -6.5F, 6.5F);
            setYRot(nextYaw);
            this.yBodyRot = nextYaw;
            this.yHeadRot = nextYaw;

            float desiredPitch = Mth.clamp((float) (-Mth.atan2(toTarget.y, Math.max(0.1D, toTarget.horizontalDistance())) * Mth.RAD_TO_DEG), -36.0F, 36.0F);
            entityData.set(DATA_HULL_PITCH, Mth.lerp(0.14F, getHullPitchDeg(), desiredPitch));
        }

        if (anim == ANIM_IDLE || anim == ANIM_SWIM) {
            // Maintain optimal underwater combat range (9..16 blocks)
            Vec3 desiredVel = Vec3.ZERO;
            if (dist > 15.0D) {
                desiredVel = toTarget.normalize().scale(0.24D);
                if (anim != ANIM_SWIM) setAnim(ANIM_SWIM);
            } else if (dist < 6.5D) {
                desiredVel = toTarget.normalize().scale(-0.14D);
                if (anim != ANIM_SWIM) setAnim(ANIM_SWIM);
            } else {
                // Gentle vertical hover alignment
                desiredVel = new Vec3(0.0D, Mth.clamp(toTarget.y * 0.08D, -0.10D, 0.10D), 0.0D);
                if (anim != ANIM_IDLE) setAnim(ANIM_IDLE);
            }
            setDeltaMovement(getDeltaMovement().lerp(desiredVel, 0.16D));

            if (tickCount % 30 == 0) {
                playSfx(TeyvatBossesSounds.subPropellerHum(), SoundEvents.BUBBLE_COLUMN_UPWARDS_AMBIENT, 0.55F, 0.90F);
            }

            if (attackCooldown > 0) {
                attackCooldown--;
            } else {
                chooseNextAttack(dist);
            }
        } else if (anim == ANIM_TORPEDO) {
            setDeltaMovement(getDeltaMovement().scale(0.88D));
            int[] fireTicks = {18, 23, 28, 33, 38, 43};
            for (int i = 0; i < fireTicks.length; i++) {
                if (t == fireTicks[i]) {
                    entityData.set(DATA_TORPEDO_FIRED_MASK, getTorpedoFiredMask() | (1 << i));
                    boolean leftSide = (i < 3);
                    float yawRad = getYRot() * Mth.DEG_TO_RAD;
                    Vec3 fwd = new Vec3(-Mth.sin(yawRad), 0.0D, Mth.cos(yawRad));
                    Vec3 right = new Vec3(-Mth.cos(yawRad), 0.0D, -Mth.sin(yawRad));
                    double sideOff = leftSide ? -1.45D : 1.45D;
                    double yOff = 1.4D + ((i % 3) - 1) * 0.35D;
                    Vec3 origin = position().add(right.scale(sideOff)).add(fwd.scale(1.1D)).add(0.0D, yOff, 0.0D);
                    Vec3 initVel = fwd.scale(0.65D).add(right.scale(sideOff * 0.12D)).add(0.0D, 0.12D, 0.0D);
                    RuinTorpedoProjectile.launchHoming(sl, this, target, origin, initVel, 16.0F, false);
                    playSfx(TeyvatBossesSounds.subTorpedoLaunch(), SoundEvents.CROSSBOW_SHOOT, 1.1F, 0.85F + i * 0.05F);
                }
            }
            if (t >= 64) {
                entityData.set(DATA_TORPEDO_FIRED_MASK, 0);
                attackCooldown = 48;
                setAnim(ANIM_IDLE);
            }
        } else if (anim == ANIM_JET_RAM) {
            if (t < 18) {
                ramDir = toTarget.normalize();
                setDeltaMovement(ramDir.scale(-0.12D));
                if (t == 1) playSfx(TeyvatBossesSounds.subPropellerHum(), SoundEvents.BUBBLE_COLUMN_WHIRLPOOL_INSIDE, 1.2F, 1.35F);
            } else if (t <= 44) {
                setDeltaMovement(ramDir.scale(0.72D));
                if (t % 2 == 0) {
                    sl.sendParticles(ParticleTypes.BUBBLE, getX(), getY() + 1.5D, getZ(), 6, 0.6D, 0.6D, 0.6D, 0.08D);
                }
                AABB hitBox = getBoundingBox().inflate(0.85D);
                for (LivingEntity e : sl.getEntitiesOfClass(LivingEntity.class, hitBox, le -> le.isAlive() && le != this && le instanceof Player)) {
                    StavesElementBridge.dealElementalDamage(e, this, "HYDRO", 26.0F, 1.0F, false, 0.25F);
                    e.push(ramDir.x * 1.4D, 0.45D, ramDir.z * 1.4D);
                }
                if (horizontalCollision) {
                    // Crashed into arena pillar -> stunned!
                    transitionTo(STATE_STUNNED, ANIM_STUNNED);
                    playSfx(TeyvatBossesSounds.mechMetalClank(), SoundEvents.STONE_BREAK, 1.4F, 0.65F);
                    return;
                }
            } else {
                setDeltaMovement(getDeltaMovement().scale(0.82D));
            }
            if (t >= 58) {
                attackCooldown = 44;
                setAnim(ANIM_IDLE);
            }
        } else if (anim == ANIM_SONAR) {
            setDeltaMovement(getDeltaMovement().scale(0.85D));
            if (t == 18 || t == 26 || t == 34) {
                playSfx(TeyvatBossesSounds.subSonarPing(), SoundEvents.WARDEN_SONIC_BOOM, 1.3F, 1.15F);
                AABB box = getBoundingBox().inflate(9.5D);
                for (LivingEntity e : sl.getEntitiesOfClass(LivingEntity.class, box, le -> le.isAlive() && le != this && le instanceof Player)) {
                    if (e.distanceToSqr(this) <= 9.5D * 9.5D) {
                        StavesElementBridge.dealElementalDamage(e, this, "HYDRO", 12.0F, 1.0F, false, 0.15F);
                        e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 1));
                        Vec3 push = e.position().subtract(position()).normalize();
                        e.push(push.x * 1.1D, 0.25D, push.z * 1.1D);
                    }
                }
                sl.sendParticles(ParticleTypes.SONIC_BOOM, getX(), getY() + 1.6D, getZ(), 1, 0, 0, 0, 0);
            }
            if (t >= 46) {
                attackCooldown = 40;
                setAnim(ANIM_IDLE);
            }
        } else if (anim == ANIM_WHIRLPOOL) {
            setDeltaMovement(Vec3.ZERO);
            if (t >= 16 && t <= 76) {
                AABB box = getBoundingBox().inflate(13.0D);
                for (Player p : sl.getEntitiesOfClass(Player.class, box, le -> le.isAlive() && !le.isSpectator() && !le.isCreative())) {
                    Vec3 pull = position().add(0.0D, 1.5D, 0.0D).subtract(p.position());
                    double d = pull.length();
                    if (d > 2.2D && d < 13.0D) {
                        p.setDeltaMovement(p.getDeltaMovement().add(pull.normalize().scale(0.085D)));
                        p.hurtMarked = true;
                    }
                    if (d <= 4.2D && t % 10 == 0) {
                        StavesElementBridge.dealElementalDamage(p, this, "HYDRO", 11.0F, 1.0F, false, 0.15F);
                    }
                }
                if (t % 3 == 0) {
                    sl.sendParticles(ParticleTypes.BUBBLE, getX(), getY() + 1.5D, getZ(), 12, 3.5D, 1.5D, 3.5D, 0.12D);
                }
            }
            if (t >= 92) {
                attackCooldown = 50;
                setAnim(ANIM_IDLE);
            }
        } else if (anim == ANIM_BEAM) {
            setDeltaMovement(getDeltaMovement().scale(0.85D));
            if (t == 24) {
                playSfx(TeyvatBossesSounds.subSonarPing(), SoundEvents.BEACON_ACTIVATE, 1.4F, 0.72F);
            }
            if (t >= 24 && t <= 58 && t % 4 == 0) {
                float prog = Mth.clamp((t - 24.0F) / 34.0F, 0.0F, 1.0F);
                float sweepYaw = (getYRot() + Mth.lerp(prog, -45.0F, 45.0F)) * Mth.DEG_TO_RAD;
                float pitchRad = getHullPitchDeg() * Mth.DEG_TO_RAD;
                Vec3 dir = new Vec3(-Mth.sin(sweepYaw) * Mth.cos(pitchRad), -Mth.sin(pitchRad), Mth.cos(sweepYaw) * Mth.cos(pitchRad)).normalize();
                Vec3 start = position().add(0.0D, 1.6D, 0.0D).add(dir.scale(2.2D));
                for (int step = 1; step <= 18; step++) {
                    Vec3 pt = start.add(dir.scale(step));
                    if (step % 2 == 0) {
                        sl.sendParticles(ParticleTypes.BUBBLE_POP, pt.x, pt.y, pt.z, 2, 0.15D, 0.15D, 0.15D, 0.02D);
                    }
                    for (Player p : sl.getEntitiesOfClass(Player.class, new AABB(pt, pt).inflate(1.3D), le -> le.isAlive() && !le.isSpectator())) {
                        StavesElementBridge.dealElementalDamage(p, this, "HYDRO", 14.0F, 1.0F, false, 0.20F);
                    }
                }
            }
            if (t >= 74) {
                attackCooldown = 52;
                setAnim(ANIM_IDLE);
            }
        }
    }

    private void chooseNextAttack(double dist) {
        if (dist <= 7.0D) {
            setAnim(ANIM_SONAR);
            return;
        }
        int pick = attackCycle++ % 4;
        switch (pick) {
            case 0 -> { entityData.set(DATA_TORPEDO_FIRED_MASK, 0); setAnim(ANIM_TORPEDO); }
            case 1 -> setAnim(ANIM_JET_RAM);
            case 2 -> setAnim(ANIM_BEAM);
            default -> setAnim(ANIM_WHIRLPOOL);
        }
    }

    private Player findTarget(ServerLevel sl, double range) {
        Player best = null;
        double bestSq = range * range;
        for (Player p : sl.players()) {
            if (!p.isAlive() || p.isSpectator() || p.isCreative()) continue;
            double d = p.distanceToSqr(this);
            if (d < bestSq) {
                bestSq = d;
                best = p;
            }
        }
        return best;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("BossState", getBossState());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("BossState")) {
            int st = tag.getInt("BossState");
            entityData.set(DATA_STATE, st);
            if (st == STATE_DORMANT) entityData.set(DATA_ANIM_ID, ANIM_DORMANT);
        }
    }
}

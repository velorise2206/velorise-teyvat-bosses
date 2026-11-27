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
import net.minecraft.tags.FluidTags;
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * AAA 2-Phase Underwater Boss: <b>Ruin Nautilus — Abyssal Dreadnought</b>.
 *
 * <p>Inspired by L_Ender's Cataclysm (The Leviathan) and Ace Combat 7 (Alicorn):
 * <ul>
 *   <li><b>Anti-cheese underwater combat</b>: 80% damage reduction out of water, 60% reduction beyond 36 blocks,
 *       28-damage per-hit cap, and fair skill-based stun interrupts (no infinite bow stun-lock).</li>
 *   <li><b>Phase 1 (100%-50% HP — Hunter-Killer)</b>: Distance-reactive Sonar Shockwave, Fin-Blade Sweep,
 *       Interruptible Hydro-Jet Ram, 6-Torpedo Salvo, Floating Acoustic Depth Mines, Harpoon Tether Pull,
 *       and Sweeping Abyssal Eye Beam.</li>
 *   <li><b>Phase 2 (&lt;50% HP — Abyssal Dreadnought Overdrive)</b>: Uninterruptible 4s transformation deploying
 *       the hidden <b>Dorsal Hydro-Railgun</b>, unlocking Dorsal Railgun Overcharge (with 3.5s thermal vent
 *       overheat punish window), Sonar Blackout Breach Ambush, and Trench Whirlpool.</li>
 * </ul>
 */
public class RuinNautilusEntity extends Mob {

    public static final int STATE_DORMANT    = 0;
    public static final int STATE_AWAKEN     = 1;
    public static final int STATE_COMBAT     = 2;
    public static final int STATE_STUNNED    = 3;
    public static final int STATE_DEATH      = 4;
    public static final int STATE_PHASE2_CIN = 5;

    public static final int ANIM_DORMANT      = 0;
    public static final int ANIM_AWAKEN       = 1;
    public static final int ANIM_IDLE         = 2;
    public static final int ANIM_SWIM         = 3;
    public static final int ANIM_TORPEDO      = 4;
    public static final int ANIM_JET_RAM      = 5;
    public static final int ANIM_SONAR        = 6;
    public static final int ANIM_WHIRLPOOL    = 7;
    public static final int ANIM_BEAM         = 8;
    public static final int ANIM_STUNNED      = 9;
    public static final int ANIM_RECOVER      = 10;
    public static final int ANIM_DEATH        = 11;
    public static final int ANIM_FIN_SWEEP    = 12;
    public static final int ANIM_HARPOON      = 13;
    public static final int ANIM_DEPTH_MINES  = 14;
    public static final int ANIM_PHASE2_TRANS = 15;
    public static final int ANIM_RAILGUN      = 16;
    public static final int ANIM_SONAR_BREACH = 17;

    private static final float HIT_DAMAGE_CAP = 28.0F;

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
    private static final EntityDataAccessor<Boolean> DATA_PHASE2 =
            SynchedEntityData.defineId(RuinNautilusEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> DATA_RAILGUN_YAW =
            SynchedEntityData.defineId(RuinNautilusEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_RAILGUN_PITCH =
            SynchedEntityData.defineId(RuinNautilusEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<String> DATA_MINES_SYNC =
            SynchedEntityData.defineId(RuinNautilusEntity.class, EntityDataSerializers.STRING);

    private static final class DepthMine {
        Vec3 pos;
        int age;
        final int maxAge;
        DepthMine(Vec3 pos, int maxAge) { this.pos = pos; this.maxAge = maxAge; }
    }

    private final ServerBossEvent bossEvent = new ServerBossEvent(
            Component.translatable("entity.velorise_teyvat_bosses.ruin_nautilus"),
            BossEvent.BossBarColor.BLUE,
            BossEvent.BossBarOverlay.NOTCHED_10
    );

    private final List<DepthMine> activeMines = new ArrayList<>();
    private int serverAnimTick = 0;
    private int clientAnimTick = 0;
    private int lastSyncedAnimId = -1;
    private int lastSyncedEpoch = -1;
    private int attackCooldown = 34;
    private int lastAttackId = -1;
    private float overheatDamageAccum = 0.0F;
    private final BossDamageCap damageCap = new BossDamageCap();
    private static final double ARENA_SOFT_LEASH_RADIUS = 64.0D;
    private Vec3 arenaCenter = Vec3.ZERO;
    private boolean arenaInit = false;
    private boolean blossomSpawned = false;
    private Vec3 ramDir = Vec3.ZERO;

    public RuinNautilusEntity(EntityType<? extends Mob> type, Level level) {
        super(type, level);
        this.xpReward = 180;
        this.setPersistenceRequired();
        this.setNoGravity(true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 520.0D)
                .add(Attributes.ARMOR, 12.0D)
                .add(Attributes.ARMOR_TOUGHNESS, 6.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.34D)
                .add(Attributes.FOLLOW_RANGE, 48.0D);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_STATE, STATE_DORMANT);
        builder.define(DATA_ANIM_ID, ANIM_DORMANT);
        builder.define(DATA_ANIM_EPOCH, 0);
        builder.define(DATA_TORPEDO_FIRED_MASK, 0);
        builder.define(DATA_HULL_PITCH, 0.0F);
        builder.define(DATA_PHASE2, false);
        builder.define(DATA_RAILGUN_YAW, 0.0F);
        builder.define(DATA_RAILGUN_PITCH, 0.0F);
        builder.define(DATA_MINES_SYNC, "");
    }

    public int getBossState() { return entityData.get(DATA_STATE); }
    public int getAnimId() { return entityData.get(DATA_ANIM_ID); }
    public int getAnimEpoch() { return entityData.get(DATA_ANIM_EPOCH); }
    public int getTorpedoFiredMask() { return entityData.get(DATA_TORPEDO_FIRED_MASK); }
    public float getHullPitchDeg() { return entityData.get(DATA_HULL_PITCH); }
    public boolean isPhase2() { return entityData.get(DATA_PHASE2); }
    public float getRailgunYawDeg() { return entityData.get(DATA_RAILGUN_YAW); }
    public float getRailgunPitchDeg() { return entityData.get(DATA_RAILGUN_PITCH); }
    public String getSyncedMinesData() { return entityData.get(DATA_MINES_SYNC); }
    public int getAnimTick() { return level().isClientSide() ? clientAnimTick : serverAnimTick; }

    public String getCurrentClipName() {
        return switch (getAnimId()) {
            case ANIM_DORMANT      -> "dormant_sunken";
            case ANIM_AWAKEN       -> "awaken_surface";
            case ANIM_SWIM         -> "swim_patrol";
            case ANIM_TORPEDO      -> "torpedo_salvo";
            case ANIM_JET_RAM      -> "hydro_jet_ram";
            case ANIM_SONAR        -> "sonar_shockwave";
            case ANIM_WHIRLPOOL    -> "whirlpool_vortex";
            case ANIM_BEAM         -> "abyssal_beam";
            case ANIM_STUNNED      -> "core_overheat_stun";
            case ANIM_RECOVER      -> "recover_repressurize";
            case ANIM_DEATH        -> "death_sink";
            case ANIM_FIN_SWEEP    -> "fin_blade_sweep";
            case ANIM_HARPOON      -> "harpoon_tether";
            case ANIM_DEPTH_MINES  -> "depth_mine_barrage";
            case ANIM_PHASE2_TRANS -> "phase2_overdrive";
            case ANIM_RAILGUN      -> "dorsal_railgun";
            case ANIM_SONAR_BREACH -> "sonar_breach";
            default                -> "idle_hover";
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

    private boolean isSubmergedOrWet() {
        return isInWater() || isUnderWater() || level().getFluidState(blockPosition()).is(FluidTags.WATER);
    }

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
        if (source.is(DamageTypes.DROWN) || source.is(DamageTypes.IN_WALL)
                || source.is(DamageTypes.CRAMMING) || source.is(DamageTypes.FALL)) {
            return false;
        }
        int st = getBossState();
        if (st == STATE_DEATH || st == STATE_AWAKEN || st == STATE_PHASE2_CIN) return false;
        if (st == STATE_DORMANT) {
            if (source.getEntity() instanceof Player) startAwaken();
            return false;
        }

        float dmg = Math.min(amount, HIT_DAMAGE_CAP);

        // Anti-cheese: 80% armor resistance out of water, 60% reduction from >36 blocks away
        if (!isSubmergedOrWet()) {
            dmg *= 0.20F;
        }
        Entity attacker = source.getEntity();
        if (attacker != null && attacker.distanceToSqr(this) > 36.0D * 36.0D) {
            dmg *= 0.40F;
        }

        // Stunned overheat takes 1.5x damage
        if (st == STATE_STUNNED) {
            dmg *= 1.5F;
        }

        // Fair skill-based interrupts (replaces broken 3-arrow bow stun-lock!):
        // 1. Interrupting Hydro-Jet Ram during its telegraph windup (t=4..20)
        if (st == STATE_COMBAT && getAnimId() == ANIM_JET_RAM && serverAnimTick >= 4 && serverAnimTick <= 20 && dmg >= 8.0F) {
            triggerOverheatStun();
        }
        // 2. Punishing Thermal Vent Overheat window right after Dorsal Railgun fires (t=64..108)
        if (st == STATE_COMBAT && getAnimId() == ANIM_RAILGUN && serverAnimTick >= 64) {
            overheatDamageAccum += dmg;
            if (overheatDamageAccum >= 32.0F) {
                overheatDamageAccum = 0.0F;
                triggerOverheatStun();
            }
        }

        dmg = damageCap.apply(this, source, dmg, st == STATE_STUNNED);
        if (dmg <= 0.0F) return false;
        boolean ok = super.hurt(source, dmg);

        // Trigger Phase 2 Overdrive transformation at <= 50% HP
        if (ok && !isPhase2() && getBossState() == STATE_COMBAT && getHealth() <= getMaxHealth() * 0.5F && getHealth() > 1.0F) {
            startPhase2Transformation();
        }
        return ok;
    }

    private void triggerOverheatStun() {
        transitionTo(STATE_STUNNED, ANIM_STUNNED);
        playSfx(TeyvatBossesSounds.subBallastHiss(), SoundEvents.GUARDIAN_HURT, 1.5F, 0.70F);
    }

    private void startPhase2Transformation() {
        entityData.set(DATA_PHASE2, true);
        transitionTo(STATE_PHASE2_CIN, ANIM_PHASE2_TRANS);
        bossEvent.setColor(BossEvent.BossBarColor.PURPLE);
        playSfx(TeyvatBossesSounds.subSonarPing(), SoundEvents.ELDER_GUARDIAN_CURSE, 1.6F, 0.72F);
        playSfx(TeyvatBossesSounds.subBallastHiss(), SoundEvents.BEACON_ACTIVATE, 1.5F, 0.85F);
    }

    @Override
    public void die(DamageSource cause) {
        if (getBossState() != STATE_DEATH) {
            setHealth(1.0F);
            activeMines.clear();
            syncMinesToClients();
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
        if (!arenaInit) {
            arenaCenter = position();
            arenaInit = true;
        } else if (isAlive()) {
            double hDist = Math.hypot(getX() - arenaCenter.x, getZ() - arenaCenter.z);
            if (hDist > ARENA_SOFT_LEASH_RADIUS) {
                Vec3 back = arenaCenter.subtract(position());
                if (hDist > ARENA_SOFT_LEASH_RADIUS + 24.0D) {
                    teleportTo(arenaCenter.x, arenaCenter.y, arenaCenter.z);
                    setDeltaMovement(Vec3.ZERO);
                } else if (back.lengthSqr() > 1.0E-4D) {
                    setDeltaMovement(getDeltaMovement().scale(0.6D).add(back.normalize().scale(0.32D)));
                }
            }
        }
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
        tickDepthMines(sl);

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
            Player near = sl.getNearestPlayer(this, 18.0D);
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
                attackCooldown = 26;
            }
            return;
        }

        if (st == STATE_PHASE2_CIN) {
            setDeltaMovement(getDeltaMovement().scale(0.85D));
            if (t % 4 == 0) {
                sl.sendParticles(ParticleTypes.FLAME, getX(), getY() + 2.4D, getZ(), 8, 1.4D, 0.6D, 1.4D, 0.05D);
                sl.sendParticles(ParticleTypes.BUBBLE_POP, getX(), getY() + 2.0D, getZ(), 10, 2.0D, 1.0D, 2.0D, 0.08D);
            }
            if (t == 56) {
                // Deploy a protective ring of 6 floating Acoustic Depth Mines
                spawnMineRing(6, 7.5D);
                playSfx(TeyvatBossesSounds.subSonarPing(), SoundEvents.WARDEN_SONIC_BOOM, 1.5F, 0.90F);
            }
            if (t >= 80) {
                transitionTo(STATE_COMBAT, ANIM_IDLE);
                attackCooldown = 22;
            }
            return;
        }

        if (st == STATE_STUNNED) {
            setDeltaMovement(getDeltaMovement().scale(0.85D));
            if (anim == ANIM_STUNNED) {
                if (t % 5 == 0) {
                    sl.sendParticles(ParticleTypes.SMOKE, getX(), getY() + 2.2D, getZ(), 8, 1.2D, 0.6D, 1.2D, 0.05D);
                    sl.sendParticles(ParticleTypes.ELECTRIC_SPARK, getX(), getY() + 2.0D, getZ(), 5, 1.0D, 0.5D, 1.0D, 0.06D);
                }
                if (t >= 90) {
                    setAnim(ANIM_RECOVER);
                    playSfx(TeyvatBossesSounds.subBallastHiss(), SoundEvents.IRON_DOOR_CLOSE, 1.2F, 0.85F);
                }
            } else if (anim == ANIM_RECOVER && t >= 34) {
                transitionTo(STATE_COMBAT, ANIM_IDLE);
                attackCooldown = 24;
            }
            return;
        }

        // STATE_COMBAT
        Player target = findTarget(sl, 46.0D);
        if (target == null) {
            setDeltaMovement(getDeltaMovement().scale(0.88D));
            if (anim != ANIM_IDLE) setAnim(ANIM_IDLE);
            return;
        }

        if (tickCount % 20 == 0) {
            for (ServerPlayer sp : sl.players()) {
                if (sp.distanceToSqr(this) <= 48.0D * 48.0D) bossEvent.addPlayer(sp);
                else bossEvent.removePlayer(sp);
            }
        }

        Vec3 toTarget = target.position().add(0.0D, target.getBbHeight() * 0.5D, 0.0D).subtract(position().add(0.0D, 1.8D, 0.0D));
        double dist = toTarget.length();
        boolean lockAim = (anim == ANIM_JET_RAM || anim == ANIM_BEAM || anim == ANIM_FIN_SWEEP
                || (anim == ANIM_RAILGUN && t >= 32));
        if (!lockAim) {
            float desiredYaw = (float) (Mth.atan2(toTarget.z, toTarget.x) * Mth.RAD_TO_DEG) - 90.0F;
            float turnRate = isPhase2() ? 8.0F : 6.5F;
            float nextYaw = getYRot() + Mth.clamp(Mth.wrapDegrees(desiredYaw - getYRot()), -turnRate, turnRate);
            setYRot(nextYaw);
            this.yBodyRot = nextYaw;
            this.yHeadRot = nextYaw;

            float desiredPitch = Mth.clamp((float) (-Mth.atan2(toTarget.y, Math.max(0.1D, toTarget.horizontalDistance())) * Mth.RAD_TO_DEG), -34.0F, 34.0F);
            entityData.set(DATA_HULL_PITCH, Mth.lerp(0.15F, getHullPitchDeg(), desiredPitch));
        }

        if (anim == ANIM_IDLE || anim == ANIM_SWIM) {
            Vec3 desiredVel;
            double strafeSign = ((tickCount / 80) % 2 == 0) ? 1.0D : -1.0D;
            Vec3 perp = new Vec3(-toTarget.z, 0.0D, toTarget.x).normalize().scale(0.14D * strafeSign);
            if (dist > 15.0D) {
                desiredVel = toTarget.normalize().scale(isPhase2() ? 0.30D : 0.25D).add(perp.scale(0.5D));
                if (anim != ANIM_SWIM) setAnim(ANIM_SWIM);
            } else if (dist < 6.5D) {
                desiredVel = toTarget.normalize().scale(-0.15D).add(perp);
                if (anim != ANIM_SWIM) setAnim(ANIM_SWIM);
            } else {
                desiredVel = perp.add(0.0D, Mth.clamp(toTarget.y * 0.09D, -0.11D, 0.11D), 0.0D);
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
            int[] fireTicks = {16, 21, 26, 31, 36, 41};
            for (int i = 0; i < fireTicks.length; i++) {
                if (t == fireTicks[i]) {
                    entityData.set(DATA_TORPEDO_FIRED_MASK, getTorpedoFiredMask() | (1 << i));
                    boolean leftSide = (i < 3);
                    float yawRad = getYRot() * Mth.DEG_TO_RAD;
                    Vec3 fwd = new Vec3(-Mth.sin(yawRad), 0.0D, Mth.cos(yawRad));
                    Vec3 right = new Vec3(-Mth.cos(yawRad), 0.0D, -Mth.sin(yawRad));
                    double sideOff = leftSide ? -1.85D : 1.85D;
                    double yOff = 1.6D + ((i % 3) - 1) * 0.4D;
                    Vec3 origin = position().add(right.scale(sideOff)).add(fwd.scale(1.4D)).add(0.0D, yOff, 0.0D);
                    Vec3 initVel = fwd.scale(0.72D).add(right.scale(sideOff * 0.14D)).add(0.0D, 0.12D, 0.0D);
                    RuinTorpedoProjectile.launchHoming(sl, this, target, origin, initVel, isPhase2() ? 18.0F : 15.0F, false);
                    playSfx(TeyvatBossesSounds.subTorpedoLaunch(), SoundEvents.CROSSBOW_SHOOT, 1.1F, 0.85F + i * 0.05F);
                }
            }
            if (t >= 62) {
                entityData.set(DATA_TORPEDO_FIRED_MASK, 0);
                finishAttack(isPhase2() ? 36 : 44);
            }
        } else if (anim == ANIM_JET_RAM) {
            if (t < 20) {
                ramDir = toTarget.normalize();
                setDeltaMovement(ramDir.scale(-0.14D));
                if (t == 1) playSfx(TeyvatBossesSounds.subPropellerHum(), SoundEvents.BUBBLE_COLUMN_WHIRLPOOL_INSIDE, 1.3F, 1.35F);
                if (t % 3 == 0) sl.sendParticles(ParticleTypes.ELECTRIC_SPARK, getX(), getY() + 1.8D, getZ(), 6, 1.2D, 0.8D, 1.2D, 0.04D);
            } else if (t <= 46) {
                setDeltaMovement(ramDir.scale(isPhase2() ? 0.88D : 0.76D));
                if (t % 2 == 0) sl.sendParticles(ParticleTypes.BUBBLE, getX(), getY() + 1.6D, getZ(), 8, 0.8D, 0.8D, 0.8D, 0.10D);
                for (Player p : sl.getEntitiesOfClass(Player.class, getBoundingBox().inflate(1.0D), le -> le.isAlive() && !le.isSpectator())) {
                    StavesElementBridge.dealElementalDamage(p, this, "HYDRO", 26.0F, 1.0F, false, 0.25F);
                    p.push(ramDir.x * 1.5D, 0.45D, ramDir.z * 1.5D);
                }
                if (horizontalCollision) {
                    triggerOverheatStun();
                    playSfx(TeyvatBossesSounds.mechMetalClank(), SoundEvents.STONE_BREAK, 1.4F, 0.65F);
                    return;
                }
            } else {
                setDeltaMovement(getDeltaMovement().scale(0.82D));
            }
            if (t >= 60) finishAttack(38);
        } else if (anim == ANIM_SONAR) {
            setDeltaMovement(getDeltaMovement().scale(0.85D));
            if (t == 18 || t == 26 || t == 34) {
                playSfx(TeyvatBossesSounds.subSonarPing(), SoundEvents.WARDEN_SONIC_BOOM, 1.3F, 1.15F);
                for (Player p : sl.getEntitiesOfClass(Player.class, getBoundingBox().inflate(10.0D), le -> le.isAlive() && !le.isSpectator())) {
                    if (p.distanceToSqr(this) <= 10.0D * 10.0D) {
                        StavesElementBridge.dealElementalDamage(p, this, "HYDRO", 12.0F, 1.0F, false, 0.15F);
                        p.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 1));
                        Vec3 push = p.position().subtract(position()).normalize();
                        p.push(push.x * 1.25D, 0.28D, push.z * 1.25D);
                    }
                }
                sl.sendParticles(ParticleTypes.SONIC_BOOM, getX(), getY() + 1.8D, getZ(), 1, 0, 0, 0, 0);
            }
            if (t >= 46) finishAttack(34);
        } else if (anim == ANIM_FIN_SWEEP) {
            setDeltaMovement(getDeltaMovement().scale(0.88D));
            if (t == 16 || t == 24 || t == 32) {
                playSfx(TeyvatBossesSounds.subBallastHiss(), SoundEvents.PLAYER_ATTACK_SWEEP, 1.3F, 0.85F);
                for (Player p : sl.getEntitiesOfClass(Player.class, getBoundingBox().inflate(7.5D), le -> le.isAlive() && !le.isSpectator())) {
                    if (p.distanceToSqr(this) <= 7.5D * 7.5D) {
                        StavesElementBridge.dealElementalDamage(p, this, "HYDRO", 16.0F, 1.0F, false, 0.20F);
                        Vec3 push = p.position().subtract(position()).normalize();
                        p.push(push.x * 1.15D, 0.45D, push.z * 1.15D);
                    }
                }
                sl.sendParticles(ParticleTypes.SWEEP_ATTACK, getX(), getY() + 1.5D, getZ(), 6, 3.0D, 0.6D, 3.0D, 0.0D);
            }
            if (t >= 44) finishAttack(30);
        } else if (anim == ANIM_HARPOON) {
            setDeltaMovement(getDeltaMovement().scale(0.85D));
            if (t == 16) {
                playSfx(TeyvatBossesSounds.subTorpedoLaunch(), SoundEvents.FISHING_BOBBER_THROW, 1.3F, 0.70F);
            }
            if (t >= 18 && t <= 40) {
                Vec3 pull = position().add(0.0D, 1.6D, 0.0D).subtract(target.position());
                double d = pull.length();
                if (d > 4.5D && d < 32.0D) {
                    target.setDeltaMovement(target.getDeltaMovement().add(pull.normalize().scale(0.18D)));
                    target.hurtMarked = true;
                }
                if (t == 22) {
                    StavesElementBridge.dealElementalDamage(target, this, "HYDRO", 10.0F, 1.0F, false, 0.15F);
                }
            }
            if (t >= 52) finishAttack(28);
        } else if (anim == ANIM_DEPTH_MINES) {
            setDeltaMovement(getDeltaMovement().scale(0.90D));
            if (t == 22) {
                spawnMineRing(isPhase2() ? 7 : 5, 6.0D);
                playSfx(TeyvatBossesSounds.subBallastHiss(), SoundEvents.DISPENSER_LAUNCH, 1.3F, 0.75F);
            }
            if (t >= 48) finishAttack(34);
        } else if (anim == ANIM_WHIRLPOOL) {
            setDeltaMovement(Vec3.ZERO);
            if (t >= 16 && t <= 74) {
                for (Player p : sl.getEntitiesOfClass(Player.class, getBoundingBox().inflate(14.0D), le -> le.isAlive() && !le.isSpectator() && !le.isCreative())) {
                    // Horizontal suction with vertical escape corridor (swimming up/down escapes pull!)
                    double dyAbs = Math.abs(p.getY() - (getY() + 1.6D));
                    Vec3 pull = position().add(0.0D, 1.6D, 0.0D).subtract(p.position());
                    double d = pull.length();
                    if (d > 2.4D && d < 14.0D && dyAbs < 5.5D) {
                        p.setDeltaMovement(p.getDeltaMovement().add(pull.normalize().scale(0.08D)));
                        p.hurtMarked = true;
                    }
                    if (d <= 4.5D && t % 10 == 0) {
                        StavesElementBridge.dealElementalDamage(p, this, "HYDRO", 12.0F, 1.0F, false, 0.15F);
                    }
                }
                if (t % 3 == 0) sl.sendParticles(ParticleTypes.BUBBLE, getX(), getY() + 1.6D, getZ(), 14, 4.0D, 1.5D, 4.0D, 0.12D);
            }
            if (t >= 88) finishAttack(42);
        } else if (anim == ANIM_BEAM) {
            setDeltaMovement(getDeltaMovement().scale(0.85D));
            if (t == 24) playSfx(TeyvatBossesSounds.subSonarPing(), SoundEvents.BEACON_ACTIVATE, 1.4F, 0.72F);
            if (t >= 24 && t <= 58 && t % 4 == 0) {
                float prog = Mth.clamp((t - 24.0F) / 34.0F, 0.0F, 1.0F);
                float sweepYaw = (getYRot() + Mth.lerp(prog, -42.0F, 42.0F)) * Mth.DEG_TO_RAD;
                float pitchRad = getHullPitchDeg() * Mth.DEG_TO_RAD;
                fireBeamDamage(sl, sweepYaw, pitchRad, 22.0D, 15.0F, 1.8D);
            }
            if (t >= 74) finishAttack(42);
        } else if (anim == ANIM_RAILGUN) {
            // Phase 2 Dorsal Hydro-Railgun: t=0..34 lock-on laser, t=36..62 piercing superbeam, t=64..108 overheat window!
            setDeltaMovement(getDeltaMovement().scale(0.85D));
            if (t < 32) {
                entityData.set(DATA_RAILGUN_YAW, getYRot());
                entityData.set(DATA_RAILGUN_PITCH, getHullPitchDeg());
                if (t == 1) {
                    overheatDamageAccum = 0.0F;
                    playSfx(TeyvatBossesSounds.subSonarPing(), SoundEvents.BEACON_POWER_SELECT, 1.5F, 1.45F);
                }
            } else if (t == 36) {
                playSfx(TeyvatBossesSounds.subSonarPing(), SoundEvents.WARDEN_SONIC_BOOM, 1.8F, 0.58F);
            }
            if (t >= 36 && t <= 62 && t % 3 == 0) {
                float rYaw = getRailgunYawDeg() * Mth.DEG_TO_RAD;
                float rPitch = getRailgunPitchDeg() * Mth.DEG_TO_RAD;
                fireBeamDamage(sl, rYaw, rPitch, 32.0D, 22.0F, 3.8D);
            }
            if (t >= 64 && t % 5 == 0) {
                sl.sendParticles(ParticleTypes.FLAME, getX(), getY() + 3.4D, getZ(), 6, 1.1D, 0.4D, 1.1D, 0.03D);
            }
            if (t >= 108) finishAttack(38);
        } else if (anim == ANIM_SONAR_BREACH) {
            if (t == 1) {
                playSfx(TeyvatBossesSounds.subSonarPing(), SoundEvents.WARDEN_SONIC_BOOM, 1.5F, 0.65F);
                for (Player p : sl.getEntitiesOfClass(Player.class, getBoundingBox().inflate(28.0D), le -> le.isAlive() && !le.isSpectator())) {
                    p.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 45, 0));
                }
            }
            if (t < 24) {
                setDeltaMovement(0.0D, -0.35D, 0.0D);
            } else if (t == 24) {
                ramDir = toTarget.normalize();
                playSfx(TeyvatBossesSounds.subBallastHiss(), SoundEvents.DOLPHIN_SPLASH, 1.5F, 0.70F);
            } else if (t <= 54) {
                setDeltaMovement(ramDir.scale(0.92D));
                if (t % 2 == 0) sl.sendParticles(ParticleTypes.BUBBLE, getX(), getY() + 1.8D, getZ(), 10, 1.0D, 1.0D, 1.0D, 0.12D);
                for (Player p : sl.getEntitiesOfClass(Player.class, getBoundingBox().inflate(1.2D), le -> le.isAlive() && !le.isSpectator())) {
                    StavesElementBridge.dealElementalDamage(p, this, "HYDRO", 24.0F, 1.0F, false, 0.25F);
                    p.push(ramDir.x * 1.4D, 0.65D, ramDir.z * 1.4D);
                }
            } else {
                setDeltaMovement(getDeltaMovement().scale(0.82D));
            }
            if (t >= 68) finishAttack(36);
        }
    }

    private void fireBeamDamage(ServerLevel sl, float yawRad, float pitchRad, double range, float dmg, double yOffset) {
        Vec3 dir = new Vec3(-Mth.sin(yawRad) * Mth.cos(pitchRad), -Mth.sin(pitchRad), Mth.cos(yawRad) * Mth.cos(pitchRad)).normalize();
        Vec3 start = position().add(0.0D, yOffset, 0.0D).add(dir.scale(3.0D));
        for (int step = 1; step <= (int) range; step++) {
            Vec3 pt = start.add(dir.scale(step));
            if (step % 2 == 0) {
                sl.sendParticles(ParticleTypes.BUBBLE_POP, pt.x, pt.y, pt.z, 2, 0.18D, 0.18D, 0.18D, 0.02D);
            }
            for (Player p : sl.getEntitiesOfClass(Player.class, new AABB(pt, pt).inflate(1.45D), le -> le.isAlive() && !le.isSpectator())) {
                StavesElementBridge.dealElementalDamage(p, this, "HYDRO", dmg, 1.0F, false, 0.20F);
            }
        }
    }

    private void spawnMineRing(int count, double radius) {
        for (int i = 0; i < count; i++) {
            double ang = (Math.PI * 2.0D * i) / count;
            double dy = (random.nextDouble() - 0.5D) * 3.0D;
            activeMines.add(new DepthMine(position().add(Math.cos(ang) * radius, 1.8D + dy, Math.sin(ang) * radius), 220));
        }
        syncMinesToClients();
    }

    private void tickDepthMines(ServerLevel sl) {
        Iterator<DepthMine> it = activeMines.iterator();
        while (it.hasNext()) {
            DepthMine m = it.next();
            m.age++;
            if (m.age % 4 == 0) {
                sl.sendParticles(ParticleTypes.GLOW, m.pos.x, m.pos.y, m.pos.z, 2, 0.15D, 0.15D, 0.15D, 0.01D);
                sl.sendParticles(ParticleTypes.BUBBLE, m.pos.x, m.pos.y, m.pos.z, 1, 0.1D, 0.1D, 0.1D, 0.02D);
            }
            boolean triggered = false;
            if (m.age >= 24) {
                for (Player p : sl.getEntitiesOfClass(Player.class, new AABB(m.pos, m.pos).inflate(2.4D), le -> le.isAlive() && !le.isSpectator())) {
                    StavesElementBridge.dealElementalDamage(p, this, "HYDRO", 16.0F, 1.0F, false, 0.25F);
                    p.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 80, 1));
                    triggered = true;
                }
            }
            if (triggered || m.age >= m.maxAge) {
                if (triggered) {
                    sl.sendParticles(ParticleTypes.EXPLOSION, m.pos.x, m.pos.y, m.pos.z, 1, 0, 0, 0, 0);
                    sl.playSound(null, m.pos.x, m.pos.y, m.pos.z, SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 0.9F, 1.15F);
                }
                it.remove();
                syncMinesToClients();
            }
        }
    }

    private void syncMinesToClients() {
        if (activeMines.isEmpty()) { entityData.set(DATA_MINES_SYNC, ""); return; }
        StringBuilder sb = new StringBuilder();
        for (DepthMine m : activeMines) {
            if (sb.length() > 0) sb.append(";");
            sb.append(String.format(java.util.Locale.ROOT, "%.2f,%.2f,%.2f", m.pos.x, m.pos.y, m.pos.z));
        }
        entityData.set(DATA_MINES_SYNC, sb.toString());
    }

    private void finishAttack(int cd) {
        attackCooldown = cd;
        setAnim(ANIM_IDLE);
    }

    private void chooseNextAttack(double dist) {
        int next;
        if (dist < 7.5D) {
            next = (lastAttackId == ANIM_SONAR) ? ANIM_FIN_SWEEP : ANIM_SONAR;
        } else if (dist > 20.0D) {
            next = (lastAttackId == ANIM_HARPOON) ? ANIM_TORPEDO : ANIM_HARPOON;
        } else if (isPhase2()) {
            int[] pool = {ANIM_RAILGUN, ANIM_SONAR_BREACH, ANIM_TORPEDO, ANIM_WHIRLPOOL, ANIM_JET_RAM, ANIM_DEPTH_MINES};
            next = pickNonRepeat(pool);
        } else {
            int[] pool = {ANIM_TORPEDO, ANIM_JET_RAM, ANIM_BEAM, ANIM_DEPTH_MINES, ANIM_WHIRLPOOL};
            next = pickNonRepeat(pool);
        }
        lastAttackId = next;
        if (next == ANIM_TORPEDO) entityData.set(DATA_TORPEDO_FIRED_MASK, 0);
        setAnim(next);
    }

    private int pickNonRepeat(int[] pool) {
        int pick = pool[random.nextInt(pool.length)];
        if (pick == lastAttackId) pick = pool[(random.nextInt(pool.length - 1) + 1) % pool.length];
        return pick;
    }

    private Player findTarget(ServerLevel sl, double range) {
        Player best = null;
        double bestSq = range * range;
        for (Player p : sl.players()) {
            if (!p.isAlive() || p.isSpectator() || p.isCreative()) continue;
            double d = p.distanceToSqr(this);
            if (d < bestSq) { bestSq = d; best = p; }
        }
        return best;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("ArenaInit", arenaInit);
        tag.putDouble("ArenaX", arenaCenter.x);
        tag.putDouble("ArenaY", arenaCenter.y);
        tag.putDouble("ArenaZ", arenaCenter.z);
        tag.putInt("BossState", getBossState());
        tag.putBoolean("Phase2", isPhase2());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.getBoolean("ArenaInit")) {
            arenaInit = true;
            arenaCenter = new Vec3(tag.getDouble("ArenaX"), tag.getDouble("ArenaY"), tag.getDouble("ArenaZ"));
        }
        if (tag.contains("BossState")) {
            int st = tag.getInt("BossState");
            entityData.set(DATA_STATE, st);
            if (st == STATE_DORMANT) entityData.set(DATA_ANIM_ID, ANIM_DORMANT);
        }
        if (tag.contains("Phase2")) {
            boolean p2 = tag.getBoolean("Phase2");
            entityData.set(DATA_PHASE2, p2);
            if (p2) bossEvent.setColor(BossEvent.BossBarColor.PURPLE);
        }
    }
}

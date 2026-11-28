package com.velorise.teyvatbosses.entity;

import com.velorise.teyvatbosses.compat.StavesElementBridge;
import com.velorise.teyvatbosses.config.TeyvatBossesConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * Boss #6: Goldflame Qucusaur Tyrant (Holawaqa Ngoubou) — pure Pyro flying wyvern World Boss.
 * <ul>
 *   <li>680 base HP, 70% Pyro RES, 3-layer {@link BossDamageCap}, and custom Goldflame BossBar.</li>
 *   <li>Ground phase: 3-hit {@code wing_swipe} combo, {@code shockwave_slam}, {@code charge_drill}, {@code breath_sweep}.</li>
 *   <li>Golden Flame aerial phase (24 GU Pyro Shield): {@code takeoff_roar}, {@code feather_barrage}
 *       (spawning breakable {@link FlamewindFeatherEntity}), and {@code dive_bomb} (100% intact / 65% broken).</li>
 *   <li>Breaking the Golden Flame shield triggers {@code crash_stun} (6s vulnerable window).</li>
 * </ul>
 */
public class GoldflameQucusaurEntity extends Monster {
    public static final String BOSS_ID = "goldflame_qucusaur";

    public static final int STATE_IDLE_GROUND       = 0;
    public static final int STATE_WALK              = 1;
    public static final int STATE_WING_SWIPE        = 2;
    public static final int STATE_SHOCKWAVE_SLAM    = 3;
    public static final int STATE_CHARGE_DRILL      = 4;
    public static final int STATE_BREATH_SWEEP      = 5;
    public static final int STATE_TAKEOFF_ROAR      = 6;
    public static final int STATE_GOLDEN_FLAME_HOVER = 7;
    public static final int STATE_FEATHER_BARRAGE   = 8;
    public static final int STATE_DIVE_BOMB         = 9;
    public static final int STATE_CRASH_STUN        = 10;
    public static final int STATE_DEATH_COLLAPSE    = 11;
    public static final int STATE_DORMANT           = 12;
    public static final int STATE_WAKE_UP           = 13;
    public static final int STATE_RETURN_FLIGHT     = 14;

    private static final EntityDataAccessor<Integer> DATA_STATE =
            SynchedEntityData.defineId(GoldflameQucusaurEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_STATE_TICKS =
            SynchedEntityData.defineId(GoldflameQucusaurEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DATA_PYRO_SHIELD =
            SynchedEntityData.defineId(GoldflameQucusaurEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_DIVE_X =
            SynchedEntityData.defineId(GoldflameQucusaurEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_DIVE_Y =
            SynchedEntityData.defineId(GoldflameQucusaurEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_DIVE_Z =
            SynchedEntityData.defineId(GoldflameQucusaurEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> DATA_FEATHER_BROKEN =
            SynchedEntityData.defineId(GoldflameQucusaurEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> DATA_TELE_X =
            SynchedEntityData.defineId(GoldflameQucusaurEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_TELE_Y =
            SynchedEntityData.defineId(GoldflameQucusaurEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_TELE_Z =
            SynchedEntityData.defineId(GoldflameQucusaurEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_TELE_YAW =
            SynchedEntityData.defineId(GoldflameQucusaurEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> DATA_HAS_TARGET =
            SynchedEntityData.defineId(GoldflameQucusaurEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> DATA_BREATH_PITCH =
            SynchedEntityData.defineId(GoldflameQucusaurEntity.class, EntityDataSerializers.FLOAT);

    private static final float MAX_PYRO_SHIELD = 240.0F; // Represents 24 GU Golden Flame Ward

    private final ServerBossEvent bossEvent = new ServerBossEvent(
            Component.translatable("entity.velorise_teyvat_bosses.goldflame_qucusaur").withStyle(ChatFormatting.GOLD),
            BossEvent.BossBarColor.RED,
            BossEvent.BossBarOverlay.PROGRESS
    );
    private final BossDamageCap damageCap = new BossDamageCap();

    private Vec3 homePos;
    private Vec3 attackDir = new Vec3(0.0D, 0.0D, 1.0D);
    private Vec3 diveTarget;
    private int attackCooldown = 30;
    private int aerialPhaseCount = 0;
    private int aerialTimer = 0;
    private boolean featherDestroyedThisCycle = false;
    private int deathAnimTicks = 0;
    private boolean blossomSpawned = false;
    private int idleOutOfCombatTicks = 0;
    private int destroyBlocksTick = 0;
    private Vec3 lastTrackPos = Vec3.ZERO;
    private int stuckTicks = 0;

    public GoldflameQucusaurEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.xpReward = 220;
        this.setPersistenceRequired();
        TeyvatBossesConfig.applyBossScaling(this, BOSS_ID);
        this.bossEvent.setVisible(false);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 680.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.34D)
                .add(Attributes.ATTACK_DAMAGE, 24.0D)
                .add(Attributes.ARMOR, 10.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.FOLLOW_RANGE, 48.0D)
                .add(Attributes.STEP_HEIGHT, 1.75D);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_STATE, STATE_DORMANT);
        builder.define(DATA_STATE_TICKS, 0);
        builder.define(DATA_PYRO_SHIELD, 0.0F);
        builder.define(DATA_DIVE_X, 0.0F);
        builder.define(DATA_DIVE_Y, 0.0F);
        builder.define(DATA_DIVE_Z, 0.0F);
        builder.define(DATA_FEATHER_BROKEN, false);
        builder.define(DATA_TELE_X, 0.0F);
        builder.define(DATA_TELE_Y, 0.0F);
        builder.define(DATA_TELE_Z, 0.0F);
        builder.define(DATA_TELE_YAW, 0.0F);
        builder.define(DATA_HAS_TARGET, false);
        builder.define(DATA_BREATH_PITCH, 8.0F);
    }

    public int getAnimState() { return entityData.get(DATA_STATE); }
    public int getStateTicks() { return entityData.get(DATA_STATE_TICKS); }
    public float getPyroShield() { return entityData.get(DATA_PYRO_SHIELD); }
    public float getPyroShieldProgress() { return Mth.clamp(getPyroShield() / MAX_PYRO_SHIELD, 0.0F, 1.0F); }
    public boolean isGoldenFlameActive() { return getPyroShield() > 0.0F; }
    public Vec3 getSyncedDiveTarget() { return new Vec3(entityData.get(DATA_DIVE_X), entityData.get(DATA_DIVE_Y), entityData.get(DATA_DIVE_Z)); }
    public boolean isFeatherBrokenThisCycle() { return entityData.get(DATA_FEATHER_BROKEN); }
    public Vec3 getSyncedTelegraphPos() { return new Vec3(entityData.get(DATA_TELE_X), entityData.get(DATA_TELE_Y), entityData.get(DATA_TELE_Z)); }
    public float getSyncedTelegraphYaw() { return entityData.get(DATA_TELE_YAW); }
    public boolean hasCombatTarget() { return entityData.get(DATA_HAS_TARGET); }
    public float getSyncedBreathPitch() { return entityData.get(DATA_BREATH_PITCH); }
    private void lockTelegraphHere(float yawDeg) {
        entityData.set(DATA_TELE_X, (float) getX());
        entityData.set(DATA_TELE_Y, (float) getY());
        entityData.set(DATA_TELE_Z, (float) getZ());
        entityData.set(DATA_TELE_YAW, yawDeg);
    }
    private void setSyncedDiveTarget(Vec3 tgt) {
        this.diveTarget = tgt;
        entityData.set(DATA_DIVE_X, (float) tgt.x);
        entityData.set(DATA_DIVE_Y, (float) tgt.y);
        entityData.set(DATA_DIVE_Z, (float) tgt.z);
    }
    public boolean isDormant() { return getAnimState() == STATE_DORMANT; }

    public boolean isAirborneState() {
        int st = getAnimState();
        return st == STATE_TAKEOFF_ROAR || st == STATE_GOLDEN_FLAME_HOVER || st == STATE_FEATHER_BARRAGE || st == STATE_DIVE_BOMB || st == STATE_RETURN_FLIGHT;
    }

    private void setState(int next) {
        entityData.set(DATA_STATE, next);
        entityData.set(DATA_STATE_TICKS, 0);
        setNoGravity(isAirborneState());
        if (next == STATE_SHOCKWAVE_SLAM || next == STATE_CHARGE_DRILL || next == STATE_WING_SWIPE) {
            float yaw = (next == STATE_CHARGE_DRILL && attackDir.lengthSqr() > 1e-4D)
                    ? (float) (Mth.atan2(attackDir.z, attackDir.x) * (180.0D / Math.PI)) - 90.0F
                    : getYRot();
            lockTelegraphHere(yaw);
        }
    }

    public String getActiveAnimationClip() {
        return switch (getAnimState()) {
            case STATE_WALK -> "walk";
            case STATE_WING_SWIPE -> "wing_swipe";
            case STATE_SHOCKWAVE_SLAM -> "shockwave_slam";
            case STATE_CHARGE_DRILL -> "charge_drill";
            case STATE_BREATH_SWEEP -> "breath_sweep";
            case STATE_TAKEOFF_ROAR -> "takeoff_roar";
            case STATE_GOLDEN_FLAME_HOVER -> isGoldenFlameActive() ? "golden_flame_idle" : "fly_hover";
            case STATE_FEATHER_BARRAGE -> "feather_barrage";
            case STATE_DIVE_BOMB -> "dive_bomb";
            case STATE_CRASH_STUN -> "crash_stun";
            case STATE_DEATH_COLLAPSE -> "death_collapse";
            case STATE_DORMANT -> "dormant_sleep";
            case STATE_WAKE_UP -> "wake_up";
            case STATE_RETURN_FLIGHT -> "fly_hover";
            default -> "idle_ground";
        };
    }

    @Override
    public boolean fireImmune() { return true; }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        bossEvent.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        bossEvent.removePlayer(player);
    }

    public void onFlamewindFeatherDestroyed(Vec3 featherPos) {
        featherDestroyedThisCycle = true;
        entityData.set(DATA_FEATHER_BROKEN, true);
        if (isGoldenFlameActive()) {
            float rem = Math.max(0.0F, getPyroShield() - 80.0F);
            entityData.set(DATA_PYRO_SHIELD, rem);
            if (rem <= 0.0F) {
                triggerCrashStun();
            }
        }
    }

    private void triggerCrashStun() {
        entityData.set(DATA_PYRO_SHIELD, 0.0F);
        setState(STATE_CRASH_STUN);
        setDeltaMovement(0.0D, -0.8D, 0.0D);
        if (level() instanceof ServerLevel sl) {
            sl.playSound(null, getX(), getY(), getZ(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 1.4F, 0.75F);
            sl.sendParticles(ParticleTypes.SMOKE, getX(), getY() + 1.5D, getZ(), 32, 1.8D, 1.2D, 1.8D, 0.08D);
        }
        BossClientBridge.triggerCameraShake(position(), 0.75F, 28.0D);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (getAnimState() == STATE_DEATH_COLLAPSE) return false;
        if (getAnimState() == STATE_DORMANT && !level().isClientSide()) {
            triggerWakeUp((ServerLevel) level());
        }
        if (source.is(DamageTypeTags.IS_FIRE)) {
            amount *= 0.30F; // 70% Pyro RES
        }
        if (isGoldenFlameActive() && !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            float shieldHit = source.is(DamageTypeTags.IS_DROWNING) || source.is(DamageTypeTags.IS_FREEZING)
                    ? Math.max(18.0F, amount * 2.2F)
                    : Math.max(8.0F, amount * 0.85F);
            float rem = Math.max(0.0F, getPyroShield() - shieldHit);
            entityData.set(DATA_PYRO_SHIELD, rem);
            if (rem <= 0.0F) {
                triggerCrashStun();
            }
            return true;
        }
        boolean vulnerable = getAnimState() == STATE_CRASH_STUN;
        float capped = damageCap.apply(this, source, vulnerable ? amount * 1.25F : amount, vulnerable);
        if (capped <= 0.0F) return false;
        return super.hurt(source, capped);
    }

    @Override
    public void die(DamageSource cause) {
        if (getAnimState() != STATE_DEATH_COLLAPSE) {
            setHealth(1.0F);
            setState(STATE_DEATH_COLLAPSE);
            deathAnimTicks = 0;
            entityData.set(DATA_PYRO_SHIELD, 0.0F);
            bossEvent.setProgress(0.0F);
            if (level() instanceof ServerLevel sl) {
                sl.playSound(null, getX(), getY(), getZ(), SoundEvents.ENDER_DRAGON_DEATH, SoundSource.HOSTILE, 1.1F, 1.15F);
            }
            return;
        }
        super.die(cause);
    }

    @Override
    public void tick() {
        super.tick();
        if (homePos == null) {
            homePos = position();
        }
        if (level().isClientSide()) {
            if (isGoldenFlameActive() && tickCount % 2 == 0) {
                level().addParticle(ParticleTypes.FLAME,
                        getX() + (random.nextDouble() - 0.5D) * 3.0D,
                        getY() + 1.0D + random.nextDouble() * 2.6D,
                        getZ() + (random.nextDouble() - 0.5D) * 3.0D,
                        0.0D, 0.06D, 0.0D);
            }
            return;
        }

        ServerLevel sl = (ServerLevel) level();
        int st = getAnimState();
        int ticks = getStateTicks() + 1;
        entityData.set(DATA_STATE_TICKS, ticks);

        if (st == STATE_DEATH_COLLAPSE) {
            setDeltaMovement(0.0D, onGround() ? 0.0D : -0.35D, 0.0D);
            deathAnimTicks++;
            if (deathAnimTicks >= 48 && !blossomSpawned) {
                blossomSpawned = true;
                TrounceBlossomEntity.spawn(sl, BossRespawnAnchorEntity.BOSS_GOLDFLAME_QUCUSAUR,
                        homePos != null ? homePos : position(), getYRot());
                discard();
            }
            return;
        }

        bossEvent.setProgress(Mth.clamp(getHealth() / getMaxHealth(), 0.0F, 1.0F));
        bossEvent.setColor(isGoldenFlameActive() ? BossEvent.BossBarColor.YELLOW : BossEvent.BossBarColor.RED);
        bossEvent.setVisible(st != STATE_DORMANT);

        Player target = sl.getNearestPlayer(getX(), getY(), getZ(), 42.0D,
                e -> e instanceof Player p && !p.isCreative() && !p.isSpectator() && p.isAlive());
        setTarget(target);
        entityData.set(DATA_HAS_TARGET, target != null);

        if (st == STATE_DORMANT) {
            tickDormant(sl, target);
            return;
        }
        if (st == STATE_WAKE_UP) {
            tickWakeUp(sl, target, ticks);
            return;
        }

        if (st != STATE_CRASH_STUN && st != STATE_RETURN_FLIGHT) {
            tickAntiTrapBlockBreak(sl, st == STATE_CHARGE_DRILL);
        }

        if (attackCooldown > 0) attackCooldown--;

        // Trigger Golden Flame Aerial Phase at 75% and 35% HP
        float hpPct = getHealth() / getMaxHealth();
        if (!isAirborneState() && st != STATE_CRASH_STUN && (st == STATE_IDLE_GROUND || st == STATE_WALK)) {
            if ((aerialPhaseCount == 0 && hpPct <= 0.75F) || (aerialPhaseCount == 1 && hpPct <= 0.35F)) {
                aerialPhaseCount++;
                aerialTimer = 0;
                featherDestroyedThisCycle = false;
                entityData.set(DATA_FEATHER_BROKEN, false);
                entityData.set(DATA_PYRO_SHIELD, MAX_PYRO_SHIELD);
                setState(STATE_TAKEOFF_ROAR);
                sl.playSound(null, getX(), getY(), getZ(), SoundEvents.ENDER_DRAGON_GROWL, SoundSource.HOSTILE, 1.6F, 0.95F);
                return;
            }
        }

        switch (st) {
            case STATE_IDLE_GROUND, STATE_WALK -> tickGroundLocomotion(sl, target);
            case STATE_WING_SWIPE -> tickWingSwipe(sl, target, ticks);
            case STATE_SHOCKWAVE_SLAM -> tickShockwaveSlam(sl, ticks);
            case STATE_CHARGE_DRILL -> tickChargeDrill(sl, target, ticks);
            case STATE_BREATH_SWEEP -> tickBreathSweep(sl, target, ticks);
            case STATE_TAKEOFF_ROAR -> tickTakeoffRoar(sl, ticks);
            case STATE_GOLDEN_FLAME_HOVER -> tickGoldenFlameHover(sl, target, ticks);
            case STATE_FEATHER_BARRAGE -> tickFeatherBarrage(sl, target, ticks);
            case STATE_DIVE_BOMB -> tickDiveBomb(sl, ticks);
            case STATE_RETURN_FLIGHT -> tickReturnFlight(sl, target, ticks);
            case STATE_CRASH_STUN -> {
                setDeltaMovement(0.0D, onGround() ? 0.0D : -0.35D, 0.0D);
                if (ticks >= 120) {
                    setState(STATE_IDLE_GROUND);
                    attackCooldown = 25;
                }
            }
            default -> setState(STATE_IDLE_GROUND);
        }
    }

    private void triggerWakeUp(ServerLevel sl) {
        if (getAnimState() != STATE_DORMANT) return;
        idleOutOfCombatTicks = 0;
        setState(STATE_WAKE_UP);
        bossEvent.setVisible(true);
        sl.playSound(null, getX(), getY(), getZ(), SoundEvents.ENDER_DRAGON_FLAP, SoundSource.HOSTILE, 1.2F, 0.85F);
    }

    private void tickDormant(ServerLevel sl, Player target) {
        setDeltaMovement(0.0D, onGround() ? 0.0D : -0.35D, 0.0D);
        bossEvent.setVisible(false);
        if (target != null && distanceToSqr(target) <= 16.0D * 16.0D) {
            triggerWakeUp(sl);
        }
    }

    private void tickWakeUp(ServerLevel sl, Player target, int ticks) {
        setDeltaMovement(0.0D, onGround() ? 0.0D : -0.35D, 0.0D);
        if (target != null && ticks >= 10) {
            faceTarget(target.position(), 6.0F);
        }
        if (ticks == 20) { // 1.0s roar peak
            sl.playSound(null, getX(), getY(), getZ(), SoundEvents.ENDER_DRAGON_GROWL, SoundSource.HOSTILE, 1.5F, 0.92F);
            sl.sendParticles(ParticleTypes.FLAME, getX(), getY() + 1.4D, getZ(), 28, 2.2D, 0.8D, 2.2D, 0.10D);
            BossClientBridge.triggerCameraShake(position(), 0.55F, 26.0D);
        }
        if (ticks >= 36) { // 1.8s clip duration
            setState(STATE_IDLE_GROUND);
            attackCooldown = 18;
        }
    }

    /**
     * Smooth aerial return-home flight: ascends above terrain, glides directly over {@code homePos},
     * and lands cleanly on the arena pedestal without digging through hills or getting stuck below grade.
     */
    private void tickReturnFlight(ServerLevel sl, Player target, int ticks) {
        if (homePos == null) {
            setState(STATE_IDLE_GROUND);
            return;
        }
        // Re-engage if a player walks back into close combat range
        if (target != null && distanceToSqr(target) <= 24.0D * 24.0D) {
            setState(STATE_IDLE_GROUND);
            attackCooldown = 16;
            return;
        }

        // Heal gently while flying back out of combat
        if (ticks % 20 == 0 && getHealth() < getMaxHealth()) {
            heal(getMaxHealth() * 0.05F);
        }

        double dx = homePos.x - getX();
        double dz = homePos.z - getZ();
        double horizDist = Math.sqrt(dx * dx + dz * dz);

        if (horizDist > 2.5D) {
            // Fly over any hill/wall: cruise at least 6 blocks above homePos (or climb if below it)
            double cruiseY = Math.max(homePos.y + 6.5D, getY() < homePos.y + 3.0D ? homePos.y + 6.5D : getY());
            double dy = cruiseY - getY();
            double vx = (dx / horizDist) * 0.58D;
            double vz = (dz / horizDist) * 0.58D;
            double vy = Mth.clamp(dy * 0.18D, -0.35D, 0.52D);
            setDeltaMovement(vx, vy, vz);
            faceTarget(homePos, 10.0F);
        } else {
            // Directly above homePos -> descend smoothly to land on the pedestal
            double dy = homePos.y - getY();
            if (Math.abs(dy) <= 1.2D || onGround()) {
                setDeltaMovement(0.0D, 0.0D, 0.0D);
                setState(STATE_IDLE_GROUND);
                return;
            }
            setDeltaMovement(dx * 0.25D, Mth.clamp(dy * 0.22D, -0.42D, 0.35D), dz * 0.25D);
        }

        if (ticks % 18 == 0) {
            sl.playSound(null, getX(), getY() + 1.5D, getZ(), SoundEvents.ENDER_DRAGON_FLAP, SoundSource.HOSTILE, 0.9F, 1.05F);
        }

        // Safety fallback if somehow trapped under a solid ceiling for > 11 seconds
        if (ticks >= 220) {
            teleportTo(homePos.x, homePos.y + 0.5D, homePos.z);
            setDeltaMovement(0.0D, 0.0D, 0.0D);
            setState(STATE_IDLE_GROUND);
        }
    }

    private void tickGroundLocomotion(ServerLevel sl, Player target) {
        if (target == null) {
            if (homePos != null && position().distanceToSqr(homePos) > 16.0D) {
                getNavigation().stop();
                stuckTicks = 0;
                setState(STATE_RETURN_FLIGHT);
                sl.playSound(null, getX(), getY() + 1.5D, getZ(), SoundEvents.ENDER_DRAGON_FLAP, SoundSource.HOSTILE, 1.3F, 1.05F);
            } else {
                getNavigation().stop();
                setDeltaMovement(0.0D, getDeltaMovement().y, 0.0D);
                if (getAnimState() != STATE_IDLE_GROUND) setState(STATE_IDLE_GROUND);
                idleOutOfCombatTicks++;
                if (idleOutOfCombatTicks % 20 == 0 && getHealth() < getMaxHealth()) {
                    heal(getMaxHealth() * 0.05F);
                }
                if (idleOutOfCombatTicks >= 160) {
                    idleOutOfCombatTicks = 0;
                    aerialPhaseCount = 0;
                    entityData.set(DATA_PYRO_SHIELD, 0.0F);
                    setState(STATE_DORMANT);
                    bossEvent.setVisible(false);
                }
            }
            return;
        }
        idleOutOfCombatTicks = 0;

        faceTarget(target.position(), 8.0F);
        double dist = distanceTo(target);
        if (attackCooldown <= 0) {
            double dyAbove = target.getY() - getY();
            if (dyAbove >= 3.5D) {
                // Anti-pillar-cheese: never stare helplessly when player towers up on blocks!
                if (random.nextFloat() < 0.50F) {
                    Vec3 mouth = position().add(0.0D, 2.8D, 0.0D);
                    Vec3 aim = target.position().add(0.0D, 0.8D, 0.0D).subtract(mouth).normalize();
                    float fbDmg = TeyvatBossesConfig.scaleDamage(BOSS_ID, 12.0F);
                    for (int i = -1; i <= 1; i++) {
                        Vec3 spreadDir = aim.yRot((float) Math.toRadians(i * 9.0D));
                        GoldflameFireballEntity.spawn(sl, this, mouth, spreadDir, 1.65F, fbDmg, 2.6F, false);
                    }
                    sl.playSound(null, getX(), getY() + 2.4D, getZ(), SoundEvents.BLAZE_SHOOT, SoundSource.HOSTILE, 1.55F, 0.78F);
                    attackCooldown = 28;
                } else {
                    // Launch an immediate aerial dive-slam directly onto the player's pillar!
                    setSyncedDiveTarget(target.position());
                    setState(STATE_DIVE_BOMB);
                    sl.playSound(null, getX(), getY() + 1.8D, getZ(), SoundEvents.ENDER_DRAGON_FLAP, SoundSource.HOSTILE, 1.4F, 0.95F);
                }
                return;
            }
            attackDir = target.position().subtract(position()).multiply(1.0D, 0.0D, 1.0D).normalize();
            if (dist < 7.5D) {
                setState(random.nextFloat() < 0.60F ? STATE_WING_SWIPE : STATE_SHOCKWAVE_SLAM);
            } else if (dist < 16.0D) {
                setState(random.nextFloat() < 0.55F ? STATE_BREATH_SWEEP : STATE_CHARGE_DRILL);
            } else {
                if (random.nextFloat() < 0.45F) {
                    // Punish long-range bow snipers with a 3-fireball Goldflame burst
                    Vec3 mouth = position().add(0.0D, 2.6D, 0.0D);
                    Vec3 aim = target.position().add(0.0D, 1.0D, 0.0D).subtract(mouth).normalize();
                    float fbDmg = TeyvatBossesConfig.scaleDamage(BOSS_ID, 11.0F);
                    for (int i = -1; i <= 1; i++) {
                        Vec3 spreadDir = aim.yRot((float) Math.toRadians(i * 10.0D));
                        GoldflameFireballEntity.spawn(sl, this, mouth, spreadDir, 1.55F, fbDmg, 2.2F, false);
                    }
                    sl.playSound(null, getX(), getY() + 2.2D, getZ(), SoundEvents.BLAZE_SHOOT, SoundSource.HOSTILE, 1.5F, 0.80F);
                    attackCooldown = 34;
                } else {
                    setState(STATE_CHARGE_DRILL);
                }
            }
            return;
        }

        if (dist > 5.5D) {
            moveToward(target.position(), 0.30D);
            if (getAnimState() != STATE_WALK) setState(STATE_WALK);
        } else {
            setDeltaMovement(0.0D, getDeltaMovement().y, 0.0D);
            if (getAnimState() != STATE_IDLE_GROUND) setState(STATE_IDLE_GROUND);
        }
    }

    private void tickWingSwipe(ServerLevel sl, Player target, int ticks) {
        // Synced 1:1 with 3.80s (76 ticks) wing_swipe 3-hit combo:
        // Hit 1: tick 15 (0.75s), Hit 2: tick 38 (1.90s), Hit 3 Cyclone Dash: ticks 58..69 (2.90..3.45s)
        if (target != null && ((ticks >= 16 && ticks <= 21) || (ticks >= 39 && ticks <= 45))) {
            faceTarget(target.position(), 18.0F);
            attackDir = target.position().subtract(position()).multiply(1.0D, 0.0D, 1.0D).normalize();
        }
        if (ticks == 22 || ticks == 46) {
            lockTelegraphHere(getYRot());
        }
        if (ticks == 15 || ticks == 38) {
            dealConePyroDamage(sl, 7.5D, 110.0F, 11.0F);
            sl.playSound(null, getX(), getY() + 1.5D, getZ(), SoundEvents.ENDER_DRAGON_FLAP, SoundSource.HOSTILE, 1.25F, 1.15F);
        } else if (ticks >= 58 && ticks <= 69) {
            setDeltaMovement(attackDir.x * 0.85D, ticks < 64 ? 0.12D : -0.15D, attackDir.z * 0.85D);
            if (ticks == 62 || ticks == 68) {
                dealRadialPyroDamage(sl, position(), 6.8D, 14.0F, 1.1D);
                sl.sendParticles(ParticleTypes.FLAME, getX(), getY() + 1.2D, getZ(), 24, 2.2D, 0.6D, 2.2D, 0.14D);
            }
        } else {
            setDeltaMovement(getDeltaMovement().scale(0.75D));
        }
        if (ticks >= 76) {
            setState(STATE_IDLE_GROUND);
            attackCooldown = 36;
        }
    }

    private void tickShockwaveSlam(ServerLevel sl, int ticks) {
        setDeltaMovement(0.0D, getDeltaMovement().y, 0.0D);
        if (ticks == 16) { // 0.80s impact
            dealRadialPyroDamage(sl, position(), 9.0D, 15.0F, 1.35D);
            sl.playSound(null, getX(), getY(), getZ(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 1.4F, 0.85F);
            sl.sendParticles(ParticleTypes.FLAME, getX(), getY() + 0.5D, getZ(), 48, 3.8D, 0.4D, 3.8D, 0.18D);
            BossClientBridge.triggerCameraShake(position(), 0.70F, 26.0D);
        }
        if (ticks >= 31) {
            setState(STATE_IDLE_GROUND);
            attackCooldown = 42;
        }
    }

    private void tickChargeDrill(ServerLevel sl, Player target, int ticks) {
        if (ticks <= 6 && target != null) {
            // Aim at the player's current position during initial windup before locking the lane
            Vec3 to = target.position().subtract(position()).multiply(1.0D, 0.0D, 1.0D);
            if (to.lengthSqr() > 1e-4D) {
                attackDir = to.normalize();
                float yaw = (float) (Mth.atan2(attackDir.z, attackDir.x) * (180.0D / Math.PI)) - 90.0F;
                faceTarget(target.position(), 22.0F);
                lockTelegraphHere(yaw);
            }
        }
        if (ticks >= 12 && ticks <= 28) {
            double vy = getDeltaMovement().y;
            if (target != null) {
                double dy = target.getY() - getY();
                if (dy > 1.0D) {
                    vy = Mth.clamp(dy * 0.14D, 0.08D, 0.36D);
                } else if (!onGround()) {
                    vy = Math.max(-0.25D, vy - 0.04D);
                }
            }
            setDeltaMovement(attackDir.x * 1.20D, vy, attackDir.z * 1.20D);
            if (ticks % 4 == 0) {
                dealRadialPyroDamage(sl, position(), 4.2D, 12.0F, 0.9D);
                sl.sendParticles(ParticleTypes.FLAME, getX(), getY() + 1.4D, getZ(), 16, 1.2D, 0.8D, 1.2D, 0.10D);
            }
        } else {
            setDeltaMovement(getDeltaMovement().scale(0.7D));
        }
        if (ticks >= 34) {
            setState(STATE_IDLE_GROUND);
            attackCooldown = 44;
        }
    }

    private void tickBreathSweep(ServerLevel sl, Player target, int ticks) {
        setDeltaMovement(0.0D, getDeltaMovement().y, 0.0D);
        Vec3 origin = position().add(0.0D, 2.5D, 0.0D);
        if (target != null) {
            // Track target yaw briskly during windup (0..15) and gently during the breath torrent (16..48)
            faceTarget(target.position(), ticks < 16 ? 14.0F : 3.2F);
            Vec3 toTarget = target.position().add(0.0D, 1.0D, 0.0D).subtract(origin);
            double horiz = Math.max(0.5D, Math.sqrt(toTarget.x * toTarget.x + toTarget.z * toTarget.z));
            float desiredPitch = Mth.clamp((float) (-Math.toDegrees(Math.atan2(toTarget.y, horiz))), -42.0F, 48.0F);
            float nextPitch = Mth.lerp(ticks < 16 ? 0.35F : 0.18F, getSyncedBreathPitch(), desiredPitch);
            entityData.set(DATA_BREATH_PITCH, nextPitch);
        }
        if (ticks >= 16 && ticks <= 48 && ticks % 4 == 0) {
            float progress = Mth.clamp((ticks - 16) / 32.0F, 0.0F, 1.0F);
            float sweepYaw = getYRot() + Mth.lerp(progress, -28.0F, 28.0F);
            float pitch = getSyncedBreathPitch();
            Vec3 dir = Vec3.directionFromRotation(pitch, sweepYaw);
            for (double d = 2.0D; d <= 16.0D; d += 2.0D) {
                Vec3 pt = origin.add(dir.scale(d));
                sl.sendParticles(ParticleTypes.FLAME, pt.x, pt.y, pt.z, 3, 0.45D, 0.35D, 0.45D, 0.03D);
                dealRadialPyroDamage(sl, pt, 2.3D, 7.0F, 0.35D);
            }
        }
        if (ticks >= 60) {
            setState(STATE_IDLE_GROUND);
            attackCooldown = 48;
        }
    }

    private void tickTakeoffRoar(ServerLevel sl, int ticks) {
        if (ticks >= 10 && ticks <= 28) {
            setDeltaMovement(0.0D, 0.55D, 0.0D);
        } else {
            setDeltaMovement(0.0D, 0.05D, 0.0D);
        }
        if (ticks == 16) {
            dealRadialPyroDamage(sl, position(), 6.5D, 10.0F, 1.2D);
            sl.sendParticles(ParticleTypes.FLAME, getX(), getY() + 0.5D, getZ(), 32, 2.5D, 0.6D, 2.5D, 0.14D);
        }
        if (ticks >= 33) {
            setState(STATE_GOLDEN_FLAME_HOVER);
        }
    }

    private void tickGoldenFlameHover(ServerLevel sl, Player target, int ticks) {
        aerialTimer++;
        Vec3 center = homePos != null ? homePos : position();
        double desiredY = center.y + 10.0D;
        double angle = aerialTimer * 0.045D;
        Vec3 orbitPos = new Vec3(center.x + Math.cos(angle) * 9.0D, desiredY, center.z + Math.sin(angle) * 9.0D);
        Vec3 delta = orbitPos.subtract(position());
        setDeltaMovement(delta.scale(0.10D));
        if (target != null) {
            faceTarget(target.position(), 8.0F);
        }
        if (ticks == 40) {
            setState(STATE_FEATHER_BARRAGE);
        } else if (aerialTimer >= 900 || !isGoldenFlameActive()) {
            // End Golden Flame phase cleanly
            entityData.set(DATA_PYRO_SHIELD, 0.0F);
            setSyncedDiveTarget(target != null ? target.position() : center);
            setState(STATE_DIVE_BOMB);
        }
    }

    private void tickFeatherBarrage(ServerLevel sl, Player target, int ticks) {
        setDeltaMovement(0.0D, 0.02D, 0.0D);
        if (target != null) faceTarget(target.position(), 10.0F);
        if (ticks == 14) {
            Vec3 dropPos = target != null
                    ? target.position().add((random.nextDouble() - 0.5D) * 6.0D, 0.0D, (random.nextDouble() - 0.5D) * 6.0D)
                    : (homePos != null ? homePos : position());
            FlamewindFeatherEntity.spawn(sl, this, dropPos);
            if (target != null) {
                GoldflameFireballEntity.spawn(sl, this, position().add(0.0D, 1.8D, 0.0D),
                        target.position().add(0.0D, 1.0D, 0.0D).subtract(position().add(0.0D, 1.8D, 0.0D)),
                        1.45F, TeyvatBossesConfig.scaleDamage(BOSS_ID, 12.0F), 2.5F, false);
            }
        }
        if (ticks >= 31) {
            setSyncedDiveTarget(target != null ? target.position() : (homePos != null ? homePos : position()));
            setState(STATE_DIVE_BOMB);
        }
    }

    private void tickDiveBomb(ServerLevel sl, int ticks) {
        Vec3 tgt = diveTarget != null ? diveTarget : (homePos != null ? homePos : position());
        if (ticks < 11) {
            setDeltaMovement(0.0D, 0.25D, 0.0D);
            faceTarget(tgt, 12.0F);
        } else if (ticks <= 24) {
            Vec3 dir = tgt.subtract(position());
            if (dir.lengthSqr() > 1.5D && !onGround()) {
                setDeltaMovement(dir.normalize().scale(1.65D));
            } else {
                float dmg = featherDestroyedThisCycle ? 15.0F : 24.0F;
                float rad = featherDestroyedThisCycle ? 5.5F : 8.0F;
                dealRadialPyroDamage(sl, position(), rad, dmg, 1.5D);
                sl.playSound(null, getX(), getY(), getZ(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 1.6F, 0.8F);
                sl.sendParticles(ParticleTypes.EXPLOSION_EMITTER, getX(), getY() + 0.5D, getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
                sl.sendParticles(ParticleTypes.FLAME, getX(), getY() + 0.5D, getZ(), 60, rad * 0.45D, 0.8D, rad * 0.45D, 0.20D);
                BossClientBridge.triggerCameraShake(position(), 0.95F, 34.0D);
                // Shatter player-built pillars if impact is elevated above the arena floor
                if (TeyvatBossesConfig.get().enableBossBlockBreaking
                        && sl.getGameRules().getBoolean(net.minecraft.world.level.GameRules.RULE_MOBGRIEFING)
                        && (homePos == null || getY() > homePos.y + 1.5D)) {
                    BlockPos.MutableBlockPos bpos = new BlockPos.MutableBlockPos();
                    int cx = Mth.floor(getX()), cy = Mth.floor(getY()), cz = Mth.floor(getZ());
                    int floorMinY = homePos != null ? Mth.floor(homePos.y) + 1 : cy - 3;
                    for (int bx = cx - 2; bx <= cx + 2; bx++) {
                        for (int by = Math.max(floorMinY, cy - 3); by <= cy + 2; by++) {
                            for (int bz = cz - 2; bz <= cz + 2; bz++) {
                                bpos.set(bx, by, bz);
                                net.minecraft.world.level.block.state.BlockState bs = sl.getBlockState(bpos);
                                if (bs.isAir() || !bs.getFluidState().isEmpty() || bs.hasBlockEntity()) continue;
                                float h = bs.getDestroySpeed(sl, bpos);
                                if (h >= 0.0F && h < 50.0F) {
                                    sl.destroyBlock(bpos, true, this);
                                }
                            }
                        }
                    }
                }
                featherDestroyedThisCycle = false;
                if (isGoldenFlameActive() && aerialTimer < 860) {
                    setState(STATE_TAKEOFF_ROAR);
                } else {
                    entityData.set(DATA_PYRO_SHIELD, 0.0F);
                    setState(STATE_IDLE_GROUND);
                    attackCooldown = 45;
                }
            }
        } else {
            setState(isGoldenFlameActive() ? STATE_GOLDEN_FLAME_HOVER : STATE_IDLE_GROUND);
        }
    }

    /**
     * Anti-trap block breaking (matching RuinGuardEntity): shatters obstructing blocks
     * intersecting the wyvern's hitbox when pinned against walls or charging, respecting
     * config and mobGriefing while preserving chests, Bedrock, Obsidian, and the floor.
     */
    private void tickAntiTrapBlockBreak(ServerLevel sl, boolean immediateCharge) {
        if (!TeyvatBossesConfig.get().enableBossBlockBreaking) return;
        boolean stuck = (stuckTicks >= 12);
        if ((this.horizontalCollision || this.isInWall() || stuck) && destroyBlocksTick <= 0) {
            destroyBlocksTick = (immediateCharge || stuck) ? 1 : 6;
        }
        if (destroyBlocksTick <= 0) return;
        destroyBlocksTick--;
        if (destroyBlocksTick != 0) return;
        if (!sl.getGameRules().getBoolean(net.minecraft.world.level.GameRules.RULE_MOBGRIEFING)) return;

        Vec3 fwd = Vec3.directionFromRotation(0.0F, getYRot()).scale(0.75D);
        AABB box = getBoundingBox().inflate(0.55D, 0.05D, 0.55D).expandTowards(fwd.x, 0.0D, fwd.z);
        int minX = Mth.floor(box.minX);
        int minY = Mth.floor(getY());
        int minZ = Mth.floor(box.minZ);
        int maxX = Mth.floor(box.maxX);
        int maxY = Mth.floor(box.maxY);
        int maxZ = Mth.floor(box.maxZ);
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    pos.set(x, y, z);
                    net.minecraft.world.level.block.state.BlockState state = sl.getBlockState(pos);
                    if (state.isAir() || !state.getFluidState().isEmpty() || state.hasBlockEntity()) continue;
                    float hardness = state.getDestroySpeed(sl, pos);
                    if (hardness < 0.0F || hardness >= 50.0F) continue;
                    sl.destroyBlock(pos, true, this);
                }
            }
        }
        stuckTicks = 0;
    }

    private void moveToward(Vec3 pos, double speed) {
        // Track actual movement to detect being pinned against pillars/gates
        if (position().distanceToSqr(lastTrackPos) < 0.015D) {
            stuckTicks++;
        } else {
            stuckTicks = 0;
        }
        lastTrackPos = position();

        // Use real Minecraft A* navigation to steer smoothly around pillars & gates
        if (tickCount % 6 == 0 || getNavigation().isDone()) {
            getNavigation().moveTo(pos.x, pos.y, pos.z, 1.05D);
        }

        // If navigation cannot find a clear path or boss is pinned, steer directly + winged hop over ledges
        Vec3 dir = pos.subtract(position()).multiply(1.0D, 0.0D, 1.0D);
        if (dir.lengthSqr() > 0.01D) {
            dir = dir.normalize();
            if (getNavigation().isDone() || stuckTicks >= 6) {
                double vy = getDeltaMovement().y;
                if (onGround() && (horizontalCollision || stuckTicks >= 10)) {
                    vy = 0.58D; // Winged hop over obstacles/ledges
                }
                setDeltaMovement(dir.x * speed, vy, dir.z * speed);
            }
            faceTarget(pos, 9.0F);
        }
    }

    private void faceTarget(Vec3 pos, float maxTurnDeg) {
        double dx = pos.x - getX();
        double dz = pos.z - getZ();
        float desired = (float) (Mth.atan2(dz, dx) * (180.0D / Math.PI)) - 90.0F;
        float next = Mth.approachDegrees(getYRot(), desired, maxTurnDeg);
        setYRot(next);
        yBodyRot = next;
        yHeadRot = next;
    }

    private void dealRadialPyroDamage(ServerLevel sl, Vec3 center, double radius, float baseDmg, double kb) {
        float dmg = TeyvatBossesConfig.scaleDamage(BOSS_ID, baseDmg);
        AABB box = new AABB(center.x - radius, center.y - 2.0D, center.z - radius,
                center.x + radius, center.y + 4.0D, center.z + radius);
        List<LivingEntity> list = sl.getEntitiesOfClass(LivingEntity.class, box,
                e -> e != this && e.isAlive()
                        && e.position().distanceToSqr(center) <= radius * radius);
        for (LivingEntity e : list) {
            StavesElementBridge.dealElementalDamage(e, this, "PYRO", dmg, 1.0F, false, 0.25F);
            e.igniteForSeconds(4.0F);
            Vec3 push = e.position().subtract(center).multiply(1.0D, 0.0D, 1.0D);
            if (push.lengthSqr() > 1e-4D) {
                push = push.normalize().scale(kb);
                e.push(push.x, 0.42D, push.z);
            }
        }
    }

    private void dealConePyroDamage(ServerLevel sl, double radius, float halfAngleDeg, float baseDmg) {
        float dmg = TeyvatBossesConfig.scaleDamage(BOSS_ID, baseDmg);
        Vec3 forward = Vec3.directionFromRotation(0.0F, getSyncedTelegraphYaw()).normalize();
        double cosLim = Math.cos(Math.toRadians(halfAngleDeg));
        AABB box = getBoundingBox().inflate(radius, 3.0D, radius);
        for (LivingEntity e : sl.getEntitiesOfClass(LivingEntity.class, box,
                t -> t != this && t.isAlive())) {
            Vec3 to = e.position().subtract(position()).multiply(1.0D, 0.0D, 1.0D);
            if (to.length() <= radius && to.lengthSqr() > 1e-4D && to.normalize().dot(forward) >= cosLim) {
                StavesElementBridge.dealElementalDamage(e, this, "PYRO", dmg, 1.0F, false, 0.20F);
                e.igniteForSeconds(3.0F);
                Vec3 push = to.normalize().scale(0.95D);
                e.push(push.x, 0.35D, push.z);
            }
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (homePos != null) {
            tag.putDouble("HomeX", homePos.x);
            tag.putDouble("HomeY", homePos.y);
            tag.putDouble("HomeZ", homePos.z);
        }
        tag.putInt("AerialPhaseCount", aerialPhaseCount);
        tag.putFloat("PyroShield", getPyroShield());
        tag.putBoolean("Dormant", getAnimState() == STATE_DORMANT);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("HomeX")) {
            homePos = new Vec3(tag.getDouble("HomeX"), tag.getDouble("HomeY"), tag.getDouble("HomeZ"));
        }
        aerialPhaseCount = tag.getInt("AerialPhaseCount");
        entityData.set(DATA_PYRO_SHIELD, tag.getFloat("PyroShield"));
        if (tag.contains("Dormant")) {
            boolean dormant = tag.getBoolean("Dormant");
            entityData.set(DATA_STATE, dormant ? STATE_DORMANT : STATE_IDLE_GROUND);
            bossEvent.setVisible(!dormant);
        }
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean canUsePortal(boolean allowPassengers) {
        return false;
    }

    @Override
    public boolean startRiding(Entity vehicle, boolean force) {
        return false;
    }

    @Override
    public boolean canBeLeashed() {
        return false;
    }

    @Override
    public boolean canBeAffected(MobEffectInstance effect) {
        if (effect.getEffect().equals(MobEffects.LEVITATION)) {
            return false;
        }
        if (effect.getEffect().equals(MobEffects.MOVEMENT_SLOWDOWN) && effect.getAmplifier() >= 3) {
            return false;
        }
        return super.canBeAffected(effect);
    }

    @Override
    protected int decreaseAirSupply(int currentAir) {
        return currentAir;
    }

    @Override
    public AABB getBoundingBoxForCulling() {
        return super.getBoundingBoxForCulling().inflate(7.0D);
    }
}

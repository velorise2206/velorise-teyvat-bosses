package com.velorise.teyvatbosses.entity;

import com.velorise.teyvatbosses.compat.StavesElementBridge;
import com.velorise.teyvatbosses.entity.ai.BossGroundNavigation;
import com.velorise.teyvatbosses.registry.TeyvatBossesSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * Boss-tier Ruin Guard (GN/Hu-24601 — Thousand Winds Overlord) with deterministic state machine,
 * full-length death animation, weakpoint immunity runes after stun recovery, Stomp-to-Clap Genshin chain,
 * Phase 2 Cataclysmic Overdrive Finisher, world-locked telegraph rings, and stride-synced arena movement.
 */
public class RuinGuardEntity extends Monster {

    public static final int STATE_DORMANT = 0;
    public static final int STATE_AWAKEN  = 1;
    public static final int STATE_COMBAT  = 2;
    public static final int STATE_STUNNED = 3;
    public static final int STATE_DEATH   = 4;

    public static final int ANIM_DORMANT            = 0;
    public static final int ANIM_AWAKEN             = 1;
    public static final int ANIM_IDLE               = 2;
    public static final int ANIM_WALK               = 3;
    public static final int ANIM_MISSILE_BARRAGE    = 4;
    public static final int ANIM_MELEE_3HIT         = 5;
    public static final int ANIM_DOUBLE_CLAP        = 6;
    public static final int ANIM_STOMP_SHOCKWAVE    = 7;
    public static final int ANIM_CYCLONE_WINDUP     = 8;
    public static final int ANIM_CYCLONE_ACTIVE     = 9;
    public static final int ANIM_CYCLONE_RECOVER    = 10;
    public static final int ANIM_LEAP_SLAM          = 11;
    public static final int ANIM_PHASE2_OVERDRIVE   = 12;
    public static final int ANIM_STUNNED            = 13;
    public static final int ANIM_DEATH              = 14;
    public static final int ANIM_STUN_RECOVERY      = 15;
    public static final int ANIM_OVERDRIVE_FINISHER = 16;
    public static final int ANIM_WEAKPOINT_FLINCH   = 17;

    private static final int DEATH_DURATION_TICKS    = 58;
    private static final int STUN_COLLAPSE_TICKS     = 122;
    private static final int STUN_RECOVERY_TICKS     = 28;
    private static final int WEAKPOINT_FLINCH_TICKS  = 19;
    private static final int WEAKPOINT_IMMUNE_TICKS  = 80;
    private static final double ARENA_SOFT_LEASH_RADIUS = 56.0D;

    private static final EntityDataAccessor<Integer> DATA_BOSS_STATE =
            SynchedEntityData.defineId(RuinGuardEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_ANIM_ID =
            SynchedEntityData.defineId(RuinGuardEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_ANIM_EPOCH =
            SynchedEntityData.defineId(RuinGuardEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_PHASE2 =
            SynchedEntityData.defineId(RuinGuardEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> DATA_TELEGRAPH_RADIUS =
            SynchedEntityData.defineId(RuinGuardEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_TELEGRAPH_X =
            SynchedEntityData.defineId(RuinGuardEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_TELEGRAPH_Y =
            SynchedEntityData.defineId(RuinGuardEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_TELEGRAPH_Z =
            SynchedEntityData.defineId(RuinGuardEntity.class, EntityDataSerializers.FLOAT);

    private final ServerBossEvent bossEvent = new ServerBossEvent(
            Component.translatable("entity.velorise_teyvat_bosses.ruin_guard"),
            BossEvent.BossBarColor.YELLOW,
            BossEvent.BossBarOverlay.NOTCHED_10
    );

    private Vec3 arenaCenter = Vec3.ZERO;
    private boolean arenaInit = false;
    private float savedSpawnYaw = 0.0F;

    private int stateTick = 0;
    private int noTargetTicks = 0;
    private int attackCooldown = 40;
    private int finisherCooldown = 0;
    private int speechCooldownTicks = 0;
    private int lastAttack = -1;
    private int eyeHits = 0;
    private int eyeResetTimer = 0;
    private int weakpointImmuneTicks = 0;
    private boolean weakpointRearHit = false;
    private float weakpointSwivelTargetYaw = 0.0F;
    private boolean pendingPhase2 = false;
    private boolean customDeathHandled = false;
    private Vec3 leapStart = Vec3.ZERO;
    private Vec3 leapTarget = Vec3.ZERO;

    // Client-side smooth animation tick (reset immediately on epoch packet arrival)
    private int clientAnimTick = 0;
    private int lastSyncedAnim = -1;
    private int lastSyncedEpoch = -1;

    public RuinGuardEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.xpReward = 700;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 580.0D)
                .add(Attributes.ARMOR, 8.0D)
                .add(Attributes.ARMOR_TOUGHNESS, 2.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.23D)
                .add(Attributes.ATTACK_DAMAGE, 14.0D)
                .add(Attributes.FOLLOW_RANGE, 42.0D)
                .add(Attributes.STEP_HEIGHT, 1.75D);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_BOSS_STATE, STATE_DORMANT);
        builder.define(DATA_ANIM_ID, ANIM_DORMANT);
        builder.define(DATA_ANIM_EPOCH, 0);
        builder.define(DATA_PHASE2, false);
        builder.define(DATA_TELEGRAPH_RADIUS, 0.0F);
        builder.define(DATA_TELEGRAPH_X, 0.0F);
        builder.define(DATA_TELEGRAPH_Y, 0.0F);
        builder.define(DATA_TELEGRAPH_Z, 0.0F);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (level().isClientSide() && DATA_ANIM_EPOCH.equals(key)) {
            lastSyncedAnim = getAnimId();
            lastSyncedEpoch = getAnimEpoch();
            clientAnimTick = 0;
        }
    }

    @Override
    protected void registerGoals() {}

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new BossGroundNavigation(this, level);
    }

    public int getBossState() { return entityData.get(DATA_BOSS_STATE); }
    public int getAnimId() { return entityData.get(DATA_ANIM_ID); }
    public int getAnimEpoch() { return entityData.get(DATA_ANIM_EPOCH); }
    public int getAnimTick() { return level().isClientSide() ? clientAnimTick : stateTick; }
    public boolean isPhase2() { return entityData.get(DATA_PHASE2); }
    public float getTelegraphRadius() { return entityData.get(DATA_TELEGRAPH_RADIUS); }
    public Vec3 getTelegraphPos() {
        return new Vec3(entityData.get(DATA_TELEGRAPH_X), entityData.get(DATA_TELEGRAPH_Y), entityData.get(DATA_TELEGRAPH_Z));
    }

    private void setTelegraph(Vec3 worldPos, float radius) {
        entityData.set(DATA_TELEGRAPH_X, (float) worldPos.x);
        entityData.set(DATA_TELEGRAPH_Y, (float) worldPos.y);
        entityData.set(DATA_TELEGRAPH_Z, (float) worldPos.z);
        entityData.set(DATA_TELEGRAPH_RADIUS, radius);
    }

    private void clearTelegraph() {
        entityData.set(DATA_TELEGRAPH_RADIUS, 0.0F);
    }

    public String getCurrentAnimName() {
        return switch (getAnimId()) {
            case ANIM_DORMANT -> "dormant_kneeling";
            case ANIM_AWAKEN -> "awaken_bootup";
            case ANIM_WALK -> "walk_combat";
            case ANIM_MISSILE_BARRAGE -> "missile_barrage";
            case ANIM_MELEE_3HIT -> "melee_combo_3hit";
            case ANIM_DOUBLE_CLAP -> "double_clap";
            case ANIM_STOMP_SHOCKWAVE -> "stomp_shockwave";
            case ANIM_CYCLONE_WINDUP -> "cyclone_spin_windup";
            case ANIM_CYCLONE_ACTIVE -> "cyclone_spin_active";
            case ANIM_CYCLONE_RECOVER -> "cyclone_spin_recovery";
            case ANIM_LEAP_SLAM -> "leap_slam";
            case ANIM_PHASE2_OVERDRIVE -> "phase2_overdrive";
            case ANIM_STUNNED -> "stunned_collapse";
            case ANIM_DEATH -> "death_shutdown";
            case ANIM_STUN_RECOVERY -> "stunned_recovery";
            case ANIM_OVERDRIVE_FINISHER -> "overdrive_finisher";
            case ANIM_WEAKPOINT_FLINCH -> "weakpoint_flinch";
            default -> "idle_combat";
        };
    }

    @Override
    public boolean isPushable() { return false; }

    @Override
    public void push(Entity entity) {}

    @Override
    protected void doPush(Entity entity) {}

    @Override
    public void push(double x, double y, double z) {}

    @Override
    public void knockback(double strength, double x, double z) {}

    @Override
    public boolean removeWhenFarAway(double dist) { return false; }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossEvent.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossEvent.removePlayer(player);
    }

    private void transitionTo(int newState, int newAnim) {
        entityData.set(DATA_BOSS_STATE, newState);
        entityData.set(DATA_ANIM_ID, newAnim);
        entityData.set(DATA_ANIM_EPOCH, getAnimEpoch() + 1);
        clearTelegraph();
        stateTick = 0;
    }

    private boolean isValidCombatTarget(LivingEntity le) {
        if (le == null || !le.isAlive() || le == this || le instanceof RuinGuardEntity) return false;
        if (le instanceof Player p && (p.isCreative() || p.isSpectator())) return false;
        return le.distanceToSqr(this) <= 36.0D * 36.0D;
    }

    private LivingEntity findCombatTarget(ServerLevel sl) {
        Player nearestPlayer = sl.getNearestPlayer(getX(), getY(), getZ(), 34.0D,
                e -> e instanceof Player p && p.isAlive() && !p.isCreative() && !p.isSpectator());
        if (nearestPlayer != null) {
            return nearestPlayer;
        }

        LivingEntity cur = getTarget();
        if (isValidCombatTarget(cur)) {
            return cur;
        }

        LivingEntity lastAttacker = getLastHurtByMob();
        if (isValidCombatTarget(lastAttacker)) {
            return lastAttacker;
        }

        AABB search = getBoundingBox().inflate(28.0D);
        List<Mob> aggroMobs = sl.getEntitiesOfClass(Mob.class, search,
                m -> isValidCombatTarget(m) && m.getTarget() == this);
        if (!aggroMobs.isEmpty()) {
            Mob closest = aggroMobs.get(0);
            double bestD = closest.distanceToSqr(this);
            for (int i = 1; i < aggroMobs.size(); i++) {
                double d = aggroMobs.get(i).distanceToSqr(this);
                if (d < bestD) {
                    bestD = d;
                    closest = aggroMobs.get(i);
                }
            }
            return closest;
        }
        return null;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        int st = getBossState();
        if (st == STATE_DEATH) return false;
        if (st == STATE_AWAKEN) {
            if (!level().isClientSide() && source.getEntity() instanceof LivingEntity attacker && isValidCombatTarget(attacker)) {
                setLastHurtByMob(attacker);
                setTarget(attacker);
            }
            return false;
        }
        if (st == STATE_DORMANT && !level().isClientSide()) {
            if (source.getEntity() instanceof LivingEntity attacker && isValidCombatTarget(attacker)) {
                setLastHurtByMob(attacker);
                setTarget(attacker);
            }
            awaken((ServerLevel) level());
            return false;
        }

        // Eye & Back-Reactor Weakpoint Detection (Genshin-accurate: front eye + rear core, not exposed while paralyzed)
        if (st == STATE_COMBAT && weakpointImmuneTicks <= 0 && !level().isClientSide()
                && source.getDirectEntity() instanceof Projectile proj) {
            if (proj.getY() >= getY() + 2.25D && proj.getY() <= getY() + 4.15D) {
                Vec3 toProj = new Vec3(proj.getX() - getX(), 0.0D, proj.getZ() - getZ());
                Vec3 forward = Vec3.directionFromRotation(0.0F, getYRot());
                double dot = toProj.lengthSqr() < 0.04D ? 1.0D : toProj.normalize().dot(forward);
                boolean hitFrontEye = dot >= 0.28D;
                boolean hitRearCore = dot <= -0.28D;
                if (hitFrontEye || hitRearCore) {
                    eyeHits++;
                    eyeResetTimer = 140;
                    ServerLevel sl = (ServerLevel) level();
                    sl.playSound(null, getX(), getY() + 3.0D, getZ(), (TeyvatBossesSounds.mechMetalClank() != null ? TeyvatBossesSounds.mechMetalClank() : SoundEvents.IRON_GOLEM_DAMAGE), SoundSource.HOSTILE, 1.15F, 1.72F);
                    sl.playSound(null, getX(), getY() + 3.0D, getZ(), SoundEvents.GLASS_BREAK, SoundSource.HOSTILE, 0.95F, 1.35F);
                    sl.sendParticles(ParticleTypes.CRIT, getX(), getY() + 3.1D, getZ(), 26, 0.45D, 0.45D, 0.45D, 0.16D);
                    sl.sendParticles(ParticleTypes.ELECTRIC_SPARK, getX(), getY() + 3.1D, getZ(), 22, 0.45D, 0.35D, 0.45D, 0.15D);
                    amount *= 1.35F;
                    if (eyeHits >= 2 || (proj instanceof AbstractArrow && eyeHits >= 2)) {
                        weakpointRearHit = false;
                        triggerEyeStun(sl);
                    } else {
                        // First weakpoint hit: trigger mechanical flinch/stagger animation; swivel to attacker if hit from rear core
                        weakpointRearHit = hitRearCore;
                        if (source.getEntity() instanceof LivingEntity attacker) {
                            double dx = attacker.getX() - getX();
                            double dz = attacker.getZ() - getZ();
                            weakpointSwivelTargetYaw = (float) (Math.atan2(dz, dx) * (180.0D / Math.PI)) - 90.0F;
                        } else {
                            weakpointSwivelTargetYaw = getYRot() + 180.0F;
                        }
                        transitionTo(STATE_COMBAT, ANIM_WEAKPOINT_FLINCH);
                        if (source.getEntity() instanceof Player p) {
                            p.displayClientMessage(Component.literal(hitRearCore
                                    ? "✦ Back-Core Weakpoint Cracked (1/2) — Shoot again to paralyze!"
                                    : "✦ Eye Weakpoint Cracked (1/2) — Shoot again to paralyze!")
                                    .withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD), true);
                        }
                    }
                }
            }
        }

        // 45% Physical Resistance while active; 1.5x bonus damage while Eye-Stunned
        if (st == STATE_STUNNED) {
            amount *= 1.50F;
        } else if (!source.is(DamageTypeTags.BYPASSES_ARMOR) && !source.is(DamageTypeTags.WITCH_RESISTANT_TO)) {
            amount *= 0.55F;
        }

        return super.hurt(source, amount);
    }

    private void playAttackSpeech(ServerLevel sl, float chance) {
        if (speechCooldownTicks > 0 || random.nextFloat() >= chance) return;
        net.minecraft.sounds.SoundEvent speech = TeyvatBossesSounds.ruinGuardSpeech();
        if (speech != null) {
            float pitch = 0.96F + random.nextFloat() * 0.08F;
            sl.playSound(null, getX(), getY() + 2.4D, getZ(), speech, SoundSource.HOSTILE, 1.45F, pitch);
            speechCooldownTicks = 60;
        }
    }

    private void playPowerDownSpeech(ServerLevel sl) {
        net.minecraft.sounds.SoundEvent powerDown = TeyvatBossesSounds.ruinGuardPowerDown();
        if (powerDown != null) {
            float pitch = 0.96F + random.nextFloat() * 0.08F;
            sl.playSound(null, getX(), getY() + 2.4D, getZ(), powerDown, SoundSource.HOSTILE, 1.55F, pitch);
            speechCooldownTicks = 60;
        }
    }

    private void triggerEyeStun(ServerLevel sl) {
        eyeHits = 0;
        eyeResetTimer = 0;
        transitionTo(STATE_STUNNED, ANIM_STUNNED);
        sl.playSound(null, getX(), getY() + 2.5D, getZ(), SoundEvents.IRON_GOLEM_DAMAGE, SoundSource.HOSTILE, 1.6F, 0.62F);
        sl.playSound(null, getX(), getY() + 2.5D, getZ(), SoundEvents.BEACON_DEACTIVATE, SoundSource.HOSTILE, 1.35F, 0.78F);
        playPowerDownSpeech(sl);
        sl.sendParticles(ParticleTypes.ELECTRIC_SPARK, getX(), getY() + 3.0D, getZ(), 36, 0.9D, 0.7D, 0.9D, 0.12D);
        for (Player p : sl.getEntitiesOfClass(Player.class, getBoundingBox().inflate(32.0D))) {
            p.displayClientMessage(Component.translatable("message.velorise_teyvat_bosses.ruin_guard.weakpoint_stun")
                    .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD), true);
        }
    }

    @Override
    protected void tickDeath() {
        this.deathTime++;
        if (this.deathTime >= DEATH_DURATION_TICKS && !this.level().isClientSide() && !this.isRemoved()) {
            this.level().broadcastEntityEvent(this, (byte) 60);
            this.remove(Entity.RemovalReason.KILLED);
        }
    }

    @Override
    public void die(DamageSource cause) {
        if (!level().isClientSide() && !customDeathHandled) {
            customDeathHandled = true;
            transitionTo(STATE_DEATH, ANIM_DEATH);
            if (level() instanceof ServerLevel sl) {
                sl.playSound(null, getX(), getY(), getZ(), SoundEvents.IRON_GOLEM_DEATH, SoundSource.HOSTILE, 1.6F, 0.58F);
                sl.playSound(null, getX(), getY(), getZ(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 1.4F, 0.72F);
                playPowerDownSpeech(sl);
                sl.sendParticles(ParticleTypes.EXPLOSION_EMITTER, getX(), getY() + 1.8D, getZ(), 2, 0.6D, 0.6D, 0.6D, 0.0D);
                if (!cause.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
                    Vec3 pos = arenaInit ? arenaCenter : position();
                    TrounceBlossomEntity.spawn(sl, BossRespawnAnchorEntity.BOSS_RUIN_GUARD, pos, savedSpawnYaw);
                }
            }
        }
        super.die(cause);
    }

    private void awaken(ServerLevel sl) {
        noTargetTicks = 0;
        transitionTo(STATE_AWAKEN, ANIM_AWAKEN);
        sl.playSound(null, getX(), getY() + 2.0D, getZ(), SoundEvents.BEACON_ACTIVATE, SoundSource.HOSTILE, 1.4F, 0.75F);
        sl.playSound(null, getX(), getY() + 2.0D, getZ(), SoundEvents.IRON_GOLEM_REPAIR, SoundSource.HOSTILE, 1.5F, 0.65F);
        playAttackSpeech(sl, 1.0F);
    }

    private void resetToDormant() {
        noTargetTicks = 0;
        eyeHits = 0;
        eyeResetTimer = 0;
        weakpointImmuneTicks = 0;
        pendingPhase2 = false;
        finisherCooldown = 0;
        speechCooldownTicks = 0;
        lastAttack = -1;
        attackCooldown = 40;
        setTarget(null);
        setDeltaMovement(Vec3.ZERO);
        setPos(arenaCenter.x, arenaCenter.y, arenaCenter.z);
        setYRot(savedSpawnYaw);
        yBodyRot = savedSpawnYaw;
        yHeadRot = savedSpawnYaw;
        setHealth(getMaxHealth());
        entityData.set(DATA_PHASE2, false);
        transitionTo(STATE_DORMANT, ANIM_DORMANT);
    }

    @Override
    public void tick() {
        super.tick();
        if (!arenaInit) {
            arenaCenter = position();
            savedSpawnYaw = getYRot();
            arenaInit = true;
        }

        if (level().isClientSide()) {
            int curId = getAnimId();
            int curEp = getAnimEpoch();
            if (curId != lastSyncedAnim || curEp != lastSyncedEpoch) {
                lastSyncedAnim = curId;
                lastSyncedEpoch = curEp;
                clientAnimTick = 0;
            } else {
                clientAnimTick++;
            }
            return;
        }

        ServerLevel sl = (ServerLevel) level();
        stateTick++;
        if (eyeResetTimer > 0 && --eyeResetTimer == 0) {
            if (eyeHits > 0) {
                // Genshin Wiki: protective runes briefly prevent immediate re-disable even if only Hit 1 landed
                weakpointImmuneTicks = Math.max(weakpointImmuneTicks, 50);
            }
            eyeHits = 0;
        }
        if (weakpointImmuneTicks > 0) {
            weakpointImmuneTicks--;
        }
        if (finisherCooldown > 0) {
            finisherCooldown--;
        }
        if (speechCooldownTicks > 0) {
            speechCooldownTicks--;
        }

        int st = getBossState();
        if (st == STATE_DEATH) {
            setDeltaMovement(0.0D, getDeltaMovement().y, 0.0D);
            bossEvent.setVisible(false);
            return;
        }

        LivingEntity combatTarget = findCombatTarget(sl);

        if (st == STATE_DORMANT) {
            bossEvent.setVisible(false);
            if (combatTarget != null && combatTarget.distanceToSqr(this) <= 13.0D * 13.0D) {
                setTarget(combatTarget);
                awaken(sl);
            }
            return;
        }

        bossEvent.setVisible(true);
        updateBossBar();

        if (combatTarget == null) {
            setDeltaMovement(0.0D, getDeltaMovement().y, 0.0D);
            // Cleanly cancel any lingering attack animation into idle while waiting for reset
            if (st == STATE_COMBAT && getAnimId() != ANIM_IDLE && onGround()) {
                transitionTo(STATE_COMBAT, ANIM_IDLE);
            }
            if (++noTargetTicks > 180) {
                resetToDormant();
            }
            return;
        }
        noTargetTicks = 0;
        setTarget(combatTarget);

        // Queue Phase 2 Overdrive at <= 50% HP; only trigger when grounded & not mid-attack!
        if (!isPhase2() && !pendingPhase2 && getHealth() <= getMaxHealth() * 0.5F) {
            pendingPhase2 = true;
        }
        if (pendingPhase2 && st == STATE_COMBAT && onGround()
                && (getAnimId() == ANIM_IDLE || getAnimId() == ANIM_WALK)) {
            pendingPhase2 = false;
            entityData.set(DATA_PHASE2, true);
            transitionTo(STATE_COMBAT, ANIM_PHASE2_OVERDRIVE);
            sl.playSound(null, getX(), getY() + 2.0D, getZ(), SoundEvents.WITHER_SPAWN, SoundSource.HOSTILE, 1.3F, 0.72F);
            speechCooldownTicks = 0;
            playAttackSpeech(sl, 1.0F);
            return;
        }

        switch (st) {
            case STATE_AWAKEN -> {
                faceTarget(combatTarget, 5.0F);
                if (stateTick == 17) sl.sendParticles(ParticleTypes.CLOUD, getX(), getY() + 2.6D, getZ(), 18, 0.8D, 0.6D, 0.8D, 0.06D);
                if (stateTick == 40) sl.playSound(null, getX(), getY(), getZ(), SoundEvents.IRON_GOLEM_STEP, SoundSource.HOSTILE, 1.4F, 0.65F);
                if (stateTick >= 52) {
                    transitionTo(STATE_COMBAT, ANIM_IDLE);
                    attackCooldown = 40;
                }
            }
            case STATE_STUNNED -> {
                setDeltaMovement(0.0D, getDeltaMovement().y, 0.0D);
                if (getAnimId() == ANIM_STUNNED) {
                    if (stateTick == 22) {
                        sl.playSound(null, getX(), getY(), getZ(), SoundEvents.IRON_GOLEM_STEP, SoundSource.HOSTILE, 1.5F, 0.48F);
                        spawnGroundDustRing(sl, 3.6D, 24);
                    }
                    if (stateTick % 12 == 0) {
                        sl.sendParticles(ParticleTypes.ELECTRIC_SPARK, getX(), getY() + 2.0D, getZ(), 8, 0.7D, 0.5D, 0.7D, 0.05D);
                    }
                    if (stateTick >= STUN_COLLAPSE_TICKS) {
                        sl.playSound(null, getX(), getY() + 2.0D, getZ(), SoundEvents.BEACON_ACTIVATE, SoundSource.HOSTILE, 1.25F, 0.85F);
                        sl.playSound(null, getX(), getY() + 1.5D, getZ(), SoundEvents.IRON_GOLEM_REPAIR, SoundSource.HOSTILE, 1.35F, 0.68F);
                        transitionTo(STATE_STUNNED, ANIM_STUN_RECOVERY);
                    }
                } else if (getAnimId() == ANIM_STUN_RECOVERY) {
                    faceTarget(combatTarget, 4.0F);
                    if (stateTick == 18) {
                        sl.playSound(null, getX(), getY(), getZ(), SoundEvents.IRON_GOLEM_STEP, SoundSource.HOSTILE, 1.3F, 0.62F);
                    }
                    if (stateTick >= STUN_RECOVERY_TICKS) {
                        weakpointImmuneTicks = WEAKPOINT_IMMUNE_TICKS;
                        transitionTo(STATE_COMBAT, ANIM_IDLE);
                        attackCooldown = 42;
                    }
                }
            }
            case STATE_COMBAT -> tickCombat(sl, combatTarget);
        }

        // Soft leash (Cataclysm style): allow free combat chase & leaps; only reset if dragged >56 blocks from spawn
        if (st == STATE_COMBAT && getAnimId() != ANIM_LEAP_SLAM && arenaInit) {
            double dx = getX() - arenaCenter.x;
            double dz = getZ() - arenaCenter.z;
            if (dx * dx + dz * dz > ARENA_SOFT_LEASH_RADIUS * ARENA_SOFT_LEASH_RADIUS) {
                resetToDormant();
            }
        }
    }

    private void faceTarget(LivingEntity target, float maxTurn) {
        double dx = target.getX() - getX();
        double dz = target.getZ() - getZ();
        float targetYaw = (float) (Math.atan2(dz, dx) * (180.0D / Math.PI)) - 90.0F;
        float newYaw = Mth.approachDegrees(getYRot(), targetYaw, maxTurn);
        setYRot(newYaw);
        yBodyRot = newYaw;
        yHeadRot = newYaw;
    }


    private int destroyBlocksTick = 0;

    /**
     * Anti-trap block breaking (Wither/Cataclysm style): after a 1-second (20-tick) delay
     * when pinned against walls or attacked while obstructed, shatters obstructing blocks
     * intersecting the boss's hitbox (respecting mobGriefing, preserving chests/BlockEntities,
     * Obsidian, Bedrock, and the floor underneath).
     */
    private void tickAntiTrapBlockBreak(ServerLevel sl) {
        if ((this.horizontalCollision || this.isInWall()) && destroyBlocksTick <= 0) {
            destroyBlocksTick = 8;
        }
        if (destroyBlocksTick <= 0) return;
        destroyBlocksTick--;
        if (destroyBlocksTick != 0) return;
        if (!sl.getGameRules().getBoolean(net.minecraft.world.level.GameRules.RULE_MOBGRIEFING)) return;

        AABB box = getBoundingBox().inflate(0.28D, 0.05D, 0.28D);
        int minX = Mth.floor(box.minX);
        int minY = Mth.floor(getY());
        int minZ = Mth.floor(box.minZ);
        int maxX = Mth.floor(box.maxX);
        int maxY = Mth.floor(box.maxY);
        int maxZ = Mth.floor(box.maxZ);

        boolean brokeAny = false;
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                for (int z = minZ; z <= maxZ; z++) {
                    pos.set(x, y, z);
                    BlockState state = sl.getBlockState(pos);
                    if (state.isAir() || !state.getFluidState().isEmpty() || state.hasBlockEntity()) continue;
                    float hardness = state.getDestroySpeed(sl, pos);
                    if (hardness < 0.0F || hardness >= 50.0F) continue; // Preserve Bedrock, Obsidian, Ancient Debris
                    if (sl.destroyBlock(pos, true, this)) {
                        brokeAny = true;
                    }
                }
            }
        }
        if (brokeAny) {
            sl.levelEvent(null, 1022, blockPosition(), 0);
        }
    }

    private void tickCombat(ServerLevel sl, LivingEntity target) {
        tickAntiTrapBlockBreak(sl);
        int anim = getAnimId();
        double dist = Math.hypot(target.getX() - getX(), target.getZ() - getZ());

        if (anim == ANIM_IDLE || anim == ANIM_WALK) {
            double dxT = target.getX() - getX();
            double dzT = target.getZ() - getZ();
            float desiredYaw = (float) (Math.atan2(dzT, dxT) * (180.0D / Math.PI)) - 90.0F;
            float yawDiff = Math.abs(Mth.wrapDegrees(desiredYaw - getYRot()));

            faceTarget(target, isPhase2() ? 7.5F : 6.2F);
            boolean shouldWalk = (anim == ANIM_WALK) ? (dist > 3.2D) : (dist > 4.2D);
            boolean needsPivotStep = (anim == ANIM_WALK) ? (yawDiff > 7.5F) : (yawDiff > 16.0F);
            Vec3 cur = getDeltaMovement();

            if (shouldWalk || needsPivotStep) {
                if (anim != ANIM_WALK) transitionTo(STATE_COMBAT, ANIM_WALK);
                if (shouldWalk) {
                    // Path-only A* steering (no MoveControl velocity stacking!) + heavy iron-mech stride speed
                    if (getNavigation() instanceof BossGroundNavigation bnav) {
                        if (stateTick % 6 == 0 || bnav.isDone()) {
                            bnav.computePathTo(target);
                        }
                    }
                    Vec3 dir = (getNavigation() instanceof BossGroundNavigation bnav)
                            ? bnav.getSteerDirection(target.position())
                            : new Vec3(dxT, 0.0D, dzT).normalize();
                    double stepPhase = (stateTick % 24) / 24.0D;
                    double cadence = 0.84D + 0.16D * Math.sin(stepPhase * Math.PI * 2.0D);
                    double speed = (isPhase2() ? 0.145D : 0.125D) * cadence;
                    double jumpY = cur.y;
                    if (this.horizontalCollision && this.onGround()) {
                        jumpY = 0.46D; // Step/hop cleanly out of 2-block craters & ledges
                    }
                    setDeltaMovement(
                            Mth.lerp(0.42D, cur.x, dir.x * speed),
                            jumpY,
                            Mth.lerp(0.42D, cur.z, dir.z * speed)
                    );
                } else {
                    getNavigation().stop();
                    setDeltaMovement(cur.x * 0.32D, cur.y, cur.z * 0.32D);
                }
                if (stateTick % 24 == 1) {
                    net.minecraft.sounds.SoundEvent whir = TeyvatBossesSounds.mechServoWhir();
                    if (whir != null) sl.playSound(null, getX(), getY() + 1.6D, getZ(), whir, SoundSource.HOSTILE, 0.58F, 0.88F);
                    net.minecraft.sounds.SoundEvent creak = TeyvatBossesSounds.mechJointCreak();
                    if (creak != null) sl.playSound(null, getX(), getY() + 1.4D, getZ(), creak, SoundSource.HOSTILE, 0.52F, 0.84F);
                } else if (stateTick % 24 == 12) {
                    sl.playSound(null, getX(), getY(), getZ(), SoundEvents.IRON_GOLEM_STEP, SoundSource.HOSTILE, shouldWalk ? 1.40F : 0.90F, 0.48F);
                    net.minecraft.sounds.SoundEvent clank = TeyvatBossesSounds.mechMetalClank();
                    if (clank != null) sl.playSound(null, getX(), getY() + 0.3D, getZ(), clank, SoundSource.HOSTILE, shouldWalk ? 0.78F : 0.45F, 0.86F);
                    net.minecraft.sounds.SoundEvent hiss = TeyvatBossesSounds.mechHydraulicHiss();
                    if (hiss != null && shouldWalk) sl.playSound(null, getX(), getY() + 0.6D, getZ(), hiss, SoundSource.HOSTILE, 0.45F, 0.92F);
                    sl.sendParticles(ParticleTypes.POOF, getX(), getY() + 0.08D, getZ(), shouldWalk ? 8 : 3, 0.55D, 0.05D, 0.55D, 0.02D);
                    if (shouldWalk) sl.sendParticles(ParticleTypes.CRIT, getX(), getY() + 0.15D, getZ(), 5, 0.45D, 0.08D, 0.45D, 0.06D);
                }
            } else {
                if (anim != ANIM_IDLE) transitionTo(STATE_COMBAT, ANIM_IDLE);
                setDeltaMovement(cur.x * 0.35D, cur.y, cur.z * 0.35D);
            }

            if (--attackCooldown <= 0) {
                chooseNextAttack(sl, target, dist);
            }
            return;
        }

        switch (anim) {
            case ANIM_MISSILE_BARRAGE -> tickMissileBarrage(sl, target);
            case ANIM_MELEE_3HIT -> tickMelee3Hit(sl, target);
            case ANIM_DOUBLE_CLAP -> tickDoubleClap(sl, target);
            case ANIM_STOMP_SHOCKWAVE -> tickStompShockwave(sl, target);
            case ANIM_CYCLONE_WINDUP -> {
                faceTarget(target, 5.5F);
                setDeltaMovement(getDeltaMovement().x * 0.7D, getDeltaMovement().y, getDeltaMovement().z * 0.7D);
                if (stateTick == 1) {
                    sl.playSound(null, getX(), getY() + 2.0D, getZ(), SoundEvents.IRON_GOLEM_REPAIR, SoundSource.HOSTILE, 1.4F, 0.64F);
                    playAttackSpeech(sl, 0.80F);
                } else if (stateTick == 22) {
                    sl.playSound(null, getX(), getY() + 1.8D, getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.HOSTILE, 1.3F, 0.58F);
                }
                if (stateTick >= 28) transitionTo(STATE_COMBAT, ANIM_CYCLONE_ACTIVE);
            }
            case ANIM_CYCLONE_ACTIVE -> tickCycloneActive(sl, target);
            case ANIM_CYCLONE_RECOVER -> {
                double drag = (stateTick < 22) ? 0.88D : 0.62D;
                setDeltaMovement(getDeltaMovement().x * drag, getDeltaMovement().y, getDeltaMovement().z * drag);
                if (stateTick == 22) {
                    sl.playSound(null, getX(), getY(), getZ(), SoundEvents.IRON_GOLEM_STEP, SoundSource.HOSTILE, 1.5F, 0.50F);
                    sl.playSound(null, getX(), getY() + 1.8D, getZ(), SoundEvents.IRON_GOLEM_REPAIR, SoundSource.HOSTILE, 1.05F, 0.54F);
                    sl.sendParticles(ParticleTypes.POOF, getX(), getY() + 0.12D, getZ(), 12, 0.85D, 0.08D, 0.85D, 0.03D);
                }
                if (stateTick >= 44) {
                    if (isPhase2() && finisherCooldown <= 0) {
                        startOverdriveFinisher(sl, target);
                    } else if (isPhase2()) {
                        startLeapSlam(sl, target);
                    } else {
                        transitionTo(STATE_COMBAT, ANIM_IDLE);
                        attackCooldown = 48;
                    }
                }
            }
            case ANIM_LEAP_SLAM -> tickLeapSlam(sl, target);
            case ANIM_PHASE2_OVERDRIVE -> tickPhase2Overdrive(sl);
            case ANIM_OVERDRIVE_FINISHER -> tickOverdriveFinisher(sl, target);
            case ANIM_WEAKPOINT_FLINCH -> tickWeakpointFlinch(sl, target);
            default -> transitionTo(STATE_COMBAT, ANIM_IDLE);
        }
    }

    /**
     * Range-gated Cataclysm-style attack selector: never whiffs 5.2m melee attacks at 6–9m!
     * <ul>
     *   <li>Close Melee (&le; 5.2m &amp; |dy| &le; 2.8m): 3-Hit Combo, Double Clap, Stomp Shockwave, Cyclone.</li>
     *   <li>Mid Gap-Closer (5.2–13.5m): Leap Slam directly onto target, Cyclone chase, or Missile Barrage.</li>
     *   <li>Long Range (&gt; 13.5m): Leap Slam gap-close or Homing Missile Barrage.</li>
     * </ul>
     */
    private void chooseNextAttack(ServerLevel sl, LivingEntity target, double dist) {
        getNavigation().stop();
        double dy = Math.abs(target.getY() - getY());
        if (isPhase2() && finisherCooldown <= 0 && dist <= 7.6D && dy <= 3.0D && random.nextFloat() < 0.40F) {
            lastAttack = ANIM_OVERDRIVE_FINISHER;
            startOverdriveFinisher(sl, target);
            return;
        }

        int chosen;
        if (dist <= 5.2D && dy <= 2.8D) {
            int[] closePool = {ANIM_MELEE_3HIT, ANIM_DOUBLE_CLAP, ANIM_STOMP_SHOCKWAVE, ANIM_CYCLONE_WINDUP};
            chosen = closePool[random.nextInt(closePool.length)];
            if (chosen == lastAttack) {
                chosen = (lastAttack == ANIM_MELEE_3HIT) ? ANIM_STOMP_SHOCKWAVE : ANIM_MELEE_3HIT;
            }
        } else if (dist <= 13.5D) {
            // Gap-closers ONLY — never whiff stationary melee into thin air!
            float r = random.nextFloat();
            if (dy > 3.0D) {
                chosen = (r < 0.55F) ? ANIM_LEAP_SLAM : ANIM_MISSILE_BARRAGE;
            } else if (r < 0.50F) {
                chosen = ANIM_LEAP_SLAM;
            } else if (r < 0.82F) {
                chosen = ANIM_CYCLONE_WINDUP;
            } else {
                chosen = ANIM_MISSILE_BARRAGE;
            }
            if (chosen == lastAttack) {
                chosen = (lastAttack == ANIM_LEAP_SLAM) ? ANIM_CYCLONE_WINDUP : ANIM_LEAP_SLAM;
            }
        } else {
            chosen = (random.nextFloat() < 0.55F) ? ANIM_MISSILE_BARRAGE : ANIM_LEAP_SLAM;
            if (chosen == lastAttack) {
                chosen = (lastAttack == ANIM_MISSILE_BARRAGE) ? ANIM_LEAP_SLAM : ANIM_MISSILE_BARRAGE;
            }
        }

        lastAttack = chosen;
        if (chosen == ANIM_LEAP_SLAM) {
            startLeapSlam(sl, target);
        } else {
            transitionTo(STATE_COMBAT, chosen);
        }
    }

    private void startLeapSlam(ServerLevel sl, LivingEntity target) {
        leapStart = position();
        Vec3 clamped = clampLeapDestination(target);
        double groundY = sampleGroundY(sl, clamped.x, clamped.z, target.getY());
        leapTarget = new Vec3(clamped.x, groundY, clamped.z);
        transitionTo(STATE_COMBAT, ANIM_LEAP_SLAM);
        setTelegraph(leapTarget, 6.0F);
        if (TeyvatBossesSounds.ruinGuardSpeech() != null) {
            playAttackSpeech(sl, 0.85F);
        } else {
            sl.playSound(null, getX(), getY() + 2.0D, getZ(), SoundEvents.RAVAGER_ROAR, SoundSource.HOSTILE, 1.2F, 0.65F);
        }
        sl.playSound(null, getX(), getY() + 1.5D, getZ(), SoundEvents.IRON_GOLEM_REPAIR, SoundSource.HOSTILE, 1.25F, 0.58F);
    }

    private void startOverdriveFinisher(ServerLevel sl, LivingEntity target) {
        finisherCooldown = 260;
        faceTarget(target, 180.0F);
        transitionTo(STATE_COMBAT, ANIM_OVERDRIVE_FINISHER);
        double groundY = sampleGroundY(sl, getX(), getZ(), getY());
        setTelegraph(new Vec3(getX(), groundY, getZ()), 7.8F);
        sl.playSound(null, getX(), getY() + 2.4D, getZ(), SoundEvents.WITHER_SPAWN, SoundSource.HOSTILE, 1.2F, 0.85F);
        sl.playSound(null, getX(), getY() + 2.0D, getZ(), SoundEvents.BEACON_ACTIVATE, SoundSource.HOSTILE, 1.4F, 0.68F);
    }

    /**
     * Samples the exact solid ground surface Y near referenceY at (x, z) so jump slams
     * and ground telegraphs always land flush on hills, slopes, or valleys.
     */
    private double sampleGroundY(ServerLevel sl, double x, double z, double referenceY) {
        int bx = Mth.floor(x);
        int bz = Mth.floor(z);
        int startY = Mth.floor(referenceY) + 4;
        int minY = Math.max(sl.getMinBuildHeight(), Mth.floor(referenceY) - 8);
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int y = startY; y >= minY; y--) {
            pos.set(bx, y, bz);
            BlockState state = sl.getBlockState(pos);
            if (!state.getCollisionShape(sl, pos).isEmpty()) {
                double topY = y + state.getCollisionShape(sl, pos).max(Direction.Axis.Y);
                if (sl.getBlockState(pos.above()).getCollisionShape(sl, pos.above()).isEmpty()
                        && sl.getBlockState(pos.above(2)).getCollisionShape(sl, pos.above(2)).isEmpty()
                        && sl.getBlockState(pos.above(3)).getCollisionShape(sl, pos.above(3)).isEmpty()) {
                    return topY;
                }
            }
        }
        return referenceY;
    }


    /**
     * Advances a parabolic leap step using Minecraft's swept-AABB collision solver so
     * Ruin Guard never clips through walls, pillars, roofs, or ceilings.
     */
    private void moveLeapStep(double nx, double ny, double nz) {
        Vec3 delta = new Vec3(nx - getX(), ny - getY(), nz - getZ());
        this.move(MoverType.SELF, delta);
        this.setDeltaMovement(Vec3.ZERO);
        this.fallDistance = 0.0F;
    }

    /**
     * Settles Ruin Guard onto the ground on the landing tick using collision-aware movement
     * rather than teleporting through obstacles into leapTarget.
     */
    private void finishLeapLanding(double tx, double ty, double tz) {
        Vec3 toTarget = new Vec3(tx - getX(), ty - getY(), tz - getZ());
        if (toTarget.horizontalDistanceSqr() > 16.0D) {
            this.move(MoverType.SELF, new Vec3(0.0D, Math.min(-0.5D, ty - getY()), 0.0D));
        } else {
            this.move(MoverType.SELF, toTarget);
        }
        this.setDeltaMovement(Vec3.ZERO);
        this.fallDistance = 0.0F;
    }

    /**
     * Computes a realistic leap destination directly onto the target (with light velocity leading,
     * capped to 16 blocks max leap distance — no invisible 22m arena wall blocking jumps!).
     */
    private Vec3 clampLeapDestination(LivingEntity target) {
        Vec3 lead = target.position().add(target.getDeltaMovement().multiply(4.0D, 0.0D, 4.0D));
        Vec3 delta = new Vec3(lead.x - getX(), 0.0D, lead.z - getZ());
        double dist = delta.length();
        double maxLeap = 16.0D;
        if (dist > maxLeap && dist > 0.01D) {
            Vec3 capped = position().add(delta.scale(maxLeap / dist));
            return new Vec3(capped.x, lead.y, capped.z);
        }
        return lead;
    }

    private void tickMissileBarrage(ServerLevel sl, LivingEntity target) {
        if (stateTick < 17 || stateTick > 45) {
            faceTarget(target, 5.0F);
        }
        setDeltaMovement(getDeltaMovement().x * 0.7D, getDeltaMovement().y, getDeltaMovement().z * 0.7D);
        if (stateTick == 8 || stateTick == 46) {
            sl.playSound(null, getX(), getY() + 2.4D, getZ(), SoundEvents.IRON_GOLEM_REPAIR, SoundSource.HOSTILE, 1.35F, 0.65F);
            if (stateTick == 8) {
                playAttackSpeech(sl, 0.75F);
            }
        }
        int totalMissiles = 6;
        if (stateTick >= 20 && stateTick < 20 + totalMissiles * 3 && (stateTick - 20) % 3 == 0) {
            int idx = (stateTick - 20) / 3;
            launchPodMissile(sl, target, idx, isPhase2() ? 15.5F : 13.0F);
        }
        if (stateTick >= 64) {
            transitionTo(STATE_COMBAT, ANIM_IDLE);
            attackCooldown = 40;
        }
    }

    private void launchPodMissile(ServerLevel sl, LivingEntity target, int idx, float damage) {
        double side = (idx % 2 == 0) ? -1.1D : 1.1D;
        float yawRad = getYRot() * Mth.DEG_TO_RAD;
        Vec3 right = new Vec3(Math.cos(yawRad), 0.0D, Math.sin(yawRad));
        Vec3 forward = Vec3.directionFromRotation(0.0F, getYRot());
        Vec3 origin = position().add(right.scale(side)).add(0.0D, 3.65D, 0.0D);
        Vec3 launchVel = right.scale(side * 0.25D)
                .add(forward.scale(0.18D))
                .add(0.0D, 0.52D + (idx % 3) * 0.06D, 0.0D);
        RuinMissileProjectile.launch(sl, this, target, origin, launchVel, damage);
        sl.sendParticles(ParticleTypes.FLAME, origin.x, origin.y, origin.z, 8, 0.15D, 0.15D, 0.15D, 0.05D);
    }

    /**
     * Exact frame-matched Genshin Ruin Guard 3-Hit Combo (128 ticks = 6.40s):
     * Fist-clap spark opener @ tick 7 (0.35s), Hit 1 sweep @ tick 28 (1.40s),
     * Hit 2 360-deg spin-through cleave @ tick 50 (2.50s), crouch 58..68,
     * feet-first parabolic jump 69..85 (0.80s), ground slam @ tick 86 (4.30s),
     * crater hold 86..103, slow hydraulic stand-up 103..128.
     */
    private void tickMelee3Hit(ServerLevel sl, LivingEntity target) {
        Vec3 forward = Vec3.directionFromRotation(0.0F, getYRot());
        if (stateTick < 24 || (stateTick > 32 && stateTick < 45) || (stateTick >= 58 && stateTick < 69)) {
            faceTarget(target, 6.5F);
        }

        Vec3 cur = getDeltaMovement();
        if (stateTick >= 25 && stateTick <= 28) {
            setDeltaMovement(Mth.lerp(0.48D, cur.x, forward.x * 0.34D), cur.y, Mth.lerp(0.48D, cur.z, forward.z * 0.34D));
        } else if (stateTick >= 47 && stateTick <= 50) {
            setDeltaMovement(Mth.lerp(0.48D, cur.x, forward.x * 0.38D), cur.y, Mth.lerp(0.48D, cur.z, forward.z * 0.38D));
        } else if (stateTick >= 58 && stateTick < 69) {
            setDeltaMovement(cur.x * 0.55D, cur.y, cur.z * 0.55D);
            if (stateTick == 59) {
                sl.playSound(null, getX(), getY() + 1.5D, getZ(), SoundEvents.IRON_GOLEM_REPAIR, SoundSource.HOSTILE, 1.25F, 0.56F);
            }
            if (stateTick >= 59 && stateTick <= 67) {
                Vec3 toT = new Vec3(target.getX() - getX(), 0.0D, target.getZ() - getZ());
                double dist = toT.length();
                double maxHopDist = 6.5D;
                Vec3 rawDest = (dist > maxHopDist && dist > 0.01D)
                        ? position().add(toT.scale(maxHopDist / dist))
                        : new Vec3(target.getX(), target.getY(), target.getZ());
                double groundY = sampleGroundY(sl, rawDest.x, rawDest.z, target.getY());
                leapTarget = new Vec3(rawDest.x, groundY, rawDest.z);
                setTelegraph(leapTarget, 5.2F);
            }
            leapStart = position();
        } else if (stateTick >= 69 && stateTick <= 85) {
            if (stateTick == 69) {
                sl.playSound(null, getX(), getY() + 1.0D, getZ(), SoundEvents.IRON_GOLEM_REPAIR, SoundSource.HOSTILE, 1.35F, 0.70F);
                sl.playSound(null, getX(), getY(), getZ(), SoundEvents.IRON_GOLEM_STEP, SoundSource.HOSTILE, 1.5F, 0.48F);
                sl.sendParticles(ParticleTypes.POOF, getX(), getY() + 0.15D, getZ(), 14, 0.8D, 0.08D, 0.8D, 0.05D);
            }
            float f = (stateTick - 69) / 16.0F;
            float horizF = f * f * (3.0F - 2.0F * f);
            double shaped = Math.sin(Math.pow(f, 0.80D) * Math.PI);
            double arcY = Math.pow(Math.max(0.0D, shaped), 0.90D) * 4.0D;
            double nx = Mth.lerp(horizF, leapStart.x, leapTarget.x);
            double nz = Mth.lerp(horizF, leapStart.z, leapTarget.z);
            double ny = Mth.lerp(f, leapStart.y, leapTarget.y) + arcY;
            moveLeapStep(nx, ny, nz);
        } else if (stateTick == 86) {
            clearTelegraph();
            finishLeapLanding(leapTarget.x, leapTarget.y, leapTarget.z);
            dealRadialDamage(sl, 5.4D, 27.0F, 1.35D, 0.62D);
            sl.playSound(null, getX(), getY(), getZ(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 1.75F, 0.56F);
            sl.playSound(null, getX(), getY(), getZ(), SoundEvents.IRON_GOLEM_DAMAGE, SoundSource.HOSTILE, 1.5F, 0.50F);
            sl.playSound(null, getX(), getY(), getZ(), (TeyvatBossesSounds.mechMetalClank() != null ? TeyvatBossesSounds.mechMetalClank() : SoundEvents.IRON_GOLEM_DAMAGE), SoundSource.HOSTILE, 1.15F, 0.46F);
            sl.sendParticles(ParticleTypes.EXPLOSION, getX(), getY() + 0.4D, getZ(), 5, 2.0D, 0.25D, 2.0D, 0.0D);
            spawnGroundDustRing(sl, 4.8D, 36);
        } else {
            setDeltaMovement(cur.x * 0.65D, cur.y, cur.z * 0.65D);
        }

        if (stateTick == 7) {
            sl.playSound(null, getX(), getY() + 2.0D, getZ(), (TeyvatBossesSounds.mechMetalClank() != null ? TeyvatBossesSounds.mechMetalClank() : SoundEvents.IRON_GOLEM_DAMAGE), SoundSource.HOSTILE, 0.85F, 0.82F);
            playAttackSpeech(sl, 0.75F);
            Vec3 clapSpot = position().add(forward.scale(1.4D));
            sl.sendParticles(ParticleTypes.FLAME, clapSpot.x, clapSpot.y + 1.85D, clapSpot.z, 10, 0.25D, 0.2D, 0.25D, 0.05D);
            sl.sendParticles(ParticleTypes.CRIT, clapSpot.x, clapSpot.y + 1.85D, clapSpot.z, 8, 0.3D, 0.25D, 0.3D, 0.12D);
        } else if (stateTick == 21 || stateTick == 42) {
            sl.playSound(null, getX(), getY() + 1.8D, getZ(), SoundEvents.IRON_GOLEM_REPAIR, SoundSource.HOSTILE, 1.15F, 0.60F);
        } else if (stateTick == 28) {
            dealArcDamage(sl, 5.2D, 155.0F, 18.5F, 1.00D);
            sl.playSound(null, getX(), getY() + 1.6D, getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.HOSTILE, 1.65F, 0.52F);
            sl.playSound(null, getX(), getY() + 1.6D, getZ(), SoundEvents.IRON_GOLEM_ATTACK, SoundSource.HOSTILE, 1.35F, 0.64F);
            Vec3 front = position().add(forward.scale(2.8D));
            sl.sendParticles(ParticleTypes.SWEEP_ATTACK, front.x, front.y + 1.6D, front.z, 3, 1.2D, 0.2D, 1.2D, 0.0D);
        } else if (stateTick == 50) {
            dealArcDamage(sl, 5.4D, 165.0F, 21.0F, 1.10D);
            sl.playSound(null, getX(), getY() + 1.6D, getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.HOSTILE, 1.75F, 0.45F);
            sl.playSound(null, getX(), getY() + 1.6D, getZ(), SoundEvents.IRON_GOLEM_ATTACK, SoundSource.HOSTILE, 1.45F, 0.58F);
            Vec3 front = position().add(forward.scale(2.9D));
            sl.sendParticles(ParticleTypes.SWEEP_ATTACK, front.x, front.y + 1.5D, front.z, 4, 1.3D, 0.25D, 1.3D, 0.0D);
        } else if (stateTick == 103) {
            sl.playSound(null, getX(), getY() + 1.4D, getZ(), SoundEvents.IRON_GOLEM_REPAIR, SoundSource.HOSTILE, 0.9F, 0.52F);
        }

        if (stateTick >= 128) {
            transitionTo(STATE_COMBAT, ANIM_IDLE);
            attackCooldown = 46;
        }
    }

    private void tickDoubleClap(ServerLevel sl, LivingEntity target) {
        Vec3 forward = Vec3.directionFromRotation(0.0F, getYRot());
        if (stateTick < 20) faceTarget(target, 6.0F);
        if (stateTick == 13) {
            sl.playSound(null, getX(), getY() + 2.0D, getZ(), SoundEvents.IRON_GOLEM_REPAIR, SoundSource.HOSTILE, 1.3F, 0.62F);
            playAttackSpeech(sl, 0.70F);
        }
        Vec3 cur = getDeltaMovement();
        if (stateTick >= 21 && stateTick <= 23) {
            setDeltaMovement(Mth.lerp(0.5D, cur.x, forward.x * 0.34D), cur.y, Mth.lerp(0.5D, cur.z, forward.z * 0.34D));
        } else {
            setDeltaMovement(cur.x * 0.65D, cur.y, cur.z * 0.65D);
        }
        if (stateTick == 23) {
            dealArcDamage(sl, 5.4D, 105.0F, 24.0F, 1.40D);
            sl.playSound(null, getX(), getY() + 1.8D, getZ(), (TeyvatBossesSounds.mechMetalClank() != null ? TeyvatBossesSounds.mechMetalClank() : SoundEvents.IRON_GOLEM_DAMAGE), SoundSource.HOSTILE, 1.5F, 0.52F);
            sl.playSound(null, getX(), getY() + 1.8D, getZ(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 1.45F, 0.74F);
            Vec3 clapPos = position().add(forward.scale(2.4D));
            sl.sendParticles(ParticleTypes.GUST_EMITTER_SMALL, clapPos.x, clapPos.y + 1.7D, clapPos.z, 3, 0.8D, 0.4D, 0.8D, 0.0D);
            sl.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, clapPos.x, clapPos.y + 1.6D, clapPos.z, 20, 1.1D, 0.5D, 1.1D, 0.08D);
        }
        if (stateTick >= 52) {
            transitionTo(STATE_COMBAT, ANIM_IDLE);
            attackCooldown = 42;
        }
    }

    private void tickStompShockwave(ServerLevel sl, LivingEntity target) {
        setDeltaMovement(getDeltaMovement().x * 0.7D, getDeltaMovement().y, getDeltaMovement().z * 0.7D);
        if (stateTick == 10) {
            sl.playSound(null, getX(), getY() + 1.4D, getZ(), SoundEvents.IRON_GOLEM_REPAIR, SoundSource.HOSTILE, 1.25F, 0.58F);
            playAttackSpeech(sl, 0.70F);
        }
        if (stateTick == 13) setTelegraph(new Vec3(getX(), sampleGroundY(sl, getX(), getZ(), getY()), getZ()), 5.0F);
        if (stateTick == 20) {
            clearTelegraph();
            dealRadialDamage(sl, 5.2D, 22.0F, 1.20D, 0.58D);
            sl.playSound(null, getX(), getY(), getZ(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 1.6F, 0.62F);
            sl.playSound(null, getX(), getY(), getZ(), SoundEvents.IRON_GOLEM_STEP, SoundSource.HOSTILE, 1.75F, 0.46F);
            spawnGroundDustRing(sl, 4.6D, 34);
        }
        if (stateTick >= 44) {
            double dist = Math.hypot(target.getX() - getX(), target.getZ() - getZ());
            if (dist <= 5.4D && random.nextFloat() < 0.65F) {
                lastAttack = ANIM_DOUBLE_CLAP;
                transitionTo(STATE_COMBAT, ANIM_DOUBLE_CLAP);
            } else {
                transitionTo(STATE_COMBAT, ANIM_IDLE);
                attackCooldown = 40;
            }
        }
    }

    private void tickCycloneActive(ServerLevel sl, LivingEntity target) {
        if (getNavigation() instanceof BossGroundNavigation bnav) {
            if (stateTick % 6 == 0 || bnav.isDone()) {
                bnav.computePathTo(target);
            }
        }
        Vec3 toTarget = new Vec3(target.getX() - getX(), 0.0D, target.getZ() - getZ());
        if (toTarget.lengthSqr() > 0.25D) {
            Vec3 steer = (getNavigation() instanceof BossGroundNavigation bnav)
                    ? bnav.getSteerDirection(target.position())
                    : toTarget.normalize();
            Vec3 desired = steer.scale(isPhase2() ? 0.30D : 0.26D);
            Vec3 cur = getDeltaMovement();
            double jumpY = (this.horizontalCollision && this.onGround()) ? 0.46D : cur.y;
            setDeltaMovement(Mth.lerp(0.24D, cur.x, desired.x), jumpY, Mth.lerp(0.24D, cur.z, desired.z));
        }
        if (stateTick % 8 == 0) {
            dealRadialDamage(sl, 5.0D, 12.5F, 0.90D, 0.36D);
            sl.playSound(null, getX(), getY() + 1.8D, getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.HOSTILE, 1.4F, 0.68F);
            sl.sendParticles(ParticleTypes.SWEEP_ATTACK, getX(), getY() + 1.9D, getZ(), 4, 2.4D, 0.3D, 2.4D, 0.0D);
        }
        if (stateTick % 10 == 5) {
            sl.playSound(null, getX(), getY(), getZ(), SoundEvents.IRON_GOLEM_STEP, SoundSource.HOSTILE, 1.2F, 0.54F);
            sl.sendParticles(ParticleTypes.POOF, getX(), getY() + 0.1D, getZ(), 5, 0.55D, 0.05D, 0.55D, 0.02D);
        }
        // Exact integer multiples of the 20-tick (1.00s = 3 revs) spin clip so waist hands off at 0 deg to recovery
        int maxSpinTicks = isPhase2() ? 120 : 100;
        if (stateTick >= maxSpinTicks) {
            transitionTo(STATE_COMBAT, ANIM_CYCLONE_RECOVER);
        }
    }

    /**
     * Weighted Terrain-Aware Leap Slam (78 ticks = 3.90s):
     * Deep hydraulic crouch 0..16, asymmetric parabolic arc 17..35 (0.90s),
     * gorilla-crater ground-contact slam @ tick 36 (1.80s), crater lock 36..53, slow hydraulic stand-up 53..78.
     */
    private void tickLeapSlam(ServerLevel sl, LivingEntity target) {
        if (stateTick < 17) {
            faceTarget(target, 7.0F);
            setDeltaMovement(getDeltaMovement().x * 0.6D, getDeltaMovement().y, getDeltaMovement().z * 0.6D);
            if (stateTick == 8) {
                sl.playSound(null, getX(), getY() + 1.4D, getZ(), SoundEvents.IRON_GOLEM_REPAIR, SoundSource.HOSTILE, 1.25F, 0.54F);
                net.minecraft.sounds.SoundEvent creak = TeyvatBossesSounds.mechJointCreak();
                if (creak != null) sl.playSound(null, getX(), getY() + 1.4D, getZ(), creak, SoundSource.HOSTILE, 0.85F, 0.78F);
                net.minecraft.sounds.SoundEvent whir = TeyvatBossesSounds.mechServoWhir();
                if (whir != null) sl.playSound(null, getX(), getY() + 1.6D, getZ(), whir, SoundSource.HOSTILE, 0.80F, 0.85F);
            }
            if (stateTick < 13) {
                Vec3 dest = clampLeapDestination(target);
                double groundY = sampleGroundY(sl, dest.x, dest.z, target.getY());
                leapTarget = new Vec3(dest.x, groundY, dest.z);
                setTelegraph(leapTarget, 6.0F);
            }
            leapStart = position();
        } else if (stateTick >= 17 && stateTick <= 35) {
            if (stateTick == 17) {
                sl.playSound(null, getX(), getY(), getZ(), SoundEvents.IRON_GOLEM_STEP, SoundSource.HOSTILE, 1.65F, 0.46F);
                net.minecraft.sounds.SoundEvent hiss = TeyvatBossesSounds.mechHydraulicHiss();
                if (hiss != null) sl.playSound(null, getX(), getY() + 0.5D, getZ(), hiss, SoundSource.HOSTILE, 1.05F, 0.85F);
                sl.sendParticles(ParticleTypes.POOF, getX(), getY() + 0.15D, getZ(), 16, 0.9D, 0.1D, 0.9D, 0.05D);
            }
            float f = (stateTick - 17) / 18.0F;
            float smoothF = f * f * (3.0F - 2.0F * f);
            double shaped = Math.sin(Math.pow(f, 0.80D) * Math.PI);
            double arcH = 5.6D + Math.max(0.0D, leapTarget.y - leapStart.y) * 0.55D;
            double arcY = Math.pow(Math.max(0.0D, shaped), 0.90D) * arcH;
            double nx = Mth.lerp(smoothF, leapStart.x, leapTarget.x);
            double nz = Mth.lerp(smoothF, leapStart.z, leapTarget.z);
            double ny = Mth.lerp(f, leapStart.y, leapTarget.y) + arcY;
            moveLeapStep(nx, ny, nz);
        } else if (stateTick == 36) {
            clearTelegraph();
            finishLeapLanding(leapTarget.x, leapTarget.y, leapTarget.z);
            dealRadialDamage(sl, 6.4D, 29.0F, 1.50D, 0.72D);
            sl.playSound(null, getX(), getY(), getZ(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 1.85F, 0.52F);
            sl.playSound(null, getX(), getY(), getZ(), SoundEvents.IRON_GOLEM_DAMAGE, SoundSource.HOSTILE, 1.55F, 0.48F);
            sl.playSound(null, getX(), getY(), getZ(), (TeyvatBossesSounds.mechMetalClank() != null ? TeyvatBossesSounds.mechMetalClank() : SoundEvents.IRON_GOLEM_DAMAGE), SoundSource.HOSTILE, 1.25F, 0.45F);
            sl.sendParticles(ParticleTypes.EXPLOSION_EMITTER, getX(), getY() + 0.5D, getZ(), 1, 0.5D, 0.2D, 0.5D, 0.0D);
            spawnGroundDustRing(sl, 5.8D, 46);
        } else {
            setDeltaMovement(0.0D, getDeltaMovement().y, 0.0D);
            if (stateTick == 53) {
                sl.playSound(null, getX(), getY() + 1.4D, getZ(), SoundEvents.IRON_GOLEM_REPAIR, SoundSource.HOSTILE, 0.9F, 0.50F);
            }
        }
        if (stateTick >= 78) {
            transitionTo(STATE_COMBAT, ANIM_IDLE);
            attackCooldown = 40;
        }
    }

    private void tickPhase2Overdrive(ServerLevel sl) {
        setDeltaMovement(0.0D, getDeltaMovement().y, 0.0D);
        if (stateTick == 1) setTelegraph(new Vec3(getX(), sampleGroundY(sl, getX(), getZ(), getY()), getZ()), 8.0F);
        if (stateTick == 23) {
            clearTelegraph();
            dealRadialDamage(sl, 8.0D, 20.0F, 1.65D, 0.62D);
            sl.playSound(null, getX(), getY() + 1.5D, getZ(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 1.6F, 0.68F);
            sl.sendParticles(ParticleTypes.FLAME, getX(), getY() + 2.0D, getZ(), 55, 2.8D, 1.6D, 2.8D, 0.14D);
            spawnGroundDustRing(sl, 6.5D, 36);
        }
        if (stateTick >= 48) {
            transitionTo(STATE_COMBAT, ANIM_IDLE);
            attackCooldown = 16;
        }
    }

    /** Phase 2 Cataclysmic Core Slam + Overdrive Missile Salvo Finisher (64 ticks = 3.20s). */
    private void tickOverdriveFinisher(ServerLevel sl, LivingEntity target) {
        setDeltaMovement(getDeltaMovement().x * 0.65D, getDeltaMovement().y, getDeltaMovement().z * 0.65D);
        if (stateTick < 18) {
            faceTarget(target, 7.0F);
            if (stateTick % 4 == 0) {
                sl.sendParticles(ParticleTypes.FLAME, getX(), getY() + 2.6D, getZ(), 10, 0.9D, 0.7D, 0.9D, 0.06D);
            }
        }
        if (stateTick == 16) {
            if (TeyvatBossesSounds.ruinGuardSpeech() != null) {
                speechCooldownTicks = 0;
                playAttackSpeech(sl, 1.0F);
            } else {
                sl.playSound(null, getX(), getY() + 2.0D, getZ(), SoundEvents.RAVAGER_ROAR, SoundSource.HOSTILE, 1.5F, 0.60F);
            }
        }
        if (stateTick == 25) {
            clearTelegraph();
            dealRadialDamage(sl, 8.0D, 34.0F, 1.80D, 0.75D);
            sl.playSound(null, getX(), getY(), getZ(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 2.0F, 0.48F);
            sl.playSound(null, getX(), getY(), getZ(), SoundEvents.IRON_GOLEM_DAMAGE, SoundSource.HOSTILE, 1.6F, 0.50F);
            sl.sendParticles(ParticleTypes.EXPLOSION_EMITTER, getX(), getY() + 0.5D, getZ(), 2, 1.2D, 0.3D, 1.2D, 0.0D);
            sl.sendParticles(ParticleTypes.FLAME, getX(), getY() + 0.8D, getZ(), 48, 3.2D, 0.6D, 3.2D, 0.14D);
            spawnGroundDustRing(sl, 7.2D, 48);
        }
        if (stateTick == 27 || stateTick == 30 || stateTick == 33 || stateTick == 36) {
            int idx = (stateTick - 27) / 3;
            launchPodMissile(sl, target, idx, 14.5F);
        }
        if (stateTick >= 64) {
            transitionTo(STATE_COMBAT, ANIM_IDLE);
            attackCooldown = 40;
        }
    }

    /** Genshin-faithful Hit-1 weakpoint stagger (19 ticks = 0.95s); swivels around if struck on the rear core. */
    private void tickWeakpointFlinch(ServerLevel sl, LivingEntity target) {
        Vec3 cur = getDeltaMovement();
        setDeltaMovement(cur.x * 0.45D, cur.y, cur.z * 0.45D);
        if (stateTick == 1) {
            sl.playSound(null, getX(), getY() + 2.8D, getZ(), SoundEvents.IRON_GOLEM_HURT, SoundSource.HOSTILE, 1.35F, 0.72F);
            sl.sendParticles(ParticleTypes.ELECTRIC_SPARK, getX(), getY() + 3.0D, getZ(), 14, 0.55D, 0.45D, 0.55D, 0.12D);
        }
        if (weakpointRearHit && stateTick >= 4 && stateTick <= 16) {
            float newYaw = Mth.approachDegrees(getYRot(), weakpointSwivelTargetYaw, 16.5F);
            setYRot(newYaw);
            yBodyRot = newYaw;
            yHeadRot = newYaw;
        } else if (!weakpointRearHit && stateTick >= 10) {
            faceTarget(target, 5.0F);
        }
        if (stateTick >= WEAKPOINT_FLINCH_TICKS) {
            weakpointRearHit = false;
            transitionTo(STATE_COMBAT, ANIM_IDLE);
            attackCooldown = Math.max(attackCooldown, 14);
        }
    }

    private void spawnGroundDustRing(ServerLevel sl, double radius, int count) {
        for (int i = 0; i < count; i++) {
            double ang = (Math.PI * 2.0D * i) / count;
            double ox = Math.cos(ang) * radius * (0.65D + random.nextDouble() * 0.35D);
            double oz = Math.sin(ang) * radius * (0.65D + random.nextDouble() * 0.35D);
            sl.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, getX() + ox, getY() + 0.2D, getZ() + oz, 1, 0.1D, 0.12D, 0.1D, 0.04D);
            if (i % 2 == 0) {
                sl.sendParticles(ParticleTypes.CLOUD, getX() + ox * 0.7D, getY() + 0.15D, getZ() + oz * 0.7D, 1, 0.15D, 0.08D, 0.15D, 0.05D);
            }
        }
    }

    private void dealArcDamage(ServerLevel sl, double radius, float arcDeg, float dmg, double knockback) {
        Vec3 forward = Vec3.directionFromRotation(0.0F, getYRot());
        double cosHalf = Math.cos(Math.toRadians(arcDeg * 0.5D));
        AABB box = new AABB(getX() - radius, getY() - 0.5D, getZ() - radius,
                getX() + radius, getY() + 4.2D, getZ() + radius);
        for (LivingEntity e : sl.getEntitiesOfClass(LivingEntity.class, box, this::isValidCombatTarget)) {
            Vec3 toE = new Vec3(e.getX() - getX(), 0.0D, e.getZ() - getZ());
            double d = toE.length();
            if (d <= radius && (d < 1.2D || toE.normalize().dot(forward) >= cosHalf)) {
                StavesElementBridge.dealElementalDamage(e, this, "PHYSICAL", dmg, 0.0F, true, 0.18F);
                Vec3 push = d > 0.05D ? toE.normalize().scale(knockback) : forward.scale(knockback);
                e.setDeltaMovement(push.x, 0.42D, push.z);
                e.hurtMarked = true;
            }
        }
    }

    private void dealRadialDamage(ServerLevel sl, double radius, float dmg, double knockback, double lift) {
        AABB box = new AABB(getX() - radius, getY() - 0.5D, getZ() - radius,
                getX() + radius, getY() + 4.2D, getZ() + radius);
        for (LivingEntity e : sl.getEntitiesOfClass(LivingEntity.class, box, this::isValidCombatTarget)) {
            Vec3 toE = new Vec3(e.getX() - getX(), 0.0D, e.getZ() - getZ());
            if (toE.length() <= radius) {
                StavesElementBridge.dealElementalDamage(e, this, "PHYSICAL", dmg, 0.0F, true, 0.20F);
                Vec3 push = toE.lengthSqr() > 0.01D ? toE.normalize().scale(knockback) : new Vec3(0.0D, 0.0D, knockback);
                e.setDeltaMovement(push.x, lift, push.z);
                e.hurtMarked = true;
            }
        }
    }

    private void updateBossBar() {
        bossEvent.setProgress(Mth.clamp(getHealth() / getMaxHealth(), 0.0F, 1.0F));
        Component base = Component.translatable("entity.velorise_teyvat_bosses.ruin_guard");
        if (getBossState() == STATE_STUNNED) {
            bossEvent.setColor(BossEvent.BossBarColor.WHITE);
            bossEvent.setName(base.copy().append(Component.literal(" — CORE PARALYZED")));
        } else if (isPhase2()) {
            bossEvent.setColor(BossEvent.BossBarColor.RED);
            bossEvent.setName(base.copy().append(Component.literal(" — GN/Hu-24601 [OVERDRIVE]")));
        } else {
            bossEvent.setColor(BossEvent.BossBarColor.YELLOW);
            bossEvent.setName(base.copy().append(Component.literal(" — GN/Hu-24601")));
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("ArenaInit", arenaInit);
        tag.putDouble("AX", arenaCenter.x);
        tag.putDouble("AY", arenaCenter.y);
        tag.putDouble("AZ", arenaCenter.z);
        tag.putFloat("SavedYaw", savedSpawnYaw);
        tag.putBoolean("Phase2", isPhase2());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.getBoolean("ArenaInit")) {
            arenaInit = true;
            arenaCenter = new Vec3(tag.getDouble("AX"), tag.getDouble("AY"), tag.getDouble("AZ"));
            savedSpawnYaw = tag.getFloat("SavedYaw");
        }
        entityData.set(DATA_PHASE2, tag.getBoolean("Phase2"));
    }
}

package com.velorise.teyvatbosses.entity;

import com.velorise.teyvatbosses.compat.StavesElementBridge;
import net.minecraft.ChatFormatting;
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
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * Boss-tier Ruin Guard (GN/Hu-24601 — Thousand Winds Overlord) with full multi-target combat
 * (fights Players, Iron Golems, and hostile mobs), world-locked Leap Slam telegraph rings,
 * smooth stride-synced movement with hysteresis, and balanced mid-late progression stats.
 */
public class RuinGuardEntity extends Monster {

    public static final int STATE_DORMANT = 0;
    public static final int STATE_AWAKEN  = 1;
    public static final int STATE_COMBAT  = 2;
    public static final int STATE_STUNNED = 3;
    public static final int STATE_DEATH   = 4;

    public static final int ANIM_DORMANT          = 0;
    public static final int ANIM_AWAKEN           = 1;
    public static final int ANIM_IDLE             = 2;
    public static final int ANIM_WALK             = 3;
    public static final int ANIM_MISSILE_BARRAGE  = 4;
    public static final int ANIM_MELEE_3HIT       = 5;
    public static final int ANIM_DOUBLE_CLAP      = 6;
    public static final int ANIM_STOMP_SHOCKWAVE  = 7;
    public static final int ANIM_CYCLONE_WINDUP   = 8;
    public static final int ANIM_CYCLONE_ACTIVE   = 9;
    public static final int ANIM_CYCLONE_RECOVER  = 10;
    public static final int ANIM_LEAP_SLAM        = 11;
    public static final int ANIM_PHASE2_OVERDRIVE = 12;
    public static final int ANIM_STUNNED          = 13;
    public static final int ANIM_DEATH            = 14;

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
    private int attackCooldown = 26;
    private int lastAttack = -1;
    private int eyeHits = 0;
    private int eyeResetTimer = 0;
    private boolean customDeathHandled = false;
    private Vec3 leapStart = Vec3.ZERO;
    private Vec3 leapTarget = Vec3.ZERO;

    // Client-side smooth animation tick
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
                .add(Attributes.FOLLOW_RANGE, 42.0D);
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
    protected void registerGoals() {}

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
            default -> "idle_combat";
        };
    }

    @Override
    public boolean isPushable() { return false; }

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

    /**
     * Finds the active combat target: nearest Survival Player, else last attacker (e.g. Iron Golem),
     * else any nearby Mob actively targeting this boss. Prevents the wake-sleep-music loop when golems attack!
     */
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
            // Still record attacker during bootup so aggro is locked as soon as it stands up
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

        // Eye & Back-Reactor Weakpoint Detection: projectile hitting front eye or rear core (Y +2.35..+4.15)
        if (st == STATE_COMBAT && !level().isClientSide() && source.getDirectEntity() instanceof Projectile proj) {
            if (proj.getY() >= getY() + 2.35D && proj.getY() <= getY() + 4.15D) {
                Vec3 toProj = new Vec3(proj.getX() - getX(), 0.0D, proj.getZ() - getZ());
                Vec3 forward = Vec3.directionFromRotation(0.0F, getYRot());
                boolean hitFrontOrBack = toProj.lengthSqr() < 0.04D || Math.abs(toProj.normalize().dot(forward)) >= 0.30D;
                if (hitFrontOrBack) {
                    eyeHits++;
                    eyeResetTimer = 140;
                    ServerLevel sl = (ServerLevel) level();
                    sl.playSound(null, getX(), getY() + 3.0D, getZ(), SoundEvents.ANVIL_LAND, SoundSource.HOSTILE, 1.0F, 1.72F);
                    sl.playSound(null, getX(), getY() + 3.0D, getZ(), SoundEvents.GLASS_BREAK, SoundSource.HOSTILE, 0.9F, 1.35F);
                    sl.sendParticles(ParticleTypes.CRIT, getX(), getY() + 3.1D, getZ(), 24, 0.45D, 0.45D, 0.45D, 0.16D);
                    sl.sendParticles(ParticleTypes.ELECTRIC_SPARK, getX(), getY() + 3.1D, getZ(), 18, 0.40D, 0.35D, 0.40D, 0.14D);
                    amount *= 1.35F;
                    if (eyeHits >= 2 || (proj instanceof AbstractArrow && eyeHits >= 2)) {
                        triggerEyeStun(sl);
                    } else if (source.getEntity() instanceof Player p) {
                        p.displayClientMessage(Component.literal("✦ Eye Weakpoint Cracked (1/2) — Shoot again to paralyze!")
                                .withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD), true);
                    }
                }
            }
        }

        // Balanced 45% Physical Resistance while active; 1.5x bonus damage while Eye-Stunned!
        if (st == STATE_STUNNED) {
            amount *= 1.50F;
        } else if (!source.is(DamageTypeTags.BYPASSES_ARMOR) && !source.is(DamageTypeTags.WITCH_RESISTANT_TO)) {
            amount *= 0.55F;
        }

        return super.hurt(source, amount);
    }

    private void triggerEyeStun(ServerLevel sl) {
        eyeHits = 0;
        transitionTo(STATE_STUNNED, ANIM_STUNNED);
        sl.playSound(null, getX(), getY() + 2.5D, getZ(), SoundEvents.IRON_GOLEM_DAMAGE, SoundSource.HOSTILE, 1.6F, 0.62F);
        sl.playSound(null, getX(), getY() + 2.5D, getZ(), SoundEvents.BEACON_DEACTIVATE, SoundSource.HOSTILE, 1.35F, 0.78F);
        sl.sendParticles(ParticleTypes.ELECTRIC_SPARK, getX(), getY() + 3.0D, getZ(), 36, 0.9D, 0.7D, 0.9D, 0.12D);
        for (Player p : sl.getEntitiesOfClass(Player.class, getBoundingBox().inflate(32.0D))) {
            p.displayClientMessage(Component.translatable("message.velorise_teyvat_bosses.ruin_guard.weakpoint_stun")
                    .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD), true);
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
            eyeHits = 0;
        }

        int st = getBossState();
        if (st == STATE_DEATH) {
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
            if (++noTargetTicks > 180) {
                noTargetTicks = 0;
                setPos(arenaCenter.x, arenaCenter.y, arenaCenter.z);
                setHealth(getMaxHealth());
                entityData.set(DATA_PHASE2, false);
                transitionTo(STATE_DORMANT, ANIM_DORMANT);
            }
            return;
        }
        noTargetTicks = 0;
        setTarget(combatTarget);

        // Trigger Phase 2 Overdrive at < 50% HP!
        if (!isPhase2() && getHealth() <= getMaxHealth() * 0.5F && st == STATE_COMBAT && getAnimId() != ANIM_PHASE2_OVERDRIVE) {
            entityData.set(DATA_PHASE2, true);
            transitionTo(STATE_COMBAT, ANIM_PHASE2_OVERDRIVE);
            sl.playSound(null, getX(), getY() + 2.0D, getZ(), SoundEvents.WITHER_SPAWN, SoundSource.HOSTILE, 1.3F, 0.72F);
            return;
        }

        switch (st) {
            case STATE_AWAKEN -> {
                faceTarget(combatTarget, 5.0F);
                if (stateTick == 17) sl.sendParticles(ParticleTypes.CLOUD, getX(), getY() + 2.6D, getZ(), 18, 0.8D, 0.6D, 0.8D, 0.06D);
                if (stateTick == 40) sl.playSound(null, getX(), getY(), getZ(), SoundEvents.IRON_GOLEM_STEP, SoundSource.HOSTILE, 1.4F, 0.65F);
                if (stateTick >= 52) {
                    transitionTo(STATE_COMBAT, ANIM_IDLE);
                    attackCooldown = 18;
                }
            }
            case STATE_STUNNED -> {
                setDeltaMovement(0.0D, getDeltaMovement().y, 0.0D);
                if (stateTick % 12 == 0) {
                    sl.sendParticles(ParticleTypes.ELECTRIC_SPARK, getX(), getY() + 2.0D, getZ(), 8, 0.7D, 0.5D, 0.7D, 0.05D);
                }
                if (stateTick >= 150) {
                    sl.playSound(null, getX(), getY() + 2.0D, getZ(), SoundEvents.BEACON_ACTIVATE, SoundSource.HOSTILE, 1.25F, 0.85F);
                    transitionTo(STATE_COMBAT, ANIM_IDLE);
                    attackCooldown = 24;
                }
            }
            case STATE_COMBAT -> tickCombat(sl, combatTarget);
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

    private void tickCombat(ServerLevel sl, LivingEntity target) {
        int anim = getAnimId();
        double dist = Math.hypot(target.getX() - getX(), target.getZ() - getZ());

        if (anim == ANIM_IDLE || anim == ANIM_WALK) {
            faceTarget(target, 5.5F);
            // Smooth walk/idle hysteresis so the boss never stutters at a single distance threshold
            boolean shouldWalk = (anim == ANIM_WALK) ? (dist > 3.5D) : (dist > 4.6D);
            Vec3 cur = getDeltaMovement();

            if (shouldWalk) {
                if (anim != ANIM_WALK) transitionTo(STATE_COMBAT, ANIM_WALK);
                Vec3 dir = new Vec3(target.getX() - getX(), 0.0D, target.getZ() - getZ()).normalize();
                // Stride-synced speed modulation matching 1.60s (32-tick) heavy mechanical march
                double stridePhase = Math.abs(Math.sin((stateTick / 16.0D) * Math.PI));
                double speed = 0.11D + 0.09D * stridePhase;
                setDeltaMovement(
                        Mth.lerp(0.24D, cur.x, dir.x * speed),
                        cur.y,
                        Mth.lerp(0.24D, cur.z, dir.z * speed)
                );
                if (stateTick % 16 == 8) {
                    sl.playSound(null, getX(), getY(), getZ(), SoundEvents.IRON_GOLEM_STEP, SoundSource.HOSTILE, 1.25F, 0.62F);
                    sl.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, getX(), getY() + 0.1D, getZ(), 4, 0.5D, 0.05D, 0.5D, 0.02D);
                }
            } else {
                if (anim != ANIM_IDLE) transitionTo(STATE_COMBAT, ANIM_IDLE);
                setDeltaMovement(cur.x * 0.65D, cur.y, cur.z * 0.65D);
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
            case ANIM_STOMP_SHOCKWAVE -> tickStompShockwave(sl);
            case ANIM_CYCLONE_WINDUP -> {
                faceTarget(target, 6.5F);
                setDeltaMovement(getDeltaMovement().x * 0.7D, getDeltaMovement().y, getDeltaMovement().z * 0.7D);
                if (stateTick == 1) sl.playSound(null, getX(), getY() + 2.0D, getZ(), SoundEvents.IRON_GOLEM_REPAIR, SoundSource.HOSTILE, 1.4F, 0.72F);
                if (stateTick >= 22) transitionTo(STATE_COMBAT, ANIM_CYCLONE_ACTIVE);
            }
            case ANIM_CYCLONE_ACTIVE -> tickCycloneActive(sl, target);
            case ANIM_CYCLONE_RECOVER -> {
                setDeltaMovement(getDeltaMovement().x * 0.75D, getDeltaMovement().y, getDeltaMovement().z * 0.75D);
                if (stateTick >= 24) {
                    if (isPhase2()) {
                        startLeapSlam(sl, target);
                    } else {
                        transitionTo(STATE_COMBAT, ANIM_IDLE);
                        attackCooldown = 28;
                    }
                }
            }
            case ANIM_LEAP_SLAM -> tickLeapSlam(sl, target);
            case ANIM_PHASE2_OVERDRIVE -> tickPhase2Overdrive(sl);
            default -> transitionTo(STATE_COMBAT, ANIM_IDLE);
        }
    }

    private void chooseNextAttack(ServerLevel sl, LivingEntity target, double dist) {
        int chosen;
        if (dist >= 9.0D) {
            chosen = (random.nextFloat() < 0.55F) ? ANIM_MISSILE_BARRAGE
                    : (random.nextFloat() < 0.55F ? ANIM_LEAP_SLAM : ANIM_CYCLONE_WINDUP);
        } else {
            int[] meleePool = {ANIM_MELEE_3HIT, ANIM_DOUBLE_CLAP, ANIM_STOMP_SHOCKWAVE, ANIM_CYCLONE_WINDUP, ANIM_LEAP_SLAM};
            chosen = meleePool[random.nextInt(meleePool.length)];
        }
        if (chosen == lastAttack) {
            chosen = (dist >= 8.0D) ? ANIM_MISSILE_BARRAGE : ANIM_MELEE_3HIT;
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
        leapTarget = new Vec3(target.getX(), arenaCenter.y, target.getZ());
        transitionTo(STATE_COMBAT, ANIM_LEAP_SLAM);
        setTelegraph(leapTarget, 6.0F);
        sl.playSound(null, getX(), getY() + 2.0D, getZ(), SoundEvents.RAVAGER_ROAR, SoundSource.HOSTILE, 1.2F, 0.68F);
        sl.playSound(null, getX(), getY() + 1.5D, getZ(), SoundEvents.IRON_GOLEM_REPAIR, SoundSource.HOSTILE, 1.2F, 0.62F);
    }

    private void tickMissileBarrage(ServerLevel sl, LivingEntity target) {
        faceTarget(target, 5.5F);
        setDeltaMovement(getDeltaMovement().x * 0.7D, getDeltaMovement().y, getDeltaMovement().z * 0.7D);
        if (stateTick == 10) {
            sl.playSound(null, getX(), getY() + 2.5D, getZ(), SoundEvents.IRON_GOLEM_REPAIR, SoundSource.HOSTILE, 1.3F, 0.80F);
        }
        int totalMissiles = isPhase2() ? 6 : 4;
        if (stateTick >= 23 && stateTick < 23 + totalMissiles * 2 && (stateTick - 23) % 2 == 0) {
            int idx = (stateTick - 23) / 2;
            double side = (idx % 2 == 0) ? -1.1D : 1.1D;
            float yawRad = getYRot() * Mth.DEG_TO_RAD;
            Vec3 right = new Vec3(Math.cos(yawRad), 0.0D, Math.sin(yawRad));
            Vec3 forward = Vec3.directionFromRotation(0.0F, getYRot());
            Vec3 origin = position().add(right.scale(side)).add(0.0D, 3.65D, 0.0D);
            Vec3 launchVel = right.scale(side * 0.25D)
                    .add(forward.scale(0.18D))
                    .add(0.0D, 0.52D + (idx % 3) * 0.06D, 0.0D);
            RuinMissileProjectile.launch(sl, this, target, origin, launchVel, 8.5F);
            sl.sendParticles(ParticleTypes.FLAME, origin.x, origin.y, origin.z, 8, 0.15D, 0.15D, 0.15D, 0.05D);
        }
        if (stateTick >= 48) {
            transitionTo(STATE_COMBAT, ANIM_IDLE);
            attackCooldown = 34;
        }
    }

    private void tickMelee3Hit(ServerLevel sl, LivingEntity target) {
        Vec3 forward = Vec3.directionFromRotation(0.0F, getYRot());
        if (stateTick < 14 || (stateTick > 20 && stateTick < 28) || (stateTick > 34 && stateTick < 42)) {
            faceTarget(target, 7.0F);
        }

        Vec3 cur = getDeltaMovement();
        if (stateTick >= 15 && stateTick <= 17) {
            setDeltaMovement(Mth.lerp(0.5D, cur.x, forward.x * 0.36D), cur.y, Mth.lerp(0.5D, cur.z, forward.z * 0.36D));
        } else if (stateTick >= 29 && stateTick <= 31) {
            setDeltaMovement(Mth.lerp(0.5D, cur.x, forward.x * 0.36D), cur.y, Mth.lerp(0.5D, cur.z, forward.z * 0.36D));
        } else if (stateTick >= 42 && stateTick <= 49) {
            double hopVy = (stateTick == 42) ? 0.42D : cur.y;
            setDeltaMovement(Mth.lerp(0.4D, cur.x, forward.x * 0.24D), hopVy, Mth.lerp(0.4D, cur.z, forward.z * 0.24D));
        } else {
            setDeltaMovement(cur.x * 0.65D, cur.y, cur.z * 0.65D);
        }

        if (stateTick == 12 || stateTick == 26) {
            sl.playSound(null, getX(), getY() + 1.8D, getZ(), SoundEvents.IRON_GOLEM_REPAIR, SoundSource.HOSTILE, 1.1F, 0.65F);
        } else if (stateTick == 17) {
            dealArcDamage(sl, 4.8D, 145.0F, 11.0F, 0.95D);
            sl.playSound(null, getX(), getY() + 1.6D, getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.HOSTILE, 1.6F, 0.54F);
            sl.playSound(null, getX(), getY() + 1.6D, getZ(), SoundEvents.IRON_GOLEM_ATTACK, SoundSource.HOSTILE, 1.35F, 0.68F);
            Vec3 front = position().add(forward.scale(2.6D));
            sl.sendParticles(ParticleTypes.SWEEP_ATTACK, front.x, front.y + 1.6D, front.z, 3, 1.1D, 0.2D, 1.1D, 0.0D);
        } else if (stateTick == 31) {
            dealArcDamage(sl, 4.8D, 145.0F, 12.0F, 1.00D);
            sl.playSound(null, getX(), getY() + 1.6D, getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.HOSTILE, 1.6F, 0.48F);
            sl.playSound(null, getX(), getY() + 1.6D, getZ(), SoundEvents.IRON_GOLEM_ATTACK, SoundSource.HOSTILE, 1.4F, 0.62F);
            Vec3 front = position().add(forward.scale(2.6D));
            sl.sendParticles(ParticleTypes.SWEEP_ATTACK, front.x, front.y + 1.6D, front.z, 3, 1.1D, 0.2D, 1.1D, 0.0D);
        } else if (stateTick == 38) {
            setTelegraph(new Vec3(getX() + forward.x * 1.4D, arenaCenter.y, getZ() + forward.z * 1.4D), 5.2F);
        } else if (stateTick == 50) {
            clearTelegraph();
            dealRadialDamage(sl, 5.2D, 16.0F, 1.25D, 0.58D);
            sl.playSound(null, getX(), getY(), getZ(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 1.65F, 0.62F);
            sl.playSound(null, getX(), getY(), getZ(), SoundEvents.IRON_GOLEM_DAMAGE, SoundSource.HOSTILE, 1.45F, 0.55F);
            sl.sendParticles(ParticleTypes.EXPLOSION, getX(), getY() + 0.4D, getZ(), 4, 2.0D, 0.3D, 2.0D, 0.0D);
            spawnGroundDustRing(sl, 4.4D, 28);
        }
        if (stateTick >= 68) {
            transitionTo(STATE_COMBAT, ANIM_IDLE);
            attackCooldown = 28;
        }
    }

    private void tickDoubleClap(ServerLevel sl, LivingEntity target) {
        Vec3 forward = Vec3.directionFromRotation(0.0F, getYRot());
        if (stateTick < 18) faceTarget(target, 6.5F);
        if (stateTick == 12) {
            sl.playSound(null, getX(), getY() + 2.0D, getZ(), SoundEvents.IRON_GOLEM_REPAIR, SoundSource.HOSTILE, 1.25F, 0.68F);
        }
        Vec3 cur = getDeltaMovement();
        if (stateTick >= 19 && stateTick <= 21) {
            setDeltaMovement(Mth.lerp(0.5D, cur.x, forward.x * 0.34D), cur.y, Mth.lerp(0.5D, cur.z, forward.z * 0.34D));
        } else {
            setDeltaMovement(cur.x * 0.65D, cur.y, cur.z * 0.65D);
        }
        if (stateTick == 21) {
            dealArcDamage(sl, 5.0D, 95.0F, 14.5F, 1.30D);
            sl.playSound(null, getX(), getY() + 1.8D, getZ(), SoundEvents.ANVIL_LAND, SoundSource.HOSTILE, 1.4F, 0.55F);
            sl.playSound(null, getX(), getY() + 1.8D, getZ(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 1.4F, 0.78F);
            Vec3 clapPos = position().add(forward.scale(2.4D));
            sl.sendParticles(ParticleTypes.GUST_EMITTER_SMALL, clapPos.x, clapPos.y + 1.7D, clapPos.z, 3, 0.8D, 0.4D, 0.8D, 0.0D);
            sl.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, clapPos.x, clapPos.y + 1.6D, clapPos.z, 18, 1.1D, 0.5D, 1.1D, 0.08D);
        }
        if (stateTick >= 42) {
            transitionTo(STATE_COMBAT, ANIM_IDLE);
            attackCooldown = 26;
        }
    }

    private void tickStompShockwave(ServerLevel sl) {
        setDeltaMovement(getDeltaMovement().x * 0.7D, getDeltaMovement().y, getDeltaMovement().z * 0.7D);
        if (stateTick == 10) {
            sl.playSound(null, getX(), getY() + 1.4D, getZ(), SoundEvents.IRON_GOLEM_REPAIR, SoundSource.HOSTILE, 1.2F, 0.62F);
        }
        if (stateTick == 12) setTelegraph(new Vec3(getX(), arenaCenter.y, getZ()), 5.0F);
        if (stateTick == 18) {
            clearTelegraph();
            dealRadialDamage(sl, 5.0D, 13.5F, 1.15D, 0.55D);
            sl.playSound(null, getX(), getY(), getZ(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 1.55F, 0.65F);
            sl.playSound(null, getX(), getY(), getZ(), SoundEvents.IRON_GOLEM_STEP, SoundSource.HOSTILE, 1.7F, 0.48F);
            spawnGroundDustRing(sl, 4.6D, 32);
        }
        if (stateTick >= 36) {
            transitionTo(STATE_COMBAT, ANIM_IDLE);
            attackCooldown = 24;
        }
    }

    private void tickCycloneActive(ServerLevel sl, LivingEntity target) {
        Vec3 toTarget = new Vec3(target.getX() - getX(), 0.0D, target.getZ() - getZ());
        if (toTarget.lengthSqr() > 0.25D) {
            Vec3 desired = toTarget.normalize().scale(0.28D);
            Vec3 cur = getDeltaMovement();
            setDeltaMovement(Mth.lerp(0.18D, cur.x, desired.x), cur.y, Mth.lerp(0.18D, cur.z, desired.z));
        }
        if (stateTick % 8 == 0) {
            dealRadialDamage(sl, 4.8D, 7.5F, 0.85D, 0.34D);
            sl.playSound(null, getX(), getY() + 1.8D, getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.HOSTILE, 1.4F, 0.72F);
            sl.sendParticles(ParticleTypes.SWEEP_ATTACK, getX(), getY() + 1.9D, getZ(), 4, 2.4D, 0.3D, 2.4D, 0.0D);
        }
        int maxSpinTicks = isPhase2() ? 112 : 84;
        if (stateTick >= maxSpinTicks) {
            transitionTo(STATE_COMBAT, ANIM_CYCLONE_RECOVER);
        }
    }

    private void tickLeapSlam(ServerLevel sl, LivingEntity target) {
        if (stateTick < 13) {
            faceTarget(target, 7.5F);
            setDeltaMovement(getDeltaMovement().x * 0.6D, getDeltaMovement().y, getDeltaMovement().z * 0.6D);
            // Smoothly track target until launch, keeping the telegraph locked on the ground at leapTarget!
            if (stateTick < 10) {
                leapTarget = new Vec3(target.getX(), arenaCenter.y, target.getZ());
                setTelegraph(leapTarget, 6.0F);
            }
            leapStart = position();
        } else if (stateTick >= 13 && stateTick <= 26) {
            float f = (stateTick - 13) / 13.0F;
            float smoothF = f * f * (3.0F - 2.0F * f);
            double nx = Mth.lerp(smoothF, leapStart.x, leapTarget.x);
            double nz = Mth.lerp(smoothF, leapStart.z, leapTarget.z);
            double ny = Mth.lerp(f, leapStart.y, leapTarget.y) + Math.sin(f * Math.PI) * 4.5D;
            Vec3 next = new Vec3(nx, ny, nz);
            setDeltaMovement(next.subtract(position()));
            setPos(nx, ny, nz);
        } else if (stateTick == 27) {
            clearTelegraph();
            setDeltaMovement(Vec3.ZERO);
            setPos(leapTarget.x, arenaCenter.y, leapTarget.z);
            dealRadialDamage(sl, 6.2D, 17.5F, 1.45D, 0.68D);
            sl.playSound(null, getX(), getY(), getZ(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 1.8F, 0.55F);
            sl.playSound(null, getX(), getY(), getZ(), SoundEvents.IRON_GOLEM_DAMAGE, SoundSource.HOSTILE, 1.5F, 0.52F);
            sl.sendParticles(ParticleTypes.EXPLOSION_EMITTER, getX(), getY() + 0.5D, getZ(), 1, 0.5D, 0.2D, 0.5D, 0.0D);
            spawnGroundDustRing(sl, 5.6D, 40);
        } else {
            setDeltaMovement(0.0D, getDeltaMovement().y, 0.0D);
        }
        if (stateTick >= 54) {
            transitionTo(STATE_COMBAT, ANIM_IDLE);
            attackCooldown = 32;
        }
    }

    private void tickPhase2Overdrive(ServerLevel sl) {
        setDeltaMovement(0.0D, getDeltaMovement().y, 0.0D);
        if (stateTick == 1) setTelegraph(new Vec3(getX(), arenaCenter.y, getZ()), 8.0F);
        if (stateTick == 23) {
            clearTelegraph();
            dealRadialDamage(sl, 8.0D, 12.5F, 1.65D, 0.60D);
            sl.playSound(null, getX(), getY() + 1.5D, getZ(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 1.6F, 0.68F);
            sl.sendParticles(ParticleTypes.FLAME, getX(), getY() + 2.0D, getZ(), 55, 2.8D, 1.6D, 2.8D, 0.14D);
            spawnGroundDustRing(sl, 6.5D, 36);
        }
        if (stateTick >= 48) {
            transitionTo(STATE_COMBAT, ANIM_IDLE);
            attackCooldown = 16;
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

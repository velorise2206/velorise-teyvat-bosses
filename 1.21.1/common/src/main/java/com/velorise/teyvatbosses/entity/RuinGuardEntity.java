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

/**
 * Boss-tier Ruin Guard (GN/Hu-24601 — Thousand Winds Overlord) synced 1:1 with
 * {@code RuinGuardAnimations.java} (15 Catmull-Rom clips) and {@code ruin_guard_animations_spec.json}.
 *
 * <p>Canon Genshin mechanics:
 * <ul>
 *   <li>55% Physical Resistance while active; 1.5x bonus damage when paralyzed via Eye Weakpoint break.</li>
 *   <li>2 Eye Weakpoint hits (Bow/Crossbow headshot or ranged projectile hitting eye height) trigger 7.5s Stunned Collapse.</li>
 *   <li>Full move-set: 3-Hit Melee Combo, Double Clap, Stomp Shockwave, Homing Missile Barrage (6 -> 12 in Phase 2),
 *       Cyclone Spin Chase (chains into Leap Slam in Phase 2), Leap Slam, and Phase 2 Overdrive at &lt;50% HP.</li>
 *   <li>Spawns {@link TrounceBlossomEntity} upon defeat at the Thousand Winds Temple center.</li>
 * </ul>
 */
public class RuinGuardEntity extends Monster {

    public static final int STATE_DORMANT = 0;
    public static final int STATE_AWAKEN  = 1;
    public static final int STATE_COMBAT  = 2;
    public static final int STATE_STUNNED = 3;
    public static final int STATE_DEATH   = 4;

    public static final int ANIM_DORMANT         = 0;
    public static final int ANIM_AWAKEN          = 1;
    public static final int ANIM_IDLE            = 2;
    public static final int ANIM_WALK            = 3;
    public static final int ANIM_MISSILE_BARRAGE = 4;
    public static final int ANIM_MELEE_3HIT      = 5;
    public static final int ANIM_DOUBLE_CLAP     = 6;
    public static final int ANIM_STOMP_SHOCKWAVE = 7;
    public static final int ANIM_CYCLONE_WINDUP  = 8;
    public static final int ANIM_CYCLONE_ACTIVE  = 9;
    public static final int ANIM_CYCLONE_RECOVER = 10;
    public static final int ANIM_LEAP_SLAM       = 11;
    public static final int ANIM_PHASE2_OVERDRIVE = 12;
    public static final int ANIM_STUNNED         = 13;
    public static final int ANIM_DEATH           = 14;

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

    private final ServerBossEvent bossEvent = new ServerBossEvent(
            Component.translatable("entity.velorise_teyvat_bosses.ruin_guard"),
            BossEvent.BossBarColor.YELLOW,
            BossEvent.BossBarOverlay.NOTCHED_10
    );

    private Vec3 arenaCenter = Vec3.ZERO;
    private boolean arenaInit = false;
    private float savedSpawnYaw = 0.0F;

    private int stateTick = 0;
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
        this.xpReward = 220;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 1200.0D)
                .add(Attributes.ARMOR, 14.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.23D)
                .add(Attributes.ATTACK_DAMAGE, 18.0D)
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
    }

    @Override
    protected void registerGoals() {}

    public int getBossState() { return entityData.get(DATA_BOSS_STATE); }
    public int getAnimId() { return entityData.get(DATA_ANIM_ID); }
    public int getAnimEpoch() { return entityData.get(DATA_ANIM_EPOCH); }
    public int getAnimTick() { return level().isClientSide() ? clientAnimTick : stateTick; }
    public boolean isPhase2() { return entityData.get(DATA_PHASE2); }
    public float getTelegraphRadius() { return entityData.get(DATA_TELEGRAPH_RADIUS); }

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
        entityData.set(DATA_TELEGRAPH_RADIUS, 0.0F);
        stateTick = 0;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        int st = getBossState();
        if (st == STATE_DEATH || st == STATE_AWAKEN) return false;
        if (st == STATE_DORMANT && !level().isClientSide()) {
            awaken((ServerLevel) level());
            return false;
        }

        // Eye Weakpoint Detection: projectile hitting upper eye region (Y >= getY() + 2.35)
        if (st == STATE_COMBAT && !level().isClientSide() && source.getDirectEntity() instanceof Projectile proj) {
            if (proj.getY() >= getY() + 2.35D && proj.getY() <= getY() + 4.1D) {
                eyeHits++;
                eyeResetTimer = 140;
                ServerLevel sl = (ServerLevel) level();
                sl.playSound(null, getX(), getY() + 3.0D, getZ(), SoundEvents.ANVIL_LAND, SoundSource.HOSTILE, 0.9F, 1.65F);
                sl.sendParticles(ParticleTypes.CRIT, getX(), getY() + 3.1D, getZ(), 20, 0.45D, 0.45D, 0.45D, 0.15D);
                if (eyeHits >= 2 || (proj instanceof AbstractArrow && eyeHits >= 2)) {
                    triggerEyeStun(sl);
                }
            }
        }

        // Canon 55% Physical Resistance while active; 1.5x bonus damage while Stunned!
        if (st == STATE_STUNNED) {
            amount *= 1.50F;
        } else if (!source.is(DamageTypeTags.BYPASSES_ARMOR) && !source.is(DamageTypeTags.WITCH_RESISTANT_TO)) {
            amount *= 0.45F;
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

        Player nearest = sl.getNearestPlayer(getX(), getY(), getZ(), 34.0D,
                e -> e instanceof Player p && p.isAlive() && !p.isCreative() && !p.isSpectator());

        if (st == STATE_DORMANT) {
            bossEvent.setVisible(false);
            if (nearest != null && nearest.distanceToSqr(this) <= 13.0D * 13.0D) {
                setTarget(nearest);
                awaken(sl);
            }
            return;
        }

        bossEvent.setVisible(true);
        updateBossBar();

        if (nearest == null) {
            if (stateTick > 180) {
                setPos(arenaCenter.x, arenaCenter.y, arenaCenter.z);
                setHealth(getMaxHealth());
                entityData.set(DATA_PHASE2, false);
                transitionTo(STATE_DORMANT, ANIM_DORMANT);
            }
            return;
        }
        setTarget(nearest);

        // Trigger Phase 2 Overdrive at < 50% HP!
        if (!isPhase2() && getHealth() <= getMaxHealth() * 0.5F && st == STATE_COMBAT && getAnimId() != ANIM_PHASE2_OVERDRIVE) {
            entityData.set(DATA_PHASE2, true);
            transitionTo(STATE_COMBAT, ANIM_PHASE2_OVERDRIVE);
            sl.playSound(null, getX(), getY() + 2.0D, getZ(), SoundEvents.WITHER_SPAWN, SoundSource.HOSTILE, 1.3F, 0.72F);
            return;
        }

        switch (st) {
            case STATE_AWAKEN -> {
                faceTarget(nearest, 4.0F);
                if (stateTick == 17) sl.sendParticles(ParticleTypes.CLOUD, getX(), getY() + 2.6D, getZ(), 18, 0.8D, 0.6D, 0.8D, 0.06D);
                if (stateTick == 40) sl.playSound(null, getX(), getY(), getZ(), SoundEvents.IRON_GOLEM_STEP, SoundSource.HOSTILE, 1.4F, 0.65F);
                if (stateTick >= 52) {
                    transitionTo(STATE_COMBAT, ANIM_IDLE);
                    attackCooldown = 18;
                }
            }
            case STATE_STUNNED -> {
                if (stateTick % 12 == 0) {
                    sl.sendParticles(ParticleTypes.ELECTRIC_SPARK, getX(), getY() + 2.0D, getZ(), 8, 0.7D, 0.5D, 0.7D, 0.05D);
                }
                if (stateTick >= 150) { // 7.5 seconds stun
                    sl.playSound(null, getX(), getY() + 2.0D, getZ(), SoundEvents.BEACON_ACTIVATE, SoundSource.HOSTILE, 1.25F, 0.85F);
                    transitionTo(STATE_COMBAT, ANIM_IDLE);
                    attackCooldown = 24;
                }
            }
            case STATE_COMBAT -> tickCombat(sl, nearest);
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

    private void tickCombat(ServerLevel sl, Player target) {
        int anim = getAnimId();
        double dist = Math.hypot(target.getX() - getX(), target.getZ() - getZ());

        if (anim == ANIM_IDLE || anim == ANIM_WALK) {
            faceTarget(target, 6.5F);
            if (dist > 4.2D) {
                if (anim != ANIM_WALK) transitionTo(STATE_COMBAT, ANIM_WALK);
                Vec3 dir = new Vec3(target.getX() - getX(), 0.0D, target.getZ() - getZ()).normalize();
                setDeltaMovement(dir.x * 0.16D, getDeltaMovement().y, dir.z * 0.16D);
                if (stateTick % 16 == 8) {
                    sl.playSound(null, getX(), getY(), getZ(), SoundEvents.IRON_GOLEM_STEP, SoundSource.HOSTILE, 1.15F, 0.68F);
                }
            } else {
                if (anim != ANIM_IDLE) transitionTo(STATE_COMBAT, ANIM_IDLE);
                setDeltaMovement(0.0D, getDeltaMovement().y, 0.0D);
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
                faceTarget(target, 7.0F);
                if (stateTick == 1) sl.playSound(null, getX(), getY() + 2.0D, getZ(), SoundEvents.IRON_GOLEM_REPAIR, SoundSource.HOSTILE, 1.3F, 0.85F);
                if (stateTick >= 22) transitionTo(STATE_COMBAT, ANIM_CYCLONE_ACTIVE);
            }
            case ANIM_CYCLONE_ACTIVE -> tickCycloneActive(sl, target);
            case ANIM_CYCLONE_RECOVER -> {
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

    private void chooseNextAttack(ServerLevel sl, Player target, double dist) {
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

    private void startLeapSlam(ServerLevel sl, Player target) {
        leapStart = position();
        leapTarget = target.position();
        transitionTo(STATE_COMBAT, ANIM_LEAP_SLAM);
        entityData.set(DATA_TELEGRAPH_RADIUS, 6.0F);
        sl.playSound(null, getX(), getY() + 2.0D, getZ(), SoundEvents.RAVAGER_ROAR, SoundSource.HOSTILE, 1.2F, 0.72F);
    }

    private void tickMissileBarrage(ServerLevel sl, Player target) {
        faceTarget(target, 6.0F);
        int totalMissiles = isPhase2() ? 12 : 6;
        // Active fire window: ticks 23..35 (1.15s - 1.75s)
        if (stateTick >= 23 && stateTick < 23 + totalMissiles) {
            int idx = stateTick - 23;
            double side = (idx % 2 == 0) ? -1.1D : 1.1D;
            float yawRad = getYRot() * Mth.DEG_TO_RAD;
            Vec3 right = new Vec3(Math.cos(yawRad), 0.0D, Math.sin(yawRad));
            Vec3 origin = position().add(right.scale(side)).add(0.0D, 3.65D, 0.0D);
            Vec3 launchVel = right.scale(side * 0.24D).add(0.0D, 0.65D + (idx % 3) * 0.08D, 0.0D);
            RuinMissileProjectile.launch(sl, this, target, origin, launchVel, 11.5F);
        }
        if (stateTick >= 48) {
            transitionTo(STATE_COMBAT, ANIM_IDLE);
            attackCooldown = 34;
        }
    }

    private void tickMelee3Hit(ServerLevel sl, Player target) {
        if (stateTick < 15 || (stateTick > 20 && stateTick < 30)) faceTarget(target, 7.0F);
        if (stateTick == 17) {
            dealArcDamage(sl, 4.6D, 140.0F, 14.0F, 0.8D);
            sl.playSound(null, getX(), getY() + 1.5D, getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.HOSTILE, 1.5F, 0.62F);
        } else if (stateTick == 31) {
            dealArcDamage(sl, 4.6D, 140.0F, 15.0F, 0.85D);
            sl.playSound(null, getX(), getY() + 1.5D, getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.HOSTILE, 1.5F, 0.56F);
        } else if (stateTick == 38) {
            entityData.set(DATA_TELEGRAPH_RADIUS, 5.2F);
        } else if (stateTick == 50) {
            entityData.set(DATA_TELEGRAPH_RADIUS, 0.0F);
            dealRadialDamage(sl, 5.2D, 20.0F, 1.1D, 0.55D);
            sl.playSound(null, getX(), getY(), getZ(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 1.4F, 0.72F);
            sl.sendParticles(ParticleTypes.EXPLOSION, getX(), getY() + 0.4D, getZ(), 3, 1.8D, 0.3D, 1.8D, 0.0D);
        }
        if (stateTick >= 68) {
            transitionTo(STATE_COMBAT, ANIM_IDLE);
            attackCooldown = 28;
        }
    }

    private void tickDoubleClap(ServerLevel sl, Player target) {
        if (stateTick < 18) faceTarget(target, 7.0F);
        if (stateTick == 21) {
            dealArcDamage(sl, 4.8D, 85.0F, 18.5F, 1.15D);
            sl.playSound(null, getX(), getY() + 1.8D, getZ(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 1.25F, 0.82F);
            sl.sendParticles(ParticleTypes.GUST_EMITTER_SMALL, getX(), getY() + 1.6D, getZ(), 2, 1.0D, 0.5D, 1.0D, 0.0D);
        }
        if (stateTick >= 42) {
            transitionTo(STATE_COMBAT, ANIM_IDLE);
            attackCooldown = 26;
        }
    }

    private void tickStompShockwave(ServerLevel sl) {
        if (stateTick == 12) entityData.set(DATA_TELEGRAPH_RADIUS, 5.0F);
        if (stateTick == 18) {
            entityData.set(DATA_TELEGRAPH_RADIUS, 0.0F);
            dealRadialDamage(sl, 5.0D, 17.0F, 1.05D, 0.52D);
            sl.playSound(null, getX(), getY(), getZ(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 1.35F, 0.76F);
            sl.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, getX(), getY() + 0.2D, getZ(), 24, 2.2D, 0.15D, 2.2D, 0.06D);
        }
        if (stateTick >= 36) {
            transitionTo(STATE_COMBAT, ANIM_IDLE);
            attackCooldown = 24;
        }
    }

    private void tickCycloneActive(ServerLevel sl, Player target) {
        // Chase player at 0.34 speed while spinning violently!
        Vec3 toTarget = new Vec3(target.getX() - getX(), 0.0D, target.getZ() - getZ());
        if (toTarget.lengthSqr() > 0.25D) {
            Vec3 move = toTarget.normalize().scale(0.32D);
            setDeltaMovement(move.x, getDeltaMovement().y, move.z);
        }
        if (stateTick % 6 == 0) {
            dealRadialDamage(sl, 4.8D, 11.0F, 0.85D, 0.35D);
            sl.playSound(null, getX(), getY() + 1.8D, getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.HOSTILE, 1.35F, 0.85F);
            sl.sendParticles(ParticleTypes.SWEEP_ATTACK, getX(), getY() + 1.9D, getZ(), 4, 2.4D, 0.3D, 2.4D, 0.0D);
        }
        int maxSpinTicks = isPhase2() ? 128 : 96; // 6.4s vs 4.8s
        if (stateTick >= maxSpinTicks) {
            setDeltaMovement(0.0D, getDeltaMovement().y, 0.0D);
            transitionTo(STATE_COMBAT, ANIM_CYCLONE_RECOVER);
        }
    }

    private void tickLeapSlam(ServerLevel sl, Player target) {
        if (stateTick < 13) {
            faceTarget(target, 8.0F);
            leapTarget = target.position();
        } else if (stateTick >= 13 && stateTick <= 26) {
            // Smooth parabolic airborne leap (0.65s - 1.30s)
            float f = (stateTick - 13) / 13.0F;
            double nx = Mth.lerp(f, leapStart.x, leapTarget.x);
            double nz = Mth.lerp(f, leapStart.z, leapTarget.z);
            double ny = Mth.lerp(f, leapStart.y, leapTarget.y) + Math.sin(f * Math.PI) * 4.8D;
            setPos(nx, ny, nz);
            setDeltaMovement(Vec3.ZERO);
        } else if (stateTick == 27) {
            entityData.set(DATA_TELEGRAPH_RADIUS, 0.0F);
            setPos(leapTarget.x, arenaCenter.y, leapTarget.z);
            dealRadialDamage(sl, 6.2D, 23.0F, 1.35D, 0.65D);
            sl.playSound(null, getX(), getY(), getZ(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 1.65F, 0.62F);
            sl.sendParticles(ParticleTypes.EXPLOSION_EMITTER, getX(), getY() + 0.5D, getZ(), 1, 0.5D, 0.2D, 0.5D, 0.0D);
            sl.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, getX(), getY() + 0.2D, getZ(), 32, 3.0D, 0.2D, 3.0D, 0.08D);
        }
        if (stateTick >= 54) {
            transitionTo(STATE_COMBAT, ANIM_IDLE);
            attackCooldown = 32;
        }
    }

    private void tickPhase2Overdrive(ServerLevel sl) {
        if (stateTick == 1) entityData.set(DATA_TELEGRAPH_RADIUS, 8.0F);
        if (stateTick == 23) {
            entityData.set(DATA_TELEGRAPH_RADIUS, 0.0F);
            dealRadialDamage(sl, 8.0D, 16.0F, 1.65D, 0.60D);
            sl.playSound(null, getX(), getY() + 1.5D, getZ(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 1.6F, 0.68F);
            sl.sendParticles(ParticleTypes.FLAME, getX(), getY() + 2.0D, getZ(), 55, 2.8D, 1.6D, 2.8D, 0.14D);
        }
        if (stateTick >= 48) {
            transitionTo(STATE_COMBAT, ANIM_IDLE);
            attackCooldown = 16;
        }
    }

    private void dealArcDamage(ServerLevel sl, double radius, float arcDeg, float dmg, double knockback) {
        Vec3 forward = Vec3.directionFromRotation(0.0F, getYRot());
        double cosHalf = Math.cos(Math.toRadians(arcDeg * 0.5D));
        AABB box = new AABB(getX() - radius, getY() - 0.5D, getZ() - radius,
                getX() + radius, getY() + 4.2D, getZ() + radius);
        for (Player p : sl.getEntitiesOfClass(Player.class, box, pl -> pl.isAlive() && !pl.isCreative() && !pl.isSpectator())) {
            Vec3 toP = new Vec3(p.getX() - getX(), 0.0D, p.getZ() - getZ());
            double d = toP.length();
            if (d <= radius && (d < 1.2D || toP.normalize().dot(forward) >= cosHalf)) {
                StavesElementBridge.dealElementalDamage(p, this, "PHYSICAL", dmg, 0.0F, true, 0.25F);
                Vec3 push = d > 0.05D ? toP.normalize().scale(knockback) : forward.scale(knockback);
                p.setDeltaMovement(push.x, 0.38D, push.z);
                p.hurtMarked = true;
            }
        }
    }

    private void dealRadialDamage(ServerLevel sl, double radius, float dmg, double knockback, double lift) {
        AABB box = new AABB(getX() - radius, getY() - 0.5D, getZ() - radius,
                getX() + radius, getY() + 4.2D, getZ() + radius);
        for (Player p : sl.getEntitiesOfClass(Player.class, box, pl -> pl.isAlive() && !pl.isCreative() && !pl.isSpectator())) {
            Vec3 toP = new Vec3(p.getX() - getX(), 0.0D, p.getZ() - getZ());
            if (toP.length() <= radius) {
                StavesElementBridge.dealElementalDamage(p, this, "PHYSICAL", dmg, 0.0F, true, 0.30F);
                Vec3 push = toP.lengthSqr() > 0.01D ? toP.normalize().scale(knockback) : new Vec3(0.0D, 0.0D, knockback);
                p.setDeltaMovement(push.x, lift, push.z);
                p.hurtMarked = true;
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

package com.velorise.teyvatbosses.entity;

import com.velorise.teyvatbosses.compat.StavesElementBridge;
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
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * Cryo Regisvine World Boss (Velorise: Teyvat Bosses edition).
 *
 * <p>Implements official Genshin Impact Cryo Regisvine mechanics:
 * <ul>
 *   <li>Rooted at spawn anchor, zero terrain griefing.</li>
 *   <li>Two-phase Cryo Weakpoint Shield (Roots 12U -> Corolla 6U) via {@link StavesElementBridge},
 *       with 14s Stunned vulnerability window (1.35x damage) on shield shatter.</li>
 *   <li>All 14 overhauled Cataclysm-style Windup -> Hold -> Snap animations with exact tick-synced hitboxes
 *       and ground telegraph particle cues.</li>
 * </ul>
 */
public class CryoRegisvineEntity extends Monster {

    public static final int STATE_DORMANT = 0;
    public static final int STATE_AWAKEN  = 1;
    public static final int STATE_COMBAT  = 2;
    public static final int STATE_STUNNED = 3;
    public static final int STATE_DEATH   = 4;

    public static final int ANIM_DORMANT          = 0;
    public static final int ANIM_AWAKEN           = 1;
    public static final int ANIM_IDLE_COMBAT      = 2;
    public static final int ANIM_ICE_BEAMS_CROSS  = 3;
    public static final int ANIM_HOMING_ICICLES   = 4;
    public static final int ANIM_FACEPLANT_SINGLE = 5;
    public static final int ANIM_FACEPLANT_TRIPLE = 6;
    public static final int ANIM_SPIN_FROST_RING  = 7;
    public static final int ANIM_BLIZZARD_CHANNEL = 8;
    public static final int ANIM_FLAIL_360        = 9;
    public static final int ANIM_HALF_FLAIL_COMBO = 10;
    public static final int ANIM_ROTARY_SPRAY     = 11;
    public static final int ANIM_STUNNED          = 12;
    public static final int ANIM_DEATH            = 13;

    public static final String[] ANIM_NAMES = {
            "dormant_buried",
            "awaken_emerge",
            "idle_combat",
            "ice_beams_cross",
            "homing_icicles",
            "faceplant_single",
            "faceplant_triple",
            "spin_frost_ring",
            "blizzard_channel",
            "flail_360",
            "half_flail_combo",
            "rotary_spray_beam",
            "stunned_paralyzed",
            "death_wither"
    };

    public static final int[] ANIM_DURATIONS_TICKS = {
            60, 44, 80, 52, 48, 44, 84, 56, 72, 56, 80, 96, 80, 52
    };

    public static final float ROOTS_SHIELD_UNITS = 12.0F;
    public static final float COROLLA_SHIELD_UNITS = 6.0F;
    private static final int STUN_DURATION_TICKS = 280; // 14.0 seconds

    private static final EntityDataAccessor<Integer> DATA_STATE =
            SynchedEntityData.defineId(CryoRegisvineEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_ANIM_ID =
            SynchedEntityData.defineId(CryoRegisvineEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_ANIM_EPOCH =
            SynchedEntityData.defineId(CryoRegisvineEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DATA_SHIELD_PROGRESS =
            SynchedEntityData.defineId(CryoRegisvineEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> DATA_COROLLA_PHASE =
            SynchedEntityData.defineId(CryoRegisvineEntity.class, EntityDataSerializers.INT);

    private static volatile boolean SHIELD_LISTENER_REGISTERED = false;

    private final ServerBossEvent bossEvent = new ServerBossEvent(
            Component.translatable("entity.velorise_teyvat_bosses.cryo_regisvine"),
            BossEvent.BossBarColor.BLUE,
            BossEvent.BossBarOverlay.NOTCHED_10
    );

    private Vec3 anchorPos = Vec3.ZERO;
    private boolean anchorInitialized = false;
    private int stateTicks = 0;
    private int serverAnimTick = 0;
    private int clientAnimTick = 0;
    private int lastSyncedAnimId = -1;
    private int lastSyncedEpoch = -1;
    private int attackCooldown = 30;
    private int noPlayerTicks = 0;
    private int lastAttackAnim = -1;
    private float attackStartYaw = 0.0F;
    private boolean customDeathHandled = false;
    private float fallbackShield = 0.0F;

    private static final class BlizzardZone {
        final double x, y, z;
        int ticksLeft;
        BlizzardZone(double x, double y, double z, int ticksLeft) {
            this.x = x; this.y = y; this.z = z; this.ticksLeft = ticksLeft;
        }
    }

    private final List<BlizzardZone> blizzardZones = new ArrayList<>();

    public CryoRegisvineEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.xpReward = 250;
        this.setPersistenceRequired();
        ensureShieldBreakListener();
    }

    private static void ensureShieldBreakListener() {
        if (!SHIELD_LISTENER_REGISTERED) {
            SHIELD_LISTENER_REGISTERED = true;
            StavesElementBridge.registerShieldBreakListener((level, target, attacker, shieldElem, breakerElem) -> {
                if (target instanceof CryoRegisvineEntity boss && boss.getBossState() == STATE_COMBAT) {
                    boss.onCorollaShieldBroken(level);
                }
            });
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 450.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.ARMOR, 10.0D)
                .add(Attributes.ARMOR_TOUGHNESS, 6.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.ATTACK_DAMAGE, 20.0D);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_STATE, STATE_DORMANT);
        builder.define(DATA_ANIM_ID, ANIM_DORMANT);
        builder.define(DATA_ANIM_EPOCH, 0);
        builder.define(DATA_SHIELD_PROGRESS, 0.0F);
        builder.define(DATA_COROLLA_PHASE, 0);
    }

    public int getBossState() { return entityData.get(DATA_STATE); }
    public int getAnimId() { return entityData.get(DATA_ANIM_ID); }
    public int getAnimTick() { return level().isClientSide() ? clientAnimTick : serverAnimTick; }
    public int getAnimEpoch() { return entityData.get(DATA_ANIM_EPOCH); }
    public float getShieldProgress() { return entityData.get(DATA_SHIELD_PROGRESS); }
    public int getCorollaPhase() { return entityData.get(DATA_COROLLA_PHASE); }

    public String getCurrentAnimName() {
        int id = getAnimId();
        return (id >= 0 && id < ANIM_NAMES.length) ? ANIM_NAMES[id] : "idle_combat";
    }

    private void transitionTo(int newState, int animId) {
        entityData.set(DATA_STATE, newState);
        entityData.set(DATA_ANIM_ID, animId);
        entityData.set(DATA_ANIM_EPOCH, entityData.get(DATA_ANIM_EPOCH) + 1);
        serverAnimTick = 0;
        stateTicks = 0;
    }

    private void playAnim(int animId) {
        entityData.set(DATA_ANIM_ID, animId);
        entityData.set(DATA_ANIM_EPOCH, entityData.get(DATA_ANIM_EPOCH) + 1);
        serverAnimTick = 0;
    }

    private void applyPhaseShield() {
        float shieldUnits = (getCorollaPhase() == 0) ? ROOTS_SHIELD_UNITS : COROLLA_SHIELD_UNITS;
        if (StavesElementBridge.isAvailable()) {
            StavesElementBridge.setElementalShield(this, "CRYO", shieldUnits, true, 0.15F);
        } else {
            fallbackShield = shieldUnits * 6.0F;
        }
        entityData.set(DATA_SHIELD_PROGRESS, 1.0F);
    }

    public void onCorollaShieldBroken(ServerLevel level) {
        transitionTo(STATE_STUNNED, ANIM_STUNNED);
        fallbackShield = 0.0F;
        entityData.set(DATA_SHIELD_PROGRESS, 0.0F);
        level.playSound(null, getX(), getY(), getZ(), SoundEvents.WITHER_BREAK_BLOCK, SoundSource.HOSTILE, 1.45F, 0.65F);
        level.playSound(null, getX(), getY(), getZ(), SoundEvents.GLASS_BREAK, SoundSource.HOSTILE, 1.6F, 0.75F);
        level.sendParticles(ParticleTypes.SNOWFLAKE, getX(), getY() + 2.5D, getZ(), 56, 2.2D, 1.8D, 2.2D, 0.12D);
    }

    @Override
    protected void registerGoals() {}

    @Override
    public boolean isPushable() { return false; }

    @Override
    protected void doPush(Entity entity) {}

    @Override
    public void knockback(double strength, double x, double z) {}

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) { return false; }

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

    @Override
    public boolean hurt(DamageSource source, float amount) {
        int st = getBossState();
        if (st == STATE_DEATH || st == STATE_AWAKEN) return false;
        if (!level().isClientSide() && source.getEntity() instanceof LivingEntity attacker
                && attacker != this && !(attacker instanceof CryoRegisvineEntity)
                && !(attacker instanceof Player p && (p.isCreative() || p.isSpectator()))) {
            setTarget(attacker);
        }
        if (st == STATE_DORMANT && !level().isClientSide()) {
            awaken((ServerLevel) level());
            return false;
        }
        if (st == STATE_STUNNED) {
            amount *= 1.35F;
        } else if (StavesElementBridge.isAvailable()) {
            if (StavesElementBridge.hasElementalShield(this)) {
                amount *= 0.25F;
            }
        } else if (fallbackShield > 0.0F) {
            float shieldDmg = source.is(net.minecraft.tags.DamageTypeTags.IS_FIRE) ? amount * 2.5F : amount * 0.6F;
            fallbackShield = Math.max(0.0F, fallbackShield - shieldDmg);
            float maxShield = ((getCorollaPhase() == 0) ? ROOTS_SHIELD_UNITS : COROLLA_SHIELD_UNITS) * 6.0F;
            entityData.set(DATA_SHIELD_PROGRESS, fallbackShield / maxShield);
            if (fallbackShield <= 0.0F && level() instanceof ServerLevel sl) {
                onCorollaShieldBroken(sl);
            }
            amount *= 0.25F;
        }
        if (source.getEntity() instanceof LivingEntity && !(source.getEntity() instanceof Player)) {
            amount *= 0.55F;
        }
        amount = Math.min(amount, 26.0F);
        return super.hurt(source, amount);
    }

    @Override
    public void die(DamageSource cause) {
        if (!level().isClientSide() && !customDeathHandled) {
            customDeathHandled = true;
            StavesElementBridge.clearElementalShield(this);
            transitionTo(STATE_DEATH, ANIM_DEATH);
            if (level() instanceof ServerLevel sl) {
                sl.playSound(null, getX(), getY(), getZ(), SoundEvents.GLASS_BREAK, SoundSource.HOSTILE, 1.6F, 0.55F);
            }
        }
        super.die(cause);
    }

    private void awaken(ServerLevel sl) {
        entityData.set(DATA_COROLLA_PHASE, 0);
        transitionTo(STATE_AWAKEN, ANIM_AWAKEN);
        applyPhaseShield();
        sl.playSound(null, getX(), getY(), getZ(), SoundEvents.ELDER_GUARDIAN_CURSE, SoundSource.HOSTILE, 1.25F, 1.25F);
        sl.sendParticles(ParticleTypes.SNOWFLAKE, getX(), getY() + 1.2D, getZ(), 42, 1.8D, 1.4D, 1.8D, 0.1D);
    }

    @Override
    public void tick() {
        super.tick();
        if (!anchorInitialized) {
            anchorPos = position();
            anchorInitialized = true;
        }
        setDeltaMovement(Vec3.ZERO);
        if (position().distanceToSqr(anchorPos) > 0.01D) {
            setPos(anchorPos.x, anchorPos.y, anchorPos.z);
        }

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
        stateTicks++;
        serverAnimTick++;
        tickBlizzardZones(sl);

        int st = getBossState();
        updateBossBar(st);

        if (st == STATE_DEATH) {
            if (stateTicks >= ANIM_DURATIONS_TICKS[ANIM_DEATH]) discard();
            return;
        }

        LivingEntity nearest = findActiveTarget(sl, 28.0D);

        if (st == STATE_DORMANT) {
            bossEvent.setVisible(false);
            if (tickCount % 10 == 0) {
                sl.sendParticles(ParticleTypes.SNOWFLAKE, getX(), getY() + 0.5D, getZ(), 4, 1.2D, 0.3D, 1.2D, 0.01D);
            }
            if (nearest != null && nearest.distanceToSqr(this) <= 100.0D) {
                awaken(sl);
            }
            return;
        }

        bossEvent.setVisible(true);

        if (nearest == null) {
            noPlayerTicks++;
            if (noPlayerTicks >= 200) {
                noPlayerTicks = 0;
                setHealth(getMaxHealth());
                StavesElementBridge.clearElementalShield(this);
                fallbackShield = 0.0F;
                entityData.set(DATA_SHIELD_PROGRESS, 0.0F);
                transitionTo(STATE_DORMANT, ANIM_DORMANT);
            }
            return;
        } else {
            noPlayerTicks = 0;
        }

        if (st == STATE_AWAKEN) {
            faceTarget(nearest, 12.0F);
            if (stateTicks == 15) {
                sl.playSound(null, getX(), getY(), getZ(), SoundEvents.RAVAGER_ROAR, SoundSource.HOSTILE, 1.3F, 1.15F);
            }
            if (stateTicks >= ANIM_DURATIONS_TICKS[ANIM_AWAKEN]) {
                transitionTo(STATE_COMBAT, ANIM_IDLE_COMBAT);
                attackCooldown = 24;
            }
            return;
        }

        if (st == STATE_STUNNED) {
            if (stateTicks % 15 == 0) {
                sl.sendParticles(ParticleTypes.SNOWFLAKE, getX(), getY() + 1.2D, getZ(), 6, 1.4D, 0.4D, 1.4D, 0.01D);
            }
            if (stateTicks >= STUN_DURATION_TICKS) {
                int nextPhase = (getCorollaPhase() + 1) % 2;
                entityData.set(DATA_COROLLA_PHASE, nextPhase);
                applyPhaseShield();
                transitionTo(STATE_COMBAT, ANIM_SPIN_FROST_RING);
                attackStartYaw = getYRot();
                attackCooldown = 35;
                sl.playSound(null, getX(), getY(), getZ(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.HOSTILE, 1.4F, 0.85F);
            }
            return;
        }

        if (StavesElementBridge.isAvailable()) {
            entityData.set(DATA_SHIELD_PROGRESS, StavesElementBridge.getShieldProgress(this));
        }

        int anim = getAnimId();
        int aTick = getAnimTick();

        if (anim == ANIM_IDLE_COMBAT) {
            faceTarget(nearest, 8.0F);
            if (attackCooldown > 0) {
                attackCooldown--;
            } else {
                pickNextAttack(nearest);
            }
        } else {
            executeAttackTick(sl, nearest, anim, aTick);
            if (aTick >= ANIM_DURATIONS_TICKS[anim]) {
                playAnim(ANIM_IDLE_COMBAT);
                attackCooldown = 22 + random.nextInt(16);
            }
        }
    }

    private void pickNextAttack(LivingEntity target) {
        double dist = Math.sqrt(target.distanceToSqr(this));
        int[] pool = dist < 7.5D
                ? new int[]{ANIM_FACEPLANT_SINGLE, ANIM_FACEPLANT_TRIPLE, ANIM_FLAIL_360, ANIM_HALF_FLAIL_COMBO, ANIM_SPIN_FROST_RING}
                : new int[]{ANIM_ICE_BEAMS_CROSS, ANIM_HOMING_ICICLES, ANIM_BLIZZARD_CHANNEL, ANIM_ROTARY_SPRAY, ANIM_FACEPLANT_TRIPLE};
        int chosen = pool[random.nextInt(pool.length)];
        if (chosen == lastAttackAnim && pool.length > 1) {
            chosen = pool[(random.nextInt(pool.length - 1) + 1) % pool.length];
        }
        lastAttackAnim = chosen;
        faceTarget(target, 180.0F);
        attackStartYaw = getYRot();
        playAnim(chosen);
    }

    /**
     * All impact ticks strictly synced 1:1 with the overhauled CryoRegisvineAnimations keyframes.
     */
    private void executeAttackTick(ServerLevel sl, LivingEntity target, int anim, int t) {
        switch (anim) {
            case ANIM_FACEPLANT_SINGLE -> {
                if (t < 11) faceTarget(target, 10.0F);
                if (t == 10 || t == 13) spawnSlamTelegraph(sl, getYRot(), 4.2D, 3.8D);
                if (t == 17) impactSlamAtYaw(sl, getYRot(), 4.2D, 4.0D, 24.0F);
            }
            case ANIM_FACEPLANT_TRIPLE -> {
                if (t < 9) faceTarget(target, 12.0F);
                if (t == 12) spawnSlamTelegraph(sl, getYRot() - 26.0F, 4.2D, 3.6D);
                if (t == 17) impactSlamAtYaw(sl, getYRot() - 26.0F, 4.2D, 3.8D, 20.0F);
                if (t == 32) spawnSlamTelegraph(sl, getYRot(), 4.4D, 3.6D);
                if (t == 37) impactSlamAtYaw(sl, getYRot(), 4.4D, 3.8D, 21.0F);
                if (t == 52) spawnSlamTelegraph(sl, getYRot() + 26.0F, 4.2D, 3.8D);
                if (t == 57) impactSlamAtYaw(sl, getYRot() + 26.0F, 4.2D, 4.0D, 24.0F);
            }
            case ANIM_FLAIL_360 -> {
                if (t == 12) sl.playSound(null, getX(), getY(), getZ(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.HOSTILE, 1.1F, 0.7F);
                if (t == 19 || t == 29) ringAoE(sl, 8.0D, 18.0F, 0.85D);
            }
            case ANIM_HALF_FLAIL_COMBO -> {
                if (t < 11 || (t > 24 && t < 31) || (t > 44 && t < 53)) faceTarget(target, 10.0F);
                if (t == 19 || t == 37) arcSweepInFront(sl, 8.4D, 17.0F);
                if (t == 53) spawnSlamTelegraph(sl, getYRot(), 4.2D, 4.0D);
                if (t == 59) impactSlamAtYaw(sl, getYRot(), 4.2D, 4.2D, 26.0F);
            }
            case ANIM_SPIN_FROST_RING -> {
                if (t == 17 || t == 29) ringAoE(sl, 9.2D, 17.5F, 1.2D);
            }
            case ANIM_ICE_BEAMS_CROSS -> {
                if (t < 12) faceTarget(target, 10.0F);
                if (t >= 16 && t <= 42 && t % 4 == 0) {
                    float progress = (t - 16) / 26.0F;
                    float angleA = getYRot() - 36.0F + progress * 72.0F;
                    float angleB = getYRot() + 36.0F - progress * 72.0F;
                    sweepBeamRay(sl, angleA, 17.0D, 14.0F);
                    sweepBeamRay(sl, angleB, 17.0D, 14.0F);
                }
            }
            case ANIM_ROTARY_SPRAY -> {
                if (t >= 16 && t <= 82 && t % 3 == 0) {
                    float a = Mth.clamp((t - 14.0F) / 68.0F, 0.0F, 1.0F);
                    float smooth = a * a * (3.0F - 2.0F * a);
                    float beamYaw = attackStartYaw - smooth * 360.0F;
                    sweepBeamRay(sl, beamYaw, 18.0D, 15.5F);
                }
            }
            case ANIM_HOMING_ICICLES -> {
                faceTarget(target, 10.0F);
                if (t == 16 || t == 21 || t == 26 || t == 31 || t == 36) {
                    double side = (t % 2 == 0) ? 2.2D : -2.2D;
                    float rad = getYRot() * Mth.DEG_TO_RAD;
                    Vec3 right = new Vec3(Math.cos(rad), 0.0D, Math.sin(rad));
                    Vec3 origin = position().add(right.scale(side)).add(0.0D, 4.8D, 0.0D);
                    Vec3 dir = target.position().add(0.0D, 1.0D, 0.0D).subtract(origin).normalize().scale(0.68D);
                    CryoIcicleProjectile.spawn(sl, this, target, origin, dir, 11.5F);
                    sl.playSound(null, origin.x, origin.y, origin.z, SoundEvents.SNOW_GOLEM_SHOOT, SoundSource.HOSTILE, 0.9F, 1.35F);
                }
            }
            case ANIM_BLIZZARD_CHANNEL -> {
                faceTarget(target, 6.0F);
                if (t == 14 || t == 24 || t == 34 || t == 44 || t == 54) {
                    double ox = (random.nextDouble() - 0.5D) * 5.0D;
                    double oz = (random.nextDouble() - 0.5D) * 5.0D;
                    blizzardZones.add(new BlizzardZone(target.getX() + ox, target.getY(), target.getZ() + oz, 22));
                }
            }
            default -> {}
        }
    }

    private void spawnSlamTelegraph(ServerLevel sl, float yawDeg, double forwardDist, double radius) {
        float rad = yawDeg * Mth.DEG_TO_RAD;
        Vec3 center = position().add(-Math.sin(rad) * forwardDist, 0.15D, Math.cos(rad) * forwardDist);
        for (int i = 0; i < 20; i++) {
            double ang = (Math.PI * 2.0D * i) / 20.0D;
            sl.sendParticles(ParticleTypes.SNOWFLAKE,
                    center.x + Math.cos(ang) * radius, center.y, center.z + Math.sin(ang) * radius,
                    1, 0.05D, 0.05D, 0.05D, 0.01D);
        }
        sl.playSound(null, center.x, center.y, center.z, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.HOSTILE, 0.7F, 1.35F);
    }

    private void impactSlamAtYaw(ServerLevel sl, float yawDeg, double forwardDist, double radius, float dmg) {
        float rad = yawDeg * Mth.DEG_TO_RAD;
        Vec3 center = position().add(-Math.sin(rad) * forwardDist, 0.5D, Math.cos(rad) * forwardDist);
        sl.sendParticles(ParticleTypes.SNOWFLAKE, center.x, center.y + 0.4D, center.z, 40, radius * 0.5D, 0.5D, radius * 0.5D, 0.09D);
        sl.sendParticles(ParticleTypes.EXPLOSION, center.x, center.y + 0.5D, center.z, 3, 0.5D, 0.2D, 0.5D, 0.01D);
        sl.playSound(null, center.x, center.y, center.z, SoundEvents.GENERIC_EXPLODE, SoundSource.HOSTILE, 1.15F, 0.75F);
        sl.playSound(null, center.x, center.y, center.z, SoundEvents.GLASS_BREAK, SoundSource.HOSTILE, 1.1F, 0.8F);
        AABB box = new AABB(center.x - radius, center.y - 1.5D, center.z - radius, center.x + radius, center.y + 3.0D, center.z + radius);
        for (LivingEntity e : sl.getEntitiesOfClass(LivingEntity.class, box, le -> le.isAlive() && le != this && !(le instanceof CryoRegisvineEntity))) {
            if (e.position().distanceToSqr(center) <= radius * radius) {
                StavesElementBridge.dealElementalDamage(e, this, "CRYO", dmg, 1.0F, false, 0.25F);
            }
        }
    }

    private void ringAoE(ServerLevel sl, double radius, float dmg, double pushStrength) {
        for (int i = 0; i < 36; i++) {
            double ang = (Math.PI * 2.0D * i) / 36.0D;
            sl.sendParticles(ParticleTypes.SNOWFLAKE, getX() + Math.cos(ang) * radius * 0.8D, getY() + 0.8D, getZ() + Math.sin(ang) * radius * 0.8D, 2, 0.2D, 0.2D, 0.2D, 0.05D);
        }
        sl.playSound(null, getX(), getY(), getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.HOSTILE, 1.4F, 0.65F);
        AABB box = getBoundingBox().inflate(radius, 2.5D, radius);
        for (LivingEntity e : sl.getEntitiesOfClass(LivingEntity.class, box, le -> le.isAlive() && le != this && !(le instanceof CryoRegisvineEntity))) {
            if (e.distanceToSqr(this) <= radius * radius) {
                StavesElementBridge.dealElementalDamage(e, this, "CRYO", dmg, 1.0F, false, 0.25F);
                Vec3 push = e.position().subtract(position()).normalize().scale(pushStrength);
                e.push(push.x, 0.35D, push.z);
            }
        }
    }

    private void arcSweepInFront(ServerLevel sl, double radius, float dmg) {
        float rad = getYRot() * Mth.DEG_TO_RAD;
        Vec3 forward = new Vec3(-Math.sin(rad), 0.0D, Math.cos(rad));
        sl.playSound(null, getX(), getY(), getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.HOSTILE, 1.3F, 0.78F);
        AABB box = getBoundingBox().inflate(radius, 2.5D, radius);
        for (LivingEntity e : sl.getEntitiesOfClass(LivingEntity.class, box, le -> le.isAlive() && le != this && !(le instanceof CryoRegisvineEntity))) {
            Vec3 to = e.position().subtract(position());
            if (to.horizontalDistanceSqr() <= radius * radius && to.normalize().dot(forward) > 0.1D) {
                StavesElementBridge.dealElementalDamage(e, this, "CRYO", dmg, 1.0F, false, 0.25F);
            }
        }
    }

    private void sweepBeamRay(ServerLevel sl, float yawDeg, double length, float dmg) {
        float rad = yawDeg * Mth.DEG_TO_RAD;
        Vec3 dir = new Vec3(-Math.sin(rad), 0.0D, Math.cos(rad));
        Vec3 start = position().add(0.0D, 1.4D, 0.0D);
        for (double step = 2.0D; step <= length; step += 1.6D) {
            Vec3 pt = start.add(dir.scale(step));
            sl.sendParticles(ParticleTypes.SNOWFLAKE, pt.x, pt.y, pt.z, 2, 0.18D, 0.18D, 0.18D, 0.01D);
        }
        AABB sweepBox = getBoundingBox().inflate(length, 3.0D, length);
        for (LivingEntity e : sl.getEntitiesOfClass(LivingEntity.class, sweepBox, le -> le.isAlive() && le != this && !(le instanceof CryoRegisvineEntity))) {
            Vec3 mid = e.position().add(0.0D, e.getBbHeight() * 0.5D, 0.0D);
            double proj = mid.subtract(start).dot(dir);
            if (proj > 1.0D && proj <= length) {
                if (mid.distanceToSqr(start.add(dir.scale(proj))) <= 2.25D) {
                    StavesElementBridge.dealElementalDamage(e, this, "CRYO", dmg, 1.0F, false, 0.25F);
                }
            }
        }
    }

    private void tickBlizzardZones(ServerLevel sl) {
        Iterator<BlizzardZone> it = blizzardZones.iterator();
        while (it.hasNext()) {
            BlizzardZone z = it.next();
            z.ticksLeft--;
            if (z.ticksLeft > 0) {
                if (z.ticksLeft % 3 == 0) {
                    sl.sendParticles(ParticleTypes.SNOWFLAKE, z.x, z.y + 0.15D, z.z, 8, 1.5D, 0.1D, 1.5D, 0.01D);
                }
            } else {
                sl.sendParticles(ParticleTypes.SNOWFLAKE, z.x, z.y + 1.0D, z.z, 28, 1.4D, 1.2D, 1.4D, 0.1D);
                sl.playSound(null, z.x, z.y, z.z, SoundEvents.GLASS_BREAK, SoundSource.HOSTILE, 0.9F, 1.1F);
                AABB box = new AABB(z.x - 2.6D, z.y - 1.0D, z.z - 2.6D, z.x + 2.6D, z.y + 3.2D, z.z + 2.6D);
                for (LivingEntity e : sl.getEntitiesOfClass(LivingEntity.class, box, le -> le.isAlive() && le != this && !(le instanceof CryoRegisvineEntity))) {
                    StavesElementBridge.dealElementalDamage(e, this, "CRYO", 15.0F, 1.0F, false, 0.25F);
                }
                it.remove();
            }
        }
    }

    private void updateBossBar(int st) {
        bossEvent.setProgress(Mth.clamp(getHealth() / getMaxHealth(), 0.0F, 1.0F));
        if (st == STATE_STUNNED) {
            bossEvent.setColor(BossEvent.BossBarColor.WHITE);
            bossEvent.setName(Component.translatable("entity.velorise_teyvat_bosses.cryo_regisvine")
                    .append(Component.literal(" — STUNNED (1.35x DMG)")));
        } else {
            bossEvent.setColor(BossEvent.BossBarColor.BLUE);
            int pct = Math.round(getShieldProgress() * 100.0F);
            String phase = getCorollaPhase() == 0 ? "Roots" : "Corolla";
            bossEvent.setName(Component.translatable("entity.velorise_teyvat_bosses.cryo_regisvine")
                    .append(Component.literal(String.format(Locale.ROOT, " [%s Cryo Shield: %d%%]", phase, pct))));
        }
    }

    private LivingEntity findActiveTarget(ServerLevel sl, double range) {
        double maxDistSq = range * range;
        LivingEntity current = getTarget();
        if (current != null && current.isAlive() && current != this && !(current instanceof CryoRegisvineEntity)
                && !(current instanceof Player p && (p.isCreative() || p.isSpectator()))
                && current.distanceToSqr(this) <= maxDistSq) {
            return current;
        }
        LivingEntity lastAttacker = getLastHurtByMob();
        if (lastAttacker != null && lastAttacker.isAlive() && lastAttacker != this && !(lastAttacker instanceof CryoRegisvineEntity)
                && !(lastAttacker instanceof Player p && (p.isCreative() || p.isSpectator()))
                && lastAttacker.distanceToSqr(this) <= maxDistSq) {
            setTarget(lastAttacker);
            return lastAttacker;
        }
        Player best = null;
        double bestDistSq = maxDistSq;
        for (Player p : sl.players()) {
            if (!p.isAlive() || p.isSpectator() || p.isCreative()) continue;
            double dSq = p.distanceToSqr(this);
            if (dSq < bestDistSq) {
                bestDistSq = dSq;
                best = p;
            }
        }
        if (best != null) setTarget(best);
        return best;
    }

    private void faceTarget(Entity target, float maxStepDeg) {
        double dx = target.getX() - getX();
        double dz = target.getZ() - getZ();
        float targetYaw = (float) (Mth.atan2(dz, dx) * Mth.RAD_TO_DEG) - 90.0F;
        float nextYaw = Mth.approachDegrees(getYRot(), targetYaw, maxStepDeg);
        setYRot(nextYaw);
        yBodyRot = nextYaw;
        yHeadRot = nextYaw;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putDouble("AnchorX", anchorPos.x);
        tag.putDouble("AnchorY", anchorPos.y);
        tag.putDouble("AnchorZ", anchorPos.z);
        tag.putBoolean("AnchorInit", anchorInitialized);
        tag.putInt("CorollaPhase", getCorollaPhase());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.getBoolean("AnchorInit")) {
            anchorPos = new Vec3(tag.getDouble("AnchorX"), tag.getDouble("AnchorY"), tag.getDouble("AnchorZ"));
            anchorInitialized = true;
        }
        entityData.set(DATA_COROLLA_PHASE, tag.getInt("CorollaPhase"));
    }
}

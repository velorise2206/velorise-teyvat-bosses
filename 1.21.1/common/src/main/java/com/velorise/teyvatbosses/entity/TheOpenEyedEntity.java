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
import net.minecraft.tags.DamageTypeTags;
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
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * The Open-Eyed (Watcher: Fallen Vigil / Nihilita) — Normal Boss (Velorise: Teyvat Bosses).
 *
 * <p>Implements official Genshin Impact mechanics with deterministic epoch-synced animation state machine:
 * <ul>
 *   <li><b>10% Base All RES</b> (0.90x incoming damage multiplier).</li>
 *   <li><b>Standard Spear Moveset</b> (1:1 tick-synced to {@code TheOpenEyedAnimations}):
 *       Descend (20% ATK Geo), Spear Slam (60%), Spear Thrust with backward retreat (60%),
 *       Double Lunge (41%+47%), Double Slash (32%+34%), Forced Teleport (wraps distant target in cubes & pulls them in),
 *       Leaping Slam with smooth parabolic aerial arc & world-locked telegraph (60% AoE),
 *       and celestial hover-glide & orbital strafing movement.</li>
 *   <li><b>Supreme Overseer (2 Recordings per cycle):</b> Spins spear (40% ATK Geo), monitors Pyro/Hydro/Electro/Cryo
 *       hits for 5 seconds, then transitions via epoch-synced {@code ANIM_OVERSEER_SWEEP} for a 68% ATK elemental cleave.</li>
 *   <li><b>Phenomena's Delirium & Judgment Finisher:</b> After 2 Supreme Overseer instances, teleports to arena center,
 *       summons 2 {@link ElementalStarEntity} orbs (1/6 Max HP each), and charges for 20 seconds (400 ticks).
 *       Destroying both Stars interrupts the ritual and stuns for 6 seconds; otherwise transitions via epoch-synced
 *       {@code ANIM_DELIRIUM_FINISHER} to call down massive Elemental Sky Spears dealing 100% ATK each.</li>
 * </ul>
 */
public class TheOpenEyedEntity extends Monster {

    public static final int STATE_DESCEND  = 0;
    public static final int STATE_COMBAT   = 1;
    public static final int STATE_OVERSEER = 2;
    public static final int STATE_DELIRIUM = 3;
    public static final int STATE_STUNNED  = 4;
    public static final int STATE_DEATH    = 5;
    public static final int STATE_DORMANT  = 6;

    public static final int ANIM_IDLE               = 0;
    public static final int ANIM_DESCEND            = 1;
    public static final int ANIM_SPEAR_SLAM         = 2;
    public static final int ANIM_SPEAR_THRUST       = 3;
    public static final int ANIM_DOUBLE_LUNGE       = 4;
    public static final int ANIM_DOUBLE_SLASH       = 5;
    public static final int ANIM_FORCED_TELEPORT    = 6;
    public static final int ANIM_LEAPING_SLAM       = 7;
    public static final int ANIM_SUPREME_OVERSEER   = 8;
    public static final int ANIM_PHENOMENA_DELIRIUM = 9;
    public static final int ANIM_STUNNED            = 10;
    public static final int ANIM_DEATH              = 11;
    public static final int ANIM_MOVE_GLIDE         = 12;
    public static final int ANIM_OVERSEER_SWEEP     = 13;
    public static final int ANIM_DELIRIUM_FINISHER  = 14;

    public static final String[] ANIM_NAMES = {
            "animation.the_open_eyed.idle",
            "animation.the_open_eyed.descend",
            "animation.the_open_eyed.spear_slam",
            "animation.the_open_eyed.spear_thrust",
            "animation.the_open_eyed.double_lunge",
            "animation.the_open_eyed.double_slash",
            "animation.the_open_eyed.forced_teleport",
            "animation.the_open_eyed.leaping_slam",
            "animation.the_open_eyed.supreme_overseer",
            "animation.the_open_eyed.phenomena_delirium",
            "animation.the_open_eyed.stunned",
            "animation.the_open_eyed.death",
            "animation.the_open_eyed.move_glide",
            "animation.the_open_eyed.overseer_sweep",
            "animation.the_open_eyed.delirium_finisher"
    };

    public static final int[] ANIM_DURATIONS_TICKS = {
            80, 44, 42, 46, 52, 50, 42, 54, 44, 60, 50, 60, 40, 28, 52
    };

    private static final float BASE_ATK = 34.0F;
    private static final int OVERSEER_MONITOR_TICKS = 100; // 5.0 seconds
    private static final int DELIRIUM_CHARGE_TICKS  = 400; // 20.0 seconds
    private static final int STUN_DURATION_TICKS    = 120; // 6.0 seconds
    private static final int DEATH_DURATION_TICKS   = 60;  // 3.0 seconds

    private static final EntityDataAccessor<Integer> DATA_STATE =
            SynchedEntityData.defineId(TheOpenEyedEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_ANIM_ID =
            SynchedEntityData.defineId(TheOpenEyedEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_ANIM_EPOCH =
            SynchedEntityData.defineId(TheOpenEyedEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_ANIM_HOLD_AT =
            SynchedEntityData.defineId(TheOpenEyedEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DATA_FURY_PROGRESS =
            SynchedEntityData.defineId(TheOpenEyedEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> DATA_OVERSEER_ELEMENT =
            SynchedEntityData.defineId(TheOpenEyedEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DATA_TELEGRAPH_RADIUS =
            SynchedEntityData.defineId(TheOpenEyedEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_TELEGRAPH_X =
            SynchedEntityData.defineId(TheOpenEyedEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_TELEGRAPH_Y =
            SynchedEntityData.defineId(TheOpenEyedEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_TELEGRAPH_Z =
            SynchedEntityData.defineId(TheOpenEyedEntity.class, EntityDataSerializers.FLOAT);

    private static volatile boolean ELEMENT_LISTENER_REGISTERED = false;

    private final ServerBossEvent bossEvent = new ServerBossEvent(
            Component.translatable("entity.velorise_teyvat_bosses.the_open_eyed"),
            BossEvent.BossBarColor.YELLOW,
            BossEvent.BossBarOverlay.NOTCHED_10
    );

    private Vec3 arenaCenter = Vec3.ZERO;
    private boolean arenaInit = false;
    private int stateTicks = 0;
    private int noTargetTicks = 0;
    private int serverAnimTick = 0;
    private int clientAnimTick = 0;
    private int lastSyncedAnimId = -1;
    private int lastSyncedEpoch = -1;
    private int attackCooldown = 26;
    private int attacksSinceOverseer = 0;
    private int lastAttackAnim = -1;
    private int strafeDir = 1;
    private boolean customDeathHandled = false;

    // Supreme Overseer monitoring state
    private int overseerCount = 0;
    private final int[] recordedElements = { ElementalStarEntity.ELEM_INERT_GEO, ElementalStarEntity.ELEM_INERT_GEO };
    private final int[] monitorHits = new int[5]; // index 1..4 = PYRO, HYDRO, ELECTRO, CRYO
    private boolean overseerSweepPending = false;
    private int overseerSweepElement = ElementalStarEntity.ELEM_INERT_GEO;

    // Phenomena's Delirium state
    private final List<UUID> activeStarUuids = new ArrayList<>();
    private int starsDestroyedThisCycle = 0;
    private boolean deliriumUnleashed = false;
    private Vec3 leapStartPos = Vec3.ZERO;
    private Vec3 leapTargetPos = Vec3.ZERO;

    public TheOpenEyedEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.xpReward = 900;
        this.setPersistenceRequired();
        this.addTag("VeloriseInnateChecked");
        this.setNoGravity(true);
        ensureElementListener();
    }

    private static void ensureElementListener() {
        if (!ELEMENT_LISTENER_REGISTERED) {
            ELEMENT_LISTENER_REGISTERED = true;
            StavesElementBridge.registerPreAuraListener((level, target, attacker, element, gaugeUnits) -> {
                if (target instanceof ElementalStarEntity star && !star.isInert()) {
                    if (ElementalStarEntity.elementName(star.getElementId()).equalsIgnoreCase(element)) {
                        return false;
                    }
                }
                if (target instanceof TheOpenEyedEntity watcher && watcher.getBossState() == STATE_OVERSEER) {
                    int elemId = parseMonitoredElement(element);
                    if (elemId != ElementalStarEntity.ELEM_INERT_GEO) {
                        watcher.recordElementalHit(level, elemId);
                    }
                }
                return true;
            });
        }
    }

    private static int parseMonitoredElement(String element) {
        if (element == null) return ElementalStarEntity.ELEM_INERT_GEO;
        return switch (element.toUpperCase(Locale.ROOT)) {
            case "PYRO" -> ElementalStarEntity.ELEM_PYRO;
            case "HYDRO" -> ElementalStarEntity.ELEM_HYDRO;
            case "ELECTRO" -> ElementalStarEntity.ELEM_ELECTRO;
            case "CRYO" -> ElementalStarEntity.ELEM_CRYO;
            default -> ElementalStarEntity.ELEM_INERT_GEO;
        };
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 650.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.30D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.ARMOR, 6.0D)
                .add(Attributes.ARMOR_TOUGHNESS, 4.0D)
                .add(Attributes.FOLLOW_RANGE, 36.0D)
                .add(Attributes.ATTACK_DAMAGE, BASE_ATK);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_STATE, STATE_DORMANT);
        builder.define(DATA_ANIM_ID, ANIM_IDLE);
        builder.define(DATA_ANIM_EPOCH, 0);
        builder.define(DATA_ANIM_HOLD_AT, -1);
        builder.define(DATA_FURY_PROGRESS, 0.0F);
        builder.define(DATA_OVERSEER_ELEMENT, ElementalStarEntity.ELEM_INERT_GEO);
        builder.define(DATA_TELEGRAPH_RADIUS, 0.0F);
        builder.define(DATA_TELEGRAPH_X, 0.0F);
        builder.define(DATA_TELEGRAPH_Y, 0.0F);
        builder.define(DATA_TELEGRAPH_Z, 0.0F);
    }

    public int getBossState() { return entityData.get(DATA_STATE); }
    public int getAnimId() { return entityData.get(DATA_ANIM_ID); }
    public int getAnimTick() { return level().isClientSide() ? clientAnimTick : serverAnimTick; }
    public int getAnimEpoch() { return entityData.get(DATA_ANIM_EPOCH); }
    public int getAnimHoldAt() { return entityData.get(DATA_ANIM_HOLD_AT); }
    public float getFuryProgress() { return entityData.get(DATA_FURY_PROGRESS); }
    public int getOverseerElement() { return entityData.get(DATA_OVERSEER_ELEMENT); }
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

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (level().isClientSide() && DATA_ANIM_EPOCH.equals(key)) {
            lastSyncedAnimId = getAnimId();
            lastSyncedEpoch = getAnimEpoch();
            clientAnimTick = 0;
        }
    }

    public String getCurrentAnimName() {
        int id = getAnimId();
        return (id >= 0 && id < ANIM_NAMES.length) ? ANIM_NAMES[id] : ANIM_NAMES[0];
    }

    private void transitionTo(int newState, int animId) {
        entityData.set(DATA_STATE, newState);
        entityData.set(DATA_ANIM_ID, animId);
        entityData.set(DATA_ANIM_HOLD_AT, -1);
        entityData.set(DATA_ANIM_EPOCH, entityData.get(DATA_ANIM_EPOCH) + 1);
        clearTelegraph();
        serverAnimTick = 0;
        stateTicks = 0;
    }

    private void playAnim(int animId) {
        entityData.set(DATA_ANIM_ID, animId);
        entityData.set(DATA_ANIM_HOLD_AT, -1);
        entityData.set(DATA_ANIM_EPOCH, entityData.get(DATA_ANIM_EPOCH) + 1);
        clearTelegraph();
        serverAnimTick = 0;
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
        if (st == STATE_DEATH || st == STATE_DESCEND || st == STATE_DORMANT) return false;

        if (st == STATE_OVERSEER && !StavesElementBridge.isAvailable() && level() instanceof ServerLevel sl) {
            if (source.is(DamageTypeTags.IS_FIRE)) recordElementalHit(sl, ElementalStarEntity.ELEM_PYRO);
            else if (source.is(DamageTypeTags.IS_FREEZING)) recordElementalHit(sl, ElementalStarEntity.ELEM_CRYO);
            else if (source.is(DamageTypeTags.IS_LIGHTNING)) recordElementalHit(sl, ElementalStarEntity.ELEM_ELECTRO);
            else if (source.is(DamageTypeTags.IS_DROWNING)) recordElementalHit(sl, ElementalStarEntity.ELEM_HYDRO);
        }

        if (!level().isClientSide() && source.getEntity() instanceof LivingEntity attacker
                && attacker != this && !(attacker instanceof ElementalStarEntity)
                && !(attacker instanceof Player p && (p.isCreative() || p.isSpectator()))) {
            setTarget(attacker);
        }

        if (st == STATE_OVERSEER || st == STATE_DELIRIUM) {
            amount *= 0.40F;
        } else if (st != STATE_STUNNED) {
            amount *= 0.90F;
        }
        if (source.getEntity() instanceof LivingEntity && !(source.getEntity() instanceof Player)) {
            amount *= 0.50F;
        }
        return super.hurt(source, amount);
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
                clearActiveStars(sl);
                sl.playSound(null, getX(), getY(), getZ(), SoundEvents.WITHER_DEATH, SoundSource.HOSTILE, 1.3F, 1.15F);
                if (!cause.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
                    TrounceBlossomEntity.spawn(sl, BossRespawnAnchorEntity.BOSS_THE_OPEN_EYED, arenaInit ? arenaCenter : position(), getYRot());
                }
            }
        }
        super.die(cause);
    }

    public void recordElementalHit(ServerLevel sl, int elemId) {
        if (getBossState() != STATE_OVERSEER || overseerSweepPending) return;
        if (elemId < 1 || elemId > 4) return;
        monitorHits[elemId]++;
        int required = (overseerCount == 1 && recordedElements[0] == elemId) ? 9 : 3;
        float progress = Mth.clamp((float) monitorHits[elemId] / required, 0.0F, 1.0F);
        if (progress > getFuryProgress()) {
            entityData.set(DATA_FURY_PROGRESS, progress);
        }
        if (monitorHits[elemId] >= required) {
            finishOverseerMonitoring(sl, elemId);
        }
    }

    private void finishOverseerMonitoring(ServerLevel sl, int recordedElem) {
        overseerSweepPending = true;
        overseerSweepElement = recordedElem;
        entityData.set(DATA_OVERSEER_ELEMENT, recordedElem);
        if (overseerCount < 2) {
            recordedElements[overseerCount] = recordedElem;
        }
        overseerCount++;
        entityData.set(DATA_FURY_PROGRESS, 1.0F);
        // Epoch-synced transition into dedicated ANIM_OVERSEER_SWEEP (eliminates client-server hold desync)
        playAnim(ANIM_OVERSEER_SWEEP);
        sl.playSound(null, getX(), getY(), getZ(), SoundEvents.BEACON_POWER_SELECT, SoundSource.HOSTILE, 1.35F, 1.2F);
    }

    public void onElementalStarDestroyed(ServerLevel sl, ElementalStarEntity star) {
        activeStarUuids.remove(star.getUUID());
        starsDestroyedThisCycle++;

        float pct = (starsDestroyedThisCycle == 1) ? 0.03F : 0.09F;
        float trueHit = getMaxHealth() * pct;
        String elem = ElementalStarEntity.elementName(star.getElementId());

        setHealth(Math.max(1.0F, getHealth() - trueHit));
        StavesElementBridge.dealElementalDamage(this, null, elem, trueHit, 1.0F, true);
        sl.playSound(null, getX(), getY(), getZ(), SoundEvents.TOTEM_USE, SoundSource.HOSTILE, 1.2F, 1.25F);
        sl.sendParticles(ElementalStarEntity.particleForElement(star.getElementId()), getX(), getY() + 2.5D, getZ(), 36, 1.2D, 1.5D, 1.2D, 0.12D);

        if (activeStarUuids.isEmpty() && getBossState() == STATE_DELIRIUM) {
            deliriumUnleashed = true;
            entityData.set(DATA_FURY_PROGRESS, 0.0F);
            transitionTo(STATE_STUNNED, ANIM_STUNNED);
            sl.playSound(null, getX(), getY(), getZ(), SoundEvents.WITHER_BREAK_BLOCK, SoundSource.HOSTILE, 1.5F, 0.7F);
        }
    }

    private void resetToDormant(ServerLevel sl) {
        noTargetTicks = 0;
        clearActiveStars(sl);
        clearTelegraph();
        setTarget(null);
        setHealth(getMaxHealth());
        overseerCount = 0;
        recordedElements[0] = ElementalStarEntity.ELEM_INERT_GEO;
        recordedElements[1] = ElementalStarEntity.ELEM_INERT_GEO;
        for (int i = 0; i < monitorHits.length; i++) monitorHits[i] = 0;
        overseerSweepPending = false;
        overseerSweepElement = ElementalStarEntity.ELEM_INERT_GEO;
        deliriumUnleashed = false;
        starsDestroyedThisCycle = 0;
        attacksSinceOverseer = 0;
        lastAttackAnim = -1;
        attackCooldown = 26;
        entityData.set(DATA_FURY_PROGRESS, 0.0F);
        entityData.set(DATA_OVERSEER_ELEMENT, ElementalStarEntity.ELEM_INERT_GEO);
        if (arenaInit) {
            setPos(arenaCenter.x, arenaCenter.y, arenaCenter.z);
        }
        transitionTo(STATE_DORMANT, ANIM_IDLE);
    }

    @Override
    public void tick() {
        super.tick();
        if (!arenaInit) {
            arenaCenter = position();
            arenaInit = true;
        }
        setDeltaMovement(Vec3.ZERO);

        if (level().isClientSide()) {
            int curId = getAnimId();
            int curEp = getAnimEpoch();
            int holdAt = getAnimHoldAt();
            if (curId != lastSyncedAnimId || curEp != lastSyncedEpoch) {
                lastSyncedAnimId = curId;
                lastSyncedEpoch = curEp;
                clientAnimTick = 0;
            } else if (holdAt >= 0) {
                if (clientAnimTick < holdAt) clientAnimTick++;
                else clientAnimTick = holdAt;
            } else {
                clientAnimTick++;
            }
            return;
        }

        ServerLevel sl = (ServerLevel) level();
        stateTicks++;
        int st = getBossState();
        updateBossBar(st);

        if (st == STATE_DEATH) {
            serverAnimTick++;
            return;
        }

        if (st == STATE_DORMANT) {
            if (arenaInit && distanceToSqr(arenaCenter) > 0.25D) {
                setPos(arenaCenter.x, arenaCenter.y, arenaCenter.z);
            }
            if (getHealth() < getMaxHealth()) {
                setHealth(getMaxHealth());
            }
            LivingEntity near = findActiveTarget(sl, 15.0D);
            if (near != null) {
                noTargetTicks = 0;
                faceTarget(near, 180.0F);
                transitionTo(STATE_DESCEND, ANIM_DESCEND);
                sl.playSound(null, getX(), getY() + 2.5D, getZ(), SoundEvents.BEACON_ACTIVATE, SoundSource.HOSTILE, 1.5F, 0.72F);
                sl.playSound(null, getX(), getY() + 2.5D, getZ(), SoundEvents.TRIDENT_THUNDER.value(), SoundSource.HOSTILE, 1.2F, 0.9F);
                sl.sendParticles(ParticleTypes.CRIMSON_SPORE, getX(), getY() + 3.0D, getZ(), 42, 1.8D, 1.6D, 1.8D, 0.08D);
                sl.sendParticles(ParticleTypes.END_ROD, getX(), getY() + 3.0D, getZ(), 24, 1.2D, 1.2D, 1.2D, 0.09D);
            }
            return;
        }

        LivingEntity target = findActiveTarget(sl, 36.0D);

        if (st == STATE_DESCEND) {
            serverAnimTick++;
            int t = getAnimTick();
            if (target != null) faceTarget(target, 15.0F);
            if (t == 9) {
                sl.playSound(null, getX(), getY(), getZ(), SoundEvents.TRIDENT_THUNDER.value(), SoundSource.HOSTILE, 1.1F, 1.2F);
                aoeDamage(sl, position(), 4.5D, BASE_ATK * 0.20F, "GEO");
            }
            if (t == 26) {
                sl.sendParticles(ParticleTypes.END_ROD, getX(), getY() + 0.5D, getZ(), 28, 2.0D, 0.4D, 2.0D, 0.08D);
                sl.playSound(null, getX(), getY(), getZ(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 0.9F, 0.85F);
            }
            if (stateTicks >= ANIM_DURATIONS_TICKS[ANIM_DESCEND]) {
                transitionTo(STATE_COMBAT, ANIM_IDLE);
                attackCooldown = 36;
            }
            return;
        }

        if (target == null) {
            noTargetTicks++;
            if (st == STATE_COMBAT && getAnimId() != ANIM_IDLE) {
                playAnim(ANIM_IDLE);
            }
            serverAnimTick = (serverAnimTick + 1) % ANIM_DURATIONS_TICKS[ANIM_IDLE];
            if (noTargetTicks >= 200) {
                resetToDormant(sl);
            }
            return;
        }
        noTargetTicks = 0;

        if (st == STATE_STUNNED) {
            serverAnimTick = (serverAnimTick + 1) % ANIM_DURATIONS_TICKS[ANIM_STUNNED];
            if (stateTicks >= STUN_DURATION_TICKS) {
                overseerCount = 0;
                recordedElements[0] = ElementalStarEntity.ELEM_INERT_GEO;
                recordedElements[1] = ElementalStarEntity.ELEM_INERT_GEO;
                attacksSinceOverseer = 0;
                transitionTo(STATE_COMBAT, ANIM_IDLE);
                attackCooldown = 44;
            }
            return;
        }

        if (st == STATE_OVERSEER) {
            tickSupremeOverseer(sl, target);
            return;
        }

        if (st == STATE_DELIRIUM) {
            tickPhenomenaDelirium(sl, target);
            return;
        }

        // STATE_COMBAT
        serverAnimTick++;
        int anim = getAnimId();
        int aTick = getAnimTick();

        if (anim == ANIM_IDLE || anim == ANIM_MOVE_GLIDE) {
            faceTarget(target, 10.0F);
            double dist = Math.hypot(target.getX() - getX(), target.getZ() - getZ());
            boolean shouldGlide = (anim == ANIM_MOVE_GLIDE)
                    ? (dist > 5.6D && dist < 26.0D)
                    : (dist > 7.2D && dist < 26.0D);
            boolean shouldStrafe = !shouldGlide && dist >= 3.2D && dist <= 7.2D && attackCooldown > 14;

            if (shouldGlide || shouldStrafe) {
                if (anim != ANIM_MOVE_GLIDE) {
                    strafeDir = random.nextBoolean() ? 1 : -1;
                    playAnim(ANIM_MOVE_GLIDE);
                }
                Vec3 toTarget = new Vec3(target.getX() - getX(), 0.0D, target.getZ() - getZ());
                if (toTarget.lengthSqr() > 1e-4D) {
                    Vec3 fwd = toTarget.normalize();
                    Vec3 side = new Vec3(-fwd.z * strafeDir, 0.0D, fwd.x * strafeDir);
                    Vec3 moveVec = shouldGlide
                            ? fwd.scale(0.18D).add(side.scale(0.06D))
                            : side.scale(0.11D);
                    moveInArena(moveVec.x, moveVec.z);
                }
            } else {
                if (anim != ANIM_IDLE) {
                    playAnim(ANIM_IDLE);
                }
            }

            if (attackCooldown > 0) {
                attackCooldown--;
            } else {
                pickNextCombatAction(sl, target, dist);
            }
        } else {
            executeCombatTick(sl, target, anim, aTick);
            if (aTick >= ANIM_DURATIONS_TICKS[anim]) {
                if (anim == ANIM_FORCED_TELEPORT) {
                    faceTarget(target, 180.0F);
                    playAnim(ANIM_SPEAR_SLAM);
                } else {
                    playAnim(ANIM_IDLE);
                    attackCooldown = 40 + random.nextInt(22);
                }
            }
        }
    }

    private void pickNextCombatAction(ServerLevel sl, LivingEntity target, double dist) {
        if (overseerCount >= 2) {
            startPhenomenaDelirium(sl);
            return;
        }
        if (attacksSinceOverseer >= 3) {
            startSupremeOverseer(sl, target);
            return;
        }

        attacksSinceOverseer++;
        int chosen;
        if (dist > 14.0D) {
            chosen = random.nextBoolean() ? ANIM_FORCED_TELEPORT : ANIM_LEAPING_SLAM;
        } else if (dist > 8.5D) {
            int[] farPool = { ANIM_SPEAR_THRUST, ANIM_LEAPING_SLAM, ANIM_DOUBLE_LUNGE };
            chosen = farPool[random.nextInt(farPool.length)];
        } else {
            int[] nearPool = { ANIM_SPEAR_SLAM, ANIM_DOUBLE_SLASH, ANIM_DOUBLE_LUNGE, ANIM_SPEAR_THRUST };
            chosen = nearPool[random.nextInt(nearPool.length)];
        }
        if (chosen == lastAttackAnim) {
            chosen = (chosen == ANIM_SPEAR_SLAM) ? ANIM_DOUBLE_SLASH : ANIM_SPEAR_SLAM;
        }
        lastAttackAnim = chosen;
        leapStartPos = position();
        leapTargetPos = clampToArena(new Vec3(target.getX(), arenaCenter.y, target.getZ()));
        faceTarget(target, 180.0F);
        playAnim(chosen);
    }

    private void startSupremeOverseer(ServerLevel sl, LivingEntity target) {
        attacksSinceOverseer = 0;
        overseerSweepPending = false;
        overseerSweepElement = ElementalStarEntity.ELEM_INERT_GEO;
        entityData.set(DATA_OVERSEER_ELEMENT, ElementalStarEntity.ELEM_INERT_GEO);
        for (int i = 0; i < monitorHits.length; i++) monitorHits[i] = 0;
        entityData.set(DATA_FURY_PROGRESS, 0.0F);
        faceTarget(target, 180.0F);
        transitionTo(STATE_OVERSEER, ANIM_SUPREME_OVERSEER);
        entityData.set(DATA_ANIM_HOLD_AT, 42);
        sl.playSound(null, getX(), getY(), getZ(), SoundEvents.BEACON_ACTIVATE, SoundSource.HOSTILE, 1.3F, 1.15F);
    }

    private void tickSupremeOverseer(ServerLevel sl, LivingEntity target) {
        faceTarget(target, 8.0F);
        if (!overseerSweepPending) {
            if (stateTicks == 8) {
                aoeDamage(sl, position(), 6.0D, BASE_ATK * 0.40F, "GEO");
                sl.playSound(null, getX(), getY(), getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.HOSTILE, 1.3F, 0.8F);
            }
            if (serverAnimTick < 42) serverAnimTick++;
            if (stateTicks % 6 == 0) {
                sl.sendParticles(ParticleTypes.ENCHANT, getX(), getY() + 3.0D, getZ(), 12, 1.2D, 1.2D, 1.2D, 0.05D);
            }
            if (stateTicks >= OVERSEER_MONITOR_TICKS) {
                finishOverseerMonitoring(sl, ElementalStarEntity.ELEM_INERT_GEO);
            }
        } else {
            serverAnimTick++;
            int t = serverAnimTick;
            // Dedicated ANIM_OVERSEER_SWEEP cleave snap at t == 9 (0.45s)
            if (t == 9) {
                String elem = ElementalStarEntity.elementName(overseerSweepElement);
                arcAttack(sl, 9.5D, 0.0D, BASE_ATK * 0.68F, elem, 0.28F);
                sl.playSound(null, getX(), getY(), getZ(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 1.2F, 0.9F);
                sl.sendParticles(ElementalStarEntity.particleForElement(overseerSweepElement), getX(), getY() + 2.0D, getZ(), 36, 3.0D, 0.6D, 3.0D, 0.1D);
            }
            if (t >= ANIM_DURATIONS_TICKS[ANIM_OVERSEER_SWEEP]) {
                entityData.set(DATA_FURY_PROGRESS, 0.0F);
                transitionTo(STATE_COMBAT, ANIM_IDLE);
                attackCooldown = 44;
            }
        }
    }

    private void startPhenomenaDelirium(ServerLevel sl) {
        clearActiveStars(sl);
        starsDestroyedThisCycle = 0;
        deliriumUnleashed = false;
        setPos(arenaCenter.x, arenaCenter.y, arenaCenter.z);
        entityData.set(DATA_FURY_PROGRESS, 0.0F);
        transitionTo(STATE_DELIRIUM, ANIM_PHENOMENA_DELIRIUM);
        entityData.set(DATA_ANIM_HOLD_AT, 56);

        float starHp = getMaxHealth() / 6.0F;
        float rad = getYRot() * Mth.DEG_TO_RAD;
        Vec3 right = new Vec3(Math.cos(rad), 0.0D, Math.sin(rad));
        Vec3 leftPos = arenaCenter.add(right.scale(-4.0D)).add(0.0D, 0.15D, 0.0D);
        Vec3 rightPos = arenaCenter.add(right.scale(4.0D)).add(0.0D, 0.15D, 0.0D);

        ElementalStarEntity s1 = ElementalStarEntity.spawnForBoss(sl, this, leftPos, recordedElements[0], starHp);
        ElementalStarEntity s2 = ElementalStarEntity.spawnForBoss(sl, this, rightPos, recordedElements[1], starHp);
        activeStarUuids.add(s1.getUUID());
        activeStarUuids.add(s2.getUUID());

        sl.playSound(null, getX(), getY(), getZ(), SoundEvents.WITHER_SPAWN, SoundSource.HOSTILE, 1.25F, 1.25F);
    }

    private void tickPhenomenaDelirium(ServerLevel sl, LivingEntity target) {
        faceTarget(target, 6.0F);
        if (!deliriumUnleashed) {
            float prog = Mth.clamp((float) stateTicks / DELIRIUM_CHARGE_TICKS, 0.0F, 1.0F);
            entityData.set(DATA_FURY_PROGRESS, prog);
            if (serverAnimTick < 56) serverAnimTick++;

            if (stateTicks >= DELIRIUM_CHARGE_TICKS) {
                deliriumUnleashed = true;
                // Epoch-synced transition into dedicated ANIM_DELIRIUM_FINISHER (zero desync!)
                playAnim(ANIM_DELIRIUM_FINISHER);
                sl.playSound(null, getX(), getY() + 3.0D, getZ(), SoundEvents.TRIDENT_THUNDER.value(), SoundSource.HOSTILE, 1.5F, 0.72F);
            }
        } else {
            serverAnimTick++;
            int t = serverAnimTick;
            // Sequential Sky-Spear Judgment Decree synced to dual-arm decree beats: t == 15 (0.75s) and t == 21 (1.05s)
            if (t == 15 && !activeStarUuids.isEmpty()) {
                launchNextSkySpear(sl, target);
                sl.playSound(null, getX(), getY() + 2.5D, getZ(), SoundEvents.TRIDENT_THUNDER.value(), SoundSource.HOSTILE, 1.35F, 0.95F);
            } else if (t == 21 && !activeStarUuids.isEmpty()) {
                while (!activeStarUuids.isEmpty()) {
                    launchNextSkySpear(sl, target);
                }
                sl.playSound(null, getX(), getY() + 2.5D, getZ(), SoundEvents.TRIDENT_THUNDER.value(), SoundSource.HOSTILE, 1.5F, 0.78F);
            }
            if (t >= ANIM_DURATIONS_TICKS[ANIM_DELIRIUM_FINISHER]) {
                overseerCount = 0;
                recordedElements[0] = ElementalStarEntity.ELEM_INERT_GEO;
                recordedElements[1] = ElementalStarEntity.ELEM_INERT_GEO;
                attacksSinceOverseer = 0;
                entityData.set(DATA_FURY_PROGRESS, 0.0F);
                transitionTo(STATE_COMBAT, ANIM_IDLE);
                attackCooldown = 50;
            }
        }
    }

    private void launchNextSkySpear(ServerLevel sl, LivingEntity target) {
        if (activeStarUuids.isEmpty()) return;
        UUID uid = activeStarUuids.remove(0);
        if (sl.getEntity(uid) instanceof ElementalStarEntity star && star.isAlive()) {
            Vec3 strikePos = target.position().add((random.nextDouble() - 0.5D) * 2.2D, 0.0D, (random.nextDouble() - 0.5D) * 2.2D);
            SkySpearEntity.spawn(sl, this, strikePos.x, arenaCenter.y, strikePos.z, star.getElementId(), BASE_ATK * 1.00F, 5.6F);
            star.discard();
        }
    }

    private void clearActiveStars(ServerLevel sl) {
        for (UUID uid : activeStarUuids) {
            Entity e = sl.getEntity(uid);
            if (e != null) e.discard();
        }
        activeStarUuids.clear();
    }

    /** Exact hit ticks synced 1:1 with overhauled TheOpenEyedAnimations. */
    private void executeCombatTick(ServerLevel sl, LivingEntity target, int anim, int t) {
        switch (anim) {
            case ANIM_SPEAR_SLAM -> {
                if (t < 17) faceTarget(target, 10.0F);
                if (t == 10) setTelegraph(new Vec3(forwardPoint(4.0D).x, arenaCenter.y, forwardPoint(4.0D).z), 4.0F);
                if (t == 12) {
                    sl.playSound(null, getX(), getY() + 2.0D, getZ(), SoundEvents.TRIDENT_RIPTIDE_1.value(), SoundSource.HOSTILE, 1.0F, 0.72F);
                }
                if (t == 14) spawnWarningRing(sl, forwardPoint(4.0D), 3.6D);
                if (t == 21) {
                    Vec3 hitPos = forwardPoint(4.0D);
                    clearTelegraph();
                    aoeDamage(sl, hitPos, 4.0D, BASE_ATK * 0.60F, "GEO", 0.22F, 0.0F);
                    sl.sendParticles(ParticleTypes.EXPLOSION, hitPos.x, arenaCenter.y + 0.4D, hitPos.z, 3, 0.6D, 0.2D, 0.6D, 0.01D);
                    sl.sendParticles(ParticleTypes.CRIT, hitPos.x, arenaCenter.y + 0.5D, hitPos.z, 22, 1.8D, 0.4D, 1.8D, 0.14D);
                    sl.playSound(null, hitPos.x, arenaCenter.y, hitPos.z, SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 1.15F, 0.78F);
                }
            }
            case ANIM_SPEAR_THRUST -> {
                if (t < 18) faceTarget(target, 10.0F);
                // Official Genshin Spear Thrust: retreats backwards first before launching forward in a straight line
                if (t >= 2 && t <= 12) dashForwardInArena(-0.24D);
                if (t == 12) {
                    sl.playSound(null, getX(), getY() + 2.0D, getZ(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.HOSTILE, 1.25F, 0.80F);
                }
                if (t >= 20 && t <= 26) dashForwardInArena(0.84D);
                if (t == 23) {
                    lineThrustDamage(sl, 11.0D, BASE_ATK * 0.60F, "GEO");
                    sl.playSound(null, getX(), getY(), getZ(), SoundEvents.TRIDENT_THROW.value(), SoundSource.HOSTILE, 1.35F, 0.85F);
                }
            }
            case ANIM_DOUBLE_LUNGE -> {
                if (t < 12 || (t > 21 && t < 29)) faceTarget(target, 12.0F);
                if (t >= 13 && t <= 18) dashForwardInArena(0.75D);
                if (t == 16) {
                    lineThrustDamage(sl, 7.8D, BASE_ATK * 0.41F, "GEO");
                    sl.playSound(null, getX(), getY(), getZ(), SoundEvents.TRIDENT_THROW.value(), SoundSource.HOSTILE, 1.15F, 1.1F);
                }
                if (t >= 31 && t <= 37) dashForwardInArena(0.92D);
                if (t == 34) {
                    lineThrustDamage(sl, 9.8D, BASE_ATK * 0.47F, "GEO");
                    sl.playSound(null, getX(), getY(), getZ(), SoundEvents.TRIDENT_THROW.value(), SoundSource.HOSTILE, 1.35F, 0.88F);
                }
            }
            case ANIM_DOUBLE_SLASH -> {
                if (t < 11 || (t > 19 && t < 26)) faceTarget(target, 12.0F);
                if (t == 14) {
                    Vec3 sweepPos = forwardPoint(3.0D);
                    arcAttack(sl, 6.2D, 0.15D, BASE_ATK * 0.32F, "GEO");
                    sl.sendParticles(ParticleTypes.SWEEP_ATTACK, sweepPos.x, getY() + 1.8D, sweepPos.z, 3, 1.2D, 0.2D, 1.2D, 0.0D);
                    sl.playSound(null, getX(), getY(), getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.HOSTILE, 1.25F, 1.0F);
                }
                if (t == 29) {
                    Vec3 sweepPos = forwardPoint(3.4D);
                    arcAttack(sl, 6.8D, 0.10D, BASE_ATK * 0.34F, "GEO");
                    sl.sendParticles(ParticleTypes.SWEEP_ATTACK, sweepPos.x, getY() + 2.0D, sweepPos.z, 4, 1.5D, 0.25D, 1.5D, 0.0D);
                    sl.playSound(null, getX(), getY(), getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.HOSTILE, 1.35F, 0.85F);
                }
            }
            case ANIM_FORCED_TELEPORT -> {
                faceTarget(target, 10.0F);
                if (t == 15) {
                    sl.sendParticles(ParticleTypes.PORTAL, target.getX(), target.getY() + 1.0D, target.getZ(), 28, 0.6D, 0.8D, 0.6D, 0.1D);
                }
                if (t == 25) {
                    Vec3 dest = forwardPoint(3.2D);
                    target.teleportTo(dest.x, arenaCenter.y + 0.2D, dest.z);
                    sl.sendParticles(ParticleTypes.REVERSE_PORTAL, dest.x, arenaCenter.y + 1.0D, dest.z, 36, 0.6D, 0.9D, 0.6D, 0.08D);
                    sl.playSound(null, dest.x, arenaCenter.y, dest.z, SoundEvents.ENDERMAN_TELEPORT, SoundSource.HOSTILE, 1.35F, 0.75F);
                }
            }
            case ANIM_LEAPING_SLAM -> {
                if (t < 19) {
                    faceTarget(target, 10.0F);
                    leapStartPos = position();
                    leapTargetPos = clampToArena(new Vec3(target.getX(), arenaCenter.y, target.getZ()));
                    setTelegraph(leapTargetPos, 5.2F);
                } else if (t <= 34) {
                    // Smooth parabolic aerial flight arc (+5.2 blocks zenith) — replaces instant teleport!
                    float f = (t - 19) / 15.0F;
                    float smoothF = f * f * (3.0F - 2.0F * f);
                    double nx = Mth.lerp(smoothF, leapStartPos.x, leapTargetPos.x);
                    double nz = Mth.lerp(smoothF, leapStartPos.z, leapTargetPos.z);
                    double ny = arenaCenter.y + Math.sin(f * Math.PI) * 5.2D;
                    setPos(nx, ny, nz);
                }
                if (t == 19) {
                    sl.playSound(null, getX(), getY(), getZ(), SoundEvents.TRIDENT_RIPTIDE_2.value(), SoundSource.HOSTILE, 1.25F, 0.82F);
                    sl.sendParticles(ParticleTypes.END_ROD, getX(), getY() + 0.5D, getZ(), 20, 1.2D, 0.3D, 1.2D, 0.08D);
                }
                if (t == 23 || t == 29) spawnWarningRing(sl, leapTargetPos, 4.8D);
                if (t == 35) {
                    clearTelegraph();
                    setPos(leapTargetPos.x, arenaCenter.y, leapTargetPos.z);
                    aoeDamage(sl, leapTargetPos, 5.2D, BASE_ATK * 0.60F, "GEO", 0.28F, 0.05F);
                    sl.sendParticles(ParticleTypes.EXPLOSION, leapTargetPos.x, leapTargetPos.y + 0.5D, leapTargetPos.z, 4, 0.8D, 0.2D, 0.8D, 0.01D);
                    sl.playSound(null, leapTargetPos.x, leapTargetPos.y, leapTargetPos.z, SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 1.35F, 0.7F);
                }
            }
            default -> {}
        }
    }

    private Vec3 clampToArena(Vec3 pos) {
        double dx = pos.x - arenaCenter.x;
        double dz = pos.z - arenaCenter.z;
        double distSq = dx * dx + dz * dz;
        if (distSq > 15.5D * 15.5D) {
            double scale = 15.5D / Math.sqrt(distSq);
            return new Vec3(arenaCenter.x + dx * scale, pos.y, arenaCenter.z + dz * scale);
        }
        return pos;
    }

    private void moveInArena(double dxStep, double dzStep) {
        Vec3 clamped = clampToArena(new Vec3(getX() + dxStep, arenaCenter.y, getZ() + dzStep));
        setPos(clamped.x, arenaCenter.y, clamped.z);
    }

    private void dashForwardInArena(double stepDist) {
        float rad = getYRot() * Mth.DEG_TO_RAD;
        moveInArena(-Math.sin(rad) * stepDist, Math.cos(rad) * stepDist);
    }

    private Vec3 forwardPoint(double dist) {
        float rad = getYRot() * Mth.DEG_TO_RAD;
        return position().add(-Math.sin(rad) * dist, 0.2D, Math.cos(rad) * dist);
    }

    private void spawnWarningRing(ServerLevel sl, Vec3 center, double radius) {
        for (int i = 0; i < 24; i++) {
            double ang = (Math.PI * 2.0D * i) / 24.0D;
            sl.sendParticles(ParticleTypes.END_ROD, center.x + Math.cos(ang) * radius, center.y + 0.2D, center.z + Math.sin(ang) * radius, 1, 0, 0, 0, 0.01D);
        }
    }

    private void aoeDamage(ServerLevel sl, Vec3 center, double radius, float dmg, String element) {
        aoeDamage(sl, center, radius, dmg, element, 0.15F, 0.0F);
    }

    private void aoeDamage(ServerLevel sl, Vec3 center, double radius, float dmg, String element, float pierceRatio, float maxHpBonus) {
        AABB box = new AABB(center.x - radius, center.y - 2.0D, center.z - radius, center.x + radius, center.y + 4.0D, center.z + radius);
        for (LivingEntity e : sl.getEntitiesOfClass(LivingEntity.class, box, le -> le.isAlive() && le != this && !(le instanceof ElementalStarEntity))) {
            if (e.position().distanceToSqr(center) <= radius * radius) {
                float totalDmg = dmg + (maxHpBonus > 0.0F ? e.getMaxHealth() * maxHpBonus : 0.0F);
                StavesElementBridge.dealElementalDamage(e, this, element, totalDmg, 1.0F, false, pierceRatio);
                Vec3 knock = e.position().subtract(center).multiply(1.0D, 0.0D, 1.0D);
                if (knock.lengthSqr() > 1e-4D) {
                    knock = knock.normalize().scale(0.85D);
                    e.push(knock.x, 0.42D, knock.z);
                }
            }
        }
    }

    private void lineThrustDamage(ServerLevel sl, double length, float dmg, String element) {
        float rad = getYRot() * Mth.DEG_TO_RAD;
        Vec3 dir = new Vec3(-Math.sin(rad), 0.0D, Math.cos(rad));
        Vec3 start = position().add(0.0D, 1.6D, 0.0D);
        for (double step = 1.5D; step <= length; step += 1.2D) {
            Vec3 pt = start.add(dir.scale(step));
            sl.sendParticles(ParticleTypes.END_ROD, pt.x, pt.y, pt.z, 2, 0.15D, 0.15D, 0.15D, 0.01D);
        }
        AABB box = getBoundingBox().inflate(length, 3.0D, length);
        for (LivingEntity e : sl.getEntitiesOfClass(LivingEntity.class, box, le -> le.isAlive() && le != this && !(le instanceof ElementalStarEntity))) {
            Vec3 mid = e.position().add(0.0D, e.getBbHeight() * 0.5D, 0.0D);
            double proj = mid.subtract(start).dot(dir);
            if (proj > 0.5D && proj <= length && mid.distanceToSqr(start.add(dir.scale(proj))) <= 2.5D) {
                StavesElementBridge.dealElementalDamage(e, this, element, dmg, 1.0F, false, 0.22F);
            }
        }
    }

    private void arcAttack(ServerLevel sl, double radius, double minDot, float dmg, String element) {
        arcAttack(sl, radius, minDot, dmg, element, 0.15F);
    }

    private void arcAttack(ServerLevel sl, double radius, double minDot, float dmg, String element, float pierceRatio) {
        float rad = getYRot() * Mth.DEG_TO_RAD;
        Vec3 forward = new Vec3(-Math.sin(rad), 0.0D, Math.cos(rad));
        AABB box = getBoundingBox().inflate(radius, 3.0D, radius);
        for (LivingEntity e : sl.getEntitiesOfClass(LivingEntity.class, box, le -> le.isAlive() && le != this && !(le instanceof ElementalStarEntity))) {
            Vec3 to = e.position().subtract(position());
            if (to.horizontalDistanceSqr() <= radius * radius && to.normalize().dot(forward) >= minDot) {
                StavesElementBridge.dealElementalDamage(e, this, element, dmg, 1.0F, false, pierceRatio);
            }
        }
    }

    private void updateBossBar(int st) {
        bossEvent.setVisible(st != STATE_DORMANT && st != STATE_DEATH);
        bossEvent.setProgress(Mth.clamp(getHealth() / getMaxHealth(), 0.0F, 1.0F));
        Component base = Component.translatable("entity.velorise_teyvat_bosses.the_open_eyed");
        if (st == STATE_OVERSEER) {
            bossEvent.setColor(BossEvent.BossBarColor.PURPLE);
            int pct = Math.round(getFuryProgress() * 100.0F);
            bossEvent.setName(base.copy().append(Component.literal(String.format(Locale.ROOT, " — Supreme Overseer [%d%%]", pct))));
        } else if (st == STATE_DELIRIUM) {
            bossEvent.setColor(BossEvent.BossBarColor.RED);
            int pct = Math.round(getFuryProgress() * 100.0F);
            bossEvent.setName(base.copy().append(Component.literal(String.format(Locale.ROOT, " — Phenomena's Delirium [%d%%]", pct))));
        } else if (st == STATE_STUNNED) {
            bossEvent.setColor(BossEvent.BossBarColor.WHITE);
            bossEvent.setName(base.copy().append(Component.literal(" — STUNNED")));
        } else {
            bossEvent.setColor(BossEvent.BossBarColor.YELLOW);
            bossEvent.setName(base);
        }
    }

    private LivingEntity findActiveTarget(ServerLevel sl, double range) {
        double maxDistSq = range * range;
        LivingEntity current = getTarget();
        if (current != null && current.isAlive() && current != this && !(current instanceof ElementalStarEntity)
                && !(current instanceof Player p && (p.isCreative() || p.isSpectator()))
                && current.distanceToSqr(this) <= maxDistSq) {
            return current;
        }
        LivingEntity lastAttacker = getLastHurtByMob();
        if (lastAttacker != null && lastAttacker.isAlive() && lastAttacker != this && !(lastAttacker instanceof ElementalStarEntity)
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
        tag.putDouble("ArenaX", arenaCenter.x);
        tag.putDouble("ArenaY", arenaCenter.y);
        tag.putDouble("ArenaZ", arenaCenter.z);
        tag.putBoolean("ArenaInit", arenaInit);
        tag.putInt("OverseerCount", overseerCount);
        tag.putInt("Rec0", recordedElements[0]);
        tag.putInt("Rec1", recordedElements[1]);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.getBoolean("ArenaInit")) {
            arenaCenter = new Vec3(tag.getDouble("ArenaX"), tag.getDouble("ArenaY"), tag.getDouble("ArenaZ"));
            arenaInit = true;
        }
        overseerCount = tag.getInt("OverseerCount");
        recordedElements[0] = tag.getInt("Rec0");
        recordedElements[1] = tag.getInt("Rec1");
    }
}

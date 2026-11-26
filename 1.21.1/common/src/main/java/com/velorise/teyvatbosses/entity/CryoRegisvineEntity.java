package com.velorise.teyvatbosses.entity;

import com.velorise.teyvatbosses.compat.StavesElementBridge;
import com.velorise.teyvatbosses.registry.TeyvatBossesItems;
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

import java.util.Locale;

/**
 * Overhauled Cryo Regisvine World Boss (Genshin Impact-faithful edition).
 *
 * <p>Core mechanics synced 1:1 with Genshin Impact gameplay:
 * <ul>
 *   <li>Dormant curled bud -> Dramatic petal-bloom Awakening shockwave at 13 blocks.</li>
 *   <li>Two-phase Cryo Weakpoint Core loop: <b>Roots Core (12U)</b> <-> <b>Corolla Core (6U)</b>
 *       with exact phase-shift alerts, 80% resistance while core is intact, 2.2x Corolla vulnerability
 *       window during head slams, and 13s collapsed Stun state (1.55x damage).</li>
 *   <li>Full moveset with 3D entities & frame-synced hitboxes: 2-pass Twin Pod Cross-Lasers hitting the floor,
 *       8-shard outward-curving Homing Cryo Darts, Single & Triple player-tracking Head Slams erupting 3D ice spikes,
 *       continuous 360° Whip Spin sector hitbox, Frost Field + 10 chasing 3D Ice Pillars, and 360° Rotary Corolla Beam.</li>
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
            60, 44, 80, 60, 52, 58, 84, 68, 96, 68, 80, 96, 80, 52
    };

    public static final float ROOTS_SHIELD_UNITS = 12.0F;
    public static final float COROLLA_SHIELD_UNITS = 6.0F;
    private static final int STUN_DURATION_TICKS = 260; // 13.0 seconds

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
    private static final EntityDataAccessor<Float> DATA_ROOT_YAW =
            SynchedEntityData.defineId(CryoRegisvineEntity.class, EntityDataSerializers.FLOAT);

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
    private int attackCooldown = 26;
    private int noPlayerTicks = 0;
    private int lastAttackAnim = -1;
    private int closeCampTicks = 0;
    private float attackStartYaw = 0.0F;
    private float tripleSlamYaw = 0.0F;
    private boolean customDeathHandled = false;
    private float fallbackShield = 0.0F;

    public CryoRegisvineEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.xpReward = 600;
        this.setPersistenceRequired();
        this.addTag("VeloriseInnateChecked");
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
                .add(Attributes.MAX_HEALTH, 520.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.ARMOR, 4.0D)
                .add(Attributes.ARMOR_TOUGHNESS, 2.0D)
                .add(Attributes.FOLLOW_RANGE, 36.0D)
                .add(Attributes.ATTACK_DAMAGE, 22.0D);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_STATE, STATE_DORMANT);
        builder.define(DATA_ANIM_ID, ANIM_DORMANT);
        builder.define(DATA_ANIM_EPOCH, 0);
        builder.define(DATA_SHIELD_PROGRESS, 0.0F);
        builder.define(DATA_COROLLA_PHASE, 0);
        builder.define(DATA_ROOT_YAW, 0.0F);
    }

    public int getBossState() { return entityData.get(DATA_STATE); }
    public int getAnimId() { return entityData.get(DATA_ANIM_ID); }
    public int getAnimTick() { return level().isClientSide() ? clientAnimTick : serverAnimTick; }
    public int getAnimEpoch() { return entityData.get(DATA_ANIM_EPOCH); }
    public float getShieldProgress() { return entityData.get(DATA_SHIELD_PROGRESS); }
    public int getCorollaPhase() { return entityData.get(DATA_COROLLA_PHASE); }
    public float getRootYaw() { return entityData.get(DATA_ROOT_YAW); }

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
            fallbackShield = shieldUnits * 6.5F;
        }
        entityData.set(DATA_SHIELD_PROGRESS, 1.0F);
    }

    private void notifyNearbyPlayers(ServerLevel sl, String translationKey) {
        Component msg = Component.translatable(translationKey).withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD);
        for (ServerPlayer sp : sl.players()) {
            if (sp.distanceToSqr(this) <= 38.0D * 38.0D) {
                sp.displayClientMessage(msg, true);
            }
        }
    }

    public void onCorollaShieldBroken(ServerLevel level) {
        transitionTo(STATE_STUNNED, ANIM_STUNNED);
        fallbackShield = 0.0F;
        entityData.set(DATA_SHIELD_PROGRESS, 0.0F);
        level.playSound(null, getX(), getY(), getZ(), SoundEvents.WITHER_BREAK_BLOCK, SoundSource.HOSTILE, 1.5F, 0.65F);
        level.playSound(null, getX(), getY(), getZ(), SoundEvents.GLASS_BREAK, SoundSource.HOSTILE, 1.75F, 0.72F);
        level.playSound(null, getX(), getY(), getZ(), SoundEvents.AMETHYST_CLUSTER_BREAK, SoundSource.HOSTILE, 1.5F, 0.65F);
        level.sendParticles(ParticleTypes.SNOWFLAKE, getX(), getY() + 2.4D, getZ(), 72, 2.6D, 2.0D, 2.6D, 0.15D);
        level.sendParticles(ParticleTypes.ITEM_SNOWBALL, getX(), getY() + 1.6D, getZ(), 36, 2.2D, 1.2D, 2.2D, 0.18D);
        notifyNearbyPlayers(level, "message.velorise_teyvat_bosses.cryo_regisvine.core_broken");
    }

    private boolean isCorollaVulnerableWindow() {
        if (getCorollaPhase() != 1) return false;
        int anim = getAnimId();
        int t = serverAnimTick;
        if (anim == ANIM_FACEPLANT_SINGLE && t >= 20 && t <= 40) return true;
        if (anim == ANIM_FACEPLANT_TRIPLE && ((t >= 16 && t <= 23) || (t >= 36 && t <= 43) || (t >= 56 && t <= 70))) return true;
        return anim == ANIM_HALF_FLAIL_COMBO && t >= 58 && t <= 70;
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
            if (source.getEntity() instanceof LivingEntity attacker && attacker != this
                    && !(attacker instanceof Player p && (p.isCreative() || p.isSpectator()))) {
                setLastHurtByMob(attacker);
                setTarget(attacker);
            }
            awaken((ServerLevel) level());
            return false;
        }
        if (st == STATE_STUNNED) {
            amount *= 1.55F;
        } else if (StavesElementBridge.isAvailable()) {
            if (StavesElementBridge.hasElementalShield(this)) {
                amount *= 0.20F;
            }
        } else if (fallbackShield > 0.0F) {
            float shieldMult = source.is(net.minecraft.tags.DamageTypeTags.IS_FIRE) ? 2.8F : 0.65F;
            if (isCorollaVulnerableWindow()) {
                shieldMult *= 2.2F;
            }
            fallbackShield = Math.max(0.0F, fallbackShield - amount * shieldMult);
            float maxShield = ((getCorollaPhase() == 0) ? ROOTS_SHIELD_UNITS : COROLLA_SHIELD_UNITS) * 6.5F;
            entityData.set(DATA_SHIELD_PROGRESS, fallbackShield / maxShield);
            if (fallbackShield <= 0.0F && level() instanceof ServerLevel sl) {
                onCorollaShieldBroken(sl);
            }
            amount *= 0.20F;
        }
        if (source.getEntity() instanceof LivingEntity && !(source.getEntity() instanceof Player)) {
            amount *= 0.50F;
        }
        return super.hurt(source, amount);
    }

    @Override
    public void die(DamageSource cause) {
        if (!level().isClientSide() && !customDeathHandled) {
            customDeathHandled = true;
            StavesElementBridge.clearElementalShield(this);
            transitionTo(STATE_DEATH, ANIM_DEATH);
            if (level() instanceof ServerLevel sl) {
                sl.playSound(null, getX(), getY(), getZ(), SoundEvents.GLASS_BREAK, SoundSource.HOSTILE, 1.7F, 0.52F);
                sl.sendParticles(ParticleTypes.SNOWFLAKE, getX(), getY() + 2.5D, getZ(), 65, 2.5D, 2.0D, 2.5D, 0.12D);
                if (!cause.is(net.minecraft.tags.DamageTypeTags.BYPASSES_INVULNERABILITY)) {
                    TrounceBlossomEntity.spawn(sl, BossRespawnAnchorEntity.BOSS_CRYO_REGISVINE, anchorInitialized ? anchorPos : position(), getRootYaw());
                }
            }
        }
        super.die(cause);
    }

    private void awaken(ServerLevel sl) {
        entityData.set(DATA_COROLLA_PHASE, 0);
        transitionTo(STATE_AWAKEN, ANIM_AWAKEN);
        applyPhaseShield();
        sl.playSound(null, getX(), getY(), getZ(), SoundEvents.ELDER_GUARDIAN_CURSE, SoundSource.HOSTILE, 1.3F, 1.25F);
        sl.playSound(null, getX(), getY(), getZ(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.HOSTILE, 1.4F, 0.9F);
        sl.sendParticles(ParticleTypes.SNOWFLAKE, getX(), getY() + 1.4D, getZ(), 54, 2.2D, 1.6D, 2.2D, 0.12D);
    }

    @Override
    public void tick() {
        super.tick();
        if (!anchorInitialized) {
            anchorPos = position();
            if (!level().isClientSide()) {
                entityData.set(DATA_ROOT_YAW, getYRot());
            }
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

        int st = getBossState();
        updateBossBar(st);

        if (st == STATE_DEATH) {
            if (stateTicks >= ANIM_DURATIONS_TICKS[ANIM_DEATH]) discard();
            return;
        }

        LivingEntity nearest = findActiveTarget(sl, 34.0D);

        if (st == STATE_DORMANT) {
            bossEvent.setVisible(false);
            if (tickCount % 8 == 0) {
                sl.sendParticles(ParticleTypes.SNOWFLAKE, getX(), getY() + 0.6D, getZ(), 5, 1.3D, 0.35D, 1.3D, 0.015D);
            }
            if (nearest != null && nearest.distanceToSqr(this) <= 169.0D) { // 13 blocks
                awaken(sl);
            }
            return;
        }

        bossEvent.setVisible(true);

        if (nearest == null) {
            noPlayerTicks++;
            if (noPlayerTicks >= 220) {
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
            faceTarget(nearest, 14.0F);
            if (stateTicks == 15) {
                sl.playSound(null, getX(), getY(), getZ(), SoundEvents.RAVAGER_ROAR, SoundSource.HOSTILE, 1.35F, 1.18F);
                spawnFrostShockwaveParticles(sl, 6.5D, 36);
            }
            if (stateTicks >= ANIM_DURATIONS_TICKS[ANIM_AWAKEN]) {
                transitionTo(STATE_COMBAT, ANIM_IDLE_COMBAT);
                attackCooldown = 36;
            }
            return;
        }

        if (st == STATE_STUNNED) {
            if (stateTicks % 12 == 0) {
                sl.sendParticles(ParticleTypes.SNOWFLAKE, getX(), getY() + 1.0D, getZ(), 7, 1.6D, 0.35D, 1.6D, 0.012D);
            }
            if (stateTicks >= STUN_DURATION_TICKS) {
                int nextPhase = (getCorollaPhase() + 1) % 2;
                entityData.set(DATA_COROLLA_PHASE, nextPhase);
                applyPhaseShield();
                faceTarget(nearest, 180.0F);
                attackStartYaw = getYRot();
                // Exact Genshin phase transition: Corolla phase opens with 360° Rotary Beam; Roots reform opens with Frost Ring!
                if (nextPhase == 1) {
                    notifyNearbyPlayers(sl, "message.velorise_teyvat_bosses.cryo_regisvine.corolla_converge");
                    transitionTo(STATE_COMBAT, ANIM_ROTARY_SPRAY);
                } else {
                    notifyNearbyPlayers(sl, "message.velorise_teyvat_bosses.cryo_regisvine.roots_reformed");
                    transitionTo(STATE_COMBAT, ANIM_SPIN_FROST_RING);
                }
                attackCooldown = 44;
                sl.playSound(null, getX(), getY(), getZ(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.HOSTILE, 1.5F, 0.82F);
            }
            return;
        }

        if (StavesElementBridge.isAvailable()) {
            entityData.set(DATA_SHIELD_PROGRESS, StavesElementBridge.getShieldProgress(this));
        }

        double distSq = nearest.distanceToSqr(this);
        if (distSq < 42.25D) { // within 6.5 blocks
            closeCampTicks++;
        } else {
            closeCampTicks = Math.max(0, closeCampTicks - 2);
        }

        int anim = getAnimId();
        int aTick = serverAnimTick;

        if (anim == ANIM_IDLE_COMBAT) {
            faceTarget(nearest, 9.0F);
            if (attackCooldown > 0) {
                attackCooldown--;
            } else {
                pickNextAttack(nearest);
            }
        } else {
            executeAttackTick(sl, nearest, anim, aTick);
            if (aTick >= ANIM_DURATIONS_TICKS[anim]) {
                playAnim(ANIM_IDLE_COMBAT);
                int phaseBonus = (getCorollaPhase() == 1) ? 6 : 0;
                attackCooldown = Math.max(52, 58 - phaseBonus) + random.nextInt(24);
            }
        }
    }

    private void pickNextAttack(LivingEntity target) {
        double dist = Math.sqrt(target.distanceToSqr(this));
        int chosen;

        // Reactive anti-camp punish if player hugs roots for > 3.5 seconds
        if (closeCampTicks >= 70 && lastAttackAnim != ANIM_FLAIL_360) {
            closeCampTicks = 0;
            chosen = random.nextBoolean() ? ANIM_FLAIL_360 : ANIM_SPIN_FROST_RING;
        } else {
            int[] pool;
            if (getCorollaPhase() == 1) {
                // Corolla Phase: favors Triple Slam, Frost Field Ice Pillars, Rotary Beam, 2-Pass Lasers
                pool = dist < 8.5D
                        ? new int[]{ANIM_FACEPLANT_TRIPLE, ANIM_FACEPLANT_SINGLE, ANIM_FLAIL_360, ANIM_HALF_FLAIL_COMBO, ANIM_BLIZZARD_CHANNEL}
                        : new int[]{ANIM_BLIZZARD_CHANNEL, ANIM_ICE_BEAMS_CROSS, ANIM_HOMING_ICICLES, ANIM_ROTARY_SPRAY, ANIM_FACEPLANT_TRIPLE};
            } else {
                // Roots Phase: balanced mix of Twin Lasers, Homing Darts, Slams, Whip Spin, Pillars
                pool = dist < 8.0D
                        ? new int[]{ANIM_FACEPLANT_SINGLE, ANIM_FLAIL_360, ANIM_HALF_FLAIL_COMBO, ANIM_FACEPLANT_TRIPLE, ANIM_SPIN_FROST_RING}
                        : new int[]{ANIM_ICE_BEAMS_CROSS, ANIM_HOMING_ICICLES, ANIM_BLIZZARD_CHANNEL, ANIM_ROTARY_SPRAY, ANIM_FACEPLANT_SINGLE};
            }
            chosen = pool[random.nextInt(pool.length)];
            if (chosen == lastAttackAnim && pool.length > 1) {
                chosen = pool[(random.nextInt(pool.length - 1) + 1) % pool.length];
            }
        }

        lastAttackAnim = chosen;
        faceTarget(target, 180.0F);
        attackStartYaw = getYRot();
        tripleSlamYaw = getYRot();
        playAnim(chosen);
    }

    /**
     * All impact ticks strictly synced 1:1 with animations and 3D CryoIceSpike / CryoIcicle / Beam VFX.
     */
    private void executeAttackTick(ServerLevel sl, LivingEntity target, int anim, int t) {
        switch (anim) {
            case ANIM_FACEPLANT_SINGLE -> {
                if (t < 14) faceTarget(target, 10.0F);
                if (t == 12 || t == 16) spawnSlamTelegraph(sl, getYRot(), 4.5D, 4.0D);
                if (t == 21) impactSlamAtYaw(sl, getYRot(), 4.5D, 4.2D, 32.0F, 6);
            }
            case ANIM_FACEPLANT_TRIPLE -> {
                if (t < 11) {
                    faceTarget(target, 12.0F);
                    tripleSlamYaw = getYRot();
                } else if (t >= 24 && t < 33) {
                    faceTarget(target, 10.0F);
                    tripleSlamYaw = getYRot();
                } else if (t >= 44 && t < 53) {
                    faceTarget(target, 10.0F);
                    tripleSlamYaw = getYRot();
                }
                if (t == 12) spawnSlamTelegraph(sl, tripleSlamYaw, 4.4D, 3.8D);
                if (t == 17) impactSlamAtYaw(sl, tripleSlamYaw, 4.4D, 3.9D, 27.0F, 4);
                if (t == 32) spawnSlamTelegraph(sl, tripleSlamYaw, 4.6D, 4.0D);
                if (t == 37) impactSlamAtYaw(sl, tripleSlamYaw, 4.6D, 4.1D, 29.0F, 5);
                if (t == 52) spawnSlamTelegraph(sl, tripleSlamYaw, 4.8D, 4.4D);
                if (t == 57) impactSlamAtYaw(sl, tripleSlamYaw, 4.8D, 4.5D, 34.0F, 7);
            }
            case ANIM_FLAIL_360 -> {
                if (t == 9) {
                    sl.playSound(null, getX(), getY(), getZ(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.HOSTILE, 1.2F, 0.78F);
                }
                if (t == 13 || t == 22 || t == 32) {
                    sl.playSound(null, getX(), getY(), getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.HOSTILE, 1.55F, 0.72F + (t - 13) * 0.008F);
                }
                // Genshin-accurate rapid 3-revolution 1080° whip spin (ticks 13..41)
                if (t >= 13 && t <= 41 && t % 2 == 0) {
                    float prog = (t - 13) / 28.0F;
                    float sweepYaw = attackStartYaw - prog * 1080.0F;
                    sweepSectorHitbox(sl, sweepYaw, 8.8D, 62.0F, 22.5F, 1.05D);
                }
            }
            case ANIM_HALF_FLAIL_COMBO -> {
                if (t < 11 || (t > 24 && t < 31) || (t > 44 && t < 53)) faceTarget(target, 11.0F);
                if (t == 19 || t == 37) arcSweepInFront(sl, 8.6D, 23.0F);
                if (t == 53) spawnSlamTelegraph(sl, getYRot(), 4.5D, 4.2D);
                if (t == 59) impactSlamAtYaw(sl, getYRot(), 4.5D, 4.4D, 34.0F, 6);
            }
            case ANIM_SPIN_FROST_RING -> {
                // Genshin-accurate rapid 3-revolution frost spin (ticks 12..39)
                if (t == 16) {
                    ringAoE(sl, 9.6D, 22.0F, 1.25D);
                    spawnSpikeRing(sl, 6.8D, 8, 13.0F, 0.95F);
                }
                if (t == 26) {
                    ringAoE(sl, 10.2D, 23.0F, 1.15D);
                }
                if (t == 36) {
                    ringAoE(sl, 10.8D, 26.0F, 1.35D);
                }
            }
            case ANIM_ICE_BEAMS_CROSS -> {
                if (t < 12) faceTarget(target, 11.0F);
                if (t == 10 || t == 34) {
                    sl.playSound(null, getX(), getY(), getZ(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.HOSTILE, 1.1F, 1.4F);
                }
                if (t == 16 || t == 38) {
                    sl.playSound(null, getX(), getY(), getZ(), SoundEvents.BEACON_ACTIVATE, SoundSource.HOSTILE, 1.25F, 1.55F);
                }
                // Pass 1: Cross sweep L -> R (ticks 16..32)
                if (t >= 16 && t <= 32 && t % 4 == 0) {
                    float progress = (t - 16) / 16.0F;
                    float angleA = getYRot() - 38.0F + progress * 76.0F;
                    float angleB = getYRot() + 38.0F - progress * 76.0F;
                    sweepBeamRay(sl, angleA, 17.5D, 16.0F);
                    sweepBeamRay(sl, angleB, 17.5D, 16.0F);
                }
                // Pass 2: Reverse cross sweep R -> L (ticks 38..54)
                if (t >= 38 && t <= 54 && t % 4 == 0) {
                    float progress = (t - 38) / 16.0F;
                    float angleA = getYRot() + 38.0F - progress * 76.0F;
                    float angleB = getYRot() - 38.0F + progress * 76.0F;
                    sweepBeamRay(sl, angleA, 17.5D, 17.0F);
                    sweepBeamRay(sl, angleB, 17.5D, 17.0F);
                }
            }
            case ANIM_ROTARY_SPRAY -> {
                if (t == 10) {
                    sl.playSound(null, getX(), getY(), getZ(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.HOSTILE, 1.3F, 1.15F);
                }
                if (t == 16) {
                    sl.playSound(null, getX(), getY(), getZ(), SoundEvents.BEACON_POWER_SELECT, SoundSource.HOSTILE, 1.4F, 1.25F);
                }
                if (t >= 16 && t <= 84 && t % 4 == 0) {
                    float a = Mth.clamp((t - 14.0F) / 70.0F, 0.0F, 1.0F);
                    float smooth = a * a * (3.0F - 2.0F * a);
                    float beamYaw = attackStartYaw - smooth * 360.0F;
                    sweepBeamRay(sl, beamYaw, 18.5D, 19.0F);
                }
            }
            case ANIM_HOMING_ICICLES -> {
                faceTarget(target, 11.0F);
                // 8 curving Cryo Crystal Darts alternating left/right stamen pods
                if (t >= 14 && t <= 42 && (t - 14) % 4 == 0) {
                    int idx = (t - 14) / 4;
                    double side = (idx % 2 == 0) ? 2.5D : -2.5D;
                    float rad = getYRot() * Mth.DEG_TO_RAD;
                    Vec3 right = new Vec3(Math.cos(rad), 0.0D, Math.sin(rad));
                    Vec3 forward = new Vec3(-Math.sin(rad), 0.0D, Math.cos(rad));
                    Vec3 origin = position().add(right.scale(side)).add(0.0D, 4.9D, 0.0D);
                    // Launch outward + slightly up so it curves gracefully into the player like Genshin
                    Vec3 initialVel = right.scale(side * 0.18D).add(forward.scale(0.32D)).add(0.0D, 0.22D, 0.0D);
                    CryoIcicleProjectile.spawn(sl, this, target, origin, initialVel, 12.5F);
                    sl.sendParticles(ParticleTypes.SNOWFLAKE, origin.x, origin.y, origin.z, 8, 0.25D, 0.25D, 0.25D, 0.04D);
                    sl.playSound(null, origin.x, origin.y, origin.z, SoundEvents.SNOW_GOLEM_SHOOT, SoundSource.HOSTILE, 0.95F, 1.38F);
                }
            }
            case ANIM_BLIZZARD_CHANNEL -> {
                faceTarget(target, 6.0F);
                if (t == 10) {
                    sl.playSound(null, getX(), getY(), getZ(), SoundEvents.ELDER_GUARDIAN_CURSE, SoundSource.HOSTILE, 0.9F, 1.55F);
                    spawnFrostShockwaveParticles(sl, 12.0D, 48);
                }
                if (t % 4 == 0 && t >= 12 && t <= 86) {
                    // Ambient arena frost field mist
                    double ang = random.nextDouble() * Math.PI * 2.0D;
                    double rad = 3.0D + random.nextDouble() * 11.0D;
                    sl.sendParticles(ParticleTypes.SNOWFLAKE, getX() + Math.cos(ang) * rad, getY() + 0.25D, getZ() + Math.sin(ang) * rad,
                            3, 0.6D, 0.1D, 0.6D, 0.02D);
                }
                // 10 chasing 3D Cryo Ice Pillars erupting beneath/ahead of target!
                if (t >= 14 && t <= 86 && (t - 14) % 8 == 0) {
                    Vec3 lead = target.getDeltaMovement().scale(6.0D);
                    double ox = (random.nextDouble() - 0.5D) * 1.4D;
                    double oz = (random.nextDouble() - 0.5D) * 1.4D;
                    double px = target.getX() + lead.x + ox;
                    double pz = target.getZ() + lead.z + oz;
                    double py = Math.min(target.getY(), getY() + 0.5D);
                    CryoIceSpikeEntity.spawnPillar(sl, this, px, py, pz, 16.5F, 1.05F + random.nextFloat() * 0.2F);
                }
            }
            default -> {}
        }
    }

    private void spawnFrostShockwaveParticles(ServerLevel sl, double radius, int count) {
        for (int i = 0; i < count; i++) {
            double ang = (Math.PI * 2.0D * i) / count;
            sl.sendParticles(ParticleTypes.SNOWFLAKE,
                    getX() + Math.cos(ang) * radius, getY() + 0.35D, getZ() + Math.sin(ang) * radius,
                    2, 0.2D, 0.15D, 0.2D, 0.04D);
        }
    }

    private void spawnSlamTelegraph(ServerLevel sl, float yawDeg, double forwardDist, double radius) {
        float rad = yawDeg * Mth.DEG_TO_RAD;
        Vec3 center = position().add(-Math.sin(rad) * forwardDist, 0.15D, Math.cos(rad) * forwardDist);
        for (int i = 0; i < 24; i++) {
            double ang = (Math.PI * 2.0D * i) / 24.0D;
            sl.sendParticles(ParticleTypes.SNOWFLAKE,
                    center.x + Math.cos(ang) * radius, center.y, center.z + Math.sin(ang) * radius,
                    1, 0.05D, 0.05D, 0.05D, 0.01D);
        }
        sl.playSound(null, center.x, center.y, center.z, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.HOSTILE, 0.8F, 1.35F);
    }

    private void impactSlamAtYaw(ServerLevel sl, float yawDeg, double forwardDist, double radius, float dmg, int spikeCount) {
        float rad = yawDeg * Mth.DEG_TO_RAD;
        Vec3 center = position().add(-Math.sin(rad) * forwardDist, 0.5D, Math.cos(rad) * forwardDist);
        sl.sendParticles(ParticleTypes.SNOWFLAKE, center.x, center.y + 0.4D, center.z, 48, radius * 0.55D, 0.6D, radius * 0.55D, 0.11D);
        sl.sendParticles(ParticleTypes.ITEM_SNOWBALL, center.x, center.y + 0.4D, center.z, 24, radius * 0.45D, 0.5D, radius * 0.45D, 0.14D);
        sl.sendParticles(ParticleTypes.EXPLOSION, center.x, center.y + 0.5D, center.z, 3, 0.6D, 0.2D, 0.6D, 0.01D);
        sl.playSound(null, center.x, center.y, center.z, SoundEvents.GENERIC_EXPLODE, SoundSource.HOSTILE, 1.2F, 0.72F);
        sl.playSound(null, center.x, center.y, center.z, SoundEvents.GLASS_BREAK, SoundSource.HOSTILE, 1.25F, 0.78F);

        // Spawn jagged 3D ice spikes erupting around the slam impact ring!
        for (int i = 0; i < spikeCount; i++) {
            double ang = (Math.PI * 2.0D * i) / spikeCount + (random.nextDouble() - 0.5D) * 0.25D;
            double dist = radius * (0.58D + random.nextDouble() * 0.35D);
            double sx = center.x + Math.cos(ang) * dist;
            double sz = center.z + Math.sin(ang) * dist;
            CryoIceSpikeEntity.spawnSlamSpike(sl, this, sx, getY(), sz, 0.0F, 0.72F + random.nextFloat() * 0.35F, (float) Math.toDegrees(ang));
        }

        AABB box = new AABB(center.x - radius, center.y - 1.5D, center.z - radius, center.x + radius, center.y + 3.2D, center.z + radius);
        for (LivingEntity e : sl.getEntitiesOfClass(LivingEntity.class, box, le -> le.isAlive() && le != this && !(le instanceof CryoRegisvineEntity))) {
            if (e.position().distanceToSqr(center) <= radius * radius) {
                StavesElementBridge.dealElementalDamage(e, this, "CRYO", dmg, 1.0F, false, 0.20F);
            }
        }
    }

    private void spawnSpikeRing(ServerLevel sl, double radius, int count, float dmg, float scale) {
        for (int i = 0; i < count; i++) {
            double ang = (Math.PI * 2.0D * i) / count;
            double sx = getX() + Math.cos(ang) * radius;
            double sz = getZ() + Math.sin(ang) * radius;
            CryoIceSpikeEntity.spawnSlamSpike(sl, this, sx, getY(), sz, dmg, scale, (float) Math.toDegrees(ang));
        }
    }

    private void sweepSectorHitbox(ServerLevel sl, float sweepYawDeg, double radius, float halfConeDeg, float dmg, double pushStrength) {
        float rad = sweepYawDeg * Mth.DEG_TO_RAD;
        Vec3 forward = new Vec3(-Math.sin(rad), 0.0D, Math.cos(rad));
        double cosThresh = Math.cos(halfConeDeg * Mth.DEG_TO_RAD);

        // Sweeping frost particles along the whipping head arc
        Vec3 tip = position().add(forward.scale(radius * 0.78D)).add(0.0D, 0.9D, 0.0D);
        sl.sendParticles(ParticleTypes.SNOWFLAKE, tip.x, tip.y, tip.z, 8, 0.55D, 0.35D, 0.55D, 0.05D);

        AABB box = getBoundingBox().inflate(radius, 2.5D, radius);
        for (LivingEntity e : sl.getEntitiesOfClass(LivingEntity.class, box, le -> le.isAlive() && le != this && !(le instanceof CryoRegisvineEntity))) {
            Vec3 to = e.position().subtract(position());
            double hDistSq = to.horizontalDistanceSqr();
            if (hDistSq <= radius * radius && hDistSq > 0.25D) {
                Vec3 hDir = new Vec3(to.x, 0.0D, to.z).normalize();
                if (hDir.dot(forward) >= cosThresh) {
                    StavesElementBridge.dealElementalDamage(e, this, "CRYO", dmg, 1.0F, false, 0.25F);
                    Vec3 push = hDir.scale(pushStrength);
                    e.push(push.x, 0.35D, push.z);
                }
            }
        }
    }

    private void ringAoE(ServerLevel sl, double radius, float dmg, double pushStrength) {
        for (int i = 0; i < 42; i++) {
            double ang = (Math.PI * 2.0D * i) / 42.0D;
            sl.sendParticles(ParticleTypes.SNOWFLAKE, getX() + Math.cos(ang) * radius * 0.8D, getY() + 0.8D, getZ() + Math.sin(ang) * radius * 0.8D, 2, 0.2D, 0.2D, 0.2D, 0.06D);
        }
        sl.playSound(null, getX(), getY(), getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.HOSTILE, 1.45F, 0.65F);
        AABB box = getBoundingBox().inflate(radius, 2.5D, radius);
        for (LivingEntity e : sl.getEntitiesOfClass(LivingEntity.class, box, le -> le.isAlive() && le != this && !(le instanceof CryoRegisvineEntity))) {
            if (e.distanceToSqr(this) <= radius * radius) {
                StavesElementBridge.dealElementalDamage(e, this, "CRYO", dmg, 1.0F, false, 0.25F);
                Vec3 push = e.position().subtract(position()).normalize().scale(pushStrength);
                e.push(push.x, 0.38D, push.z);
            }
        }
    }

    private void arcSweepInFront(ServerLevel sl, double radius, float dmg) {
        float rad = getYRot() * Mth.DEG_TO_RAD;
        Vec3 forward = new Vec3(-Math.sin(rad), 0.0D, Math.cos(rad));
        sl.playSound(null, getX(), getY(), getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.HOSTILE, 1.35F, 0.78F);
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
        Vec3 start = position().add(0.0D, 1.2D, 0.0D);
        // Ground-scorch frost crystal shatter trail along the beam path
        for (double step = 3.0D; step <= length; step += 2.2D) {
            Vec3 pt = start.add(dir.scale(step));
            sl.sendParticles(ParticleTypes.SNOWFLAKE, pt.x, getY() + 0.25D, pt.z, 3, 0.25D, 0.2D, 0.25D, 0.04D);
            if (step > 8.0D) {
                sl.sendParticles(ParticleTypes.ITEM_SNOWBALL, pt.x, getY() + 0.2D, pt.z, 1, 0.2D, 0.2D, 0.2D, 0.06D);
            }
        }
        AABB sweepBox = getBoundingBox().inflate(length, 3.0D, length);
        for (LivingEntity e : sl.getEntitiesOfClass(LivingEntity.class, sweepBox, le -> le.isAlive() && le != this && !(le instanceof CryoRegisvineEntity))) {
            Vec3 mid = e.position().add(0.0D, e.getBbHeight() * 0.5D, 0.0D);
            double proj = mid.subtract(start).dot(dir);
            if (proj > 1.0D && proj <= length) {
                if (mid.distanceToSqr(start.add(dir.scale(proj))) <= 2.4D) {
                    StavesElementBridge.dealElementalDamage(e, this, "CRYO", dmg, 1.0F, false, 0.20F);
                }
            }
        }
    }

    private void updateBossBar(int st) {
        bossEvent.setProgress(Mth.clamp(getHealth() / getMaxHealth(), 0.0F, 1.0F));
        if (st == STATE_STUNNED) {
            int secsLeft = Math.max(1, (STUN_DURATION_TICKS - stateTicks + 19) / 20);
            bossEvent.setColor(BossEvent.BossBarColor.WHITE);
            bossEvent.setName(Component.translatable("entity.velorise_teyvat_bosses.cryo_regisvine")
                    .append(Component.literal(String.format(Locale.ROOT, " — STUNNED (%ds | 1.55x DMG)", secsLeft))));
        } else {
            bossEvent.setColor(BossEvent.BossBarColor.BLUE);
            int pct = Math.round(getShieldProgress() * 100.0F);
            String phase = getCorollaPhase() == 0 ? "Roots Core" : "Corolla Core";
            bossEvent.setName(Component.translatable("entity.velorise_teyvat_bosses.cryo_regisvine")
                    .append(Component.literal(String.format(Locale.ROOT, " [%s: %d%%]", phase, pct))));
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
        tag.putFloat("RootYaw", getRootYaw());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.getBoolean("AnchorInit")) {
            anchorPos = new Vec3(tag.getDouble("AnchorX"), tag.getDouble("AnchorY"), tag.getDouble("AnchorZ"));
            anchorInitialized = true;
        }
        entityData.set(DATA_COROLLA_PHASE, tag.getInt("CorollaPhase"));
        if (tag.contains("RootYaw")) entityData.set(DATA_ROOT_YAW, tag.getFloat("RootYaw"));
    }
}

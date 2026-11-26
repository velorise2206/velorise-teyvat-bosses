package com.velorise.teyvatbosses.entity;

import com.velorise.teyvatbosses.compat.StavesElementBridge;
import net.minecraft.core.particles.ParticleOptions;
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
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Locale;

/**
 * Astralion: Prismatic Sovereign — Endgame Omni-Elemental Boss (Velorise: Teyvat Bosses).
 *
 * <p>3-Form Sovereign Combat State Machine synced 1:1 to all 16 overhauled {@code AstralionAnimations} clips:
 * <ul>
 *   <li><b>7-Element Prismatic Affinity:</b> Shifts across Pyro, Hydro, Electro, Cryo, Anemo, Geo, and Dendro
 *       via {@code elemental_shift_roar} (70% RES to active element, 30% base all-RES).</li>
 *   <li><b>Form 0 — Monolith Sovereign:</b> Greatsword {@code attack_prismatic_cleave}, Aegis ram
 *       {@code attack_shield_bash_burst}, 3-hit {@code combo_omni_trinity}, 6-wing sequential
 *       {@code attack_wing_barrage}, and {@code elemental_shift_roar}.</li>
 *   <li><b>Form 1 — Leviathan Sky-Serpent:</b> Morphs via {@code morph_to_leviathan}, swoops in
 *       {@code leviathan_flight}, and sweeps a 120° raycast {@code leviathan_chromatic_beam}.</li>
 *   <li><b>Form 2 — Cosmic Singularity:</b> Morphs via {@code morph_to_singularity}, projects a breakable
 *       Prismatic Shield with gravity pull in {@code singularity_hover}. Counter-element attacks shatter
 *       the shield into {@code shield_break_collapse} (6.0s stunned, 1.35x damage taken); otherwise unleashes
 *       {@code ultimate_sevenfold_cataclysm} (4.2s ascension → 7-element crater slam at tick 58).</li>
 * </ul>
 */
public class AstralionEntity extends Monster {

    // Boss States
    public static final int STATE_MONOLITH    = 0;
    public static final int STATE_LEVIATHAN   = 1;
    public static final int STATE_SINGULARITY = 2;
    public static final int STATE_STUNNED     = 3;
    public static final int STATE_DEATH       = 4;
    public static final int STATE_DORMANT     = 5;

    // 16 Overhauled Animation Clips
    public static final int ANIM_IDLE                      = 0;
    public static final int ANIM_HOVER_GLIDE               = 1;
    public static final int ANIM_PRISMATIC_CLEAVE          = 2;
    public static final int ANIM_SHIELD_BASH_BURST         = 3;
    public static final int ANIM_COMBO_TRINITY             = 4;
    public static final int ANIM_WING_BARRAGE              = 5;
    public static final int ANIM_ELEMENTAL_SHIFT_ROAR      = 6;
    public static final int ANIM_MORPH_TO_LEVIATHAN        = 7;
    public static final int ANIM_LEVIATHAN_FLIGHT          = 8;
    public static final int ANIM_LEVIATHAN_CHROMATIC_BEAM  = 9;
    public static final int ANIM_MORPH_TO_SINGULARITY      = 10;
    public static final int ANIM_SINGULARITY_HOVER         = 11;
    public static final int ANIM_ULTIMATE_CATACLYSM        = 12;
    public static final int ANIM_MORPH_TO_MONOLITH         = 13;
    public static final int ANIM_SHIELD_BREAK_COLLAPSE     = 14;
    public static final int ANIM_DEATH                     = 15;

    public static final String[] ANIM_NAMES = {
            "animation.astralion.idle",
            "animation.astralion.hover_glide",
            "animation.astralion.attack_prismatic_cleave",
            "animation.astralion.attack_shield_bash_burst",
            "animation.astralion.combo_omni_trinity",
            "animation.astralion.attack_wing_barrage",
            "animation.astralion.elemental_shift_roar",
            "animation.astralion.morph_to_leviathan",
            "animation.astralion.leviathan_flight",
            "animation.astralion.leviathan_chromatic_beam",
            "animation.astralion.morph_to_singularity",
            "animation.astralion.singularity_hover",
            "animation.astralion.ultimate_sevenfold_cataclysm",
            "animation.astralion.morph_to_monolith",
            "animation.astralion.shield_break_collapse",
            "animation.astralion.death"
    };

    public static final int[] ANIM_DURATIONS_TICKS = {
            80, 32, 32, 30, 56, 44, 44, 36, 44, 64, 36, 56, 84, 36, 48, 64
    };

    // 7 Elements
    public static final String[] ELEMENTS = { "PYRO", "HYDRO", "ELECTRO", "CRYO", "ANEMO", "GEO", "DENDRO" };

    private static final float BASE_ATK = 48.0F;
    private static final float MAX_SHIELD_GAUGE = 100.0F;
    private static final int SINGULARITY_CHARGE_TICKS = 150; // 7.5s window to break shield
    private static final int STUN_DURATION_TICKS = 120;      // 6.0s collapse vulnerability
    private static final int DEATH_DURATION_TICKS = 64;      // 3.2s

    private static final EntityDataAccessor<Integer> DATA_STATE =
            SynchedEntityData.defineId(AstralionEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_ANIM_ID =
            SynchedEntityData.defineId(AstralionEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_ANIM_EPOCH =
            SynchedEntityData.defineId(AstralionEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_ELEMENT_ID =
            SynchedEntityData.defineId(AstralionEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DATA_SHIELD_PROGRESS =
            SynchedEntityData.defineId(AstralionEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_TELEGRAPH_RADIUS =
            SynchedEntityData.defineId(AstralionEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_TELEGRAPH_X =
            SynchedEntityData.defineId(AstralionEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_TELEGRAPH_Y =
            SynchedEntityData.defineId(AstralionEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_TELEGRAPH_Z =
            SynchedEntityData.defineId(AstralionEntity.class, EntityDataSerializers.FLOAT);

    private static volatile boolean ELEMENT_LISTENER_REGISTERED = false;

    private final ServerBossEvent bossEvent = new ServerBossEvent(
            Component.translatable("entity.velorise_teyvat_bosses.astralion"),
            BossEvent.BossBarColor.PURPLE,
            BossEvent.BossBarOverlay.NOTCHED_12
    );

    private Vec3 arenaCenter = Vec3.ZERO;
    private boolean arenaInit = false;
    private int stateTicks = 0;
    private int noTargetTicks = 0;
    private int serverAnimTick = 0;
    private int clientAnimTick = 0;
    private int lastSyncedAnimId = -1;
    private int lastSyncedEpoch = -1;
    private int attackCooldown = 28;
    private int monolithActions = 0;
    private int leviathanBeamsFired = 0;
    private int lastAttackAnim = -1;
    private int strafeDir = 1;
    private float shieldGauge = 0.0F;
    private boolean singularityUltimateStarted = false;
    private boolean customDeathHandled = false;
    private Vec3 cataclysmTargetPos = Vec3.ZERO;

    public AstralionEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.xpReward = 1800;
        this.setPersistenceRequired();
        this.addTag("VeloriseInnateChecked");
        this.setNoGravity(true);
        ensureElementListener();
    }

    private static void ensureElementListener() {
        if (!ELEMENT_LISTENER_REGISTERED) {
            ELEMENT_LISTENER_REGISTERED = true;
            StavesElementBridge.registerPreAuraListener((level, target, attacker, element, gaugeUnits) -> {
                if (target instanceof AstralionEntity boss && boss.getBossState() == STATE_SINGULARITY && boss.shieldGauge > 0.0F) {
                    boss.damagePrismaticShield(level, element, Math.max(8.0F, gaugeUnits * 14.0F));
                }
                return true;
            });
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 1200.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.34D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.ARMOR, 10.0D)
                .add(Attributes.ARMOR_TOUGHNESS, 6.0D)
                .add(Attributes.FOLLOW_RANGE, 42.0D)
                .add(Attributes.ATTACK_DAMAGE, BASE_ATK);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_STATE, STATE_DORMANT);
        builder.define(DATA_ANIM_ID, ANIM_IDLE);
        builder.define(DATA_ANIM_EPOCH, 0);
        builder.define(DATA_ELEMENT_ID, 0);
        builder.define(DATA_SHIELD_PROGRESS, 0.0F);
        builder.define(DATA_TELEGRAPH_RADIUS, 0.0F);
        builder.define(DATA_TELEGRAPH_X, 0.0F);
        builder.define(DATA_TELEGRAPH_Y, 0.0F);
        builder.define(DATA_TELEGRAPH_Z, 0.0F);
    }

    public int getBossState() { return entityData.get(DATA_STATE); }
    public int getAnimId() { return entityData.get(DATA_ANIM_ID); }
    public int getAnimTick() { return level().isClientSide() ? clientAnimTick : serverAnimTick; }
    public int getAnimEpoch() { return entityData.get(DATA_ANIM_EPOCH); }
    public int getElementId() { return entityData.get(DATA_ELEMENT_ID); }
    public String getActiveElementName() {
        int id = getElementId();
        return (id >= 0 && id < ELEMENTS.length) ? ELEMENTS[id] : ELEMENTS[0];
    }
    public float getShieldProgress() { return entityData.get(DATA_SHIELD_PROGRESS); }
    public float getTelegraphRadius() { return entityData.get(DATA_TELEGRAPH_RADIUS); }
    public Vec3 getTelegraphPos() {
        return new Vec3(entityData.get(DATA_TELEGRAPH_X), entityData.get(DATA_TELEGRAPH_Y), entityData.get(DATA_TELEGRAPH_Z));
    }

    public String getCurrentAnimName() {
        int id = getAnimId();
        return (id >= 0 && id < ANIM_NAMES.length) ? ANIM_NAMES[id] : ANIM_NAMES[0];
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

    private void transitionTo(int newState, int animId) {
        entityData.set(DATA_STATE, newState);
        entityData.set(DATA_ANIM_ID, animId);
        entityData.set(DATA_ANIM_EPOCH, entityData.get(DATA_ANIM_EPOCH) + 1);
        clearTelegraph();
        serverAnimTick = 0;
        stateTicks = 0;
    }

    private void playAnim(int animId) {
        entityData.set(DATA_ANIM_ID, animId);
        entityData.set(DATA_ANIM_EPOCH, entityData.get(DATA_ANIM_EPOCH) + 1);
        clearTelegraph();
        serverAnimTick = 0;
    }

    @Override protected void registerGoals() {}
    @Override public boolean isPushable() { return false; }
    @Override protected void doPush(Entity entity) {}
    @Override public void knockback(double strength, double x, double z) {}
    @Override public boolean removeWhenFarAway(double distanceToClosestPlayer) { return false; }

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

    public void damagePrismaticShield(ServerLevel sl, String incomingElem, float amount) {
        if (getBossState() != STATE_SINGULARITY || singularityUltimateStarted || shieldGauge <= 0.0F) return;
        String active = getActiveElementName();
        boolean isCounter = incomingElem != null && !incomingElem.equalsIgnoreCase(active);
        float mult = isCounter ? 2.2F : 0.45F;
        shieldGauge = Math.max(0.0F, shieldGauge - amount * mult);
        entityData.set(DATA_SHIELD_PROGRESS, shieldGauge / MAX_SHIELD_GAUGE);
        sl.sendParticles(ParticleTypes.END_ROD, getX(), getY() + 2.8D, getZ(), 10, 1.2D, 1.2D, 1.2D, 0.08D);
        if (shieldGauge <= 0.0F) {
            triggerShieldBreak(sl);
        }
    }

    private void triggerShieldBreak(ServerLevel sl) {
        shieldGauge = 0.0F;
        singularityUltimateStarted = false;
        entityData.set(DATA_SHIELD_PROGRESS, 0.0F);
        transitionTo(STATE_STUNNED, ANIM_SHIELD_BREAK_COLLAPSE);
        sl.playSound(null, getX(), getY(), getZ(), SoundEvents.WITHER_BREAK_BLOCK, SoundSource.HOSTILE, 1.6F, 0.72F);
        sl.sendParticles(ParticleTypes.EXPLOSION, getX(), getY() + 2.0D, getZ(), 4, 1.0D, 0.8D, 1.0D, 0.02D);
        notifyNearbyPlayers(sl, "message.velorise_teyvat_bosses.astralion.shield_broken");
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        int st = getBossState();
        if (st == STATE_DEATH) return false;
        if (st == STATE_DORMANT) {
            if (!level().isClientSide() && level() instanceof ServerLevel sl) {
                awakenBoss(sl, source.getEntity() instanceof LivingEntity le ? le : null);
            }
            return false;
        }
        if (!level().isClientSide() && source.getEntity() instanceof LivingEntity attacker
                && attacker != this && !(attacker instanceof Player p && (p.isCreative() || p.isSpectator()))) {
            setTarget(attacker);
        }

        if (st == STATE_SINGULARITY && !singularityUltimateStarted && level() instanceof ServerLevel sl) {
            String elem = null;
            if (source.is(DamageTypeTags.IS_FIRE)) elem = "PYRO";
            else if (source.is(DamageTypeTags.IS_FREEZING)) elem = "CRYO";
            else if (source.is(DamageTypeTags.IS_LIGHTNING)) elem = "ELECTRO";
            damagePrismaticShield(sl, elem, Math.max(5.0F, amount * 0.65F));
            amount *= 0.25F;
        } else if (st == STATE_STUNNED) {
            amount *= 1.35F;
        } else {
            amount *= 0.70F; // 30% Base All RES
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
                sl.playSound(null, getX(), getY(), getZ(), SoundEvents.WITHER_DEATH, SoundSource.HOSTILE, 1.5F, 0.92F);
                if (!cause.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
                    TrounceBlossomEntity.spawn(sl, BossRespawnAnchorEntity.BOSS_ASTRALION, arenaInit ? arenaCenter : position(), getYRot());
                }
            }
        }
        super.die(cause);
    }

    private void awakenBoss(ServerLevel sl, LivingEntity trigger) {
        noTargetTicks = 0;
        if (trigger != null) {
            setTarget(trigger);
            faceTarget(trigger, 180.0F);
        }
        entityData.set(DATA_ELEMENT_ID, random.nextInt(ELEMENTS.length));
        transitionTo(STATE_MONOLITH, ANIM_ELEMENTAL_SHIFT_ROAR);
        sl.playSound(null, getX(), getY() + 2.0D, getZ(), SoundEvents.WITHER_SPAWN, SoundSource.HOSTILE, 1.4F, 1.15F);
        sl.sendParticles(ParticleTypes.END_ROD, getX(), getY() + 2.5D, getZ(), 48, 2.0D, 2.0D, 2.0D, 0.12D);
    }

    private void resetToDormant(ServerLevel sl) {
        noTargetTicks = 0;
        clearTelegraph();
        setTarget(null);
        setHealth(getMaxHealth());
        monolithActions = 0;
        leviathanBeamsFired = 0;
        shieldGauge = 0.0F;
        singularityUltimateStarted = false;
        entityData.set(DATA_SHIELD_PROGRESS, 0.0F);
        if (arenaInit) setPos(arenaCenter.x, arenaCenter.y, arenaCenter.z);
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
        int st = getBossState();
        updateBossBar(st);

        if (st == STATE_DEATH) {
            serverAnimTick++;
            if (serverAnimTick == 35) {
                sl.sendParticles(ParticleTypes.EXPLOSION, getX(), getY() + 2.5D, getZ(), 5, 1.2D, 1.2D, 1.2D, 0.02D);
                sl.playSound(null, getX(), getY(), getZ(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 1.3F, 0.78F);
            }
            return;
        }

        if (st == STATE_DORMANT) {
            if (arenaInit && distanceToSqr(arenaCenter) > 0.25D) setPos(arenaCenter.x, arenaCenter.y, arenaCenter.z);
            if (getHealth() < getMaxHealth()) setHealth(getMaxHealth());
            LivingEntity near = findActiveTarget(sl, 16.0D);
            if (near != null) awakenBoss(sl, near);
            return;
        }

        LivingEntity target = findActiveTarget(sl, 42.0D);
        if (target == null) {
            noTargetTicks++;
            if (st == STATE_MONOLITH && getAnimId() != ANIM_IDLE) playAnim(ANIM_IDLE);
            serverAnimTick = (serverAnimTick + 1) % ANIM_DURATIONS_TICKS[ANIM_IDLE];
            if (noTargetTicks >= 220) resetToDormant(sl);
            return;
        }
        noTargetTicks = 0;

        switch (st) {
            case STATE_STUNNED -> tickStunned(sl);
            case STATE_LEVIATHAN -> tickLeviathan(sl, target);
            case STATE_SINGULARITY -> tickSingularity(sl, target);
            default -> tickMonolith(sl, target);
        }
    }

    private void tickStunned(ServerLevel sl) {
        serverAnimTick = (serverAnimTick + 1) % ANIM_DURATIONS_TICKS[ANIM_SHIELD_BREAK_COLLAPSE];
        if (stateTicks % 10 == 0) {
            sl.sendParticles(ParticleTypes.SMOKE, getX(), getY() + 1.5D, getZ(), 6, 0.9D, 0.6D, 0.9D, 0.02D);
        }
        if (stateTicks >= STUN_DURATION_TICKS) {
            monolithActions = 0;
            transitionTo(STATE_MONOLITH, ANIM_IDLE);
            attackCooldown = 36;
        }
    }

    private void tickMonolith(ServerLevel sl, LivingEntity target) {
        serverAnimTick++;
        int anim = getAnimId();
        int t = serverAnimTick;

        if (anim == ANIM_MORPH_TO_MONOLITH) {
            faceTarget(target, 8.0F);
            if (t >= ANIM_DURATIONS_TICKS[ANIM_MORPH_TO_MONOLITH]) {
                playAnim(ANIM_IDLE);
                attackCooldown = 28;
            }
            return;
        }

        if (anim == ANIM_IDLE || anim == ANIM_HOVER_GLIDE) {
            faceTarget(target, 11.0F);
            double dist = Math.hypot(target.getX() - getX(), target.getZ() - getZ());
            boolean shouldGlide = (anim == ANIM_HOVER_GLIDE) ? (dist > 5.0D && dist < 30.0D) : (dist > 6.8D && dist < 30.0D);
            boolean shouldStrafe = !shouldGlide && dist >= 3.2D && dist <= 6.8D && attackCooldown > 12;

            if (shouldGlide || shouldStrafe) {
                if (anim != ANIM_HOVER_GLIDE) {
                    strafeDir = random.nextBoolean() ? 1 : -1;
                    playAnim(ANIM_HOVER_GLIDE);
                }
                Vec3 toTarget = new Vec3(target.getX() - getX(), 0.0D, target.getZ() - getZ());
                if (toTarget.lengthSqr() > 1e-4D) {
                    Vec3 fwd = toTarget.normalize();
                    Vec3 side = new Vec3(-fwd.z * strafeDir, 0.0D, fwd.x * strafeDir);
                    Vec3 move = shouldGlide ? fwd.scale(0.22D).add(side.scale(0.07D)) : side.scale(0.13D);
                    moveInArena(move.x, move.z);
                }
            } else if (anim != ANIM_IDLE) {
                playAnim(ANIM_IDLE);
            }

            if (attackCooldown > 0) {
                attackCooldown--;
            } else {
                pickMonolithAction(sl, target, dist);
            }
            return;
        }

        executeMonolithAttackTick(sl, target, anim, t);
        if (t >= ANIM_DURATIONS_TICKS[anim]) {
            playAnim(ANIM_IDLE);
            attackCooldown = 52 + random.nextInt(22);
        }
    }

    private void pickMonolithAction(ServerLevel sl, LivingEntity target, double dist) {
        float hpPct = getHealth() / getMaxHealth();
        if (monolithActions >= 4) {
            monolithActions = 0;
            if (hpPct < 0.55F && random.nextBoolean()) {
                enterSingularityForm(sl);
            } else {
                enterLeviathanForm(sl, target);
            }
            return;
        }

        monolithActions++;
        int chosen;
        if (monolithActions == 3) {
            chosen = ANIM_ELEMENTAL_SHIFT_ROAR;
        } else if (dist > 11.0D) {
            chosen = random.nextBoolean() ? ANIM_WING_BARRAGE : ANIM_SHIELD_BASH_BURST;
        } else if (dist > 6.5D) {
            int[] midPool = { ANIM_SHIELD_BASH_BURST, ANIM_PRISMATIC_CLEAVE, ANIM_WING_BARRAGE };
            chosen = midPool[random.nextInt(midPool.length)];
        } else {
            int[] nearPool = { ANIM_PRISMATIC_CLEAVE, ANIM_COMBO_TRINITY, ANIM_SHIELD_BASH_BURST };
            chosen = nearPool[random.nextInt(nearPool.length)];
        }
        if (chosen == lastAttackAnim) {
            chosen = (chosen == ANIM_PRISMATIC_CLEAVE) ? ANIM_COMBO_TRINITY : ANIM_PRISMATIC_CLEAVE;
        }
        lastAttackAnim = chosen;
        faceTarget(target, 180.0F);
        playAnim(chosen);
    }

    private void executeMonolithAttackTick(ServerLevel sl, LivingEntity target, int anim, int t) {
        String elem = getActiveElementName();
        switch (anim) {
            case ANIM_PRISMATIC_CLEAVE -> {
                if (t < 12) faceTarget(target, 12.0F);
                if (t == 7) setTelegraph(forwardPoint(3.8D), 4.6F);
                if (t == 11) sl.playSound(null, getX(), getY() + 2.0D, getZ(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.HOSTILE, 1.2F, 0.85F);
                if (t >= 12 && t <= 15) dashForwardInArena(0.45D);
                if (t == 15) { // 0.75s exact impact snap
                    clearTelegraph();
                    arcAttack(sl, 7.2D, 0.05D, BASE_ATK * 0.75F, elem, 0.25F);
                    sl.sendParticles(ParticleTypes.SWEEP_ATTACK, forwardPoint(3.5D).x, getY() + 2.0D, forwardPoint(3.5D).z, 5, 1.8D, 0.3D, 1.8D, 0.0D);
                    sl.playSound(null, getX(), getY(), getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.HOSTILE, 1.5F, 0.78F);
                }
            }
            case ANIM_SHIELD_BASH_BURST -> {
                if (t < 11) faceTarget(target, 12.0F);
                if (t >= 11 && t <= 15) dashForwardInArena(0.95D);
                if (t == 14) { // 0.70s exact ram burst
                    aoeDamage(sl, forwardPoint(2.5D), 5.0D, BASE_ATK * 0.65F, elem, 0.22F, true);
                    sl.sendParticles(ParticleTypes.EXPLOSION, forwardPoint(2.5D).x, getY() + 1.5D, forwardPoint(2.5D).z, 3, 0.8D, 0.4D, 0.8D, 0.02D);
                    sl.playSound(null, getX(), getY(), getZ(), SoundEvents.SHIELD_BLOCK, SoundSource.HOSTILE, 1.5F, 0.65F);
                    sl.playSound(null, getX(), getY(), getZ(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 1.1F, 0.9F);
                }
            }
            case ANIM_COMBO_TRINITY -> {
                if (t < 12 || (t > 18 && t < 26) || (t > 32 && t < 41)) faceTarget(target, 14.0F);
                if (t >= 12 && t <= 15) dashForwardInArena(0.55D);
                if (t == 14) { // Hit 1: Greatsword cleave (0.72s)
                    arcAttack(sl, 6.8D, 0.10D, BASE_ATK * 0.45F, elem, 0.18F);
                    sl.playSound(null, getX(), getY(), getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.HOSTILE, 1.35F, 1.0F);
                }
                if (t >= 26 && t <= 30) dashForwardInArena(0.80D);
                if (t == 29) { // Hit 2: Aegis bash (1.45s)
                    aoeDamage(sl, forwardPoint(2.6D), 4.6D, BASE_ATK * 0.55F, elem, 0.20F, false);
                    sl.playSound(null, getX(), getY(), getZ(), SoundEvents.SHIELD_BLOCK, SoundSource.HOSTILE, 1.4F, 0.8F);
                }
                if (t == 38) setTelegraph(position(), 6.8F);
                if (t == 45) { // Hit 3: 360° Prismatic Whirlwind Finisher (2.25s)
                    clearTelegraph();
                    aoeDamage(sl, position(), 6.8D, BASE_ATK * 0.85F, elem, 0.28F, true);
                    sl.sendParticles(particleForElement(getElementId()), getX(), getY() + 1.8D, getZ(), 44, 3.2D, 0.6D, 3.2D, 0.12D);
                    sl.playSound(null, getX(), getY(), getZ(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 1.4F, 0.82F);
                }
            }
            case ANIM_WING_BARRAGE -> {
                faceTarget(target, 10.0F);
                // 6 sequential wing snaps synced to t = 14, 17, 20, 23, 26, 28
                if (t == 14 || t == 17 || t == 20 || t == 23 || t == 26 || t == 28) {
                    int boltIdx = (t - 14) / 3;
                    float spreadDeg = (boltIdx % 2 == 0 ? -1 : 1) * (6.0F + boltIdx * 2.5F);
                    firePrismaticWingRay(sl, target, spreadDeg, BASE_ATK * 0.30F, elem);
                    sl.playSound(null, getX(), getY() + 2.6D, getZ(), SoundEvents.TRIDENT_THROW.value(), SoundSource.HOSTILE, 1.15F, 1.15F + boltIdx * 0.05F);
                }
            }
            case ANIM_ELEMENTAL_SHIFT_ROAR -> {
                faceTarget(target, 8.0F);
                if (t == 21) { // 1.05s roar eruption
                    int nextElem = (getElementId() + 1 + random.nextInt(ELEMENTS.length - 1)) % ELEMENTS.length;
                    entityData.set(DATA_ELEMENT_ID, nextElem);
                    String newElem = getActiveElementName();
                    aoeDamage(sl, position(), 6.2D, BASE_ATK * 0.50F, newElem, 0.18F, false);
                    sl.sendParticles(particleForElement(nextElem), getX(), getY() + 2.2D, getZ(), 52, 2.8D, 1.6D, 2.8D, 0.14D);
                    sl.playSound(null, getX(), getY() + 2.0D, getZ(), SoundEvents.ENDER_DRAGON_GROWL, SoundSource.HOSTILE, 1.4F, 1.12F);
                    sl.playSound(null, getX(), getY() + 2.0D, getZ(), SoundEvents.BEACON_POWER_SELECT, SoundSource.HOSTILE, 1.3F, 1.18F);
                }
            }
            default -> {}
        }
    }

    // -------------------------------------------------------------------------
    // FORM 1: LEVIATHAN (morph -> flight -> chromatic beam)
    // -------------------------------------------------------------------------
    private void enterLeviathanForm(ServerLevel sl, LivingEntity target) {
        leviathanBeamsFired = 0;
        faceTarget(target, 180.0F);
        transitionTo(STATE_LEVIATHAN, ANIM_MORPH_TO_LEVIATHAN);
        sl.playSound(null, getX(), getY() + 2.5D, getZ(), SoundEvents.ENDER_DRAGON_GROWL, SoundSource.HOSTILE, 1.4F, 0.85F);
        sl.sendParticles(ParticleTypes.END_ROD, getX(), getY() + 2.5D, getZ(), 36, 2.2D, 1.8D, 2.2D, 0.10D);
    }

    private void tickLeviathan(ServerLevel sl, LivingEntity target) {
        serverAnimTick++;
        int anim = getAnimId();
        int t = serverAnimTick;

        if (anim == ANIM_MORPH_TO_LEVIATHAN) {
            faceTarget(target, 10.0F);
            if (t >= ANIM_DURATIONS_TICKS[ANIM_MORPH_TO_LEVIATHAN]) {
                playAnim(ANIM_LEVIATHAN_FLIGHT);
            }
            return;
        }

        if (anim == ANIM_LEVIATHAN_FLIGHT) {
            faceTarget(target, 9.0F);
            Vec3 toTarget = new Vec3(target.getX() - getX(), 0.0D, target.getZ() - getZ());
            if (toTarget.lengthSqr() > 1e-4D) {
                Vec3 fwd = toTarget.normalize();
                Vec3 side = new Vec3(-fwd.z * strafeDir, 0.0D, fwd.x * strafeDir);
                moveInArena(side.x * 0.28D + fwd.x * 0.06D, side.z * 0.28D + fwd.z * 0.06D);
            }
            if (t >= ANIM_DURATIONS_TICKS[ANIM_LEVIATHAN_FLIGHT]) {
                if (leviathanBeamsFired >= 2) {
                    if (getHealth() / getMaxHealth() < 0.55F) {
                        enterSingularityForm(sl);
                    } else {
                        transitionTo(STATE_MONOLITH, ANIM_IDLE);
                        attackCooldown = 36;
                    }
                } else {
                    leviathanBeamsFired++;
                    faceTarget(target, 180.0F);
                    playAnim(ANIM_LEVIATHAN_CHROMATIC_BEAM);
                    sl.playSound(null, getX(), getY() + 2.5D, getZ(), SoundEvents.BEACON_ACTIVATE, SoundSource.HOSTILE, 1.4F, 1.35F);
                }
            }
            return;
        }

        if (anim == ANIM_LEVIATHAN_CHROMATIC_BEAM) {
            // Sweeping chromatic beam active ticks 15..51 (0.75s - 2.55s)
            if (t >= 15 && t <= 51 && t % 4 == 0) {
                float sweepFrac = (t - 15) / 36.0F;
                float sweepYawDeg = getYRot() - 58.0F + sweepFrac * 116.0F;
                sweepBeamDamage(sl, sweepYawDeg, 18.0D, BASE_ATK * 0.22F, ELEMENTS[(t / 4) % ELEMENTS.length]);
                if (t % 8 == 3) {
                    sl.playSound(null, getX(), getY() + 2.5D, getZ(), SoundEvents.TRIDENT_THUNDER.value(), SoundSource.HOSTILE, 0.9F, 1.35F);
                }
            }
            if (t >= ANIM_DURATIONS_TICKS[ANIM_LEVIATHAN_CHROMATIC_BEAM]) {
                strafeDir = -strafeDir;
                playAnim(ANIM_LEVIATHAN_FLIGHT);
            }
        }
    }

    // -------------------------------------------------------------------------
    // FORM 2: SINGULARITY (morph -> hover/shield -> ultimate or shield break -> monolith)
    // -------------------------------------------------------------------------
    private void enterSingularityForm(ServerLevel sl) {
        shieldGauge = MAX_SHIELD_GAUGE;
        singularityUltimateStarted = false;
        entityData.set(DATA_SHIELD_PROGRESS, 1.0F);
        setPos(arenaCenter.x, arenaCenter.y, arenaCenter.z);
        transitionTo(STATE_SINGULARITY, ANIM_MORPH_TO_SINGULARITY);
        sl.playSound(null, getX(), getY() + 2.5D, getZ(), SoundEvents.WITHER_SPAWN, SoundSource.HOSTILE, 1.35F, 1.30F);
        notifyNearbyPlayers(sl, "message.velorise_teyvat_bosses.astralion.singularity");
    }

    private void tickSingularity(ServerLevel sl, LivingEntity target) {
        serverAnimTick++;
        int anim = getAnimId();
        int t = serverAnimTick;

        if (anim == ANIM_MORPH_TO_SINGULARITY) {
            faceTarget(target, 6.0F);
            if (t >= ANIM_DURATIONS_TICKS[ANIM_MORPH_TO_SINGULARITY]) {
                playAnim(ANIM_SINGULARITY_HOVER);
            }
            return;
        }

        if (anim == ANIM_SINGULARITY_HOVER) {
            faceTarget(target, 6.0F);
            // Gentle cosmic gravity pull toward arena center
            if (stateTicks % 4 == 0) {
                AABB pullBox = getBoundingBox().inflate(15.0D, 6.0D, 15.0D);
                for (Player p : sl.getEntitiesOfClass(Player.class, pullBox, pl -> pl.isAlive() && !pl.isCreative() && !pl.isSpectator())) {
                    Vec3 pull = position().subtract(p.position()).multiply(1.0D, 0.0D, 1.0D);
                    if (pull.lengthSqr() > 4.0D) {
                        Vec3 step = pull.normalize().scale(0.14D);
                        p.push(step.x, 0.0D, step.z);
                        p.hurtMarked = true;
                    }
                }
                sl.sendParticles(ParticleTypes.PORTAL, getX(), getY() + 3.0D, getZ(), 18, 2.5D, 1.8D, 2.5D, 0.15D);
            }
            if (stateTicks >= SINGULARITY_CHARGE_TICKS) {
                singularityUltimateStarted = true;
                cataclysmTargetPos = clampToArena(new Vec3(target.getX(), arenaCenter.y, target.getZ()));
                faceTarget(target, 180.0F);
                playAnim(ANIM_ULTIMATE_CATACLYSM);
                sl.playSound(null, getX(), getY() + 3.5D, getZ(), SoundEvents.TRIDENT_THUNDER.value(), SoundSource.HOSTILE, 1.6F, 0.72F);
            }
            return;
        }

        if (anim == ANIM_ULTIMATE_CATACLYSM) {
            if (t < 46) {
                faceTarget(target, 8.0F);
                cataclysmTargetPos = clampToArena(new Vec3(target.getX(), arenaCenter.y, target.getZ()));
                if (t >= 20) setTelegraph(cataclysmTargetPos, 9.5F);
            }
            if (t == 58) { // 2.90s exact Sevenfold Cataclysm meteor impact!
                clearTelegraph();
                setPos(cataclysmTargetPos.x, arenaCenter.y, cataclysmTargetPos.z);
                aoeDamage(sl, cataclysmTargetPos, 9.5D, BASE_ATK * 1.30F, getActiveElementName(), 0.35F, true);
                sl.sendParticles(ParticleTypes.EXPLOSION_EMITTER, cataclysmTargetPos.x, arenaCenter.y + 0.8D, cataclysmTargetPos.z, 2, 1.0D, 0.5D, 1.0D, 0.01D);
                sl.playSound(null, cataclysmTargetPos.x, arenaCenter.y, cataclysmTargetPos.z, SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 1.8F, 0.65F);
                sl.playSound(null, cataclysmTargetPos.x, arenaCenter.y, cataclysmTargetPos.z, SoundEvents.TRIDENT_THUNDER.value(), SoundSource.HOSTILE, 1.6F, 0.80F);
                // 7-element radial shockwave pillars
                for (int i = 0; i < ELEMENTS.length; i++) {
                    double ang = (Math.PI * 2.0D * i) / ELEMENTS.length;
                    double px = cataclysmTargetPos.x + Math.cos(ang) * 6.5D;
                    double pz = cataclysmTargetPos.z + Math.sin(ang) * 6.5D;
                    sl.sendParticles(particleForElement(i), px, arenaCenter.y + 1.4D, pz, 28, 0.7D, 2.2D, 0.7D, 0.08D);
                }
            }
            if (t >= ANIM_DURATIONS_TICKS[ANIM_ULTIMATE_CATACLYSM]) {
                shieldGauge = 0.0F;
                singularityUltimateStarted = false;
                entityData.set(DATA_SHIELD_PROGRESS, 0.0F);
                transitionTo(STATE_MONOLITH, ANIM_MORPH_TO_MONOLITH);
            }
        }
    }

    // -------------------------------------------------------------------------
    // Combat Geometry & Helper Utilities
    // -------------------------------------------------------------------------
    private void firePrismaticWingRay(ServerLevel sl, LivingEntity target, float spreadDeg, float dmg, String elem) {
        Vec3 start = position().add(0.0D, 2.8D, 0.0D);
        Vec3 to = target.position().add(0.0D, target.getBbHeight() * 0.5D, 0.0D).subtract(start);
        if (to.lengthSqr() < 1e-4D) return;
        Vec3 dir = to.normalize().yRot(spreadDeg * Mth.DEG_TO_RAD);
        ParticleOptions p = particleForElement(getElementId());
        for (double s = 1.5D; s <= 18.0D; s += 1.4D) {
            Vec3 pt = start.add(dir.scale(s));
            sl.sendParticles(p, pt.x, pt.y, pt.z, 2, 0.14D, 0.14D, 0.14D, 0.01D);
        }
        AABB box = getBoundingBox().inflate(19.0D, 5.0D, 19.0D);
        for (LivingEntity e : sl.getEntitiesOfClass(LivingEntity.class, box, le -> le.isAlive() && le != this)) {
            Vec3 mid = e.position().add(0.0D, e.getBbHeight() * 0.5D, 0.0D);
            double proj = mid.subtract(start).dot(dir);
            if (proj > 1.0D && proj <= 18.5D && mid.distanceToSqr(start.add(dir.scale(proj))) <= 2.4D) {
                StavesElementBridge.dealElementalDamage(e, this, elem, dmg, 1.0F, false, 0.20F);
            }
        }
    }

    private void sweepBeamDamage(ServerLevel sl, float yawDeg, double length, float dmg, String elem) {
        float rad = yawDeg * Mth.DEG_TO_RAD;
        Vec3 dir = new Vec3(-Math.sin(rad), -0.10D, Math.cos(rad)).normalize();
        Vec3 start = position().add(0.0D, 3.0D, 0.0D);
        for (double s = 1.5D; s <= length; s += 1.3D) {
            Vec3 pt = start.add(dir.scale(s));
            sl.sendParticles(ParticleTypes.END_ROD, pt.x, pt.y, pt.z, 2, 0.22D, 0.22D, 0.22D, 0.02D);
        }
        AABB box = getBoundingBox().inflate(length, 5.0D, length);
        for (LivingEntity e : sl.getEntitiesOfClass(LivingEntity.class, box, le -> le.isAlive() && le != this)) {
            Vec3 mid = e.position().add(0.0D, e.getBbHeight() * 0.5D, 0.0D);
            double proj = mid.subtract(start).dot(dir);
            if (proj > 0.8D && proj <= length && mid.distanceToSqr(start.add(dir.scale(proj))) <= 3.2D) {
                StavesElementBridge.dealElementalDamage(e, this, elem, dmg, 1.0F, false, 0.22F);
            }
        }
    }

    private void aoeDamage(ServerLevel sl, Vec3 center, double radius, float dmg, String element, float pierceRatio, boolean applySlow) {
        AABB box = new AABB(center.x - radius, center.y - 2.0D, center.z - radius, center.x + radius, center.y + 4.5D, center.z + radius);
        for (LivingEntity e : sl.getEntitiesOfClass(LivingEntity.class, box, le -> le.isAlive() && le != this)) {
            if (e.position().distanceToSqr(center) <= radius * radius) {
                StavesElementBridge.dealElementalDamage(e, this, element, dmg, 1.0F, false, pierceRatio);
                Vec3 knock = e.position().subtract(center).multiply(1.0D, 0.0D, 1.0D);
                if (knock.lengthSqr() > 1e-4D) {
                    knock = knock.normalize().scale(0.95D);
                    e.push(knock.x, 0.45D, knock.z);
                }
                if (applySlow) {
                    e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 50, 1));
                }
            }
        }
    }

    private void arcAttack(ServerLevel sl, double radius, double minDot, float dmg, String element, float pierceRatio) {
        float rad = getYRot() * Mth.DEG_TO_RAD;
        Vec3 forward = new Vec3(-Math.sin(rad), 0.0D, Math.cos(rad));
        AABB box = getBoundingBox().inflate(radius, 3.5D, radius);
        for (LivingEntity e : sl.getEntitiesOfClass(LivingEntity.class, box, le -> le.isAlive() && le != this)) {
            Vec3 to = e.position().subtract(position());
            if (to.horizontalDistanceSqr() <= radius * radius && to.normalize().dot(forward) >= minDot) {
                StavesElementBridge.dealElementalDamage(e, this, element, dmg, 1.0F, false, pierceRatio);
            }
        }
    }

    private Vec3 clampToArena(Vec3 pos) {
        double dx = pos.x - arenaCenter.x;
        double dz = pos.z - arenaCenter.z;
        double distSq = dx * dx + dz * dz;
        if (distSq > 18.0D * 18.0D) {
            double scale = 18.0D / Math.sqrt(distSq);
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

    private void notifyNearbyPlayers(ServerLevel sl, String langKey) {
        Component msg = Component.translatable(langKey);
        for (ServerPlayer sp : sl.players()) {
            if (sp.distanceToSqr(this) <= 44.0D * 44.0D) {
                sp.displayClientMessage(msg, true);
            }
        }
    }

    public static ParticleOptions particleForElement(int elemId) {
        return switch (elemId) {
            case 0 -> ParticleTypes.FLAME;
            case 1 -> ParticleTypes.SPLASH;
            case 2 -> ParticleTypes.ELECTRIC_SPARK;
            case 3 -> ParticleTypes.SNOWFLAKE;
            case 4 -> ParticleTypes.CLOUD;
            case 5 -> ParticleTypes.END_ROD;
            default -> ParticleTypes.HAPPY_VILLAGER;
        };
    }

    private void updateBossBar(int st) {
        bossEvent.setVisible(st != STATE_DORMANT && st != STATE_DEATH);
        bossEvent.setProgress(Mth.clamp(getHealth() / getMaxHealth(), 0.0F, 1.0F));
        Component base = Component.translatable("entity.velorise_teyvat_bosses.astralion");
        if (st == STATE_SINGULARITY) {
            bossEvent.setColor(BossEvent.BossBarColor.RED);
            int pct = Math.round(getShieldProgress() * 100.0F);
            bossEvent.setName(base.copy().append(Component.literal(String.format(Locale.ROOT, " — Singularity Shield [%d%%]", pct))));
        } else if (st == STATE_LEVIATHAN) {
            bossEvent.setColor(BossEvent.BossBarColor.BLUE);
            bossEvent.setName(base.copy().append(Component.literal(" — Leviathan Form [" + getActiveElementName() + "]")));
        } else if (st == STATE_STUNNED) {
            bossEvent.setColor(BossEvent.BossBarColor.WHITE);
            bossEvent.setName(base.copy().append(Component.literal(" — SHIELD BROKEN")));
        } else {
            bossEvent.setColor(BossEvent.BossBarColor.PURPLE);
            bossEvent.setName(base.copy().append(Component.literal(" [" + getActiveElementName() + "]")));
        }
    }

    private LivingEntity findActiveTarget(ServerLevel sl, double range) {
        double maxDistSq = range * range;
        LivingEntity current = getTarget();
        if (current != null && current.isAlive() && current != this
                && !(current instanceof Player p && (p.isCreative() || p.isSpectator()))
                && current.distanceToSqr(this) <= maxDistSq) {
            return current;
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
        tag.putInt("ElemId", getElementId());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.getBoolean("ArenaInit")) {
            arenaCenter = new Vec3(tag.getDouble("ArenaX"), tag.getDouble("ArenaY"), tag.getDouble("ArenaZ"));
            arenaInit = true;
        }
        if (tag.contains("ElemId")) entityData.set(DATA_ELEMENT_ID, tag.getInt("ElemId"));
    }
}

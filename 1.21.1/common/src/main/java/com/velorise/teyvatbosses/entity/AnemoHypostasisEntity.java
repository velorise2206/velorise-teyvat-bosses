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
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Genshin-accurate Anemo Hypostasis (Beth) World Boss.
 *
 * <p>Implements full canon combat loop:
 * <ul>
 *   <li>Invulnerable while shielded by its 8 rune-carved cubes; vulnerable during exposed core windows after attacks.</li>
 *   <li>6 signature attacks: Butterfly Winged Gust Sweep, Magic Circle Missile Volley, 3x Ground Wind Pillar Eruptions,
 *       Hurricane Tornadoes + Updrafts + Anemo Orbs, Crushing Box Clap Slam, and Vacuum Pull & Shockwave Implosion.</li>
 *   <li>Revival Phase at &le;5% HP: spawns 4 airborne Wind Crystals & 4 Updraft Wind Currents; unabsorbed crystals heal Beth.</li>
 *   <li>Spawns a Genshin-accurate {@link TrounceBlossomEntity} upon defeat at the arena center.</li>
 * </ul>
 */
public class AnemoHypostasisEntity extends Monster {

    // Boss states
    public static final int STATE_DORMANT    = 0;
    public static final int STATE_AWAKEN     = 1;
    public static final int STATE_COMBAT     = 2;
    public static final int STATE_VULNERABLE = 3;
    public static final int STATE_REVIVAL    = 4;
    public static final int STATE_DEATH      = 5;

    // Animation IDs
    public static final int ANIM_DORMANT          = 0;
    public static final int ANIM_AWAKEN           = 1;
    public static final int ANIM_IDLE             = 2;
    public static final int ANIM_VULNERABLE       = 3;
    public static final int ANIM_BUTTERFLY_SWEEP  = 4;
    public static final int ANIM_MISSILE_CIRCLE   = 5;
    public static final int ANIM_PILLAR_ERUPTION  = 6;
    public static final int ANIM_TORNADO_STORM    = 7;
    public static final int ANIM_CLAP_SLAM        = 8;
    public static final int ANIM_VACUUM_IMPLOSION = 9;
    public static final int ANIM_REVIVAL          = 10;
    public static final int ANIM_DEATH            = 11;

    private static final EntityDataAccessor<Integer> DATA_BOSS_STATE =
            SynchedEntityData.defineId(AnemoHypostasisEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_ANIM_ID =
            SynchedEntityData.defineId(AnemoHypostasisEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_ANIM_EPOCH =
            SynchedEntityData.defineId(AnemoHypostasisEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_TELEGRAPH_ACTIVE =
            SynchedEntityData.defineId(AnemoHypostasisEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> DATA_TELEGRAPH_X =
            SynchedEntityData.defineId(AnemoHypostasisEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_TELEGRAPH_Y =
            SynchedEntityData.defineId(AnemoHypostasisEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_TELEGRAPH_Z =
            SynchedEntityData.defineId(AnemoHypostasisEntity.class, EntityDataSerializers.FLOAT);

    private final ServerBossEvent bossEvent = new ServerBossEvent(
            Component.translatable("entity.velorise_teyvat_bosses.anemo_hypostasis"),
            BossEvent.BossBarColor.GREEN,
            BossEvent.BossBarOverlay.NOTCHED_10
    );

    private Vec3 arenaCenter = Vec3.ZERO;
    private boolean arenaInit = false;
    private static final double ARENA_SOFT_LEASH_RADIUS = 48.0D;
    private float savedSpawnYaw = 0.0F;

    private int stateTick = 0;
    private int noTargetTicks = 0;
    private int attackCooldown = 30;
    private int lastAttackAnim = -1;
    private int attacksSinceTornado = 0;
    private boolean revivalUsed = false;
    private final BossDamageCap damageCap = new BossDamageCap();
    private boolean customDeathHandled = false;
    private int revivalCrystalsRemaining = 0;
    private int pendingRecallOrbs = 0;

    private Vec3 moveTarget = null;
    private Vec3 telegraphPos = Vec3.ZERO;

    // Client-side smooth animation tick (zero 20-FPS network jitter)
    private int clientAnimTick = 0;
    private int lastSyncedAnimId = -1;
    private int lastSyncedEpoch = -1;

    private boolean innateAnemoSynced = false;

    public AnemoHypostasisEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.xpReward = 450;
        this.setNoGravity(true);
        this.noPhysics = false;
        this.addTag("VeloriseInnateChecked");
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 420.0D)
                .add(Attributes.ARMOR, 6.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.35D)
                .add(Attributes.ATTACK_DAMAGE, 14.0D)
                .add(Attributes.FOLLOW_RANGE, 40.0D);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_BOSS_STATE, STATE_DORMANT);
        builder.define(DATA_ANIM_ID, ANIM_DORMANT);
        builder.define(DATA_ANIM_EPOCH, 0);
        builder.define(DATA_TELEGRAPH_ACTIVE, false);
        builder.define(DATA_TELEGRAPH_X, 0.0F);
        builder.define(DATA_TELEGRAPH_Y, 0.0F);
        builder.define(DATA_TELEGRAPH_Z, 0.0F);
    }

    @Override
    protected void registerGoals() {
        // Custom deterministic Genshin boss state machine drives all movement and attacks.
    }

    public int getBossState() { return entityData.get(DATA_BOSS_STATE); }
    public int getAnimId() { return entityData.get(DATA_ANIM_ID); }
    public int getAnimEpoch() { return entityData.get(DATA_ANIM_EPOCH); }
    public int getAnimTick() { return level().isClientSide() ? clientAnimTick : stateTick; }
    public boolean isTelegraphActive() { return entityData.get(DATA_TELEGRAPH_ACTIVE); }
    public Vec3 getTelegraphPos() {
        return new Vec3(entityData.get(DATA_TELEGRAPH_X), entityData.get(DATA_TELEGRAPH_Y), entityData.get(DATA_TELEGRAPH_Z));
    }

    public String getCurrentAnimName() {
        return switch (getAnimId()) {
            case ANIM_DORMANT -> "dormant";
            case ANIM_AWAKEN -> "awaken";
            case ANIM_VULNERABLE -> "vulnerable";
            case ANIM_BUTTERFLY_SWEEP -> "butterfly_sweep";
            case ANIM_MISSILE_CIRCLE -> "missile_circle";
            case ANIM_PILLAR_ERUPTION -> "pillar_eruption";
            case ANIM_TORNADO_STORM -> "tornado_storm";
            case ANIM_CLAP_SLAM -> "clap_slam";
            case ANIM_VACUUM_IMPLOSION -> "vacuum_implosion";
            case ANIM_REVIVAL -> "revival";
            case ANIM_DEATH -> "death";
            default -> "idle";
        };
    }

    public boolean isCoreExposed() {
        return getBossState() == STATE_VULNERABLE;
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
        entityData.set(DATA_TELEGRAPH_ACTIVE, false);
        stateTick = 0;
    }

    private void setTelegraph(Vec3 pos) {
        telegraphPos = pos;
        entityData.set(DATA_TELEGRAPH_X, (float) pos.x);
        entityData.set(DATA_TELEGRAPH_Y, (float) pos.y);
        entityData.set(DATA_TELEGRAPH_Z, (float) pos.z);
        entityData.set(DATA_TELEGRAPH_ACTIVE, true);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        int st = getBossState();
        if (st == STATE_DEATH || st == STATE_AWAKEN || st == STATE_REVIVAL) {
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
        // Canon Hypostasis mechanic: Invulnerable while protected by the 8 outer shell cubes!
        if (st != STATE_VULNERABLE && !source.is(net.minecraft.tags.DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            if (!level().isClientSide() && level() instanceof ServerLevel sl) {
                sl.playSound(null, getX(), getY() + 1.5D, getZ(), SoundEvents.SHIELD_BLOCK, SoundSource.HOSTILE, 0.85F, 1.45F);
                sl.sendParticles(ParticleTypes.CRIT, getX(), getY() + 1.8D, getZ(), 6, 0.7D, 0.7D, 0.7D, 0.05D);
            }
            return false;
        }

        // Trigger Revival Phase when HP would drop to <= 5% for the first time!
        float revivalThreshold = getMaxHealth() * 0.05F;
        if (!revivalUsed && !source.is(net.minecraft.tags.DamageTypeTags.BYPASSES_INVULNERABILITY)
                && (getHealth() - amount) <= revivalThreshold) {
            setHealth(Math.max(2.0F, revivalThreshold));
            if (!level().isClientSide() && level() instanceof ServerLevel sl) {
                enterRevivalPhase(sl);
            }
            return true;
        }

        amount = damageCap.apply(this, source, amount, isCoreExposed());
        if (amount <= 0.0F) return false;
        return super.hurt(source, amount);
    }

    @Override
    public void die(DamageSource cause) {
        if (!level().isClientSide() && !customDeathHandled) {
            customDeathHandled = true;
            transitionTo(STATE_DEATH, ANIM_DEATH);
            if (level() instanceof ServerLevel sl) {
                sl.playSound(null, getX(), getY(), getZ(), SoundEvents.BREEZE_DEATH, SoundSource.HOSTILE, 1.5F, 0.75F);
                sl.playSound(null, getX(), getY(), getZ(), SoundEvents.AMETHYST_CLUSTER_BREAK, SoundSource.HOSTILE, 1.6F, 0.85F);
                sl.sendParticles(ParticleTypes.GUST_EMITTER_LARGE, getX(), getY() + 1.5D, getZ(), 3, 0.8D, 0.8D, 0.8D, 0.0D);
                sl.sendParticles(ParticleTypes.END_ROD, getX(), getY() + 1.5D, getZ(), 75, 2.2D, 1.8D, 2.2D, 0.14D);
                if (!cause.is(net.minecraft.tags.DamageTypeTags.BYPASSES_INVULNERABILITY)) {
                    Vec3 spawnPos = arenaInit ? arenaCenter : position();
                    TrounceBlossomEntity.spawn(sl, BossRespawnAnchorEntity.BOSS_ANEMO_HYPOSTASIS, spawnPos, savedSpawnYaw);
                }
            }
        }
        super.die(cause);
    }

    private void awaken(ServerLevel sl) {
        StavesElementBridge.setInnateElement(this, "ANEMO");
        transitionTo(STATE_AWAKEN, ANIM_AWAKEN);
        sl.playSound(null, getX(), getY(), getZ(), SoundEvents.BEACON_ACTIVATE, SoundSource.HOSTILE, 1.4F, 1.25F);
        sl.playSound(null, getX(), getY(), getZ(), SoundEvents.BREEZE_WIND_CHARGE_BURST.value(), SoundSource.HOSTILE, 1.3F, 0.9F);
        sl.sendParticles(ParticleTypes.END_ROD, getX(), getY() + 1.5D, getZ(), 45, 1.8D, 1.5D, 1.8D, 0.12D);
    }

    private void enterRevivalPhase(ServerLevel sl) {
        revivalUsed = true;
        transitionTo(STATE_REVIVAL, ANIM_REVIVAL);
        moveTarget = arenaCenter;
        revivalCrystalsRemaining = 4;

        sl.playSound(null, getX(), getY(), getZ(), SoundEvents.BEACON_POWER_SELECT, SoundSource.HOSTILE, 1.5F, 1.35F);
        sl.playSound(null, getX(), getY(), getZ(), SoundEvents.BREEZE_WIND_CHARGE_BURST.value(), SoundSource.HOSTILE, 1.4F, 0.85F);

        // Broadcast Genshin-accurate revival prompt to all nearby players
        for (Player p : sl.getEntitiesOfClass(Player.class, getBoundingBox().inflate(36.0D))) {
            p.displayClientMessage(Component.translatable("message.velorise_teyvat_bosses.anemo_hypostasis.revival")
                    .withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD), true);
        }

        // Spawn 4 airborne Wind Crystals + 4 Updraft Wind Currents symmetrically around arena!
        for (int i = 0; i < 4; i++) {
            double ang = (Math.PI * 0.5D * i) + Math.PI * 0.25D;
            double ox = arenaCenter.x + Math.cos(ang) * 9.5D;
            double oz = arenaCenter.z + Math.sin(ang) * 9.5D;
            AnemoWindOrbEntity.spawn(sl, ox, arenaCenter.y + 4.4D, oz, getUUID(), true, 340);

            double wx = arenaCenter.x + Math.cos(ang + 0.35D) * 7.5D;
            double wz = arenaCenter.z + Math.sin(ang + 0.35D) * 7.5D;
            WindCurrentEntity.spawn(sl, wx, arenaCenter.y, wz, 2.7F, 9.5F, 320);
        }
    }

    public void onOrbAbsorbedByPlayer(AnemoWindOrbEntity orb) {
        if (orb.isRevivalCrystal() && getBossState() == STATE_REVIVAL) {
            revivalCrystalsRemaining = Math.max(0, revivalCrystalsRemaining - 1);
            if (revivalCrystalsRemaining == 0 && level() instanceof ServerLevel sl) {
                for (Player p : sl.getEntitiesOfClass(Player.class, getBoundingBox().inflate(36.0D))) {
                    p.displayClientMessage(Component.translatable("message.velorise_teyvat_bosses.anemo_hypostasis.revival_prevented")
                            .withStyle(ChatFormatting.GREEN), true);
                }
                // All 4 absorbed -> immediately drop vulnerable at low HP for finishing blow!
                transitionTo(STATE_VULNERABLE, ANIM_VULNERABLE);
            }
        }
    }

    public void onOrbRecalledToBoss(AnemoWindOrbEntity orb) {
        if (!(level() instanceof ServerLevel sl)) return;
        if (orb.isRevivalCrystal()) {
            // Each unabsorbed Wind Crystal heals 12% Max HP!
            heal(getMaxHealth() * 0.12F);
            sl.playSound(null, getX(), getY(), getZ(), SoundEvents.BEACON_ACTIVATE, SoundSource.HOSTILE, 1.1F, 1.55F);
            sl.sendParticles(ParticleTypes.HAPPY_VILLAGER, getX(), getY() + 1.8D, getZ(), 18, 1.1D, 1.1D, 1.1D, 0.08D);
        } else {
            // Tornado Anemo Orb recalled -> bonus shockwave!
            pendingRecallOrbs++;
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!arenaInit) {
            arenaCenter = position();
            savedSpawnYaw = getYRot();
            arenaInit = true;
        }
        // Arena Soft-Leash: prevent kiting Anemo Hypostasis far outside its arena
        if (arenaInit && isAlive() && getBossState() == STATE_COMBAT) {
            double hDist = Math.hypot(getX() - arenaCenter.x, getZ() - arenaCenter.z);
            if (hDist > ARENA_SOFT_LEASH_RADIUS) {
                Vec3 back = new Vec3(arenaCenter.x - getX(), (arenaCenter.y + 2.4D) - getY(), arenaCenter.z - getZ());
                if (hDist > ARENA_SOFT_LEASH_RADIUS + 20.0D) {
                    teleportTo(arenaCenter.x, arenaCenter.y + 2.4D, arenaCenter.z);
                    setDeltaMovement(Vec3.ZERO);
                } else if (back.lengthSqr() > 1.0E-4D) {
                    setDeltaMovement(back.normalize().scale(0.55D));
                }
            }
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
        if (!innateAnemoSynced) {
            innateAnemoSynced = true;
            StavesElementBridge.setInnateElement(this, "ANEMO");
        }
        stateTick++;
        setDeltaMovement(Vec3.ZERO);

        // Smooth glide toward moveTarget if set
        if (moveTarget != null) {
            Vec3 delta = moveTarget.subtract(position());
            double d = delta.length();
            if (d > 0.18D) {
                Vec3 step = delta.scale(Math.min(0.45D, d) / d);
                setPos(getX() + step.x, arenaCenter.y, getZ() + step.z);
            } else {
                setPos(moveTarget.x, arenaCenter.y, moveTarget.z);
                moveTarget = null;
            }
        }

        int st = getBossState();
        if (st == STATE_DEATH) {
            bossEvent.setVisible(false);
            return;
        }

        LivingEntity nearest = findCombatTarget(sl);

        if (st == STATE_DORMANT) {
            bossEvent.setVisible(false);
            if (nearest != null && nearest.distanceToSqr(this) <= 10.5D * 10.5D) {
                setTarget(nearest);
                awaken(sl);
            }
            return;
        }

        bossEvent.setVisible(true);
        updateBossBar();

        if (nearest == null) {
            if (++noTargetTicks > 180) {
                noTargetTicks = 0;
                setPos(arenaCenter.x, arenaCenter.y, arenaCenter.z);
                setHealth(getMaxHealth());
                revivalUsed = false;
                transitionTo(STATE_DORMANT, ANIM_DORMANT);
            }
            return;
        }
        noTargetTicks = 0;
        setTarget(nearest);
        faceTarget(nearest);

        switch (st) {
            case STATE_AWAKEN -> {
                if (stateTick >= 48) {
                    transitionTo(STATE_COMBAT, ANIM_IDLE);
                    attackCooldown = 20;
                }
            }
            case STATE_VULNERABLE -> {
                if (stateTick >= 125) {
                    sl.playSound(null, getX(), getY(), getZ(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.HOSTILE, 1.0F, 0.95F);
                    transitionTo(STATE_COMBAT, ANIM_IDLE);
                    attackCooldown = 28;
                }
            }
            case STATE_REVIVAL -> tickRevival(sl);
            case STATE_COMBAT -> tickCombat(sl, nearest);
        }
    }

    private void faceTarget(LivingEntity target) {
        double dx = target.getX() - getX();
        double dz = target.getZ() - getZ();
        float targetYaw = (float) (Math.atan2(dz, dx) * (180.0D / Math.PI)) - 90.0F;
        float newYaw = Mth.approachDegrees(getYRot(), targetYaw, 8.5F);
        setYRot(newYaw);
        yBodyRot = newYaw;
        yHeadRot = newYaw;
    }

    private void tickRevival(ServerLevel sl) {
        // After 14s (280 ticks), recall any surviving Wind Crystals
        if (stateTick == 280) {
            List<AnemoWindOrbEntity> orbs = sl.getEntitiesOfClass(AnemoWindOrbEntity.class,
                    getBoundingBox().inflate(32.0D), o -> o.isRevivalCrystal() && getUUID().equals(o.getOwnerUuid()));
            for (AnemoWindOrbEntity o : orbs) {
                o.startRecall(position().add(0.0D, 2.2D, 0.0D));
            }
        }
        if (stateTick >= 335) {
            // If healed by crystals, allow another revival if player didn't finish; otherwise back to combat
            transitionTo(STATE_COMBAT, ANIM_IDLE);
            attackCooldown = 25;
        }
    }

    private void tickCombat(ServerLevel sl, LivingEntity target) {
        int anim = getAnimId();
        if (anim == ANIM_IDLE) {
            // Gently float near arena center
            if (position().distanceToSqr(arenaCenter) > 100.0D && moveTarget == null) {
                moveTarget = arenaCenter;
            }
            if (--attackCooldown <= 0) {
                chooseNextAttack(sl, target);
            }
            return;
        }

        switch (anim) {
            case ANIM_BUTTERFLY_SWEEP -> tickButterflySweep(sl, target);
            case ANIM_MISSILE_CIRCLE -> tickMissileCircle(sl, target);
            case ANIM_PILLAR_ERUPTION -> tickPillarEruption(sl, target);
            case ANIM_TORNADO_STORM -> tickTornadoStorm(sl);
            case ANIM_CLAP_SLAM -> tickClapSlam(sl, target);
            case ANIM_VACUUM_IMPLOSION -> tickVacuumImplosion(sl);
            default -> transitionTo(STATE_COMBAT, ANIM_IDLE);
        }
    }

    private void chooseNextAttack(ServerLevel sl, LivingEntity target) {
        attacksSinceTornado++;
        int chosen;
        if (attacksSinceTornado >= 4) {
            chosen = ANIM_TORNADO_STORM;
            attacksSinceTornado = 0;
        } else {
            int[] pool = {ANIM_BUTTERFLY_SWEEP, ANIM_MISSILE_CIRCLE, ANIM_PILLAR_ERUPTION, ANIM_CLAP_SLAM, ANIM_VACUUM_IMPLOSION};
            chosen = pool[random.nextInt(pool.length)];
            if (chosen == lastAttackAnim) {
                chosen = pool[(random.nextInt(pool.length - 1) + 1) % pool.length];
            }
        }
        lastAttackAnim = chosen;

        if (chosen == ANIM_TORNADO_STORM || chosen == ANIM_VACUUM_IMPLOSION) {
            moveTarget = arenaCenter;
        } else if (chosen == ANIM_CLAP_SLAM) {
            // Glide close to target for the iconic box clap!
            Vec3 toTarget = target.position().subtract(arenaCenter);
            if (toTarget.length() > 13.0D) toTarget = toTarget.normalize().scale(13.0D);
            moveTarget = arenaCenter.add(toTarget.x, 0.0D, toTarget.z);
        }

        transitionTo(STATE_COMBAT, chosen);
        sl.playSound(null, getX(), getY() + 1.5D, getZ(), SoundEvents.BREEZE_INHALE, SoundSource.HOSTILE, 1.15F, 1.15F);
    }

    private void tickButterflySweep(ServerLevel sl, LivingEntity target) {
        if (stateTick == 26 || stateTick == 46 || stateTick == 66) {
            Vec3 origin = position().add(0.0D, 3.0D, 0.0D);
            Vec3 dir = target.position().add(0.0D, 0.9D, 0.0D).subtract(origin).normalize();
            AnemoMissileProjectile.launch(sl, this, origin, dir.scale(1.05D), 12.0F, AnemoMissileProjectile.KIND_WIND_BLADE);
            sl.playSound(null, origin.x, origin.y, origin.z, SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.HOSTILE, 1.4F, 0.85F);
            sl.playSound(null, origin.x, origin.y, origin.z, SoundEvents.BREEZE_SHOOT, SoundSource.HOSTILE, 1.2F, 1.05F);
        }
        if (stateTick >= 90) {
            transitionTo(STATE_VULNERABLE, ANIM_VULNERABLE);
        }
    }

    private void tickMissileCircle(ServerLevel sl, LivingEntity target) {
        if (stateTick >= 36 && stateTick <= 116 && (stateTick - 36) % 16 == 0) {
            Vec3 origin = position().add(0.0D, 3.2D, 0.0D);
            Vec3 dir = target.position().add(0.0D, 0.85D, 0.0D).subtract(origin).normalize();
            AnemoMissileProjectile.launch(sl, this, origin, dir.scale(1.22D), 9.5F, AnemoMissileProjectile.KIND_ORB_BOLT);
            sl.playSound(null, origin.x, origin.y, origin.z, SoundEvents.BREEZE_SHOOT, SoundSource.HOSTILE, 1.15F, 1.35F);
        }
        if (stateTick >= 140) {
            transitionTo(STATE_VULNERABLE, ANIM_VULNERABLE);
        }
    }

    private void tickPillarEruption(ServerLevel sl, LivingEntity target) {
        // 3 sequential ground telegraphs -> eruptions
        if (stateTick == 12 || stateTick == 42 || stateTick == 72) {
            setTelegraph(new Vec3(target.getX(), arenaCenter.y, target.getZ()));
            sl.playSound(null, telegraphPos.x, telegraphPos.y, telegraphPos.z, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.HOSTILE, 1.0F, 1.45F);
        }
        if (stateTick == 30 || stateTick == 60 || stateTick == 90) {
            entityData.set(DATA_TELEGRAPH_ACTIVE, false);
            sl.playSound(null, telegraphPos.x, telegraphPos.y, telegraphPos.z, SoundEvents.BREEZE_WIND_CHARGE_BURST.value(), SoundSource.HOSTILE, 1.4F, 0.85F);
            sl.sendParticles(ParticleTypes.GUST_EMITTER_LARGE, telegraphPos.x, telegraphPos.y + 0.8D, telegraphPos.z, 2, 0.5D, 0.8D, 0.5D, 0.0D);
            sl.sendParticles(ParticleTypes.END_ROD, telegraphPos.x, telegraphPos.y + 1.2D, telegraphPos.z, 26, 1.4D, 1.8D, 1.4D, 0.12D);

            AABB blast = new AABB(telegraphPos.x - 3.2D, telegraphPos.y - 0.5D, telegraphPos.z - 3.2D,
                    telegraphPos.x + 3.2D, telegraphPos.y + 4.5D, telegraphPos.z + 3.2D);
            for (LivingEntity p : sl.getEntitiesOfClass(LivingEntity.class, blast, this::isValidCombatTarget)) {
                StavesElementBridge.dealElementalDamage(p, this, "ANEMO", 22.0F, 1.0F, false, 0.30F);
                p.setDeltaMovement(p.getDeltaMovement().x, 0.68D, p.getDeltaMovement().z);
                p.hurtMarked = true;
            }
        }
        if (stateTick >= 100) {
            transitionTo(STATE_COMBAT, ANIM_IDLE);
            attackCooldown = 25;
        }
    }

    private void tickTornadoStorm(ServerLevel sl) {
        if (stateTick == 35) {
            sl.playSound(null, getX(), getY() + 3.0D, getZ(), SoundEvents.BREEZE_WHIRL, SoundSource.HOSTILE, 1.6F, 0.75F);
            // Spawn 6 roaming Hurricane Tornadoes + 3 Updraft Wind Currents!
            for (int i = 0; i < 6; i++) {
                float ang = (float) (i * Math.PI / 3.0D);
                float rad = (i % 2 == 0) ? 7.5F : 12.5F;
                float spd = (i % 2 == 0) ? 0.048F : -0.036F;
                AnemoTornadoEntity.spawn(sl, arenaCenter, getUUID(), rad, ang, spd, 215, true);
            }
            for (int i = 0; i < 3; i++) {
                double ang = i * (Math.PI * 2.0D / 3.0D) + 0.5D;
                WindCurrentEntity.spawn(sl, arenaCenter.x + Math.cos(ang) * 10.5D, arenaCenter.y,
                        arenaCenter.z + Math.sin(ang) * 10.5D, 2.5F, 9.0F, 250);
            }
        }
        if (stateTick >= 280) {
            transitionTo(STATE_COMBAT, ANIM_IDLE);
            attackCooldown = 30;
        }
    }

    private void tickClapSlam(ServerLevel sl, LivingEntity target) {
        // Snap impact at tick 38 (1.9s)
        if (stateTick == 38) {
            sl.playSound(null, getX(), getY() + 1.5D, getZ(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 1.35F, 0.95F);
            sl.playSound(null, getX(), getY() + 1.5D, getZ(), SoundEvents.BREEZE_WIND_CHARGE_BURST.value(), SoundSource.HOSTILE, 1.5F, 0.75F);
            sl.sendParticles(ParticleTypes.GUST_EMITTER_LARGE, getX(), getY() + 1.8D, getZ(), 3, 1.2D, 1.0D, 1.2D, 0.0D);
            sl.sendParticles(ParticleTypes.END_ROD, getX(), getY() + 1.6D, getZ(), 40, 2.4D, 1.5D, 2.4D, 0.14D);

            AABB crushBox = new AABB(getX() - 4.0D, getY() - 0.5D, getZ() - 4.0D,
                    getX() + 4.0D, getY() + 4.2D, getZ() + 4.0D);
            for (LivingEntity p : sl.getEntitiesOfClass(LivingEntity.class, crushBox, this::isValidCombatTarget)) {
                StavesElementBridge.dealElementalDamage(p, this, "ANEMO", 30.0F, 1.5F, true, 0.35F);
                Vec3 push = p.position().subtract(position()).normalize().scale(1.1D);
                p.setDeltaMovement(push.x, 0.45D, push.z);
                p.hurtMarked = true;
            }
        }
        if (stateTick >= 90) {
            transitionTo(STATE_VULNERABLE, ANIM_VULNERABLE);
        }
    }

    private void tickVacuumImplosion(ServerLevel sl) {
        if (stateTick == 14) {
            // Recall any leftover Tornado Anemo Orbs in the arena!
            List<AnemoWindOrbEntity> orbs = sl.getEntitiesOfClass(AnemoWindOrbEntity.class,
                    getBoundingBox().inflate(28.0D), o -> !o.isRevivalCrystal() && getUUID().equals(o.getOwnerUuid()));
            for (AnemoWindOrbEntity o : orbs) {
                o.startRecall(position().add(0.0D, 2.0D, 0.0D));
            }
        }
        // Continuous suction pull during ticks 12..68
        if (stateTick >= 12 && stateTick <= 68) {
            AABB pullArea = new AABB(getX() - 14.0D, getY() - 2.0D, getZ() - 14.0D,
                    getX() + 14.0D, getY() + 6.0D, getZ() + 14.0D);
            for (LivingEntity p : sl.getEntitiesOfClass(LivingEntity.class, pullArea, this::isValidCombatTarget)) {
                Vec3 toBoss = position().subtract(p.position());
                double dist = toBoss.length();
                if (dist > 2.4D && dist < 14.0D) {
                    Vec3 pull = toBoss.scale(0.11D / dist);
                    p.setDeltaMovement(p.getDeltaMovement().add(pull.x, 0.0D, pull.z));
                    p.hurtMarked = true;
                }
            }
            if (stateTick % 12 == 0) {
                sl.playSound(null, getX(), getY() + 1.5D, getZ(), SoundEvents.BREEZE_INHALE, SoundSource.HOSTILE, 0.9F, 0.85F);
            }
        }
        // Main shockwave at tick 70 (+ bonus waves from recalled orbs at ticks 88, 106)
        if (stateTick == 70 || (pendingRecallOrbs > 0 && (stateTick == 88 || stateTick == 106))) {
            if (stateTick > 70) pendingRecallOrbs--;
            sl.playSound(null, getX(), getY() + 1.5D, getZ(), SoundEvents.BREEZE_WIND_CHARGE_BURST.value(), SoundSource.HOSTILE, 1.6F, 0.72F);
            sl.sendParticles(ParticleTypes.GUST_EMITTER_LARGE, getX(), getY() + 1.5D, getZ(), 4, 2.5D, 1.0D, 2.5D, 0.0D);
            AABB blast = new AABB(getX() - 8.5D, getY() - 1.0D, getZ() - 8.5D, getX() + 8.5D, getY() + 5.0D, getZ() + 8.5D);
            for (LivingEntity p : sl.getEntitiesOfClass(LivingEntity.class, blast, this::isValidCombatTarget)) {
                StavesElementBridge.dealElementalDamage(p, this, "ANEMO", 24.5F, 1.0F, false, 0.30F);
                Vec3 push = p.position().subtract(position()).normalize().scale(1.25D);
                p.setDeltaMovement(push.x, 0.52D, push.z);
                p.hurtMarked = true;
            }
        }
        if (stateTick >= 130) {
            pendingRecallOrbs = 0;
            transitionTo(STATE_VULNERABLE, ANIM_VULNERABLE);
        }
    }

    private boolean isValidCombatTarget(LivingEntity le) {
        if (le == null || !le.isAlive() || le == this || le instanceof AnemoHypostasisEntity) return false;
        if (le instanceof Player p && (p.isCreative() || p.isSpectator())) return false;
        return le.distanceToSqr(this) <= 34.0D * 34.0D;
    }

    private LivingEntity findCombatTarget(ServerLevel sl) {
        Player nearestPlayer = sl.getNearestPlayer(getX(), getY(), getZ(), 32.0D,
                e -> e instanceof Player p && p.isAlive() && !p.isCreative() && !p.isSpectator());
        if (nearestPlayer != null) return nearestPlayer;
        if (isValidCombatTarget(getTarget())) return getTarget();
        if (isValidCombatTarget(getLastHurtByMob())) return getLastHurtByMob();
        List<Mob> aggroMobs = sl.getEntitiesOfClass(Mob.class, getBoundingBox().inflate(28.0D),
                m -> isValidCombatTarget(m) && m.getTarget() == this);
        return aggroMobs.isEmpty() ? null : aggroMobs.get(0);
    }

    private void updateBossBar() {
        bossEvent.setProgress(Mth.clamp(getHealth() / getMaxHealth(), 0.0F, 1.0F));
        Component base = Component.translatable("entity.velorise_teyvat_bosses.anemo_hypostasis");
        int st = getBossState();
        if (st == STATE_VULNERABLE) {
            bossEvent.setColor(BossEvent.BossBarColor.WHITE);
            bossEvent.setName(base.copy().append(Component.literal(" — Core Exposed")));
        } else if (st == STATE_REVIVAL) {
            bossEvent.setColor(BossEvent.BossBarColor.BLUE);
            bossEvent.setName(base.copy().append(Component.literal(String.format(Locale.ROOT, " — Revival [%d Crystals]", revivalCrystalsRemaining))));
        } else {
            bossEvent.setColor(BossEvent.BossBarColor.GREEN);
            bossEvent.setName(base.copy().append(Component.literal(" — Beth")));
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
        tag.putBoolean("RevivalUsed", revivalUsed);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.getBoolean("ArenaInit")) {
            arenaInit = true;
            arenaCenter = new Vec3(tag.getDouble("AX"), tag.getDouble("AY"), tag.getDouble("AZ"));
            savedSpawnYaw = tag.getFloat("SavedYaw");
        }
        revivalUsed = tag.getBoolean("RevivalUsed");
    }
}

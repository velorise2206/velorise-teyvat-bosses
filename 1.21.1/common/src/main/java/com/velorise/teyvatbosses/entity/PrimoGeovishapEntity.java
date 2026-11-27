package com.velorise.teyvatbosses.entity;

import com.velorise.teyvatbosses.config.TeyvatBossesConfig;

import com.velorise.teyvatbosses.TeyvatBossesConstants;
import com.velorise.teyvatbosses.client.BossCameraShake;
import com.velorise.teyvatbosses.client.BossMusicManager;
import com.velorise.teyvatbosses.compat.StavesElementBridge;
import com.velorise.teyvatbosses.registry.TeyvatBossesSounds;
import com.velorise.teyvatbosses.entity.ai.BossGroundNavigation;
import com.velorise.teyvatbosses.entity.ai.BossReturnHomeHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
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
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * AAA Geo World Boss: <b>Primo Geovishap</b> — Long-Slumbering Earthshaker Dragon.
 *
 * <p>Canonical Genshin Impact combat AI, scale (~5.1 blocks tall on all fours, ~15.0 blocks long),
 * per-skill cooldowns, angle-aware anti-flank Whirlwind (ignores rest interval when players run behind),
 * Near-Death (&le;20% HP) Primeval Spring phase shift, delayed Elemental-Infused Stones with all 4
 * canonical elemental detonation patterns (Pyro lava pool, Hydro multi-eruption, Cryo 3-wave ring,
 * Electro X-cross), predictive target leading, Left Claw 2-hit shockwave, and signature
 * <b>Primordial Shower</b> shield-counter reflection (15% Max HP elemental / 10% vanilla shield,
 * 5.0s stagger with +40% vulnerability).
 */
public class PrimoGeovishapEntity extends Monster {

    public static final ResourceLocation BOSS_MUSIC_ID = TeyvatBossesConstants.id("music.primo_geovishap_theme");

    // Boss states
    public static final int STATE_DORMANT = 0;
    public static final int STATE_AWAKEN  = 1;
    public static final int STATE_COMBAT  = 2;
    public static final int STATE_STAGGER = 3;
    public static final int STATE_DEATH   = 4;

    // 14 synced animation IDs matching PrimoGeovishapAnimations
    public static final int ANIM_IDLE              = 0;
    public static final int ANIM_WALK              = 1;
    public static final int ANIM_SPAWN_AWAKEN      = 2;
    public static final int ANIM_LEFT_CLAW         = 3;
    public static final int ANIM_RIGHT_CLAW        = 4;
    public static final int ANIM_COMBO_SLASH       = 5;
    public static final int ANIM_WHIRLWIND         = 6;
    public static final int ANIM_BURROW            = 7;
    public static final int ANIM_SCATTERED_ROCKS   = 8;
    public static final int ANIM_ELEMENTAL_BREATH  = 9;
    public static final int ANIM_PRIMORDIAL_SHOWER = 10;
    public static final int ANIM_PRIMEVAL_SPRING   = 11;
    public static final int ANIM_COUNTERED_STAGGER = 12;
    public static final int ANIM_DEATH             = 13;
    public static final int ANIM_DORMANT           = 14;
    public static final int ANIM_REAR_TAIL_SLAM    = 15;
    public static final int ANIM_VERTICAL_TAIL_CLEAVE = 16;

    // Elemental infusions (matches Genshin Primo Geovishap variants)
    public static final int INFUSION_PYRO    = 0;
    public static final int INFUSION_HYDRO   = 1;
    public static final int INFUSION_ELECTRO = 2;
    public static final int INFUSION_CRYO    = 3;

    private static final EntityDataAccessor<Integer> DATA_STATE =
            SynchedEntityData.defineId(PrimoGeovishapEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_ANIM =
            SynchedEntityData.defineId(PrimoGeovishapEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_ANIM_TICK =
            SynchedEntityData.defineId(PrimoGeovishapEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_ANIM_EPOCH =
            SynchedEntityData.defineId(PrimoGeovishapEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_INFUSION =
            SynchedEntityData.defineId(PrimoGeovishapEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DATA_TELEGRAPH_RADIUS =
            SynchedEntityData.defineId(PrimoGeovishapEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<CompoundTag> DATA_STONES_NBT =
            SynchedEntityData.defineId(PrimoGeovishapEntity.class, EntityDataSerializers.COMPOUND_TAG);
    private static final EntityDataAccessor<CompoundTag> DATA_COUNTER_BEAM =
            SynchedEntityData.defineId(PrimoGeovishapEntity.class, EntityDataSerializers.COMPOUND_TAG);

    private final ServerBossEvent bossEvent = new ServerBossEvent(
            Component.translatable("entity.velorise_teyvat_bosses.primo_geovishap"),
            BossEvent.BossBarColor.YELLOW,
            BossEvent.BossBarOverlay.NOTCHED_10
    );

    // Canonical Genshin per-skill cooldowns (in ticks, 20 ticks = 1s)
    private int stateTick = 0;
    private int attackCooldown = 28;         // Brisk Genshin action interval (~1.4–2.1s)
    private int showerCooldown = 260;        // Initial 13s CD (canonical Genshin 12-15s)
    private int springCooldown = 0;          // Near-death (<=20% HP) Primeval Spring CD (20-30s)
    private int whirlwindCooldown = 0;       // Anti-flank Whirlwind CD (5s)
    private int burrowCooldown = 120;        // Burrow Close (16s) / Far (6s)
    private int breathCooldown = 140;        // Elemental Breath CD (14-20s)
    private int comboCooldown = 100;         // Combo Slash CD (11-16s)
    private int scatteredCooldown = 80;      // Scattered Rocks CD (9-13s)
    private int rearTailSlamCooldown = 0;    // Anti-flank Rear Tail Slam CD (5s)
    private int verticalCleaveCooldown = 120;// Mid-range Vertical Tail Cleave CD (11-15s)

    private int lastAttackAnim = -1;
    private int noTargetTicks = 0;
    private boolean showerCountered = false;
    private boolean spawnedBlossom = false;
    private final BossDamageCap damageCap = new BossDamageCap();
    private static final double ARENA_SOFT_LEASH_RADIUS = 54.0D;
    private Vec3 homePos = null;
    private float savedSpawnYaw = 0.0F;
    private boolean infusionInitialized = false;
    private int clientAnimTick = 0;
    private int lastSyncedAnim = -1;
    private int lastSyncedEpoch = -1;
    private final BossReturnHomeHelper returnHelper = new BossReturnHomeHelper();
    private Vec3 burrowStart = Vec3.ZERO;
    private Vec3 burrowDest = Vec3.ZERO;
    private int destroyBlocksTick = 0;

    /** Server-side delayed Elemental-Infused Stones & Pyro lava pools (canonical Genshin hazards). */
    private final List<InfusedStoneHazard> activeStones = new ArrayList<>();
    private final List<LavaPoolHazard> activeLavaPools = new ArrayList<>();

    private static final class InfusedStoneHazard {
        final Vec3 pos;
        int fuseTicks;
        final int infusion;

        InfusedStoneHazard(Vec3 pos, int fuseTicks, int infusion) {
            this.pos = pos;
            this.fuseTicks = fuseTicks;
            this.infusion = infusion;
        }
    }

    private static final class LavaPoolHazard {
        final Vec3 pos;
        int lifeTicks;

        LavaPoolHazard(Vec3 pos, int lifeTicks) {
            this.pos = pos;
            this.lifeTicks = lifeTicks;
        }
    }

    public PrimoGeovishapEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        TeyvatBossesConfig.applyBossScaling(this, "primo_geovishap");
        this.xpReward = 240;
        this.setPersistenceRequired();
        this.bossEvent.setVisible(false);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 680.0D)
                .add(Attributes.ARMOR, 10.0D)
                .add(Attributes.ARMOR_TOUGHNESS, 6.0D)
                .add(Attributes.ATTACK_DAMAGE, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.STEP_HEIGHT, 1.85D)
                .add(Attributes.FOLLOW_RANGE, 44.0D);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_STATE, STATE_DORMANT);
        builder.define(DATA_ANIM, ANIM_DORMANT);
        builder.define(DATA_ANIM_TICK, 0);
        builder.define(DATA_ANIM_EPOCH, 0);
        builder.define(DATA_INFUSION, INFUSION_PYRO);
        builder.define(DATA_TELEGRAPH_RADIUS, 0.0F);
        builder.define(DATA_STONES_NBT, new CompoundTag());
        builder.define(DATA_COUNTER_BEAM, new CompoundTag());
    }

    public int getBossState() { return entityData.get(DATA_STATE); }
    public int getAnimId() { return entityData.get(DATA_ANIM); }
    public int getAnimTick() {
        if (level().isClientSide()) {
            // Prevent 1-frame stale-tick VFX flashes when SetEntityData arrives before client tick() runs!
            if (getAnimId() != lastSyncedAnim || entityData.get(DATA_ANIM_EPOCH) != lastSyncedEpoch) {
                return 0;
            }
            return clientAnimTick;
        }
        return stateTick;
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (level().isClientSide() && (DATA_ANIM.equals(key) || DATA_ANIM_EPOCH.equals(key))) {
            int curA = getAnimId();
            int curE = entityData.get(DATA_ANIM_EPOCH);
            if (curA != lastSyncedAnim || curE != lastSyncedEpoch) {
                lastSyncedAnim = curA;
                lastSyncedEpoch = curE;
                clientAnimTick = 0;
            }
        }
    }
    public int getInfusion() { return entityData.get(DATA_INFUSION); }
    public float getTelegraphRadius() { return entityData.get(DATA_TELEGRAPH_RADIUS); }
    public CompoundTag getStonesTag() { return entityData.get(DATA_STONES_NBT); }
    public CompoundTag getCounterBeamTag() { return entityData.get(DATA_COUNTER_BEAM); }

    public boolean isNearDeathPhase() {
        return getHealth() <= getMaxHealth() * 0.20F;
    }

    public String getInfusionElementName() {
        return switch (getInfusion()) {
            case INFUSION_HYDRO -> "HYDRO";
            case INFUSION_ELECTRO -> "ELECTRO";
            case INFUSION_CRYO -> "CRYO";
            default -> "PYRO";
        };
    }

    public int[] getInfusionRgb() {
        return switch (getInfusion()) {
            case INFUSION_HYDRO -> new int[]{40, 190, 255};
            case INFUSION_ELECTRO -> new int[]{200, 75, 255};
            case INFUSION_CRYO -> new int[]{135, 238, 255};
            default -> new int[]{255, 115, 35};
        };
    }

    private void syncBossBarStyle() {
        BossEvent.BossBarColor col = switch (getInfusion()) {
            case INFUSION_HYDRO -> BossEvent.BossBarColor.BLUE;
            case INFUSION_ELECTRO -> BossEvent.BossBarColor.PURPLE;
            case INFUSION_CRYO -> BossEvent.BossBarColor.WHITE;
            default -> BossEvent.BossBarColor.RED;
        };
        bossEvent.setColor(col);
        String elemLabel = switch (getInfusion()) {
            case INFUSION_HYDRO -> "Hydro";
            case INFUSION_ELECTRO -> "Electro";
            case INFUSION_CRYO -> "Cryo";
            default -> "Pyro";
        };
        bossEvent.setName(Component.translatable("entity.velorise_teyvat_bosses.primo_geovishap")
                .append(Component.literal(" · " + elemLabel)));
    }

    public static String clipNameForAnim(int animId) {
        return switch (animId) {
            case ANIM_WALK -> "walk";
            case ANIM_SPAWN_AWAKEN -> "spawn_awaken";
            case ANIM_LEFT_CLAW -> "attack_left_claw";
            case ANIM_RIGHT_CLAW -> "attack_right_claw";
            case ANIM_COMBO_SLASH -> "attack_combo_slash";
            case ANIM_WHIRLWIND -> "attack_whirlwind";
            case ANIM_BURROW -> "attack_burrow";
            case ANIM_SCATTERED_ROCKS -> "attack_scattered_rocks";
            case ANIM_ELEMENTAL_BREATH -> "attack_elemental_breath";
            case ANIM_PRIMORDIAL_SHOWER -> "attack_primordial_shower";
            case ANIM_PRIMEVAL_SPRING -> "attack_primeval_spring";
            case ANIM_COUNTERED_STAGGER -> "countered_stagger";
            case ANIM_DEATH -> "death";
            case ANIM_DORMANT -> "dormant_sleep";
            case ANIM_REAR_TAIL_SLAM -> "attack_rear_tail_slam";
            case ANIM_VERTICAL_TAIL_CLEAVE -> "attack_vertical_tail_cleave";
            default -> "idle";
        };
    }

    @Override
    protected void registerGoals() {
        // Fully deterministic canonical boss state machine — no vanilla Wander/Melee goals
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new BossGroundNavigation(this, level);
    }

    @Override
    public boolean removeWhenFarAway(double distSq) { return false; }

    @Override
    public boolean isPushable() { return false; }

    @Override
    public void push(Entity entity) {}

    @Override
    public void push(double x, double y, double z) {}

    @Override
    public void knockback(double strength, double x, double z) {}

    @Override
    public boolean ignoreExplosion(net.minecraft.world.level.Explosion explosion) { return true; }

    @Override
    protected boolean canRide(Entity vehicle) { return false; }

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

    private void transitionTo(int newState, int newAnim) {
        entityData.set(DATA_STATE, newState);
        entityData.set(DATA_ANIM, newAnim);
        entityData.set(DATA_ANIM_TICK, 0);
        entityData.set(DATA_ANIM_EPOCH, entityData.get(DATA_ANIM_EPOCH) + 1);
        entityData.set(DATA_TELEGRAPH_RADIUS, 0.0F);
        stateTick = 0;
        showerCountered = false;
        this.noPhysics = false;
        this.setNoGravity(false);
    }

    private boolean isValidCombatTarget(LivingEntity le) {
        if (le == null || !le.isAlive() || le == this || le instanceof PrimoGeovishapEntity) return false;
        if (le instanceof Player p && (p.isCreative() || p.isSpectator())) return false;
        return le.distanceToSqr(this) <= 44.0D * 44.0D;
    }

    private LivingEntity resolveAttacker(DamageSource source) {
        Entity direct = source.getEntity();
        if (direct instanceof LivingEntity le) {
            if (isValidCombatTarget(le)) return le;
            if (le.getVehicle() instanceof LivingEntity veh && isValidCombatTarget(veh)) return veh;
        }
        Entity immediate = source.getDirectEntity();
        if (immediate instanceof LivingEntity le && isValidCombatTarget(le)) {
            return le;
        }
        return null;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        int st = getBossState();
        if (st == STATE_DEATH) return false;
        if (returnHelper.isHardReset()) return false;
        if (!level().isClientSide() && returnHelper.isActive()) {
            LivingEntity atk = resolveAttacker(source);
            if (atk != null && isValidCombatTarget(atk)) {
                returnHelper.reset();
                setTarget(atk);
            }
        }
        // Invulnerable while deep underground during burrow
        if (st == STATE_COMBAT && getAnimId() == ANIM_BURROW && stateTick >= 24 && stateTick <= 56) {
            return false;
        }
        if (!level().isClientSide()) {
            LivingEntity attacker = resolveAttacker(source);
            if (attacker != null) {
                setLastHurtByMob(attacker);
                noTargetTicks = 0;
                if (st == STATE_DORMANT || !isValidCombatTarget(getTarget())) {
                    setTarget(attacker);
                }
            }
        }
        if (st == STATE_DORMANT && level() instanceof ServerLevel sl) {
            awaken(sl);
            amount *= 0.25F; // High opening RES while dormant
        } else if (st == STATE_AWAKEN) {
            // Canonical Genshin Beginning-of-Fight high RES (~210-250%) during wake-up roar
            amount *= 0.30F;
        } else if (st == STATE_STAGGER) {
            // Canonical -50% all-RES countered stagger vulnerability window!
            amount *= 1.40F;
        } else if (st == STATE_COMBAT && getAnimId() == ANIM_PRIMORDIAL_SHOWER) {
            // Geo poise during ultimate charge
            amount *= 0.80F;
        }
        if (!level().isClientSide() && st == STATE_COMBAT && destroyBlocksTick <= 0 && (this.horizontalCollision || this.isInWall())) {
            destroyBlocksTick = 20;
        }
        amount = damageCap.apply(this, source, amount, st == STATE_STAGGER);
        if (amount <= 0.0F) return false;
        return super.hurt(source, amount);
    }

    @Override
    public void die(DamageSource source) {
        if (getBossState() != STATE_DEATH) {
            this.setHealth(1.0F);
            activeStones.clear();
            activeLavaPools.clear();
            transitionTo(STATE_DEATH, ANIM_DEATH);
            if (level() instanceof ServerLevel sl) {
                sl.playSound(null, getX(), getY() + 2.8D, getZ(), SoundEvents.ENDER_DRAGON_GROWL, SoundSource.HOSTILE, 2.0F, 0.52F);
            }
            return;
        }
        super.die(source);
    }

    @Override
    protected void tickDeath() {
        // Managed deterministically inside tick() for full 3.6s collapse animation
    }

    private void awaken(ServerLevel sl) {
        transitionTo(STATE_AWAKEN, ANIM_SPAWN_AWAKEN);
        sl.playSound(null, getX(), getY() + 2.8D, getZ(), SoundEvents.RAVAGER_ROAR, SoundSource.HOSTILE, 2.0F, 0.55F);
        sl.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.STONE.defaultBlockState()),
                getX(), getY() + 0.8D, getZ(), 48, 2.6D, 0.8D, 2.6D, 0.14D);
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide()) {
            int curA = getAnimId();
            int curE = entityData.get(DATA_ANIM_EPOCH);
            if (curA != lastSyncedAnim || curE != lastSyncedEpoch) {
                lastSyncedAnim = curA;
                lastSyncedEpoch = curE;
                clientAnimTick = 0;
            } else {
                clientAnimTick++;
            }
            BossMusicManager.update(this, BOSS_MUSIC_ID,
                    getBossState() == STATE_AWAKEN || getBossState() == STATE_COMBAT || getBossState() == STATE_STAGGER);
            triggerClientCameraShake();
            if (tickCount % 5 == 0 && getBossState() != STATE_DEATH) {
                ParticleOptions p = infusionParticle();
                level().addParticle(p,
                        getX() + (random.nextDouble() - 0.5D) * 4.2D,
                        getY() + 2.0D + random.nextDouble() * 2.2D,
                        getZ() + (random.nextDouble() - 0.5D) * 4.2D,
                        0.0D, 0.04D, 0.0D);
            }
            return;
        }

        ServerLevel sl = (ServerLevel) level();
        if (homePos == null) {
            homePos = position();
            savedSpawnYaw = getYRot();
        }
        if (!infusionInitialized) {
            entityData.set(DATA_INFUSION, random.nextInt(4));
            infusionInitialized = true;
        }

        stateTick++;
        entityData.set(DATA_ANIM_TICK, stateTick);
        tickCooldowns();
        tickElementalHazards(sl);

        int st = getBossState();
        if (st == STATE_DEATH) {
            setDeltaMovement(0.0D, getDeltaMovement().y, 0.0D);
            bossEvent.setVisible(false);
            if (stateTick == 26 || stateTick == 48) {
                sl.playSound(null, getX(), getY(), getZ(), sfx(TeyvatBossesSounds.primoDeath(), SoundEvents.GENERIC_EXPLODE.value()), SoundSource.HOSTILE, 1.6F, 0.95F);
                sl.sendParticles(ParticleTypes.POOF, getX(), getY() + 0.8D, getZ(), 32, 3.0D, 0.4D, 3.0D, 0.07D);
            }
            if (stateTick >= 72) {
                if (!spawnedBlossom) {
                    spawnedBlossom = true;
                    TrounceBlossomEntity.spawn(sl, BossRespawnAnchorEntity.BOSS_PRIMO_GEOVISHAP, position(), getYRot());
                }
                this.remove(Entity.RemovalReason.KILLED);
            }
            return;
        }

        LivingEntity target = findCombatTarget(sl);
        if (st == STATE_DORMANT) {
            bossEvent.setVisible(false);
            if (getAnimId() != ANIM_DORMANT) {
                entityData.set(DATA_ANIM, ANIM_DORMANT);
            }
            setDeltaMovement(0.0D, getDeltaMovement().y, 0.0D);
            if (target != null && target.distanceToSqr(this) <= 18.0D * 18.0D) {
                setTarget(target);
                awaken(sl);
            }
            return;
        }

        syncBossBarStyle();
        bossEvent.setVisible(true);
        bossEvent.setProgress(Mth.clamp(getHealth() / getMaxHealth(), 0.0F, 1.0F));

        if (st == STATE_AWAKEN) {
            setDeltaMovement(0.0D, getDeltaMovement().y, 0.0D);
            if (target != null) faceTargetSmooth(target, 4.5F);
            if (stateTick == 36) {
                sl.playSound(null, getX(), getY() + 3.0D, getZ(), sfx(TeyvatBossesSounds.primoRoar(), SoundEvents.ENDER_DRAGON_GROWL), SoundSource.HOSTILE, 2.0F, 0.95F);
                sl.sendParticles(ParticleTypes.POOF, getX(), getY() + 2.2D, getZ(), 42, 3.0D, 1.5D, 3.0D, 0.09D);
            } else if (stateTick == 59) {
                sl.playSound(null, getX(), getY(), getZ(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 1.5F, 0.55F);
            }
            if (stateTick >= 68) {
                attackCooldown = 20;
                transitionTo(STATE_COMBAT, ANIM_IDLE);
            }
            return;
        }

        if (st == STATE_STAGGER) {
            setDeltaMovement(getDeltaMovement().x * 0.7D, getDeltaMovement().y, getDeltaMovement().z * 0.7D);
            if (stateTick == 17) {
                sl.playSound(null, getX(), getY(), getZ(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 1.4F, 0.52F);
                sl.sendParticles(ParticleTypes.POOF, getX(), getY() + 0.6D, getZ(), 32, 3.0D, 0.4D, 3.0D, 0.08D);
            }
            // Canonical 5.0-second (100-tick) stagger window after Primordial Shower reflection
            if (stateTick >= 100) {
                attackCooldown = 40;
                transitionTo(STATE_COMBAT, ANIM_IDLE);
            }
            return;
        }

        double homeDist = (homePos != null) ? Math.hypot(getX() - homePos.x, getZ() - homePos.z) : 0.0D;
        boolean burrowing = (getAnimId() == ANIM_BURROW && stateTick >= 16 && stateTick <= 68);
        if (homePos != null && !burrowing && onGround() && homeDist > ARENA_SOFT_LEASH_RADIUS && !returnHelper.isHardReset()) {
            setTarget(null);
            returnHelper.beginReturn(true, homeDist);
        }
        if (returnHelper.isActive()) {
            if (!returnHelper.isHardReset() && target != null && homeDist <= ARENA_SOFT_LEASH_RADIUS) {
                returnHelper.reset();
            } else {
                tickReturnHome(sl);
                return;
            }
        }
        if (target == null) {
            if (!burrowing && onGround()) {
                returnHelper.beginSearch();
                tickReturnHome(sl);
            } else {
                setDeltaMovement(0.0D, getDeltaMovement().y, 0.0D);
            }
            return;
        }
        noTargetTicks = 0;
        returnHelper.recordTargetPos(target.position());
        setTarget(target);

        tickCombat(sl, target);
    }

    private int lastClientShakeKey = -1;

    private void triggerClientCameraShake() {
        int st = getBossState();
        int anim = getAnimId();
        int t = getAnimTick();
        int key = (st << 16) | (anim << 8) | t;
        if (key == lastClientShakeKey) return;
        float intensity = 0.0F;
        double range = 26.0D;
        if (st == STATE_AWAKEN && (t == 36 || t == 59)) {
            intensity = (t == 36) ? 0.78F : 0.62F;
            range = 32.0D;
        } else if (st == STATE_STAGGER && t == 17) {
            intensity = 0.72F;
        } else if (st == STATE_COMBAT) {
            if (anim == ANIM_PRIMORDIAL_SHOWER && t == 47) {
                intensity = 1.00F;
                range = 38.0D;
            } else if (anim == ANIM_COMBO_SLASH && t == 48) {
                intensity = 0.75F;
            } else if (anim == ANIM_BURROW && (t == 22 || t == 64)) {
                intensity = (t == 64) ? 0.85F : 0.45F;
                range = 30.0D;
            } else if (anim == ANIM_PRIMEVAL_SPRING && t == 24) {
                intensity = 0.80F;
                range = 30.0D;
            } else if (anim == ANIM_LEFT_CLAW && t == 28) {
                intensity = 0.48F;
            } else if (anim == ANIM_REAR_TAIL_SLAM && t == 18) {
                intensity = 0.56F;
                range = 26.0D;
            } else if (anim == ANIM_VERTICAL_TAIL_CLEAVE && t == 25) {
                intensity = 0.68F;
                range = 30.0D;
            }
        }
        if (intensity > 0.0F) {
            lastClientShakeKey = key;
            BossCameraShake.triggerAt(position(), intensity, range);
        }
    }

    private void tickCooldowns() {
        if (showerCooldown > 0) showerCooldown--;
        if (springCooldown > 0) springCooldown--;
        if (whirlwindCooldown > 0) whirlwindCooldown--;
        if (burrowCooldown > 0) burrowCooldown--;
        if (breathCooldown > 0) breathCooldown--;
        if (comboCooldown > 0) comboCooldown--;
        if (scatteredCooldown > 0) scatteredCooldown--;
        if (rearTailSlamCooldown > 0) rearTailSlamCooldown--;
        if (verticalCleaveCooldown > 0) verticalCleaveCooldown--;
    }

    /**
     * Ticks delayed Elemental-Infused Stones and Pyro lava pools with canonical Genshin Impact patterns:
     * Pyro = explosion + lava DoT pool; Hydro = 7 rapid geyser eruptions;
     * Cryo = 3 outward-propagating ice-spike rings; Electro = X-pattern lightning detonation.
     */
    private void tickElementalHazards(ServerLevel sl) {
        Iterator<InfusedStoneHazard> it = activeStones.iterator();
        while (it.hasNext()) {
            InfusedStoneHazard stone = it.next();
            stone.fuseTicks--;
            if (stone.fuseTicks > 0) {
                if (stone.fuseTicks % 4 == 0) {
                    sl.sendParticles(infusionParticleFor(stone.infusion),
                            stone.pos.x, stone.pos.y + 0.55D, stone.pos.z, 3, 0.35D, 0.45D, 0.35D, 0.02D);
                    sl.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.AMETHYST_BLOCK.defaultBlockState()),
                            stone.pos.x, stone.pos.y + 0.3D, stone.pos.z, 2, 0.25D, 0.2D, 0.25D, 0.01D);
                }
            } else {
                detonateInfusedStone(sl, stone);
                it.remove();
            }
        }

        syncStonesToClient();

        Iterator<LavaPoolHazard> lit = activeLavaPools.iterator();
        while (lit.hasNext()) {
            LavaPoolHazard pool = lit.next();
            pool.lifeTicks--;
            if (pool.lifeTicks <= 0) {
                lit.remove();
                continue;
            }
            if (pool.lifeTicks % 5 == 0) {
                sl.sendParticles(ParticleTypes.FLAME, pool.pos.x, pool.pos.y + 0.15D, pool.pos.z, 6, 1.4D, 0.1D, 1.4D, 0.02D);
                sl.sendParticles(ParticleTypes.LAVA, pool.pos.x, pool.pos.y + 0.2D, pool.pos.z, 2, 1.0D, 0.1D, 1.0D, 0.01D);
            }
            if (pool.lifeTicks % 10 == 0) {
                dealPointRadialDamage(sl, pool.pos, 2.4D, 4.5F, 0.15D, 0.10D, true);
            }
        }
    }

    private void syncStonesToClient() {
        CompoundTag root = new CompoundTag();
        if (!activeStones.isEmpty()) {
            ListTag list = new ListTag();
            for (InfusedStoneHazard s : activeStones) {
                CompoundTag ct = new CompoundTag();
                ct.putDouble("x", s.pos.x);
                ct.putDouble("y", s.pos.y);
                ct.putDouble("z", s.pos.z);
                ct.putInt("f", s.fuseTicks);
                list.add(ct);
            }
            root.put("stones", list);
        }
        entityData.set(DATA_STONES_NBT, root);
    }

    private void spawnInfusedStone(ServerLevel sl, double x, double z, int fuseTicks) {
        double y = sampleSafeGroundY(sl, x, z, getY());
        PrimoInfusedStoneEntity.spawn(sl, this, x, y, z, getInfusion(), fuseTicks);
    }

    private void detonateInfusedStone(ServerLevel sl, InfusedStoneHazard stone) {
        Vec3 p = stone.pos;
        ParticleOptions part = infusionParticleFor(stone.infusion);
        switch (stone.infusion) {
            case INFUSION_PYRO -> {
                sl.playSound(null, p.x, p.y, p.z, SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 0.9F, 0.85F);
                sl.sendParticles(ParticleTypes.EXPLOSION, p.x, p.y + 0.4D, p.z, 2, 0.6D, 0.3D, 0.6D, 0.0D);
                sl.sendParticles(ParticleTypes.FLAME, p.x, p.y + 0.5D, p.z, 18, 1.3D, 0.6D, 1.3D, 0.08D);
                dealPointRadialDamage(sl, p, 3.0D, 11.0F, 0.7D, 0.35D, true);
                activeLavaPools.add(new LavaPoolHazard(p, 70)); // 3.5s lava pool DoT
            }
            case INFUSION_HYDRO -> {
                sl.playSound(null, p.x, p.y, p.z, SoundEvents.PLAYER_SPLASH_HIGH_SPEED, SoundSource.HOSTILE, 1.2F, 0.8F);
                // 7 geyser eruptions around the stone
                for (int i = 0; i < 7; i++) {
                    double ang = (Math.PI * 2.0D * i) / 7.0D;
                    double rad = (i == 0) ? 0.0D : 2.2D;
                    Vec3 gPos = p.add(Math.cos(ang) * rad, 0.0D, Math.sin(ang) * rad);
                    sl.sendParticles(ParticleTypes.SPLASH, gPos.x, gPos.y + 0.8D, gPos.z, 10, 0.3D, 0.9D, 0.3D, 0.12D);
                    sl.sendParticles(ParticleTypes.BUBBLE_POP, gPos.x, gPos.y + 1.0D, gPos.z, 4, 0.3D, 0.5D, 0.3D, 0.05D);
                }
                dealPointRadialDamage(sl, p, 3.6D, 10.5F, 0.65D, 0.45D, true);
            }
            case INFUSION_CRYO -> {
                sl.playSound(null, p.x, p.y, p.z, SoundEvents.GLASS_BREAK, SoundSource.HOSTILE, 1.2F, 0.75F);
                // 3 outward ice-spike rings
                for (double r : new double[]{1.4D, 2.6D, 3.8D}) {
                    for (int i = 0; i < 12; i++) {
                        double a = (Math.PI * 2.0D * i) / 12.0D;
                        sl.sendParticles(ParticleTypes.SNOWFLAKE, p.x + Math.cos(a) * r, p.y + 0.5D, p.z + Math.sin(a) * r,
                                2, 0.1D, 0.4D, 0.1D, 0.03D);
                    }
                }
                dealPointRadialDamage(sl, p, 4.0D, 11.5F, 0.60D, 0.32D, true);
            }
            default -> { // ELECTRO: X-pattern detonation
                sl.playSound(null, p.x, p.y, p.z, SoundEvents.LIGHTNING_BOLT_IMPACT, SoundSource.HOSTILE, 0.9F, 1.35F);
                for (int arm = 0; arm < 4; arm++) {
                    double ang = Math.PI * 0.25D + arm * (Math.PI * 0.5D);
                    for (double d = 0.6D; d <= 3.6D; d += 0.75D) {
                        sl.sendParticles(part, p.x + Math.cos(ang) * d, p.y + 0.5D, p.z + Math.sin(ang) * d,
                                3, 0.15D, 0.35D, 0.15D, 0.04D);
                    }
                }
                dealPointRadialDamage(sl, p, 3.8D, 10.5F, 0.75D, 0.30D, true);
            }
        }
    }

    /**
     * Anti-trap block breaking (Wither/Cataclysm style): after a 1-second delay when pinned against walls,
     * shatters obstructing blocks intersecting the boss's hitbox (respecting mobGriefing, preserving chests,
     * Obsidian, Bedrock, and the floor underneath).
     */
    private void tickAntiTrapBlockBreak(ServerLevel sl) {
        if (!TeyvatBossesConfig.get().enableBossBlockBreaking) return;
        if ((this.horizontalCollision || this.isInWall()) && destroyBlocksTick <= 0) {
            destroyBlocksTick = 10;
        }
        if (destroyBlocksTick <= 0) return;
        destroyBlocksTick--;
        if (destroyBlocksTick != 0) return;
        if (!sl.getGameRules().getBoolean(net.minecraft.world.level.GameRules.RULE_MOBGRIEFING)) return;

        AABB box = getBoundingBox().inflate(0.32D, 0.05D, 0.32D);
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
                    if (hardness < 0.0F || hardness >= 50.0F) continue;
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

    private float getRelativeAngleDeg(LivingEntity target) {
        double dx = target.getX() - getX();
        double dz = target.getZ() - getZ();
        float desiredYaw = (float) (Mth.atan2(dz, dx) * Mth.RAD_TO_DEG) - 90.0F;
        return Math.abs(Mth.wrapDegrees(desiredYaw - getYRot()));
    }

    private void tickReturnHome(ServerLevel sl) {
        if (homePos == null) homePos = position();
        double hDist = Math.hypot(getX() - homePos.x, getZ() - homePos.z);
        int phase = returnHelper.getPhase();
        if (phase == BossReturnHomeHelper.PHASE_SEARCH) {
            if (getAnimId() != ANIM_IDLE) transitionTo(STATE_COMBAT, ANIM_IDLE);
            setDeltaMovement(getDeltaMovement().x * 0.45D, getDeltaMovement().y, getDeltaMovement().z * 0.45D);
            Vec3 lastPos = returnHelper.getLastKnownTargetPos();
            if (lastPos != null) {
                float lookYaw = (float) (Math.atan2(lastPos.z - getZ(), lastPos.x - getX()) * (180.0D / Math.PI)) - 90.0F;
                float ny = Mth.approachDegrees(getYRot(), lookYaw, 3.5F);
                setYRot(ny); yBodyRot = ny; yHeadRot = ny;
            }
            returnHelper.tickSearch(hDist);
            return;
        }
        BossReturnHomeHelper.tickGradualRegen(this, 0.06F);
        if (phase == BossReturnHomeHelper.PHASE_RETURN) {
            if (getAnimId() != ANIM_WALK) transitionTo(STATE_COMBAT, ANIM_WALK);
            if (getNavigation() instanceof BossGroundNavigation bnav) {
                if (stateTick % 8 == 0 || bnav.isDone()) {
                    bnav.computePathTo(homePos);
                }
            }
            Vec3 dir = (getNavigation() instanceof BossGroundNavigation bnav)
                    ? bnav.getSteerDirection(homePos)
                    : new Vec3(homePos.x - getX(), 0.0D, homePos.z - getZ()).normalize();
            if (dir.lengthSqr() > 1.0E-4D) {
                float moveYaw = (float) (Math.atan2(dir.z, dir.x) * (180.0D / Math.PI)) - 90.0F;
                float ny = Mth.approachDegrees(getYRot(), moveYaw, 6.5F);
                setYRot(ny); yBodyRot = ny; yHeadRot = ny;
            }
            Vec3 cur = getDeltaMovement();
            double jumpY = (this.horizontalCollision && this.onGround()) ? 0.46D : cur.y;
            setDeltaMovement(Mth.lerp(0.35D, cur.x, dir.x * 0.21D), jumpY, Mth.lerp(0.35D, cur.z, dir.z * 0.21D));
            returnHelper.checkReturnProgress(sl, this, homePos, hDist, 2.8D);
            return;
        }
        if (phase == BossReturnHomeHelper.PHASE_SETTLE) {
            getNavigation().stop();
            setDeltaMovement(getDeltaMovement().x * 0.35D, getDeltaMovement().y, getDeltaMovement().z * 0.35D);
            if (getAnimId() != ANIM_IDLE) transitionTo(STATE_COMBAT, ANIM_IDLE);
            if (returnHelper.tickSettleYaw(this, savedSpawnYaw, 6.0F)) {
                returnHelper.reset();
                noTargetTicks = 0;
                setHealth(getMaxHealth());
                activeStones.clear();
                activeLavaPools.clear();
                transitionTo(STATE_DORMANT, ANIM_DORMANT);
            }
        }
    }

    private void tickCombat(ServerLevel sl, LivingEntity target) {
        if (getAnimId() != ANIM_BURROW) tickAntiTrapBlockBreak(sl);
        int anim = getAnimId();
        double dist = distanceTo(target);

        if (anim == ANIM_IDLE || anim == ANIM_WALK) {
            float relAngle = getRelativeAngleDeg(target);
            // Smooth head look-tracking toward target even before body finishes turning
            lookAtTargetHead(target);

            // Canonical Genshin Anti-Flank AI: Rear Tail Slam or Whirlwind when player runs behind tail!
            if (dist <= 9.8D && relAngle >= 120.0F && (rearTailSlamCooldown <= 0 || whirlwindCooldown <= 0)) {
                boolean pickRearSlam = (rearTailSlamCooldown <= 0) && (whirlwindCooldown > 0 || lastAttackAnim == ANIM_WHIRLWIND || random.nextFloat() < 0.62F);
                if (pickRearSlam) {
                    rearTailSlamCooldown = 110;
                    lastAttackAnim = ANIM_REAR_TAIL_SLAM;
                    transitionTo(STATE_COMBAT, ANIM_REAR_TAIL_SLAM);
                } else {
                    whirlwindCooldown = 100;
                    lastAttackAnim = ANIM_WHIRLWIND;
                    transitionTo(STATE_COMBAT, ANIM_WHIRLWIND);
                }
                return;
            }

            // Responsive turn rate so boss locks on briskly without wasting seconds rotating
            faceTargetSmooth(target, dist > 7.5D ? 10.5F : 8.5F);
            if (attackCooldown > 0) attackCooldown--;

            // Smooth hysteresis walk cadence (no walk<->idle jitter, weighty acceleration & coast-stop)
            boolean shouldWalk = (anim == ANIM_WALK) ? (dist > 5.4D) : (dist > 6.6D);
            if (shouldWalk) {
                if (anim != ANIM_WALK) {
                    entityData.set(DATA_ANIM, ANIM_WALK);
                    entityData.set(DATA_ANIM_TICK, 0);
                    stateTick = 0;
                }
                if (getNavigation() instanceof BossGroundNavigation bnav) {
                    if (stateTick % 6 == 0 || bnav.isDone()) {
                        bnav.computePathTo(target);
                    }
                }
                Vec3 dir = (getNavigation() instanceof BossGroundNavigation bnav)
                        ? bnav.getSteerDirection(target.position())
                        : new Vec3(target.getX() - getX(), 0.0D, target.getZ() - getZ()).normalize();
                double speed = dist > 15.0D ? 0.19D : 0.16D;
                double jumpY = (this.horizontalCollision && this.onGround()) ? 0.48D : getDeltaMovement().y;
                setDeltaMovement(Mth.lerp(0.18D, getDeltaMovement().x, dir.x * speed),
                        jumpY,
                        Mth.lerp(0.18D, getDeltaMovement().z, dir.z * speed));
                if (stateTick % 18 == 9) {
                    sl.playSound(null, getX(), getY(), getZ(), sfx(TeyvatBossesSounds.primoStep(), SoundEvents.RAVAGER_STEP), SoundSource.HOSTILE, 1.35F, 0.95F);
                }
            } else {
                if (anim != ANIM_IDLE) {
                    entityData.set(DATA_ANIM, ANIM_IDLE);
                    entityData.set(DATA_ANIM_TICK, 0);
                    stateTick = 0;
                }
                setDeltaMovement(getDeltaMovement().x * 0.78D, getDeltaMovement().y, getDeltaMovement().z * 0.78D);
            }

            if (attackCooldown <= 0) {
                chooseNextAttack(sl, target, dist, relAngle);
            }
            return;
        }

        // Execute active skill clip
        switch (anim) {
            case ANIM_LEFT_CLAW -> tickSingleClaw(sl, target, true);
            case ANIM_RIGHT_CLAW -> tickSingleClaw(sl, target, false);
            case ANIM_COMBO_SLASH -> tickComboSlash(sl, target);
            case ANIM_WHIRLWIND -> tickWhirlwind(sl, target);
            case ANIM_BURROW -> tickBurrow(sl, target);
            case ANIM_SCATTERED_ROCKS -> tickScatteredRocks(sl, target);
            case ANIM_ELEMENTAL_BREATH -> tickElementalBreath(sl, target);
            case ANIM_PRIMORDIAL_SHOWER -> tickPrimordialShower(sl, target);
            case ANIM_PRIMEVAL_SPRING -> tickPrimevalSpring(sl, target);
            case ANIM_REAR_TAIL_SLAM -> tickRearTailSlam(sl, target);
            case ANIM_VERTICAL_TAIL_CLEAVE -> tickVerticalTailCleave(sl, target);
            default -> finishAttack(46);
        }
    }

    /**
     * Canonical Genshin Impact Priority + Range + Angle + Cooldown selector.
     */
    private void chooseNextAttack(ServerLevel sl, LivingEntity target, double dist, float relAngle) {
        int chosen = -1;
        boolean nearDeath = isNearDeathPhase();

        // Priority 1 (Near Death <=20% HP): Primeval Spring replaces Primordial Shower
        if (nearDeath && springCooldown <= 0 && dist <= 20.0D && relAngle <= 65.0F) {
            chosen = ANIM_PRIMEVAL_SPRING;
            springCooldown = 400 + random.nextInt(200); // 20-30s
        }
        // Priority 1 (Normal >20% HP): Primordial Shower whenever off CD & front-facing (no HP<=90% lock!)
        else if (!nearDeath && showerCooldown <= 0 && dist <= 15.0D && relAngle <= 75.0F) {
            chosen = ANIM_PRIMORDIAL_SHOWER;
            showerCooldown = 360; // Canonical 18s CD
        }
        // Elevation gap (>=2.2 blocks higher/lower, e.g. player on cliff/ledge): Burrow underground and erupt beneath target!
        else if (Math.abs(target.getY() - this.getY()) >= 2.2D && burrowCooldown <= 0) {
            chosen = ANIM_BURROW;
            burrowCooldown = 120; // Fast 6s CD to punish high-ground kiting
        }
        // Far range (>20m): Fast Burrow (Far) with short 5s CD to punish kiters
        else if (dist > 20.0D && burrowCooldown <= 0) {
            chosen = ANIM_BURROW;
            burrowCooldown = 120; // Fast 6s CD for far kiting
        }
        // Mid-Far range (12.5-20m): Burrow (Close) or Scattered Rocks
        else if (dist > 12.5D) {
            if (burrowCooldown <= 0 && random.nextFloat() < 0.55F) {
                chosen = ANIM_BURROW;
                burrowCooldown = 360; // 18s CD
            } else if (scatteredCooldown <= 0) {
                chosen = ANIM_SCATTERED_ROCKS;
                scatteredCooldown = 220 + random.nextInt(120);
            } else {
                chosen = ANIM_BURROW;
                burrowCooldown = 300;
            }
        }
        // Anti-flank behind or side-rear (75-180 deg) within 10.5m
        else if (whirlwindCooldown <= 0 && dist <= 10.5D && relAngle >= 75.0F) {
            chosen = ANIM_WHIRLWIND;
            whirlwindCooldown = 160;
        }
        // Mid-Close combat (0-12.5m): smart weighted pool respecting per-skill cooldowns
        else {
            List<Integer> candidates = new ArrayList<>();
            // Strict range-gated melee: never whiff 7.2m claws at 9-12m!
            if (dist <= 7.2D) {
                candidates.add(ANIM_RIGHT_CLAW);
                candidates.add(ANIM_LEFT_CLAW);
            }
            if (comboCooldown <= 0 && dist <= 8.6D) {
                candidates.add(ANIM_COMBO_SLASH);
                if (nearDeath) candidates.add(ANIM_COMBO_SLASH);
            }
            if (breathCooldown <= 0 && dist <= 13.0D && relAngle <= 95.0F) {
                candidates.add(ANIM_ELEMENTAL_BREATH);
            }
            if (scatteredCooldown <= 0 && dist >= 5.5D) {
                candidates.add(ANIM_SCATTERED_ROCKS);
            }
            if (verticalCleaveCooldown <= 0 && dist >= 4.2D && dist <= 12.0D && relAngle <= 75.0F) {
                candidates.add(ANIM_VERTICAL_TAIL_CLEAVE);
            }
            if (burrowCooldown <= 0 && dist >= 7.8D) {
                candidates.add(ANIM_BURROW);
            }
            if (candidates.isEmpty()) {
                // If out of melee reach and gap-closers are on cooldown, keep walking closer instead of whiffing!
                if (dist > 7.4D) {
                    attackCooldown = 10;
                    return;
                }
                candidates.add(ANIM_RIGHT_CLAW);
                candidates.add(ANIM_LEFT_CLAW);
            }
            chosen = pickNonRepeating(candidates);
            if (chosen == ANIM_COMBO_SLASH) comboCooldown = 220 + random.nextInt(100);
            else if (chosen == ANIM_ELEMENTAL_BREATH) breathCooldown = 280 + random.nextInt(120);
            else if (chosen == ANIM_SCATTERED_ROCKS) scatteredCooldown = 180 + random.nextInt(80);
            else if (chosen == ANIM_BURROW) burrowCooldown = 320;
            else if (chosen == ANIM_VERTICAL_TAIL_CLEAVE) verticalCleaveCooldown = 220 + random.nextInt(80);
        }

        lastAttackAnim = chosen;
        transitionTo(STATE_COMBAT, chosen);

        if (chosen == ANIM_PRIMORDIAL_SHOWER) {
            entityData.set(DATA_TELEGRAPH_RADIUS, 16.0F);
            sl.playSound(null, getX(), getY() + 2.8D, getZ(), sfx(TeyvatBossesSounds.primoShowerCharge(), SoundEvents.WITHER_SPAWN), SoundSource.HOSTILE, 1.7F, 1.0F);
            for (Player p : sl.getEntitiesOfClass(Player.class, getBoundingBox().inflate(36.0D))) {
                p.displayClientMessage(Component.translatable("message.velorise_teyvat_bosses.primo_geovishap.shower_warn")
                        .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD), true);
            }
        } else if (chosen == ANIM_BURROW) {
            burrowStart = position();
            // Predictive target leading so Burrow intercepts moving players
            Vec3 lead = target.position().add(target.getDeltaMovement().multiply(10.0D, 0.0D, 10.0D));
            burrowDest = lead;
        }
    }

    private int pickNonRepeating(List<Integer> pool) {
        if (pool.size() == 1) return pool.get(0);
        int c = pool.get(random.nextInt(pool.size()));
        if (c == lastAttackAnim) {
            for (int alt : pool) {
                if (alt != lastAttackAnim) return alt;
            }
        }
        return c;
    }

    /** Finishes an attack with brisk Genshin-style pacing (1.4s–2.1s, faster near death). */
    private void finishAttack(int baseRestTicks) {
        int clamped = Mth.clamp((int) (baseRestTicks * 0.68F), 24, 34);
        int scaled = isNearDeathPhase() ? Math.max(18, (int) (clamped * 0.78F)) : clamped;
        attackCooldown = scaled + random.nextInt(10);
        transitionTo(STATE_COMBAT, ANIM_IDLE);
    }

    private void tickSingleClaw(ServerLevel sl, LivingEntity target, boolean left) {
        setDeltaMovement(getDeltaMovement().x * 0.65D, getDeltaMovement().y, getDeltaMovement().z * 0.65D);
        if (stateTick < 13) faceTargetSmooth(target, 7.5F);
        // Subtle forward commit step during the strike arc
        if (stateTick >= 11 && stateTick <= 14) {
            Vec3 lunge = Vec3.directionFromRotation(0.0F, getYRot()).scale(0.32D);
            setDeltaMovement(lunge.x, getDeltaMovement().y, lunge.z);
        }
        if (stateTick == 14) {
            sl.playSound(null, getX(), getY() + 1.8D, getZ(), sfx(TeyvatBossesSounds.primoClawSwipe(), SoundEvents.PLAYER_ATTACK_SWEEP), SoundSource.HOSTILE, 1.8F, 0.95F);
            dealConeDamage(sl, 7.2D, 80.0D, left ? 16.0F : 19.5F, 1.25D, false);
            Vec3 fwd = Vec3.directionFromRotation(0.0F, getYRot()).scale(4.2D);
            sl.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.COBBLESTONE.defaultBlockState()),
                    getX() + fwd.x, getY() + 1.2D, getZ() + fwd.z, 12, 1.2D, 0.4D, 1.2D, 0.10D);
        } else if (left && stateTick == 28) {
            // Canonical Genshin Left Claw Hit 2: explosive rip-up ground rupture synced to 1.42s keyframe!
            Vec3 fwd = Vec3.directionFromRotation(0.0F, getYRot()).scale(4.0D);
            Vec3 rupturePos = position().add(fwd);
            sl.playSound(null, rupturePos.x, getY(), rupturePos.z, SoundEvents.STONE_BREAK, SoundSource.HOSTILE, 1.7F, 0.52F);
            sl.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.COBBLESTONE.defaultBlockState()),
                    rupturePos.x, getY() + 0.5D, rupturePos.z, 26, 1.3D, 0.6D, 1.3D, 0.16D);
            dealPointRadialDamage(sl, rupturePos, 4.4D, 15.0F, 0.95D, 0.50D, false);
        }
        int endTick = left ? 46 : 40;
        if (stateTick >= endTick) finishAttack(42);
    }

    private void tickComboSlash(ServerLevel sl, LivingEntity target) {
        setDeltaMovement(getDeltaMovement().x * 0.68D, getDeltaMovement().y, getDeltaMovement().z * 0.68D);
        if (stateTick < 12 || (stateTick >= 18 && stateTick < 27) || (stateTick >= 34 && stateTick < 45)) {
            faceTargetSmooth(target, 7.8F);
        }
        // Authentic Genshin forward step-lunge momentum during each of the 3 chained strikes
        if ((stateTick >= 11 && stateTick <= 14) || (stateTick >= 26 && stateTick <= 29) || (stateTick >= 44 && stateTick <= 47)) {
            double spd = (stateTick >= 44) ? 0.38D : 0.44D;
            Vec3 lunge = Vec3.directionFromRotation(0.0F, getYRot()).scale(spd);
            setDeltaMovement(lunge.x, getDeltaMovement().y, lunge.z);
        }
        if (stateTick == 14) {
            sl.playSound(null, getX(), getY() + 1.8D, getZ(), sfx(TeyvatBossesSounds.primoClawSwipe(), SoundEvents.PLAYER_ATTACK_SWEEP), SoundSource.HOSTILE, 1.7F, 1.0F);
            dealConeDamage(sl, 7.4D, 82.0D, 16.5F, 1.00D, false);
        } else if (stateTick == 29) {
            sl.playSound(null, getX(), getY() + 1.8D, getZ(), sfx(TeyvatBossesSounds.primoClawSwipe(), SoundEvents.PLAYER_ATTACK_SWEEP), SoundSource.HOSTILE, 1.7F, 0.92F);
            dealConeDamage(sl, 7.6D, 82.0D, 19.0F, 1.10D, false);
        } else if (stateTick == 48) {
            sl.playSound(null, getX(), getY(), getZ(), sfx(TeyvatBossesSounds.primoSlam(), SoundEvents.GENERIC_EXPLODE.value()), SoundSource.HOSTILE, 2.0F, 0.95F);
            sl.playSound(null, getX(), getY(), getZ(), SoundEvents.STONE_BREAK, SoundSource.HOSTILE, 1.8F, 0.45F);
            dealRadialDamage(sl, 8.6D, 27.5F, 1.55D, 0.70D, false);
            spawnGroundRing(sl, 7.6D, 48, new BlockParticleOption(ParticleTypes.BLOCK, Blocks.COBBLESTONE.defaultBlockState()));
            sl.sendParticles(ParticleTypes.EXPLOSION, getX(), getY() + 0.6D, getZ(), 6, 2.6D, 0.4D, 2.6D, 0.0D);
        }
        if (stateTick >= 72) finishAttack(50);
    }

    private void tickWhirlwind(ServerLevel sl, LivingEntity target) {
        setDeltaMovement(getDeltaMovement().x * 0.68D, getDeltaMovement().y, getDeltaMovement().z * 0.68D);
        if (stateTick < 10) faceTargetSmooth(target, 6.0F);
        // Synced directly to Phase-2 ultra-fast 0.60s whip spin (tick 11..23)!
        if (stateTick == 14 || stateTick == 20) {
            sl.playSound(null, getX(), getY() + 1.6D, getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.HOSTILE, 1.9F, 0.46F);
            dealRadialDamage(sl, 8.5D, 19.5F, 1.55D, 0.50D, false);
            spawnGroundRing(sl, 7.8D, 36, ParticleTypes.CLOUD);
            spawnGroundRing(sl, 6.5D, 24, new BlockParticleOption(ParticleTypes.BLOCK, Blocks.DIRT.defaultBlockState()));
        }
        if (stateTick >= 52) finishAttack(44);
    }

    /** Anti-flank Rear Tail Slam: raises curled tail high behind hips and slams straight down onto ground behind boss (tick 18). */
    private void tickRearTailSlam(ServerLevel sl, LivingEntity target) {
        setDeltaMovement(getDeltaMovement().x * 0.65D, getDeltaMovement().y, getDeltaMovement().z * 0.65D);
        // Stay facing forward so the tail hammer lands squarely on the player behind the boss!
        if (stateTick == 18) {
            Vec3 back = Vec3.directionFromRotation(0.0F, getYRot()).scale(-5.2D);
            Vec3 impactPos = position().add(back);
            sl.playSound(null, impactPos.x, getY(), impactPos.z, sfx(TeyvatBossesSounds.primoSlam(), SoundEvents.GENERIC_EXPLODE.value()), SoundSource.HOSTILE, 1.9F, 0.88F);
            sl.playSound(null, impactPos.x, getY(), impactPos.z, SoundEvents.STONE_BREAK, SoundSource.HOSTILE, 1.8F, 0.48F);
            dealPointRadialDamage(sl, impactPos, 5.6D, 23.0F, 1.45D, 0.68D, false);
            sl.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.COBBLESTONE.defaultBlockState()),
                    impactPos.x, getY() + 0.5D, impactPos.z, 38, 2.2D, 0.6D, 2.2D, 0.16D);
            sl.sendParticles(ParticleTypes.EXPLOSION, impactPos.x, getY() + 0.6D, impactPos.z, 4, 1.6D, 0.3D, 1.6D, 0.0D);
        }
        if (stateTick >= 48) finishAttack(40);
    }

    /** Mid-range Vertical Spine-Curl Overhead Tail Cleave: curls vertically on forepaws and chops tail straight overhead forward (tick 25). */
    private void tickVerticalTailCleave(ServerLevel sl, LivingEntity target) {
        setDeltaMovement(getDeltaMovement().x * 0.65D, getDeltaMovement().y, getDeltaMovement().z * 0.65D);
        if (stateTick < 18) faceTargetSmooth(target, 7.5F);
        if (stateTick >= 19 && stateTick <= 24) {
            Vec3 lunge = Vec3.directionFromRotation(0.0F, getYRot()).scale(0.30D);
            setDeltaMovement(lunge.x, getDeltaMovement().y, lunge.z);
        }
        if (stateTick == 25) {
            Vec3 dir = Vec3.directionFromRotation(0.0F, getYRot());
            Vec3 impactPos = position().add(dir.scale(5.6D));
            sl.playSound(null, impactPos.x, getY(), impactPos.z, sfx(TeyvatBossesSounds.primoSlam(), SoundEvents.GENERIC_EXPLODE.value()), SoundSource.HOSTILE, 2.0F, 0.85F);
            sl.playSound(null, impactPos.x, getY(), impactPos.z, SoundEvents.STONE_BREAK, SoundSource.HOSTILE, 1.9F, 0.46F);
            dealPointRadialDamage(sl, impactPos, 5.0D, 25.0F, 1.50D, 0.72D, false);
            dealConeDamage(sl, 9.6D, 34.0D, 21.0F, 1.35D, false);
            // Linear Geo fissure burst along the forward tail chop line
            for (int d = 3; d <= 9; d += 2) {
                Vec3 pt = position().add(dir.scale(d));
                sl.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.COBBLESTONE.defaultBlockState()),
                        pt.x, getY() + 0.5D, pt.z, 16, 1.1D, 0.5D, 1.1D, 0.14D);
                sl.sendParticles(infusionParticle(), pt.x, getY() + 0.6D, pt.z, 6, 0.8D, 0.4D, 0.8D, 0.08D);
            }
            sl.sendParticles(ParticleTypes.EXPLOSION, impactPos.x, getY() + 0.6D, impactPos.z, 5, 1.8D, 0.3D, 1.8D, 0.0D);
        }
        if (stateTick >= 58) finishAttack(44);
    }

    /**
     * Canonical Genshin 3-Phase Burrow Physics:
     * <ul>
     *   <li>Phase 1 (tick 0..21): Nose-dive dig into the earth with stone/dust fountain.</li>
     *   <li>Phase 2 (tick 22..56): REAL continuous subterranean pursuit (no teleport!) — protruding dorsal
     *       crystal mane plows across the arena at 13.6 blk/s, dealing contact DMG &amp; dropping 3D Infused Stones!</li>
     *   <li>Phase 3 (tick 57..88): Explosive arc eruption + heavy golden Geo slam at tick 64.</li>
     * </ul>
     */
    private void tickBurrow(ServerLevel sl, LivingEntity target) {
        if (stateTick < 16) {
            setDeltaMovement(getDeltaMovement().x * 0.6D, getDeltaMovement().y, getDeltaMovement().z * 0.6D);
            faceTargetSmooth(target, 7.0F);
        } else if (stateTick == 21) {
            burrowStart = position();
            sl.playSound(null, getX(), getY(), getZ(), sfx(TeyvatBossesSounds.primoBurrow(), SoundEvents.STONE_BREAK), SoundSource.HOSTILE, 1.9F, 1.0F);
            spawnGroundRing(sl, 4.2D, 30, new BlockParticleOption(ParticleTypes.BLOCK, Blocks.STONE.defaultBlockState()));
        } else if (stateTick >= 22 && stateTick <= 55) {
            // True 3D subterranean tunneling: phases through cliff walls & ascends/descends to erupt beneath elevated targets!
            this.noPhysics = true;
            this.setNoGravity(true);
            Vec3 predicted = target.position().add(target.getDeltaMovement().multiply(6.0D, 0.0D, 6.0D));
            double baseRefY = (burrowStart != null) ? burrowStart.y : getY();
            double destY = Mth.clamp(findBurrowSurfaceY(sl, predicted.x, predicted.z, target.getY()), baseRefY - 24.0D, baseRefY + 24.0D);
            Vec3 to = new Vec3(predicted.x - getX(), destY - getY(), predicted.z - getZ());
            double horizDist = Math.hypot(to.x, to.z);
            double dy = to.y;
            faceTargetSmooth(target, 14.0F);
            double vx = getDeltaMovement().x * 0.45D;
            double vz = getDeltaMovement().z * 0.45D;
            if (horizDist > 1.8D) {
                double speed = Math.min(0.72D, horizDist * 0.35D);
                vx = Mth.lerp(0.35D, getDeltaMovement().x, (to.x / horizDist) * speed);
                vz = Mth.lerp(0.35D, getDeltaMovement().z, (to.z / horizDist) * speed);
            }
            // Smooth vertical tunneling speed so boss climbs cliffs or dives to lower ledges cleanly before t=56
            double vy = Mth.clamp(dy * 0.28D, -0.75D, 0.75D);
            setDeltaMovement(vx, vy, vz);

            // Plowing dirt/stone wake on the ground surface above/around the tunneling boss
            double surfacePfxY = findBurrowSurfaceY(sl, getX(), getZ(), Math.max(getY(), destY));
            if (stateTick % 3 == 0) {
                sl.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.DIRT.defaultBlockState()),
                        getX(), surfacePfxY + 0.5D, getZ(), 12, 1.6D, 0.4D, 1.6D, 0.10D);
                sl.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.COBBLESTONE.defaultBlockState()),
                        getX(), surfacePfxY + 0.4D, getZ(), 6, 1.4D, 0.5D, 1.4D, 0.12D);
            }
            if (stateTick % 7 == 0) {
                sl.playSound(null, getX(), surfacePfxY, getZ(), SoundEvents.RAVAGER_STEP, SoundSource.HOSTILE, 1.5F, 0.48F);
            }
            // Drop canonical 3D Elemental-Infused Stones along the burrow trail
            if (stateTick == 28 || stateTick == 38 || stateTick == 48) {
                spawnInfusedStone(sl,
                        getX() + (random.nextDouble() - 0.5D) * 2.4D,
                        getZ() + (random.nextDouble() - 0.5D) * 2.4D,
                        42);
            }
            // Canonical 25% ATK contact damage while plowing through enemies
            if (stateTick % 5 == 0) {
                dealRadialDamage(sl, 3.6D, 10.5F, 0.95D, 0.42D, false);
            }
            // Show emergence telegraph ring as boss prepares to erupt
            if (stateTick >= 46) {
                entityData.set(DATA_TELEGRAPH_RADIUS, 8.5F);
            }
        } else if (stateTick >= 56 && stateTick < 64) {
            if (stateTick == 56) {
                this.noPhysics = false;
                this.setNoGravity(false);
                // Pop cleanly onto open solid surface at current XZ so boss never suffocates inside cliff rock
                double emergeY = findBurrowSurfaceY(sl, getX(), getZ(), Math.max(getY() + 1.5D, target.getY()));
                if (Math.abs(emergeY - getY()) <= 12.0D) {
                    this.setPos(getX(), emergeY, getZ());
                }
                this.setDeltaMovement(Vec3.ZERO);
            } else {
                setDeltaMovement(getDeltaMovement().x * 0.5D, getDeltaMovement().y, getDeltaMovement().z * 0.5D);
            }
            if (stateTick == 57) {
                sl.playSound(null, getX(), getY() + 1.5D, getZ(), sfx(TeyvatBossesSounds.primoEmerge(), SoundEvents.RAVAGER_ROAR), SoundSource.HOSTILE, 1.8F, 1.0F);
            }
        } else if (stateTick == 64) {
            entityData.set(DATA_TELEGRAPH_RADIUS, 0.0F);
            sl.playSound(null, getX(), getY(), getZ(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 2.1F, 0.52F);
            dealRadialDamage(sl, 8.6D, 26.5F, 1.60D, 0.76D, true);
            spawnGroundRing(sl, 7.8D, 52, new BlockParticleOption(ParticleTypes.BLOCK, Blocks.COBBLESTONE.defaultBlockState()));
        } else {
            setDeltaMovement(getDeltaMovement().x * 0.6D, getDeltaMovement().y, getDeltaMovement().z * 0.6D);
        }
        if (stateTick >= 88) finishAttack(48);
    }

    /** Finds the nearest safe open ground surface Y near (x, z, hintY) for subterranean burrow ascent/emergence. */
    private double findBurrowSurfaceY(ServerLevel sl, double x, double z, double hintY) {
        int bx = Mth.floor(x);
        int bz = Mth.floor(z);
        int startY = Mth.clamp(Mth.floor(hintY), sl.getMinBuildHeight() + 2, sl.getMaxBuildHeight() - 4);
        BlockPos.MutableBlockPos p = new BlockPos.MutableBlockPos(bx, startY, bz);
        // If hintY is inside solid rock, scan upward to break out to the top surface
        if (sl.getBlockState(p).blocksMotion()) {
            for (int dy = 1; dy <= 16; dy++) {
                p.set(bx, startY + dy, bz);
                if (!sl.getBlockState(p).blocksMotion() && !sl.getBlockState(p.above()).blocksMotion()) {
                    return startY + dy;
                }
            }
        }
        // Otherwise scan downward to find the solid floor beneath an airborne/standing target
        for (int dy = 0; dy <= 16; dy++) {
            int cy = startY - dy;
            if (cy <= sl.getMinBuildHeight() + 1) break;
            p.set(bx, cy - 1, bz);
            if (sl.getBlockState(p).blocksMotion()) {
                p.set(bx, cy, bz);
                if (!sl.getBlockState(p).blocksMotion()) {
                    return cy;
                }
            }
        }
        return hintY;
    }

    private void tickScatteredRocks(ServerLevel sl, LivingEntity target) {
        setDeltaMovement(getDeltaMovement().x * 0.6D, getDeltaMovement().y, getDeltaMovement().z * 0.6D);
        if (stateTick < 20 || (stateTick >= 30 && stateTick < 40)) faceTargetSmooth(target, 6.5F);
        // Synced to Phase-2 Handstand Tail Flick #1 (1.25s = tick 25) and Flick #2 (2.15s = tick 43)!
        if (stateTick == 25 || stateTick == 43) {
            sl.playSound(null, getX(), getY() + 2.0D, getZ(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 1.5F, 0.62F);
            sl.playSound(null, getX(), getY() + 2.5D, getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.HOSTILE, 1.6F, 0.55F);
            Vec3 leadPos = target.position().add(target.getDeltaMovement().multiply(6.0D, 0.0D, 6.0D));
            Vec3 dir = new Vec3(leadPos.x - getX(), 0.0D, leadPos.z - getZ());
            if (dir.lengthSqr() > 0.01D) {
                dir = dir.normalize();
                Vec3 perp = new Vec3(-dir.z, 0.0D, dir.x);
                // Fan out 3 crystalline Elemental-Infused Stones per tail flick
                for (int k = -1; k <= 1; k++) {
                    double dist = 7.0D + random.nextDouble() * 6.5D;
                    Vec3 p = position().add(dir.scale(dist)).add(perp.scale(k * 3.2D));
                    spawnInfusedStone(sl, p.x, p.z, 44 + (k + 1) * 6);
                }
            }
            dealConeDamage(sl, 15.0D, 38.0D, 15.0F, 1.05D, true);
        }
        if (stateTick >= 64) finishAttack(46);
    }

    private void tickElementalBreath(ServerLevel sl, LivingEntity target) {
        setDeltaMovement(getDeltaMovement().x * 0.6D, getDeltaMovement().y, getDeltaMovement().z * 0.6D);
        if (stateTick < 21) faceTargetSmooth(target, 6.5F);
        if (stateTick == 23) {
            sl.playSound(null, getX(), getY() + 2.4D, getZ(), SoundEvents.ENDER_DRAGON_GROWL, SoundSource.HOSTILE, 1.9F, 0.70F);
        }
        if (stateTick >= 22 && stateTick <= 62) {
            float progress = (stateTick - 22) / 40.0F;
            float sweepYaw = getYRot() + Mth.lerp(progress, 46.0F, -46.0F);
            Vec3 aim = Vec3.directionFromRotation(0.0F, sweepYaw);
            ParticleOptions secondary = switch (getInfusion()) {
                case INFUSION_HYDRO -> ParticleTypes.BUBBLE_POP;
                case INFUSION_CRYO -> ParticleTypes.CLOUD;
                case INFUSION_ELECTRO -> ParticleTypes.END_ROD;
                default -> ParticleTypes.LARGE_SMOKE;
            };
            for (int d = 3; d <= 16; d += 2) {
                double beamY = Math.max(0.45D, 2.25D - d * 0.095D);
                double spread = 0.28D + d * 0.055D;
                Vec3 pt = position().add(aim.scale(d)).add(0.0D, beamY, 0.0D);
                sl.sendParticles(infusionParticle(), pt.x, pt.y, pt.z, 6, spread, spread * 0.7D, spread, 0.08D);
                if (d % 4 == 1) {
                    sl.sendParticles(secondary, pt.x, pt.y, pt.z, 2, spread * 0.8D, 0.35D, spread * 0.8D, 0.04D);
                }
            }
            // Genshin-style billowing ground-impact elemental spray along the floor contact zone
            Vec3 impact = position().add(aim.scale(13.8D));
            sl.sendParticles(infusionParticle(), impact.x, getY() + 0.35D, impact.z, 10, 1.1D, 0.45D, 1.1D, 0.12D);
            sl.sendParticles(secondary, impact.x, getY() + 0.55D, impact.z, 4, 0.9D, 0.35D, 0.9D, 0.05D);
            if ((stateTick - 22) % 6 == 0) {
                dealBeamSweepDamage(sl, aim, 16.5D, 2.6D, 11.5F);
            }
        }
        if (stateTick >= 84) finishAttack(48);
    }

    /**
     * Detects Velorise's Staves Elemental/Jade/Crystallize shields (via API, Absorption hearts,
     * or any active shield/jade MobEffect) so Zhongli's Jade Shield reliably counters Primordial Shower.
     */
    private static boolean hasElementalOrModShield(LivingEntity e) {
        if (StavesElementBridge.hasElementalShield(e)) return true;
        if (e.getAbsorptionAmount() > 0.0F) return true;
        for (net.minecraft.world.effect.MobEffectInstance inst : e.getActiveEffects()) {
            String key = inst.getEffect().getRegisteredName().toLowerCase(java.util.Locale.ROOT);
            if (key.contains("shield") || key.contains("jade") || key.contains("crystallize")) {
                return true;
            }
        }
        return false;
    }

    private void tickPrimordialShower(ServerLevel sl, LivingEntity target) {
        setDeltaMovement(0.0D, getDeltaMovement().y, 0.0D);
        if (stateTick < 38) faceTargetSmooth(target, 5.0F);
        if (stateTick < 46 && stateTick % 4 == 0) {
            spawnGroundRing(sl, 16.0D * (stateTick / 46.0F), 36, infusionParticle());
        }
        if (stateTick == 47) {
            entityData.set(DATA_TELEGRAPH_RADIUS, 0.0F);
            sl.playSound(null, getX(), getY() + 1.5D, getZ(), sfx(TeyvatBossesSounds.primoShowerBlast(), SoundEvents.GENERIC_EXPLODE.value()), SoundSource.HOSTILE, 2.2F, 1.0F);
            sl.playSound(null, getX(), getY() + 2.5D, getZ(), SoundEvents.ENDER_DRAGON_GROWL, SoundSource.HOSTILE, 2.2F, 0.50F);
            spawnGroundRing(sl, 16.0D, 76, infusionParticle());
            spawnGroundRing(sl, 11.5D, 52, ParticleTypes.EXPLOSION);

            AABB box = getBoundingBox().inflate(16.0D, 6.0D, 16.0D);
            LivingEntity counterSource = null;
            boolean counterUsedElemShield = false;
            for (LivingEntity e : sl.getEntitiesOfClass(LivingEntity.class, box)) {
                if (e == this || !e.isAlive() || e.distanceToSqr(this) > 16.0D * 16.0D) continue;
                boolean hasElemShield = hasElementalOrModShield(e);
                boolean hasVanillaShield = (e instanceof Player p && p.isBlocking());
                if (hasElemShield || hasVanillaShield) {
                    if (counterSource == null || (hasElemShield && !counterUsedElemShield)) {
                        counterSource = e;
                        counterUsedElemShield = hasElemShield;
                    }
                    continue;
                }
                applyElementalOrPhysicalHit(sl, e, 34.0F, true);
                Vec3 push = e.position().subtract(position()).multiply(1.0D, 0.0D, 1.0D);
                if (push.lengthSqr() > 1.0E-4D) push = push.normalize().scale(1.75D);
                e.setDeltaMovement(push.x, 0.70D, push.z);
                e.hurtMarked = true;
            }
            if (counterSource != null && !showerCountered) {
                showerCountered = true;
                float reflectDmg = getMaxHealth() * (counterUsedElemShield ? 0.15F : 0.10F);
                super.hurt(sl.damageSources().thorns(counterSource), reflectDmg);
                sl.playSound(null, counterSource.getX(), counterSource.getY() + 1.2D, counterSource.getZ(), SoundEvents.SHIELD_BLOCK, SoundSource.PLAYERS, 2.2F, 0.85F);
                sl.playSound(null, getX(), getY() + 2.2D, getZ(), sfx(TeyvatBossesSounds.primoShieldReflect(), SoundEvents.ANVIL_LAND), SoundSource.HOSTILE, 2.0F, 1.0F);
                sl.sendParticles(ParticleTypes.FLASH, getX(), getY() + 2.4D, getZ(), 3, 0.7D, 0.7D, 0.7D, 0.0D);
                if (counterSource instanceof Player p) {
                    p.displayClientMessage(Component.translatable("message.velorise_teyvat_bosses.primo_geovishap.countered")
                            .withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD), true);
                }
                Vec3 recoil = position().subtract(counterSource.position()).multiply(1.0D, 0.0D, 1.0D);
                if (recoil.lengthSqr() > 1.0E-4D) {
                    recoil = recoil.normalize().scale(0.85D);
                    setDeltaMovement(recoil.x, 0.18D, recoil.z);
                }
                CompoundTag beamTag = new CompoundTag();
                beamTag.putFloat("dx", (float) (counterSource.getX() - getX()));
                beamTag.putFloat("dy", (float) (counterSource.getY() - getY()));
                beamTag.putFloat("dz", (float) (counterSource.getZ() - getZ()));
                entityData.set(DATA_COUNTER_BEAM, beamTag);
                transitionTo(STATE_STAGGER, ANIM_COUNTERED_STAGGER);
                return;
            }
        }
        if (stateTick >= 92) finishAttack(56);
    }

    private void tickPrimevalSpring(ServerLevel sl, LivingEntity target) {
        setDeltaMovement(getDeltaMovement().x * 0.6D, getDeltaMovement().y, getDeltaMovement().z * 0.6D);
        if (stateTick < 20) faceTargetSmooth(target, 6.5F);
        if (stateTick == 24) {
            sl.playSound(null, getX(), getY(), getZ(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 1.9F, 0.52F);
            dealRadialDamage(sl, 9.2D, 22.0F, 1.35D, 0.66D, false);
            spawnGroundRing(sl, 8.4D, 52, new BlockParticleOption(ParticleTypes.BLOCK, Blocks.STONE.defaultBlockState()));

            for (int i = 0; i < 8; i++) {
                double ang = (Math.PI * 2.0D * i) / 8.0D + random.nextDouble() * 0.35D;
                double dist = 5.5D + random.nextDouble() * 6.5D;
                spawnInfusedStone(sl, getX() + Math.cos(ang) * dist, getZ() + Math.sin(ang) * dist, 38 + i * 3);
            }
            spawnInfusedStone(sl, target.getX() + 1.5D, target.getZ(), 42);
            spawnInfusedStone(sl, target.getX() - 1.5D, target.getZ(), 48);
        }
        if (stateTick >= 68) finishAttack(44);
    }

    private void dealRadialDamage(ServerLevel sl, double radius, float dmg, double kb, double lift, boolean elemental) {
        dealPointRadialDamage(sl, position(), radius, dmg, kb, lift, elemental);
    }

    private void dealPointRadialDamage(ServerLevel sl, Vec3 center, double radius, float dmg, double kb, double lift, boolean elemental) {
        AABB box = new AABB(center.x - radius, center.y - 2.0D, center.z - radius,
                center.x + radius, center.y + 4.5D, center.z + radius);
        for (LivingEntity e : sl.getEntitiesOfClass(LivingEntity.class, box)) {
            if (e == this || !e.isAlive() || e.position().distanceToSqr(center) > radius * radius) continue;
            applyElementalOrPhysicalHit(sl, e, dmg, elemental);
            Vec3 push = e.position().subtract(center).multiply(1.0D, 0.0D, 1.0D);
            if (push.lengthSqr() > 1.0E-4D) push = push.normalize().scale(kb);
            e.setDeltaMovement(push.x, lift, push.z);
            e.hurtMarked = true;
        }
    }

    private void dealConeDamage(ServerLevel sl, double range, double halfAngleDeg, float dmg, double kb, boolean elemental) {
        Vec3 fwd = Vec3.directionFromRotation(0.0F, getYRot()).normalize();
        double minDot = Math.cos(Math.toRadians(halfAngleDeg));
        AABB box = getBoundingBox().inflate(range, 4.0D, range);
        for (LivingEntity e : sl.getEntitiesOfClass(LivingEntity.class, box)) {
            if (e == this || !e.isAlive()) continue;
            Vec3 to = e.position().subtract(position()).multiply(1.0D, 0.0D, 1.0D);
            double d = to.length();
            if (d > range || d < 0.01D) continue;
            if (to.scale(1.0D / d).dot(fwd) < minDot) continue;
            applyElementalOrPhysicalHit(sl, e, dmg, elemental);
            e.setDeltaMovement(to.scale(kb / d).x, 0.44D, to.scale(kb / d).z);
            e.hurtMarked = true;
        }
    }

    private void dealBeamSweepDamage(ServerLevel sl, Vec3 aimDir, double range, double halfWidth, float dmg) {
        AABB box = getBoundingBox().inflate(range, 4.0D, range);
        for (LivingEntity e : sl.getEntitiesOfClass(LivingEntity.class, box)) {
            if (e == this || !e.isAlive()) continue;
            Vec3 rel = e.position().subtract(position()).multiply(1.0D, 0.0D, 1.0D);
            double along = rel.dot(aimDir);
            if (along < 1.5D || along > range) continue;
            double perpSq = rel.lengthSqr() - along * along;
            if (perpSq <= halfWidth * halfWidth) {
                applyElementalOrPhysicalHit(sl, e, dmg, true);
            }
        }
    }

    private void applyElementalOrPhysicalHit(ServerLevel sl, LivingEntity target, float dmg, boolean elemental) {
        if (StavesElementBridge.isAvailable()) {
            if (elemental) {
                StavesElementBridge.dealElementalDamage(target, this, getInfusionElementName(), dmg, 1.0F, false, 0.25F);
            } else {
                // Canonical Primo Geovishap melee & ground slams deal Geo damage (triggers Crystallize with Velorise's Staves!)
                StavesElementBridge.dealElementalDamage(target, this, "GEO", dmg, 1.0F, false, 0.22F);
            }
        } else {
            target.hurt(sl.damageSources().mobAttack(this), TeyvatBossesConfig.scaleDamage("primo_geovishap", dmg));
        }
    }

    private static net.minecraft.sounds.SoundEvent sfx(net.minecraft.sounds.SoundEvent custom, net.minecraft.sounds.SoundEvent fallback) {
        return custom != null ? custom : fallback;
    }

    private ParticleOptions infusionParticle() {
        return infusionParticleFor(getInfusion());
    }

    private static ParticleOptions infusionParticleFor(int infusion) {
        return switch (infusion) {
            case INFUSION_HYDRO -> ParticleTypes.SPLASH;
            case INFUSION_ELECTRO -> ParticleTypes.ELECTRIC_SPARK;
            case INFUSION_CRYO -> ParticleTypes.SNOWFLAKE;
            default -> ParticleTypes.FLAME;
        };
    }

    private void spawnGroundRing(ServerLevel sl, double radius, int count, ParticleOptions particle) {
        for (int i = 0; i < count; i++) {
            double a = (Math.PI * 2.0D * i) / count;
            sl.sendParticles(particle, getX() + Math.cos(a) * radius, getY() + 0.4D, getZ() + Math.sin(a) * radius,
                    1, 0.12D, 0.16D, 0.12D, 0.03D);
        }
    }

    private double sampleSafeGroundY(ServerLevel sl, double x, double z, double refY) {
        int bx = Mth.floor(x);
        int bz = Mth.floor(z);
        int startY = Mth.floor(refY) + 4;
        int minY = Math.max(sl.getMinBuildHeight(), Mth.floor(refY) - 6);
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int y = startY; y >= minY; y--) {
            pos.set(bx, y, bz);
            BlockState st = sl.getBlockState(pos);
            if (!st.getCollisionShape(sl, pos).isEmpty()) {
                if (sl.getBlockState(pos.above()).getCollisionShape(sl, pos.above()).isEmpty()
                        && sl.getBlockState(pos.above(2)).getCollisionShape(sl, pos.above(2)).isEmpty()
                        && sl.getBlockState(pos.above(3)).getCollisionShape(sl, pos.above(3)).isEmpty()) {
                    return y + st.getCollisionShape(sl, pos).max(Direction.Axis.Y);
                }
            }
        }
        return refY;
    }

    private void lookAtTargetHead(LivingEntity target) {
        double dx = target.getX() - getX();
        double dy = (target.getEyeY() - (getY() + 3.2D));
        double dz = target.getZ() - getZ();
        double horiz = Math.sqrt(dx * dx + dz * dz);
        float desiredYaw = (float) (Mth.atan2(dz, dx) * Mth.RAD_TO_DEG) - 90.0F;
        float desiredPitch = (float) (-(Mth.atan2(dy, Math.max(0.1D, horiz)) * Mth.RAD_TO_DEG));
        float yawDelta = Mth.wrapDegrees(desiredYaw - yHeadRot);
        yHeadRot += Mth.clamp(yawDelta, -10.0F, 10.0F);
        setXRot(Mth.lerp(0.25F, getXRot(), Mth.clamp(desiredPitch, -20.0F, 20.0F)));
    }

    private void faceTargetSmooth(LivingEntity target, float maxTurnDeg) {
        double dx = target.getX() - getX();
        double dz = target.getZ() - getZ();
        float desiredYaw = (float) (Mth.atan2(dz, dx) * Mth.RAD_TO_DEG) - 90.0F;
        float delta = Mth.wrapDegrees(desiredYaw - getYRot());
        // Responsive proportional turn so boss tracks targets briskly without micro-jitter
        float step = Mth.clamp(delta * 0.45F, -maxTurnDeg, maxTurnDeg);
        float next = getYRot() + step;
        setYRot(next);
        yBodyRot = next;
    }

    private LivingEntity findCombatTarget(ServerLevel sl) {
        List<Player> players = sl.getEntitiesOfClass(Player.class, getBoundingBox().inflate(40.0D),
                p -> p.isAlive() && !p.isCreative() && !p.isSpectator());
        Player closestPlayer = null;
        double best = Double.MAX_VALUE;
        for (Player p : players) {
            double d = p.distanceToSqr(this);
            if (d < best) {
                best = d;
                closestPlayer = p;
            }
        }
        if (closestPlayer != null) {
            return closestPlayer;
        }

        LivingEntity cur = getTarget();
        if (isValidCombatTarget(cur)) {
            return cur;
        }

        LivingEntity lastAttacker = getLastHurtByMob();
        if (isValidCombatTarget(lastAttacker)) {
            return lastAttacker;
        }

        List<Mob> aggroMobs = sl.getEntitiesOfClass(Mob.class, getBoundingBox().inflate(36.0D),
                m -> isValidCombatTarget(m) && m.getTarget() == this);
        if (!aggroMobs.isEmpty()) {
            Mob closestMob = aggroMobs.get(0);
            double bestMobDist = closestMob.distanceToSqr(this);
            for (int i = 1; i < aggroMobs.size(); i++) {
                double d = aggroMobs.get(i).distanceToSqr(this);
                if (d < bestMobDist) {
                    bestMobDist = d;
                    closestMob = aggroMobs.get(i);
                }
            }
            return closestMob;
        }
        return null;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("BossState", getBossState());
        tag.putInt("Infusion", getInfusion());
        if (homePos != null) {
            tag.putDouble("HomeX", homePos.x);
            tag.putDouble("HomeY", homePos.y);
            tag.putDouble("HomeZ", homePos.z);
            tag.putFloat("SavedSpawnYaw", savedSpawnYaw);
        }
        tag.putInt("AttackCooldown", attackCooldown);
        tag.putInt("ShowerCooldown", showerCooldown);
        tag.putInt("SpringCooldown", springCooldown);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Infusion")) {
            entityData.set(DATA_INFUSION, Mth.clamp(tag.getInt("Infusion"), 0, 3));
            infusionInitialized = true;
        }
        if (tag.contains("HomeX") && tag.contains("HomeY") && tag.contains("HomeZ")) {
            homePos = new Vec3(tag.getDouble("HomeX"), tag.getDouble("HomeY"), tag.getDouble("HomeZ"));
            savedSpawnYaw = tag.getFloat("SavedSpawnYaw");
        }
        if (tag.contains("BossState") && tag.getInt("BossState") != STATE_DEATH) {
            entityData.set(DATA_STATE, tag.getInt("BossState"));
        }
        attackCooldown = tag.getInt("AttackCooldown");
        showerCooldown = tag.getInt("ShowerCooldown");
        if (tag.contains("SpringCooldown")) springCooldown = tag.getInt("SpringCooldown");
    }
}

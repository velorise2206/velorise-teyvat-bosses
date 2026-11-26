package com.velorise.teyvatbosses.entity;

import com.velorise.teyvatbosses.TeyvatBossesConstants;
import com.velorise.teyvatbosses.client.BossMusicManager;
import com.velorise.teyvatbosses.compat.StavesElementBridge;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
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
    private static final EntityDataAccessor<Integer> DATA_INFUSION =
            SynchedEntityData.defineId(PrimoGeovishapEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DATA_TELEGRAPH_RADIUS =
            SynchedEntityData.defineId(PrimoGeovishapEntity.class, EntityDataSerializers.FLOAT);

    private final ServerBossEvent bossEvent = new ServerBossEvent(
            Component.translatable("entity.velorise_teyvat_bosses.primo_geovishap"),
            BossEvent.BossBarColor.YELLOW,
            BossEvent.BossBarOverlay.NOTCHED_10
    );

    // Canonical Genshin per-skill cooldowns (in ticks, 20 ticks = 1s)
    private int stateTick = 0;
    private int attackCooldown = 44;         // Global action interval (~2.2–3.2s)
    private int showerCooldown = 400;        // Initial 20s CD (canonical Genshin)
    private int springCooldown = 0;          // Near-death (<=20% HP) Primeval Spring CD (20-40s)
    private int whirlwindCooldown = 0;       // Anti-flank Whirlwind CD (5s)
    private int burrowCooldown = 160;        // Burrow Close (20s) / Far (5s)
    private int breathCooldown = 180;        // Elemental Breath CD (15-24s)
    private int comboCooldown = 140;         // Combo Slash CD (15-22s)
    private int scatteredCooldown = 100;     // Scattered Rocks CD (10-16s)

    private int lastAttackAnim = -1;
    private int noTargetTicks = 0;
    private boolean showerCountered = false;
    private boolean spawnedBlossom = false;
    private Vec3 homePos = null;
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
                .add(Attributes.STEP_HEIGHT, 1.5D)
                .add(Attributes.FOLLOW_RANGE, 44.0D);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_STATE, STATE_DORMANT);
        builder.define(DATA_ANIM, ANIM_IDLE);
        builder.define(DATA_ANIM_TICK, 0);
        builder.define(DATA_INFUSION, INFUSION_PYRO);
        builder.define(DATA_TELEGRAPH_RADIUS, 0.0F);
    }

    public int getBossState() { return entityData.get(DATA_STATE); }
    public int getAnimId() { return entityData.get(DATA_ANIM); }
    public int getAnimTick() { return entityData.get(DATA_ANIM_TICK); }
    public int getInfusion() { return entityData.get(DATA_INFUSION); }
    public float getTelegraphRadius() { return entityData.get(DATA_TELEGRAPH_RADIUS); }

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
            case INFUSION_HYDRO -> new int[]{75, 195, 255};
            case INFUSION_ELECTRO -> new int[]{205, 115, 255};
            case INFUSION_CRYO -> new int[]{140, 240, 255};
            default -> new int[]{255, 145, 55};
        };
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
            default -> "idle";
        };
    }

    @Override
    protected void registerGoals() {
        // Fully deterministic canonical boss state machine — no vanilla Wander/Melee goals
    }

    @Override
    public boolean removeWhenFarAway(double distSq) { return false; }

    @Override
    public boolean isPushable() { return false; }

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
        entityData.set(DATA_TELEGRAPH_RADIUS, 0.0F);
        stateTick = 0;
        showerCountered = false;
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
            BossMusicManager.update(this, BOSS_MUSIC_ID,
                    getBossState() == STATE_AWAKEN || getBossState() == STATE_COMBAT || getBossState() == STATE_STAGGER);
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
            if (tickCount <= 2) {
                entityData.set(DATA_INFUSION, random.nextInt(4));
            }
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
                sl.playSound(null, getX(), getY(), getZ(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 1.4F, 0.50F);
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
            setDeltaMovement(0.0D, getDeltaMovement().y, 0.0D);
            if (target != null && target.distanceToSqr(this) <= 18.0D * 18.0D) {
                setTarget(target);
                awaken(sl);
            }
            return;
        }

        bossEvent.setVisible(true);
        bossEvent.setProgress(Mth.clamp(getHealth() / getMaxHealth(), 0.0F, 1.0F));

        if (st == STATE_AWAKEN) {
            setDeltaMovement(0.0D, getDeltaMovement().y, 0.0D);
            if (target != null) faceTargetSmooth(target, 4.5F);
            if (stateTick == 36) {
                sl.playSound(null, getX(), getY() + 3.0D, getZ(), SoundEvents.ENDER_DRAGON_GROWL, SoundSource.HOSTILE, 2.0F, 0.58F);
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

        if (target == null) {
            setDeltaMovement(0.0D, getDeltaMovement().y, 0.0D);
            if (getAnimId() != ANIM_IDLE) {
                transitionTo(STATE_COMBAT, ANIM_IDLE);
            }
            if (++noTargetTicks > 200) {
                setHealth(getMaxHealth());
                activeStones.clear();
                activeLavaPools.clear();
                transitionTo(STATE_DORMANT, ANIM_IDLE);
            }
            return;
        }
        noTargetTicks = 0;
        setTarget(target);

        tickCombat(sl, target);
    }

    private void tickCooldowns() {
        if (showerCooldown > 0) showerCooldown--;
        if (springCooldown > 0) springCooldown--;
        if (whirlwindCooldown > 0) whirlwindCooldown--;
        if (burrowCooldown > 0) burrowCooldown--;
        if (breathCooldown > 0) breathCooldown--;
        if (comboCooldown > 0) comboCooldown--;
        if (scatteredCooldown > 0) scatteredCooldown--;
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

    private void spawnInfusedStone(ServerLevel sl, double x, double z, int fuseTicks) {
        double y = sampleSafeGroundY(sl, x, z, getY());
        activeStones.add(new InfusedStoneHazard(new Vec3(x, y, z), fuseTicks, getInfusion()));
        sl.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.DRIPSTONE_BLOCK.defaultBlockState()),
                x, y + 0.4D, z, 8, 0.4D, 0.3D, 0.4D, 0.08D);
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
        if ((this.horizontalCollision || this.isInWall()) && destroyBlocksTick <= 0) {
            destroyBlocksTick = 20;
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

    private void tickCombat(ServerLevel sl, LivingEntity target) {
        if (getAnimId() != ANIM_BURROW) tickAntiTrapBlockBreak(sl);
        int anim = getAnimId();
        double dist = distanceTo(target);

        if (anim == ANIM_IDLE || anim == ANIM_WALK) {
            float relAngle = getRelativeAngleDeg(target);
            // Smooth head look-tracking toward target even before body finishes turning
            lookAtTargetHead(target);

            // Canonical Genshin Anti-Flank AI: Whirlwind ignores Action Interval if player runs behind!
            if (whirlwindCooldown <= 0 && dist <= 10.5D && relAngle >= 115.0F) {
                whirlwindCooldown = 100; // 5s CD
                lastAttackAnim = ANIM_WHIRLWIND;
                transitionTo(STATE_COMBAT, ANIM_WHIRLWIND);
                return;
            }

            // Smooth turn rate; slower when idle for heavy draconic weight, nimble when pursuing
            faceTargetSmooth(target, dist > 7.5D ? 6.5F : 5.0F);
            if (attackCooldown > 0) attackCooldown--;

            // Clean rest & pursuit cadence (scaled for 1.60x canonical body size)
            if (dist > 7.6D) {
                if (anim != ANIM_WALK) {
                    entityData.set(DATA_ANIM, ANIM_WALK);
                    entityData.set(DATA_ANIM_TICK, 0);
                    stateTick = 0;
                }
                Vec3 dir = new Vec3(target.getX() - getX(), 0.0D, target.getZ() - getZ()).normalize();
                double speed = dist > 15.0D ? 0.27D : 0.23D;
                setDeltaMovement(Mth.lerp(0.24D, getDeltaMovement().x, dir.x * speed),
                        getDeltaMovement().y,
                        Mth.lerp(0.24D, getDeltaMovement().z, dir.z * speed));
                if (stateTick % 18 == 9) {
                    sl.playSound(null, getX(), getY(), getZ(), SoundEvents.RAVAGER_STEP, SoundSource.HOSTILE, 1.35F, 0.58F);
                }
            } else {
                if (anim != ANIM_IDLE) {
                    entityData.set(DATA_ANIM, ANIM_IDLE);
                    entityData.set(DATA_ANIM_TICK, 0);
                    stateTick = 0;
                }
                setDeltaMovement(getDeltaMovement().x * 0.55D, getDeltaMovement().y, getDeltaMovement().z * 0.55D);
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
        // Priority 1 (Normal >20% HP): Primordial Shower only when front-facing (0-60 deg) & <=14m
        else if (!nearDeath && showerCooldown <= 0 && getHealth() <= getMaxHealth() * 0.90F
                && dist <= 14.0D && relAngle <= 65.0F) {
            chosen = ANIM_PRIMORDIAL_SHOWER;
            showerCooldown = 400; // Canonical 20s CD
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
            if (dist <= 9.0D) {
                candidates.add(ANIM_RIGHT_CLAW);
                candidates.add(ANIM_LEFT_CLAW);
            }
            if (comboCooldown <= 0 && dist <= 12.5D) {
                candidates.add(ANIM_COMBO_SLASH);
                if (nearDeath) candidates.add(ANIM_COMBO_SLASH); // Extra aggression near death
            }
            if (breathCooldown <= 0 && dist <= 11.5D && relAngle <= 95.0F) {
                candidates.add(ANIM_ELEMENTAL_BREATH);
            }
            if (scatteredCooldown <= 0 && dist >= 7.5D) {
                candidates.add(ANIM_SCATTERED_ROCKS);
            }
            if (candidates.isEmpty()) {
                candidates.add(ANIM_RIGHT_CLAW);
                candidates.add(ANIM_LEFT_CLAW);
            }
            chosen = pickNonRepeating(candidates);
            if (chosen == ANIM_COMBO_SLASH) comboCooldown = 260 + random.nextInt(120);
            else if (chosen == ANIM_ELEMENTAL_BREATH) breathCooldown = 300 + random.nextInt(160);
            else if (chosen == ANIM_SCATTERED_ROCKS) scatteredCooldown = 220 + random.nextInt(100);
        }

        lastAttackAnim = chosen;
        transitionTo(STATE_COMBAT, chosen);

        if (chosen == ANIM_PRIMORDIAL_SHOWER) {
            entityData.set(DATA_TELEGRAPH_RADIUS, 15.0F);
            sl.playSound(null, getX(), getY() + 2.8D, getZ(), SoundEvents.WITHER_SPAWN, SoundSource.HOSTILE, 1.5F, 0.70F);
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

    /** Finishes an attack and enforces a clean Genshin-style action interval (2.2s–3.2s, faster near death). */
    private void finishAttack(int baseRestTicks) {
        int scaled = isNearDeathPhase() ? Math.max(28, (int) (baseRestTicks * 0.78F)) : baseRestTicks;
        attackCooldown = scaled + random.nextInt(20);
        transitionTo(STATE_COMBAT, ANIM_IDLE);
    }

    private void tickSingleClaw(ServerLevel sl, LivingEntity target, boolean left) {
        setDeltaMovement(getDeltaMovement().x * 0.6D, getDeltaMovement().y, getDeltaMovement().z * 0.6D);
        if (stateTick < 14) faceTargetSmooth(target, 7.5F);
        if (stateTick == 15) {
            sl.playSound(null, getX(), getY() + 2.0D, getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.HOSTILE, 1.8F, 0.52F);
            // Canonical Left Claw hits on impact (35%) and again on pull-out (35%); Right Claw deals 40% once
            dealConeDamage(sl, 8.2D, 80.0D, left ? 16.0F : 19.5F, 1.30D, false);
            Vec3 fwd = Vec3.directionFromRotation(0.0F, getYRot()).scale(4.4D);
            sl.sendParticles(ParticleTypes.SWEEP_ATTACK, getX() + fwd.x, getY() + 1.8D, getZ() + fwd.z, 4, 1.1D, 0.4D, 1.1D, 0.0D);
        } else if (left && stateTick == 28) {
            // Canonical Genshin Left Claw pull-out ground rupture!
            Vec3 fwd = Vec3.directionFromRotation(0.0F, getYRot()).scale(4.6D);
            Vec3 rupturePos = position().add(fwd);
            sl.playSound(null, rupturePos.x, getY(), rupturePos.z, SoundEvents.STONE_BREAK, SoundSource.HOSTILE, 1.6F, 0.55F);
            sl.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.COBBLESTONE.defaultBlockState()),
                    rupturePos.x, getY() + 0.5D, rupturePos.z, 22, 1.4D, 0.5D, 1.4D, 0.14D);
            dealPointRadialDamage(sl, rupturePos, 4.6D, 15.0F, 0.95D, 0.48D, false);
        }
        if (stateTick >= 44) finishAttack(44);
    }

    private void tickComboSlash(ServerLevel sl, LivingEntity target) {
        setDeltaMovement(getDeltaMovement().x * 0.6D, getDeltaMovement().y, getDeltaMovement().z * 0.6D);
        if (stateTick < 13 || (stateTick >= 18 && stateTick < 28) || (stateTick >= 34 && stateTick < 46)) {
            faceTargetSmooth(target, 7.5F);
        }
        // Subtle forward step lunge during strikes
        if (stateTick == 13 || stateTick == 28 || stateTick == 46) {
            Vec3 lunge = Vec3.directionFromRotation(0.0F, getYRot()).scale(0.45D);
            setDeltaMovement(lunge.x, getDeltaMovement().y, lunge.z);
        }
        if (stateTick == 14) {
            sl.playSound(null, getX(), getY() + 2.0D, getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.HOSTILE, 1.6F, 0.60F);
            dealConeDamage(sl, 8.2D, 80.0D, 16.5F, 1.05D, false);
        } else if (stateTick == 29) {
            sl.playSound(null, getX(), getY() + 2.0D, getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.HOSTILE, 1.6F, 0.54F);
            dealConeDamage(sl, 8.5D, 80.0D, 19.0F, 1.15D, false);
        } else if (stateTick == 48) {
            sl.playSound(null, getX(), getY(), getZ(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 2.0F, 0.50F);
            dealRadialDamage(sl, 9.6D, 27.5F, 1.60D, 0.70D, false);
            spawnGroundRing(sl, 8.2D, 48, ParticleTypes.POOF);
            sl.sendParticles(ParticleTypes.EXPLOSION, getX(), getY() + 0.6D, getZ(), 6, 3.0D, 0.4D, 3.0D, 0.0D);
        }
        if (stateTick >= 72) finishAttack(52);
    }

    private void tickWhirlwind(ServerLevel sl, LivingEntity target) {
        setDeltaMovement(getDeltaMovement().x * 0.65D, getDeltaMovement().y, getDeltaMovement().z * 0.65D);
        if (stateTick < 11) faceTargetSmooth(target, 6.0F);
        if (stateTick == 20 || stateTick == 32) {
            sl.playSound(null, getX(), getY() + 2.0D, getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.HOSTILE, 1.9F, 0.46F);
            dealRadialDamage(sl, 9.5D, 19.5F, 1.55D, 0.52D, false);
            spawnGroundRing(sl, 8.6D, 40, ParticleTypes.CLOUD);
        }
        if (stateTick >= 60) finishAttack(48);
    }

    private void tickBurrow(ServerLevel sl, LivingEntity target) {
        setDeltaMovement(Vec3.ZERO);
        if (stateTick < 16) faceTargetSmooth(target, 6.5F);
        if (stateTick == 22) {
            burrowStart = position();
            sl.playSound(null, getX(), getY(), getZ(), SoundEvents.STONE_BREAK, SoundSource.HOSTILE, 2.0F, 0.52F);
            spawnGroundRing(sl, 4.8D, 30, new BlockParticleOption(ParticleTypes.BLOCK, Blocks.STONE.defaultBlockState()));
        } else if (stateTick >= 26 && stateTick <= 42 && stateTick % 5 == 0) {
            // Canonical Genshin Burrow: dig along ground toward predicted target, dropping Elemental-Infused Stones & dealing contact DMG
            double frac = (stateTick - 24) / 20.0D;
            Vec3 trailPos = burrowStart.lerp(target.position(), Mth.clamp(frac, 0.15D, 0.85D));
            spawnInfusedStone(sl, trailPos.x + (random.nextDouble() - 0.5D) * 2.5D,
                    trailPos.z + (random.nextDouble() - 0.5D) * 2.5D, 34);
            dealPointRadialDamage(sl, trailPos, 3.6D, 11.0F, 0.9D, 0.4D, false);
        } else if (stateTick == 44) {
            // Predictive intercept on target with collision-safe ground sample
            Vec3 predicted = target.position().add(target.getDeltaMovement().multiply(8.0D, 0.0D, 8.0D));
            double gy = sampleSafeGroundY(sl, predicted.x, predicted.z, target.getY());
            burrowDest = new Vec3(predicted.x, gy, predicted.z);
            this.move(MoverType.SELF, burrowDest.subtract(position()));
            entityData.set(DATA_TELEGRAPH_RADIUS, 9.2F);
            sl.playSound(null, getX(), getY(), getZ(), SoundEvents.RAVAGER_STEP, SoundSource.HOSTILE, 1.8F, 0.45F);
        } else if (stateTick >= 45 && stateTick < 62 && stateTick % 4 == 0) {
            sl.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.DIRT.defaultBlockState()),
                    getX(), getY() + 0.4D, getZ(), 18, 2.6D, 0.3D, 2.6D, 0.09D);
        } else if (stateTick == 64) {
            entityData.set(DATA_TELEGRAPH_RADIUS, 0.0F);
            sl.playSound(null, getX(), getY(), getZ(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 2.1F, 0.52F);
            dealRadialDamage(sl, 9.4D, 26.5F, 1.65D, 0.78D, true);
            spawnGroundRing(sl, 8.4D, 52, new BlockParticleOption(ParticleTypes.BLOCK, Blocks.COBBLESTONE.defaultBlockState()));
        }
        if (stateTick >= 88) finishAttack(52);
    }

    private void tickScatteredRocks(ServerLevel sl, LivingEntity target) {
        setDeltaMovement(getDeltaMovement().x * 0.6D, getDeltaMovement().y, getDeltaMovement().z * 0.6D);
        if (stateTick < 22 || (stateTick >= 28 && stateTick < 41)) faceTargetSmooth(target, 6.5F);
        if (stateTick == 24 || stateTick == 43) {
            sl.playSound(null, getX(), getY(), getZ(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 1.6F, 0.60F);
            Vec3 leadPos = target.position().add(target.getDeltaMovement().multiply(6.0D, 0.0D, 6.0D));
            Vec3 dir = new Vec3(leadPos.x - getX(), 0.0D, leadPos.z - getZ());
            if (dir.lengthSqr() > 0.01D) {
                dir = dir.normalize();
                for (int step = 5; step <= 17; step += 4) {
                    Vec3 p = position().add(dir.scale(step));
                    sl.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.DRIPSTONE_BLOCK.defaultBlockState()),
                            p.x, getY() + 0.7D, p.z, 12, 0.9D, 0.6D, 0.9D, 0.1D);
                    // Spawn canonical delayed Elemental-Infused Stones along the barrage!
                    spawnInfusedStone(sl,
                            p.x + (random.nextDouble() - 0.5D) * 3.0D,
                            p.z + (random.nextDouble() - 0.5D) * 3.0D,
                            36 + random.nextInt(14));
                }
            }
            dealConeDamage(sl, 18.0D, 36.0D, 16.5F, 1.15D, true);
        }
        if (stateTick >= 64) finishAttack(48);
    }

    private void tickElementalBreath(ServerLevel sl, LivingEntity target) {
        setDeltaMovement(getDeltaMovement().x * 0.6D, getDeltaMovement().y, getDeltaMovement().z * 0.6D);
        if (stateTick < 21) faceTargetSmooth(target, 6.5F);
        if (stateTick == 23) {
            sl.playSound(null, getX(), getY() + 2.8D, getZ(), SoundEvents.ENDER_DRAGON_GROWL, SoundSource.HOSTILE, 1.9F, 0.70F);
        }
        if (stateTick >= 23 && stateTick <= 62) {
            float progress = (stateTick - 23) / 39.0F;
            float sweepYaw = getYRot() + Mth.lerp(progress, 38.0F, -38.0F);
            Vec3 aim = Vec3.directionFromRotation(0.0F, sweepYaw);
            for (int d = 4; d <= 16; d += 2) {
                Vec3 pt = position().add(aim.scale(d)).add(0.0D, 2.2D, 0.0D);
                sl.sendParticles(infusionParticle(), pt.x, pt.y, pt.z, 6, 0.75D, 0.6D, 0.75D, 0.05D);
            }
            if ((stateTick - 23) % 6 == 0) {
                dealBeamSweepDamage(sl, aim, 16.5D, 2.8D, 11.5F);
            }
        }
        if (stateTick >= 84) finishAttack(52);
    }

    private void tickPrimordialShower(ServerLevel sl, LivingEntity target) {
        setDeltaMovement(0.0D, getDeltaMovement().y, 0.0D);
        if (stateTick < 38) faceTargetSmooth(target, 5.0F);
        if (stateTick < 46 && stateTick % 4 == 0) {
            spawnGroundRing(sl, 15.0D * (stateTick / 46.0D), 36, infusionParticle());
        }
        if (stateTick == 47) {
            entityData.set(DATA_TELEGRAPH_RADIUS, 0.0F);
            sl.playSound(null, getX(), getY() + 1.5D, getZ(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 2.4F, 0.44F);
            sl.playSound(null, getX(), getY() + 2.8D, getZ(), SoundEvents.ENDER_DRAGON_GROWL, SoundSource.HOSTILE, 2.2F, 0.50F);
            spawnGroundRing(sl, 15.0D, 76, infusionParticle());
            spawnGroundRing(sl, 11.0D, 56, ParticleTypes.EXPLOSION);

            // Canonical Primordial Shower Shield Reflection mechanic
            AABB box = getBoundingBox().inflate(15.0D, 6.0D, 15.0D);
            for (LivingEntity e : sl.getEntitiesOfClass(LivingEntity.class, box)) {
                if (e == this || !e.isAlive() || e.distanceToSqr(this) > 15.0D * 15.0D) continue;
                boolean hasElemShield = StavesElementBridge.hasElementalShield(e);
                boolean hasVanillaShield = (e instanceof Player p && p.isBlocking());
                if ((hasElemShield || hasVanillaShield) && !showerCountered) {
                    showerCountered = true;
                    // Canonical Genshin: 15% Max HP with elemental/Geo shield, 10% with standard shield
                    float reflectDmg = getMaxHealth() * (hasElemShield ? 0.15F : 0.10F);
                    super.hurt(sl.damageSources().thorns(e), reflectDmg);
                    sl.playSound(null, e.getX(), e.getY() + 1.2D, e.getZ(), SoundEvents.SHIELD_BLOCK, SoundSource.PLAYERS, 2.2F, 0.85F);
                    sl.playSound(null, getX(), getY() + 2.5D, getZ(), SoundEvents.ANVIL_LAND, SoundSource.HOSTILE, 2.0F, 0.52F);
                    sl.sendParticles(ParticleTypes.FLASH, getX(), getY() + 2.8D, getZ(), 3, 0.7D, 0.7D, 0.7D, 0.0D);
                    if (e instanceof Player p) {
                        p.displayClientMessage(Component.translatable("message.velorise_teyvat_bosses.primo_geovishap.countered")
                                .withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD), true);
                    }
                    transitionTo(STATE_STAGGER, ANIM_COUNTERED_STAGGER);
                    return;
                }
                applyElementalOrPhysicalHit(sl, e, 34.0F, true);
                Vec3 push = e.position().subtract(position()).multiply(1.0D, 0.0D, 1.0D);
                if (push.lengthSqr() > 1.0E-4D) push = push.normalize().scale(1.80D);
                e.setDeltaMovement(push.x, 0.72D, push.z);
                e.hurtMarked = true;
            }
        }
        if (stateTick >= 92) finishAttack(60);
    }

    /**
     * Canonical Genshin Near-Death (<=20% HP) Primeval Spring:
     * slams the ground and erupts a dense ring of delayed Elemental-Infused Stones around the arena!
     */
    private void tickPrimevalSpring(ServerLevel sl, LivingEntity target) {
        setDeltaMovement(getDeltaMovement().x * 0.6D, getDeltaMovement().y, getDeltaMovement().z * 0.6D);
        if (stateTick < 20) faceTargetSmooth(target, 6.5F);
        if (stateTick == 24) {
            sl.playSound(null, getX(), getY(), getZ(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 1.9F, 0.52F);
            dealRadialDamage(sl, 10.2D, 22.0F, 1.40D, 0.68D, false);
            spawnGroundRing(sl, 9.2D, 52, new BlockParticleOption(ParticleTypes.BLOCK, Blocks.STONE.defaultBlockState()));

            // Spawn a barrage of 8 Elemental-Infused Stones around the boss + 2 near the target
            for (int i = 0; i < 8; i++) {
                double ang = (Math.PI * 2.0D * i) / 8.0D + random.nextDouble() * 0.35D;
                double dist = 6.5D + random.nextDouble() * 7.5D;
                spawnInfusedStone(sl, getX() + Math.cos(ang) * dist, getZ() + Math.sin(ang) * dist, 38 + i * 3);
            }
            spawnInfusedStone(sl, target.getX() + 1.5D, target.getZ(), 42);
            spawnInfusedStone(sl, target.getX() - 1.5D, target.getZ(), 48);
        }
        if (stateTick >= 68) finishAttack(48);
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
        if (elemental && StavesElementBridge.isAvailable()) {
            StavesElementBridge.dealElementalDamage(target, this, getInfusionElementName(), dmg, 1.0F, false);
        } else {
            target.hurt(sl.damageSources().mobAttack(this), dmg);
        }
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
        // Smooth proportional turn so small adjustments glide effortlessly without micro-jitter
        float step = Mth.clamp(delta * 0.35F, -maxTurnDeg, maxTurnDeg);
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
        tag.putInt("AttackCooldown", attackCooldown);
        tag.putInt("ShowerCooldown", showerCooldown);
        tag.putInt("SpringCooldown", springCooldown);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Infusion")) entityData.set(DATA_INFUSION, tag.getInt("Infusion"));
        if (tag.contains("BossState") && tag.getInt("BossState") != STATE_DEATH) {
            entityData.set(DATA_STATE, tag.getInt("BossState"));
        }
        attackCooldown = tag.getInt("AttackCooldown");
        showerCooldown = tag.getInt("ShowerCooldown");
        if (tag.contains("SpringCooldown")) springCooldown = tag.getInt("SpringCooldown");
    }
}

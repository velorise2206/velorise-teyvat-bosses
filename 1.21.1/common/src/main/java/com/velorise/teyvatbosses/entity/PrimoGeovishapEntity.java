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

import java.util.List;

/**
 * AAA Geo World Boss: <b>Primo Geovishap</b> — Sovereign of the Ancient Veins.
 *
 * <p>Features full 14-clip Cataclysm-grade skeletal animation synchronization, Genshin-faithful
 * attack/rest cadence (2.4–3.6s recovery windows between attacks, no per-hit damage cap),
 * elemental infusion (Pyro / Hydro / Electro / Cryo), and signature <b>Primordial Shower</b>
 * shield-counter stagger reflection.
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

    private int stateTick = 0;
    private int attackCooldown = 48; // 2.4s initial breathing room after awakening
    private int showerCooldown = 260;
    private int burrowCooldown = 200;
    private int lastAttackAnim = -1;
    private int noTargetTicks = 0;
    private boolean showerCountered = false;
    private boolean spawnedBlossom = false;
    private Vec3 homePos = null;
    private Vec3 burrowDest = Vec3.ZERO;

    public PrimoGeovishapEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.xpReward = 220;
        this.setPersistenceRequired();
        this.bossEvent.setVisible(false);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 640.0D)
                .add(Attributes.ARMOR, 10.0D)
                .add(Attributes.ARMOR_TOUGHNESS, 6.0D)
                .add(Attributes.ATTACK_DAMAGE, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.24D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.FOLLOW_RANGE, 40.0D);
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
        // Fully deterministic boss state machine — no vanilla Wander/Melee goals
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

    @Override
    public boolean hurt(DamageSource source, float amount) {
        int st = getBossState();
        if (st == STATE_DEATH) return false;
        // Invulnerable while deep underground during burrow
        if (st == STATE_COMBAT && getAnimId() == ANIM_BURROW && stateTick >= 26 && stateTick <= 56) {
            return false;
        }
        if (st == STATE_DORMANT && level() instanceof ServerLevel sl) {
            if (source.getEntity() instanceof LivingEntity attacker) {
                setTarget(attacker);
            }
            awaken(sl);
            amount *= 0.5F;
        }
        // Take 25% bonus damage while staggered after Primordial Shower reflection!
        if (st == STATE_STAGGER) {
            amount *= 1.25F;
        } else if (st == STATE_COMBAT && getAnimId() == ANIM_PRIMORDIAL_SHOWER) {
            // Slight Geo armor poise during ultimate charge
            amount *= 0.80F;
        }
        return super.hurt(source, amount);
    }

    @Override
    public void die(DamageSource source) {
        if (getBossState() != STATE_DEATH) {
            this.setHealth(1.0F);
            transitionTo(STATE_DEATH, ANIM_DEATH);
            if (level() instanceof ServerLevel sl) {
                sl.playSound(null, getX(), getY() + 2.0D, getZ(), SoundEvents.ENDER_DRAGON_GROWL, SoundSource.HOSTILE, 1.8F, 0.55F);
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
        sl.playSound(null, getX(), getY() + 2.0D, getZ(), SoundEvents.RAVAGER_ROAR, SoundSource.HOSTILE, 1.8F, 0.58F);
        sl.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.STONE.defaultBlockState()),
                getX(), getY() + 0.5D, getZ(), 36, 1.8D, 0.6D, 1.8D, 0.12D);
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide()) {
            BossMusicManager.update(this, BOSS_MUSIC_ID,
                    getBossState() == STATE_AWAKEN || getBossState() == STATE_COMBAT || getBossState() == STATE_STAGGER);
            if (tickCount % 6 == 0 && getBossState() != STATE_DEATH) {
                ParticleOptions p = infusionParticle();
                level().addParticle(p,
                        getX() + (random.nextDouble() - 0.5D) * 2.4D,
                        getY() + 1.4D + random.nextDouble() * 1.4D,
                        getZ() + (random.nextDouble() - 0.5D) * 2.4D,
                        0.0D, 0.03D, 0.0D);
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
        if (showerCooldown > 0) showerCooldown--;
        if (burrowCooldown > 0) burrowCooldown--;

        int st = getBossState();
        if (st == STATE_DEATH) {
            setDeltaMovement(0.0D, getDeltaMovement().y, 0.0D);
            bossEvent.setVisible(false);
            if (stateTick == 26 || stateTick == 48) {
                sl.playSound(null, getX(), getY(), getZ(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 1.2F, 0.52F);
                sl.sendParticles(ParticleTypes.POOF, getX(), getY() + 0.5D, getZ(), 24, 2.0D, 0.3D, 2.0D, 0.06D);
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
            if (target != null && target.distanceToSqr(this) <= 16.0D * 16.0D) {
                setTarget(target);
                awaken(sl);
            }
            return;
        }

        bossEvent.setVisible(true);
        bossEvent.setProgress(Mth.clamp(getHealth() / getMaxHealth(), 0.0F, 1.0F));

        if (st == STATE_AWAKEN) {
            setDeltaMovement(0.0D, getDeltaMovement().y, 0.0D);
            if (target != null) faceTarget(target, 5.0F);
            if (stateTick == 36) {
                sl.playSound(null, getX(), getY() + 2.2D, getZ(), SoundEvents.ENDER_DRAGON_GROWL, SoundSource.HOSTILE, 1.9F, 0.62F);
                sl.sendParticles(ParticleTypes.POOF, getX(), getY() + 1.5D, getZ(), 32, 2.0D, 1.2D, 2.0D, 0.08D);
            } else if (stateTick == 59) {
                sl.playSound(null, getX(), getY(), getZ(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 1.4F, 0.58F);
            }
            if (stateTick >= 68) {
                attackCooldown = 44; // Clean 2.2s breather before first attack
                transitionTo(STATE_COMBAT, ANIM_IDLE);
            }
            return;
        }

        if (st == STATE_STAGGER) {
            setDeltaMovement(getDeltaMovement().x * 0.7D, getDeltaMovement().y, getDeltaMovement().z * 0.7D);
            if (stateTick == 17) {
                sl.playSound(null, getX(), getY(), getZ(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 1.3F, 0.55F);
                sl.sendParticles(ParticleTypes.POOF, getX(), getY() + 0.4D, getZ(), 24, 2.0D, 0.3D, 2.0D, 0.07D);
            }
            if (stateTick >= 72) {
                attackCooldown = 50; // 2.5s rest after recovering from stagger
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
                transitionTo(STATE_DORMANT, ANIM_IDLE);
            }
            return;
        }
        noTargetTicks = 0;
        setTarget(target);

        tickCombat(sl, target);
    }

    private void tickCombat(ServerLevel sl, LivingEntity target) {
        int anim = getAnimId();
        double dist = distanceTo(target);

        if (anim == ANIM_IDLE || anim == ANIM_WALK) {
            faceTarget(target, 6.5F);
            if (attackCooldown > 0) attackCooldown--;

            // Clean rest & repositioning phase between attacks
            if (dist > 5.8D) {
                if (anim != ANIM_WALK) {
                    entityData.set(DATA_ANIM, ANIM_WALK);
                    entityData.set(DATA_ANIM_TICK, 0);
                    stateTick = 0;
                }
                Vec3 dir = new Vec3(target.getX() - getX(), 0.0D, target.getZ() - getZ()).normalize();
                double speed = 0.21D;
                setDeltaMovement(Mth.lerp(0.28D, getDeltaMovement().x, dir.x * speed),
                        getDeltaMovement().y,
                        Mth.lerp(0.28D, getDeltaMovement().z, dir.z * speed));
                if (stateTick % 18 == 9) {
                    sl.playSound(null, getX(), getY(), getZ(), SoundEvents.RAVAGER_STEP, SoundSource.HOSTILE, 1.1F, 0.65F);
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
                chooseNextAttack(sl, target, dist);
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
            default -> finishAttack(48);
        }
    }

    private void chooseNextAttack(ServerLevel sl, LivingEntity target, double dist) {
        int chosen;
        // Signature Primordial Shower when ready & HP < 85%
        if (showerCooldown <= 0 && getHealth() <= getMaxHealth() * 0.85F) {
            chosen = ANIM_PRIMORDIAL_SHOWER;
            showerCooldown = 360;
        } else if (dist > 11.5D) {
            if (burrowCooldown <= 0 && random.nextFloat() < 0.45F) {
                chosen = ANIM_BURROW;
                burrowCooldown = 260;
            } else {
                chosen = random.nextBoolean() ? ANIM_SCATTERED_ROCKS : ANIM_ELEMENTAL_BREATH;
            }
        } else if (dist > 6.5D) {
            int[] midPool = {ANIM_ELEMENTAL_BREATH, ANIM_SCATTERED_ROCKS, ANIM_PRIMEVAL_SPRING, ANIM_COMBO_SLASH};
            chosen = pickNonRepeating(midPool);
        } else {
            int[] meleePool = {ANIM_LEFT_CLAW, ANIM_RIGHT_CLAW, ANIM_COMBO_SLASH, ANIM_WHIRLWIND, ANIM_PRIMEVAL_SPRING};
            chosen = pickNonRepeating(meleePool);
        }
        lastAttackAnim = chosen;
        transitionTo(STATE_COMBAT, chosen);

        if (chosen == ANIM_PRIMORDIAL_SHOWER) {
            entityData.set(DATA_TELEGRAPH_RADIUS, 11.5F);
            sl.playSound(null, getX(), getY() + 2.0D, getZ(), SoundEvents.WITHER_SPAWN, SoundSource.HOSTILE, 1.3F, 0.72F);
            for (Player p : sl.getEntitiesOfClass(Player.class, getBoundingBox().inflate(32.0D))) {
                p.displayClientMessage(Component.translatable("message.velorise_teyvat_bosses.primo_geovishap.shower_warn")
                        .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD), true);
            }
        } else if (chosen == ANIM_BURROW) {
            burrowDest = target.position();
        }
    }

    private int pickNonRepeating(int[] pool) {
        int c = pool[random.nextInt(pool.length)];
        if (c == lastAttackAnim && pool.length > 1) {
            c = pool[(random.nextInt(pool.length - 1) + 1) % pool.length];
        }
        return c;
    }

    /** Finishes an attack and enforces a clean Genshin-style rest/breathing window (2.4s–3.6s). */
    private void finishAttack(int baseRestTicks) {
        attackCooldown = baseRestTicks + random.nextInt(24);
        transitionTo(STATE_COMBAT, ANIM_IDLE);
    }

    private void tickSingleClaw(ServerLevel sl, LivingEntity target, boolean left) {
        setDeltaMovement(getDeltaMovement().x * 0.6D, getDeltaMovement().y, getDeltaMovement().z * 0.6D);
        if (stateTick < 14) faceTarget(target, 7.5F);
        if (stateTick == 15) {
            sl.playSound(null, getX(), getY() + 1.5D, getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.HOSTILE, 1.6F, 0.55F);
            dealConeDamage(sl, 6.2D, 75.0D, 19.0F, 1.25D, false);
            Vec3 fwd = Vec3.directionFromRotation(0.0F, getYRot()).scale(3.0D);
            sl.sendParticles(ParticleTypes.SWEEP_ATTACK, getX() + fwd.x, getY() + 1.4D, getZ() + fwd.z, 3, 0.8D, 0.3D, 0.8D, 0.0D);
        }
        if (stateTick >= 44) finishAttack(48); // 2.4s–3.6s rest
    }

    private void tickComboSlash(ServerLevel sl, LivingEntity target) {
        setDeltaMovement(getDeltaMovement().x * 0.6D, getDeltaMovement().y, getDeltaMovement().z * 0.6D);
        if (stateTick < 13 || (stateTick >= 18 && stateTick < 28) || (stateTick >= 34 && stateTick < 46)) {
            faceTarget(target, 7.0F);
        }
        if (stateTick == 14) {
            sl.playSound(null, getX(), getY() + 1.5D, getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.HOSTILE, 1.5F, 0.62F);
            dealConeDamage(sl, 6.0D, 75.0D, 16.0F, 0.95D, false);
        } else if (stateTick == 29) {
            sl.playSound(null, getX(), getY() + 1.5D, getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.HOSTILE, 1.5F, 0.56F);
            dealConeDamage(sl, 6.2D, 75.0D, 17.0F, 1.05D, false);
        } else if (stateTick == 48) {
            sl.playSound(null, getX(), getY(), getZ(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 1.8F, 0.52F);
            dealRadialDamage(sl, 7.2D, 27.0F, 1.50D, 0.65D, false);
            spawnGroundRing(sl, 6.0D, 40, ParticleTypes.POOF);
            sl.sendParticles(ParticleTypes.EXPLOSION, getX(), getY() + 0.5D, getZ(), 5, 2.2D, 0.3D, 2.2D, 0.0D);
        }
        if (stateTick >= 72) finishAttack(56); // 2.8s–4.0s rest after heavy 3-hit combo
    }

    private void tickWhirlwind(ServerLevel sl, LivingEntity target) {
        setDeltaMovement(getDeltaMovement().x * 0.65D, getDeltaMovement().y, getDeltaMovement().z * 0.65D);
        if (stateTick < 11) faceTarget(target, 6.0F);
        if (stateTick == 20 || stateTick == 32) {
            sl.playSound(null, getX(), getY() + 1.5D, getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.HOSTILE, 1.7F, 0.48F);
            dealRadialDamage(sl, 6.8D, 18.0F, 1.45D, 0.48D, false);
            spawnGroundRing(sl, 6.2D, 32, ParticleTypes.CLOUD);
        }
        if (stateTick >= 60) finishAttack(52);
    }

    private void tickBurrow(ServerLevel sl, LivingEntity target) {
        setDeltaMovement(Vec3.ZERO);
        if (stateTick < 16) faceTarget(target, 6.0F);
        if (stateTick == 22) {
            sl.playSound(null, getX(), getY(), getZ(), SoundEvents.STONE_BREAK, SoundSource.HOSTILE, 1.8F, 0.55F);
            spawnGroundRing(sl, 3.6D, 24, new BlockParticleOption(ParticleTypes.BLOCK, Blocks.STONE.defaultBlockState()));
        } else if (stateTick == 44) {
            // Relocate smoothly underground toward target using collision-safe ground sample
            Vec3 dest = target.position();
            double gy = sampleSafeGroundY(sl, dest.x, dest.z, dest.y);
            burrowDest = new Vec3(dest.x, gy, dest.z);
            this.move(MoverType.SELF, burrowDest.subtract(position()));
            entityData.set(DATA_TELEGRAPH_RADIUS, 6.8F);
            sl.playSound(null, getX(), getY(), getZ(), SoundEvents.RAVAGER_STEP, SoundSource.HOSTILE, 1.6F, 0.45F);
        } else if (stateTick >= 45 && stateTick < 62 && stateTick % 4 == 0) {
            sl.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.DIRT.defaultBlockState()),
                    getX(), getY() + 0.3D, getZ(), 14, 1.8D, 0.2D, 1.8D, 0.08D);
        } else if (stateTick == 64) {
            entityData.set(DATA_TELEGRAPH_RADIUS, 0.0F);
            sl.playSound(null, getX(), getY(), getZ(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 1.9F, 0.55F);
            dealRadialDamage(sl, 7.0D, 25.0F, 1.55D, 0.72D, true);
            spawnGroundRing(sl, 6.2D, 44, new BlockParticleOption(ParticleTypes.BLOCK, Blocks.COBBLESTONE.defaultBlockState()));
        }
        if (stateTick >= 88) finishAttack(56);
    }

    private void tickScatteredRocks(ServerLevel sl, LivingEntity target) {
        setDeltaMovement(getDeltaMovement().x * 0.6D, getDeltaMovement().y, getDeltaMovement().z * 0.6D);
        if (stateTick < 22 || (stateTick >= 28 && stateTick < 41)) faceTarget(target, 6.5F);
        if (stateTick == 24 || stateTick == 43) {
            sl.playSound(null, getX(), getY(), getZ(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 1.5F, 0.62F);
            Vec3 dir = new Vec3(target.getX() - getX(), 0.0D, target.getZ() - getZ());
            if (dir.lengthSqr() > 0.01D) {
                dir = dir.normalize();
                for (int step = 3; step <= 13; step += 2) {
                    Vec3 p = position().add(dir.scale(step));
                    sl.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.DRIPSTONE_BLOCK.defaultBlockState()),
                            p.x, getY() + 0.6D, p.z, 12, 0.8D, 0.6D, 0.8D, 0.1D);
                    sl.sendParticles(infusionParticle(), p.x, getY() + 1.0D, p.z, 8, 0.6D, 0.5D, 0.6D, 0.05D);
                }
            }
            dealConeDamage(sl, 14.0D, 36.0D, 18.0F, 1.15D, true);
        }
        if (stateTick >= 64) finishAttack(52);
    }

    private void tickElementalBreath(ServerLevel sl, LivingEntity target) {
        setDeltaMovement(getDeltaMovement().x * 0.6D, getDeltaMovement().y, getDeltaMovement().z * 0.6D);
        if (stateTick < 21) faceTarget(target, 6.0F);
        if (stateTick == 23) {
            sl.playSound(null, getX(), getY() + 2.0D, getZ(), SoundEvents.ENDER_DRAGON_GROWL, SoundSource.HOSTILE, 1.7F, 0.72F);
        }
        if (stateTick >= 23 && stateTick <= 62) {
            float progress = (stateTick - 23) / 39.0F;
            float sweepYaw = getYRot() + Mth.lerp(progress, 34.0F, -34.0F);
            Vec3 aim = Vec3.directionFromRotation(0.0F, sweepYaw);
            for (int d = 3; d <= 13; d += 2) {
                Vec3 pt = position().add(aim.scale(d)).add(0.0D, 1.5D, 0.0D);
                sl.sendParticles(infusionParticle(), pt.x, pt.y, pt.z, 5, 0.6D, 0.5D, 0.6D, 0.04D);
            }
            if ((stateTick - 23) % 6 == 0) {
                dealBeamSweepDamage(sl, aim, 13.5D, 2.4D, 11.0F);
            }
        }
        if (stateTick >= 84) finishAttack(56);
    }

    private void tickPrimordialShower(ServerLevel sl, LivingEntity target) {
        setDeltaMovement(0.0D, getDeltaMovement().y, 0.0D);
        if (stateTick < 38) faceTarget(target, 5.0F);
        if (stateTick < 46 && stateTick % 4 == 0) {
            spawnGroundRing(sl, 11.5F * (stateTick / 46.0F), 28, infusionParticle());
        }
        if (stateTick == 47) {
            entityData.set(DATA_TELEGRAPH_RADIUS, 0.0F);
            sl.playSound(null, getX(), getY() + 1.0D, getZ(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 2.2F, 0.45F);
            sl.playSound(null, getX(), getY() + 2.0D, getZ(), SoundEvents.ENDER_DRAGON_GROWL, SoundSource.HOSTILE, 2.0F, 0.52F);
            spawnGroundRing(sl, 11.5D, 64, infusionParticle());
            spawnGroundRing(sl, 8.5D, 48, ParticleTypes.EXPLOSION);

            // Check if any hit player counters Primordial Shower with a Shield!
            AABB box = getBoundingBox().inflate(11.5D, 5.0D, 11.5D);
            for (LivingEntity e : sl.getEntitiesOfClass(LivingEntity.class, box)) {
                if (e == this || !e.isAlive() || e.distanceToSqr(this) > 11.5D * 11.5D) continue;
                boolean shielded = (e instanceof Player p && p.isBlocking()) || StavesElementBridge.hasElementalShield(e);
                if (shielded && !showerCountered) {
                    showerCountered = true;
                    float reflectDmg = getMaxHealth() * 0.12F;
                    super.hurt(sl.damageSources().thorns(e), reflectDmg);
                    sl.playSound(null, e.getX(), e.getY() + 1.0D, e.getZ(), SoundEvents.SHIELD_BLOCK, SoundSource.PLAYERS, 2.0F, 0.85F);
                    sl.playSound(null, getX(), getY() + 1.8D, getZ(), SoundEvents.ANVIL_LAND, SoundSource.HOSTILE, 1.8F, 0.55F);
                    sl.sendParticles(ParticleTypes.FLASH, getX(), getY() + 2.0D, getZ(), 2, 0.5D, 0.5D, 0.5D, 0.0D);
                    if (e instanceof Player p) {
                        p.displayClientMessage(Component.translatable("message.velorise_teyvat_bosses.primo_geovishap.countered")
                                .withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD), true);
                    }
                    transitionTo(STATE_STAGGER, ANIM_COUNTERED_STAGGER);
                    return;
                }
                applyElementalOrPhysicalHit(sl, e, 32.0F, true);
                Vec3 push = e.position().subtract(position()).multiply(1.0D, 0.0D, 1.0D);
                if (push.lengthSqr() > 1.0E-4D) push = push.normalize().scale(1.65D);
                e.setDeltaMovement(push.x, 0.65D, push.z);
                e.hurtMarked = true;
            }
        }
        if (stateTick >= 92) finishAttack(64); // Generous 3.2s–4.4s recovery after ultimate
    }

    private void tickPrimevalSpring(ServerLevel sl, LivingEntity target) {
        setDeltaMovement(getDeltaMovement().x * 0.6D, getDeltaMovement().y, getDeltaMovement().z * 0.6D);
        if (stateTick < 20) faceTarget(target, 6.5F);
        if (stateTick == 24) {
            sl.playSound(null, getX(), getY(), getZ(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 1.75F, 0.54F);
            dealRadialDamage(sl, 8.2D, 24.0F, 1.35D, 0.68D, false);
            spawnGroundRing(sl, 7.6D, 48, new BlockParticleOption(ParticleTypes.BLOCK, Blocks.STONE.defaultBlockState()));
        }
        if (stateTick >= 68) finishAttack(54);
    }

    private void dealRadialDamage(ServerLevel sl, double radius, float dmg, double kb, double lift, boolean elemental) {
        AABB box = getBoundingBox().inflate(radius, 3.5D, radius);
        for (LivingEntity e : sl.getEntitiesOfClass(LivingEntity.class, box)) {
            if (e == this || !e.isAlive() || e.distanceToSqr(this) > radius * radius) continue;
            applyElementalOrPhysicalHit(sl, e, dmg, elemental);
            Vec3 push = e.position().subtract(position()).multiply(1.0D, 0.0D, 1.0D);
            if (push.lengthSqr() > 1.0E-4D) push = push.normalize().scale(kb);
            e.setDeltaMovement(push.x, lift, push.z);
            e.hurtMarked = true;
        }
    }

    private void dealConeDamage(ServerLevel sl, double range, double halfAngleDeg, float dmg, double kb, boolean elemental) {
        Vec3 fwd = Vec3.directionFromRotation(0.0F, getYRot()).normalize();
        double minDot = Math.cos(Math.toRadians(halfAngleDeg));
        AABB box = getBoundingBox().inflate(range, 3.5D, range);
        for (LivingEntity e : sl.getEntitiesOfClass(LivingEntity.class, box)) {
            if (e == this || !e.isAlive()) continue;
            Vec3 to = e.position().subtract(position()).multiply(1.0D, 0.0D, 1.0D);
            double d = to.length();
            if (d > range || d < 0.01D) continue;
            if (to.scale(1.0D / d).dot(fwd) < minDot) continue;
            applyElementalOrPhysicalHit(sl, e, dmg, elemental);
            e.setDeltaMovement(to.scale(kb / d).x, 0.42D, to.scale(kb / d).z);
            e.hurtMarked = true;
        }
    }

    private void dealBeamSweepDamage(ServerLevel sl, Vec3 aimDir, double range, double halfWidth, float dmg) {
        AABB box = getBoundingBox().inflate(range, 3.5D, range);
        for (LivingEntity e : sl.getEntitiesOfClass(LivingEntity.class, box)) {
            if (e == this || !e.isAlive()) continue;
            Vec3 rel = e.position().subtract(position()).multiply(1.0D, 0.0D, 1.0D);
            double along = rel.dot(aimDir);
            if (along < 1.0D || along > range) continue;
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
        return switch (getInfusion()) {
            case INFUSION_HYDRO -> ParticleTypes.SPLASH;
            case INFUSION_ELECTRO -> ParticleTypes.ELECTRIC_SPARK;
            case INFUSION_CRYO -> ParticleTypes.SNOWFLAKE;
            default -> ParticleTypes.FLAME;
        };
    }

    private void spawnGroundRing(ServerLevel sl, double radius, int count, ParticleOptions particle) {
        for (int i = 0; i < count; i++) {
            double a = (Math.PI * 2.0D * i) / count;
            sl.sendParticles(particle, getX() + Math.cos(a) * radius, getY() + 0.35D, getZ() + Math.sin(a) * radius,
                    1, 0.1D, 0.15D, 0.1D, 0.03D);
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

    private void faceTarget(LivingEntity target, float maxTurnDeg) {
        double dx = target.getX() - getX();
        double dz = target.getZ() - getZ();
        float desiredYaw = (float) (Mth.atan2(dz, dx) * Mth.RAD_TO_DEG) - 90.0F;
        float delta = Mth.wrapDegrees(desiredYaw - getYRot());
        float next = getYRot() + Mth.clamp(delta, -maxTurnDeg, maxTurnDeg);
        setYRot(next);
        yBodyRot = next;
        yHeadRot = next;
    }

    private LivingEntity findCombatTarget(ServerLevel sl) {
        List<Player> players = sl.getEntitiesOfClass(Player.class, getBoundingBox().inflate(36.0D),
                p -> p.isAlive() && !p.isCreative() && !p.isSpectator());
        Player closest = null;
        double best = Double.MAX_VALUE;
        for (Player p : players) {
            double d = p.distanceToSqr(this);
            if (d < best) {
                best = d;
                closest = p;
            }
        }
        return closest;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("BossState", getBossState());
        tag.putInt("Infusion", getInfusion());
        tag.putInt("AttackCooldown", attackCooldown);
        tag.putInt("ShowerCooldown", showerCooldown);
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
    }
}

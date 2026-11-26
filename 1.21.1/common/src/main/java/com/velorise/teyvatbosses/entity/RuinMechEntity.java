package com.velorise.teyvatbosses.entity;

import com.velorise.teyvatbosses.compat.StavesElementBridge;
import com.velorise.teyvatbosses.item.RuinMechPartItem;
import com.velorise.teyvatbosses.registry.TeyvatBossesSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PlayerRideableJumping;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Pilotable Ruin Mech assembled outdoors from 5 Ruin Guard salvage parts:
 * Core, Left Arm, Right Arm, Left Leg, Right Leg.
 *
 * <p>Upgraded features:
 * <ul>
 *   <li><b>10-Block Redstone Battery:</b> Holds up to 10 Redstone Blocks (1000 Energy, 100 per block)
 *       loaded through the animated rear reactor hatch ({@code ANIM_RECHARGE_BACK}).</li>
 *   <li><b>Full Ruin Guard Boss Moveset (6 Skills):</b> Double Clap, 3-Hit Heavy Combo, Stomp Shockwave,
 *       Steerable Cyclone Whirlwind Spin, Leap Slam, and 6-Missile Homing Barrage.</li>
 *   <li><b>Authentic 48-Tick Heavy Stride Rhythm:</b> Synced footstep sounds, dust clouds, and camera thump
 *       matching {@code walk_combat} (24 ticks per step).</li>
 *   <li><b>Zero Action-Bar Text Spam:</b> All telemetry (HP, Armor, 10-cell Battery, Skill Cooldowns) is
 *       displayed by the client pixel-art Cockpit Visor HUD.</li>
 * </ul>
 */
public class RuinMechEntity extends Mob implements PlayerRideableJumping {

    public static final int PART_CORE      = 1;
    public static final int PART_LEFT_ARM  = 1 << 1;
    public static final int PART_RIGHT_ARM = 1 << 2;
    public static final int PART_LEFT_LEG  = 1 << 3;
    public static final int PART_RIGHT_LEG = 1 << 4;
    public static final int FULL_PARTS     = PART_CORE | PART_LEFT_ARM | PART_RIGHT_ARM | PART_LEFT_LEG | PART_RIGHT_LEG;

    // Animation IDs
    public static final int ANIM_DORMANT         = 0;
    public static final int ANIM_AWAKEN          = 1;
    public static final int ANIM_IDLE            = 2;
    public static final int ANIM_WALK            = 3;
    public static final int ANIM_DOUBLE_CLAP     = 4;
    public static final int ANIM_STOMP           = 5;
    public static final int ANIM_LEAP_SLAM       = 6;
    public static final int ANIM_MISSILE_SALVO   = 7;
    public static final int ANIM_ENTER_COCKPIT   = 8;
    public static final int ANIM_RECHARGE_BACK   = 9;
    public static final int ANIM_MELEE_3HIT      = 10;
    public static final int ANIM_CYCLONE_WINDUP  = 11;
    public static final int ANIM_CYCLONE_ACTIVE  = 12;
    public static final int ANIM_CYCLONE_RECOVER = 13;

    // 6 Skill IDs (used by C2S RuinMechSkillPayload & HUD)
    public static final int SKILL_DOUBLE_CLAP = 1;
    public static final int SKILL_MELEE_3HIT  = 2;
    public static final int SKILL_STOMP       = 3;
    public static final int SKILL_CYCLONE     = 4;
    public static final int SKILL_LEAP_SLAM   = 5;
    public static final int SKILL_MISSILES    = 6;

    // 10 Redstone Blocks max (100 energy per block = 1000 max energy)
    public static final int   MAX_REDSTONE_CELLS = 10;
    public static final float ENERGY_PER_BLOCK   = 100.0F;
    public static final float MAX_ENERGY         = MAX_REDSTONE_CELLS * ENERGY_PER_BLOCK;
    public static final float MAX_ARMOR          = 100.0F;

    private static final EntityDataAccessor<Integer> DATA_PARTS_MASK =
            SynchedEntityData.defineId(RuinMechEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_ANIM_ID =
            SynchedEntityData.defineId(RuinMechEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_ANIM_EPOCH =
            SynchedEntityData.defineId(RuinMechEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DATA_ENERGY =
            SynchedEntityData.defineId(RuinMechEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_ARMOR =
            SynchedEntityData.defineId(RuinMechEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> DATA_COOLDOWN =
            SynchedEntityData.defineId(RuinMechEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_LOCKED_TARGET_ID =
            SynchedEntityData.defineId(RuinMechEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DATA_LEAP_SX =
            SynchedEntityData.defineId(RuinMechEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_LEAP_SY =
            SynchedEntityData.defineId(RuinMechEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_LEAP_SZ =
            SynchedEntityData.defineId(RuinMechEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_LEAP_TX =
            SynchedEntityData.defineId(RuinMechEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_LEAP_TY =
            SynchedEntityData.defineId(RuinMechEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_LEAP_TZ =
            SynchedEntityData.defineId(RuinMechEntity.class, EntityDataSerializers.FLOAT);

    private int serverAnimTick = 0;
    private int clientAnimTick = 0;
    private int lastSyncedAnimId = -1;
    private int lastSyncedEpoch = -1;
    private int actionCooldown = 0;
    private boolean lastPilotSwinging = false;
    private Vec3 leapStart = Vec3.ZERO;
    private Vec3 leapTarget = Vec3.ZERO;
    private double prevServerX = 0.0D;
    private double prevServerZ = 0.0D;
    private float prevServerYaw = 0.0F;
    private int walkGraceTicks = 0;

    public RuinMechEntity(EntityType<? extends Mob> type, Level level) {
        super(type, level);
        this.setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 360.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.22D)
                .add(Attributes.ARMOR, 14.0D)
                .add(Attributes.ARMOR_TOUGHNESS, 6.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.STEP_HEIGHT, 1.6D);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_PARTS_MASK, FULL_PARTS);
        builder.define(DATA_ANIM_ID, ANIM_DORMANT);
        builder.define(DATA_ANIM_EPOCH, 0);
        builder.define(DATA_ENERGY, 200.0F);
        builder.define(DATA_ARMOR, MAX_ARMOR);
        builder.define(DATA_COOLDOWN, 0);
        builder.define(DATA_LOCKED_TARGET_ID, -1);
        builder.define(DATA_LEAP_SX, 0.0F);
        builder.define(DATA_LEAP_SY, 0.0F);
        builder.define(DATA_LEAP_SZ, 0.0F);
        builder.define(DATA_LEAP_TX, 0.0F);
        builder.define(DATA_LEAP_TY, 0.0F);
        builder.define(DATA_LEAP_TZ, 0.0F);
    }

    public void initAssemblyChassis() {
        entityData.set(DATA_PARTS_MASK, PART_CORE);
        entityData.set(DATA_ENERGY, 0.0F);
        entityData.set(DATA_ARMOR, MAX_ARMOR);
        playAnim(ANIM_DORMANT);
        updateAssemblyLabel();
    }

    public int getPartsMask() { return entityData.get(DATA_PARTS_MASK); }
    public boolean isFullyAssembled() { return (getPartsMask() & FULL_PARTS) == FULL_PARTS; }
    public boolean hasPart(int bit) { return (getPartsMask() & bit) != 0; }
    public int getAnimId() { return entityData.get(DATA_ANIM_ID); }
    public int getAnimEpoch() { return entityData.get(DATA_ANIM_EPOCH); }
    public int getAnimTick() { return level().isClientSide() ? clientAnimTick : serverAnimTick; }
    public float getEnergy() { return entityData.get(DATA_ENERGY); }
    public float getArmorIntegrity() { return entityData.get(DATA_ARMOR); }
    public int getActionCooldown() { return entityData.get(DATA_COOLDOWN); }
    public int getLockedTargetId() { return entityData.get(DATA_LOCKED_TARGET_ID); }

    public String getBaseClipName() {
        return switch (getAnimId()) {
            case ANIM_DORMANT, ANIM_RECHARGE_BACK -> "dormant_kneeling";
            case ANIM_AWAKEN -> "awaken_bootup";
            case ANIM_WALK -> "walk_combat";
            case ANIM_DOUBLE_CLAP -> "double_clap";
            case ANIM_MELEE_3HIT -> "melee_combo_3hit";
            case ANIM_STOMP -> "stomp_shockwave";
            case ANIM_CYCLONE_WINDUP -> "cyclone_spin_windup";
            case ANIM_CYCLONE_ACTIVE -> "cyclone_spin_active";
            case ANIM_CYCLONE_RECOVER -> "cyclone_spin_recovery";
            case ANIM_LEAP_SLAM -> "leap_slam";
            case ANIM_MISSILE_SALVO -> "missile_barrage";
            default -> "idle_combat";
        };
    }

    private void playAnim(int animId) {
        entityData.set(DATA_ANIM_ID, animId);
        entityData.set(DATA_ANIM_EPOCH, entityData.get(DATA_ANIM_EPOCH) + 1);
        serverAnimTick = 0;
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

    @Override protected void registerGoals() {}
    @Override public boolean removeWhenFarAway(double dist) { return false; }
    @Override public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) { return false; }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.getEntity() != null && this.hasPassenger(source.getEntity())) {
            return false;
        }
        float armor = getArmorIntegrity();
        if (armor > 0.0F) {
            float absorbed = amount * 0.45F;
            amount -= absorbed;
            entityData.set(DATA_ARMOR, Math.max(0.0F, armor - Math.max(1.0F, absorbed * 0.42F)));
        }
        return super.hurt(source, amount);
    }

    @Override
    public void die(DamageSource cause) {
        for (Entity p : List.copyOf(getPassengers())) {
            p.setInvisible(false);
            p.stopRiding();
        }
        super.die(cause);
    }

    @Override
    protected void removePassenger(Entity passenger) {
        passenger.setInvisible(false);
        super.removePassenger(passenger);
        if (!level().isClientSide() && isFullyAssembled()) {
            playAnim(ANIM_DORMANT);
        }
    }

    @Override
    protected void positionRider(Entity passenger, MoveFunction callback) {
        float rad = this.yBodyRot * Mth.DEG_TO_RAD;
        double fwdX = -Math.sin(rad) * 0.78D;
        double fwdZ = Math.cos(rad) * 0.78D;
        callback.accept(passenger, getX() + fwdX, getY() + 2.85D, getZ() + fwdZ);
    }

    @Nullable
    @Override
    public LivingEntity getControllingPassenger() {
        Entity first = getFirstPassenger();
        return first instanceof LivingEntity le ? le : null;
    }

    private void updateAssemblyLabel() {
        int count = Integer.bitCount(getPartsMask() & FULL_PARTS);
        if (count < 5) {
            setCustomName(Component.literal(count + " / 5"));
            setCustomNameVisible(true);
        } else {
            setCustomName(null);
            setCustomNameVisible(false);
        }
    }

    /**
     * Triggers one of the 6 Ruin Guard combat skills on the server (called from C2S packet, mouse swing, or jump bar).
     */
    public boolean tryTriggerSkill(int skillId) {
        if (level().isClientSide() || !isFullyAssembled() || actionCooldown > 0) return false;
        int anim = getAnimId();
        if (anim != ANIM_IDLE && anim != ANIM_WALK) return false;

        float energy = getEnergy();
        return switch (skillId) {
            case SKILL_DOUBLE_CLAP -> {
                if (energy < 12.0F) yield false;
                consumeEnergy(12.0F);
                playAnim(ANIM_DOUBLE_CLAP);
                setCooldown(42);
                yield true;
            }
            case SKILL_MELEE_3HIT -> {
                if (energy < 22.0F) yield false;
                consumeEnergy(22.0F);
                playAnim(ANIM_MELEE_3HIT);
                setCooldown(126);
                yield true;
            }
            case SKILL_STOMP -> {
                if (energy < 15.0F) yield false;
                consumeEnergy(15.0F);
                playAnim(ANIM_STOMP);
                setCooldown(38);
                yield true;
            }
            case SKILL_CYCLONE -> {
                if (energy < 30.0F) yield false;
                consumeEnergy(30.0F);
                playAnim(ANIM_CYCLONE_WINDUP);
                setCooldown(152);
                yield true;
            }
            case SKILL_LEAP_SLAM -> {
                if (energy < 18.0F) yield false;
                consumeEnergy(18.0F);
                float aimYaw = (getControllingPassenger() instanceof Player p) ? p.getYRot() : getYRot();
                this.setYRot(aimYaw);
                this.yBodyRot = aimYaw;
                this.yHeadRot = aimYaw;
                leapStart = position();
                leapTarget = findExplorationLanding(aimYaw, 11.0D);
                entityData.set(DATA_LEAP_SX, (float) leapStart.x);
                entityData.set(DATA_LEAP_SY, (float) leapStart.y);
                entityData.set(DATA_LEAP_SZ, (float) leapStart.z);
                entityData.set(DATA_LEAP_TX, (float) leapTarget.x);
                entityData.set(DATA_LEAP_TY, (float) leapTarget.y);
                entityData.set(DATA_LEAP_TZ, (float) leapTarget.z);
                playAnim(ANIM_LEAP_SLAM);
                setCooldown(48);
                yield true;
            }
            case SKILL_MISSILES -> {
                if (energy < 25.0F) yield false;
                consumeEnergy(25.0F);
                playAnim(ANIM_MISSILE_SALVO);
                setCooldown(56);
                yield true;
            }
            default -> false;
        };
    }

    private void consumeEnergy(float amt) {
        entityData.set(DATA_ENERGY, Math.max(0.0F, getEnergy() - amt));
    }

    private void setCooldown(int ticks) {
        actionCooldown = ticks;
        entityData.set(DATA_COOLDOWN, ticks);
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level().isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        ServerLevel sl = (ServerLevel) level();

        // 1. Outdoor 5-part assembly welding
        if (!isFullyAssembled()) {
            if (stack.getItem() instanceof RuinMechPartItem partItem) {
                int bit = partItem.getPartBit();
                if (!hasPart(bit)) {
                    int nextMask = getPartsMask() | bit;
                    entityData.set(DATA_PARTS_MASK, nextMask);
                    if (!player.getAbilities().instabuild) stack.shrink(1);
                    sl.playSound(null, getX(), getY() + 1.5D, getZ(), SoundEvents.IRON_GOLEM_REPAIR, SoundSource.PLAYERS, 1.4F, 0.85F);
                    sl.sendParticles(ParticleTypes.FLAME, getX(), getY() + 1.8D, getZ(), 22, 0.9D, 0.9D, 0.9D, 0.06D);
                    sl.sendParticles(ParticleTypes.CRIT, getX(), getY() + 1.8D, getZ(), 16, 0.9D, 0.9D, 0.9D, 0.12D);
                    updateAssemblyLabel();
                    if ((nextMask & FULL_PARTS) == FULL_PARTS) {
                        entityData.set(DATA_ENERGY, 200.0F);
                        playAnim(ANIM_AWAKEN);
                        sl.playSound(null, getX(), getY() + 2.2D, getZ(), SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 1.5F, 0.9F);
                    }
                    return InteractionResult.CONSUME;
                }
            }
            return InteractionResult.CONSUME;
        }

        // 2. Iron Ingot / Iron Block -> Repair HP + Armor Integrity
        if (stack.is(Items.IRON_INGOT) || stack.is(Items.IRON_BLOCK)) {
            boolean isBlock = stack.is(Items.IRON_BLOCK);
            if (getHealth() < getMaxHealth() || getArmorIntegrity() < MAX_ARMOR) {
                heal(isBlock ? 180.0F : 45.0F);
                entityData.set(DATA_ARMOR, Math.min(MAX_ARMOR, getArmorIntegrity() + (isBlock ? 60.0F : 15.0F)));
                if (!player.getAbilities().instabuild) stack.shrink(1);
                sl.playSound(null, getX(), getY() + 1.8D, getZ(), SoundEvents.IRON_GOLEM_REPAIR, SoundSource.PLAYERS, 1.3F, 1.05F);
                sl.sendParticles(ParticleTypes.CRIT, getX(), getY() + 2.0D, getZ(), 20, 1.0D, 1.0D, 1.0D, 0.10D);
                return InteractionResult.CONSUME;
            }
        }

        // 3. Redstone Block -> Open rear reactor hatch & insert energy cell (+100 Energy, up to 10 blocks = 1000)
        if (stack.is(Items.REDSTONE_BLOCK)) {
            if (getEnergy() < MAX_ENERGY - 0.5F && getAnimId() != ANIM_RECHARGE_BACK) {
                if (!player.getAbilities().instabuild) stack.shrink(1);
                playAnim(ANIM_RECHARGE_BACK);
                sl.playSound(null, getX(), getY() + 2.2D, getZ(), SoundEvents.IRON_GOLEM_REPAIR, SoundSource.PLAYERS, 1.3F, 0.70F);
                return InteractionResult.CONSUME;
            }
        }

        // 4. Mount cockpit (requires energy > 0)
        if (!isVehicle()) {
            if (getEnergy() <= 0.1F) {
                sl.playSound(null, getX(), getY() + 2.0D, getZ(), SoundEvents.BEACON_DEACTIVATE, SoundSource.PLAYERS, 1.0F, 0.65F);
                return InteractionResult.CONSUME;
            }
            if (getAnimId() != ANIM_AWAKEN && getAnimId() != ANIM_RECHARGE_BACK) {
                playAnim(ANIM_ENTER_COCKPIT);
                player.startRiding(this);
                player.setInvisible(true);
                SoundEvent seal = TeyvatBossesSounds.mechVisorSeal();
                sl.playSound(null, getX(), getY() + 2.4D, getZ(), seal != null ? seal : SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 1.15F, 1.0F);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public void tick() {
        super.tick();
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
        serverAnimTick++;
        if (actionCooldown > 0) {
            actionCooldown--;
            entityData.set(DATA_COOLDOWN, actionCooldown);
        }

        int anim = getAnimId();
        int t = serverAnimTick;

        // 58-tick (2.9s) AAA Mechanical Rear-Reactor Redstone Block Reload (+100 Energy = 1 cell)
        if (anim == ANIM_RECHARGE_BACK) {
            // Compute TRUE back reactor world position (Mojang forward = (-sin, cos), so BACK = (+sin, -cos))
            float rad = this.yBodyRot * Mth.DEG_TO_RAD;
            double bx = getX() + Math.sin(rad) * 1.15D;
            double by = getY() + 1.85D;
            double bz = getZ() - Math.cos(rad) * 1.15D;

            if (t == 3) {
                // Phase 1: Hydraulic kneel depressurization hiss + white steam vent
                sl.playSound(null, bx, by, bz, SoundEvents.PISTON_EXTEND, SoundSource.PLAYERS, 1.2F, 0.52F);
                sl.sendParticles(ParticleTypes.CLOUD, bx, by, bz, 12, 0.45D, 0.25D, 0.45D, 0.05D);
            } else if (t == 13) {
                // Phase 2: Armored shrouds split + rear reactor hatch unlatches
                sl.playSound(null, bx, by, bz, SoundEvents.IRON_TRAPDOOR_OPEN, SoundSource.PLAYERS, 1.4F, 0.54F);
                sl.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, bx, by, bz, 6, 0.25D, 0.20D, 0.25D, 0.02D);
            } else if (t == 27) {
                // Phase 3a: Magnetic alignment hum at reactor rim
                sl.playSound(null, bx, by, bz, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 0.9F, 0.72F);
                sl.sendParticles(DustParticleOptions.REDSTONE, bx, by, bz, 10, 0.25D, 0.25D, 0.25D, 0.02D);
            } else if (t == 36) {
                // Phase 3b: Hydraulic lock-in slam -> +100 Energy + redstone/gold spark burst
                float nextE = Math.min(MAX_ENERGY, getEnergy() + ENERGY_PER_BLOCK);
                entityData.set(DATA_ENERGY, nextE);
                sl.playSound(null, bx, by, bz, SoundEvents.PISTON_CONTRACT, SoundSource.PLAYERS, 1.35F, 0.72F);
                sl.playSound(null, bx, by, bz, SoundEvents.BEACON_POWER_SELECT, SoundSource.PLAYERS, 1.4F, 1.15F);
                sl.sendParticles(DustParticleOptions.REDSTONE, bx, by, bz, 28, 0.45D, 0.40D, 0.45D, 0.08D);
                sl.sendParticles(ParticleTypes.END_ROD, bx, by, bz, 14, 0.35D, 0.35D, 0.35D, 0.07D);
            } else if (t == 46) {
                // Phase 4: Rear hatch slams shut + pressure seal steam puff
                sl.playSound(null, bx, by, bz, SoundEvents.IRON_TRAPDOOR_CLOSE, SoundSource.PLAYERS, 1.35F, 0.50F);
                sl.playSound(null, bx, by, bz, SoundEvents.PISTON_CONTRACT, SoundSource.PLAYERS, 1.1F, 0.58F);
                sl.sendParticles(ParticleTypes.CLOUD, bx, by, bz, 10, 0.40D, 0.20D, 0.40D, 0.04D);
            }
            if (t >= 58) {
                playAnim(isVehicle() ? ANIM_IDLE : ANIM_DORMANT);
            }
            return;
        }

        if (anim == ANIM_AWAKEN && t >= 52) {
            playAnim(ANIM_IDLE);
            return;
        }
        if (anim == ANIM_ENTER_COCKPIT && t >= 26) {
            playAnim(ANIM_IDLE);
            return;
        }

        // Damage smoke & electric sparks when Armor < 30%
        if (isFullyAssembled() && getArmorIntegrity() < 30.0F && tickCount % 4 == 0) {
            boolean crit = getArmorIntegrity() <= 0.5F;
            sl.sendParticles(ParticleTypes.LARGE_SMOKE, getX(), getY() + 2.6D, getZ(), crit ? 3 : 1, 0.75D, 0.45D, 0.75D, 0.02D);
            if (tickCount % 8 == 0) {
                sl.sendParticles(ParticleTypes.ELECTRIC_SPARK, getX(), getY() + 2.3D, getZ(), crit ? 8 : 4, 0.85D, 0.55D, 0.85D, 0.08D);
            }
        }

        LivingEntity pilot = getControllingPassenger();
        if (pilot instanceof Player player) {
            if (!player.isInvisible()) player.setInvisible(true);

            // Continuous low mechanical engine hum while piloted
            if (tickCount % 26 == 0) {
                SoundEvent hum = TeyvatBossesSounds.mechEngineHum();
                if (hum != null) {
                    float pitch = (anim == ANIM_WALK || anim == ANIM_CYCLONE_ACTIVE) ? 1.10F : 0.96F;
                    sl.playSound(null, getX(), getY() + 2.0D, getZ(), hum, SoundSource.PLAYERS, 0.36F, pitch);
                }
            }

            // Live crosshair aim target lock-on (every 3 ticks)
            if (tickCount % 3 == 0) {
                LivingEntity aimed = findAimedHostile(sl, player, 34.0D);
                entityData.set(DATA_LOCKED_TARGET_ID, aimed != null ? aimed.getId() : -1);
            }

            // Server-side movement & pivot-turn detection (travel() runs on controlling client, so server detects displacement here!)
            if (anim == ANIM_IDLE || anim == ANIM_WALK) {
                double dx = getX() - prevServerX;
                double dz = getZ() - prevServerZ;
                double movedSq = dx * dx + dz * dz;
                float yawMoved = Math.abs(Mth.wrapDegrees(getYRot() - prevServerYaw));
                if (movedSq > 0.00018D || yawMoved > 0.85F) {
                    walkGraceTicks = 7;
                } else if (walkGraceTicks > 0) {
                    walkGraceTicks--;
                }

                if (walkGraceTicks > 0) {
                    if (anim != ANIM_WALK) {
                        playAnim(ANIM_WALK);
                        anim = ANIM_WALK;
                    }
                    if (serverAnimTick % 24 == 2) {
                        sl.playSound(null, getX(), getY() + 1.5D, getZ(), SoundEvents.IRON_GOLEM_REPAIR, SoundSource.PLAYERS, 0.35F, 0.54F);
                    } else if (serverAnimTick % 24 == 12) {
                        sl.playSound(null, getX(), getY(), getZ(), SoundEvents.IRON_GOLEM_STEP, SoundSource.PLAYERS, movedSq > 0.00018D ? 1.40F : 0.90F, 0.45F);
                        sl.sendParticles(ParticleTypes.POOF, getX(), getY() + 0.12D, getZ(), movedSq > 0.00018D ? 9 : 4, 0.75D, 0.06D, 0.75D, 0.025D);
                    }
                } else if (anim == ANIM_WALK) {
                    playAnim(ANIM_IDLE);
                    anim = ANIM_IDLE;
                }
            } else {
                walkGraceTicks = 0;
            }
            prevServerX = getX();
            prevServerZ = getZ();
            prevServerYaw = getYRot();

            // Gentle passive drain (-0.5 energy per 2s -> 10 Redstone Blocks = ~66 minutes idle / ~25 min combat)
            if (tickCount % 40 == 0) {
                float e = Math.max(0.0F, getEnergy() - 0.5F);
                entityData.set(DATA_ENERGY, e);
                if (e <= 0.0F) {
                    sl.playSound(null, getX(), getY() + 2.0D, getZ(), SoundEvents.BEACON_DEACTIVATE, SoundSource.PLAYERS, 1.2F, 0.6F);
                    player.stopRiding();
                    return;
                }
            }

            // All 6 skills are dispatched cleanly via LMB/RMB Tap, Double-Tap, and Hold gestures (RuinMechSkillPayload)
        }

        if (pilot == null) {
            if (getLockedTargetId() != -1) entityData.set(DATA_LOCKED_TARGET_ID, -1);
            prevServerX = getX();
            prevServerZ = getZ();
            prevServerYaw = getYRot();
            walkGraceTicks = 0;
        }

        // Execute all 6 Ruin Guard Boss Combat Skills
        if (anim == ANIM_DOUBLE_CLAP) {
            if (t == 23) {
                dealFrontAoE(sl, 5.8D, 30.0F, 1.35D);
                sl.playSound(null, getX(), getY() + 1.8D, getZ(), SoundEvents.ANVIL_LAND, SoundSource.PLAYERS, 1.4F, 0.55F);
                sl.playSound(null, getX(), getY() + 1.8D, getZ(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.PLAYERS, 1.2F, 0.78F);
                sl.sendParticles(ParticleTypes.EXPLOSION, getX(), getY() + 1.6D, getZ(), 2, 1.2D, 0.5D, 1.2D, 0.01D);
            }
            if (t >= 44) playAnim(ANIM_IDLE);
        } else if (anim == ANIM_MELEE_3HIT) {
            // Authentic Genshin 3-Hit Combo: Hit 1 (t=24), Hit 2 (t=58), Hit 3 Finisher Slam (t=98)
            if (t == 24) {
                dealFrontAoE(sl, 5.2D, 22.0F, 0.95D);
                sl.playSound(null, getX(), getY() + 1.8D, getZ(), SoundEvents.IRON_GOLEM_ATTACK, SoundSource.PLAYERS, 1.4F, 0.65F);
                sl.sendParticles(ParticleTypes.SWEEP_ATTACK, getX(), getY() + 1.8D, getZ(), 3, 1.4D, 0.4D, 1.4D, 0.01D);
            } else if (t == 58) {
                dealFrontAoE(sl, 5.4D, 26.0F, 1.15D);
                sl.playSound(null, getX(), getY() + 1.8D, getZ(), SoundEvents.IRON_GOLEM_ATTACK, SoundSource.PLAYERS, 1.5F, 0.55F);
                sl.sendParticles(ParticleTypes.SWEEP_ATTACK, getX(), getY() + 1.8D, getZ(), 4, 1.5D, 0.4D, 1.5D, 0.01D);
            } else if (t == 98) {
                dealFrontAoE(sl, 6.4D, 38.0F, 1.55D);
                sl.playSound(null, getX(), getY() + 1.2D, getZ(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.PLAYERS, 1.6F, 0.60F);
                sl.sendParticles(ParticleTypes.EXPLOSION_EMITTER, getX(), getY() + 1.0D, getZ(), 1, 0.6D, 0.2D, 0.6D, 0.01D);
            }
            if (t >= 128) playAnim(ANIM_IDLE);
        } else if (anim == ANIM_STOMP) {
            if (t == 20) {
                dealRadialAoE(sl, 5.8D, 26.0F, 1.20D, 0.54D);
                sl.playSound(null, getX(), getY(), getZ(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.PLAYERS, 1.4F, 0.65F);
                sl.sendParticles(ParticleTypes.POOF, getX(), getY() + 0.3D, getZ(), 28, 2.3D, 0.2D, 2.3D, 0.08D);
            }
            if (t >= 38) playAnim(ANIM_IDLE);
        } else if (anim == ANIM_CYCLONE_WINDUP) {
            if (t == 1) {
                sl.playSound(null, getX(), getY() + 2.0D, getZ(), SoundEvents.PISTON_EXTEND, SoundSource.PLAYERS, 1.4F, 0.55F);
            }
            if (t >= 28) playAnim(ANIM_CYCLONE_ACTIVE);
        } else if (anim == ANIM_CYCLONE_ACTIVE) {
            // 4 full high-speed spin revolutions (80 ticks), steerable with WASD!
            if (t % 10 == 0) {
                dealRadialAoE(sl, 5.2D, 14.0F, 0.95D, 0.32D);
                sl.playSound(null, getX(), getY() + 1.8D, getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.4F, 0.55F);
                sl.sendParticles(ParticleTypes.CLOUD, getX(), getY() + 1.2D, getZ(), 14, 2.2D, 0.5D, 2.2D, 0.09D);
            }
            if (t >= 80) playAnim(ANIM_CYCLONE_RECOVER);
        } else if (anim == ANIM_CYCLONE_RECOVER) {
            if (t == 20) {
                sl.playSound(null, getX(), getY(), getZ(), SoundEvents.IRON_GOLEM_STEP, SoundSource.PLAYERS, 1.3F, 0.48F);
            }
            if (t >= 44) playAnim(ANIM_IDLE);
        } else if (anim == ANIM_MISSILE_SALVO) {
            if (t >= 16 && t < 34 && (t - 16) % 3 == 0) {
                int idx = (t - 16) / 3;
                Entity lockedEnt = getLockedTargetId() != -1 ? sl.getEntity(getLockedTargetId()) : null;
                LivingEntity lock = (lockedEnt instanceof LivingEntity le && le.isAlive()) ? le : findNearestHostile(sl, 34.0D);
                double side = (idx % 2 == 0) ? -1.1D : 1.1D;
                float rad = getYRot() * Mth.DEG_TO_RAD;
                Vec3 right = new Vec3(Math.cos(rad), 0.0D, Math.sin(rad));
                Vec3 fwd = Vec3.directionFromRotation(0.0F, getYRot());
                Vec3 origin = position().add(right.scale(side)).add(0.0D, 3.6D, 0.0D);
                Vec3 vel = right.scale(side * 0.24D).add(fwd.scale(0.32D)).add(0.0D, 0.52D, 0.0D);
                RuinMissileProjectile.launch(sl, this, lock, origin, vel, 16.0F);
                sl.playSound(null, origin.x, origin.y, origin.z, SoundEvents.FIREWORK_ROCKET_LAUNCH, SoundSource.PLAYERS, 1.2F, 0.85F);
                sl.sendParticles(ParticleTypes.FLAME, origin.x, origin.y, origin.z, 8, 0.15D, 0.15D, 0.15D, 0.05D);
            }
            if (t >= 54) playAnim(ANIM_IDLE);
        } else if (anim == ANIM_LEAP_SLAM) {
            if (t >= 10 && t <= 28) {
                float f = (t - 10) / 18.0F;
                float sf = f * f * (3.0F - 2.0F * f);
                double nx = Mth.lerp(sf, leapStart.x, leapTarget.x);
                double nz = Mth.lerp(sf, leapStart.z, leapTarget.z);
                double apex = Math.max(leapStart.y, leapTarget.y) + 4.6D;
                double baseLine = Mth.lerp(f, leapStart.y, leapTarget.y);
                double ny = baseLine + Math.sin(f * Math.PI) * Math.max(4.4D, apex - baseLine);
                setPos(nx, ny, nz);
            } else if (t == 29) {
                setPos(leapTarget.x, leapTarget.y, leapTarget.z);
                dealRadialAoE(sl, 7.0D, 40.0F, 1.60D, 0.72D);
                sl.playSound(null, getX(), getY(), getZ(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.PLAYERS, 1.8F, 0.55F);
                sl.sendParticles(ParticleTypes.EXPLOSION_EMITTER, getX(), getY() + 0.5D, getZ(), 1, 0.6D, 0.2D, 0.6D, 0.01D);
            }
            if (t >= 52) playAnim(ANIM_IDLE);
        }
    }

    @Override
    public void travel(Vec3 travelVector) {
        int anim = getAnimId();
        // Controlling client must execute the exact same parabolic leap arc so vehicle position sync never rubber-bands!
        if (anim == ANIM_LEAP_SLAM && level().isClientSide() && isVehicle()) {
            int t = getAnimTick();
            double sx = entityData.get(DATA_LEAP_SX);
            double sy = entityData.get(DATA_LEAP_SY);
            double sz = entityData.get(DATA_LEAP_SZ);
            double tx = entityData.get(DATA_LEAP_TX);
            double ty = entityData.get(DATA_LEAP_TY);
            double tz = entityData.get(DATA_LEAP_TZ);
            if (t >= 10 && t <= 28) {
                float f = (t - 10) / 18.0F;
                float sf = f * f * (3.0F - 2.0F * f);
                double nx = Mth.lerp(sf, sx, tx);
                double nz = Mth.lerp(sf, sz, tz);
                double apex = Math.max(sy, ty) + 4.6D;
                double baseLine = Mth.lerp(f, sy, ty);
                double ny = baseLine + Math.sin(f * Math.PI) * Math.max(4.4D, apex - baseLine);
                setPos(nx, ny, nz);
                setDeltaMovement(Vec3.ZERO);
                return;
            } else if (t == 29) {
                setPos(tx, ty, tz);
                setDeltaMovement(Vec3.ZERO);
                return;
            }
        }
        boolean canMove = (anim == ANIM_IDLE || anim == ANIM_WALK || anim == ANIM_CYCLONE_ACTIVE);
        if (isAlive() && isVehicle() && getControllingPassenger() instanceof Player pilot && canMove) {
            // 1. Heavy mechanical turn-rate limit matching original RuinGuardEntity (4.2 deg/tick, 6.2 during cyclone)
            float maxTurn = (anim == ANIM_CYCLONE_ACTIVE) ? 6.2F : 4.2F;
            float targetYaw = pilot.getYRot();
            float yawDelta = Mth.wrapDegrees(targetYaw - this.getYRot());
            float clampedTurn = Mth.clamp(yawDelta, -maxTurn, maxTurn);
            this.yRotO = this.getYRot();
            this.setYRot(this.getYRot() + clampedTurn);
            this.setXRot(Mth.clamp(pilot.getXRot() * 0.45F, -25.0F, 25.0F));
            this.yBodyRot = this.getYRot();
            this.yHeadRot = this.getYRot() + Mth.clamp(Mth.wrapDegrees(targetYaw - this.getYRot()), -38.0F, 38.0F);

            float side = pilot.xxa * 0.45F;
            float fwd = pilot.zza;
            if (fwd < 0.0F) fwd *= 0.55F;

            boolean wantsMove = Math.abs(fwd) > 0.05F || Math.abs(side) > 0.05F;
            boolean needsPivotStep = (anim == ANIM_WALK) ? (Math.abs(yawDelta) > 7.5F) : (Math.abs(yawDelta) > 15.0F);

            if (anim == ANIM_CYCLONE_ACTIVE) {
                this.setSpeed(0.16F);
                super.travel(new Vec3(side, travelVector.y, fwd));
                return;
            }

            // 2. Authentic RuinGuardEntity 24-tick zero-slide weighted stride curve:
            //    Anchors firmly at footplant (stepPhase 0.46..0.66) so giant metal feet NEVER skate across ground!
            int stepTick = getAnimTick();
            double stepPhase = (stepTick % 24) / 24.0D;
            double strideSpeed;
            if (stepPhase >= 0.46D && stepPhase <= 0.66D) {
                strideSpeed = 0.008D;
            } else if (stepPhase < 0.46D) {
                strideSpeed = 0.042D + 0.028D * Math.sin((stepPhase / 0.46D) * Math.PI);
            } else {
                strideSpeed = 0.044D + 0.024D * Math.sin(((stepPhase - 0.66D) / 0.34D) * Math.PI);
            }

            if (wantsMove) {
                this.setSpeed((float) strideSpeed);
                super.travel(new Vec3(side, travelVector.y, fwd));
            } else {
                Vec3 cur = getDeltaMovement();
                setDeltaMovement(cur.x * 0.32D, cur.y, cur.z * 0.32D);
                super.travel(Vec3.ZERO);
            }

            if (!level().isClientSide()) {
                boolean activeWalk = wantsMove || needsPivotStep;
                if (activeWalk && anim != ANIM_WALK) {
                    playAnim(ANIM_WALK);
                } else if (!activeWalk && anim == ANIM_WALK) {
                    playAnim(ANIM_IDLE);
                }
                if (activeWalk && anim == ANIM_WALK) {
                    if (serverAnimTick % 24 == 2) {
                        // Subtle hydraulic joint creak on leg lift
                        level().playSound(null, getX(), getY() + 1.5D, getZ(), SoundEvents.IRON_GOLEM_REPAIR, SoundSource.PLAYERS, 0.38F, 0.54F);
                    } else if (serverAnimTick % 24 == 12) {
                        // Heavy metallic stomp on footplant
                        level().playSound(null, getX(), getY(), getZ(), SoundEvents.IRON_GOLEM_STEP, SoundSource.PLAYERS, wantsMove ? 1.40F : 0.90F, 0.45F);
                        if (level() instanceof ServerLevel sl) {
                            sl.sendParticles(ParticleTypes.POOF, getX(), getY() + 0.12D, getZ(), wantsMove ? 9 : 4, 0.75D, 0.06D, 0.75D, 0.025D);
                        }
                    }
                }
            }
            return;
        }
        super.travel(Vec3.ZERO);
    }

    /**
     * Finds a safe terrain landing point along the pilot's look direction for exploration leaps
     * (crosses rivers/ravines, climbs cliffs up to +6 blocks, or drops into valleys down to -12 blocks).
     */
    private Vec3 findExplorationLanding(float yawDeg, double maxDist) {
        Vec3 fwd = Vec3.directionFromRotation(0.0F, yawDeg);
        for (double dist = maxDist; dist >= 3.0D; dist -= 1.0D) {
            double tx = getX() + fwd.x * dist;
            double tz = getZ() + fwd.z * dist;
            int bx = Mth.floor(tx);
            int bz = Mth.floor(tz);
            int startY = Mth.floor(getY()) + 6;
            int minY = Math.max(level().getMinBuildHeight() + 2, Mth.floor(getY()) - 12);
            for (int y = startY; y >= minY; y--) {
                BlockPos floorPos = new BlockPos(bx, y - 1, bz);
                BlockPos feetPos = new BlockPos(bx, y, bz);
                BlockPos bodyPos = new BlockPos(bx, y + 1, bz);
                BlockPos headPos = new BlockPos(bx, y + 2, bz);
                if (level().getBlockState(floorPos).isSolid()
                        && !level().getBlockState(feetPos).isSolid()
                        && !level().getBlockState(bodyPos).isSolid()
                        && !level().getBlockState(headPos).isSolid()) {
                    return new Vec3(tx, y, tz);
                }
            }
        }
        return position().add(fwd.scale(5.0D));
    }

    // PlayerRideableJumping: Space bar still works as convenient shortcut for Stomp (tap) / Leap Slam (hold)
    @Override public void onPlayerJump(int jumpPower) {}
    @Override public boolean canJump() {
        return false;
    }

    @Override
    public void handleStartJump(int jumpPower) {
        if (level().isClientSide() || !canJump()) return;
        tryTriggerSkill(jumpPower >= 55 ? SKILL_LEAP_SLAM : SKILL_STOMP);
    }

    @Override public void handleStopJump() {}

    private void dealFrontAoE(ServerLevel sl, double radius, float dmg, double kb) {
        Vec3 fwd = Vec3.directionFromRotation(0.0F, getYRot());
        Vec3 center = position().add(fwd.scale(2.8D));
        AABB box = new AABB(center.x - radius, center.y - 1.5D, center.z - radius, center.x + radius, center.y + 4.0D, center.z + radius);
        LivingEntity pilot = getControllingPassenger();
        for (LivingEntity e : sl.getEntitiesOfClass(LivingEntity.class, box, le -> le.isAlive() && le != this && le != pilot)) {
            StavesElementBridge.dealElementalDamage(e, pilot != null ? pilot : this, "PHYSICAL", dmg, 0.0F, true, 0.22F);
            e.push(fwd.x * kb, 0.48D, fwd.z * kb);
        }
    }

    private void dealRadialAoE(ServerLevel sl, double radius, float dmg, double kb, double lift) {
        AABB box = getBoundingBox().inflate(radius, 3.0D, radius);
        LivingEntity pilot = getControllingPassenger();
        for (LivingEntity e : sl.getEntitiesOfClass(LivingEntity.class, box, le -> le.isAlive() && le != this && le != pilot)) {
            Vec3 to = e.position().subtract(position()).multiply(1.0D, 0.0D, 1.0D);
            if (to.lengthSqr() <= radius * radius) {
                StavesElementBridge.dealElementalDamage(e, pilot != null ? pilot : this, "PHYSICAL", dmg, 0.0F, true, 0.25F);
                if (to.lengthSqr() > 1e-4D) {
                    Vec3 p = to.normalize().scale(kb);
                    e.push(p.x, lift, p.z);
                }
            }
        }
    }


    @Nullable
    private LivingEntity findAimedHostile(ServerLevel sl, Player pilot, double range) {
        Vec3 eyePos = pilot.getEyePosition();
        Vec3 look = pilot.getLookAngle().normalize();
        AABB box = getBoundingBox().inflate(range, 14.0D, range);
        LivingEntity best = null;
        double bestScore = -1e9;
        for (LivingEntity e : sl.getEntitiesOfClass(LivingEntity.class, box,
                le -> le.isAlive() && le != this && le != pilot && !(le instanceof RuinMechEntity) && le instanceof Enemy)) {
            Vec3 to = e.position().add(0.0D, e.getBbHeight() * 0.5D, 0.0D).subtract(eyePos);
            double dist = to.length();
            if (dist > range || dist < 1.5D) continue;
            double dot = to.normalize().dot(look);
            if (dot < 0.72D) continue;
            double score = dot * 40.0D - dist * 0.45D;
            if (score > bestScore) {
                bestScore = score;
                best = e;
            }
        }
        return best;
    }

    @Nullable
    private LivingEntity findNearestHostile(ServerLevel sl, double range) {
        AABB box = getBoundingBox().inflate(range, 10.0D, range);
        LivingEntity best = null;
        double bestSq = range * range;
        for (LivingEntity e : sl.getEntitiesOfClass(LivingEntity.class, box, le -> le.isAlive() && le instanceof Enemy && le != this)) {
            double d = e.distanceToSqr(this);
            if (d < bestSq) { bestSq = d; best = e; }
        }
        return best;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("PartsMask", getPartsMask());
        tag.putFloat("MechEnergy", getEnergy());
        tag.putFloat("MechArmor", getArmorIntegrity());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("PartsMask")) entityData.set(DATA_PARTS_MASK, tag.getInt("PartsMask"));
        if (tag.contains("MechEnergy")) entityData.set(DATA_ENERGY, tag.getFloat("MechEnergy"));
        if (tag.contains("MechArmor")) entityData.set(DATA_ARMOR, tag.getFloat("MechArmor"));
        updateAssemblyLabel();
    }
}

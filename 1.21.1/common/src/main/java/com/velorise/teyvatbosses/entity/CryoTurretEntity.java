package com.velorise.teyvatbosses.entity;

import com.velorise.teyvatbosses.compat.StavesElementBridge;
import com.velorise.teyvatbosses.item.CryoTurretPartItem;
import com.velorise.teyvatbosses.registry.TeyvatBossesItems;
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
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * Miniature 3x6x3 Defensive Cryo Regisvine Turret assembled outdoors from 3 Cryo Regisvine salvage parts:
 * Root ({@code cryo_turret_root}), Stem ({@code cryo_turret_stem}), and Corolla ({@code cryo_turret_corolla}).
 *
 * <p>Once all 3 parts are welded, the turret automatically guards the area within 24 blocks, firing
 * 3-shot homing Cryo Icicle volleys at hostile mobs and unleashing a close-range Frost Nova shockwave
 * whenever enemies approach within 5.5 blocks. Never harms players or friendly constructs.
 */
public class CryoTurretEntity extends Mob {

    public static final int PART_ROOT    = 1;
    public static final int PART_STEM    = 1 << 1;
    public static final int PART_COROLLA = 1 << 2;
    public static final int FULL_PARTS   = PART_ROOT | PART_STEM | PART_COROLLA;

    public static final int ANIM_DORMANT    = 0;
    public static final int ANIM_AWAKEN     = 1;
    public static final int ANIM_IDLE       = 2;
    public static final int ANIM_ICICLE     = 3;
    public static final int ANIM_FROST_NOVA = 4;

    private static final EntityDataAccessor<Integer> DATA_PARTS_MASK =
            SynchedEntityData.defineId(CryoTurretEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_ANIM_ID =
            SynchedEntityData.defineId(CryoTurretEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_ANIM_EPOCH =
            SynchedEntityData.defineId(CryoTurretEntity.class, EntityDataSerializers.INT);

    private int serverAnimTick = 0;
    private int clientAnimTick = 0;
    private int lastSyncedAnimId = -1;
    private int lastSyncedEpoch = -1;
    private int attackCooldown = 20;

    public CryoTurretEntity(EntityType<? extends Mob> type, Level level) {
        super(type, level);
        this.setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 220.0D)
                .add(Attributes.ARMOR, 10.0D)
                .add(Attributes.ARMOR_TOUGHNESS, 4.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.0D);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_PARTS_MASK, FULL_PARTS);
        builder.define(DATA_ANIM_ID, ANIM_IDLE);
        builder.define(DATA_ANIM_EPOCH, 0);
    }

    public void initAssemblyChassis() {
        entityData.set(DATA_PARTS_MASK, PART_ROOT);
        playAnim(ANIM_DORMANT);
        updateAssemblyLabel();
    }

    public int getPartsMask() { return entityData.get(DATA_PARTS_MASK); }
    public boolean isFullyAssembled() { return (getPartsMask() & FULL_PARTS) == FULL_PARTS; }
    public boolean hasPart(int bit) { return (getPartsMask() & bit) != 0; }
    public int getAnimId() { return entityData.get(DATA_ANIM_ID); }
    public int getAnimEpoch() { return entityData.get(DATA_ANIM_EPOCH); }
    public int getAnimTick() { return level().isClientSide() ? clientAnimTick : serverAnimTick; }

    public String getBaseClipName() {
        return switch (getAnimId()) {
            case ANIM_DORMANT -> "idle_combat";
            case ANIM_AWAKEN -> "awaken_emerge";
            case ANIM_ICICLE -> "homing_icicles";
            case ANIM_FROST_NOVA -> "spin_frost_ring";
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
        if (level().isClientSide() && (DATA_ANIM_EPOCH.equals(key) || DATA_ANIM_ID.equals(key))) {
            lastSyncedAnimId = getAnimId();
            lastSyncedEpoch = getAnimEpoch();
            clientAnimTick = 0;
        }
    }

    @Override protected void registerGoals() {}
    @Override public boolean removeWhenFarAway(double dist) { return false; }
    @Override public boolean isPushable() { return false; }
    @Override public void push(Entity entity) {}
    @Override protected void doPush(Entity entity) {}
    @Override public void push(double x, double y, double z) {}
    @Override public void knockback(double strength, double x, double z) {}

    private void uprootWithShovel(ServerLevel sl, Player player) {
        if (hasPart(PART_ROOT) && TeyvatBossesItems.cryoTurretRoot() != null) spawnAtLocation(TeyvatBossesItems.cryoTurretRoot());
        if (hasPart(PART_STEM) && TeyvatBossesItems.cryoTurretStem() != null) spawnAtLocation(TeyvatBossesItems.cryoTurretStem());
        if (hasPart(PART_COROLLA) && TeyvatBossesItems.cryoTurretCorolla() != null) spawnAtLocation(TeyvatBossesItems.cryoTurretCorolla());
        sl.playSound(null, getX(), getY() + 1.0D, getZ(), SoundEvents.SNOW_BREAK, SoundSource.PLAYERS, 1.3F, 0.85F);
        sl.playSound(null, getX(), getY() + 1.5D, getZ(), SoundEvents.GLASS_BREAK, SoundSource.PLAYERS, 1.0F, 1.15F);
        sl.sendParticles(ParticleTypes.SNOWFLAKE, getX(), getY() + 2.0D, getZ(), 32, 0.9D, 1.4D, 0.9D, 0.08D);
        player.displayClientMessage(Component.translatable("message.velorise_teyvat_bosses.cryo_turret.uprooted"), true);
        discard();
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypes.IN_WALL) || source.is(DamageTypes.CRAMMING) || source.is(DamageTypes.FREEZE)) {
            return false;
        }
        if (!level().isClientSide() && level() instanceof ServerLevel sl
                && source.getEntity() instanceof Player player
                && player.getMainHandItem().getItem() instanceof ShovelItem) {
            uprootWithShovel(sl, player);
            return true;
        }
        return super.hurt(source, amount);
    }

    @Override
    public void die(DamageSource cause) {
        if (!level().isClientSide()) {
            if (hasPart(PART_ROOT) && TeyvatBossesItems.cryoTurretRoot() != null) spawnAtLocation(TeyvatBossesItems.cryoTurretRoot());
            if (hasPart(PART_STEM) && TeyvatBossesItems.cryoTurretStem() != null) spawnAtLocation(TeyvatBossesItems.cryoTurretStem());
            if (hasPart(PART_COROLLA) && TeyvatBossesItems.cryoTurretCorolla() != null) spawnAtLocation(TeyvatBossesItems.cryoTurretCorolla());
        }
        super.die(cause);
    }

    private void updateAssemblyLabel() {
        int count = Integer.bitCount(getPartsMask() & FULL_PARTS);
        if (count < 3) {
            setCustomName(Component.literal(count + " / 3"));
            setCustomNameVisible(true);
        } else {
            setCustomName(null);
            setCustomNameVisible(false);
        }
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level().isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        ServerLevel sl = (ServerLevel) level();

        // 0. Any Shovel uproots the entire Cryo Turret cleanly and drops all welded parts
        if (stack.getItem() instanceof ShovelItem) {
            uprootWithShovel(sl, player);
            return InteractionResult.CONSUME;
        }

        // 1. Strict bottom-up 3-part assembly (Root -> Stem -> Corolla)
        if (!isFullyAssembled()) {
            if (stack.getItem() instanceof CryoTurretPartItem partItem) {
                int bit = partItem.getPartBit();
                if (bit == PART_COROLLA && !hasPart(PART_STEM)) {
                    sl.playSound(null, getX(), getY() + 1.5D, getZ(), SoundEvents.VILLAGER_NO, SoundSource.PLAYERS, 0.8F, 1.1F);
                    player.displayClientMessage(Component.translatable("message.velorise_teyvat_bosses.cryo_turret.need_stem"), true);
                    return InteractionResult.CONSUME;
                }
                if (!hasPart(bit)) {
                    int nextMask = getPartsMask() | bit;
                    entityData.set(DATA_PARTS_MASK, nextMask);
                    if (!player.getAbilities().instabuild) stack.shrink(1);
                    sl.playSound(null, getX(), getY() + 2.0D, getZ(), SoundEvents.GLASS_PLACE, SoundSource.PLAYERS, 1.3F, 1.15F);
                    sl.sendParticles(ParticleTypes.SNOWFLAKE, getX(), getY() + 2.4D, getZ(), 26, 0.8D, 1.2D, 0.8D, 0.05D);
                    updateAssemblyLabel();
                    if ((nextMask & FULL_PARTS) == FULL_PARTS) {
                        playAnim(ANIM_AWAKEN);
                        sl.playSound(null, getX(), getY() + 2.5D, getZ(), SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 1.3F, 1.25F);
                        player.displayClientMessage(Component.translatable("message.velorise_teyvat_bosses.cryo_turret.assembled"), true);
                    } else {
                        int count = Integer.bitCount(nextMask & FULL_PARTS);
                        player.displayClientMessage(Component.translatable("message.velorise_teyvat_bosses.cryo_turret.progress", count, 3), true);
                    }
                    return InteractionResult.CONSUME;
                }
            }
            return InteractionResult.CONSUME;
        }

        // 2. Repair with Packed Ice (+40 HP) or Blue Ice (+80 HP)
        if (stack.is(Items.PACKED_ICE) || stack.is(Items.BLUE_ICE)) {
            if (getHealth() < getMaxHealth()) {
                heal(stack.is(Items.BLUE_ICE) ? 80.0F : 40.0F);
                if (!player.getAbilities().instabuild) stack.shrink(1);
                sl.playSound(null, getX(), getY() + 2.2D, getZ(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 1.2F, 1.35F);
                sl.sendParticles(ParticleTypes.SNOWFLAKE, getX(), getY() + 2.4D, getZ(), 18, 0.8D, 1.0D, 0.8D, 0.06D);
                return InteractionResult.CONSUME;
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public void tick() {
        super.tick();
        setDeltaMovement(0.0D, Math.min(0.0D, getDeltaMovement().y), 0.0D);

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
        int anim = getAnimId();
        int t = serverAnimTick;

        if (!isFullyAssembled()) return;

        if (anim == ANIM_AWAKEN) {
            if (t >= 44) playAnim(ANIM_IDLE);
            return;
        }

        // Ambient frost aura particles
        if (tickCount % 8 == 0) {
            sl.sendParticles(ParticleTypes.SNOWFLAKE, getX(), getY() + 4.0D, getZ(), 3, 0.8D, 0.6D, 0.8D, 0.02D);
        }

        LivingEntity target = findNearestHostile(sl, 24.0D);
        if (target != null) {
            double dx = target.getX() - getX();
            double dz = target.getZ() - getZ();
            float desiredYaw = (float) (Math.atan2(dz, dx) * (180.0D / Math.PI)) - 90.0F;
            float delta = Mth.wrapDegrees(desiredYaw - getYRot());
            float nextYaw = getYRot() + Mth.clamp(delta, -8.5F, 8.5F);
            setYRot(nextYaw);
            this.yBodyRot = nextYaw;
            this.yHeadRot = nextYaw;
        }

        if (anim == ANIM_IDLE) {
            if (attackCooldown > 0) {
                attackCooldown--;
            } else if (target != null) {
                double distSq = distanceToSqr(target);
                if (distSq <= 5.5D * 5.5D) {
                    playAnim(ANIM_FROST_NOVA);
                } else {
                    playAnim(ANIM_ICICLE);
                }
            }
        } else if (anim == ANIM_ICICLE) {
            // Fires 3 homing Cryo Icicles at ticks 10, 16, 22
            if (t == 10 || t == 16 || t == 22) {
                LivingEntity aim = target != null ? target : findNearestHostile(sl, 26.0D);
                if (aim != null) {
                    float rad = getYRot() * Mth.DEG_TO_RAD;
                    double side = (t == 10) ? -0.75D : (t == 22 ? 0.75D : 0.0D);
                    Vec3 right = new Vec3(Math.cos(rad), 0.0D, Math.sin(rad));
                    Vec3 fwd = Vec3.directionFromRotation(0.0F, getYRot());
                    Vec3 origin = position().add(right.scale(side)).add(fwd.scale(0.9D)).add(0.0D, 4.35D, 0.0D);
                    Vec3 initVel = right.scale(side * 0.25D).add(fwd.scale(0.55D)).add(0.0D, 0.24D, 0.0D);
                    CryoIcicleProjectile.spawn(sl, this, aim, origin, initVel, 11.0F);
                    sl.playSound(null, origin.x, origin.y, origin.z, SoundEvents.PLAYER_HURT_FREEZE, SoundSource.NEUTRAL, 0.85F, 1.35F);
                    sl.sendParticles(ParticleTypes.SNOWFLAKE, origin.x, origin.y, origin.z, 8, 0.2D, 0.2D, 0.2D, 0.04D);
                }
            }
            if (t >= 52) {
                attackCooldown = 28;
                playAnim(ANIM_IDLE);
            }
        } else if (anim == ANIM_FROST_NOVA) {
            if (t == 16) {
                AABB box = getBoundingBox().inflate(6.0D, 2.5D, 6.0D);
                for (LivingEntity e : sl.getEntitiesOfClass(LivingEntity.class, box,
                        le -> le.isAlive() && le instanceof Enemy && le != this)) {
                    if (e.distanceToSqr(this) <= 6.2D * 6.2D) {
                        StavesElementBridge.dealElementalDamage(e, this, "CRYO", 16.0F, 1.0F, false, 0.20F);
                        e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 80, 2));
                        Vec3 push = e.position().subtract(position()).multiply(1.0D, 0.0D, 1.0D);
                        if (push.lengthSqr() > 1e-4D) {
                            Vec3 p = push.normalize().scale(0.95D);
                            e.push(p.x, 0.35D, p.z);
                        }
                    }
                }
                sl.playSound(null, getX(), getY() + 1.2D, getZ(), SoundEvents.GLASS_BREAK, SoundSource.NEUTRAL, 1.4F, 0.72F);
                sl.sendParticles(ParticleTypes.SNOWFLAKE, getX(), getY() + 1.0D, getZ(), 48, 3.2D, 0.5D, 3.2D, 0.10D);
                sl.sendParticles(ParticleTypes.CLOUD, getX(), getY() + 0.6D, getZ(), 20, 2.8D, 0.3D, 2.8D, 0.06D);
            }
            if (t >= 68) {
                attackCooldown = 36;
                playAnim(ANIM_IDLE);
            }
        }
    }

    private LivingEntity findNearestHostile(ServerLevel sl, double range) {
        AABB box = getBoundingBox().inflate(range, 10.0D, range);
        LivingEntity best = null;
        double bestSq = range * range;
        for (LivingEntity e : sl.getEntitiesOfClass(LivingEntity.class, box,
                le -> le.isAlive() && le instanceof Enemy && le != this && !(le instanceof RuinMechEntity))) {
            double d = e.distanceToSqr(this);
            if (d < bestSq) {
                bestSq = d;
                best = e;
            }
        }
        return best;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("PartsMask", getPartsMask());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("PartsMask")) entityData.set(DATA_PARTS_MASK, tag.getInt("PartsMask"));
        updateAssemblyLabel();
    }
}

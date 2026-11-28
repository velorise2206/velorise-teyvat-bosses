package com.velorise.teyvatbosses.entity;

import com.velorise.teyvatbosses.compat.StavesElementBridge;
import com.velorise.teyvatbosses.item.GoldflameQucusaurCrestItem;
import com.velorise.teyvatbosses.network.GoldflameQucusaurControlPayload;
import com.velorise.teyvatbosses.registry.TeyvatBossesItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
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
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

/**
 * Ridable fire-breathing Goldflame Qucusaur Mount (hatched from {@link GoldflameQucusaurEggEntity}).
 * <ul>
 *   <li>Shares the unified {@code goldflame_qucusaur.bbmodel} at {@code 0.62x} scale with toggleable {@code saddle}.</li>
 *   <li>Ground Walk (WASD) + Space Takeoff -> Seamless Flight (Space ascend, Descend key / look down, Sprint Glide Boost).</li>
 *   <li>4 Damaging Skills: LMB Tap Fireball Spit (crosshair raycast, zero auto-aim), LMB Hold Golden Flame Breath
 *       (18-block volumetric stream), RMB Air Golden Dive (6-block 18 HP Pyro AoE), RMB Ground Heatwave Takeoff.</li>
 *   <li>Shift + Right-Click packs the dragon into {@link GoldflameQucusaurCrestItem} preserving HP &amp; Phlogiston.</li>
 * </ul>
 */
public class GoldflameQucusaurMountEntity extends PathfinderMob {
    public static final float MAX_PHLOGISTON = 200.0F;

    public static final int ACTION_NONE       = 0;
    public static final int ACTION_TAKEOFF    = 1;
    public static final int ACTION_SPIT       = 2;
    public static final int ACTION_BREATH     = 3;
    public static final int ACTION_DIVE       = 4;

    private static final EntityDataAccessor<Boolean> DATA_SADDLED =
            SynchedEntityData.defineId(GoldflameQucusaurMountEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_FLYING =
            SynchedEntityData.defineId(GoldflameQucusaurMountEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_BOOSTING =
            SynchedEntityData.defineId(GoldflameQucusaurMountEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_ACTION =
            SynchedEntityData.defineId(GoldflameQucusaurMountEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_ACTION_TICKS =
            SynchedEntityData.defineId(GoldflameQucusaurMountEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DATA_PHLOGISTON =
            SynchedEntityData.defineId(GoldflameQucusaurMountEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_BANK_ROLL =
            SynchedEntityData.defineId(GoldflameQucusaurMountEntity.class, EntityDataSerializers.FLOAT);

    private UUID ownerUuid;
    private boolean inputAscend = false;
    private boolean inputDescend = false;
    private boolean inputBreath = false;
    private boolean inputBoost = false;
    private int spitCooldown = 0;
    private int diveCooldown = 0;

    public GoldflameQucusaurMountEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
        this.setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 160.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.34D)
                .add(Attributes.ARMOR, 8.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.7D)
                .add(Attributes.STEP_HEIGHT, 1.5D);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_SADDLED, false);
        builder.define(DATA_FLYING, false);
        builder.define(DATA_BOOSTING, false);
        builder.define(DATA_ACTION, ACTION_NONE);
        builder.define(DATA_ACTION_TICKS, 0);
        builder.define(DATA_PHLOGISTON, MAX_PHLOGISTON);
        builder.define(DATA_BANK_ROLL, 0.0F);
    }

    public boolean isSaddled() { return entityData.get(DATA_SADDLED); }
    public void setSaddled(boolean saddled) { entityData.set(DATA_SADDLED, saddled); }
    public boolean isFlying() { return entityData.get(DATA_FLYING); }
    public boolean isBoosting() { return entityData.get(DATA_BOOSTING); }
    public int getActionState() { return entityData.get(DATA_ACTION); }
    public int getActionTicks() { return entityData.get(DATA_ACTION_TICKS); }
    public float getPhlogiston() { return entityData.get(DATA_PHLOGISTON); }
    public void setPhlogiston(float v) { entityData.set(DATA_PHLOGISTON, Mth.clamp(v, 0.0F, MAX_PHLOGISTON)); }
    public float getBankRoll() { return entityData.get(DATA_BANK_ROLL); }
    public boolean isBreathing() { return getActionState() == ACTION_BREATH; }

    public void setOwnerUuid(@Nullable UUID id) { this.ownerUuid = id; }
    @Nullable public UUID getOwnerUuid() { return ownerUuid; }

    public String getActiveAnimationClip() {
        int act = getActionState();
        if (act == ACTION_TAKEOFF) return "takeoff_roar";
        if (act == ACTION_SPIT) return "mount_fireball_spit";
        if (act == ACTION_DIVE) return "dive_bomb";
        if (act == ACTION_BREATH) return isFlying() ? "mount_breath_air" : "breath_sweep";
        if (isFlying()) {
            if (isBoosting()) return "mount_boost";
            double hSpeed = getDeltaMovement().horizontalDistance();
            return hSpeed > 0.35D ? "fly_glide" : "fly_hover";
        }
        return getDeltaMovement().horizontalDistance() > 0.04D ? "walk" : "idle_ground";
    }

    @Override
    public boolean fireImmune() { return true; }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    public boolean removeWhenFarAway(double dist) { return false; }

    @Override
    public @Nullable LivingEntity getControllingPassenger() {
        Entity first = getFirstPassenger();
        return first instanceof LivingEntity le ? le : null;
    }

    @Override
    protected Vec3 getPassengerAttachmentPoint(Entity entity, net.minecraft.world.entity.EntityDimensions dims, float scale) {
        return new Vec3(0.0D, 1.55D, 0.15D);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (level().isClientSide()) return InteractionResult.SUCCESS;
        ItemStack stack = player.getItemInHand(hand);

        // Equip Phlogiston Saddle on a newly hatched dragon
        if (!isSaddled() && TeyvatBossesItems.phlogistonSaddle() != null && stack.is(TeyvatBossesItems.phlogistonSaddle())) {
            setSaddled(true);
            if (!player.isCreative()) stack.shrink(1);
            level().playSound(null, getX(), getY(), getZ(), SoundEvents.HORSE_SADDLE, SoundSource.NEUTRAL, 1.0F, 1.0F);
            player.displayClientMessage(Component.translatable("message.velorise_teyvat_bosses.qucusaur_mount.saddled")
                    .withStyle(ChatFormatting.GOLD), true);
            return InteractionResult.CONSUME;
        }

        // Shift + Right-Click packs the dragon into GoldflameQucusaurCrestItem
        if (player.isShiftKeyDown() && stack.isEmpty()) {
            if (ownerUuid == null || ownerUuid.equals(player.getUUID()) || player.isCreative()) {
                ItemStack crest = GoldflameQucusaurCrestItem.packFromMount(this);
                if (!player.getInventory().add(crest)) {
                    player.drop(crest, false);
                }
                level().playSound(null, getX(), getY(), getZ(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.NEUTRAL, 1.0F, 1.2F);
                discard();
                return InteractionResult.CONSUME;
            }
        }

        if (!isSaddled()) {
            player.displayClientMessage(Component.translatable("message.velorise_teyvat_bosses.qucusaur_mount.need_saddle")
                    .withStyle(ChatFormatting.YELLOW), true);
            return InteractionResult.CONSUME;
        }

        if (!isVehicle()) {
            player.startRiding(this);
            return InteractionResult.CONSUME;
        }
        return super.mobInteract(player, hand);
    }

    public void handleControlAction(int action, ServerPlayer player) {
        if (!isSaddled() || getControllingPassenger() != player) return;
        switch (action) {
            case GoldflameQucusaurControlPayload.ACTION_SPACE_TAKEOFF -> {
                if (!isFlying()) {
                    startTakeoff(false, player);
                }
            }
            case GoldflameQucusaurControlPayload.ACTION_ASCEND_START -> inputAscend = true;
            case GoldflameQucusaurControlPayload.ACTION_ASCEND_STOP -> inputAscend = false;
            case GoldflameQucusaurControlPayload.ACTION_DESCEND_START -> inputDescend = true;
            case GoldflameQucusaurControlPayload.ACTION_DESCEND_STOP -> inputDescend = false;
            case GoldflameQucusaurControlPayload.ACTION_BOOST_START -> inputBoost = true;
            case GoldflameQucusaurControlPayload.ACTION_BOOST_STOP -> inputBoost = false;
            case GoldflameQucusaurControlPayload.ACTION_FIREBALL_SPIT -> tryFireballSpit(player);
            case GoldflameQucusaurControlPayload.ACTION_BREATH_START -> {
                inputBreath = true;
                if (getPhlogiston() >= 8.0F && getActionState() != ACTION_DIVE) {
                    setAction(ACTION_BREATH);
                }
            }
            case GoldflameQucusaurControlPayload.ACTION_BREATH_STOP -> {
                inputBreath = false;
                if (getActionState() == ACTION_BREATH) {
                    setAction(ACTION_NONE);
                }
            }
            case GoldflameQucusaurControlPayload.ACTION_RMB_SKILL -> {
                if (isFlying()) {
                    tryGoldenDive(player);
                } else {
                    startTakeoff(true, player);
                }
            }
            default -> {}
        }
    }

    private void setAction(int act) {
        entityData.set(DATA_ACTION, act);
        entityData.set(DATA_ACTION_TICKS, 0);
    }

    private void startTakeoff(boolean heatwaveBlast, ServerPlayer player) {
        if (heatwaveBlast) {
            if (getPhlogiston() < 50.0F) return;
            setPhlogiston(getPhlogiston() - 50.0F);
            if (level() instanceof ServerLevel sl) {
                AABB box = getBoundingBox().inflate(5.0D, 3.0D, 5.0D);
                for (LivingEntity e : sl.getEntitiesOfClass(LivingEntity.class, box,
                        t -> t != this && t != player && t.isAlive())) {
                    StavesElementBridge.dealElementalDamage(e, player, "PYRO", 10.0F, 1.0F, false, 0.2F);
                    e.igniteForSeconds(4.0F);
                    Vec3 push = e.position().subtract(position()).multiply(1.0D, 0.0D, 1.0D).normalize().scale(1.15D);
                    e.push(push.x, 0.45D, push.z);
                }
                sl.sendParticles(ParticleTypes.FLAME, getX(), getY() + 0.5D, getZ(), 40, 2.4D, 0.4D, 2.4D, 0.16D);
            }
        }
        entityData.set(DATA_FLYING, true);
        setNoGravity(true);
        setAction(ACTION_TAKEOFF);
        setDeltaMovement(getDeltaMovement().x, heatwaveBlast ? 1.15D : 0.72D, getDeltaMovement().z);
        level().playSound(null, getX(), getY(), getZ(), SoundEvents.ENDER_DRAGON_FLAP, SoundSource.NEUTRAL, 1.1F, 1.15F);
    }

    private void tryFireballSpit(ServerPlayer player) {
        if (spitCooldown > 0 || getPhlogiston() < 30.0F) return;
        setPhlogiston(getPhlogiston() - 30.0F);
        spitCooldown = 14;
        setAction(ACTION_SPIT);
        Vec3 look = player.getLookAngle().normalize();
        Vec3 mouth = position().add(look.scale(1.9D)).add(0.0D, 1.75D, 0.0D);
        GoldflameFireballEntity.spawn(level(), player, mouth, look, 1.85F, 12.0F, 2.5F, true);
        level().playSound(null, mouth.x, mouth.y, mouth.z, SoundEvents.BLAZE_SHOOT, SoundSource.NEUTRAL, 1.0F, 1.05F);
    }

    private void tryGoldenDive(ServerPlayer player) {
        if (diveCooldown > 0 || getPhlogiston() < 80.0F) return;
        setPhlogiston(getPhlogiston() - 80.0F);
        diveCooldown = 50;
        setAction(ACTION_DIVE);
        Vec3 look = player.getLookAngle().normalize();
        Vec3 diveVec = new Vec3(look.x * 1.45D, Math.min(-1.15D, look.y * 1.65D), look.z * 1.45D);
        setDeltaMovement(diveVec);
        level().playSound(null, getX(), getY(), getZ(), SoundEvents.FIRECHARGE_USE, SoundSource.NEUTRAL, 1.15F, 0.85F);
    }

    @Override
    public void travel(Vec3 travelVector) {
        LivingEntity rider = getControllingPassenger();
        if (rider instanceof Player player && isSaddled()) {
            float targetYaw = player.getYRot();
            setYRot(Mth.rotLerp(0.28F, getYRot(), targetYaw));
            yRotO = getYRot();
            yBodyRot = getYRot();
            yHeadRot = getYRot();
            setXRot(player.getXRot() * 0.5F);

            float forward = player.zza;
            float strafe = player.xxa;

            if (isFlying()) {
                setNoGravity(true);
                int act = getActionState();
                if (act == ACTION_DIVE) {
                    move(MoverType.SELF, getDeltaMovement());
                    if (onGround() || verticalCollision) {
                        detonateGoldenDive(player);
                    }
                    return;
                }

                boolean boosting = inputBoost && forward > 0.05F && getPhlogiston() > 2.0F;
                entityData.set(DATA_BOOSTING, boosting);
                if (boosting && !level().isClientSide()) {
                    setPhlogiston(getPhlogiston() - 1.1F);
                }

                double speed = boosting ? 1.50D : 0.72D; // ~30 blocks/s on boost
                Vec3 look = Vec3.directionFromRotation(0.0F, getYRot());
                Vec3 right = look.cross(new Vec3(0.0D, 1.0D, 0.0D)).normalize();
                double vx = (look.x * forward - right.x * strafe * 0.65D) * speed;
                double vz = (look.z * forward - right.z * strafe * 0.65D) * speed;
                double vy = 0.0D;
                if (inputAscend) vy += 0.45D;
                if (inputDescend) vy -= 0.48D;
                if (!inputAscend && !inputDescend && Math.abs(forward) > 0.1F) {
                    vy = Mth.clamp(-player.getXRot() / 90.0D * 0.55D, -0.45D, 0.45D);
                }
                if (act == ACTION_TAKEOFF && getActionTicks() < 12) {
                    vy = Math.max(vy, 0.55D);
                }

                Vec3 nextVel = getDeltaMovement().lerp(new Vec3(vx, vy, vz), 0.25D);
                setDeltaMovement(nextVel);
                move(MoverType.SELF, getDeltaMovement());

                float bankTarget = Mth.clamp(-strafe * 22.0F, -24.0F, 24.0F);
                entityData.set(DATA_BANK_ROLL, Mth.lerp(0.18F, getBankRoll(), bankTarget));

                // Smoothly land when touching ground while descending or moving slowly downward
                if (onGround() && vy <= 0.05D && act != ACTION_TAKEOFF) {
                    entityData.set(DATA_FLYING, false);
                    entityData.set(DATA_BOOSTING, false);
                    setNoGravity(false);
                    entityData.set(DATA_BANK_ROLL, 0.0F);
                }
                return;
            } else {
                setNoGravity(false);
                entityData.set(DATA_BOOSTING, false);
                entityData.set(DATA_BANK_ROLL, 0.0F);
                setSpeed(0.30F);
                super.travel(new Vec3(strafe * 0.55F, travelVector.y, forward));
                return;
            }
        }

        // Unridden: settle gently to ground
        inputAscend = inputDescend = inputBreath = inputBoost = false;
        entityData.set(DATA_BOOSTING, false);
        if (isFlying()) {
            setNoGravity(false);
            if (onGround()) entityData.set(DATA_FLYING, false);
        }
        super.travel(travelVector);
    }

    private void detonateGoldenDive(Player rider) {
        setAction(ACTION_NONE);
        entityData.set(DATA_FLYING, false);
        setNoGravity(false);
        setDeltaMovement(Vec3.ZERO);
        if (level() instanceof ServerLevel sl) {
            AABB box = getBoundingBox().inflate(6.0D, 3.0D, 6.0D);
            for (LivingEntity e : sl.getEntitiesOfClass(LivingEntity.class, box,
                    t -> t != this && t != rider && t.isAlive() && t.position().distanceToSqr(position()) <= 36.0D)) {
                StavesElementBridge.dealElementalDamage(e, rider, "PYRO", 18.0F, 1.5F, false, 0.30F);
                e.igniteForSeconds(5.0F);
                Vec3 push = e.position().subtract(position()).multiply(1.0D, 0.0D, 1.0D).normalize().scale(1.35D);
                e.push(push.x, 0.52D, push.z);
            }
            sl.playSound(null, getX(), getY(), getZ(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.NEUTRAL, 1.3F, 0.9F);
            sl.sendParticles(ParticleTypes.EXPLOSION, getX(), getY() + 0.4D, getZ(), 4, 1.2D, 0.3D, 1.2D, 0.0D);
            sl.sendParticles(ParticleTypes.FLAME, getX(), getY() + 0.4D, getZ(), 45, 2.8D, 0.4D, 2.8D, 0.16D);
        }
        BossClientBridge.triggerCameraShake(position(), 0.55F, 16.0D);
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide()) return;
        ServerLevel sl = (ServerLevel) level();

        if (spitCooldown > 0) spitCooldown--;
        if (diveCooldown > 0) diveCooldown--;

        int act = getActionState();
        int ticks = getActionTicks() + 1;
        entityData.set(DATA_ACTION_TICKS, ticks);

        if (act == ACTION_TAKEOFF && ticks >= 22) {
            setAction(ACTION_NONE);
        } else if (act == ACTION_SPIT && ticks >= 12) {
            setAction(ACTION_NONE);
        } else if (act == ACTION_DIVE && ticks >= 45) {
            setAction(ACTION_NONE);
        } else if (act == ACTION_BREATH) {
            LivingEntity rider = getControllingPassenger();
            if (!inputBreath || !(rider instanceof Player player) || getPhlogiston() < 3.0F) {
                setAction(ACTION_NONE);
            } else {
                setPhlogiston(getPhlogiston() - 2.75F); // ~55/s
                if (ticks % 5 == 0) {
                    Vec3 look = player.getLookAngle().normalize();
                    Vec3 mouth = position().add(0.0D, 1.75D, 0.0D).add(look.scale(1.6D));
                    for (double d = 1.5D; d <= 18.0D; d += 2.0D) {
                        Vec3 pt = mouth.add(look.scale(d));
                        sl.sendParticles(ParticleTypes.FLAME, pt.x, pt.y, pt.z, 2, 0.35D, 0.35D, 0.35D, 0.02D);
                        AABB box = new AABB(pt.x - 1.6D, pt.y - 1.6D, pt.z - 1.6D, pt.x + 1.6D, pt.y + 1.6D, pt.z + 1.6D);
                        List<LivingEntity> hits = sl.getEntitiesOfClass(LivingEntity.class, box,
                                e -> e != this && e != player && e.isAlive());
                        for (LivingEntity e : hits) {
                            StavesElementBridge.dealElementalDamage(e, player, "PYRO", 5.0F, 0.5F, false, 0.20F);
                            e.igniteForSeconds(3.0F);
                        }
                    }
                }
            }
        } else {
            // Regenerate Phlogiston naturally
            float regen = isFlying() ? (isBoosting() ? 0.0F : 0.45F) : 1.50F;
            if (regen > 0.0F && getPhlogiston() < MAX_PHLOGISTON) {
                setPhlogiston(getPhlogiston() + regen);
            }
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (ownerUuid != null) tag.putUUID("Owner", ownerUuid);
        tag.putBoolean("Saddled", isSaddled());
        tag.putFloat("Phlogiston", getPhlogiston());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.hasUUID("Owner")) ownerUuid = tag.getUUID("Owner");
        setSaddled(tag.getBoolean("Saddled"));
        if (tag.contains("Phlogiston")) setPhlogiston(tag.getFloat("Phlogiston"));
    }
}

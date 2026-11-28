package com.velorise.teyvatbosses.entity;

import com.velorise.teyvatbosses.compat.StavesElementBridge;
import com.velorise.teyvatbosses.item.GoldflameQucusaurCrestItem;
import com.velorise.teyvatbosses.network.GoldflameQucusaurControlPayload;
import com.velorise.teyvatbosses.registry.TeyvatBossesItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Items;
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
    public static final int ACTION_DASH       = 5;
    public static final int ACTION_COMBO      = 6;

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
    private static final EntityDataAccessor<Float> DATA_FLIGHT_PITCH =
            SynchedEntityData.defineId(GoldflameQucusaurMountEntity.class, EntityDataSerializers.FLOAT);

    private UUID ownerUuid;
    private boolean followMode = false;
    private Vec3 fluteRecallTarget = null;
    private int fluteRecallTicks = 0;
    private boolean inputAscend = false;
    private boolean inputDescend = false;
    private boolean inputBreath = false;
    private boolean inputBoost = false;
    private boolean inputAimDive = false;
    private int spitCooldown = 0;
    private int diveCooldown = 0;
    private int dashCooldown = 0;
    private int comboStep = 0;
    private int comboWindowTicks = 0;
    private Vec3 dashDir = Vec3.ZERO;
    private float prevBankRoll = 0.0F;
    private float prevFlightPitch = 0.0F;
    private double smoothSeatH = 1.53D;
    private double smoothSeatFwd = 0.14D;

    public GoldflameQucusaurMountEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
        this.setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 320.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.34D)
                .add(Attributes.ARMOR, 8.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.7D)
                .add(Attributes.STEP_HEIGHT, 1.5D);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_SADDLED, true);
        builder.define(DATA_FLYING, false);
        builder.define(DATA_BOOSTING, false);
        builder.define(DATA_ACTION, ACTION_NONE);
        builder.define(DATA_ACTION_TICKS, 0);
        builder.define(DATA_PHLOGISTON, MAX_PHLOGISTON);
        builder.define(DATA_BANK_ROLL, 0.0F);
        builder.define(DATA_FLIGHT_PITCH, 0.0F);
    }

    public boolean isSaddled() { return true; }
    public void setSaddled(boolean saddled) { entityData.set(DATA_SADDLED, saddled); }
    public boolean isFlying() { return entityData.get(DATA_FLYING); }
    public boolean isBoosting() { return entityData.get(DATA_BOOSTING); }
    public int getActionState() { return entityData.get(DATA_ACTION); }
    public int getActionTicks() { return entityData.get(DATA_ACTION_TICKS); }
    public float getPhlogiston() { return entityData.get(DATA_PHLOGISTON); }
    public void setPhlogiston(float v) { entityData.set(DATA_PHLOGISTON, Mth.clamp(v, 0.0F, MAX_PHLOGISTON)); }
    public float getBankRoll() { return entityData.get(DATA_BANK_ROLL); }
    public float getPrevBankRoll() { return prevBankRoll; }
    public float getFlightPitch() { return entityData.get(DATA_FLIGHT_PITCH); }
    public float getPrevFlightPitch() { return prevFlightPitch; }
    public boolean isBreathing() { return getActionState() == ACTION_BREATH; }

    // Live client-side forward-kinematics saddle animation offset (updated every frame by GoldflameQucusaurMountRenderer)
    private float clientSaddleDx = 0.0F;
    private float clientSaddleDy = 0.0F;
    private float clientSaddleDz = 0.0F;
    private float clientSaddlePitchDeg = 0.0F;
    private float clientSaddleRollDeg = 0.0F;

    public void setClientSaddleSync(float dx, float dy, float dz, float pitchDeg, float rollDeg) {
        this.clientSaddleDx = dx;
        this.clientSaddleDy = dy;
        this.clientSaddleDz = dz;
        this.clientSaddlePitchDeg = pitchDeg;
        this.clientSaddleRollDeg = rollDeg;
    }

    public float getClientSaddleDx() { return clientSaddleDx; }
    public float getClientSaddleDy() { return clientSaddleDy; }
    public float getClientSaddleDz() { return clientSaddleDz; }
    public float getClientSaddlePitchDeg() { return clientSaddlePitchDeg; }
    public float getClientSaddleRollDeg() { return clientSaddleRollDeg; }

    /** Syncs live client flight keys so client-side travel() ascends, descends, and boosts immediately. */
    public void setClientFlightInputs(boolean ascend, boolean descend, boolean boost) {
        this.inputAscend = ascend;
        this.inputDescend = descend;
        this.inputBoost = boost;
    }

    /** Immediate client-side takeoff prediction so pressing Space lifts off with zero lag. */
    public void triggerClientTakeoff() {
        if (!isFlying()) {
            entityData.set(DATA_FLYING, true);
            setNoGravity(true);
            setDeltaMovement(getDeltaMovement().x, 0.72D, getDeltaMovement().z);
        }
    }

    public void setOwnerUuid(@Nullable UUID id) { this.ownerUuid = id; }
    @Nullable public UUID getOwnerUuid() { return ownerUuid; }

    public boolean isNearGround() {
        BlockPos base = blockPosition();
        for (int dy = 1; dy <= 3; dy++) {
            BlockPos check = base.below(dy);
            if (!level().getBlockState(check).isAir()) {
                return true;
            }
        }
        return false;
    }

    /** Base full-body locomotion / action clip (ACTION_SPIT is layered additively so wings never freeze). */
    public String getActiveAnimationClip() {
        int act = getActionState();
        if (act == ACTION_TAKEOFF) return "takeoff_roar";
        if (act == ACTION_DIVE) return "charge_drill";
        if (act == ACTION_DASH) return "dive_bomb";
        if (act == ACTION_COMBO) return "wing_swipe";
        if (act == ACTION_BREATH) return isFlying() ? "mount_breath_air" : "breath_sweep";
        if (isFlying()) {
            if (isBoosting()) return "mount_boost";
            double dy = getY() - yo;
            boolean climbing = inputAscend || dy > 0.012D || getDeltaMovement().y > 0.025D;
            if (climbing) {
                return "fly_hover";
            }
            double hSpeed = getDeltaMovement().horizontalDistance();
            if (hSpeed > 0.36D) return "fly_glide";
            return isNearGround() ? "low_altitude_hover" : "fly_hover";
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
        return new Vec3(0.0D, 1.53D, -0.14D);
    }

    @Override
    protected void positionRider(Entity passenger, MoveFunction callback) {
        if (!hasPassenger(passenger)) return;

        // Anchor rider to the exact bind-pose saddle cushion; live per-frame animation bob & pitch
        // are synced 1:1 from the posed saddle bone in GoldflameQucusaurMountRenderer + LivingEntityRendererMixin.
        double targetH = 1.56D;
        double targetFwd = 0.125D;
        smoothSeatH = Mth.lerp(0.25D, smoothSeatH, targetH);
        smoothSeatFwd = Mth.lerp(0.25D, smoothSeatFwd, targetFwd);

        // Exact world transform matching GoldflameQucusaurMountRenderer: R_y(180 - yBodyRot) * R_z(bankRoll)
        float yawRad = this.yBodyRot * Mth.DEG_TO_RAD;
        double cosY = Mth.cos(yawRad);
        double sinY = Mth.sin(yawRad);
        double bankRad = isFlying() ? getBankRoll() * Mth.DEG_TO_RAD : 0.0D;
        double bankSide = smoothSeatH * Math.sin(bankRad);
        double bankY = smoothSeatH * Math.cos(bankRad);

        // Player sitting hips sit ~0.56 blocks above passenger entity feet, placing rider flush on saddle_cushion
        double riderSitOffset = 0.56D;
        double px = getX() - sinY * smoothSeatFwd + cosY * bankSide;
        double py = getY() + bankY - riderSitOffset;
        double pz = getZ() + cosY * smoothSeatFwd + sinY * bankSide;
        callback.accept(passenger, px, py, pz);
        clampPassengerBody(passenger);
    }

    @Override
    public void onPassengerTurned(Entity passenger) {
        clampPassengerBody(passenger);
    }

    @Override
    protected void removePassenger(Entity passenger) {
        super.removePassenger(passenger);
        inputAscend = inputDescend = inputBreath = inputBoost = inputAimDive = false;
        entityData.set(DATA_BOOSTING, false);
        int act = getActionState();
        if (act == ACTION_DIVE || act == ACTION_DASH || act == ACTION_BREATH) {
            setAction(ACTION_NONE);
        }
        if (fluteRecallTicks <= 0) {
            setNoGravity(false);
        }
    }

    private void clampPassengerBody(Entity passenger) {
        // Align rider torso & legs to the dragon's spine while keeping 360-degree free camera/head look
        passenger.setYBodyRot(this.yBodyRot);
        if (passenger instanceof LivingEntity le) {
            le.yBodyRotO = this.yBodyRotO;
        }
    }

    @Override
    public Vec3 getDismountLocationForPassenger(LivingEntity passenger) {
        float yawRad = this.yBodyRot * Mth.DEG_TO_RAD;
        double rightX = Mth.cos(yawRad);
        double rightZ = Mth.sin(yawRad);
        double[] sides = {1.65D, -1.65D};
        for (double s : sides) {
            double tx = getX() + rightX * s;
            double tz = getZ() + rightZ * s;
            BlockPos bp = BlockPos.containing(tx, getY() + 0.5D, tz);
            if (!level().getBlockState(bp).isSuffocating(level(), bp)
                    && !level().getBlockState(bp.above()).isSuffocating(level(), bp.above())) {
                return new Vec3(tx, getY() + 0.25D, tz);
            }
        }
        return new Vec3(getX(), getY() + 1.65D, getZ());
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (level().isClientSide()) return InteractionResult.SUCCESS;
        ItemStack stack = player.getItemInHand(hand);

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

        // Feed normal Minecraft food or fire items when injured to heal the dragon
        FoodProperties food = stack.get(DataComponents.FOOD);
        if ((food != null || isFireHealItem(stack)) && getHealth() < getMaxHealth()) {
            if (level() instanceof ServerLevel sl) {
                float healAmt = food != null ? Math.max(10.0F, food.nutrition() * 5.0F) : 40.0F;
                heal(healAmt);
                if (!player.isCreative()) stack.shrink(1);
                sl.sendParticles(ParticleTypes.HEART, getX(), getY() + 2.0D, getZ(), 6, 0.6D, 0.4D, 0.6D, 0.02D);
                sl.playSound(null, getX(), getY(), getZ(), SoundEvents.GENERIC_EAT, SoundSource.NEUTRAL, 1.0F, 0.9F);
                return InteractionResult.CONSUME;
            }
            return InteractionResult.SUCCESS;
        }

        if (!isVehicle()) {
            player.startRiding(this);
            return InteractionResult.CONSUME;
        }
        return super.mobInteract(player, hand);
    }

    public void handleControlAction(int action, ServerPlayer player) {
        if (getControllingPassenger() != player) return;
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
            case GoldflameQucusaurControlPayload.ACTION_FIREBALL_SPIT -> {}
            case GoldflameQucusaurControlPayload.ACTION_BREATH_START -> {
                inputBreath = true;
                if (getPhlogiston() >= 8.0F && getActionState() != ACTION_DIVE && getActionState() != ACTION_DASH) {
                    setAction(ACTION_BREATH);
                }
            }
            case GoldflameQucusaurControlPayload.ACTION_BREATH_STOP -> {
                inputBreath = false;
                if (getActionState() == ACTION_BREATH) {
                    setAction(ACTION_NONE);
                }
            }
            case GoldflameQucusaurControlPayload.ACTION_RMB_DASH -> tryForwardFlameDash(player);
            case GoldflameQucusaurControlPayload.ACTION_RMB_AIM_DIVE_START -> startAimedDrillDive(player);
            case GoldflameQucusaurControlPayload.ACTION_RMB_AIM_DIVE_STOP -> stopAimedDrillDive(player);
            case GoldflameQucusaurControlPayload.ACTION_RMB_SKILL -> tryForwardFlameDash(player);
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

    /** LMB Tap: 3-hit chained Dragon Combo (Hit 1 Wing Slash -> Hit 2 Flame Bite -> Hit 3 Explosive Fireball Finisher). */
    private void tryComboAttack(ServerPlayer player) {
        if (spitCooldown > 0 || getActionState() == ACTION_DIVE) return;
        if (comboWindowTicks <= 0) comboStep = 0;
        comboStep = (comboStep % 3) + 1;
        comboWindowTicks = 32;
        spitCooldown = (comboStep == 3) ? 12 : 7;

        Vec3 look = player.getLookAngle().normalize();
        Vec3 center = position().add(look.scale(2.8D)).add(0.0D, 1.4D, 0.0D);

        if (comboStep == 3) {
            setAction(ACTION_SPIT);
            if (getPhlogiston() >= 15.0F) {
                setPhlogiston(getPhlogiston() - 15.0F);
            }
            Vec3 mouth = position().add(look.scale(1.9D)).add(0.0D, 1.75D, 0.0D);
            GoldflameFireballEntity.spawn(level(), player, mouth, look, 2.05F, 15.0F, 2.8F, true);
            level().playSound(null, mouth.x, mouth.y, mouth.z, SoundEvents.BLAZE_SHOOT, SoundSource.NEUTRAL, 1.15F, 0.92F);
        } else {
            setAction(ACTION_COMBO);
            level().playSound(null, getX(), getY() + 1.4D, getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.NEUTRAL, 1.2F, comboStep == 1 ? 0.95F : 0.78F);
        }

        if (level() instanceof ServerLevel sl) {
            float dmg = (comboStep == 1) ? 10.5F : (comboStep == 2 ? 13.0F : 15.5F);
            double rad = (comboStep == 3) ? 4.4D : 3.8D;
            AABB box = new AABB(center.x - rad, center.y - 2.4D, center.z - rad, center.x + rad, center.y + 2.4D, center.z + rad);
            for (LivingEntity e : sl.getEntitiesOfClass(LivingEntity.class, box,
                    t -> t != this && t != player && t.isAlive())) {
                StavesElementBridge.dealElementalDamage(e, player, "PYRO", dmg, 1.0F, false, 0.25F);
                e.igniteForSeconds(comboStep == 3 ? 5.0F : 3.0F);
                e.push(look.x * (0.55D + comboStep * 0.18D), 0.28D + comboStep * 0.08D, look.z * (0.55D + comboStep * 0.18D));
            }
            sl.sendParticles(ParticleTypes.FLAME, center.x, center.y, center.z, 18 + comboStep * 8, 1.4D, 0.6D, 1.4D, 0.12D);
            sl.sendParticles(ParticleTypes.SWEEP_ATTACK, center.x, center.y, center.z, 2, 0.5D, 0.3D, 0.5D, 0.0D);
        }
    }

    /** RMB Tap: Short high-speed piercing forward Flame Dash along the rider's aim direction. */
    private void tryForwardFlameDash(ServerPlayer player) {
        if (dashCooldown > 0 || getPhlogiston() < 22.0F || getActionState() == ACTION_DIVE) return;
        setPhlogiston(getPhlogiston() - 22.0F);
        dashCooldown = 16;
        setAction(ACTION_DASH);
        Vec3 look = player.getLookAngle().normalize();
        if (isFlying()) {
            this.dashDir = look;
        } else {
            Vec3 flat = new Vec3(look.x, 0.0D, look.z);
            this.dashDir = flat.lengthSqr() > 0.001D ? flat.normalize() : Vec3.directionFromRotation(0.0F, getYRot());
        }
        setDeltaMovement(dashDir.scale(1.65D));
        level().playSound(null, getX(), getY() + 1.2D, getZ(), SoundEvents.FIRECHARGE_USE, SoundSource.NEUTRAL, 1.15F, 1.12F);
    }

    /** RMB Hold: Continuous Golden Drill Dive rocketing straight along the rider's live crosshair until impact or release. */
    private void startAimedDrillDive(ServerPlayer player) {
        if (!isFlying() || diveCooldown > 0 || getPhlogiston() < 15.0F) return;
        inputAimDive = true;
        setNoGravity(true);
        setAction(ACTION_DIVE);
        Vec3 look = player.getLookAngle().normalize();
        setDeltaMovement(look.scale(1.62D));
        level().playSound(null, getX(), getY() + 1.2D, getZ(), SoundEvents.FIRECHARGE_USE, SoundSource.NEUTRAL, 1.3F, 0.80F);
    }

    private void stopAimedDrillDive(ServerPlayer player) {
        inputAimDive = false;
        if (getActionState() == ACTION_DIVE) {
            setAction(ACTION_NONE);
            diveCooldown = 18;
            setDeltaMovement(getDeltaMovement().scale(0.45D));
        }
    }

    @Override
    public void travel(Vec3 travelVector) {
        LivingEntity rider = getControllingPassenger();
        if (rider instanceof Player player) {
            prevBankRoll = getBankRoll();
            prevFlightPitch = getFlightPitch();
            float targetYaw = player.getYRot();
            float yawDelta = Mth.wrapDegrees(targetYaw - getYRot());
            float turnRate = isFlying() ? 0.22F : 0.28F;
            setYRot(Mth.rotLerp(turnRate, getYRot(), targetYaw));
            yBodyRot = getYRot();
            yHeadRot = player.getYRot();
            setXRot(Mth.clamp(player.getXRot() * 0.45F, -35.0F, 35.0F));

            float forward = player.zza;
            float strafe = player.xxa;

            if (isFlying()) {
                setNoGravity(true);
                int act = getActionState();
                if (act == ACTION_DIVE) {
                    // RMB Hold: continuously steer straight along rider's live 3D crosshair until impact or release!
                    Vec3 aim = player.getLookAngle().normalize();
                    Vec3 drillVel = getDeltaMovement().lerp(aim.scale(1.65D), 0.35D);
                    setDeltaMovement(drillVel);
                    float targetPitch = Mth.clamp(player.getXRot() * 0.85F, -55.0F, 55.0F);
                    entityData.set(DATA_FLIGHT_PITCH, Mth.lerp(0.30F, getFlightPitch(), targetPitch));
                    move(MoverType.SELF, getDeltaMovement());
                    if (onGround() || verticalCollision || horizontalCollision) {
                        detonateGoldenDive(player);
                    }
                    return;
                }
                if (act == ACTION_DASH) {
                    Vec3 dVel = (dashDir.lengthSqr() > 0.01D ? dashDir : player.getLookAngle().normalize()).scale(1.55D);
                    setDeltaMovement(dVel);
                    entityData.set(DATA_FLIGHT_PITCH, Mth.lerp(0.30F, getFlightPitch(), Mth.clamp(player.getXRot() * 0.65F, -35.0F, 35.0F)));
                    move(MoverType.SELF, getDeltaMovement());
                    return;
                }

                boolean boosting = inputBoost && forward > 0.05F && getPhlogiston() > 2.0F;
                entityData.set(DATA_BOOSTING, boosting);
                if (boosting && !level().isClientSide()) {
                    setPhlogiston(getPhlogiston() - 1.1F);
                }

                double speed = boosting ? 1.50D : 0.78D;
                // True 3D look-pitch flight: looking up + W climbs smoothly, looking down + W descends smoothly!
                Vec3 look3D = player.getLookAngle().normalize();
                Vec3 flatFwd = Vec3.directionFromRotation(0.0F, getYRot());
                Vec3 right = flatFwd.cross(new Vec3(0.0D, 1.0D, 0.0D)).normalize();
                double vx = (look3D.x * forward - right.x * strafe * 0.65D) * speed;
                double vz = (look3D.z * forward - right.z * strafe * 0.65D) * speed;
                double vy = 0.0D;
                if (Math.abs(forward) > 0.05F) {
                    vy = look3D.y * forward * speed;
                    // Realistic dragon gliding sink: gliding level/forward without climbing or boosting gently loses altitude
                    if (!inputAscend && !boosting && vy <= 0.025D) {
                        vy -= 0.038D;
                    }
                }
                if (inputAscend) vy = Math.max(vy, 0.48D);
                if (inputDescend) vy = Math.min(vy, -0.50D);
                if (act == ACTION_TAKEOFF && getActionTicks() < 12) {
                    vy = Math.max(vy, 0.55D);
                }

                Vec3 nextVel = getDeltaMovement().lerp(new Vec3(vx, vy, vz), 0.25D);
                setDeltaMovement(nextVel);
                move(MoverType.SELF, getDeltaMovement());

                float bankTarget = Mth.clamp(strafe * 16.0F - yawDelta * 0.65F, -24.0F, 24.0F);
                entityData.set(DATA_BANK_ROLL, Mth.lerp(0.18F, getBankRoll(), bankTarget));
                float pitchTarget = Mth.clamp((float) (-nextVel.y * 34.0D) + (boosting ? 9.0F : 0.0F), -24.0F, 26.0F);
                entityData.set(DATA_FLIGHT_PITCH, Mth.lerp(0.16F, getFlightPitch(), pitchTarget));

                // Smoothly land when touching ground while descending or moving slowly downward
                if (onGround() && !inputAscend && vy <= 0.02D && act != ACTION_TAKEOFF) {
                    entityData.set(DATA_FLYING, false);
                    entityData.set(DATA_BOOSTING, false);
                    setNoGravity(false);
                    entityData.set(DATA_BANK_ROLL, 0.0F);
                    entityData.set(DATA_FLIGHT_PITCH, 0.0F);
                }
                return;
            } else {
                setNoGravity(false);
                entityData.set(DATA_BOOSTING, false);
                entityData.set(DATA_BANK_ROLL, Mth.lerp(0.25F, getBankRoll(), 0.0F));
                entityData.set(DATA_FLIGHT_PITCH, Mth.lerp(0.25F, getFlightPitch(), 0.0F));
                if (getActionState() == ACTION_DASH) {
                    Vec3 dVel = (dashDir.lengthSqr() > 0.01D ? dashDir : Vec3.directionFromRotation(0.0F, getYRot())).scale(1.35D);
                    setDeltaMovement(dVel.x, getDeltaMovement().y, dVel.z);
                    move(MoverType.SELF, getDeltaMovement());
                    return;
                }
                // Grounded heavy dragon stride speed matched 1:1 to walk animation cadence
                setSpeed(0.155F);
                super.travel(new Vec3(strafe * 0.50F, travelVector.y, forward));
                return;
            }
        }

        // Unridden (e.g., rider died or dismounted mid-air): re-enable gravity and descend cleanly to ground
        prevBankRoll = getBankRoll();
        prevFlightPitch = getFlightPitch();
        entityData.set(DATA_BANK_ROLL, Mth.lerp(0.20F, getBankRoll(), 0.0F));
        entityData.set(DATA_FLIGHT_PITCH, Mth.lerp(0.20F, getFlightPitch(), 0.0F));
        inputAscend = inputDescend = inputBreath = inputBoost = inputAimDive = false;
        entityData.set(DATA_BOOSTING, false);
        if (fluteRecallTicks <= 0) {
            setNoGravity(false);
            if (isFlying()) {
                if (onGround()) {
                    entityData.set(DATA_FLYING, false);
                    setDeltaMovement(getDeltaMovement().x * 0.5D, 0.0D, getDeltaMovement().z * 0.5D);
                } else {
                    Vec3 cur = getDeltaMovement();
                    double fallY = Math.max(cur.y - 0.055D, -0.45D);
                    setDeltaMovement(cur.x * 0.85D, fallY, cur.z * 0.85D);
                    move(MoverType.SELF, getDeltaMovement());
                    if (onGround()) {
                        entityData.set(DATA_FLYING, false);
                    }
                    return;
                }
            }
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
        if (level().isClientSide()) {
            if (isBreathing()) {
                LivingEntity rider = getControllingPassenger();
                Vec3 dir = (rider != null)
                        ? rider.getLookAngle().normalize()
                        : Vec3.directionFromRotation(0.0F, yBodyRot).normalize();
                Vec3 mouth = position().add(0.0D, 1.58D, 0.0D).add(dir.scale(1.65D));
                float heatFactor = Mth.clamp(getActionTicks() / 80.0F, 0.0F, 1.0F);
                double range = 17.0D + heatFactor * 4.0D;
                GoldflameQucusaurEntity.spawnIceAndFireBreathParticles(this, mouth, dir, range, 0.95F, heatFactor);
            }
            return;
        }
        ServerLevel sl = (ServerLevel) level();

        if (spitCooldown > 0) spitCooldown--;
        if (diveCooldown > 0) diveCooldown--;
        if (dashCooldown > 0) dashCooldown--;
        if (comboWindowTicks > 0) comboWindowTicks--;

        // Autonomous Flute Recall, Follow/Stay AI, and Breeding (runs every server tick when unridden)
        if (!isVehicle()) {
            if (fluteRecallTicks > 0 && fluteRecallTarget != null) {
                fluteRecallTicks--;
                Vec3 delta = fluteRecallTarget.subtract(position());
                double d = delta.length();
                if (d <= 3.2D || fluteRecallTicks <= 0) {
                    fluteRecallTicks = 0;
                    fluteRecallTarget = null;
                    entityData.set(DATA_FLYING, false);
                    setNoGravity(false);
                    setDeltaMovement(0.0D, -0.15D, 0.0D);
                } else {
                    entityData.set(DATA_FLYING, true);
                    setNoGravity(true);
                    Vec3 step = delta.normalize().scale(Math.min(0.75D, Math.max(0.32D, d * 0.16D)));
                    setDeltaMovement(step.x, step.y, step.z);
                    float yaw = (float) (Mth.atan2(delta.z, delta.x) * (180.0D / Math.PI)) - 90.0F;
                    setYRot(yaw);
                    yBodyRot = yaw;
                    yHeadRot = yaw;
                }
            } else if (followMode && ownerUuid != null) {
                Player owner = sl.getPlayerByUUID(ownerUuid);
                if (owner != null && owner.isAlive() && owner.level() == sl) {
                    double dSq = distanceToSqr(owner);
                    if (dSq > 100.0D && dSq < 4096.0D) {
                        fluteRecallTarget = owner.position().add(0.0D, 0.6D, 0.0D);
                        fluteRecallTicks = 60;
                        entityData.set(DATA_FLYING, true);
                        setNoGravity(true);
                    } else if (dSq > 36.0D && !isFlying()) {
                        getNavigation().moveTo(owner, 1.15D);
                    }
                }
            }
        }

        int act = getActionState();
        int ticks = getActionTicks() + 1;
        entityData.set(DATA_ACTION_TICKS, ticks);

        if (act == ACTION_TAKEOFF && ticks >= 22) {
            setAction(ACTION_NONE);
        } else if (act == ACTION_SPIT && ticks >= 12) {
            setAction(ACTION_NONE);
        } else if (act == ACTION_COMBO && ticks >= 10) {
            setAction(ACTION_NONE);
        } else if (act == ACTION_DASH) {
            if (getControllingPassenger() instanceof Player rp) {
                AABB box = getBoundingBox().inflate(2.2D, 1.4D, 2.2D);
                for (LivingEntity e : sl.getEntitiesOfClass(LivingEntity.class, box,
                        t -> t != this && t != rp && t.isAlive())) {
                    StavesElementBridge.dealElementalDamage(e, rp, "PYRO", 13.5F, 1.0F, false, 0.25F);
                    e.igniteForSeconds(4.0F);
                    e.push(dashDir.x * 0.95D, 0.35D, dashDir.z * 0.95D);
                }
                sl.sendParticles(ParticleTypes.FLAME, getX(), getY() + 1.0D, getZ(), 10, 0.9D, 0.5D, 0.9D, 0.08D);
            }
            if (ticks >= 9) {
                setAction(ACTION_NONE);
                setDeltaMovement(getDeltaMovement().scale(0.45D));
            }
        } else if (act == ACTION_DIVE) {
            if (getControllingPassenger() instanceof Player rp) {
                setPhlogiston(getPhlogiston() - 1.6F);
                sl.sendParticles(ParticleTypes.FLAME, getX(), getY() + 1.0D, getZ(), 14, 1.0D, 0.6D, 1.0D, 0.12D);
                sl.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, getX(), getY() + 1.0D, getZ(), 4, 0.7D, 0.4D, 0.7D, 0.06D);
                AABB hitBox = getBoundingBox().inflate(2.4D, 1.6D, 2.4D);
                boolean hitEnemy = false;
                for (LivingEntity e : sl.getEntitiesOfClass(LivingEntity.class, hitBox,
                        t -> t != this && t != rp && t.isAlive())) {
                    hitEnemy = true;
                    break;
                }
                if (hitEnemy) {
                    detonateGoldenDive(rp);
                } else if (!inputAimDive || getPhlogiston() <= 1.0F || ticks >= 120) {
                    inputAimDive = false;
                    diveCooldown = 18;
                    setAction(ACTION_NONE);
                    setDeltaMovement(getDeltaMovement().scale(0.45D));
                }
            } else {
                inputAimDive = false;
                setAction(ACTION_NONE);
            }
        } else if (act == ACTION_BREATH) {
            LivingEntity rider = getControllingPassenger();
            if (!inputBreath || !(rider instanceof Player player) || getPhlogiston() < 3.0F) {
                setAction(ACTION_NONE);
            } else {
                setPhlogiston(getPhlogiston() - 2.75F); // ~55/s
                float heatFactor = Mth.clamp(ticks / 80.0F, 0.0F, 1.0F);
                if (ticks % 10 == 0) {
                    sl.playSound(null, getX(), getY() + 1.6D, getZ(),
                            SoundEvents.BLAZE_SHOOT, SoundSource.NEUTRAL,
                            0.65F + heatFactor * 0.45F, 0.72F + heatFactor * 0.68F);
                }
                if (ticks % 5 == 0) {
                    float breathDmg = 5.0F + heatFactor * 10.0F; // ramps 5.0 -> 15.0 Pyro dmg over 4s (3x!)
                    float burnSec = 3.0F + heatFactor * 3.0F;
                    double maxReach = 17.0D + heatFactor * 4.0D;
                    double hitR = 1.6D + heatFactor * 0.5D;
                    Vec3 look = player.getLookAngle().normalize();
                    Vec3 mouth = position().add(0.0D, 1.75D, 0.0D).add(look.scale(1.6D));
                    for (double d = 1.5D; d <= maxReach; d += 2.0D) {
                        Vec3 pt = mouth.add(look.scale(d));
                        AABB box = new AABB(pt.x - hitR, pt.y - hitR, pt.z - hitR, pt.x + hitR, pt.y + hitR, pt.z + hitR);
                        List<LivingEntity> hits = sl.getEntitiesOfClass(LivingEntity.class, box,
                                e -> e != this && e != player && e.isAlive());
                        for (LivingEntity e : hits) {
                            StavesElementBridge.dealElementalDamage(e, player, "PYRO", breathDmg, 0.5F + heatFactor * 0.5F, false, 0.20F);
                            e.igniteForSeconds(burnSec);
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


    public boolean isOwnedBy(Player player) {
        return ownerUuid == null || ownerUuid.equals(player.getUUID());
    }

    public static boolean isFireHealItem(ItemStack stack) {
        return stack.is(Items.BLAZE_ROD) || stack.is(Items.MAGMA_CREAM) || stack.is(Items.FIRE_CHARGE);
    }

    public boolean toggleFollowMode() {
        followMode = !followMode;
        if (!followMode) {
            fluteRecallTarget = null;
            fluteRecallTicks = 0;
        }
        return followMode;
    }

    public void callToPlayer(ServerLevel sl, Player player) {
        if (isVehicle()) return;
        fluteRecallTarget = player.position().add(0.0D, 0.5D, 0.0D);
        fluteRecallTicks = 120;
        entityData.set(DATA_FLYING, true);
        setNoGravity(true);
        sl.playSound(null, getX(), getY() + 1.2D, getZ(), SoundEvents.ENDER_DRAGON_FLAP, SoundSource.NEUTRAL, 1.1F, 1.15F);
    }

    public void commandFireballAt(ServerLevel sl, LivingEntity target) {
        Vec3 mouth = position().add(0.0D, 1.75D, 0.0D);
        Vec3 dir = target.position().add(0.0D, target.getBbHeight() * 0.5D, 0.0D).subtract(mouth).normalize();
        float yaw = (float) (Mth.atan2(dir.z, dir.x) * (180.0D / Math.PI)) - 90.0F;
        setYRot(yaw);
        yBodyRot = yaw;
        yHeadRot = yaw;
        GoldflameFireballEntity.spawn(sl, this, mouth, dir, 1.60F, 14.0F, 2.5F, true);
        sl.playSound(null, mouth.x, mouth.y, mouth.z, SoundEvents.BLAZE_SHOOT, SoundSource.PLAYERS, 1.3F, 0.95F);
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
        setSaddled(true);
        if (tag.contains("Phlogiston")) setPhlogiston(tag.getFloat("Phlogiston"));
    }
}

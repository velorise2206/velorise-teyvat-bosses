package com.velorise.teyvatbosses.entity;

import com.velorise.teyvatbosses.compat.StavesElementBridge;
import com.velorise.teyvatbosses.item.RuinSubPartItem;
import com.velorise.teyvatbosses.registry.TeyvatBossesItems;
import com.velorise.teyvatbosses.registry.TeyvatBossesSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
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
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LightBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Pilotable 6-DoF Underwater Submersible (Fontaine Clockwork Dreadnought with curved glass canopy).
 *
 * <p>Hydrodynamic physics inspired by Alex's Caves Submarine, Better Diving (Seamoth), and Simple Submarines:
 * <ul>
 *   <li>Client-authoritative {@link #travel(Vec3)} synced smoothly via {@code ServerboundMoveVehiclePacket}.</li>
 *   <li>True 3D look-guided pitch &amp; yaw propulsion when moving (look down + W dives smoothly; look up + W climbs).</li>
 *   <li>Comfortable free-look deadzone (&plusmn;32&deg;) when hovering so glancing around does not spin the hull.</li>
 *   <li>Independent ballast vertical control (Space / Ctrl) with Archimedes waterline buoyancy clamping.</li>
 *   <li>Hydrodynamic momentum, coasting water drag, banking roll, and live rudder/diving-fin animations.</li>
 * </ul>
 */
public class RuinSubmersibleEntity extends Mob {

    public static final int PART_HULL        = 1;
    public static final int PART_COCKPIT     = 1 << 1;
    public static final int PART_PROPELLER   = 1 << 2;
    public static final int PART_TORPEDO_POD = 1 << 3;
    public static final int FULL_PARTS       = PART_HULL | PART_COCKPIT | PART_PROPELLER | PART_TORPEDO_POD;

    public static final float MAX_ENERGY = 900.0F;
    public static final int MAX_TORPEDOES = 6;
    public static final int SONAR_COOLDOWN_TICKS = 28;
    public static final int DASH_COOLDOWN_TICKS = 36;
    public static final int TORPEDO_COOLDOWN_TICKS = 6;
    public static final int TORPEDO_REGEN_TICKS = 100;

    public static final int ACTION_NONE      = 0;
    public static final int ACTION_SONAR     = 1;
    public static final int ACTION_TORPEDO   = 2;
    public static final int ACTION_DASH      = 3;
    public static final int ACTION_HEADLIGHT = 4;

    private static final EntityDataAccessor<Integer> DATA_PARTS_MASK =
            SynchedEntityData.defineId(RuinSubmersibleEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DATA_ENERGY =
            SynchedEntityData.defineId(RuinSubmersibleEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> DATA_CAPACITY_LEVEL =
            SynchedEntityData.defineId(RuinSubmersibleEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_CONSERVATION_LEVEL =
            SynchedEntityData.defineId(RuinSubmersibleEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_TORPEDO_AMMO =
            SynchedEntityData.defineId(RuinSubmersibleEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_HEADLIGHT =
            SynchedEntityData.defineId(RuinSubmersibleEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> DATA_HULL_PITCH =
            SynchedEntityData.defineId(RuinSubmersibleEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_HULL_ROLL =
            SynchedEntityData.defineId(RuinSubmersibleEntity.class, EntityDataSerializers.FLOAT);

    // Client-side immediate pilot inputs & smooth interpolated kinematics
    private float clientFwd = 0.0F;
    private float clientStrafe = 0.0F;
    private int clientVert = 0;
    private int clientDashCooldown = 0;
    private int clientSonarCooldown = 0;
    private int clientTorpedoCooldown = 0;

    private float yawRate = 0.0F;
    private float localPitchDeg = 0.0F;
    private float prevLocalPitchDeg = 0.0F;
    private float localRollDeg = 0.0F;
    private float prevLocalRollDeg = 0.0F;
    private float localRudderDeg = 0.0F;
    private float prevLocalRudderDeg = 0.0F;
    private float localFinPitchDeg = 0.0F;
    private float prevLocalFinPitchDeg = 0.0F;
    private float propSpinAngle = 0.0F;
    private float prevPropSpinAngle = 0.0F;
    private float syncedPitchPrev = 0.0F;
    private float syncedRollPrev = 0.0F;

    // Server-side state & cooldowns
    private int sonarCooldown = 0;
    private int torpedoCooldown = 0;
    private int dashCooldown = 0;
    private int torpedoRegenTicks = 0;
    private double prevServerX = 0.0D;
    private double prevServerY = 0.0D;
    private double prevServerZ = 0.0D;
    private Player lastPilot = null;
    private BlockPos dynamicLightPos = null;

    public RuinSubmersibleEntity(EntityType<? extends Mob> type, Level level) {
        super(type, level);
        this.setPersistenceRequired();
        this.setNoGravity(true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 320.0D)
                .add(Attributes.ARMOR, 12.0D)
                .add(Attributes.ARMOR_TOUGHNESS, 6.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.40D)
                .add(Attributes.STEP_HEIGHT, 1.2D);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_PARTS_MASK, FULL_PARTS);
        builder.define(DATA_ENERGY, 0.0F);
        builder.define(DATA_CAPACITY_LEVEL, 0);
        builder.define(DATA_CONSERVATION_LEVEL, 0);
        builder.define(DATA_TORPEDO_AMMO, MAX_TORPEDOES);
        builder.define(DATA_HEADLIGHT, true);
        builder.define(DATA_HULL_PITCH, 0.0F);
        builder.define(DATA_HULL_ROLL, 0.0F);
    }

    public void initAssemblyChassis() {
        entityData.set(DATA_PARTS_MASK, PART_HULL);
        entityData.set(DATA_ENERGY, 0.0F);
        entityData.set(DATA_TORPEDO_AMMO, MAX_TORPEDOES);
        updateAssemblyLabel();
    }

    public void setClientPilotInputs(float fwd, float strafe, int vert) {
        this.clientFwd = Mth.clamp(fwd, -1.0F, 1.0F);
        this.clientStrafe = Mth.clamp(strafe, -1.0F, 1.0F);
        this.clientVert = Mth.clamp(vert, -1, 1);
    }

    public boolean triggerClientDash(Vec3 aimDir) {
        if (!level().isClientSide() || clientDashCooldown > 0 || getEnergy() < 14.0F || !isInOrOnWater()) {
            return false;
        }
        clientDashCooldown = DASH_COOLDOWN_TICKS;
        Vec3 dash = aimDir.lengthSqr() > 1e-4D ? aimDir.normalize() : Vec3.directionFromRotation(localPitchDeg, getYRot());
        // Clamp upward dash near waterline so we never launch into the sky
        double surfaceY = getWaterSurfaceY();
        if (dash.y > 0.0D && !Double.isNaN(surfaceY) && getY() >= surfaceY - 2.2D) {
            dash = new Vec3(dash.x, Math.min(dash.y, 0.05D), dash.z).normalize();
        }
        setDeltaMovement(getDeltaMovement().scale(0.45D).add(dash.scale(1.45D)));
        return true;
    }

    public boolean canClientFireTorpedo() {
        return clientTorpedoCooldown <= 0 && getTorpedoAmmo() > 0 && getEnergy() >= 5.0F;
    }

    public void markClientTorpedoFired() {
        clientTorpedoCooldown = TORPEDO_COOLDOWN_TICKS;
    }

    public boolean canClientTriggerSonar() {
        return clientSonarCooldown <= 0 && getEnergy() >= 8.0F;
    }

    public void markClientSonarTriggered() {
        clientSonarCooldown = SONAR_COOLDOWN_TICKS;
    }

    public int getClientDashCooldown() { return clientDashCooldown; }
    public int getClientSonarCooldown() { return clientSonarCooldown; }
    public int getPartsMask() { return entityData.get(DATA_PARTS_MASK); }
    public boolean isFullyAssembled() { return (getPartsMask() & FULL_PARTS) == FULL_PARTS; }
    public boolean hasPart(int bit) { return (getPartsMask() & bit) != 0; }
    public float getEnergy() { return entityData.get(DATA_ENERGY); }
    public int getCapacityLevel() { return entityData.get(DATA_CAPACITY_LEVEL); }
    public int getConservationLevel() { return entityData.get(DATA_CONSERVATION_LEVEL); }
    public float getMaxEnergy() { return DeviceEnergyHelper.maxEnergyForLevel(getCapacityLevel()); }

    public void setCoreEnchants(int capacityLevel, int conservationLevel) {
        entityData.set(DATA_CAPACITY_LEVEL, Mth.clamp(capacityLevel, 0, 5));
        entityData.set(DATA_CONSERVATION_LEVEL, Mth.clamp(conservationLevel, 0, 5));
    }
    public int getTorpedoAmmo() { return entityData.get(DATA_TORPEDO_AMMO); }
    public boolean isHeadlightOn() { return entityData.get(DATA_HEADLIGHT); }

    public float getHullPitchDeg() {
        return isControlledByLocalInstance() ? localPitchDeg : entityData.get(DATA_HULL_PITCH);
    }

    public float getHullPitchDeg(float partialTick) {
        if (isControlledByLocalInstance()) {
            return Mth.lerp(partialTick, prevLocalPitchDeg, localPitchDeg);
        }
        return Mth.lerp(partialTick, syncedPitchPrev, entityData.get(DATA_HULL_PITCH));
    }

    public float getHullRollDeg() {
        return isControlledByLocalInstance() ? localRollDeg : entityData.get(DATA_HULL_ROLL);
    }

    public float getHullRollDeg(float partialTick) {
        if (isControlledByLocalInstance()) {
            return Mth.lerp(partialTick, prevLocalRollDeg, localRollDeg);
        }
        return Mth.lerp(partialTick, syncedRollPrev, entityData.get(DATA_HULL_ROLL));
    }

    public float getRudderDeg(float partialTick) {
        return Mth.lerp(partialTick, prevLocalRudderDeg, localRudderDeg);
    }

    public float getFinPitchDeg(float partialTick) {
        return Mth.lerp(partialTick, prevLocalFinPitchDeg, localFinPitchDeg);
    }

    public float getPropSpinAngle() { return propSpinAngle; }

    public float getPropSpinAngle(float partialTick) {
        return Mth.lerp(partialTick, prevPropSpinAngle, propSpinAngle);
    }

    private void addEnergy(float delta) {
        float applied = (delta < 0.0F) ? -DeviceEnergyHelper.scaleCost(-delta, getConservationLevel()) : delta;
        entityData.set(DATA_ENERGY, Mth.clamp(getEnergy() + applied, 0.0F, getMaxEnergy()));
    }

    private void updateAssemblyLabel() {
        int c = Integer.bitCount(getPartsMask() & FULL_PARTS);
        if (c < 4) {
            setCustomName(Component.literal(c + " / 4"));
            setCustomNameVisible(true);
        } else {
            setCustomName(null);
            setCustomNameVisible(false);
        }
    }

    public boolean isInOrOnWater() {
        return isInWater() || isUnderWater()
                || level().getFluidState(blockPosition()).is(FluidTags.WATER)
                || level().getFluidState(blockPosition().below()).is(FluidTags.WATER);
    }

    /**
     * Finds the exact Y coordinate of the water surface above or around the submarine,
     * or {@code Double.NaN} if deeply submerged (> 4 blocks below surface) or on dry land.
     */
    public double getWaterSurfaceY() {
        BlockPos base = blockPosition();
        for (int dy = 0; dy <= 4; dy++) {
            BlockPos check = base.above(dy);
            FluidState fs = level().getFluidState(check);
            FluidState above = level().getFluidState(check.above());
            if (fs.is(FluidTags.WATER) && !above.is(FluidTags.WATER)) {
                return check.getY() + fs.getHeight(level(), check);
            }
        }
        return Double.NaN;
    }

    @Override protected void registerGoals() {}
    @Override public boolean removeWhenFarAway(double dist) { return false; }
    @Override public boolean isPushable() { return false; }
    @Override public boolean isPushedByFluid() { return false; }
    @Override public void push(Entity e) {}
    @Override protected void doPush(Entity e) {}
    @Override public void push(double x, double y, double z) {}
    @Override public void knockback(double s, double x, double z) {}
    @Override protected int decreaseAirSupply(int currentAir) { return currentAir; }

    @Override
    public LivingEntity getControllingPassenger() {
        return getFirstPassenger() instanceof Player p ? p : null;
    }

    @Override
    protected void positionRider(Entity passenger, MoveFunction moveFunc) {
        if (!hasPassenger(passenger)) return;
        float yawRad = yBodyRot * Mth.DEG_TO_RAD;
        float pitchRad = getHullPitchDeg() * Mth.DEG_TO_RAD;
        double cosP = Mth.cos(pitchRad);
        double sinP = Mth.sin(pitchRad);
        // Rotate seat offset cleanly with hull pitch so pilot stays centered inside the curved glass canopy
        double fwdDist = 1.90D;
        double fwdX = -Mth.sin(yawRad) * cosP * fwdDist;
        double fwdZ = Mth.cos(yawRad) * cosP * fwdDist;
        double pitchLiftY = -sinP * fwdDist * 0.72D;
        moveFunc.accept(passenger, getX() + fwdX, getY() + 1.05D + pitchLiftY, getZ() + fwdZ);
    }

    @Override
    public Vec3 getDismountLocationForPassenger(LivingEntity passenger) {
        return new Vec3(getX(), getY() + getBbHeight() + 0.55D, getZ());
    }

    /**
     * Smooth client-authoritative 6-DoF hydrodynamic submarine propulsion.
     * Combines Better Diving / Seamoth 3D look-guided diving with Alex's Caves free-look &amp; waterline buoyancy.
     */
    @Override
    public void travel(Vec3 travelVector) {
        if (!isAlive()) return;
        boolean inWaterNow = isInOrOnWater();
        LivingEntity pilot = getControllingPassenger();

        if (pilot instanceof Player p && isFullyAssembled() && isControlledByLocalInstance()) {
            prevLocalPitchDeg = localPitchDeg;
            prevLocalRollDeg = localRollDeg;
            prevLocalRudderDeg = localRudderDeg;
            prevLocalFinPitchDeg = localFinPitchDeg;

            if (getEnergy() > 0.5F && inWaterNow) {
                float fwdIn = Math.abs(clientFwd) > 0.01F ? clientFwd : p.zza;
                float strafeIn = Math.abs(clientStrafe) > 0.01F ? clientStrafe : p.xxa;
                boolean thrusting = Math.abs(fwdIn) > 0.01F || Math.abs(strafeIn) > 0.01F;

                // 1. Smooth hydrodynamic yaw steering with comfortable free-look deadzone when idling
                float deltaYaw = Mth.wrapDegrees(p.getYRot() - getYRot());
                float targetYawRate;
                if (thrusting) {
                    targetYawRate = Mth.clamp(deltaYaw * 0.24F, -6.5F, 6.5F);
                } else {
                    float deadzone = 32.0F;
                    if (Math.abs(deltaYaw) > deadzone) {
                        float excess = (Math.abs(deltaYaw) - deadzone) * Math.signum(deltaYaw);
                        targetYawRate = Mth.clamp(excess * 0.16F, -4.2F, 4.2F);
                    } else {
                        targetYawRate = 0.0F;
                    }
                }
                yawRate = Mth.lerp(0.26F, yawRate, targetYawRate);
                float nextYaw = getYRot() + yawRate;
                setYRot(nextYaw);
                // Do NOT overwrite yRotO / yBodyRotO so Minecraft interpolates rotation smoothly every frame
                this.yBodyRot = nextYaw;
                this.yHeadRot = nextYaw;

                // 2. True 3D look-guided pitch when moving forward/reverse; auto-level neutral trim when hovering
                float targetPitch;
                if (Math.abs(fwdIn) > 0.01F) {
                    float lookPitch = Mth.clamp(p.getXRot(), -42.0F, 42.0F);
                    targetPitch = fwdIn > 0.0F ? lookPitch : Mth.clamp(lookPitch * 0.45F, -18.0F, 18.0F);
                    targetPitch = Mth.clamp(targetPitch - clientVert * 10.0F, -44.0F, 44.0F);
                } else {
                    targetPitch = -clientVert * 14.0F;
                }
                localPitchDeg = Mth.lerp(0.14F, localPitchDeg, targetPitch);

                // 3. Compute 3D hydrodynamic thrust vector
                float yawRad = nextYaw * Mth.DEG_TO_RAD;
                float pitchRad = localPitchDeg * Mth.DEG_TO_RAD;
                double cosP = Mth.cos(pitchRad);
                Vec3 fwd3D = new Vec3(-Mth.sin(yawRad) * cosP, -Mth.sin(pitchRad), Mth.cos(yawRad) * cosP);
                Vec3 left = new Vec3(Mth.cos(yawRad), 0.0D, Mth.sin(yawRad));

                double forwardSpeed = fwdIn >= 0.0F ? 0.78D : 0.44D;
                double strafeSpeed = 0.36D;
                double ballastSpeed = 0.40D;

                Vec3 wish = fwd3D.scale(fwdIn * forwardSpeed)
                        .add(left.scale(strafeIn * strafeSpeed))
                        .add(0.0D, clientVert * ballastSpeed, 0.0D);

                // 4. Archimedes waterline clamping (keeps submarine at natural periscope waterline without popping out)
                double surfaceY = getWaterSurfaceY();
                if (!Double.isNaN(surfaceY)) {
                    double maxHullY = surfaceY - 1.55D;
                    if (getY() > maxHullY && wish.y > 0.0D) {
                        wish = new Vec3(wish.x, 0.0D, wish.z);
                        // Gentle settling back to waterline
                        if (getY() > maxHullY + 0.15D) {
                            wish = wish.add(0.0D, -0.06D, 0.0D);
                        }
                    } else if (getY() > maxHullY - 0.6D && wish.y > 0.0D) {
                        double factor = Mth.clamp((maxHullY - getY()) / 0.6D, 0.0D, 1.0D);
                        wish = new Vec3(wish.x, wish.y * factor, wish.z);
                    }
                    // Auto-level nose near surface so cockpit doesn't pitch into the air
                    if (getY() >= maxHullY - 0.25D && localPitchDeg < 0.0F) {
                        localPitchDeg = Mth.lerp(0.25F, localPitchDeg, 0.0F);
                    }
                }

                // 5. Hydrodynamic acceleration when thrusting vs smooth gliding drag when coasting
                Vec3 curVel = getDeltaMovement();
                Vec3 nextVel;
                if (thrusting || clientVert != 0) {
                    nextVel = curVel.lerp(wish, 0.15D);
                } else {
                    // Realistic underwater coasting + subtle neutral-buoyancy micro-bob
                    double bob = Mth.sin(tickCount * 0.07F) * 0.004D;
                    nextVel = new Vec3(curVel.x * 0.90D, curVel.y * 0.86D + bob, curVel.z * 0.90D);
                    if (nextVel.horizontalDistanceSqr() < 1.0e-5D) {
                        nextVel = new Vec3(0.0D, nextVel.y, 0.0D);
                    }
                }

                // Prevent popping above waterline during coasting/dash
                if (!Double.isNaN(surfaceY) && getY() + nextVel.y > surfaceY - 1.50D && nextVel.y > 0.0D) {
                    nextVel = new Vec3(nextVel.x, Math.max(0.0D, (surfaceY - 1.50D) - getY()) * 0.35D, nextVel.z);
                }

                setDeltaMovement(nextVel);
                move(MoverType.SELF, nextVel);

                // 6. Smooth banking roll, rudder deflection & pectoral diving fins
                float targetRoll = Mth.clamp(-yawRate * 2.8F - strafeIn * 9.0F, -22.0F, 22.0F);
                float targetRudder = Mth.clamp(-yawRate * 6.5F, -32.0F, 32.0F);
                float targetFin = Mth.clamp(localPitchDeg * 0.70F - clientVert * 16.0F, -32.0F, 32.0F);
                localRollDeg = Mth.lerp(0.16F, localRollDeg, targetRoll);
                localRudderDeg = Mth.lerp(0.22F, localRudderDeg, targetRudder);
                localFinPitchDeg = Mth.lerp(0.20F, localFinPitchDeg, targetFin);
            } else {
                // Out of water or out of energy: heavy grounded/sinking feel
                yawRate *= 0.70F;
                double vy = inWaterNow ? -0.02D : Math.max(-0.40D, getDeltaMovement().y - 0.055D);
                Vec3 damp = new Vec3(getDeltaMovement().x * 0.78D, vy, getDeltaMovement().z * 0.78D);
                setDeltaMovement(damp);
                move(MoverType.SELF, damp);
                localPitchDeg = Mth.lerp(0.12F, localPitchDeg, 0.0F);
                localRollDeg = Mth.lerp(0.12F, localRollDeg, 0.0F);
                localRudderDeg = Mth.lerp(0.15F, localRudderDeg, 0.0F);
                localFinPitchDeg = Mth.lerp(0.15F, localFinPitchDeg, 0.0F);
            }
            return;
        }

        // Unpiloted submarine on server: gentle buoyancy/gravity settling
        if (!level().isClientSide() && !isVehicle()) {
            double vy = inWaterNow ? 0.0D : Math.max(-0.35D, getDeltaMovement().y - 0.05D);
            Vec3 damp = new Vec3(getDeltaMovement().x * 0.80D, vy, getDeltaMovement().z * 0.80D);
            setDeltaMovement(damp);
            move(MoverType.SELF, damp);
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypes.DROWN) || source.is(DamageTypes.IN_WALL)
                || source.is(DamageTypes.CRAMMING) || source.is(DamageTypes.FALL)) {
            return false;
        }
        return super.hurt(source, amount);
    }

    @Override
    public void die(DamageSource cause) {
        if (!level().isClientSide() && level() instanceof ServerLevel sl) {
            clearDynamicLight(sl);
            dropWeldedParts();
        }
        super.die(cause);
    }

    private void dropWeldedParts() {
        if (hasPart(PART_HULL) && TeyvatBossesItems.ruinSubHull() != null) spawnAtLocation(TeyvatBossesItems.ruinSubHull());
        if (hasPart(PART_COCKPIT) && TeyvatBossesItems.ruinSubCockpit() != null) spawnAtLocation(TeyvatBossesItems.ruinSubCockpit());
        if (hasPart(PART_PROPELLER) && TeyvatBossesItems.ruinSubPropeller() != null) spawnAtLocation(TeyvatBossesItems.ruinSubPropeller());
        if (hasPart(PART_TORPEDO_POD) && TeyvatBossesItems.ruinSubTorpedoPod() != null) spawnAtLocation(TeyvatBossesItems.ruinSubTorpedoPod());
    }

    private void playSfx(SoundEvent primary, SoundEvent fallback, float vol, float pitch) {
        SoundEvent ev = primary != null ? primary : fallback;
        level().playSound(null, getX(), getY() + 0.9D, getZ(), ev, SoundSource.PLAYERS, vol, pitch);
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level().isClientSide()) return InteractionResult.SUCCESS;
        ServerLevel sl = (ServerLevel) level();

        if (player.isShiftKeyDown() && stack.getItem() instanceof PickaxeItem && !isVehicle()) {
            dropWeldedParts();
            playSfx(TeyvatBossesSounds.subBallastHiss(), SoundEvents.NETHERITE_BLOCK_BREAK, 1.2F, 0.95F);
            sl.sendParticles(ParticleTypes.BUBBLE_POP, getX(), getY() + 1.0D, getZ(), 24, 0.9D, 0.6D, 0.9D, 0.06D);
            clearDynamicLight(sl);
            player.displayClientMessage(Component.translatable("message.velorise_teyvat_bosses.ruin_sub.disassembled"), true);
            discard();
            return InteractionResult.CONSUME;
        }

        if (!isFullyAssembled()) {
            if (stack.getItem() instanceof RuinSubPartItem part) {
                int bit = part.getPartBit();
                if (!hasPart(bit)) {
                    int next = getPartsMask() | bit;
                    entityData.set(DATA_PARTS_MASK, next);
                    if (!player.getAbilities().instabuild) stack.shrink(1);
                    playSfx(TeyvatBossesSounds.mechMetalClank(), SoundEvents.IRON_DOOR_CLOSE, 1.1F, 1.15F);
                    sl.sendParticles(ParticleTypes.BUBBLE_POP, getX(), getY() + 1.0D, getZ(), 18, 0.8D, 0.5D, 0.8D, 0.05D);
                    updateAssemblyLabel();
                    if ((next & FULL_PARTS) == FULL_PARTS) {
                        playSfx(TeyvatBossesSounds.subSonarPing(), SoundEvents.BEACON_ACTIVATE, 1.3F, 1.15F);
                        player.displayClientMessage(Component.translatable("message.velorise_teyvat_bosses.ruin_sub.assembled"), true);
                    } else {
                        player.displayClientMessage(Component.translatable("message.velorise_teyvat_bosses.ruin_sub.progress", Integer.bitCount(next & FULL_PARTS), 4), true);
                    }
                    return InteractionResult.CONSUME;
                }
            }
            return InteractionResult.CONSUME;
        }

        float fuelGain = DeviceEnergyHelper.getRedstoneFuelValue(stack);
        if (fuelGain > 0.0F && getEnergy() < getMaxEnergy() - 0.5F) {
            addEnergy(fuelGain);
            if (!player.getAbilities().instabuild) stack.shrink(1);
            playSfx(TeyvatBossesSounds.subBallastHiss(), SoundEvents.BEACON_POWER_SELECT, 1.0F, 1.25F);
            sl.sendParticles(ParticleTypes.GLOW, getX(), getY() + 1.0D, getZ(), 14, 0.6D, 0.4D, 0.6D, 0.04D);
            return InteractionResult.CONSUME;
        }

        if (stack.is(Items.IRON_INGOT) && getHealth() < getMaxHealth()) {
            heal(40.0F);
            if (!player.getAbilities().instabuild) stack.shrink(1);
            playSfx(TeyvatBossesSounds.mechMetalClank(), SoundEvents.IRON_GOLEM_REPAIR, 1.0F, 1.15F);
            return InteractionResult.CONSUME;
        }

        if (!isVehicle() && !player.isSecondaryUseActive()) {
            player.startRiding(this);
            playSfx(TeyvatBossesSounds.mechVisorSeal(), SoundEvents.IRON_TRAPDOOR_CLOSE, 1.0F, 0.90F);
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }

    public void handlePilotControl(ServerLevel sl, Player pilot, float fwdIn, float strafeIn,
                                   int vertInput, int action, Vec3 aimDir) {
        if (getControllingPassenger() != pilot) return;

        if (action == ACTION_HEADLIGHT) {
            boolean nextState = !isHeadlightOn();
            entityData.set(DATA_HEADLIGHT, nextState);
            if (!nextState) {
                pilot.removeEffect(MobEffects.NIGHT_VISION);
                clearDynamicLight(sl);
            } else if (isInOrOnWater()) {
                pilot.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 240, 0, true, false));
            }
            playSfx(TeyvatBossesSounds.subSonarPing(), SoundEvents.LEVER_CLICK, 0.65F, nextState ? 1.55F : 1.15F);
            return;
        }
        if (action == ACTION_DASH && dashCooldown <= 0 && getEnergy() >= 14.0F && isInOrOnWater()) {
            addEnergy(-14.0F);
            dashCooldown = DASH_COOLDOWN_TICKS;
            playSfx(TeyvatBossesSounds.subBallastHiss(), SoundEvents.DOLPHIN_SPLASH, 1.2F, 0.95F);
            float rad = yBodyRot * Mth.DEG_TO_RAD;
            Vec3 fwd = new Vec3(-Mth.sin(rad), 0.0D, Mth.cos(rad));
            Vec3 tail = position().subtract(fwd.scale(3.1D)).add(0.0D, 1.25D, 0.0D);
            sl.sendParticles(ParticleTypes.BUBBLE_POP, tail.x, tail.y, tail.z, 22, 0.65D, 0.55D, 0.65D, 0.10D);
            return;
        }
        if (action == ACTION_SONAR && sonarCooldown <= 0 && getEnergy() >= 8.0F) {
            addEnergy(-8.0F);
            sonarCooldown = SONAR_COOLDOWN_TICKS;
            playSfx(TeyvatBossesSounds.subSonarPing(), SoundEvents.WARDEN_SONIC_BOOM, 1.25F, 1.25F);
            AABB box = getBoundingBox().inflate(24.0D);
            boolean hitAny = false;
            for (LivingEntity e : sl.getEntitiesOfClass(LivingEntity.class, box,
                    le -> le.isAlive() && le instanceof Enemy && le != this)) {
                e.addEffect(new MobEffectInstance(MobEffects.GLOWING, 160, 0));
                if (e.distanceToSqr(this) <= 11.5D * 11.5D) {
                    StavesElementBridge.dealElementalDamage(e, pilot, "HYDRO", 16.0F, 1.0F, false, 0.15F);
                    Vec3 push = e.position().subtract(position()).normalize();
                    e.push(push.x * 1.2D, 0.28D, push.z * 1.2D);
                    hitAny = true;
                }
            }
            if (hitAny && getTorpedoAmmo() < MAX_TORPEDOES) {
                entityData.set(DATA_TORPEDO_AMMO, Math.min(MAX_TORPEDOES, getTorpedoAmmo() + 1));
            }
            // Visible expanding sonar ring particles
            Vec3 center = position().add(0.0D, 1.3D, 0.0D);
            sl.sendParticles(ParticleTypes.SONIC_BOOM, center.x, center.y, center.z, 1, 0, 0, 0, 0);
            for (int i = 0; i < 16; i++) {
                double ang = (Math.PI * 2.0D * i) / 16.0D;
                sl.sendParticles(ParticleTypes.BUBBLE_POP,
                        center.x + Math.cos(ang) * 2.8D, center.y, center.z + Math.sin(ang) * 2.8D,
                        1, Math.cos(ang) * 0.25D, 0.02D, Math.sin(ang) * 0.25D, 0.08D);
            }
            return;
        }
        if (action == ACTION_TORPEDO && torpedoCooldown <= 0 && getTorpedoAmmo() > 0 && getEnergy() >= 5.0F) {
            int ammo = getTorpedoAmmo();
            entityData.set(DATA_TORPEDO_AMMO, ammo - 1);
            addEnergy(-5.0F);
            torpedoCooldown = TORPEDO_COOLDOWN_TICKS;

            float rad = yBodyRot * Mth.DEG_TO_RAD;
            Vec3 left = new Vec3(Mth.cos(rad), 0.0D, Mth.sin(rad));
            Vec3 fwd = aimDir.lengthSqr() > 1e-4D ? aimDir.normalize() : Vec3.directionFromRotation(getHullPitchDeg(), yBodyRot);
            double side = (ammo % 2 == 0) ? 1.45D : -1.45D;
            Vec3 origin = position().add(left.scale(side)).add(fwd.scale(2.1D)).add(0.0D, 1.15D, 0.0D);

            LivingEntity lock = findConeTarget(sl, origin, fwd, 34.0D);
            // Pass pilot as owner so player receives XP/kill credit (friendlyToPlayers protects pilot & sub)
            if (lock != null) {
                RuinTorpedoProjectile.launchHoming(sl, pilot, lock, origin, fwd.scale(0.92D), 20.0F, true);
            } else {
                RuinTorpedoProjectile.launchFreeAim(sl, pilot, origin, fwd, 20.0F, true);
            }
            sl.sendParticles(ParticleTypes.BUBBLE_POP, origin.x, origin.y, origin.z, 8, 0.25D, 0.25D, 0.25D, 0.06D);
            playSfx(TeyvatBossesSounds.subTorpedoLaunch(), SoundEvents.CROSSBOW_SHOOT, 1.1F, 0.98F + (MAX_TORPEDOES - ammo) * 0.04F);
        }
    }

    public LivingEntity findConeTarget(Level level, Vec3 origin, Vec3 dir, double range) {
        Vec3 normDir = dir.normalize();
        Vec3 endPos = origin.add(normDir.scale(range));
        HitResult blockHit = level.clip(new ClipContext(origin, endPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        double bestDistSq = blockHit.getType() != HitResult.Type.MISS
                ? blockHit.getLocation().distanceToSqr(origin)
                : range * range;

        AABB searchBox = new AABB(origin, endPos).inflate(1.5D);
        LivingEntity best = null;
        LivingEntity pilot = getControllingPassenger();
        for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, searchBox,
                le -> le.isAlive() && le != this && le != pilot)) {
            // Strict crosshair raycast only — no wide-cone auto-aim magnet
            var hit = e.getBoundingBox().inflate(0.25D).clip(origin, endPos);
            if (hit.isPresent()) {
                double dSq = hit.get().distanceToSqr(origin);
                if (dSq < bestDistSq) {
                    bestDistSq = dSq;
                    best = e;
                }
            }
        }
        return best;
    }

    @Override
    public void tick() {
        prevPropSpinAngle = propSpinAngle;
        syncedPitchPrev = entityData.get(DATA_HULL_PITCH);
        syncedRollPrev = entityData.get(DATA_HULL_ROLL);

        super.tick();
        setAirSupply(getMaxAirSupply());
        double speed = getDeltaMovement().length();
        propSpinAngle += (float) (0.12D + speed * 1.75D);

        if (level().isClientSide()) {
            if (clientDashCooldown > 0) clientDashCooldown--;
            if (clientSonarCooldown > 0) clientSonarCooldown--;
            if (clientTorpedoCooldown > 0) clientTorpedoCooldown--;
            return;
        }
        ServerLevel sl = (ServerLevel) level();

        if (sonarCooldown > 0) sonarCooldown--;
        if (torpedoCooldown > 0) torpedoCooldown--;
        if (dashCooldown > 0) dashCooldown--;

        if (getTorpedoAmmo() < MAX_TORPEDOES) {
            torpedoRegenTicks++;
            if (torpedoRegenTicks >= TORPEDO_REGEN_TICKS) {
                torpedoRegenTicks = 0;
                entityData.set(DATA_TORPEDO_AMMO, getTorpedoAmmo() + 1);
            }
        } else {
            torpedoRegenTicks = 0;
        }

        boolean inWaterNow = isInOrOnWater();
        LivingEntity pilot = getControllingPassenger();
        Player currentPilot = pilot instanceof Player p ? p : null;

        // Cleanly remove submarine-granted Night Vision when pilot dismounts
        if (lastPilot != null && lastPilot != currentPilot && lastPilot.isAlive()) {
            lastPilot.removeEffect(MobEffects.NIGHT_VISION);
        }
        lastPilot = currentPilot;

        if (currentPilot != null && isFullyAssembled()) {
            currentPilot.setAirSupply(currentPilot.getMaxAirSupply());
            if (inWaterNow) {
                // WATER_BREATHING instead of CONDUIT_POWER so Light[F] genuinely controls underwater visibility!
                currentPilot.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 60, 0, true, false));
                if (isHeadlightOn() && getEnergy() > 0.5F) {
                    currentPilot.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 240, 0, true, false));
                    updateDynamicLight(sl);
                } else {
                    clearDynamicLight(sl);
                }
            } else {
                clearDynamicLight(sl);
            }

            double dx = getX() - prevServerX;
            double dy = getY() - prevServerY;
            double dz = getZ() - prevServerZ;
            double movedSq = dx * dx + dy * dy + dz * dz;
            if (movedSq > 0.002D) {
                if (tickCount % 25 == 0) addEnergy(-0.4F);
                float rad = yBodyRot * Mth.DEG_TO_RAD;
                Vec3 fwd = new Vec3(-Mth.sin(rad), 0.0D, Mth.cos(rad));
                if (tickCount % 3 == 0) {
                    Vec3 tail = position().subtract(fwd.scale(3.3D)).add(0.0D, 1.30D, 0.0D);
                    sl.sendParticles(ParticleTypes.BUBBLE, tail.x, tail.y, tail.z, 0, -fwd.x * 0.18D, 0.02D, -fwd.z * 0.18D, 1.0D);
                }
                if (tickCount % 28 == 0) {
                    playSfx(TeyvatBossesSounds.subPropellerHum(), SoundEvents.BUBBLE_COLUMN_UPWARDS_AMBIENT, 0.35F, 1.05F);
                }
            }
            float syncedPitch = Mth.clamp((float) (-dy * 68.0D), -42.0F, 42.0F);
            entityData.set(DATA_HULL_PITCH, Mth.lerp(0.22F, entityData.get(DATA_HULL_PITCH), syncedPitch));
        } else {
            clearDynamicLight(sl);
            entityData.set(DATA_HULL_PITCH, Mth.lerp(0.12F, entityData.get(DATA_HULL_PITCH), 0.0F));
            entityData.set(DATA_HULL_ROLL, Mth.lerp(0.12F, entityData.get(DATA_HULL_ROLL), 0.0F));
        }
        prevServerX = getX();
        prevServerY = getY();
        prevServerZ = getZ();
    }

    private void updateDynamicLight(ServerLevel sl) {
        if ((tickCount & 3) != 0) return;
        float yawRad = yBodyRot * Mth.DEG_TO_RAD;
        float pitchRad = getHullPitchDeg() * Mth.DEG_TO_RAD;
        double cosP = Mth.cos(pitchRad);
        Vec3 fwd = new Vec3(-Mth.sin(yawRad) * cosP, -Mth.sin(pitchRad), Mth.cos(yawRad) * cosP);
        BlockPos target = BlockPos.containing(position().add(fwd.scale(4.5D)).add(0.0D, 1.3D, 0.0D));
        if (target.equals(dynamicLightPos)) return;

        clearDynamicLight(sl);
        BlockState existing = sl.getBlockState(target);
        if (existing.is(Blocks.WATER) && sl.getFluidState(target).isSource()) {
            BlockState lightState = Blocks.LIGHT.defaultBlockState()
                    .setValue(LightBlock.LEVEL, 15)
                    .setValue(LightBlock.WATERLOGGED, true);
            sl.setBlock(target, lightState, 3);
            dynamicLightPos = target;
        }
    }

    private void clearDynamicLight(ServerLevel sl) {
        if (dynamicLightPos != null) {
            BlockState st = sl.getBlockState(dynamicLightPos);
            if (st.is(Blocks.LIGHT)) {
                sl.setBlock(dynamicLightPos,
                        st.getValue(LightBlock.WATERLOGGED) ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState(),
                        3);
            }
            dynamicLightPos = null;
        }
    }

    @Override
    public void remove(RemovalReason reason) {
        if (!level().isClientSide() && level() instanceof ServerLevel sl) {
            clearDynamicLight(sl);
        }
        super.remove(reason);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("PartsMask", getPartsMask());
        tag.putFloat("Energy", getEnergy());
        tag.putInt("CoreCapacity", getCapacityLevel());
        tag.putInt("EnergyConservation", getConservationLevel());
        tag.putInt("TorpedoAmmo", getTorpedoAmmo());
        tag.putBoolean("Headlight", isHeadlightOn());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("PartsMask")) entityData.set(DATA_PARTS_MASK, tag.getInt("PartsMask"));
        if (tag.contains("CoreCapacity")) entityData.set(DATA_CAPACITY_LEVEL, Mth.clamp(tag.getInt("CoreCapacity"), 0, 5));
        if (tag.contains("EnergyConservation")) entityData.set(DATA_CONSERVATION_LEVEL, Mth.clamp(tag.getInt("EnergyConservation"), 0, 5));
        if (tag.contains("Energy")) entityData.set(DATA_ENERGY, Mth.clamp(tag.getFloat("Energy"), 0.0F, getMaxEnergy()));
        if (tag.contains("TorpedoAmmo")) entityData.set(DATA_TORPEDO_AMMO, tag.getInt("TorpedoAmmo"));
        if (tag.contains("Headlight")) entityData.set(DATA_HEADLIGHT, tag.getBoolean("Headlight"));
        updateAssemblyLabel();
    }
}

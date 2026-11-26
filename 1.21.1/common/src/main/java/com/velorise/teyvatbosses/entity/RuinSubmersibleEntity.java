package com.velorise.teyvatbosses.entity;

import com.velorise.teyvatbosses.compat.StavesElementBridge;
import com.velorise.teyvatbosses.item.RuinSubPartItem;
import com.velorise.teyvatbosses.registry.TeyvatBossesItems;
import com.velorise.teyvatbosses.registry.TeyvatBossesSounds;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * Pilotable 6-DoF Underwater Submersible (Fontaine Clockwork Dreadnought with curved glass canopy).
 *
 * <p>Uses proper Minecraft 1.21.1 client-authoritative {@link #travel(Vec3)} vehicle physics
 * (just like Alex's Caves Submarine &amp; vanilla Boat): movement runs smoothly on the controlling client
 * and syncs automatically via {@code ServerboundMoveVehiclePacket}, completely eliminating the
 * frozen/rubber-banding bug caused by moving on the server while {@code LocalPlayer} owns the vehicle.
 */
public class RuinSubmersibleEntity extends Mob {

    public static final int PART_HULL        = 1;
    public static final int PART_COCKPIT     = 1 << 1;
    public static final int PART_PROPELLER   = 1 << 2;
    public static final int PART_TORPEDO_POD = 1 << 3;
    public static final int FULL_PARTS       = PART_HULL | PART_COCKPIT | PART_PROPELLER | PART_TORPEDO_POD;

    public static final float MAX_ENERGY = 1000.0F;
    public static final int MAX_TORPEDOES = 6;

    public static final int ACTION_NONE      = 0;
    public static final int ACTION_SONAR     = 1;
    public static final int ACTION_TORPEDO   = 2;
    public static final int ACTION_DASH      = 3;
    public static final int ACTION_HEADLIGHT = 4;

    private static final EntityDataAccessor<Integer> DATA_PARTS_MASK =
            SynchedEntityData.defineId(RuinSubmersibleEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DATA_ENERGY =
            SynchedEntityData.defineId(RuinSubmersibleEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> DATA_TORPEDO_AMMO =
            SynchedEntityData.defineId(RuinSubmersibleEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_HEADLIGHT =
            SynchedEntityData.defineId(RuinSubmersibleEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> DATA_HULL_PITCH =
            SynchedEntityData.defineId(RuinSubmersibleEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_HULL_ROLL =
            SynchedEntityData.defineId(RuinSubmersibleEntity.class, EntityDataSerializers.FLOAT);

    // Client-side immediate pilot inputs (fed by RuinSubClientController for 0ms lag!)
    private float clientFwd = 0.0F;
    private float clientStrafe = 0.0F;
    private int clientVert = 0;
    private int clientDashCooldown = 0;
    private float localPitchDeg = 0.0F;
    private float localRollDeg = 0.0F;

    // Server-side state & cooldowns
    private int sonarCooldown = 0;
    private int torpedoCooldown = 0;
    private int dashCooldown = 0;
    private int torpedoRegenTicks = 0;
    private float propSpinAngle = 0.0F;
    private double prevServerX = 0.0D;
    private double prevServerY = 0.0D;
    private double prevServerZ = 0.0D;

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
        builder.define(DATA_ENERGY, 600.0F);
        builder.define(DATA_TORPEDO_AMMO, MAX_TORPEDOES);
        builder.define(DATA_HEADLIGHT, true);
        builder.define(DATA_HULL_PITCH, 0.0F);
        builder.define(DATA_HULL_ROLL, 0.0F);
    }

    public void initAssemblyChassis() {
        entityData.set(DATA_PARTS_MASK, PART_HULL);
        entityData.set(DATA_ENERGY, 400.0F);
        entityData.set(DATA_TORPEDO_AMMO, MAX_TORPEDOES);
        updateAssemblyLabel();
    }

    public void setClientPilotInputs(float fwd, float strafe, int vert) {
        this.clientFwd = Mth.clamp(fwd, -1.0F, 1.0F);
        this.clientStrafe = Mth.clamp(strafe, -1.0F, 1.0F);
        this.clientVert = Mth.clamp(vert, -1, 1);
    }

    public void triggerClientDash(Vec3 aimDir) {
        if (!level().isClientSide() || clientDashCooldown > 0 || getEnergy() < 14.0F || !isInOrOnWater()) return;
        clientDashCooldown = 36;
        Vec3 dash = aimDir.lengthSqr() > 1e-4D ? aimDir.normalize() : Vec3.directionFromRotation(getXRot(), getYRot());
        setDeltaMovement(getDeltaMovement().add(dash.scale(1.65D)));
    }

    public int getPartsMask() { return entityData.get(DATA_PARTS_MASK); }
    public boolean isFullyAssembled() { return (getPartsMask() & FULL_PARTS) == FULL_PARTS; }
    public boolean hasPart(int bit) { return (getPartsMask() & bit) != 0; }
    public float getEnergy() { return entityData.get(DATA_ENERGY); }
    public int getTorpedoAmmo() { return entityData.get(DATA_TORPEDO_AMMO); }
    public boolean isHeadlightOn() { return entityData.get(DATA_HEADLIGHT); }
    public float getHullPitchDeg() { return isControlledByLocalInstance() ? localPitchDeg : entityData.get(DATA_HULL_PITCH); }
    public float getHullRollDeg() { return isControlledByLocalInstance() ? localRollDeg : entityData.get(DATA_HULL_ROLL); }
    public float getPropSpinAngle() { return propSpinAngle; }

    private void addEnergy(float delta) {
        entityData.set(DATA_ENERGY, Mth.clamp(getEnergy() + delta, 0.0F, MAX_ENERGY));
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
        float rad = yBodyRot * Mth.DEG_TO_RAD;
        double fwdX = -Mth.sin(rad);
        double fwdZ = Mth.cos(rad);
        // Seat pilot cleanly inside the curved glass nose cockpit so they are clearly visible in 3rd person!
        moveFunc.accept(passenger, getX() + fwdX * 1.95D, getY() + 1.05D, getZ() + fwdZ * 1.95D);
    }

    @Override
    public Vec3 getDismountLocationForPassenger(LivingEntity passenger) {
        return new Vec3(getX(), getY() + getBbHeight() + 0.65D, getZ());
    }

    /**
     * Proper Minecraft 1.21.1 client-authoritative 6-DoF submarine propulsion!
     * Runs on the controlling client (when piloted) so {@code ServerboundMoveVehiclePacket} syncs
     * smooth zero-lag motion to the server without ever snapping back or getting stuck.
     */
    @Override
    public void travel(Vec3 travelVector) {
        if (!isAlive()) return;
        boolean inWaterNow = isInOrOnWater();
        LivingEntity pilot = getControllingPassenger();

        if (pilot instanceof Player p && isFullyAssembled() && isControlledByLocalInstance()) {
            if (getEnergy() > 0.5F && inWaterNow) {
                float targetYaw = p.getYRot();
                float deltaYaw = Mth.wrapDegrees(targetYaw - getYRot());
                float nextYaw = getYRot() + Mth.clamp(deltaYaw, -9.0F, 9.0F);
                setYRot(nextYaw);
                this.yRotO = nextYaw;
                this.yBodyRot = nextYaw;
                this.yHeadRot = nextYaw;

                // Read client inputs (fallback to player.zza / player.xxa for complete reliability)
                float fwdIn = Math.abs(clientFwd) > 0.01F ? clientFwd : p.zza;
                float strafeIn = Math.abs(clientStrafe) > 0.01F ? clientStrafe : p.xxa;

                float rad = nextYaw * Mth.DEG_TO_RAD;
                Vec3 fwd = new Vec3(-Mth.sin(rad), 0.0D, Mth.cos(rad));
                Vec3 right = new Vec3(-Mth.cos(rad), 0.0D, -Mth.sin(rad));

                double maxSpd = 0.72D;
                double vertVel = clientVert * 0.42D;
                // Allow gliding right up to water surface without launching out of the ocean
                if (vertVel > 0.0D && !level().getFluidState(blockPosition().above()).is(FluidTags.WATER)) {
                    vertVel = 0.0D;
                }

                Vec3 wish = fwd.scale(fwdIn * maxSpd)
                        .add(right.scale(-strafeIn * 0.42D))
                        .add(0.0D, vertVel, 0.0D);
                Vec3 nextVel = getDeltaMovement().lerp(wish, 0.26D);
                setDeltaMovement(nextVel);
                move(MoverType.SELF, nextVel);

                float targetPitch = Mth.clamp((float) (-nextVel.y * 65.0D), -30.0F, 30.0F);
                float targetRoll = Mth.clamp(-deltaYaw * 1.8F - strafeIn * 10.0F, -20.0F, 20.0F);
                localPitchDeg = Mth.lerp(0.20F, localPitchDeg, targetPitch);
                localRollDeg = Mth.lerp(0.20F, localRollDeg, targetRoll);
            } else {
                // Out of water or out of energy: gentle sink/stop
                double vy = inWaterNow ? 0.0D : Math.max(-0.35D, getDeltaMovement().y - 0.05D);
                Vec3 damp = new Vec3(getDeltaMovement().x * 0.78D, vy, getDeltaMovement().z * 0.78D);
                setDeltaMovement(damp);
                move(MoverType.SELF, damp);
                localPitchDeg = Mth.lerp(0.15F, localPitchDeg, 0.0F);
                localRollDeg = Mth.lerp(0.15F, localRollDeg, 0.0F);
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
        if (!level().isClientSide()) {
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

        float fuelGain = 0.0F;
        if (stack.is(Items.SEA_LANTERN)) fuelGain = 250.0F;
        else if (stack.is(Items.PRISMARINE_BRICKS) || stack.is(Items.REDSTONE_BLOCK)) fuelGain = 100.0F;
        if (fuelGain > 0.0F && getEnergy() < MAX_ENERGY - 1.0F) {
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

        if (!isVehicle()) {
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
            entityData.set(DATA_HEADLIGHT, !isHeadlightOn());
            playSfx(TeyvatBossesSounds.subSonarPing(), SoundEvents.LEVER_CLICK, 0.65F, 1.55F);
            return;
        }
        if (action == ACTION_DASH && dashCooldown <= 0 && getEnergy() >= 14.0F && isInOrOnWater()) {
            addEnergy(-14.0F);
            dashCooldown = 36;
            playSfx(TeyvatBossesSounds.subBallastHiss(), SoundEvents.DOLPHIN_SPLASH, 1.2F, 0.95F);
            return;
        }
        if (action == ACTION_SONAR && sonarCooldown <= 0 && getEnergy() >= 8.0F) {
            addEnergy(-8.0F);
            sonarCooldown = 26;
            playSfx(TeyvatBossesSounds.subSonarPing(), SoundEvents.WARDEN_SONIC_BOOM, 1.2F, 1.25F);
            AABB box = getBoundingBox().inflate(18.0D);
            boolean hitAny = false;
            for (LivingEntity e : sl.getEntitiesOfClass(LivingEntity.class, box,
                    le -> le.isAlive() && le instanceof Enemy && le != this)) {
                e.addEffect(new MobEffectInstance(MobEffects.GLOWING, 120, 0));
                if (e.distanceToSqr(this) <= 10.0D * 10.0D) {
                    StavesElementBridge.dealElementalDamage(e, pilot, "HYDRO", 14.0F, 1.0F, false, 0.15F);
                    Vec3 push = e.position().subtract(position()).normalize();
                    e.push(push.x * 1.1D, 0.25D, push.z * 1.1D);
                    hitAny = true;
                }
            }
            if (hitAny && getTorpedoAmmo() < MAX_TORPEDOES) {
                entityData.set(DATA_TORPEDO_AMMO, Math.min(MAX_TORPEDOES, getTorpedoAmmo() + 1));
            }
            sl.sendParticles(ParticleTypes.SONIC_BOOM, getX(), getY() + 1.0D, getZ(), 1, 0, 0, 0, 0);
            return;
        }
        if (action == ACTION_TORPEDO && torpedoCooldown <= 0 && getTorpedoAmmo() > 0 && getEnergy() >= 5.0F) {
            int ammo = getTorpedoAmmo();
            entityData.set(DATA_TORPEDO_AMMO, ammo - 1);
            addEnergy(-5.0F);
            torpedoCooldown = 6;

            float rad = yBodyRot * Mth.DEG_TO_RAD;
            Vec3 right = new Vec3(-Mth.cos(rad), 0.0D, -Mth.sin(rad));
            Vec3 fwd = aimDir.lengthSqr() > 1e-4D ? aimDir.normalize() : Vec3.directionFromRotation(0.0F, yBodyRot);
            double side = (ammo % 2 == 0) ? -1.35D : 1.35D;
            Vec3 origin = position().add(right.scale(side)).add(fwd.scale(1.8D)).add(0.0D, 0.95D, 0.0D);

            LivingEntity lock = findConeTarget(sl, origin, fwd, 32.0D);
            if (lock != null) {
                RuinTorpedoProjectile.launchHoming(sl, this, lock, origin, fwd.scale(0.85D), 18.0F, true);
            } else {
                RuinTorpedoProjectile.launchFreeAim(sl, this, origin, fwd, 18.0F, true);
            }
            playSfx(TeyvatBossesSounds.subTorpedoLaunch(), SoundEvents.CROSSBOW_SHOOT, 1.05F, 1.0F + (6 - ammo) * 0.05F);
        }
    }

    private LivingEntity findConeTarget(ServerLevel sl, Vec3 origin, Vec3 dir, double range) {
        LivingEntity best = null;
        double bestScore = 0.88D;
        for (LivingEntity e : sl.getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(range),
                le -> le.isAlive() && le instanceof Enemy && le != this)) {
            Vec3 to = e.position().add(0, e.getBbHeight() * 0.5D, 0).subtract(origin);
            double d = to.length();
            if (d < 1.0D || d > range) continue;
            double dot = to.normalize().dot(dir);
            if (dot > bestScore) { bestScore = dot; best = e; }
        }
        return best;
    }

    @Override
    public void tick() {
        super.tick();
        setAirSupply(getMaxAirSupply());
        double speed = getDeltaMovement().length();
        propSpinAngle += (float) (0.14D + speed * 1.45D);

        if (level().isClientSide()) {
            if (clientDashCooldown > 0) clientDashCooldown--;
            return;
        }
        ServerLevel sl = (ServerLevel) level();

        if (sonarCooldown > 0) sonarCooldown--;
        if (torpedoCooldown > 0) torpedoCooldown--;
        if (dashCooldown > 0) dashCooldown--;

        if (getTorpedoAmmo() < MAX_TORPEDOES) {
            torpedoRegenTicks++;
            if (torpedoRegenTicks >= 120) {
                torpedoRegenTicks = 0;
                entityData.set(DATA_TORPEDO_AMMO, getTorpedoAmmo() + 1);
            }
        } else {
            torpedoRegenTicks = 0;
        }

        boolean inWaterNow = isInOrOnWater();
        LivingEntity pilot = getControllingPassenger();
        if (pilot instanceof Player p && isFullyAssembled()) {
            p.setAirSupply(p.getMaxAirSupply());
            if (inWaterNow) {
                p.addEffect(new MobEffectInstance(MobEffects.CONDUIT_POWER, 60, 0, true, false));
                if (isHeadlightOn()) {
                    p.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 400, 0, true, false));
                }
            }

            // Detect real client-driven displacement on server for energy drain, bubbles, sound & multiplayer pitch sync
            double dx = getX() - prevServerX;
            double dy = getY() - prevServerY;
            double dz = getZ() - prevServerZ;
            double movedSq = dx * dx + dy * dy + dz * dz;
            if (movedSq > 0.002D) {
                if (tickCount % 20 == 0) addEnergy(-0.5F);
                float rad = yBodyRot * Mth.DEG_TO_RAD;
                Vec3 fwd = new Vec3(-Mth.sin(rad), 0.0D, Mth.cos(rad));
                if (tickCount % 3 == 0) {
                    Vec3 tail = position().subtract(fwd.scale(3.3D)).add(0.0D, 1.35D, 0.0D);
                    sl.sendParticles(ParticleTypes.BUBBLE, tail.x, tail.y, tail.z, 0, -fwd.x * 0.18D, 0.02D, -fwd.z * 0.18D, 1.0D);
                }
                if (tickCount % 28 == 0) {
                    playSfx(TeyvatBossesSounds.subPropellerHum(), SoundEvents.BUBBLE_COLUMN_UPWARDS_AMBIENT, 0.40F, 1.05F);
                }
            }
            float syncedPitch = Mth.clamp((float) (-dy * 65.0D), -30.0F, 30.0F);
            entityData.set(DATA_HULL_PITCH, Mth.lerp(0.22F, entityData.get(DATA_HULL_PITCH), syncedPitch));
        } else {
            entityData.set(DATA_HULL_PITCH, Mth.lerp(0.12F, entityData.get(DATA_HULL_PITCH), 0.0F));
            entityData.set(DATA_HULL_ROLL, Mth.lerp(0.12F, entityData.get(DATA_HULL_ROLL), 0.0F));
        }
        prevServerX = getX();
        prevServerY = getY();
        prevServerZ = getZ();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("PartsMask", getPartsMask());
        tag.putFloat("Energy", getEnergy());
        tag.putInt("TorpedoAmmo", getTorpedoAmmo());
        tag.putBoolean("Headlight", isHeadlightOn());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("PartsMask")) entityData.set(DATA_PARTS_MASK, tag.getInt("PartsMask"));
        if (tag.contains("Energy")) entityData.set(DATA_ENERGY, tag.getFloat("Energy"));
        if (tag.contains("TorpedoAmmo")) entityData.set(DATA_TORPEDO_AMMO, tag.getInt("TorpedoAmmo"));
        if (tag.contains("Headlight")) entityData.set(DATA_HEADLIGHT, tag.getBoolean("Headlight"));
        updateAssemblyLabel();
    }
}

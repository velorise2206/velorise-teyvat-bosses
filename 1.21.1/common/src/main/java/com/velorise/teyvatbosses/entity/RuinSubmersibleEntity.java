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
 * Pilotable 6-DoF Underwater Submersible with Alex's Caves-style Panoramic Glass Canopy Dome:
 * assembled from 4 {@link RuinNautilusEntity} salvage parts (Hull, Cockpit, Propeller, Torpedo Pods).
 *
 * <p>Uses direct client-synced W/A/S/D + Space/Ctrl controls (zero stuck input), single-pass 6-DoF physics
 * (no double-move jitter), surface-skimming water detection, safe overhead dismount, infinite breathing +
 * flicker-free Night Vision headlight, Sonar Pulse (LMB), 6-Torpedo salvo (RMB), Jet Dash (R), and Headlight (F).
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

    private float forwardInput = 0.0F;
    private float strafeInput = 0.0F;
    private int verticalInput = 0; // +1 ascend (Space), -1 descend (Ctrl)
    private int inputTimeoutTicks = 0;
    private int sonarCooldown = 0;
    private int torpedoCooldown = 0;
    private int dashCooldown = 0;
    private int torpedoRegenTicks = 0;
    private float propSpinAngle = 0.0F;

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
                .add(Attributes.MOVEMENT_SPEED, 0.35D);
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

    public int getPartsMask() { return entityData.get(DATA_PARTS_MASK); }
    public boolean isFullyAssembled() { return (getPartsMask() & FULL_PARTS) == FULL_PARTS; }
    public boolean hasPart(int bit) { return (getPartsMask() & bit) != 0; }
    public float getEnergy() { return entityData.get(DATA_ENERGY); }
    public int getTorpedoAmmo() { return entityData.get(DATA_TORPEDO_AMMO); }
    public boolean isHeadlightOn() { return entityData.get(DATA_HEADLIGHT); }
    public float getHullPitchDeg() { return entityData.get(DATA_HULL_PITCH); }
    public float getHullRollDeg() { return entityData.get(DATA_HULL_ROLL); }
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

    private boolean isInOrOnWater() {
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
    public void travel(Vec3 travelVector) {
        // Movement is executed once per tick inside tick() — no duplicate move() call!
    }

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
        // Seat pilot cleanly inside the forward Alex's Caves-style panoramic glass canopy dome
        moveFunc.accept(passenger, getX() + fwdX * 2.10D, getY() + 0.45D, getZ() + fwdZ * 2.10D);
    }

    @Override
    public Vec3 getDismountLocationForPassenger(LivingEntity passenger) {
        // Always dismount safely above the submarine deck so the pilot never clips into seabed/walls
        return new Vec3(getX(), getY() + getBbHeight() + 0.65D, getZ());
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
        this.forwardInput = Mth.clamp(fwdIn, -1.0F, 1.0F);
        this.strafeInput = Mth.clamp(strafeIn, -1.0F, 1.0F);
        this.verticalInput = Mth.clamp(vertInput, -1, 1);
        this.inputTimeoutTicks = 12;

        if (action == ACTION_HEADLIGHT) {
            entityData.set(DATA_HEADLIGHT, !isHeadlightOn());
            playSfx(TeyvatBossesSounds.subSonarPing(), SoundEvents.LEVER_CLICK, 0.65F, 1.55F);
            return;
        }
        if (action == ACTION_DASH && dashCooldown <= 0 && getEnergy() >= 14.0F && isInOrOnWater()) {
            addEnergy(-14.0F);
            dashCooldown = 36;
            Vec3 dash = aimDir.lengthSqr() > 1e-4D ? aimDir.normalize() : Vec3.directionFromRotation(getXRot(), getYRot());
            setDeltaMovement(getDeltaMovement().add(dash.scale(1.85D)));
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

        if (level().isClientSide()) return;
        ServerLevel sl = (ServerLevel) level();

        if (sonarCooldown > 0) sonarCooldown--;
        if (torpedoCooldown > 0) torpedoCooldown--;
        if (dashCooldown > 0) dashCooldown--;
        if (inputTimeoutTicks > 0) {
            inputTimeoutTicks--;
        } else {
            forwardInput = 0.0F;
            strafeInput = 0.0F;
            verticalInput = 0;
        }

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
            // Infinite air + crystal-clear underwater vision (400t Night Vision prevents <200t flicker)
            p.setAirSupply(p.getMaxAirSupply());
            if (inWaterNow) {
                p.addEffect(new MobEffectInstance(MobEffects.CONDUIT_POWER, 60, 0, true, false));
                if (isHeadlightOn()) {
                    p.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 400, 0, true, false));
                }
            }

            if (getEnergy() > 0.5F && inWaterNow) {
                float targetYaw = p.getYRot();
                float deltaYaw = Mth.wrapDegrees(targetYaw - getYRot());
                float nextYaw = getYRot() + Mth.clamp(deltaYaw, -7.5F, 7.5F);
                setYRot(nextYaw);
                this.yBodyRot = nextYaw;
                this.yHeadRot = nextYaw;

                float rad = nextYaw * Mth.DEG_TO_RAD;
                Vec3 fwd = new Vec3(-Mth.sin(rad), 0.0D, Mth.cos(rad));
                Vec3 right = new Vec3(-Mth.cos(rad), 0.0D, -Mth.sin(rad));

                double maxSpd = 0.92D;
                double vertVel = verticalInput * 0.48D;
                // Prevent breaching far above water surface while still allowing easy surface skimming
                if (vertVel > 0.0D && !level().getFluidState(blockPosition().above()).is(FluidTags.WATER)) {
                    vertVel = 0.0D;
                }
                Vec3 wish = fwd.scale(forwardInput * maxSpd).add(right.scale(-strafeInput * 0.48D)).add(0.0D, vertVel, 0.0D);
                Vec3 nextVel = getDeltaMovement().lerp(wish, 0.30D);
                setDeltaMovement(nextVel);
                move(MoverType.SELF, nextVel);

                float targetPitch = Mth.clamp((float) (-nextVel.y * 70.0D), -32.0F, 32.0F);
                float targetRoll = Mth.clamp(-deltaYaw * 2.0F - strafeInput * 10.0F, -22.0F, 22.0F);
                entityData.set(DATA_HULL_PITCH, Mth.lerp(0.18F, getHullPitchDeg(), targetPitch));
                entityData.set(DATA_HULL_ROLL, Mth.lerp(0.18F, getHullRollDeg(), targetRoll));

                if (wish.lengthSqr() > 1e-3D && tickCount % 20 == 0) {
                    addEnergy(-0.5F);
                }
                if (wish.lengthSqr() > 0.02D && tickCount % 3 == 0) {
                    Vec3 tail = position().subtract(fwd.scale(3.3D)).add(0.0D, 1.35D, 0.0D);
                    sl.sendParticles(ParticleTypes.BUBBLE, tail.x, tail.y, tail.z, 0, -fwd.x * 0.18D, 0.02D, -fwd.z * 0.18D, 1.0D);
                }
                if (wish.lengthSqr() > 0.02D && tickCount % 28 == 0) {
                    playSfx(TeyvatBossesSounds.subPropellerHum(), SoundEvents.BUBBLE_COLUMN_UPWARDS_AMBIENT, 0.40F, 1.05F);
                }
            } else {
                double vy = inWaterNow ? 0.0D : Math.max(-0.4D, getDeltaMovement().y - 0.06D);
                setDeltaMovement(getDeltaMovement().x * 0.75D, vy, getDeltaMovement().z * 0.75D);
                move(MoverType.SELF, getDeltaMovement());
            }
        } else {
            forwardInput = 0.0F;
            strafeInput = 0.0F;
            verticalInput = 0;
            entityData.set(DATA_HULL_PITCH, Mth.lerp(0.12F, getHullPitchDeg(), 0.0F));
            entityData.set(DATA_HULL_ROLL, Mth.lerp(0.12F, getHullRollDeg(), 0.0F));
            double vy = inWaterNow ? 0.0D : Math.max(-0.4D, getDeltaMovement().y - 0.06D);
            setDeltaMovement(getDeltaMovement().x * 0.80D, vy, getDeltaMovement().z * 0.80D);
            move(MoverType.SELF, getDeltaMovement());
        }
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

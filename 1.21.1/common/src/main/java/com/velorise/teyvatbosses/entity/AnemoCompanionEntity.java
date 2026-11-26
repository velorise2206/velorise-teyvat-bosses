package com.velorise.teyvatbosses.entity;

import com.velorise.teyvatbosses.registry.TeyvatBossesItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

/**
 * Miniature 1-block 8-cube Anemo Hypostasis Companion Pet assembled on a single block outdoors
 * from 8 {@code anemo_prism_cube} drops.
 *
 * <p>Uses the Mini Ender Dragon Pet world-space free-flight AI &amp; 3-state FSM
 * ({@code FOLLOW} / {@code ENGAGE} / {@code ATTACK}, plus {@code RECHARGE}) from {@code velorise-staves}:
 * <ul>
 *   <li>World-space independent orbit around owner (never jerks when the player spins their camera).</li>
 *   <li>Aerodynamic acceleration, arrival braking, smooth turn-rate limiting, and terrain-safe hover clearance.</li>
 *   <li>Hysteresis-buffered combat engage orbit ({@code 4.2} blocks), brief aim-lock brake on each shot,
 *       and defensive return-to-owner orbit while regenerating all 8 shell cubes.</li>
 * </ul>
 */
public class AnemoCompanionEntity extends Mob {
    public static final int MAX_CUBES = 8;
    private static final int FIRE_INTERVAL_TICKS = 11;
    private static final int RECHARGE_PER_CUBE_TICKS = 22;
    private static final double ENGAGE_DETECT_RANGE = 20.0D;
    private static final double TELEPORT_DIST = 32.0D;
    private static final float CUBE_DAMAGE = 8.5F;

    // Mini Ender Dragon Pet world-space free-flight physics constants (per-tick equivalents, dt = 0.05s)
    private static final double FOLLOW_ORBIT_RADIUS = 1.75D;
    private static final double FOLLOW_HEIGHT_OFFSET = 0.55D;
    private static final double FOLLOW_BOB_AMP = 0.16D;
    private static final float FOLLOW_OMEGA_PER_TICK = 1.05F * 0.05F;
    private static final double ENGAGE_ORBIT_RADIUS = 4.20D;
    private static final double ENGAGE_HEIGHT_OFFSET = 1.45D;
    private static final float ENGAGE_OMEGA_PER_TICK = 1.35F * 0.05F;
    private static final double MAX_SPEED_FOLLOW = 0.35D;   // 7.0 blocks/s
    private static final double MAX_SPEED_COMBAT = 0.50D;   // 10.0 blocks/s
    private static final double MAX_ACCEL_TICK = 0.08D;     // 32.0 blocks/s^2 * dt^2 scaled cleanly
    private static final double ARRIVAL_K = 0.21D;          // smooth arrival braking gain
    private static final float MAX_TURN_FOLLOW_TICK = 13.5F; // 270 deg/s
    private static final float MAX_TURN_COMBAT_TICK = 36.0F; // 720 deg/s
    private static final int ENGAGE_HYSTERESIS_TICKS = 5;   // 250ms grace window

    private static final EntityDataAccessor<Integer> DATA_ASSEMBLED =
            SynchedEntityData.defineId(AnemoCompanionEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_ACTIVE_SHELLS =
            SynchedEntityData.defineId(AnemoCompanionEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_RECHARGING =
            SynchedEntityData.defineId(AnemoCompanionEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_STAYING =
            SynchedEntityData.defineId(AnemoCompanionEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Optional<UUID>> DATA_OWNER =
            SynchedEntityData.defineId(AnemoCompanionEntity.class, EntityDataSerializers.OPTIONAL_UUID);

    private int fireCooldown = 0;
    private int rechargeTimer = 0;
    private float orbitAngle = 0.0F;
    private int attackBrakeTicks = 0;
    private int targetGraceTicks = 0;
    private @Nullable UUID lastTargetUuid = null;

    public AnemoCompanionEntity(EntityType<? extends Mob> type, Level level) {
        super(type, level);
        this.setPersistenceRequired();
        this.setNoGravity(true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 80.0D)
                .add(Attributes.ARMOR, 8.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.35D);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_ASSEMBLED, 1);
        builder.define(DATA_ACTIVE_SHELLS, 1);
        builder.define(DATA_RECHARGING, false);
        builder.define(DATA_STAYING, false);
        builder.define(DATA_OWNER, Optional.empty());
    }

    public void initAssembly() {
        entityData.set(DATA_ASSEMBLED, 1);
        entityData.set(DATA_ACTIVE_SHELLS, 1);
        entityData.set(DATA_RECHARGING, false);
        entityData.set(DATA_STAYING, false);
        updateAssemblyLabel();
    }

    public int getAssembledCount() { return entityData.get(DATA_ASSEMBLED); }
    public boolean isFullyAssembled() { return getAssembledCount() >= MAX_CUBES; }
    public int getActiveShells() { return entityData.get(DATA_ACTIVE_SHELLS); }
    public boolean isRecharging() { return entityData.get(DATA_RECHARGING); }
    public boolean isStaying() { return entityData.get(DATA_STAYING); }
    public @Nullable UUID getOwnerUuid() { return entityData.get(DATA_OWNER).orElse(null); }

    public boolean isOwnedBy(Player player) {
        UUID owner = getOwnerUuid();
        return owner != null && player != null && owner.equals(player.getUUID());
    }

    private void updateAssemblyLabel() {
        int c = getAssembledCount();
        if (c < MAX_CUBES) {
            setCustomName(Component.literal(c + " / " + MAX_CUBES));
            setCustomNameVisible(true);
        } else {
            setCustomName(null);
            setCustomNameVisible(false);
        }
    }

    public boolean addAssemblyCube(ServerLevel sl, Player player, ItemStack stack) {
        if (isFullyAssembled()) return false;
        int next = Math.min(MAX_CUBES, getAssembledCount() + 1);
        entityData.set(DATA_ASSEMBLED, next);
        entityData.set(DATA_ACTIVE_SHELLS, next);
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        sl.playSound(null, getX(), getY() + 0.5D, getZ(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 0.9F, 0.95F + next * 0.08F);
        sl.sendParticles(ParticleTypes.END_ROD, getX(), getY() + 0.55D, getZ(), 8, 0.30D, 0.30D, 0.30D, 0.04D);

        if (next >= MAX_CUBES) {
            entityData.set(DATA_OWNER, Optional.of(player.getUUID()));
            entityData.set(DATA_RECHARGING, false);
            entityData.set(DATA_STAYING, false);
            orbitAngle = (getId() * 1.618F) % ((float) Math.PI * 2.0F);
            updateAssemblyLabel();
            sl.playSound(null, getX(), getY() + 0.5D, getZ(), SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 1.1F, 1.30F);
            sl.sendParticles(ParticleTypes.GUST_EMITTER_SMALL, getX(), getY() + 0.6D, getZ(), 1, 0, 0, 0, 0);
            sl.sendParticles(ParticleTypes.END_ROD, getX(), getY() + 0.6D, getZ(), 24, 0.45D, 0.45D, 0.45D, 0.08D);
            player.displayClientMessage(Component.translatable("message.velorise_teyvat_bosses.anemo_pet.assembled"), true);
        } else {
            updateAssemblyLabel();
            player.displayClientMessage(Component.translatable("message.velorise_teyvat_bosses.anemo_pet.progress", next, MAX_CUBES), true);
        }
        return true;
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
    @Override public boolean fireImmune() { return true; }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypes.FALL) || source.is(DamageTypes.DROWN)
                || source.is(DamageTypes.IN_WALL) || source.is(DamageTypes.CRAMMING)
                || source.is(DamageTypes.ON_FIRE) || source.is(DamageTypes.IN_FIRE)) {
            return false;
        }
        if (source.getEntity() instanceof Player p && isOwnedBy(p)) {
            return false;
        }
        return super.hurt(source, amount);
    }

    @Override
    public void die(DamageSource cause) {
        if (!level().isClientSide()) {
            dropAssembledCubes();
        }
        super.die(cause);
    }

    private void dropAssembledCubes() {
        int count = getAssembledCount();
        if (count > 0 && TeyvatBossesItems.anemoPrismCube() != null) {
            spawnAtLocation(new ItemStack(TeyvatBossesItems.anemoPrismCube(), count));
        }
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level().isClientSide()) return InteractionResult.SUCCESS;
        ServerLevel sl = (ServerLevel) level();

        if (player.isShiftKeyDown() && stack.getItem() instanceof PickaxeItem) {
            if (!isFullyAssembled() || isOwnedBy(player) || player.getAbilities().instabuild) {
                dropAssembledCubes();
                sl.playSound(null, getX(), getY() + 0.5D, getZ(), SoundEvents.BREEZE_WIND_CHARGE_BURST.value(), SoundSource.PLAYERS, 0.9F, 1.25F);
                sl.sendParticles(ParticleTypes.CLOUD, getX(), getY() + 0.5D, getZ(), 14, 0.35D, 0.35D, 0.35D, 0.05D);
                player.displayClientMessage(Component.translatable("message.velorise_teyvat_bosses.anemo_pet.disassembled"), true);
                discard();
                return InteractionResult.CONSUME;
            }
            return InteractionResult.CONSUME;
        }

        if (!isFullyAssembled()) {
            if (TeyvatBossesItems.anemoPrismCube() != null && stack.is(TeyvatBossesItems.anemoPrismCube())) {
                addAssemblyCube(sl, player, stack);
            }
            return InteractionResult.CONSUME;
        }

        if (isOwnedBy(player)) {
            boolean nextStay = !isStaying();
            entityData.set(DATA_STAYING, nextStay);
            sl.playSound(null, getX(), getY() + 0.5D, getZ(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 0.75F, nextStay ? 0.90F : 1.35F);
            player.displayClientMessage(Component.translatable(
                    nextStay ? "message.velorise_teyvat_bosses.anemo_pet.stay" : "message.velorise_teyvat_bosses.anemo_pet.follow"
            ), true);
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }

    private static float approachAngle(float cur, float tgt, float maxStep) {
        float d = Mth.wrapDegrees(tgt - cur);
        return cur + Mth.clamp(d, -maxStep, maxStep);
    }

    private static double resolveNaturalGroundY(ServerLevel level, double x, double refY, double z) {
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos(
                Mth.floor(x), Mth.floor(refY) + 3, Mth.floor(z));
        for (int i = 0; i < 8; i++) {
            BlockPos below = cursor.below();
            boolean solidOrLiquidBelow = !level.getBlockState(below).getCollisionShape(level, below).isEmpty()
                    || !level.getFluidState(below).isEmpty();
            boolean freeAtCursor = level.getBlockState(cursor).getCollisionShape(level, cursor).isEmpty()
                    && level.getFluidState(cursor).isEmpty();
            if (solidOrLiquidBelow && freeAtCursor) {
                return below.getY() + 1.0D;
            }
            cursor.move(Direction.DOWN);
        }
        return refY;
    }

    @Override
    public void tick() {
        super.tick();
        if (isOnFire()) clearFire();

        if (level().isClientSide()) {
            if (isFullyAssembled() && tickCount % 6 == 0) {
                level().addParticle(ParticleTypes.END_ROD,
                        getX() + (random.nextDouble() - 0.5D) * 0.5D,
                        getY() + 0.45D + (random.nextDouble() - 0.5D) * 0.35D,
                        getZ() + (random.nextDouble() - 0.5D) * 0.5D,
                        0.0D, 0.015D, 0.0D);
            }
            return;
        }

        ServerLevel sl = (ServerLevel) level();
        if (!isFullyAssembled()) {
            setDeltaMovement(Vec3.ZERO);
            return;
        }

        if (tickCount % 40 == 0 && getHealth() < getMaxHealth()) {
            heal(2.0F);
        }
        if (attackBrakeTicks > 0) attackBrakeTicks--;

        UUID ownerId = getOwnerUuid();
        Player owner = ownerId != null ? sl.getPlayerByUUID(ownerId) : null;

        // Hysteresis-buffered target acquisition (prevents rapid state flickering like Mini Ender Dragon Pet)
        LivingEntity target = findTarget(sl, owner);
        if (target != null) {
            lastTargetUuid = target.getUUID();
            targetGraceTicks = ENGAGE_HYSTERESIS_TICKS;
        } else if (targetGraceTicks > 0 && lastTargetUuid != null) {
            targetGraceTicks--;
            Entity prev = sl.getEntity(lastTargetUuid);
            if (prev instanceof LivingEntity le && isValidTarget(le, owner)
                    && distanceToSqr(le) <= (ENGAGE_DETECT_RANGE + 3.0D) * (ENGAGE_DETECT_RANGE + 3.0D)) {
                target = le;
            }
        } else {
            lastTargetUuid = null;
        }

        boolean inCombat = (target != null && !isRecharging());

        // 1. Mini Ender Dragon Pet World-Space Free-Flight Physics (FOLLOW / ENGAGE / ATTACK)
        if (owner != null && owner.isAlive() && !isStaying()) {
            double ownerDistSq = distanceToSqr(owner);
            if (ownerDistSq > TELEPORT_DIST * TELEPORT_DIST) {
                orbitAngle = (getId() * 1.618F) % ((float) Math.PI * 2.0F);
                double tx = owner.getX() + Math.cos(orbitAngle) * FOLLOW_ORBIT_RADIUS;
                double tz = owner.getZ() + Math.sin(orbitAngle) * FOLLOW_ORBIT_RADIUS;
                double ty = Math.max(resolveNaturalGroundY(sl, tx, owner.getY(), tz) + 1.1D,
                        owner.getY() + owner.getBbHeight() + FOLLOW_HEIGHT_OFFSET);
                moveTo(tx, ty, tz, owner.getYRot(), 0.0F);
                setDeltaMovement(Vec3.ZERO);
                sl.sendParticles(ParticleTypes.CLOUD, tx, ty, tz, 8, 0.25D, 0.25D, 0.25D, 0.04D);
            } else {
                // World-space orbit angle advances smoothly, completely independent of player camera rotation!
                orbitAngle += inCombat ? ENGAGE_OMEGA_PER_TICK : FOLLOW_OMEGA_PER_TICK;
                if (orbitAngle > Math.PI * 2.0D) orbitAngle -= (float) (Math.PI * 2.0D);

                Vec3 anchor;
                if (inCombat) {
                    double bob = Math.sin(tickCount * 0.085D) * 0.26D;
                    double ax = target.getX() + Math.cos(orbitAngle) * ENGAGE_ORBIT_RADIUS;
                    double az = target.getZ() + Math.sin(orbitAngle) * ENGAGE_ORBIT_RADIUS;
                    double groundY = resolveNaturalGroundY(sl, ax, target.getY(), az);
                    double ay = Math.max(groundY + 1.15D, target.getY() + ENGAGE_HEIGHT_OFFSET + bob);
                    anchor = new Vec3(ax, ay, az);
                } else {
                    double bob = Math.sin(tickCount * 0.075D) * FOLLOW_BOB_AMP;
                    double r = isRecharging() ? 1.45D : FOLLOW_ORBIT_RADIUS;
                    double ax = owner.getX() + Math.cos(orbitAngle) * r;
                    double az = owner.getZ() + Math.sin(orbitAngle) * r;
                    double groundY = resolveNaturalGroundY(sl, ax, owner.getY(), az);
                    double ay = Math.max(groundY + 1.10D, owner.getY() + owner.getBbHeight() + FOLLOW_HEIGHT_OFFSET + bob);
                    anchor = new Vec3(ax, ay, az);
                }

                Vec3 curVel = getDeltaMovement();
                Vec3 nextVel;
                if (attackBrakeTicks > 0) {
                    // Brief hover-brake right as a shell cube launches (exact Mini Ender Dragon ATTACK feel!)
                    nextVel = curVel.scale(0.55D);
                } else {
                    Vec3 toAnchor = anchor.subtract(position());
                    Vec3 desiredVel = toAnchor.scale(ARRIVAL_K);
                    double maxSpd = (inCombat || ownerDistSq > 49.0D) ? MAX_SPEED_COMBAT : MAX_SPEED_FOLLOW;
                    double dLen = desiredVel.length();
                    if (dLen > maxSpd) {
                        desiredVel = desiredVel.scale(maxSpd / dLen);
                    }
                    // Aerodynamic steering with acceleration limit + subtle drag
                    Vec3 steer = desiredVel.subtract(curVel);
                    double sLen = steer.length();
                    if (sLen > MAX_ACCEL_TICK) {
                        steer = steer.scale(MAX_ACCEL_TICK / sLen);
                    }
                    nextVel = curVel.add(steer).scale(0.945D);
                }

                setDeltaMovement(nextVel);
                move(MoverType.SELF, nextVel);

                // Smooth yaw tracking with realistic turn-rate limit (270 deg/s follow, 720 deg/s combat)
                Vec3 lookVec = inCombat ? target.position().subtract(position()) : nextVel;
                if (lookVec.horizontalDistanceSqr() > 1.0E-4D) {
                    float desiredYaw = (float) (Mth.atan2(-lookVec.x, lookVec.z) * Mth.RAD_TO_DEG);
                    float maxTurn = inCombat ? MAX_TURN_COMBAT_TICK : MAX_TURN_FOLLOW_TICK;
                    float nextYaw = approachAngle(getYRot(), desiredYaw, maxTurn);
                    setYRot(nextYaw);
                    yBodyRot = nextYaw;
                    yHeadRot = nextYaw;
                }
            }
        } else {
            double bob = Mth.sin(tickCount * 0.09F) * 0.012D;
            Vec3 hover = new Vec3(getDeltaMovement().x * 0.75D, bob, getDeltaMovement().z * 0.75D);
            setDeltaMovement(hover);
            move(MoverType.SELF, hover);
        }

        // 2. Shell Cube Recharge Cycle: once empty (0/8), MUST regenerate back to full 8/8 before firing again!
        if (isRecharging()) {
            rechargeTimer++;
            if (rechargeTimer >= RECHARGE_PER_CUBE_TICKS) {
                rechargeTimer = 0;
                int nextShells = Math.min(MAX_CUBES, getActiveShells() + 1);
                entityData.set(DATA_ACTIVE_SHELLS, nextShells);
                sl.playSound(null, getX(), getY() + 0.5D, getZ(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 0.45F, 1.05F + nextShells * 0.06F);
                sl.sendParticles(ParticleTypes.END_ROD, getX(), getY() + 0.55D, getZ(), 4, 0.28D, 0.28D, 0.28D, 0.03D);
                if (nextShells >= MAX_CUBES) {
                    entityData.set(DATA_RECHARGING, false);
                    fireCooldown = 6;
                }
            }
            return;
        }

        // 3. Auto-Target & Fire 1 Shell Cube per Shot
        if (fireCooldown > 0) {
            fireCooldown--;
            return;
        }
        if (target == null) return;

        int shells = getActiveShells();
        if (shells <= 0) {
            entityData.set(DATA_RECHARGING, true);
            rechargeTimer = 0;
            return;
        }

        Vec3 origin = position().add(0.0D, 0.45D, 0.0D);
        Vec3 leadTarget = target.position().add(target.getDeltaMovement().scale(3.0D)).add(0.0D, target.getBbHeight() * 0.5D, 0.0D);
        Vec3 aim = leadTarget.subtract(origin);
        if (aim.lengthSqr() < 1.0e-4D) return;
        float aimYaw = (float) (Mth.atan2(-aim.x, aim.z) * Mth.RAD_TO_DEG);
        setYRot(aimYaw);
        yBodyRot = aimYaw;
        yHeadRot = aimYaw;
        attackBrakeTicks = 4;

        LivingEntity credit = owner != null ? owner : this;
        Vec3 vel = aim.normalize().scale(1.24D);
        AnemoMissileProjectile.launchPetCube(sl, credit, origin, vel, CUBE_DAMAGE);
        sl.playSound(null, origin.x, origin.y, origin.z, SoundEvents.BREEZE_SHOOT, SoundSource.PLAYERS, 0.75F, 1.25F + (MAX_CUBES - shells) * 0.04F);
        sl.sendParticles(ParticleTypes.CLOUD, origin.x, origin.y, origin.z, 4, 0.16D, 0.16D, 0.16D, 0.03D);

        int remaining = shells - 1;
        entityData.set(DATA_ACTIVE_SHELLS, remaining);
        fireCooldown = FIRE_INTERVAL_TICKS;

        if (remaining <= 0) {
            entityData.set(DATA_RECHARGING, true);
            rechargeTimer = 0;
        }
    }

    private @Nullable LivingEntity findTarget(ServerLevel sl, @Nullable Player owner) {
        Vec3 scanCenter = owner != null && !isStaying() ? owner.position() : position();
        if (owner != null) {
            LivingEntity hurtMob = owner.getLastHurtMob();
            if (isValidTarget(hurtMob, owner) && hurtMob.position().distanceToSqr(scanCenter) <= ENGAGE_DETECT_RANGE * ENGAGE_DETECT_RANGE && hasLineOfSight(hurtMob)) {
                return hurtMob;
            }
            LivingEntity hurtBy = owner.getLastHurtByMob();
            if (isValidTarget(hurtBy, owner) && hurtBy.position().distanceToSqr(scanCenter) <= ENGAGE_DETECT_RANGE * ENGAGE_DETECT_RANGE && hasLineOfSight(hurtBy)) {
                return hurtBy;
            }
        }
        AABB box = new AABB(scanCenter, scanCenter).inflate(ENGAGE_DETECT_RANGE);
        LivingEntity best = null;
        double bestDistSq = ENGAGE_DETECT_RANGE * ENGAGE_DETECT_RANGE;
        for (LivingEntity e : sl.getEntitiesOfClass(LivingEntity.class, box, le -> isValidTarget(le, owner))) {
            double dSq = e.position().distanceToSqr(scanCenter);
            if (dSq < bestDistSq && hasLineOfSight(e)) {
                bestDistSq = dSq;
                best = e;
            }
        }
        return best;
    }

    private boolean isValidTarget(@Nullable LivingEntity e, @Nullable Player owner) {
        if (e == null || !e.isAlive() || e == this || e == owner) return false;
        if (e instanceof Player || e instanceof AnemoCompanionEntity || e instanceof RuinSubmersibleEntity
                || e instanceof RuinMechEntity || e instanceof CryoTurretEntity) {
            return false;
        }
        if (owner != null && e instanceof OwnableEntity ownable && owner.getUUID().equals(ownable.getOwnerUUID())) {
            return false;
        }
        return e instanceof Enemy;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("Assembled", getAssembledCount());
        tag.putInt("ActiveShells", getActiveShells());
        tag.putBoolean("Recharging", isRecharging());
        tag.putBoolean("Staying", isStaying());
        UUID owner = getOwnerUuid();
        if (owner != null) tag.putUUID("Owner", owner);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Assembled")) entityData.set(DATA_ASSEMBLED, Mth.clamp(tag.getInt("Assembled"), 1, MAX_CUBES));
        if (tag.contains("ActiveShells")) entityData.set(DATA_ACTIVE_SHELLS, Mth.clamp(tag.getInt("ActiveShells"), 0, MAX_CUBES));
        if (tag.contains("Recharging")) entityData.set(DATA_RECHARGING, tag.getBoolean("Recharging"));
        if (tag.contains("Staying")) entityData.set(DATA_STAYING, tag.getBoolean("Staying"));
        if (tag.hasUUID("Owner")) entityData.set(DATA_OWNER, Optional.of(tag.getUUID("Owner")));
        updateAssemblyLabel();
    }
}

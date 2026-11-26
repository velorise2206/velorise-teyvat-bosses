package com.velorise.teyvatbosses.entity;

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
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

/**
 * Miniature 8-cube Anemo Hypostasis Companion Pet assembled directly on a single block outdoors
 * from 8 {@code anemo_prism_cube} drops.
 *
 * <ul>
 *   <li>Place 1st cube on a block ({@code 1 / 8}), then weld 7 more cubes onto it.</li>
 *   <li>Completing {@code 8 / 8} permanently binds ownership to the completing player.</li>
 *   <li>Follows the owner smoothly overhead, auto-targeting nearby hostile mobs.</li>
 *   <li>Each shot launches 1 physical shell cube from its 8-cube body, dealing Anemo damage
 *       (via {@code StavesElementBridge} when Velorise's Staves is present, or normal damage
 *       fallback otherwise) plus wind knockback.</li>
 *   <li>Once all 8 cubes are fired, enters recharge mode and waits until all 8 cubes regenerate
 *       before firing again.</li>
 * </ul>
 */
public class AnemoCompanionEntity extends Mob {
    public static final int MAX_CUBES = 8;
    private static final int FIRE_INTERVAL_TICKS = 11;
    private static final int RECHARGE_PER_CUBE_TICKS = 14;
    private static final double TARGET_RANGE = 18.0D;
    private static final float CUBE_DAMAGE = 8.5F;

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

    /**
     * Adds 1 cube during outdoor assembly. Returns true if consumed.
     */
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

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypes.FALL) || source.is(DamageTypes.DROWN)
                || source.is(DamageTypes.IN_WALL) || source.is(DamageTypes.CRAMMING)) {
            return false;
        }
        // Owner cannot accidentally kill their own companion
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

        // Shift + Pickaxe disassembles and recovers all welded cubes (owner only once completed)
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

        // Still assembling (1..7 / 8)
        if (!isFullyAssembled()) {
            if (TeyvatBossesItems.anemoPrismCube() != null && stack.is(TeyvatBossesItems.anemoPrismCube())) {
                addAssemblyCube(sl, player, stack);
            }
            return InteractionResult.CONSUME;
        }

        // Owner toggles Follow / Guard mode
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

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide()) {
            if (isFullyAssembled() && tickCount % 6 == 0) {
                level().addParticle(ParticleTypes.END_ROD,
                        getX() + (random.nextDouble() - 0.5D) * 0.6D,
                        getY() + 0.45D + (random.nextDouble() - 0.5D) * 0.4D,
                        getZ() + (random.nextDouble() - 0.5D) * 0.6D,
                        0.0D, 0.015D, 0.0D);
            }
            return;
        }

        ServerLevel sl = (ServerLevel) level();
        if (!isFullyAssembled()) {
            setDeltaMovement(Vec3.ZERO);
            return;
        }

        // Passive regeneration so companion stays durable
        if (tickCount % 40 == 0 && getHealth() < getMaxHealth()) {
            heal(2.0F);
        }

        UUID ownerId = getOwnerUuid();
        Player owner = ownerId != null ? sl.getPlayerByUUID(ownerId) : null;

        // 1. Smooth hovering follow behavior behind owner's shoulder
        if (owner != null && owner.isAlive() && !isStaying()) {
            double distSq = distanceToSqr(owner);
            if (distSq > 22.0D * 22.0D) {
                // Teleport cleanly near owner if too far away
                float yawRad = owner.getYRot() * Mth.DEG_TO_RAD;
                double tx = owner.getX() + Mth.sin(yawRad) * 1.6D + Mth.cos(yawRad) * 1.1D;
                double tz = owner.getZ() - Mth.cos(yawRad) * 1.6D + Mth.sin(yawRad) * 1.1D;
                double ty = owner.getY() + 1.85D;
                moveTo(tx, ty, tz, owner.getYRot(), 0.0F);
                setDeltaMovement(Vec3.ZERO);
                sl.sendParticles(ParticleTypes.CLOUD, tx, ty, tz, 8, 0.25D, 0.25D, 0.25D, 0.04D);
            } else {
                float yawRad = owner.yBodyRot * Mth.DEG_TO_RAD;
                double backX = Mth.sin(yawRad);
                double backZ = -Mth.cos(yawRad);
                double rightX = -Mth.cos(yawRad);
                double rightZ = -Mth.sin(yawRad);
                double bob = Mth.sin(tickCount * 0.10F) * 0.18D;
                Vec3 goal = new Vec3(
                        owner.getX() + backX * 1.55D + rightX * 1.15D,
                        owner.getY() + 1.85D + bob,
                        owner.getZ() + backZ * 1.55D + rightZ * 1.15D
                );
                Vec3 diff = goal.subtract(position());
                double len = diff.length();
                Vec3 desired = len > 0.15D ? diff.scale(Math.min(0.42D, 0.16D + len * 0.06D) / Math.max(len, 0.01D) * Math.min(len, 2.2D)) : Vec3.ZERO;
                Vec3 nextVel = getDeltaMovement().lerp(desired, 0.28D);
                setDeltaMovement(nextVel);
                move(MoverType.SELF, nextVel);
            }
        } else {
            // Hover gently in place when staying or owner offline
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
                sl.sendParticles(ParticleTypes.END_ROD, getX(), getY() + 0.55D, getZ(), 4, 0.30D, 0.30D, 0.30D, 0.03D);
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

        LivingEntity target = findTarget(sl, owner);
        if (target == null) return;

        int shells = getActiveShells();
        if (shells <= 0) {
            entityData.set(DATA_RECHARGING, true);
            rechargeTimer = 0;
            return;
        }

        // Face target smoothly
        Vec3 origin = position().add(0.0D, 0.55D, 0.0D);
        Vec3 aim = target.position().add(0.0D, target.getBbHeight() * 0.5D, 0.0D).subtract(origin);
        if (aim.lengthSqr() < 1.0e-4D) return;
        float aimYaw = (float) (Mth.atan2(-aim.x, aim.z) * Mth.RAD_TO_DEG);
        setYRot(aimYaw);
        yBodyRot = aimYaw;
        yHeadRot = aimYaw;

        // Launch 1 physical shell cube projectile (attributes kill/XP to owner if online, else pet)
        LivingEntity credit = owner != null ? owner : this;
        Vec3 vel = aim.normalize().scale(1.18D);
        AnemoMissileProjectile.launchPetCube(sl, credit, origin, vel, CUBE_DAMAGE);
        sl.playSound(null, origin.x, origin.y, origin.z, SoundEvents.BREEZE_SHOOT, SoundSource.PLAYERS, 0.75F, 1.25F + (MAX_CUBES - shells) * 0.04F);
        sl.sendParticles(ParticleTypes.CLOUD, origin.x, origin.y, origin.z, 4, 0.18D, 0.18D, 0.18D, 0.03D);

        int remaining = shells - 1;
        entityData.set(DATA_ACTIVE_SHELLS, remaining);
        fireCooldown = FIRE_INTERVAL_TICKS;

        // Fired all 8 cubes -> enter recharge until full 8/8!
        if (remaining <= 0) {
            entityData.set(DATA_RECHARGING, true);
            rechargeTimer = 0;
        }
    }

    private @Nullable LivingEntity findTarget(ServerLevel sl, @Nullable Player owner) {
        // Priority 1: Mob the owner recently attacked or that attacked the owner
        if (owner != null) {
            LivingEntity hurtMob = owner.getLastHurtMob();
            if (isValidTarget(hurtMob, owner) && distanceToSqr(hurtMob) <= TARGET_RANGE * TARGET_RANGE && hasLineOfSight(hurtMob)) {
                return hurtMob;
            }
            LivingEntity hurtBy = owner.getLastHurtByMob();
            if (isValidTarget(hurtBy, owner) && distanceToSqr(hurtBy) <= TARGET_RANGE * TARGET_RANGE && hasLineOfSight(hurtBy)) {
                return hurtBy;
            }
        }
        // Priority 2: Closest hostile enemy in range
        AABB box = getBoundingBox().inflate(TARGET_RANGE);
        LivingEntity best = null;
        double bestDistSq = TARGET_RANGE * TARGET_RANGE;
        for (LivingEntity e : sl.getEntitiesOfClass(LivingEntity.class, box, le -> isValidTarget(le, owner))) {
            double dSq = distanceToSqr(e);
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

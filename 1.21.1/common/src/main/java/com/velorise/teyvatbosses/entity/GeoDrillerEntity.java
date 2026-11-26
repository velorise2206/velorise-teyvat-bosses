package com.velorise.teyvatbosses.entity;

import com.velorise.teyvatbosses.item.GeoDrillerPartItem;
import com.velorise.teyvatbosses.network.GeoDrillerControlPayload;
import com.velorise.teyvatbosses.registry.TeyvatBossesItems;
import com.velorise.teyvatbosses.registry.TeyvatBossesSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LightBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Pilotable AAA Geo Tunnel Driller assembled outdoors from 3 Primo Geovishap salvage parts
 * (Chassis, Bore Head, Reactor Engine).
 *
 * <ul>
 *   <li>1x Single / 2x Double Vanilla Chest attachment on rear deck (27 / 54 slots).</li>
 *   <li>Heavy mechanical engine spool-up (lever pull -> 0.7s rumble -> roll/bore).</li>
 *   <li>Controls: [R] Toggle Drill ON/OFF, [Space] Cycle Bore Pitch (Level/Down/Up), [F] Headlight.</li>
 *   <li>Auto-floor bridging &amp; lava sealing using mined/cargo stone; drill head deals contact damage.</li>
 *   <li>Enchantable Bore Head via Vanilla Enchanted Books: Fortune, Silk Touch, Efficiency,
 *       Unbreaking (fuel efficiency), and Sweeping Edge (expands bore 3x3 -> 4x3 -> 5x4).</li>
 * </ul>
 */
public class GeoDrillerEntity extends Mob {

    public static final int PART_CHASSIS = 1;
    public static final int PART_DRILL   = 2;
    public static final int PART_ENGINE  = 4;
    public static final int PART_ALL     = PART_CHASSIS | PART_DRILL | PART_ENGINE;
    public static final int MAX_FUEL     = 1000;

    public static final int PITCH_LEVEL = 0;
    public static final int PITCH_DOWN  = 1;
    public static final int PITCH_UP    = 2;

    private static final EntityDataAccessor<Integer> DATA_PARTS =
            SynchedEntityData.defineId(GeoDrillerEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_CHEST_COUNT =
            SynchedEntityData.defineId(GeoDrillerEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_FUEL =
            SynchedEntityData.defineId(GeoDrillerEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_DRILL_ARMED =
            SynchedEntityData.defineId(GeoDrillerEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_PITCH_MODE =
            SynchedEntityData.defineId(GeoDrillerEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_HEADLIGHT =
            SynchedEntityData.defineId(GeoDrillerEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> DATA_THROTTLE =
            SynchedEntityData.defineId(GeoDrillerEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_STEER =
            SynchedEntityData.defineId(GeoDrillerEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> DATA_FORTUNE =
            SynchedEntityData.defineId(GeoDrillerEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_SILK =
            SynchedEntityData.defineId(GeoDrillerEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_EFFICIENCY =
            SynchedEntityData.defineId(GeoDrillerEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_SWEEPING =
            SynchedEntityData.defineId(GeoDrillerEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_UNBREAKING =
            SynchedEntityData.defineId(GeoDrillerEntity.class, EntityDataSerializers.INT);

    private final SimpleContainer cargo = new SimpleContainer(54);
    private BlockPos activeLightPos = null;
    private int mineCooldown = 0;

    // Client + Server smooth mechanical inertia & partialTick animation state
    public float wheelSpin = 0.0F, prevWheelSpin = 0.0F;
    public float drillSpin = 0.0F, prevDrillSpin = 0.0F;
    public float driveSpool = 0.0F, prevDriveSpool = 0.0F;
    public float drillSpool = 0.0F, prevDrillSpool = 0.0F;
    public float leverLAngle = 0.0F, prevLeverLAngle = 0.0F;
    public float leverRAngle = 0.0F, prevLeverRAngle = 0.0F;

    public GeoDrillerEntity(EntityType<? extends Mob> type, Level level) {
        super(type, level);
        this.setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 340.0D)
                .add(Attributes.ARMOR, 14.0D)
                .add(Attributes.ARMOR_TOUGHNESS, 6.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.24D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.STEP_HEIGHT, 1.15D);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_PARTS, PART_ALL);
        builder.define(DATA_CHEST_COUNT, 0);
        builder.define(DATA_FUEL, MAX_FUEL);
        builder.define(DATA_DRILL_ARMED, false); // Default OFF so driving near base never breaks walls accidentally
        builder.define(DATA_PITCH_MODE, PITCH_LEVEL);
        builder.define(DATA_HEADLIGHT, false);
        builder.define(DATA_THROTTLE, 0.0F);
        builder.define(DATA_STEER, 0.0F);
        builder.define(DATA_FORTUNE, 0);
        builder.define(DATA_SILK, false);
        builder.define(DATA_EFFICIENCY, 0);
        builder.define(DATA_SWEEPING, 0);
        builder.define(DATA_UNBREAKING, 0);
    }

    public void initAssemblyChassis() {
        entityData.set(DATA_PARTS, PART_CHASSIS);
        entityData.set(DATA_CHEST_COUNT, 0);
        entityData.set(DATA_FUEL, MAX_FUEL / 2);
    }

    public int getPartMask() { return entityData.get(DATA_PARTS); }
    public boolean isFullyAssembled() { return (getPartMask() & PART_ALL) == PART_ALL; }
    public int getAssembledCount() { return Integer.bitCount(getPartMask() & PART_ALL); }
    public int getChestCount() { return entityData.get(DATA_CHEST_COUNT); }
    public int getFuel() { return entityData.get(DATA_FUEL); }
    public boolean isDrillArmed() { return entityData.get(DATA_DRILL_ARMED); }
    public boolean isDrilling() { return isDrillArmed() && isVehicle() && getFuel() > 0; }
    public int getPitchMode() { return entityData.get(DATA_PITCH_MODE); }
    public boolean isHeadlightOn() { return entityData.get(DATA_HEADLIGHT); }
    public float getThrottleInput() { return entityData.get(DATA_THROTTLE); }
    public float getSteerInput() { return entityData.get(DATA_STEER); }
    public int getFortuneLevel() { return entityData.get(DATA_FORTUNE); }
    public boolean hasSilkTouch() { return entityData.get(DATA_SILK); }
    public int getEfficiencyLevel() { return entityData.get(DATA_EFFICIENCY); }
    public int getSweepingLevel() { return entityData.get(DATA_SWEEPING); }
    public int getUnbreakingLevel() { return entityData.get(DATA_UNBREAKING); }

    public String getBoreLabel() {
        int sw = getSweepingLevel();
        if (sw >= 2) return "5x4";
        if (sw == 1) return "4x3";
        return "3x3";
    }

    @Override
    protected void registerGoals() {}

    @Override
    public boolean removeWhenFarAway(double distSq) { return false; }

    @Override
    public boolean isPushable() { return false; }

    @Override
    protected boolean canRide(Entity vehicle) { return false; }

    @Override
    public @Nullable LivingEntity getControllingPassenger() {
        return getFirstPassenger() instanceof LivingEntity le ? le : null;
    }

    @Override
    protected Vec3 getPassengerAttachmentPoint(Entity passenger, EntityDimensions dims, float partialTick) {
        // Seated snugly inside the raised cockpit_cage right behind lever_l & lever_r
        return new Vec3(0.0D, 0.56D, -0.22D);
    }

    public void handleControlAction(int action, Player player) {
        if (!isFullyAssembled()) return;
        if (action == GeoDrillerControlPayload.ACTION_TOGGLE_DRILL) {
            boolean next = !isDrillArmed();
            entityData.set(DATA_DRILL_ARMED, next);
            level().playSound(null, getX(), getY() + 1.1D, getZ(),
                    next ? SoundEvents.PISTON_EXTEND : SoundEvents.PISTON_CONTRACT, SoundSource.PLAYERS, 1.0F, next ? 0.85F : 0.72F);
        } else if (action == GeoDrillerControlPayload.ACTION_CYCLE_PITCH) {
            int next = (getPitchMode() + 1) % 3;
            entityData.set(DATA_PITCH_MODE, next);
            level().playSound(null, getX(), getY() + 1.1D, getZ(), SoundEvents.LEVER_CLICK, SoundSource.PLAYERS, 1.0F, 0.9F + next * 0.15F);
        } else if (action == GeoDrillerControlPayload.ACTION_TOGGLE_LIGHT) {
            boolean next = !isHeadlightOn();
            entityData.set(DATA_HEADLIGHT, next);
            level().playSound(null, getX(), getY() + 1.1D, getZ(), SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.PLAYERS, 0.9F, next ? 1.3F : 0.8F);
            if (!next && level() instanceof ServerLevel sl) clearHeadlight(sl);
        }
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        // 1. Modular outdoor assembly (3 parts: Chassis -> Drill -> Engine)
        if (!isFullyAssembled()) {
            if (stack.getItem() instanceof GeoDrillerPartItem partItem) {
                int bit = partItem.getPartBit();
                if ((getPartMask() & bit) == 0) {
                    if (!level().isClientSide() && level() instanceof ServerLevel sl) {
                        entityData.set(DATA_PARTS, getPartMask() | bit);
                        if (!player.getAbilities().instabuild) stack.shrink(1);
                        sl.playSound(null, getX(), getY() + 1.0D, getZ(), SoundEvents.ANVIL_USE, SoundSource.PLAYERS, 1.0F, 1.05F);
                        sl.sendParticles(ParticleTypes.CRIT, getX(), getY() + 1.2D, getZ(), 20, 0.8D, 0.6D, 0.8D, 0.08D);
                        if (isFullyAssembled()) {
                            player.displayClientMessage(Component.translatable("message.velorise_teyvat_bosses.geo_driller.complete")
                                    .withStyle(ChatFormatting.GOLD), true);
                        } else {
                            player.displayClientMessage(Component.translatable("message.velorise_teyvat_bosses.geo_driller.part_welded",
                                    getAssembledCount()).withStyle(ChatFormatting.YELLOW), true);
                        }
                    }
                    return InteractionResult.sidedSuccess(level().isClientSide());
                }
            }
            if (player.isSecondaryUseActive() && stack.getItem() instanceof PickaxeItem && !isVehicle()) {
                if (!level().isClientSide() && level() instanceof ServerLevel sl) {
                    dropPartsAndCargo(sl);
                    discard();
                }
                return InteractionResult.sidedSuccess(level().isClientSide());
            }
            return InteractionResult.PASS;
        }

        // 2. Attach 1st or 2nd Vanilla Chest directly onto rear deck (Single -> Double Chest!)
        if (stack.is(Items.CHEST) && getChestCount() < 2) {
            if (!level().isClientSide() && level() instanceof ServerLevel sl) {
                int next = getChestCount() + 1;
                entityData.set(DATA_CHEST_COUNT, next);
                if (!player.getAbilities().instabuild) stack.shrink(1);
                sl.playSound(null, getX(), getY() + 1.0D, getZ(), SoundEvents.WOOD_PLACE, SoundSource.PLAYERS, 1.2F, 0.95F);
                sl.sendParticles(ParticleTypes.HAPPY_VILLAGER, getX(), getY() + 1.4D, getZ(), 10, 0.6D, 0.4D, 0.6D, 0.02D);
                String key = next == 1
                        ? "message.velorise_teyvat_bosses.geo_driller.chest_single"
                        : "message.velorise_teyvat_bosses.geo_driller.chest_double";
                player.displayClientMessage(Component.translatable(key).withStyle(ChatFormatting.GREEN), true);
            }
            return InteractionResult.sidedSuccess(level().isClientSide());
        }

        // 3. Upgrade Bore Head using Vanilla Enchanted Books (Fortune, Silk Touch, Efficiency, Sweeping Edge, Unbreaking)
        if (stack.is(Items.ENCHANTED_BOOK)) {
            if (!level().isClientSide() && level() instanceof ServerLevel sl) {
                if (applyEnchantedBook(sl, stack, player)) {
                    if (!player.getAbilities().instabuild) stack.shrink(1);
                    return InteractionResult.CONSUME;
                }
            }
            return InteractionResult.sidedSuccess(level().isClientSide());
        }

        // 4. Shift + RMB: dismantle with Pickaxe, or open attached Vanilla Chest (27/54 slots)
        if (player.isSecondaryUseActive()) {
            if (stack.getItem() instanceof PickaxeItem && !isVehicle()) {
                if (!level().isClientSide() && level() instanceof ServerLevel sl) {
                    clearHeadlight(sl);
                    dropPartsAndCargo(sl);
                    discard();
                }
                return InteractionResult.sidedSuccess(level().isClientSide());
            }
            if (getChestCount() > 0) {
                if (!level().isClientSide()) {
                    int count = getChestCount();
                    Component title = Component.translatable("entity.velorise_teyvat_bosses.geo_driller");
                    player.openMenu(new SimpleMenuProvider((id, inv, p) -> count >= 2
                            ? ChestMenu.sixRows(id, inv, cargo)
                            : ChestMenu.threeRows(id, inv, cargo), title));
                    level().playSound(null, getX(), getY() + 1.0D, getZ(), SoundEvents.CHEST_OPEN, SoundSource.PLAYERS, 0.85F, 1.0F);
                }
                return InteractionResult.sidedSuccess(level().isClientSide());
            }
            return InteractionResult.PASS;
        }

        // 5. Fuel recharge & Hull repair
        int fuelAdd = stack.is(Items.COAL) || stack.is(Items.CHARCOAL) ? 100
                : stack.is(Items.REDSTONE_BLOCK) ? 300
                : stack.is(Items.COAL_BLOCK) ? 900 : 0;
        if (fuelAdd > 0 && getFuel() < MAX_FUEL) {
            if (!level().isClientSide()) {
                int nextFuel = Math.min(MAX_FUEL, getFuel() + fuelAdd);
                entityData.set(DATA_FUEL, nextFuel);
                if (!player.getAbilities().instabuild) stack.shrink(1);
                level().playSound(null, getX(), getY() + 1.0D, getZ(), SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, 0.9F, 1.1F);
                player.displayClientMessage(Component.translatable("message.velorise_teyvat_bosses.geo_driller.recharged",
                        nextFuel, MAX_FUEL).withStyle(ChatFormatting.GOLD), true);
            }
            return InteractionResult.sidedSuccess(level().isClientSide());
        }
        float healAdd = stack.is(Items.IRON_INGOT) ? 45.0F : stack.is(Items.IRON_BLOCK) ? 180.0F : 0.0F;
        if (healAdd > 0.0F && getHealth() < getMaxHealth()) {
            if (!level().isClientSide()) {
                heal(healAdd);
                if (!player.getAbilities().instabuild) stack.shrink(1);
                level().playSound(null, getX(), getY() + 1.0D, getZ(), SoundEvents.IRON_GOLEM_REPAIR, SoundSource.PLAYERS, 1.0F, 1.05F);
                player.displayClientMessage(Component.translatable("message.velorise_teyvat_bosses.geo_driller.repaired",
                        (int) getHealth(), (int) getMaxHealth()).withStyle(ChatFormatting.AQUA), true);
            }
            return InteractionResult.sidedSuccess(level().isClientSide());
        }

        // 6. Mount & pilot
        if (!level().isClientSide() && !isVehicle()) {
            player.startRiding(this);
        }
        return InteractionResult.sidedSuccess(level().isClientSide());
    }

    private boolean applyEnchantedBook(ServerLevel sl, ItemStack book, Player player) {
        ItemEnchantments stored = EnchantmentHelper.getEnchantmentsForCrafting(book);
        if (stored.isEmpty()) return false;
        var reg = sl.registryAccess().registryOrThrow(Registries.ENCHANTMENT);
        boolean upgraded = false;

        int fort = stored.getLevel(reg.getHolderOrThrow(Enchantments.FORTUNE));
        if (fort > getFortuneLevel()) {
            entityData.set(DATA_FORTUNE, Math.min(3, fort));
            entityData.set(DATA_SILK, false); // Fortune & Silk Touch mutually exclusive
            upgraded = true;
        }
        int silk = stored.getLevel(reg.getHolderOrThrow(Enchantments.SILK_TOUCH));
        if (silk > 0 && !hasSilkTouch()) {
            entityData.set(DATA_SILK, true);
            entityData.set(DATA_FORTUNE, 0);
            upgraded = true;
        }
        int eff = stored.getLevel(reg.getHolderOrThrow(Enchantments.EFFICIENCY));
        if (eff > getEfficiencyLevel()) {
            entityData.set(DATA_EFFICIENCY, Math.min(5, eff));
            upgraded = true;
        }
        int sweep = stored.getLevel(reg.getHolderOrThrow(Enchantments.SWEEPING_EDGE));
        if (sweep > getSweepingLevel()) {
            entityData.set(DATA_SWEEPING, Math.min(3, sweep));
            upgraded = true;
        }
        int unb = stored.getLevel(reg.getHolderOrThrow(Enchantments.UNBREAKING));
        if (unb > getUnbreakingLevel()) {
            entityData.set(DATA_UNBREAKING, Math.min(3, unb));
            upgraded = true;
        }

        if (upgraded) {
            sl.playSound(null, getX(), getY() + 1.2D, getZ(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 1.2F, 1.05F);
            sl.sendParticles(ParticleTypes.ENCHANT, getX(), getY() + 1.6D, getZ(), 28, 0.9D, 0.6D, 0.9D, 0.15D);
            player.displayClientMessage(Component.literal("Bore Head Upgraded! Bore: " + getBoreLabel()
                    + (hasSilkTouch() ? " | Silk Touch" : (getFortuneLevel() > 0 ? " | Fortune " + getFortuneLevel() : ""))
                    + (getEfficiencyLevel() > 0 ? " | Eff " + getEfficiencyLevel() : "")
                    + (getUnbreakingLevel() > 0 ? " | Unb " + getUnbreakingLevel() : "")
            ).withStyle(ChatFormatting.LIGHT_PURPLE), true);
        }
        return upgraded;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!level().isClientSide() && !isVehicle() && source.getEntity() instanceof Player p
                && p.isSecondaryUseActive() && p.getMainHandItem().getItem() instanceof PickaxeItem
                && level() instanceof ServerLevel sl) {
            clearHeadlight(sl);
            dropPartsAndCargo(sl);
            discard();
            return true;
        }
        return super.hurt(source, amount);
    }

    @Override
    public void die(DamageSource source) {
        if (!level().isClientSide() && level() instanceof ServerLevel sl) {
            clearHeadlight(sl);
            dropPartsAndCargo(sl);
        }
        super.die(source);
    }

    @Override
    public void remove(RemovalReason reason) {
        if (!level().isClientSide() && level() instanceof ServerLevel sl) {
            clearHeadlight(sl);
        }
        super.remove(reason);
    }

    private void dropPartsAndCargo(ServerLevel sl) {
        int mask = getPartMask();
        if ((mask & PART_CHASSIS) != 0 && TeyvatBossesItems.geoDrillerChassis() != null)
            spawnAtLocation(new ItemStack(TeyvatBossesItems.geoDrillerChassis()));
        if ((mask & PART_DRILL) != 0 && TeyvatBossesItems.geoDrillerDrill() != null)
            spawnAtLocation(new ItemStack(TeyvatBossesItems.geoDrillerDrill()));
        if ((mask & PART_ENGINE) != 0 && TeyvatBossesItems.geoDrillerEngine() != null)
            spawnAtLocation(new ItemStack(TeyvatBossesItems.geoDrillerEngine()));
        if (getChestCount() > 0) {
            spawnAtLocation(new ItemStack(Items.CHEST, getChestCount()));
            Containers.dropContents(sl, blockPosition(), cargo);
        }
    }

    @Override
    public void tick() {
        prevWheelSpin = wheelSpin;
        prevDrillSpin = drillSpin;
        prevDriveSpool = driveSpool;
        prevDrillSpool = drillSpool;
        prevLeverLAngle = leverLAngle;
        prevLeverRAngle = leverRAngle;

        super.tick();

        // Update smooth mechanical spool-up & lever angles on BOTH sides so client & server match
        float thr = getThrottleInput();
        boolean hasFuel = getFuel() > 0;
        boolean wantsDrive = isVehicle() && isFullyAssembled() && hasFuel && Math.abs(thr) > 0.05F;
        boolean wantsDrill = isDrilling();

        // Heavy mechanical inertia: ~0.65s (13 ticks) engine spool-up before full drive torque!
        driveSpool = wantsDrive ? Math.min(1.0F, driveSpool + 0.08F) : Math.max(0.0F, driveSpool - 0.12F);
        drillSpool = wantsDrill ? Math.min(1.0F, drillSpool + 0.075F) : Math.max(0.0F, drillSpool - 0.10F);

        // Left lever = forward/reverse throttle; Right lever = drill engage + pitch angle
        float targetLeverL = wantsDrive ? Mth.clamp(thr, -1.0F, 1.0F) * 0.45F : 0.0F;
        float targetLeverR = (isDrillArmed() ? 0.38F : -0.18F)
                + (getPitchMode() == PITCH_DOWN ? 0.18F : (getPitchMode() == PITCH_UP ? -0.18F : 0.0F));
        leverLAngle = Mth.lerp(0.26F, leverLAngle, targetLeverL);
        leverRAngle = Mth.lerp(0.26F, leverRAngle, targetLeverR);

        // True signed displacement along facing vector for silky partial-tick wheel & tread rotation!
        Vec3 fwd = Vec3.directionFromRotation(0.0F, getYRot()).normalize();
        double dx = getX() - xo;
        double dz = getZ() - zo;
        double signedDist = dx * fwd.x + dz * fwd.z;
        wheelSpin += (float) (signedDist * 3.4D);
        drillSpin += drillSpool * 0.78F + (isVehicle() && hasFuel ? 0.04F : 0.0F);

        if (level().isClientSide()) {
            return;
        }

        ServerLevel sl = (ServerLevel) level();
        if (mineCooldown > 0) mineCooldown--;

        if (isAlive() && isFullyAssembled() && getControllingPassenger() instanceof Player pilot) {
            float pFwd = pilot.zza;
            float pSide = pilot.xxa;
            entityData.set(DATA_THROTTLE, pFwd);
            entityData.set(DATA_STEER, pSide);

            // Engine rumble while spooling up or drilling
            if ((wantsDrive || wantsDrill) && tickCount % 16 == 0) {
                if (TeyvatBossesSounds.mechEngineHum() != null) {
                    sl.playSound(null, getX(), getY() + 1.0D, getZ(), TeyvatBossesSounds.mechEngineHum(), SoundSource.PLAYERS, 0.55F, 0.72F + driveSpool * 0.28F);
                }
            }

            // Headlight (consumes extra fuel gently: -1 every 30 ticks)
            if (isHeadlightOn() && hasFuel) {
                updateHeadlight(sl, fwd);
                if (tickCount % 30 == 0) consumeFuel(1);
            } else {
                clearHeadlight(sl);
            }

            // Active drilling: bore tunnel, auto-bridge floor, seal lava, damage mobs in front!
            if (wantsDrill && drillSpool >= 0.45F) {
                if (mineCooldown <= 0) {
                    mineAndBridge(sl, pilot, fwd);
                    mineCooldown = Math.max(2, 5 - getEfficiencyLevel());
                }
                if (tickCount % 6 == 0) {
                    damageEntitiesInDrill(sl, pilot, fwd);
                }
            }

            if (wantsDrive && tickCount % 40 == 0) {
                consumeFuel(1);
            }
        } else {
            if (getThrottleInput() != 0.0F) entityData.set(DATA_THROTTLE, 0.0F);
            if (getSteerInput() != 0.0F) entityData.set(DATA_STEER, 0.0F);
            if (isHeadlightOn()) {
                entityData.set(DATA_HEADLIGHT, false);
                clearHeadlight(sl);
            }
        }
    }

    private void consumeFuel(int amount) {
        int unb = getUnbreakingLevel();
        if (unb > 0 && random.nextInt(unb + 1) != 0) return;
        entityData.set(DATA_FUEL, Math.max(0, getFuel() - amount));
    }

    @Override
    public void travel(Vec3 travelVector) {
        if (isAlive() && isFullyAssembled() && getControllingPassenger() instanceof Player pilot) {
            float fwd = pilot.zza;
            float side = pilot.xxa;
            if (Math.abs(side) > 0.05F) {
                float turn = -side * 3.6F;
                this.setYRot(this.getYRot() + turn);
                this.yRotO = this.getYRot();
                this.yBodyRot = this.getYRot();
                this.yHeadRot = this.getYRot();
            }
            boolean hasFuel = getFuel() > 0;
            // Heavy spool-up: engine rumbles for ~0.5s (driveSpool < 0.35) before rolling smoothly up to cruise speed!
            float torque = Mth.clamp((driveSpool - 0.30F) / 0.70F, 0.0F, 1.0F);
            if (hasFuel && Math.abs(fwd) > 0.05F && torque > 0.02F) {
                float baseSpd = fwd > 0 ? 0.21F : 0.12F;
                this.setSpeed(baseSpd * torque);
                super.travel(new Vec3(0.0D, travelVector.y, fwd));
            } else {
                setDeltaMovement(getDeltaMovement().x * 0.65D, getDeltaMovement().y, getDeltaMovement().z * 0.65D);
                super.travel(Vec3.ZERO);
            }
            return;
        }
        super.travel(travelVector);
    }

    private ItemStack buildVirtualPickaxe(ServerLevel sl) {
        ItemStack pick = new ItemStack(Items.NETHERITE_PICKAXE);
        var reg = sl.registryAccess().registryOrThrow(Registries.ENCHANTMENT);
        if (hasSilkTouch()) {
            pick.enchant(reg.getHolderOrThrow(Enchantments.SILK_TOUCH), 1);
        } else if (getFortuneLevel() > 0) {
            pick.enchant(reg.getHolderOrThrow(Enchantments.FORTUNE), getFortuneLevel());
        }
        return pick;
    }

    private void mineAndBridge(ServerLevel sl, Player pilot, Vec3 fwd) {
        Vec3 right = new Vec3(-fwd.z, 0.0D, fwd.x).normalize();
        ItemStack virtualPick = buildVirtualPickaxe(sl);

        // Sweeping Edge expands bore dimensions: 0 -> 3x3, I -> 4x3, II+ -> 5x4
        int sw = getSweepingLevel();
        int minDx = sw >= 2 ? -2 : -1;
        int maxDx = sw >= 1 ? 2 : 1;
        int height = sw >= 2 ? 4 : 3;

        int pitch = getPitchMode();
        int yShift = pitch == PITCH_DOWN ? -1 : (pitch == PITCH_UP ? 1 : 0);

        boolean minedAny = false;
        double[] depths = {1.75D, 2.50D};
        for (double depth : depths) {
            Vec3 slice = position().add(fwd.scale(depth));
            int baseY = Mth.floor(getY()) + yShift;

            // 1. Mine bore cross-section
            for (int dy = 0; dy < height; dy++) {
                for (int dx = minDx; dx <= maxDx; dx++) {
                    BlockPos bp = BlockPos.containing(slice.x + right.x * dx, baseY + dy + 0.1D, slice.z + right.z * dx);
                    BlockState state = sl.getBlockState(bp);
                    if (state.getFluidState().is(FluidTags.LAVA)) {
                        tryPlaceBridgeBlock(sl, bp); // Seal lava pockets directly ahead!
                        continue;
                    }
                    if (state.isAir() || !state.getFluidState().isEmpty() || state.hasBlockEntity()) continue;
                    float hardness = state.getDestroySpeed(sl, bp);
                    if (hardness < 0.0F || hardness > 50.0F) continue;

                    List<ItemStack> drops = Block.getDrops(state, sl, bp, null, pilot, virtualPick);
                    sl.setBlock(bp, Blocks.AIR.defaultBlockState(), 3);
                    sl.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, state),
                            bp.getX() + 0.5D, bp.getY() + 0.5D, bp.getZ() + 0.5D, 5, 0.3D, 0.3D, 0.3D, 0.05D);
                    for (ItemStack drop : drops) storeOrDrop(sl, drop);
                    minedAny = true;
                }
            }

            // 2. Auto-bridge floor underneath treads (and build step-up ramp when PITCH_UP!)
            for (int dx = -1; dx <= 1; dx++) {
                BlockPos floorPos = BlockPos.containing(slice.x + right.x * dx, baseY - 0.9D, slice.z + right.z * dx);
                BlockState floorState = sl.getBlockState(floorPos);
                if (floorState.isAir() || !floorState.getFluidState().isEmpty() || floorState.canBeReplaced()) {
                    tryPlaceBridgeBlock(sl, floorPos);
                }
            }
        }

        if (minedAny) {
            consumeFuel(1);
            Vec3 tip = position().add(fwd.scale(2.2D));
            sl.playSound(null, tip.x, getY() + 1.0D, tip.z, SoundEvents.STONE_BREAK, SoundSource.PLAYERS, 0.9F, 0.72F);
        }
    }

    private void tryPlaceBridgeBlock(ServerLevel sl, BlockPos pos) {
        // Pull Cobblestone / Cobbled Deepslate / Stone / Deepslate / Dirt from cargo first
        int slots = getChestCount() >= 2 ? 54 : (getChestCount() == 1 ? 27 : 0);
        for (int i = 0; i < slots; i++) {
            ItemStack s = cargo.getItem(i);
            if (!s.isEmpty() && s.getItem() instanceof BlockItem bi && isBridgeMaterial(bi.getBlock())) {
                sl.setBlock(pos, bi.getBlock().defaultBlockState(), 3);
                s.shrink(1);
                return;
            }
        }
        // Fallback: synthesize compacted Cobblestone from crushed rock tailings
        sl.setBlock(pos, Blocks.COBBLESTONE.defaultBlockState(), 3);
    }

    private static boolean isBridgeMaterial(Block b) {
        return b == Blocks.COBBLESTONE || b == Blocks.COBBLED_DEEPSLATE || b == Blocks.STONE
                || b == Blocks.DEEPSLATE || b == Blocks.DIRT || b == Blocks.NETHERRACK || b == Blocks.TUFF;
    }

    private void damageEntitiesInDrill(ServerLevel sl, Player pilot, Vec3 fwd) {
        Vec3 tip = position().add(fwd.scale(2.2D)).add(0.0D, 1.0D, 0.0D);
        AABB box = new AABB(tip.x - 1.4D, tip.y - 1.1D, tip.z - 1.4D, tip.x + 1.4D, tip.y + 1.3D, tip.z + 1.4D);
        for (LivingEntity e : sl.getEntitiesOfClass(LivingEntity.class, box)) {
            if (e == this || e == pilot || !e.isAlive()) continue;
            if (e.hurt(sl.damageSources().mobAttack(this), 10.0F)) {
                e.setDeltaMovement(fwd.x * 0.65D, 0.32D, fwd.z * 0.65D);
                e.hurtMarked = true;
                sl.playSound(null, e.getX(), e.getY() + 0.8D, e.getZ(), SoundEvents.ANVIL_LAND, SoundSource.PLAYERS, 0.55F, 1.35F);
                sl.sendParticles(ParticleTypes.CRIT, e.getX(), e.getY() + 0.8D, e.getZ(), 10, 0.35D, 0.35D, 0.35D, 0.12D);
            }
        }
    }

    private void updateHeadlight(ServerLevel sl, Vec3 fwd) {
        BlockPos desired = BlockPos.containing(getX() + fwd.x * 3.0D, getY() + 1.5D, getZ() + fwd.z * 3.0D);
        if (desired.equals(activeLightPos)) return;
        clearHeadlight(sl);
        if (sl.getBlockState(desired).isAir()) {
            sl.setBlock(desired, Blocks.LIGHT.defaultBlockState().setValue(LightBlock.LEVEL, 15), 2);
            activeLightPos = desired;
        }
    }

    private void clearHeadlight(ServerLevel sl) {
        if (activeLightPos != null) {
            if (sl.getBlockState(activeLightPos).is(Blocks.LIGHT)) {
                sl.setBlock(activeLightPos, Blocks.AIR.defaultBlockState(), 2);
            }
            activeLightPos = null;
        }
    }

    private void storeOrDrop(ServerLevel sl, ItemStack stack) {
        if (stack.isEmpty()) return;
        int slots = getChestCount() >= 2 ? 54 : (getChestCount() == 1 ? 27 : 0);
        if (slots > 0) {
            for (int i = 0; i < slots && !stack.isEmpty(); i++) {
                ItemStack cur = cargo.getItem(i);
                if (!cur.isEmpty() && ItemStack.isSameItemSameComponents(cur, stack) && cur.getCount() < cur.getMaxStackSize()) {
                    int add = Math.min(stack.getCount(), cur.getMaxStackSize() - cur.getCount());
                    cur.grow(add);
                    stack.shrink(add);
                }
            }
            for (int i = 0; i < slots && !stack.isEmpty(); i++) {
                if (cargo.getItem(i).isEmpty()) {
                    cargo.setItem(i, stack.copy());
                    stack.setCount(0);
                }
            }
        }
        if (!stack.isEmpty()) {
            spawnAtLocation(stack);
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("Parts", getPartMask());
        tag.putInt("ChestCount", getChestCount());
        tag.putInt("Fuel", getFuel());
        tag.putBoolean("DrillArmed", isDrillArmed());
        tag.putInt("PitchMode", getPitchMode());
        tag.putInt("Fortune", getFortuneLevel());
        tag.putBoolean("SilkTouch", hasSilkTouch());
        tag.putInt("Efficiency", getEfficiencyLevel());
        tag.putInt("Sweeping", getSweepingLevel());
        tag.putInt("Unbreaking", getUnbreakingLevel());
        ListTag items = new ListTag();
        for (int i = 0; i < cargo.getContainerSize(); i++) {
            ItemStack s = cargo.getItem(i);
            if (!s.isEmpty()) {
                CompoundTag itemTag = new CompoundTag();
                itemTag.putByte("Slot", (byte) i);
                items.add(s.save(this.registryAccess(), itemTag));
            }
        }
        tag.put("Cargo", items);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Parts")) entityData.set(DATA_PARTS, tag.getInt("Parts"));
        if (tag.contains("ChestCount")) entityData.set(DATA_CHEST_COUNT, Mth.clamp(tag.getInt("ChestCount"), 0, 2));
        if (tag.contains("Fuel")) entityData.set(DATA_FUEL, Mth.clamp(tag.getInt("Fuel"), 0, MAX_FUEL));
        if (tag.contains("DrillArmed")) entityData.set(DATA_DRILL_ARMED, tag.getBoolean("DrillArmed"));
        if (tag.contains("PitchMode")) entityData.set(DATA_PITCH_MODE, Mth.clamp(tag.getInt("PitchMode"), 0, 2));
        if (tag.contains("Fortune")) entityData.set(DATA_FORTUNE, Mth.clamp(tag.getInt("Fortune"), 0, 3));
        if (tag.contains("SilkTouch")) entityData.set(DATA_SILK, tag.getBoolean("SilkTouch"));
        if (tag.contains("Efficiency")) entityData.set(DATA_EFFICIENCY, Mth.clamp(tag.getInt("Efficiency"), 0, 5));
        if (tag.contains("Sweeping")) entityData.set(DATA_SWEEPING, Mth.clamp(tag.getInt("Sweeping"), 0, 3));
        if (tag.contains("Unbreaking")) entityData.set(DATA_UNBREAKING, Mth.clamp(tag.getInt("Unbreaking"), 0, 3));
        cargo.clearContent();
        if (tag.contains("Cargo", Tag.TAG_LIST)) {
            ListTag items = tag.getList("Cargo", Tag.TAG_COMPOUND);
            for (int i = 0; i < items.size(); i++) {
                CompoundTag itemTag = items.getCompound(i);
                int slot = itemTag.getByte("Slot") & 255;
                if (slot < cargo.getContainerSize()) {
                    cargo.setItem(slot, ItemStack.parse(this.registryAccess(), itemTag).orElse(ItemStack.EMPTY));
                }
            }
        }
    }
}

package com.velorise.teyvatbosses.entity;

import net.minecraft.world.level.GameRules;
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
    public static final int MAX_FUEL     = 900;

    public static final int PITCH_LEVEL = 0;
    public static final int PITCH_DOWN  = 1;
    public static final int PITCH_UP    = 2;

    private static final EntityDataAccessor<Integer> DATA_PARTS =
            SynchedEntityData.defineId(GeoDrillerEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_CHEST_COUNT =
            SynchedEntityData.defineId(GeoDrillerEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_FUEL =
            SynchedEntityData.defineId(GeoDrillerEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_CAPACITY_LEVEL =
            SynchedEntityData.defineId(GeoDrillerEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_CONSERVATION_LEVEL =
            SynchedEntityData.defineId(GeoDrillerEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_DRILL_ARMED =
            SynchedEntityData.defineId(GeoDrillerEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_PITCH_MODE =
            SynchedEntityData.defineId(GeoDrillerEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_HEADLIGHT =
            SynchedEntityData.defineId(GeoDrillerEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_PAVE_ENABLED =
            SynchedEntityData.defineId(GeoDrillerEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_SURGE_TICKS =
            SynchedEntityData.defineId(GeoDrillerEntity.class, EntityDataSerializers.INT);
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
    private int surgeCooldown = 0;

    // Client + Server smooth mechanical inertia & partialTick animation state
    public float wheelSpinL = 0.0F, prevWheelSpinL = 0.0F;
    public float wheelSpinR = 0.0F, prevWheelSpinR = 0.0F;
    public float drillSpin = 0.0F, prevDrillSpin = 0.0F;
    public float driveSpool = 0.0F, prevDriveSpool = 0.0F;
    public float drillSpool = 0.0F, prevDrillSpool = 0.0F;
    public float leverLAngle = 0.0F, prevLeverLAngle = 0.0F;
    public float leverRAngle = 0.0F, prevLeverRAngle = 0.0F;
    public float flapperAngle = 0.0F, prevFlapperAngle = 0.0F;
    private double lastTrackX = 0.0D, lastTrackZ = 0.0D;
    private boolean hasLastTrackPos = false;

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
        builder.define(DATA_FUEL, 0);
        builder.define(DATA_CAPACITY_LEVEL, 0);
        builder.define(DATA_CONSERVATION_LEVEL, 0);
        builder.define(DATA_DRILL_ARMED, false); // Default OFF so driving near base never breaks walls accidentally
        builder.define(DATA_PITCH_MODE, PITCH_LEVEL);
        builder.define(DATA_HEADLIGHT, false);
        builder.define(DATA_PAVE_ENABLED, false);
        builder.define(DATA_SURGE_TICKS, 0);
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
        entityData.set(DATA_FUEL, 0);
    }

    public int getPartMask() { return entityData.get(DATA_PARTS); }
    public boolean isFullyAssembled() { return (getPartMask() & PART_ALL) == PART_ALL; }
    public int getAssembledCount() { return Integer.bitCount(getPartMask() & PART_ALL); }
    public int getChestCount() { return entityData.get(DATA_CHEST_COUNT); }
    public int getFuel() { return entityData.get(DATA_FUEL); }
    public int getCapacityLevel() { return entityData.get(DATA_CAPACITY_LEVEL); }
    public int getConservationLevel() { return entityData.get(DATA_CONSERVATION_LEVEL); }
    public int getMaxFuel() { return DeviceEnergyHelper.maxIntFuelForLevel(getCapacityLevel()); }

    public void setCoreEnchants(int capacityLevel, int conservationLevel) {
        entityData.set(DATA_CAPACITY_LEVEL, Mth.clamp(capacityLevel, 0, 5));
        entityData.set(DATA_CONSERVATION_LEVEL, Mth.clamp(conservationLevel, 0, 5));
    }
    public boolean isDrillArmed() { return entityData.get(DATA_DRILL_ARMED); }
    public boolean isDrilling() { return isDrillArmed() && isVehicle() && getFuel() > 0; }
    public int getPitchMode() { return entityData.get(DATA_PITCH_MODE); }
    public boolean isHeadlightOn() { return entityData.get(DATA_HEADLIGHT); }
    public boolean isPaveEnabled() { return entityData.get(DATA_PAVE_ENABLED); }
    public int getSurgeTicks() { return entityData.get(DATA_SURGE_TICKS); }
    public boolean isSurging() { return getSurgeTicks() > 0; }
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
        // Centered cleanly on the cockpit seat cushion (+Z is forward) with clear eye-line over the hood
        return new Vec3(0.0D, 0.78D, 0.24D);
    }

    @Override
    protected void positionRider(Entity passenger, MoveFunction callback) {
        if (!hasPassenger(passenger)) return;
        // Exact trigonometric seat lock matching GeoDrillerModel cockpit seat (+0.24 blocks forward along hull yaw)
        float rad = this.getYRot() * Mth.DEG_TO_RAD;
        double seatFwd = 0.24D;
        double px = this.getX() - Mth.sin(rad) * seatFwd;
        double py = this.getY() + 0.78D + passenger.getVehicleAttachmentPoint(this).y;
        double pz = this.getZ() + Mth.cos(rad) * seatFwd;
        callback.accept(passenger, px, py, pz);
        clampPassengerBody(passenger);
    }

    @Override
    public void onPassengerTurned(Entity passenger) {
        clampPassengerBody(passenger);
    }

    private void clampPassengerBody(Entity passenger) {
        // Lock seated torso posture to the driller hull without clamping 3rd-person camera orbit
        passenger.setYBodyRot(this.getYRot());
        if (passenger instanceof net.minecraft.world.entity.LivingEntity le) {
            le.yBodyRotO = this.yRotO;
        }
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
        } else if (action == GeoDrillerControlPayload.ACTION_TOGGLE_PAVE) {
            boolean next = !isPaveEnabled();
            entityData.set(DATA_PAVE_ENABLED, next);
            level().playSound(null, getX(), getY() + 1.1D, getZ(), SoundEvents.STONE_PLACE, SoundSource.PLAYERS, 0.9F, next ? 1.15F : 0.75F);
        } else if (action == GeoDrillerControlPayload.ACTION_DRILL_SURGE) {
            if (surgeCooldown <= 0 && getFuel() >= 4 && !isSurging()) {
                entityData.set(DATA_SURGE_TICKS, 12);
                surgeCooldown = 40;
                driveSpool = 1.0F;
                drillSpool = 1.0F;
                consumeFuel(4);
                level().playSound(null, getX(), getY() + 1.1D, getZ(),
                        TeyvatBossesSounds.drillBoreSpin() != null ? TeyvatBossesSounds.drillBoreSpin() : SoundEvents.PISTON_EXTEND,
                        SoundSource.PLAYERS, 1.0F, 1.25F);
                level().playSound(null, getX(), getY() + 1.0D, getZ(), SoundEvents.PISTON_EXTEND, SoundSource.PLAYERS, 0.85F, 0.72F);
            }
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

        // 5. Redstone-only Fuel recharge (Dust +11, Redstone Block +100, Condensed Redstone Block +900) & Hull repair
        int fuelAdd = DeviceEnergyHelper.getRedstoneIntFuelValue(stack);
        int maxFuel = getMaxFuel();
        if (fuelAdd > 0 && getFuel() < maxFuel) {
            if (!level().isClientSide()) {
                int nextFuel = Math.min(maxFuel, getFuel() + fuelAdd);
                entityData.set(DATA_FUEL, nextFuel);
                if (!player.getAbilities().instabuild) stack.shrink(1);
                level().playSound(null, getX(), getY() + 1.0D, getZ(), SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, 0.9F, 1.1F);
                player.displayClientMessage(Component.translatable("message.velorise_teyvat_bosses.geo_driller.recharged",
                        nextFuel, maxFuel).withStyle(ChatFormatting.GOLD), true);
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
        if ((mask & PART_CHASSIS) != 0 && TeyvatBossesItems.geoDrillerChassis() != null) {
            ItemStack chassisStack = new ItemStack(TeyvatBossesItems.geoDrillerChassis());
            DeviceEnergyHelper.applyCoreEnchants(sl, chassisStack, getCapacityLevel(), getConservationLevel());
            spawnAtLocation(chassisStack);
        }
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
        prevWheelSpinL = wheelSpinL;
        prevWheelSpinR = wheelSpinR;
        prevDrillSpin = drillSpin;
        prevDriveSpool = driveSpool;
        prevDrillSpool = drillSpool;
        prevLeverLAngle = leverLAngle;
        prevLeverRAngle = leverRAngle;
        prevFlapperAngle = flapperAngle;

        super.tick();
        this.yBodyRot = this.getYRot();
        this.yBodyRotO = this.yRotO;
        this.yHeadRot = this.getYRot();
        this.yHeadRotO = this.yRotO;

        // Update smooth mechanical spool-up & lever angles on BOTH sides so client & server match
        float thr = getThrottleInput();
        float steer = getSteerInput();
        boolean hasFuel = getFuel() > 0;
        boolean engineRunning = isVehicle() && isFullyAssembled() && hasFuel;
        boolean surging = isSurging();
        boolean wantsDrive = engineRunning && (Math.abs(thr) > 0.05F || surging);
        boolean wantsDrill = isDrilling() || (engineRunning && surging);

        // Heavy mechanical inertia: ~0.65s (13 ticks) engine spool-up before full drive torque
        driveSpool = wantsDrive ? Math.min(1.0F, driveSpool + 0.08F) : Math.max(0.0F, driveSpool - 0.12F);
        drillSpool = wantsDrill ? Math.min(1.0F, drillSpool + 0.075F) : Math.max(0.0F, drillSpool - 0.10F);

        // Left lever = forward/reverse throttle; Right lever = drill engage + pitch angle
        float targetLeverL = wantsDrive ? Mth.clamp(thr, -1.0F, 1.0F) * 0.42F : 0.0F;
        float targetLeverR = (isDrillArmed() ? 0.34F : -0.16F)
                + (getPitchMode() == PITCH_DOWN ? 0.16F : (getPitchMode() == PITCH_UP ? -0.16F : 0.0F));
        leverLAngle = Mth.lerp(0.26F, leverLAngle, targetLeverL);
        leverRAngle = Mth.lerp(0.26F, leverRAngle, targetLeverR);

        // Smokestack rain-flapper cap lifts dynamically with engine load & exhaust pulses
        float engineLoad = Math.max(driveSpool, drillSpool);
        float targetFlapper = engineRunning
                ? (0.22F + engineLoad * 0.68F + Mth.sin(tickCount * (0.55F + engineLoad * 0.9F)) * (0.06F + engineLoad * 0.12F))
                : 0.0F;
        flapperAngle = Mth.lerp(0.32F, flapperAngle, targetFlapper);

        // True signed displacement tracked independently of super.tick() resetting xo/zo
        if (!hasLastTrackPos) {
            lastTrackX = getX();
            lastTrackZ = getZ();
            hasLastTrackPos = true;
        }
        double dx = getX() - lastTrackX;
        double dz = getZ() - lastTrackZ;
        lastTrackX = getX();
        lastTrackZ = getZ();
        Vec3 fwd = Vec3.directionFromRotation(0.0F, getYRot()).normalize();
        double signedDist = dx * fwd.x + dz * fwd.z;
        float rollDelta = (float) (signedDist * 4.2D);
        float tankSteerDelta = (engineRunning && Math.abs(steer) > 0.05F) ? steer * 0.24F : 0.0F;
        wheelSpinL += rollDelta - tankSteerDelta;
        wheelSpinR += rollDelta + tankSteerDelta;
        drillSpin += drillSpool * (surging ? 1.35F : 0.78F) + (engineRunning ? 0.04F : 0.0F);

        if (level().isClientSide()) {
            // Twin industrial smokestack exhaust plumes
            if (engineRunning && (getPartMask() & PART_ENGINE) != 0) {
                int interval = engineLoad > 0.2F ? 2 : 5;
                if (tickCount % interval == 0) {
                    Vec3 right = new Vec3(-fwd.z, 0.0D, fwd.x).normalize();
                    double backOffset = -0.48D;
                    double sideOffset = 0.52D;
                    double stackTopY = getY() + 1.82D;
                    for (int side = -1; side <= 1; side += 2) {
                        double sx = getX() + fwd.x * backOffset + right.x * (side * sideOffset);
                        double sz = getZ() + fwd.z * backOffset + right.z * (side * sideOffset);
                        level().addParticle(
                                engineLoad > 0.35F ? ParticleTypes.LARGE_SMOKE : ParticleTypes.SMOKE,
                                sx, stackTopY, sz,
                                (random.nextDouble() - 0.5D) * 0.015D,
                                0.055D + engineLoad * 0.05D,
                                (random.nextDouble() - 0.5D) * 0.015D
                        );
                        if (engineLoad > 0.55F && random.nextFloat() < 0.35F) {
                            level().addParticle(
                                    ParticleTypes.FLAME,
                                    sx, stackTopY - 0.04D, sz,
                                    (random.nextDouble() - 0.5D) * 0.01D,
                                    0.04D,
                                    (random.nextDouble() - 0.5D) * 0.01D
                            );
                        }
                    }
                }
            }
            return;
        }

        ServerLevel sl = (ServerLevel) level();
        if (mineCooldown > 0) mineCooldown--;
        if (surgeCooldown > 0) surgeCooldown--;
        if (getSurgeTicks() > 0) {
            entityData.set(DATA_SURGE_TICKS, getSurgeTicks() - 1);
        }

        if (isAlive() && isFullyAssembled() && getControllingPassenger() instanceof Player pilot) {
            float pFwd = pilot.zza;
            float pSide = pilot.xxa;
            entityData.set(DATA_THROTTLE, pFwd);
            entityData.set(DATA_STEER, pSide);

            // Custom diesel engine idle/drive rumble
            if (hasFuel && tickCount % 36 == 0) {
                var idleSfx = TeyvatBossesSounds.drillEngineIdle() != null
                        ? TeyvatBossesSounds.drillEngineIdle()
                        : TeyvatBossesSounds.mechEngineHum();
                if (idleSfx != null) {
                    sl.playSound(null, getX(), getY() + 1.0D, getZ(), idleSfx, SoundSource.PLAYERS,
                            wantsDrive ? 0.72F : 0.45F, 0.86F + driveSpool * 0.28F);
                }
            }
            // Custom bore-head spinning sound while drilling
            if (wantsDrill && tickCount % 32 == 0) {
                var boreSfx = TeyvatBossesSounds.drillBoreSpin();
                if (boreSfx != null) {
                    sl.playSound(null, getX() + fwd.x * 1.8D, getY() + 0.9D, getZ() + fwd.z * 1.8D,
                            boreSfx, SoundSource.PLAYERS, 0.78F, surging ? 1.22F : 1.0F);
                }
            }

            // Headlight (consumes extra fuel gently: -1 every 30 ticks)
            if (isHeadlightOn() && hasFuel) {
                updateHeadlight(sl, fwd);
                if (tickCount % 30 == 0) consumeFuel(1);
            } else {
                clearHeadlight(sl);
            }

            // Active drilling & RMB forward surge: bore tunnel, auto-pave when [B] ON, damage mobs in front
            if (surging) {
                if (mineCooldown <= 0) {
                    mineAndBridge(sl, pilot, fwd);
                    mineCooldown = 1;
                }
                if (tickCount % 3 == 0) {
                    damageEntitiesInDrill(sl, pilot, fwd, 16.0F, 1.15D);
                }
            } else if (wantsDrill && drillSpool >= 0.45F) {
                if (mineCooldown <= 0) {
                    mineAndBridge(sl, pilot, fwd);
                    mineCooldown = Math.max(2, 5 - getEfficiencyLevel());
                }
                if (tickCount % 6 == 0) {
                    damageEntitiesInDrill(sl, pilot, fwd, 10.0F, 0.65D);
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
        int scaled = DeviceEnergyHelper.scaleIntCost(amount, getConservationLevel(), this.random);
        entityData.set(DATA_FUEL, Math.max(0, getFuel() - scaled));
    }

    @Override
    public void travel(Vec3 travelVector) {
        if (isAlive() && isFullyAssembled() && getControllingPassenger() instanceof Player pilot) {
            float fwd = pilot.zza;
            float side = pilot.xxa;
            if (Math.abs(side) > 0.05F) {
                float turn = -side * 3.6F;
                this.setYRot(this.getYRot() + turn);
                this.yBodyRot = this.getYRot();
                this.yHeadRot = this.getYRot();
            }
            boolean hasFuel = getFuel() > 0;
            if (isSurging()) {
                // Rapid RMB forward drill surge (~4.5-5.5 blocks burst)
                float surgeFrac = Mth.clamp(getSurgeTicks() / 12.0F, 0.25F, 1.0F);
                this.setSpeed(0.56F * surgeFrac);
                super.travel(new Vec3(0.0D, travelVector.y, 1.0D));
            } else {
                // Heavy spool-up: engine rumbles briefly before rolling smoothly up to cruise speed
                float torque = Mth.clamp((driveSpool - 0.30F) / 0.70F, 0.0F, 1.0F);
                if (hasFuel && Math.abs(fwd) > 0.05F && torque > 0.02F) {
                    float baseSpd = fwd > 0 ? 0.21F : 0.12F;
                    this.setSpeed(baseSpd * torque);
                    super.travel(new Vec3(0.0D, travelVector.y, fwd));
                } else {
                    setDeltaMovement(getDeltaMovement().x * 0.65D, getDeltaMovement().y, getDeltaMovement().z * 0.65D);
                    super.travel(Vec3.ZERO);
                }
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
        if (!sl.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
            return;
        }
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
        int baseY = Mth.floor(getY()) + yShift;
        double[] depths = {1.45D, 2.15D, 2.80D};
        for (double depth : depths) {
            Vec3 slice = position().add(fwd.scale(depth));

            // 1. Mine bore cross-section cleanly across full width
            for (int dy = 0; dy < height; dy++) {
                for (double dx = minDx; dx <= maxDx; dx += 0.5D) {
                    BlockPos bp = BlockPos.containing(slice.x + right.x * dx, baseY + dy + 0.1D, slice.z + right.z * dx);
                    if (!sl.mayInteract(pilot, bp) || !pilot.mayInteract(sl, bp)) continue;
                    BlockState state = sl.getBlockState(bp);
                    if (state.getFluidState().is(FluidTags.LAVA)) {
                        tryPlaceBridgeBlock(sl, pilot, bp); // Seal lava pockets directly ahead
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
        }

        // 2. Thorough gap-free auto-paving ONLY when toggle [B] is ON and NOT drilling downward (PITCH_DOWN)
        if (isPaveEnabled() && pitch != PITCH_DOWN) {
            int floorY = baseY - 1;
            for (double depth = 0.35D; depth <= 2.65D; depth += 0.45D) {
                Vec3 slice = position().add(fwd.scale(depth));
                for (double dx = -1.25D; dx <= 1.25D; dx += 0.5D) {
                    BlockPos floorPos = BlockPos.containing(slice.x + right.x * dx, floorY + 0.1D, slice.z + right.z * dx);
                    if (!sl.mayInteract(pilot, floorPos) || !pilot.mayInteract(sl, floorPos)) continue;
                    BlockState floorState = sl.getBlockState(floorPos);
                    if (floorState.isAir() || !floorState.getFluidState().isEmpty() || floorState.canBeReplaced()) {
                        tryPlaceBridgeBlock(sl, pilot, floorPos);
                    }
                }
            }
        }

        if (minedAny) {
            consumeFuel(1);
            Vec3 tip = position().add(fwd.scale(2.2D));
            sl.playSound(null, tip.x, getY() + 1.0D, tip.z, SoundEvents.STONE_BREAK, SoundSource.PLAYERS, 0.9F, 0.72F);
        }
    }

    private void tryPlaceBridgeBlock(ServerLevel sl, Player pilot, BlockPos pos) {
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
        // Only synthesize fallback Cobblestone in Creative mode to prevent infinite cobblestone duplication in Survival
        if (pilot.getAbilities().instabuild) {
            sl.setBlock(pos, Blocks.COBBLESTONE.defaultBlockState(), 3);
        }
    }

    private static boolean isBridgeMaterial(Block b) {
        return b == Blocks.COBBLESTONE || b == Blocks.COBBLED_DEEPSLATE || b == Blocks.STONE
                || b == Blocks.DEEPSLATE || b == Blocks.DIRT || b == Blocks.NETHERRACK || b == Blocks.TUFF;
    }

    private void damageEntitiesInDrill(ServerLevel sl, Player pilot, Vec3 fwd, float damage, double knockback) {
        Vec3 tip = position().add(fwd.scale(2.2D)).add(0.0D, 1.0D, 0.0D);
        AABB box = new AABB(tip.x - 1.4D, tip.y - 1.1D, tip.z - 1.4D, tip.x + 1.4D, tip.y + 1.3D, tip.z + 1.4D);
        for (LivingEntity e : sl.getEntitiesOfClass(LivingEntity.class, box)) {
            if (e == this || e == pilot || !e.isAlive()) continue;
            if (e.hurt(sl.damageSources().mobAttack(this), damage)) {
                e.setDeltaMovement(fwd.x * knockback, 0.36D, fwd.z * knockback);
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
        tag.putInt("CoreCapacity", getCapacityLevel());
        tag.putInt("EnergyConservation", getConservationLevel());
        tag.putBoolean("DrillArmed", isDrillArmed());
        tag.putInt("PitchMode", getPitchMode());
        tag.putBoolean("PaveEnabled", isPaveEnabled());
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
        if (activeLightPos != null) {
            tag.putLong("ActiveLightPos", activeLightPos.asLong());
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Parts")) entityData.set(DATA_PARTS, tag.getInt("Parts"));
        if (tag.contains("ChestCount")) entityData.set(DATA_CHEST_COUNT, Mth.clamp(tag.getInt("ChestCount"), 0, 2));
        if (tag.contains("CoreCapacity")) entityData.set(DATA_CAPACITY_LEVEL, Mth.clamp(tag.getInt("CoreCapacity"), 0, 5));
        if (tag.contains("EnergyConservation")) entityData.set(DATA_CONSERVATION_LEVEL, Mth.clamp(tag.getInt("EnergyConservation"), 0, 5));
        if (tag.contains("Fuel")) entityData.set(DATA_FUEL, Mth.clamp(tag.getInt("Fuel"), 0, getMaxFuel()));
        if (tag.contains("DrillArmed")) entityData.set(DATA_DRILL_ARMED, tag.getBoolean("DrillArmed"));
        if (tag.contains("PitchMode")) entityData.set(DATA_PITCH_MODE, Mth.clamp(tag.getInt("PitchMode"), 0, 2));
        if (tag.contains("PaveEnabled")) entityData.set(DATA_PAVE_ENABLED, tag.getBoolean("PaveEnabled"));
        if (tag.contains("Fortune")) entityData.set(DATA_FORTUNE, Mth.clamp(tag.getInt("Fortune"), 0, 3));
        if (tag.contains("SilkTouch")) entityData.set(DATA_SILK, tag.getBoolean("SilkTouch"));
        if (tag.contains("Efficiency")) entityData.set(DATA_EFFICIENCY, Mth.clamp(tag.getInt("Efficiency"), 0, 5));
        if (tag.contains("Sweeping")) entityData.set(DATA_SWEEPING, Mth.clamp(tag.getInt("Sweeping"), 0, 3));
        if (tag.contains("Unbreaking")) entityData.set(DATA_UNBREAKING, Mth.clamp(tag.getInt("Unbreaking"), 0, 3));
        activeLightPos = tag.contains("ActiveLightPos") ? BlockPos.of(tag.getLong("ActiveLightPos")) : null;
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

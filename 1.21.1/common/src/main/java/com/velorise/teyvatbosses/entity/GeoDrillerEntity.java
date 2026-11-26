package com.velorise.teyvatbosses.entity;

import com.velorise.teyvatbosses.item.GeoDrillerPartItem;
import com.velorise.teyvatbosses.registry.TeyvatBossesItems;
import com.velorise.teyvatbosses.registry.TeyvatBossesSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
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
import net.minecraft.util.Mth;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Pilotable Geo Tunnel Driller assembled outdoors from 3 Primo Geovishap salvage parts:
 * Chassis, Bore Head, and Reactor Engine.
 *
 * <p>Supports attaching 1 or 2 vanilla {@link Items#CHEST} directly onto the rear deck
 * (27-slot Single Chest -> 54-slot Double Chest). Auto-bores a 3x3 tunnel ahead while
 * driven forward and routes mined drops straight into the attached chest(s).
 */
public class GeoDrillerEntity extends Mob {

    public static final int PART_CHASSIS = 1;
    public static final int PART_DRILL   = 2;
    public static final int PART_ENGINE  = 4;
    public static final int PART_ALL     = PART_CHASSIS | PART_DRILL | PART_ENGINE;
    public static final int MAX_FUEL     = 1000;

    private static final EntityDataAccessor<Integer> DATA_PARTS =
            SynchedEntityData.defineId(GeoDrillerEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_CHEST_COUNT =
            SynchedEntityData.defineId(GeoDrillerEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_FUEL =
            SynchedEntityData.defineId(GeoDrillerEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_DRILLING =
            SynchedEntityData.defineId(GeoDrillerEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> DATA_STEER =
            SynchedEntityData.defineId(GeoDrillerEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_THROTTLE =
            SynchedEntityData.defineId(GeoDrillerEntity.class, EntityDataSerializers.FLOAT);

    private final SimpleContainer cargo = new SimpleContainer(54);
    public float wheelSpin = 0.0F;
    public float drillSpin = 0.0F;
    private int mineCooldown = 0;

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
                .add(Attributes.STEP_HEIGHT, 1.1D);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_PARTS, PART_ALL);
        builder.define(DATA_CHEST_COUNT, 0);
        builder.define(DATA_FUEL, MAX_FUEL);
        builder.define(DATA_DRILLING, false);
        builder.define(DATA_STEER, 0.0F);
        builder.define(DATA_THROTTLE, 0.0F);
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
    public boolean isDrilling() { return entityData.get(DATA_DRILLING); }
    public float getSteerInput() { return entityData.get(DATA_STEER); }
    public float getThrottleInput() { return entityData.get(DATA_THROTTLE); }

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
    protected Vec3 getPassengerAttachmentPoint(Entity passenger, net.minecraft.world.entity.EntityDimensions dims, float partialTick) {
        return new Vec3(0.0D, 1.05D, 0.22D);
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

        // 3. Shift + RMB opens attached Vanilla Chest (27 slots single / 54 slots double)
        if (player.isSecondaryUseActive() && getChestCount() > 0) {
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

        // 4. Fuel recharge (Coal / Coal Block / Redstone Block) & Hull Repair (Iron Ingot / Block)
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

        // 5. Mount & pilot
        if (!level().isClientSide() && !isVehicle()) {
            player.startRiding(this);
        }
        return InteractionResult.sidedSuccess(level().isClientSide());
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!level().isClientSide() && source.getEntity() instanceof Player p
                && p.isSecondaryUseActive() && p.getMainHandItem().getItem() instanceof PickaxeItem) {
            dropPartsAndCargo((ServerLevel) level());
            discard();
            return true;
        }
        return super.hurt(source, amount);
    }

    @Override
    public void die(DamageSource source) {
        if (!level().isClientSide() && level() instanceof ServerLevel sl) {
            dropPartsAndCargo(sl);
        }
        super.die(source);
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
        super.tick();
        if (level().isClientSide()) {
            double hSpeed = getDeltaMovement().horizontalDistance();
            wheelSpin += (float) (hSpeed * 4.2D) * (getThrottleInput() < -0.05F ? -1.0F : 1.0F);
            drillSpin += isDrilling() ? 0.62F : (isVehicle() && getFuel() > 0 ? 0.10F : 0.0F);
            return;
        }
        if (mineCooldown > 0) mineCooldown--;
    }

    @Override
    public void travel(Vec3 travelVector) {
        if (isAlive() && isFullyAssembled() && getControllingPassenger() instanceof Player pilot) {
            float fwd = pilot.zza;
            float side = pilot.xxa;
            if (!level().isClientSide()) {
                entityData.set(DATA_THROTTLE, fwd);
                entityData.set(DATA_STEER, side);
            }
            if (Math.abs(side) > 0.05F) {
                float turn = -side * 3.8F;
                this.setYRot(this.getYRot() + turn);
                this.yRotO = this.getYRot();
                this.yBodyRot = this.getYRot();
                this.yHeadRot = this.getYRot();
            }
            boolean hasFuel = getFuel() > 0;
            boolean movingFwd = fwd > 0.05F && hasFuel;
            if (!level().isClientSide()) {
                entityData.set(DATA_DRILLING, movingFwd);
                if (movingFwd && level() instanceof ServerLevel sl && mineCooldown <= 0) {
                    mineTunnel3x3(sl, pilot);
                    mineCooldown = 4;
                }
            }
            if (hasFuel && Math.abs(fwd) > 0.05F) {
                this.setSpeed(fwd > 0 ? 0.22F : 0.13F);
                super.travel(new Vec3(0.0D, travelVector.y, fwd));
                if (!level().isClientSide() && tickCount % 40 == 0) {
                    entityData.set(DATA_FUEL, Math.max(0, getFuel() - 1));
                }
            } else {
                setDeltaMovement(getDeltaMovement().x * 0.65D, getDeltaMovement().y, getDeltaMovement().z * 0.65D);
                super.travel(Vec3.ZERO);
            }
            return;
        }
        if (!level().isClientSide()) {
            entityData.set(DATA_DRILLING, false);
            entityData.set(DATA_THROTTLE, 0.0F);
            entityData.set(DATA_STEER, 0.0F);
        }
        super.travel(travelVector);
    }

    private void mineTunnel3x3(ServerLevel sl, Player pilot) {
        Vec3 fwd = Vec3.directionFromRotation(0.0F, getYRot()).normalize();
        Vec3 right = new Vec3(-fwd.z, 0.0D, fwd.x).normalize();
        Vec3 frontCenter = position().add(fwd.scale(2.35D));

        boolean minedAny = false;
        for (int dy = 0; dy <= 2; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                BlockPos bp = BlockPos.containing(
                        frontCenter.x + right.x * dx,
                        getY() + dy + 0.1D,
                        frontCenter.z + right.z * dx
                );
                BlockState state = sl.getBlockState(bp);
                if (state.isAir() || !state.getFluidState().isEmpty() || state.hasBlockEntity()) continue;
                float hardness = state.getDestroySpeed(sl, bp);
                if (hardness < 0.0F || hardness > 50.0F) continue; // Skip bedrock / unbreakables

                List<ItemStack> drops = Block.getDrops(state, sl, bp, null, pilot, new ItemStack(Items.NETHERITE_PICKAXE));
                sl.setBlock(bp, Blocks.AIR.defaultBlockState(), 3);
                sl.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, state),
                        bp.getX() + 0.5D, bp.getY() + 0.5D, bp.getZ() + 0.5D, 6, 0.3D, 0.3D, 0.3D, 0.06D);
                for (ItemStack drop : drops) {
                    storeOrDrop(sl, drop);
                }
                minedAny = true;
            }
        }
        if (minedAny) {
            entityData.set(DATA_FUEL, Math.max(0, getFuel() - 1));
            sl.playSound(null, frontCenter.x, getY() + 1.0D, frontCenter.z, SoundEvents.STONE_BREAK, SoundSource.PLAYERS, 0.9F, 0.72F);
            if (TeyvatBossesSounds.mechServoWhir() != null && tickCount % 12 == 0) {
                sl.playSound(null, getX(), getY() + 1.0D, getZ(), TeyvatBossesSounds.mechServoWhir(), SoundSource.PLAYERS, 0.45F, 0.85F);
            }
        }
    }

    private void storeOrDrop(ServerLevel sl, ItemStack stack) {
        if (stack.isEmpty()) return;
        int slots = getChestCount() >= 2 ? 54 : (getChestCount() == 1 ? 27 : 0);
        if (slots > 0) {
            // Merge into existing stacks first, then empty slots within active chest capacity
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

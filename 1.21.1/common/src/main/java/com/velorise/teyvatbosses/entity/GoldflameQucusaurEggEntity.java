package com.velorise.teyvatbosses.entity;

import com.velorise.teyvatbosses.config.TeyvatBossesConfig;
import com.velorise.teyvatbosses.registry.TeyvatBossesEntities;
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
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.UUID;

/**
 * Living Goldflame Qucusaur Egg entity placed in-world by the player.
 * Requires a Phlogiston heat source (Magma Block, Fire, Soul Fire, Campfire, or Lava) directly
 * beneath or adjacent to incubate. Shakes with rising golden flames and hatches into a tamed
 * {@link GoldflameQucusaurMountEntity} owned by the placer.
 */
public class GoldflameQucusaurEggEntity extends Entity {
    private static final EntityDataAccessor<Integer> DATA_HATCH_TICKS =
            SynchedEntityData.defineId(GoldflameQucusaurEggEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_REQUIRED_TICKS =
            SynchedEntityData.defineId(GoldflameQucusaurEggEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_HEATED =
            SynchedEntityData.defineId(GoldflameQucusaurEggEntity.class, EntityDataSerializers.BOOLEAN);

    private UUID ownerUuid;

    public GoldflameQucusaurEggEntity(EntityType<? extends GoldflameQucusaurEggEntity> type, Level level) {
        super(type, level);
    }

    public static GoldflameQucusaurEggEntity spawn(ServerLevel level, BlockPos pos, Player owner) {
        GoldflameQucusaurEggEntity egg = new GoldflameQucusaurEggEntity(TeyvatBossesEntities.goldflameQucusaurEgg(), level);
        egg.setPos(pos.getX() + 0.5D, pos.getY() + 0.02D, pos.getZ() + 0.5D);
        egg.ownerUuid = owner != null ? owner.getUUID() : null;
        int req = Math.max(100, TeyvatBossesConfig.get().qucusaurEggHatchSeconds * 20);
        egg.entityData.set(DATA_REQUIRED_TICKS, req);
        egg.entityData.set(DATA_HATCH_TICKS, 0);
        egg.setCustomNameVisible(true);
        level.addFreshEntity(egg);
        level.playSound(null, egg.getX(), egg.getY(), egg.getZ(), SoundEvents.TURTLE_EGG_CRACK, SoundSource.NEUTRAL, 0.9F, 0.75F);
        return egg;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_HATCH_TICKS, 0);
        builder.define(DATA_REQUIRED_TICKS, 1200);
        builder.define(DATA_HEATED, false);
    }

    public float getHatchProgress() {
        int req = Math.max(1, entityData.get(DATA_REQUIRED_TICKS));
        return Math.max(0.0F, Math.min(1.0F, entityData.get(DATA_HATCH_TICKS) / (float) req));
    }

    public boolean isHeated() {
        return entityData.get(DATA_HEATED);
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (level().isClientSide()) return true;
        if (source.getEntity() instanceof Player player) {
            if (!player.getAbilities().instabuild) {
                dropEggItem();
            }
            level().playSound(null, getX(), getY(), getZ(), SoundEvents.TURTLE_EGG_CRACK, SoundSource.NEUTRAL, 0.9F, 1.15F);
            discard();
            return true;
        }
        return false;
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if (level().isClientSide()) return InteractionResult.SUCCESS;
        if (player.isShiftKeyDown()) {
            dropEggItem();
            discard();
            return InteractionResult.CONSUME;
        }
        int pct = (int) (getHatchProgress() * 100.0F);
        if (isHeated()) {
            player.displayClientMessage(Component.translatable("message.velorise_teyvat_bosses.qucusaur_egg.incubating", pct)
                    .withStyle(ChatFormatting.GOLD), true);
        } else {
            player.displayClientMessage(Component.translatable("message.velorise_teyvat_bosses.qucusaur_egg.need_heat")
                    .withStyle(ChatFormatting.RED), true);
        }
        return InteractionResult.CONSUME;
    }

    private void dropEggItem() {
        if (TeyvatBossesItems.goldflameQucusaurEgg() != null) {
            ItemEntity ie = new ItemEntity(level(), getX(), getY() + 0.3D, getZ(),
                    new ItemStack(TeyvatBossesItems.goldflameQucusaurEgg()));
            level().addFreshEntity(ie);
        }
    }

    private boolean checkHeatSource() {
        BlockPos below = blockPosition().below();
        if (isHeatBlock(level().getBlockState(below)) || isHeatBlock(level().getBlockState(blockPosition()))) {
            return true;
        }
        for (BlockPos p : new BlockPos[]{blockPosition().north(), blockPosition().south(), blockPosition().east(), blockPosition().west()}) {
            if (isHeatBlock(level().getBlockState(p))) return true;
        }
        return false;
    }

    private static boolean isHeatBlock(BlockState st) {
        if (st.is(Blocks.MAGMA_BLOCK) || st.is(Blocks.FIRE) || st.is(Blocks.SOUL_FIRE) || st.is(Blocks.LAVA)) {
            return true;
        }
        return (st.is(Blocks.CAMPFIRE) || st.is(Blocks.SOUL_CAMPFIRE))
                && st.hasProperty(CampfireBlock.LIT) && st.getValue(CampfireBlock.LIT);
    }

    @Override
    public void tick() {
        super.tick();
        if (!isNoGravity()) {
            setDeltaMovement(getDeltaMovement().add(0.0D, -0.06D, 0.0D));
            move(net.minecraft.world.entity.MoverType.SELF, getDeltaMovement());
            if (onGround()) {
                setDeltaMovement(0.0D, 0.0D, 0.0D);
            }
        }
        if (level().isClientSide()) {
            if (isHeated() && tickCount % 4 == 0) {
                level().addParticle(ParticleTypes.FLAME,
                        getX() + (random.nextDouble() - 0.5D) * 0.6D,
                        getY() + 0.25D + random.nextDouble() * 0.45D,
                        getZ() + (random.nextDouble() - 0.5D) * 0.6D,
                        0.0D, 0.04D, 0.0D);
            }
            return;
        }

        boolean heated = checkHeatSource();
        entityData.set(DATA_HEATED, heated);
        int req = Math.max(100, entityData.get(DATA_REQUIRED_TICKS));
        int cur = entityData.get(DATA_HATCH_TICKS);

        if (heated) {
            cur++;
            entityData.set(DATA_HATCH_TICKS, cur);
            if (cur % 60 == 0) {
                level().playSound(null, getX(), getY(), getZ(), SoundEvents.TURTLE_EGG_CRACK, SoundSource.NEUTRAL, 0.7F, 0.85F + getHatchProgress() * 0.4F);
            }
        }

        if (tickCount % 10 == 0) {
            int pct = (int) (getHatchProgress() * 100.0F);
            if (heated) {
                setCustomName(Component.translatable("message.velorise_teyvat_bosses.qucusaur_egg.label_heated", pct)
                        .withStyle(ChatFormatting.GOLD));
            } else {
                setCustomName(Component.translatable("message.velorise_teyvat_bosses.qucusaur_egg.label_cold")
                        .withStyle(ChatFormatting.GRAY));
            }
        }

        if (cur >= req && level() instanceof ServerLevel sl) {
            hatchIntoMount(sl);
        }
    }

    private void hatchIntoMount(ServerLevel sl) {
        GoldflameQucusaurMountEntity mount = new GoldflameQucusaurMountEntity(TeyvatBossesEntities.goldflameQucusaurMount(), sl);
        mount.setPos(getX(), getY() + 0.1D, getZ());
        mount.setOwnerUuid(ownerUuid);
        mount.setSaddled(false);
        sl.addFreshEntity(mount);
        sl.sendParticles(ParticleTypes.FLAME, getX(), getY() + 0.8D, getZ(), 42, 0.9D, 0.9D, 0.9D, 0.12D);
        sl.sendParticles(ParticleTypes.END_ROD, getX(), getY() + 0.8D, getZ(), 20, 0.7D, 0.7D, 0.7D, 0.08D);
        sl.playSound(null, getX(), getY(), getZ(), SoundEvents.ENDER_DRAGON_GROWL, SoundSource.NEUTRAL, 0.95F, 1.35F);
        if (ownerUuid != null && sl.getPlayerByUUID(ownerUuid) instanceof Player p) {
            p.displayClientMessage(Component.translatable("message.velorise_teyvat_bosses.qucusaur_egg.hatched")
                    .withStyle(ChatFormatting.GOLD), false);
        }
        discard();
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.hasUUID("Owner")) ownerUuid = tag.getUUID("Owner");
        entityData.set(DATA_HATCH_TICKS, tag.getInt("HatchTicks"));
        if (tag.contains("RequiredTicks")) entityData.set(DATA_REQUIRED_TICKS, tag.getInt("RequiredTicks"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        if (ownerUuid != null) tag.putUUID("Owner", ownerUuid);
        tag.putInt("HatchTicks", entityData.get(DATA_HATCH_TICKS));
        tag.putInt("RequiredTicks", entityData.get(DATA_REQUIRED_TICKS));
    }
}

package com.velorise.teyvatbosses.entity;

import com.velorise.teyvatbosses.registry.TeyvatBossesEntities;
import com.velorise.teyvatbosses.registry.TeyvatBossesItems;
import net.minecraft.ChatFormatting;
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
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Genshin-accurate Trounce Blossom (Ley Line Reward Flower) that emerges at the center
 * of the arena upon defeating a World Boss.
 */
public class TrounceBlossomEntity extends Entity {

    private static final EntityDataAccessor<Integer> DATA_BOSS_KIND =
            SynchedEntityData.defineId(TrounceBlossomEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_CLAIMED =
            SynchedEntityData.defineId(TrounceBlossomEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_BLOOM_TICKS =
            SynchedEntityData.defineId(TrounceBlossomEntity.class, EntityDataSerializers.INT);

    private float savedYaw = 0.0F;
    private int despawnAfterClaimTicks = 0;

    public TrounceBlossomEntity(EntityType<? extends TrounceBlossomEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public static TrounceBlossomEntity spawn(ServerLevel level, int bossKind, Vec3 pos, float savedYaw) {
        TrounceBlossomEntity blossom = new TrounceBlossomEntity(TeyvatBossesEntities.trounceBlossom(), level);
        blossom.setPos(pos.x, pos.y, pos.z);
        blossom.savedYaw = savedYaw;
        blossom.entityData.set(DATA_BOSS_KIND, bossKind);
        blossom.entityData.set(DATA_CLAIMED, false);
        blossom.entityData.set(DATA_BLOOM_TICKS, 0);
        blossom.setCustomName(Component.translatable("message.velorise_teyvat_bosses.trounce_blossom.touch")
                .withStyle(ChatFormatting.AQUA));
        blossom.setCustomNameVisible(true);
        level.addFreshEntity(blossom);
        level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.AMBIENT, 1.3F, 1.15F);
        level.sendParticles(ParticleTypes.END_ROD, pos.x, pos.y + 1.0D, pos.z, 32, 0.8D, 0.9D, 0.8D, 0.07D);
        return blossom;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_BOSS_KIND, BossRespawnAnchorEntity.BOSS_ANEMO_HYPOSTASIS);
        builder.define(DATA_CLAIMED, false);
        builder.define(DATA_BLOOM_TICKS, 0);
    }

    public int getBossKind() { return entityData.get(DATA_BOSS_KIND); }
    public boolean isClaimed() { return entityData.get(DATA_CLAIMED); }
    public int getBloomTicks() { return entityData.get(DATA_BLOOM_TICKS); }

    @Override
    public boolean isPickable() { return true; }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!level().isClientSide() && source.getEntity() instanceof Player p && p.isCreative()) {
            discard();
            return true;
        }
        return false;
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if (hand != InteractionHand.MAIN_HAND) return InteractionResult.PASS;
        if (isClaimed()) return InteractionResult.PASS;
        if (!level().isClientSide() && level() instanceof ServerLevel sl) {
            claimRewards(sl, player);
        }
        return InteractionResult.sidedSuccess(level().isClientSide());
    }

    private void claimRewards(ServerLevel sl, Player player) {
        entityData.set(DATA_CLAIMED, true);
        setCustomNameVisible(false);

        sl.playSound(null, getX(), getY() + 1.0D, getZ(), SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundSource.PLAYERS, 0.9F, 1.1F);
        sl.playSound(null, getX(), getY() + 1.0D, getZ(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 1.4F, 1.35F);
        sl.sendParticles(ParticleTypes.END_ROD, getX(), getY() + 1.2D, getZ(), 48, 1.1D, 1.0D, 1.1D, 0.12D);
        sl.sendParticles(ParticleTypes.GLOW, getX(), getY() + 1.1D, getZ(), 30, 0.8D, 0.8D, 0.8D, 0.06D);

        int kind = getBossKind();
        if (kind == BossRespawnAnchorEntity.BOSS_ANEMO_HYPOSTASIS) {
            if (TeyvatBossesItems.anemoDisc() != null) dropReward(sl, new ItemStack(TeyvatBossesItems.anemoDisc()));
            dropReward(sl, new ItemStack(Items.WIND_CHARGE, 12 + random.nextInt(9)));
            dropReward(sl, new ItemStack(Items.EMERALD, 6 + random.nextInt(5)));
            dropReward(sl, new ItemStack(Items.DIAMOND, 2 + random.nextInt(3)));
            dropReward(sl, new ItemStack(Items.BREEZE_ROD, 3 + random.nextInt(3)));
        } else if (kind == BossRespawnAnchorEntity.BOSS_ASTRALION) {
            dropReward(sl, new ItemStack(Items.NETHERITE_SCRAP, 2 + random.nextInt(3)));
            dropReward(sl, new ItemStack(Items.DIAMOND, 5 + random.nextInt(4)));
            dropReward(sl, new ItemStack(Items.AMETHYST_SHARD, 12 + random.nextInt(9)));
            dropReward(sl, new ItemStack(Items.EMERALD, 8 + random.nextInt(6)));
        } else if (kind == BossRespawnAnchorEntity.BOSS_RUIN_GUARD) {
            if (TeyvatBossesItems.ruinGuardDisc() != null) dropReward(sl, new ItemStack(TeyvatBossesItems.ruinGuardDisc()));
            dropReward(sl, new ItemStack(Items.IRON_BLOCK, 2 + random.nextInt(3)));
            dropReward(sl, new ItemStack(Items.REDSTONE_BLOCK, 2 + random.nextInt(2)));
            dropReward(sl, new ItemStack(Items.DIAMOND, 3 + random.nextInt(3)));
            dropReward(sl, new ItemStack(Items.BLAZE_ROD, 4 + random.nextInt(4)));
        } else if (kind == BossRespawnAnchorEntity.BOSS_CRYO_REGISVINE) {
            if (TeyvatBossesItems.cryoDisc() != null) dropReward(sl, new ItemStack(TeyvatBossesItems.cryoDisc()));
            dropReward(sl, new ItemStack(Items.BLUE_ICE, 16));
            dropReward(sl, new ItemStack(Items.DIAMOND, 2 + random.nextInt(3)));
        } else {
            if (TeyvatBossesItems.openEyedDisc() != null) dropReward(sl, new ItemStack(TeyvatBossesItems.openEyedDisc()));
            dropReward(sl, new ItemStack(Items.GOLD_INGOT, 10 + random.nextInt(6)));
            dropReward(sl, new ItemStack(Items.DIAMOND, 3 + random.nextInt(3)));
        }
        ExperienceOrb.award(sl, position().add(0.0D, 1.0D, 0.0D), 280);
        player.displayClientMessage(Component.translatable("message.velorise_teyvat_bosses.trounce_blossom.claimed")
                .withStyle(ChatFormatting.GREEN), true);
    }

    private void dropReward(ServerLevel sl, ItemStack stack) {
        ItemEntity item = new ItemEntity(sl, getX(), getY() + 1.25D, getZ(), stack);
        item.setDeltaMovement((random.nextDouble() - 0.5D) * 0.25D, 0.36D, (random.nextDouble() - 0.5D) * 0.25D);
        sl.addFreshEntity(item);
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide()) {
            if (tickCount % 5 == 0) {
                level().addParticle(isClaimed() ? ParticleTypes.END_ROD : ParticleTypes.GLOW,
                        getX() + (random.nextDouble() - 0.5D) * 1.1D,
                        getY() + 0.8D + random.nextDouble() * 1.1D,
                        getZ() + (random.nextDouble() - 0.5D) * 1.1D,
                        0.0D, 0.04D, 0.0D);
            }
            return;
        }

        if (isClaimed()) {
            entityData.set(DATA_BLOOM_TICKS, getBloomTicks() + 1);
            despawnAfterClaimTicks++;
            if (despawnAfterClaimTicks >= 90 && level() instanceof ServerLevel sl) {
                sl.sendParticles(ParticleTypes.END_ROD, getX(), getY() + 1.0D, getZ(), 24, 0.6D, 0.8D, 0.6D, 0.08D);
                BossRespawnAnchorEntity.spawn(sl, getBossKind(), position(), savedYaw, BossRespawnAnchorEntity.DEFAULT_RESPAWN_TICKS);
                discard();
            }
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.contains("BossKind")) entityData.set(DATA_BOSS_KIND, tag.getInt("BossKind"));
        if (tag.contains("Claimed")) entityData.set(DATA_CLAIMED, tag.getBoolean("Claimed"));
        if (tag.contains("BloomTicks")) entityData.set(DATA_BLOOM_TICKS, tag.getInt("BloomTicks"));
        savedYaw = tag.getFloat("SavedYaw");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("BossKind", getBossKind());
        tag.putBoolean("Claimed", isClaimed());
        tag.putInt("BloomTicks", getBloomTicks());
        tag.putFloat("SavedYaw", savedYaw);
    }
}

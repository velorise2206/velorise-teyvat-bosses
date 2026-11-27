package com.velorise.teyvatbosses.entity;

import com.velorise.teyvatbosses.registry.TeyvatBossesBlocks;
import com.velorise.teyvatbosses.registry.TeyvatBossesEntities;
import com.velorise.teyvatbosses.registry.TeyvatBossesItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Per-player instanced Trounce Blossom (Ley Line Reward Flower) that remains on the ground
 * for 60 seconds ({@code 1200} ticks). Every player who clicks it receives their own private loot
 * directly into their inventory (with smart anti-duplicate part selection) before the blossom
 * transitions into a {@link BossRespawnAnchorEntity}.
 */
public class TrounceBlossomEntity extends Entity {

    public static final int LIFETIME_TICKS = 3600; // 180 seconds (3 minutes)

    private static final EntityDataAccessor<Integer> DATA_BOSS_KIND =
            SynchedEntityData.defineId(TrounceBlossomEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_CLAIMED =
            SynchedEntityData.defineId(TrounceBlossomEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_BLOOM_TICKS =
            SynchedEntityData.defineId(TrounceBlossomEntity.class, EntityDataSerializers.INT);

    private final Set<UUID> claimedPlayers = new HashSet<>();
    private final Set<UUID> eligiblePlayers = new HashSet<>();
    private float savedYaw = 0.0F;
    private int ageTicks = 0;

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
        blossom.updateLabel(LIFETIME_TICKS);
        blossom.setCustomNameVisible(true);
        // Snapshot all nearby non-spectator participants and grant shared multiplayer kill advancement
        ResourceLocation advId = advancementForBossKind(bossKind);
        AdvancementHolder advHolder = advId != null ? level.getServer().getAdvancements().get(advId) : null;
        for (ServerPlayer sp : level.players()) {
            if (!sp.isSpectator() && sp.position().distanceToSqr(pos) <= 64.0D * 64.0D) {
                blossom.eligiblePlayers.add(sp.getUUID());
                if (advHolder != null) {
                    AdvancementProgress prog = sp.getAdvancements().getOrStartProgress(advHolder);
                    if (!prog.isDone()) {
                        for (String criterion : prog.getRemainingCriteria()) {
                            sp.getAdvancements().award(advHolder, criterion);
                        }
                    }
                }
            }
        }
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

    private void updateLabel(int remainingTicks) {
        int secs = Math.max(1, (remainingTicks + 19) / 20);
        setCustomName(Component.translatable("message.velorise_teyvat_bosses.trounce_blossom.touch")
                .append(Component.literal(" (" + secs + "s)").withStyle(ChatFormatting.YELLOW))
                .withStyle(ChatFormatting.AQUA));
    }

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
        if (!level().isClientSide() && level() instanceof ServerLevel sl) {
            if (!eligiblePlayers.isEmpty() && !eligiblePlayers.contains(player.getUUID()) && !player.isCreative()) {
                player.displayClientMessage(Component.translatable("message.velorise_teyvat_bosses.trounce_blossom.not_contributor")
                        .withStyle(ChatFormatting.RED), true);
                return InteractionResult.CONSUME;
            }
            if (claimedPlayers.contains(player.getUUID())) {
                player.displayClientMessage(Component.translatable("message.velorise_teyvat_bosses.trounce_blossom.already_claimed")
                        .withStyle(ChatFormatting.YELLOW), true);
                return InteractionResult.CONSUME;
            }
            claimRewards(sl, player);
        }
        return InteractionResult.sidedSuccess(level().isClientSide());
    }

    private void claimRewards(ServerLevel sl, Player player) {
        claimedPlayers.add(player.getUUID());
        entityData.set(DATA_CLAIMED, true);

        sl.playSound(null, player.getX(), player.getY() + 1.0D, player.getZ(), SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundSource.PLAYERS, 0.85F, 1.12F);
        sl.playSound(null, getX(), getY() + 1.0D, getZ(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 1.25F, 1.35F);
        sl.sendParticles(ParticleTypes.END_ROD, getX(), getY() + 1.2D, getZ(), 32, 0.9D, 0.9D, 0.9D, 0.10D);

        int kind = getBossKind();
        if (TeyvatBossesBlocks.trophyForBossKind(kind) != null) {
            giveToPlayer(sl, player, new ItemStack(TeyvatBossesBlocks.trophyForBossKind(kind)));
        }

        if (kind == BossRespawnAnchorEntity.BOSS_ANEMO_HYPOSTASIS) {
            if (TeyvatBossesItems.anemoDisc() != null) giveToPlayer(sl, player, new ItemStack(TeyvatBossesItems.anemoDisc()));
            if (TeyvatBossesItems.anemoPrismCube() != null) giveToPlayer(sl, player, new ItemStack(TeyvatBossesItems.anemoPrismCube(), 2 + random.nextInt(3)));
            giveToPlayer(sl, player, new ItemStack(Items.WIND_CHARGE, 12 + random.nextInt(9)));
            giveToPlayer(sl, player, new ItemStack(Items.EMERALD, 6 + random.nextInt(5)));
            giveToPlayer(sl, player, new ItemStack(Items.DIAMOND, 2 + random.nextInt(3)));
            giveToPlayer(sl, player, new ItemStack(Items.BREEZE_ROD, 3 + random.nextInt(3)));
        } else if (kind == BossRespawnAnchorEntity.BOSS_RUIN_GUARD) {
            if (TeyvatBossesItems.ruinGuardDisc() != null) giveToPlayer(sl, player, new ItemStack(TeyvatBossesItems.ruinGuardDisc()));
            int partDrops = 2 + random.nextInt(2); // 2-3 smart anti-duplicate Mech salvage parts
            for (int i = 0; i < partDrops; i++) {
                Item mechPart = pickSmartPart(player, TeyvatBossesItems.mechPartPool());
                if (mechPart != null) giveToPlayer(sl, player, new ItemStack(mechPart));
            }
            giveToPlayer(sl, player, new ItemStack(Items.IRON_BLOCK, 2 + random.nextInt(3)));
            giveToPlayer(sl, player, new ItemStack(Items.REDSTONE_BLOCK, 2 + random.nextInt(2)));
            giveToPlayer(sl, player, new ItemStack(Items.DIAMOND, 3 + random.nextInt(3)));
            giveToPlayer(sl, player, new ItemStack(Items.BLAZE_ROD, 4 + random.nextInt(4)));
        } else if (kind == BossRespawnAnchorEntity.BOSS_RUIN_NAUTILUS) {
            if (TeyvatBossesItems.ruinNautilusDisc() != null) giveToPlayer(sl, player, new ItemStack(TeyvatBossesItems.ruinNautilusDisc()));
            int partDrops = 2 + random.nextInt(2); // 2-3 smart anti-duplicate Submersible salvage parts
            for (int i = 0; i < partDrops; i++) {
                Item subPart = pickSmartPart(player, TeyvatBossesItems.subPartPool());
                if (subPart != null) giveToPlayer(sl, player, new ItemStack(subPart));
            }
            giveToPlayer(sl, player, new ItemStack(Items.SEA_LANTERN, 3 + random.nextInt(3)));
            giveToPlayer(sl, player, new ItemStack(Items.PRISMARINE_BRICKS, 8 + random.nextInt(6)));
            giveToPlayer(sl, player, new ItemStack(Items.DIAMOND, 3 + random.nextInt(3)));
        } else if (kind == BossRespawnAnchorEntity.BOSS_CRYO_REGISVINE) {
            if (TeyvatBossesItems.cryoDisc() != null) giveToPlayer(sl, player, new ItemStack(TeyvatBossesItems.cryoDisc()));
            int partDrops = 2 + random.nextInt(2); // 2-3 smart anti-duplicate Cryo Turret salvage parts
            for (int i = 0; i < partDrops; i++) {
                Item cryoPart = pickSmartPart(player, TeyvatBossesItems.cryoTurretPartPool());
                if (cryoPart != null) giveToPlayer(sl, player, new ItemStack(cryoPart));
            }
            giveToPlayer(sl, player, new ItemStack(Items.BLUE_ICE, 16));
            giveToPlayer(sl, player, new ItemStack(Items.DIAMOND, 2 + random.nextInt(3)));
        } else { // Primo Geovishap: drops 2-3 Primordial Jade (crafts both Dragon-Scale Armor & Geo Driller!)
            if (TeyvatBossesItems.primoGeovishapDisc() != null) giveToPlayer(sl, player, new ItemStack(TeyvatBossesItems.primoGeovishapDisc()));
            if (TeyvatBossesItems.primordialJade() != null) {
                giveToPlayer(sl, player, new ItemStack(TeyvatBossesItems.primordialJade(), 2 + random.nextInt(2)));
            }
            giveToPlayer(sl, player, new ItemStack(Items.RAW_GOLD, 8 + random.nextInt(6)));
            giveToPlayer(sl, player, new ItemStack(Items.DIAMOND, 3 + random.nextInt(3)));
            giveToPlayer(sl, player, new ItemStack(Items.AMETHYST_SHARD, 8 + random.nextInt(6)));
        }
        ExperienceOrb.award(sl, player.position().add(0.0D, 0.5D, 0.0D), 280);
        player.displayClientMessage(Component.translatable("message.velorise_teyvat_bosses.trounce_blossom.claimed")
                .withStyle(ChatFormatting.GREEN), true);
    }

    /**
     * Smart anti-duplicate salvage selector: prioritizes parts the player does not yet hold
     * in their inventory so completing a 3/4/5-part set avoids punishing Coupon Collector RNG.
     */
    private Item pickSmartPart(Player player, Item[] pool) {
        List<Item> valid = new ArrayList<>();
        List<Item> missing = new ArrayList<>();
        for (Item item : pool) {
            if (item == null) continue;
            valid.add(item);
            if (!player.getInventory().hasAnyMatching(s -> s.is(item))) {
                missing.add(item);
            }
        }
        if (!missing.isEmpty()) {
            return missing.get(random.nextInt(missing.size()));
        }
        return valid.isEmpty() ? null : valid.get(random.nextInt(valid.size()));
    }

    private void giveToPlayer(ServerLevel sl, Player player, ItemStack stack) {
        if (stack.isEmpty()) return;
        boolean added = player.getInventory().add(stack);
        if (!added && !stack.isEmpty()) {
            // Inventory full: drop locked to this player's UUID right at their feet
            ItemEntity drop = new ItemEntity(sl, player.getX(), player.getY() + 0.35D, player.getZ(), stack);
            drop.setTarget(player.getUUID());
            drop.setNoPickUpDelay();
            drop.setDeltaMovement(0.0D, 0.15D, 0.0D);
            sl.addFreshEntity(drop);
        }
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

        ageTicks++;
        if (isClaimed()) {
            entityData.set(DATA_BLOOM_TICKS, getBloomTicks() + 1);
        }
        if (ageTicks % 20 == 0) {
            updateLabel(LIFETIME_TICKS - ageTicks);
        }
        if (ageTicks >= LIFETIME_TICKS && level() instanceof ServerLevel sl) {
            sl.sendParticles(ParticleTypes.END_ROD, getX(), getY() + 1.0D, getZ(), 24, 0.6D, 0.8D, 0.6D, 0.08D);
            BossRespawnAnchorEntity.spawn(sl, getBossKind(), position(), savedYaw, BossRespawnAnchorEntity.DEFAULT_RESPAWN_TICKS);
            discard();
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.contains("BossKind")) entityData.set(DATA_BOSS_KIND, tag.getInt("BossKind"));
        if (tag.contains("Claimed")) entityData.set(DATA_CLAIMED, tag.getBoolean("Claimed"));
        if (tag.contains("BloomTicks")) entityData.set(DATA_BLOOM_TICKS, tag.getInt("BloomTicks"));
        savedYaw = tag.getFloat("SavedYaw");
        ageTicks = tag.getInt("AgeTicks");
        claimedPlayers.clear();
        if (tag.contains("ClaimedPlayers", Tag.TAG_LIST)) {
            ListTag list = tag.getList("ClaimedPlayers", Tag.TAG_INT_ARRAY);
            for (Tag t : list) {
                claimedPlayers.add(NbtUtils.loadUUID(t));
            }
        }
        eligiblePlayers.clear();
        if (tag.contains("EligiblePlayers", Tag.TAG_LIST)) {
            ListTag list = tag.getList("EligiblePlayers", Tag.TAG_INT_ARRAY);
            for (Tag t : list) {
                eligiblePlayers.add(NbtUtils.loadUUID(t));
            }
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("BossKind", getBossKind());
        tag.putBoolean("Claimed", isClaimed());
        tag.putInt("BloomTicks", getBloomTicks());
        tag.putFloat("SavedYaw", savedYaw);
        tag.putInt("AgeTicks", ageTicks);
        ListTag list = new ListTag();
        for (UUID id : claimedPlayers) {
            list.add(NbtUtils.createUUID(id));
        }
        tag.put("ClaimedPlayers", list);
        ListTag eligList = new ListTag();
        for (UUID id : eligiblePlayers) {
            eligList.add(NbtUtils.createUUID(id));
        }
        tag.put("EligiblePlayers", eligList);
    }

    @org.jetbrains.annotations.Nullable
    private static ResourceLocation advancementForBossKind(int kind) {
        return switch (kind) {
            case BossRespawnAnchorEntity.BOSS_CRYO_REGISVINE -> com.velorise.teyvatbosses.TeyvatBossesConstants.id("kill_cryo_regisvine");
            case BossRespawnAnchorEntity.BOSS_ANEMO_HYPOSTASIS -> com.velorise.teyvatbosses.TeyvatBossesConstants.id("kill_anemo_hypostasis");
            case BossRespawnAnchorEntity.BOSS_RUIN_GUARD -> com.velorise.teyvatbosses.TeyvatBossesConstants.id("kill_ruin_guard");
            case BossRespawnAnchorEntity.BOSS_RUIN_NAUTILUS -> com.velorise.teyvatbosses.TeyvatBossesConstants.id("kill_ruin_nautilus");
            case BossRespawnAnchorEntity.BOSS_PRIMO_GEOVISHAP -> com.velorise.teyvatbosses.TeyvatBossesConstants.id("kill_primo_geovishap");
            default -> null;
        };
    }
}

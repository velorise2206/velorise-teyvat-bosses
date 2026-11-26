package com.velorise.teyvatbosses.item;

import com.velorise.teyvatbosses.entity.RuinSubmersibleEntity;
import com.velorise.teyvatbosses.registry.TeyvatBossesEntities;
import com.velorise.teyvatbosses.registry.TeyvatBossesSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * One of the 4 modular Ruin Nautilus salvage parts ({@code ruin_sub_hull}, {@code ruin_sub_cockpit},
 * {@code ruin_sub_propeller}, {@code ruin_sub_torpedo_pod}) used to assemble a pilotable 6-DoF
 * {@link RuinSubmersibleEntity}.
 */
public class RuinSubPartItem extends Item {
    private final int partBit;

    public RuinSubPartItem(int partBit, Properties properties) {
        super(properties);
        this.partBit = partBit;
    }

    public int getPartBit() {
        return partBit;
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        if (partBit != RuinSubmersibleEntity.PART_HULL) {
            return InteractionResult.PASS;
        }
        Level level = ctx.getLevel();
        if (!level.isClientSide() && level instanceof ServerLevel sl) {
            BlockPos pos = ctx.getClickedPos().relative(ctx.getClickedFace());
            deployHull(sl, new Vec3(pos.getX() + 0.5D, pos.getY() + 0.1D, pos.getZ() + 0.5D), ctx.getPlayer(), ctx.getItemInHand());
            return InteractionResult.CONSUME;
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (partBit != RuinSubmersibleEntity.PART_HULL) {
            return InteractionResultHolder.pass(stack);
        }
        BlockHitResult hit = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
        if (hit.getType() == HitResult.Type.BLOCK) {
            if (!level.isClientSide() && level instanceof ServerLevel sl) {
                BlockPos pos = hit.getBlockPos();
                deployHull(sl, new Vec3(pos.getX() + 0.5D, pos.getY() + 0.2D, pos.getZ() + 0.5D), player, stack);
            }
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
        }
        return InteractionResultHolder.pass(stack);
    }

    private static void deployHull(ServerLevel sl, Vec3 spawnPos, Player player, ItemStack stack) {
        RuinSubmersibleEntity sub = TeyvatBossesEntities.ruinSubmersible().create(sl);
        if (sub == null) return;
        float yaw = player != null ? player.getYRot() : 0.0F;
        sub.moveTo(spawnPos.x, spawnPos.y, spawnPos.z, yaw, 0.0F);
        sub.setYBodyRot(yaw);
        sub.setYHeadRot(yaw);
        sub.initAssemblyChassis();
        sl.addFreshEntity(sub);
        var sfx = TeyvatBossesSounds.subBallastHiss() != null ? TeyvatBossesSounds.subBallastHiss() : SoundEvents.NETHERITE_BLOCK_PLACE;
        sl.playSound(null, spawnPos.x, spawnPos.y, spawnPos.z, sfx, SoundSource.PLAYERS, 1.2F, 0.90F);
        sl.sendParticles(ParticleTypes.BUBBLE_POP, spawnPos.x, spawnPos.y + 0.8D, spawnPos.z, 20, 0.8D, 0.5D, 0.8D, 0.05D);
        if (player != null) {
            if (!player.getAbilities().instabuild) stack.shrink(1);
            player.displayClientMessage(Component.translatable("message.velorise_teyvat_bosses.ruin_sub.hull_placed"), true);
        }
    }
}

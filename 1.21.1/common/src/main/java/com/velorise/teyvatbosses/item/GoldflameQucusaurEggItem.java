package com.velorise.teyvatbosses.item;

import com.velorise.teyvatbosses.entity.GoldflameQucusaurEggEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;

import java.util.List;

/**
 * Places a living {@link GoldflameQucusaurEggEntity} on a heat source (Magma Block, Fire, Campfire, Lava)
 * to incubate and hatch into a tamed {@link com.velorise.teyvatbosses.entity.GoldflameQucusaurMountEntity}.
 */
public class GoldflameQucusaurEggItem extends Item {
    public GoldflameQucusaurEggItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        if (ctx.getLevel().isClientSide()) return InteractionResult.SUCCESS;
        if (ctx.getLevel() instanceof ServerLevel sl) {
            BlockPos placePos = ctx.getClickedPos().relative(ctx.getClickedFace());
            GoldflameQucusaurEggEntity.spawn(sl, placePos, ctx.getPlayer());
            if (ctx.getPlayer() == null || !ctx.getPlayer().isCreative()) {
                ctx.getItemInHand().shrink(1);
            }
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext ctx, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.velorise_teyvat_bosses.goldflame_qucusaur_egg")
                .withStyle(ChatFormatting.GOLD));
    }
}

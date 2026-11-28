package com.velorise.teyvatbosses.item;

import com.velorise.teyvatbosses.entity.GoldflameQucusaurMountEntity;
import com.velorise.teyvatbosses.registry.TeyvatBossesEntities;
import com.velorise.teyvatbosses.registry.TeyvatBossesItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;

import java.util.List;

/**
 * Summon/recall crest item storing a tamed {@link GoldflameQucusaurMountEntity}'s HP, Phlogiston,
 * and saddled state in NBT so players can safely store and redeploy their dragon anywhere.
 */
public class GoldflameQucusaurCrestItem extends Item {
    private static final String TAG_HP = "DragonHp";
    private static final String TAG_PHLOG = "DragonPhlogiston";
    private static final String TAG_SADDLED = "DragonSaddled";

    public GoldflameQucusaurCrestItem(Properties properties) {
        super(properties);
    }

    public static ItemStack packFromMount(GoldflameQucusaurMountEntity mount) {
        ItemStack stack = new ItemStack(TeyvatBossesItems.goldflameQucusaurCrest());
        CompoundTag tag = new CompoundTag();
        tag.putFloat(TAG_HP, mount.getHealth());
        tag.putFloat(TAG_PHLOG, mount.getPhlogiston());
        tag.putBoolean(TAG_SADDLED, mount.isSaddled());
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        return stack;
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        if (ctx.getLevel().isClientSide()) return InteractionResult.SUCCESS;
        if (ctx.getLevel() instanceof ServerLevel sl) {
            BlockPos pos = ctx.getClickedPos().relative(ctx.getClickedFace());
            GoldflameQucusaurMountEntity mount = new GoldflameQucusaurMountEntity(TeyvatBossesEntities.goldflameQucusaurMount(), sl);
            mount.setPos(pos.getX() + 0.5D, pos.getY() + 0.1D, pos.getZ() + 0.5D);
            if (ctx.getPlayer() != null) {
                mount.setOwnerUuid(ctx.getPlayer().getUUID());
                mount.setYRot(ctx.getPlayer().getYRot());
            }
            CompoundTag tag = ctx.getItemInHand().getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
            if (tag.contains(TAG_HP)) mount.setHealth(Math.max(1.0F, tag.getFloat(TAG_HP)));
            if (tag.contains(TAG_PHLOG)) mount.setPhlogiston(tag.getFloat(TAG_PHLOG));
            mount.setSaddled(!tag.contains(TAG_SADDLED) || tag.getBoolean(TAG_SADDLED));
            sl.addFreshEntity(mount);
            sl.playSound(null, pos, SoundEvents.ENDER_DRAGON_FLAP, SoundSource.NEUTRAL, 1.0F, 1.25F);
            if (ctx.getPlayer() == null || !ctx.getPlayer().isCreative()) {
                ctx.getItemInHand().shrink(1);
            }
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext ctx, List<Component> tooltip, TooltipFlag flag) {
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        float hp = tag.contains(TAG_HP) ? tag.getFloat(TAG_HP) : 160.0F;
        float phlog = tag.contains(TAG_PHLOG) ? tag.getFloat(TAG_PHLOG) : 200.0F;
        tooltip.add(Component.translatable("tooltip.velorise_teyvat_bosses.goldflame_qucusaur_crest",
                (int) hp, (int) phlog).withStyle(ChatFormatting.GOLD));
    }
}

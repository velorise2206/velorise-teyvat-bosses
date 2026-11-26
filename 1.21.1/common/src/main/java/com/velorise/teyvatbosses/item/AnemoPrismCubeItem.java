package com.velorise.teyvatbosses.item;
import net.minecraft.ChatFormatting;
import net.minecraft.world.item.TooltipFlag;
import java.util.List;

import com.velorise.teyvatbosses.entity.AnemoCompanionEntity;
import com.velorise.teyvatbosses.registry.TeyvatBossesEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;


/**
 * Single Anemo Hypostasis shell cube dropped (2–4 per kill) by Anemo Hypostasis.
 * Right-click a single block on the ground 8 times to assemble an 8-cube
 * {@link AnemoCompanionEntity} pet bound permanently to the player who completes it.
 */
public class AnemoPrismCubeItem extends Item {

    public AnemoPrismCubeItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        Level level = ctx.getLevel();
        Player player = ctx.getPlayer();
        if (player == null) return InteractionResult.PASS;

        if (!level.isClientSide() && level instanceof ServerLevel sl) {
            if (AnemoCompanionEntity.hasActiveCompanion(sl, player.getUUID())) {
                player.displayClientMessage(Component.literal("You can only own 1 Anemo Cube Pet at a time!")
                        .withStyle(ChatFormatting.RED), true);
                return InteractionResult.FAIL;
            }
            BlockPos pos = ctx.getClickedPos().relative(ctx.getClickedFace());
            double cx = pos.getX() + 0.5D;
            double cy = pos.getY() + 0.15D;
            double cz = pos.getZ() + 0.5D;

            // If an unfinished Anemo Companion is already being assembled on this block, add directly to it!
            AABB checkBox = new AABB(cx - 1.1D, cy - 1.1D, cz - 1.1D, cx + 1.1D, cy + 1.6D, cz + 1.1D);
            List<AnemoCompanionEntity> existing = sl.getEntitiesOfClass(
                    AnemoCompanionEntity.class, checkBox, e -> e.isAlive() && !e.isFullyAssembled()
            );
            if (!existing.isEmpty()) {
                existing.get(0).addAssemblyCube(sl, player, ctx.getItemInHand());
                return InteractionResult.CONSUME;
            }

            // Otherwise deploy the 1st cube (1 / 8) right on this block
            AnemoCompanionEntity pet = TeyvatBossesEntities.anemoCompanion().create(sl);
            if (pet == null) return InteractionResult.PASS;
            float yaw = player.getYRot();
            pet.moveTo(cx, cy, cz, yaw, 0.0F);
            pet.setYBodyRot(yaw);
            pet.setYHeadRot(yaw);
            pet.initAssembly();
            sl.addFreshEntity(pet);

            ItemStack stack = ctx.getItemInHand();
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            sl.playSound(null, cx, cy + 0.4D, cz, SoundEvents.AMETHYST_BLOCK_PLACE, SoundSource.PLAYERS, 1.0F, 1.10F);
            sl.sendParticles(ParticleTypes.END_ROD, cx, cy + 0.5D, cz, 10, 0.25D, 0.25D, 0.25D, 0.04D);
            player.displayClientMessage(Component.translatable("message.velorise_teyvat_bosses.anemo_pet.placed"), true);
            return InteractionResult.CONSUME;
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("tooltip.velorise_teyvat_bosses.anemo_cube").withStyle(ChatFormatting.GRAY));
    }
}

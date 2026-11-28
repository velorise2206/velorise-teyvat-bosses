package com.velorise.teyvatbosses.item;

import com.velorise.teyvatbosses.entity.GoldflameQucusaurMountEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.Comparator;
import java.util.List;

/**
 * Phlogiston Flute — melodic Natlan command flute for summoning and directing
 * your hatched {@link GoldflameQucusaurMountEntity}:
 * <ul>
 *   <li><b>Right-Click:</b> Blows a summoning melody that calls your nearest dragon (within 64 blocks) to fly to you and land.</li>
 *   <li><b>Shift + Right-Click:</b> Toggles your dragon between <b>Follow</b> and <b>Stay</b> modes.</li>
 *   <li><b>Right-Click on a Mob:</b> Commands your dragon to spit a Goldflame Fireball at that target.</li>
 * </ul>
 */
public class PhlogistonFluteItem extends Item {

    public PhlogistonFluteItem(Properties properties) {
        super(properties);
    }

    private static GoldflameQucusaurMountEntity findNearestMount(ServerLevel sl, Player player) {
        AABB box = player.getBoundingBox().inflate(64.0D);
        List<GoldflameQucusaurMountEntity> mounts = sl.getEntitiesOfClass(
                GoldflameQucusaurMountEntity.class,
                box,
                m -> m.isAlive() && (m.isOwnedBy(player) || player.isCreative())
        );
        return mounts.stream()
                .min(Comparator.comparingDouble(m -> m.distanceToSqr(player)))
                .orElse(null);
    }

    private static void playFluteNotes(ServerLevel sl, Player player, boolean highPitch) {
        sl.playSound(null, player.getX(), player.getY() + 1.4D, player.getZ(),
                SoundEvents.NOTE_BLOCK_FLUTE.value(), SoundSource.PLAYERS, 1.25F, highPitch ? 1.35F : 1.05F);
        sl.sendParticles(ParticleTypes.NOTE, player.getX(), player.getY() + 2.1D, player.getZ(),
                4, 0.45D, 0.25D, 0.45D, 0.02D);
        sl.sendParticles(ParticleTypes.FLAME, player.getX(), player.getY() + 1.6D, player.getZ(),
                6, 0.35D, 0.25D, 0.35D, 0.02D);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide()) {
            return InteractionResultHolder.sidedSuccess(stack, true);
        }
        ServerLevel sl = (ServerLevel) level;
        GoldflameQucusaurMountEntity mount = findNearestMount(sl, player);
        if (mount == null) {
            playFluteNotes(sl, player, false);
            player.displayClientMessage(Component.translatable("message.velorise_teyvat_bosses.phlogiston_flute.no_dragon")
                    .withStyle(ChatFormatting.YELLOW), true);
            player.getCooldowns().addCooldown(this, 16);
            return InteractionResultHolder.consume(stack);
        }

        if (player.isShiftKeyDown()) {
            boolean nowFollowing = mount.toggleFollowMode();
            playFluteNotes(sl, player, nowFollowing);
            String key = nowFollowing
                    ? "message.velorise_teyvat_bosses.phlogiston_flute.follow"
                    : "message.velorise_teyvat_bosses.phlogiston_flute.stay";
            player.displayClientMessage(Component.translatable(key).withStyle(ChatFormatting.GOLD), true);
        } else {
            mount.callToPlayer(sl, player);
            playFluteNotes(sl, player, true);
            player.displayClientMessage(Component.translatable("message.velorise_teyvat_bosses.phlogiston_flute.called")
                    .withStyle(ChatFormatting.GOLD), true);
        }
        player.getCooldowns().addCooldown(this, 20);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        if (target instanceof GoldflameQucusaurMountEntity) {
            return InteractionResult.PASS;
        }
        if (player.level().isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        ServerLevel sl = (ServerLevel) player.level();
        GoldflameQucusaurMountEntity mount = findNearestMount(sl, player);
        if (mount != null && target.isAlive() && target != player) {
            mount.commandFireballAt(sl, target);
            playFluteNotes(sl, player, true);
            player.displayClientMessage(Component.translatable("message.velorise_teyvat_bosses.phlogiston_flute.attack",
                    target.getDisplayName()).withStyle(ChatFormatting.GOLD), true);
            player.getCooldowns().addCooldown(this, 24);
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.velorise_teyvat_bosses.phlogiston_flute").withStyle(ChatFormatting.GOLD));
    }
}

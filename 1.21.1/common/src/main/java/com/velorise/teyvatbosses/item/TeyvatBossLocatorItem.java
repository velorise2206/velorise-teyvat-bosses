package com.velorise.teyvatbosses.item;

import com.velorise.teyvatbosses.TeyvatBossesConstants;
import com.velorise.teyvatbosses.config.TeyvatBossesConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * Teyvat Boss Locator Compass — reusable survival structure locator for all 5 Teyvat Boss Arenas.
 * <ul>
 *   <li><b>Shift + Right-Click:</b> Cycles target Boss Arena (Cryo Regisvine, Anemo Hypostasis,
 *       Ruin Guard, Ruin Nautilus, Primo Geovishap).</li>
 *   <li><b>Right-Click:</b> Scans for the nearest matching arena, launches a guiding star-particle
 *       stream toward it, and reports coordinates, distance, and cardinal direction.</li>
 * </ul>
 */
public class TeyvatBossLocatorItem extends Item {

    private static final String TAG_TARGET_INDEX = "TargetBossIndex";

    private record BossTarget(String tagName, String langKey, ChatFormatting color) {}

    private static final BossTarget[] TARGETS = {
            new BossTarget("cryo_regisvine_arena", "entity.velorise_teyvat_bosses.cryo_regisvine", ChatFormatting.AQUA),
            new BossTarget("anemo_hypostasis_arena", "entity.velorise_teyvat_bosses.anemo_hypostasis", ChatFormatting.GREEN),
            new BossTarget("thousand_winds_temple_arena", "entity.velorise_teyvat_bosses.ruin_guard", ChatFormatting.GOLD),
            new BossTarget("ruin_nautilus_arena", "entity.velorise_teyvat_bosses.ruin_nautilus", ChatFormatting.DARK_AQUA),
            new BossTarget("primo_geovishap_arena", "entity.velorise_teyvat_bosses.primo_geovishap", ChatFormatting.YELLOW)
    };

    public TeyvatBossLocatorItem(Properties properties) {
        super(properties);
    }

    private static int getTargetIndex(ItemStack stack) {
        CustomData cd = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag tag = cd.copyTag();
        int idx = tag.getInt(TAG_TARGET_INDEX);
        return Math.floorMod(idx, TARGETS.length);
    }

    private static void setTargetIndex(ItemStack stack, int idx) {
        int wrapped = Math.floorMod(idx, TARGETS.length);
        CustomData cd = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag tag = cd.copyTag();
        tag.putInt(TAG_TARGET_INDEX, wrapped);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        int idx = getTargetIndex(stack);

        if (player.isShiftKeyDown()) {
            int next = (idx + 1) % TARGETS.length;
            setTargetIndex(stack, next);
            BossTarget target = TARGETS[next];
            if (!level.isClientSide()) {
                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.PLAYERS, 0.7F, 1.25F);
                player.displayClientMessage(
                        Component.translatable("message.velorise_teyvat_bosses.locator.selected",
                                Component.translatable(target.langKey()).withStyle(target.color(), ChatFormatting.BOLD)),
                        true
                );
            }
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
        }

        if (!level.isClientSide() && level instanceof ServerLevel sl) {
            BossTarget target = TARGETS[idx];
            TagKey<Structure> tagKey = TagKey.create(Registries.STRUCTURE, TeyvatBossesConstants.id(target.tagName()));
            int radiusChunks = TeyvatBossesConfig.get().locatorSearchRadiusChunks;
            BlockPos found = sl.findNearestMapStructure(tagKey, player.blockPosition(), radiusChunks, false);

            player.getCooldowns().addCooldown(this, 40);
            if (found != null) {
                double dx = found.getX() - player.getX();
                double dz = found.getZ() - player.getZ();
                int distBlocks = (int) Math.round(Math.hypot(dx, dz));
                String cardinal = toCardinal(dx, dz);

                sl.playSound(null, player.getX(), player.getY() + 1.0D, player.getZ(),
                        SoundEvents.ENDER_EYE_LAUNCH, SoundSource.PLAYERS, 0.9F, 1.15F);

                // Spawn glowing guiding star trail flying toward the located Boss Arena
                Vec3 dir = new Vec3(dx, 0.0D, dz);
                if (dir.lengthSqr() > 1.0E-4D) {
                    dir = dir.normalize();
                    Vec3 eye = player.getEyePosition();
                    for (int i = 1; i <= 18; i++) {
                        double step = i * 0.48D;
                        double lift = Math.sin((i / 18.0D) * Math.PI) * 0.85D;
                        sl.sendParticles(ParticleTypes.END_ROD,
                                eye.x + dir.x * step, eye.y + lift, eye.z + dir.z * step,
                                1, 0.03D, 0.03D, 0.03D, 0.01D);
                        if (i % 3 == 0) {
                            sl.sendParticles(ParticleTypes.ENCHANT,
                                    eye.x + dir.x * step, eye.y + lift, eye.z + dir.z * step,
                                    2, 0.10D, 0.10D, 0.10D, 0.02D);
                        }
                    }
                }

                player.displayClientMessage(
                        Component.translatable("message.velorise_teyvat_bosses.locator.found",
                                Component.translatable(target.langKey()).withStyle(target.color(), ChatFormatting.BOLD),
                                found.getX(), found.getZ(), distBlocks, cardinal),
                        true
                );
            } else {
                sl.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.VILLAGER_NO, SoundSource.PLAYERS, 0.7F, 0.95F);
                player.displayClientMessage(
                        Component.translatable("message.velorise_teyvat_bosses.locator.not_found",
                                Component.translatable(target.langKey()).withStyle(target.color())),
                        true
                );
            }
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    private static String toCardinal(double dx, double dz) {
        double deg = (Math.toDegrees(Math.atan2(dz, dx)) + 450.0D) % 360.0D;
        int octant = (int) Math.round(deg / 45.0D) % 8;
        return switch (octant) {
            case 0 -> "N";
            case 1 -> "NE";
            case 2 -> "E";
            case 3 -> "SE";
            case 4 -> "S";
            case 5 -> "SW";
            case 6 -> "W";
            default -> "NW";
        };
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        BossTarget target = TARGETS[getTargetIndex(stack)];
        tooltip.add(Component.translatable("tooltip.velorise_teyvat_bosses.locator.target",
                Component.translatable(target.langKey()).withStyle(target.color(), ChatFormatting.BOLD))
                .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.velorise_teyvat_bosses.locator.usage")
                .withStyle(ChatFormatting.DARK_GRAY));
    }
}

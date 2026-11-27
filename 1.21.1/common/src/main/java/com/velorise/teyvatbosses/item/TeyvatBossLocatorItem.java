package com.velorise.teyvatbosses.item;

import com.velorise.teyvatbosses.TeyvatBossesConstants;
import com.velorise.teyvatbosses.config.TeyvatBossesConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
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
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Teyvat Boss Locator Compass — reusable survival structure locator with live 360-degree needle tracking.
 * <ul>
 *   <li>Dedicated boss compasses ({@code fixedTargetIndex >= 0}) track one specific Teyvat Boss Arena.</li>
 *   <li>The universal compass ({@code fixedTargetIndex < 0}) cycles all 5 Boss Arenas via Shift + Right-Click.</li>
 *   <li>Right-Click scans for the nearest matching arena, locks its {@link GlobalPos} onto the item so the
 *       compass needle points directly toward it, launches a guiding star trail, and reports coordinates.</li>
 * </ul>
 */
public class TeyvatBossLocatorItem extends Item {

    private static final String TAG_TARGET_INDEX = "TargetBossIndex";
    private static final String TAG_LOCKED = "HasLockedPos";
    private static final String TAG_LOCKED_X = "LockedX";
    private static final String TAG_LOCKED_Y = "LockedY";
    private static final String TAG_LOCKED_Z = "LockedZ";
    private static final String TAG_LOCKED_DIM = "LockedDim";

    private record BossTarget(String tagName, String langKey, ChatFormatting color) {}

    private static final BossTarget[] TARGETS = {
            new BossTarget("cryo_regisvine_arena", "entity.velorise_teyvat_bosses.cryo_regisvine", ChatFormatting.AQUA),
            new BossTarget("anemo_hypostasis_arena", "entity.velorise_teyvat_bosses.anemo_hypostasis", ChatFormatting.GREEN),
            new BossTarget("thousand_winds_temple_arena", "entity.velorise_teyvat_bosses.ruin_guard", ChatFormatting.GOLD),
            new BossTarget("ruin_nautilus_arena", "entity.velorise_teyvat_bosses.ruin_nautilus", ChatFormatting.DARK_AQUA),
            new BossTarget("primo_geovishap_arena", "entity.velorise_teyvat_bosses.primo_geovishap", ChatFormatting.YELLOW)
    };

    private record CachedScan(@Nullable BlockPos pos, long expiresMs) {}
    private static final Map<String, CachedScan> LOCATOR_CACHE = new ConcurrentHashMap<>();
    private static final long CACHE_TTL_MS = 300_000L; // 5 minutes per 256x256 block region
    private static final long GLOBAL_SCAN_COOLDOWN_MS = 1_000L;
    private static volatile long lastGlobalScanMs = 0L;

    private final int fixedTargetIndex;

    public TeyvatBossLocatorItem(Properties properties) {
        this(properties, -1);
    }

    public TeyvatBossLocatorItem(Properties properties, int fixedTargetIndex) {
        super(properties);
        this.fixedTargetIndex = fixedTargetIndex;
    }

    private int resolveTargetIndex(ItemStack stack) {
        if (fixedTargetIndex >= 0 && fixedTargetIndex < TARGETS.length) {
            return fixedTargetIndex;
        }
        CustomData cd = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag tag = cd.copyTag();
        return Math.floorMod(tag.getInt(TAG_TARGET_INDEX), TARGETS.length);
    }

    private static void setTargetIndex(ItemStack stack, int idx) {
        int wrapped = Math.floorMod(idx, TARGETS.length);
        CustomData cd = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag tag = cd.copyTag();
        tag.putInt(TAG_TARGET_INDEX, wrapped);
        tag.putBoolean(TAG_LOCKED, false);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    private static void setLockedTarget(ItemStack stack, ResourceKey<Level> dim, BlockPos pos) {
        CustomData cd = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag tag = cd.copyTag();
        tag.putBoolean(TAG_LOCKED, true);
        tag.putInt(TAG_LOCKED_X, pos.getX());
        tag.putInt(TAG_LOCKED_Y, pos.getY());
        tag.putInt(TAG_LOCKED_Z, pos.getZ());
        tag.putString(TAG_LOCKED_DIM, dim.location().toString());
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    /** Returns the locked arena GlobalPos for client-side live 360-degree needle rotation, or null if not yet scanned. */
    @Nullable
    public static GlobalPos getLockedTarget(ItemStack stack) {
        CustomData cd = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag tag = cd.copyTag();
        if (!tag.getBoolean(TAG_LOCKED)) {
            return null;
        }
        ResourceLocation dimId = ResourceLocation.tryParse(tag.getString(TAG_LOCKED_DIM));
        if (dimId == null) {
            return null;
        }
        ResourceKey<Level> dimKey = ResourceKey.create(Registries.DIMENSION, dimId);
        BlockPos pos = new BlockPos(tag.getInt(TAG_LOCKED_X), tag.getInt(TAG_LOCKED_Y), tag.getInt(TAG_LOCKED_Z));
        return GlobalPos.of(dimKey, pos);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        int idx = resolveTargetIndex(stack);

        if (fixedTargetIndex < 0 && player.isShiftKeyDown()) {
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
            int regionX = player.blockPosition().getX() >> 8;
            int regionZ = player.blockPosition().getZ() >> 8;
            String cacheKey = sl.getSeed() + "|" + sl.dimension().location() + "|" + target.tagName()
                    + "|" + regionX + "|" + regionZ + "|" + radiusChunks;
            long nowMs = System.currentTimeMillis();
            CachedScan cached = LOCATOR_CACHE.get(cacheKey);
            if (cached != null && cached.expiresMs() > nowMs) {
                player.getCooldowns().addCooldown(this, 120);
                deliverScanResult(sl, player, stack, target, cached.pos());
            } else {
                if (nowMs - lastGlobalScanMs < GLOBAL_SCAN_COOLDOWN_MS) {
                    player.getCooldowns().addCooldown(this, 25);
                    player.displayClientMessage(
                            Component.translatable("message.velorise_teyvat_bosses.locator.busy")
                                    .withStyle(ChatFormatting.YELLOW),
                            true
                    );
                    return InteractionResultHolder.sidedSuccess(stack, false);
                }
                lastGlobalScanMs = nowMs;
                player.getCooldowns().addCooldown(this, 120);
                BlockPos origin = player.blockPosition().immutable();
                // Execute directly on the Server thread (matching vanilla EnderEyeItem) to avoid off-thread
                // ServerChunkCache.getChunk().join() stalls/deadlocks and permanently stuck player locks.
                BlockPos foundPos = null;
                boolean succeeded = false;
                try {
                    foundPos = sl.findNearestMapStructure(tagKey, origin, radiusChunks, false);
                    succeeded = true;
                } catch (Throwable t) {
                    TeyvatBossesConstants.LOGGER.error("Boss Locator scan failed for {} at {} (radius={} chunks)",
                            target.tagName(), origin, radiusChunks, t);
                }
                if (succeeded) {
                    if (LOCATOR_CACHE.size() > 256) LOCATOR_CACHE.clear();
                    LOCATOR_CACHE.put(cacheKey, new CachedScan(foundPos, System.currentTimeMillis() + CACHE_TTL_MS));
                }
                deliverScanResult(sl, player, stack, target, foundPos);
            }
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    private static void deliverScanResult(ServerLevel sl, Player player, ItemStack stack, BossTarget target, @Nullable BlockPos found) {
        if (found != null) {
            if (stack.getItem() instanceof TeyvatBossLocatorItem) {
                setLockedTarget(stack, sl.dimension(), found);
            }
            double dx = found.getX() - player.getX();
            double dz = found.getZ() - player.getZ();
            int distBlocks = (int) Math.round(Math.hypot(dx, dz));
            String cardinal = toCardinal(dx, dz);

            sl.playSound(null, player.getX(), player.getY() + 1.0D, player.getZ(),
                    SoundEvents.ENDER_EYE_LAUNCH, SoundSource.PLAYERS, 0.9F, 1.15F);

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
        BossTarget target = TARGETS[resolveTargetIndex(stack)];
        tooltip.add(Component.translatable("tooltip.velorise_teyvat_bosses.locator.target",
                Component.translatable(target.langKey()).withStyle(target.color(), ChatFormatting.BOLD))
                .withStyle(ChatFormatting.GRAY));
        GlobalPos locked = getLockedTarget(stack);
        if (locked != null) {
            tooltip.add(Component.translatable("tooltip.velorise_teyvat_bosses.locator.locked",
                    locked.pos().getX(), locked.pos().getZ()).withStyle(ChatFormatting.AQUA));
        }
        tooltip.add(Component.translatable(fixedTargetIndex < 0
                        ? "tooltip.velorise_teyvat_bosses.locator.usage"
                        : "tooltip.velorise_teyvat_bosses.locator.usage_single")
                .withStyle(ChatFormatting.DARK_GRAY));
    }
}

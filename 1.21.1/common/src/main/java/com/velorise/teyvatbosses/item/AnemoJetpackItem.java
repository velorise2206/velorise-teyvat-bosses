package com.velorise.teyvatbosses.item;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Wearable Anemo Jet-Wing Jetpack crafted from the Anemo Hypostasis Core Cube ({@code anemo_prism_cube}).
 * <ul>
 *   <li>Equip in the Chestplate slot.</li>
 *   <li>Hold <b>Space</b> while mid-air to ignite dual Anemo wind thrusters (vertical lift + forward boost).</li>
 *   <li>Hold <b>Shift</b> while falling to wind-glide softly; grants complete fall-damage immunity.</li>
 *   <li>Self-recharges wind pressure rapidly whenever standing on solid ground (100 max pressure, never breaks).</li>
 * </ul>
 */
public class AnemoJetpackItem extends ArmorItem {
    public static final int MAX_PRESSURE = 100;
    private static final Map<UUID, Boolean> BOOSTING_PLAYERS = new ConcurrentHashMap<>();

    public AnemoJetpackItem(Properties properties) {
        super(ArmorMaterials.DIAMOND, Type.CHESTPLATE, properties.durability(MAX_PRESSURE));
    }

    public static void setBoosting(UUID playerId, boolean boosting) {
        if (boosting) {
            BOOSTING_PLAYERS.put(playerId, true);
        } else {
            BOOSTING_PLAYERS.remove(playerId);
        }
    }

    public static boolean isBoosting(UUID playerId) {
        return BOOSTING_PLAYERS.getOrDefault(playerId, false);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return stack.getDamageValue() > 0;
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return 0x55FFD8; // Anemo turquoise glow
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (!(entity instanceof Player player)) return;
        if (player.getItemBySlot(EquipmentSlot.CHEST) != stack) return;

        // Zero fall damage while wearing the Anemo Jet-Wing
        player.resetFallDistance();

        int used = stack.getDamageValue();
        int remaining = MAX_PRESSURE - used;

        // Fast ground recharge (+3 pressure/tick -> ~1.7s full recharge)
        if (player.onGround()) {
            if (!level.isClientSide() && used > 0) {
                stack.setDamageValue(Math.max(0, used - 3));
            }
            return;
        }

        // Gentle wind-glide descent when holding Sneak in mid-air
        if (player.isShiftKeyDown() && !isBoosting(player.getUUID())) {
            Vec3 vel = player.getDeltaMovement();
            if (vel.y < -0.22D) {
                player.setDeltaMovement(vel.x * 1.01D, -0.22D, vel.z * 1.01D);
                if (level instanceof ServerLevel sl && player.tickCount % 3 == 0) {
                    sl.sendParticles(ParticleTypes.CLOUD, player.getX(), player.getY() + 0.5D, player.getZ(), 2, 0.35D, 0.05D, 0.35D, 0.01D);
                }
            }
            return;
        }

        // Active Anemo Jet-Wing Thrust while holding Space airborne
        if (isBoosting(player.getUUID()) && remaining > 1) {
            Vec3 look = player.getLookAngle();
            Vec3 vel = player.getDeltaMovement();
            double ny = Math.min(0.54D, vel.y + 0.135D);
            double nx = vel.x + look.x * 0.055D;
            double nz = vel.z + look.z * 0.055D;
            double horizSq = nx * nx + nz * nz;
            if (horizSq > 0.72D * 0.72D) {
                double scale = 0.72D / Math.sqrt(horizSq);
                nx *= scale;
                nz *= scale;
            }
            player.setDeltaMovement(nx, ny, nz);
            player.hurtMarked = true;

            if (!level.isClientSide() && level instanceof ServerLevel sl) {
                if (!player.getAbilities().instabuild) {
                    stack.setDamageValue(Math.min(MAX_PRESSURE - 1, used + 1));
                }
                if (player.tickCount % 2 == 0) {
                    sl.sendParticles(ParticleTypes.CLOUD, player.getX(), player.getY() + 0.35D, player.getZ(), 4, 0.32D, 0.12D, 0.32D, 0.04D);
                    sl.sendParticles(ParticleTypes.END_ROD, player.getX(), player.getY() + 0.45D, player.getZ(), 2, 0.25D, 0.10D, 0.25D, 0.03D);
                }
                if (player.tickCount % 7 == 0) {
                    sl.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BREEZE_WIND_CHARGE_BURST.value(), SoundSource.PLAYERS, 0.35F, 1.45F);
                }
            }
        }
    }
}

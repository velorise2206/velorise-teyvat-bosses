package com.velorise.teyvatbosses.compat;

import com.velorise.teyvatbosses.TeyvatBossesConstants;
import com.velorise.teyvatbosses.entity.CryoTurretEntity;
import com.velorise.teyvatbosses.entity.RuinMechEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;
import snownee.jade.api.config.IPluginConfig;

/**
 * Native Jade (WAILA) integration for Velorise: Teyvat Bosses (NeoForge & Fabric 1.21.1).
 * Displays live Reactor Energy, Armor Integrity, Missile Pod Ammo, and Assembly progress
 * when looking at a Ruin Mech or Cryo Regisvine Turret.
 */
@WailaPlugin
public class TeyvatBossesJadePlugin implements IWailaPlugin {
    private static final ResourceLocation RUIN_MECH_UID = TeyvatBossesConstants.id("ruin_mech_info");
    private static final ResourceLocation CRYO_TURRET_UID = TeyvatBossesConstants.id("cryo_turret_info");

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerEntityComponent(RuinMechProvider.INSTANCE, RuinMechEntity.class);
        registration.registerEntityComponent(CryoTurretProvider.INSTANCE, CryoTurretEntity.class);
    }

    private enum RuinMechProvider implements IEntityComponentProvider {
        INSTANCE;

        @Override
        public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig config) {
            if (!(accessor.getEntity() instanceof RuinMechEntity mech)) return;

            if (!mech.isFullyAssembled()) {
                int parts = Integer.bitCount(mech.getPartsMask() & RuinMechEntity.FULL_PARTS);
                tooltip.add(Component.translatable("jade.velorise_teyvat_bosses.assembly", parts, 5)
                        .withStyle(ChatFormatting.GOLD));
                return;
            }

            int energy = Math.round(mech.getEnergy());
            int maxEnergy = Math.round(RuinMechEntity.MAX_ENERGY);
            int cells = Mth.clamp((int) Math.ceil(mech.getEnergy() / RuinMechEntity.ENERGY_PER_BLOCK),
                    0, RuinMechEntity.MAX_REDSTONE_CELLS);
            ChatFormatting energyCol = cells <= 1 ? ChatFormatting.RED : ChatFormatting.GOLD;
            tooltip.add(Component.translatable("jade.velorise_teyvat_bosses.energy", energy, maxEnergy, cells, RuinMechEntity.MAX_REDSTONE_CELLS)
                    .withStyle(energyCol));

            int armor = Math.round(mech.getArmorIntegrity());
            ChatFormatting armorCol = armor < 30 ? ChatFormatting.RED : ChatFormatting.AQUA;
            tooltip.add(Component.translatable("jade.velorise_teyvat_bosses.armor", armor)
                    .withStyle(armorCol));

            int ammo = mech.getMissileAmmo();
            tooltip.add(Component.translatable("jade.velorise_teyvat_bosses.missiles", ammo, RuinMechEntity.MAX_MISSILE_AMMO)
                    .withStyle(ammo > 0 ? ChatFormatting.YELLOW : ChatFormatting.GRAY));
        }

        @Override
        public ResourceLocation getUid() {
            return RUIN_MECH_UID;
        }
    }

    private enum CryoTurretProvider implements IEntityComponentProvider {
        INSTANCE;

        @Override
        public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig config) {
            if (!(accessor.getEntity() instanceof CryoTurretEntity turret)) return;
            if (!turret.isFullyAssembled()) {
                int parts = Integer.bitCount(turret.getPartsMask() & CryoTurretEntity.FULL_PARTS);
                tooltip.add(Component.translatable("jade.velorise_teyvat_bosses.assembly", parts, 3)
                        .withStyle(ChatFormatting.AQUA));
            } else {
                tooltip.add(Component.translatable("jade.velorise_teyvat_bosses.turret_active")
                        .withStyle(ChatFormatting.AQUA));
            }
        }

        @Override
        public ResourceLocation getUid() {
            return CRYO_TURRET_UID;
        }
    }
}

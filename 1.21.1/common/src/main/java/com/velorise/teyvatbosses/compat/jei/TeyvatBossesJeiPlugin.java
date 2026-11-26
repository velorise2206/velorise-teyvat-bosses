package com.velorise.teyvatbosses.compat.jei;

import com.velorise.teyvatbosses.TeyvatBossesConstants;
import com.velorise.teyvatbosses.registry.TeyvatBossesItems;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

/**
 * JEI Information Plugin for Velorise: Teyvat Bosses (shared across NeoForge 1.21.1 &amp; Fabric 1.21.1).
 * Registers clear drop source, outdoor assembly instructions, and control guides for all modular
 * salvage parts (Ruin Submersible, Ruin Mech, Cryo Turret, and Anemo Cube Companion).
 */
@JeiPlugin
public final class TeyvatBossesJeiPlugin implements IModPlugin {
    private static final ResourceLocation UID = TeyvatBossesConstants.id("jei_plugin");

    @Override
    public ResourceLocation getPluginUid() {
        return UID;
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        // 1. Ruin Submersible (4 modular parts)
        addInfo(registration, TeyvatBossesItems.ruinSubHull(), "jei.velorise_teyvat_bosses.ruin_sub");
        addInfo(registration, TeyvatBossesItems.ruinSubCockpit(), "jei.velorise_teyvat_bosses.ruin_sub");
        addInfo(registration, TeyvatBossesItems.ruinSubPropeller(), "jei.velorise_teyvat_bosses.ruin_sub");
        addInfo(registration, TeyvatBossesItems.ruinSubTorpedoPod(), "jei.velorise_teyvat_bosses.ruin_sub");

        // 2. Pilotable Ruin Mech (5 modular parts)
        addInfo(registration, TeyvatBossesItems.mechCore(), "jei.velorise_teyvat_bosses.ruin_mech");
        addInfo(registration, TeyvatBossesItems.mechLeftArm(), "jei.velorise_teyvat_bosses.ruin_mech");
        addInfo(registration, TeyvatBossesItems.mechRightArm(), "jei.velorise_teyvat_bosses.ruin_mech");
        addInfo(registration, TeyvatBossesItems.mechLeftLeg(), "jei.velorise_teyvat_bosses.ruin_mech");
        addInfo(registration, TeyvatBossesItems.mechRightLeg(), "jei.velorise_teyvat_bosses.ruin_mech");

        // 3. Cryo Turret (3 modular parts)
        addInfo(registration, TeyvatBossesItems.cryoTurretRoot(), "jei.velorise_teyvat_bosses.cryo_turret");
        addInfo(registration, TeyvatBossesItems.cryoTurretStem(), "jei.velorise_teyvat_bosses.cryo_turret");
        addInfo(registration, TeyvatBossesItems.cryoTurretCorolla(), "jei.velorise_teyvat_bosses.cryo_turret");

        // 4. Anemo Cube Companion (8 cubes assembled on 1 block)
        addInfo(registration, TeyvatBossesItems.anemoPrismCube(), "jei.velorise_teyvat_bosses.anemo_cube");

        // 5. Geo Tunnel Driller (3 modular parts + 1-2 vanilla Chests)
        addInfo(registration, TeyvatBossesItems.geoDrillerChassis(), "jei.velorise_teyvat_bosses.geo_driller");
        addInfo(registration, TeyvatBossesItems.geoDrillerDrill(), "jei.velorise_teyvat_bosses.geo_driller");
        addInfo(registration, TeyvatBossesItems.geoDrillerEngine(), "jei.velorise_teyvat_bosses.geo_driller");
    }

    private static void addInfo(IRecipeRegistration reg, Item item, String key) {
        if (item != null) {
            reg.addIngredientInfo(item, Component.translatable(key));
        }
    }
}

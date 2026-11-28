package com.velorise.teyvatbosses.compat.jei;

import com.velorise.teyvatbosses.TeyvatBossesConstants;
import com.velorise.teyvatbosses.item.AnemoDoubleJumpHelper;
import com.velorise.teyvatbosses.registry.TeyvatBossesItems;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * JEI Information &amp; Custom Recipe Display Plugin for Velorise: Teyvat Bosses
 * (shared across NeoForge 1.21.1 &amp; Fabric 1.21.1).
 *
 * <p>Registers:
 * <ul>
 *   <li>Crafting grid previews for {@code [Anemo Prism Cube][Boots][Anemo Prism Cube]} Windborne Double Jump infusion.</li>
 *   <li>Drop sources, outdoor assembly instructions, and control guides for all modular vehicles,
 *       turrets, companions, armor sets, Goldflame Qucusaur mount items, fuel blocks, and boss music discs.</li>
 * </ul>
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
        // 0. Register visible Crafting Table recipes for the custom Anemo Boots Infusion ([Cube][Boots][Cube])
        registerAnemoBootsInfusionPreviews(registration);

        // 1. Ruin Submersible (3 modular parts)
        addInfo(registration, TeyvatBossesItems.ruinSubHull(), "jei.velorise_teyvat_bosses.ruin_sub");
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

        // 4. Anemo Cube Companion (8 cubes assembled on 1 block) & Anemo Boots Infusion
        addInfo(registration, TeyvatBossesItems.anemoPrismCube(), "jei.velorise_teyvat_bosses.anemo_cube");

        // 5. Geo Tunnel Driller (crafted from Primordial Jade + 1-2 vanilla Chests)
        addInfo(registration, TeyvatBossesItems.geoDrillerChassis(), "jei.velorise_teyvat_bosses.geo_driller");
        addInfo(registration, TeyvatBossesItems.geoDrillerDrill(), "jei.velorise_teyvat_bosses.geo_driller");
        addInfo(registration, TeyvatBossesItems.geoDrillerEngine(), "jei.velorise_teyvat_bosses.geo_driller");

        // 6. Primordial Jade, Geo Scale Ingot & Primo Geovishap Dragon-Scale Armor
        addInfo(registration, TeyvatBossesItems.primordialJade(), "jei.velorise_teyvat_bosses.primordial_jade");
        addInfo(registration, TeyvatBossesItems.primordialGeoIngot(), "jei.velorise_teyvat_bosses.geovishap_armor");
        addInfo(registration, TeyvatBossesItems.geovishapHelmet(), "jei.velorise_teyvat_bosses.geovishap_armor");
        addInfo(registration, TeyvatBossesItems.geovishapChestplate(), "jei.velorise_teyvat_bosses.geovishap_armor");
        addInfo(registration, TeyvatBossesItems.geovishapLeggings(), "jei.velorise_teyvat_bosses.geovishap_armor");
        addInfo(registration, TeyvatBossesItems.geovishapBoots(), "jei.velorise_teyvat_bosses.geovishap_armor");

        // 7. Goldflame Qucusaur Mount Egg, Phlogiston Saddle, Crest & Mark of the Binding Blessing
        addInfo(registration, TeyvatBossesItems.goldflameQucusaurEgg(), "jei.velorise_teyvat_bosses.goldflame_egg");
        addInfo(registration, TeyvatBossesItems.phlogistonSaddle(), "jei.velorise_teyvat_bosses.phlogiston_saddle");
        addInfo(registration, TeyvatBossesItems.goldflameQucusaurCrest(), "jei.velorise_teyvat_bosses.goldflame_crest");
        addInfo(registration, TeyvatBossesItems.markOfBindingBlessing(), "jei.velorise_teyvat_bosses.mark_of_binding");

        // 8. High-Density Vehicle Fuel Block
        addInfo(registration, TeyvatBossesItems.condensedRedstoneBlock(), "jei.velorise_teyvat_bosses.condensed_redstone");

        // 9. All 6 Boss Music Discs
        addInfo(registration, TeyvatBossesItems.anemoDisc(), "jei.velorise_teyvat_bosses.music_discs");
        addInfo(registration, TeyvatBossesItems.cryoDisc(), "jei.velorise_teyvat_bosses.music_discs");
        addInfo(registration, TeyvatBossesItems.ruinGuardDisc(), "jei.velorise_teyvat_bosses.music_discs");
        addInfo(registration, TeyvatBossesItems.primoGeovishapDisc(), "jei.velorise_teyvat_bosses.music_discs");
        addInfo(registration, TeyvatBossesItems.ruinNautilusDisc(), "jei.velorise_teyvat_bosses.music_discs");
        addInfo(registration, TeyvatBossesItems.goldflameQucusaurDisc(), "jei.velorise_teyvat_bosses.music_discs");
    }

    private static void registerAnemoBootsInfusionPreviews(IRecipeRegistration registration) {
        Item cube = TeyvatBossesItems.anemoPrismCube();
        if (cube == null) return;

        List<Item> sampleBoots = new ArrayList<>();
        sampleBoots.add(Items.IRON_BOOTS);
        sampleBoots.add(Items.DIAMOND_BOOTS);
        sampleBoots.add(Items.NETHERITE_BOOTS);
        if (TeyvatBossesItems.geovishapBoots() != null) {
            sampleBoots.add(TeyvatBossesItems.geovishapBoots());
        }

        List<RecipeHolder<CraftingRecipe>> holders = new ArrayList<>();
        int idx = 0;
        for (Item boots : sampleBoots) {
            ShapedRecipePattern pattern = ShapedRecipePattern.of(
                    Map.of(
                            'C', Ingredient.of(cube),
                            'B', Ingredient.of(boots)
                    ),
                    List.of("CBC")
            );
            ItemStack result = AnemoDoubleJumpHelper.infuseBoots(new ItemStack(boots));
            ShapedRecipe recipe = new ShapedRecipe(
                    "anemo_boots_infusion",
                    CraftingBookCategory.EQUIPMENT,
                    pattern,
                    result
            );
            holders.add(new RecipeHolder<>(
                    TeyvatBossesConstants.id("jei_anemo_boots_infusion_" + (idx++)),
                    recipe
            ));
        }
        registration.addRecipes(RecipeTypes.CRAFTING, holders);
    }

    private static void addInfo(IRecipeRegistration reg, Item item, String key) {
        if (item != null) {
            reg.addIngredientInfo(item, Component.translatable(key));
        }
    }
}

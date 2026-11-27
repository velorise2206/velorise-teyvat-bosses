package com.velorise.teyvatbosses.recipe;

import com.velorise.teyvatbosses.item.AnemoDoubleJumpHelper;
import com.velorise.teyvatbosses.item.AnemoPrismCubeItem;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import java.util.function.Supplier;

/**
 * Custom crafting recipe infusing any vanilla or modded boots placed between two
 * {@link AnemoPrismCubeItem}s ({@code [Cube][Boots][Cube]}) with Windborne Double Jump,
 * preserving all enchantments, durability, trim, and custom data.
 */
public class AnemoBootsInfusionRecipe extends CustomRecipe {

    private static Supplier<RecipeSerializer<?>> serializerSupplier;

    public static void bindSerializer(Supplier<RecipeSerializer<?>> supplier) {
        serializerSupplier = supplier;
    }

    public AnemoBootsInfusionRecipe(CraftingBookCategory category) {
        super(category);
    }

    private static boolean isCube(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof AnemoPrismCubeItem;
    }

    private static boolean isInfusableBoots(ItemStack stack) {
        return !stack.isEmpty()
                && stack.getItem() instanceof ArmorItem armor
                && armor.getType() == ArmorItem.Type.BOOTS
                && !AnemoDoubleJumpHelper.hasAnemoDoubleJump(stack);
    }

    private static ItemStack findBootsInPattern(CraftingInput input) {
        if (input.width() < 3 || input.ingredientCount() != 3) {
            return ItemStack.EMPTY;
        }
        for (int r = 0; r < input.height(); r++) {
            for (int c = 0; c <= input.width() - 3; c++) {
                ItemStack left = input.getItem(c, r);
                ItemStack center = input.getItem(c + 1, r);
                ItemStack right = input.getItem(c + 2, r);
                if (isCube(left) && isInfusableBoots(center) && isCube(right)) {
                    return center;
                }
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        return !findBootsInPattern(input).isEmpty();
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        ItemStack boots = findBootsInPattern(input);
        return boots.isEmpty() ? ItemStack.EMPTY : AnemoDoubleJumpHelper.infuseBoots(boots);
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= 3 && height >= 1;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return serializerSupplier != null ? serializerSupplier.get() : null;
    }
}

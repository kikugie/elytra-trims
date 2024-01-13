package dev.kikugie.elytratrims.common.recipe;

import dev.kikugie.elytratrims.common.ETServer;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.world.World;


public class ElytraGlowRecipe extends SpecialCraftingRecipe {
    /*? if <1.20.2 {*/
    public ElytraGlowRecipe(net.minecraft.util.Identifier id, CraftingRecipeCategory category) {
        super(id, category);
    }
    /*?} else {*//*
    public ElytraGlowRecipe(CraftingRecipeCategory category) {
        super(category);
    }
    *//*?} */

    @Override
    public boolean matches(
            /*? if >=1.20 {*//*
            net.minecraft.inventory.RecipeInputInventory inventory,
            *//*?} else {*/
            net.minecraft.inventory.CraftingInventory inventory,
            /*?} */
            World world) {
        int item = 0;
        int sac = 0;

        for (int slot = 0; slot < inventory.size(); slot++) {
            ItemStack stack = inventory.getStack(slot);
            if (stack.getItem() instanceof GlowingItem) {
                item++;
            } else if (stack.getItem() == Items.GLOW_INK_SAC) {
                sac++;
            } else if (!stack.isEmpty()) {
                return false;
            }

            if (item > 1 || sac > 1) return false;
        }

        return item == 1 && sac == 1;
    }

    @Override
    public ItemStack craft(
            /*? if >=1.20 {*//*
            net.minecraft.inventory.RecipeInputInventory inventory,
            *//*?} else {*/
            net.minecraft.inventory.CraftingInventory inventory,
            /*?} */
            DynamicRegistryManager registryManager) {
        ItemStack item = ItemStack.EMPTY;
        for (int slot = 0; slot < inventory.size(); slot++) {
            ItemStack stack = inventory.getStack(slot);
            if (!(stack.getItem() instanceof ElytraItem)) continue;
            item = stack.copy();
        }
        ETServer.GLOWING.setGlow(item);
        return item;
    }

    @Override
    public boolean fits(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ETRecipeSerializers.ELYTRA_GLOW;
    }
}

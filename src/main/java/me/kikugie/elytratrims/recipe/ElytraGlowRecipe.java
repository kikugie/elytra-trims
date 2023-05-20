package me.kikugie.elytratrims.recipe;

import me.kikugie.elytratrims.ElytraTrimsMod;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class ElytraGlowRecipe extends SpecialCraftingRecipe {
    public ElytraGlowRecipe(Identifier id, CraftingRecipeCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(CraftingInventory inventory, World world) {
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
    public ItemStack craft(CraftingInventory inventory, DynamicRegistryManager registryManager) {
        ItemStack item = ItemStack.EMPTY;
        for (int slot = 0; slot < inventory.size(); slot++) {
            ItemStack stack = inventory.getStack(slot);
            if (!(stack.getItem() instanceof ElytraItem)) continue;
            item = stack.copy();
        }
        ElytraTrimsMod.GLOWING.setGlow(item);
        return item;
    }

    @Override
    public boolean fits(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ElytraTrimsMod.ELYTRA_GLOW_RECIPE;
    }
}

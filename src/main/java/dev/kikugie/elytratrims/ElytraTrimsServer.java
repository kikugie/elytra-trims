package dev.kikugie.elytratrims;

import dev.kikugie.elytratrims.recipe.ElytraGlowRecipe;
import dev.kikugie.elytratrims.recipe.ElytraPatternRecipe;
import dev.kikugie.elytratrims.recipe.GlowingItem;
import net.fabricmc.api.ModInitializer;
import net.minecraft.item.DyeableItem;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.SpecialRecipeSerializer;

public class ElytraTrimsServer implements ModInitializer {
    public static final RecipeSerializer<SpecialCraftingRecipe> ELYTRA_PATTERNS_RECIPE = RecipeSerializer.register("crafting_special_elytrapatterns", new SpecialRecipeSerializer<>(ElytraPatternRecipe::new));
    public static final RecipeSerializer<ElytraGlowRecipe> ELYTRA_GLOW_RECIPE = RecipeSerializer.register("crafting_special_elytraglow", new SpecialRecipeSerializer<>(ElytraGlowRecipe::new));
    public static final DyeableItem DYEABLE = new DyeableItem() {
    };
    public static final GlowingItem GLOWING = new GlowingItem() {
    };
    @Override
    public void onInitialize() {

    }
}

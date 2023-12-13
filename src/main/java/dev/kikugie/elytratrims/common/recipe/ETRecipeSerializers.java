package dev.kikugie.elytratrims.common.recipe;

import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialRecipeSerializer;

public class ETRecipeSerializers {
    public static final RecipeSerializer<ElytraPatternRecipe> ELYTRA_PATTERNS = new SpecialRecipeSerializer<>(ElytraPatternRecipe::new);
    public static final RecipeSerializer<ElytraGlowRecipe> ELYTRA_GLOW = new SpecialRecipeSerializer<>(ElytraGlowRecipe::new);
}
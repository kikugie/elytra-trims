package dev.kikugie.elytratrims.common.recipe;

import net.minecraft.recipe.SpecialRecipeSerializer;

public class ETRecipeSerializers {
    public static final SpecialRecipeSerializer<ElytraPatternRecipe> ELYTRA_PATTERNS = new SpecialRecipeSerializer<>(ElytraPatternRecipe::new);
    public static final SpecialRecipeSerializer<ElytraGlowRecipe> ELYTRA_GLOW = new SpecialRecipeSerializer<>(ElytraGlowRecipe::new);
}
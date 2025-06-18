package dev.kikugie.elytratrims.mixin.recipe;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.kikugie.elytratrims.recipe.ETSmithingRecipe;
import net.minecraft.world.item.crafting.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(RecipeManager.class)
public class RecipeManagerMixin {
    @Unique
    private static final String METHOD =
        /*? if fabric {*/"method_64689";
        /*?} else*//*"lambda$prepare$3";*/

    @SuppressWarnings("unchecked")
    @ModifyExpressionValue(method = METHOD, at = @At(value = "NEW", target = "(Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/world/item/crafting/Recipe;)Lnet/minecraft/world/item/crafting/RecipeHolder;"))
    private static RecipeHolder<?> modifyETRecipes(RecipeHolder<?> original) {
        return ETSmithingRecipe.modify((RecipeHolder<Recipe<?>>) original);
    }
}

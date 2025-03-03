package dev.kikugie.elytratrims.mixin.recipe;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.kikugie.elytratrims.recipe.ETSmithingRecipe;
import net.minecraft.world.item.crafting.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(RecipeManager.class)
public class RecipeManagerMixin {
    @SuppressWarnings("unchecked")
    //? if >=1.21.4 {
    @ModifyExpressionValue(
        method = "method_64689",
        at = @At(value = "NEW", target = "(Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/world/item/crafting/Recipe;)Lnet/minecraft/world/item/crafting/RecipeHolder;")
    )
    private static RecipeHolder<?> modifyETRecipes(RecipeHolder<?> original) {
        return ETSmithingRecipe.modify((RecipeHolder<Recipe<?>>) original);
    }//?} else {
    /*@ModifyExpressionValue(
        method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/RecipeHolder;<init>(Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/world/item/crafting/Recipe;)V")
    )
    private RecipeHolder<?> modifyETRecipes(RecipeHolder<?> original) {
        return ETSmithingRecipe.modify((RecipeHolder<Recipe<?>>) original);
    }
    *///?}
}

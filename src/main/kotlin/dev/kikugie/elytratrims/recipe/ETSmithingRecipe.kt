package dev.kikugie.elytratrims.recipe

import dev.kikugie.elytratrims.Identifier
import dev.kikugie.elytratrims.item.ETFlag
import dev.kikugie.elytratrims.item.banner
import dev.kikugie.elytratrims.item.flags
import net.minecraft.core.HolderLookup
import net.minecraft.core.component.DataComponentPatch
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeHolder
import net.minecraft.world.item.crafting.SmithingRecipe
import net.minecraft.world.item.crafting.SmithingRecipeInput
import net.minecraft.world.item.crafting.SmithingTransformRecipe
import net.minecraft.world.item.crafting.SmithingTrimRecipe
import net.minecraft.world.item.crafting.TransmuteResult

abstract class ETSmithingRecipe(delegate: SmithingRecipe) : SmithingTransformRecipe(
    delegate.templateIngredient(),
    delegate.baseIngredient(),
    delegate.additionIngredient(),
    TransmuteResult(BuiltInRegistries.ITEM.get(Identifier.withDefaultNamespace("elytra")).get(), 1, DataComponentPatch.EMPTY)
) {
    companion object {
        private infix fun RecipeHolder<*>.replace(recipe: Recipe<*>) = RecipeHolder(id, recipe)

        @JvmStatic
        fun modify(entry: RecipeHolder<Recipe<*>>): RecipeHolder<Recipe<*>> = when (val recipe = entry.value) {
            is SmithingRecipe -> when (entry.id.location().toString()) {
                "elytratrims:apply_shield_pattern" -> entry replace PatternsRecipe(recipe, false)
                "elytratrims:apply_banner_pattern" -> entry replace PatternsRecipe(recipe, true)
                "elytratrims:apply_animation_effect" -> entry replace FlagRecipe(recipe, true, ETFlag.ANIMATION)
                "elytratrims:apply_gateway_effect" -> entry replace FlagRecipe(recipe, true, ETFlag.GATEWAY)
                "elytratrims:apply_glow_effect" -> entry replace FlagRecipe(recipe, true, ETFlag.GLOW)
                else -> entry
            }

            else -> entry
        }
    }

    open class FlagRecipe(delegate: SmithingRecipe, private val setter: Boolean, private val flag: ETFlag) : ETSmithingRecipe(delegate) {
        override fun assemble(input: SmithingRecipeInput, provider: HolderLookup.Provider): ItemStack? = input.base.copy().apply {
            flags[flag] = setter
        }
    }

    class PatternsRecipe(delegate: SmithingRecipe, setter: Boolean) : FlagRecipe(delegate, setter, ETFlag.BANNER) {
        override fun assemble(input: SmithingRecipeInput, provider: HolderLookup.Provider): ItemStack? =
            super.assemble(input, provider)?.apply { banner.copy(input.template) }
    }
}


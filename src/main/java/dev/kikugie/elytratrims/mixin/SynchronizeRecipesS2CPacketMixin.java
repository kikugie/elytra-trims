package dev.kikugie.elytratrims.mixin;

import com.google.common.collect.Iterables;
import dev.kikugie.elytratrims.recipe.ElytraGlowRecipe;
import dev.kikugie.elytratrims.recipe.ElytraPatternRecipe;
import net.minecraft.network.packet.s2c.play.SynchronizeRecipesS2CPacket;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(SynchronizeRecipesS2CPacket.class)
public abstract class SynchronizeRecipesS2CPacketMixin {
    @ModifyArg(method = "<init>(Ljava/util/Collection;)V", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Lists;newArrayList(Ljava/lang/Iterable;)Ljava/util/ArrayList;"))
    private Iterable<RecipeEntry<? extends Recipe<?>>> removeElytraPatternRecipe(Iterable<RecipeEntry<? extends Recipe<?>>> elements) {
        return Iterables.filter(elements, entry -> {
            Recipe<?> recipe = entry.value();
            return !(recipe instanceof ElytraPatternRecipe) && !(recipe instanceof ElytraGlowRecipe);
        });
    }
}

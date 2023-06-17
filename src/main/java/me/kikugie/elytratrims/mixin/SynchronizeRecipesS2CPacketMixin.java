package me.kikugie.elytratrims.mixin;

import me.kikugie.elytratrims.recipe.ElytraGlowRecipe;
import me.kikugie.elytratrims.recipe.ElytraPatternRecipe;
import net.minecraft.network.packet.s2c.play.SynchronizeRecipesS2CPacket;
import net.minecraft.recipe.Recipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.ArrayList;

@Mixin(SynchronizeRecipesS2CPacket.class)
public abstract class SynchronizeRecipesS2CPacketMixin {
    @ModifyArg(method = "<init>(Ljava/util/Collection;)V", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Lists;newArrayList(Ljava/lang/Iterable;)Ljava/util/ArrayList;", remap = false))
    private Iterable<Recipe<?>> removeElytraPatternRecipe(Iterable<Recipe<?>> elements) {
        ArrayList<Recipe<?>> recipes = new ArrayList<>();
        elements.forEach(recipe -> {
            if (!(recipe instanceof ElytraPatternRecipe ||
                    recipe instanceof ElytraGlowRecipe)) {
                recipes.add(recipe);
            }
        });
        return recipes;
    }
}

package dev.kikugie.elytratrims.mixin.resource;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.data.recipes.SmithingTrimRecipeBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Mixin(SmithingTrimRecipeBuilder.class)
public class MultiRecipeBuilderMixin {
    //? if >=1.21.4 {
    @WrapOperation(
        method = "save(Lnet/minecraft/data/recipes/RecipeOutput;Lnet/minecraft/resources/ResourceKey;)V",
        at = @At(value = "INVOKE", target = "Ljava/util/Optional;of(Ljava/lang/Object;)Ljava/util/Optional;")
    )
    private Optional<?> epicOfNullableInjection(Object value, Operation<Optional<?>> ignored) {
        return Optional.ofNullable(value);
    }
    //?}
}

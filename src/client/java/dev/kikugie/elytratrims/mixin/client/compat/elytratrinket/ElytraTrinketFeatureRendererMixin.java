package dev.kikugie.elytratrims.mixin.client.compat.elytratrinket;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import dev.kikugie.elytratrims.ElytraTrimsMod;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.ElytraEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

/**
 * If I had a nickel every time I add compatibility for an elytra trinket mod, I would have 2. Which isn't a lot, but its weird that it happened twice.
 */
/* On the third time I swear to make this code cleaner. */
@SuppressWarnings("ALL")
@Environment(EnvType.CLIENT)
@Restriction(require = @Condition("elytra_trinket"))
@Pseudo
@Mixin(targets = "pw.lakuna.elytra_trinket.ElytraTrinketFeatureRenderer")
public abstract class ElytraTrinketFeatureRendererMixin extends FeatureRenderer {
    public ElytraTrinketFeatureRendererMixin(FeatureRendererContext context) {
        super(context);
    }

    @ModifyExpressionValue(method = "render",
            at = @At(value = "INVOKE",
                    target = "Ljava/util/List;get(I)Ljava/lang/Object;"))
    private Object saveItemStack(Object stack, @Share("stack") LocalRef<ItemStack> stackRef) {
        stackRef.set((ItemStack) stack);
        return stack;
    }

    @WrapOperation(method = "render",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/render/entity/model/ElytraEntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V"))
    private void elytraPostRender(ElytraEntityModel<?> model,
                                  MatrixStack matrices,
                                  VertexConsumer vertices,
                                  int light,
                                  int overlay,
                                  float red,
                                  float green,
                                  float blue,
                                  float alpha,
                                  Operation<ElytraEntityModel<?>> original,
                                  @Local(argsOnly = true) VertexConsumerProvider provider,
                                  @Local(argsOnly = true) LivingEntity entity,
                                  @Share("stack") LocalRef<ItemStack> stackRef) {
        original.call(model, matrices, vertices, light, overlay, red, green, blue, alpha);
        ElytraTrimsMod.ELYTRA_RENDERER.render(model, matrices, provider, entity, stackRef.get(), light, alpha);
    }
}

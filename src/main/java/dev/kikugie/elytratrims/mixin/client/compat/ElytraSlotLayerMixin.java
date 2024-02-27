package dev.kikugie.elytratrims.mixin.client.compat;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import dev.kikugie.elytratrims.client.ETClient;
import dev.kikugie.elytratrims.common.plugin.MixinConfigurable;
import dev.kikugie.elytratrims.common.plugin.RequireMod;
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

@Pseudo
@MixinConfigurable
@SuppressWarnings("ALL")
@RequireMod("elytraslot")
@Mixin(targets = "com.illusivesoulworks.elytraslot.client.ElytraSlotLayer")
public abstract class ElytraSlotLayerMixin extends FeatureRenderer {
    public ElytraSlotLayerMixin(FeatureRendererContext context) {
        super(context);
    }

    @ModifyExpressionValue(method = "lambda$render$0",
            at = @At(value = "INVOKE",
                    target = "Lcom/illusivesoulworks/elytraslot/client/ElytraRenderResult;stack()Lnet/minecraft/item/ItemStack;"))
    private ItemStack saveItemStack(ItemStack stack, @Share("stack") LocalRef<ItemStack> stackRef) {
        stackRef.set(stack);
        return stack;
    }

    @WrapOperation(method = "lambda$render$0",
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
        ETClient.getRenderer().render(model, matrices, provider, entity, stackRef.get(), light, alpha);
    }
}
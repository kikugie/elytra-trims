package dev.kikugie.elytratrims.mixin.render;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.kikugie.elytratrims.render.*;
import dev.kikugie.elytratrims.resource.image.Color4i;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//? if >=1.21.4 {
@Mixin(net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer.class)
public class ElytraRendererMixin {
    @Unique
    private static final String RENDER_METHOD = "renderLayers(Lnet/minecraft/client/resources/model/EquipmentClientInfo$LayerType;" +
        "Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/client/model/Model;Lnet/minecraft/world/item/ItemStack;" +
        "Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/resources/ResourceLocation;)V";

    @Inject(
        method = RENDER_METHOD,
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/item/ItemStack;hasFoil()Z"
        )
    )
    private void invokeETOverrideRenderers(
        net.minecraft.client.resources.model.EquipmentClientInfo.LayerType type,
        ResourceKey<net.minecraft.world.item.equipment.EquipmentAsset> asset,
        Model model,
        ItemStack stack,
        PoseStack matrices,
        MultiBufferSource provider,
        int light,
        @Nullable ResourceLocation identifier,
        CallbackInfo ci,
        @Share("parameters") LocalRef<ETRenderParameters> parameters,
        @Share("overridden") LocalBooleanRef overridden) {
        if (type != net.minecraft.client.resources.model.EquipmentClientInfo.LayerType.WINGS) return;

        light = ETGlowRenderer.getEffectiveLight(stack, light);
        ETRenderParameters params = ETRenderParameters.of(model, matrices, provider, stack, light, Color4i.WHITE);
        parameters.set(params);
        if (ETGatewayRenderer.INSTANCE.render(params))
            overridden.set(true);
    }

    @ModifyExpressionValue(
        method = RENDER_METHOD,
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/entity/layers/EquipmentLayerRenderer;getColorForLayer(Lnet/minecraft/client/resources/model/EquipmentClientInfo$Layer;I)I"
        )
    )
    private int cancelDefaultRenderer(int original, @Share("overridden") LocalBooleanRef overridden) {
        return overridden.get() ? 0 : original;
    }

    @Inject(
        method = RENDER_METHOD,
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/item/ItemStack;get(Lnet/minecraft/core/component/DataComponentType;)Ljava/lang/Object;"
        ),
        cancellable = true
    )
    private void invokeETRenderers(
        net.minecraft.client.resources.model.EquipmentClientInfo.LayerType type,
        ResourceKey<net.minecraft.world.item.equipment.EquipmentAsset> asset,
        Model model,
        ItemStack stack,
        PoseStack matrices,
        MultiBufferSource provider,
        int light,
        @Nullable ResourceLocation identifier,
        CallbackInfo ci,
        @Share("parameters") LocalRef<ETRenderParameters> parameters
    ) {
        if (type != net.minecraft.client.resources.model.EquipmentClientInfo.LayerType.WINGS) return;
        if (ETRenderer.renderPost(parameters.get())) ci.cancel();
    }
}
//?} else {
/*@Mixin(ElytraLayer.class)
public class ElytraRendererMixin {
    @WrapOperation(
        method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ElytraModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;II)V ")
    )
    private void invokeETRenderers(
        @Coerce EntityModel<LivingEntity> model,
        PoseStack matrices,
        VertexConsumer consumer,
        int light,
        int overlay,
        Operation<Void> original,
        @Local(ordinal = 0) ItemStack stack,
        @Local(argsOnly = true) MultiBufferSource provider,
        @Local(argsOnly = true) LivingEntity entity) {
        if (ETGatewayRenderer.INSTANCE.render(model, matrices, provider, stack, light, Color4i.WHITE))
            return;

        light = ETGlowRenderer.getEffectiveLight(stack, light);
        ETColorRenderer.INSTANCE.render(model, matrices, provider, stack, light, Color4i.WHITE);
        ETPatternsRenderer.INSTANCE.render(model, matrices, provider, stack, light, Color4i.WHITE);
    }
}
*///?}
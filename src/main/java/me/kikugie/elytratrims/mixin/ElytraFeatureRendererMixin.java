package me.kikugie.elytratrims.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import me.kikugie.elytratrims.render.ExtraElytraFeatureRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.render.entity.feature.ElytraFeatureRenderer;
import net.minecraft.client.render.entity.model.ElytraEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(ElytraFeatureRenderer.class)
public class ElytraFeatureRendererMixin {
    @Shadow
    @Final
    private ElytraEntityModel<?> elytra;

    private ExtraElytraFeatureRenderer extraRenderer;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void initExtraRenderer(CallbackInfo ci) {
        extraRenderer = new ExtraElytraFeatureRenderer(elytra, MinecraftClient.getInstance().getBakedModelManager().getAtlas(TexturedRenderLayers.ARMOR_TRIMS_ATLAS_TEXTURE));
    }

    @ModifyArgs(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderLayer;getArmorCutoutNoCull(Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/render/RenderLayer;"))
    private void renderCapeOnGuiArmorStand(Args args, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, LivingEntity entity, float f, float g, float h, float j, float k, float l) {
        if (ExtraElytraFeatureRenderer.shouldRenderBlankIfMissing(entity)) return;
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        assert player != null;

        if (player.getCapeTexture() != null && player.isPartVisible(PlayerModelPart.CAPE)) {
            args.set(0, player.getCapeTexture());
        }
    }

    @WrapOperation(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/ElytraEntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V"))
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
                                  @Local(argsOnly = true) LivingEntity entity) {
        original.call(model, matrices, vertices, light, overlay, red, green, blue, alpha);
        extraRenderer.render(matrices, provider, entity, entity.getEquippedStack(EquipmentSlot.CHEST), light, alpha);
    }
}

package dev.kikugie.elytratrims.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.kikugie.elytratrims.client.ETClient;
import dev.kikugie.elytratrims.client.config.RenderConfig.RenderType;
import dev.kikugie.elytratrims.client.render.ETFeatureRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.render.entity.feature.ElytraFeatureRenderer;
import net.minecraft.client.render.entity.model.ElytraEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = ElytraFeatureRenderer.class, priority = 1100)
public class ElytraFeatureRendererMixin {
    @Shadow
    @Final
    private static Identifier SKIN;

    /**
     * Renders player's cape on the armor stand in a smithing table
     */
    @ModifyArg(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderLayer;getArmorCutoutNoCull(Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/render/RenderLayer;"))
    private Identifier renderCapeOnGuiArmorStand(Identifier texture, @Local(argsOnly = true) LivingEntity entity) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        assert player != null;
        //#if MC < 12001
        Identifier cape = player.getCapeTexture();
        //#else
        //$$ Identifier cape = player.getSkinTextures().capeTexture();
        //#endif
        if (texture.equals(SKIN) || texture.equals(cape)) {
            if (entity instanceof PlayerEntity && ETClient.getRenderer().cancelRender(RenderType.CAPE, entity))
                return SKIN;
            if (ETFeatureRenderer.renderMissingTexture(entity) && cape != null && player.isPartVisible(PlayerModelPart.CAPE))
                return cape;
        }
        return texture;
    }

    /**
     * Handles rendering of the mod features
     */
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
        ETClient.getRenderer().render(model, matrices, provider, entity, entity.getEquippedStack(EquipmentSlot.CHEST), light, alpha);
    }
}

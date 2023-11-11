package dev.kikugie.elytratrims.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.kikugie.elytratrims.ElytraTrims;
import dev.kikugie.elytratrims.config.RenderConfig;
import dev.kikugie.elytratrims.render.ExtraElytraFeatureRenderer;
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

    @ModifyArg(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderLayer;getArmorCutoutNoCull(Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/render/RenderLayer;"))
    private Identifier renderCapeOnGuiArmorStand(Identifier texture, @Local(argsOnly = true) LivingEntity entity) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        assert player != null;
        //#if MC > 12001
        Identifier cape = player.getSkinTextures().capeTexture();
        //#else
        //$$ Identifier cape = player.getCapeTexture();
        //#endif
        if (texture.equals(SKIN) || texture.equals(cape)) {
            if (entity instanceof PlayerEntity
                    && ExtraElytraFeatureRenderer.cancelRender(RenderConfig.RenderType.CAPE, entity))
                return SKIN;
            if (!ExtraElytraFeatureRenderer.skipRenderIfMissingTexture(entity)
                    && cape != null
                    && player.isPartVisible(PlayerModelPart.CAPE))
                return cape;
        }
        return texture;
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
        ElytraTrims.ELYTRA_RENDERER.render(model, matrices, provider, entity, entity.getEquippedStack(EquipmentSlot.CHEST), light, alpha);
    }
}

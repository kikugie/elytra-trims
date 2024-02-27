package dev.kikugie.elytratrims.mixin.compat;

import com.bawnorton.mixinsquared.TargetHandler;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.kikugie.elytratrims.client.ETClient;
import dev.kikugie.elytratrims.common.plugin.MixinConfigurable;
import dev.kikugie.elytratrims.common.plugin.RequireMod;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ElytraFeatureRenderer;
import net.minecraft.client.render.entity.model.ElytraEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@MixinConfigurable
@RequireMod("minecraftcapes")
@SuppressWarnings("ALL")
@Mixin(value = ElytraFeatureRenderer.class, priority = 1500)
public class MinecraftCapesCompat {
    @TargetHandler(mixin = "net.minecraftcapes.mixin.MixinElytraLayer", name = "render")
    @WrapOperation(method = "@MixinSquared:Handler", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/ElytraEntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V"))
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
package me.kikugie.elytratrims.mixin;

import me.kikugie.elytratrims.ElytraTrimsMod;
import me.kikugie.elytratrims.access.ArmorStandEntityAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.render.entity.feature.ElytraFeatureRenderer;
import net.minecraft.client.render.entity.model.ElytraEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.util.Identifier;
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

    private SpriteAtlasTexture atlas;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void saveTrimAtlas(CallbackInfo ci) {
        atlas = MinecraftClient.getInstance().getBakedModelManager().getAtlas(TexturedRenderLayers.ARMOR_TRIMS_ATLAS_TEXTURE);
    }

    @ModifyArgs(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V", at = @At(value = "INVOKE",  target = "Lnet/minecraft/client/render/entity/model/ElytraEntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V"))
    private void renderDyedElytra(Args args, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, LivingEntity entity, float f, float g, float h, float j, float k, float l) {
        ItemStack stack = entity.getEquippedStack(EquipmentSlot.CHEST);
        if (ElytraTrimsMod.DYEABLE.hasColor(stack)) {
            var color = ElytraTrimsMod.DYEABLE.getColor(stack);
            float red = (float)(color >> 16 & 0xFF) / 255.0F;
            float green = (float)(color >> 8 & 0xFF) / 255.0F;
            float blue = (float)(color & 0xFF) / 255.0F;
            args.set(4, red);
            args.set(5, green);
            args.set(6, blue);
        }
    }

    @ModifyArgs(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderLayer;getArmorCutoutNoCull(Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/render/RenderLayer;"))
    private void renderCapeOnGuiArmorStand(Args args, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, LivingEntity entity, float f, float g, float h, float j, float k, float l) {
        if (shouldRenderBlankIfMissing(entity)) return;
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        assert player != null;

        if (player.getCapeTexture() != null && player.isPartVisible(PlayerModelPart.CAPE)) {
            args.set(0, player.getCapeTexture());
        }
    }

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;pop()V"))
    private void renderElytraTrims(MatrixStack matrices, VertexConsumerProvider provider, int light, LivingEntity entity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
        ItemStack stack = entity.getEquippedStack(EquipmentSlot.CHEST);
        ArmorTrim trim = ArmorTrim.getTrim(entity.world.getRegistryManager(), stack).orElse(null);
        if (trim == null) return;

        Sprite sprite = getTrimSprite(trim);
        if (sprite.getContents().getId().equals(MissingSprite.getMissingSpriteId()) && shouldRenderBlankIfMissing(entity)) {
            return;
        }

        VertexConsumer vertexConsumer = sprite.getTextureSpecificVertexConsumer(
                ItemRenderer.getDirectItemGlintConsumer(provider, TexturedRenderLayers.getArmorTrims(), false, stack.hasGlint())
        );

        elytra.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 1F, 1F, 1F, 1F);
    }

    private Sprite getTrimSprite(ArmorTrim trim) {
        String material = trim.getMaterial().value().assetName();
        Identifier identifier = trim.getPattern().value().assetId().withPath(path -> "trims/models/elytra/" + path + "_" + material);
        return atlas.getSprite(identifier);
    }

    private boolean shouldRenderBlankIfMissing(LivingEntity entity) {
        return !(entity instanceof ArmorStandEntity) || !((ArmorStandEntityAccessor) entity).isGui();
    }
}

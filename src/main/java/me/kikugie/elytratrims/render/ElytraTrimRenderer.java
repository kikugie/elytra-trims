package me.kikugie.elytratrims.render;

import me.kikugie.elytratrims.ElytraTrimsMod;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.ElytraEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.trim.ArmorTrim;

public class ElytraTrimRenderer {
    public void renderTrim(
            MatrixStack matrices,
            ElytraEntityModel<?> elytra,
            VertexConsumerProvider provider,
            LivingEntity entity,
            ItemStack stack,
            int light
    ) {
        ArmorTrim trim = ArmorTrim.getTrim(entity.world.getRegistryManager(), stack).orElse(null);
        if (trim == null) return;

        Sprite sprite = ElytraTrimsMod.MANAGER.getSprite(trim);
        if (sprite.getContents().getId().equals(MissingSprite.getMissingSpriteId())) return;

        VertexConsumer vertexConsumer = sprite.getTextureSpecificVertexConsumer(
                ItemRenderer.getDirectItemGlintConsumer(provider, RenderLayer.getArmorCutoutNoCull(ElytraTrimsManager.TRIMS_ATLAS_ID), false, stack.hasGlint())
        );

        elytra.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 1F, 1F, 1F, 1F);
    }
}

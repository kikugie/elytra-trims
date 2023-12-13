package dev.kikugie.elytratrims.client.compat;

import com.bawnorton.allthetrims.client.util.PaletteHelper;
import dev.kikugie.elytratrims.client.ETClient;
import dev.kikugie.elytratrims.client.render.ETFeatureRenderer;
import dev.kikugie.elytratrims.client.resource.ETAtlasHolder;
import dev.kikugie.elytratrims.client.resource.ImageUtils;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.ElytraEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.awt.*;
import java.util.List;
import java.util.function.BiFunction;

public class AllTheTrimsCompat {
    public static void renderTrim(SpriteAtlasTexture atlas, ElytraEntityModel<?> elytra, ArmorTrim trim, ItemStack stack, MatrixStack matrices, VertexConsumerProvider provider, int light, float alpha) {
        List<Color> palette = PaletteHelper.getPalette(trim.getMaterial().value().ingredient().value());
        Sprite sprite;
        for(int i = 0; i < 8; i++) {
            sprite = getTrimSprite(atlas, trim, i);
            if (ImageUtils.isMissing(sprite))
                continue;
            VertexConsumer vertexConsumer = sprite.getTextureSpecificVertexConsumer(ItemRenderer.getDirectItemGlintConsumer(provider, ETFeatureRenderer.ELYTRA_LAYER.apply(ETAtlasHolder.ID), false, stack.hasGlint()));
            Color colour = palette.get(i);
            elytra.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, colour.getRed() / 255F, colour.getGreen() / 255F, colour.getBlue() / 255F, alpha);
        }
    }

    private static Sprite getTrimSprite(SpriteAtlasTexture atlas, ArmorTrim trim, int i) {
        String material = trim.getMaterial().value().assetName();
        Identifier identifier = trim.getPattern().value().assetId().withPath(path -> "trims/models/elytra/%s_%s_%s".formatted(path, i, material));
        if (ETClient.getConfig().texture.useDarkerTrim) {
            Sprite sprite = atlas.getSprite(identifier.withSuffixedPath("_darker"));
            if (!ImageUtils.isMissing(sprite))
                return sprite;
        }
        return atlas.getSprite(identifier);
    }
}

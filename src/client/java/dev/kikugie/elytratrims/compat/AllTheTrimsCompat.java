package dev.kikugie.elytratrims.compat;

import com.bawnorton.allthetrims.client.util.PaletteHelper;
import dev.kikugie.elytratrims.ElytraTrims;
import dev.kikugie.elytratrims.render.ExtraElytraFeatureRenderer;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
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

import java.awt.*;
import java.util.List;

public abstract class AllTheTrimsCompat {
    public static void renderTrim(ElytraEntityModel<?> elytra, ArmorTrim trim, ItemStack stack, MatrixStack matrices, VertexConsumerProvider provider, int light, SpriteAtlasTexture atlas, RenderLayer layer) {
        List<Color> palette = PaletteHelper.getPalette(trim.getMaterial().value().ingredient().value());
        Sprite sprite;
        for(int i = 0; i < 8; i++) {
            sprite = getTrimSprite(atlas, trim, i);
            VertexConsumer vertexConsumer = sprite.getTextureSpecificVertexConsumer(ItemRenderer.getDirectItemGlintConsumer(provider, layer, false, stack.hasGlint()));
            Color colour = palette.get(i);
            elytra.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, colour.getRed() / 255f, colour.getGreen() / 255f, colour.getBlue() / 255f, 1.0F);
        }
    }

    private static Sprite getTrimSprite(SpriteAtlasTexture atlas, ArmorTrim trim, int i) {
        String material = trim.getMaterial().value().assetName();
        Identifier identifier = trim.getPattern().value().assetId().withPath(path -> "trims/models/elytra/%s_%s_%s".formatted(path, i, material));
        if (ElytraTrims.getConfig().texture.useDarkerTrim) {
            Sprite sprite = atlas.getSprite(identifier.withSuffixedPath("_darker"));
            if (!ExtraElytraFeatureRenderer.isMissing(sprite))
                return sprite;
        }
        return atlas.getSprite(identifier);
    }
}

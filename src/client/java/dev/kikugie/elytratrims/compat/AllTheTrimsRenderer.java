package dev.kikugie.elytratrims.compat;

import com.bawnorton.allthetrims.client.util.PaletteHelper;
import dev.kikugie.elytratrims.ElytraTrims;
import dev.kikugie.elytratrims.render.ExtraElytraFeatureRenderer;
import dev.kikugie.elytratrims.resource.ETAtlasHolder;
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

import static dev.kikugie.elytratrims.render.ExtraElytraFeatureRenderer.ELYTRA_LAYER;
import static dev.kikugie.elytratrims.render.ExtraElytraFeatureRenderer.isMissing;

public class AllTheTrimsRenderer {
    private final SpriteAtlasTexture atlas;
    private final BiFunction<ArmorTrim, Integer, Sprite> trimGetter = Util.memoize(this::trimSpriteGetter);

    public AllTheTrimsRenderer(SpriteAtlasTexture atlas) {
        this.atlas = atlas;
    }

    public void renderTrim(ElytraEntityModel<?> elytra, ArmorTrim trim, ItemStack stack, MatrixStack matrices, VertexConsumerProvider provider, int light, float alpha) {
        List<Color> palette = PaletteHelper.getPalette(trim.getMaterial().value().ingredient().value());
        Sprite sprite;
        for(int i = 0; i < 8; i++) {
            sprite = getTrimSprite(trim, i);
            if (isMissing(sprite))
                continue;
            VertexConsumer vertexConsumer = sprite.getTextureSpecificVertexConsumer(ItemRenderer.getDirectItemGlintConsumer(provider, ELYTRA_LAYER.apply(ETAtlasHolder.NAME), false, stack.hasGlint()));
            Color colour = palette.get(i);
            elytra.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, colour.getRed() / 255F, colour.getGreen() / 255F, colour.getBlue() / 255F, alpha);
        }
    }

    private Sprite getTrimSprite(ArmorTrim trim, int i) {
        return this.trimGetter.apply(trim, i);
    }

    private Sprite trimSpriteGetter(ArmorTrim trim, int i) {
        String material = trim.getMaterial().value().assetName();
        Identifier identifier = trim.getPattern().value().assetId().withPath(path -> "trims/models/elytra/%s_%s_%s".formatted(path, i, material));
        if (ElytraTrims.getConfig().texture.useDarkerTrim) {
            Sprite sprite = this.atlas.getSprite(identifier.withSuffixedPath("_darker"));
            if (!ExtraElytraFeatureRenderer.isMissing(sprite))
                return sprite;
        }
        return this.atlas.getSprite(identifier);
    }
}

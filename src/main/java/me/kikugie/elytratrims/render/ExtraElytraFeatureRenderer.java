package me.kikugie.elytratrims.render;

import com.mojang.datafixers.util.Pair;
import me.kikugie.elytratrims.access.ArmorStandEntityAccessor;
import me.kikugie.elytratrims.access.ElytraOverlaysAccessor;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BannerBlockEntityRenderer;
import net.minecraft.client.render.entity.model.ElytraEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static net.minecraft.client.render.RenderPhase.*;
import static net.minecraft.client.render.TexturedRenderLayers.ARMOR_TRIMS_ATLAS_TEXTURE;

public class ExtraElytraFeatureRenderer {
    private static final Function<Identifier, RenderLayer> ELYTRA_LAYER = Util.memoize(
            texture -> {
                RenderLayer.MultiPhaseParameters multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder()
                        .program(ENTITY_NO_OUTLINE_PROGRAM)
                        .texture(new Texture(texture, false, false))
                        .transparency(TRANSLUCENT_TRANSPARENCY)
                        .cull(DISABLE_CULLING)
                        .lightmap(ENABLE_LIGHTMAP)
                        .overlay(ENABLE_OVERLAY_COLOR)
                        .layering(VIEW_OFFSET_Z_LAYERING)
                        .writeMaskState(COLOR_MASK)
                        .build(true);
                return RenderLayer.of(
                        "elytra_layer", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, VertexFormat.DrawMode.QUADS, 256, true, true, multiPhaseParameters
                );
            }
    );
    private final ElytraEntityModel<?> elytra;
    private final SpriteAtlasTexture atlas;

    public ExtraElytraFeatureRenderer(ElytraEntityModel<?> elytra, SpriteAtlasTexture atlas) {
        this.elytra = elytra;
        this.atlas = atlas;
    }

    public static boolean shouldRenderBlankIfMissing(LivingEntity entity) {
        return !(entity instanceof ArmorStandEntity) || !((ArmorStandEntityAccessor) entity).isGui();
    }

    public void render(MatrixStack matrices, VertexConsumerProvider provider, LivingEntity entity, ItemStack stack, int light, float alpha) {
        if (!renderJebElytra(matrices, provider, entity, stack, light, alpha)) {
            renderElytraOverlay(matrices, provider, entity, stack, light, alpha);
            renderElytraPatterns(matrices, provider, entity, stack, light, alpha);
        }
        renderElytraTrims(matrices, provider, entity, stack, light, alpha);
    }

    private void renderElytraOverlay(MatrixStack matrices, VertexConsumerProvider provider, LivingEntity ignoredEntity, ItemStack stack, int light, float alpha) {
        int color = ((ElytraOverlaysAccessor) (Object) stack).getColor();
        if (color != 0) {
            renderElytraColor(matrices, provider, ignoredEntity, stack, light, color, alpha);
        }
    }

    private void renderElytraColor(MatrixStack matrices, VertexConsumerProvider provider, LivingEntity ignoredEntity, ItemStack stack, int light, int color, float alpha) {
        float red = (float) (color >> 16 & 0xFF) / 255.0F;
        float green = (float) (color >> 8 & 0xFF) / 255.0F;
        float blue = (float) (color & 0xFF) / 255.0F;
        Sprite sprite = getOverlaySprite();
        VertexConsumer vertexConsumer = sprite.getTextureSpecificVertexConsumer(
                ItemRenderer.getDirectItemGlintConsumer(
                        provider,
                        ELYTRA_LAYER.apply(ARMOR_TRIMS_ATLAS_TEXTURE),
                        false,
                        stack.hasGlint()));
        elytra.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, red, green, blue, alpha);
    }

    private void renderElytraPatterns(MatrixStack matrices, VertexConsumerProvider provider, LivingEntity ignoredEntity, ItemStack stack, int light, float alpha) {
        List<Pair<RegistryEntry<BannerPattern>, DyeColor>> patterns = ((ElytraOverlaysAccessor) (Object) stack).getPatterns();

        for (int i = 0; i < 17 && i < patterns.size(); i++) {
            Pair<RegistryEntry<BannerPattern>, DyeColor> pair = patterns.get(i);
            Sprite sprite = getPatternSprite(pair.getFirst());
            if (sprite.getContents().getId().equals(MissingSprite.getMissingSpriteId()))
                continue;

            float[] color = pair.getSecond().getColorComponents();
            VertexConsumer vertexConsumer = sprite.getTextureSpecificVertexConsumer(
                    ItemRenderer.getDirectItemGlintConsumer(
                            provider,
                            ELYTRA_LAYER.apply(ARMOR_TRIMS_ATLAS_TEXTURE),
                            false,
                            stack.hasGlint()));
            elytra.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, color[0], color[1], color[2], alpha);
        }
    }

    private void renderElytraTrims(MatrixStack matrices, VertexConsumerProvider provider, LivingEntity entity, ItemStack stack, int light, float alpha) {
        ArmorTrim trim = ArmorTrim.getTrim(entity.world.getRegistryManager(), stack).orElse(null);
        if (trim == null)
            return;

        Sprite sprite = getTrimSprite(trim);
        if (sprite.getContents().getId().equals(MissingSprite.getMissingSpriteId()) && shouldRenderBlankIfMissing(entity))
            return;

        VertexConsumer vertexConsumer = sprite.getTextureSpecificVertexConsumer(
                ItemRenderer.getDirectItemGlintConsumer(
                        provider,
                        ELYTRA_LAYER.apply(ARMOR_TRIMS_ATLAS_TEXTURE),
                        false,
                        stack.hasGlint()));
        elytra.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 1F, 1F, 1F, alpha);
    }

    private boolean renderJebElytra(MatrixStack matrices, VertexConsumerProvider provider, LivingEntity entity, ItemStack stack, int light, float alpha) {
        if (((ElytraOverlaysAccessor) (Object) stack).getPatterns().isEmpty() && stack.getName().getString().equals("jeb_")) {
            assert MinecraftClient.getInstance().world != null;
            long tick = MinecraftClient.getInstance().world.getTime() % 360;
            int color = MathHelper.hsvToRgb(tick / 360F, 1F, 1F);
            renderElytraColor(matrices, provider, entity, stack, light, color, alpha);
            return true;
        }
        return false;
    }

    private Sprite getOverlaySprite() {
        return atlas.getSprite(new Identifier("entity/elytra_overlay"));
    }

    private Sprite getPatternSprite(RegistryEntry<BannerPattern> pattern) {
        Optional<RegistryKey<BannerPattern>> optional = pattern.getKey();
        if (optional.isPresent()) {
            String[] path = TexturedRenderLayers.getShieldPatternTextureId(optional.get()).getTextureId().getPath().split("/");
            String id = path[path.length - 1];
            return atlas.getSprite(new Identifier("entity/elytra/patterns/" + id));
        }
        return atlas.getSprite(null);
    }

    private Sprite getTrimSprite(ArmorTrim trim) {
        String material = trim.getMaterial().value().assetName();
        Identifier identifier = trim.getPattern().value().assetId().withPath(path -> "trims/models/elytra/" + path + "_" + material);
        return atlas.getSprite(identifier);
    }
}

package dev.kikugie.elytratrims.render;

import com.mojang.datafixers.util.Pair;
import dev.kikugie.elytratrims.ElytraTrims;
import dev.kikugie.elytratrims.ElytraTrimsServer;
import dev.kikugie.elytratrims.ModStatus;
import dev.kikugie.elytratrims.access.ElytraOverlaysAccessor;
import dev.kikugie.elytratrims.access.LivingEntityAccessor;
import dev.kikugie.elytratrims.compat.AllTheTrimsRenderer;
import dev.kikugie.elytratrims.compat.StackableTrimsList;
import dev.kikugie.elytratrims.config.RenderConfig;
import dev.kikugie.elytratrims.resource.ETAtlasHolder;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.model.ElytraEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static net.minecraft.client.render.RenderPhase.*;

@SuppressWarnings("DataFlowIssue")
public class ExtraElytraFeatureRenderer {
    public static final Function<Identifier, RenderLayer> ELYTRA_LAYER = Util.memoize(
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
    private final SpriteAtlasTexture atlas;
    private final Function<ArmorTrim, Sprite> trimGetter = Util.memoize(this::trimSpriteGetter);
    private final Function<RegistryEntry<BannerPattern>, Sprite> patternGetter = Util.memoize(this::patternSpriteGetter);
    @Nullable
    private AllTheTrimsRenderer attRenderer = null;

    public ExtraElytraFeatureRenderer(SpriteAtlasTexture atlas) {
        this.atlas = atlas;
        if (ModStatus.allTheTrimsLoaded)
            this.attRenderer = new AllTheTrimsRenderer(atlas);
    }

    public static boolean cancelRender(RenderConfig.RenderType type, LivingEntity entity) {
        RenderConfig.RenderMode mode = ElytraTrims.getConfig().render.getEffective(type);
        return switch (mode) {
            case ALL -> false;
            case NONE -> true;
            case SELF ->
                    entity != MinecraftClient.getInstance().player && ExtraElytraFeatureRenderer.skipRenderIfMissingTexture(entity);
            case OTHERS ->
                    entity == MinecraftClient.getInstance().player || !ExtraElytraFeatureRenderer.skipRenderIfMissingTexture(entity);
        };
    }

    public static boolean skipRenderIfMissingTexture(LivingEntity entity) {
        return !((LivingEntityAccessor) entity).elytra_trims$isGui();
    }

    public static boolean isMissing(Sprite sprite) {
        return sprite == null || sprite.getContents().getId().equals(MissingSprite.getMissingSpriteId());
    }

    public void render(ElytraEntityModel<?> elytra, MatrixStack matrices, VertexConsumerProvider provider, LivingEntity entity, ItemStack stack, int light, float alpha) {
        light = getLight(entity, stack, light);
        if (!renderJebElytra(elytra, matrices, provider, entity, stack, light, alpha)) {
            renderElytraOverlay(elytra, matrices, provider, entity, stack, light, alpha);
            renderElytraPatterns(elytra, matrices, provider, entity, stack, light, alpha);
        }
        renderElytraTrims(elytra, matrices, provider, entity, stack, light, alpha);
    }

    private int getLight(LivingEntity entity, ItemStack stack, int light) {
        if (!cancelRender(RenderConfig.RenderType.GLOW, entity) && ElytraTrimsServer.GLOWING.hasGlow(stack))
            return 0xFF00FF;
        return light;
    }

    private void renderElytraOverlay(ElytraEntityModel<?> elytra, MatrixStack matrices, VertexConsumerProvider provider, LivingEntity entity, ItemStack stack, int light, float alpha) {
        if (cancelRender(RenderConfig.RenderType.COLOR, entity))
            return;

        int color = ((ElytraOverlaysAccessor) (Object) stack).elytra_trims$getColor();
        if (color != 0)
            renderElytraColor(elytra, matrices, provider, entity, stack, light, color, alpha);
    }

    private void renderElytraColor(ElytraEntityModel<?> elytra, MatrixStack matrices, VertexConsumerProvider provider, LivingEntity ignoredEntity, ItemStack stack, int light, int color, float alpha) {
        Sprite sprite = getOverlaySprite();
        if (isMissing(sprite))
            return;

        float red = (float) (color >> 16 & 0xFF) / 255.0F;
        float green = (float) (color >> 8 & 0xFF) / 255.0F;
        float blue = (float) (color & 0xFF) / 255.0F;
        VertexConsumer vertexConsumer = sprite.getTextureSpecificVertexConsumer(
                ItemRenderer.getDirectItemGlintConsumer(
                        provider,
                        ELYTRA_LAYER.apply(ETAtlasHolder.NAME),
                        false,
                        stack.hasGlint()));
        elytra.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, red, green, blue, alpha);
    }

    private void renderElytraPatterns(ElytraEntityModel<?> elytra, MatrixStack matrices, VertexConsumerProvider provider, LivingEntity entity, ItemStack stack, int light, float alpha) {
        if (cancelRender(RenderConfig.RenderType.PATTERNS, entity))
            return;

        List<Pair<RegistryEntry<BannerPattern>, DyeColor>> patterns = ((ElytraOverlaysAccessor) (Object) stack).elytra_trims$getPatterns();

        for (int i = 0; i < 17 && i < patterns.size(); i++) {
            Pair<RegistryEntry<BannerPattern>, DyeColor> pair = patterns.get(i);
            Sprite sprite = getPatternSprite(pair.getFirst());
            if (isMissing(sprite))
                continue;

            float[] color = pair.getSecond().getColorComponents();
            VertexConsumer vertexConsumer = sprite.getTextureSpecificVertexConsumer(
                    ItemRenderer.getDirectItemGlintConsumer(
                            provider,
                            ELYTRA_LAYER.apply(ETAtlasHolder.NAME),
                            false,
                            stack.hasGlint()));
            elytra.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, color[0], color[1], color[2], alpha);
        }
    }

    private void renderElytraTrims(ElytraEntityModel<?> elytra, MatrixStack matrices, VertexConsumerProvider provider, LivingEntity entity, ItemStack stack, int light, float alpha) {
        if (cancelRender(RenderConfig.RenderType.TRIMS, entity))
            return;

        World world = entity.getWorld();

        List<ArmorTrim> trims;
        if (ModStatus.stackedTrimsLoaded)
            trims = StackableTrimsList.getTrims(world.getRegistryManager(), stack);
        else
            trims = ArmorTrim.getTrim(world.getRegistryManager(),
                    stack
                    //#if MC > 11904
                    , true
                    //#endif
            ).map(Collections::singletonList).orElse(Collections.emptyList());

        for (ArmorTrim trim : trims)
            renderTrim(elytra, trim, matrices, provider, entity, stack, light, alpha);
    }

    private void renderTrim(ElytraEntityModel<?> elytra, ArmorTrim trim, MatrixStack matrices, VertexConsumerProvider provider, LivingEntity entity, ItemStack stack, int light, float alpha) {
        if (trim == null)
            return;

        Sprite sprite = getTrimSprite(trim);
        if (isMissing(sprite))
            if (this.attRenderer != null) {
                this.attRenderer.renderTrim(elytra, trim, stack, matrices, provider, light, alpha);
                return;
            } else if (skipRenderIfMissingTexture(entity))
                return;

        VertexConsumer vertexConsumer = sprite.getTextureSpecificVertexConsumer(
                ItemRenderer.getDirectItemGlintConsumer(
                        provider,
                        ELYTRA_LAYER.apply(ETAtlasHolder.NAME),
                        false,
                        stack.hasGlint()));
        elytra.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 1F, 1F, 1F, alpha);
    }

    private boolean renderJebElytra(ElytraEntityModel<?> elytra, MatrixStack matrices, VertexConsumerProvider provider, LivingEntity entity, ItemStack stack, int light, float alpha) {
        if (cancelRender(RenderConfig.RenderType.COLOR, entity))
            return true;

        if (((ElytraOverlaysAccessor) (Object) stack).elytra_trims$getPatterns().isEmpty() && stack.getName().getString().equals("jeb_")) {
            assert MinecraftClient.getInstance().world != null;
            long tick = MinecraftClient.getInstance().world.getTime() % 360;
            int color = MathHelper.hsvToRgb(tick / 360F, 1F, 1F);
            renderElytraColor(elytra, matrices, provider, entity, stack, light, color, alpha);
            return true;
        }
        return false;
    }

    private Sprite getTrimSprite(ArmorTrim trim) {
        return this.trimGetter.apply(trim);
    }

    private Sprite getPatternSprite(RegistryEntry<BannerPattern> pattern) {
        return this.patternGetter.apply(pattern);
    }

    private Sprite getOverlaySprite() {
        return this.atlas.getSprite(new Identifier("entity/elytra"));
    }

    private Sprite trimSpriteGetter(ArmorTrim trim) {
        String material = trim.getMaterial().value().assetName();
        Identifier identifier = trim.getPattern().value().assetId().withPath(path -> "trims/models/elytra/%s_%s".formatted(path, material));
        if (ElytraTrims.getConfig().texture.useDarkerTrim) {
            Sprite sprite = this.atlas.getSprite(identifier.withSuffixedPath("_darker"));
            if (!isMissing(sprite))
                return sprite;
        }
        return this.atlas.getSprite(identifier);
    }

    private Sprite patternSpriteGetter(RegistryEntry<BannerPattern> pattern) {
        Optional<RegistryKey<BannerPattern>> optional = pattern.getKey();
        if (optional.isEmpty())
            return this.atlas.getSprite(null);

        SpriteIdentifier shieldSprite = ElytraTrims.getConfig().texture.useBannerTextures
                ? TexturedRenderLayers.getBannerPatternTextureId(optional.get())
                : TexturedRenderLayers.getShieldPatternTextureId(optional.get());
        return this.atlas.getSprite(shieldSprite.getTextureId());
    }
}

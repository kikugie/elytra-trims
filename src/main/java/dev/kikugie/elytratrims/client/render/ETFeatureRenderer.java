package dev.kikugie.elytratrims.client.render;

import com.mojang.datafixers.util.Pair;
import dev.kikugie.elytratrims.client.ETClient;
import dev.kikugie.elytratrims.client.access.LivingEntityAccessor;
import dev.kikugie.elytratrims.client.compat.AllTheTrimsCompat;
import dev.kikugie.elytratrims.client.compat.StackableTrimsCompat;
import dev.kikugie.elytratrims.client.config.ETClientConfig;
import dev.kikugie.elytratrims.client.config.RenderConfig;
import dev.kikugie.elytratrims.client.config.RenderConfig.RenderType;
import dev.kikugie.elytratrims.client.config.TextureConfig;
import dev.kikugie.elytratrims.client.resource.ETAtlasHolder;
import dev.kikugie.elytratrims.client.resource.ImageUtils;
import dev.kikugie.elytratrims.client.resource.Textures;
import dev.kikugie.elytratrims.common.ETServer;
import dev.kikugie.elytratrims.common.access.ElytraOverlaysAccessor;
import dev.kikugie.elytratrims.common.plugin.ModStatus;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.model.ElytraEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static net.minecraft.client.render.RenderPhase.*;

@SuppressWarnings("DataFlowIssue")
public class ETFeatureRenderer {
    public static final Function<Identifier, RenderLayer> ELYTRA_LAYER = Util.memoize(texture -> RenderLayer.of("elytra_layer", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, VertexFormat.DrawMode.QUADS, 256, true, true, RenderLayer.MultiPhaseParameters.builder()
            .program(ENTITY_NO_OUTLINE_PROGRAM)
            .texture(new Texture(texture, false, false))
            .transparency(TRANSLUCENT_TRANSPARENCY)
            .cull(DISABLE_CULLING)
            .lightmap(ENABLE_LIGHTMAP)
            .overlay(ENABLE_OVERLAY_COLOR)
            .layering(VIEW_OFFSET_Z_LAYERING)
            .writeMaskState(COLOR_MASK)
            .build(true)));
    private final ETAtlasHolder holder;
    private final SpriteAtlasTexture atlas;
    private final ETClientConfig config = ETClient.getConfig();
    private Function<ArmorTrim, Sprite> trimGetter;
    private Function<RegistryEntry<BannerPattern>, Sprite> patternGetter;

    public ETFeatureRenderer() {
        holder = ETClient.getAtlasHolder();
        atlas = holder.getAtlas();
        resetCache();
    }

    public void resetCache() {
        trimGetter = Util.memoize(this::trimSpriteGetter);
        patternGetter = Util.memoize(this::patternSpriteGetter);
    }

    public void render(ElytraEntityModel<?> elytra, MatrixStack matrices, VertexConsumerProvider provider, LivingEntity entity, ItemStack stack, int light, float alpha) {
        if (!holder.isReady()) return;
        if (!cancelRender(RenderType.GLOW, entity) && ETServer.GLOWING.hasGlow(stack))
            light = 0xFF00FF;

        if (!renderJebElytra(elytra, matrices, provider, entity, stack, light, alpha)) {
            renderElytraOverlay(elytra, matrices, provider, entity, stack, light, alpha);
            renderElytraPatterns(elytra, matrices, provider, entity, stack, light, alpha);
        }
        renderElytraTrims(elytra, matrices, provider, entity, stack, light, alpha);
    }

    private void renderElytraTrims(ElytraEntityModel<?> elytra, MatrixStack matrices, VertexConsumerProvider provider, LivingEntity entity, ItemStack stack, int light, float alpha) {
        if (cancelRender(RenderConfig.RenderType.TRIMS, entity))
            return;

        World world = entity.getWorld();

        List<ArmorTrim> trims;
        if (ModStatus.isLoaded("stacked_trims"))
            trims = StackableTrimsCompat.getTrims(world.getRegistryManager(), stack);
        else {
            Optional<ArmorTrim> optional = ArmorTrim.getTrim(world.getRegistryManager(),
                    stack
                    /*? if >=1.20.2 */
                    /*, true*/
            );
            trims = optional.map(List::of).orElseGet(List::of);
        }

        for (ArmorTrim trim : trims)
            renderTrim(elytra, trim, matrices, provider, entity, stack, light, alpha);
    }

    private void renderElytraPatterns(ElytraEntityModel<?> elytra, MatrixStack matrices, VertexConsumerProvider provider, LivingEntity entity, ItemStack stack, int light, float alpha) {
        if (cancelRender(RenderConfig.RenderType.PATTERNS, entity))
            return;

        List<Pair<RegistryEntry<BannerPattern>, DyeColor>> patterns = ((ElytraOverlaysAccessor) (Object) stack).elytra_trims$getPatterns();

        for (int i = 0; i < 17 && i < patterns.size(); i++) {
            Pair<RegistryEntry<BannerPattern>, DyeColor> pair = patterns.get(i);
            Sprite sprite = getPatternSprite(pair.getFirst());
            if (ImageUtils.isMissing(sprite))
                continue;

            float[] color = pair.getSecond().getColorComponents();
            VertexConsumer vertexConsumer = sprite.getTextureSpecificVertexConsumer(
                    ItemRenderer.getDirectItemGlintConsumer(
                            provider,
                            ELYTRA_LAYER.apply(ETAtlasHolder.ID),
                            false,
                            stack.hasGlint()));
            elytra.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, color[0], color[1], color[2], alpha);
        }
    }

    private void renderElytraOverlay(ElytraEntityModel<?> elytra, MatrixStack matrices, VertexConsumerProvider provider, LivingEntity entity, ItemStack stack, int light, float alpha) {
        if (cancelRender(RenderType.COLOR, entity))
            return;

        int color = ((ElytraOverlaysAccessor) (Object) stack).elytra_trims$getColor();
        if (color != 0)
            renderElytraColor(elytra, matrices, provider, entity, stack, light, color, alpha);
    }

    /**
     * Easter egg hee hee.
     * @return {@code true} if jeb_ variant was rendered.
     */
    private boolean renderJebElytra(ElytraEntityModel<?> elytra, MatrixStack matrices, VertexConsumerProvider provider, LivingEntity entity, ItemStack stack, int light, float alpha) {
        if (cancelRender(RenderType.COLOR, entity))
            return true;

        if (((ElytraOverlaysAccessor) (Object) stack).elytra_trims$getPatterns().isEmpty() && stack.getName().getString().equals("jeb_")) {
            long tick = MinecraftClient.getInstance().getRenderTime() % 360;
            int color = MathHelper.hsvToRgb(tick / 360F, 1F, 1F);
            renderElytraColor(elytra, matrices, provider, entity, stack, light, color, alpha);
            return true;
        }
        return false;
    }

    /**
     * Implementation for {@link ETFeatureRenderer#renderElytraOverlay} and {@link ETFeatureRenderer#renderJebElytra}
     */
    private void renderElytraColor(ElytraEntityModel<?> elytra, MatrixStack matrices, VertexConsumerProvider provider, LivingEntity ignoredEntity, ItemStack stack, int light, int color, float alpha) {
        Sprite sprite = getOverlaySprite();
        if (ImageUtils.isMissing(sprite))
            return;

        float red = (float) (color >> 16 & 0xFF) / 255.0F;
        float green = (float) (color >> 8 & 0xFF) / 255.0F;
        float blue = (float) (color & 0xFF) / 255.0F;
        VertexConsumer vertexConsumer = sprite.getTextureSpecificVertexConsumer(
                ItemRenderer.getDirectItemGlintConsumer(
                        provider,
                        ELYTRA_LAYER.apply(ETAtlasHolder.ID),
                        false,
                        stack.hasGlint()));
        elytra.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, red, green, blue, alpha);
    }

    private void renderTrim(ElytraEntityModel<?> elytra, ArmorTrim trim, MatrixStack matrices, VertexConsumerProvider provider, LivingEntity entity, ItemStack stack, int light, float alpha) {
        if (trim == null)
            return;

        Sprite sprite = getTrimSprite(trim);
        if (ImageUtils.isMissing(sprite))
            if (ModStatus.isLoaded("allthetrims")) {
                AllTheTrimsCompat.renderTrim(atlas, elytra, trim, stack, matrices, provider, light, alpha);
                return;
            } else if (!renderMissingTexture(entity))
                return;

        VertexConsumer vertexConsumer = sprite.getTextureSpecificVertexConsumer(
                ItemRenderer.getDirectItemGlintConsumer(
                        provider,
                        ELYTRA_LAYER.apply(ETAtlasHolder.ID),
                        false,
                        stack.hasGlint()));
        elytra.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 1F, 1F, 1F, alpha);
    }

    /**
     * Decides if rendering of a feature should be canceled entirely based on the player's config.
     */
    public boolean cancelRender(RenderType type, LivingEntity entity) {
        RenderConfig.RenderMode mode = config.render.getEffective(type);
        PlayerEntity player = MinecraftClient.getInstance().player;
        return switch (mode) {
            case ALL -> false;
            case NONE -> true;
            case SELF -> entity != player && !renderMissingTexture(entity);
            case OTHERS -> entity == player || renderMissingTexture(entity);
        };
    }

    /**
     * Implementation for {@link ETFeatureRenderer#trimGetter}. Textures should be located at {@code {trim namespace}/textures/trims/models/elytra}. If {@link TextureConfig#useDarkerTrim} is enabled attempts to get a darker version.
     */
    private Sprite trimSpriteGetter(ArmorTrim trim) {
        String material = trim.getMaterial().value().assetName();
        Identifier identifier = trim.getPattern().value().assetId().withPath(path -> "trims/models/elytra/%s_%s".formatted(path, material));
        if (config.texture.useDarkerTrim.get()) {
            Sprite darker = this.atlas.getSprite(identifier.withSuffixedPath("_darker"));
            if (!ImageUtils.isMissing(darker)) return darker;
        }
        return atlas.getSprite(identifier);
    }

    /**
     * Implementation for {@link ETFeatureRenderer#patternGetter}. Textures copy the respective shield/banner path.
     */
    private Sprite patternSpriteGetter(RegistryEntry<BannerPattern> pattern) {
        Optional<RegistryKey<BannerPattern>> optional = pattern.getKey();
        if (optional.isEmpty()) return atlas.getSprite(null);

        SpriteIdentifier shieldSprite = config.texture.useBannerTextures.get()
                ? TexturedRenderLayers.getBannerPatternTextureId(optional.get())
                : TexturedRenderLayers.getShieldPatternTextureId(optional.get());
        return atlas.getSprite(shieldSprite.getTextureId());
    }

    /**
     * If a trim texture is missing, the mod will skip it instead of rendering the purple-black checkerboard because it's ugly.<br>
     * However, it will render it on the armor stand inside a smithing table to inform the player.
     */
    public static boolean renderMissingTexture(LivingEntity entity) {
        return ((LivingEntityAccessor) entity).elytra_trims$isGui();
    }

    private Sprite getTrimSprite(ArmorTrim trim) {
        return this.trimGetter.apply(trim);
    }

    private Sprite getPatternSprite(RegistryEntry<BannerPattern> pattern) {
        return this.patternGetter.apply(pattern);
    }

    private Sprite getOverlaySprite() {
        return this.atlas.getSprite(Textures.COLOR_OVERLAY);
    }
}
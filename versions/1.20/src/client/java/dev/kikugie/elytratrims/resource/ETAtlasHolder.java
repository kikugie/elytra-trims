package dev.kikugie.elytratrims.resource;

import dev.kikugie.elytratrims.ElytraTrims;
import dev.kikugie.elytratrims.config.TextureConfig;
import dev.kikugie.elytratrims.render.ExtraElytraFeatureRenderer;
import dev.kikugie.elytratrims.util.LogWrapper;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.*;
import net.minecraft.client.texture.SpriteLoader.StitchResult;
import net.minecraft.client.texture.atlas.AtlasLoader;
import net.minecraft.client.texture.atlas.Sprite;
import net.minecraft.registry.Registries;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

public class ETAtlasHolder implements SimpleResourceReloadListener<StitchResult> {
    public static final Identifier ELYTRA_OUTLINE = ElytraTrims.id("item/elytra_outline");

    private static final Identifier ELYTRA_ITEM = new Identifier("textures/item/elytra.png");
    private static final Identifier ELYTRA_MODEL = new Identifier("textures/entity/elytra.png");
    private static final Identifier ELYTRA_TRIM = ElytraTrims.id("trims/items/default");
    private static final LogWrapper LOGGER = LogWrapper.of(ETAtlasHolder.class);
    private static final int OUTLINE_COLOR = 0xFF555555;
    public static Identifier TEXTURE = ElytraTrims.id("textures/atlas/elytra_features.png");
    public static Identifier NAME = ElytraTrims.id("elytra_features");
    private static ETAtlasHolder instance;
    private SpriteAtlasTexture atlas;

    public static ETAtlasHolder create() {
        instance = new ETAtlasHolder();
        return instance;
    }

    public static Optional<ETAtlasHolder> getInstance() {
        return Optional.ofNullable(instance);
    }

    public SpriteAtlasTexture getAtlas() {
        return this.atlas;
    }

    public void init() {
        this.atlas = new SpriteAtlasTexture(TEXTURE);
        MinecraftClient.getInstance().getTextureManager().registerTexture(NAME, this.atlas);
        ElytraTrims.ELYTRA_RENDERER = new ExtraElytraFeatureRenderer(this.atlas);
    }

    @Override
    public CompletableFuture<StitchResult> load(ResourceManager manager, Profiler profiler, Executor executor) {
        return CompletableFuture
                .supplyAsync(() -> {
                    this.atlas.clear();
                    return getSprites(manager);
                }, executor)
                .thenCompose(sprites -> SpriteLoader.loadAll(sprites, executor))
                .thenApply(sprites -> SpriteLoader.fromAtlas(this.atlas).stitch(sprites, 0, executor))
                .thenCompose(StitchResult::whenComplete);
    }

    private List<Supplier<SpriteContents>> getSprites(ResourceManager manager) {
        Sprite elytraModel;
        try {
            elytraModel = ImageUtils.loadTexture(ELYTRA_MODEL, manager, 1);
        } catch (FileNotFoundException e) {
            LOGGER.error("Failed to load elytra model texture");
            return Collections.emptyList();
        }

        List<Supplier<SpriteContents>> sprites = new ArrayList<>(getTrims(manager, elytraModel));
        sprites.addAll(getPatterns(manager, elytraModel));
        sprites.add(getOverlay(elytraModel));
//        sprites.add(getOutline(manager));
        sprites.add(MissingSprite::createSpriteContents);

        ETResourceListener.close();
        elytraModel.close();
        return sprites;
    }

    private Collection<Supplier<SpriteContents>> getTrims(ResourceManager manager, Sprite elytraModel) {
        AtlasLoader trimSources = new AtlasLoader(ETResourceListener.getTrims());
        TextureConfig config = ElytraTrims.getConfig().texture;
        return config.cropTrims
                ? ImageUtils.transform(trimSources.loadSources(manager), image -> ImageUtils.mask(image, elytraModel))
                : trimSources.loadSources(manager);
    }

    private Collection<Supplier<SpriteContents>> getPatterns(ResourceManager manager, Sprite elytraModel) {
        List<Supplier<SpriteContents>> patterns = new ArrayList<>();
        TextureConfig config = ElytraTrims.getConfig().texture;
        ResourceFinder finder = new ResourceFinder("textures", ".png");
        Registries.BANNER_PATTERN.getKeys().forEach(key -> patterns.add(() -> {
            Identifier id = BannerPattern.getSpriteId(key, config.useBannerTextures);
            Identifier texture = finder.toResourcePath(id);
            NativeImage pattern;
            try {
                pattern = ImageUtils.loadTexture(texture, manager, 1).read();
            } catch (IOException e) {
                LOGGER.error("Failed to load pattern texture: {}", id);
                return null;
            }

            pattern = config.useBannerTextures
                    ? ImageUtils.dims(pattern, pattern.getWidth() * 2, pattern.getHeight())
                    : ImageUtils.dims(pattern, pattern.getWidth(), pattern.getHeight() / 2);
            int scale = pattern.getWidth() / 64;
            int xOffset = (int) ((config.useBannerTextures ? 35.5F : 34F) * scale);
            int yOffset = config.useBannerTextures ? (int) (scale * 1.5F) : 0;
            NativeImage offset = ImageUtils.offsetNotClosing(pattern, xOffset, yOffset , pattern.getWidth(), pattern.getHeight());

            return ImageUtils.mask(ImageUtils.createContents(offset, id), elytraModel);
        }));
        return patterns;
    }

    private Supplier<SpriteContents> getOverlay(Sprite elytraModel) {
        return () -> ImageUtils.createContents(ImageUtils.createSaturationMaskNotClosing(elytraModel), elytraModel.id.withPath(path -> path.replace("textures/", "").replace(".png", "")));
    }

    private Supplier<SpriteContents> getOutline(ResourceManager manager) {
        return () -> {
            Sprite elytra;
            try {
                elytra = ImageUtils.loadTexture(ELYTRA_ITEM, manager, 1);
            } catch (FileNotFoundException e) {
                LOGGER.error("Failed to load elytra model texture");
                return null;
            }

            try {
                return ImageUtils.createContents(ImageUtils.outlineNotClosing(elytra.read(), OUTLINE_COLOR), ELYTRA_OUTLINE);
            } catch (IOException e) {
                LOGGER.error("Failed to create outline texture");
                return null;
            } finally {
                elytra.close();
            }
        };
    }

    @Override
    public CompletableFuture<Void> apply(StitchResult data, ResourceManager manager, Profiler profiler, Executor executor) {
        return CompletableFuture.runAsync(() -> {
            profiler.startTick();
            profiler.push("upload");
            this.atlas.upload(data);
            profiler.pop();
            profiler.endTick();
        }, executor);
    }

    @Override
    public Identifier getFabricId() {
        return ElytraTrims.id("elytra_features");
    }
}

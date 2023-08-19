package dev.kikugie.elytratrims.resource;

import dev.kikugie.elytratrims.ElytraTrims;
import dev.kikugie.elytratrims.render.ExtraElytraFeatureRenderer;
import dev.kikugie.elytratrims.util.LogWrapper;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.texture.*;
import net.minecraft.client.texture.SpriteLoader.StitchResult;
import net.minecraft.client.texture.atlas.AtlasLoader;
import net.minecraft.client.texture.atlas.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.registry.Registries;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

public class ETAtlasHolder implements SimpleResourceReloadListener<StitchResult> {
    private static final LogWrapper LOGGER = LogWrapper.of(ETAtlasHolder.class);
    private static final Identifier ELYTRA_MODEL = new Identifier("textures/entity/elytra.png");
    public static Identifier TEXTURE = ElytraTrims.id("textures/atlas/elytra_features.png");
    public static Identifier NAME = ElytraTrims.id("elytra_features");
    private static ETAtlasHolder instance;
    SpriteAtlasTexture atlas;

    public static ETAtlasHolder create() {
        instance = new ETAtlasHolder();
        return instance;
    }

    public static ETAtlasHolder getInstance() {
        return instance;
    }

    private void init() {
        this.atlas = new SpriteAtlasTexture(TEXTURE);
        MinecraftClient.getInstance().getTextureManager().registerTexture(NAME, this.atlas);
        ElytraTrims.ELYTRA_RENDERER = new ExtraElytraFeatureRenderer(this.atlas);
    }

    @Override
    public CompletableFuture<StitchResult> load(ResourceManager manager, Profiler profiler, Executor executor) {
        return CompletableFuture
                .supplyAsync(() -> {
                    init();
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
        sprites.add(MissingSprite::createSpriteContents);

        ETResourceListener.close();
        elytraModel.close();
        return sprites;
    }

    private Collection<Supplier<SpriteContents>> getTrims(ResourceManager manager, Sprite elytraModel) {
        AtlasLoader trimSources = new AtlasLoader(ETResourceListener.getTrims());
        return ImageUtils.transform(trimSources.loadSources(manager),
                image -> ImageUtils.mask(image, elytraModel));
    }

    private Collection<Supplier<SpriteContents>> getPatterns(ResourceManager manager, Sprite elytraModel) {
        List<Supplier<SpriteContents>> patterns = new ArrayList<>();
        ResourceFinder finder = new ResourceFinder("textures", ".png");
        Registries.BANNER_PATTERN.getKeys().forEach(key -> patterns.add(() -> {
            SpriteIdentifier sprite = TexturedRenderLayers.getShieldPatternTextureId(key);
            Identifier id = finder.toResourcePath(sprite.getTextureId());
            NativeImage pattern;
            try {
                pattern = ImageUtils.loadTexture(id, manager, 1).read();
            } catch (IOException e) {
                LOGGER.error("Failed to load pattern texture: {}", id);
                return null;
            }
            int scale = pattern.getWidth() / 64;
            NativeImage offset = ImageUtils.offsetNotClosing(pattern, 34 * scale, 0, pattern.getWidth(), pattern.getHeight() / 2);
            return ImageUtils.mask(ImageUtils.createContents(offset, id), elytraModel);
        }));
        return patterns;
    }

    private Supplier<SpriteContents> getOverlay(Sprite elytraModel) {
        return () -> ImageUtils.createContents(ImageUtils.createSaturationMaskNotClosing(elytraModel), elytraModel.id.withSuffixedPath("_overlay"));
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

package dev.kikugie.elytratrims.client.resource;

import dev.kikugie.elytratrims.client.ETClient;
import dev.kikugie.elytratrims.client.config.TextureConfig;
import dev.kikugie.elytratrims.common.ETReference;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.*;
import net.minecraft.client.texture.atlas.AtlasLoader;
import net.minecraft.client.texture.atlas.Sprite;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.Supplier;

public class ETAtlasHolder implements ResourceReloader {
    /*? if >=1.20.2 */
    /*private static final SpriteOpener opener = SpriteOpener.create(SpriteLoader.METADATA_READERS);*/
    public static final Identifier TEXTURE = ETReference.id("textures/atlas/elytra_features.png");
    public static final Identifier ID = ETReference.id("elytra_features");
    private final SpriteAtlasTexture atlas;
    private boolean ready = false;

    public ETAtlasHolder() {
        atlas = new SpriteAtlasTexture(TEXTURE);
        MinecraftClient.getInstance().getTextureManager().registerTexture(ID, atlas);
    }

    public boolean isReady() {
        return ready;
    }

    public SpriteAtlasTexture getAtlas() {
        return atlas;
    }

    private List<Supplier<SpriteContents>> getSprites(ResourceManager manager) {
        Sprite elytra;
        try {
            elytra = ImageUtils.loadTexture(Textures.ELYTRA_MODEL, manager, 1);
        } catch (FileNotFoundException e) {
            ETReference.LOGGER.error("Failed to load elytra model texture");
            return List.of();
        }

        List<Supplier<SpriteContents>> sprites = new ArrayList<>(getTrims(manager, elytra));
        sprites.addAll(getPatterns(manager, elytra));
        sprites.add(getOverlay(elytra));
        sprites.add(MissingSprite::createSpriteContents);
        elytra.close();
        return sprites;
    }

    private Collection<Supplier<SpriteContents>> getTrims(ResourceManager manager, Sprite elytra) {
        var preSources = new AtlasLoader(ETResourceListener.getTrims()).loadSources(manager);
        /*? if <1.20.2 {*/
        var trimSources = preSources;
        /*?} else {*//*
        Collection<Supplier<SpriteContents>> trimSources = new ArrayList<>(preSources.size());
        for (Function<SpriteOpener, SpriteContents> func : preSources)
            trimSources.add(() -> func.apply(opener));
        *//*?} */
        return ETClient.getConfig().texture.cropTrims.get() ? ImageUtils.transform(trimSources, it -> ImageUtils.mask(it, elytra)) : trimSources;
    }

    private @NotNull Collection<Supplier<SpriteContents>> getPatterns(ResourceManager manager, Sprite elytraModel) {
        List<Supplier<SpriteContents>> patterns = new ArrayList<>();
        TextureConfig config = ETClient.getConfig().texture;
        ResourceFinder finder = new ResourceFinder("textures", ".png");
        for (RegistryKey<BannerPattern> key : Registries.BANNER_PATTERN.getKeys()) {
            patterns.add(() -> {
                Identifier id = BannerPattern.getSpriteId(key, config.useBannerTextures.get());
                Identifier texture = finder.toResourcePath(id);
                NativeImage pattern;
                try {
                    pattern = ImageUtils.loadTexture(texture, manager, 1).read();
                } catch (IOException e) {
                    ETReference.LOGGER.error("Failed to load pattern texture: {}", id);
                    return null;
                }

                pattern = config.useBannerTextures.get()
                        ? ImageUtils.dims(pattern, pattern.getWidth() * 2, pattern.getHeight())
                        : ImageUtils.dims(pattern, pattern.getWidth(), pattern.getHeight() / 2);
                int scale = pattern.getWidth() / 64;
                int xOffset = (int) ((config.useBannerTextures.get() ? 35.5F : 34F) * scale);
                int yOffset = config.useBannerTextures.get() ? (int) (scale * 1.5F) : 0;
                NativeImage offset = ImageUtils.offsetNotClosing(pattern, xOffset, yOffset, pattern.getWidth(), pattern.getHeight());

                return ImageUtils.mask(ImageUtils.createContents(offset, id), elytraModel);
            });
        }
        return patterns;
    }

    private Supplier<SpriteContents> getOverlay(Sprite elytraModel) {
        return () -> ImageUtils.createContents(ImageUtils.createSaturationMaskNotClosing(elytraModel), elytraModel.id.withPath(path -> path.replace("textures/", "").replace(".png", "")));
    }

    private CompletableFuture<List<SpriteContents>> transform(List<Supplier<SpriteContents>> sprites, Executor executor) {
        /*? if <1.20.2 {*/
        return SpriteLoader.loadAll(sprites, executor);
        /*?} else {*//*
        List<Function<SpriteOpener, SpriteContents>> transformed = new ArrayList<>(sprites.size());
        for (Supplier<SpriteContents> sup : sprites)
            transformed.add(o -> sup.get());
        return SpriteLoader.loadAll(opener, transformed, executor);
        *//*?} */
    }

    CompletableFuture<SpriteLoader.StitchResult> load(ResourceManager manager, Profiler ignoredProfiler, Executor executor) {
        return CompletableFuture
                .supplyAsync(() -> {
                    ready = false;
                    atlas.clear();
                    return getSprites(manager);
                }, executor)
                .thenCompose(sprites -> transform(sprites, executor))
                .thenApply(sprites -> SpriteLoader.fromAtlas(atlas).stitch(sprites, 0, executor))
                .thenCompose(SpriteLoader.StitchResult::whenComplete);
    }

    CompletableFuture<Void> apply(SpriteLoader.StitchResult data, ResourceManager ignoredManager, Profiler profiler, Executor executor) {
        return CompletableFuture.runAsync(() -> {
            profiler.startTick();
            profiler.push("upload");
            ETResourceListener.reset();
            ETClient.getRenderer().resetCache();
            atlas.upload(data);
            ready = true;
            profiler.pop();
            profiler.endTick();
        }, executor);
    }

    @Override
    public CompletableFuture<Void> reload(ResourceReloader.Synchronizer helper, ResourceManager manager, Profiler loadProfiler, Profiler applyProfiler, Executor loadExecutor, Executor applyExecutor) {
        return load(manager, loadProfiler, loadExecutor).thenCompose(helper::whenPrepared).thenCompose((o) -> apply(o, manager, applyProfiler, applyExecutor));
    }
}
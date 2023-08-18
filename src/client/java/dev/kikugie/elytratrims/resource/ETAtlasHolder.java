package dev.kikugie.elytratrims.resource;

import dev.kikugie.elytratrims.ElytraTrims;
import dev.kikugie.elytratrims.util.LogWrapper;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.SpriteContents;
import net.minecraft.client.texture.SpriteLoader;
import net.minecraft.client.texture.SpriteLoader.StitchResult;
import net.minecraft.client.texture.atlas.AtlasLoader;
import net.minecraft.client.texture.atlas.Sprite;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

public record ETAtlasHolder(SpriteAtlasTexture atlas) implements SimpleResourceReloadListener<StitchResult> {
    private static ETAtlasHolder instance;
    private static final LogWrapper LOGGER = LogWrapper.of("ET Atlas");
    public static Identifier TEXTURE = ElytraTrims.id("textures/atlas/elytra_features.png");
    public static Identifier NAME = ElytraTrims.id("elytra_features");
    private static final Identifier ELYTRA_MODEL = new Identifier("textures/entity/elytra.png");

    public static ETAtlasHolder init() {
        SpriteAtlasTexture atlas = new SpriteAtlasTexture(NAME);
        MinecraftClient.getInstance().getTextureManager().registerTexture(TEXTURE, atlas);
        instance = new ETAtlasHolder(atlas);
        return instance;
    }

    public static ETAtlasHolder getInstance() {
        return instance;
    }

    @Override
    public CompletableFuture<StitchResult> load(ResourceManager manager, Profiler profiler, Executor executor) {
        return CompletableFuture.supplyAsync(() -> getSprites(manager), executor)
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

        ETResourceListener.close();
        elytraModel.close();
        return sprites;
    }

    private Collection<Supplier<SpriteContents>> getTrims(ResourceManager manager, Sprite elytraModel) {
        AtlasLoader trimSources = new AtlasLoader(ETResourceListener.getTrims());
        return ImageUtils.transform(trimSources.loadSources(manager),
                image -> ImageUtils.mask(image, elytraModel));
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

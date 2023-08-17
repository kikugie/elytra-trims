package dev.kikugie.elytratrims.texture;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.kikugie.elytratrims.ElytraTrims;
import net.minecraft.client.resource.metadata.AnimationResourceMetadata;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.SpriteContents;
import net.minecraft.client.texture.SpriteDimensions;
import net.minecraft.client.texture.atlas.AtlasSource;
import net.minecraft.client.texture.atlas.AtlasSourceType;
import net.minecraft.client.texture.atlas.Sprite;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

public class ElytraPatternsAtlasSource implements AtlasSource {
    public static final Codec<ElytraPatternsAtlasSource> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("patterns").forGetter(atlasSource -> atlasSource.patterns),
            Identifier.CODEC.fieldOf("mask").forGetter(atlasSource -> atlasSource.maskKey),
            Codec.INT.fieldOf("x_offset").forGetter(atlasSource -> atlasSource.xOffset),
            Codec.INT.fieldOf("y_offset").forGetter(atlasSource -> atlasSource.yOffset)
    ).apply(instance, ElytraPatternsAtlasSource::new));
    private final Identifier patterns;
    private final Identifier maskKey;
    private final int xOffset;
    private final int yOffset;

    private ElytraPatternsAtlasSource(Identifier patterns, Identifier maskKey, int xOffset, int yOffset) {
        this.patterns = patterns;
        this.maskKey = maskKey;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }

    @Override
    public void load(ResourceManager resourceManager, SpriteRegions regions) {
        Identifier maskPath = RESOURCE_FINDER.toResourcePath(this.maskKey);
        Sprite mask;

        ResourceFinder patternsFinder = new ResourceFinder("textures/%s".formatted(this.patterns.getPath()), ".png");
        Map<Identifier, Resource> resources = patternsFinder.findResources(resourceManager);

        try {
            mask = ImageUtils.loadTexture(maskPath, resourceManager, resources.size());
        } catch (FileNotFoundException e) {
            return;
        }

        resources.forEach((identifier, resource) -> {
            try {
                Identifier sourcePath = patternsFinder.toResourcePath(identifier);
                Identifier sourceId = patternsFinder.toResourceId(identifier).withPrefixedPath(this.maskKey.getPath() + "/patterns/");
                Sprite source = ImageUtils.loadTexture(identifier, resourceManager, 1);
                regions.add(sourceId, new ElytraPatternsSpriteRegion(source, mask, sourcePath, maskPath, this.yOffset, this.xOffset, sourceId));
            } catch (FileNotFoundException ignored) {
            }
        });
    }

    @Override
    public AtlasSourceType getType() {
        return ElytraTrims.ELYTRA_PATTERNS;
    }

    private record ElytraPatternsSpriteRegion(
            Sprite source,
            Sprite mask,
            Identifier sourceId,
            Identifier maskId,
            int yOffset,
            int xOffset,
            Identifier key
    ) implements SpriteRegion {

        @Override
        //#if MC < 12002
        public SpriteContents get()
        //#else
        //$$ public SpriteContents apply(net.minecraft.client.texture.SpriteOpener unused)
        //#endif
        {
            NativeImage tempSource;
            NativeImage tempMask;
            try {
                tempSource = this.source.read();
                tempMask = this.mask.read();
            } catch (IOException e) {
                return null;
            }

            Pair<NativeImage, NativeImage> pair = ImageUtils.matchWidth(tempSource, tempMask, this.sourceId, this.maskId);
            if (pair == null) return null;

            NativeImage localSource = pair.getLeft();
            NativeImage localMask = pair.getRight();
            int width = localMask.getWidth();
            int height = localMask.getHeight();
            int scale = tempSource.getWidth() / tempMask.getWidth();

            NativeImage offsetSource = ImageUtils.offsetClosing(localSource, this.xOffset * scale, this.yOffset * scale, width, height);
            ImageUtils.applyMaskClosing(offsetSource, localMask);
            return new SpriteContents(this.key,
                    new SpriteDimensions(width, height),
                    offsetSource,
                    //#if MC < 12002
                    AnimationResourceMetadata.EMPTY
                    //#else
                    //$$ net.minecraft.resource.metadata.ResourceMetadata.NONE
                    //#endif
            );
        }

        @Override
        public void close() {
            this.source.close();
        }
    }
}

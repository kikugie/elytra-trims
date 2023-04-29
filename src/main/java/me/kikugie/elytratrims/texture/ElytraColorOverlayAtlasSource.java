package me.kikugie.elytratrims.texture;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.resource.metadata.AnimationResourceMetadata;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.SpriteContents;
import net.minecraft.client.texture.SpriteDimensions;
import net.minecraft.client.texture.atlas.AtlasSource;
import net.minecraft.client.texture.atlas.AtlasSourceType;
import net.minecraft.client.texture.atlas.Sprite;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.FileNotFoundException;
import java.io.IOException;

public class ElytraColorOverlayAtlasSource implements AtlasSource {
    public static final Codec<ElytraColorOverlayAtlasSource> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("overlay").forGetter(atlasSource -> atlasSource.overlay),
            Identifier.CODEC.fieldOf("mask").forGetter(atlasSource -> atlasSource.maskKey)
    ).apply(instance, ElytraColorOverlayAtlasSource::new));
    private final Identifier maskKey;
    private final Identifier overlay;

    private ElytraColorOverlayAtlasSource(Identifier overlay, Identifier maskKey) {
        this.overlay = overlay;
        this.maskKey = maskKey;
    }

    @Override
    public void load(ResourceManager resourceManager, AtlasSource.SpriteRegions regions) {
        Identifier maskPath = RESOURCE_FINDER.toResourcePath(maskKey);
        Identifier sourcePath = RESOURCE_FINDER.toResourcePath(overlay);

        try {
            Sprite mask = ImageUtils.loadTexture(maskPath, resourceManager, 1);
            Sprite source = ImageUtils.loadTexture(sourcePath, resourceManager, 1);
            Identifier key = RESOURCE_FINDER.toResourceId(maskPath).withSuffixedPath("_overlay");
            regions.add(key, new ElytraColorOverlaySpriteRegion(source, mask, sourcePath, maskPath, key));
        } catch (FileNotFoundException ignored) {
        }
    }

    @Override
    public AtlasSourceType getType() {
        return null;
    }

    private record ElytraColorOverlaySpriteRegion(
            Sprite source,
            Sprite mask,
            Identifier sourceId,
            Identifier maskId,
            Identifier key) implements AtlasSource.SpriteRegion {
        @Override
        public SpriteContents get() {
            NativeImage image;
            try {
                image = mask.read();
                ImageUtils.createSaturationMask(image);
            } catch (IOException e) {
                return null;
            }
            return new SpriteContents(key, new SpriteDimensions(image.getWidth(), image.getHeight()), image, AnimationResourceMetadata.EMPTY);
        }


        @Override
        public void close() {
            source.close();
            mask.close();
        }
    }
}

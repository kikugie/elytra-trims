package dev.kikugie.elytratrims.texture;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.kikugie.elytratrims.ElytraTrimsMod;
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
            Identifier.CODEC.fieldOf("source").forGetter(atlasSource -> atlasSource.overlay)
    ).apply(instance, ElytraColorOverlayAtlasSource::new));
    private final Identifier overlay;

    private ElytraColorOverlayAtlasSource(Identifier overlay) {
        this.overlay = overlay;
    }

    @Override
    public void load(ResourceManager resourceManager, SpriteRegions regions) {
        Identifier sourcePath = RESOURCE_FINDER.toResourcePath(overlay);

        try {
            Sprite source = ImageUtils.loadTexture(sourcePath, resourceManager, 1);
            Identifier key = ElytraTrimsMod.id(RESOURCE_FINDER.toResourceId(sourcePath).withSuffixedPath("_overlay").getPath());
            regions.add(key, new ElytraColorOverlaySpriteRegion(source, sourcePath, key));
        } catch (FileNotFoundException ignored) {
        }
    }

    @Override
    public AtlasSourceType getType() {
        return ElytraTrimsMod.ELYTRA_OVERLAY;
    }

    private record ElytraColorOverlaySpriteRegion(
            Sprite source,
            Identifier sourceId,
            Identifier key) implements SpriteRegion {
        @Override
        public SpriteContents get() {
            NativeImage image;
            try {
                image = source.read();
                ImageUtils.createSaturationMask(image);
            } catch (IOException e) {
                return null;
            }
            return new SpriteContents(key, new SpriteDimensions(image.getWidth(), image.getHeight()), image, AnimationResourceMetadata.EMPTY);
        }


        @Override
        public void close() {
            source.close();
        }
    }
}

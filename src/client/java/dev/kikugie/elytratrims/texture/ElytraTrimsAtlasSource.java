package dev.kikugie.elytratrims.texture;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.kikugie.elytratrims.ElytraTrimsMod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.SpriteContents;
import net.minecraft.client.texture.atlas.AtlasSourceType;
import net.minecraft.client.texture.atlas.PalettedPermutationsAtlasSource;
import net.minecraft.client.texture.atlas.Sprite;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.IntUnaryOperator;
import java.util.function.Supplier;

public class ElytraTrimsAtlasSource extends PalettedPermutationsAtlasSource {
    public static final Codec<ElytraTrimsAtlasSource> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.list(Identifier.CODEC).fieldOf("textures").forGetter(atlasSource -> atlasSource.textures),
            Codec.unboundedMap(Codec.STRING, Identifier.CODEC).fieldOf("permutations").forGetter(atlasSource -> atlasSource.permutations),
            Identifier.CODEC.fieldOf("palette_key").forGetter(atlasSource -> atlasSource.paletteKey),
            Identifier.CODEC.fieldOf("mask").forGetter(atlasSource -> atlasSource.maskKey)
    ).apply(instance, ElytraTrimsAtlasSource::new));
    private final Identifier maskKey;

    private ElytraTrimsAtlasSource(List<Identifier> textures, Map<String, Identifier> permutations, Identifier paletteKey, Identifier maskKey) {
        super(textures, paletteKey, permutations);
        this.maskKey = maskKey;
    }

    @Override
    public void load(ResourceManager resourceManager, SpriteRegions regions) {
        Identifier maskPath = RESOURCE_FINDER.toResourcePath(this.maskKey);
        Sprite mask;
        try {
            mask = ImageUtils.loadTexture(maskPath, resourceManager, 1);
        } catch (FileNotFoundException ignored) {
            return;
        }

        Supplier<int[]> supplier = Suppliers.memoize(() -> method_48486(resourceManager, this.paletteKey));
        Map<String, Supplier<IntUnaryOperator>> map = new HashMap<>();
        this.permutations.forEach((string, identifierx) -> map.put(string, Suppliers.memoize(() -> method_48492(supplier.get(), method_48486(resourceManager, identifierx)))));

        for (Identifier texture : this.textures) {
            Identifier sourcePath = RESOURCE_FINDER.toResourcePath(texture);
            try {
                Sprite source = ImageUtils.loadTexture(sourcePath, resourceManager, 1);
                for (Map.Entry<String, Supplier<IntUnaryOperator>> entry : map.entrySet()) {
                    Identifier trimmedPath = texture.withSuffixedPath("_%s".formatted(entry.getKey()));
                    regions.add(trimmedPath, new ElytraTrimsSpriteRegion(source, mask, sourcePath, maskPath, trimmedPath, entry.getValue()));
                }
            } catch (FileNotFoundException ignored) {
            }
        }
    }

    @Override
    public AtlasSourceType getType() {
        return ElytraTrimsMod.ELYTRA_TRIMS;
    }

    @Environment(EnvType.CLIENT)
    private record ElytraTrimsSpriteRegion(
            Sprite source,
            Sprite mask,
            Identifier sourceId,
            Identifier maskId,
            Identifier permutationLocation,
            Supplier<IntUnaryOperator> palette
    ) implements SpriteRegion {
        @Override
        public SpriteContents get() {
            return null;
        }

        @Override
        public void close() {
            this.source.close();
        }
    }
}

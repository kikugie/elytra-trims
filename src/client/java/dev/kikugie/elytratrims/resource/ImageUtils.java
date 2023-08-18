package dev.kikugie.elytratrims.resource;

import com.google.common.base.Preconditions;
import dev.kikugie.elytratrims.util.LogWrapper;
import net.minecraft.client.resource.metadata.AnimationResourceMetadata;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.SpriteContents;
import net.minecraft.client.texture.SpriteDimensions;
import net.minecraft.client.texture.atlas.Sprite;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class ImageUtils {
    private static final LogWrapper LOGGER = LogWrapper.of("ET Image Utils");

    public static Collection<Supplier<SpriteContents>> transform(Iterable<Supplier<SpriteContents>> contents,
                                                       UnaryOperator<@Nullable SpriteContents> operator) {
        List<Supplier<SpriteContents>> result = new ArrayList<>();
        for (Supplier<SpriteContents> supplier : contents)
            result.add(() -> operator.apply(supplier != null ? supplier.get() : null));
        return result;
    }

    @Nullable
    @Contract("!null, _ -> new")
    public static SpriteContents mask(@Nullable SpriteContents image, Sprite mask) {
        if (image == null)
            return null;
        if (image.createAnimator() != null) {
            LOGGER.warn("Cannot mask animated texture {}", image.getId());
            return null;
        }
        NativeImage masked = applyMask(image, mask);
        if (masked == null)
            return null;
        SpriteContents newContents = new SpriteContents(image.getId(), new SpriteDimensions(image.getWidth(), image.getHeight()), masked, AnimationResourceMetadata.EMPTY);
        image.close();
        return newContents;
    }

    @Nullable
    private static NativeImage applyMask(SpriteContents image, Sprite mask) {
        try {
            Pair<NativeImage, NativeImage> scaled = matchScale(image.image, copy(mask.read()), image.getId(), mask.id);
            if (scaled == null)
                return null;

            NativeImage masked = scaled.getLeft();
            NativeImage maskImg = scaled.getRight();

            for (int y = 0; y < masked.getHeight(); y++) {
                for (int x = 0; x < masked.getWidth(); x++) {
                    int alpha = maskImg.getColor(x, y) >> 24 & 0xFF;
                    if (alpha == 0)
                        masked.setColor(x, y, 0);
                }
            }
            return masked;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Nullable
    public static Pair<NativeImage, NativeImage> matchScale(NativeImage img1, NativeImage img2, Identifier id1, Identifier id2) {
        float xScale = (float) img1.getWidth() / img2.getWidth();
        float yScale = (float) img1.getHeight() / img2.getHeight();
        if (!isPowerOf2(xScale) || !isPowerOf2(yScale)) {
            LOGGER.error("Image scale ratio is not a power of 2 for image {} and mask {}", id1, id2);
            return null;
        }
        if ((int) xScale != (int) yScale) {
            LOGGER.error("Image scale ratio is not equal for image {} and mask {}", id1, id2);
            return null;
        }
        Pair<NativeImage, NativeImage> result;
        if (xScale == 1)
            result = new Pair<>(copy(img1), copy(img2));
        else if (xScale > 1)
            result = new Pair<>(upscale(img1, (int) xScale), copy(img2));
        else
            result = new Pair<>(copy(img1), upscale(img2, (int) (1 / xScale)));
        img1.close();
        img2.close();
        return result;
    }

    @Contract("_, _ -> new")
    private static NativeImage upscale(@NotNull NativeImage image, int scale) {
        Preconditions.checkArgument(scale > 0, "Scale must be greater than 0");
        int width = image.getWidth() * scale;
        int height = image.getHeight() * scale;

        NativeImage scaled = new NativeImage(width, height, true);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int color = image.getColor(x / scale, y / scale);
                scaled.setColor(x, y, color);
            }
        }
        image.close();
        return scaled;
    }

    @Contract("_ -> new")
    private static NativeImage copy(NativeImage source) {
        NativeImage copy = new NativeImage(source.getWidth(), source.getHeight(), true);
        copy.copyFrom(source);
        return copy;
    }

    private static boolean isPowerOf2(float n) {
        return Float.floatToIntBits(n) << 9 == 0;
    }

    public static Sprite loadTexture(Identifier id, ResourceManager resourceManager, int regions) throws FileNotFoundException {
        Optional<Resource> resource = resourceManager.getResource(id);
        if (resource.isPresent())
            return new net.minecraft.client.texture.atlas.Sprite(id, resource.get(), regions);
        LOGGER.error("Can't find texture: %s".formatted(id));
        throw new FileNotFoundException();
    }
}

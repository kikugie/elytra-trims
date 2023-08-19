package dev.kikugie.elytratrims.resource;

import com.google.common.base.Preconditions;
import dev.kikugie.elytratrims.util.LogWrapper;
import net.minecraft.client.resource.metadata.AnimationResourceMetadata;
import net.minecraft.client.texture.MissingSprite;
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

    public static SpriteContents createContents(NativeImage image, Identifier id) {
        return new SpriteContents(id, new SpriteDimensions(image.getWidth(), image.getHeight()), image, AnimationResourceMetadata.EMPTY);
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
        SpriteContents newContents = createContents(masked, image.getId());
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
            //TODO: Do something idk
            throw new RuntimeException(e);
        }
    }

    public static NativeImage createSaturationMaskNotClosing(Sprite sprite) {
        try {
            NativeImage image = sprite.read();
            int maxSaturation = 0;
            for (int y = 0; y < image.getHeight(); y++) {
                for (int x = 0; x < image.getWidth(); x++) {
                    int color = image.getColor(x, y);
                    int red = (color >> 16 & 0xFF);
                    int green = (color >> 8 & 0xFF);
                    int blue = (color & 0xFF);
                    int alpha = (color >> 24 & 0xFF);
                    if (alpha == 0) continue;
                    maxSaturation = Math.max(maxSaturation, Math.max(red, Math.max(green, blue)));
                }
            }
            int saturationDiff = 255 - maxSaturation;
            NativeImage masked = new NativeImage(image.getWidth(), image.getHeight(), true);

            for (int y = 0; y < image.getHeight(); y++) {
                for (int x = 0; x < image.getWidth(); x++) {
                    int color = image.getColor(x, y);
                    int red = (color >> 16 & 0xFF);
                    int green = (color >> 8 & 0xFF);
                    int blue = (color & 0xFF);
                    int saturation = (Math.max(red, Math.max(green, blue)) + saturationDiff);
                    masked.setColor(x, y, color & 0xFF000000 | saturation << 16 | saturation << 8 | saturation);
                }
            }
            return masked;
        } catch (IOException e) {
            //TODO: Do something idk
            throw new RuntimeException(e);
        }
    }

    public static NativeImage offsetNotClosing(NativeImage source, int dx, int dy, int width, int height) {
        NativeImage offset = new NativeImage(width, height, true);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                try {
                    offset.setColor(x + dx, y + dy, source.getColor(x, y));
                } catch (IllegalArgumentException ignored) {
                    // Ignore out of bounds
                }
            }
        }
        return offset;
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
    public static NativeImage copy(NativeImage source) {
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

    public static boolean isMissing(SpriteContents sprite) {
        return sprite.getId().equals(MissingSprite.getMissingSpriteId());
    }
}

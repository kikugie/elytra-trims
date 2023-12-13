package dev.kikugie.elytratrims.client.resource;

import com.google.common.base.Preconditions;
import dev.kikugie.elytratrims.common.ETReference;
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
    @SuppressWarnings({"unchecked"})
    public static <T extends Supplier<SpriteContents>> Collection<T> transform (Collection<T> contents, UnaryOperator<SpriteContents> operator) {
        List<T> result = new ArrayList<T>(contents.size());
        for (T func : contents)
            if (func != null) result.add((T) (Supplier<SpriteContents>) () -> operator.apply(func.get()));
        return result;
    }

    public static SpriteContents createContents(NativeImage image, Identifier id) {
        return new SpriteContents(id, new SpriteDimensions(image.getWidth(), image.getHeight()), image, AnimationResourceMetadata.EMPTY);
    }

    @Nullable
    public static SpriteContents mask(@Nullable SpriteContents image, net.minecraft.client.texture.atlas.Sprite mask) {
        if (image == null || isMissing(image))
            return null;
        if (image.createAnimator() != null) {
            ETReference.LOGGER.warn("Cannot mask animated texture {}", image.getId());
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

    public static NativeImage createSaturationMaskNotClosing(net.minecraft.client.texture.atlas.Sprite sprite) {
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
            ETReference.LOGGER.error("Image scale ratio is not a power of 2 for image {} and mask {}", id1, id2);
            return null;
        }
        if ((int) xScale != (int) yScale) {
            ETReference.LOGGER.error("Image scale ratio is not equal for image {} and mask {}", id1, id2);
            return null;
        }
        Pair<NativeImage, NativeImage> result;
        if (xScale == 1)
            result = new Pair<>(copy(img1), copy(img2));
        else if (xScale > 1)
            result = new Pair<>(copy(img1), upscale(img2, (int) xScale));
        else
            result = new Pair<>(upscale(img1, img2.getWidth() / img1.getWidth()), copy(img2));
        img1.close();
        img2.close();
        return result;
    }

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

    public static NativeImage outlineNotClosing(NativeImage source, int color) {
        NativeImage outline = new NativeImage(source.getWidth(), source.getHeight(), true);
        for (int y = 0; y < source.getHeight(); y++) {
            for (int x = 0; x < source.getWidth(); x++) {
                int sourceColor = source.getColor(x, y);
                if ((sourceColor >> 24 & 0xFF) == 0)
                    continue;
                // What the fuck
                if (getColorSafe(source, x - 1, y) == 0 ||
                        getColorSafe(source, x + 1, y) == 0 ||
                        getColorSafe(source, x, y - 1) == 0 ||
                        getColorSafe(source, x, y + 1) == 0)
                    outline.setColor(x, y, color);
            }
        }
        return outline;
    }

    private static int getColorSafe(NativeImage image, int x, int y) {
        if (x < 0 || y < 0 || x >= image.getWidth() || y >= image.getHeight())
            return 0;
        return image.getColor(x, y);
    }

    public static NativeImage dims(NativeImage source, int width, int height) {
        NativeImage dims = new NativeImage(width, height, true);
        int xMax = Math.min(source.getWidth(), width);
        int yMax = Math.min(source.getHeight(), height);

        for (int y = 0; y < yMax; y++) {
            for (int x = 0; x < xMax; x++) {
                dims.setColor(x, y, source.getColor(x, y));
            }
        }
        source.close();
        return dims;
    }

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
        ETReference.LOGGER.error("Can't find texture: %s".formatted(id));
        throw new FileNotFoundException();
    }

    public static boolean isMissing(SpriteContents sprite) {
        return sprite.getId().equals(MissingSprite.getMissingSpriteId());
    }

    public static boolean isMissing(net.minecraft.client.texture.Sprite sprite) {
        return sprite == null || isMissing(sprite.getContents());
    }
}
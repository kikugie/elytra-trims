package dev.kikugie.elytratrims.texture;

import dev.kikugie.elytratrims.ElytraTrimsMod;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.atlas.Sprite;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import org.jetbrains.annotations.Nullable;

import java.io.FileNotFoundException;
import java.util.Optional;

public class ImageUtils {
    public static boolean isPowerOf2(float n) {
        return Float.floatToIntBits(n) << 9 == 0;
    }

    public static Sprite loadTexture(Identifier id, ResourceManager resourceManager, int regions) throws FileNotFoundException {
        Optional<Resource> resource = resourceManager.getResource(id);
        if (resource.isPresent()) return new Sprite(id, resource.get(), regions);

        throw new FileNotFoundException("Can't find texture: %s".formatted(id));
    }

    @Nullable
    public static Pair<NativeImage, NativeImage> matchWidth(NativeImage source, NativeImage mask, Identifier sourceId, Identifier maskId) {
        float scaleFactor = (float) source.getWidth() / mask.getWidth();
        if (!ImageUtils.isPowerOf2(scaleFactor)) {
            if (scaleFactor > 1) {
                ElytraTrimsMod.LOGGER.error("Unable to scale mask {} ({}px) to {} ({}px)", maskId, mask.getWidth(), sourceId, source.getWidth());
            } else {
                ElytraTrimsMod.LOGGER.error("Unable to scale source {} ({}px) to {} ({}px)", sourceId, source.getWidth(), maskId, mask.getWidth());
            }
            return null;
        }

        //FIXME: copying image sucks
        NativeImage localSource = scaleFactor < 1 ? ImageUtils.upscale(source, (int) (1.0F / scaleFactor)) : copy(source);
        NativeImage localMask = scaleFactor > 1 ? ImageUtils.upscale(mask, (int) scaleFactor) : copy(mask);
        return new Pair<>(localSource, localMask);
    }

    private static NativeImage copy(NativeImage source) {
        NativeImage copy = new NativeImage(source.getWidth(), source.getHeight(), true);
        copy.copyFrom(source);
        return copy;
    }

    public static void createSaturationMask(NativeImage image) {
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
        System.out.println(saturationDiff);

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int color = image.getColor(x, y);
                int red = (color >> 16 & 0xFF);
                int green = (color >> 8 & 0xFF);
                int blue = (color & 0xFF);
                int saturation = (Math.max(red, Math.max(green, blue)) + saturationDiff);
                image.setColor(x, y, color & 0xFF000000 | saturation << 16 | saturation << 8 | saturation);
            }
        }
    }

    public static void applyMaskClosing(NativeImage source, NativeImage mask) {
        int xmin = Math.min(source.getWidth(), mask.getWidth());
        int ymin = Math.min(source.getHeight(), mask.getHeight());

        for (int y = 0; y < ymin; y++) {
            for (int x = 0; x < xmin; x++) {
                int color = source.getColor(x, y);
                if ((color >> 24 & 0xFF) == 0) continue;
                int maskAlpha = mask.getColor(x, y) >> 24 & 0xFF;
                int sourceAlpha = color >> 24 & 0xFF;
                source.setColor(x, y, color & 0xFFFFFF | Math.min(maskAlpha, sourceAlpha) << 24);
            }
        }
        mask.close();
    }

    public static NativeImage offsetClosing(NativeImage source, int dx, int dy, int width, int height) {
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
        source.close();
        return offset;
    }

    public static NativeImage upscale(NativeImage source, int scale) {
        int width = source.getWidth() * scale;
        int height = source.getHeight() * scale;
        NativeImage upscaled = new NativeImage(width, height, true);
        for (int y = 0; y < height; y++) {
            int sy = y / scale;
            for (int x = 0; x < width; x++) {
                int sx = x / scale;
                upscaled.setColor(x, y, source.getColor(sx, sy));
            }
        }
        return upscaled;
    }
}

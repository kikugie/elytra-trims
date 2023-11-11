package dev.kikugie.elytratrims.config;

import net.minecraft.text.Text;

public class TextureConfig {
    public static final Text GROUP = Text.translatable("elytratrims.config.category.texture");
    public boolean useBannerTextures = false;
    public boolean cropTrims = true;
    public boolean useDarkerTrim = false;
    public boolean showBannerIcon = false;

    public static Boolean getField(TextureConfig config, String field) {
        try {
            return config.getClass().getDeclaredField(field).getBoolean(config);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setField(TextureConfig config, String field, boolean value) {
        try {
            config.getClass().getDeclaredField(field).setBoolean(config, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}

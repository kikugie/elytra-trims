package dev.kikugie.elytratrims.client.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class TextureConfig {
    public static final Codec<TextureConfig> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.BOOL.optionalFieldOf("useBannerTextures", false).forGetter(it -> it.useBannerTextures),
                    Codec.BOOL.optionalFieldOf("cropTrims", true).forGetter(it -> it.cropTrims),
                    Codec.BOOL.optionalFieldOf("useDarkerTrim", false).forGetter(it -> it.useDarkerTrim),
                    Codec.BOOL.optionalFieldOf("showBannerIcon", false).forGetter(it -> it.showBannerIcon)
            ).apply(instance, TextureConfig::new));
    public boolean useBannerTextures;
    public boolean cropTrims;
    public boolean useDarkerTrim;
    public boolean showBannerIcon;

    private TextureConfig(boolean useBannerTextures, boolean cropTrims, boolean useDarkerTrim, boolean showBannerIcon) {
        this.useBannerTextures = useBannerTextures;
        this.cropTrims = cropTrims;
        this.useDarkerTrim = useDarkerTrim;
        this.showBannerIcon = showBannerIcon;
    }

    static TextureConfig create() {
        return new TextureConfig(false, true, false, false);
    }
}
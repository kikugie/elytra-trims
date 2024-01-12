package dev.kikugie.elytratrims.client.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.kikugie.elytratrims.client.config.option.BooleanOption;
import dev.kikugie.elytratrims.client.config.option.Option;
import net.minecraft.text.Text;

public class TextureConfig {
    public static final Text GROUP = Text.translatable("elytratrims.config.category.texture");
    public static final Codec<TextureConfig> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.BOOL.fieldOf("useBannerTextures").forGetter(it -> it.useBannerTextures.get()),
                    Codec.BOOL.fieldOf("cropTrims").forGetter(it -> it.cropTrims.get()),
                    Codec.BOOL.fieldOf("useDarkerTrim").forGetter(it -> it.useDarkerTrim.get()),
                    Codec.BOOL.fieldOf("showBannerIcon").forGetter(it -> it.showBannerIcon.get())
            ).apply(instance, TextureConfig::new));
    public final BooleanOption useBannerTextures;
    public final BooleanOption cropTrims;
    public final BooleanOption useDarkerTrim;
    public final BooleanOption showBannerIcon;

    private TextureConfig(boolean useBannerTextures, boolean cropTrims, boolean useDarkerTrim, boolean showBannerIcon) {
        this.useBannerTextures = new BooleanOption("texture", "useBannerTextures", useBannerTextures);
        this.cropTrims = new BooleanOption("texture", "cropTrims", cropTrims);
        this.useDarkerTrim = new BooleanOption("texture", "useDarkerTrim", useDarkerTrim);
        this.showBannerIcon = new BooleanOption("texture", "showBannerIcon", showBannerIcon);
    }

    static TextureConfig create() {
        return new TextureConfig(false, true, false, false);
    }
}
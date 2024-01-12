package dev.kikugie.elytratrims.client.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.kikugie.elytratrims.common.config.ConfigLoader;
import dev.kikugie.elytratrims.common.plugin.ModStatus;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.io.IOException;
import java.nio.file.Path;

public class ETClientConfig {
    public static final Codec<ETClientConfig> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    RenderConfig.CODEC.fieldOf("render").forGetter(it -> it.render),
                    TextureConfig.CODEC.fieldOf("texture").forGetter(it -> it.texture)
            ).apply(instance, ETClientConfig::new));
    public static final Text TITLE = Text.translatable("elytratrims.config.title");
    public static final Text CATEGORY = Text.translatable("elytratrims.config.category");
    private static final Path CONFIG_FILE = ModStatus.configDir.resolve("elytra-trims.json");
    public final RenderConfig render;
    public final TextureConfig texture;

    private ETClientConfig(RenderConfig render, TextureConfig texture) {
        this.render = render;
        this.texture = texture;
    }

    public static ETClientConfig load() {
        try {
            return ConfigLoader.load(CONFIG_FILE, CODEC, ETClientConfig::create);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static ETClientConfig create() {
        return new ETClientConfig(RenderConfig.create(), TextureConfig.create());
    }

    public void save() {
        ConfigLoader.save(CONFIG_FILE, CODEC, this);
    }
}
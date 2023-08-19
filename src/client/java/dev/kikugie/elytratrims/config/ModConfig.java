package dev.kikugie.elytratrims.config;

import net.minecraft.text.Text;

public class ModConfig {
    public static final Text TITLE = Text.translatable("elytratrims.config.title");
    public static final Text CATEGORY = Text.translatable("elytratrims.config.category");

    public RenderConfig render;
    public TextureConfig texture;

    public ModConfig() {
        this.render = new RenderConfig();
        this.texture = new TextureConfig();
    }
}

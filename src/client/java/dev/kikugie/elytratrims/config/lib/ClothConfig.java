package dev.kikugie.elytratrims.config.lib;

import dev.kikugie.elytratrims.ElytraTrims;
import dev.kikugie.elytratrims.config.ConfigLoader;
import dev.kikugie.elytratrims.config.ModConfig;
import dev.kikugie.elytratrims.config.RenderConfig;
import dev.kikugie.elytratrims.config.TextureConfig;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ClothConfig {
    public static Screen createGui(Screen parent) {
        ModConfig config = ElytraTrims.getConfig();
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(ModConfig.TITLE);
        ConfigCategory render = builder.getOrCreateCategory(RenderConfig.GROUP);
        ConfigEntryBuilder renderEntryBuilder = builder.entryBuilder();
        for (RenderConfig.RenderType type : RenderConfig.RenderType.values()) {
            addOption(config.render, renderEntryBuilder, render, type);
        }

        ConfigCategory texture = builder.getOrCreateCategory(TextureConfig.GROUP);
        ConfigEntryBuilder textureEntryBuilder = builder.entryBuilder();
        texture.addEntry(textureEntryBuilder.startBooleanToggle(
                        Text.translatable("elytratrims.config.texture.useBannerTextures"),
                        config.texture.useBannerTextures)
                .setTooltip(Text.translatable("elytratrims.config.texture.useBannerTextures.tooltip"))
                .setDefaultValue(false)
                .setSaveConsumer(value -> config.texture.useBannerTextures = value)
                .requireRestart()
                .build());
        texture.addEntry(textureEntryBuilder.startBooleanToggle(
                        Text.translatable("elytratrims.config.texture.cropTrims"),
                        config.texture.cropTrims)
                .setTooltip(Text.translatable("elytratrims.config.texture.cropTrims.tooltip"))
                .setDefaultValue(false)
                .setSaveConsumer(value -> config.texture.cropTrims = value)
                .requireRestart()
                .build());
        texture.addEntry(textureEntryBuilder.startBooleanToggle(
                        Text.translatable("elytratrims.config.texture.useDarkerTrim"),
                        config.texture.useDarkerTrim)
                .setTooltip(Text.translatable("elytratrims.config.texture.useDarkerTrim.tooltip"))
                .setDefaultValue(false)
                .setSaveConsumer(value -> config.texture.useDarkerTrim = value)
                .build());
        texture.addEntry(textureEntryBuilder.startBooleanToggle(
                        Text.translatable("elytratrims.config.texture.showBannerIcon"),
                        config.texture.showBannerIcon)
                .setTooltip(Text.translatable("elytratrims.config.texture.showBannerIcon.tooltip"))
                .setDefaultValue(false)
                .setSaveConsumer(value -> config.texture.showBannerIcon = value)
                .build());

        builder.setSavingRunnable(() -> ConfigLoader.saveConfig(config));
        return builder.build();
    }

    private static void addOption(RenderConfig config, ConfigEntryBuilder builder, ConfigCategory category, RenderConfig.RenderType type) {
        category.addEntry(builder.startEnumSelector(type.getName(),
                        RenderConfig.RenderMode.class,
                        config.get(type))
                .setEnumNameProvider(mode -> ((RenderConfig.RenderMode) mode).getName())
                .setTooltip(type.getTooltip())
                .setDefaultValue(RenderConfig.RenderMode.ALL)
                .setSaveConsumer(mode -> config.set(type, mode))
                .build());
    }
}

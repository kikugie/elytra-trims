package dev.kikugie.elytratrims.config.lib;

import dev.kikugie.elytratrims.ElytraTrims;
import dev.kikugie.elytratrims.config.ConfigLoader;
import dev.kikugie.elytratrims.config.ModConfig;
import dev.kikugie.elytratrims.config.RenderConfig;
import dev.kikugie.elytratrims.config.TextureConfig;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.BooleanListEntry;
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
        texture.addEntry(booleanOption(textureEntryBuilder, "useBannerTextures", false, true));
        texture.addEntry(booleanOption(textureEntryBuilder, "cropTrims", true, true));
        texture.addEntry(booleanOption(textureEntryBuilder, "useDarkerTrim", false, false));
        texture.addEntry(booleanOption(textureEntryBuilder, "showBannerIcon", true, false));

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

    private static BooleanListEntry booleanOption(ConfigEntryBuilder builder, String field, boolean def, boolean reload) {
        TextureConfig config = ElytraTrims.getConfig().texture;
        var opt = builder.startBooleanToggle(
                        Text.translatable("elytratrims.config.texture.%s".formatted(field)),
                        TextureConfig.getField(config, field))
                .setTooltip(Text.translatable("elytratrims.config.texture.%s.tooltip".formatted(field)))
                .setDefaultValue(def)
                .setSaveConsumer(value -> TextureConfig.setField(config, field, value));
        if (reload) opt.requireRestart();
        return opt.build();
    }
}

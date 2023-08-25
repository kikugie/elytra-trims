package dev.kikugie.elytratrims.config.lib;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import dev.isxander.yacl3.gui.controllers.cycling.EnumController;
import dev.kikugie.elytratrims.ElytraTrims;
import dev.kikugie.elytratrims.config.ConfigLoader;
import dev.kikugie.elytratrims.config.ModConfig;
import dev.kikugie.elytratrims.config.RenderConfig;
import dev.kikugie.elytratrims.config.TextureConfig;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;

public class YaclConfig {
    public static Screen createGui(Screen parent) {
        ModConfig config = ElytraTrims.getConfig();
        return YetAnotherConfigLib.createBuilder()
                .title(ModConfig.TITLE)
                .category(ConfigCategory.createBuilder()
                        .name(ModConfig.CATEGORY)
                        .group(OptionGroup.createBuilder()
                                .name(RenderConfig.GROUP)
                                .options(renderOptions(config.render))
                                .build())
                        .group(OptionGroup.createBuilder()
                                .name(TextureConfig.GROUP)
                                .option(booleanOption("useBannerTextures", false, true))
                                .option(booleanOption("cropTrims", true, true))
                                .option(booleanOption("useDarkerTrim", false, false))
                                .option(booleanOption("showBannerIcon", true, false))
                                .build())
                        .build())
                .save(() -> ConfigLoader.saveConfig(config))
                .build()
                .generateScreen(parent);
    }

    private static Collection<Option<RenderConfig.RenderMode>> renderOptions(RenderConfig config) {
        ArrayList<Option<RenderConfig.RenderMode>> options = new ArrayList<>(RenderConfig.RenderType.values().length);
        for (RenderConfig.RenderType type : RenderConfig.RenderType.values()) {
            options.add(optionFor(config, type));
        }
        return options;
    }

    private static Option<RenderConfig.RenderMode> optionFor(RenderConfig config, RenderConfig.RenderType type) {
        return Option.<RenderConfig.RenderMode>createBuilder()
                .name(type.getName())
                .description(OptionDescription.of(type.getTooltip()))
                .binding(RenderConfig.RenderMode.ALL,
                        () -> config.get(type),
                        mode -> config.set(type, mode))
                .customController(opt -> new EnumController<>(opt, RenderConfig.RenderMode::getName, RenderConfig.RenderMode.values()))
                .build();
    }

    private static Option<Boolean> booleanOption(String field, boolean def, boolean reload) {
        TextureConfig config = ElytraTrims.getConfig().texture;
        var opt = Option.<Boolean>createBuilder()
                .name(Text.translatable("elytratrims.config.texture.%s".formatted(field)))
                .description(OptionDescription.of(Text.translatable("elytratrims.config.texture.%s.tooltip".formatted(field))))
                .binding(def,
                        () -> TextureConfig.getField(config, field),
                        value -> TextureConfig.setField(config, field, value))
                .controller(TickBoxControllerBuilder::create);
        if (reload) opt.flag(OptionFlag.ASSET_RELOAD);
        return opt.build();
    }
}

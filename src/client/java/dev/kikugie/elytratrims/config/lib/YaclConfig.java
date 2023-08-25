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
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.translatable("elytratrims.config.texture.useBannerTextures"))
                                        .description(OptionDescription.of(Text.translatable("elytratrims.config.texture.useBannerTextures.tooltip")))
                                        .binding(false,
                                                () -> config.texture.useBannerTextures,
                                                (value) -> config.texture.useBannerTextures = value)
                                        .controller(TickBoxControllerBuilder::create)
                                        .flag(OptionFlag.ASSET_RELOAD)
                                        .build())
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.translatable("elytratrims.config.texture.cropTrims"))
                                        .description(OptionDescription.of(Text.translatable("elytratrims.config.texture.cropTrims.tooltip")))
                                        .binding(true,
                                                () -> config.texture.cropTrims,
                                                (value) -> config.texture.cropTrims = value)
                                        .controller(TickBoxControllerBuilder::create)
                                        .flag(OptionFlag.ASSET_RELOAD)
                                        .build())
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.translatable("elytratrims.config.texture.useDarkerTrim"))
                                        .description(OptionDescription.of(Text.translatable("elytratrims.config.texture.useDarkerTrim.tooltip")))
                                        .binding(false,
                                                () -> config.texture.useDarkerTrim,
                                                (value) -> config.texture.useDarkerTrim = value)
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.translatable("elytratrims.config.texture.showBannerIcon"))
                                        .description(OptionDescription.of(Text.translatable("elytratrims.config.texture.showBannerIcon.tooltip")))
                                        .binding(false,
                                                () -> config.texture.showBannerIcon,
                                                (value) -> config.texture.showBannerIcon = value)
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
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
}

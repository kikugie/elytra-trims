package dev.kikugie.elytratrims.client.config.lib;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import dev.isxander.yacl3.gui.controllers.cycling.EnumController;
import dev.kikugie.elytratrims.client.ETClient;
import dev.kikugie.elytratrims.client.config.ETClientConfig;
import dev.kikugie.elytratrims.client.config.RenderConfig;
import dev.kikugie.elytratrims.client.config.RenderConfig.RenderMode;
import dev.kikugie.elytratrims.client.config.RenderConfig.RenderType;
import dev.kikugie.elytratrims.client.config.TextureConfig;
import dev.kikugie.elytratrims.client.config.option.BooleanOption;
import net.minecraft.client.gui.screen.Screen;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;

public class YaclConfig {
    public static Screen createGui(@Nullable Screen parent) {
        ETClientConfig config = ETClient.getConfig();
        return YetAnotherConfigLib.createBuilder()
                .title(ETClientConfig.TITLE)
                .category(ConfigCategory.createBuilder()
                        .name(ETClientConfig.CATEGORY)
                        .group(OptionGroup.createBuilder()
                                .name(RenderConfig.GROUP)
                                .options(renderOptions(config.render))
                                .build())
                        .group(OptionGroup.createBuilder()
                                .name(TextureConfig.GROUP)
                                .option(textureOption(config.texture.cropTrims, true))
                                .option(textureOption(config.texture.useDarkerTrim, true))
                                .option(textureOption(config.texture.useBannerTextures, true))
                                .option(textureOption(config.texture.showBannerIcon, false))
                                .build())
                        .build())
                .save(config::save)
                .build()
                .generateScreen(parent);
    }

    private static Collection<Option<RenderMode>> renderOptions(RenderConfig config) {
        ArrayList<Option<RenderMode>> options = new ArrayList<>(RenderType.values().length);
        for (RenderType type : RenderType.values()) {
            var opt = optionFor(config.get(type));
            var mode = config.get(type).get();
            opt.customController(it -> new EnumController<>(it, $ -> mode.getName(), RenderMode.values()));
            options.add(opt.build());
        }
        return options;
    }

    private static Option<Boolean> textureOption(BooleanOption option, boolean reload) {
        var opt = optionFor(option);
        opt.controller(TickBoxControllerBuilder::create);
        if (reload) opt.flag(OptionFlag.ASSET_RELOAD);
        return opt.build();
    }

    private static <T> Option.Builder<T> optionFor(dev.kikugie.elytratrims.client.config.option.Option<T> option) {
        return Option.<T>createBuilder()
                .name(option.name())
                .description(OptionDescription.of(option.desc()))
                .binding(option.def(), option::get, option::set);
    }
}
package me.kikugie.elytratrims.config;

import dev.isxander.yacl.api.ConfigCategory;
import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.api.OptionGroup;
import dev.isxander.yacl.api.YetAnotherConfigLib;
import dev.isxander.yacl.gui.controllers.cycling.EnumController;
import me.kikugie.elytratrims.ElytraTrimsMod;
import net.minecraft.client.gui.screen.Screen;

public class YaclConfig {
    public static Screen createGui(Screen parent) {
        return YetAnotherConfigLib.createBuilder()
                .title(ConfigState.title)
                .category(ConfigCategory.createBuilder()
                        .name(ConfigState.category)
                        .group(OptionGroup.createBuilder()
                                .name(ConfigState.renderGroup)
                                .option(optionFor(ConfigState.RenderType.COLOR))
                                .option(optionFor(ConfigState.RenderType.PATTERNS))
                                .option(optionFor(ConfigState.RenderType.TRIMS))
                                .option(optionFor(ConfigState.RenderType.CAPE))
                                .option(optionFor(ConfigState.RenderType.GLOW))
                                .option(optionFor(ConfigState.RenderType.GLOBAL))
                                .build())
                        .build())
                .save(ElytraTrimsMod.getConfigState()::save)
                .build()
                .generateScreen(parent);
    }

    private static Option<ConfigState.RenderMode> optionFor(ConfigState.RenderType type) {
        return Option.createBuilder(ConfigState.RenderMode.class)
                .name(type.getName())
                .tooltip(type.getTooltip())
                .binding(ConfigState.RenderMode.ALL,
                        () -> ElytraTrimsMod.getConfigState().getFor(type),
                        mode -> ElytraTrimsMod.getConfigState().setFor(type, mode))
                .controller(opt -> new EnumController<>(opt, ConfigState.RenderMode::getName))
                .build();
    }
}

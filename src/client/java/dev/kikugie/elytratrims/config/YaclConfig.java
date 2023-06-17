package dev.kikugie.elytratrims.config;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.gui.controllers.cycling.EnumController;
import dev.kikugie.elytratrims.ElytraTrimsMod;
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
        return Option.<ConfigState.RenderMode>createBuilder()
                .name(type.getName())
                .description(OptionDescription.createBuilder()
                        .text(type.getTooltip())
                        .build()
                )
                .binding(ConfigState.RenderMode.ALL,
                        () -> ElytraTrimsMod.getConfigState().getFor(type),
                        mode -> ElytraTrimsMod.getConfigState().setFor(type, mode))
                .customController(opt -> new EnumController<>(opt, ConfigState.RenderMode::getName, ConfigState.RenderMode.values()))
                .build();
    }
}

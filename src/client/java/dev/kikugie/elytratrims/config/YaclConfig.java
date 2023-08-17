package dev.kikugie.elytratrims.config;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.gui.controllers.cycling.EnumController;
import dev.kikugie.elytratrims.ElytraTrims;
import dev.kikugie.elytratrims.config.ConfigState.RenderMode;
import dev.kikugie.elytratrims.config.ConfigState.RenderType;
import net.minecraft.client.gui.screen.Screen;

import java.util.ArrayList;
import java.util.Collection;

public class YaclConfig {
    public static Screen createGui(Screen parent) {
        return YetAnotherConfigLib.createBuilder()
                .title(ConfigState.title)
                .category(ConfigCategory.createBuilder()
                        .name(ConfigState.category)
                        .group(OptionGroup.createBuilder()
                                .name(ConfigState.renderGroup)
                                .options(allOptions())
                                .build())
                        .build())
                .save(ElytraTrims.getConfigState()::save)
                .build()
                .generateScreen(parent);
    }

    private static Collection<Option<RenderMode>> allOptions() {
        ArrayList<Option<RenderMode>> options = new ArrayList<>(RenderType.values().length);
        for (RenderType type : RenderType.values()) {
            options.add(optionFor(type));
        }
        return options;
    }

    private static Option<RenderMode> optionFor(RenderType type) {
        return Option.<RenderMode>createBuilder()
                .name(type.getName())
                .description(OptionDescription.of(type.getTooltip()))
                .binding(RenderMode.ALL,
                        () -> ElytraTrims.getConfigState().getFor(type),
                        mode -> ElytraTrims.getConfigState().setFor(type, mode))
                .customController(opt -> new EnumController<>(opt, ConfigState.RenderMode::getName, ConfigState.RenderMode.values()))
                .build();
    }
}

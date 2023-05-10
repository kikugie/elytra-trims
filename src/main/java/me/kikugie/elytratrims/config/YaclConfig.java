package me.kikugie.elytratrims.config;

import dev.isxander.yacl.api.ConfigCategory;
import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.api.OptionGroup;
import dev.isxander.yacl.api.YetAnotherConfigLib;
import dev.isxander.yacl.gui.controllers.cycling.EnumController;
import me.kikugie.elytratrims.ElytraTrimsMod;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class YaclConfig {
    public static Screen createGui(Screen parent) {
        return YetAnotherConfigLib.createBuilder()
                .title(Text.of("Elytra Trims"))
                .category(ConfigCategory.createBuilder()
                        .name(Text.of("Elytra Trims Config"))
                        .group(OptionGroup.createBuilder()
                                .name(Text.of("Render"))
                                .option(optionFor(ConfigState.RenderType.COLOR))
                                .option(optionFor(ConfigState.RenderType.PATTERNS))
                                .option(optionFor(ConfigState.RenderType.TRIMS))
                                .option(optionFor(ConfigState.RenderType.CAPE))
                                .option(optionFor(ConfigState.RenderType.GLOBAL))
                                .build())
                        .build())
                .save(ElytraTrimsMod.getConfigState()::save)
                .build()
                .generateScreen(parent);
    }

    private static Option<ConfigState.RenderMode> optionFor(ConfigState.RenderType type) {
        return Option.createBuilder(ConfigState.RenderMode.class)
                .name(Text.of(type.getType()))
                .binding(ConfigState.RenderMode.ALL,
                        () -> ElytraTrimsMod.getConfigState().getFor(type),
                        mode -> ElytraTrimsMod.getConfigState().setFor(type, mode))
                .controller(EnumController::new)
                .build();
    }
}

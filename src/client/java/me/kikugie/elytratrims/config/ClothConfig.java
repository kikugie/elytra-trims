package me.kikugie.elytratrims.config;

import me.kikugie.elytratrims.ElytraTrimsMod;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;

public class ClothConfig {
    public static Screen createGui(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(ConfigState.title);
        ConfigCategory render = builder.getOrCreateCategory(ConfigState.renderGroup);
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        addOption(entryBuilder, render, ConfigState.RenderType.COLOR);
        addOption(entryBuilder, render, ConfigState.RenderType.CAPE);
        addOption(entryBuilder, render, ConfigState.RenderType.GLOW);
        addOption(entryBuilder, render, ConfigState.RenderType.PATTERNS);
        addOption(entryBuilder, render, ConfigState.RenderType.TRIMS);
        addOption(entryBuilder, render, ConfigState.RenderType.GLOBAL);
        builder.setSavingRunnable(ElytraTrimsMod.getConfigState()::save);
        return builder.build();
    }

    private static void addOption(ConfigEntryBuilder builder, ConfigCategory category, ConfigState.RenderType type) {
        category.addEntry(builder.startEnumSelector(type.getName(),
                        ConfigState.RenderMode.class,
                        ElytraTrimsMod.getConfigState().getFor(type))
                .setEnumNameProvider(mode -> ((ConfigState.RenderMode) mode).getName())
                .setTooltip(type.getTooltip())
                .setDefaultValue(ConfigState.RenderMode.ALL)
                .setSaveConsumer(mode -> ElytraTrimsMod.getConfigState().setFor(type, mode))
                .build());
    }
}

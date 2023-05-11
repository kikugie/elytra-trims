package me.kikugie.elytratrims.config;

import me.kikugie.elytratrims.ElytraTrimsMod;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ClothConfig {
    public static Screen createGui(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.of("Elytra Trims Config"));
        ConfigCategory render = builder.getOrCreateCategory(Text.of("Render"));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        addOption(entryBuilder, render, ConfigState.RenderType.COLOR);
        addOption(entryBuilder, render, ConfigState.RenderType.CAPE);
        addOption(entryBuilder, render, ConfigState.RenderType.PATTERNS);
        addOption(entryBuilder, render, ConfigState.RenderType.TRIMS);
        addOption(entryBuilder, render, ConfigState.RenderType.GLOBAL);
        builder.setSavingRunnable(ElytraTrimsMod.getConfigState()::save);
        return builder.build();
    }

    private static void addOption(ConfigEntryBuilder builder, ConfigCategory category, ConfigState.RenderType type) {
        category.addEntry(builder.startEnumSelector(Text.of(type.getType()),
                        ConfigState.RenderMode.class,
                        ElytraTrimsMod.getConfigState().getFor(type))
                .setDefaultValue(ConfigState.RenderMode.ALL)
                .setSaveConsumer(mode -> ElytraTrimsMod.getConfigState().setFor(type, mode))
                .build());
    }
}

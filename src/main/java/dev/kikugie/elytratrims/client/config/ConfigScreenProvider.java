package dev.kikugie.elytratrims.client.config;

import dev.kikugie.elytratrims.client.config.lib.YaclConfig;
import dev.kikugie.elytratrims.common.plugin.ModStatus;
import net.minecraft.client.gui.screen.Screen;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class ConfigScreenProvider {
    public static @Nullable Function<Screen, Screen> getScreen() {
        if (ModStatus.isLoaded("yet_another_config_lib_v3"))
            return YaclConfig::createGui;
        else return null;
    }
}
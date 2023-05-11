package me.kikugie.elytratrims.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.loader.api.FabricLoader;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        if (FabricLoader.getInstance().isModLoaded("yet-another-config-lib")) {
            return YaclConfig::createGui;
        } else if (FabricLoader.getInstance().isModLoaded("cloth-config")) {
            return ClothConfig::createGui;
        }
        return null;
    }
}

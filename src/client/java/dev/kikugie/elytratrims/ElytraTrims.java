package dev.kikugie.elytratrims;

import dev.kikugie.elytratrims.config.CommandConfig;
import dev.kikugie.elytratrims.config.ConfigState;
import dev.kikugie.elytratrims.render.ExtraElytraFeatureRenderer;
import dev.kikugie.elytratrims.resource.ETAtlasHolder;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ElytraTrims implements ClientModInitializer {
    public static final String MOD_ID = "elytratrims";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static ExtraElytraFeatureRenderer ELYTRA_RENDERER;
    private static ConfigState configState;

    public static boolean stackedTrimsLoaded = false;

    public static ConfigState getConfigState() {
        return configState;
    }

    private void loadConfig() {
        configState = ConfigState.load();
    }

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }

    @Override
    public void onInitializeClient() {
        LOGGER.info("Making elytras fancier!");
        loadConfig();

        FabricLoader fabric = FabricLoader.getInstance();
        stackedTrimsLoaded = fabric.isModLoaded("stacked_trims");
        if (fabric.isModLoaded("command-config"))
            CommandConfig.register();
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(ETAtlasHolder.create());
    }
}

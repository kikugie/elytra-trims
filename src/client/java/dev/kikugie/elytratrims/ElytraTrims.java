package dev.kikugie.elytratrims;

import dev.kikugie.elytratrims.config.ConfigLoader;
import dev.kikugie.elytratrims.config.ModConfig;
import dev.kikugie.elytratrims.config.lib.CommandConfig;
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
    public static boolean stackedTrimsLoaded = false;
    public static boolean elytraTrimmingAvailable = false;
    private static ModConfig config;

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }

    public static ModConfig getConfig() {
        return config;
    }

    @Override
    public void onInitializeClient() {
        LOGGER.info("Making elytras fancier!");
        config = ConfigLoader.loadConfig();

        FabricLoader fabric = FabricLoader.getInstance();
        stackedTrimsLoaded = fabric.isModLoaded("stacked_trims");
        if (fabric.isModLoaded("command-config"))
            CommandConfig.register();
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(ETAtlasHolder.create());
    }
}

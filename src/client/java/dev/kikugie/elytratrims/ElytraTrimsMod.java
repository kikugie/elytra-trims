package dev.kikugie.elytratrims;

import dev.kikugie.elytratrims.config.CommandConfig;
import dev.kikugie.elytratrims.config.ConfigState;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.texture.atlas.AtlasSourceType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ElytraTrimsMod implements ClientModInitializer {
    public static final String MOD_ID = "elytratrims";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static AtlasSourceType ELYTRA_TRIMS;
    public static AtlasSourceType ELYTRA_PATTERNS;
    public static AtlasSourceType ELYTRA_OVERLAY;
    private static ConfigState configState;

    public static boolean stackedTrimsLoaded = false;

    public static ConfigState getConfigState() {
        return configState;
    }

    private void loadConfig() {
        configState = ConfigState.load();
    }

    @Override
    public void onInitializeClient() {
        LOGGER.info("Making elytras fancier!");
        loadConfig();

        FabricLoader fabric = FabricLoader.getInstance();
        stackedTrimsLoaded = fabric.isModLoaded("stacked_trims");
        fabric.getModContainer(MOD_ID).ifPresent(container -> {
            LOGGER.info("Registering Elytra Trims resourcepack");
            ResourceManagerHelper.registerBuiltinResourcePack(
                    new Identifier(MOD_ID, "default"),
                    container,
                    Text.literal("Elytra Trims Defaults"),
                    configState.misc.lockDefaultPack.value ? ResourcePackActivationType.ALWAYS_ENABLED : ResourcePackActivationType.DEFAULT_ENABLED
            );
        });
        if (FabricLoader.getInstance().isModLoaded("command-config"))
            CommandConfig.register();
    }
}

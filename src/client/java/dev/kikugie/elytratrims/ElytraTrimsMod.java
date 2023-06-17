package dev.kikugie.elytratrims;

import dev.kikugie.elytratrims.config.ConfigCommand;
import dev.kikugie.elytratrims.config.ConfigState;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
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

    public static ConfigState getConfigState() {
        return configState;
    }

    private void loadConfig() {
        configState = ConfigState.load();
    }

    @Override
    public void onInitializeClient() {
        LOGGER.info("Making elytras fancier!");

        FabricLoader.getInstance().getModContainer(MOD_ID).ifPresent(container -> {
                    LOGGER.info("Registering resourcepack");
                    ResourceManagerHelper.registerBuiltinResourcePack(
                            new Identifier(MOD_ID, "default"),
                            container,
                            Text.literal("Elytra Trims Defaults"),
                            ResourcePackActivationType.ALWAYS_ENABLED
                    );
                });
        loadConfig();
        ClientCommandRegistrationCallback.EVENT.register(ConfigCommand::register);
    }
}

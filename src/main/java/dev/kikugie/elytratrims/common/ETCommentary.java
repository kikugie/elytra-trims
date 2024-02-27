package dev.kikugie.elytratrims.common;

import dev.kikugie.elytratrims.common.plugin.ModStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.BiConsumer;

public class ETCommentary {
    static void run() {
        Logger logger = LoggerFactory.getLogger("Elytra Trims commentary");
        BiConsumer<String, String> pattern = (mod, text) -> {
            if (ModStatus.isLoaded(mod)) logger.info(text);
        };

        pattern.accept("dashloader", "DashLoader, what do you mean all textures already exist? This is not how we do things here!");
        pattern.accept("customizableelytra", "Ah yes, the *inferior* CustomizableElytra mod.");
        pattern.accept("betterend", "IHATEBCLIB IHATEBCLIB IHATEBCLIB IHATEBCLIB IHATEBCLIB IHATEBCLIB IHATEBCLIB IHATEBCLIB");
        pattern.accept("minecraftcapes", "MinecraftCapes, would you like me to teach you how to write mixins? For free, even.");
        pattern.accept("optifine", "*Metal pipe sound effect*"); // Forge moment
        pattern.accept("optifabric", "*Metal pipe sound effect*"); // In case dependency overrides are applied
    }
}
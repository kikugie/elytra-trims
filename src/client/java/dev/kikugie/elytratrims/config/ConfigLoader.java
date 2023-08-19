package dev.kikugie.elytratrims.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import dev.kikugie.elytratrims.util.LogWrapper;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigLoader {
    public static final Path CONFIG_FILE = FabricLoader.getInstance().getConfigDir().resolve("elytra_trims.json");
    private static final LogWrapper LOGGER = LogWrapper.of(ConfigLoader.class);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void saveConfig(ModConfig config) {
        JsonElement json = GSON.toJsonTree(config);
        FileStatus status = getStatus(CONFIG_FILE);
        if (status == FileStatus.NOT_FOUND) {
            if (!createFile())
                return;
        } else if (status != FileStatus.OK) {
            LOGGER.error("Failed to save config file: " + status.message);
            return;
        }
        try {
            Files.writeString(CONFIG_FILE, GSON.toJson(json));
        } catch (IOException e) {
            LOGGER.error("Failed to save config file", e);
        }
    }

    public static ModConfig loadConfig() {
        FileStatus status = getStatus(CONFIG_FILE);
        if (status == FileStatus.NOT_FOUND) {
            if (!createFile())
                return new ModConfig();
        } else if (status != FileStatus.OK) {
            LOGGER.error("Failed to load config file: " + status.message);
            return new ModConfig();
        }
        try {
            String json = Files.readString(CONFIG_FILE);
            return GSON.fromJson(json, ModConfig.class);
        } catch (IOException e) {
            LOGGER.error("Failed to load config file", e);
            return new ModConfig();
        }
    }

    private static boolean createFile() {
        try {
            Files.createFile(CONFIG_FILE);
            return true;
        } catch (IOException e) {
            LOGGER.error("Failed to create config file", e);
            return false;
        }
    }

    private static FileStatus getStatus(Path path) {
        if (!Files.exists(path))
            return FileStatus.NOT_FOUND;
        else if (!Files.isReadable(path))
            return FileStatus.UNREADABLE;
        else if (!Files.isWritable(path))
            return FileStatus.UNWRITABLE;
        else
            return FileStatus.OK;
    }

    private enum FileStatus {
        NOT_FOUND("Config file not found"),
        UNREADABLE("Config file is unreadable"),
        UNWRITABLE("Config file is unwritable"),
        OK("👍");

        public final String message;

        FileStatus(String message) {
            this.message = message;
        }
    }
}

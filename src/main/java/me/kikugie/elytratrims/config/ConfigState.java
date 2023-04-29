package me.kikugie.elytratrims.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.kikugie.elytratrims.ElytraTrimsMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.StringIdentifiable;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;

public class ConfigState {
    public static final File CONFIG_FILE = new File(MinecraftClient.getInstance().runDirectory, "config/elytra_trims.json");
    public static final Codec<ConfigState> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("color_mode").forGetter(state -> state.color.mode),
            Codec.STRING.fieldOf("patterns_mode").forGetter(state -> state.patterns.mode),
            Codec.STRING.fieldOf("trims_mode").forGetter(state -> state.trims.mode),
            Codec.STRING.fieldOf("global_mode").forGetter(state -> state.global.mode)
    ).apply(instance, ConfigState::new));
    public RenderMode color;
    public RenderMode patterns;
    public RenderMode trims;
    public RenderMode global;

    private ConfigState(String colorMode, String patternsMode, String trimsMode, String globalMode) {
        this.color = RenderMode.valueOf(colorMode.toUpperCase());
        this.patterns = RenderMode.valueOf(patternsMode.toUpperCase());
        this.trims = RenderMode.valueOf(trimsMode.toUpperCase());
        this.global = RenderMode.valueOf(globalMode.toUpperCase());
    }

    public static ConfigState load() {
        if (CONFIG_FILE.exists()) {
            try {
                String jsonString = FileUtils.readFileToString(CONFIG_FILE, StandardCharsets.UTF_8);
                JsonElement json = JsonParser.parseString(jsonString);
                return CODEC.decode(JsonOps.INSTANCE, json)
                        .resultOrPartial(s -> ElytraTrimsMod.LOGGER.error("Error reading config data!\n{}", s))
                        .orElseThrow().getFirst();
            } catch (IOException e) {
                ElytraTrimsMod.LOGGER.error("Error reading config file!\n", e);
            } catch (NoSuchElementException ignored) {
            }
        }

        ConfigState state = new ConfigState("all", "all", "all", "all");
        try {
            CONFIG_FILE.createNewFile();
            state.save();
        } catch (IOException e) {
            ElytraTrimsMod.LOGGER.error("Couldn't create config file!\n", e);
        }
        return state;
    }

    public void reset() {
        color = RenderMode.ALL;
        patterns = RenderMode.ALL;
        trims = RenderMode.ALL;
        global = RenderMode.ALL;
    }

    public RenderMode getFor(RenderType type) {
        return switch (type) {
            case COLOR -> color;
            case PATTERNS -> patterns;
            case TRIMS -> trims;
            case GLOBAL -> global;
        };
    }

    public RenderMode getConfigFor(RenderType type) {
        RenderMode mode = switch (type) {
            case COLOR -> color;
            case PATTERNS -> patterns;
            case TRIMS -> trims;
            case GLOBAL -> global;
        };
        return mode.weight < global.weight ? mode : global;
    }

    public void setFor(RenderType type, RenderMode mode) {
        switch (type) {
            case COLOR -> color = mode;
            case PATTERNS -> patterns = mode;
            case TRIMS -> trims = mode;
            case GLOBAL -> global = mode;
        }
    }

    public void save() {
        try {
            DataResult<JsonElement> result = CODEC.encodeStart(JsonOps.INSTANCE, this);
            String jsonString = result.resultOrPartial(s -> ElytraTrimsMod.LOGGER.error("Error saving config data! How odd...\n{}", s)).orElseThrow().toString();
            FileUtils.write(CONFIG_FILE, jsonString, StandardCharsets.UTF_8);
        } catch (IOException e) {
            ElytraTrimsMod.LOGGER.error("Error writing config file!\n", e);
        } catch (NoSuchElementException ignored) {
        }
    }

    public enum RenderType {
        COLOR("color"),
        PATTERNS("patterns"),
        TRIMS("trims"),
        GLOBAL("global");

        public final String type;

        RenderType(String type) {
            this.type = type;
        }
    }

    public enum RenderMode implements StringIdentifiable {
        NONE("NONE", 0),
        SELF("SELF", 1),
        ALL("ALL", 2);
        public final String mode;
        public final int weight;

        RenderMode(String mode, int weight) {
            this.mode = mode;
            this.weight = weight;
        }

        @Override
        public String asString() {
            return mode;
        }
    }
}

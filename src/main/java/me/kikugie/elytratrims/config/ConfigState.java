package me.kikugie.elytratrims.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.kikugie.elytratrims.ElytraTrimsMod;
import me.kikugie.elytratrims.render.ExtraElytraFeatureRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;
import net.minecraft.util.StringIdentifiable;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;

public class ConfigState {
    public static final File CONFIG_FILE = new File(MinecraftClient.getInstance().runDirectory, "config/elytra_trims.json");
    public static final Codec<ConfigState> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            RenderMode.getCodec(RenderType.COLOR).forGetter(state -> state.color),
            RenderMode.getCodec(RenderType.PATTERNS).forGetter(state -> state.patterns),
            RenderMode.getCodec(RenderType.TRIMS).forGetter(state -> state.trims),
            RenderMode.getCodec(RenderType.CAPE).forGetter(state -> state.cape),
            RenderMode.getCodec(RenderType.GLOBAL).forGetter(state -> state.global)
    ).apply(instance, ConfigState::new));

    public static final Text title = Text.translatable("elytratrims.config.title");
    public static final Text category = Text.translatable("elytratrims.config.category");
    public static final Text renderGroup = Text.translatable("elytratrims.config.category.render");
    private RenderMode color;
    private RenderMode patterns;
    private RenderMode trims;
    private RenderMode cape;
    private RenderMode global;

    private ConfigState(RenderMode colorMode, RenderMode patternsMode, RenderMode trimsMode, RenderMode capeMode, RenderMode globalMode) {
        this.color = colorMode;
        this.patterns = patternsMode;
        this.trims = trimsMode;
        this.cape = capeMode;
        this.global = globalMode;
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

        ConfigState state = new ConfigState(RenderMode.ALL, RenderMode.ALL, RenderMode.ALL, RenderMode.ALL, RenderMode.ALL);
        try {
            CONFIG_FILE.createNewFile();
            state.save();
        } catch (IOException e) {
            ElytraTrimsMod.LOGGER.error("Couldn't create config file!\n", e);
        }
        return state;
    }

    public static boolean cancelRender(RenderType type, LivingEntity entity) {
        RenderMode mode = ElytraTrimsMod.getConfigState().getConfigFor(type);
        return switch (mode) {
            case ALL -> false;
            case NONE -> true;
            case SELF ->
                    entity != MinecraftClient.getInstance().player && ExtraElytraFeatureRenderer.skipRenderIfMissingTexture(entity);
            case OTHERS ->
                    entity == MinecraftClient.getInstance().player || ExtraElytraFeatureRenderer.skipRenderIfMissingTexture(entity);
        };
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

    public void reset() {
        color = RenderMode.ALL;
        patterns = RenderMode.ALL;
        trims = RenderMode.ALL;
        cape = RenderMode.ALL;
        global = RenderMode.ALL;
    }

    public RenderMode getFor(RenderType type) {
        return switch (type) {
            case COLOR -> color;
            case PATTERNS -> patterns;
            case TRIMS -> trims;
            case CAPE -> cape;
            case GLOBAL -> global;
        };
    }

    public RenderMode getConfigFor(RenderType type) {
        RenderMode mode = switch (type) {
            case COLOR -> color;
            case PATTERNS -> patterns;
            case TRIMS -> trims;
            case CAPE -> cape;
            case GLOBAL -> global;
        };
        return mode.weight < global.weight ? mode : global;
    }

    public void setFor(RenderType type, RenderMode mode) {
        switch (type) {
            case COLOR -> color = mode;
            case PATTERNS -> patterns = mode;
            case TRIMS -> trims = mode;
            case CAPE -> cape = mode;
            case GLOBAL -> global = mode;
        }
    }

    public enum RenderType {
        COLOR("color"),
        PATTERNS("patterns"),
        TRIMS("trims"),
        CAPE("cape"),
        GLOBAL("global");

        public final String type;
        private final String translation;

        RenderType(String type) {
            this.type = type;
            this.translation = "elytratrims.config.type." + type;
        }

        public Text getName() {
            return Text.translatable(translation);
        }

        public Text getTooltip() {
            return Text.translatable(translation + ".tooltip");
        }
    }

    public enum RenderMode implements StringIdentifiable {
        NONE("none", 0),
        SELF("self", 1),
        OTHERS("others", 1),
        ALL("all", 2);
        public static final com.mojang.serialization.Codec<RenderMode> codec = com.mojang.serialization.Codec.STRING.xmap(
                string -> RenderMode.valueOf(string.toUpperCase()),
                mode -> mode.mode);
        public final String mode;
        public final int weight;
        private final String translation;

        RenderMode(String mode, int weight) {
            this.mode = mode;
            this.weight = weight;
            this.translation = "elytratrims.config.mode." + mode;
        }

        public static MapCodec<RenderMode> getCodec(RenderType type) {
            return codec.fieldOf(type.type + "_mode");
        }

        public Text getName() {
            return Text.translatable(translation);
        }

//        public Text getTooltip() {
//            return Text.translatable(translation + ".tooltip");
//        }

        @Override
        public String asString() {
            return mode;
        }
    }
}

package dev.kikugie.elytratrims.client.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.text.Text;
import net.minecraft.util.StringIdentifiable;

public class RenderConfig {
    public static final Codec<RenderConfig> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    RenderMode.CODEC.optionalFieldOf("color", RenderMode.ALL).forGetter(it -> it.color),
                    RenderMode.CODEC.optionalFieldOf("patterns", RenderMode.ALL).forGetter(it -> it.patterns),
                    RenderMode.CODEC.optionalFieldOf("trims", RenderMode.ALL).forGetter(it -> it.trims),
                    RenderMode.CODEC.optionalFieldOf("cape", RenderMode.ALL).forGetter(it -> it.cape),
                    RenderMode.CODEC.optionalFieldOf("glow", RenderMode.ALL).forGetter(it -> it.glow),
                    RenderMode.CODEC.optionalFieldOf("global", RenderMode.ALL).forGetter(it -> it.global)
            ).apply(instance, RenderConfig::new));
    private RenderMode color;
    private RenderMode patterns;
    private RenderMode trims;
    private RenderMode cape;
    private RenderMode glow;
    private RenderMode global;

    private RenderConfig(RenderMode color, RenderMode patterns, RenderMode trims, RenderMode cape, RenderMode glow, RenderMode global) {
        this.color = color;
        this.patterns = patterns;
        this.trims = trims;

        this.cape = cape;
        this.glow = glow;
        this.global = global;
    }

    static RenderConfig create() {
        return new RenderConfig(RenderMode.ALL, RenderMode.ALL, RenderMode.ALL, RenderMode.ALL, RenderMode.ALL, RenderMode.ALL);
    }

    public RenderMode get(RenderType type) {
        return switch (type) {
            case COLOR -> color;
            case PATTERNS -> patterns;
            case TRIMS -> trims;
            case CAPE -> cape;
            case GLOW -> glow;
            case GLOBAL -> global;
        };
    }

    public RenderMode getEffective(RenderType type) {
        RenderMode mode = get(type);
        return mode.weight < global.weight ? mode : global;
    }

    public void set(RenderType type, RenderMode mode) {
        switch (type) {
            case COLOR -> color = mode;
            case PATTERNS -> patterns = mode;
            case TRIMS -> trims = mode;
            case CAPE -> cape = mode;
            case GLOW -> glow = mode;
            case GLOBAL -> global = mode;
        }
    }

    public enum RenderType implements StringIdentifiable {
        COLOR,
        PATTERNS,
        TRIMS,
        CAPE,
        GLOW,
        GLOBAL;
        private final String translation;

        RenderType() {
            this.translation = "elytratrims.config.type." + asString();
        }

        public Text getName() {
            return Text.translatable(translation);
        }

        public Text getTooltip() {
            return Text.translatable(translation + ".tooltip");
        }

        @Override
        public String asString() {
            return name().toLowerCase();
        }
    }

    public enum RenderMode implements StringIdentifiable {
        NONE(0),
        SELF(1),
        OTHERS(1),
        ALL(2);
        public static final com.mojang.serialization.Codec<RenderMode> CODEC = StringIdentifiable.createCodec(RenderMode::values);
        public final int weight;
        private final String translation;

        RenderMode(int weight) {
            this.weight = weight;
            this.translation = "elytratrims.config.mode." + asString();
        }

        public Text getName() {
            return Text.translatable(translation);
        }

        @Override
        public String asString() {
            return name().toLowerCase();
        }
    }
}
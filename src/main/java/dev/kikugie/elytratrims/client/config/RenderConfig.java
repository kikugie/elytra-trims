package dev.kikugie.elytratrims.client.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.kikugie.elytratrims.client.config.option.Option;
import dev.kikugie.elytratrims.client.config.option.RenderModeOption;
import net.minecraft.text.Text;
import net.minecraft.util.StringIdentifiable;

public class RenderConfig {
    public static final Text GROUP = Text.translatable("elytratrims.config.category.render");
    public static final Codec<RenderConfig> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    RenderMode.CODEC.fieldOf("color").forGetter(it -> it.color.get()),
                    RenderMode.CODEC.fieldOf("patterns").forGetter(it -> it.patterns.get()),
                    RenderMode.CODEC.fieldOf("trims").forGetter(it -> it.trims.get()),
                    RenderMode.CODEC.fieldOf("cape").forGetter(it -> it.cape.get()),
                    RenderMode.CODEC.fieldOf("glow").forGetter(it -> it.glow.get()),
                    RenderMode.CODEC.fieldOf("global").forGetter(it -> it.global.get())
            ).apply(instance, RenderConfig::new));
    private final RenderModeOption color;
    private final RenderModeOption patterns;
    private final RenderModeOption trims;
    private final RenderModeOption cape;
    private final RenderModeOption glow;
    private final RenderModeOption global;

    private RenderConfig(RenderMode color, RenderMode patterns, RenderMode trims, RenderMode cape, RenderMode glow, RenderMode global) {
        this.color = new RenderModeOption("type", "color", color);
        this.patterns = new RenderModeOption("type", "patterns", patterns);
        this.trims = new RenderModeOption("type", "trims", trims);
        this.cape = new RenderModeOption("type", "cape", cape);
        this.glow = new RenderModeOption("type", "glow", glow);
        this.global = new RenderModeOption("type", "global", global);
    }

    static RenderConfig create() {
        return new RenderConfig(RenderMode.ALL, RenderMode.ALL, RenderMode.ALL, RenderMode.ALL, RenderMode.ALL, RenderMode.ALL);
    }

    public RenderModeOption get(RenderType type) {
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
        RenderMode mode = get(type).get();
        return mode.weight < global.get().weight ? mode : global.get();
    }

    public void set(RenderType type, RenderMode mode) {
        switch (type) {
            case COLOR -> color.set(mode);
            case PATTERNS -> patterns.set(mode);
            case TRIMS -> trims.set(mode);
            case CAPE -> cape.set(mode);
            case GLOW -> glow.set(mode);
            case GLOBAL -> global.set(mode);
        }
    }

    public enum RenderType implements StringIdentifiable {
        COLOR,
        PATTERNS,
        TRIMS,
        CAPE,
        GLOW,
        GLOBAL;

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
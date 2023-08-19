package dev.kikugie.elytratrims.config;

import net.minecraft.text.Text;
import net.minecraft.util.StringIdentifiable;

public class RenderConfig {
    public static final Text GROUP = Text.translatable("elytratrims.config.category.render");
    private RenderMode color = RenderMode.ALL;
    private RenderMode patterns = RenderMode.ALL;
    private RenderMode trims = RenderMode.ALL;
    private RenderMode cape = RenderMode.ALL;
    private RenderMode glow = RenderMode.ALL;
    private RenderMode global = RenderMode.ALL;

    public RenderMode get(RenderType type) {
        return switch (type) {
            case COLOR -> this.color;
            case PATTERNS -> this.patterns;
            case TRIMS -> this.trims;
            case CAPE -> this.cape;
            case GLOW -> this.glow;
            case GLOBAL -> this.global;
        };
    }

    public RenderMode getEffective(RenderType type) {
        RenderMode mode = get(type);
        return mode.weight < this.global.weight ? mode : this.global;
    }

    public void set(RenderType type, RenderMode mode) {
        switch (type) {
            case COLOR -> this.color = mode;
            case PATTERNS -> this.patterns = mode;
            case TRIMS -> this.trims = mode;
            case CAPE -> this.cape = mode;
            case GLOW -> this.glow = mode;
            case GLOBAL -> this.global = mode;
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
            return Text.translatable(this.translation);
        }

        public Text getTooltip() {
            return Text.translatable(this.translation + ".tooltip");
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
        public final int weight;
        private final String translation;

        RenderMode(int weight) {
            this.weight = weight;
            this.translation = "elytratrims.config.mode." + asString();
        }

        public Text getName() {
            return Text.translatable(this.translation);
        }

        @Override
        public String asString() {
            return name().toLowerCase();
        }
    }
}

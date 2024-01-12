package dev.kikugie.elytratrims.client.config.option;

import dev.kikugie.elytratrims.client.config.RenderConfig.RenderMode;
import net.minecraft.text.Text;

public class RenderModeOption implements Option<RenderMode> {
    private final String type;
    private final String id;
    private final RenderMode def;
    private RenderMode value;

    public RenderModeOption(String type, String id, RenderMode def) {
        this.type = type;
        this.id = id;
        this.def = def;
        this.value = def;
    }

    @Override
    public RenderMode def() {
        return def;
    }

    @Override
    public RenderMode get() {
        return value;
    }

    @Override
    public void set(RenderMode value) {
        this.value = value;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public Text name() {
        return Text.translatable("elytratrims.config.%s.%s".formatted(type, id));
    }

    @Override
    public Text desc() {
        return Text.translatable("elytratrims.config.%s.%s.tooltip".formatted(type, id));
    }
}
package dev.kikugie.elytratrims.client.config.option;

import net.minecraft.text.Text;

public class BooleanOption implements Option<Boolean> {
    private final String type;
    private final String id;
    private final boolean def;
    private boolean value;
    public BooleanOption(String type, String id, boolean def) {
        this.type = type;
        this.id = id;
        this.def = def;
        this.value = def;
    }

    @Override
    public Boolean def() {
        return def;
    }

    @Override
    public Boolean get() {
        return value;
    }

    @Override
    public void set(Boolean value) {
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
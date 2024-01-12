package dev.kikugie.elytratrims.client.config.option;

import net.minecraft.text.Text;

public interface Option<T> {
    T def();
    T get();
    void set(T value);
    String id();
    Text name();
    Text desc();
}

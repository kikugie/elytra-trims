package dev.kikugie.elytratrims.common;

import dev.kikugie.elytratrims.common.config.ETServerConfig;
import dev.kikugie.elytratrims.common.recipe.GlowingItem;
import net.minecraft.item.DyeableItem;

public class ETServer {
    public static final DyeableItem DYEABLE = new DyeableItem() {
    };
    public static final GlowingItem GLOWING = new GlowingItem() {
    };
    private static ETServerConfig config = new ETServerConfig(true, true, true, true);

    public static void configInit() {
        if (config == null) config = ETServerConfig.load();
    }

    public static void init() {
    }

    public static ETServerConfig getConfig() {
        return config;
    }
}
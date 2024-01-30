package dev.kikugie.elytratrims.common.config;

import dev.kikugie.elytratrims.common.plugin.ModStatus;

public class ServerConfigs {
    private static ETServerConfig config;
    private static ETMixinConfig mixinConfig;

    public static void init() {
        if (config == null) config = ModStatus.isServer || ModStatus.isDev
                ? ETServerConfig.load()
                : ETServerConfig.create();
        if (mixinConfig == null)
            mixinConfig = ETMixinConfig.load();
    }

    public static ETServerConfig getConfig() {
        return config;
    }
    public static ETMixinConfig getMixinConfig() {
        return mixinConfig;
    }
}
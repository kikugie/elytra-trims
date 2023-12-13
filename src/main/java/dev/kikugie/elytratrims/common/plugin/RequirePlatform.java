package dev.kikugie.elytratrims.common.plugin;

public @interface RequirePlatform {
    Loader loader();

    enum Loader {
        FABRIC,
        FORGE
    }
}

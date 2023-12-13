package dev.kikugie.elytratrims.common.plugin;

/**
 * Annotation for mixins to apply only if a mod is loaded.
 */
public @interface RequireMod {
    String mod();
}

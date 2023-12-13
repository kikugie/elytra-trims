package dev.kikugie.elytratrims.common.plugin;

public @interface RequireTest {
    Class<? extends Tester> tester();
}

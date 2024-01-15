package dev.kikugie.elytratrims.common.config;

import dev.kikugie.elytratrims.common.ETReference;
import dev.kikugie.elytratrims.common.plugin.ModStatus;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class ETMixinConfig {
    private static final Path CONFIG_FILE = ModStatus.configDir.resolve("elytra-trims-disabled-mixins.txt");
    private final Set<String> paths;

    private ETMixinConfig(Set<String> paths) {
        this.paths = paths;
    }

    public static ETMixinConfig load() {
        try {
            if (!Files.exists(CONFIG_FILE)) {
                Files.createDirectories(CONFIG_FILE.getParent());
                Files.createFile(CONFIG_FILE);
                return new ETMixinConfig(Collections.emptySet());
            }
            List<String> lines = Files.readAllLines(CONFIG_FILE);
            return new ETMixinConfig(Set.copyOf(lines));
        } catch (IOException e) {
            ETReference.LOGGER.warn("Failed to read mixin config: " + e);
        }
        return new ETMixinConfig(Collections.emptySet());
    }

    public boolean contains(String mixinClassName) {
        return paths.contains(mixinClassName);
    }
}
package dev.kikugie.elytratrims.common.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import dev.kikugie.elytratrims.common.ETReference;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.function.Supplier;

public class ConfigLoader {
    public static <T> T load(Path file, Codec<T> codec, Supplier<T> provider) throws IOException {
        if (!Files.exists(file)) {
            Files.createDirectories(file.getParent());
            T newConfig = provider.get();
            save(file, codec, newConfig);
            return newConfig;
        }
        try {
            JsonElement json = JsonParser.parseReader(Files.newBufferedReader(file));
            return codec.decode(JsonOps.INSTANCE, json).getOrThrow(true, $ -> {}).getFirst();
        } catch (Exception e) {
            ETReference.LOGGER.warn("Failed to read config: " + e);
            T newConfig = provider.get();
            save(file, codec, newConfig);
            return newConfig;
        }
    }

    public static <T> void save(Path file, Codec<T> codec, T instance) {
        try {
            DataResult<JsonElement> result = codec.encodeStart(JsonOps.INSTANCE, instance);
            Files.writeString(file, result.getOrThrow(false, e -> {
            }).toString(), StandardOpenOption.CREATE);
        } catch (Exception e) {
            ETReference.LOGGER.warn("Failed to save config to %s:\n%s".formatted(file, e));
        }
    }
}
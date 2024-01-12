package dev.kikugie.elytratrims.common.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.kikugie.elytratrims.common.plugin.ModStatus;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ETServerConfig {
    public static final Codec<ETServerConfig> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.BOOL.fieldOf("addTrims").forGetter(it -> it.addTrims),
                    Codec.BOOL.fieldOf("addPatterns").forGetter(it -> it.addPatterns),
                    Codec.BOOL.fieldOf("addGlow").forGetter(it -> it.addGlow),
                    Codec.BOOL.fieldOf("cleanableElytra").forGetter(it -> it.cleanableElytra)
            ).apply(instance, ETServerConfig::new));
    private static final Path CONFIG_FILE = ModStatus.configDir.resolve("elytra-trims-server.json");
    public final boolean addTrims;
    public final boolean addPatterns;
    public final boolean addGlow;
    public final boolean cleanableElytra;

    public ETServerConfig(boolean addTrims, boolean addPatterns, boolean addGlow, boolean cleanableElytra) {
        this.addTrims = addTrims;
        this.addPatterns = addPatterns;
        this.addGlow = addGlow;
        this.cleanableElytra = cleanableElytra;
    }

    public static ETServerConfig load() {
        try {
            return ConfigLoader.load(CONFIG_FILE, CODEC, ETServerConfig::create);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static ETServerConfig create() {
        return new ETServerConfig(true, true, true, true);
    }

    public void save() {
        ConfigLoader.save(CONFIG_FILE, CODEC, this);
    }
}
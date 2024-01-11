package dev.kikugie.elytratrims.common.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.io.IOException;
import java.nio.file.Path;

public class ETServerConfig {
    public static final Codec<ETServerConfig> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.BOOL.optionalFieldOf("addTrims", true).forGetter(it -> it.addTrims),
                    Codec.BOOL.optionalFieldOf("addPatterns", true).forGetter(it -> it.addPatterns),
                    Codec.BOOL.optionalFieldOf("addGlow", true).forGetter(it -> it.addGlow),
                    Codec.BOOL.optionalFieldOf("cleanableElytra", true).forGetter(it -> it.cleanableElytra)
            ).apply(instance, ETServerConfig::new));
    // FIXME: Get config dir compatible with Forge
    private static final Path CONFIG_FILE = null;
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

    static ETServerConfig create() {
        return new ETServerConfig(true, true, true, true);
    }

    public void save() {
        ConfigLoader.save(CONFIG_FILE, CODEC, this);
    }

}
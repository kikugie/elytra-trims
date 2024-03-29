package dev.kikugie.elytratrims.common.plugin;

import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import java.nio.file.Path;
import java.util.Map;

import java.io.IOException;
import java.nio.file.Files;

/*? if fabric {*/
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.api.EnvType;

public class ModStatus {
    private static final FabricLoader FABRIC = FabricLoader.getInstance();
    public static final boolean isFabric = true;
    public static final boolean isClient = FABRIC.getEnvironmentType() == EnvType.CLIENT;
    public static final boolean isServer = FABRIC.getEnvironmentType() == EnvType.SERVER;
    public static final boolean isDev = FABRIC.isDevelopmentEnvironment();
    public static final Path configDir = FABRIC.getConfigDir();
    private static final Map<String, Boolean> cache = new Object2BooleanOpenHashMap<>();

    public static boolean isLoading(String mod) {
        return FABRIC.isModLoaded(mod);
    }
    public static boolean isLoaded(String mod) {
        return cache.computeIfAbsent(mod, ModStatus::isLoadedImpl);
    }

    private static boolean isLoadedImpl(String mod) {
        return FABRIC.isModLoaded(mod);
    }
}
/*?} elif forge {*//*
import java.io.IOException;
import java.nio.file.Files;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.LoadingModList;
public class ModStatus {
    public static final boolean isFabric = false;
    public static final boolean isClient = FMLLoader.getDist() == Dist.CLIENT;
    public static final boolean isServer = FMLLoader.getDist() == Dist.DEDICATED_SERVER;
    public static final boolean isDev = !FMLLoader.isProduction();
    public static final Path configDir = FMLLoader.getGamePath().resolve("config");
    private static final Map<String, Boolean> cache = new Object2BooleanOpenHashMap<>();

    static {
        if (Files.exists(configDir)) {
            try {
                Files.createDirectories(configDir);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static boolean isLoading(String mod) {
        return LoadingModList.get().getModFileById(mod) != null;
    }

    public static boolean isLoaded(String mod) {
        return cache.computeIfAbsent(mod, ModStatus::isLoadedImpl);
    }

    private static boolean isLoadedImpl(String mod) {
        return ModList.get().isLoaded(mod);
    }
}
*//*?} else {*//*
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.LoadingModList;
public class ModStatus {
    public static final boolean isFabric = false;
    public static final boolean isClient = FMLLoader.getDist() == Dist.CLIENT;
    public static final boolean isServer = FMLLoader.getDist() == Dist.DEDICATED_SERVER;
    public static final boolean isDev = !FMLLoader.isProduction();
    public static final Path configDir = FMLLoader.getGamePath().resolve("config");
    private static final Map<String, Boolean> cache = new Object2BooleanOpenHashMap<>();

    static {
        if (Files.exists(configDir)) {
            try {
                Files.createDirectories(configDir);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static boolean isLoading(String mod) {
        return LoadingModList.get().getModFileById(mod) != null;
    }

    public static boolean isLoaded(String mod) {
        return cache.computeIfAbsent(mod, ModStatus::isLoadedImpl);
    }

    private static boolean isLoadedImpl(String mod) {
        return ModList.get().isLoaded(mod);
    }
}
*//*?}*/
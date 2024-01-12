package dev.kikugie.elytratrims.common.plugin;

import net.minecraft.util.Util;

import java.nio.file.Path;
import java.util.function.Function;

/*? if fabric {*/
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.api.EnvType;

public class ModStatus {
    public static final boolean isFabric = true;
    public static final boolean isClient = FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
    public static final boolean isServer = FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER;
    public static final Path configDir = FabricLoader.getInstance().getConfigDir();
    private static final Function<String, Boolean> cache = Util.memoize(ModStatus::isLoadedImpl);

    public static boolean isLoading(String mod) {
        return FabricLoader.getInstance().isModLoaded(mod);
    }
    public static boolean isLoaded(String mod) {
        return cache.apply(mod);
    }

    private static boolean isLoadedImpl(String mod) {
        return FabricLoader.getInstance().isModLoaded(mod);
    }
}
/*?} else {*//*
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
    public static final Path configDir = FMLLoader.getGamePath().resolve("config");
    private static final Function<String, Boolean> cache = Util.memoize(ModStatus::isLoadedImpl);

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
        return cache.apply(mod);
    }

    private static boolean isLoadedImpl(String mod) {
        return ModList.get().isLoaded(mod);
    }
}
*//*?}*/
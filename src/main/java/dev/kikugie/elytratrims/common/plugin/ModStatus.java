package dev.kikugie.elytratrims.common.plugin;

import net.minecraft.util.Util;
import java.util.function.Function;

/*? fabric {*/
import net.fabricmc.loader.api.FabricLoader;

public class ModStatus {
    public static final boolean isFabric = true;
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
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.LoadingModList;

public class ModStatus {
   public static final boolean isFabric = false;
   private static final Function<String, Boolean> cache = Util.memoize(ModStatus::isLoadedImpl);

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
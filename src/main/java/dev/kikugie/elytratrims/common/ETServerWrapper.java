package dev.kikugie.elytratrims.common;

import dev.kikugie.elytratrims.common.recipe.ETRecipeSerializers;
import net.minecraft.recipe.RecipeSerializer;

/*? fabric {*/
import net.fabricmc.api.ModInitializer;

public class ETServerWrapper implements ModInitializer {
    @Override
    public void onInitialize() {
        ETServer.init();
    }
}
/*?} else {*//*
import dev.kikugie.elytratrims.client.ETClientWrapper;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(ETReference.MOD_ID)
public class ETServerWrapper {
    public ETServerWrapper() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ETClientWrapper::init);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ETServerWrapper::init);

        ForgeRegistries.RECIPE_SERIALIZERS.register("crafting_special_elytrapatterns", ETRecipeSerializers.ELYTRA_PATTERNS);
        ForgeRegistries.RECIPE_SERIALIZERS.register("crafting_special_elytraglow", ETRecipeSerializers.ELYTRA_GLOW);
    }

    public static void init(FMLCommonSetupEvent event) {
        ETServer.init();
    }
}
*//*?}*/
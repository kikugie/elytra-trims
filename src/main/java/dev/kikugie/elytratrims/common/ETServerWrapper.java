package dev.kikugie.elytratrims.common;

/*? if fabric {*/
import dev.kikugie.elytratrims.common.recipe.ETRecipeSerializers;
import net.fabricmc.api.ModInitializer;
import net.minecraft.recipe.RecipeSerializer;

public class ETServerWrapper implements ModInitializer {
    @Override
    public void onInitialize() {
        ETServer.init();

        if (ETServer.getConfig().addPatterns)
            RecipeSerializer.register("crafting_special_elytrapatterns", ETRecipeSerializers.ELYTRA_PATTERNS);
        if (ETServer.getConfig().addGlow)
            RecipeSerializer.register("crafting_special_elytraglow", ETRecipeSerializers.ELYTRA_GLOW);
    }
}
/*?} else {*//*
import dev.kikugie.elytratrims.client.ETClientWrapper;
import dev.kikugie.elytratrims.common.recipe.ETRecipeSerializers;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(ETReference.MOD_ID)
public class ETServerWrapper {
    public ETServerWrapper() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ETClientWrapper::init);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ETServerWrapper::init);

        DeferredRegister<RecipeSerializer<?>> EVENT = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, "minecraft");

        if (ETServer.getConfig().addPatterns)
            EVENT.register("crafting_special_elytrapatterns", () -> ETRecipeSerializers.ELYTRA_PATTERNS);
        if (ETServer.getConfig().addGlow)
            EVENT.register("crafting_special_elytraglow", () -> ETRecipeSerializers.ELYTRA_GLOW);
        EVENT.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    public static void init(FMLCommonSetupEvent event) {
        ETServer.init();
    }
}
*//*?}*/
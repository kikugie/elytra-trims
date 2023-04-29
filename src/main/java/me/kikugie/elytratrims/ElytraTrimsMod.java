package me.kikugie.elytratrims;

import me.kikugie.elytratrims.recipe.ElytraPatternRecipe;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.texture.atlas.AtlasSourceType;
import net.minecraft.item.DyeableItem;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ElytraTrimsMod implements ClientModInitializer {

    public static final String MOD_ID = "elytratrims";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final DyeableItem DYEABLE = new DyeableItem() {
    };
    public static final RecipeSerializer<SpecialCraftingRecipe> ELYTRA_PATTERNS_RECIPE = RecipeSerializer.register("crafting_special_elytrapatterns", new SpecialRecipeSerializer<>(ElytraPatternRecipe::new));
    public static AtlasSourceType ELYTRA_TRIMS;
    public static AtlasSourceType ELYTRA_PATTERNS;
    public static AtlasSourceType ELYTRA_OVERLAY;

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }

    @Override
    public void onInitializeClient() {
        LOGGER.info("Making elytras fancier!");
        FabricLoader.getInstance().getModContainer(MOD_ID).ifPresent(container -> ResourceManagerHelper.registerBuiltinResourcePack(
                id("default"), container,
                Text.literal("Elytra Trims Defaults"),
                ResourcePackActivationType.DEFAULT_ENABLED
        ));
    }
}

package dev.kikugie.elytratrims;

import dev.kikugie.elytratrims.recipe.ETCauldronInteraction;
import net.fabricmc.api.ModInitializer;

public class FabricCommonEntrypoint implements ModInitializer {
    @Override
    public void onInitialize() {
        ETCauldronInteraction.register();
    }
}

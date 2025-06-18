package dev.kikugie.elytratrims;

import dev.kikugie.elytratrims.recipe.ETCauldronInteraction;

//? if fabric {
public class FabricCommonEntrypoint implements net.fabricmc.api.ModInitializer {
    @Override
    public void onInitialize() {
        ETCauldronInteraction.register();
    }
}
//?}
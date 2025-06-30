package dev.kikugie.elytratrims;

import dev.kikugie.elytratrims.recipe.ETCauldronInteraction;
import dev.kikugie.fletching_table.annotation.fabric.Entrypoint;

//? if fabric {
@Entrypoint
public class FabricCommonEntrypoint implements net.fabricmc.api.ModInitializer {
    @Override
    public void onInitialize() {
        ETCauldronInteraction.register();
    }
}
//?}
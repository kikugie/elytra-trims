package dev.kikugie.elytratrims;

import dev.kikugie.elytratrims.render.ElytraRenderLayers;
import dev.kikugie.fletching_table.annotation.fabric.Entrypoint;

//? if fabric {
@Entrypoint
public class FabricClientEntrypoint implements net.fabricmc.api.ClientModInitializer {
    @Override
    public void onInitializeClient() {
        var $ = ElytraRenderLayers.GATEWAY_MASKED; // Instantiate it
    }
}
//?}
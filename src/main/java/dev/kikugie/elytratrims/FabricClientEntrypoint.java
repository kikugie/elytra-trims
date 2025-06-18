package dev.kikugie.elytratrims;

import dev.kikugie.elytratrims.render.ElytraRenderLayers;

//? if fabric {
public class FabricClientEntrypoint implements net.fabricmc.api.ClientModInitializer {
    @Override
    public void onInitializeClient() {
        var $ = ElytraRenderLayers.GATEWAY_MASKED; // Instantiate it
    }
}
//?}
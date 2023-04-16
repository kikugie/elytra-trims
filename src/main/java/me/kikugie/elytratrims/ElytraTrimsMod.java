package me.kikugie.elytratrims;

import me.kikugie.elytratrims.render.ElytraTrimsManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ElytraTrimsMod implements ClientModInitializer {

    public static final String MOD_ID = "elytratrims";
    public static ElytraTrimsManager MANAGER;

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }

    @Override
    public void onInitializeClient() {
        FabricLoader.getInstance().getModContainer(MOD_ID).ifPresent(container -> ResourceManagerHelper.registerBuiltinResourcePack(
                id("default"), container,
                Text.literal("Elytra Trims Defaults"),
                ResourcePackActivationType.NORMAL
        ));
    }
}

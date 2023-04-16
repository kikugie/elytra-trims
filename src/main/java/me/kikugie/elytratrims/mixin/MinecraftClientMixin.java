package me.kikugie.elytratrims.mixin;

import me.kikugie.elytratrims.ElytraTrimsMod;
import me.kikugie.elytratrims.render.ElytraTrimsManager;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.resource.ResourceType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/EntityModelLoader;<init>()V"))
    private void registerElytraTrimsManager(RunArgs args, CallbackInfo ci) {
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES)
                .registerReloadListener(ElytraTrimsMod.MANAGER = new ElytraTrimsManager());
    }
}

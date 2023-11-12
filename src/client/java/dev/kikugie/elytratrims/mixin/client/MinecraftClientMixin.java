package dev.kikugie.elytratrims.mixin.client;

import dev.kikugie.elytratrims.resource.ETAtlasHolder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/texture/PaintingManager;<init>(Lnet/minecraft/client/texture/TextureManager;)V"))
    private void initETAtlas(RunArgs args, CallbackInfo ci) {
        ETAtlasHolder.getInstance().ifPresent(ETAtlasHolder::init);
    }
}

package dev.kikugie.elytratrims.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.kikugie.elytratrims.client.ETClient;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.concurrent.CompletableFuture;

/**
 * Resets hashmap cache used by the renderer.
 */
@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @ModifyReturnValue(method = "reloadResources(Z)Ljava/util/concurrent/CompletableFuture;", at = @At("RETURN"))
    private CompletableFuture<Void> resetElytraTrimsCache(CompletableFuture<Void> original) {
        return original.thenRun(ETClient.getRenderer()::resetCache);
    }
}
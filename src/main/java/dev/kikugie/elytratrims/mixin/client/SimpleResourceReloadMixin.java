package dev.kikugie.elytratrims.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.kikugie.elytratrims.client.ETClient;
import dev.kikugie.elytratrims.client.access.ResourceTypeAccessor;
import net.minecraft.resource.*;
import net.minecraft.util.Unit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(value = SimpleResourceReload.class, priority = 1100)
public class SimpleResourceReloadMixin {

    @WrapOperation(method = "start", at = @At(value = "NEW", target = "(Lnet/minecraft/resource/ResourceManager;Ljava/util/List;Ljava/util/concurrent/Executor;Ljava/util/concurrent/Executor;Ljava/util/concurrent/CompletableFuture;)Lnet/minecraft/resource/ProfiledResourceReload;"))
    private static ProfiledResourceReload injectProfiledElytraTrimsReloader(ResourceManager manager, List<ResourceReloader> reloaders, Executor prepareExecutor, Executor applyExecutor, CompletableFuture<Unit> initialStage, Operation<ProfiledResourceReload> original) {
        return original.call(manager, addElytraTrimsReloader(manager, reloaders), prepareExecutor, applyExecutor, initialStage);
    }

    @WrapOperation(method = "start", at = @At(value = "INVOKE", target = "Lnet/minecraft/resource/SimpleResourceReload;create(Lnet/minecraft/resource/ResourceManager;Ljava/util/List;Ljava/util/concurrent/Executor;Ljava/util/concurrent/Executor;Ljava/util/concurrent/CompletableFuture;)Lnet/minecraft/resource/SimpleResourceReload;"))
    private static SimpleResourceReload<?> injectSimpleElytraTrimsReloader(ResourceManager manager, List<ResourceReloader> reloaders, Executor prepareExecutor, Executor applyExecutor, CompletableFuture<Unit> initialStage, Operation<ProfiledResourceReload> original) {
        return original.call(manager, addElytraTrimsReloader(manager, reloaders), prepareExecutor, applyExecutor, initialStage);
    }

    @Unique
    private static List<ResourceReloader> addElytraTrimsReloader(ResourceManager manager, List<ResourceReloader> original) {
        if (manager instanceof ResourceTypeAccessor rta && rta.getResourceType() == ResourceType.CLIENT_RESOURCES) {
            var modified = new ArrayList<>(original);
            modified.add(ETClient.getAtlasHolder());
            return modified;
        }
        return original;
    }

}
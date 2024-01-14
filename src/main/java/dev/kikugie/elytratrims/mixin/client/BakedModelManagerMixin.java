package dev.kikugie.elytratrims.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.kikugie.elytratrims.client.ETClient;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;

@Mixin(BakedModelManager.class)
public class BakedModelManagerMixin {
    @WrapOperation(method = "reload", at = @At(value = "INVOKE", target = "Ljava/util/concurrent/CompletableFuture;thenApplyAsync(Ljava/util/function/Function;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;"))
    private CompletableFuture<Void> reloadElytraAtlas(
            CompletableFuture<Void> instance,
            Function<?, ?> fn,
            Executor executor,
            Operation<CompletableFuture<Void>> original,
            @Local(argsOnly = true) ResourceReloader.Synchronizer synchronizer,
            @Local(argsOnly = true) ResourceManager manager,
            @Local(argsOnly = true, ordinal = 0) Profiler prepareProfiler,
            @Local(argsOnly = true, ordinal = 1) Profiler applyProfiler,
            @Local(argsOnly = true, ordinal = 0) Executor prepareExecutor,
            @Local(argsOnly = true, ordinal = 1) Executor applyExecutor) {
        return original.call(instance.thenRunAsync(() -> ETClient.getAtlasHolder().reload(synchronizer, manager, prepareProfiler, applyProfiler, prepareExecutor, applyExecutor)), fn, executor);
    }
}
package dev.kikugie.elytratrims.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.kikugie.elytratrims.ElytraTrims;
import dev.kikugie.elytratrims.render.ExtraElytraFeatureRenderer;
import dev.kikugie.elytratrims.resource.ETAtlasHolder;
import dev.kikugie.elytratrims.resource.ETResourceListener;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Stream;

@Mixin(BakedModelManager.class)
public abstract class BakedModelManagerMixin {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void initETAtlas(TextureManager textureManager, BlockColors colorMap, int mipmap, CallbackInfo ci) {
        ETAtlasHolder.init();
    }

    @Inject(method = "reload", at = @At("HEAD"))
    private void initETListener(ResourceReloader.Synchronizer synchronizer, ResourceManager manager, Profiler prepareProfiler, Profiler applyProfiler, Executor prepareExecutor, Executor applyExecutor, CallbackInfoReturnable<CompletableFuture<Void>> cir) {
        ETResourceListener.init();
    }

    @ModifyExpressionValue(method = "reload", at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;concat(Ljava/util/stream/Stream;Ljava/util/stream/Stream;)Ljava/util/stream/Stream;"))
    private Stream<? extends CompletableFuture<Void>> loadETAssets(
            Stream<? extends CompletableFuture<Void>> stream,
            @Local ResourceReloader.Synchronizer synchronizer,
            @Local ResourceManager manager,
            @Local(ordinal = 0) Profiler prepareProfiler,
            @Local(ordinal = 1) Profiler applyProfiler,
            @Local(ordinal = 0) Executor prepareExecutor,
            @Local(ordinal = 1) Executor applyExecutor) {
        return Stream.concat(stream, Stream.of(ETAtlasHolder.getInstance().reload(synchronizer, manager, prepareProfiler, applyProfiler, prepareExecutor, applyExecutor)));
    }

    @Inject(method = "upload", at = @At("TAIL"))
    private void initElytraRenderer(BakedModelManager.BakingResult bakingResult, Profiler profiler, CallbackInfo ci) {
        ElytraTrims.ELYTRA_RENDERER = new ExtraElytraFeatureRenderer(ETAtlasHolder.getInstance().atlas());
    }
}

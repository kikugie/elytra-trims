package dev.kikugie.elytratrims.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.kikugie.elytratrims.ElytraTrimsMod;
import dev.kikugie.elytratrims.render.ExtraElytraFeatureRenderer;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

@Mixin(BakedModelManager.class)
public abstract class BakedModelManagerMixin {
    @Shadow public abstract SpriteAtlasTexture getAtlas(Identifier id);

    @ModifyExpressionValue(method = "<clinit>", at = @At(value = "INVOKE", target = "Ljava/util/Map;of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/Map;"))
    private static Map<Identifier, Identifier> addElytraTrimsAtlas(Map<Identifier, Identifier> original) {
        HashMap<Identifier, Identifier> mutable = new HashMap<>(original);
        mutable.put(ElytraTrimsMod.ELYTRA_TRIMS_ATLAS_TEXTURE, ElytraTrimsMod.id("elytra_trims"));
        return mutable;
    }

    @Inject(method = "upload", at = @At("TAIL"))
    private void initElytraRenderer(BakedModelManager.BakingResult bakingResult, Profiler profiler, CallbackInfo ci) {
        ElytraTrimsMod.ELYTRA_RENDERER = new ExtraElytraFeatureRenderer(getAtlas(ElytraTrimsMod.ELYTRA_TRIMS_ATLAS_TEXTURE));
    }
}

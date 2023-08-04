package dev.kikugie.elytratrims.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.kikugie.elytratrims.ElytraTrimsMod;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.HashMap;
import java.util.Map;

@Mixin(BakedModelManager.class)
public class BakedModelManagerMixin {
    @ModifyExpressionValue(method = "<clinit>", at = @At(value = "INVOKE", target = "Ljava/util/Map;of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/Map;"))
    private static Map<Identifier, Identifier> addElytraTrimsAtlas(Map<Identifier, Identifier> original) {
        HashMap<Identifier, Identifier> mutable = new HashMap<>(original);
        mutable.put(ElytraTrimsMod.ELYTRA_TRIMS_ATLAS_TEXTURE, ElytraTrimsMod.id("elytra_trims"));
        return mutable;
    }
}

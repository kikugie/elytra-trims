package dev.kikugie.elytratrims.mixin.resource;

import com.llamalad7.mixinextras.sugar.Local;
import dev.kikugie.elytratrims.resource.ETListWrapperKt;
import dev.kikugie.elytratrims.resource.pack.ETRuntimePack;
import dev.kikugie.elytratrims.resource.pack.ETRuntimePackUtilsKt;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.MultiPackResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.ArrayList;
import java.util.List;

@Mixin(MultiPackResourceManager.class)
public class MultiPackResourceManagerMixin {
    @ModifyVariable(method = "<init>", at = @At("HEAD"), argsOnly = true)
    private static List<PackResources> injectElytraTrimsPacks(List<PackResources> packs, @Local(argsOnly = true) PackType type) {
        if (packs.isEmpty() || ETListWrapperKt.isWrapped(packs) || !ETRuntimePackUtilsKt.hasElytraTrimsPack(packs))
            return ETListWrapperKt.unwrap(packs);
        ResourceManager delegate = new MultiPackResourceManager(type, ETListWrapperKt.wrap(packs));
        ETRuntimePack injected = ETRuntimePack.of(type, delegate);
        if (injected == null) return packs;

        List<PackResources> copy = new ArrayList<>(packs);
        copy.add(injected);
        return copy;
    }
}

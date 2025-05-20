package dev.kikugie.elytratrims.mixin.render;

import dev.kikugie.elytratrims.render.ElytraRenderLayers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//? if >=1.21.4 {
//@Mixin(net.minecraft.client.renderer.CoreShaders.class)
//public class CoreShadersMixin {
//    @org.spongepowered.asm.mixin.Shadow
//    @org.spongepowered.asm.mixin.Final
//    private static java.util.List<net.minecraft.client.renderer.ShaderProgram> PROGRAMS;
//
//    @Inject(method = "<clinit>", at = @At("TAIL"))
//    private static void appendETShaders(CallbackInfo ci) {
//        PROGRAMS.add(ElytraRenderLayers.GATEWAY_MASKED);
//    }
//}
//?} else {
/*@Mixin(net.minecraft.client.renderer.GameRenderer.class)
public class CoreShadersMixin {
    // Who needs imports anyway
    @SuppressWarnings("rawtypes")
    @Inject(method = "reloadShaders", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 0))
    private void appendETShaders(
        net.minecraft.server.packs.resources.ResourceProvider provider,
        CallbackInfo ci,
        @com.llamalad7.mixinextras.sugar.Local(ordinal = 1) java.util.List<com.mojang.datafixers.util.Pair> list) {
        var shader = ElytraRenderLayers.createGatewayShader(provider);
        java.util.function.Consumer<net.minecraft.client.renderer.ShaderInstance> consumer =
            ElytraRenderLayers::applyGatewayShader;
        list.add(com.mojang.datafixers.util.Pair.of(shader, consumer));
    }
}
*///?}
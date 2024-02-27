package dev.kikugie.elytratrims.mixin.compat;

import com.bawnorton.mixinsquared.TargetHandler;
import dev.kikugie.elytratrims.client.access.ElytraRotationAccessor;
import dev.kikugie.elytratrims.common.plugin.MixinConfigurable;
import dev.kikugie.elytratrims.common.plugin.RequireMod;
import net.minecraft.client.gui.screen.ingame.SmithingScreen;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Pseudo
@MixinConfigurable
@RequireMod("bettersmithingtable")
@SuppressWarnings("ALL")
@Mixin(value = SmithingScreen.class, priority = 1500)
public abstract class BetterSmithingTableMixin implements ElytraRotationAccessor {
    @TargetHandler(
            mixin = "me.bettersmithingtable.mixin.SmithingScreenMixin",
            /*? if >=1.20.2 {*//*
            name = "renderBg"
            *//*?} else {*/
            name = "drawArmorStandPreview"
            /*?} */
    )
    @ModifyArg(method = "@MixinSquared:Handler", at = @At(value = "INVOKE",
            /*? if >=1.20.2 {*//*
            target = "Lnet/minecraft/client/gui/screen/ingame/InventoryScreen;drawEntity(Lnet/minecraft/client/gui/DrawContext;FFILorg/joml/Vector3f;Lorg/joml/Quaternionf;Lorg/joml/Quaternionf;Lnet/minecraft/entity/LivingEntity;)V"
            *//*?} elif >=1.20.1 {*//*
            target = "Lnet/minecraft/client/gui/screen/ingame/InventoryScreen;drawEntity(Lnet/minecraft/client/gui/DrawContext;IIILorg/joml/Quaternionf;Lorg/joml/Quaternionf;Lnet/minecraft/entity/LivingEntity;)V"
            *//*?} else {*/
            target = "Lnet/minecraft/client/gui/screen/ingame/InventoryScreen;drawEntity(Lnet/minecraft/client/util/math/MatrixStack;IIILorg/joml/Quaternionf;Lorg/joml/Quaternionf;Lnet/minecraft/entity/LivingEntity;)V"
            /*?} */
    ), index = /*? if >=1.20.2 {*//* 5 *//*?} else {*/ 4 /*?} */)
    private Quaternionf applyElytraRotation(Quaternionf quaternionf) {
        return elytra_trims$rotateElytra(quaternionf);
    }
}
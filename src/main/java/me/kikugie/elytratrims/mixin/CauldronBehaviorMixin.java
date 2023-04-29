package me.kikugie.elytratrims.mixin;

import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.minecraft.block.cauldron.CauldronBehavior.CLEAN_DYEABLE_ITEM;
import static net.minecraft.block.cauldron.CauldronBehavior.WATER_CAULDRON_BEHAVIOR;

@Mixin(CauldronBehavior.class)
public interface CauldronBehaviorMixin {
    @Inject(method = "registerBehavior", at = @At("TAIL"))
    private static void cleanElytra(CallbackInfo ci) {
        WATER_CAULDRON_BEHAVIOR.put(Items.ELYTRA, CLEAN_DYEABLE_ITEM);
    }
}

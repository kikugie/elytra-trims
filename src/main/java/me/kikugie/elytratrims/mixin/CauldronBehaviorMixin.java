package me.kikugie.elytratrims.mixin;

import me.kikugie.elytratrims.recipe.GlowingItem;
import net.minecraft.block.LeveledCauldronBlock;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CauldronBehavior.class)
public interface CauldronBehaviorMixin {
    @Inject(method = "registerBehavior", at = @At("TAIL"))
    private static void cleanElytra(CallbackInfo ci) {
        CauldronBehavior CLEAN_GLOW = (state, world, pos, player, hand, stack) -> {
            Item item = stack.getItem();
            if (!(item instanceof GlowingItem glowingItem)) {
                return ActionResult.PASS;
            } else if (!glowingItem.hasGlow(stack)) {
                return ActionResult.PASS;
            } else {
                if (!world.isClient) {
                    glowingItem.removeGlow(stack);
                    LeveledCauldronBlock.decrementFluidLevel(state, world, pos);
                }
                return ActionResult.success(world.isClient);
            }
        };

        CauldronBehavior.WATER_CAULDRON_BEHAVIOR.put(Items.ELYTRA, (state, world, pos, player, hand, stack) -> {
            ActionResult result = CauldronBehavior.CLEAN_DYEABLE_ITEM.interact(state, world, pos, player, hand, stack);
            ActionResult temp = CauldronBehavior.CLEAN_BANNER.interact(state, world, pos, player, hand, stack);
            result = temp == ActionResult.PASS ? result : temp;
            temp = CLEAN_GLOW.interact(state, world, pos, player, hand, stack);
            return temp == ActionResult.PASS ? result : temp;
        });
    }
}

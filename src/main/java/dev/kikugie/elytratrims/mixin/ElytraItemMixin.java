package dev.kikugie.elytratrims.mixin;

import dev.kikugie.elytratrims.recipe.GlowingItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.*;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ElytraItem.class)
public abstract class ElytraItemMixin extends ItemMixin implements DyeableItem, GlowingItem {
    @Override
    protected void modifyTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context, CallbackInfo ci) {
        BannerItem.appendBannerTooltip(stack, tooltip);
    }
}

package dev.kikugie.elytratrims.mixin.common;

import dev.kikugie.elytratrims.common.config.ConfigTesters;
import dev.kikugie.elytratrims.common.plugin.RequireTest;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.BannerItem;
import net.minecraft.item.DyeableItem;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@RequireTest(ConfigTesters.Patterns.class)
@Mixin(ElytraItem.class)
public class BannerElytraItemMixin extends ItemMixin implements DyeableItem {
    @Override
    protected void elytra_trims$modifyTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context, CallbackInfo ci) {
        BannerItem.appendBannerTooltip(stack, tooltip);
    }
}
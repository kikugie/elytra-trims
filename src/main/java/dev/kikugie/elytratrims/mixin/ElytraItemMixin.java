package dev.kikugie.elytratrims.mixin;

import dev.kikugie.elytratrims.recipe.GlowingItem;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.*;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

import java.util.List;

@Mixin(ElytraItem.class)
public class ElytraItemMixin extends Item implements DyeableItem, GlowingItem {
    public ElytraItemMixin(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        BannerItem.appendBannerTooltip(stack, tooltip);
    }
}

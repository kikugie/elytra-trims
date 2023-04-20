package me.kikugie.elytratrims.mixin;

import net.minecraft.item.DyeableItem;
import net.minecraft.item.ElytraItem;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ElytraItem.class)
public class ElytraItemMixin implements DyeableItem {
}

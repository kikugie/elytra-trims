package dev.kikugie.elytratrims.mixin.common;

import dev.kikugie.elytratrims.common.config.ConfigTesters;
import dev.kikugie.elytratrims.common.plugin.RequireTest;
import dev.kikugie.elytratrims.common.recipe.GlowingItem;
import net.minecraft.item.ElytraItem;
import org.spongepowered.asm.mixin.Mixin;

// Yes, this needs to just implement the interface
@RequireTest(ConfigTesters.Glow.class)
@Mixin(ElytraItem.class)
public class GlowingElytraItemMixin implements GlowingItem {
}
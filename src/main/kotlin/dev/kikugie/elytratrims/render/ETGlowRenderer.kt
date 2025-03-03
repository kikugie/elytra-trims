package dev.kikugie.elytratrims.render

import dev.kikugie.elytratrims.item.ETFlag
import dev.kikugie.elytratrims.item.flags
import net.minecraft.world.item.ItemStack

object ETGlowRenderer {
    @JvmStatic fun getEffectiveLight(stack: ItemStack, default: Int) = if (stack.flags[ETFlag.GLOW]) 0xFF00FF else default
}
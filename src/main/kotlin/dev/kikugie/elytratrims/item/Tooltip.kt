package dev.kikugie.elytratrims.item

import dev.kikugie.elytratrims.Text
import dev.kikugie.elytratrims.translatable
import net.minecraft.network.chat.Component.literal
import net.minecraft.world.item.BannerItem
import net.minecraft.world.item.ItemStack
import java.util.function.Consumer

fun appendTooltip(stack: ItemStack, consumer: Consumer<Text>) {
//    mutableListOf<Text>()
//        .apply { BannerItem.appendHoverTextFromBannerBlockEntityTag(stack, this) }
//        .forEach(consumer)
    val glow = stack.flags[ETFlag.GLOW]
    val gateway = stack.flags[ETFlag.GATEWAY]
    if (!glow && !gateway) return
    consumer.accept("elytratrims.item.tooltip.decorations".translatable())
    if (glow) consumer.accept(literal(" ").append("elytratrims.item.tooltip.glow".translatable()))
    if (gateway) consumer.accept(literal(" ").append("elytratrims.item.tooltip.gateway".translatable()))
}
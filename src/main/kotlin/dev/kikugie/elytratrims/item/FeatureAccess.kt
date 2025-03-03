package dev.kikugie.elytratrims.item

import dev.kikugie.elytratrims.resource.image.Color4i
import net.minecraft.world.item.DyeColor
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.equipment.trim.ArmorTrim

val ItemStack.trim: TrimAccess get() = TrimAccessImpl(this)
val ItemStack.color: ColorAccess get() = ColorAccessImpl(this)
val ItemStack.banner: PatternsAccess get() = PatternsAccessImpl(this)
val ItemStack.flags: FlagAccess get() = FlagAccessImpl(this)

enum class ETFlag(val key: String) {
    GLOW("elytratrims:glow"),
    BANNER("elytratrims:banner"),
    GATEWAY("elytratrims:gateway"),
    ANIMATION("elytratrims:animation")
}

interface TrimAccess {
    var value: ArmorTrim?
    fun clear()
}

interface ColorAccess {
    var value: Color4i
    fun clear()
}

interface PatternsAccess {
    val patterns: Collection<BannerLayer>
    val base: DyeColor?
    fun copy(from: ItemStack)
    fun clear()
}

interface FlagAccess {
    operator fun get(flag: ETFlag): Boolean
    operator fun set(flag: ETFlag, value: Boolean)
}
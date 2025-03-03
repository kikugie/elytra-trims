package dev.kikugie.elytratrims.item

import dev.kikugie.elytratrims.resource.image.Color4i
import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.DyeColor
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.CustomData
import net.minecraft.world.item.component.DyedItemColor
import net.minecraft.world.item.equipment.trim.ArmorTrim

@Suppress("DEPRECATION") // Why copy nbt if we don't modify it
private fun ItemStack.hasCustomFlag(key: String) = get(DataComponents.CUSTOM_DATA)?.contains(key) == true
private fun ItemStack.setCustomFlag(key: String, value: Boolean) =
    if (value) addCustomFlag(key)
    else removeCustomFlag(key)

private fun ItemStack.addCustomFlag(key: String) {
    val data = (get(DataComponents.CUSTOM_DATA) ?: CustomData.EMPTY).copyTag()
    data.putBoolean(key, true)
    CustomData.set(DataComponents.CUSTOM_DATA, this, data)
}

private fun ItemStack.removeCustomFlag(key: String) {
    val data = get(DataComponents.CUSTOM_DATA)?.copyTag() ?: return
    data.remove(key)
    CustomData.set(DataComponents.CUSTOM_DATA, this, data)
}

@JvmInline
value class TrimAccessImpl(private val stack: ItemStack) : TrimAccess {
    override var value: ArmorTrim?
        get() = stack.get(DataComponents.TRIM)
        set(value) {
            if (value !== null) stack.set(DataComponents.TRIM, value)
            else stack.remove(DataComponents.TRIM)
        }

    override fun clear() {
        stack.remove(DataComponents.TRIM)
    }
}

@JvmInline
value class ColorAccessImpl(private val stack: ItemStack) : ColorAccess {
    override var value: Color4i
        get() = Color4i(DyedItemColor.getOrDefault(stack, 0))
        set(value) {
            stack.set(DataComponents.DYED_COLOR, DyedItemColor(value.value, true))
        }

    override fun clear() {
        stack.remove(DataComponents.DYED_COLOR)
    }
}

@JvmInline
value class PatternsAccessImpl(private val stack: ItemStack) : PatternsAccess {
    override val patterns: Collection<BannerLayer>
        get() = stack.get(DataComponents.BANNER_PATTERNS)?.layers?.map {
            BannerLayer(it.pattern, it.color)
        } ?: emptyList()
    override val base: DyeColor?
        get() = stack.get(DataComponents.BASE_COLOR)

    override fun copy(from: ItemStack) {
        from.get(DataComponents.BASE_COLOR).let { stack.set(DataComponents.BASE_COLOR, it) }
        from.get(DataComponents.BANNER_PATTERNS).let { stack.set(DataComponents.BANNER_PATTERNS, it) }
    }

    override fun clear() {
        stack.remove(DataComponents.BANNER_PATTERNS)
        stack.remove(DataComponents.BASE_COLOR)
    }
}

@JvmInline
value class FlagAccessImpl(private val stack: ItemStack) : FlagAccess {
    override fun get(flag: ETFlag): Boolean = stack.hasCustomFlag(flag.key)
    override fun set(flag: ETFlag, value: Boolean) = stack.setCustomFlag(flag.key, value)
}
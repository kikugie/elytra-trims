package dev.kikugie.elytratrims.item

import net.minecraft.core.Holder
import net.minecraft.world.item.DyeColor
import net.minecraft.world.level.block.entity.BannerPattern

data class BannerLayer(val pattern: Holder<BannerPattern>, val color: DyeColor)
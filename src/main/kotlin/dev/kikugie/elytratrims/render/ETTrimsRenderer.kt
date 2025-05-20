package dev.kikugie.elytratrims.render

import dev.kikugie.elytratrims.Identifier
import dev.kikugie.elytratrims.Memoizer
import dev.kikugie.elytratrims.item.trim
import dev.kikugie.elytratrims.memoize
import dev.kikugie.elytratrims.render.ETRenderer.Companion.getSpriteReporting
import net.minecraft.client.renderer.Sheets
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.resources.model.EquipmentClientInfo
import net.minecraft.world.item.equipment.EquipmentAssets
import net.minecraft.world.item.equipment.trim.ArmorTrim

// Renders trims above the banner layer
object ETTrimsRenderer : ETRenderer {
    private val reported = mutableSetOf<Identifier>()
    private val trimCache: Memoizer<ArmorTrim, TextureAtlasSprite?> = memoize {
        getSpriteReporting(Sheets.ARMOR_TRIMS_SHEET, texture(it))
    }
    override fun ETRenderParameters.render(): Boolean = stack.trim.value.run {
        if (this === null) return@run false
        val sprite = trimCache(this) ?: return@run false
        ETRenderer.render(model, sprite, matrices, provider, stack, light, color, Sheets.ARMOR_TRIMS_SHEET)
        true
    }

    private fun texture(trim: ArmorTrim) = trim.layerAssetId(
        EquipmentClientInfo.LayerType.WINGS.trimAssetPrefix(),
        EquipmentAssets.ELYTRA
    )


    override fun clear() {
        reported.clear()
    }
}
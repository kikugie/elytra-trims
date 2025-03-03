package dev.kikugie.elytratrims.render

import dev.kikugie.elytratrims.Identifier
import dev.kikugie.elytratrims.Memoizer
import dev.kikugie.elytratrims.item.color
import dev.kikugie.elytratrims.memoize
import dev.kikugie.elytratrims.resource.image.Color4i
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.ItemRenderer
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.util.Mth.hsvToRgb
import net.minecraft.world.item.Item

object ETColorRenderer : ETRenderer {
    private val minecraft = Minecraft.getInstance()
    private val reported = mutableSetOf<Identifier>()

    // TODO: pass used texture in parameters
    private val elytras: Memoizer<Item, Identifier> = memoize { item ->
        BuiltInRegistries.ITEM.getKey(item).withPath { "textures/entity/equipment/wings/${it}_overlay.png" }
    }

    override fun ETRenderParameters.render(): Boolean {
        val custom = if (stack.displayName.tryCollapseToString() == "jeb_")
            Color4i(hsvToRgb((minecraft.level?.gameTime ?: 0) % 360 / 360F, 1F, 1F))
        else if (stack.color.value.isVisible())
            stack.color.value
        else return false

        val consumer = ItemRenderer.getArmorFoilBuffer(provider, RenderType.armorCutoutNoCull(elytras(stack.item)), stack.hasFoil())
        model.renderToBuffer(matrices, consumer, light, OverlayTexture.NO_OVERLAY, color.blend(custom).value)
        return true
    }

    override fun report(id: Identifier) {
        if (reported.add(id)) ETRenderer.LOGGER.warn("Missing color texture: $id")
    }

    override fun clear() {
        reported.clear()
    }
}
package dev.kikugie.elytratrims.render

import dev.kikugie.elytratrims.Identifier
import dev.kikugie.elytratrims.Memoizer
import dev.kikugie.elytratrims.item.ETFlag
import dev.kikugie.elytratrims.item.flags
import dev.kikugie.elytratrims.memoize
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.Item

object ETGatewayRenderer : ETRenderer {
    // TODO: pass used texture in parameters
    private val elytras: Memoizer<Item, Identifier> = memoize { item ->
        BuiltInRegistries.ITEM.getKey(item).withPath { "textures/entity/equipment/wings/${it}.png" }
    }

    override fun ETRenderParameters.render() : Boolean {
        if (!stack.flags[ETFlag.GATEWAY]) return false
        val consumer = provider.getBuffer(ElytraRenderLayers.GATEWAY(elytras(stack.item)))
        model.renderToBuffer(matrices, consumer, light, OverlayTexture.NO_OVERLAY, color.value)
        return true
    }
}
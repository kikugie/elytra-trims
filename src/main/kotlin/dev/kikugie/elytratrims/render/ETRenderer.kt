package dev.kikugie.elytratrims.render

import com.mojang.blaze3d.vertex.PoseStack
import dev.kikugie.elytratrims.Identifier
import dev.kikugie.elytratrims.render.ETTrimsRenderer.render
import dev.kikugie.elytratrims.resource.image.Color4i
import net.minecraft.client.Minecraft
import net.minecraft.client.model.Model
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.ItemRenderer
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.world.item.ItemStack
import org.slf4j.Logger
import org.slf4j.LoggerFactory

val TextureAtlasSprite.isMissing get() = contents().name().equals(MissingTextureAtlasSprite.getLocation())

data class ETRenderParameters(
    val model: Model,
    val matrices: PoseStack,
    val provider: MultiBufferSource,
    val stack: ItemStack,
    val light: Int,
    val color: Color4i,
) {
    companion object {
        @JvmStatic
        fun of(model: Model, matrices: PoseStack, provider: MultiBufferSource, stack: ItemStack, light: Int, color: Int) =
            ETRenderParameters(model, matrices, provider, stack, light, Color4i(color))
    }
}

interface ETRenderer {
    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger("ETRenderer")
        val POST_RENDERERS = listOf(
            ETColorRenderer,
            ETPatternsRenderer,
            ETTrimsRenderer
        )

        fun render(
            model: Model,
            sprite: TextureAtlasSprite,
            matrices: PoseStack,
            provider: MultiBufferSource,
            stack: ItemStack,
            light: Int,
            color: Color4i,
            atlas: Identifier,
        ) {
            val consumer = ItemRenderer.getArmorFoilBuffer(provider, ElytraRenderLayers.TRANSLUESCENT(atlas), stack.hasFoil())
                .let { sprite.wrap(it) }
            model.renderToBuffer(matrices, consumer, light, OverlayTexture.NO_OVERLAY, color.value)
        }

        fun ETRenderer.getSpriteReporting(atlas: Identifier, texture: Identifier) = Minecraft.getInstance()
            .getTextureAtlas(atlas).apply(texture)
            .takeUnless(TextureAtlasSprite::isMissing)
            .also { if (it == null) report(texture) }

        @JvmStatic
        fun renderPost(parameters: ETRenderParameters): Boolean {
            var rendered = false
            for (it in POST_RENDERERS) with(it) {
                rendered = parameters.render() || rendered
            }
            return rendered
        }
    }

    fun ETRenderParameters.render(): Boolean
    fun report(id: Identifier) {}
    fun clear() {}
}
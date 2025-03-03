package dev.kikugie.elytratrims.render

import dev.kikugie.elytratrims.*
import dev.kikugie.elytratrims.item.ETFlag
import dev.kikugie.elytratrims.item.banner
import dev.kikugie.elytratrims.item.flags
import dev.kikugie.elytratrims.render.ETRenderer.Companion.getSpriteReporting
import net.minecraft.client.renderer.Sheets
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.core.Holder
import net.minecraft.world.level.block.entity.BannerPattern


object ETPatternsRenderer : ETRenderer {
    private val reported = mutableSetOf<Identifier>()
    private val shieldBase: TextureAtlasSprite? by memoized {
        getSpriteReporting(Sheets.SHIELD_SHEET, texture(vanilla("base"), false))
    }
    private val bannerBase: TextureAtlasSprite? by memoized {
        getSpriteReporting(Sheets.BANNER_SHEET, texture(vanilla("base"), true))
    }

    private val shield: Memoizer<Holder<BannerPattern>, TextureAtlasSprite?> = memoize {
        getSpriteReporting(Sheets.SHIELD_SHEET, texture(it.value().assetId, false))
    }
    private val banner: Memoizer<Holder<BannerPattern>, TextureAtlasSprite?> = memoize{
        getSpriteReporting(Sheets.BANNER_SHEET, texture(it.value().assetId, true))
    }

    override fun ETRenderParameters.render() = stack.banner.run {
        val isBanner = stack.flags[ETFlag.BANNER]
        val atlas = if (isBanner) Sheets.BANNER_SHEET else Sheets.SHIELD_SHEET
        var rendered = false
        base?.let {
            val sprite = (if (isBanner) bannerBase else shieldBase) ?: return@let
            val color = color.blend(Color4i(it))
            ETRenderer.render(model, sprite, matrices, provider, stack, light, color, atlas)
            rendered = true
        }
        patterns.forEach { (pattern, it) ->
            val sprite = (if (isBanner) banner(pattern) else shield(pattern)) ?: return@forEach
            val color = color.blend(Color4i(it))
            ETRenderer.render(model, sprite, matrices, provider, stack, light, color, atlas)
            rendered = true
        }
        rendered
    }

    override fun report(id: Identifier) {
        if (reported.add(id)) ETRenderer.LOGGER.warn("Missing pattern texture: $id")
    }

    override fun clear() {
        reported.clear()
        shield.clear()
        banner.clear()

        (::shieldBase.getDelegate() as? MemoizedProperty<*>)?.clear()
        (::bannerBase.getDelegate() as? MemoizedProperty<*>)?.clear()
    }

    private fun texture(asset: Identifier, banner: Boolean) = asset.withPrefix(if (banner) "entity/wings/banner/" else "entity/wings/shield/")
}
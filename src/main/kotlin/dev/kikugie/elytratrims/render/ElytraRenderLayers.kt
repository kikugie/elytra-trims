package dev.kikugie.elytratrims.render

import com.mojang.blaze3d.pipeline.RenderPipeline
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.VertexFormat
import dev.kikugie.elytratrims.Identifier
import dev.kikugie.elytratrims.Memoizer
import dev.kikugie.elytratrims.elytratrims
import dev.kikugie.elytratrims.memoize
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.client.renderer.RenderPipelines.*
import net.minecraft.client.renderer.RenderStateShard
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.TheEndPortalRenderer

object ElytraRenderLayers {
    private val SNIPPET_ARGS =
        /*? if >=1.21.6 {*/arrayOf(MATRICES_PROJECTION_SNIPPET, ENTITY_SNIPPET, FOG_SNIPPET, GLOBALS_SNIPPET)
        /*?} else*//*arrayOf(MATRICES_SNIPPET, ENTITY_SNIPPET, FOG_SNIPPET)*/

    @JvmField val GATEWAY_MASKED_SNIPPET: RenderPipeline.Snippet = RenderPipeline.builder(*SNIPPET_ARGS)
        .withVertexShader(elytratrims("core/elytratrims_gateway"))
        .withFragmentShader(elytratrims("core/elytratrims_gateway"))
        .withSampler("Sampler0")
        .withSampler("Sampler1")
        .withSampler("Sampler2")
        .withVertexFormat(DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS)
        .withCull(false)
        .buildSnippet()

    @JvmField val GATEWAY_MASKED: RenderPipeline = RenderPipeline.builder(GATEWAY_MASKED_SNIPPET)
        .withLocation(elytratrims("pipeline/elytratrims_gateway"))
        .withShaderDefine("PORTAL_LAYERS", 16)
        .build().let(RenderPipelines::register)

    @JvmField val GATEWAY: Memoizer<Identifier, RenderType> = memoize {
        val state = RenderType.CompositeState.builder()
            .setTextureState(
                RenderStateShard.MultiTextureStateShard.builder()
                    .add(TheEndPortalRenderer.END_SKY_LOCATION)
                    .add(TheEndPortalRenderer.END_PORTAL_LOCATION)
                    .add(it)
                    .build()
            )
            .createCompositeState(false)
        RenderType.create("elytra_gateway", 1536, false, false, GATEWAY_MASKED, state)
    }
    /*
    val TRANSLUESCENT: Memoizer<Identifier, RenderType> = memoize {
        val state = RenderType.CompositeState.builder()
            .setTextureState(RenderStateShard.TextureStateShard(it, false))
            .setLightmapState(RenderStateShard.LightmapStateShard.LIGHTMAP)
            .setOverlayState(RenderStateShard.OverlayStateShard.OVERLAY)
            .setLayeringState(RenderStateShard.LayeringStateShard.VIEW_OFFSET_Z_LAYERING)
            .createCompositeState(true)
        RenderType.create("elytra_translucent", 1536, true, true, RenderPipelines.ARMOR_CUTOUT_NO_CULL, state)
    }
     */

    private fun RenderStateShard.MultiTextureStateShard.Builder.add(tex: Identifier) =
        /*? if >=1.21.6 {*/add(tex, false)
        /*?} else*//*add(tex, false, false)*/
}
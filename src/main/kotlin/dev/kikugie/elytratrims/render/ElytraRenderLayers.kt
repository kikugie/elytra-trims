package dev.kikugie.elytratrims.render

import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.VertexFormat
import dev.kikugie.elytratrims.*
import net.minecraft.client.renderer.RenderStateShard
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.TheEndPortalRenderer
import net.minecraft.util.TriState

private typealias Shader =
    /*? if >=1.21.4 {*/ net.minecraft.client.renderer.ShaderProgram
    /*?} else*/ /*net.minecraft.client.renderer.ShaderInstance*/

@Suppress("JoinDeclarationAndAssignment", "UNNECESSARY_LATEINIT")
object ElytraRenderLayers {
    @JvmStatic
    lateinit var GATEWAY_MASKED: Shader
    lateinit var GATEWAY_MASKED_SHARD: RenderStateShard.ShaderStateShard
        private set

    init {
        //? if >=1.21.4 {
        GATEWAY_MASKED = Shader(
            vanilla("core/elytratrims_gateway"),
            DefaultVertexFormat.POSITION,
            net.minecraft.client.renderer.ShaderDefines.EMPTY
        )
        GATEWAY_MASKED_SHARD = RenderStateShard.ShaderStateShard(GATEWAY_MASKED)
        //?} else
        /*GATEWAY_MASKED_SHARD = RenderStateShard.ShaderStateShard(::GATEWAY_MASKED)*/
    }

    //? if <1.21.4 {
    /*@JvmStatic fun createGatewayShader(provider: net.minecraft.server.packs.resources.ResourceProvider) =
        Shader(provider, "elytratrims_gateway", DefaultVertexFormat.POSITION)
    @JvmStatic fun applyGatewayShader(shader: Shader) {
        GATEWAY_MASKED = shader
    }
    *///?}

    val GATEWAY: Memoizer<Identifier, RenderType> = memoize {
        RenderType.create(
            "elytra_gateway",
            DefaultVertexFormat.POSITION_TEX,
            VertexFormat.Mode.QUADS,
            1536,
            false,
            false,
            RenderType.CompositeState.builder()
                .setShaderState(GATEWAY_MASKED_SHARD)
                .setCullState(RenderStateShard.NO_CULL)
                .setTexturingState(RenderStateShard.DEFAULT_TEXTURING)
                .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                .setTextureState(
                    RenderStateShard.MultiTextureStateShard.builder()
                        .add(TheEndPortalRenderer.END_SKY_LOCATION, false, false)
                        .add(TheEndPortalRenderer.END_PORTAL_LOCATION, false, false)
                        .add(it, false, false)
                        .build()
                ).createCompositeState(false)
        )
    }

    val TRANSLUESCENT: Memoizer<Identifier, RenderType> = memoize {
        RenderType.create(
            "elytra_transluescent",
            DefaultVertexFormat.NEW_ENTITY,
            VertexFormat.Mode.QUADS,
            1536,
            true,
            true,
            RenderType.CompositeState.builder()
                .setShaderState(RenderStateShard.RENDERTYPE_ENTITY_NO_OUTLINE_SHADER)
                .setTextureState(RenderStateShard.TextureStateShard(it, TriState.DEFAULT, false))
                .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                .setCullState(RenderStateShard.NO_CULL)
                .setLightmapState(RenderStateShard.LIGHTMAP)
                .setOverlayState(RenderStateShard.OVERLAY)
                .setLayeringState(RenderStateShard.VIEW_OFFSET_Z_LAYERING)
                .setWriteMaskState(RenderStateShard.COLOR_WRITE)
                .createCompositeState(true)
        )
    }
}
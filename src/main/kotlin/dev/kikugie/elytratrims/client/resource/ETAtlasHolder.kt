package dev.kikugie.elytratrims.client.resource

import com.google.gson.JsonParser
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.serialization.Dynamic
import com.mojang.serialization.JsonOps
import dev.kikugie.elytratrims.client.ETClient
import dev.kikugie.elytratrims.client.render.ETRenderer
import dev.kikugie.elytratrims.common.ETReference
import dev.kikugie.elytratrims.common.util.getAnyway
import dev.kikugie.elytratrims.common.util.identifier
import dev.kikugie.elytratrims.mixin.client.PalettedPermutationsAtlasSourceAccessor
import net.minecraft.client.MinecraftClient
import net.minecraft.client.texture.*
import net.minecraft.client.texture.SpriteLoader.StitchResult
import net.minecraft.client.texture.atlas.AtlasLoader
import net.minecraft.client.texture.atlas.AtlasSourceManager
import net.minecraft.client.texture.atlas.PalettedPermutationsAtlasSource
import net.minecraft.resource.ResourceFinder
import net.minecraft.resource.ResourceManager
import net.minecraft.resource.ResourceReloader
import net.minecraft.util.Identifier
import net.minecraft.util.profiler.Profiler
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor
import java.util.function.Function
import java.util.function.Supplier
import kotlin.jvm.optionals.getOrNull

object ETAtlasHolder : ResourceReloader {
    /*? if >=1.20.2*/
    /*private val opener: SpriteOpener = SpriteOpener.create(SpriteLoader.METADATA_READERS)*/
    val id: Identifier = ETReference.id("elytra_features")
    val atlas = SpriteAtlasTexture(ETReference.id("textures/atlas/elytra_features.png")).also {
        RenderSystem.recordRenderCall {
            @Suppress("UsePropertyAccessSyntax") // Neoforge sees it as a private property access idk why
            MinecraftClient.getInstance().getTextureManager().registerTexture(id, it)
        }
    }
    var ready = false
        private set

    private fun sprites(manager: ResourceManager, model: NativeImage, id: Identifier) = buildList {
        addAll(trims(manager, model))
        addAll(patterns(manager, model))
        add(color(id, model))
        addAll(animation(manager))
//        val item = loadTexture(identifier("textures/item/elytra.png"), manager)?.readSafe()
//        if (item != null) {
//            add(itemColor(ETReference.id("item/elytra_overlay"), item))
//            add(itemOutline(ETReference.id("item/elytra_outline"), item))
//            item.close()
//        }
        add(MissingSprite::createSpriteContents)
    }

    private fun patterns(manager: ResourceManager, model: NativeImage): Collection<ContentSupplier> {
        val useBanners = ETClient.config.texture.useBannerTextures
        val finder = ResourceFinder("textures/entity/${if (useBanners) "banner" else "shield"}", ".png")
        return buildList {
            for ((id, res) in finder.findResources(manager)) {
                var image = AtlasSprite(finder.toResourceId(id), res, 1).readSafe() ?: continue
                image = image.use {
                    if (useBanners) it.cropped(it.width * 2)
                    else it.cropped(height = it.height / 2)
                }
                val scale: Int = image.width / 64
                val xOffset = ((if (useBanners) 35.5F else 34F) * scale).toInt()
                val yOffset = if (useBanners) (scale * 1.5F).toInt() else 0
                add {
                    image
                        .use { it.offset(xOffset, yOffset) }
                        .use { it.mask(model) }
                        .toContents(id.withPath { path -> path.substring(9..<path.length - 4) })
                }
            }
        }
    }

    private fun trims(manager: ResourceManager, model: NativeImage): Collection<ContentSupplier> {
        val crop = ETClient.config.texture.cropTrims
        val atlases = manager.findAllResources("atlases") { it.path.endsWith("armor_trims.json") }
        val sources = atlases.values.flatten().flatMap {
            try {
                val dynamic = it.reader.use { Dynamic(JsonOps.INSTANCE, JsonParser.parseReader(it)) }
                val data = AtlasSourceManager.LIST_CODEC.parse(dynamic).getAnyway()
                data.filterIsInstance<PalettedPermutationsAtlasSource>()
            } catch (e: Exception) {
                emptyList()
            }
        }
        sources.forEach { src ->
            src as PalettedPermutationsAtlasSourceAccessor
            src.textures = src.textures
                .filter { "armor" in it.path && "leggings" !in it.path }
                .map { it.withPath { path -> path.replaceFirst("armor", "elytra") } }
        }

        fun SpriteContents.cropIfNeeded() = if (crop) transform { it.mask(model) } else this

        return AtlasLoader(sources).loadSources(manager).map {
            {
                val sprite = /*? if <1.20.2 {*/it.get()/*?} else*//*it.apply(opener)*/
                sprite?.cropIfNeeded()
            }
        }
    }

    private fun color(id: Identifier, model: NativeImage): ContentSupplier = { saturationMask(model).toContents(id) }

    private fun itemOutline(id: Identifier, model: NativeImage): ContentSupplier {
        val out = model.outline { edge -> if (edge) -1 else 0 }
        return { out.toContents(id) }
    }

    private fun itemColor(id: Identifier, model: NativeImage): ContentSupplier {
        val out = saturationMask(model).outline { edge -> if (edge) 0 else null }
        return { out.toContents(id) }
    }

    private fun animation(manager: ResourceManager): Collection<ContentSupplier> = buildList {
        val resource = manager.getResource(ETReference.id("textures/animation/animation.png")).getOrNull()
            ?: return emptyList()
        /*? if <1.20.2 {*/
        val sprite = SpriteLoader.load(ETReference.id("animation/animation"), resource) ?: return emptyList()
        /*?} else*/
        /*val sprite = opener.loadSprite(ETReference.id("animation/animation"), resource) ?: return emptyList()*/
        add { sprite }
    }

    private fun transform(
        sprites: List<ContentSupplier>,
        executor: Executor,
    ): CompletableFuture<List<SpriteContents>> =
        /*? if <1.20.2 {*/SpriteLoader.loadAll(sprites.mapNotNull(::asSupplier), executor);
        /*?} else*//*SpriteLoader.loadAll(opener, sprites.mapNotNull(::asFunction), executor)*/

    private fun load(manager: ResourceManager, executor: Executor): CompletableFuture<StitchResult> {
        var model: NativeImage? = null
        return CompletableFuture.supplyAsync {
            ready = false
            ETRenderer.reset()
            atlas.clear()
            val id = identifier("textures/entity/elytra.png")
            model = loadTexture(id, manager)?.readSafe() ?: return@supplyAsync emptyList()
            sprites(manager, model!!, id.withPath { path: String ->
                path.replace("textures/", "").replace(".png", "")
            })
        }.thenCompose {
            transform(it, executor)
        }.thenApply {
            SpriteLoader.fromAtlas(atlas).stitch(it, 0, executor)
        }.thenCompose(StitchResult::whenComplete)
            .whenComplete { _, _ -> model?.close() }
    }

    fun apply(
        data: StitchResult,
        profiler: Profiler,
        executor: Executor,
    ): CompletableFuture<Void> = CompletableFuture.runAsync({
        profiler.startTick()
        profiler.push("upload")
        atlas.upload(data)
        ready = true
        profiler.pop()
        profiler.endTick()
    }, executor)

    override fun reload(
        helper: ResourceReloader.Synchronizer,
        manager: ResourceManager,
        loadProfiler: Profiler,
        applyProfiler: Profiler,
        loadExecutor: Executor,
        applyExecutor: Executor,
    ): CompletableFuture<Void> = load(manager, loadExecutor)
        .thenCompose {
            helper.whenPrepared(it)
        }.thenCompose {
            apply(it, applyProfiler, applyExecutor)
        }

    internal fun <T> asSupplier(it: (() -> T)?) = if (it != null) Supplier { it() } else null
    private fun <P, T> asFunction(it: (() -> T)?) = if (it != null) Function<P, T> { it() } else null
}
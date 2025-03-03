package dev.kikugie.elytratrims.resource.provider

import com.google.gson.JsonParser
import com.mojang.serialization.Dynamic
import com.mojang.serialization.JsonOps
import dev.kikugie.elytratrims.Identifier
import dev.kikugie.elytratrims.mixin.resource.PalettedPermutationsAccessor
import dev.kikugie.elytratrims.resource.pack.InputSupplier
import dev.kikugie.elytratrims.resource.pack.PackIdentifier
import dev.kikugie.elytratrims.vanilla
import net.minecraft.client.renderer.texture.atlas.SpriteSource
import net.minecraft.client.renderer.texture.atlas.SpriteSources
import net.minecraft.client.renderer.texture.atlas.sources.DirectoryLister
import net.minecraft.client.renderer.texture.atlas.sources.PalettedPermutations
import net.minecraft.server.packs.PackType
import net.minecraft.server.packs.resources.ResourceManager
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.function.Predicate

class ETAtlasGenerator(val lookup: ResourceManager) : ETResourceProvider<List<SpriteSource>>() {
    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger("ET Atlas Generator")
    }

    override fun generate(): Map<PackIdentifier, List<SpriteSource>> = buildMap {
        val sources = collectPalettedPermutations()

        atlas(vanilla("armor_trims")) {
            this += sources.map {
                it as PalettedPermutationsAccessor
                val textures = it.textures.distinct().mapNotNull {
                    if ("humanoid" !in it.path || "leggings" in it.path) null
                    else it.withPath { it.replace("humanoid", "wings") }
                }
                PalettedPermutations(textures, it.paletteKey, it.permutations)
            }
        }
        atlas(vanilla("blocks")) {
            val textures = listOf("trims/items/wings_trim", "trims/items/wings_broken_trim").map(::vanilla)
            this += sources.map {
                it as PalettedPermutationsAccessor
                PalettedPermutations(textures, it.paletteKey, it.permutations)
            }
        }
        atlas(vanilla("banner_patterns")) {
            this += "entity/wings/banner".let { DirectoryLister(it, "$it/") }
        }
        atlas(vanilla("shield_patterns")) {
            this += "entity/wings/shield".let { DirectoryLister(it, "$it/") }
        }
    }

    override fun convert(value: List<SpriteSource>): InputSupplier =
        toJsonSupplier(SpriteSources.FILE_CODEC, value)

    fun collectPatterns(useBanners: Boolean): Collection<Identifier> {
        val type = if (useBanners) "banner" else "shield"
        val atlases = lookup.listResourceStacks("atlases") { it.path.endsWith("${type}_patterns.json") }
        val proxy = ProxyOutput(mutableSetOf<Identifier>())
        for (res in atlases.values.asSequence().flatten()) res.runCatching {
            openAsReader()
                .use { Dynamic(JsonOps.INSTANCE, JsonParser.parseReader(it)) }
                .let { SpriteSources.FILE_CODEC.parse(it).orThrow }
                .forEach { it.run(lookup, proxy) }
        }.onFailure {
            LOGGER.error("Failed to read atlas source", it)
        }
        return proxy.delegate
    }

    fun collectPalettedPermutations(): Collection<PalettedPermutations> {
        val atlases = lookup.listResourceStacks("atlases") { it.path.endsWith("armor_trims.json") }
        val sources = atlases.values.asSequence().flatten().flatMap { res ->
            res.runCatching {
                openAsReader()
                    .use { Dynamic(JsonOps.INSTANCE, JsonParser.parseReader(it)) }
                    .let { SpriteSources.FILE_CODEC.parse(it).orThrow }
                    .filterIsInstance<PalettedPermutations>()
            }.getOrElse {
                LOGGER.error("Failed to read atlas source", it)
                emptyList()
            }
        }
        val grouped: Map<Identifier, PalettedPermutations> = sources
            .groupingBy { (it as PalettedPermutationsAccessor).paletteKey }
            .fold(::newSource) { _, accum, src ->
                accum as PalettedPermutationsAccessor
                src as PalettedPermutationsAccessor
                accum.apply {
                    textures.addAll(src.textures)
                    permutations.putAll(src.permutations)
                }
            }
        return grouped.values
    }

    private fun newSource(key: Identifier, source: PalettedPermutations): PalettedPermutations {
        source as PalettedPermutationsAccessor
        return PalettedPermutations(source.textures.toMutableList(), key, source.permutations.toMutableMap())
    }

    private inline fun MutableMap<PackIdentifier, List<SpriteSource>>.atlas(id: Identifier, action: MutableList<SpriteSource>.() -> Unit) {
        val path = PackIdentifier.of(PackType.CLIENT_RESOURCES, id.namespace, "atlases/${id.path}.json")
        this[path] = mutableListOf<SpriteSource>().apply(action)
    }

    private class ProxyOutput(val delegate: MutableSet<Identifier>) : SpriteSource.Output {
        override fun add(identifier: Identifier, ignored: SpriteSource.SpriteSupplier) {
            delegate += identifier
        }

        override fun removeAll(predicate: Predicate<Identifier>) {
            delegate.removeIf(predicate)
        }
    }
}
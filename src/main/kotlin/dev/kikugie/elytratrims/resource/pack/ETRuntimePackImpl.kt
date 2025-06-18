package dev.kikugie.elytratrims.resource.pack

import dev.kikugie.elytratrims.ModData
import dev.kikugie.elytratrims.resource.provider.ETAtlasGenerator
import dev.kikugie.elytratrims.resource.provider.ETItemModelGenerator
import dev.kikugie.elytratrims.resource.provider.ETTagGenerator
import dev.kikugie.elytratrims.resource.provider.ETTextureGenerator
import dev.kikugie.elytratrims.text
import net.minecraft.server.packs.PackLocationInfo
import net.minecraft.server.packs.PackType
import net.minecraft.server.packs.repository.PackSource
import net.minecraft.server.packs.resources.ResourceManager
import org.slf4j.LoggerFactory
import java.util.Optional
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import kotlin.streams.asSequence
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class ETRuntimePackImpl(
    val type: PackType,
    val manager: ResourceManager
) : ETRuntimePack {
    companion object {
        val LOGGER = LoggerFactory.getLogger("Elytra Trims Runtime Pack")

        val CLIENT = listOf(
            ::ETAtlasGenerator,
            ::ETItemModelGenerator,
            ::ETTextureGenerator,
        )
        val SERVER = listOf(
            ::ETTagGenerator
        )
    }

    val base = requireNotNull(manager.listPacks().asSequence().find { "elytratrims" in it.packId() }) {
        "Missing 'elytratrims' pack, present packs:\n" + manager.listPacks().asSequence().joinToString("\n") { "  - ${it.packId()}" }
    }
    override val metadata: PackLocationInfo = PackLocationInfo(
        "elytra-trims-runtime",
        "Elytra Trims Generated".text(),
        PackSource.BUILT_IN,
        Optional.empty()
    )
    override val namespaces: Map<PackType, Set<String>> = buildMap<PackType, MutableSet<String>> {
        for (pack in manager.listPacks()) for (type in PackType.entries)
            getOrPut(type) { mutableSetOf() } += pack.getNamespaces(type)
    }
    override val resources: Map<PackIdentifier, InputSupplier> = collectResources()

    init {
        if (ModData.isDevEnv) dump(ModData.gameDir.resolve(".et-debug"))
    }

    override fun open(file: String): InputSupplier? {
        return if ('/' !in file) base.getRootResource(file)
        else resources[PackIdentifier.Companion.of(file)]
    }

    override fun locate(path: PackIdentifier): Sequence<ResourceEntry> = resources.entries.asSequence()
        .filter { path.type == it.key.type && path.namespace == it.key.namespace && it.key.path.startsWith(path.path) }
        .map { (it.key to it.value) }

    @OptIn(ExperimentalTime::class)
    private fun collectResources(): Map<PackIdentifier, InputSupplier> {
        val data = ConcurrentHashMap<PackIdentifier, InputSupplier>()
        val time = measureTime {
            when (type) {
                PackType.CLIENT_RESOURCES -> CLIENT
                PackType.SERVER_DATA -> SERVER
            }.map { it(manager).run().thenAcceptAsync { data += it } }
                .let { CompletableFuture.allOf(*it.toTypedArray()) }
                .join()
        }

        val resources = data.keys.joinToString("\n") { "  - $it" }
        LOGGER.info("Collected ${data.size} ${type.directory} resources in $time:\n$resources")
        return data
    }
}
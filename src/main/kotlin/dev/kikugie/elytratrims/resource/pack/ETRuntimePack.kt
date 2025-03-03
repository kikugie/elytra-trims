package dev.kikugie.elytratrims.resource.pack

import dev.kikugie.elytratrims.Identifier
import dev.kikugie.elytratrims.ResourcePack
import dev.kikugie.elytratrims.resource.pack.PackIdentifier
import dev.kikugie.elytratrims.then
import net.minecraft.FileUtil
import net.minecraft.server.packs.AbstractPackResources
import net.minecraft.server.packs.PackLocationInfo
import net.minecraft.server.packs.PackResources
import net.minecraft.server.packs.PackType
import net.minecraft.server.packs.resources.IoSupplier
import java.io.InputStream

typealias InputSupplier = IoSupplier<InputStream>
typealias ResourceEntry = Pair<PackIdentifier, InputSupplier>

private typealias MetadataAccessor<T> =
    /*? >=1.21.4 {*/ net.minecraft.server.packs.metadata.MetadataSectionType<T>
    /*?} else*/ /*net.minecraft.server.packs.metadata.MetadataSectionSerializer<T>*/

interface ETRuntimePack : ResourcePack {
    val metadata: PackLocationInfo
    val namespaces: Map<PackType, Set<String>>
    val resources: Map<PackIdentifier, InputSupplier>
    fun open(file: String): InputSupplier?
    fun locate(path: PackIdentifier): Sequence<ResourceEntry>

    override fun location(): PackLocationInfo? = metadata
    override fun getNamespaces(type: PackType): Set<String> = namespaces[type] ?: emptySet()

    override fun getRootResource(vararg strings: String?): InputSupplier? =
        FileUtil.validatePath(*strings) then open(strings.joinToString("/"))

    override fun getResource(packType: PackType, identifier: Identifier): InputSupplier? =
        open(getFilename(packType, identifier))

    override fun listResources(type: PackType, namespace: String, path: String, visitor: PackResources.ResourceOutput) =
        locate(PackIdentifier.Companion.of(type, namespace, path)).forEach { (id, supplier) -> visitor.accept(id.toIdentifier(), supplier) }

    override fun <T : Any?> getMetadataSection(reader: MetadataAccessor<T?>): T? =
        requireNotNull(open("pack.mcmeta")?.get()) { "Missing 'pack.mcmeta' resource" }
            .use { AbstractPackResources.getMetadataFromStream(reader, it) }

    override fun close() {
    }

    private fun getFilename(type: PackType, identifier: Identifier) =
        "${type.directory}/${identifier.namespace}/${identifier.path}"
}
package dev.kikugie.elytratrims.resource.pack

import dev.kikugie.elytratrims.Identifier
import net.minecraft.server.packs.PackType

data class PackIdentifier(
    val type: PackType,
    val namespace: String,
    val path: String,
) {
    companion object {
        fun of(type: PackType, namespace: String, path: String) = PackIdentifier(type, namespace, path)
        fun of(type: PackType, id: Identifier) = PackIdentifier(type, id.namespace, id.path)
        fun of(file: String): PackIdentifier {
            val (type, namespace, path) = file.split('/', limit = 3)
            val entry = requireNotNull(PackType.values().find { it.directory == type }) {
                "File '$file' contains invalid resource type '$type'"
            }
            return PackIdentifier(entry, namespace, path)
        }
    }
    fun toIdentifier() = Identifier.fromNamespaceAndPath(namespace, path)
    fun toPath() = "${type.directory}/$namespace/$path"
    override fun toString(): String = "${type.directory} - $namespace:$path"
}
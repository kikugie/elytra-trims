package dev.kikugie.elytratrims.resource.pack

import dev.kikugie.elytratrims.ResourcePack
import java.io.ByteArrayOutputStream
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import kotlin.collections.iterator
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.createDirectories
import kotlin.io.path.deleteRecursively
import kotlin.io.path.exists
import kotlin.io.path.writeBytes

@OptIn(ExperimentalPathApi::class)
fun ETRuntimePack.dump(dir: Path) {
    for ((id, supplier) in resources) dir.resolve(id.toPath()).also {
        if (!it.parent.exists()) it.parent.createDirectories()
        val output = ByteArrayOutputStream()
        supplier.get().copyTo(output)
        if (it.exists()) it.deleteRecursively()
        it.writeBytes(output.toByteArray(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
    }
}

fun List<ResourcePack>.hasElytraTrimsPack(): Boolean = any { "elytratrims" in it.packId() }
package dev.kikugie.elytratrims.resource.provider

import com.mojang.blaze3d.platform.NativeImage
import dev.kikugie.elytratrims.Identifier
import dev.kikugie.elytratrims.resource.image.Color4i
import dev.kikugie.elytratrims.resource.pack.ETRuntimePackImpl.Companion.LOGGER
import dev.kikugie.elytratrims.resource.pack.InputSupplier
import dev.kikugie.elytratrims.resource.pack.PackIdentifier
import dev.kikugie.elytratrims.resource.image.EditableImage
import dev.kikugie.elytratrims.resource.image.toEditable
import dev.kikugie.elytratrims.then
import dev.kikugie.elytratrims.vanilla
import net.minecraft.server.packs.PackType
import net.minecraft.server.packs.resources.ResourceManager
import java.io.ByteArrayOutputStream
import java.nio.channels.Channels
import kotlin.jvm.optionals.getOrNull

class ETTextureGenerator(val lookup: ResourceManager) : ETResourceProvider<EditableImage>() {
    override fun generate(): Map<PackIdentifier, EditableImage> = buildMap {
        fun suffixed(suffix: String): (String) -> String = { it.replace(".png", "_$suffix.png")}

        val saturated = listOf(
            "entity/equipment/wings/elytra.png",
            "item/elytra.png",
            "item/elytra_broken.png",
        )
        for (image in saturated) texture(vanilla(image), suffixed("overlay")) {
            use(EditableImage::saturated)
        }

        val outlined = listOf(
            "item/elytra.png",
            "item/nether_star.png",
            "item/paper.png",
            "item/leather.png",
            "item/glow_ink_sac.png"
        )
        for (image in outlined) texture(vanilla(image), { "gui/sprites/container/slot/${it.substringAfterLast('/')}" }) {
            use { it.outlined(Color4i(0xFF373737)) }
        }

        texture(vanilla("trims/items/wings_trim.png"), suffixed("broken")) {
            load(vanilla("item/elytra_broken.png"))?.use { mask -> use { it.masked(mask) } } ?: this
        }

        load(vanilla("entity/equipment/wings/elytra.png"))?.use { base ->
            for (it in ETAtlasGenerator(lookup).collectPatterns(true)) {
                val texture = load(it.withSuffix(".png"))?.use { it.resized(x2 = it.dims.height * 2) } ?: continue
                val identifier = it.withPath { "entity/wings/banner/${it.substringAfterLast('/')}.png" }
                val image = base.applyPattern(texture, true)
                texture(identifier, image)
            }
            for (it in ETAtlasGenerator(lookup).collectPatterns(false)) {
                val texture = load(it.withSuffix(".png"))?.use { it.resized(y2 = it.dims.width / 2) } ?: continue
                val identifier = it.withPath { "entity/wings/shield/${it.substringAfterLast('/')}.png" }
                val image = base.applyPattern(texture, false)
                texture(identifier, image)
            }
        }
    }

    override fun convert(value: EditableImage): InputSupplier {
        val output = ByteArrayOutputStream()
        val channel = Channels.newChannel(output)
        value.use { it.native.writeToChannel(channel) }
        return BytesSupplier(output.toByteArray())
    }

    private inline fun MutableMap<PackIdentifier, EditableImage>.texture(
        source: Identifier,
        noinline dest: (String) -> String,
        action: EditableImage.() -> EditableImage
    ) = texture(source, source.withPath(dest), action)

    private inline fun MutableMap<PackIdentifier, EditableImage>.texture(
        source: Identifier,
        dest: Identifier = source,
        action: EditableImage.() -> EditableImage
    ) {
        texture(dest, load(source)?.action() ?: return)
    }

    private fun MutableMap<PackIdentifier, EditableImage>.texture(
        dest: Identifier,
        image: EditableImage,
    ) {
        val path = PackIdentifier.of(PackType.CLIENT_RESOURCES, dest.namespace, "textures/${dest.path}")
        this[path] = image
    }

    private fun load(identifier: Identifier): EditableImage? {
        val path = identifier.withPrefix("textures/")
        val resource = lookup.getResource(path).getOrNull() ?: return null.also {
            LOGGER.warn("Resource '$path' wasn't found")
        }
        return resource.runCatching {
            NativeImage.read(open()).toEditable()
        }.getOrElse {
            LOGGER.warn("Error while reading resource '$path'", it) then null
        }
    }

    private fun EditableImage.applyPattern(texture: EditableImage, isBanner: Boolean): EditableImage {
        val scale = texture.dims.width / 64
        val dx = (if (isBanner) 35.5F else 34F) * scale
        val dy = (if (isBanner) 1.5F else 0F) * scale
        return texture.use { it.offset(dx.toInt(), dy.toInt()) }.use { it.masked(this) }
    }
}
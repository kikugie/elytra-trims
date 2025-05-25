package dev.kikugie.elytratrims.client.resource

import dev.kikugie.elytratrims.common.util.*
import net.minecraft.client.texture.MissingSprite
import net.minecraft.client.texture.NativeImage
import net.minecraft.client.texture.Sprite
import net.minecraft.client.texture.SpriteContents
import net.minecraft.client.texture.SpriteDimensions
import net.minecraft.resource.ResourceManager
import net.minecraft.util.Identifier
import java.io.IOException
import kotlin.math.max
import kotlin.math.min

internal typealias AtlasSprite = /*? if <=1.20.4 {*/net.minecraft.client.texture.atlas.Sprite/*?} else {*//*net.minecraft.client.texture.atlas.AtlasSprite  *//*?}*/
internal typealias ContentSupplier = () -> SpriteContents?

fun loadTexture(id: Identifier, manager: ResourceManager, regions: Int = 1) =
    manager.getResource(id).orElse(null)?.let { AtlasSprite(id, it, regions) }

fun AtlasSprite.readSafe(): NativeImage? = try {
    read()
} catch (e: IOException) {
    null
}

inline fun SpriteContents.transform(action: (NativeImage) -> NativeImage): SpriteContents = image.use { action(it).toContents(id) }

fun NativeImage.alpha(x: Int, y: Int) = getColor(x, y) shr 24 and 0xFF

fun NativeImage.copyColor(to: NativeImage, x: Int, y: Int) = to.setColor(x, y, getColor(x, y))

fun NativeImage.isInBounds(x: Int, y: Int) = x < width && y < height && x >= 0 && y >= 0

inline fun NativeImage.forEachPixel(
    widthLimit: Int = this.width,
    heightLimit: Int = this.height,
    action: (Int, Int) -> Unit,
) {
    for (y in 0..<heightLimit)
        for (x in 0..<widthLimit)
            action(x, y)
}

fun NativeImage.cropped(width: Int = this.width, height: Int = this.height) = NativeImage(width, height, true).also {
    forEachPixel(min(this.width, width), min(this.height, height)) { x, y -> copyColor(it, x, y) }
}

fun NativeImage.offset(dx: Int, dy: Int) = NativeImage(width, height, true).also {
    forEachPixel { x, y ->
        val nx = x + dx
        val ny = y + dy
        if (isInBounds(nx, ny)) it.setColor(nx, ny, getColor(x, y))
    }
}

internal fun NativeImage.mask(mask: NativeImage) = NativeImage(width, height, true).also {
    val scale = mask.width / width.toFloat()
    when {
        scale < 1 -> forEachPixel { x, y -> // Mask is smaller
            val mx = (x * scale).toInt()
            val my = (y * scale).toInt()
            if (mask.isInBounds(mx, my) && mask.alpha(mx, my) != 0)
                copyColor(it, x, y)
        }
        scale > 1 -> forEachPixel { x, y -> // Mask is bigger
            val sx = (scale * x).toInt()
            val sy = (scale * y).toInt()
            for (my in sy..<sy + scale.toInt()) for (mx in sx..<sx + scale.toInt())
                if (mask.isInBounds(mx, my) && mask.alpha(mx, my) != 0) copyColor(it, x, y)
        }
        else -> forEachPixel { x, y ->
            if (mask.alpha(x, y) != 0) copyColor(it, x, y)
        }
    }
}

fun NativeImage.toContents(id: Identifier) = SpriteContents(
    id,
    SpriteDimensions(width, height),
    this,
    /*? if <1.20.2*/net.minecraft.client.resource.metadata.AnimationResourceMetadata.EMPTY
    /*? if >=1.20.2*//*net.minecraft.resource.metadata.ResourceMetadata.NONE*/  
)

fun saturationMask(image: NativeImage): NativeImage {
    var maxSaturation = 0
    image.forEachPixel { x, y ->
        val color = image.getColor(x, y)
        val red = (color shr 16 and 0xFF)
        val green = (color shr 8 and 0xFF)
        val blue = (color and 0xFF)
        val alpha = (color shr 24 and 0xFF)
        if (alpha != 0) maxSaturation = max(maxSaturation, max(red, max(green, blue)))
    }
    val saturationDiff = 255 - maxSaturation
    val masked = NativeImage(image.width, image.height, true)
    masked.forEachPixel { x, y ->
        val color = image.getColor(x, y)
        val saturation = max(color.red, max(color.green, color.blue)) + saturationDiff
        masked.setColor(x, y, color and -0x1000000 or (saturation shl 16) or (saturation shl 8) or saturation)
    }
    return masked
}

inline fun NativeImage.outline(action: (Boolean) -> ARGB?) = NativeImage(width, height, true).also { img ->
    forEachPixel { x, y ->
        var outline = false
        val color = getColor(x, y)
        if (color.alpha == 0) return@forEachPixel
        main@for (lx in x-1..x+1) for (ly in y-1..y+1) {
            if ((lx == 0 && ly == 0) || isInBounds(lx, ly) && getColor(lx, ly).alpha != 0) continue
            outline = true
            break@main
        }
        val result = action(outline)
        if (result == null) img.setColor(x, y, color)
        else img.setColor(x, y, result)
    }
}

val Sprite.missing get() = contents.id == MissingSprite.getMissingSpriteId()
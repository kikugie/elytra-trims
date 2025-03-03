package dev.kikugie.elytratrims.resource.image

import com.mojang.blaze3d.platform.NativeImage

@Suppress("NOTHING_TO_INLINE")
private inline fun NativeImage.get(x: Int, y: Int) =
    /*? if >=1.21.4 {*/ getPixel(x, y)
    /*?} else*/ /*getPixelRGBA(x, y)*/

@Suppress("NOTHING_TO_INLINE")
private inline fun NativeImage.set(x: Int, y: Int, color: Int) =
    /*? if >=1.21.4 {*/ setPixel(x, y, color)
    /*?} else*/ /*setPixelRGBA(x, y, color)*/

fun NativeImage.toEditable() = EditableImage(this)

fun Int.fastIsPow2(): Boolean = this and (this - 1) == 0
inline fun forInBox(x1: Int, y1: Int, x2: Int, y2: Int, action: (Int, Int) -> Unit) {
    for (y in y1 until y2) for (x in x1 until x2) action(x, y)
}

class ImageIterator(val image: EditableImage) : Iterator<Pair<Int, Int>> {
    private var x = 0
    private var y = 0

    override fun hasNext(): Boolean = x in image.dims.x && y in image.dims.y
    override fun next(): Pair<Int, Int> = (x to y).also {
        if (++x >= image.dims.width) {
            x = 0; y++
        }
    }
}

@JvmInline
value class ImageAxis(val size: Int) {
    operator fun contains(pos: Int) = pos >= 0 && pos < size
}

@JvmInline
value class ImageDimensions(val native: NativeImage) {
    val x get() = ImageAxis(native.width)
    val y get() = ImageAxis(native.height)
    val width get() = native.width
    val height get() = native.height
}

@JvmInline
value class EditableImage(val native: NativeImage) : AutoCloseable by native, Iterable<Pair<Int, Int>> {
    val dims get() = ImageDimensions(native)
    operator fun get(x: Int, y: Int) = Color4i(native.get(x, y))
    operator fun set(x: Int, y: Int, color: Color4i) = native.set(x, y, color.value)
    operator fun contains(pos: Pair<Int, Int>) = pos.first in dims.x && pos.second in dims.y
    override fun iterator(): Iterator<Pair<Int, Int>> = ImageIterator(this)
    inline fun forEachPixel(action: EditableImage.(Int, Int, Color4i) -> Unit) {
        forEach { x, y -> action(x, y, this[x, y]) }
    }

    inline fun forEach(action: EditableImage.(Int, Int) -> Unit) = forInBox(0, 0, dims.width, dims.height) { x, y ->
        action(x, y)
    }

    fun emptyCopy(x: Int = dims.width, y: Int = dims.height) = NativeImage(native.format(), x, y, true).toEditable()
    fun copy() = NativeImage(native.format(), dims.width, dims.height, true).apply { copyFrom(native) }.toEditable()
    fun offset(dx: Int, dy: Int): EditableImage {
        val image = emptyCopy()
        forEach { x, y ->
            if ((x + dx) in image.dims.x && (y + dy) in image.dims.y)
                image[x + dx, y + dy] = this@EditableImage[x, y]
        }
        return image
    }

    fun resized(x1: Int = 0, y1: Int = 0, x2: Int = dims.width, y2: Int = dims.height): EditableImage {
        require(x2 - x1 > 0) { "Empty x axis" }
        require(y2 - y1 > 0) { "Empty y axis" }
        val image = emptyCopy(x2 - x1, y2 - y1)
        image.forEach { x, y ->
            val ox = x + x1
            val oy = y + y1
            if (ox to oy in this@EditableImage) this[x, y] = this@EditableImage[ox, oy]
        }
        return image
    }

    fun upscaled(scale: Int): EditableImage {
        require(scale > 0 && scale.fastIsPow2()) { "Scale must be a positive power of 2" }
        if (scale == 1) return copy()

        val image = emptyCopy(dims.width * scale, dims.height * scale)
        image.forEach { x, y ->
            val ox = x / scale
            val oy = y / scale
            this[x, y] = this@EditableImage[ox, oy]
        }
        return image
    }

    fun saturated(): EditableImage {
        val diff = 255 - maxOf { (x, y) ->
            val color = this[x, y]
            if (color.alpha == 0) 0
            else maxOf(color.red, color.green, color.blue)
        }
        val masked = emptyCopy()
        forEachPixel { x, y, color ->
            if (!color.isVisible()) return@forEachPixel
            val saturation = maxOf(color.red, color.green, color.blue) + diff
            masked[x, y] = Color4i(saturation, saturation, saturation, color.alpha)
        }
        return masked
    }

    fun masked(mask: EditableImage): EditableImage {
        require(dims.width / mask.dims.width == dims.height / mask.dims.height) {
            "Masked image must have same proportions, got Src(${dims.width}, ${dims.height}) Mask(${mask.dims.width}, ${mask.dims.height})"
        }
        val scale = dims.width / mask.dims.width.toFloat()
        return when {
            scale == 1F -> maskedImpl(mask)
            scale > 1F -> mask.upscaled(scale.toInt()).use { maskedImpl(it) }
            scale < 1F -> upscaled((1 / scale).toInt()).use { it.maskedImpl(mask) }
            else -> error("Unreachable")
        }
    }

    fun outlined(edge: Color4i): EditableImage {
        val image = emptyCopy()
        forEachPixel { x, y, color ->
            if (!color.isVisible()) return@forEachPixel
            forInBox(x - 1, y - 1, x + 2, y + 2) { ox, oy ->
                if ((ox == 0 && oy == 0) || ox to oy in this && this[ox, oy].isVisible()) return@forInBox
                image[x, y] = edge
                return@forEachPixel
            }
        }
        return image
    }

    private fun maskedImpl(mask: EditableImage): EditableImage {
        val masked = copy()
        mask.forEachPixel { x, y, color ->
            val mcolor = masked[x, y]
            if (mcolor.alpha > color.alpha)
                masked[x, y] = mcolor.withAlpha(color.alpha)
        }
        return masked
    }
}
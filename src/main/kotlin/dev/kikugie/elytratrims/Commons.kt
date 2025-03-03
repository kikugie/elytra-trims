package dev.kikugie.elytratrims

import dev.kikugie.elytratrims.resource.image.Color4i
import net.minecraft.resources.ResourceLocation.fromNamespaceAndPath
import net.minecraft.resources.ResourceLocation.withDefaultNamespace
import net.minecraft.world.item.DyeColor
import java.util.Optional
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

@Suppress("NOTHING_TO_INLINE")
inline infix fun <T> Any.then(o: T): T = o
fun <T : Any?> T.asOptional() = Optional.ofNullable(this)

fun vanilla(path: String): Identifier = withDefaultNamespace(path)
fun elytratrims(path: String): Identifier = fromNamespaceAndPath("elytratrims", path)

fun String.text(): Text = Text.literal(this)
fun String.translatable(): Text = Text.translatable(this)
fun String.translatable(vararg args: Any): Text = Text.translatable(this, *args)

interface Memoizer<K, V> : (K) -> V {
    fun clear()
}

fun <K, V> memoize(provider: (K) -> V): Memoizer<K, V> = object : Memoizer<K, V> {
    private val cache: MutableMap<K, V> = mutableMapOf()

    override fun invoke(key: K): V = cache.computeIfAbsent(key, provider)
    override fun clear() = cache.clear()
}

fun <T> memoized(supplier: () -> T): MemoizedProperty<T> = MemoizedProperty(supplier)

class MemoizedProperty<T>(private val supplier: () -> T) : ReadOnlyProperty<Any?, T> {
    private var value: T? = null
    override fun getValue(thisRef: Any?, property: KProperty<*>): T =
        value ?: supplier().also { value = it }

    fun clear() {
        value = null
    }
}

fun Color4i(dye: DyeColor) = Color4i(dye.textureDiffuseColor)
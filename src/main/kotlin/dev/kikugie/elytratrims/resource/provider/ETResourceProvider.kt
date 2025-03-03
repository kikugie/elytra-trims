package dev.kikugie.elytratrims.resource.provider

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.Strictness
import com.mojang.serialization.Codec
import com.mojang.serialization.JsonOps
import dev.kikugie.elytratrims.resource.pack.InputSupplier
import dev.kikugie.elytratrims.resource.pack.PackIdentifier
import net.minecraft.Util
import java.util.concurrent.CompletableFuture

abstract class ETResourceProvider<T> {
    companion object {
        val GSON: Gson = GsonBuilder().setStrictness(Strictness.LENIENT).create()
        fun <T> toJsonSupplier(codec: Codec<T>, value: T) = codec.encodeStart(JsonOps.INSTANCE, value).orThrow
            .let<JsonElement, String>(GSON::toJson)
            .let(::StringSupplier)
    }

    abstract fun generate(): Map<PackIdentifier, T>
    abstract fun convert(value: T): InputSupplier
    fun run(): CompletableFuture<Map<PackIdentifier, InputSupplier>> = CompletableFuture.supplyAsync({
        generate().mapValues { (_, value) -> convert(value) }
    }, Util.backgroundExecutor().forName(this::class.simpleName!!))
}
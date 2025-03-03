package dev.kikugie.elytratrims.resource.provider

import dev.kikugie.elytratrims.resource.pack.InputSupplier
import java.io.InputStream

@JvmInline
value class StringSupplier(val string: String) : InputSupplier {
    override fun get(): InputStream = string.byteInputStream(Charsets.UTF_8)
}

@JvmInline
value class BytesSupplier(val bytes: ByteArray) : InputSupplier {
    override fun get(): InputStream = bytes.inputStream()
}

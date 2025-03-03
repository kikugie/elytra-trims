package dev.kikugie.elytratrims.resource.pack

import dev.kikugie.elytratrims.then
import kotlinx.atomicfu.locks.withLock
import java.io.InputStream
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantLock

object ETResourceMemoizer {
    private val cache = ConcurrentHashMap<String, MemoizedInputStreamProvider>()

    fun memoize(id: String, supplier: InputSupplier): InputSupplier =
        cache.getOrPut(id) { MemoizedInputStreamProvider(supplier.get()) }

    private class MemoizedInputStreamProvider(original: InputStream) : InputSupplier {
        val bytes: MutableList<Byte> = mutableListOf<Byte>()
        val tracked = TrackedInputStream(original)

        override fun get(): InputStream = MemoizedInputStream()

        inner class TrackedInputStream(val original: InputStream) : InputStream() {
            val lock = ReentrantLock()
            var cursor = 0
            override fun read(): Int = lock.withLock {
                original.read().also { if (it >= 0) cursor++ then bytes += it.toByte() }
            }
        }

        inner class MemoizedInputStream : InputStream() {
            var cursor = 0
            override fun read(): Int =
                if (cursor < bytes.size) bytes[cursor++.toInt()].toInt()
                else tracked.read()
        }
    }
}
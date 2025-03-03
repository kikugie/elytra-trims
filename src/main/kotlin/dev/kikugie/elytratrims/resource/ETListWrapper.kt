package dev.kikugie.elytratrims.resource

private class ETListWrapper<T>(val list: List<T>) : List<T> by list

fun <T> List<T>.wrap(): List<T> = ETListWrapper(this)
fun <T> List<T>.unwrap(): List<T> = if (this is ETListWrapper) list else this
fun List<*>.isWrapped(): Boolean = this is ETListWrapper

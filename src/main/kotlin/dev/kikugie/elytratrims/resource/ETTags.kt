package dev.kikugie.elytratrims.resource

import dev.kikugie.elytratrims.elytratrims
import net.minecraft.core.registries.Registries
import net.minecraft.tags.TagKey

object ETTags {
    val ELYTRA_DECORATEABLE = create("decorateable")

    private fun create(name: String) = TagKey.create(Registries.ITEM, elytratrims(name))
}
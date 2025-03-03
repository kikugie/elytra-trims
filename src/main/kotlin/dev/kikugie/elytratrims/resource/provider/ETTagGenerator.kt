package dev.kikugie.elytratrims.resource.provider

import dev.kikugie.elytratrims.resource.ETTags
import dev.kikugie.elytratrims.resource.pack.InputSupplier
import dev.kikugie.elytratrims.resource.pack.PackIdentifier
import dev.kikugie.elytratrims.vanilla
import net.minecraft.server.packs.PackType
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.tags.ItemTags
import net.minecraft.tags.TagBuilder
import net.minecraft.tags.TagFile
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item

class ETTagGenerator(val lookup: ResourceManager) : ETResourceProvider<TagBuilder>() {
    override fun generate(): Map<PackIdentifier, TagBuilder> = buildMap {
//        val items = listOf(vanilla("elytra"))
//        tag(ItemTags.TRIMMABLE_ARMOR) {
//            for (it in items) addOptionalElement(it)
//        }
//        tag(ItemTags.DYEABLE) {
//            for (it in items) addOptionalElement(it)
//        }
//        tag(ETTags.ELYTRA_DECORATEABLE) {
//            for (it in items) addOptionalElement(it)
//        }
    }

    override fun convert(value: TagBuilder): InputSupplier = toJsonSupplier(TagFile.CODEC, TagFile(value.build(), false))

    private inline fun MutableMap<PackIdentifier, TagBuilder>.tag(key: TagKey<Item>, action: TagBuilder.() -> Unit) {
        val path = PackIdentifier.of(PackType.SERVER_DATA, key.location.namespace, "tags/${key.registry.location().path}/${key.location.path}.json")
        this[path] = TagBuilder.create().apply(action)
    }
}
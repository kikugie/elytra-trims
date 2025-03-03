package dev.kikugie.elytratrims.resource.provider

import com.google.gson.JsonElement
import com.mojang.serialization.JsonOps
import dev.kikugie.elytratrims.Identifier
import dev.kikugie.elytratrims.mixin.resource.PalettedPermutationsAccessor
import dev.kikugie.elytratrims.resource.pack.InputSupplier
import dev.kikugie.elytratrims.resource.pack.PackIdentifier
import net.minecraft.client.color.item.Dye
import net.minecraft.client.data.models.ItemModelGenerators
import net.minecraft.client.data.models.ItemModelOutput
import net.minecraft.client.data.models.model.*
import net.minecraft.client.renderer.item.ClientItem
import net.minecraft.client.renderer.item.ItemModel
import net.minecraft.client.renderer.item.properties.conditional.Broken
import net.minecraft.client.renderer.item.properties.select.TrimMaterialProperty
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import dev.kikugie.elytratrims.vanilla
import net.minecraft.server.packs.PackType
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items
import java.util.function.BiConsumer

class ETItemModelGenerator(val lookup: ResourceManager) : ETResourceProvider<JsonElement>() {
    companion object {
        val COLOR = Dye(0xFFBB99FF.toInt())
    }

    override fun generate(): Map<PackIdentifier, JsonElement> = buildMap {
        val generators = ModelProxy(this).let { ItemModelGenerators(it, it) }
        val materials = ETAtlasGenerator(lookup).collectPalettedPermutations()
            .flatMap { (it as PalettedPermutationsAccessor).permutations.keys }
            .toSet()

        generators.generateElytraModel(Items.ELYTRA, materials)
    }

    override fun convert(value: JsonElement): InputSupplier = GSON.toJson(value)
        .let(::StringSupplier)

    private fun ItemModelGenerators.generateElytraModel(item: Item, materials: Iterable<String>) {
        generateBooleanDispatch(
            item,
            Broken(),
            generateConditionalElytraModel(item, false, materials),
            generateConditionalElytraModel(item, true, materials),
        )
    }

    private fun ItemModelGenerators.generateConditionalElytraModel(
        item: Item,
        functional: Boolean,
        materials: Iterable<String>
    ): ItemModel.Unbaked {
        val suffix = if (functional) "" else "_broken"
        val model = ModelLocationUtils.getModelLocation(item, suffix)
        val texture = TextureMapping.getItemTexture(item, "_overlay$suffix")

        val cases = materials.map {
            val key = ResourceKey.create(Registries.TRIM_MATERIAL, vanilla(it))
            ItemModelUtils.`when`(key, generateTrimmedElytraModel(model, texture, it, suffix))
        }

        ModelTemplates.FLAT_ITEM.create(model, TextureMapping.layer0(texture), modelOutput)
        return ItemModelUtils.select(TrimMaterialProperty(), ItemModelUtils.tintedModel(model, COLOR), cases)
    }

    private fun ItemModelGenerators.generateTrimmedElytraModel(
        model: Identifier,
        texture: Identifier,
        trim: String,
        suffix: String
    ): ItemModel.Unbaked {
        val model = model.withSuffix("_${trim}_trim")
        val item = vanilla("trims/items/wings${suffix}_trim_${trim}")
        generateLayeredItem(model, texture, item)
        return ItemModelUtils.tintedModel(model, COLOR)
    }

    private class ModelProxy(val map: MutableMap<PackIdentifier, JsonElement>) : ItemModelOutput, BiConsumer<Identifier, ModelInstance> {
        override fun accept(item: Item, model: ItemModel.Unbaked) {
            val id = BuiltInRegistries.ITEM.getKey(item)
            val path = PackIdentifier.of(PackType.CLIENT_RESOURCES, id.withPath { "items/$it.json" })
            map[path] = ClientItem.CODEC.encodeStart(JsonOps.INSTANCE, ClientItem(model, ClientItem.Properties.DEFAULT)).orThrow
        }

        override fun accept(identifier: Identifier, model: ModelInstance) {
            val path = PackIdentifier.of(PackType.CLIENT_RESOURCES, identifier.withPath { "models/$it.json" })
            map[path] = model.get()
        }

        override fun copy(item: Item, item2: Item) {
            throw UnsupportedOperationException("Not yet implemented")
        }
    }
}
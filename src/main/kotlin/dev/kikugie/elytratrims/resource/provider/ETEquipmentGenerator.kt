package dev.kikugie.elytratrims.resource.provider

import dev.kikugie.elytratrims.asOptional
import dev.kikugie.elytratrims.resource.pack.InputSupplier
import dev.kikugie.elytratrims.resource.pack.PackIdentifier
import net.minecraft.client.resources.model.EquipmentClientInfo
import net.minecraft.resources.ResourceKey
import dev.kikugie.elytratrims.vanilla
import net.minecraft.server.packs.PackType
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.world.item.equipment.EquipmentAsset
import net.minecraft.world.item.equipment.EquipmentAssets

// TODO: To be used in resourcepack gen
class ETEquipmentGenerator(val lookup: ResourceManager) : ETResourceProvider<EquipmentClientInfo>() {
    override fun generate(): Map<PackIdentifier, EquipmentClientInfo> = buildMap {
        asset(EquipmentAssets.ELYTRA) {
            addLayers(
                EquipmentClientInfo.LayerType.WINGS,
                EquipmentClientInfo.Layer(
                    vanilla("elytra_overlay"),
                    EquipmentClientInfo.Dyeable(ETItemModelGenerator.COLOR.defaultColor.asOptional()).asOptional(),
                    false,
                )
            )
        }
    }

    override fun convert(value: EquipmentClientInfo): InputSupplier = toJsonSupplier(EquipmentClientInfo.CODEC, value)

    private inline fun MutableMap<PackIdentifier, EquipmentClientInfo>.asset(
        key: ResourceKey<EquipmentAsset>,
        action: EquipmentClientInfo.Builder.() -> Unit
    ) {
        val path = PackIdentifier.of(PackType.CLIENT_RESOURCES, key.location().namespace, "equipment/${key.location().path}.json")
        this[path] = EquipmentClientInfo.builder().apply(action).build()
    }
}
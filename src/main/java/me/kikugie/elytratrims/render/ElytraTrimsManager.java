package me.kikugie.elytratrims.render;

import me.kikugie.elytratrims.ElytraTrimsMod;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasHolder;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.util.Identifier;

public class ElytraTrimsManager extends SpriteAtlasHolder implements IdentifiableResourceReloadListener {
    public static final Identifier TRIMS_ATLAS_ID = ElytraTrimsMod.id("textures/atlas/elytra_trims.png");
    public static final Identifier TRIMS_ATLAS_CONF = ElytraTrimsMod.id("elytra_trims");

    public ElytraTrimsManager() {
        super(MinecraftClient.getInstance().getTextureManager(), TRIMS_ATLAS_ID, TRIMS_ATLAS_CONF);
    }

    public Sprite getSprite(ArmorTrim trim) {
        return getSprite(getTrimId(trim));
    }

    public Identifier getTrimId(ArmorTrim trim) {
        String material = trim.getMaterial().value().assetName();
        return trim.getPattern().value().assetId().withPath(path -> "trims/models/elytra/" + path + "_" + material);
    }

    @Override
    public Identifier getFabricId() {
        return ElytraTrimsMod.id("elytra_trims");
    }
}

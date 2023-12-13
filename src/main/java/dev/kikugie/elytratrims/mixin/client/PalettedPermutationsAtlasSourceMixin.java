package dev.kikugie.elytratrims.mixin.client;

import dev.kikugie.elytratrims.client.resource.ETResourceListener;
import dev.kikugie.elytratrims.client.access.ElytraSourceAccessor;
import net.minecraft.client.texture.atlas.AtlasSource;
import net.minecraft.client.texture.atlas.PalettedPermutationsAtlasSource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;

/**
 * Generates textures for every occuring palette. Unfortunately for every occuring pattern as well, which may cause some log spam.
 */
@Mixin(PalettedPermutationsAtlasSource.class)
public class PalettedPermutationsAtlasSourceMixin implements ElytraSourceAccessor {
    @Shadow
    @Final
    private List<Identifier> textures;
    @Shadow
    @Final
    private Identifier paletteKey;
    @Shadow
    @Final
    private Map<String, Identifier> permutations;
    @Unique
    private boolean noCallback = false;

    @Inject(method = "load", at = @At("HEAD"))
    private void loadElytraPermutations(ResourceManager resourceManager, AtlasSource.SpriteRegions regions, CallbackInfo ci) {
        if (this.noCallback)
            return;
        ETResourceListener.addTrims(this.textures, this.paletteKey, this.permutations);
    }

    @Unique
    @Override
    public void elytra_trims$ignoreListener() {
        this.noCallback = true;
    }
}

package dev.kikugie.elytratrims.mixin.client;

import dev.kikugie.elytratrims.ElytraTrims;
import dev.kikugie.elytratrims.access.ElytraSourceAccessor;
import dev.kikugie.elytratrims.resource.ETResourceListener;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Generates textures for every occuring palette. Unfortunately for every occuring pattern as well, which may cause some log spam.
 */
@Mixin(PalettedPermutationsAtlasSource.class)
public class PalettedPermutationsAtlasSourceMixin implements ElytraSourceAccessor {
    @Unique
    private final String SUPPORTED_PATTERN = "trims/models/armor/[\\w_-]+";
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
        List<Identifier> elytraTextures = new ArrayList<>(this.textures.size());
        for (Identifier texture : this.textures) {
            String path = texture.getPath();
            if (path.contains("armor")
                    && !path.contains("leggings")
                    && path.matches(this.SUPPORTED_PATTERN))
                elytraTextures.add(new Identifier(texture.getNamespace(), path.replaceFirst("armor", "elytra")));
        }
        if (!elytraTextures.isEmpty())
            ETResourceListener.addTrims(elytraTextures, this.paletteKey, this.permutations);
    }

    @Unique
    @Override
    public void elytra_trims$ignoreListener() {
        this.noCallback = true;
    }
}

package dev.kikugie.elytratrims.mixin.resource;

import net.minecraft.client.renderer.texture.atlas.sources.PalettedPermutations;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Map;

@Mixin(PalettedPermutations.class)
public interface PalettedPermutationsAccessor {
    @Accessor ResourceLocation getPaletteKey();
    @Accessor List<ResourceLocation> getTextures();
    @Accessor Map<String, ResourceLocation> getPermutations();

    @Accessor @Mutable void setPaletteKey(ResourceLocation key);
    @Accessor @Mutable void setTextures(List<ResourceLocation> textures);
    @Accessor @Mutable void setPermutations(Map<String, ResourceLocation> permutations);
}

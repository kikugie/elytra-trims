package dev.kikugie.elytratrims.mixin.client;

import com.google.common.collect.ImmutableList;
import dev.kikugie.elytratrims.ElytraTrims;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;

@Environment(EnvType.CLIENT)
@Mixin(SimpleRegistry.class)
public class SimpleRegistryMixin {
    @SuppressWarnings("unchecked")
    @Inject(method = "populateTags", at = @At("HEAD"))
    private void markElytraAsTrimmable(Map<TagKey<?>, List<RegistryEntry<?>>> tagEntries, CallbackInfo ci) {
        List<RegistryEntry<Item>> trims = (List<RegistryEntry<Item>>) (Object) tagEntries.get(ItemTags.TRIMMABLE_ARMOR);
        if (trims == null)
            return;
        RegistryEntry<Item> elytra = Items.ELYTRA.getRegistryEntry();
        ElytraTrims.elytraTrimmingAvailable = trims.contains(elytra);
        if (ElytraTrims.elytraTrimmingAvailable)
            return;

        tagEntries.put(ItemTags.TRIMMABLE_ARMOR, ImmutableList.<RegistryEntry<?>>builder().addAll(trims).add(elytra).build());
    }
}

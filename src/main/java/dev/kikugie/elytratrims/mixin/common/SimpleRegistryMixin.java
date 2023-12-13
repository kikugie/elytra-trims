package dev.kikugie.elytratrims.mixin.common;

import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Marks all elytra items as trimmable on the server. It's done with a mixin instead of a json file to account for modded elytras. If an unknown item is present in that file, it's ignored entirely.
 */
@Mixin(SimpleRegistry.class)
public class SimpleRegistryMixin {
    @SuppressWarnings("deprecation")
    @Inject(method = "populateTags", at = @At("HEAD"))
    private void makeElytraTrimmable(CallbackInfo ci, @Local(argsOnly = true) LocalRef<Map<TagKey<Item>, List<RegistryEntry<Item>>>> tagEntriesRef) {
        var tagEntries = tagEntriesRef.get();
        var trims = tagEntries.get(ItemTags.TRIMMABLE_ARMOR);
        if (trims == null)
            return;

        List<RegistryEntry<Item>> newTrimmable = new ArrayList<>(trims);
        for (Item item : Registries.ITEM)
            if (item instanceof ElytraItem) newTrimmable.add(item.getRegistryEntry());
        var mutable = new HashMap<>(tagEntries);
        mutable.put(ItemTags.TRIMMABLE_ARMOR, newTrimmable);
        tagEntriesRef.set(mutable);
    }
}

package dev.kikugie.elytratrims.mixin.common;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import dev.kikugie.elytratrims.common.config.ConfigTesters;
import dev.kikugie.elytratrims.common.plugin.MixinConfigurable;
import dev.kikugie.elytratrims.common.plugin.RequireTest;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

/**
 * Marks all elytra items as trimmable on the server. It's done with a mixin instead of a json file to account for modded elytras. If an unknown item is present in that file, it's ignored entirely.
 */
@MixinConfigurable
@RequireTest(ConfigTesters.Trims.class)
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
            if (shouldBeTrimmable(item)) newTrimmable.add(item.getRegistryEntry());
        var mutable = new HashMap<>(tagEntries);
        mutable.put(ItemTags.TRIMMABLE_ARMOR, newTrimmable);
        tagEntriesRef.set(mutable);
    }

    @Unique
    private static boolean shouldBeTrimmable(Item item) {
        if (item instanceof ElytraItem) return true;
        // Blame BetterEnd for being "not like the others"
        return Registries.ITEM.getId(item).getPath().contains("elytra");
    }
}

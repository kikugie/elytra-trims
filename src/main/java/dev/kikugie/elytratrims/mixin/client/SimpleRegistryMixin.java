package dev.kikugie.elytratrims.mixin.client;

import com.google.common.collect.ImmutableList;
import dev.kikugie.elytratrims.client.ETClient;
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

/**
 * Informs the client side if elytra can be trimmed. It will be marked as "trimmable" by the server mixin to make sure trims render. {@link ETClient#isTrimmable} controls whenever the elytra icon will be displayed in the smithing table GUI.
 */
@Mixin(value = SimpleRegistry.class, priority = 900)
public class SimpleRegistryMixin {
    @SuppressWarnings("deprecation")
    @Inject(method = "populateTags", at = @At("HEAD"))
    private void markElytraAsTrimmable(Map<TagKey<Item>, List<RegistryEntry<Item>>> tagEntries, CallbackInfo ci) {
        List<RegistryEntry<Item>> trims = tagEntries.get(ItemTags.TRIMMABLE_ARMOR);
        if (trims == null)
            return;
        ETClient.isTrimmable = trims.contains(Items.ELYTRA.getRegistryEntry());
    }
}

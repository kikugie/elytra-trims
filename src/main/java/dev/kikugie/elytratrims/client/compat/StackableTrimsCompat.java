package dev.kikugie.elytratrims.client.compat;

import net.minecraft.item.ItemStack;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.registry.DynamicRegistryManager;

import java.util.Collections;
import java.util.List;

/**
 * Compatibility for <a href="https://modrinth.com/mod/stackable-trims">Stackable Trims mod</a><br>
 * Separate class is needed to avoid class loading errors when the mod is not present.
 */
public class StackableTrimsCompat {
    public static List<ArmorTrim> getTrims(DynamicRegistryManager registryManager, ItemStack stack) {
        //#if FABRIC == 1
        return io.github.apfelrauber.stacked_trims.ArmorTrimList.getTrims(registryManager, stack).orElse(Collections.emptyList());
        //#else
        //$$ // Stackable Trims is not available on forge anyway
        //$$ return Collections.emptyList();
        //#endif
    }
}

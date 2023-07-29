package dev.kikugie.elytratrims.compat;

import io.github.apfelrauber.stacked_trims.ArmorTrimList;
import net.minecraft.item.ItemStack;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.registry.DynamicRegistryManager;

import java.util.List;
import java.util.Optional;

/**
 * Compatibility for <a href="https://modrinth.com/mod/stackable-trims">Stackable Trims mod</a><br>
 * Separate class is needed to avoid class loading errors when the mod is not present.
 */
public class StackableTrimsList {
    public static Optional<List<ArmorTrim>> getTrims(DynamicRegistryManager registryManager, ItemStack stack) {
        return ArmorTrimList.getTrims(registryManager, stack);
    }
}

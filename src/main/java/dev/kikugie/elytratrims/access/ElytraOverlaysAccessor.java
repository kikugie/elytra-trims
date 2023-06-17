package dev.kikugie.elytratrims.access;

import com.mojang.datafixers.util.Pair;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.DyeColor;

import java.util.List;

public interface ElytraOverlaysAccessor {
    List<Pair<RegistryEntry<BannerPattern>, DyeColor>> getPatterns();

    int getColor();
}

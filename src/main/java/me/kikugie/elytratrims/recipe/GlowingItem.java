package me.kikugie.elytratrims.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

public interface GlowingItem {
    default boolean hasGlow(ItemStack stack) {
        NbtCompound nbtCompound = stack.getSubNbt("display");
        return nbtCompound != null && nbtCompound.contains("glow", NbtElement.BYTE_TYPE) && nbtCompound.getBoolean("glow");
    }

    default void removeGlow(ItemStack stack) {
        NbtCompound nbtCompound = stack.getSubNbt("display");
        if (nbtCompound != null && nbtCompound.contains("color")) {
            nbtCompound.remove("glow");
        }

    }

    default void setGlow(ItemStack stack) {
        stack.getOrCreateSubNbt("display").putBoolean("glow", true);
    }
}

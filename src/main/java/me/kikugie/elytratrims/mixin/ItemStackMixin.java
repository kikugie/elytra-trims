package me.kikugie.elytratrims.mixin;

import com.mojang.datafixers.util.Pair;
import me.kikugie.elytratrims.ElytraTrimsMod;
import me.kikugie.elytratrims.access.ElytraOverlaysAccessor;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.DyeColor;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements ElytraOverlaysAccessor {
    private static final String PATTERNS_KEY = "Patterns";
    private static final String BASE_COLOR_KEY = "Base";
    @Nullable
    private List<Pair<RegistryEntry<BannerPattern>, DyeColor>> patterns;
    @Nullable
    private Integer color;

    @Shadow
    public abstract @Nullable NbtCompound getNbt();

    @Inject(method = "setNbt", at = @At("HEAD"))
    private void resetPatternsOnNbt(CallbackInfo ci) {
        patterns = null;
        color = null;
    }

    @Override
    public List<Pair<RegistryEntry<BannerPattern>, DyeColor>> getPatterns() {
        if (patterns == null) {
            NbtCompound nbtCompound = getNbt();
            if (nbtCompound == null || !nbtCompound.contains(PATTERNS_KEY, NbtElement.LIST_TYPE)) return List.of();

            NbtList nbtList = nbtCompound.getList(PATTERNS_KEY, NbtElement.COMPOUND_TYPE);
            DyeColor baseColor = nbtCompound.contains(BASE_COLOR_KEY) ? DyeColor.byId(nbtCompound.getInt(BASE_COLOR_KEY)) : DyeColor.WHITE;
            patterns = BannerBlockEntity.getPatternsFromNbt(baseColor, nbtList);
        }

        return patterns;
    }

    @Override
    public int getColor() {
        if (color == null) {
            color = ElytraTrimsMod.DYEABLE.hasColor((ItemStack) (Object) this) ? ElytraTrimsMod.DYEABLE.getColor((ItemStack) (Object) this) : 0;
        }

        return color;
    }
}

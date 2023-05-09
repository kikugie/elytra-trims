package me.kikugie.elytratrims.mixin;

import com.mojang.datafixers.util.Pair;
import me.kikugie.elytratrims.ElytraTrimsMod;
import me.kikugie.elytratrims.access.ElytraOverlaysAccessor;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.DyeColor;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements ElytraOverlaysAccessor {
    private static final String BASE_COLOR_KEY = "Base";
    @Nullable
    private List<Pair<RegistryEntry<BannerPattern>, DyeColor>> patterns;
    @Nullable
    private Integer color;

    @Inject(method = "setNbt", at = @At("HEAD"))
    private void resetPatternsOnNbt(CallbackInfo ci) {
        patterns = null;
        color = null;
    }

    @Override
    public List<Pair<RegistryEntry<BannerPattern>, DyeColor>> getPatterns() {
        if (patterns == null) {
            NbtList list = BannerBlockEntity.getPatternListNbt((ItemStack) (Object) this);
            if (list == null) {
                patterns = List.of();
                return patterns;
            }

            NbtCompound nbt = BlockItem.getBlockEntityNbt((ItemStack) (Object) this);
            assert nbt != null;
            DyeColor baseColor = nbt.contains(BASE_COLOR_KEY) ? DyeColor.byId(nbt.getInt(BASE_COLOR_KEY)) : DyeColor.WHITE;
            patterns = BannerBlockEntity.getPatternsFromNbt(baseColor, list);
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

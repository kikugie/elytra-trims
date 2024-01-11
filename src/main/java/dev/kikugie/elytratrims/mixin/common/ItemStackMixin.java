package dev.kikugie.elytratrims.mixin.common;

import com.mojang.datafixers.util.Pair;
import dev.kikugie.elytratrims.common.ETServer;
import dev.kikugie.elytratrims.common.access.ElytraOverlaysAccessor;
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
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@SuppressWarnings("DataFlowIssue")
@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements ElytraOverlaysAccessor {
    @Unique
    private static final String BASE_COLOR_KEY = "Base";
    @Unique
    @Nullable
    private List<Pair<RegistryEntry<BannerPattern>, DyeColor>> patterns;
    @Unique
    @Nullable
    private Integer color;

    @Inject(method = "setNbt", at = @At("HEAD"))
    private void resetPatternsOnNbt(CallbackInfo ci) {
        this.patterns = null;
        this.color = null;
    }

    @Override
    public List<Pair<RegistryEntry<BannerPattern>, DyeColor>> elytra_trims$getPatterns() {
        if (this.patterns == null) {
            NbtList list = BannerBlockEntity.getPatternListNbt((ItemStack) (Object) this);
            if (list == null) {
                this.patterns = List.of();
                return this.patterns;
            }

            NbtCompound nbt = BlockItem.getBlockEntityNbt((ItemStack) (Object) this);
            assert nbt != null;
            DyeColor baseColor = nbt.contains(BASE_COLOR_KEY) ? DyeColor.byId(nbt.getInt(BASE_COLOR_KEY)) : DyeColor.WHITE;
            this.patterns = BannerBlockEntity.getPatternsFromNbt(baseColor, list);
        }

        return this.patterns;
    }

    @Override
    public int elytra_trims$getColor() {
        if (this.color == null) {
            this.color = ETServer.DYEABLE.hasColor((ItemStack) (Object) this) ? ETServer.DYEABLE.getColor((ItemStack) (Object) this) : 0;
        }

        return this.color;
    }
}

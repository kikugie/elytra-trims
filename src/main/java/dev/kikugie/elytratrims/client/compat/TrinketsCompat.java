package dev.kikugie.elytratrims.client.compat;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class TrinketsCompat {
    @Nullable
    public static ItemStack getTrinketElytra(LivingEntity entity) {
        Optional<TrinketComponent> maybe = TrinketsApi.getTrinketComponent(entity);
        if (maybe.isEmpty()) return null;

        TrinketComponent component = maybe.get();
        for (Pair<SlotReference, ItemStack> pair : component.getAllEquipped()) {
            ItemStack stack = pair.getRight();
            if (pair.getLeft().inventory().getSlotType().getName().equals("cape"))
                return stack;
        }
        return null;
    }
}
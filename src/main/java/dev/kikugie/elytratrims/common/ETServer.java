package dev.kikugie.elytratrims.common;

import dev.kikugie.elytratrims.common.access.ElytraOverlaysAccessor;
import dev.kikugie.elytratrims.common.config.ServerConfigs;
import dev.kikugie.elytratrims.common.recipe.GlowingItem;
import net.minecraft.block.LeveledCauldronBlock;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.item.DyeableItem;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;

public class ETServer {
    public static DyeableItem DYEABLE;
    public static GlowingItem GLOWING;
    public static CauldronBehavior CLEAN_ELYTRA;

    public static void init() {
        DYEABLE = new DyeableItem() {
        };
        GLOWING = new GlowingItem() {
        };

        CLEAN_ELYTRA = (state, world, pos, player, hand, stack) -> {
            boolean glowRemoval = false;
            boolean bannerRemoval = false;
            boolean dyeRemoval = false;

            if (GLOWING.hasGlow(stack)) {
                GLOWING.removeGlow(stack);
                glowRemoval = true;
            }
            if (DYEABLE.hasColor(stack)) {
                DYEABLE.removeColor(stack);
                dyeRemoval = true;
            }
            if (!((ElytraOverlaysAccessor) (Object) stack).elytra_trims$getPatterns().isEmpty()) {
                NbtCompound nbt = BlockItem.getBlockEntityNbt(stack);
                if (nbt != null) nbt.remove("Patterns");
                bannerRemoval = true;
            }
            if (glowRemoval || bannerRemoval || dyeRemoval) {
                player.incrementStat(Stats.CLEAN_ARMOR);
                LeveledCauldronBlock.decrementFluidLevel(state, world, pos);
                return ActionResult.success(world.isClient);
            }
            return ActionResult.PASS;
        };

        if (ServerConfigs.getConfig().cleanableElytra) CauldronBehavior.WATER_CAULDRON_BEHAVIOR
                /*? if >1.20.2 */
                /*.map()*/
                .put(Items.ELYTRA, CLEAN_ELYTRA::interact);
    }
}
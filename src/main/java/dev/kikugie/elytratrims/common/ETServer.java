package dev.kikugie.elytratrims.common;

import dev.kikugie.elytratrims.common.access.ElytraOverlaysAccessor;
import dev.kikugie.elytratrims.common.config.ETMixinConfig;
import dev.kikugie.elytratrims.common.config.ETServerConfig;
import dev.kikugie.elytratrims.common.plugin.ModStatus;
import dev.kikugie.elytratrims.common.recipe.GlowingItem;
import net.minecraft.block.LeveledCauldronBlock;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.item.DyeableItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;

public class ETServer {
    public static final DyeableItem DYEABLE = new DyeableItem() {
    };
    public static final GlowingItem GLOWING = new GlowingItem() {
    };
    public static CauldronBehavior CLEAN_ELYTRA;
    private static ETServerConfig config;
    private static ETMixinConfig mixinConfig;

    public static void configInit() {
        if (config == null) config = ModStatus.isServer || ModStatus.isDev
                ? ETServerConfig.load()
                : ETServerConfig.create();
        if (mixinConfig == null)
            mixinConfig = ETMixinConfig.load();
    }

    public static void init() {
        CLEAN_ELYTRA = (state, world, pos, player, hand, stack) -> {
            Item item = stack.getItem();
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

        CauldronBehavior.WATER_CAULDRON_BEHAVIOR
                /*? if >1.20.2 */
                /*.map()*/
                .put(Items.ELYTRA, CLEAN_ELYTRA::interact);
    }

    public static ETServerConfig getConfig() {
        return config;
    }
    public static ETMixinConfig getMixinConfig() {
        return mixinConfig;
    }
}
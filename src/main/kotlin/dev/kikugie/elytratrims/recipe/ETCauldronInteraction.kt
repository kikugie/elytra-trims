package dev.kikugie.elytratrims.recipe

import dev.kikugie.elytratrims.item.ETFlag
import dev.kikugie.elytratrims.item.banner
import dev.kikugie.elytratrims.item.color
import dev.kikugie.elytratrims.item.flags
import dev.kikugie.elytratrims.resource.ETTags
import net.minecraft.core.BlockPos
import net.minecraft.core.cauldron.CauldronInteraction
import net.minecraft.stats.Stats
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.ItemUtils
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.LayeredCauldronBlock
import net.minecraft.world.level.block.state.BlockState

object ETCauldronInteraction : CauldronInteraction {
    @JvmStatic fun register() {
        val map = CauldronInteraction.WATER.map
        // TODO: Multi-item support
        val interaction = if (Items.ELYTRA !in map) this
        else composeInteractions(map[Items.ELYTRA]!!)
        map[Items.ELYTRA] = interaction
    }

    override fun interact(state: BlockState, level: Level, pos: BlockPos, player: Player, hand: InteractionHand, stack: ItemStack): InteractionResult {
        val cleaned = when {
            !stack.`is`(ETTags.ELYTRA_DECORATEABLE) -> false
            stack.flags[ETFlag.GLOW] -> stack.replace(player, hand) { flags[ETFlag.GLOW] = false }
            stack.color.value.isVisible() -> stack.replace(player, hand) { color.clear() }
            stack.banner.base != null -> stack.replace(player, hand) { banner.clear() }
            stack.banner.patterns.isNotEmpty() -> stack.replace(player, hand) { banner.clear() }
            stack.flags[ETFlag.GATEWAY] -> stack.replace(player, hand) { flags[ETFlag.GATEWAY] = false }
            else -> true
        }
        if (cleaned && !level.isClientSide) awardStatAndDrain(state, level, pos, player)
        return if (cleaned) InteractionResult.SUCCESS else InteractionResult.TRY_WITH_EMPTY_HAND
    }

    @Suppress("SameReturnValue")
    private inline fun ItemStack.replace(player: Player, hand: InteractionHand, modification: ItemStack.() -> Unit): Boolean {
        if (player.level().isClientSide) return true
        val copy = copy().apply(modification).let { ItemUtils.createFilledResult(this, player, it, false) }
        player.setItemInHand(hand, copy)
        return true
    }

    private fun awardStatAndDrain(state: BlockState, level: Level, pos: BlockPos, player: Player) {
        player.awardStat(Stats.CLEAN_ARMOR)
        LayeredCauldronBlock.lowerFillLevel(state, level, pos)
    }

    private fun composeInteractions(existing: CauldronInteraction) = CauldronInteraction { state, world, pos, player, hand, stack ->
        val result: InteractionResult = existing.interact(state, world, pos, player, hand, stack)
        if (result.consumesAction()) result else interact(state, world, pos, player, hand, stack)
    }
}
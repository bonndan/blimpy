package com.github.bonndan.blimpy.blimp.entity.bombbay

import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.item.PrimedTnt
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Items
import net.minecraft.world.level.gameevent.GameEvent
import net.neoforged.api.distmarker.Dist
import net.neoforged.api.distmarker.OnlyIn
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent

object BombBay {

    fun launchBomb(player: ServerPlayer) {

        val itemStack = player.getItemInHand(InteractionHand.MAIN_HAND)
        if (!itemStack.`is`(Items.TNT) || itemStack == null || itemStack.isEmpty) {
            return
        }

        val level = player.level()
        val x = player.x
        val y = player.y - 1
        val z = player.z

        val tnt = PrimedTnt(level, x, y, z, player)
        level.addFreshEntity(tnt)
        level.gameEvent(player, GameEvent.PRIME_FUSE, player.blockPosition())

        if (!player.abilities.instabuild) {
            itemStack.shrink(1)
            if (itemStack.isEmpty) {
                player.getInventory().removeItem(itemStack)
            }
        }
    }

    fun launchBomb(
        event: PlayerInteractEvent.RightClickItem,
        player: Player,
    ): Boolean {
        val tnt = event.itemStack
        if (!tnt.`is`(Items.TNT) || tnt == null || tnt.isEmpty) {
            return false
        }

        val level = event.level
        level.playSound(player, player.x, player.y, player.z, SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0f, 1.0f)
        level.playSound(
            player,
            player.x,
            player.y,
            player.z,
            SoundEvents.IRON_TRAPDOOR_OPEN,
            SoundSource.BLOCKS,
            1.0f,
            1.0f
        )
        return true
    }

}
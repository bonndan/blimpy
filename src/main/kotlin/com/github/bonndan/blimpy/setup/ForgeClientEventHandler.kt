package com.github.bonndan.blimpy.setup

import com.github.bonndan.blimpy.BlimpyMod
import com.github.bonndan.blimpy.blimp.entity.BlimpEntity
import com.github.bonndan.blimpy.blimp.entity.bombbay.BombBay
import com.github.bonndan.blimpy.blimp.entity.bombbay.BombPacket
import com.github.bonndan.blimpy.blimp.entity.bombbay.BombPacketHandler
import net.minecraft.client.Minecraft
import net.minecraft.client.player.LocalPlayer
import net.minecraft.world.entity.player.Player
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.ClientTickEvent
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent


/**
 * Forge-wide event bus
 */
@EventBusSubscriber(modid = BlimpyMod.MOD_ID, value = [Dist.CLIENT])
object ForgeClientEventHandler {

    @SubscribeEvent
    fun onClientTick(@Suppress("UNUSED_PARAMETER") event: ClientTickEvent.Post) {
        val minecraft = Minecraft.getInstance()
        val player = minecraft.player

        if (minecraft.screen != null) {
            return
        }

        if (player !is LocalPlayer || !player.isPassenger) {
            return
        }

        val vehicle = player.vehicle

        if (vehicle !is BlimpEntity) {
            return
        }

        if (minecraft.options.keySprint.isDown) {
            vehicle.sink()
        }

        if (minecraft.options.keyJump.isDown) {
            vehicle.rise()
        }
    }

    @SubscribeEvent(receiveCanceled = true)
    fun onPlayerRightClickEvent(event: PlayerInteractEvent.RightClickItem) {

        val player = event.entity
        if (!isRidingTheBlimp(player)) {
            return
        }

        if (!BombBay.launchBomb(event, player)) {
            return
        }

        BombPacketHandler.send(BombPacket(player.vehicle!!.id))
    }

    fun isRidingTheBlimp(player: Player?): Boolean {

        if (player !is LocalPlayer || !player.isPassenger) {
            return false
        }

        return player.vehicle is BlimpEntity
    }
}
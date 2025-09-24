package com.github.bonndan.blimpy.setup

import com.github.bonndan.blimpy.BlimpyMod
import com.github.bonndan.blimpy.blimp.entity.BlimpEntity
import com.github.bonndan.blimpy.locomotive.entity.LocomotiveEntity
import com.github.bonndan.blimpy.blimp.entity.bombbay.BombBay
import com.github.bonndan.blimpy.blimp.entity.bombbay.BombPacket
import com.github.bonndan.blimpy.blimp.entity.bombbay.BombPacketHandler
import net.minecraft.client.Minecraft
import net.minecraft.client.player.LocalPlayer
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Player
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.InputEvent
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent
import org.lwjgl.glfw.GLFW


/**
 * Forge-wide event bus
 */
@EventBusSubscriber(modid = BlimpyMod.MOD_ID, value = [Dist.CLIENT])
object ForgeClientEventHandler {

    val BEAM_LOCATION: ResourceLocation =
        ResourceLocation.fromNamespaceAndPath(BlimpyMod.MOD_ID, "textures/entity/beacon_beam.png")

    @SubscribeEvent
    fun onKeyInputEvent(event: InputEvent.Key) {

        val player = Minecraft.getInstance().player

        if (event.key != GLFW.GLFW_KEY_LEFT_CONTROL && event.key != GLFW.GLFW_KEY_SPACE) {
            return
        }

        if (!isRidingTheBlimp(player)) {
            return
        }

        if (vehicle is LocomotiveEntity) {
            when (event.key) {
                GLFW.GLFW_KEY_SPACE -> vehicle.throttleUp()
                GLFW.GLFW_KEY_LEFT_CONTROL -> vehicle.throttleDown()
            }
        }

        val vehicle = player!!.vehicle as BlimpEntity
        when (event.key) {
            GLFW.GLFW_KEY_LEFT_CONTROL -> vehicle.sink()
            GLFW.GLFW_KEY_SPACE -> vehicle.rise()
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
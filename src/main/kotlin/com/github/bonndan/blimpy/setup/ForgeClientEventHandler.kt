package com.github.bonndan.blimpy.setup

import com.github.bonndan.blimpy.BlimpyMod
import com.github.bonndan.blimpy.blimp.entity.BlimpEntity
import net.minecraft.client.Minecraft
import net.minecraft.client.player.LocalPlayer
import net.minecraft.resources.ResourceLocation
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.InputEvent
import org.lwjgl.glfw.GLFW

/**
 * Forge-wide event bus
 */
@EventBusSubscriber(modid = BlimpyMod.Companion.MOD_ID, value = [Dist.CLIENT])
object ForgeClientEventHandler {

    val BEAM_LOCATION: ResourceLocation =
        ResourceLocation.fromNamespaceAndPath(BlimpyMod.Companion.MOD_ID, "textures/entity/beacon_beam.png")

    @SubscribeEvent
    fun onKeyInputEvent(event: InputEvent.Key) {
        val player = Minecraft.getInstance().player

        if (event.key != GLFW.GLFW_KEY_LEFT_CONTROL && event.key != GLFW.GLFW_KEY_SPACE) {
            return
        }

        if (player !is LocalPlayer || !player.isPassenger) {
            return
        }

        val vehicle = player.vehicle
        if (vehicle is BlimpEntity) {
            when (event.key) {
                GLFW.GLFW_KEY_LEFT_CONTROL -> vehicle.sink()
                GLFW.GLFW_KEY_SPACE -> vehicle.rise()
            }
        }
    }

}
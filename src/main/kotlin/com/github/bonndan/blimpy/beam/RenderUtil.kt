package com.github.bonndan.blimpy.beam

import com.github.bonndan.blimpy.setup.ForgeClientEventHandler
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BeaconRenderer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.client.event.RenderLevelStageEvent
import kotlin.math.min

object RenderUtil {

    fun computeFixedDistance(target: Vec3, position: Vec3, scale: Double): Vec3 {
        var newTarget = target
        newTarget = newTarget.add(0.0, 2.0, 0.0)
        val delta = position.vectorTo(newTarget)

        // The distance from the player camera to render the element
        val dist = min(5.0, delta.length())
        return position.add(delta.normalize().scale(dist * scale))
    }

     fun renderBeam(
         matrixStack: PoseStack?,
         buffer: MultiBufferSource.BufferSource?,
         event: RenderLevelStageEvent,
         player: Player,
         color: Int
    ) {
        BeaconRenderer.renderBeaconBeam(
            matrixStack,
            buffer,
            ForgeClientEventHandler.BEAM_LOCATION,
            event.partialTick.gameTimeDeltaTicks,
            1f,
            player.level().gameTime,
            player.level().minY + 1,
            1024,
            color,
            0.1f,
            0.2f
        )
    }
}
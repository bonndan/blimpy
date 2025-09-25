package com.github.bonndan.blimpy.blimp.entity.engine

import com.github.bonndan.blimpy.blimp.entity.BlimpEntity
import com.github.bonndan.blimpy.locomotive.entity.LocomotiveEntity
import com.github.bonndan.blimpy.network.SetThrottlePacket
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.neoforge.network.PacketDistributor
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler
import net.neoforged.neoforge.network.handling.IPayloadContext
import java.util.*

/**
 * Handles event that are sent from client to server.
 */
object VehiclePacketHandler {

    @SubscribeEvent
    fun register(event: RegisterPayloadHandlersEvent) {

        val registrar = event.registrar("1")

        registrar.playBidirectional(
            SetEnginePacket.TYPE,
            SetEnginePacket.STREAM_CODEC,
            DirectionalPayloadHandler(
                { obj, operation -> handleSetEngine(obj, operation) },
                { obj, operation -> handleSetEngine(obj, operation) }
            )
        )

        registrar.playBidirectional(
            SetThrottlePacket.TYPE,
            SetThrottlePacket.STREAM_CODEC,
            DirectionalPayloadHandler(
                { obj, operation -> handleSetThrottle(obj, operation) },
                { obj, operation -> handleSetThrottle(obj, operation) }
            )
        )
    }

    fun sendToServer(payload: CustomPacketPayload) {
        PacketDistributor.sendToServer(payload)
    }

    private fun handleSetEngine(operation: SetEnginePacket, ctx: IPayloadContext) {
        ctx.enqueueWork {
            Optional.of<IPayloadContext>(ctx)
                .map { obj -> obj.player() }
                .ifPresent { serverPlayer ->
                    val loco = serverPlayer.level().getEntity(operation.locoId)
                    if (loco != null && loco.distanceTo(serverPlayer) < 6 && loco is BlimpEntity) {
                        loco.setEngineOn(operation.state)
                    }
                }
        }
    }

    private fun handleSetThrottle(operation: SetThrottlePacket, ctx: IPayloadContext) {
        ctx.enqueueWork {
            Optional.of(ctx)
                .map { it.player() }
                .ifPresent { serverPlayer ->
                    val entity = serverPlayer.level().getEntity(operation.locoId)
                    if (entity != null && entity.distanceTo(serverPlayer) < 6 && entity is LocomotiveEntity) {
                        println("set throttle = [${operation.throttle}]")
                        entity.setThrottle(operation.throttle)
                    }
                }
        }
    }
}

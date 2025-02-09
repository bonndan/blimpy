package com.github.bonndan.blimpy.blimp.entity.engine

import com.github.bonndan.blimpy.blimp.entity.BlimpEntity
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.neoforge.network.PacketDistributor
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler
import net.neoforged.neoforge.network.handling.IPayloadContext
import java.util.*

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
    }

    fun send(payload: CustomPacketPayload) {
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
}

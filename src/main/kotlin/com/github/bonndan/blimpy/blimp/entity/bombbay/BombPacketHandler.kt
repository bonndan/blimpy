package com.github.bonndan.blimpy.blimp.entity.bombbay

import com.github.bonndan.blimpy.blimp.entity.BlimpEntity
import net.minecraft.server.level.ServerPlayer
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.neoforge.network.PacketDistributor
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent
import net.neoforged.neoforge.network.handling.IPayloadContext
import net.neoforged.neoforge.network.handling.MainThreadPayloadHandler
import java.util.*

object BombPacketHandler {

    @SubscribeEvent
    fun register(event: RegisterPayloadHandlersEvent) {
        val registrar = event.registrar("1")
        registrar.playToServer(
            BombPacket.TYPE,
            BombPacket.STREAM_CODEC,
            MainThreadPayloadHandler { obj, operation -> handle(obj, operation) }
        )
    }

    fun send(payload: BombPacket) {
        PacketDistributor.sendToServer(payload)
    }

    private fun handle(operation: BombPacket, ctx: IPayloadContext) {
        ctx.enqueueWork {
            Optional.of<IPayloadContext>(ctx)
                .map { obj -> obj.player() }
                .ifPresent { serverPlayer ->
                    val blimp = serverPlayer.level().getEntity(operation.id)
                    if (serverPlayer is ServerPlayer && blimp != null && blimp.distanceTo(serverPlayer) < 6 && blimp is BlimpEntity) {
                        BombBay.launchBomb(serverPlayer)
                    }
                }
        }
    }
}

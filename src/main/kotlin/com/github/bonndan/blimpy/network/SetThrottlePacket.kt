package com.github.bonndan.blimpy.network

import com.github.bonndan.blimpy.BlimpyMod
import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation

class SetThrottlePacket(val locoId: Int, val throttle: Float) : CustomPacketPayload {

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
        return TYPE
    }

    companion object {

        private val LOCATION = ResourceLocation.fromNamespaceAndPath(BlimpyMod.MOD_ID, "blimpy_throttle_packet")

        val TYPE = CustomPacketPayload.Type<SetThrottlePacket>(LOCATION)

        val STREAM_CODEC: StreamCodec<ByteBuf?, SetThrottlePacket> =
            StreamCodec.composite<ByteBuf, SetThrottlePacket, Int, Float>(
                ByteBufCodecs.VAR_INT, SetThrottlePacket::locoId,
                ByteBufCodecs.FLOAT, SetThrottlePacket::throttle
            ) { locoId, throttle -> SetThrottlePacket(locoId, throttle) }
    }
}


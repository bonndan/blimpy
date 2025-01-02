package com.github.bonndan.blimpy.blimp.container

import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.inventory.ContainerData

open class DataAccessor(private var containerData: ContainerData) : ContainerData {

    /**
     * Server side serialisation
     */
    fun write(buffer: FriendlyByteBuf) {
        for (i in 0 until containerData.count) {
            buffer.writeInt(containerData[i])
        }
    }

    fun getEntityUUID(): Int? = if (containerData.count == 0) { null } else containerData[0]

    override fun get(i: Int): Int {
        return containerData[i]
    }

    override fun set(i: Int, j: Int) {
        containerData[i] = j
    }

    override fun getCount(): Int {
        return containerData.count
    }

    fun getRawData(): ContainerData {
        return containerData
    }
}
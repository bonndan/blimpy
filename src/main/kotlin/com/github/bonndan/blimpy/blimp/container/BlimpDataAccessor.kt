package com.github.bonndan.blimpy.blimp.container

import net.minecraft.world.inventory.ContainerData
import java.util.function.BooleanSupplier
import java.util.function.IntSupplier

class BlimpDataAccessor(containerData: ContainerData) : DataAccessor(containerData) {

    val isLit: Boolean
        get() {
            return getRawData()[1] == 1
        }

    val isOn: Boolean
        get() {
            return getRawData()[2] == 1
        }

    fun getBurnProgress(): Int {
        return getRawData()[15]
    }

    class Builder {

        private val arr = SupplierIntArray(20)

        fun withId(id: Int): Builder {
            arr[0] = id
            return this
        }

        fun withLit(lit: BooleanSupplier): Builder {
            arr.setSupplier(1) { if (lit.asBoolean) 1 else -1 }
            return this
        }

        fun withOn(on: BooleanSupplier): Builder {
            arr.setSupplier(2) { if (on.asBoolean) 1 else -1 }
            return this
        }

        fun withBurnProgressPct(burnProgress: IntSupplier): Builder {
            arr.setSupplier(15, burnProgress)
            return this
        }

        fun build(): BlimpDataAccessor {
            return BlimpDataAccessor(this.arr)
        }
    }
}
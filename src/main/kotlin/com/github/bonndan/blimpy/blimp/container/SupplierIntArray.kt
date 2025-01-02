package com.github.bonndan.blimpy.blimp.container

import net.minecraft.world.inventory.ContainerData
import java.util.function.IntSupplier

class SupplierIntArray(count: Int) : ContainerData {

    private val suppliers = arrayOfNulls<IntSupplier>(count)

    override fun get(i: Int): Int {
        return if (suppliers[i] == null) 0 else suppliers[i]!!.asInt
    }

    override fun set(i: Int, j: Int) {
        suppliers[i] = IntSupplier { j }
    }

    fun setSupplier(i: Int, supplier: IntSupplier?) {
        suppliers[i] = supplier
    }

    override fun getCount(): Int {
        return suppliers.size
    }
}
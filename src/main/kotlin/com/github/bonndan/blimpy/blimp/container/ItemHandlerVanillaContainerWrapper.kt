package com.github.bonndan.blimpy.blimp.container

import net.minecraft.world.Container
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.items.ItemStackHandler
import kotlin.ranges.until

/**
 *
 */
interface ItemHandlerVanillaContainerWrapper : Container {
    
     fun getRawHandler(): ItemStackHandler

     override fun getContainerSize(): Int {
        return getRawHandler().slots
    }

    override fun isEmpty(): Boolean {
        for (i in 0 until getRawHandler().slots) {
            if (!getRawHandler().getStackInSlot(i).isEmpty) {
                return false
            }
        }
        return true
    }

    override fun getItem(pIndex: Int): ItemStack {
        return getRawHandler().getStackInSlot(pIndex)
    }

    override fun removeItem(pIndex: Int, pCount: Int): ItemStack {
        return getRawHandler().extractItem(pIndex, pCount, false)
    }

    override fun removeItemNoUpdate(pIndex: Int): ItemStack {
        val stack = getRawHandler().getStackInSlot(pIndex)
        getRawHandler().setStackInSlot(pIndex, ItemStack.EMPTY)
        return stack
    }

    override fun setItem(pIndex: Int, pStack: ItemStack) {
        getRawHandler().setStackInSlot(pIndex, pStack)
    }

    override fun setChanged() {
    }

    override fun clearContent() {
        for (i in 0 until getRawHandler().slots) {
            getRawHandler().setStackInSlot(i, ItemStack.EMPTY)
        }
    }
}

package com.github.bonndan.blimpy.blimp.entity.engine

import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.FuelValues

class FueledEngine(saveStateCallback: SaveStateCallback, val fuelValues: FuelValues) : Engine(saveStateCallback) {

    override fun calculateBurnTimeOfNextItem(stack: ItemStack): Int {

        if (stack.isEmpty) {
            return 0
        }

        return stack.item.getBurnTime(stack, null, fuelValues)
    }

    override fun getEmissions(): Emissions {
        return SmokeGenerator
    }

    override fun isItemValid(slot: Int, stack: ItemStack): Boolean {

        return fuelValues.isFuel(stack)
    }
}
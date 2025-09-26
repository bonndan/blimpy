package com.github.bonndan.blimpy.blimp.entity.engine


import net.minecraft.core.RegistryAccess
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.items.ItemStackHandler
import kotlin.jvm.optionals.getOrNull

/**
 * Engine that uses some kind of fuel.
 */
abstract class Engine(private var saveStateCallback: SaveStateCallback) : ItemStackHandler(1) {

    private var engineOn: Boolean = false
    private var remainingBurnTime: Int = 0
    private var totalBurnTime: Int = 0

    override fun setStackInSlot(slot: Int, stack: ItemStack) {
        super.setStackInSlot(slot, stack)
        val burnTime = calculateBurnTimeOfNextItem(stack)
        load(burnTime)
        saveState(engineOn, remainingBurnTime)
    }

    fun getBurnProgressPct(): Int {
        return (remainingBurnTime.toFloat() / totalBurnTime.toFloat() * 100).toInt()
    }

    fun isLit(): Boolean = remainingBurnTime > 0 && isOn()

    fun setEngineOn(state: Boolean) {
        this.engineOn = state
        saveState(engineOn, remainingBurnTime)
    }

    fun isOn(): Boolean {
        return engineOn
    }

    /**
     * Consume an item of fuel
     * @return number of base ticks the fuel burns for
     */
    fun tickFuel(): Int {

        if (!isOn()) {
            return 0
        }

        if (remainingBurnTime > 0) {
            remainingBurnTime--
        } else {
            tryConsumeFuel()
        }

        saveState(engineOn, remainingBurnTime)
        return remainingBurnTime
    }

    private fun tryConsumeFuel(): Int {

        val stack = getStackInSlot(0)
        val burnTime = calculateBurnTimeOfNextItem(stack)

        if (burnTime > 0) {
            stack.shrink(1)
            load(burnTime)
        }

        return burnTime
    }

    private fun load(burnTime: Int) {
        this.totalBurnTime = burnTime
        this.remainingBurnTime = burnTime
    }

    fun readAdditionalSaveData(valueInput: ValueInput, registryAccess: RegistryAccess) {
        setBurnTime(valueInput.getInt(BURN).orElse(0))
        setTotalBurnTime(valueInput.getInt(TOTAL_BURN_CAPACITY).orElse(0))
        setEngineOn(valueInput.getBooleanOr(ENGINE_ON,false))
        valueInput.childOrEmpty(FUEL_ITEMS)
    }

    fun addAdditionalSaveData(valueOutput: ValueOutput) {
        valueOutput.putInt(BURN, remainingBurnTime)
        valueOutput.putInt(TOTAL_BURN_CAPACITY, totalBurnTime)
        valueOutput.putBoolean(ENGINE_ON, engineOn)
        valueOutput.putChild(FUEL_ITEMS, this)
    }

    abstract override fun isItemValid(slot: Int, stack: ItemStack): Boolean

    abstract fun calculateBurnTimeOfNextItem(stack: ItemStack): Int

    private fun setBurnTime(burnTime: Int) {
        this.remainingBurnTime = burnTime
    }

    private fun setTotalBurnTime(totalBurnTime: Int) {
        this.totalBurnTime = totalBurnTime
    }

    /**
     * CLient side updates only.
     */
    fun setRemainingBurnTime(remainingBurnTime: Int) {
        this.remainingBurnTime = remainingBurnTime
    }

    fun makeEmissions(level: Level, emitterPos: Vec3, entityPos: Vec3, oldEntityPos: Vec3) {

        if (!level.isClientSide) return
        if (!isLit()) return

        getEmissions().makeEmissions(level, emitterPos, entityPos, oldEntityPos)
    }

    protected abstract fun getEmissions(): Emissions

    private fun saveState(engineOn: Boolean, remainingBurnTime: Int) {
        saveStateCallback.saveState(engineOn, remainingBurnTime)
    }

    companion object {
        const val BURN = "burn"
        const val TOTAL_BURN_CAPACITY = "burn_capacity"
        const val ENGINE_ON = "eo"
        const val FUEL_ITEMS = "fuelItems"
    }

}
package com.github.bonndan.blimpy.blimp.entity.engine

import net.minecraft.world.item.ItemStack
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class EngineTest {

    private class TestEngine(saveStateCallback: SaveStateCallback) : Engine(saveStateCallback) {
        override fun isItemValid(slot: Int, stack: ItemStack): Boolean = false

        override fun calculateBurnTimeOfNextItem(stack: ItemStack): Int = 0

        override fun getEmissions(): Emissions = SmokeGenerator
    }

    var engineStateUpdate = false
    var remainingBurnTimeUpdate = 0

    private val saveStateCallback: SaveStateCallback = object : SaveStateCallback {

        override fun saveState(engineState: Boolean, remainingBurnTime: Int) {
            engineStateUpdate = engineState
            remainingBurnTimeUpdate = remainingBurnTime
        }
    }

    lateinit var engine: Engine

    @BeforeEach
    fun init() {
        engine = TestEngine(saveStateCallback = saveStateCallback)
    }

    @Test
    fun `when engine is on and has remaining burn time then it is lit`() {

        engine.setRemainingBurnTime(100)
        engine.setEngineOn(true)

        assertThat(engine.isLit()).isTrue
    }

    @Test
    fun `when engine is off and has remaining burn time then it is not lit`() {

        engine.setRemainingBurnTime(100)
        engine.setEngineOn(false)

        assertThat(engine.isLit()).isFalse
    }

    @Test
    fun `when engine is on and has no remaining burn time then it is not lit`() {

        engine.setRemainingBurnTime(0)
        engine.setEngineOn(true)

        assertThat(engine.isLit()).isFalse
    }

}
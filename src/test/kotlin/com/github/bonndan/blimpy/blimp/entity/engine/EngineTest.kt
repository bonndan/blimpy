package com.github.bonndan.blimpy.blimp.entity.engine

import net.minecraft.DetectedVersion.BUILT_IN
import net.minecraft.SharedConstants
import net.minecraft.server.Bootstrap
import net.minecraft.world.level.block.entity.FuelValues
import net.neoforged.fml.ModLoadingIssue
import net.neoforged.fml.loading.LoadingModList
import net.neoforged.fml.loading.moddiscovery.ModFile
import net.neoforged.fml.loading.moddiscovery.ModInfo
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock

class EngineTest {

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
        SharedConstants.setVersion(BUILT_IN)

        //fix LoadingModList in Bootstrap
        LoadingModList.of(
            listOf<ModFile>(),
            listOf<ModFile>(),
            listOf<ModFile>(),
            mutableListOf<ModInfo>(),
            mutableListOf<ModLoadingIssue>(),
            mapOf<ModInfo, List<ModInfo>>()
        )

        Bootstrap.bootStrap()
        engine = FueledEngine(saveStateCallback = saveStateCallback, fuelValues = mock<FuelValues>())
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
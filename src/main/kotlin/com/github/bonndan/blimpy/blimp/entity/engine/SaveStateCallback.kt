package com.github.bonndan.blimpy.blimp.entity.engine

interface SaveStateCallback {

    fun saveState(engineState: Boolean, remainingBurnTime: Int)
}

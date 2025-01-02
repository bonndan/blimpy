package com.github.bonndan.blimpy.blimp.model

import net.minecraft.client.renderer.entity.state.BoatRenderState
import net.minecraft.world.item.DyeColor

class BlimpRenderState : BoatRenderState() {

    private var color: Int? = null

    fun getColorId(): Int {
        return color ?: DyeColor.RED.id
    }

    fun setColorId(color: Int?) {
        this.color = color
    }
}
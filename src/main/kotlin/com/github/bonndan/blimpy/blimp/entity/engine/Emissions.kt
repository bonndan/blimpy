package com.github.bonndan.blimpy.blimp.entity.engine

import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3

/**
 * Sound and particle effect of running engines/vehicles.
 */
interface Emissions {

    /**
     * @param level the world
     * @param emitterPos where to emit the effects (e.g. top of an exhaust)
     * @param entityPos current position of the entity
     * @param oldEntityPos previous position ot the entity (before movement)
     */
    fun makeEmissions(level: Level, emitterPos: Vec3, entityPos: Vec3, oldEntityPos: Vec3)
}
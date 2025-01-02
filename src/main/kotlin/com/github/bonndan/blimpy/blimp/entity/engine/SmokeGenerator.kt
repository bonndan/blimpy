package com.github.bonndan.blimpy.blimp.entity.engine

import net.minecraft.core.particles.ParticleTypes
import net.minecraft.core.particles.SimpleParticleType
import net.minecraft.util.RandomSource
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import java.util.function.Supplier

object SmokeGenerator : Emissions {

    private const val smokeChance = 0.3

    override fun makeEmissions(level: Level, emitterPos: Vec3, entityPos: Vec3, oldEntityPos: Vec3) {

        val random: RandomSource = level.random

        if (random.nextFloat() < smokeChance) {
            for (i in 0 until random.nextInt(2) + 2) {
                makeParticles(level, emitterPos, entityPos, oldEntityPos)
            }
        }
    }

    private fun makeParticles(level: Level, pos: Vec3, currentPos: Vec3, oldPos: Vec3) {

        val random: RandomSource = level.getRandom()
        val h: Supplier<Boolean> = Supplier { random.nextDouble() < 0.5 }

        val dx: Double = (currentPos.x - oldPos.x) / 12.0
        val dy: Double = (currentPos.y - oldPos.y) / 12.0
        val dz: Double = (currentPos.z - oldPos.z) / 12.0

        val xDrift: Double = (if (h.get()) 1 else -1) * random.nextDouble() * 2
        val zDrift: Double = (if (h.get()) 1 else -1) * random.nextDouble() * 2

        val particleType: SimpleParticleType =
            if (random.nextBoolean()) ParticleTypes.CAMPFIRE_SIGNAL_SMOKE else ParticleTypes.CAMPFIRE_COSY_SMOKE

        level.addAlwaysVisibleParticle(
            particleType,
            true,
            pos.x + 0.5 + random.nextDouble() / 3.0 * (if (random.nextBoolean()) 1 else -1).toDouble(),
            pos.y + random.nextDouble() + random.nextDouble(),
            pos.z + 0.5 + random.nextDouble() / 3.0 * (if (random.nextBoolean()) 1 else -1).toDouble(),
            0.007 * xDrift + dx, 0.05 + dy, 0.007 * zDrift + dz
        )
    }
}
package com.github.bonndan.blimpy.blimp.entity.bombbay

import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.item.PrimedTnt
import net.minecraft.world.level.Explosion
import net.minecraft.world.level.Level

private const val EXPLOSION_POWER = 16f

class Bomb(level: Level, x: Double, y: Double, z: Double, owner: LivingEntity?) : PrimedTnt(level, x, y, z, owner) {

    override fun explode() {
        this.level()
            .explode(
                this,
                Explosion.getDefaultDamageSource(this.level(), this),
                null,
                this.x,
                this.getY(0.0625),
                this.z,
                EXPLOSION_POWER,
                true,
                Level.ExplosionInteraction.TNT
            )
    }
}
package com.github.bonndan.blimpy.blimp.entity

import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityDimensions
import net.minecraft.world.entity.Pose
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput
import net.neoforged.neoforge.entity.PartEntity


private const val BALLOON_PART_Y_OFFSET = 1.6

/**
 * @see net.minecraft.world.entity.boss.EnderDragonPart
 */
class Balloon(parent: BlimpEntity) : PartEntity<BlimpEntity>(parent) {

    init {
        this.refreshDimensions()
    }

    fun updatePosition(parentEntity: Entity) {
        val oldX: Double = this.x
        val oldY: Double = this.y
        val oldZ: Double = this.z
        val x: Double = parentEntity.x
        val z: Double = parentEntity.z
        val y: Double = parentEntity.y + BALLOON_PART_Y_OFFSET
        this.setPos(x, y, z)
        this.zOld = oldZ
        this.zo = oldZ
        this.xOld = oldX
        this.xo = oldX
        this.yOld = oldY
        this.yo = oldY
    }

    override fun defineSynchedData(builder: SynchedEntityData.Builder) {
        //empty
    }

    override fun hurtServer(
        level: ServerLevel,
        damageSource: DamageSource,
        amount: Float,
    ): Boolean {
        return parent.hurtServer(level, damageSource, amount)
    }

    override fun readAdditionalSaveData(valueInput: ValueInput) {
        //empty
    }

    override fun addAdditionalSaveData(valueOutput: ValueOutput) {
        //empty
    }

    override fun getDimensions(pose: Pose): EntityDimensions {
        return parent.getDimensions(pose)
    }

    override fun shouldBeSaved(): Boolean {
        return true
    }

    override fun getPickResult(): ItemStack? {
        return parent.pickResult
    }

    override fun `is`(entity: Entity): Boolean {
        return this == entity || parent == entity
    }

    override fun setPos(x: Double, y: Double, z: Double) {
        super.setPos(x, y, z)
    }

    override fun isPickable(): Boolean {
        return !this.isRemoved
    }

    override fun interact(player: Player, hand: InteractionHand): InteractionResult {

        return parent.interact(player, hand)
    }
}
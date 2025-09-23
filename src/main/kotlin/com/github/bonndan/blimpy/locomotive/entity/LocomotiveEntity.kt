package com.github.bonndan.blimpy.locomotive.entity

import com.github.bonndan.blimpy.setup.ModItems
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerPlayer
import net.minecraft.tags.ItemTags
import net.minecraft.util.Mth
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.vehicle.MinecartFurnace
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.properties.RailShape
import net.minecraft.world.phys.Vec3
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.deepCopy

class LocomotiveEntity(entityType: EntityType<out MinecartFurnace>, level: Level)  : MinecartFurnace(entityType, level){

    private var rotationOffset = 0f
    private var playerRotationOffset = 0f


    override fun interact(player: Player, hand: InteractionHand): InteractionResult {

        val ret = furnaceInteract(player, hand)
        if (ret.consumesAction()) return ret

        val isNotSecondary = !player.isSecondaryUseActive
        val isNotVehicle = !this.isVehicle
        val isClientSideOrStartRiding = this.level().isClientSide || player.startRiding(this)

        if (isNotSecondary && isNotVehicle && isClientSideOrStartRiding) {
            this.playerRotationOffset = this.rotationOffset
            if (!this.level().isClientSide) {
                return (if (player.startRiding(this)) InteractionResult.CONSUME else InteractionResult.PASS) as InteractionResult
            } else {
                return InteractionResult.SUCCESS
            }
        } else {
            return InteractionResult.PASS
        }
    }

    /**
     * Interaction with the furnace minecart but without the push change.
     */
    private fun furnaceInteract(
        player: Player,
        hand: InteractionHand
    ): InteractionResult {

        val push = this.push.deepCopy()
        val ret = super.interact(player, hand)

        //undo the push change from super.interact
        this.push = push

        if (ret.consumesAction()) {
            val itemstack = player.getItemInHand(hand)
            // only return SUCCESS if the item is a valid fuel item, seems to be a bug in the original code
            return if (itemstack.`is`(ItemTags.FURNACE_MINECART_FUEL)) ret else InteractionResult.PASS
        }

        return ret
    }

    override fun getDropItem(): Item {
        return ModItems.LOCOMOTIVE.get()
    }

    override fun getPickResult(): ItemStack {
        return ItemStack(ModItems.LOCOMOTIVE.get())
    }


    override fun isRideable(): Boolean {
        return true
    }

    override fun tick() {
        val d0 = this.yRot.toDouble()
        val vec3 = this.position()
        super.tick()
        val d1 = (this.yRot.toDouble() - d0) % 360.0
        if (this.level().isClientSide && vec3.distanceTo(this.position()) > 0.01) {
            this.rotationOffset += d1.toFloat()
            this.rotationOffset %= 360.0f
        }
    }

    override fun positionRider(passenger: Entity, callback: MoveFunction) {
        super.positionRider(passenger, callback)
        if (this.level().isClientSide && passenger is Player && passenger.shouldRotateWithMinecart() && useExperimentalMovement(
                this.level()
            )
        ) {
            val f = Mth.rotLerp(0.5, this.playerRotationOffset.toDouble(), this.rotationOffset.toDouble()).toFloat()
            passenger.yRot = passenger.yRot - (f - this.playerRotationOffset)
            this.playerRotationOffset = f
        }
    }

    /**
     * Speed is doubled if the player is giving movement input (WASD).
     */
    override fun makeStepAlongTrack(pos: BlockPos, railShape: RailShape, speed: Double): Double {

        var speedFactor = 1.0

        // for the new behavior, only apply speed boost if the player is giving movement input
        if (useExperimentalMovement(this.level())) {
            if (firstPassengerIsBurningFuel()) {
                speedFactor = 2.0
            }
        }
        return super.makeStepAlongTrack(pos, railShape, speed * speedFactor)
    }

    override fun applyNaturalSlowdown(speed: Vec3): Vec3 {

        var speedFactor = 1.0

        // for the old behavior, always apply speed boost when a player is  movement input
        if (!useExperimentalMovement(this.level())) {
            if (firstPassengerIsBurningFuel()) {
                speedFactor = 2.0
            }
        }

        return super.applyNaturalSlowdown(speed.scale(speedFactor))
    }

    private fun firstPassengerIsBurningFuel(): Boolean {

        if (!hasFuel()) {
            return false
        }

        val passenger = this.firstPassenger
        if (passenger is ServerPlayer ) {
            if (passenger.lastClientMoveIntent.lengthSqr() > 0.0) {
                return true
            }
        }

        return false
    }
}
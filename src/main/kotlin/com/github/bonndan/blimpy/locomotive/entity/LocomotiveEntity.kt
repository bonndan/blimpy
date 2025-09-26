package com.github.bonndan.blimpy.locomotive.entity

import com.github.bonndan.blimpy.blimp.entity.engine.VehiclePacketHandler.sendToServer
import com.github.bonndan.blimpy.network.SetThrottlePacket
import com.github.bonndan.blimpy.setup.ModItems
import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
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

class LocomotiveEntity(entityType: EntityType<out MinecartFurnace>, level: Level) : MinecartFurnace(entityType, level) {

    private var rotationOffset = 0f
    private var playerRotationOffset = 0f

    val FULL_AHEAD = 1.0f
    val ZERO_SPEED = 0f
    val BRAKES = -0.5f

    override fun defineSynchedData(builder: SynchedEntityData.Builder) {
        super.defineSynchedData(builder)
        builder.define(DATA_ID_THROTTLE, 0f)
    }

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

        val push = Vec3(this.push.x, this.push.y, this.push.z)
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
        this.behavior
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
            if (hasActiveThrottle()) {
                speedFactor = getThrottle().toDouble()
            }
        }
        return super.makeStepAlongTrack(pos, railShape, speed * speedFactor)
    }

    override fun applyNaturalSlowdown(speed: Vec3): Vec3 {

        // for the old behavior, always apply speed boost when a player is movement input
        if (!useExperimentalMovement(this.level())) {
            if (hasActiveThrottle()) {

                //this is the minecart default behavior without the push
                val slowdownFactor = this.behavior.slowdownFactor
                val speedEffect = getThrottle().toDouble()
                var newSpeed: Vec3 = speed.multiply(slowdownFactor + speedEffect, 0.0, slowdownFactor + speedEffect)
                if (this.isInWater) {
                    newSpeed = newSpeed.scale(0.95)
                }
                println("new speed [${newSpeed}]")
                return newSpeed
            }
        }

        return super.applyNaturalSlowdown(speed)
    }

    private fun hasActiveThrottle() =
        hasFuel() && this.firstPassenger is ServerPlayer && getThrottle() != ZERO_SPEED

    override fun dismountTo(x: Double, y: Double, z: Double) {
        setThrottle(0f)
        super.dismountTo(x, y, z)
    }

    fun throttleUp() {
        val throttle = getThrottle()
        when {
            throttle >= FULL_AHEAD -> return

            throttle == ZERO_SPEED && hasFuel() -> {
                sendFeedback("Thrust on")
                return sendToServer(SetThrottlePacket(id, FULL_AHEAD))
            }

            else -> {
                sendFeedback("Loosing brakes")
                return sendToServer(SetThrottlePacket(id, ZERO_SPEED))
            }
        }
    }

    fun throttleDown() {
        val throttle = getThrottle()
        when {
            throttle <= BRAKES -> return
            throttle <= ZERO_SPEED -> {
                sendFeedback("Brakes on")
                return sendToServer(SetThrottlePacket(id, BRAKES))
            }

            else -> {
                sendFeedback("Thrust off")
                return sendToServer(SetThrottlePacket(id, ZERO_SPEED))
            }
        }
    }

    private fun sendFeedback(string: String) {
        Minecraft.getInstance().player?.displayClientMessage(Component.literal(string), false)
    }

    fun getThrottle(): Float {
        return this.entityData.get(DATA_ID_THROTTLE)
    }

    fun setThrottle(throttle: Float) {
        this.entityData.set(DATA_ID_THROTTLE, throttle)
    }

    companion object {
        val DATA_ID_THROTTLE: EntityDataAccessor<Float> =
            SynchedEntityData.defineId(LocomotiveEntity::class.java, EntityDataSerializers.FLOAT)
    }

}
package com.github.bonndan.blimpy.blimp.entity


import com.github.bonndan.blimpy.blimp.container.BlimpDataAccessor
import com.github.bonndan.blimpy.blimp.container.BlimpMenu
import com.github.bonndan.blimpy.blimp.entity.engine.FueledEngine
import com.github.bonndan.blimpy.blimp.entity.engine.SaveStateCallback
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.EntityDimensions
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.vehicle.AbstractBoat
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.toVec3
import java.util.function.Supplier
import kotlin.math.abs
import kotlin.math.min

private const val COLOR = "Color"
private const val MAX_HEIGHT = 300

/**
 * A flying blimp which basically is a flying boat.
 *
 *
 */
class BlimpEntity(entityType: EntityType<out AbstractBoat>, level: Level, dropItem: Supplier<Item>) :
    AbstractBoat(entityType, level, dropItem) {

    private val saveStateCallback = object : SaveStateCallback {
        override fun saveState(engineState: Boolean, remainingBurnTime: Int) {
            entityData[ENGINE_IS_ON] = engineState
            entityData[REMAINING_BURN_TIME] = remainingBurnTime
        }
    }

    val engine = FueledEngine(saveStateCallback, level().fuelValues())

    /**
     * player input based height control
     */
    private var heightControl = 0.0

    override fun rideHeight(dimensions: EntityDimensions): Double {
        return (dimensions.height() / 3.0F).toDouble()
    }

    override fun getPaddleState(side: Int): Boolean {
        return false
    }

    /**
     * Implements up/down thrust here via heightControl and is called every tick.
     */
    override fun getDefaultGravity(): Double {

        //TODO flying not enabled
        if (y >= MAX_HEIGHT) {
            heightControl = super.getDefaultGravity()
        }

        if ((waterLevelAbove < 0 || isInWaterOrOnLand()) && heightControl > 0) {
            heightControl = 0.0
        }

        if (engine.isOn()) {
            return 0.0 + heightControl
        }
        return super.getDefaultGravity()
    }

    fun sink() {
        heightControl += super.getDefaultGravity() * 0.1
        playThrustSound()
    }

    fun rise() {
        heightControl -= super.getDefaultGravity() * 0.1
        playThrustSound()
    }

    private fun playThrustSound() {
        this.playSound(SoundEvents.AXOLOTL_IDLE_AIR)
    }

    /**
     * Reduce the ON_LAND status friction
     */
    override fun getGroundFriction(): Float {

        return min(0.9f, super.getGroundFriction())
    }

    /**
     * hack: paddle sounds apper only if in water or on land
     */
    private fun isInWaterOrOnLand(): Boolean = this.paddleSound != null

    fun setEngineOn(state: Boolean) {
        this.engine.setEngineOn(state)
    }

    override fun onAboveBubbleCol(downwards: Boolean) {
        //nothing happens
    }

    fun getColorId(): Int? {
        val color = getEntityData().get(COLOR_ID)
        return if (color == -1) null else color
    }

    fun setColorId(color: Int?) {
        var color = color
        if (color == null) color = -1
        getEntityData()[COLOR_ID] = color
    }

    override fun interact(player: Player, hand: InteractionHand): InteractionResult {

        // right click works for riding player, otherwise would dismount
        val ret = super.interact(player, hand)
        if (ret.consumesAction()) {
            return ret
        }

        if (!level().isClientSide && this.isVehicle) {
            return showEngineMenu(player)
        }

        return ret
    }

    /*
     * menu / GUI stuff
     */
    private fun showEngineMenu(pPlayer: Player): InteractionResult {
        pPlayer.openMenu(createMenuProvider(), getDataAccessor()::write)
        return InteractionResult.CONSUME
    }

    private fun createMenuProvider(): MenuProvider {
        return object : MenuProvider {

            override fun getDisplayName(): Component {
                return Component.translatable("entity.blimpy.blimp")
            }

            override fun createMenu(i: Int, playerInventory: Inventory, player: Player): AbstractContainerMenu {

                return BlimpMenu(
                    i,
                    level(),
                    getDataAccessor(),
                    playerInventory,
                    player
                )
            }
        }
    }

    fun getDataAccessor(): BlimpDataAccessor {
        val accessor = BlimpDataAccessor.Builder()
            .withId(this.id)
            .withOn { engine.isOn() }
            .withLit { engine.isLit() }
            .withBurnProgressPct { engine.getBurnProgressPct() }
            .build()

        return accessor
    }

    /*
     * data stuff
     */
    override fun defineSynchedData(pBuilder: SynchedEntityData.Builder) {
        super.defineSynchedData(pBuilder)
        pBuilder.define(ENGINE_IS_ON, false)
        pBuilder.define(REMAINING_BURN_TIME, 0)
        pBuilder.define(COLOR_ID, -1)
    }

    override fun onSyncedDataUpdated(key: EntityDataAccessor<*>) {
        super.onSyncedDataUpdated(key)

        if (!level().isClientSide) return

        if (key == COLOR_ID) {
            setColorId(entityData[COLOR_ID])
        }

        if (ENGINE_IS_ON == key) {
            setEngineOn(entityData[ENGINE_IS_ON])
        }

        if (REMAINING_BURN_TIME == key) {
            engine.setRemainingBurnTime(entityData[REMAINING_BURN_TIME])
        }
    }

    override fun readAdditionalSaveData(compound: CompoundTag) {
        super.readAdditionalSaveData(compound)
        engine.readAdditionalSaveData(compound, registryAccess())
        if (compound.contains(COLOR, Tag.TAG_INT.toInt())) {
            setColorId(compound.getInt(COLOR))
        }
    }

    override fun addAdditionalSaveData(compound: CompoundTag) {
        super.addAdditionalSaveData(compound)
        engine.addAdditionalSaveData(compound, registryAccess())

        val color = getColorId()
        if (color != null) {
            compound.putInt(COLOR, color)
        }
    }

    override fun tick() {

        super.tick()

        if (engine.isOn()) {
            engine.makeEmissions(
                level(),
                this.onPos.above().above().toVec3(),
                Vec3(this.x, this.y, this.z),
                Vec3(xOld, yOld, zOld)
            )
        }

        if (abs(heightControl) > 0) {
            if (abs(heightControl) < 0.01)
                heightControl = 0.0
            else
                heightControl *= 0.5
        }
    }

    fun isValid(player: Player): Boolean = !this.isRemoved && player.canInteractWithEntity(this.boundingBox, 4.0)

    companion object {
        private val COLOR_ID: EntityDataAccessor<Int> = SynchedEntityData.defineId(
            BlimpEntity::class.java, EntityDataSerializers.INT
        )
        private val ENGINE_IS_ON: EntityDataAccessor<Boolean> = SynchedEntityData.defineId(
            BlimpEntity::class.java, EntityDataSerializers.BOOLEAN
        )
        private val REMAINING_BURN_TIME: EntityDataAccessor<Int> = SynchedEntityData.defineId(
            BlimpEntity::class.java, EntityDataSerializers.INT
        )
    }
}
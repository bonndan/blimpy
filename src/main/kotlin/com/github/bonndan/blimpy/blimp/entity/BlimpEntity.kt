package com.github.bonndan.blimpy.blimp.entity


import com.github.bonndan.blimpy.blimp.container.BlimpDataAccessor
import com.github.bonndan.blimpy.blimp.container.BlimpMenu
import com.github.bonndan.blimpy.blimp.entity.engine.FueledEngine
import com.github.bonndan.blimpy.blimp.entity.engine.SaveStateCallback
import com.github.bonndan.blimpy.setup.ModItems
import com.github.bonndan.blimpy.setup.ModMenuTypes
import net.minecraft.core.NonNullList
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.Containers
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.InteractionResult.CONSUME
import net.minecraft.world.MenuProvider
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityDimensions
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.MoverType
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.vehicle.AbstractBoat
import net.minecraft.world.entity.vehicle.ContainerEntity
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.DyeColor
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.storage.loot.LootTable
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.entity.PartEntity
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.toVec3
import java.util.function.Supplier
import kotlin.math.abs
import kotlin.math.min


/**
 * A flying blimp which basically is a flying boat.
 *
 *
 */
class BlimpEntity(entityType: EntityType<out AbstractBoat>, level: Level, dropItem: Supplier<Item>) :
    AbstractBoat(entityType, level, dropItem), ContainerEntity {

    private val balloon: Balloon = Balloon(this)

    private val saveStateCallback = object : SaveStateCallback {
        override fun saveState(engineState: Boolean, remainingBurnTime: Int) {
            entityData[ENGINE_IS_ON] = engineState
            entityData[REMAINING_BURN_TIME] = remainingBurnTime
        }
    }

    val engine = FueledEngine(saveStateCallback, level().fuelValues())

    /**
     * see ChestBoat
     */
    private var itemStacks = NonNullList.withSize(CONTAINER_SIZE, ItemStack.EMPTY)
    private var lootTable: ResourceKey<LootTable>? = null
    private var lootTableSeed: Long = 0

    /**
     * player input based height control
     */
    private var heightControl = 0.0

    /**
     * hack: paddle sounds apper only if in water or on land, calculated on tick, since it calls getStatus()
     */
    private var isInWaterOrOnLand: Boolean = false

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

        if (isInWaterOrOnLand && heightControl > 0) {
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
     * The hitbox of the blimp is more or less the size of a boat, so a sitting player can suffocate in ceilings,
     * because the hitbox does not cover the balloon.
     *
     *  @see net.minecraft.world.entity.boss.EnderDragonPart
     */
    override fun isMultipartEntity(): Boolean {
        return true
    }

    override fun getParts(): Array<out PartEntity<*>?> {
        return arrayOf(balloon)
    }

    override fun move(type: MoverType, movement: Vec3) {

        //move the balloon first to see if it collides
        balloon.move(type, movement)
        if (balloon.horizontalCollision || balloon.verticalCollision) {
            //TODO check if it is better to use deltaMovement from balloon instead of doing nothing
            return
        }

        super.move(type, movement)
    }

    override fun getPassengerAttachmentPoint(entity: Entity, dimensions: EntityDimensions, partialTick: Float): Vec3 {
        return super.getPassengerAttachmentPoint(entity, dimensions, partialTick)
            .add(Vec3(0.0, PASSENGER_Y_OFFSET, 0.0))
    }

    override fun getSinglePassengerXOffset(): Float {
        return PASSENGER_X_OFFSET
    }

    fun setEngineOn(state: Boolean) {
        this.engine.setEngineOn(state)
    }

    override fun onAboveBubbleCol(downwards: Boolean) {
        //nothing happens
    }

    fun getColorId(): Int? {
        val color = getEntityData().get(COLOR_ID)
        return if (color == -ENGINE_SLOT) null else color
    }

    fun setColorId(color: Int?) {
        var color = color
        if (color == null) color = -ENGINE_SLOT
        getEntityData()[COLOR_ID] = color
    }

    override fun interact(player: Player, hand: InteractionHand): InteractionResult {

        // if dyecolor is used on blimp
        val color = DyeColor.getColor(player.getItemInHand(hand))
        if (color != null) {
            if (!level().isClientSide) {
                setColorId(color.id)
            }
            // don't interact *and* use current item
            return InteractionResult.SUCCESS
        }

        //right click with shift opens inventory
        if (!level().isClientSide && player.isSecondaryUseActive) {
            player.openMenu(createMenuProvider(), getDataAccessor()::write)
            return CONSUME
        }

        // right click works for riding player, otherwise would dismount
        val ret = super.interact(player, hand)
        if (ret.consumesAction()) {
            return ret
        }

        return ret
    }

    /*
     * menu / GUI stuff
     */

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
        pBuilder.define(COLOR_ID, -ENGINE_SLOT)
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

    /**
     * Updates entity state from saved data (engine, color, contents...)
     */
    override fun readAdditionalSaveData(compound: CompoundTag) {
        super.readAdditionalSaveData(compound)
        engine.readAdditionalSaveData(compound, registryAccess())

        if (compound.contains(COLOR, Tag.TAG_INT.toInt())) {
            setColorId(compound.getInt(COLOR))
        }

        this.readChestVehicleSaveData(compound, this.registryAccess());
    }

    override fun addAdditionalSaveData(compound: CompoundTag) {
        super.addAdditionalSaveData(compound)
        engine.addAdditionalSaveData(compound, registryAccess())

        val color = getColorId()
        if (color != null) {
            compound.putInt(COLOR, color)
        }

        this.addChestVehicleSaveData(compound, this.registryAccess());
    }

    override fun recreateFromPacket(packet: ClientboundAddEntityPacket) {
        super.recreateFromPacket(packet)
        balloon.id = packet.id
    }

    override fun tick() {

        super.tick()

        //calculate status once per tick
        this.isInWaterOrOnLand = this.paddleSound != null

        //adjust balloon pos, reused code from LittleLogistics - not from EnderDragon
        balloon.updatePosition(this)

        engine.tickFuel()
        if (engine.isOn()) {
            engine.makeEmissions(
                level(),
                this.onPos.above().above().toVec3(),
                Vec3(this.x, this.y, this.z),
                Vec3(xOld, yOld, zOld)
            )
        }

        //TODO add friction to y deltaMovement
        if (abs(heightControl) > 0) {
            if (abs(heightControl) < 0.01)
                heightControl = 0.0
            else
                heightControl *= 0.5
        }
    }

    fun isValid(player: Player): Boolean = !this.isRemoved && player.canInteractWithEntity(this.boundingBox, 4.0)

    /*
     * container stuff:
     * - the engine works like a container foal fuel (coal) with one slot
     * - there are 9 extra slots to store stuff
     */

    override fun destroy(level: ServerLevel, damageSource: DamageSource) {
        this.destroy(level, this.dropItem) //drop self
        this.chestVehicleDestroyed(damageSource, level, this) //drop chest contents
        this.spawnAtLocation(level, engine.getStackInSlot(ENGINE_SLOT)) //drop engine content
    }

    override fun level(): Level {
        return super.level()
    }

    override fun position(): Vec3 {
        return super.position()
    }

    override fun getDisplayName(): Component {
        return super<AbstractBoat>.displayName!!
    }

    override fun getContainerLootTable(): ResourceKey<LootTable>? {
        return this.lootTable
    }

    override fun setContainerLootTable(lootTable: ResourceKey<LootTable>?) {
        this.lootTable = lootTable
    }

    override fun getContainerLootTableSeed(): Long {
        return this.lootTableSeed
    }

    override fun setContainerLootTableSeed(lootTableSeed: Long) {
        this.lootTableSeed = lootTableSeed
    }

    /**
     * Return only the "chest" content
     */
    override fun getItemStacks(): NonNullList<ItemStack> {
        return itemStacks
    }

    override fun clearItemStacks() {
        engine.setStackInSlot(0, ItemStack.EMPTY)
        this.itemStacks = NonNullList.withSize(this.containerSize, ItemStack.EMPTY);
    }

    override fun getContainerSize(): Int {
        return CONTAINER_SIZE
    }

    override fun getItem(slot: Int): ItemStack {
        if (slot == ENGINE_SLOT) {
            return engine.getStackInSlot(slot)
        }

        return itemStacks[slot - 1]
    }

    override fun removeItem(slot: Int, amount: Int): ItemStack {
        if (slot == ENGINE_SLOT) {
            return engine.extractItem(slot, amount, false)
        }

        return removeChestVehicleItem(slot - 1, amount)
    }

    override fun removeItemNoUpdate(slot: Int): ItemStack {
        if (slot == ENGINE_SLOT) {
            return engine.getStackInSlot(slot)
        }
        return this.removeChestVehicleItemNoUpdate(slot - 1)
    }

    override fun setItem(slot: Int, stack: ItemStack) {
        if (slot == ENGINE_SLOT) {
            engine.setStackInSlot(slot, stack)
        }

        itemStacks[slot - 1] = stack
    }

    override fun setChanged() {
        //nothing
    }

    override fun stillValid(player: Player): Boolean {
        return this.isChestVehicleStillValid(player)
    }

    override fun clearContent() {
        this.clearChestVehicleContent()
    }

    override fun createMenu(
        containerId: Int,
        playerInventory: Inventory,
        player: Player,
    ): AbstractContainerMenu? {
        return createMenuProvider().createMenu(containerId, playerInventory, player)
    }

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

        private const val PASSENGER_Y_OFFSET = 0.1
        private const val PASSENGER_X_OFFSET = 0.2F

        private const val COLOR = "Color"
        private const val MAX_HEIGHT = 300
        private const val ENGINE_SLOT = 0
        const val CONTAINER_SIZE = 9
    }
}
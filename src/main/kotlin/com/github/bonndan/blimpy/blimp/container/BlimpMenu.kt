package com.github.bonndan.blimpy.blimp.container

import com.github.bonndan.blimpy.blimp.entity.BlimpEntity
import com.github.bonndan.blimpy.blimp.entity.engine.SetEnginePacket
import com.github.bonndan.blimpy.blimp.entity.engine.VehiclePacketHandler
import com.github.bonndan.blimpy.setup.ModMenuTypes
import net.minecraft.world.Container
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot

class BlimpMenu(
    windowId: Int,
    world: Level,
    private val data: BlimpDataAccessor,
    playerInventory: Inventory,
    private val player: Player,
) : AbstractContainerMenu(ModMenuTypes.BLIMP_MENU.get(), windowId) {

    private var entity: BlimpEntity? = data.getEntityUUID()?.let { world.getEntity(it) as BlimpEntity? }

    init {
        addStandardInventorySlots(playerInventory, 8, 84)
        entity?.let {
            // Engine fuel slot — backed by the ResourceHandler directly
            addSlot(ResourceHandlerSlot(it.engine, it.engine::set, 0, 80, 24))
            // Chest slots — backed by the entity as a vanilla Container (slots offset by 1)
            var x = 8
            for (i in 1..BlimpEntity.CONTAINER_SIZE) {
                addSlot(Slot(it as Container, i, x, 48))
                x += 18
            }
        }
        this.addDataSlots(data.getRawData())
    }

    val isLit: Boolean
        get() = data.isLit

    val isOn: Boolean
        get() = data.isOn

    fun setEngineState(state: Boolean) {
        VehiclePacketHandler.sendToServer(SetEnginePacket(entity!!.id, state))
    }

    fun getBurnProgress(): Int {
        return data.getBurnProgress()
    }

    override fun stillValid(player: Player): Boolean {
        return entity?.isValid(player) == true
    }

    override fun quickMoveStack(playerIn: Player, index: Int): ItemStack {

        val sourceSlot = slots[index]
        if (!sourceSlot.hasItem()) {
            return ItemStack.EMPTY
        }

        val sourceStack = sourceSlot.item
        val copyOfSourceStack = sourceStack.copy()

        when {

            // move from player to entity
            index < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT -> {

                if (!moveItemStackTo(
                        sourceStack,
                        TE_INVENTORY_FIRST_SLOT_INDEX,
                        TE_INVENTORY_FIRST_SLOT_INDEX + BlimpEntity.CONTAINER_SIZE + 1,
                        false
                    )
                ) {
                    return ItemStack.EMPTY
                }
            }
            index <= TE_INVENTORY_FIRST_SLOT_INDEX + BlimpEntity.CONTAINER_SIZE -> {
                // This is a TE slot so merge the stack into the players inventory
                if (!moveItemStackTo(
                        sourceStack,
                        VANILLA_FIRST_SLOT_INDEX,
                        VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT,
                        false
                    )
                ) {
                    return ItemStack.EMPTY
                }
            }

            else -> return ItemStack.EMPTY
        }
        if (sourceStack.count == 0) {
            sourceSlot.set(ItemStack.EMPTY)
        } else {
            sourceSlot.setChanged()
        }
        sourceSlot.onTake(player, sourceStack)
        return copyOfSourceStack
    }

    companion object {
        private const val HOTBAR_SLOT_COUNT = 9
        private const val PLAYER_INVENTORY_ROW_COUNT = 3
        private const val PLAYER_INVENTORY_COLUMN_COUNT = 9
        private const val PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT
        private const val VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT
        private const val VANILLA_FIRST_SLOT_INDEX = 0
        private const val TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT
    }
}
package com.github.bonndan.blimpy.blimp.container

import com.github.bonndan.blimpy.blimp.entity.BlimpEntity
import com.github.bonndan.blimpy.blimp.entity.engine.SetEnginePacket
import com.github.bonndan.blimpy.blimp.entity.engine.VehiclePacketHandler
import com.github.bonndan.blimpy.setup.ModMenuTypes
import net.minecraft.world.Container
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.neoforged.neoforge.items.IItemHandler
import net.neoforged.neoforge.items.SlotItemHandler
import net.neoforged.neoforge.items.wrapper.InvWrapper

class BlimpMenu(
    windowId: Int,
    world: Level,
    private val data: BlimpDataAccessor,
    playerInventory: Inventory,
    private val player: Player?,
) : AbstractContainerMenu(ModMenuTypes.BLIMP_MENU.get(), windowId) {

    private var entity: BlimpEntity? = data.getEntityUUID()?.let { world.getEntity(it) as BlimpEntity? }

    init {
        addStandardInventorySlots(playerInventory, 8, 84)
        entity?.let {
            addSlot(SlotItemHandler(it.engine, 0, 80, 24))
            addSlotRange(InvWrapper(entity as Container), index = 1, x = 8, y = 48, BlimpEntity.CONTAINER_SIZE, dx = 18)
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


    private fun addSlotRange(handler: IItemHandler, index: Int, x: Int, y: Int, amount: Int, dx: Int): Int {
        var index = index
        var x = x
        for (i in 0 until amount) {
            addSlot(SlotItemHandler(handler, index, x, y))
            x += dx
            index++
        }

        return index
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
        // If stack size == 0 (the entire stack was moved) set slot contents to null
        if (sourceStack.count == 0) {
            sourceSlot.set(ItemStack.EMPTY)
        } else {
            sourceSlot.setChanged()
        }
        sourceSlot.onTake(player, sourceStack)
        return copyOfSourceStack
    }

    companion object {
        // CREDIT GOES TO: diesieben07 | https://github.com/diesieben07/SevenCommons
        // must assign a slot number to each of the slots used by the GUI.
        // For this container, we can see both the tile inventory's slots as well as the player inventory slots and the hotbar.
        // Each time we add a Slot to the container, it automatically increases the slotIndex, which means
        //  0 - 8 = hotbar slots (which will map to the InventoryPlayer slot numbers 0 - 8)
        //  9 - 35 = player inventory slots (which map to the InventoryPlayer slot numbers 9 - 35)
        //  36 - 44 = TileInventory slots, which map to our TileEntity slot numbers 0 - 8)
        private const val HOTBAR_SLOT_COUNT = 9
        private const val PLAYER_INVENTORY_ROW_COUNT = 3
        private const val PLAYER_INVENTORY_COLUMN_COUNT = 9
        private const val PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT
        private const val VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT
        private const val VANILLA_FIRST_SLOT_INDEX = 0
        private const val TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT
    }
}
package com.github.bonndan.blimpy.blimp.container

import com.github.bonndan.blimpy.blimp.entity.BlimpEntity
import com.github.bonndan.blimpy.network.SetEnginePacket
import com.github.bonndan.blimpy.network.VehiclePacketHandler
import com.github.bonndan.blimpy.setup.ModMenuTypes
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.InventoryMenu
import net.minecraft.world.level.Level
import net.neoforged.neoforge.items.SlotItemHandler

class BlimpMenu(
    windowId: Int,
    world: Level,
    private val data: BlimpDataAccessor,
    playerInventory: Inventory,
    player: Player?,
) : AbstractItemHandlerContainer(
    ModMenuTypes.BLIMP_MENU.get(),
    windowId,
    playerInventory,
    player
) {

    private var entity: BlimpEntity? = data.getEntityUUID()?.let { world.getEntity(it) as BlimpEntity? }

    init {
        this.player = playerInventory.player
        layoutPlayerInventorySlots(8, 84)
        entity?.let { addSlot(SlotItemHandler(it.engine, 0, 42, 40)) }
        this.addDataSlots(data.getRawData())
    }

    override val slotNum: Int
        get() = 2

    val isLit: Boolean
        get() = data.isLit

    val isOn: Boolean
        get() = data.isOn

    fun setEngineState(state: Boolean) {

        VehiclePacketHandler.send(SetEnginePacket(entity!!.id, state))
    }

    fun getBurnProgress(): Int {
        return data.getBurnProgress()
    }

    companion object {
        val EMPTY_ATLAS_LOC: ResourceLocation = InventoryMenu.BLOCK_ATLAS
    }

    override fun stillValid(player: Player): Boolean {
        return entity?.isValid(player) == true
    }
}
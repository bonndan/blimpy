package com.github.bonndan.blimpy.setup

import com.github.bonndan.blimpy.blimp.container.BlimpDataAccessor
import com.github.bonndan.blimpy.blimp.container.BlimpMenu
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.inventory.SimpleContainerData
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension
import java.util.function.Supplier

object ModMenuTypes {

    val BLIMP_MENU: Supplier<MenuType<BlimpMenu>> =
        Registration.MENUS.register("blimp_menu", Supplier {
            IMenuTypeExtension.create { windowId: Int, inv: Inventory, data: RegistryFriendlyByteBuf ->
                BlimpMenu(
                    windowId,
                    inv.player.level(),
                    BlimpDataAccessor(makeIntArray(data)),
                    inv,
                    inv.player
                )
            }
        })

    fun register() {}

    //on a NPE check if somewhere player.openMenu(...) is called without 2nd param  getDataAccessor(...)::write
    private fun makeIntArray(buffer: FriendlyByteBuf): SimpleContainerData {

        val size = (buffer.readableBytes() + 1) / 4
        val arr = SimpleContainerData(size)
        for (i in 0 until size) {
            arr[i] = buffer.readInt()
        }
        return arr
    }
}

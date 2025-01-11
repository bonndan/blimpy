package com.github.bonndan.blimpy.blimp.container

import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.items.IItemHandler
import net.neoforged.neoforge.items.SlotItemHandler
import net.neoforged.neoforge.items.wrapper.InvWrapper

abstract class AbstractItemHandlerContainer protected constructor(
    menuType: MenuType<*>,
    containerId: Int,
    playerInventory: Inventory,
    protected var player: Player?
) :
    AbstractContainerMenu(menuType, containerId) {


}
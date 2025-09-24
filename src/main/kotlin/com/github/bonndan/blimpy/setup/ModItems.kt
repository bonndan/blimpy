package com.github.bonndan.blimpy.setup

import com.github.bonndan.blimpy.blimp.item.BlimpItem
import com.github.bonndan.blimpy.locomotive.item.LocomotiveItem
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.CreativeModeTabs
import net.minecraft.world.item.Item
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent
import net.neoforged.neoforge.registries.DeferredItem
import java.util.function.Consumer
import java.util.function.Function
import java.util.function.Supplier

object ModItems {

    private val PRIVATE_TAB_REGISTRY = MultiMap<ResourceKey<CreativeModeTab>, Supplier<out Item>>()

    val BLIMP = registerItem("blimp", ::BlimpItem, defaultItemProperties(1))
    val LOCOMOTIVE = registerItem("locomotive", ::LocomotiveItem, defaultItemProperties(1))

    init {
        registerTabs(BLIMP, listOf(CreativeModeTabs.TOOLS_AND_UTILITIES))
        registerTabs(LOCOMOTIVE, listOf(CreativeModeTabs.TOOLS_AND_UTILITIES))
    }

    fun buildCreativeTab(event: BuildCreativeModeTabContentsEvent) {
        PRIVATE_TAB_REGISTRY.getOrDefault(event.tabKey, ArrayList())
            .forEach(Consumer { supplier: Supplier<out Item> -> event.accept(supplier.get()) })
    }

    private fun registerItem(
        name: String,
        itemSupplier: Function<Item.Properties, Item>,
        props: Item.Properties,
    ): DeferredItem<Item> {

        return Registration.ITEMS.registerItem(name, itemSupplier, props)
    }

    private fun registerTabs(itemSupplier: Supplier<Item>, tabs: List<ResourceKey<CreativeModeTab>>) {
        for (tab in tabs) {
            PRIVATE_TAB_REGISTRY.putInsert(tab, itemSupplier)
        }
    }

    fun register() {}


    private fun defaultItemProperties(pMaxStackSize: Int = 64): Item.Properties {
        return Item.Properties().stacksTo(pMaxStackSize)
    }
}

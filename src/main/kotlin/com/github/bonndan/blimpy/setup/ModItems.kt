package com.github.bonndan.blimpy.setup

import com.github.bonndan.blimpy.blimp.item.BlimpItem
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

    val BLIMP = registerItem("blimp", ::BlimpItem, { Item.Properties().stacksTo(1) })

    init {
        PRIVATE_TAB_REGISTRY.putInsert(CreativeModeTabs.TOOLS_AND_UTILITIES, BLIMP)
    }

    fun buildCreativeTab(event: BuildCreativeModeTabContentsEvent) {
        PRIVATE_TAB_REGISTRY.getOrDefault(event.tabKey, ArrayList())
            .forEach(Consumer { supplier: Supplier<out Item> -> event.accept(supplier.get()) })
    }

    private fun registerItem(
        name: String,
        itemSupplier: Function<Item.Properties, Item>,
        props: Supplier<Item.Properties>,
    ): DeferredItem<Item> {

        return Registration.ITEMS.registerItem(name, itemSupplier, props)
    }

    fun register() {}

}

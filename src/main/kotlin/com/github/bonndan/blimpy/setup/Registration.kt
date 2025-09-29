package com.github.bonndan.blimpy.setup

import com.github.bonndan.blimpy.BlimpyMod
import com.github.bonndan.blimpy.blimp.entity.bombbay.BombPacketHandler
import com.github.bonndan.blimpy.blimp.entity.engine.VehiclePacketHandler
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.entity.EntityType
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntityType
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister

object Registration {

    val BLOCKS: DeferredRegister<Block> = DeferredRegister.createBlocks(BlimpyMod.MOD_ID);
    val MENUS: DeferredRegister<MenuType<*>> = createRegister(BuiltInRegistries.MENU)
    val ENTITIES: DeferredRegister<EntityType<*>> = createRegister(BuiltInRegistries.ENTITY_TYPE)
    val ITEMS: DeferredRegister.Items = DeferredRegister.createItems(BlimpyMod.MOD_ID);
    val RECIPE_SERIALIZERS: DeferredRegister<RecipeSerializer<*>> = createRegister(BuiltInRegistries.RECIPE_SERIALIZER)
    val TILE_ENTITIES: DeferredRegister<BlockEntityType<*>> = createRegister(BuiltInRegistries.BLOCK_ENTITY_TYPE)


    private fun <T> createRegister(registry: Registry<T>): DeferredRegister<T> {
        return DeferredRegister.create(registry, BlimpyMod.MOD_ID)
    }

    fun register(eventBus: IEventBus) {
        BLOCKS.register(eventBus)
        ITEMS.register(eventBus)
        MENUS.register(eventBus)
        RECIPE_SERIALIZERS.register(eventBus)
        TILE_ENTITIES.register(eventBus)
        ENTITIES.register(eventBus)

        //TODO static calls used to ensure correct loading sequence
        ModBlocks.register() //register blocks before items
        ModItems.register()
        ModEntityTypes.register()
        ModTileEntitiesTypes.register()
        ModMenuTypes.register()
        eventBus.register(VehiclePacketHandler)
        eventBus.register(BombPacketHandler)
        ModSounds.register(eventBus)
    }
}

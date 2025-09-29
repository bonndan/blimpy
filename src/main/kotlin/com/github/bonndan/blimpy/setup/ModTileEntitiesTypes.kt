package com.github.bonndan.blimpy.setup

import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.entity.BlockEntityType.BlockEntitySupplier
import java.util.function.Supplier

object ModTileEntitiesTypes {


    fun register() {
    }

    private fun <T : BlockEntity> supplier(
        block: Supplier<Block>,
        supplier: BlockEntitySupplier<T>
    ): Supplier<BlockEntityType<T>> =
        Supplier<BlockEntityType<T>> { BlockEntityType<T>(supplier, block.get()) }
}

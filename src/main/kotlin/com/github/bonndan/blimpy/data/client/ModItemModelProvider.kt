package com.github.bonndan.blimpy.data.client

import com.github.bonndan.blimpy.BlimpyMod
import com.github.bonndan.blimpy.setup.ModItems
import net.minecraft.client.color.item.ItemTintSource
import net.minecraft.client.data.models.BlockModelGenerators
import net.minecraft.client.data.models.ItemModelGenerators
import net.minecraft.client.data.models.ModelProvider
import net.minecraft.client.data.models.model.ModelLocationUtils
import net.minecraft.client.data.models.model.ModelTemplates
import net.minecraft.client.renderer.item.BlockModelWrapper
import net.minecraft.data.PackOutput


class ModItemModelProvider(output: PackOutput) : ModelProvider(output, BlimpyMod.MOD_ID) {

    override fun registerModels(blockModels: BlockModelGenerators, itemModels: ItemModelGenerators) {

        itemModels.generateFlatItem(ModItems.BLIMP.get(), ModelTemplates.FLAT_ITEM);

        /*
        itemModels.itemModelOutput.accept(
            ModItems.BLIMP.get(),
            BlockModelWrapper.Unbaked(
                ModelLocationUtils.getModelLocation(ModItems.BLIMP.get()),
                mutableListOf<ItemTintSource?>()
            )
        )

         */
    }

}

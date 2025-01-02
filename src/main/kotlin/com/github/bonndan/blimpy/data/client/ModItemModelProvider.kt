package com.github.bonndan.blimpy.data.client

import com.github.bonndan.blimpy.BlimpyMod
import net.minecraft.data.PackOutput
import net.minecraft.resources.ResourceLocation
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder
import net.neoforged.neoforge.client.model.generators.ItemModelProvider
import net.neoforged.neoforge.client.model.generators.ModelFile
import net.neoforged.neoforge.common.data.ExistingFileHelper


class ModItemModelProvider(output: PackOutput, existingFileHelper: ExistingFileHelper) :
    ItemModelProvider(output, BlimpyMod.MOD_ID, existingFileHelper) {

    override fun registerModels() {

        val itemGenerated: ModelFile = getExistingFile(mcLoc("item/generated"))

        builder(itemGenerated, "blimp")
    }


    private fun builder(itemGenerated: ModelFile, name: String): ItemModelBuilder {
        return getBuilder(name).parent(itemGenerated).texture("layer0", "item/$name")
    }
}

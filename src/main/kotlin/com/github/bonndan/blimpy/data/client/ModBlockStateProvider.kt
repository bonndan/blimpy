package com.github.bonndan.blimpy.data.client

import com.github.bonndan.blimpy.BlimpyMod
import net.minecraft.data.PackOutput
import net.minecraft.resources.ResourceLocation
import net.neoforged.neoforge.client.model.generators.BlockStateProvider
import net.neoforged.neoforge.common.data.ExistingFileHelper

class ModBlockStateProvider(output: PackOutput, exFileHelper: ExistingFileHelper) :
    BlockStateProvider(output, BlimpyMod.MOD_ID, exFileHelper) {


    override fun registerStatesAndModels() {

        /*
        getVariantBuilder(ModBlocks.FLUID_HOPPER.get()).forAllStates { state: BlockState ->
            ConfiguredModel.builder()
                .modelFile(
                    models()
                        .withExistingParent("fluid_hopper", modLoc("fluid_hopper_parent_model"))
                )
                .rotationY(
                    state.getValue(FluidHopperBlock.FACING).clockWise.toYRot().toInt()
                )
                .build()
        }
        */
    }

    companion object {
        fun getBlTx(name: String): ResourceLocation {
            return ResourceLocation.fromNamespaceAndPath(BlimpyMod.MOD_ID, String.format("block/%s", name))
        }
    }
}

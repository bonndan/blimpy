package com.github.bonndan.blimpy.data

import com.github.bonndan.blimpy.setup.ModItems
import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.data.recipes.RecipeCategory
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.data.recipes.RecipeProvider
import net.minecraft.tags.ItemTags.BOATS
import net.minecraft.world.entity.vehicle.MinecartFurnace
import net.minecraft.world.item.Items
import java.util.concurrent.CompletableFuture

class ModRecipeProvider(recipeOutput: RecipeOutput, pRegistries: HolderLookup.Provider) :
    RecipeProvider(pRegistries, recipeOutput) {

    override fun buildRecipes() {

        this.shaped(RecipeCategory.TRANSPORTATION, ModItems.BLIMP.get(), 1)
            .define('^', Items.PAPER)
            .define('f', Items.FURNACE)
            .define('r', Items.CHEST)
            .define('-', BOATS)
            .pattern("^^^")
            .pattern("f r")
            .pattern("---")
            .unlockedBy("has_item", has(Items.FURNACE))
            .save(output)

        this.shapeless(RecipeCategory.TRANSPORTATION, ModItems.LOCOMOTIVE.get(), 1)
            .requires ( Items.FURNACE_MINECART)
            .requires ( Items.LEVER)
            .unlockedBy("has_item", has(Items.FURNACE))
            .save(output)
    }

    class Runner(output: PackOutput, lookupProvider: CompletableFuture<HolderLookup.Provider>) :
        RecipeProvider.Runner(output, lookupProvider) {

        @Override
        override fun createRecipeProvider(lookupProvider: HolderLookup.Provider, output: RecipeOutput) =
            ModRecipeProvider(output, lookupProvider)

        @Override
        override fun getName(): String {
            return "Blimpy Recipes"
        }
    }
}

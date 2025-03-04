@file:Suppress("unused")

package com.github.bonndan.blimpy.data

import com.github.bonndan.blimpy.BlimpyMod
import com.github.bonndan.blimpy.data.client.ModItemModelProvider
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.data.event.GatherDataEvent

@EventBusSubscriber(modid = BlimpyMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
object DataGenerators {

    @SubscribeEvent
    fun gatherData(gatherDataEvent: GatherDataEvent.Client) {

        val gen = gatherDataEvent.generator
        val pack = gen.packOutput
        val lookupProvider = gatherDataEvent.lookupProvider

        gen.addProvider(true, ModItemModelProvider(pack))

        gatherDataEvent.createProvider {
            ModRecipeProvider.Runner(pack, lookupProvider)
        }
    }
}

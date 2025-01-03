package com.github.bonndan.blimpy.setup

import com.github.bonndan.blimpy.BlimpyMod
import com.github.bonndan.blimpy.blimp.model.BlimpBoatRenderer
import com.github.bonndan.blimpy.blimp.model.BlimpModel
import com.github.bonndan.blimpy.blimp.model.BlimpSteamTugModel
import com.github.bonndan.blimpy.blimp.model.TrimCarModel
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.EntityRenderersEvent
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent

/**
 * Mod-specific event bus
 */
@EventBusSubscriber(modid = BlimpyMod.Companion.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = [Dist.CLIENT])
object ModClientEventHandler {

    @SubscribeEvent
    fun onRegisterEntityRenderers(event: EntityRenderersEvent.RegisterRenderers) {

        event.registerEntityRenderer(ModEntityTypes.BLIMP.get()) { ctx: EntityRendererProvider.Context ->
            BlimpBoatRenderer(
                ctx
            )
        }

    }

    @SubscribeEvent
    fun onRegisterEntityRenderers(event: EntityRenderersEvent.RegisterLayerDefinitions) {

        event.registerLayerDefinition(TrimCarModel.Companion.LAYER_LOCATION) { TrimCarModel.Companion.createBodyLayer() }
        event.registerLayerDefinition(BlimpModel.Companion.LAYER_LOCATION) { BlimpModel.Companion.createBodyLayer() }
    }

    /**
     * Subscribe to event when building each creative mode tab. Items are added to tabs here.
     * @param event The creative tab currently being built
     */
    @SubscribeEvent
    fun buildTabContents(event: BuildCreativeModeTabContentsEvent) {
        ModBlocks.buildCreativeTab(event)
        ModItems.buildCreativeTab(event)
    }

}
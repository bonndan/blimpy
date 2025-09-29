package com.github.bonndan.blimpy.setup

import com.github.bonndan.blimpy.BlimpyMod
import com.github.bonndan.blimpy.blimp.model.BlimpBoatRenderer
import com.github.bonndan.blimpy.blimp.model.BlimpBodyModel
import com.github.bonndan.blimpy.blimp.model.BlimpTintModel
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.EntityRenderersEvent
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent

/**
 * Mod-specific event bus
 */
@EventBusSubscriber(modid = BlimpyMod.MOD_ID, value = [Dist.CLIENT])
object ModClientEventHandler {

    @SubscribeEvent
    fun onRegisterEntityRenderers(event: EntityRenderersEvent.RegisterRenderers) {

        event.registerEntityRenderer(ModEntityTypes.BLIMP.get()) { ctx: EntityRendererProvider.Context ->
            BlimpBoatRenderer(ctx)
        }
    }

    @SubscribeEvent
    fun onRegisterEntityRenderers(event: EntityRenderersEvent.RegisterLayerDefinitions) {

        event.registerLayerDefinition(BlimpBodyModel.LAYER_LOCATION) { BlimpBodyModel.createBodyLayer() }
        event.registerLayerDefinition(BlimpTintModel.LAYER_LOCATION) { BlimpTintModel.createBodyLayer() }
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
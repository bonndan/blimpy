package com.github.bonndan.blimpy.blimp.model

import net.minecraft.client.model.EntityModel
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.resources.ResourceLocation
import java.util.function.Function

data class BlimpRendererConfig(

    /**
     * The model to render, i.e. a descendant of MinecartModel
     */
    val modelLayer: ModelLayerLocation = BlimpSteamTugModel.LAYER_LOCATION,
    val modelTextureLocation: ResourceLocation = ResourceLocation.withDefaultNamespace("textures/entity/minecart.png"),
    val modelSupplier: Function<ModelPart, BlimpSteamTugModel> = Function { part: ModelPart -> BlimpSteamTugModel(part) },

    /**
     * y-axis offset for blocks inserted into the mine cart. The default suits most blocks.
     */
    val modelBlockStateYOffset: Float = -0.5f,

    /**
     * The color model defaults to the TrimCarModel (legacy model, more or less a rim around the cart)
     */
    val colorModelSupplier: Function<ModelPart, TrimCarModel> = TrimCarModel.colorModelSupplier,
    /**
     * A mostly white-ish texture to be rendered with a color that comes from RenderState
     */
    val colorTexture: ResourceLocation? = TrimCarModel.textureLocation,
    val colorLayer: ModelLayerLocation? = TrimCarModel.LAYER_LOCATION,
    val colorModelYOffset: Float = -0.95f,
    val colorModelYRotation: Float = 90f,

    /**
     * An additional model to be rendered, like an engine front part
     */
    val additionalModelSupplier: Function<ModelPart, EntityModel<BlimpRenderState>>? = null,
    val additionalTexture: ResourceLocation? = null,
    val additionalLayer: ModelLayerLocation? = null,
    val additionalModelYOffset: Float = 0f,
    val additionalModelYRotation: Float = 0f,
) {

    fun getModel(context: EntityRendererProvider.Context): BlimpSteamTugModel {
        return modelSupplier.apply(context.bakeLayer(modelLayer))
    }

    fun getColorModel(context: EntityRendererProvider.Context): TrimCarModel? {
        if (colorLayer == null || colorTexture == null) {
            return null
        }
        return colorModelSupplier.apply(context.bakeLayer(colorLayer))
    }

    fun getAdditionalModel(context: EntityRendererProvider.Context): EntityModel<BlimpRenderState>? {

        if (additionalModelSupplier == null || additionalLayer == null) {
            return null
        }
        return additionalModelSupplier.apply(context.bakeLayer(additionalLayer))
    }

}

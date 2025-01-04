package com.github.bonndan.blimpy.blimp.model

import com.github.bonndan.blimpy.BlimpyMod
import net.minecraft.client.model.EntityModel
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.*
import net.minecraft.resources.ResourceLocation

/**
 * The visual model for the tinted balloon part.
 */
class BlimpTintModel(root: ModelPart) : EntityModel<BlimpRenderState>(root.getChild("root")) {

    companion object {

        val LAYER_LOCATION: ModelLayerLocation = ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(BlimpyMod.MOD_ID, "blimp_tint"), "main")

        fun createBodyLayer(): LayerDefinition {
            val meshdefinition: MeshDefinition = MeshDefinition()
            val partdefinition: PartDefinition = meshdefinition.getRoot()

            val root: PartDefinition = partdefinition.addOrReplaceChild(
                "root",
                CubeListBuilder.create(),
                PartPose.offset(-4.9976f, 20.5f, -15.9522f)
            )

            val tint: PartDefinition? = root.addOrReplaceChild(
                "tint",
                CubeListBuilder.create().texOffs(155, 91)
                    .addBox(-3.3024f, -5.0f, -6.7478f, 16.0f, 4.0f, 30.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, -25.0f, 0.0f)
            )


            return LayerDefinition.create(meshdefinition, 256, 256)
        }
    }
}
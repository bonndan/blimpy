package com.github.bonndan.blimpy.blimp.model

import com.github.bonndan.blimpy.BlimpyMod
import net.minecraft.client.model.EntityModel
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.CubeDeformation
import net.minecraft.client.model.geom.builders.CubeListBuilder
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.model.geom.builders.MeshDefinition
import net.minecraft.resources.ResourceLocation

class BlimpModel(root: ModelPart) : EntityModel<BlimpRenderState>(root) {

    companion object {
        // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
        val LAYER_LOCATION: ModelLayerLocation = ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(BlimpyMod.MOD_ID, "energy_locomotive_model"),
            "main"
        )

        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.getRoot()

            val bone = partdefinition.addOrReplaceChild(
                "bone", CubeListBuilder.create().texOffs(0, 0).addBox(4.0f, -4.0f, -9.0f, 2.0f, 2.0f, 5.0f)
                    .texOffs(11, 4).addBox(4.0f, -3.0f, -6.0f, 1.0f, 3.0f, 3.0f)
                    .texOffs(9, 11).addBox(4.0f, -3.0f, -10.0f, 1.0f, 3.0f, 3.0f), PartPose.offset(0.0f, 24.0f, 0.0f)
            )

            val bone2 = partdefinition.addOrReplaceChild(
                "bone2", CubeListBuilder.create().texOffs(0, 0).addBox(4.0f, -4.0f, -9.0f, 2.0f, 2.0f, 5.0f)
                    .texOffs(11, 4).addBox(4.0f, -3.0f, -6.0f, 1.0f, 3.0f, 3.0f)
                    .texOffs(9, 11).addBox(4.0f, -3.0f, -10.0f, 1.0f, 3.0f, 3.0f), PartPose.offset(0.0f, 24.0f, 11.0f)
            )

            val bone3 = partdefinition.addOrReplaceChild(
                "bone3",
                CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-6.0f, -4.0f, -9.0f, 2.0f, 2.0f, 5.0f)
                    .mirror(false)
                    .texOffs(11, 4).mirror().addBox(-5.0f, -3.0f, -6.0f, 1.0f, 3.0f, 3.0f).mirror(false)
                    .texOffs(9, 11).mirror().addBox(-5.0f, -3.0f, -10.0f, 1.0f, 3.0f, 3.0f).mirror(false),
                PartPose.offset(0.0f, 24.0f, 11.0f)
            )

            val bone4 = partdefinition.addOrReplaceChild(
                "bone4",
                CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-6.0f, -4.0f, -9.0f, 2.0f, 2.0f, 5.0f)
                    .mirror(false)
                    .texOffs(11, 4).mirror().addBox(-5.0f, -3.0f, -6.0f, 1.0f, 3.0f, 3.0f).mirror(false)
                    .texOffs(9, 11).mirror().addBox(-5.0f, -3.0f, -10.0f, 1.0f, 3.0f, 3.0f).mirror(false),
                PartPose.offset(0.0f, 24.0f, 0.0f)
            )

            val bb_main = partdefinition.addOrReplaceChild(
                "bb_main", CubeListBuilder.create().texOffs(0, 0).addBox(-6.0f, -13.0f, -12.0f, 12.0f, 9.0f, 20.0f)
                    .texOffs(0, 29).addBox(-6.0f, -16.0f, -9.0f, 12.0f, 3.0f, 17.0f)
                    .texOffs(39, 30).addBox(-4.0f, -4.0f, -11.0f, 8.0f, 3.0f, 19.0f)
                    .texOffs(0, 14).addBox(-1.0f, -17.0f, -10.0f, 2.0f, 2.0f, 2.0f)
                    .texOffs(9, 0).addBox(-1.0f, -17.0f, -10.0f, 2.0f, 2.0f, 2.0f, CubeDeformation(0.25f))
                    .texOffs(0, 7).addBox(4.0f, -4.0f, -3.0f, 2.0f, 3.0f, 4.0f)
                    .texOffs(0, 29).addBox(-1.0f, -5.0f, 8.0f, 2.0f, 2.0f, 2.0f)
                    .texOffs(0, 7).addBox(-6.0f, -4.0f, -3.0f, 2.0f, 3.0f, 4.0f), PartPose.offset(0.0f, 24.0f, 0.0f)
            )

            val cube_r1 = bb_main.addOrReplaceChild(
                "cube_r1",
                CubeListBuilder.create().texOffs(0, 30).addBox(0.0f, 0.0f, -9.5f, 0.0f, 1.0f, 19.0f),
                PartPose.offsetAndRotation(6.0f, -4.0f, -1.5f, 0.0f, 0.0f, -0.0436f)
            )

            val cube_r2 = bb_main.addOrReplaceChild(
                "cube_r2",
                CubeListBuilder.create().texOffs(44, 0).addBox(-6.0f, 0.0f, 0.0f, 12.0f, 3.0f, 0.0f),
                PartPose.offsetAndRotation(0.0f, -4.0f, -12.0f, -0.7854f, 0.0f, 0.0f)
            )

            return LayerDefinition.create(meshdefinition, 128, 128)
        }
    }
}
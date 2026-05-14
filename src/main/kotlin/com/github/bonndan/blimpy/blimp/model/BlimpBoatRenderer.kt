package com.github.bonndan.blimpy.blimp.model

import com.github.bonndan.blimpy.BlimpyMod
import com.github.bonndan.blimpy.blimp.entity.BlimpEntity
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.client.model.EntityModel
import net.minecraft.client.renderer.SubmitNodeCollector
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.rendertype.RenderTypes
import net.minecraft.client.renderer.state.level.CameraRenderState
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.resources.Identifier
import net.minecraft.resources.Identifier.fromNamespaceAndPath
import net.minecraft.util.Mth
import net.minecraft.world.item.DyeColor
import kotlin.math.max

/**
 * This is based on AbstractBoatRenderer, updated for the MC 26.1 rendering API.
 */
class BlimpBoatRenderer(context: EntityRendererProvider.Context) :
    EntityRenderer<BlimpEntity, BlimpRenderState>(context) {

    private val model: EntityModel<BlimpRenderState>
    private val colorModel: EntityModel<BlimpRenderState>
    private val texture: Identifier = fromNamespaceAndPath(BlimpyMod.MOD_ID, "textures/entity/blimp_texture.png")

    init {
        this.shadowRadius = 0.8f
        this.model = BlimpBodyModel(context.bakeLayer(BlimpBodyModel.LAYER_LOCATION))
        this.colorModel = BlimpTintModel(context.bakeLayer(BlimpTintModel.LAYER_LOCATION))
    }

    override fun submit(
        state: BlimpRenderState,
        poseStack: PoseStack,
        submitNodeCollector: SubmitNodeCollector,
        camera: CameraRenderState,
    ) {
        poseStack.pushPose()
        poseStack.translate(0.0f, 1.5f, 0.0f)
        poseStack.mulPose(Axis.YP.rotationDegrees(270.0f - state.yRot)) //TODO extra 90, fix in model
        val f = state.hurtTime
        if (f > 0.0f) {
            poseStack.mulPose(Axis.XP.rotationDegrees(Mth.sin(f.toDouble()) * f * state.damageTime / 10.0f * state.hurtDir.toFloat()))
        }

        poseStack.scale(-1.0f, -1.0f, 1.0f)
        poseStack.mulPose(Axis.YP.rotationDegrees(90.0f))
        model.setupAnim(state)

        submitNodeCollector.submitModel(model, state, poseStack, texture, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor, null)

        renderColorModel(poseStack, colorModel, submitNodeCollector, texture, state.lightCoords, state)
        poseStack.popPose()
        super.submit(state, poseStack, submitNodeCollector, camera)
    }

    private fun renderColorModel(
        poseStack: PoseStack,
        colorModel: EntityModel<BlimpRenderState>,
        submitNodeCollector: SubmitNodeCollector,
        colorTexture: Identifier,
        lightCoords: Int,
        renderState: BlimpRenderState,
    ) {
        poseStack.pushPose()
        val tintColor = DyeColor.byId(renderState.getColorId()).textureDiffuseColor
        submitNodeCollector.submitModel(
            colorModel,
            renderState,
            poseStack,
            RenderTypes.entityCutout(colorTexture),
            lightCoords,
            OverlayTexture.NO_OVERLAY,
            tintColor,
            null,
            renderState.outlineColor,
            null
        )
        poseStack.popPose()
    }

    override fun createRenderState(): BlimpRenderState {
        return BlimpRenderState()
    }

    override fun extractRenderState(entity: BlimpEntity, reusedState: BlimpRenderState, partialTick: Float) {
        super.extractRenderState(entity, reusedState, partialTick)

        reusedState.yRot = entity.getYRot(partialTick)
        reusedState.hurtTime = entity.getHurtTime().toFloat() - partialTick
        reusedState.hurtDir = entity.getHurtDir()
        reusedState.damageTime = max((entity.getDamage() - partialTick).toDouble(), 0.0).toFloat()
        reusedState.bubbleAngle = 0f
        reusedState.isUnderWater = entity.isUnderWater()
        reusedState.rowingTimeLeft = entity.getRowingTime(0, partialTick)
        reusedState.rowingTimeRight = entity.getRowingTime(1, partialTick)
        reusedState.setColorId(entity.getColorId())
    }
}

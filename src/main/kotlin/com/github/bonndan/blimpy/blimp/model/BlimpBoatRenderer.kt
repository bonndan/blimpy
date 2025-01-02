package com.github.bonndan.blimpy.blimp.model

import com.github.bonndan.blimpy.BlimpyMod
import com.github.bonndan.blimpy.blimp.entity.BlimpEntity
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.client.model.EntityModel
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.block.BlockRenderDispatcher
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth
import net.neoforged.api.distmarker.Dist
import net.neoforged.api.distmarker.OnlyIn
import kotlin.math.max

/**
 * This is a copy of the AbstractBoatRenderer
 */
@OnlyIn(Dist.CLIENT)
class BlimpBoatRenderer(context: EntityRendererProvider.Context) :
    EntityRenderer<BlimpEntity, BlimpRenderState>(context) {

    private val blockRenderer: BlockRenderDispatcher
    private val model: EntityModel<BlimpRenderState>
    private val texture: ResourceLocation = ResourceLocation.fromNamespaceAndPath(BlimpyMod.MOD_ID, "textures/entity/barge/steam_tug_texture.png")

    init {
        this.shadowRadius = 0.8f
        this.model = BlimpSteamTugModel(context.bakeLayer(BlimpSteamTugModel.LAYER_LOCATION))
        this.blockRenderer = context.blockRenderDispatcher
    }

    override fun render(
        renderState: BlimpRenderState,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int,
    ) {
        poseStack.pushPose()
        poseStack.translate(0.0f, 0.375f, 0.0f)
        poseStack.mulPose(Axis.YP.rotationDegrees(270.0f - renderState.yRot)) //TODO extra 90, fix in model
        val f = renderState.hurtTime
        if (f > 0.0f) {
            poseStack.mulPose(Axis.XP.rotationDegrees(Mth.sin(f) * f * renderState.damageTime / 10.0f * renderState.hurtDir.toFloat()))
        }

        poseStack.scale(-1.0f, -1.0f, 1.0f)
        poseStack.mulPose(Axis.YP.rotationDegrees(90.0f))
        poseStack.translate(0.0, -1.0, 0.0) //raise model
        model.setupAnim(renderState)

        val vertexconsumer = bufferSource.getBuffer(model.renderType(this.texture))
        model.renderToBuffer(poseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY)

        poseStack.popPose()
        super.render(renderState, poseStack, bufferSource, packedLight)
    }

    private fun renderAdditionalModel(
        poseStack: PoseStack,
        colorModel: EntityModel<BlimpRenderState>,
        buffer: MultiBufferSource,
        colorTexture: ResourceLocation,
        packedLight: Int,
        yOffset: Float,
        yRotation: Float,
    ) {
        poseStack.pushPose()
        poseStack.translate(0f, yOffset, 0f)
        poseStack.mulPose(Axis.YP.rotationDegrees(yRotation))
        colorModel.renderToBuffer(
            poseStack,
            buffer.getBuffer(RenderType.entityCutoutNoCull(colorTexture)),
            packedLight,
            OverlayTexture.NO_OVERLAY,
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
    }
}

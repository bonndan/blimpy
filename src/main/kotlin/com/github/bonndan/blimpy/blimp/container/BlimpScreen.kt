package com.github.bonndan.blimpy.blimp.container

import com.github.bonndan.blimpy.BlimpyMod.Companion.MOD_ID
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.Tooltip
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Inventory

class BlimpScreen(menu: BlimpMenu, inventory: Inventory, component: Component) :
    AbstractContainerScreen<BlimpMenu>(menu, inventory, component) {

    private lateinit var on: Button
    private lateinit var off: Button

    override fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTicks: Float) {
        this.renderBackground(graphics, mouseX, mouseY, partialTicks)
        super.render(graphics, mouseX, mouseY, partialTicks)
        this.renderTooltip(graphics, mouseX, mouseY)
    }

    override fun init() {
        super.init()
        on = Button.Builder(Component.literal("->")) { menu.setEngineState(true) }
            .pos(this.guiLeft + 130, this.guiTop + 23)
            .size(20, 20)
            .tooltip(tooltipOf("screen.blimpy.engine.on"))
            .build()

        off = Button.Builder(Component.literal("x")) { menu.setEngineState(false) }
            .pos(this.guiLeft + 104, this.guiTop + 23)
            .size(20, 20)
            .tooltip(tooltipOf("screen.blimpy.engine.off"))
            .build()

        this.addRenderableWidget(off)
        this.addRenderableWidget(on)
    }

    private fun tooltipOf(translatableString: String): Tooltip {
        return Tooltip.create(Component.translatable(translatableString))
    }

    override fun renderBg(graphics: GuiGraphics, pPartialTick: Float, x: Int, y: Int) {

        //RenderSystem.setShader(CoreShaders.POSITION_TEX)
        //RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F)
        off.active = menu.isOn
        on.active = !menu.isOn

        val i = this.guiLeft
        val j = this.guiTop

        graphics.blit(RenderPipelines.GUI_TEXTURED, GUI, i, j, 0f, 0f, this.xSize, this.ySize, 256, 256)
        if (menu.isLit) {
            val progress = menu.getBurnProgress()
            val x1 = i + 80
            val y1 = j + 21 - progress
            val uOffset = 176f
            val vOffset = 12f - progress
            val uWidth = 14
            val vHeight = progress + 1

            graphics.blit(RenderPipelines.GUI_TEXTURED, GUI, x1, y1, uOffset, vOffset, uWidth, vHeight, 256, 256)
        }
    }

    companion object {
        private val GUI: ResourceLocation =
            ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/container/blimp_container.png")
    }
}

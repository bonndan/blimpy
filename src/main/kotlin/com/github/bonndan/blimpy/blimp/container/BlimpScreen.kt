package com.github.bonndan.blimpy.blimp.container

import com.github.bonndan.blimpy.BlimpyMod.Companion.MOD_ID
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.Tooltip
import net.minecraft.client.renderer.CoreShaders
import net.minecraft.client.renderer.RenderType
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Inventory

class BlimpScreen(menu: BlimpMenu, inventory: Inventory, component: Component) :
    AbstractVehicleScreen<BlimpMenu>(menu, inventory, component) {

    private lateinit var on: Button
    private lateinit var off: Button

    private fun tooltipOf(translatableString: String): Tooltip {
        return Tooltip.create(Component.translatable(translatableString))
    }

    override fun init() {
        super.init()
        on = Button.Builder(Component.literal("->")) { menu.setEngineState(true) }
            .pos(this.guiLeft + 130, this.guiTop + 25)
            .size(20, 20)
            .tooltip(tooltipOf("screen.blimpy.locomotive.on"))
            .build()

        off = Button.Builder(Component.literal("x")) { menu.setEngineState(false) }
            .pos(this.guiLeft + 96, this.guiTop + 25)
            .size(20, 20)
            .tooltip(tooltipOf("screen.blimpy.locomotive.off"))
            .build()

        this.addRenderableWidget(off)
        this.addRenderableWidget(on)
    }

    override fun renderBg(graphics: GuiGraphics, pPartialTick: Float, x: Int, y: Int) {

        RenderSystem.setShader(CoreShaders.POSITION_TEX)
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F)
        off.active = menu.isOn
        on.active = !menu.isOn

        val i = this.guiLeft
        val j = this.guiTop

        graphics.blit(RenderType::guiTextured, GUI, i, j, 0f, 0f, this.xSize, this.ySize, 256, 256)
        if (menu.isLit) {
            val k = menu.getBurnProgress()
            graphics.blit(RenderType::guiTextured, GUI, i + 43, j + 23 + 12 - k, 176f, 12f - k, 14, k + 1, 256, 256)
        }
    }

    companion object {
        private val GUI: ResourceLocation =
            ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/container/steam_locomotive.png")
    }
}

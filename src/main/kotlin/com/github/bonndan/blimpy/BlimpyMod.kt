package com.github.bonndan.blimpy

import com.github.bonndan.blimpy.blimp.container.BlimpScreen
import com.github.bonndan.blimpy.setup.ModMenuTypes
import com.github.bonndan.blimpy.setup.Registration
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.ModContainer
import net.neoforged.fml.common.Mod
import net.neoforged.fml.config.ModConfig
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent

@Mod(BlimpyMod.MOD_ID)
class BlimpyMod(modBus: IEventBus, container: ModContainer) {

    init {
        Registration.register(modBus)

        modBus.addListener(RegisterMenuScreensEvent::class.java) {
            event: RegisterMenuScreensEvent -> this.registerScreens(event)
        }
    }

    private fun registerScreens(event: RegisterMenuScreensEvent) {

        event.register(ModMenuTypes.BLIMP_MENU.get(), ::BlimpScreen)
    }

    companion object {
        // The value here should match an entry in the META-INF/mods.toml file
        const val MOD_ID: String = "blimpy"
    }
}

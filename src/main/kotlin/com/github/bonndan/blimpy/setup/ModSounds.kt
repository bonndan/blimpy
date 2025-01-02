package com.github.bonndan.blimpy.setup

import com.github.bonndan.blimpy.BlimpyMod
import net.minecraft.resources.ResourceLocation
import net.minecraft.sounds.SoundEvent
import java.util.function.Supplier

object ModSounds {
    
    val STEAM_WHISTLE: Supplier<SoundEvent> = Registration.SOUND_EVENTS.register("steam_tug_whistle",
        Supplier {
            SoundEvent.createFixedRangeEvent(
                ResourceLocation.fromNamespaceAndPath(
                    BlimpyMod.MOD_ID,
                    "steam_tug_whistle"
                ), 64f
            )
        })

    
    val DOCKING: Supplier<SoundEvent> = Registration.SOUND_EVENTS.register("tug_docking",
        Supplier {
            SoundEvent.createFixedRangeEvent(
                ResourceLocation.fromNamespaceAndPath(
                    BlimpyMod.MOD_ID,
                    "tug_docking"
                ), 64f
            )
        })

    
    val UNDOCKING: Supplier<SoundEvent> = Registration.SOUND_EVENTS.register("tug_undocking",
        Supplier {
            SoundEvent.createFixedRangeEvent(
                ResourceLocation.fromNamespaceAndPath(
                    BlimpyMod.MOD_ID,
                    "tug_undocking"
                ), 64f
            )
        })

    fun register() {}
}

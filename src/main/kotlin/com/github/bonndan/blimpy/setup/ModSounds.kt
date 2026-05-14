package com.github.bonndan.blimpy.setup

import com.github.bonndan.blimpy.BlimpyMod
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.Identifier.fromNamespaceAndPath
import net.minecraft.sounds.SoundEvent
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister
import net.neoforged.neoforge.registries.DeferredRegister.create
import java.util.function.Supplier

object ModSounds {

    private val SOUND_EVENTS: DeferredRegister<SoundEvent> = create(BuiltInRegistries.SOUND_EVENT, BlimpyMod.MOD_ID)

    val HISS: Supplier<SoundEvent> = SOUND_EVENTS.register(
        "hiss",
        Supplier {
            SoundEvent.createFixedRangeEvent(fromNamespaceAndPath(BlimpyMod.MOD_ID, "hiss"), 64F)
        })

    fun register(eventBus: IEventBus) {
        SOUND_EVENTS.register(eventBus)
    }

}

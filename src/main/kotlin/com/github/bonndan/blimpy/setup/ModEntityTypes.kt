package com.github.bonndan.blimpy.setup

import com.github.bonndan.blimpy.BlimpyMod
import com.github.bonndan.blimpy.blimp.entity.BlimpEntity
import com.github.bonndan.blimpy.setup.Registration.ENTITIES
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.MobCategory
import net.minecraft.world.level.Level
import java.util.function.Supplier

private const val BLIMP_DIMENSION_WIDTH = 1.2f
private const val BLIMP_DIMENSION_HEIGHT = 0.7f

object ModEntityTypes {

    fun register() {
    }

    val BLIMP: Supplier<EntityType<BlimpEntity>> =
        ENTITIES.register("blimp", Supplier {
            EntityType.Builder.of(
                { type: EntityType<BlimpEntity>, level: Level -> BlimpEntity(type, level) { ModItems.BLIMP.get() } },
                MobCategory.MISC
            )
                .sized(BLIMP_DIMENSION_WIDTH, BLIMP_DIMENSION_HEIGHT)
                .clientTrackingRange(8)
                .setShouldReceiveVelocityUpdates(true)
                .build(asResourceKey("blimp"))
        })

    private fun asResourceKey(path: String): ResourceKey<EntityType<*>?> = ResourceKey.create(
        Registries.ENTITY_TYPE,
        ResourceLocation.fromNamespaceAndPath(BlimpyMod.MOD_ID, path)
    )
}

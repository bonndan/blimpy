package com.github.bonndan.blimpy.locomotive.item

import com.github.bonndan.blimpy.locomotive.entity.LocomotiveEntity
import com.github.bonndan.blimpy.setup.ModEntityTypes
import net.minecraft.server.level.ServerLevel
import net.minecraft.stats.Stats
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntitySelector
import net.minecraft.world.entity.EntitySpawnReason
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.ClipContext
import net.minecraft.world.level.Level
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.HitResult

class LocomotiveItem(props: Properties) : Item(props) {


    override fun use(world: Level, player: Player, hand: InteractionHand): InteractionResult {

        val itemstack = player.getItemInHand(hand)
        val hitResult = getPlayerPOVHitResult(world, player, ClipContext.Fluid.ANY)
        if (hitResult.type == HitResult.Type.MISS) {
            return InteractionResult.PASS
        }

        val vector3d = player.getViewVector(1.0f)
        val entities = world.getEntities(
            player, player.boundingBox.expandTowards(vector3d.scale(5.0)).inflate(1.0),
            EntitySelector.NO_SPECTATORS.and { obj: Entity -> obj.isPickable })
        if (entities.isNotEmpty()) {
            val vector3d1 = player.getEyePosition(1.0f)

            for (entity in entities) {
                val axisalignedBox = entity.boundingBox.inflate(entity.pickRadius.toDouble())
                if (axisalignedBox.contains(vector3d1)) {
                    return InteractionResult.PASS
                }
            }
        }

        if (hitResult.type == HitResult.Type.BLOCK) {
            val entity = getEntity(world, itemstack, hitResult, player)
            if (entity == null) {
                return InteractionResult.FAIL
            }

            entity.yRot = player.yRot

            return if (!world.noCollision(entity, entity.boundingBox.inflate(-0.1))) {
                InteractionResult.FAIL
            } else {
                if (!world.isClientSide) {
                    world.addFreshEntity(entity)
                    if (!player.abilities.instabuild) {
                        itemstack.shrink(1)
                    }
                }

                player.awardStat(Stats.ITEM_USED[this])
                InteractionResult.SUCCESS
            }
        }

        return InteractionResult.PASS
    }


    private fun getEntity(world: Level, stack: ItemStack, hitResult: BlockHitResult, player: Player): LocomotiveEntity? {

        val entityType: EntityType<LocomotiveEntity> = ModEntityTypes.LOCOMOTIVE.get()
        val locomotive: LocomotiveEntity? = entityType.create(world, EntitySpawnReason.SPAWN_ITEM_USE)
        if (locomotive != null) {
            val vec3 = hitResult.getLocation();
            locomotive.setInitialPos(vec3.x, vec3.y, vec3.z);
            if (world is ServerLevel) {
                EntityType<LocomotiveEntity>.createDefaultStackConfig<LocomotiveEntity>(world, stack, player).accept(locomotive);
            }
        }

        return locomotive
    }
}

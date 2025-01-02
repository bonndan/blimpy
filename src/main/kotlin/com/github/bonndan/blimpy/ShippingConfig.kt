package com.github.bonndan.blimpy

import net.neoforged.neoforge.common.ModConfigSpec

class ShippingConfig {
    object Common {
        val BUILDER: ModConfigSpec.Builder = ModConfigSpec.Builder()
        
        val SPEC: ModConfigSpec

        val CREATE_COMPAT: ModConfigSpec.ConfigValue<Boolean>

        init {
            BUILDER.push("compat")
                .comment("Additional compatibility features for third-party mods, disable if broken by a third-party mod update.")
            CREATE_COMPAT = BUILDER.define("create", true)
            BUILDER.pop()
            SPEC = BUILDER.build()
        }
    }

    object Client {
        val BUILDER: ModConfigSpec.Builder = ModConfigSpec.Builder()
        val SPEC: ModConfigSpec

        
        val TUG_SMOKE_MODIFIER: ModConfigSpec.ConfigValue<Double>
        
        val LOCO_SMOKE_MODIFIER: ModConfigSpec.ConfigValue<Double>
        
        val DISABLE_TUG_ROUTE_BEACONS: ModConfigSpec.ConfigValue<Boolean>

        init {
            BUILDER.push("general")
            TUG_SMOKE_MODIFIER =
                BUILDER.comment("Modify the rate of smoke produced by a tug. Min 0, Max 1, Default 0.4")
                    .defineInRange("tugSmoke", 0.4, 0.0, 1.0)

            LOCO_SMOKE_MODIFIER =
                BUILDER.comment("Modify the rate of smoke produced by a locomotive. Min 0, Max 1, Default 0.2")
                    .defineInRange("locomotiveSmoke", 0.2, 0.0, 1.0)

            DISABLE_TUG_ROUTE_BEACONS =
                BUILDER.comment("Disable indicator beacons for tug route item. Default false.")
                    .define("disableTugRouteBeacons", false)
            BUILDER.pop()

            SPEC = BUILDER.build()
        }
    }

    object Server {
        val BUILDER: ModConfigSpec.Builder = ModConfigSpec.Builder()
        
        val SPEC: ModConfigSpec
        
        var FISHING_TREASURE_CHANCE_MODIFIER: ModConfigSpec.ConfigValue<Double>? = null
        
        var FISHING_LOOT_TABLE: ModConfigSpec.ConfigValue<String>? = null
        
        var FISHING_COOLDOWN: ModConfigSpec.ConfigValue<Int>? = null

        
        var TUG_BASE_SPEED: ModConfigSpec.ConfigValue<Double>? = null

        
        var STEAM_TUG_FUEL_MULTIPLIER: ModConfigSpec.ConfigValue<Double>? = null

        
        var TUG_PATHFINDING_MULTIPLIER: ModConfigSpec.ConfigValue<Int>? = null
        
        var ENERGY_TUG_BASE_CAPACITY: ModConfigSpec.ConfigValue<Int>? = null
        
        var ENERGY_TUG_BASE_ENERGY_USAGE: ModConfigSpec.ConfigValue<Int>? = null
        
        var ENERGY_TUG_BASE_MAX_CHARGE_RATE: ModConfigSpec.ConfigValue<Int>? = null

        

        var LOCO_BASE_SPEED: ModConfigSpec.ConfigValue<Double>? = null

        
        var STEAM_LOCO_FUEL_MULTIPLIER: ModConfigSpec.ConfigValue<Double>? = null
        
        var ENERGY_LOCO_BASE_CAPACITY: ModConfigSpec.ConfigValue<Int>? = null
        
        var ENERGY_LOCO_BASE_ENERGY_USAGE: ModConfigSpec.ConfigValue<Int>? = null
        
        var ENERGY_LOCO_BASE_MAX_CHARGE_RATE: ModConfigSpec.ConfigValue<Int>? = null
        
        var TRAIN_EXEMPT_DAMAGE_SOURCES: ModConfigSpec.ConfigValue<List<String>>? = null
        
        var VESSEL_EXEMPT_DAMAGE_SOURCES: ModConfigSpec.ConfigValue<List<String>>? = null


        
        val CHUNK_LOADING_LEVEL: ModConfigSpec.ConfigValue<Int>
        
        val DISABLE_CHUNK_MANAGEMENT: ModConfigSpec.ConfigValue<Boolean>
        
        val MAX_REGISTRERED_VEHICLES_PER_PLAYER: ModConfigSpec.ConfigValue<Int>
        
        val OFFLINE_LOADING: ModConfigSpec.ConfigValue<Boolean>


        init {
            BUILDER.push("chunk management - requires restart")
            BUILDER.comment("By default, little logistics allows players to register vehicles that will be loaded automatically. This is not regular chunkloading, no other ticking will happen in this chunks and no surrounding chunks will be loaded. A very minimal number of chunks will be loaded as \"border chunks\" where only LL entities are active by default.")

            CHUNK_LOADING_LEVEL =
                BUILDER.comment("Chunkloading level, from low perf impact to high. 0: no ticking (except LL, recommended), 1: tile entity ticking, 2: entity ticking (regular).")
                    .defineInRange("chunkLoadingLevel", 0, 0, 2)

            DISABLE_CHUNK_MANAGEMENT = BUILDER.comment("Completely disable the chunk management system.")
                .define("disableChunkManagement", false)

            MAX_REGISTRERED_VEHICLES_PER_PLAYER =
                BUILDER.comment("Maximum number of vehicles (barges/cars don't count, only Tugs/Locos) the player is able to register. Lowering this number will not de-register vehicles but will prevent the player from registering more.")
                    .defineInRange("maxVehiclesPerPlayer", 100, 0, 1000)

            OFFLINE_LOADING = BUILDER.comment("Load vehicles even when the player is offline")
                .define("offlineLoading", false)

            BUILDER.pop()
            BUILDER.push("vessel")
            run {
                BUILDER.push("general")
                VESSEL_EXEMPT_DAMAGE_SOURCES = BUILDER.comment("Damage sources that vessels are invulnerable to")
                    .defineList(
                        "vesselInvuln",
                        listOf("create.mechanical_saw", "create.mechanical_drill")
                    ) { _ -> true }
                BUILDER.pop()
            }
            run {
                BUILDER.push("barge")
                FISHING_TREASURE_CHANCE_MODIFIER =
                    BUILDER.comment(
                        "Modify the chance of using the treasure loot table with the auto fishing barge, other factors such as depth and overfishing still play a role. " +
                                "Default 0.02."
                    )
                        .define("fishingTreasureChance", 0.04)
                FISHING_LOOT_TABLE =
                    BUILDER.comment("Loot table to use when fishing barge catches a fish. Change to 'minecraft:gameplay/fishing' if some modded fish aren't being caught. Defaults to 'minecraft:gameplay/fishing/fish'.")
                        .define("fishingLootTable", "minecraft:gameplay/fishing/fish")

                FISHING_COOLDOWN =
                    BUILDER.comment("Cooldown before each fishing attempt")
                        .defineInRange("fishingCooldown", 40, 0, 200000)
                BUILDER.pop()
            }
            run {
                BUILDER.push("tug")
                TUG_BASE_SPEED =
                    BUILDER.comment("Base speed of the tugs. Default 2.4.")
                        .defineInRange("tugBaseSpeed", 2.4, 0.1, 10.0)

                TUG_PATHFINDING_MULTIPLIER =
                    BUILDER.comment("Multiplier for tug pathfinding search space, high values may impact performance. Default 1.")
                        .defineInRange("tugPathfindMult", 1, 1, 10)

                STEAM_TUG_FUEL_MULTIPLIER =
                    BUILDER.comment("Increases the burn duration of Steam tug fuel by N times when compared to furnace, must be >= 0.01. Default 4.0.")
                        .defineInRange("steamTugFuelMultiplier", 4.0, 0.01, Double.MAX_VALUE)

                ENERGY_TUG_BASE_CAPACITY =
                    BUILDER.comment("Base maximum capacity of the Energy tug in FE, must be an integer >= 1. Default 10000.")
                        .defineInRange("energyTugBaseCapacity", 10000, 1, Int.MAX_VALUE)
                ENERGY_TUG_BASE_ENERGY_USAGE =
                    BUILDER.comment("Base energy usage of the Energy tug in FE/tick, must be an integer >= 1. Default 1.")
                        .defineInRange("energyTugBaseEnergyUsage", 1, 1, Int.MAX_VALUE)
                ENERGY_TUG_BASE_MAX_CHARGE_RATE =
                    BUILDER.comment("Base max charge rate of the Energy tug in FE/tick, must be an integer >= 1. Default 100.")
                        .defineInRange("energyTugBaseMaxChargeRate", 100, 1, Int.MAX_VALUE)
                BUILDER.pop()
            }
            BUILDER.pop()
            BUILDER.push("dock")
            run {
                BUILDER.push("charger")
                BUILDER.pop()
            }
            BUILDER.pop()
            BUILDER.push("train")
            run {
                BUILDER.push("general")


                TRAIN_EXEMPT_DAMAGE_SOURCES = BUILDER.comment("Damage sources that trains are invulnerable to")
                    .defineList(
                        "trainInvuln",
                        listOf("create.mechanical_saw", "create.mechanical_drill")
                    ) { _ -> true }
                BUILDER.pop()
            }
            run {
                BUILDER.push("locomotive")
                LOCO_BASE_SPEED =
                    BUILDER.comment("Locomotive base speed. High speed may cause chunk loading lag or issues, not advised for servers or packs. Default 0.2, max is 0.9")
                        .defineInRange("locoBaseSpeed", 0.5, 0.01, 0.9)

                STEAM_LOCO_FUEL_MULTIPLIER =
                    BUILDER.comment("Increases the burn duration of Steam locomotive fuel by N times when compared to furnace, must be >= 0.01. Default 4.0.")
                        .defineInRange("steamLocoFuelMultiplier", 4.0, 0.01, Double.MAX_VALUE)

                ENERGY_LOCO_BASE_CAPACITY =
                    BUILDER.comment("Base maximum capacity of the Energy locomotive in FE, must be an integer >= 1. Default 10000.")
                        .defineInRange("energyLocoBaseCapacity", 10000, 1, Int.MAX_VALUE)
                ENERGY_LOCO_BASE_ENERGY_USAGE =
                    BUILDER.comment("Base energy usage of the Energy locomotive in FE/tick, must be an integer >= 1. Default 1.")
                        .defineInRange("energyLocoBaseEnergyUsage", 1, 1, Int.MAX_VALUE)
                ENERGY_LOCO_BASE_MAX_CHARGE_RATE =
                    BUILDER.comment("Base max charge rate of the Energy locomotive in FE/tick, must be an integer >= 1. Default 100.")
                        .defineInRange("energyLocoBaseMaxChargeRate", 100, 1, Int.MAX_VALUE)
                BUILDER.pop()
            }
            BUILDER.pop()
            SPEC = BUILDER.build()
        }
    }
}

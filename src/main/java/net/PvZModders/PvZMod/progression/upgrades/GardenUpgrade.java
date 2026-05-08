package net.PvZModders.PvZMod.progression.upgrades;

import java.util.Locale;
import java.util.Optional;

public enum GardenUpgrade {
    SUN_CAP_I("sun_cap_1"),
    SUN_CAP_II("sun_cap_2"),
    SUN_CAP_III("sun_cap_3"),
    SUN_CAP_IV("sun_cap_4"),
    SUN_CAP_V("sun_cap_5"),
    SUN_CAP_VI("sun_cap_6"),
    FINAL_SUN_CAP("final_sun_cap"),
    ACTIVE_SEED_SLOT_I("active_seed_slot_1"),
    ACTIVE_SEED_SLOT_II("active_seed_slot_2"),
    SECOND_SEED_PAGE_UNLOCK("second_seed_page_unlock"),
    FINAL_SEED_HOLDER("final_seed_holder"),
    SEED_STORAGE_CAPACITY_I("seed_storage_capacity_1"),
    SEED_STORAGE_CAPACITY_II("seed_storage_capacity_2"),
    SEED_STORAGE_CAPACITY_III("seed_storage_capacity_3"),
    SEED_STORAGE_CAPACITY_IV("seed_storage_capacity_4"),
    SEED_STORAGE_CAPACITY_V("seed_storage_capacity_5"),
    SEED_REPLENISHMENT_SPEED_I("seed_replenishment_speed_1"),
    SEED_REPLENISHMENT_SPEED_II("seed_replenishment_speed_2"),
    SEED_REPLENISHMENT_SPEED_III("seed_replenishment_speed_3"),
    SEED_REPLENISHMENT_SPEED_IV("seed_replenishment_speed_4"),
    SEED_REPLENISHMENT_SPEED_V("seed_replenishment_speed_5"),
    SEED_REPLENISHMENT_SPEED_VI("seed_replenishment_speed_6"),
    TOTEM_SEED_STORAGE_I("totem_seed_storage_1"),
    TOTEM_SEED_STORAGE_II("totem_seed_storage_2"),
    TOTEM_SEED_STORAGE_III("totem_seed_storage_3"),
    TOTEM_SEED_STORAGE_IV("totem_seed_storage_4"),
    TOTEM_SEED_STORAGE_V("totem_seed_storage_5"),
    TOTEM_SEED_STORAGE_VI("totem_seed_storage_6"),
    FINAL_GARDEN_STORAGE("final_garden_storage"),
    MINIMUM_STARTING_SUN_I("minimum_starting_sun_1"),
    MINIMUM_STARTING_SUN_II("minimum_starting_sun_2"),
    MINIMUM_STARTING_SUN_III("minimum_starting_sun_3"),
    FROSTBITE_SNOWFALL_UPGRADE("frostbite_snowfall_upgrade"),
    COLD_GARDEN_EFFICIENCY("cold_garden_efficiency"),
    GOLD_TILE_REPLENISHMENT_SYNERGY("gold_tile_replenishment_synergy"),
    POWER_TILE_UPGRADE_FOUNDATION("power_tile_upgrade_foundation"),
    WATER_GARDEN_CAPACITY("water_garden_capacity"),
    PLANT_SHOVEL_I("plant_shovel_1"),
    PLANT_SHOVEL_II("plant_shovel_2"),
    PLANT_SHOVEL_III("plant_shovel_3"),
    RANGE_GOGGLES("range_goggles"),
    NIGHT_ECONOMY_STORAGE("night_economy_storage"),
    SUN_MAGNET_SYNERGY_FOUNDATION("sun_magnet_synergy_foundation"),
    TOTEM_SHIELD_PREP("totem_shield_prep"),
    DINOSAUR_MECHANIC_INTENSIFIES("dinosaur_mechanic_intensifies");

    private final String id;

    GardenUpgrade(String id) {
        this.id = id;
    }

    public String id() {
        return id;
    }

    public static Optional<GardenUpgrade> byId(String id) {
        if (id == null || id.isBlank()) {
            return Optional.empty();
        }
        for (GardenUpgrade upgrade : values()) {
            if (upgrade.id.equals(id) || upgrade.name().toLowerCase(Locale.ROOT).equals(id)) {
                return Optional.of(upgrade);
            }
        }
        return Optional.empty();
    }
}

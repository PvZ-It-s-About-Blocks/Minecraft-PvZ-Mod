package net.PvZModders.PvZMod.progression.upgrades;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

public enum GardenUpgradeCategory {
    SUN_CAP("sun_cap", "Sun Cap", List.of(
            GardenUpgrade.SUN_CAP_I,
            GardenUpgrade.SUN_CAP_II,
            GardenUpgrade.SUN_CAP_III,
            GardenUpgrade.SUN_CAP_IV,
            GardenUpgrade.SUN_CAP_V,
            GardenUpgrade.SUN_CAP_VI,
            GardenUpgrade.FINAL_SUN_CAP
    )),
    ACTIVE_SEED_SLOTS("active_seed_slots", "Active Seed Slots", List.of(
            GardenUpgrade.ACTIVE_SEED_SLOT_I,
            GardenUpgrade.ACTIVE_SEED_SLOT_II,
            GardenUpgrade.SECOND_SEED_PAGE_UNLOCK,
            GardenUpgrade.FINAL_SEED_HOLDER
    )),
    SEED_STORAGE_CAPACITY("seed_storage_capacity", "Seed Storage", List.of(
            GardenUpgrade.SEED_STORAGE_CAPACITY_I,
            GardenUpgrade.SEED_STORAGE_CAPACITY_II,
            GardenUpgrade.SEED_STORAGE_CAPACITY_III,
            GardenUpgrade.SEED_STORAGE_CAPACITY_IV,
            GardenUpgrade.SEED_STORAGE_CAPACITY_V,
            GardenUpgrade.FINAL_SEED_HOLDER,
            GardenUpgrade.WATER_GARDEN_CAPACITY
    )),
    SEED_REPLENISHMENT_SPEED("seed_replenishment_speed", "Seed Replenishment", List.of(
            GardenUpgrade.SEED_REPLENISHMENT_SPEED_I,
            GardenUpgrade.SEED_REPLENISHMENT_SPEED_II,
            GardenUpgrade.SEED_REPLENISHMENT_SPEED_III,
            GardenUpgrade.SEED_REPLENISHMENT_SPEED_IV,
            GardenUpgrade.SEED_REPLENISHMENT_SPEED_V,
            GardenUpgrade.SEED_REPLENISHMENT_SPEED_VI
    )),
    TOTEM_SEED_STORAGE("totem_seed_storage", "Totem Seed Storage", List.of(
            GardenUpgrade.TOTEM_SEED_STORAGE_I,
            GardenUpgrade.TOTEM_SEED_STORAGE_II,
            GardenUpgrade.TOTEM_SEED_STORAGE_III,
            GardenUpgrade.TOTEM_SEED_STORAGE_IV,
            GardenUpgrade.TOTEM_SEED_STORAGE_V,
            GardenUpgrade.TOTEM_SEED_STORAGE_VI,
            GardenUpgrade.FINAL_GARDEN_STORAGE
    )),
    MINIMUM_STARTING_SUN("minimum_starting_sun", "Minimum Starting Sun", List.of(
            GardenUpgrade.MINIMUM_STARTING_SUN_I,
            GardenUpgrade.MINIMUM_STARTING_SUN_II,
            GardenUpgrade.MINIMUM_STARTING_SUN_III
    )),
    PLANT_SHOVEL("plant_shovel", "Plant Shovel", List.of(
            GardenUpgrade.PLANT_SHOVEL_II
    ));

    private final String id;
    private final String displayName;
    private final List<GardenUpgrade> tiers;

    GardenUpgradeCategory(String id, String displayName, List<GardenUpgrade> tiers) {
        this.id = id;
        this.displayName = displayName;
        this.tiers = List.copyOf(tiers);
    }

    public String id() {
        return id;
    }

    public String displayName() {
        return displayName;
    }

    public List<GardenUpgrade> tiers() {
        return tiers;
    }

    public int maxTier() {
        return tiers.size();
    }

    public Optional<GardenUpgrade> upgradeForTier(int tier) {
        if (tier <= 0 || tier > tiers.size()) {
            return Optional.empty();
        }
        return Optional.of(tiers.get(tier - 1));
    }

    public int tierOf(GardenUpgrade upgrade) {
        int index = tiers.indexOf(upgrade);
        return index < 0 ? 0 : index + 1;
    }

    public static Optional<GardenUpgradeCategory> byId(String id) {
        if (id == null || id.isBlank()) {
            return Optional.empty();
        }
        String normalized = id.toLowerCase(Locale.ROOT);
        for (GardenUpgradeCategory category : values()) {
            if (category.id.equals(normalized) || category.name().toLowerCase(Locale.ROOT).equals(normalized)) {
                return Optional.of(category);
            }
        }
        return Optional.empty();
    }

    public static Optional<GardenUpgradeCategory> forUpgrade(GardenUpgrade upgrade) {
        for (GardenUpgradeCategory category : values()) {
            if (category.tiers.contains(upgrade)) {
                return Optional.of(category);
            }
        }
        return Optional.empty();
    }
}

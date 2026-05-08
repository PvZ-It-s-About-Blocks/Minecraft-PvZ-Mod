package net.PvZModders.PvZMod.progression.upgrades;

import net.PvZModders.PvZMod.progression.plants.GardenPlantDefinition;
import net.PvZModders.PvZMod.progression.seed.PlantSeedDefinition;
import net.PvZModders.PvZMod.progression.seed.SeedStorage;
import net.PvZModders.PvZMod.progression.sun.SunManager;
import net.minecraft.resources.ResourceLocation;

public final class PvZUpgradeValues {
    public static final int DEFAULT_SEED_REFILL_SECONDS_PER_SUN = 2;
    public static final int MIN_SEED_REFILL_TIME_TICKS = 20 * 100;
    public static final int MIN_UPGRADED_SEED_REFILL_TIME_TICKS = 20 * 60;
    public static final int BASE_GARDEN_PACKET_CAP = 40;
    public static final int BASE_MINIMUM_WAVE_START_SUN = 50;

    private PvZUpgradeValues() {
    }

    public static int baseSeedRefillTimeTicks(PlantSeedDefinition definition) {
        return Math.max(MIN_SEED_REFILL_TIME_TICKS, definition.sunCost() * DEFAULT_SEED_REFILL_SECONDS_PER_SUN * 20);
    }

    public static int baseSeedRefillTimeTicks(GardenPlantDefinition definition) {
        return Math.max(MIN_SEED_REFILL_TIME_TICKS, definition.sunCost() * DEFAULT_SEED_REFILL_SECONDS_PER_SUN * 20);
    }

    public static int getSeedRefillTimeTicks(PlantSeedDefinition definition, PvZUpgradeSavedData upgrades) {
        return adjustedSeedRefillTimeTicks(baseSeedRefillTimeTicks(definition), upgrades);
    }

    public static int getSeedRefillTimeTicks(GardenPlantDefinition definition, PvZUpgradeSavedData upgrades) {
        return adjustedSeedRefillTimeTicks(baseSeedRefillTimeTicks(definition), upgrades);
    }

    public static int getSeedRefillTimeTicks(ResourceLocation seedPacketId, PvZUpgradeSavedData upgrades) {
        return PlantSeedDefinition.get(seedPacketId)
                .map(definition -> getSeedRefillTimeTicks(definition, upgrades))
                .orElse(MIN_SEED_REFILL_TIME_TICKS);
    }

    public static int getSeedRefillTimeTicks(String plantId, PvZUpgradeSavedData upgrades) {
        return PlantSeedDefinition.getByPlantId(plantId)
                .map(definition -> getSeedRefillTimeTicks(definition, upgrades))
                .orElse(MIN_SEED_REFILL_TIME_TICKS);
    }

    public static int adjustedSeedRefillTimeTicks(int baseTicks, PvZUpgradeSavedData upgrades) {
        return Math.max(MIN_UPGRADED_SEED_REFILL_TIME_TICKS, Math.round(baseTicks * seedRefillMultiplier(upgrades)));
    }

    public static float seedRefillMultiplier(PvZUpgradeSavedData upgrades) {
        if (upgrades.isUnlocked(GardenUpgrade.SEED_REPLENISHMENT_SPEED_VI)) {
            return 0.65F;
        }
        if (upgrades.isUnlocked(GardenUpgrade.SEED_REPLENISHMENT_SPEED_V)) {
            return 0.70F;
        }
        if (upgrades.isUnlocked(GardenUpgrade.SEED_REPLENISHMENT_SPEED_IV)) {
            return 0.75F;
        }
        if (upgrades.isUnlocked(GardenUpgrade.SEED_REPLENISHMENT_SPEED_III)) {
            return 0.80F;
        }
        if (upgrades.isUnlocked(GardenUpgrade.SEED_REPLENISHMENT_SPEED_II)) {
            return 0.85F;
        }
        if (upgrades.isUnlocked(GardenUpgrade.SEED_REPLENISHMENT_SPEED_I)) {
            return 0.90F;
        }
        return 1.0F;
    }

    public static int playerSunCap(PvZUpgradeSavedData upgrades) {
        int cap = SunManager.DEFAULT_SUN_CAP;
        if (upgrades.isUnlocked(GardenUpgrade.SUN_CAP_I)) cap += 100;
        if (upgrades.isUnlocked(GardenUpgrade.SUN_CAP_II)) cap += 100;
        if (upgrades.isUnlocked(GardenUpgrade.SUN_CAP_III)) cap += 150;
        if (upgrades.isUnlocked(GardenUpgrade.SUN_CAP_IV)) cap += 150;
        if (upgrades.isUnlocked(GardenUpgrade.SUN_CAP_V)) cap += 200;
        if (upgrades.isUnlocked(GardenUpgrade.SUN_CAP_VI)) cap += 200;
        if (upgrades.isUnlocked(GardenUpgrade.FINAL_SUN_CAP)) cap += 300;
        return cap;
    }

    public static int pageOneUnlockedSlots(PvZUpgradeSavedData upgrades) {
        if (upgrades.isUnlocked(GardenUpgrade.ACTIVE_SEED_SLOT_II) || upgrades.isUnlocked(GardenUpgrade.FINAL_SEED_HOLDER)) {
            return SeedStorage.PLANT_SLOTS_PER_PAGE;
        }
        if (upgrades.isUnlocked(GardenUpgrade.ACTIVE_SEED_SLOT_I)) {
            return 7;
        }
        return 6;
    }

    public static boolean secondPageUnlocked(PvZUpgradeSavedData upgrades) {
        return upgrades.isUnlocked(GardenUpgrade.SECOND_SEED_PAGE_UNLOCK) || upgrades.isUnlocked(GardenUpgrade.FINAL_SEED_HOLDER);
    }

    public static int pageTwoUnlockedSlots(PvZUpgradeSavedData upgrades) {
        if (upgrades.isUnlocked(GardenUpgrade.FINAL_SEED_HOLDER)) {
            return SeedStorage.PLANT_SLOTS_PER_PAGE;
        }
        return secondPageUnlocked(upgrades) ? 6 : 0;
    }

    public static int playerSeedPacketCap(PvZUpgradeSavedData upgrades) {
        int cap = SeedStorage.BASE_PLAYER_PACKET_CAP;
        if (upgrades.isUnlocked(GardenUpgrade.SEED_STORAGE_CAPACITY_I)) cap += 10;
        if (upgrades.isUnlocked(GardenUpgrade.SEED_STORAGE_CAPACITY_II)) cap += 10;
        if (upgrades.isUnlocked(GardenUpgrade.SEED_STORAGE_CAPACITY_III)) cap += 15;
        if (upgrades.isUnlocked(GardenUpgrade.SEED_STORAGE_CAPACITY_IV)) cap += 15;
        if (upgrades.isUnlocked(GardenUpgrade.SEED_STORAGE_CAPACITY_V)) cap += 20;
        if (upgrades.isUnlocked(GardenUpgrade.FINAL_SEED_HOLDER)) cap += 20;
        if (upgrades.isUnlocked(GardenUpgrade.WATER_GARDEN_CAPACITY)) cap += 10;
        return cap;
    }

    public static int gardenPacketCap(PvZUpgradeSavedData upgrades) {
        int cap = BASE_GARDEN_PACKET_CAP;
        if (upgrades.isUnlocked(GardenUpgrade.TOTEM_SEED_STORAGE_I)) cap += 10;
        if (upgrades.isUnlocked(GardenUpgrade.TOTEM_SEED_STORAGE_II)) cap += 10;
        if (upgrades.isUnlocked(GardenUpgrade.TOTEM_SEED_STORAGE_III)) cap += 15;
        if (upgrades.isUnlocked(GardenUpgrade.TOTEM_SEED_STORAGE_IV)) cap += 15;
        if (upgrades.isUnlocked(GardenUpgrade.TOTEM_SEED_STORAGE_V)) cap += 20;
        if (upgrades.isUnlocked(GardenUpgrade.TOTEM_SEED_STORAGE_VI)) cap += 20;
        if (upgrades.isUnlocked(GardenUpgrade.FINAL_GARDEN_STORAGE)) cap += 30;
        return cap;
    }

    public static int minimumWaveStartSun(PvZUpgradeSavedData upgrades) {
        if (upgrades.isUnlocked(GardenUpgrade.MINIMUM_STARTING_SUN_III)) {
            return 150;
        }
        if (upgrades.isUnlocked(GardenUpgrade.MINIMUM_STARTING_SUN_II)) {
            return 100;
        }
        if (upgrades.isUnlocked(GardenUpgrade.MINIMUM_STARTING_SUN_I)) {
            return 75;
        }
        return BASE_MINIMUM_WAVE_START_SUN;
    }
}

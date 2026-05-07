package net.PvZModders.PvZMod.progression.waves;

import java.util.ArrayList;
import java.util.List;

public final class OriginalGardenWaves {
    public static final int MAX_WAVE = 30;
    private static final List<GardenWaveDefinition> WAVES = createWaves();

    private OriginalGardenWaves() {
    }

    public static List<GardenWaveDefinition> all() {
        return WAVES;
    }

    public static GardenWaveDefinition get(int wave) {
        return WAVES.get(Math.max(1, Math.min(MAX_WAVE, wave)) - 1);
    }

    private static List<GardenWaveDefinition> createWaves() {
        List<GardenWaveDefinition> waves = new ArrayList<>();
        for (int wave = 1; wave <= MAX_WAVE; wave++) {
            waves.add(new GardenWaveDefinition(
                    wave,
                    scanTextFor(wave),
                    rewardsFor(wave),
                    wave == MAX_WAVE ? WaveObjectiveType.BOSS : WaveObjectiveType.KILL_ALL_ZOMBIES,
                    spawnGroupsFor(wave),
                    wave == MAX_WAVE
            ));
        }
        return List.copyOf(waves);
    }

    private static List<WaveSpawnGroup> spawnGroupsFor(int wave) {
        int zombieCount = Math.min(45, 2 + wave + (wave / 5) * 2);
        int directionCount = wave >= 30 ? 4 : wave >= 20 ? 3 : wave >= 10 ? 2 : 1;
        List<WaveSpawnGroup> groups = new ArrayList<>();
        if (wave <= 2) {
            addGroup(groups, "basic_zombie", zombieCount, directionCount);
        } else if (wave == 3) {
            addGroup(groups, "basic_zombie", Math.max(1, zombieCount - 1), directionCount);
            addGroup(groups, "flag_zombie", 1, directionCount);
        } else if (wave <= 5) {
            addWeighted(groups, zombieCount, directionCount,
                    entry("basic_zombie", 75),
                    entry("conehead_zombie", 25));
        } else if (wave <= 7) {
            addWeighted(groups, zombieCount, directionCount,
                    entry("basic_zombie", 65),
                    entry("conehead_zombie", 25),
                    entry("newspaper_zombie", 10));
        } else if (wave <= 10) {
            addWeighted(groups, zombieCount - (wave == 10 ? 1 : 0), directionCount,
                    entry("basic_zombie", 55),
                    entry("conehead_zombie", 25),
                    entry("newspaper_zombie", 10),
                    entry("buckethead_zombie", 10));
            if (wave == 10) {
                addGroup(groups, "flag_zombie", 1, directionCount);
            }
        } else if (wave <= 13) {
            addWeighted(groups, zombieCount, directionCount,
                    entry("basic_zombie", 45),
                    entry("conehead_zombie", 20),
                    entry("buckethead_zombie", 15),
                    entry("newspaper_zombie", 10),
                    entry("screen_door_zombie", 10));
        } else if (wave <= 16) {
            addWeighted(groups, zombieCount, directionCount,
                    entry("basic_zombie", 45),
                    entry("conehead_zombie", 20),
                    entry("buckethead_zombie", 12),
                    entry("newspaper_zombie", 10),
                    entry("screen_door_zombie", 8),
                    entry("pole_vaulting_zombie", 5));
        } else if (wave <= 20) {
            addWeighted(groups, zombieCount, directionCount,
                    entry("basic_zombie", 40),
                    entry("conehead_zombie", 18),
                    entry("buckethead_zombie", 14),
                    entry("newspaper_zombie", 10),
                    entry("screen_door_zombie", 8),
                    entry("pole_vaulting_zombie", 5),
                    entry("football_zombie", 5));
        } else if (wave <= 24) {
            addWeighted(groups, zombieCount, directionCount,
                    entry("basic_zombie", 35),
                    entry("conehead_zombie", 15),
                    entry("buckethead_zombie", 15),
                    entry("newspaper_zombie", 10),
                    entry("screen_door_zombie", 10),
                    entry("pole_vaulting_zombie", 5),
                    entry("football_zombie", 5),
                    entry("imp", 5));
        } else if (wave <= 29) {
            int gargantuars = wave >= 28 ? 1 : 0;
            addWeighted(groups, zombieCount - gargantuars, directionCount,
                    entry("basic_zombie", 33),
                    entry("conehead_zombie", 14),
                    entry("buckethead_zombie", 14),
                    entry("newspaper_zombie", 10),
                    entry("screen_door_zombie", 10),
                    entry("pole_vaulting_zombie", 6),
                    entry("football_zombie", 6),
                    entry("imp", 7));
            addGroup(groups, "gargantuar", gargantuars, directionCount);
        } else {
            addWeighted(groups, zombieCount - 2, directionCount,
                    entry("basic_zombie", 28),
                    entry("conehead_zombie", 12),
                    entry("buckethead_zombie", 14),
                    entry("newspaper_zombie", 10),
                    entry("screen_door_zombie", 10),
                    entry("pole_vaulting_zombie", 7),
                    entry("football_zombie", 7),
                    entry("imp", 12));
            addGroup(groups, "gargantuar", 2, directionCount);
        }
        return List.copyOf(groups);
    }

    private static void addWeighted(List<WaveSpawnGroup> groups, int totalCount, int directionCount, WeightedZombie... entries) {
        int remaining = Math.max(0, totalCount);
        int totalWeight = 0;
        for (WeightedZombie entry : entries) {
            totalWeight += entry.weight();
        }
        for (int i = 0; i < entries.length; i++) {
            WeightedZombie entry = entries[i];
            int count = i == entries.length - 1 ? remaining : (int) Math.floor(totalCount * (entry.weight() / (double) totalWeight));
            count = Math.min(remaining, count);
            addGroup(groups, entry.id(), count, directionCount);
            remaining -= count;
        }
    }

    private static void addGroup(List<WaveSpawnGroup> groups, String zombieId, int count, int directionCount) {
        if (count > 0) {
            groups.add(new WaveSpawnGroup("pvz2mod:" + zombieId, count, directionCount, List.of()));
        }
    }

    private static WeightedZombie entry(String id, int weight) {
        return new WeightedZombie(id, weight);
    }

    private record WeightedZombie(String id, int weight) {
    }

    private static String scanTextFor(int wave) {
        return switch (wave) {
            case 1 -> "Starter wave: Build your defense. Sunflower and Peashooter are supplied for free.";
            case 2, 3, 4 -> "Build Your Defense: basic zombies approach in simple lanes.";
            case 5 -> "Milestone Wave: expect a stronger push and unlock support after clearing.";
            case 6, 7, 8, 9 -> "Standard Defense: use sun economy and lane coverage.";
            case 10 -> "Milestone Wave: tougher armor appears. Reward scan detected.";
            case 11, 12, 13, 14 -> "Mixed Lanes: small waves may pressure multiple sides.";
            case 15 -> "Milestone Wave: defensive plant research opportunity.";
            case 16, 17, 18, 19 -> "Garden Stress Test: protect the Totem while expanding coverage.";
            case 20 -> "Milestone Wave: buried threat patterns detected.";
            case 21, 22, 23, 24 -> "Limited Plants: future tuning can restrict loadouts here.";
            case 25 -> "Milestone Wave: garden automation upgrade candidate.";
            case 26, 27, 28, 29 -> "Final Stretch: sustained pressure before completion.";
            case 30 -> "Completion Wave: survive the final Original Garden defense.";
            default -> "Build Your Defense.";
        };
    }

    private static List<WaveReward> rewardsFor(int wave) {
        return switch (wave) {
            case 1 -> List.of(new WaveReward(
                    WaveRewardType.GARDEN_SYSTEM_UNLOCK,
                    "garden_totem_activated",
                    "Garden Totem Activated",
                    "minecraft:grass_block",
                    "Confirms the player has started the first real garden defense."
            ));
            case 3 -> List.of(new WaveReward(
                    WaveRewardType.GARDEN_UPGRADE,
                    "minimum_starting_sun_1",
                    "Starting Sun Upgrade I",
                    "minecraft:wheat_seeds",
                    "World-wide: raises the minimum Sun players begin a wave with to 75."
            ));
            case 5 -> List.of(new WaveReward(
                    WaveRewardType.PLANT_UNLOCK,
                    "wall_nut",
                    "Wall-nut",
                    "minecraft:oak_log",
                    "First defensive plant. This teaches blocking and lane control."
            ));
            case 7 -> List.of(new WaveReward(
                    WaveRewardType.PLANT_UNLOCK,
                    "potato_mine",
                    "Potato Mine",
                    "minecraft:potato",
                    "First trap/explosive plant. This teaches delayed setup and emergency defense."
            ));
            case 10 -> List.of(new WaveReward(
                    WaveRewardType.ITEM_UNLOCK,
                    "biome_scouter",
                    "New Biome Detector and Crafting Recipe",
                    "pvz2mod:biome_detector",
                    "Penny gives a biome detector that will point toward a selected biome."
            ));
            case 12 -> List.of(new WaveReward(
                    WaveRewardType.PLANT_UNLOCK,
                    "repeater",
                    "Repeater / Double Peashooter",
                    "minecraft:arrow",
                    "First direct damage upgrade. Stronger offense after learning defense."
            ));
            case 15 -> List.of(new WaveReward(
                    WaveRewardType.PLAYER_UPGRADE,
                    "sun_cap_1",
                    "Sun Cap I",
                    "minecraft:sunflower",
                    "World-wide: permanently increases maximum Sun for everyone by 100."
            ));
            case 18 -> List.of(new WaveReward(
                    WaveRewardType.PLANT_UNLOCK,
                    "chomper",
                    "Chomper",
                    "minecraft:purple_dye",
                    "First close-range high-risk plant. Adds variety without giving it too early."
            ));
            case 20 -> List.of(new WaveReward(
                    WaveRewardType.PLAYER_UPGRADE,
                    "active_seed_slot_1",
                    "Seed Holder Active Slots I",
                    "minecraft:bundle",
                    "World-wide: permanently unlocks a 7th active plant slot for everyone."
            ));
            case 23 -> List.of(new WaveReward(
                    WaveRewardType.ITEM_UNLOCK,
                    "peashooter_armor_blueprint",
                    "Peashooter Armor Blueprint",
                    "minecraft:paper",
                    "Placeholder for the later armor system."
            ));
            case 25 -> List.of(new WaveReward(
                    WaveRewardType.GARDEN_UPGRADE,
                    "seed_storage_capacity_1",
                    "Seed Storage Capacity I",
                    "minecraft:chest",
                    "World-wide: permanently increases how many seed packets each Seed Holder slot can carry."
            ));
            case 30 -> List.of(
                    new WaveReward(
                            WaveRewardType.COMPLETION,
                            "original_garden_complete",
                            "Original Garden Complete",
                            "minecraft:ender_eye",
                            "Marks the Original Garden complete."
                    ),
                    new WaveReward(
                            WaveRewardType.ITEM_UNLOCK,
                            "original_garden_eye",
                            "Original Garden Eye",
                            "pvz2mod:original_garden_eye",
                            "Used for opening the End portal."
                    ),
                    new WaveReward(
                            WaveRewardType.ITEM_UNLOCK,
                            "almanac",
                            "Almanac",
                            "minecraft:book",
                            "Garden and enemy information placeholder."
                    )
            );
            default -> List.of();
        };
    }
}

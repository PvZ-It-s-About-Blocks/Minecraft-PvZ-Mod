package net.PvZModders.PvZMod.progression.waves;

import java.util.ArrayList;
import java.util.List;

public final class BigWaveBeachWaves {
    private static final List<GardenWaveDefinition> WAVES = createWaves();

    private BigWaveBeachWaves() {
    }

    public static List<GardenWaveDefinition> all() {
        return WAVES;
    }

    public static GardenWaveDefinition get(int wave) {
        return WAVES.get(Math.max(1, Math.min(OriginalGardenWaves.MAX_WAVE, wave)) - 1);
    }

    private static List<GardenWaveDefinition> createWaves() {
        List<GardenWaveDefinition> waves = new ArrayList<>();
        for (int wave = 1; wave <= OriginalGardenWaves.MAX_WAVE; wave++) {
            waves.add(new GardenWaveDefinition(
                    wave,
                    scanTextFor(wave),
                    rewardsFor(wave),
                    wave == OriginalGardenWaves.MAX_WAVE ? WaveObjectiveType.BOSS : WaveObjectiveType.KILL_ALL_ZOMBIES,
                    spawnGroupsFor(wave),
                    wave == OriginalGardenWaves.MAX_WAVE
            ));
        }
        return List.copyOf(waves);
    }

    private static List<WaveSpawnGroup> spawnGroupsFor(int wave) {
        int zombieCount = Math.min(58, 2 + wave + (wave / 5) * 3);
        int directionCount = wave >= 30 ? 4 : wave >= 20 ? 3 : wave >= 10 ? 2 : 1;
        List<WaveSpawnGroup> groups = new ArrayList<>();
        if (wave <= 2) {
            addGroup(groups, "beach_zombie", zombieCount, directionCount);
        } else if (wave == 3) {
            addGroup(groups, "beach_zombie", Math.max(1, zombieCount - 1), directionCount);
            addGroup(groups, "flag_beach_zombie", 1, directionCount);
        } else if (wave <= 5) {
            addWeighted(groups, zombieCount, directionCount,
                    entry("beach_zombie", 80),
                    entry("conehead_beach_zombie", 20));
        } else if (wave <= 8) {
            addWeighted(groups, zombieCount, directionCount,
                    entry("beach_zombie", 60),
                    entry("conehead_beach_zombie", 20),
                    entry("snorkel_zombie", 15),
                    entry("flag_beach_zombie", 5));
        } else if (wave <= 11) {
            addWeighted(groups, zombieCount, directionCount,
                    entry("beach_zombie", 55),
                    entry("conehead_beach_zombie", 20),
                    entry("snorkel_zombie", 10),
                    entry("surfer_zombie", 10),
                    entry("flag_beach_zombie", 5));
        } else if (wave <= 14) {
            addWeighted(groups, zombieCount, directionCount,
                    entry("beach_zombie", 45),
                    entry("conehead_beach_zombie", 15),
                    entry("snorkel_zombie", 12),
                    entry("surfer_zombie", 12),
                    entry("fisherman_zombie", 11),
                    entry("flag_beach_zombie", 5));
        } else if (wave <= 17) {
            addWeighted(groups, zombieCount, directionCount,
                    entry("beach_zombie", 40),
                    entry("conehead_beach_zombie", 15),
                    entry("buckethead_beach_zombie", 15),
                    entry("snorkel_zombie", 10),
                    entry("surfer_zombie", 10),
                    entry("fisherman_zombie", 10));
        } else if (wave <= 21) {
            addWeighted(groups, zombieCount, directionCount,
                    entry("beach_zombie", 38),
                    entry("conehead_beach_zombie", 13),
                    entry("buckethead_beach_zombie", 12),
                    entry("snorkel_zombie", 10),
                    entry("surfer_zombie", 10),
                    entry("fisherman_zombie", 10),
                    entry("octo_zombie", 7));
        } else if (wave <= 24) {
            addWeighted(groups, zombieCount, directionCount,
                    entry("beach_zombie", 32),
                    entry("conehead_beach_zombie", 10),
                    entry("buckethead_beach_zombie", 14),
                    entry("snorkel_zombie", 10),
                    entry("surfer_zombie", 10),
                    entry("fisherman_zombie", 10),
                    entry("octo_zombie", 8),
                    entry("mermaid_imp", 6));
        } else if (wave <= 29) {
            int gargantuars = wave >= 28 ? 1 : 0;
            addWeighted(groups, zombieCount - gargantuars, directionCount,
                    entry("beach_zombie", 30),
                    entry("conehead_beach_zombie", 10),
                    entry("buckethead_beach_zombie", 15),
                    entry("snorkel_zombie", 10),
                    entry("surfer_zombie", 10),
                    entry("fisherman_zombie", 10),
                    entry("octo_zombie", 8),
                    entry("mermaid_imp", 7));
            addGroup(groups, "deep_sea_gargantuar", gargantuars, directionCount);
        } else {
            addWeighted(groups, zombieCount - 2, directionCount,
                    entry("beach_zombie", 25),
                    entry("conehead_beach_zombie", 8),
                    entry("buckethead_beach_zombie", 14),
                    entry("flag_beach_zombie", 3),
                    entry("snorkel_zombie", 10),
                    entry("surfer_zombie", 10),
                    entry("fisherman_zombie", 10),
                    entry("octo_zombie", 10),
                    entry("mermaid_imp", 10));
            addGroup(groups, "deep_sea_gargantuar", 2, directionCount);
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
            case 1 -> "Big Wave Beach: Lily Pads support land plants on water.";
            case 3 -> "Tide warning: high tide can submerge unsupported land plants.";
            case 6, 11, 19, 27 -> "Plant unlock detected. Clear the wave to expand your beach loadout.";
            case 9 -> "Utility reward detected: Sea Pickle lighting unlock.";
            case 30 -> "Completion Wave: long high tide detected. Commander's Bucket reward available.";
            default -> "Beach zombies approach. Watch preset tide changes and keep land plants supported.";
        };
    }

    private static List<WaveReward> rewardsFor(int wave) {
        return switch (wave) {
            case 1 -> plant("lily_pad", "Lily Pad", "pvz2mod:lily_pad_seed_packet", "Supports land plants on flooded tiles.");
            case 3 -> List.of(new WaveReward(WaveRewardType.GARDEN_SYSTEM_UNLOCK, "tides", "Tides Introduced", "minecraft:water_bucket", "Preset high tides can submerge unsupported land plants."));
            case 6 -> plant("tangle_kelp", "Tangle Kelp", "pvz2mod:tangle_kelp_seed_packet", "Aquatic trap for nearby zombies.");
            case 9 -> List.of(new WaveReward(WaveRewardType.ITEM_UNLOCK, "sea_pickle", "Sea Pickle", "minecraft:sea_pickle", "Utility light source unlock."));
            case 11 -> plant("bowling_bulb", "Bowling Bulb", "pvz2mod:bowling_bulb_seed_packet", "Bounces rolling shots between zombies.");
            case 15 -> List.of(new WaveReward(WaveRewardType.GARDEN_UPGRADE, "totem_seed_storage_6", "Totem Seed Storage VI", "minecraft:prismarine_shard", "Totems can store more generated seeds."));
            case 19 -> plant("guacodile", "Guacodile", "pvz2mod:guacodile_seed_packet", "Amphibious ranged attacker that can rush.");
            case 22 -> List.of(new WaveReward(WaveRewardType.GARDEN_UPGRADE, "seed_replenishment_speed_6", "Seed Replenishment Speed VI", "minecraft:kelp", "Gardens replenish seeds 35% faster."));
            case 27 -> List.of(
                    new WaveReward(WaveRewardType.PLANT_UNLOCK, "banana_launcher", "Banana Launcher", "pvz2mod:banana_launcher_seed_packet", "Heavy splash artillery."),
                    new WaveReward(WaveRewardType.GARDEN_UPGRADE, "water_garden_capacity", "Water Garden Capacity", "minecraft:water_bucket", "Future hook for aquatic plant support and seed capacity.")
            );
            case 30 -> List.of(
                    new WaveReward(WaveRewardType.COMPLETION, "big_wave_beach_complete", "Big Wave Beach Garden Complete", "minecraft:heart_of_the_sea", "Marks Big Wave Beach complete."),
                    new WaveReward(WaveRewardType.ITEM_UNLOCK, "commanders_bucket", "Commander's Bucket", "pvz2mod:commanders_bucket", "Right-click a zombie to make reachable plants focus fire on it.")
            );
            default -> List.of();
        };
    }

    private static List<WaveReward> plant(String id, String name, String icon, String note) {
        return List.of(new WaveReward(WaveRewardType.PLANT_UNLOCK, id, name, icon, note));
    }
}

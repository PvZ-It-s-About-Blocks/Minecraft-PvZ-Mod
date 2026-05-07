package net.PvZModders.PvZMod.progression.waves;

import java.util.ArrayList;
import java.util.List;

public final class FrostbiteWaves {
    private static final List<GardenWaveDefinition> WAVES = createWaves();

    private FrostbiteWaves() {
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
        int zombieCount = Math.min(55, 3 + wave + (wave / 4) * 2);
        int directionCount = wave >= 30 ? 4 : wave >= 20 ? 3 : wave >= 10 ? 2 : 1;
        List<WaveSpawnGroup> groups = new ArrayList<>();
        if (wave <= 2) {
            addGroup(groups, "cave_zombie", zombieCount, directionCount);
        } else if (wave == 3) {
            addGroup(groups, "cave_zombie", Math.max(1, zombieCount - 1), directionCount);
            addGroup(groups, "flag_cave_zombie", 1, directionCount);
        } else if (wave <= 5) {
            addWeighted(groups, zombieCount, directionCount,
                    entry("cave_zombie", 80),
                    entry("conehead_cave_zombie", 20));
        } else if (wave <= 8) {
            addWeighted(groups, zombieCount, directionCount,
                    entry("cave_zombie", 58),
                    entry("conehead_cave_zombie", 24),
                    entry("hunter_zombie", 13),
                    entry("flag_cave_zombie", 5));
        } else if (wave <= 11) {
            addWeighted(groups, zombieCount, directionCount,
                    entry("cave_zombie", 45),
                    entry("conehead_cave_zombie", 20),
                    entry("hunter_zombie", 10),
                    entry("weasel_hoarder", 12),
                    entry("zombie_weasel", 8),
                    entry("flag_cave_zombie", 5));
        } else if (wave <= 14) {
            addWeighted(groups, zombieCount, directionCount,
                    entry("cave_zombie", 42),
                    entry("conehead_cave_zombie", 18),
                    entry("hunter_zombie", 10),
                    entry("weasel_hoarder", 10),
                    entry("ice_block_zombie", 12),
                    entry("zombie_weasel", 8));
        } else if (wave <= 17) {
            addWeighted(groups, zombieCount, directionCount,
                    entry("cave_zombie", 40),
                    entry("conehead_cave_zombie", 15),
                    entry("hunter_zombie", 10),
                    entry("weasel_hoarder", 10),
                    entry("ice_block_zombie", 12),
                    entry("troglobite", 8),
                    entry("zombie_weasel", 5));
        } else if (wave <= 20) {
            addWeighted(groups, zombieCount, directionCount,
                    entry("cave_zombie", 34),
                    entry("conehead_cave_zombie", 14),
                    entry("buckethead_cave_zombie", 15),
                    entry("hunter_zombie", 10),
                    entry("weasel_hoarder", 9),
                    entry("ice_block_zombie", 10),
                    entry("troglobite", 8));
        } else if (wave <= 23) {
            addWeighted(groups, zombieCount, directionCount,
                    entry("cave_zombie", 30),
                    entry("conehead_cave_zombie", 12),
                    entry("buckethead_cave_zombie", 14),
                    entry("hunter_zombie", 9),
                    entry("weasel_hoarder", 9),
                    entry("ice_block_zombie", 10),
                    entry("troglobite", 7),
                    entry("dodo_rider_zombie", 5),
                    entry("dodo", 4));
        } else if (wave <= 29) {
            int gargantuars = wave >= 27 ? 1 : 0;
            addWeighted(groups, zombieCount - gargantuars, directionCount,
                    entry("cave_zombie", 28),
                    entry("conehead_cave_zombie", 10),
                    entry("buckethead_cave_zombie", 14),
                    entry("hunter_zombie", 10),
                    entry("weasel_hoarder", 9),
                    entry("zombie_weasel", 5),
                    entry("ice_block_zombie", 10),
                    entry("troglobite", 7),
                    entry("dodo_rider_zombie", 4),
                    entry("dodo", 3));
            addGroup(groups, "sloth_gargantuar", gargantuars, directionCount);
        } else {
            addWeighted(groups, zombieCount - 2, directionCount,
                    entry("cave_zombie", 24),
                    entry("conehead_cave_zombie", 8),
                    entry("buckethead_cave_zombie", 12),
                    entry("flag_cave_zombie", 4),
                    entry("hunter_zombie", 10),
                    entry("troglobite", 8),
                    entry("ice_block_zombie", 10),
                    entry("weasel_hoarder", 8),
                    entry("zombie_weasel", 6),
                    entry("dodo_rider_zombie", 5),
                    entry("dodo", 5));
            addGroup(groups, "sloth_gargantuar", 2, directionCount);
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
            case 1 -> "Frostbite: Hot Potato can thaw plants. Heavy Snowfall begins on later waves.";
            case 3 -> "Heavy Snowfall detected: non-hot plants will frost over during preset storm windows.";
            case 6, 11, 19, 26 -> "Plant unlock detected. Clear the wave to expand your Frostbite loadout.";
            case 10, 15, 22, 27 -> "Frostbite garden upgrade placeholder detected.";
            case 30 -> "Completion Wave: survive a long Heavy Snowfall and claim the Freeze Ray.";
            default -> "Frostbite Caves zombies approach through the cold with hunters, ice blocks, and swarm pressure.";
        };
    }

    private static List<WaveReward> rewardsFor(int wave) {
        return switch (wave) {
            case 1 -> plant("hot_potato", "Hot Potato", "pvz2mod:hot_potato_seed_packet", "Thaws frozen, iced, or frosted plants.");
            case 3 -> List.of(new WaveReward(WaveRewardType.GARDEN_SYSTEM_UNLOCK, "heavy_snowfall", "Heavy Snowfall Introduced", "minecraft:snowball", "Preset snowstorms can freeze plants."));
            case 6 -> plant("pepper_pult", "Pepper-pult", "pvz2mod:pepper_pult_seed_packet", "Lobs peppers and warms nearby plants.");
            case 10 -> List.of(new WaveReward(WaveRewardType.GARDEN_UPGRADE, "totem_seed_storage_3", "Totem Seed Storage III", "minecraft:packed_ice", "Totems can store more generated seeds."));
            case 11 -> plant("chard_guard", "Chard Guard", "pvz2mod:chard_guard_seed_packet", "Shoves nearby zombies back with limited charges.");
            case 15 -> List.of(new WaveReward(WaveRewardType.GARDEN_UPGRADE, "frostbite_snowfall_upgrade", "Heavy Snowfall Intensity Upgrade", "minecraft:blue_ice", "Placeholder snowfall upgrade."));
            case 19 -> plant("stunion", "Stunion", "pvz2mod:stunion_seed_packet", "Stuns nearby zombies with gas.");
            case 22 -> List.of(new WaveReward(WaveRewardType.GARDEN_UPGRADE, "seed_storage_capacity_3", "Seed Storage Capacity III", "minecraft:ice", "Increases Seed Holder packet capacity."));
            case 26 -> plant("rotobaga", "Rotobaga", "pvz2mod:rotobaga_seed_packet", "Shoots diagonally in four directions.");
            case 27 -> List.of(new WaveReward(WaveRewardType.GARDEN_UPGRADE, "cold_garden_efficiency", "Cold Garden Efficiency", "minecraft:amethyst_shard", "Future hook for freeze resistance and snow-garden seed economy."));
            case 30 -> List.of(
                    new WaveReward(WaveRewardType.COMPLETION, "frostbite_complete", "Frostbite Garden Complete", "minecraft:snow_block", "Marks Frostbite complete."),
                    new WaveReward(WaveRewardType.ITEM_UNLOCK, "frostbite_eye", "Frostbite Garden Eye", "pvz2mod:frostbite_eye", "Awakens the Frostbite Portal Frame."),
                    new WaveReward(WaveRewardType.ITEM_UNLOCK, "freeze_ray", "Freeze Ray", "pvz2mod:freeze_ray", "Piercing slow beam reward.")
            );
            default -> List.of();
        };
    }

    private static List<WaveReward> plant(String id, String name, String icon, String note) {
        return List.of(new WaveReward(WaveRewardType.PLANT_UNLOCK, id, name, icon, note));
    }
}

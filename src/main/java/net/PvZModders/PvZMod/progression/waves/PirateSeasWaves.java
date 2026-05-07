package net.PvZModders.PvZMod.progression.waves;

import java.util.ArrayList;
import java.util.List;

public final class PirateSeasWaves {
    private static final List<GardenWaveDefinition> WAVES = createWaves();

    private PirateSeasWaves() {
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
        int zombieCount = Math.min(60, 2 + wave + (wave / 4) * 2);
        int directionCount = wave >= 30 ? 4 : wave >= 20 ? 3 : wave >= 10 ? 2 : 1;
        List<WaveSpawnGroup> groups = new ArrayList<>();
        if (wave <= 2) {
            addGroup(groups, "pirate_zombie", zombieCount, directionCount);
        } else if (wave == 3) {
            addGroup(groups, "pirate_zombie", Math.max(1, zombieCount - 1), directionCount);
            addGroup(groups, "flag_pirate_zombie", 1, directionCount);
        } else if (wave <= 5) {
            addWeighted(groups, zombieCount, directionCount,
                    entry("pirate_zombie", 80),
                    entry("conehead_pirate_zombie", 20));
        } else if (wave <= 8) {
            addWeighted(groups, zombieCount, directionCount,
                    entry("pirate_zombie", 55),
                    entry("conehead_pirate_zombie", 20),
                    entry("barrel_roller_zombie", 15),
                    entry("barrel_obstacle", 5),
                    entry("flag_pirate_zombie", 5));
        } else if (wave <= 11) {
            addWeighted(groups, zombieCount, directionCount,
                    entry("pirate_zombie", 55),
                    entry("conehead_pirate_zombie", 20),
                    entry("barrel_roller_zombie", 10),
                    entry("swashbuckler_zombie", 10),
                    entry("flag_pirate_zombie", 5));
        } else if (wave <= 14) {
            addWeighted(groups, zombieCount, directionCount,
                    entry("pirate_zombie", 45),
                    entry("conehead_pirate_zombie", 15),
                    entry("barrel_roller_zombie", 10),
                    entry("swashbuckler_zombie", 10),
                    entry("seagull_zombie", 15),
                    entry("flag_pirate_zombie", 5));
        } else if (wave <= 17) {
            addWeighted(groups, zombieCount, directionCount,
                    entry("pirate_zombie", 40),
                    entry("conehead_pirate_zombie", 15),
                    entry("buckethead_pirate_zombie", 15),
                    entry("barrel_roller_zombie", 10),
                    entry("swashbuckler_zombie", 10),
                    entry("seagull_zombie", 10));
        } else if (wave <= 20) {
            addWeighted(groups, zombieCount, directionCount,
                    entry("pirate_zombie", 38),
                    entry("conehead_pirate_zombie", 13),
                    entry("buckethead_pirate_zombie", 10),
                    entry("barrel_roller_zombie", 10),
                    entry("swashbuckler_zombie", 10),
                    entry("seagull_zombie", 10),
                    entry("pirate_imp", 5),
                    entry("imp_cannon", 4));
        } else if (wave <= 23) {
            addWeighted(groups, zombieCount, directionCount,
                    entry("pirate_zombie", 34),
                    entry("conehead_pirate_zombie", 11),
                    entry("buckethead_pirate_zombie", 12),
                    entry("barrel_roller_zombie", 8),
                    entry("swashbuckler_zombie", 10),
                    entry("seagull_zombie", 8),
                    entry("pelican_zombie", 7),
                    entry("pirate_imp", 5),
                    entry("imp_cannon", 5));
        } else if (wave <= 26) {
            addWeighted(groups, zombieCount, directionCount,
                    entry("pirate_zombie", 30),
                    entry("conehead_pirate_zombie", 10),
                    entry("buckethead_pirate_zombie", 14),
                    entry("barrel_roller_zombie", 8),
                    entry("swashbuckler_zombie", 10),
                    entry("seagull_zombie", 8),
                    entry("pelican_zombie", 7),
                    entry("pirate_imp", 5),
                    entry("imp_cannon", 3),
                    entry("pirate_captain_zombie", 5));
        } else if (wave <= 29) {
            int gargantuars = wave >= 28 ? 1 : 0;
            addWeighted(groups, zombieCount - gargantuars, directionCount,
                    entry("pirate_zombie", 30),
                    entry("conehead_pirate_zombie", 10),
                    entry("buckethead_pirate_zombie", 15),
                    entry("barrel_roller_zombie", 8),
                    entry("swashbuckler_zombie", 10),
                    entry("seagull_zombie", 8),
                    entry("pelican_zombie", 7),
                    entry("pirate_imp", 7),
                    entry("imp_cannon", 2),
                    entry("pirate_captain_zombie", 3));
            addGroup(groups, "pirate_gargantuar", gargantuars, directionCount);
        } else {
            addWeighted(groups, zombieCount - 2, directionCount,
                    entry("pirate_zombie", 25),
                    entry("conehead_pirate_zombie", 8),
                    entry("buckethead_pirate_zombie", 12),
                    entry("flag_pirate_zombie", 3),
                    entry("barrel_roller_zombie", 8),
                    entry("swashbuckler_zombie", 10),
                    entry("seagull_zombie", 8),
                    entry("pelican_zombie", 7),
                    entry("pirate_imp", 9),
                    entry("imp_cannon", 4),
                    entry("pirate_captain_zombie", 6));
            addGroup(groups, "pirate_gargantuar", 2, directionCount);
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
            case 1 -> "Pirate Seas: fight on ocean planks. Churning water holes cannot hold Lily Pads.";
            case 4, 7, 10, 15, 18, 22, 26 -> "Plant unlock detected. Clear the wave to expand your Pirate Seas loadout.";
            case 9 -> "Utility reward detected: Sea Pickle lighting unlock.";
            case 13 -> "Plank gaps becoming more complex. Keep plants on safe wood paths.";
            case 24 -> "Cannon foundation reward detected.";
            case 30 -> "Completion Wave: final plank layout and Pirate Ship reward detected.";
            default -> "Preset plank paths detected. Water holes churn between safe wood tiles.";
        };
    }

    private static List<WaveReward> rewardsFor(int wave) {
        return switch (wave) {
            case 1 -> plant("kernel_pult", "Kernel-pult", "pvz2mod:kernel_pult_seed_packet", "Lobs kernels and occasional stunning butter.");
            case 4 -> plant("snapdragon", "Snapdragon", "pvz2mod:snapdragon_seed_packet", "Short-range cone fire damage.");
            case 7 -> plant("spikeweed", "Spikeweed", "pvz2mod:spikeweed_seed_packet", "Damages zombies that walk over it.");
            case 9 -> List.of(new WaveReward(WaveRewardType.ITEM_UNLOCK, "sea_pickle", "Sea Pickle", "minecraft:sea_pickle", "Utility light source unlock."));
            case 10 -> plant("spring_bean", "Spring Bean", "pvz2mod:spring_bean_seed_packet", "Pushes zombies backward, possibly into churning water.");
            case 13 -> List.of(new WaveReward(WaveRewardType.PLAYER_UPGRADE, "active_seed_slot_2", "Seed Holder Active Slots II", "minecraft:oak_planks", "Unlocks an 8th active plant slot."));
            case 15 -> plant("coconut_cannon", "Coconut Cannon", "pvz2mod:coconut_cannon_seed_packet", "Heavy splash cannon plant.");
            case 18 -> plant("threepeater", "Threepeater", "pvz2mod:threepeater_seed_packet", "Fires three projectiles at nearby lanes.");
            case 22 -> plant("spikerock", "Spikerock", "pvz2mod:spikerock_seed_packet", "Stronger Spikeweed.");
            case 24 -> List.of(
                    new WaveReward(WaveRewardType.GARDEN_UPGRADE, "totem_seed_storage_2", "Totem Seed Storage II", "minecraft:barrel", "Totems can store more generated seeds."),
                    new WaveReward(WaveRewardType.ITEM_UNLOCK, "pirate_cannon", "Pirate Cannon", "pvz2mod:pirate_cannon", "Placeholder cannon foundation reward.")
            );
            case 26 -> plant("cherry_bomb", "Cherry Bomb", "pvz2mod:cherry_bomb_seed_packet", "Short-fuse area explosion.");
            case 27 -> List.of(new WaveReward(WaveRewardType.GARDEN_UPGRADE, "seed_storage_capacity_2", "Seed Storage Capacity II", "minecraft:heart_of_the_sea", "Increases Seed Holder packet capacity again."));
            case 30 -> List.of(
                    new WaveReward(WaveRewardType.COMPLETION, "pirate_seas_complete", "Pirate Seas Garden Complete", "minecraft:filled_map", "Marks Pirate Seas complete."),
                    new WaveReward(WaveRewardType.ITEM_UNLOCK, "pirate_ship", "Pirate Ship", "pvz2mod:pirate_ship", "Advanced ocean vehicle foundation.")
            );
            default -> List.of();
        };
    }

    private static List<WaveReward> plant(String id, String name, String icon, String note) {
        return List.of(new WaveReward(WaveRewardType.PLANT_UNLOCK, id, name, icon, note));
    }
}

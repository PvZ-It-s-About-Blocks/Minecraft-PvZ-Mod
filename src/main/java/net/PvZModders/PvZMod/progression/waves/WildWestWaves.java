package net.PvZModders.PvZMod.progression.waves;

import java.util.ArrayList;
import java.util.List;

public final class WildWestWaves {
    private static final List<GardenWaveDefinition> WAVES = createWaves();

    private WildWestWaves() {
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
        int zombieCount = Math.min(52, 3 + wave + (wave / 5) * 3);
        int directionCount = wave >= 30 ? 4 : wave >= 20 ? 3 : wave >= 10 ? 2 : 1;
        List<WaveSpawnGroup> groups = new ArrayList<>();
        if (wave <= 2) {
            addGroup(groups, "cowboy_zombie", zombieCount, directionCount);
        } else if (wave == 3) {
            addGroup(groups, "cowboy_zombie", Math.max(1, zombieCount - 1), directionCount);
            addGroup(groups, "flag_cowboy_zombie", 1, directionCount);
        } else if (wave <= 5) {
            addWeighted(groups, zombieCount, directionCount,
                    entry("cowboy_zombie", 80),
                    entry("conehead_cowboy", 20));
        } else if (wave <= 8) {
            addWeighted(groups, zombieCount, directionCount,
                    entry("cowboy_zombie", 60),
                    entry("conehead_cowboy", 25),
                    entry("prospector_zombie", 15));
        } else if (wave <= 11) {
            addWeighted(groups, zombieCount, directionCount,
                    entry("cowboy_zombie", 50),
                    entry("conehead_cowboy", 18),
                    entry("prospector_zombie", 10),
                    entry("chicken_wrangler_zombie", 12),
                    entry("zombie_chicken", 10));
        } else if (wave <= 14) {
            addWeighted(groups, zombieCount, directionCount,
                    entry("cowboy_zombie", 45),
                    entry("conehead_cowboy", 18),
                    entry("prospector_zombie", 10),
                    entry("chicken_wrangler_zombie", 10),
                    entry("zombie_chicken", 7),
                    entry("poncho_zombie", 10));
        } else if (wave <= 17) {
            addWeighted(groups, zombieCount, directionCount,
                    entry("cowboy_zombie", 40),
                    entry("conehead_cowboy", 15),
                    entry("prospector_zombie", 10),
                    entry("chicken_wrangler_zombie", 10),
                    entry("poncho_zombie", 10),
                    entry("pianist_zombie", 8),
                    entry("zombie_chicken", 7));
        } else if (wave <= 20) {
            addWeighted(groups, zombieCount, directionCount,
                    entry("cowboy_zombie", 38),
                    entry("conehead_cowboy", 14),
                    entry("buckethead_cowboy", 16),
                    entry("prospector_zombie", 9),
                    entry("chicken_wrangler_zombie", 9),
                    entry("poncho_zombie", 9),
                    entry("pianist_zombie", 5));
        } else if (wave <= 23) {
            addWeighted(groups, zombieCount, directionCount,
                    entry("cowboy_zombie", 32),
                    entry("conehead_cowboy", 12),
                    entry("buckethead_cowboy", 14),
                    entry("prospector_zombie", 9),
                    entry("chicken_wrangler_zombie", 9),
                    entry("poncho_zombie", 9),
                    entry("pianist_zombie", 5),
                    entry("bull_rider_zombie", 5),
                    entry("zombie_bull", 5));
        } else if (wave <= 29) {
            int gargantuars = wave >= 27 ? 1 : 0;
            addWeighted(groups, zombieCount - gargantuars, directionCount,
                    entry("cowboy_zombie", 30),
                    entry("conehead_cowboy", 10),
                    entry("buckethead_cowboy", 15),
                    entry("prospector_zombie", 10),
                    entry("chicken_wrangler_zombie", 10),
                    entry("poncho_zombie", 10),
                    entry("pianist_zombie", 5),
                    entry("bull_rider_zombie", 5),
                    entry("zombie_bull", 5));
            addGroup(groups, "wild_west_gargantuar", gargantuars, directionCount);
        } else {
            addWeighted(groups, zombieCount - 2, directionCount,
                    entry("cowboy_zombie", 25),
                    entry("conehead_cowboy", 9),
                    entry("buckethead_cowboy", 13),
                    entry("prospector_zombie", 10),
                    entry("pianist_zombie", 7),
                    entry("poncho_zombie", 9),
                    entry("chicken_wrangler_zombie", 9),
                    entry("zombie_chicken", 8),
                    entry("bull_rider_zombie", 5),
                    entry("zombie_bull", 5));
            addGroup(groups, "wild_west_gargantuar", 2, directionCount);
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
            case 1 -> "Wild West: establish split-lane coverage for multi-direction attacks.";
            case 4, 6, 9, 11, 18, 24 -> "Plant unlock detected. Clear the wave to expand your Wild West loadout.";
            case 15, 21, 27 -> "Garden upgrade placeholder detected.";
            case 30 -> "Completion Wave: survive the final Wild West defense.";
            default -> "Wild West zombies approach with lane tricks, chicken swarms, and charge pressure.";
        };
    }

    private static List<WaveReward> rewardsFor(int wave) {
        return switch (wave) {
            case 1 -> plant("split_pea", "Split Pea", "pvz2mod:split_pea_seed_packet", "Shoots forward and backward.");
            case 4 -> plant("chili_bean", "Chili Bean", "pvz2mod:chili_bean_seed_packet", "Defeats one zombie and stuns nearby zombies.");
            case 6 -> plant("pea_pod", "Pea Pod", "pvz2mod:pea_pod_seed_packet", "Stacks up to five shots on one tile.");
            case 9 -> plant("lightning_reed", "Lightning Reed", "pvz2mod:lightning_reed_seed_packet", "Chains electric damage between zombies.");
            case 11 -> plant("melon_pult", "Melon-pult", "pvz2mod:melon_pult_seed_packet", "Lobs heavy splash-damage melons.");
            case 15 -> List.of(new WaveReward(WaveRewardType.GARDEN_UPGRADE, "seed_replenishment_speed_2", "Seed Replenishment Speed II", "minecraft:rail", "World-wide: permanently makes gardens replenish seeds 15% faster."));
            case 18 -> plant("tall_nut", "Tall-nut", "pvz2mod:tall_nut_seed_packet", "Very sturdy defensive blocker.");
            case 21 -> List.of(new WaveReward(WaveRewardType.PLAYER_UPGRADE, "sun_cap_3", "Sun Cap III", "minecraft:golden_rail", "World-wide: permanently increases maximum Sun for everyone by 150."));
            case 24 -> plant("winter_melon", "Winter Melon", "pvz2mod:winter_melon_seed_packet", "Lobs chilling splash-damage melons.");
            case 27 -> List.of(new WaveReward(WaveRewardType.PLAYER_UPGRADE, "second_seed_page_unlock", "Seed Holder Page II Foundation", "minecraft:diamond", "World-wide: permanently unlocks the second active seed page foundation."));
            case 30 -> List.of(
                    new WaveReward(WaveRewardType.COMPLETION, "wild_west_complete", "Wild West Garden Complete", "minecraft:gold_block", "Marks Wild West complete."),
                    new WaveReward(WaveRewardType.ITEM_UNLOCK, "wild_west_eye", "Wild West Eye", "pvz2mod:wild_west_eye", "Awakens the Wild West Portal Frame."),
                    new WaveReward(WaveRewardType.ITEM_UNLOCK, "speedy_minecart", "Speedy Minecart", "pvz2mod:speedy_minecart", "Ride without rails. Speed scales with Sun.")
            );
            default -> List.of();
        };
    }

    private static List<WaveReward> plant(String id, String name, String icon, String note) {
        return List.of(new WaveReward(WaveRewardType.PLANT_UNLOCK, id, name, icon, note));
    }
}

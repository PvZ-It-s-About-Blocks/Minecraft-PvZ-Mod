package net.PvZModders.PvZMod.progression.waves;

import java.util.ArrayList;
import java.util.List;

public final class LostCityWaves {
    private static final List<GardenWaveDefinition> WAVES = createWaves();

    private LostCityWaves() {
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
        int zombieCount = Math.min(50, 3 + wave + (wave / 5) * 2);
        int directionCount = wave >= 30 ? 4 : wave >= 20 ? 3 : wave >= 10 ? 2 : 1;
        List<WaveSpawnGroup> groups = new ArrayList<>();
        if (wave <= 2) {
            addGroup(groups, "lost_city_zombie", zombieCount, directionCount);
        } else if (wave == 3) {
            addGroup(groups, "lost_city_zombie", Math.max(1, zombieCount - 1), directionCount);
            addGroup(groups, "flag_lost_city_zombie", 1, directionCount);
        } else if (wave <= 5) {
            addWeighted(groups, zombieCount, directionCount,
                    entry("lost_city_zombie", 80),
                    entry("conehead_lost_city_zombie", 20));
        } else if (wave <= 8) {
            addWeighted(groups, zombieCount, directionCount,
                    entry("lost_city_zombie", 58),
                    entry("conehead_lost_city_zombie", 22),
                    entry("excavator_zombie", 15),
                    entry("flag_lost_city_zombie", 5));
        } else if (wave <= 11) {
            addWeighted(groups, zombieCount, directionCount,
                    entry("lost_city_zombie", 50),
                    entry("conehead_lost_city_zombie", 18),
                    entry("excavator_zombie", 12),
                    entry("parasol_zombie", 15),
                    entry("flag_lost_city_zombie", 5));
        } else if (wave <= 14) {
            addWeighted(groups, zombieCount, directionCount,
                    entry("lost_city_zombie", 43),
                    entry("conehead_lost_city_zombie", 17),
                    entry("excavator_zombie", 12),
                    entry("parasol_zombie", 12),
                    entry("relic_hunter_zombie", 16));
        } else if (wave <= 17) {
            addWeighted(groups, zombieCount, directionCount,
                    entry("lost_city_zombie", 38),
                    entry("conehead_lost_city_zombie", 15),
                    entry("excavator_zombie", 12),
                    entry("parasol_zombie", 12),
                    entry("relic_hunter_zombie", 14),
                    entry("turquoise_skull_zombie", 9));
        } else if (wave <= 20) {
            addWeighted(groups, zombieCount, directionCount,
                    entry("lost_city_zombie", 34),
                    entry("conehead_lost_city_zombie", 12),
                    entry("buckethead_lost_city_zombie", 16),
                    entry("excavator_zombie", 10),
                    entry("parasol_zombie", 10),
                    entry("relic_hunter_zombie", 10),
                    entry("turquoise_skull_zombie", 8));
        } else if (wave <= 23) {
            addWeighted(groups, zombieCount, directionCount,
                    entry("lost_city_zombie", 30),
                    entry("conehead_lost_city_zombie", 10),
                    entry("buckethead_lost_city_zombie", 14),
                    entry("excavator_zombie", 10),
                    entry("parasol_zombie", 10),
                    entry("relic_hunter_zombie", 8),
                    entry("turquoise_skull_zombie", 7),
                    entry("lost_pilot_zombie", 6),
                    entry("bug_zombie", 5));
        } else if (wave <= 29) {
            int gargantuars = wave >= 27 ? 1 : 0;
            addWeighted(groups, zombieCount - gargantuars, directionCount,
                    entry("lost_city_zombie", 28),
                    entry("conehead_lost_city_zombie", 9),
                    entry("buckethead_lost_city_zombie", 14),
                    entry("excavator_zombie", 10),
                    entry("parasol_zombie", 10),
                    entry("relic_hunter_zombie", 8),
                    entry("turquoise_skull_zombie", 7),
                    entry("lost_pilot_zombie", 5),
                    entry("bug_zombie", 5),
                    entry("imp_porter", 4));
            addGroup(groups, "porter_gargantuar", gargantuars, directionCount);
        } else {
            addWeighted(groups, zombieCount - 2, directionCount,
                    entry("lost_city_zombie", 24),
                    entry("conehead_lost_city_zombie", 8),
                    entry("buckethead_lost_city_zombie", 12),
                    entry("flag_lost_city_zombie", 4),
                    entry("excavator_zombie", 10),
                    entry("parasol_zombie", 10),
                    entry("relic_hunter_zombie", 8),
                    entry("turquoise_skull_zombie", 7),
                    entry("lost_pilot_zombie", 5),
                    entry("bug_zombie", 6),
                    entry("imp_porter", 6));
            addGroup(groups, "porter_gargantuar", 2, directionCount);
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
            case 1 -> "Lost City: Red Stingers are strongest when placed in safe garden positions.";
            case 3 -> "Gold Tile mechanic detected. Plants on Gold Tiles generate extra Sun.";
            case 6, 10, 19, 26 -> "Plant unlock detected. Clear the wave to expand your Lost City loadout.";
            case 13, 16, 22, 27 -> "Gold Tile and garden upgrade placeholder detected.";
            case 30 -> "Completion Wave: survive the final Lost City defense.";
            default -> "Lost City zombies approach with shield counters, relic leaps, and Sun-draining skulls.";
        };
    }

    private static List<WaveReward> rewardsFor(int wave) {
        return switch (wave) {
            case 1 -> plant("red_stinger", "Red Stinger", "pvz2mod:red_stinger_seed_packet", "Position-sensitive shooter and defender.");
            case 3 -> List.of(new WaveReward(WaveRewardType.GARDEN_SYSTEM_UNLOCK, "gold_tiles_unlocked", "Gold Tile Mechanic", "minecraft:gold_block", "Plants on Gold Tiles produce Sun."));
            case 6 -> plant("akee", "A.K.E.E.", "pvz2mod:akee_seed_packet", "Bounces seed shots between zombies.");
            case 10 -> plant("endurian", "Endurian", "pvz2mod:endurian_seed_packet", "Defensive thorn blocker.");
            case 13 -> List.of(new WaveReward(WaveRewardType.PLAYER_UPGRADE, "sun_cap_4", "Sun Cap IV", "minecraft:gold_ingot", "Increases your maximum Sun by 150."));
            case 16 -> List.of(new WaveReward(WaveRewardType.GARDEN_UPGRADE, "gold_tile_replenishment_synergy", "Gold Tile Replenishment Synergy", "minecraft:gold_nugget", "Future hook for better Gold Tile economy."));
            case 19 -> plant("stallia", "Stallia", "pvz2mod:stallia_seed_packet", "Slows zombies in a perfume cloud.");
            case 22 -> List.of(new WaveReward(WaveRewardType.GARDEN_UPGRADE, "seed_replenishment_speed_3", "Seed Replenishment Speed III", "minecraft:emerald", "Gardens replenish seeds 20% faster."));
            case 26 -> plant("gold_leaf", "Gold Leaf", "pvz2mod:gold_leaf_seed_packet", "Creates a Gold Tile.");
            case 27 -> List.of(new WaveReward(WaveRewardType.GARDEN_UPGRADE, "totem_seed_storage_4", "Totem Seed Storage IV", "minecraft:diamond", "Totems can store more generated seeds."));
            case 30 -> List.of(
                    new WaveReward(WaveRewardType.COMPLETION, "lost_city_complete", "Lost City Garden Complete", "minecraft:gold_block", "Marks Lost City complete."),
                    new WaveReward(WaveRewardType.ITEM_UNLOCK, "lost_city_eye", "Lost City Eye", "pvz2mod:lost_city_eye", "Awakens the Lost City Portal Frame."),
                    new WaveReward(WaveRewardType.ITEM_UNLOCK, "flying_plane", "Flying Plane", "pvz2mod:flying_plane", "Fast flight vehicle. Speed scales with Sun.")
            );
            default -> List.of();
        };
    }

    private static List<WaveReward> plant(String id, String name, String icon, String note) {
        return List.of(new WaveReward(WaveRewardType.PLANT_UNLOCK, id, name, icon, note));
    }
}

package net.PvZModders.PvZMod.progression.waves;

import java.util.ArrayList;
import java.util.List;

public final class DarkAgesWaves {
    private static final List<GardenWaveDefinition> WAVES = createWaves();

    private DarkAgesWaves() {
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
        int zombieCount = Math.min(50, 2 + wave + (wave / 5) * 2);
        int directionCount = wave >= 30 ? 4 : wave >= 20 ? 3 : wave >= 10 ? 2 : 1;
        List<WaveSpawnGroup> groups = new ArrayList<>();
        if (wave <= 2) {
            addGroup(groups, "peasant_zombie", zombieCount, directionCount);
        } else if (wave == 3) {
            addGroup(groups, "peasant_zombie", Math.max(1, zombieCount - 1), directionCount);
            addGroup(groups, "flag_peasant_zombie", 1, directionCount);
        } else if (wave <= 5) {
            addWeighted(groups, zombieCount, directionCount,
                    entry("peasant_zombie", 80),
                    entry("conehead_peasant", 20));
        } else if (wave <= 8) {
            addWeighted(groups, zombieCount, directionCount,
                    entry("peasant_zombie", 62),
                    entry("conehead_peasant", 23),
                    entry("jester_zombie", 10),
                    entry("flag_peasant_zombie", 5));
        } else if (wave <= 11) {
            addWeighted(groups, zombieCount, directionCount,
                    entry("peasant_zombie", 55),
                    entry("conehead_peasant", 20),
                    entry("jester_zombie", 10),
                    entry("wizard_zombie", 10),
                    entry("flag_peasant_zombie", 5));
        } else if (wave <= 14) {
            addWeighted(groups, zombieCount, directionCount,
                    entry("peasant_zombie", 45),
                    entry("conehead_peasant", 17),
                    entry("buckethead_peasant", 13),
                    entry("jester_zombie", 10),
                    entry("wizard_zombie", 10),
                    entry("dragon_imp", 5));
        } else if (wave <= 17) {
            addWeighted(groups, zombieCount, directionCount,
                    entry("peasant_zombie", 40),
                    entry("conehead_peasant", 15),
                    entry("buckethead_peasant", 10),
                    entry("jester_zombie", 10),
                    entry("wizard_zombie", 10),
                    entry("knight_zombie", 10),
                    entry("dragon_imp", 5));
        } else if (wave <= 20) {
            addWeighted(groups, zombieCount, directionCount,
                    entry("peasant_zombie", 36),
                    entry("conehead_peasant", 12),
                    entry("buckethead_peasant", 13),
                    entry("jester_zombie", 10),
                    entry("wizard_zombie", 10),
                    entry("knight_zombie", 10),
                    entry("dragon_imp", 9));
        } else if (wave <= 23) {
            addWeighted(groups, zombieCount, directionCount,
                    entry("peasant_zombie", 32),
                    entry("conehead_peasant", 10),
                    entry("buckethead_peasant", 13),
                    entry("jester_zombie", 10),
                    entry("wizard_zombie", 9),
                    entry("knight_zombie", 10),
                    entry("dragon_imp", 8),
                    entry("king_zombie", 8));
        } else if (wave <= 29) {
            int gargantuars = wave >= 27 ? 1 : 0;
            addWeighted(groups, zombieCount - gargantuars, directionCount,
                    entry("peasant_zombie", 30),
                    entry("conehead_peasant", 10),
                    entry("buckethead_peasant", 15),
                    entry("jester_zombie", 10),
                    entry("wizard_zombie", 9),
                    entry("knight_zombie", 10),
                    entry("dragon_imp", 8),
                    entry("king_zombie", 8));
            addGroup(groups, "dark_ages_gargantuar", gargantuars, directionCount);
        } else {
            addWeighted(groups, zombieCount - 2, directionCount,
                    entry("peasant_zombie", 25),
                    entry("conehead_peasant", 8),
                    entry("buckethead_peasant", 12),
                    entry("flag_peasant_zombie", 4),
                    entry("jester_zombie", 10),
                    entry("wizard_zombie", 10),
                    entry("knight_zombie", 11),
                    entry("king_zombie", 8),
                    entry("dragon_imp", 12));
            addGroup(groups, "dark_ages_gargantuar", 2, directionCount);
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
            case 1 -> "Dark Ages: natural Sun is suppressed. Sun-shrooms carry the early economy.";
            case 2, 4, 6, 15 -> "Plant unlock detected. Clear the wave to expand your Dark Ages loadout.";
            case 10, 20, 25 -> "Dark Ages garden upgrade placeholder detected.";
            case 30 -> "Completion Wave: survive the final Dark Ages defense.";
            default -> "Dark Ages zombies advance with projectile tricks, wizard control, and royal guard buffs.";
        };
    }

    private static List<WaveReward> rewardsFor(int wave) {
        return switch (wave) {
            case 1 -> plant("sun_shroom", "Sun-shroom", "pvz2mod:sun_shroom_seed_packet", "Grows into stronger Sun production over time.");
            case 2 -> plant("puff_shroom", "Puff-shroom", "pvz2mod:puff_shroom_seed_packet", "Free short-range temporary shooter.");
            case 4 -> plant("fume_shroom", "Fume-shroom", "pvz2mod:fume_shroom_seed_packet", "Piercing fume attack in front.");
            case 6 -> plant("sun_bean", "Sun Bean", "pvz2mod:sun_bean_seed_packet", "Infects a zombie so damage generates Sun.");
            case 10 -> List.of(new WaveReward(WaveRewardType.GARDEN_UPGRADE, "night_economy_storage", "Night Economy Storage", "minecraft:brown_mushroom", "Future hook for Dark Ages seed and Sun economy support."));
            case 15 -> plant("magnet_shroom", "Magnet-shroom", "pvz2mod:magnet_shroom_seed_packet", "Strips armor and metal equipment.");
            case 20 -> List.of(new WaveReward(WaveRewardType.GARDEN_UPGRADE, "seed_replenishment_speed_4", "Seed Replenishment Speed IV", "minecraft:red_mushroom", "Gardens replenish seeds 25% faster."));
            case 25 -> List.of(new WaveReward(WaveRewardType.GARDEN_UPGRADE, "sun_magnet_synergy_foundation", "Sun Magnet Synergy Foundation", "minecraft:amethyst_shard", "Future hook for Sun collection and economy upgrades."));
            case 30 -> List.of(
                    new WaveReward(WaveRewardType.COMPLETION, "dark_ages_complete", "Dark Ages Garden Complete", "minecraft:dark_oak_sapling", "Marks Dark Ages complete."),
                    new WaveReward(WaveRewardType.ITEM_UNLOCK, "dark_ages_eye", "Dark Ages Eye", "pvz2mod:dark_ages_eye", "Awakens the Dark Ages Portal Frame.")
            );
            default -> List.of();
        };
    }

    private static List<WaveReward> plant(String id, String name, String icon, String note) {
        return List.of(new WaveReward(WaveRewardType.PLANT_UNLOCK, id, name, icon, note));
    }
}

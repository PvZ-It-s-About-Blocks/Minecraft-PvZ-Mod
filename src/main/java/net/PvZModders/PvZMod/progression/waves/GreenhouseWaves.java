package net.PvZModders.PvZMod.progression.waves;

import java.util.ArrayList;
import java.util.List;

public final class GreenhouseWaves {
    private static final List<GardenWaveDefinition> WAVES = createWaves();

    private GreenhouseWaves() {
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
        int zombieCount = Math.min(42, 2 + wave + (wave / 5) * 2);
        int directionCount = wave >= 25 ? 3 : wave >= 12 ? 2 : 1;
        List<WaveSpawnGroup> groups = new ArrayList<>();
        if (wave <= 2) {
            addGroup(groups, "browncoat_zombie", zombieCount, directionCount);
        } else if (wave <= 4) {
            addWeighted(groups, zombieCount, directionCount,
                    entry("browncoat_zombie", 80),
                    entry("conehead_browncoat", 20));
        } else if (wave == 5) {
            addWeighted(groups, zombieCount - 1, directionCount,
                    entry("browncoat_zombie", 70),
                    entry("conehead_browncoat", 25),
                    entry("backup_dancer_zombie", 5));
            addGroup(groups, "flag_browncoat", 1, directionCount);
        } else if (wave == 6) {
            addWeighted(groups, zombieCount, directionCount,
                    entry("browncoat_zombie", 65),
                    entry("conehead_browncoat", 20),
                    entry("backup_dancer_zombie", 15));
        } else if (wave == 7) {
            addWeighted(groups, zombieCount, directionCount,
                    entry("browncoat_zombie", 58),
                    entry("conehead_browncoat", 20),
                    entry("backup_dancer_zombie", 12),
                    entry("dancing_zombie", 10));
        } else if (wave == 8) {
            addWeighted(groups, zombieCount - 1, directionCount,
                    entry("browncoat_zombie", 55),
                    entry("conehead_browncoat", 20),
                    entry("backup_dancer_zombie", 10),
                    entry("dancing_zombie", 5),
                    entry("pogo_zombie", 10));
            addGroup(groups, "yeti_zombie", 1, directionCount);
        } else if (wave == 9) {
            addWeighted(groups, zombieCount, directionCount,
                    entry("browncoat_zombie", 50),
                    entry("conehead_browncoat", 20),
                    entry("buckethead_browncoat", 10),
                    entry("backup_dancer_zombie", 10),
                    entry("pogo_zombie", 10));
        } else if (wave == 10) {
            addWeighted(groups, zombieCount, directionCount,
                    entry("browncoat_zombie", 45),
                    entry("conehead_browncoat", 20),
                    entry("buckethead_browncoat", 10),
                    entry("backup_dancer_zombie", 10),
                    entry("pogo_zombie", 7),
                    entry("ladder_zombie", 8));
        } else if (wave == 11) {
            addWeighted(groups, zombieCount, directionCount,
                    entry("browncoat_zombie", 42),
                    entry("conehead_browncoat", 18),
                    entry("buckethead_browncoat", 10),
                    entry("backup_dancer_zombie", 10),
                    entry("pogo_zombie", 7),
                    entry("ladder_zombie", 8),
                    entry("jack_in_the_box_zombie", 5));
        } else if (wave <= 18) {
            addWeighted(groups, zombieCount, directionCount,
                    entry("browncoat_zombie", 45),
                    entry("conehead_browncoat", 20),
                    entry("buckethead_browncoat", 10),
                    entry("backup_dancer_zombie", 10),
                    entry("pogo_zombie", 5),
                    entry("ladder_zombie", 5),
                    entry("flag_browncoat", 3),
                    entry("jack_in_the_box_zombie", 2));
        } else if (wave <= 29) {
            addWeighted(groups, zombieCount - (wave == 24 || wave == 29 ? 1 : 0), directionCount,
                    entry("browncoat_zombie", 30),
                    entry("conehead_browncoat", 15),
                    entry("buckethead_browncoat", 15),
                    entry("dancing_zombie", 8),
                    entry("backup_dancer_zombie", 10),
                    entry("pogo_zombie", 8),
                    entry("ladder_zombie", 7),
                    entry("jack_in_the_box_zombie", 5),
                    entry("flag_browncoat", 2));
            if (wave == 24 || wave == 29) {
                addGroup(groups, "yeti_zombie", 1, directionCount);
            }
        } else {
            addWeighted(groups, zombieCount - 1, directionCount,
                    entry("browncoat_zombie", 28),
                    entry("conehead_browncoat", 14),
                    entry("buckethead_browncoat", 15),
                    entry("dancing_zombie", 9),
                    entry("backup_dancer_zombie", 10),
                    entry("pogo_zombie", 9),
                    entry("ladder_zombie", 8),
                    entry("jack_in_the_box_zombie", 5),
                    entry("flag_browncoat", 2));
            addGroup(groups, "yeti_zombie", 1, directionCount);
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
            case 1 -> "Greenhouse: Squash is ready for emergency defense.";
            case 3 -> "Greenhouse economy scan: Marigold can now produce coins.";
            case 5 -> "Greenhouse support scan: Gold Magnet can collect nearby coins.";
            case 7 -> "Greenhouse defense scan: Cactus can pierce zombies and hit airborne threats.";
            case 9 -> "Greenhouse support scan: Aloe can heal damaged plants.";
            case 12 -> "Greenhouse emergency scan: Jalapeno can clear a straight lane.";
            case 30 -> "Completion Wave: survive the full Greenhouse challenge mix.";
            default -> "Greenhouse base defense wave.";
        };
    }

    private static List<WaveReward> rewardsFor(int wave) {
        return switch (wave) {
            case 1 -> plant("squash", "Squash", "pvz2mod:squash_seed_packet", "Crushes the first nearby zombie, then disappears.");
            case 3 -> plant("marigold", "Marigold", "pvz2mod:marigold_seed_packet", "Produces coins for the future shop economy.");
            case 5 -> plant("gold_magnet", "Gold Magnet", "pvz2mod:gold_magnet_seed_packet", "Collects nearby coin drops automatically.");
            case 7 -> plant("cactus", "Cactus", "pvz2mod:cactus_seed_packet", "Piercing shooter that can target flying zombies.");
            case 9 -> plant("aloe", "Aloe", "pvz2mod:aloe_seed_packet", "Heals damaged nearby plants.");
            case 12 -> plant("jalapeno", "Jalapeno", "pvz2mod:jalapeno_seed_packet", "Burns a straight lane without terrain damage.");
            case 18 -> List.of(new WaveReward(WaveRewardType.RESOURCE_REWARD, "greenhouse_coin_cache", "Greenhouse Coin Cache", "pvz2mod:coin", "Adds a small coin economy reward foundation."));
            case 30 -> List.of(
                    new WaveReward(WaveRewardType.COMPLETION, "greenhouse_complete", "Greenhouse Challenge Complete", "minecraft:flower_pot", "Marks the Greenhouse challenge track complete."),
                    new WaveReward(WaveRewardType.ITEM_UNLOCK, "greenhouse_eye", "Greenhouse Garden Eye", "pvz2mod:greenhouse_eye", "Awakens the Greenhouse Portal Frame.")
            );
            default -> List.of();
        };
    }

    private static List<WaveReward> plant(String id, String name, String icon, String note) {
        return List.of(new WaveReward(WaveRewardType.PLANT_UNLOCK, id, name, icon, note));
    }
}

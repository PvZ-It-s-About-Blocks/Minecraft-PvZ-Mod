package net.PvZModders.PvZMod.progression.waves;

import java.util.ArrayList;
import java.util.List;

public final class JurassicMarshWaves {
    private static final List<GardenWaveDefinition> WAVES = createWaves();

    private JurassicMarshWaves() {
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
        int zombieCount = Math.min(48, 2 + wave + (wave / 5) * 2);
        int directionCount = wave >= 30 ? 4 : wave >= 20 ? 3 : wave >= 10 ? 2 : 1;
        List<WaveSpawnGroup> groups = new ArrayList<>();
        if (wave <= 2) {
            addGroup(groups, "jurassic_zombie", zombieCount, directionCount);
        } else if (wave == 3) {
            addGroup(groups, "jurassic_zombie", Math.max(1, zombieCount - 1), directionCount);
            addGroup(groups, "flag_jurassic_zombie", 1, directionCount);
        } else if (wave <= 5) {
            addWeighted(groups, zombieCount, directionCount,
                    entry("jurassic_zombie", 80),
                    entry("conehead_jurassic_zombie", 20));
        } else if (wave <= 8) {
            addWeighted(groups, zombieCount, directionCount,
                    entry("jurassic_zombie", 55),
                    entry("conehead_jurassic_zombie", 20),
                    entry("fossilhead_zombie", 15),
                    entry("jurassic_imp", 5),
                    entry("flag_jurassic_zombie", 5));
        } else if (wave <= 11) {
            addWeighted(groups, zombieCount, directionCount,
                    entry("jurassic_zombie", 50),
                    entry("conehead_jurassic_zombie", 18),
                    entry("fossilhead_zombie", 15),
                    entry("jurassic_imp", 12),
                    entry("flag_jurassic_zombie", 5));
        } else if (wave <= 14) {
            addWeighted(groups, zombieCount, directionCount,
                    entry("jurassic_zombie", 43),
                    entry("conehead_jurassic_zombie", 16),
                    entry("buckethead_jurassic_zombie", 10),
                    entry("fossilhead_zombie", 12),
                    entry("jurassic_imp", 12),
                    entry("jurassic_bully", 7));
        } else if (wave <= 17) {
            addWeighted(groups, zombieCount, directionCount,
                    entry("jurassic_zombie", 40),
                    entry("conehead_jurassic_zombie", 15),
                    entry("buckethead_jurassic_zombie", 10),
                    entry("fossilhead_zombie", 10),
                    entry("jurassic_imp", 10),
                    entry("jurassic_bully", 10),
                    entry("rockpuncher_zombie", 5));
        } else if (wave <= 20) {
            addWeighted(groups, zombieCount, directionCount,
                    entry("jurassic_zombie", 36),
                    entry("conehead_jurassic_zombie", 13),
                    entry("buckethead_jurassic_zombie", 12),
                    entry("fossilhead_zombie", 10),
                    entry("jurassic_imp", 9),
                    entry("jurassic_bully", 10),
                    entry("rockpuncher_zombie", 10));
        } else if (wave <= 24) {
            addWeighted(groups, zombieCount, directionCount,
                    entry("jurassic_zombie", 30),
                    entry("conehead_jurassic_zombie", 10),
                    entry("buckethead_jurassic_zombie", 15),
                    entry("fossilhead_zombie", 10),
                    entry("amberhead_zombie", 10),
                    entry("jurassic_imp", 8),
                    entry("jurassic_bully", 8),
                    entry("rockpuncher_zombie", 9));
        } else if (wave <= 29) {
            int gargantuars = wave >= 28 ? 1 : 0;
            addWeighted(groups, zombieCount - gargantuars, directionCount,
                    entry("jurassic_zombie", 28),
                    entry("conehead_jurassic_zombie", 9),
                    entry("buckethead_jurassic_zombie", 14),
                    entry("fossilhead_zombie", 10),
                    entry("amberhead_zombie", 11),
                    entry("jurassic_imp", 9),
                    entry("jurassic_bully", 9),
                    entry("rockpuncher_zombie", 10));
            addGroup(groups, "jurassic_gargantuar", gargantuars, directionCount);
        } else {
            addWeighted(groups, zombieCount - 2, directionCount,
                    entry("jurassic_zombie", 24),
                    entry("conehead_jurassic_zombie", 8),
                    entry("buckethead_jurassic_zombie", 14),
                    entry("flag_jurassic_zombie", 4),
                    entry("fossilhead_zombie", 10),
                    entry("amberhead_zombie", 12),
                    entry("jurassic_imp", 10),
                    entry("jurassic_bully", 9),
                    entry("rockpuncher_zombie", 9));
            addGroup(groups, "jurassic_gargantuar", 2, directionCount);
        }

        int dinosaurCount = dinosaurCountFor(wave);
        if (dinosaurCount > 0) {
            groups.add(new WaveSpawnGroup("pvz2mod:jurassic_dinosaur", dinosaurCount, directionCount, List.of()));
        }
        return List.copyOf(groups);
    }

    private static int dinosaurCountFor(int wave) {
        if (wave < 5) {
            return 0;
        }
        if (wave < 11) {
            return 1;
        }
        if (wave < 20) {
            return 2;
        }
        if (wave < 30) {
            return 3;
        }
        return 5;
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
            case 1 -> "Jurassic Marsh: primal plants are tougher, and dinosaurs begin roaming the lanes.";
            case 4, 8, 17, 23 -> "Plant unlock detected. Clear the wave to expand your Jurassic Marsh loadout.";
            case 10 -> "Dinosaur activity intensifies. Future waves will bring stronger prehistoric disruptions.";
            case 13 -> "Torchflower utility foundation detected.";
            case 20, 27 -> "Jurassic garden upgrade placeholder detected.";
            case 30 -> "Completion Wave: survive the final Jurassic Marsh defense.";
            default -> "Basic zombies approach. Dinosaurs may disrupt or help depending on Perfume-shroom timing.";
        };
    }

    private static List<WaveReward> rewardsFor(int wave) {
        return switch (wave) {
            case 1 -> plant("primal_peashooter", "Primal Peashooter", "pvz2mod:primal_peashooter_seed_packet", "Heavy peas briefly stun and may knock zombies back.");
            case 4 -> plant("primal_wall_nut", "Primal Wall-nut", "pvz2mod:primal_wall_nut_seed_packet", "Durable prehistoric blocker.");
            case 8 -> plant("perfume_shroom", "Perfume-shroom", "pvz2mod:perfume_shroom_seed_packet", "Charms nearby dinosaurs.");
            case 10 -> List.of(new WaveReward(WaveRewardType.GARDEN_UPGRADE, "jurassic_dinosaur_intensifies", "Jurassic Dinosaur Mechanic", "minecraft:sniffer_egg", "Dinosaurs appear more often in Jurassic waves."));
            case 13 -> List.of(new WaveReward(WaveRewardType.ITEM_UNLOCK, "torchflower_utility", "Torchflower Utility", "minecraft:torchflower", "Placeholder for Torchflower-related utility."));
            case 17 -> plant("primal_sunflower", "Primal Sunflower", "pvz2mod:primal_sunflower_seed_packet", "Produces 50 sun every few seconds.");
            case 20 -> List.of(new WaveReward(WaveRewardType.PLAYER_UPGRADE, "sun_cap_6", "Sun Cap VI", "minecraft:bone", "Increases your maximum Sun by 200."));
            case 23 -> plant("primal_potato_mine", "Primal Potato Mine", "pvz2mod:primal_potato_mine_seed_packet", "Fast mine with a larger blast.");
            case 27 -> List.of(new WaveReward(WaveRewardType.GARDEN_UPGRADE, "seed_storage_capacity_5", "Seed Storage Capacity V", "minecraft:diamond", "Increases Seed Holder packet capacity."));
            case 30 -> List.of(
                    new WaveReward(WaveRewardType.COMPLETION, "jurassic_marsh_complete", "Jurassic Marsh Garden Complete", "minecraft:sniffer_egg", "Marks Jurassic Marsh complete."),
                    new WaveReward(WaveRewardType.ITEM_UNLOCK, "dino_whistle", "Dino Whistle", "pvz2mod:dino_whistle", "Summon or recall one dinosaur pet.")
            );
            default -> List.of();
        };
    }

    private static List<WaveReward> plant(String id, String name, String icon, String note) {
        return List.of(new WaveReward(WaveRewardType.PLANT_UNLOCK, id, name, icon, note));
    }
}

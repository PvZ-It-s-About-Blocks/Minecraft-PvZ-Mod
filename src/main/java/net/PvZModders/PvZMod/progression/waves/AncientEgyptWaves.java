package net.PvZModders.PvZMod.progression.waves;

import java.util.ArrayList;
import java.util.List;

public final class AncientEgyptWaves {
    private static final List<GardenWaveDefinition> WAVES = createWaves();

    private AncientEgyptWaves() {
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
        int zombieCount = Math.min(48, 2 + wave + (wave / 4) * 2);
        int directionCount = wave >= 30 ? 4 : wave >= 20 ? 3 : wave >= 10 ? 2 : 1;
        List<WaveSpawnGroup> groups = new ArrayList<>();
        if (wave <= 2) {
            addGroup(groups, "mummy_zombie", zombieCount, directionCount);
        } else if (wave == 3) {
            addWeighted(groups, zombieCount, directionCount,
                    entry("mummy_zombie", 80),
                    entry("conehead_mummy", 20));
        } else if (wave == 4) {
            addGroup(groups, "mummy_zombie", Math.max(1, zombieCount - 1), directionCount);
            addGroup(groups, "flag_mummy_zombie", 1, directionCount);
        } else if (wave <= 8) {
            addWeighted(groups, zombieCount, directionCount,
                    entry("mummy_zombie", 65),
                    entry("conehead_mummy", 20),
                    entry("ra_zombie", 15));
        } else if (wave <= 11) {
            addWeighted(groups, zombieCount, directionCount,
                    entry("mummy_zombie", 50),
                    entry("conehead_mummy", 20),
                    entry("ra_zombie", 12),
                    entry("camel_zombie", 18));
        } else if (wave <= 15) {
            addWeighted(groups, zombieCount, directionCount,
                    entry("mummy_zombie", 45),
                    entry("conehead_mummy", 18),
                    entry("buckethead_mummy", 10),
                    entry("ra_zombie", 10),
                    entry("camel_zombie", 8),
                    entry("explorer_zombie", 5),
                    entry("tomb_raiser_zombie", 4));
        } else if (wave <= 20) {
            addWeighted(groups, zombieCount, directionCount,
                    entry("mummy_zombie", 38),
                    entry("conehead_mummy", 17),
                    entry("buckethead_mummy", 16),
                    entry("ra_zombie", 10),
                    entry("camel_zombie", 9),
                    entry("explorer_zombie", 6),
                    entry("tomb_raiser_zombie", 3),
                    entry("pharaoh_zombie", 1));
        } else if (wave <= 26) {
            addWeighted(groups, zombieCount, directionCount,
                    entry("mummy_zombie", 30),
                    entry("conehead_mummy", 15),
                    entry("buckethead_mummy", 15),
                    entry("ra_zombie", 10),
                    entry("camel_zombie", 10),
                    entry("explorer_zombie", 8),
                    entry("tomb_raiser_zombie", 7),
                    entry("pharaoh_zombie", 5));
        } else if (wave <= 29) {
            addWeighted(groups, zombieCount - 1, directionCount,
                    entry("mummy_zombie", 29),
                    entry("conehead_mummy", 14),
                    entry("buckethead_mummy", 14),
                    entry("ra_zombie", 10),
                    entry("camel_zombie", 10),
                    entry("explorer_zombie", 8),
                    entry("tomb_raiser_zombie", 8),
                    entry("pharaoh_zombie", 7));
            addGroup(groups, "mummified_gargantuar", 1, directionCount);
        } else {
            addWeighted(groups, zombieCount - 2, directionCount,
                    entry("mummy_zombie", 26),
                    entry("conehead_mummy", 12),
                    entry("buckethead_mummy", 14),
                    entry("ra_zombie", 10),
                    entry("camel_zombie", 10),
                    entry("explorer_zombie", 8),
                    entry("tomb_raiser_zombie", 10),
                    entry("pharaoh_zombie", 10));
            addGroup(groups, "mummified_gargantuar", 2, directionCount);
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
            case 1 -> "Ancient Egypt: establish the Totem and prepare for desert lanes.";
            case 2, 5, 9, 13, 19, 24 -> "Plant unlock detected. Clear the wave to expand your Ancient Egypt loadout.";
            case 16, 22, 27 -> "Garden upgrade placeholder detected.";
            case 30 -> "Completion Wave: survive the final Ancient Egypt defense.";
            default -> "Mummy variants approach with tombs, Sun disruption, and sandstorm pressure.";
        };
    }

    private static List<WaveReward> rewardsFor(int wave) {
        return switch (wave) {
            case 1 -> List.of(new WaveReward(WaveRewardType.GARDEN_SYSTEM_UNLOCK, "ancient_egypt_totem_activated", "Ancient Egypt Totem Activated", "minecraft:sand", "Confirms the Ancient Egypt Garden defense has started."));
            case 2 -> plant("bloomerang", "Bloomerang", "pvz2mod:bloomerang_seed_packet", "Throws a returning piercing boomerang.");
            case 5 -> plant("iceberg_lettuce", "Iceberg Lettuce", "pvz2mod:iceberg_lettuce_seed_packet", "Freezes one nearby zombie.");
            case 9 -> plant("grave_buster", "Grave Buster", "pvz2mod:grave_buster_seed_packet", "Consumes grave obstacles.");
            case 13 -> plant("bonk_choy", "Bonk Choy", "pvz2mod:bonk_choy_seed_packet", "Rapid close-range punching plant.");
            case 16 -> List.of(new WaveReward(WaveRewardType.GARDEN_UPGRADE, "totem_seed_storage_1", "Totem Seed Storage I", "minecraft:gold_ingot", "Totems can store more generated seeds."));
            case 19 -> plant("torchwood", "Torchwood", "pvz2mod:torchwood_seed_packet", "Doubles compatible pea projectile damage.");
            case 22 -> List.of(new WaveReward(WaveRewardType.GARDEN_UPGRADE, "seed_replenishment_speed_1", "Seed Replenishment Speed I", "minecraft:emerald", "Gardens replenish seeds 10% faster."));
            case 24 -> plant("twin_sunflower", "Twin Sunflower", "pvz2mod:twin_sunflower_seed_packet", "Produces 50 sun every few seconds.");
            case 27 -> List.of(new WaveReward(WaveRewardType.PLAYER_UPGRADE, "sun_cap_2", "Sun Cap II", "minecraft:diamond", "Increases your maximum Sun by 100."));
            case 30 -> List.of(
                    new WaveReward(WaveRewardType.COMPLETION, "ancient_egypt_complete", "Ancient Egypt Garden Complete", "minecraft:gold_block", "Marks Ancient Egypt complete."),
                    new WaveReward(WaveRewardType.ITEM_UNLOCK, "ancient_egypt_eye", "Ancient Egypt Eye", "pvz2mod:ancient_egypt_eye", "Awakens the Ancient Egypt Portal Frame."),
                    new WaveReward(WaveRewardType.ITEM_UNLOCK, "targeting_priority_changer", "Targeting Priority Changer", "pvz2mod:targeting_priority_changer", "Lets the player change plant targeting priority.")
            );
            default -> List.of();
        };
    }

    private static List<WaveReward> plant(String id, String name, String icon, String note) {
        return List.of(new WaveReward(WaveRewardType.PLANT_UNLOCK, id, name, icon, note));
    }
}

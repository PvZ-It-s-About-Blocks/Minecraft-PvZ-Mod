package net.PvZModders.PvZMod.progression.waves;

import java.util.ArrayList;
import java.util.List;

public final class FarFutureWaves {
    private static final List<GardenWaveDefinition> WAVES = createWaves();

    private FarFutureWaves() {
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
        int directionCount = wave >= 30 ? 4 : wave >= 20 ? 3 : wave >= 9 ? 2 : 1;
        List<WaveSpawnGroup> groups = new ArrayList<>();
        if (wave <= 2) {
            addGroup(groups, "future_zombie", zombieCount, directionCount);
        } else if (wave == 3) {
            addGroup(groups, "future_zombie", Math.max(1, zombieCount - 1), directionCount);
            addGroup(groups, "flag_future_zombie", 1, directionCount);
        } else if (wave <= 5) {
            addWeighted(groups, zombieCount, directionCount,
                    entry("future_zombie", 80),
                    entry("conehead_future_zombie", 20));
        } else if (wave <= 8) {
            addWeighted(groups, zombieCount, directionCount,
                    entry("future_zombie", 60),
                    entry("conehead_future_zombie", 20),
                    entry("jetpack_zombie", 15),
                    entry("flag_future_zombie", 5));
        } else if (wave <= 11) {
            addWeighted(groups, zombieCount, directionCount,
                    entry("future_zombie", 55),
                    entry("conehead_future_zombie", 20),
                    entry("jetpack_zombie", 10),
                    entry("blastronaut_zombie", 10),
                    entry("flag_future_zombie", 5));
        } else if (wave <= 14) {
            addWeighted(groups, zombieCount, directionCount,
                    entry("future_zombie", 45),
                    entry("conehead_future_zombie", 15),
                    entry("buckethead_future_zombie", 15),
                    entry("jetpack_zombie", 10),
                    entry("blastronaut_zombie", 10),
                    entry("flag_future_zombie", 5));
        } else if (wave <= 17) {
            addWeighted(groups, zombieCount, directionCount,
                    entry("future_zombie", 40),
                    entry("conehead_future_zombie", 15),
                    entry("buckethead_future_zombie", 10),
                    entry("jetpack_zombie", 10),
                    entry("blastronaut_zombie", 10),
                    entry("robo_cone_zombie", 15));
        } else if (wave <= 21) {
            addWeighted(groups, zombieCount, directionCount,
                    entry("future_zombie", 38),
                    entry("conehead_future_zombie", 13),
                    entry("buckethead_future_zombie", 10),
                    entry("jetpack_zombie", 10),
                    entry("blastronaut_zombie", 10),
                    entry("robo_cone_zombie", 12),
                    entry("mecha_football_zombie", 7));
        } else if (wave <= 24) {
            addWeighted(groups, zombieCount, directionCount,
                    entry("future_zombie", 32),
                    entry("conehead_future_zombie", 10),
                    entry("buckethead_future_zombie", 14),
                    entry("jetpack_zombie", 8),
                    entry("blastronaut_zombie", 8),
                    entry("robo_cone_zombie", 12),
                    entry("mecha_football_zombie", 8),
                    entry("disco_tron_3000", 3),
                    entry("bug_bot_imp", 5));
        } else if (wave <= 29) {
            int gargantuars = wave >= 28 ? 1 : 0;
            addWeighted(groups, zombieCount - gargantuars, directionCount,
                    entry("future_zombie", 30),
                    entry("conehead_future_zombie", 10),
                    entry("buckethead_future_zombie", 15),
                    entry("jetpack_zombie", 8),
                    entry("blastronaut_zombie", 8),
                    entry("robo_cone_zombie", 10),
                    entry("mecha_football_zombie", 8),
                    entry("disco_tron_3000", 6),
                    entry("bug_bot_imp", 5));
            addGroup(groups, "gargantuar_prime", gargantuars, directionCount);
        } else {
            addWeighted(groups, zombieCount - 2, directionCount,
                    entry("future_zombie", 26),
                    entry("conehead_future_zombie", 8),
                    entry("buckethead_future_zombie", 13),
                    entry("flag_future_zombie", 3),
                    entry("jetpack_zombie", 8),
                    entry("blastronaut_zombie", 8),
                    entry("robo_cone_zombie", 10),
                    entry("mecha_football_zombie", 9),
                    entry("disco_tron_3000", 7),
                    entry("bug_bot_imp", 8));
            addGroup(groups, "gargantuar_prime", 2, directionCount);
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
            case 1 -> "Far Future: Laser Bean pierces zombie lanes. Power Tiles appear on later waves.";
            case 3 -> "Power Tiles online: matching plants on matching tile colors boost each other.";
            case 6, 9, 13, 17, 24 -> "Plant unlock detected. Clear the wave to expand your Far Future loadout.";
            case 10, 15, 20, 25 -> "Preset Power Tile layout detected. Matching plant groups can gain damage.";
            case 30 -> "Completion Wave: strong Power Tile layout detected. Jetpack reward available.";
            default -> "Basic zombies approach through Far Future lanes. Watch the preset Power Tile layout.";
        };
    }

    private static List<WaveReward> rewardsFor(int wave) {
        return switch (wave) {
            case 1 -> plant("laser_bean", "Laser Bean", "pvz2mod:laser_bean_seed_packet", "Pierces zombies with a lane laser.");
            case 3 -> List.of(
                    new WaveReward(WaveRewardType.PLANT_UNLOCK, "blover", "Blover", "pvz2mod:blover_seed_packet", "Blows away airborne hostile mobs."),
                    new WaveReward(WaveRewardType.GARDEN_SYSTEM_UNLOCK, "power_tiles", "Power Tiles Introduced", "minecraft:blue_terracotta", "Matching plants on matching Power Tiles gain damage.")
            );
            case 6 -> plant("citron", "Citron", "pvz2mod:citron_seed_packet", "Charges a powerful plasma shot.");
            case 9 -> plant("em_peach", "E.M.Peach", "pvz2mod:em_peach_seed_packet", "Stuns enemies with an EMP burst.");
            case 13 -> plant("infi_nut", "Infi-nut", "pvz2mod:infi_nut_seed_packet", "Regenerating defensive blocker.");
            case 15 -> List.of(new WaveReward(WaveRewardType.PLAYER_UPGRADE, "second_seed_page_unlock", "Second Seed Page Unlock", "minecraft:green_terracotta", "World-wide: permanently unlocks the second active seed page foundation."));
            case 17 -> plant("magnifying_grass", "Magnifying Grass", "pvz2mod:magnifying_grass_seed_packet", "Spends Sun to fire a strong beam.");
            case 20 -> List.of(new WaveReward(WaveRewardType.PLAYER_UPGRADE, "sun_cap_5", "Sun Cap V", "minecraft:purple_terracotta", "World-wide: permanently increases maximum Sun for everyone by 200."));
            case 24 -> plant("tile_turnip", "Tile Turnip", "pvz2mod:tile_turnip_seed_packet", "Creates a Power Tile on a garden tile.");
            case 25 -> List.of(new WaveReward(WaveRewardType.GARDEN_UPGRADE, "seed_storage_capacity_4", "Advanced Seed Storage", "minecraft:shield", "World-wide: permanently increases Seed Holder packet capacity."));
            case 27 -> List.of(new WaveReward(WaveRewardType.GARDEN_UPGRADE, "power_tile_upgrade_foundation", "Power Tile Upgrade Foundation", "minecraft:nether_star", "Future hook for stronger Power Tile systems."));
            case 30 -> List.of(
                    new WaveReward(WaveRewardType.COMPLETION, "far_future_complete", "Far Future Garden Complete", "minecraft:end_crystal", "Marks Far Future complete."),
                    new WaveReward(WaveRewardType.ITEM_UNLOCK, "far_future_eye", "Far Future Eye", "pvz2mod:far_future_eye", "Awakens the Far Future Portal Frame."),
                    new WaveReward(WaveRewardType.ITEM_UNLOCK, "jetpack", "Jetpack", "pvz2mod:jetpack", "Uses Sun for rocket-boot style upward thrust."),
                    new WaveReward(WaveRewardType.ITEM_UNLOCK, "citron_armor_set", "Citron Armor Set Foundation", "pvz2mod:citron_chestplate", "Placeholder forcefield armor pieces.")
            );
            default -> List.of();
        };
    }

    private static List<WaveReward> plant(String id, String name, String icon, String note) {
        return List.of(new WaveReward(WaveRewardType.PLANT_UNLOCK, id, name, icon, note));
    }
}

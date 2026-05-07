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
        return List.of(new WaveSpawnGroup("minecraft:zombie", zombieCount, directionCount, List.of()));
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

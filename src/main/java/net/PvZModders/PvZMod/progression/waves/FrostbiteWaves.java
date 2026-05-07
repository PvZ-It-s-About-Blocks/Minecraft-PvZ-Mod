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
        int zombieCount = Math.min(55, 2 + wave + (wave / 4) * 2);
        int directionCount = wave >= 30 ? 4 : wave >= 20 ? 3 : wave >= 10 ? 2 : 1;
        return List.of(new WaveSpawnGroup("minecraft:zombie", zombieCount, directionCount, List.of()));
    }

    private static String scanTextFor(int wave) {
        return switch (wave) {
            case 1 -> "Frostbite: Hot Potato can thaw plants. Heavy Snowfall begins on later waves.";
            case 3 -> "Heavy Snowfall detected: non-hot plants will frost over during preset storm windows.";
            case 6, 11, 19, 26 -> "Plant unlock detected. Clear the wave to expand your Frostbite loadout.";
            case 10, 15, 22, 27 -> "Frostbite garden upgrade placeholder detected.";
            case 30 -> "Completion Wave: survive a long Heavy Snowfall and claim the Freeze Ray.";
            default -> "Basic zombies approach through the cold. Watch for preset Heavy Snowfall windows.";
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
                    new WaveReward(WaveRewardType.ITEM_UNLOCK, "freeze_ray", "Freeze Ray", "pvz2mod:freeze_ray", "Piercing slow beam reward.")
            );
            default -> List.of();
        };
    }

    private static List<WaveReward> plant(String id, String name, String icon, String note) {
        return List.of(new WaveReward(WaveRewardType.PLANT_UNLOCK, id, name, icon, note));
    }
}

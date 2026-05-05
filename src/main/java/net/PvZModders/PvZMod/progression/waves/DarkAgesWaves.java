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
        return List.of(new WaveSpawnGroup("minecraft:zombie", zombieCount, directionCount, List.of()));
    }

    private static String scanTextFor(int wave) {
        return switch (wave) {
            case 1 -> "Dark Ages: natural Sun is suppressed. Sun-shrooms carry the early economy.";
            case 2, 4, 6, 15 -> "Plant unlock detected. Clear the wave to expand your Dark Ages loadout.";
            case 10, 20, 25 -> "Dark Ages garden upgrade placeholder detected.";
            case 30 -> "Completion Wave: survive the final Dark Ages defense.";
            default -> "Basic zombies approach through the dark forest. Future Dark Ages zombie types will replace these placeholders.";
        };
    }

    private static List<WaveReward> rewardsFor(int wave) {
        return switch (wave) {
            case 1 -> plant("sun_shroom", "Sun-shroom", "pvz2mod:sun_shroom_seed_packet", "Grows into stronger Sun production over time.");
            case 2 -> plant("puff_shroom", "Puff-shroom", "pvz2mod:puff_shroom_seed_packet", "Free short-range temporary shooter.");
            case 4 -> plant("fume_shroom", "Fume-shroom", "pvz2mod:fume_shroom_seed_packet", "Piercing fume attack in front.");
            case 6 -> plant("sun_bean", "Sun Bean", "pvz2mod:sun_bean_seed_packet", "Infects a zombie so damage generates Sun.");
            case 10 -> List.of(new WaveReward(WaveRewardType.GARDEN_UPGRADE, "dark_ages_upgrade_1", "Dark Ages Garden Upgrade I", "minecraft:brown_mushroom", "Placeholder garden upgrade."));
            case 15 -> plant("magnet_shroom", "Magnet-shroom", "pvz2mod:magnet_shroom_seed_packet", "Strips armor and metal equipment.");
            case 20 -> List.of(new WaveReward(WaveRewardType.GARDEN_UPGRADE, "dark_ages_upgrade_2", "Dark Ages Garden Upgrade II", "minecraft:red_mushroom", "Placeholder garden upgrade."));
            case 25 -> List.of(new WaveReward(WaveRewardType.GARDEN_UPGRADE, "dark_ages_mastery_upgrade", "Dark Ages Mastery Upgrade", "minecraft:amethyst_shard", "Placeholder mastery upgrade."));
            case 30 -> List.of(new WaveReward(WaveRewardType.COMPLETION, "dark_ages_complete", "Dark Ages Garden Complete", "minecraft:dark_oak_sapling", "Marks Dark Ages complete."));
            default -> List.of();
        };
    }

    private static List<WaveReward> plant(String id, String name, String icon, String note) {
        return List.of(new WaveReward(WaveRewardType.PLANT_UNLOCK, id, name, icon, note));
    }
}

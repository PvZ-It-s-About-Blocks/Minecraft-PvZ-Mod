package net.PvZModders.PvZMod.progression.waves;

import java.util.ArrayList;
import java.util.List;

public final class PirateSeasWaves {
    private static final List<GardenWaveDefinition> WAVES = createWaves();

    private PirateSeasWaves() {
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
        int zombieCount = Math.min(60, 2 + wave + (wave / 4) * 2);
        int directionCount = wave >= 30 ? 4 : wave >= 20 ? 3 : wave >= 10 ? 2 : 1;
        return List.of(new WaveSpawnGroup("minecraft:zombie", zombieCount, directionCount, List.of()));
    }

    private static String scanTextFor(int wave) {
        return switch (wave) {
            case 1 -> "Pirate Seas: fight on ocean planks. Churning water holes cannot hold Lily Pads.";
            case 4, 7, 10, 15, 18, 22, 26 -> "Plant unlock detected. Clear the wave to expand your Pirate Seas loadout.";
            case 9 -> "Utility reward detected: Sea Pickle lighting unlock.";
            case 13 -> "Plank gaps becoming more complex. Keep plants on safe wood paths.";
            case 24 -> "Cannon foundation reward detected.";
            case 30 -> "Completion Wave: final plank layout and Pirate Ship reward detected.";
            default -> "Preset plank paths detected. Water holes churn between safe wood tiles.";
        };
    }

    private static List<WaveReward> rewardsFor(int wave) {
        return switch (wave) {
            case 1 -> plant("kernel_pult", "Kernel-pult", "pvz2mod:kernel_pult_seed_packet", "Lobs kernels and occasional stunning butter.");
            case 4 -> plant("snapdragon", "Snapdragon", "pvz2mod:snapdragon_seed_packet", "Short-range cone fire damage.");
            case 7 -> plant("spikeweed", "Spikeweed", "pvz2mod:spikeweed_seed_packet", "Damages zombies that walk over it.");
            case 9 -> List.of(new WaveReward(WaveRewardType.ITEM_UNLOCK, "sea_pickle", "Sea Pickle", "minecraft:sea_pickle", "Utility light source unlock."));
            case 10 -> plant("spring_bean", "Spring Bean", "pvz2mod:spring_bean_seed_packet", "Pushes zombies backward, possibly into churning water.");
            case 13 -> List.of(new WaveReward(WaveRewardType.GARDEN_UPGRADE, "pirate_plank_gaps_1", "Pirate Seas Plank Gaps", "minecraft:oak_planks", "More complex preset plank gaps introduced."));
            case 15 -> plant("coconut_cannon", "Coconut Cannon", "pvz2mod:coconut_cannon_seed_packet", "Heavy splash cannon plant.");
            case 18 -> plant("threepeater", "Threepeater", "pvz2mod:threepeater_seed_packet", "Fires three projectiles at nearby lanes.");
            case 22 -> plant("spikerock", "Spikerock", "pvz2mod:spikerock_seed_packet", "Stronger Spikeweed.");
            case 24 -> List.of(new WaveReward(WaveRewardType.ITEM_UNLOCK, "pirate_cannon", "Pirate Cannon", "pvz2mod:pirate_cannon", "Placeholder cannon foundation reward."));
            case 26 -> plant("cherry_bomb", "Cherry Bomb", "pvz2mod:cherry_bomb_seed_packet", "Short-fuse area explosion.");
            case 27 -> List.of(new WaveReward(WaveRewardType.GARDEN_UPGRADE, "pirate_mastery_1", "Pirate Seas Mastery Upgrade", "minecraft:heart_of_the_sea", "Placeholder mastery upgrade."));
            case 30 -> List.of(
                    new WaveReward(WaveRewardType.COMPLETION, "pirate_seas_complete", "Pirate Seas Garden Complete", "minecraft:filled_map", "Marks Pirate Seas complete."),
                    new WaveReward(WaveRewardType.ITEM_UNLOCK, "pirate_ship", "Pirate Ship", "pvz2mod:pirate_ship", "Advanced ocean vehicle foundation.")
            );
            default -> List.of();
        };
    }

    private static List<WaveReward> plant(String id, String name, String icon, String note) {
        return List.of(new WaveReward(WaveRewardType.PLANT_UNLOCK, id, name, icon, note));
    }
}

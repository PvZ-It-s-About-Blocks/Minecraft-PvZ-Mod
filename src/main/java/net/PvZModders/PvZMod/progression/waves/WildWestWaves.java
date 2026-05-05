package net.PvZModders.PvZMod.progression.waves;

import java.util.ArrayList;
import java.util.List;

public final class WildWestWaves {
    private static final List<GardenWaveDefinition> WAVES = createWaves();

    private WildWestWaves() {
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
        int zombieCount = Math.min(52, 3 + wave + (wave / 5) * 3);
        int directionCount = wave >= 30 ? 4 : wave >= 20 ? 3 : wave >= 10 ? 2 : 1;
        return List.of(new WaveSpawnGroup("minecraft:zombie", zombieCount, directionCount, List.of()));
    }

    private static String scanTextFor(int wave) {
        return switch (wave) {
            case 1 -> "Wild West: establish split-lane coverage for multi-direction attacks.";
            case 4, 6, 9, 11, 18, 24 -> "Plant unlock detected. Clear the wave to expand your Wild West loadout.";
            case 15, 21, 27 -> "Garden upgrade placeholder detected.";
            case 30 -> "Completion Wave: survive the final Wild West defense.";
            default -> "Basic zombies approach. Future Wild West zombie types will replace these placeholders.";
        };
    }

    private static List<WaveReward> rewardsFor(int wave) {
        return switch (wave) {
            case 1 -> plant("split_pea", "Split Pea", "pvz2mod:split_pea_seed_packet", "Shoots forward and backward.");
            case 4 -> plant("chili_bean", "Chili Bean", "pvz2mod:chili_bean_seed_packet", "Defeats one zombie and stuns nearby zombies.");
            case 6 -> plant("pea_pod", "Pea Pod", "pvz2mod:pea_pod_seed_packet", "Stacks up to five shots on one tile.");
            case 9 -> plant("lightning_reed", "Lightning Reed", "pvz2mod:lightning_reed_seed_packet", "Chains electric damage between zombies.");
            case 11 -> plant("melon_pult", "Melon-pult", "pvz2mod:melon_pult_seed_packet", "Lobs heavy splash-damage melons.");
            case 15 -> List.of(new WaveReward(WaveRewardType.GARDEN_UPGRADE, "wild_west_upgrade_1", "Wild West Garden Upgrade I", "minecraft:rail", "Placeholder garden upgrade."));
            case 18 -> plant("tall_nut", "Tall-nut", "pvz2mod:tall_nut_seed_packet", "Very sturdy defensive blocker.");
            case 21 -> List.of(new WaveReward(WaveRewardType.GARDEN_UPGRADE, "wild_west_upgrade_2", "Wild West Garden Upgrade II", "minecraft:golden_rail", "Placeholder garden upgrade."));
            case 24 -> plant("winter_melon", "Winter Melon", "pvz2mod:winter_melon_seed_packet", "Lobs chilling splash-damage melons.");
            case 27 -> List.of(new WaveReward(WaveRewardType.GARDEN_UPGRADE, "wild_west_mastery_upgrade", "Wild West Mastery Upgrade", "minecraft:diamond", "Placeholder mastery upgrade."));
            case 30 -> List.of(
                    new WaveReward(WaveRewardType.COMPLETION, "wild_west_complete", "Wild West Garden Complete", "minecraft:gold_block", "Marks Wild West complete."),
                    new WaveReward(WaveRewardType.ITEM_UNLOCK, "speedy_minecart", "Speedy Minecart", "pvz2mod:speedy_minecart", "Ride without rails. Speed scales with Sun.")
            );
            default -> List.of();
        };
    }

    private static List<WaveReward> plant(String id, String name, String icon, String note) {
        return List.of(new WaveReward(WaveRewardType.PLANT_UNLOCK, id, name, icon, note));
    }
}

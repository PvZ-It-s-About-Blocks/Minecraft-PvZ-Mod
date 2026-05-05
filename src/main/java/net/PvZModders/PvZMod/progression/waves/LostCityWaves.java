package net.PvZModders.PvZMod.progression.waves;

import java.util.ArrayList;
import java.util.List;

public final class LostCityWaves {
    private static final List<GardenWaveDefinition> WAVES = createWaves();

    private LostCityWaves() {
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
        int zombieCount = Math.min(50, 3 + wave + (wave / 5) * 2);
        int directionCount = wave >= 30 ? 4 : wave >= 20 ? 3 : wave >= 10 ? 2 : 1;
        return List.of(new WaveSpawnGroup("minecraft:zombie", zombieCount, directionCount, List.of()));
    }

    private static String scanTextFor(int wave) {
        return switch (wave) {
            case 1 -> "Lost City: Red Stingers are strongest when placed in safe garden positions.";
            case 3 -> "Gold Tile mechanic detected. Plants on Gold Tiles generate extra Sun.";
            case 6, 10, 19, 26 -> "Plant unlock detected. Clear the wave to expand your Lost City loadout.";
            case 13, 16, 22, 27 -> "Gold Tile and garden upgrade placeholder detected.";
            case 30 -> "Completion Wave: survive the final Lost City defense.";
            default -> "Basic zombies approach. Many Lost City waves create Gold Tiles.";
        };
    }

    private static List<WaveReward> rewardsFor(int wave) {
        return switch (wave) {
            case 1 -> plant("red_stinger", "Red Stinger", "pvz2mod:red_stinger_seed_packet", "Position-sensitive shooter and defender.");
            case 3 -> List.of(new WaveReward(WaveRewardType.GARDEN_SYSTEM_UNLOCK, "gold_tiles_unlocked", "Gold Tile Mechanic", "minecraft:gold_block", "Plants on Gold Tiles produce Sun."));
            case 6 -> plant("akee", "A.K.E.E.", "pvz2mod:akee_seed_packet", "Bounces seed shots between zombies.");
            case 10 -> plant("endurian", "Endurian", "pvz2mod:endurian_seed_packet", "Defensive thorn blocker.");
            case 13 -> List.of(new WaveReward(WaveRewardType.GARDEN_UPGRADE, "lost_city_upgrade_1", "Lost City Garden Upgrade I", "minecraft:gold_ingot", "Placeholder garden upgrade."));
            case 16 -> List.of(new WaveReward(WaveRewardType.GARDEN_UPGRADE, "gold_tile_upgrade", "Gold Tile Pattern Upgrade", "minecraft:gold_nugget", "Placeholder for more frequent Gold Tiles."));
            case 19 -> plant("stallia", "Stallia", "pvz2mod:stallia_seed_packet", "Slows zombies in a perfume cloud.");
            case 22 -> List.of(new WaveReward(WaveRewardType.GARDEN_UPGRADE, "lost_city_upgrade_2", "Lost City Garden Upgrade II", "minecraft:emerald", "Placeholder garden upgrade."));
            case 26 -> plant("gold_leaf", "Gold Leaf", "pvz2mod:gold_leaf_seed_packet", "Creates a Gold Tile.");
            case 27 -> List.of(new WaveReward(WaveRewardType.GARDEN_UPGRADE, "lost_city_mastery_upgrade", "Lost City Mastery Upgrade", "minecraft:diamond", "Placeholder mastery upgrade."));
            case 30 -> List.of(
                    new WaveReward(WaveRewardType.COMPLETION, "lost_city_complete", "Lost City Garden Complete", "minecraft:gold_block", "Marks Lost City complete."),
                    new WaveReward(WaveRewardType.ITEM_UNLOCK, "flying_plane", "Flying Plane", "pvz2mod:flying_plane", "Fast flight vehicle. Speed scales with Sun.")
            );
            default -> List.of();
        };
    }

    private static List<WaveReward> plant(String id, String name, String icon, String note) {
        return List.of(new WaveReward(WaveRewardType.PLANT_UNLOCK, id, name, icon, note));
    }
}

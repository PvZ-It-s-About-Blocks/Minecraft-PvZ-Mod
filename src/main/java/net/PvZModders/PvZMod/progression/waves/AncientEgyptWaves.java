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
        return List.of(new WaveSpawnGroup("minecraft:zombie", zombieCount, directionCount, List.of()));
    }

    private static String scanTextFor(int wave) {
        return switch (wave) {
            case 1 -> "Ancient Egypt: establish the Totem and prepare for desert lanes.";
            case 2, 5, 9, 13, 19, 24 -> "Plant unlock detected. Clear the wave to expand your Ancient Egypt loadout.";
            case 16, 22, 27 -> "Garden upgrade placeholder detected.";
            case 30 -> "Completion Wave: survive the final Ancient Egypt defense.";
            default -> "Basic zombies approach. Future Ancient Egypt zombie types will replace these placeholders.";
        };
    }

    private static List<WaveReward> rewardsFor(int wave) {
        return switch (wave) {
            case 1 -> List.of(new WaveReward(WaveRewardType.GARDEN_SYSTEM_UNLOCK, "ancient_egypt_totem_activated", "Ancient Egypt Totem Activated", "minecraft:sand", "Confirms the Ancient Egypt Garden defense has started."));
            case 2 -> plant("bloomerang", "Bloomerang", "pvz2mod:bloomerang_seed_packet", "Throws a returning piercing boomerang.");
            case 5 -> plant("iceberg_lettuce", "Iceberg Lettuce", "pvz2mod:iceberg_lettuce_seed_packet", "Freezes one nearby zombie.");
            case 9 -> plant("grave_buster", "Grave Buster", "pvz2mod:grave_buster_seed_packet", "Consumes grave obstacles.");
            case 13 -> plant("bonk_choy", "Bonk Choy", "pvz2mod:bonk_choy_seed_packet", "Rapid close-range punching plant.");
            case 16 -> List.of(new WaveReward(WaveRewardType.GARDEN_UPGRADE, "ancient_egypt_upgrade_1", "Ancient Egypt Garden Upgrade I", "minecraft:gold_ingot", "Placeholder garden upgrade."));
            case 19 -> plant("torchwood", "Torchwood", "pvz2mod:torchwood_seed_packet", "Doubles compatible pea projectile damage.");
            case 22 -> List.of(new WaveReward(WaveRewardType.GARDEN_UPGRADE, "ancient_egypt_upgrade_2", "Ancient Egypt Garden Upgrade II", "minecraft:emerald", "Placeholder garden upgrade."));
            case 24 -> plant("twin_sunflower", "Twin Sunflower", "pvz2mod:twin_sunflower_seed_packet", "Produces 50 sun every few seconds.");
            case 27 -> List.of(new WaveReward(WaveRewardType.GARDEN_UPGRADE, "ancient_egypt_mastery_upgrade", "Ancient Egypt Mastery Upgrade", "minecraft:diamond", "Placeholder mastery upgrade."));
            case 30 -> List.of(
                    new WaveReward(WaveRewardType.COMPLETION, "ancient_egypt_complete", "Ancient Egypt Garden Complete", "minecraft:gold_block", "Marks Ancient Egypt complete."),
                    new WaveReward(WaveRewardType.ITEM_UNLOCK, "targeting_priority_changer", "Targeting Priority Changer", "pvz2mod:targeting_priority_changer", "Lets the player change plant targeting priority.")
            );
            default -> List.of();
        };
    }

    private static List<WaveReward> plant(String id, String name, String icon, String note) {
        return List.of(new WaveReward(WaveRewardType.PLANT_UNLOCK, id, name, icon, note));
    }
}

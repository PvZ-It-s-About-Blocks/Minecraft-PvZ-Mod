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
        return List.of(new WaveSpawnGroup("minecraft:zombie", zombieCount, directionCount, List.of()));
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
            case 15 -> List.of(new WaveReward(WaveRewardType.PLAYER_UPGRADE, "second_seed_page_unlock", "Second Seed Page Unlock", "minecraft:green_terracotta", "Unlocks the second active seed page foundation."));
            case 17 -> plant("magnifying_grass", "Magnifying Grass", "pvz2mod:magnifying_grass_seed_packet", "Spends Sun to fire a strong beam.");
            case 20 -> List.of(new WaveReward(WaveRewardType.PLAYER_UPGRADE, "sun_cap_5", "Sun Cap V", "minecraft:purple_terracotta", "Increases your maximum Sun by 200."));
            case 24 -> plant("tile_turnip", "Tile Turnip", "pvz2mod:tile_turnip_seed_packet", "Creates a Power Tile on a garden tile.");
            case 25 -> List.of(new WaveReward(WaveRewardType.GARDEN_UPGRADE, "seed_storage_capacity_4", "Advanced Seed Storage", "minecraft:shield", "Increases Seed Holder packet capacity."));
            case 27 -> List.of(new WaveReward(WaveRewardType.GARDEN_UPGRADE, "power_tile_upgrade_foundation", "Power Tile Upgrade Foundation", "minecraft:nether_star", "Future hook for stronger Power Tile systems."));
            case 30 -> List.of(
                    new WaveReward(WaveRewardType.COMPLETION, "far_future_complete", "Far Future Garden Complete", "minecraft:end_crystal", "Marks Far Future complete."),
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

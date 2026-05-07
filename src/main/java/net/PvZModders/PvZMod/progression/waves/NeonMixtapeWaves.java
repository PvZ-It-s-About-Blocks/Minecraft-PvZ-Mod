package net.PvZModders.PvZMod.progression.waves;

import java.util.ArrayList;
import java.util.List;

public final class NeonMixtapeWaves {
    private static final List<GardenWaveDefinition> WAVES = createWaves();

    private NeonMixtapeWaves() {
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
            case 1 -> "Neon Mixtape: Phat Beet covers tight spaces with rhythmic area damage.";
            case 3 -> "Speaker mechanic detected. Music pulses energize zombies on fixed timings.";
            case 5, 9, 17, 21, 26 -> "Plant unlock detected. Clear the wave to expand your Neon Mixtape loadout.";
            case 13, 20, 27 -> "Speaker and garden upgrade placeholder detected.";
            case 30 -> "Completion Wave: survive the final Neon Mixtape defense.";
            default -> "Basic zombies approach. Speaker pulses follow predictable wave timing.";
        };
    }

    private static List<WaveReward> rewardsFor(int wave) {
        return switch (wave) {
            case 1 -> plant("phat_beet", "Phat Beet", "pvz2mod:phat_beet_seed_packet", "Thumps nearby zombies with area damage.");
            case 3 -> List.of(new WaveReward(WaveRewardType.GARDEN_SYSTEM_UNLOCK, "speaker_mechanic", "Speaker Mechanic", "minecraft:note_block", "Speakers pulse during selected Neon Mixtape waves."));
            case 5 -> plant("celery_stalker", "Celery Stalker", "pvz2mod:celery_stalker_seed_packet", "Ambushes zombies that pass it.");
            case 9 -> plant("thyme_warp", "Thyme Warp", "pvz2mod:thyme_warp_seed_packet", "Warps zombies backward.");
            case 13 -> List.of(new WaveReward(WaveRewardType.GARDEN_UPGRADE, "totem_seed_storage_5", "Totem Storage V", "minecraft:jukebox", "Totems can store more generated seeds."));
            case 17 -> plant("garlic", "Garlic", "pvz2mod:garlic_seed_packet", "Diverts and disrupts zombies.");
            case 20 -> List.of(new WaveReward(WaveRewardType.GARDEN_UPGRADE, "seed_storage_capacity_4", "Seed Holder Capacity IV", "minecraft:note_block", "Increases Seed Holder packet capacity."));
            case 21 -> plant("spore_shroom", "Spore-shroom", "pvz2mod:spore_shroom_seed_packet", "Can sprout copies from defeated zombies.");
            case 26 -> plant("intensive_carrot", "Intensive Carrot", "pvz2mod:intensive_carrot_seed_packet", "Revives recently destroyed plants.");
            case 27 -> List.of(new WaveReward(WaveRewardType.GARDEN_UPGRADE, "seed_replenishment_speed_5", "Seed Replenishment Speed V", "minecraft:diamond", "Gardens replenish seeds 30% faster."));
            case 30 -> List.of(
                    new WaveReward(WaveRewardType.COMPLETION, "neon_mixtape_complete", "Neon Mixtape Garden Complete", "minecraft:note_block", "Marks Neon Mixtape complete."),
                    new WaveReward(WaveRewardType.ITEM_UNLOCK, "totem_shield", "Totem Shield", "pvz2mod:totem_shield", "Adds a protective shield layer to garden totems.")
            );
            default -> List.of();
        };
    }

    private static List<WaveReward> plant(String id, String name, String icon, String note) {
        return List.of(new WaveReward(WaveRewardType.PLANT_UNLOCK, id, name, icon, note));
    }
}

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
        List<WaveSpawnGroup> groups = new ArrayList<>();
        if (wave <= 2) {
            addGroup(groups, "neon_zombie", zombieCount, directionCount);
        } else if (wave == 3) {
            addGroup(groups, "neon_zombie", Math.max(1, zombieCount - 1), directionCount);
            addGroup(groups, "flag_neon_zombie", 1, directionCount);
        } else if (wave <= 5) {
            addWeighted(groups, zombieCount, directionCount,
                    entry("neon_zombie", 80),
                    entry("conehead_neon_zombie", 20));
        } else if (wave <= 8) {
            addWeighted(groups, zombieCount, directionCount,
                    entry("neon_zombie", 58),
                    entry("conehead_neon_zombie", 22),
                    entry("punk_zombie", 15),
                    entry("flag_neon_zombie", 5));
        } else if (wave <= 11) {
            addWeighted(groups, zombieCount, directionCount,
                    entry("neon_zombie", 50),
                    entry("conehead_neon_zombie", 18),
                    entry("punk_zombie", 12),
                    entry("glitter_zombie", 15),
                    entry("flag_neon_zombie", 5));
        } else if (wave <= 14) {
            addWeighted(groups, zombieCount, directionCount,
                    entry("neon_zombie", 43),
                    entry("conehead_neon_zombie", 15),
                    entry("punk_zombie", 12),
                    entry("glitter_zombie", 12),
                    entry("mc_zom_b", 10),
                    entry("breakdancer_zombie", 8));
        } else if (wave <= 17) {
            addWeighted(groups, zombieCount, directionCount,
                    entry("neon_zombie", 40),
                    entry("conehead_neon_zombie", 15),
                    entry("punk_zombie", 10),
                    entry("glitter_zombie", 10),
                    entry("mc_zom_b", 10),
                    entry("breakdancer_zombie", 15));
        } else if (wave <= 20) {
            addWeighted(groups, zombieCount, directionCount,
                    entry("neon_zombie", 34),
                    entry("conehead_neon_zombie", 12),
                    entry("buckethead_neon_zombie", 16),
                    entry("punk_zombie", 10),
                    entry("glitter_zombie", 10),
                    entry("mc_zom_b", 9),
                    entry("breakdancer_zombie", 9));
        } else if (wave <= 23) {
            addWeighted(groups, zombieCount, directionCount,
                    entry("neon_zombie", 30),
                    entry("conehead_neon_zombie", 10),
                    entry("buckethead_neon_zombie", 14),
                    entry("punk_zombie", 8),
                    entry("glitter_zombie", 8),
                    entry("mc_zom_b", 8),
                    entry("breakdancer_zombie", 8),
                    entry("arcade_zombie", 8),
                    entry("eight_bit_zombie", 6));
        } else if (wave <= 26) {
            addWeighted(groups, zombieCount, directionCount,
                    entry("neon_zombie", 28),
                    entry("conehead_neon_zombie", 9),
                    entry("buckethead_neon_zombie", 14),
                    entry("punk_zombie", 8),
                    entry("glitter_zombie", 8),
                    entry("mc_zom_b", 8),
                    entry("breakdancer_zombie", 8),
                    entry("arcade_zombie", 7),
                    entry("eight_bit_zombie", 4),
                    entry("boombox_zombie", 6));
        } else if (wave <= 29) {
            int gargantuars = wave >= 28 ? 1 : 0;
            addWeighted(groups, zombieCount - gargantuars, directionCount,
                    entry("neon_zombie", 28),
                    entry("conehead_neon_zombie", 9),
                    entry("buckethead_neon_zombie", 14),
                    entry("punk_zombie", 8),
                    entry("glitter_zombie", 8),
                    entry("mc_zom_b", 8),
                    entry("breakdancer_zombie", 8),
                    entry("arcade_zombie", 7),
                    entry("boombox_zombie", 6),
                    entry("eight_bit_zombie", 4));
            addGroup(groups, "neon_gargantuar", gargantuars, directionCount);
        } else {
            addWeighted(groups, zombieCount - 2, directionCount,
                    entry("neon_zombie", 23),
                    entry("conehead_neon_zombie", 8),
                    entry("buckethead_neon_zombie", 12),
                    entry("flag_neon_zombie", 4),
                    entry("punk_zombie", 8),
                    entry("glitter_zombie", 8),
                    entry("mc_zom_b", 8),
                    entry("breakdancer_zombie", 8),
                    entry("arcade_zombie", 7),
                    entry("eight_bit_zombie", 7),
                    entry("boombox_zombie", 7));
            addGroup(groups, "neon_gargantuar", 2, directionCount);
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
            case 1 -> "Neon Mixtape: Phat Beet covers tight spaces with rhythmic area damage.";
            case 3 -> "Speaker mechanic detected. Music pulses energize zombies on fixed timings.";
            case 5, 9, 17, 21, 26 -> "Plant unlock detected. Clear the wave to expand your Neon Mixtape loadout.";
            case 13, 20, 27 -> "Speaker and garden upgrade placeholder detected.";
            case 30 -> "Completion Wave: survive the final Neon Mixtape defense.";
            default -> "Neon zombies approach with rhythm boosts, shove pressure, and arcade swarms.";
        };
    }

    private static List<WaveReward> rewardsFor(int wave) {
        return switch (wave) {
            case 1 -> plant("phat_beet", "Phat Beet", "pvz2mod:phat_beet_seed_packet", "Thumps nearby zombies with area damage.");
            case 3 -> List.of(new WaveReward(WaveRewardType.GARDEN_SYSTEM_UNLOCK, "speaker_mechanic", "Speaker Mechanic", "minecraft:note_block", "Speakers pulse during selected Neon Mixtape waves."));
            case 5 -> plant("celery_stalker", "Celery Stalker", "pvz2mod:celery_stalker_seed_packet", "Ambushes zombies that pass it.");
            case 9 -> plant("thyme_warp", "Thyme Warp", "pvz2mod:thyme_warp_seed_packet", "Warps zombies backward.");
            case 13 -> List.of(new WaveReward(WaveRewardType.GARDEN_UPGRADE, "totem_seed_storage_5", "Totem Storage V", "minecraft:jukebox", "World-wide: permanently lets totems store more generated seeds."));
            case 17 -> plant("garlic", "Garlic", "pvz2mod:garlic_seed_packet", "Diverts and disrupts zombies.");
            case 20 -> List.of(new WaveReward(WaveRewardType.GARDEN_UPGRADE, "seed_storage_capacity_4", "Seed Holder Capacity IV", "minecraft:note_block", "World-wide: permanently increases Seed Holder packet capacity."));
            case 21 -> plant("spore_shroom", "Spore-shroom", "pvz2mod:spore_shroom_seed_packet", "Can sprout copies from defeated zombies.");
            case 26 -> plant("intensive_carrot", "Intensive Carrot", "pvz2mod:intensive_carrot_seed_packet", "Revives recently destroyed plants.");
            case 27 -> List.of(new WaveReward(WaveRewardType.GARDEN_UPGRADE, "seed_replenishment_speed_5", "Seed Replenishment Speed V", "minecraft:diamond", "World-wide: permanently makes gardens replenish seeds 30% faster."));
            case 30 -> List.of(
                    new WaveReward(WaveRewardType.COMPLETION, "neon_mixtape_complete", "Neon Mixtape Garden Complete", "minecraft:note_block", "Marks Neon Mixtape complete."),
                    new WaveReward(WaveRewardType.ITEM_UNLOCK, "neon_mixtape_eye", "Neon Mixtape Eye", "pvz2mod:neon_mixtape_eye", "Awakens the Neon Mixtape Portal Frame."),
                    new WaveReward(WaveRewardType.ITEM_UNLOCK, "totem_shield", "Totem Shield", "pvz2mod:totem_shield", "Adds a protective shield layer to garden totems.")
            );
            default -> List.of();
        };
    }

    private static List<WaveReward> plant(String id, String name, String icon, String note) {
        return List.of(new WaveReward(WaveRewardType.PLANT_UNLOCK, id, name, icon, note));
    }
}

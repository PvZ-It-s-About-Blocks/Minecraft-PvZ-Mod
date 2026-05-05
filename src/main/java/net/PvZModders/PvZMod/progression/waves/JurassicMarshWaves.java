package net.PvZModders.PvZMod.progression.waves;

import java.util.ArrayList;
import java.util.List;

public final class JurassicMarshWaves {
    private static final List<GardenWaveDefinition> WAVES = createWaves();

    private JurassicMarshWaves() {
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
        int zombieCount = Math.min(48, 2 + wave + (wave / 5) * 2);
        int directionCount = wave >= 30 ? 4 : wave >= 20 ? 3 : wave >= 10 ? 2 : 1;
        int dinosaurCount = dinosaurCountFor(wave);
        if (dinosaurCount <= 0) {
            return List.of(new WaveSpawnGroup("minecraft:zombie", zombieCount, directionCount, List.of()));
        }
        return List.of(
                new WaveSpawnGroup("minecraft:zombie", zombieCount, directionCount, List.of()),
                new WaveSpawnGroup("pvz2mod:jurassic_dinosaur", dinosaurCount, directionCount, List.of())
        );
    }

    private static int dinosaurCountFor(int wave) {
        if (wave < 3) {
            return 0;
        }
        if (wave < 10) {
            return 1;
        }
        if (wave < 20) {
            return 2;
        }
        if (wave < 30) {
            return 3;
        }
        return 5;
    }

    private static String scanTextFor(int wave) {
        return switch (wave) {
            case 1 -> "Jurassic Marsh: primal plants are tougher, and dinosaurs begin roaming the lanes.";
            case 4, 8, 17, 23 -> "Plant unlock detected. Clear the wave to expand your Jurassic Marsh loadout.";
            case 10 -> "Dinosaur activity intensifies. Future waves will bring stronger prehistoric disruptions.";
            case 13 -> "Torchflower utility foundation detected.";
            case 20, 27 -> "Jurassic garden upgrade placeholder detected.";
            case 30 -> "Completion Wave: survive the final Jurassic Marsh defense.";
            default -> "Basic zombies approach. Dinosaurs may disrupt or help depending on Perfume-shroom timing.";
        };
    }

    private static List<WaveReward> rewardsFor(int wave) {
        return switch (wave) {
            case 1 -> plant("primal_peashooter", "Primal Peashooter", "pvz2mod:primal_peashooter_seed_packet", "Heavy peas briefly stun and may knock zombies back.");
            case 4 -> plant("primal_wall_nut", "Primal Wall-nut", "pvz2mod:primal_wall_nut_seed_packet", "Durable prehistoric blocker.");
            case 8 -> plant("perfume_shroom", "Perfume-shroom", "pvz2mod:perfume_shroom_seed_packet", "Charms nearby dinosaurs.");
            case 10 -> List.of(new WaveReward(WaveRewardType.GARDEN_UPGRADE, "jurassic_dinosaur_intensifies", "Jurassic Dinosaur Mechanic", "minecraft:sniffer_egg", "Dinosaurs appear more often in Jurassic waves."));
            case 13 -> List.of(new WaveReward(WaveRewardType.ITEM_UNLOCK, "torchflower_utility", "Torchflower Utility", "minecraft:torchflower", "Placeholder for Torchflower-related utility."));
            case 17 -> plant("primal_sunflower", "Primal Sunflower", "pvz2mod:primal_sunflower_seed_packet", "Produces 50 sun every few seconds.");
            case 20 -> List.of(new WaveReward(WaveRewardType.GARDEN_UPGRADE, "jurassic_upgrade_2", "Jurassic Garden Upgrade II", "minecraft:bone", "Placeholder garden upgrade."));
            case 23 -> plant("primal_potato_mine", "Primal Potato Mine", "pvz2mod:primal_potato_mine_seed_packet", "Fast mine with a larger blast.");
            case 27 -> List.of(new WaveReward(WaveRewardType.GARDEN_UPGRADE, "jurassic_mastery_upgrade", "Jurassic Mastery Upgrade", "minecraft:diamond", "Placeholder mastery upgrade."));
            case 30 -> List.of(
                    new WaveReward(WaveRewardType.COMPLETION, "jurassic_marsh_complete", "Jurassic Marsh Garden Complete", "minecraft:sniffer_egg", "Marks Jurassic Marsh complete."),
                    new WaveReward(WaveRewardType.ITEM_UNLOCK, "dino_whistle", "Dino Whistle", "pvz2mod:dino_whistle", "Summon or recall one dinosaur pet.")
            );
            default -> List.of();
        };
    }

    private static List<WaveReward> plant(String id, String name, String icon, String note) {
        return List.of(new WaveReward(WaveRewardType.PLANT_UNLOCK, id, name, icon, note));
    }
}

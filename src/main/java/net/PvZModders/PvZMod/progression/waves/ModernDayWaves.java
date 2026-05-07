package net.PvZModders.PvZMod.progression.waves;

import java.util.ArrayList;
import java.util.List;

public final class ModernDayWaves {
    private static final List<GardenWaveDefinition> WAVES = createWaves();

    private ModernDayWaves() {
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
                    ModernDayZombiePool.groupsForWave(wave),
                    wave == OriginalGardenWaves.MAX_WAVE
            ));
        }
        return List.copyOf(waves);
    }

    private static String scanTextFor(int wave) {
        return switch (wave) {
            case 1 -> "Modern Day: Moonflower produces Sun and powers nearby shadow plants.";
            case 4, 8, 15, 22 -> "Shadow plant unlock detected. Clear the wave to expand your Modern Day loadout.";
            case 10 -> "Mixed zombie pool online: previous garden zombie themes can now combine.";
            case 25 -> "Modern Day mastery checkpoint: previous garden pressure patterns intensify.";
            case 27 -> "Modern Day mastery checkpoint: final easy-mode systems are stabilizing.";
            case 30 -> "Completion Wave: Modern Day completes Easy Mode and unlocks Mystical Eye plus Dragon fight groundwork.";
            default -> "Modern Day wave: mixed garden zombie placeholders approach from previous eras.";
        };
    }

    private static List<WaveReward> rewardsFor(int wave) {
        return switch (wave) {
            case 1 -> plant("moonflower", "Moonflower", "pvz2mod:moonflower_seed_packet", "Produces Sun and powers nearby shadow plants.");
            case 4 -> plant("nightshade", "Nightshade", "pvz2mod:nightshade_seed_packet", "Short-range shadow attacker.");
            case 8 -> plant("shadow_shroom", "Shadow-shroom", "pvz2mod:shadow_shroom_seed_packet", "Applies shadow damage over time.");
            case 10 -> List.of(new WaveReward(WaveRewardType.GARDEN_SYSTEM_UNLOCK, "modern_day_mixed_zombie_pool", "Mixed Zombie Pool", "minecraft:zombie_head", "Future previous-garden zombies can mix into Modern Day waves."));
            case 15 -> plant("dusk_lobber", "Dusk Lobber", "pvz2mod:dusk_lobber_seed_packet", "Lobs shadow splash projectiles.");
            case 18 -> List.of(new WaveReward(WaveRewardType.PLAYER_UPGRADE, "final_sun_cap", "Final Sun Cap Upgrade", "minecraft:chorus_fruit", "Final easy-mode maximum Sun increase."));
            case 22 -> plant("grimrose", "Grimrose", "pvz2mod:grimrose_seed_packet", "Drags normal zombies into the shadows.");
            case 25 -> List.of(new WaveReward(WaveRewardType.PLAYER_UPGRADE, "final_seed_holder", "Final Seed Holder Upgrade", "minecraft:end_stone", "Finalizes easy-mode Seed Holder capacity and second-page slots."));
            case 27 -> List.of(new WaveReward(WaveRewardType.GARDEN_UPGRADE, "final_garden_storage", "Final Garden Storage Upgrade", "minecraft:ender_eye", "Final easy-mode totem seed storage upgrade."));
            case 30 -> List.of(
                    new WaveReward(WaveRewardType.COMPLETION, "modern_day_complete", "Modern Day Complete", "minecraft:dragon_egg", "Marks Modern Day complete and prepares the final Dragon fight."),
                    new WaveReward(WaveRewardType.ITEM_UNLOCK, "mystical_eye", "Mystical Eye", "pvz2mod:mystical_eye", "Teleport to discovered garden totems from anywhere."),
                    new WaveReward(WaveRewardType.GARDEN_SYSTEM_UNLOCK, "dragon_final_fight_foundation", "Dragon Final Fight Foundation", "minecraft:end_crystal", "Unlocks final Dragon fight hooks for a later pass.")
            );
            default -> List.of();
        };
    }

    private static List<WaveReward> plant(String id, String name, String icon, String note) {
        return List.of(new WaveReward(WaveRewardType.PLANT_UNLOCK, id, name, icon, note));
    }
}

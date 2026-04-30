package net.PvZModders.PvZMod.progression.waves;

import java.util.ArrayList;
import java.util.List;

public final class OriginalGardenWaves {
    public static final int MAX_WAVE = 30;
    private static final List<GardenWaveDefinition> WAVES = createWaves();

    private OriginalGardenWaves() {
    }

    public static List<GardenWaveDefinition> all() {
        return WAVES;
    }

    public static GardenWaveDefinition get(int wave) {
        return WAVES.get(Math.max(1, Math.min(MAX_WAVE, wave)) - 1);
    }

    private static List<GardenWaveDefinition> createWaves() {
        List<GardenWaveDefinition> waves = new ArrayList<>();
        for (int wave = 1; wave <= MAX_WAVE; wave++) {
            waves.add(new GardenWaveDefinition(wave, scanTextFor(wave), rewardsFor(wave)));
        }
        return List.copyOf(waves);
    }

    private static String scanTextFor(int wave) {
        return switch (wave) {
            case 1 -> "Tutorial: Build your defense. Sunflower and Peashooter are supplied for free.";
            case 2, 3, 4 -> "Build Your Defense: basic zombies approach in simple lanes.";
            case 5 -> "Milestone Wave: expect a stronger push and unlock support after clearing.";
            case 6, 7, 8, 9 -> "Standard Defense: use sun economy and lane coverage.";
            case 10 -> "Milestone Wave: tougher armor appears. Reward scan detected.";
            case 11, 12, 13, 14 -> "Mixed Lanes: small waves may pressure multiple sides.";
            case 15 -> "Milestone Wave: defensive plant research opportunity.";
            case 16, 17, 18, 19 -> "Garden Stress Test: protect the Totem while expanding coverage.";
            case 20 -> "Milestone Wave: buried threat patterns detected.";
            case 21, 22, 23, 24 -> "Limited Plants: future tuning can restrict loadouts here.";
            case 25 -> "Milestone Wave: garden automation upgrade candidate.";
            case 26, 27, 28, 29 -> "Final Stretch: sustained pressure before completion.";
            case 30 -> "Completion Wave: survive the final Original Garden defense.";
            default -> "Build Your Defense.";
        };
    }

    private static List<WaveReward> rewardsFor(int wave) {
        return switch (wave) {
            case 1 -> List.of(new WaveReward(
                    WaveRewardType.GARDEN_SYSTEM_UNLOCK,
                    "garden_totem_activated",
                    "Garden Totem Activated",
                    "minecraft:grass_block",
                    "Confirms the player has started the first real garden defense."
            ));
            case 3 -> List.of(new WaveReward(
                    WaveRewardType.RESOURCE_REWARD,
                    "small_seed_packet_refill",
                    "Small Seed Packet Refill",
                    "minecraft:wheat_seeds",
                    "Placeholder resource refill reward."
            ));
            case 5 -> List.of(new WaveReward(
                    WaveRewardType.PLANT_UNLOCK,
                    "wall_nut",
                    "Wall-nut",
                    "minecraft:oak_log",
                    "First defensive plant. This teaches blocking and lane control."
            ));
            case 7 -> List.of(new WaveReward(
                    WaveRewardType.PLANT_UNLOCK,
                    "potato_mine",
                    "Potato Mine",
                    "minecraft:potato",
                    "First trap/explosive plant. This teaches delayed setup and emergency defense."
            ));
            case 10 -> List.of(new WaveReward(
                    WaveRewardType.ITEM_UNLOCK,
                    "biome_scouter",
                    "New Biome Scouter and Crafting Recipe",
                    "minecraft:compass",
                    "Penny gives a biome scouter that will point toward a selected biome."
            ));
            case 12 -> List.of(new WaveReward(
                    WaveRewardType.PLANT_UNLOCK,
                    "repeater",
                    "Repeater / Double Peashooter",
                    "minecraft:arrow",
                    "First direct damage upgrade. Stronger offense after learning defense."
            ));
            case 15 -> List.of(new WaveReward(
                    WaveRewardType.PLAYER_UPGRADE,
                    "more_sun_capacity",
                    "Player Upgrade",
                    "minecraft:sunflower",
                    "Player can hold more sun."
            ));
            case 18 -> List.of(new WaveReward(
                    WaveRewardType.PLANT_UNLOCK,
                    "chomper",
                    "Chomper",
                    "minecraft:purple_dye",
                    "First close-range high-risk plant. Adds variety without giving it too early."
            ));
            case 20 -> List.of(new WaveReward(
                    WaveRewardType.PLAYER_UPGRADE,
                    "more_seed_capacity",
                    "Player Upgrade",
                    "minecraft:bundle",
                    "Player can hold more seeds."
            ));
            case 23 -> List.of(new WaveReward(
                    WaveRewardType.ITEM_UNLOCK,
                    "peashooter_armor_blueprint",
                    "Peashooter Armor Blueprint",
                    "minecraft:paper",
                    "Placeholder for the later armor system."
            ));
            case 25 -> List.of(new WaveReward(
                    WaveRewardType.PLACEHOLDER,
                    "something",
                    "Something",
                    "minecraft:chest",
                    "Something."
            ));
            case 30 -> List.of(
                    new WaveReward(
                            WaveRewardType.COMPLETION,
                            "original_garden_complete",
                            "Original Garden Complete",
                            "minecraft:ender_eye",
                            "Marks the Original Garden complete."
                    ),
                    new WaveReward(
                            WaveRewardType.ITEM_UNLOCK,
                            "original_garden_eye",
                            "Original Garden Eye",
                            "minecraft:ender_eye",
                            "Used for opening the End portal."
                    ),
                    new WaveReward(
                            WaveRewardType.ITEM_UNLOCK,
                            "almanac",
                            "Almanac",
                            "minecraft:book",
                            "Garden and enemy information placeholder."
                    )
            );
            default -> List.of();
        };
    }
}

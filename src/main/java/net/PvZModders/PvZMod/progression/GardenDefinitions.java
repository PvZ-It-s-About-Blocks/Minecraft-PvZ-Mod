package net.PvZModders.PvZMod.progression;

import net.minecraft.world.level.biome.Biomes;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class GardenDefinitions {
    private static final Map<GardenId, GardenDefinition> DEFINITIONS = new EnumMap<>(GardenId.class);

    static {
        register(new GardenDefinition(
                GardenId.INITIAL_PLAINS,
                "Initial Garden",
                Biomes.PLAINS,
                List.of("peashooter", "sunflower", "wall_nut", "potato_mine"),
                List.of("garden_bot_auto_planter"),
                1
        ));
        register(new GardenDefinition(
                GardenId.DESERT,
                "Desert Garden",
                Biomes.DESERT,
                List.of("bloomerang", "iceberg_lettuce", "grave_buster", "bonk_choy", "repeater", "twin_sunflower"),
                List.of(),
                2
        ));
        register(new GardenDefinition(
                GardenId.PIRATE_SEAS,
                "Pirate Seas Garden",
                Biomes.WARM_OCEAN,
                List.of("kernel_pult", "snapdragon", "spikeweed", "spring_bean", "coconut_cannon", "threepeater", "spikerock", "cherry_bomb"),
                List.of("pirate_ship"),
                3
        ));
    }

    private GardenDefinitions() {
    }

    private static void register(GardenDefinition definition) {
        DEFINITIONS.put(definition.id(), definition);
    }

    public static GardenDefinition get(GardenId id) {
        return DEFINITIONS.get(id);
    }

    public static Map<GardenId, GardenDefinition> all() {
        return Map.copyOf(DEFINITIONS);
    }
}

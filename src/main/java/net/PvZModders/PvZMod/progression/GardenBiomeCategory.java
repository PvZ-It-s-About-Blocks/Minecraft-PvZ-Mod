package net.PvZModders.PvZMod.progression;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;

import java.util.List;
import java.util.Optional;

public enum GardenBiomeCategory {
    GRASS(GardenId.INITIAL_PLAINS, "Grass Biome", 0x2F9F3F, List.of(Biomes.PLAINS, Biomes.SUNFLOWER_PLAINS, Biomes.MEADOW)),
    DESERT(GardenId.DESERT, "Desert Biome", 0xE6C84A, List.of(Biomes.DESERT)),
    WARM_WATER(GardenId.PIRATE_SEAS, "Warm Ocean Biome", 0x2AB7D6, List.of(Biomes.WARM_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN)),
    SAVANNAH(GardenId.WILD_WEST, "Savannah", 0xD87925, List.of(Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU, Biomes.WINDSWEPT_SAVANNA)),
    SNOW(GardenId.FROSTBITE, "Snow Biome", 0x8FE8FF, List.of(Biomes.SNOWY_PLAINS, Biomes.ICE_SPIKES, Biomes.SNOWY_TAIGA, Biomes.FROZEN_RIVER, Biomes.FROZEN_OCEAN, Biomes.DEEP_FROZEN_OCEAN, Biomes.SNOWY_SLOPES, Biomes.GROVE, Biomes.JAGGED_PEAKS, Biomes.FROZEN_PEAKS)),
    JUNGLE(GardenId.LOST_CITY, "Jungle Biome", 0x237C2F, List.of(Biomes.JUNGLE, Biomes.SPARSE_JUNGLE, Biomes.BAMBOO_JUNGLE)),
    MUSHROOM(GardenId.FAR_FUTURE, "Mushroom Biome", 0x76D7FF, List.of(Biomes.MUSHROOM_FIELDS)),
    DARK_FOREST(GardenId.DARK_AGES, "Dark Forest Biome", 0x59407A, List.of(Biomes.DARK_FOREST)),
    FLOWER_FOREST(GardenId.NEON_MIXTAPE, "Flower Forest Biome", 0xD44CFF, List.of(Biomes.FLOWER_FOREST)),
    BADLANDS(GardenId.JURASSIC_MARSH, "Badlands", 0x6B8F3A, List.of(Biomes.BADLANDS, Biomes.ERODED_BADLANDS, Biomes.WOODED_BADLANDS)),
    BEACH(GardenId.BIG_WAVE_BEACH, "Beach Biome", 0x2877CC, List.of(Biomes.BEACH));

    private final GardenId gardenId;
    private final String displayName;
    private final int color;
    private final List<ResourceKey<Biome>> biomes;

    GardenBiomeCategory(GardenId gardenId, String displayName, int color, List<ResourceKey<Biome>> biomes) {
        this.gardenId = gardenId;
        this.displayName = displayName;
        this.color = color;
        this.biomes = biomes;
    }

    public GardenId gardenId() {
        return gardenId;
    }

    public String displayName() {
        return displayName;
    }

    public int color() {
        return color;
    }

    public List<ResourceKey<Biome>> biomes() {
        return biomes;
    }

    public static Optional<GardenBiomeCategory> forBiome(ResourceKey<Biome> biome) {
        for (GardenBiomeCategory category : values()) {
            if (category.biomes.contains(biome)) {
                return Optional.of(category);
            }
        }
        return Optional.empty();
    }

    public static Optional<GardenBiomeCategory> byId(String id) {
        for (GardenBiomeCategory category : values()) {
            if (category.name().equals(id)) {
                return Optional.of(category);
            }
        }
        return Optional.empty();
    }
}

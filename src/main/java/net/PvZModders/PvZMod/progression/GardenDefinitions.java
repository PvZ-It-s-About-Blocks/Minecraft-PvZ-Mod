package net.PvZModders.PvZMod.progression;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class GardenDefinitions {
    private static final Map<GardenId, GardenDefinition> DEFINITIONS = new EnumMap<>(GardenId.class);
    private static final Map<ResourceKey<Biome>, GardenId> BIOME_GARDENS = new HashMap<>();

    static {
        register(new GardenDefinition(
                GardenId.INITIAL_PLAINS,
                "Original Garden",
                Biomes.PLAINS,
                List.of("peashooter", "sunflower", "wall_nut", "potato_mine"),
                List.of("garden_bot_auto_planter"),
                1
        ), Biomes.SUNFLOWER_PLAINS, Biomes.MEADOW);
        register(new GardenDefinition(
                GardenId.DESERT,
                "Desert Garden",
                Biomes.DESERT,
                List.of("bloomerang", "iceberg_lettuce", "grave_buster", "bonk_choy", "repeater", "twin_sunflower"),
                List.of(),
                2
        ), Biomes.ERODED_BADLANDS, Biomes.WOODED_BADLANDS);
        register(new GardenDefinition(
                GardenId.PIRATE_SEAS,
                "Pirate Seas Garden",
                Biomes.WARM_OCEAN,
                List.of("kernel_pult", "snapdragon", "spikeweed", "spring_bean", "coconut_cannon", "threepeater", "spikerock", "cherry_bomb"),
                List.of("pirate_ship"),
                3
        ), Biomes.BEACH, Biomes.LUKEWARM_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN);
        register(new GardenDefinition(
                GardenId.WILD_WEST,
                "Wild West Garden",
                Biomes.SAVANNA,
                List.of("split_pea", "chili_bean", "pea_pod", "lightning_reed", "melon_pult", "tall_nut", "winter_melon"),
                List.of(),
                4
        ), Biomes.SAVANNA_PLATEAU, Biomes.WINDSWEPT_SAVANNA);
        register(new GardenDefinition(
                GardenId.FROSTBITE,
                "Frostbite Garden",
                Biomes.SNOWY_PLAINS,
                List.of("hot_potato", "pepper_pult", "chard_guard", "stunion", "rotobaga"),
                List.of("warmer"),
                5
        ), Biomes.ICE_SPIKES, Biomes.SNOWY_TAIGA, Biomes.FROZEN_RIVER, Biomes.FROZEN_OCEAN, Biomes.DEEP_FROZEN_OCEAN, Biomes.SNOWY_SLOPES, Biomes.GROVE, Biomes.JAGGED_PEAKS, Biomes.FROZEN_PEAKS);
        register(new GardenDefinition(
                GardenId.LOST_CITY,
                "Lost City Garden",
                Biomes.JUNGLE,
                List.of("red_stinger", "akee", "endurian", "stallia", "gold_leaf"),
                List.of(),
                6
        ), Biomes.SPARSE_JUNGLE, Biomes.BAMBOO_JUNGLE);
        register(new GardenDefinition(
                GardenId.FAR_FUTURE,
                "Far Future Garden",
                Biomes.CHERRY_GROVE,
                List.of("laser_bean", "blover", "citron", "empeach", "infi_nut", "magnifying_grass", "tile_turnip"),
                List.of("jetpack"),
                7
        ));
        register(new GardenDefinition(
                GardenId.DARK_AGES,
                "Dark Ages Garden",
                Biomes.DARK_FOREST,
                List.of("sun_shroom", "puff_shroom", "fume_shroom", "sun_bean", "magnet_shroom"),
                List.of(),
                8
        ), Biomes.OLD_GROWTH_PINE_TAIGA, Biomes.OLD_GROWTH_SPRUCE_TAIGA);
        register(new GardenDefinition(
                GardenId.NEON_MIXTAPE,
                "Neon Mixtape Garden",
                Biomes.DRIPSTONE_CAVES,
                List.of("phat_beet", "celery_stalker", "thyme_warp", "garlic", "spore_shroom", "intensive_carrot"),
                List.of(),
                9
        ));
        register(new GardenDefinition(
                GardenId.JURASSIC_MARSH,
                "Jurassic Marsh Garden",
                Biomes.BADLANDS,
                List.of("primal_peashooter", "primal_wall_nut", "perfume_shroom", "primal_sunflower", "primal_potato_mine"),
                List.of("triceratops_schematic"),
                10
        ));
        register(new GardenDefinition(
                GardenId.BIG_WAVE_BEACH,
                "Big Wave Beach Garden",
                Biomes.OCEAN,
                List.of("lily_pad", "tangle_kelp", "bowling_bulb", "guacodile", "banana_launcher"),
                List.of(),
                11
        ), Biomes.DEEP_OCEAN, Biomes.COLD_OCEAN, Biomes.DEEP_COLD_OCEAN);
        register(new GardenDefinition(
                GardenId.MODERN_DAY,
                "Modern Day Garden",
                Biomes.THE_END,
                List.of("moonflower", "nightshade", "shadow_shroom", "dusk_lobber", "grimrose"),
                List.of("commanders_bucket"),
                12
        ), Biomes.END_HIGHLANDS, Biomes.END_MIDLANDS, Biomes.SMALL_END_ISLANDS, Biomes.END_BARRENS);
    }

    private GardenDefinitions() {
    }

    @SafeVarargs
    private static void register(GardenDefinition definition, ResourceKey<Biome>... biomeAliases) {
        DEFINITIONS.put(definition.id(), definition);
        BIOME_GARDENS.put(definition.biome(), definition.id());
        for (ResourceKey<Biome> biome : biomeAliases) {
            BIOME_GARDENS.put(biome, definition.id());
        }
    }

    public static GardenDefinition get(GardenId id) {
        return DEFINITIONS.get(id);
    }

    public static Map<GardenId, GardenDefinition> all() {
        return Map.copyOf(DEFINITIONS);
    }

    public static Optional<GardenDefinition> forBiome(ResourceKey<Biome> biome) {
        return Optional.ofNullable(BIOME_GARDENS.get(biome))
                .map(DEFINITIONS::get);
    }
}

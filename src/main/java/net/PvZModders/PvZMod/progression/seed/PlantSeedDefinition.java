package net.PvZModders.PvZMod.progression.seed;

import net.PvZModders.PvZMod.item.ModItems;
import net.PvZModders.PvZMod.progression.GardenId;
import net.PvZModders.PvZMod.progression.GardenPortalOption;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public record PlantSeedDefinition(ResourceLocation seedPacketId, String plantId, String displayName, int sunCost, PlantBehavior behavior, GardenId gardenId) {
    private static final Map<ResourceLocation, PlantSeedDefinition> DEFINITIONS = new HashMap<>();
    private static final Map<String, PlantSeedDefinition> DEFINITIONS_BY_PLANT_ID = new HashMap<>();
    private static final List<PlantSeedDefinition> ORDERED_DEFINITIONS = new ArrayList<>();

    static {
        register(GardenId.INITIAL_PLAINS, ModItems.PEASHOOTER_SEED_PACKET.getId(), "peashooter", "Peashooter", 100, PlantBehavior.PEASHOOTER);
        register(GardenId.INITIAL_PLAINS, ModItems.SUNFLOWER_SEED_PACKET.getId(), "sunflower", "Sunflower", 50, PlantBehavior.SUNFLOWER);
        register(GardenId.INITIAL_PLAINS, ModItems.WALL_NUT_SEED_PACKET.getId(), "wall_nut", "Wall-nut", 50, PlantBehavior.WALL_NUT);
        register(GardenId.INITIAL_PLAINS, ModItems.POTATO_MINE_SEED_PACKET.getId(), "potato_mine", "Potato Mine", 25, PlantBehavior.POTATO_MINE);
        register(GardenId.INITIAL_PLAINS, "cabbage_pult", "Cabbage-pult", 100);
        register(GardenId.INITIAL_PLAINS, ModItems.REPEATER_SEED_PACKET.getId(), "repeater", "Repeater", 200, PlantBehavior.REPEATER);
        register(GardenId.INITIAL_PLAINS, ModItems.CHOMPER_SEED_PACKET.getId(), "chomper", "Chomper", 150, PlantBehavior.CHOMPER);

        register(GardenId.DESERT, ModItems.BLOOMERANG_SEED_PACKET.getId(), "bloomerang", "Bloomerang", 175, PlantBehavior.BLOOMERANG);
        register(GardenId.DESERT, ModItems.ICEBERG_LETTUCE_SEED_PACKET.getId(), "iceberg_lettuce", "Iceberg Lettuce", 0, PlantBehavior.ICEBERG_LETTUCE);
        register(GardenId.DESERT, ModItems.GRAVE_BUSTER_SEED_PACKET.getId(), "grave_buster", "Grave Buster", 0, PlantBehavior.GRAVE_BUSTER);
        register(GardenId.DESERT, ModItems.BONK_CHOY_SEED_PACKET.getId(), "bonk_choy", "Bonk Choy", 150, PlantBehavior.BONK_CHOY);
        register(GardenId.DESERT, ModItems.TORCHWOOD_SEED_PACKET.getId(), "torchwood", "Torchwood", 175, PlantBehavior.TORCHWOOD);
        register(GardenId.DESERT, ModItems.TWIN_SUNFLOWER_SEED_PACKET.getId(), "twin_sunflower", "Twin Sunflower", 125, PlantBehavior.TWIN_SUNFLOWER);
        register(GardenId.DESERT, ModItems.FIRE_PEASHOOTER_SEED_PACKET.getId(), "fire_peashooter", "Fire Peashooter", 175, PlantBehavior.FIRE_PEASHOOTER);
        register(GardenId.DESERT, ModItems.SOLAR_TOMATO_SEED_PACKET.getId(), "solar_tomato", "Solar Tomato", 100, PlantBehavior.SOLAR_TOMATO);
        register(GardenId.DESERT, ModItems.PEA_NUT_SEED_PACKET.getId(), "pea_nut", "Pea-nut", 150, PlantBehavior.PEA_NUT);
        register(GardenId.PIRATE_SEAS, ModItems.KERNEL_PULT_SEED_PACKET.getId(), "kernel_pult", "Kernel-pult", 100, PlantBehavior.KERNEL_PULT);
        register(GardenId.PIRATE_SEAS, ModItems.SNAPDRAGON_SEED_PACKET.getId(), "snapdragon", "Snapdragon", 150, PlantBehavior.SNAPDRAGON);
        register(GardenId.PIRATE_SEAS, ModItems.SPIKEWEED_SEED_PACKET.getId(), "spikeweed", "Spikeweed", 100, PlantBehavior.SPIKEWEED);
        register(GardenId.PIRATE_SEAS, ModItems.SPRING_BEAN_SEED_PACKET.getId(), "spring_bean", "Spring Bean", 50, PlantBehavior.SPRING_BEAN);
        register(GardenId.PIRATE_SEAS, ModItems.COCONUT_CANNON_SEED_PACKET.getId(), "coconut_cannon", "Coconut Cannon", 400, PlantBehavior.COCONUT_CANNON);
        register(GardenId.PIRATE_SEAS, ModItems.THREEPEATER_SEED_PACKET.getId(), "threepeater", "Threepeater", 300, PlantBehavior.THREEPEATER);
        register(GardenId.PIRATE_SEAS, ModItems.SPIKEROCK_SEED_PACKET.getId(), "spikerock", "Spikerock", 250, PlantBehavior.SPIKEROCK);
        register(GardenId.PIRATE_SEAS, ModItems.CHERRY_BOMB_SEED_PACKET.getId(), "cherry_bomb", "Cherry Bomb", 150, PlantBehavior.CHERRY_BOMB);
        register(GardenId.WILD_WEST, ModItems.SPLIT_PEA_SEED_PACKET.getId(), "split_pea", "Split Pea", 125, PlantBehavior.SPLIT_PEA);
        register(GardenId.WILD_WEST, ModItems.CHILI_BEAN_SEED_PACKET.getId(), "chili_bean", "Chili Bean", 50, PlantBehavior.CHILI_BEAN);
        register(GardenId.WILD_WEST, ModItems.PEA_POD_SEED_PACKET.getId(), "pea_pod", "Pea Pod", 125, PlantBehavior.PEA_POD);
        register(GardenId.WILD_WEST, ModItems.LIGHTNING_REED_SEED_PACKET.getId(), "lightning_reed", "Lightning Reed", 125, PlantBehavior.LIGHTNING_REED);
        register(GardenId.WILD_WEST, ModItems.MELON_PULT_SEED_PACKET.getId(), "melon_pult", "Melon-pult", 325, PlantBehavior.MELON_PULT);
        register(GardenId.WILD_WEST, ModItems.TALL_NUT_SEED_PACKET.getId(), "tall_nut", "Tall-nut", 125, PlantBehavior.TALL_NUT);
        register(GardenId.WILD_WEST, ModItems.WINTER_MELON_SEED_PACKET.getId(), "winter_melon", "Winter Melon", 500, PlantBehavior.WINTER_MELON);
        register(GardenId.FROSTBITE, ModItems.HOT_POTATO_SEED_PACKET.getId(), "hot_potato", "Hot Potato", 0, PlantBehavior.HOT_POTATO);
        register(GardenId.FROSTBITE, ModItems.PEPPER_PULT_SEED_PACKET.getId(), "pepper_pult", "Pepper-pult", 200, PlantBehavior.PEPPER_PULT);
        register(GardenId.FROSTBITE, ModItems.CHARD_GUARD_SEED_PACKET.getId(), "chard_guard", "Chard Guard", 75, PlantBehavior.CHARD_GUARD);
        register(GardenId.FROSTBITE, ModItems.STUNION_SEED_PACKET.getId(), "stunion", "Stunion", 25, PlantBehavior.STUNION);
        register(GardenId.FROSTBITE, ModItems.ROTOBAGA_SEED_PACKET.getId(), "rotobaga", "Rotobaga", 150, PlantBehavior.ROTOBAGA);
        register(GardenId.LOST_CITY, ModItems.RED_STINGER_SEED_PACKET.getId(), "red_stinger", "Red Stinger", 150, PlantBehavior.RED_STINGER);
        register(GardenId.LOST_CITY, ModItems.AKEE_SEED_PACKET.getId(), "akee", "A.K.E.E.", 175, PlantBehavior.AKEE);
        register(GardenId.LOST_CITY, ModItems.ENDURIAN_SEED_PACKET.getId(), "endurian", "Endurian", 100, PlantBehavior.ENDURIAN);
        register(GardenId.LOST_CITY, ModItems.STALLIA_SEED_PACKET.getId(), "stallia", "Stallia", 0, PlantBehavior.STALLIA);
        register(GardenId.LOST_CITY, ModItems.GOLD_LEAF_SEED_PACKET.getId(), "gold_leaf", "Gold Leaf", 80, PlantBehavior.GOLD_LEAF);
        register(GardenId.FAR_FUTURE, ModItems.LASER_BEAN_SEED_PACKET.getId(), "laser_bean", "Laser Bean", 200, PlantBehavior.LASER_BEAN);
        register(GardenId.FAR_FUTURE, ModItems.BLOVER_SEED_PACKET.getId(), "blover", "Blover", 50, PlantBehavior.BLOVER);
        register(GardenId.FAR_FUTURE, ModItems.CITRON_SEED_PACKET.getId(), "citron", "Citron", 350, PlantBehavior.CITRON);
        PlantSeedDefinition emPeach = register(GardenId.FAR_FUTURE, ModItems.EM_PEACH_SEED_PACKET.getId(), "em_peach", "E.M.Peach", 25, PlantBehavior.EM_PEACH);
        DEFINITIONS_BY_PLANT_ID.put("empeach", emPeach);
        register(GardenId.FAR_FUTURE, ModItems.INFI_NUT_SEED_PACKET.getId(), "infi_nut", "Infi-nut", 75, PlantBehavior.INFI_NUT);
        register(GardenId.FAR_FUTURE, ModItems.MAGNIFYING_GRASS_SEED_PACKET.getId(), "magnifying_grass", "Magnifying Grass", 50, PlantBehavior.MAGNIFYING_GRASS);
        register(GardenId.FAR_FUTURE, ModItems.TILE_TURNIP_SEED_PACKET.getId(), "tile_turnip", "Tile Turnip", 0, PlantBehavior.TILE_TURNIP);
        register(GardenId.DARK_AGES, ModItems.SUN_SHROOM_SEED_PACKET.getId(), "sun_shroom", "Sun-shroom", 25, PlantBehavior.SUN_SHROOM);
        register(GardenId.DARK_AGES, ModItems.PUFF_SHROOM_SEED_PACKET.getId(), "puff_shroom", "Puff-shroom", 0, PlantBehavior.PUFF_SHROOM);
        register(GardenId.DARK_AGES, ModItems.FUME_SHROOM_SEED_PACKET.getId(), "fume_shroom", "Fume-shroom", 125, PlantBehavior.FUME_SHROOM);
        register(GardenId.DARK_AGES, ModItems.SUN_BEAN_SEED_PACKET.getId(), "sun_bean", "Sun Bean", 50, PlantBehavior.SUN_BEAN);
        register(GardenId.DARK_AGES, ModItems.MAGNET_SHROOM_SEED_PACKET.getId(), "magnet_shroom", "Magnet-shroom", 100, PlantBehavior.MAGNET_SHROOM);
        register(GardenId.NEON_MIXTAPE, ModItems.PHAT_BEET_SEED_PACKET.getId(), "phat_beet", "Phat Beet", 150, PlantBehavior.PHAT_BEET);
        register(GardenId.NEON_MIXTAPE, ModItems.CELERY_STALKER_SEED_PACKET.getId(), "celery_stalker", "Celery Stalker", 50, PlantBehavior.CELERY_STALKER);
        register(GardenId.NEON_MIXTAPE, ModItems.THYME_WARP_SEED_PACKET.getId(), "thyme_warp", "Thyme Warp", 100, PlantBehavior.THYME_WARP);
        register(GardenId.NEON_MIXTAPE, ModItems.GARLIC_SEED_PACKET.getId(), "garlic", "Garlic", 50, PlantBehavior.GARLIC);
        register(GardenId.NEON_MIXTAPE, ModItems.SPORE_SHROOM_SEED_PACKET.getId(), "spore_shroom", "Spore-shroom", 150, PlantBehavior.SPORE_SHROOM);
        register(GardenId.NEON_MIXTAPE, ModItems.INTENSIVE_CARROT_SEED_PACKET.getId(), "intensive_carrot", "Intensive Carrot", 100, PlantBehavior.INTENSIVE_CARROT);
        register(GardenId.JURASSIC_MARSH, ModItems.PRIMAL_PEASHOOTER_SEED_PACKET.getId(), "primal_peashooter", "Primal Peashooter", 175, PlantBehavior.PRIMAL_PEASHOOTER);
        register(GardenId.JURASSIC_MARSH, ModItems.PRIMAL_WALL_NUT_SEED_PACKET.getId(), "primal_wall_nut", "Primal Wall-nut", 75, PlantBehavior.PRIMAL_WALL_NUT);
        register(GardenId.JURASSIC_MARSH, ModItems.PERFUME_SHROOM_SEED_PACKET.getId(), "perfume_shroom", "Perfume-shroom", 150, PlantBehavior.PERFUME_SHROOM);
        register(GardenId.JURASSIC_MARSH, ModItems.PRIMAL_SUNFLOWER_SEED_PACKET.getId(), "primal_sunflower", "Primal Sunflower", 75, PlantBehavior.PRIMAL_SUNFLOWER);
        register(GardenId.JURASSIC_MARSH, ModItems.PRIMAL_POTATO_MINE_SEED_PACKET.getId(), "primal_potato_mine", "Primal Potato Mine", 50, PlantBehavior.PRIMAL_POTATO_MINE);
        register(GardenId.BIG_WAVE_BEACH, ModItems.LILY_PAD_SEED_PACKET.getId(), "lily_pad", "Lily Pad", 25, PlantBehavior.LILY_PAD);
        register(GardenId.BIG_WAVE_BEACH, ModItems.TANGLE_KELP_SEED_PACKET.getId(), "tangle_kelp", "Tangle Kelp", 25, PlantBehavior.TANGLE_KELP);
        register(GardenId.BIG_WAVE_BEACH, ModItems.BOWLING_BULB_SEED_PACKET.getId(), "bowling_bulb", "Bowling Bulb", 200, PlantBehavior.BOWLING_BULB);
        register(GardenId.BIG_WAVE_BEACH, ModItems.GUACODILE_SEED_PACKET.getId(), "guacodile", "Guacodile", 125, PlantBehavior.GUACODILE);
        register(GardenId.BIG_WAVE_BEACH, ModItems.BANANA_LAUNCHER_SEED_PACKET.getId(), "banana_launcher", "Banana Launcher", 500, PlantBehavior.BANANA_LAUNCHER);
        register(GardenId.MODERN_DAY, ModItems.MOONFLOWER_SEED_PACKET.getId(), "moonflower", "Moonflower", 50, PlantBehavior.MOONFLOWER);
        register(GardenId.MODERN_DAY, ModItems.NIGHTSHADE_SEED_PACKET.getId(), "nightshade", "Nightshade", 75, PlantBehavior.NIGHTSHADE);
        register(GardenId.MODERN_DAY, ModItems.SHADOW_SHROOM_SEED_PACKET.getId(), "shadow_shroom", "Shadow-shroom", 50, PlantBehavior.SHADOW_SHROOM);
        register(GardenId.MODERN_DAY, ModItems.DUSK_LOBBER_SEED_PACKET.getId(), "dusk_lobber", "Dusk Lobber", 150, PlantBehavior.DUSK_LOBBER);
        register(GardenId.MODERN_DAY, ModItems.GRIMROSE_SEED_PACKET.getId(), "grimrose", "Grimrose", 75, PlantBehavior.GRIMROSE);
        register(GardenId.GREENHOUSE, ModItems.SQUASH_SEED_PACKET.getId(), "squash", "Squash", 50, PlantBehavior.SQUASH);
        register(GardenId.GREENHOUSE, ModItems.MARIGOLD_SEED_PACKET.getId(), "marigold", "Marigold", 50, PlantBehavior.MARIGOLD);
        register(GardenId.GREENHOUSE, ModItems.GOLD_MAGNET_SEED_PACKET.getId(), "gold_magnet", "Gold Magnet", 50, PlantBehavior.GOLD_MAGNET);
        register(GardenId.GREENHOUSE, ModItems.CACTUS_SEED_PACKET.getId(), "cactus", "Cactus", 175, PlantBehavior.CACTUS);
        register(GardenId.GREENHOUSE, ModItems.ALOE_SEED_PACKET.getId(), "aloe", "Aloe", 75, PlantBehavior.ALOE);
        register(GardenId.GREENHOUSE, ModItems.JALAPENO_SEED_PACKET.getId(), "jalapeno", "Jalapeno", 125, PlantBehavior.JALAPENO);
    }

    public static Optional<PlantSeedDefinition> get(ResourceLocation seedPacketId) {
        return Optional.ofNullable(DEFINITIONS.get(seedPacketId));
    }

    public static Optional<PlantSeedDefinition> getByPlantId(String plantId) {
        return Optional.ofNullable(DEFINITIONS_BY_PLANT_ID.get(plantId));
    }

    public static Collection<PlantSeedDefinition> all() {
        return List.copyOf(ORDERED_DEFINITIONS);
    }

    public int gardenColor() {
        return GardenPortalOption.values()[GardenPortalOption.indexOf(gardenId)].color();
    }

    public static int sunCost(ResourceLocation seedPacketId) {
        return get(seedPacketId).map(PlantSeedDefinition::sunCost).orElse(100);
    }

    public static ResourceLocation sunflowerSeedPacketId() {
        return BuiltInIds.SUNFLOWER;
    }

    public static ResourceLocation peashooterSeedPacketId() {
        return BuiltInIds.PEASHOOTER;
    }

    private static void register(GardenId gardenId, String plantId, String displayName, int sunCost) {
        register(gardenId, plantId, displayName, sunCost, PlantBehavior.PLACEHOLDER);
    }

    private static void register(GardenId gardenId, String plantId, String displayName, int sunCost, PlantBehavior behavior) {
        register(gardenId, new ResourceLocation("pvz2mod", plantId + "_seed_packet"), plantId, displayName, sunCost, behavior);
    }

    private static PlantSeedDefinition register(GardenId gardenId, ResourceLocation seedPacketId, String plantId, String displayName, int sunCost, PlantBehavior behavior) {
        PlantSeedDefinition definition = new PlantSeedDefinition(seedPacketId, plantId, displayName, sunCost, behavior, gardenId);
        DEFINITIONS.put(seedPacketId, definition);
        DEFINITIONS_BY_PLANT_ID.put(plantId, definition);
        ORDERED_DEFINITIONS.add(definition);
        return definition;
    }

    private static final class BuiltInIds {
        private static final ResourceLocation SUNFLOWER = ModItems.SUNFLOWER_SEED_PACKET.getId();
        private static final ResourceLocation PEASHOOTER = ModItems.PEASHOOTER_SEED_PACKET.getId();
    }

    public enum PlantBehavior {
        PLACEHOLDER,
        PEASHOOTER,
        SUNFLOWER,
        WALL_NUT,
        POTATO_MINE,
        CHOMPER,
        REPEATER,
        BLOOMERANG,
        ICEBERG_LETTUCE,
        GRAVE_BUSTER,
        BONK_CHOY,
        TORCHWOOD,
        TWIN_SUNFLOWER,
        FIRE_PEASHOOTER,
        SOLAR_TOMATO,
        PEA_NUT,
        SPLIT_PEA,
        CHILI_BEAN,
        PEA_POD,
        LIGHTNING_REED,
        MELON_PULT,
        TALL_NUT,
        WINTER_MELON,
        RED_STINGER,
        AKEE,
        ENDURIAN,
        STALLIA,
        GOLD_LEAF,
        SUN_SHROOM,
        PUFF_SHROOM,
        FUME_SHROOM,
        SUN_BEAN,
        MAGNET_SHROOM,
        PRIMAL_PEASHOOTER,
        PRIMAL_WALL_NUT,
        PERFUME_SHROOM,
        PRIMAL_SUNFLOWER,
        PRIMAL_POTATO_MINE,
        PHAT_BEET,
        CELERY_STALKER,
        THYME_WARP,
        GARLIC,
        SPORE_SHROOM,
        INTENSIVE_CARROT,
        LASER_BEAN,
        BLOVER,
        CITRON,
        EM_PEACH,
        INFI_NUT,
        MAGNIFYING_GRASS,
        TILE_TURNIP,
        HOT_POTATO,
        PEPPER_PULT,
        CHARD_GUARD,
        STUNION,
        ROTOBAGA,
        KERNEL_PULT,
        SNAPDRAGON,
        SPIKEWEED,
        SPRING_BEAN,
        COCONUT_CANNON,
        THREEPEATER,
        SPIKEROCK,
        CHERRY_BOMB,
        LILY_PAD,
        TANGLE_KELP,
        BOWLING_BULB,
        GUACODILE,
        BANANA_LAUNCHER,
        MOONFLOWER,
        NIGHTSHADE,
        SHADOW_SHROOM,
        DUSK_LOBBER,
        GRIMROSE,
        SQUASH,
        JALAPENO,
        MARIGOLD,
        GOLD_MAGNET,
        CACTUS,
        ALOE
    }
}

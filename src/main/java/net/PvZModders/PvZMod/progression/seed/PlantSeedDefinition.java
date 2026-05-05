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
        register(GardenId.PIRATE_SEAS, "kernel_pult", "Kernel-pult", 100);
        register(GardenId.PIRATE_SEAS, "snapdragon", "Snapdragon", 150);
        register(GardenId.PIRATE_SEAS, "spikeweed", "Spikeweed", 100);
        register(GardenId.PIRATE_SEAS, "spring_bean", "Spring Bean", 50);
        register(GardenId.PIRATE_SEAS, "coconut_cannon", "Coconut Cannon", 400);
        register(GardenId.PIRATE_SEAS, "threepeater", "Threepeater", 300);
        register(GardenId.PIRATE_SEAS, "spikerock", "Spikerock", 250);
        register(GardenId.PIRATE_SEAS, "cherry_bomb", "Cherry Bomb", 150);
        register(GardenId.WILD_WEST, ModItems.SPLIT_PEA_SEED_PACKET.getId(), "split_pea", "Split Pea", 125, PlantBehavior.SPLIT_PEA);
        register(GardenId.WILD_WEST, ModItems.CHILI_BEAN_SEED_PACKET.getId(), "chili_bean", "Chili Bean", 50, PlantBehavior.CHILI_BEAN);
        register(GardenId.WILD_WEST, ModItems.PEA_POD_SEED_PACKET.getId(), "pea_pod", "Pea Pod", 125, PlantBehavior.PEA_POD);
        register(GardenId.WILD_WEST, ModItems.LIGHTNING_REED_SEED_PACKET.getId(), "lightning_reed", "Lightning Reed", 125, PlantBehavior.LIGHTNING_REED);
        register(GardenId.WILD_WEST, ModItems.MELON_PULT_SEED_PACKET.getId(), "melon_pult", "Melon-pult", 325, PlantBehavior.MELON_PULT);
        register(GardenId.WILD_WEST, ModItems.TALL_NUT_SEED_PACKET.getId(), "tall_nut", "Tall-nut", 125, PlantBehavior.TALL_NUT);
        register(GardenId.WILD_WEST, ModItems.WINTER_MELON_SEED_PACKET.getId(), "winter_melon", "Winter Melon", 500, PlantBehavior.WINTER_MELON);
        register(GardenId.FROSTBITE, "hot_potato", "Hot Potato", 0);
        register(GardenId.FROSTBITE, "pepper_pult", "Pepper-pult", 200);
        register(GardenId.FROSTBITE, "chard_guard", "Chard Guard", 75);
        register(GardenId.FROSTBITE, "stunion", "Stunion", 25);
        register(GardenId.FROSTBITE, "rotobaga", "Rotobaga", 150);
        register(GardenId.LOST_CITY, "red_stinger", "Red Stinger", 150);
        register(GardenId.LOST_CITY, "akee", "A.K.E.E.", 175);
        register(GardenId.LOST_CITY, "endurian", "Endurian", 100);
        register(GardenId.LOST_CITY, "stallia", "Stallia", 0);
        register(GardenId.LOST_CITY, "gold_leaf", "Gold Leaf", 80);
        register(GardenId.FAR_FUTURE, "laser_bean", "Laser Bean", 200);
        register(GardenId.FAR_FUTURE, "blover", "Blover", 50);
        register(GardenId.FAR_FUTURE, "citron", "Citron", 350);
        register(GardenId.FAR_FUTURE, "empeach", "E.M.Peach", 25);
        register(GardenId.FAR_FUTURE, "infi_nut", "Infi-nut", 75);
        register(GardenId.FAR_FUTURE, "magnifying_grass", "Magnifying Grass", 50);
        register(GardenId.FAR_FUTURE, "tile_turnip", "Tile Turnip", 0);
        register(GardenId.DARK_AGES, "sun_shroom", "Sun-shroom", 25);
        register(GardenId.DARK_AGES, "puff_shroom", "Puff-shroom", 0);
        register(GardenId.DARK_AGES, "fume_shroom", "Fume-shroom", 125);
        register(GardenId.DARK_AGES, "sun_bean", "Sun Bean", 50);
        register(GardenId.DARK_AGES, "magnet_shroom", "Magnet-shroom", 100);
        register(GardenId.NEON_MIXTAPE, "phat_beet", "Phat Beet", 150);
        register(GardenId.NEON_MIXTAPE, "celery_stalker", "Celery Stalker", 50);
        register(GardenId.NEON_MIXTAPE, "thyme_warp", "Thyme Warp", 100);
        register(GardenId.NEON_MIXTAPE, "garlic", "Garlic", 50);
        register(GardenId.NEON_MIXTAPE, "spore_shroom", "Spore-shroom", 150);
        register(GardenId.NEON_MIXTAPE, "intensive_carrot", "Intensive Carrot", 100);
        register(GardenId.JURASSIC_MARSH, "primal_peashooter", "Primal Peashooter", 175);
        register(GardenId.JURASSIC_MARSH, "primal_wall_nut", "Primal Wall-nut", 75);
        register(GardenId.JURASSIC_MARSH, "perfume_shroom", "Perfume-shroom", 150);
        register(GardenId.JURASSIC_MARSH, "primal_sunflower", "Primal Sunflower", 75);
        register(GardenId.JURASSIC_MARSH, "primal_potato_mine", "Primal Potato Mine", 50);
        register(GardenId.BIG_WAVE_BEACH, "lily_pad", "Lily Pad", 25);
        register(GardenId.BIG_WAVE_BEACH, "tangle_kelp", "Tangle Kelp", 25);
        register(GardenId.BIG_WAVE_BEACH, "bowling_bulb", "Bowling Bulb", 200);
        register(GardenId.BIG_WAVE_BEACH, "guacodile", "Guacodile", 125);
        register(GardenId.BIG_WAVE_BEACH, "banana_launcher", "Banana Launcher", 500);
        register(GardenId.MODERN_DAY, "moonflower", "Moonflower", 50);
        register(GardenId.MODERN_DAY, "nightshade", "Nightshade", 75);
        register(GardenId.MODERN_DAY, "shadow_shroom", "Shadow-shroom", 50);
        register(GardenId.MODERN_DAY, "dusk_lobber", "Dusk Lobber", 150);
        register(GardenId.MODERN_DAY, "grimrose", "Grimrose", 75);
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

    private static void register(GardenId gardenId, ResourceLocation seedPacketId, String plantId, String displayName, int sunCost, PlantBehavior behavior) {
        PlantSeedDefinition definition = new PlantSeedDefinition(seedPacketId, plantId, displayName, sunCost, behavior, gardenId);
        DEFINITIONS.put(seedPacketId, definition);
        DEFINITIONS_BY_PLANT_ID.put(plantId, definition);
        ORDERED_DEFINITIONS.add(definition);
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
        SPLIT_PEA,
        CHILI_BEAN,
        PEA_POD,
        LIGHTNING_REED,
        MELON_PULT,
        TALL_NUT,
        WINTER_MELON
    }
}

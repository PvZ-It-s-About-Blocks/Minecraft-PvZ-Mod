package net.PvZModders.PvZMod.progression.seed;

import net.PvZModders.PvZMod.item.ModItems;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public record PlantSeedDefinition(ResourceLocation seedPacketId, String plantId, String displayName, int sunCost, PlantBehavior behavior) {
    private static final Map<ResourceLocation, PlantSeedDefinition> DEFINITIONS = new HashMap<>();
    private static final Map<String, PlantSeedDefinition> DEFINITIONS_BY_PLANT_ID = new HashMap<>();

    static {
        register(ModItems.SUNFLOWER_SEED_PACKET.getId(), "sunflower", "Sunflower", 50, PlantBehavior.SUNFLOWER);
        register(ModItems.PEASHOOTER_SEED_PACKET.getId(), "peashooter", "Peashooter", 100, PlantBehavior.PEASHOOTER);
        register(ModItems.WALL_NUT_SEED_PACKET.getId(), "wall_nut", "Wall-nut", 50, PlantBehavior.WALL_NUT);
        register(ModItems.POTATO_MINE_SEED_PACKET.getId(), "potato_mine", "Potato Mine", 25, PlantBehavior.POTATO_MINE);
        register(ModItems.REPEATER_SEED_PACKET.getId(), "repeater", "Repeater", 200, PlantBehavior.REPEATER);
        register(ModItems.CHOMPER_SEED_PACKET.getId(), "chomper", "Chomper", 150, PlantBehavior.CHOMPER);

        register("cabbage_pult", "Cabbage-pult", 100);
        register("bloomerang", "Bloomerang", 175);
        register("iceberg_lettuce", "Iceberg Lettuce", 0);
        register("grave_buster", "Grave Buster", 0);
        register("bonk_choy", "Bonk Choy", 150);
        register("twin_sunflower", "Twin Sunflower", 125);
        register("kernel_pult", "Kernel-pult", 100);
        register("snapdragon", "Snapdragon", 150);
        register("spikeweed", "Spikeweed", 100);
        register("spring_bean", "Spring Bean", 50);
        register("coconut_cannon", "Coconut Cannon", 400);
        register("threepeater", "Threepeater", 300);
        register("spikerock", "Spikerock", 250);
        register("cherry_bomb", "Cherry Bomb", 150);
        register("split_pea", "Split Pea", 125);
        register("chili_bean", "Chili Bean", 50);
        register("pea_pod", "Pea Pod", 125);
        register("lightning_reed", "Lightning Reed", 125);
        register("melon_pult", "Melon-pult", 325);
        register("tall_nut", "Tall-nut", 125);
        register("winter_melon", "Winter Melon", 500);
        register("hot_potato", "Hot Potato", 0);
        register("pepper_pult", "Pepper-pult", 200);
        register("chard_guard", "Chard Guard", 75);
        register("stunion", "Stunion", 25);
        register("rotobaga", "Rotobaga", 150);
        register("red_stinger", "Red Stinger", 150);
        register("akee", "A.K.E.E.", 175);
        register("endurian", "Endurian", 100);
        register("stallia", "Stallia", 0);
        register("gold_leaf", "Gold Leaf", 80);
        register("laser_bean", "Laser Bean", 200);
        register("blover", "Blover", 50);
        register("citron", "Citron", 350);
        register("em_peach", "E.M.Peach", 25);
        register("infi_nut", "Infi-nut", 75);
        register("magnifying_grass", "Magnifying Grass", 50);
        register("tile_turnip", "Tile Turnip", 0);
        register("sun_shroom", "Sun-shroom", 25);
        register("puff_shroom", "Puff-shroom", 0);
        register("fume_shroom", "Fume-shroom", 125);
        register("sun_bean", "Sun Bean", 50);
        register("magnet_shroom", "Magnet-shroom", 100);
        register("phat_beet", "Phat Beet", 150);
        register("celery_stalker", "Celery Stalker", 50);
        register("thyme_warp", "Thyme Warp", 100);
        register("garlic", "Garlic", 50);
        register("spore_shroom", "Spore-shroom", 150);
        register("intensive_carrot", "Intensive Carrot", 100);
        register("primal_peashooter", "Primal Peashooter", 175);
        register("primal_wall_nut", "Primal Wall-nut", 75);
        register("perfume_shroom", "Perfume-shroom", 150);
        register("primal_sunflower", "Primal Sunflower", 75);
        register("primal_potato_mine", "Primal Potato Mine", 50);
        register("lily_pad", "Lily Pad", 25);
        register("tangle_kelp", "Tangle Kelp", 25);
        register("bowling_bulb", "Bowling Bulb", 200);
        register("guacodile", "Guacodile", 125);
        register("banana_launcher", "Banana Launcher", 500);
        register("moonflower", "Moonflower", 50);
        register("nightshade", "Nightshade", 75);
        register("shadow_shroom", "Shadow-shroom", 50);
        register("dusk_lobber", "Dusk Lobber", 150);
        register("grimrose", "Grimrose", 75);
    }

    public static Optional<PlantSeedDefinition> get(ResourceLocation seedPacketId) {
        return Optional.ofNullable(DEFINITIONS.get(seedPacketId));
    }

    public static Optional<PlantSeedDefinition> getByPlantId(String plantId) {
        return Optional.ofNullable(DEFINITIONS_BY_PLANT_ID.get(plantId));
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

    private static void register(String plantId, String displayName, int sunCost) {
        register(plantId, displayName, sunCost, PlantBehavior.PLACEHOLDER);
    }

    private static void register(String plantId, String displayName, int sunCost, PlantBehavior behavior) {
        register(new ResourceLocation("pvz2mod", plantId + "_seed_packet"), plantId, displayName, sunCost, behavior);
    }

    private static void register(ResourceLocation seedPacketId, String plantId, String displayName, int sunCost, PlantBehavior behavior) {
        PlantSeedDefinition definition = new PlantSeedDefinition(seedPacketId, plantId, displayName, sunCost, behavior);
        DEFINITIONS.put(seedPacketId, definition);
        DEFINITIONS_BY_PLANT_ID.put(plantId, definition);
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
        REPEATER
    }
}

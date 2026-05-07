package net.PvZModders.PvZMod.item;

import net.PvZModders.PvZMod.PvZ2Mod;
import net.PvZModders.PvZMod.block.ModBlocks;
import net.PvZModders.PvZMod.item.custom.BiomeDetectorItem;
import net.PvZModders.PvZMod.item.custom.CommandersBucketItem;
import net.PvZModders.PvZMod.item.custom.DinoWhistleItem;
import net.PvZModders.PvZMod.item.custom.FlyingPlaneItem;
import net.PvZModders.PvZMod.item.custom.FreezeRayItem;
import net.PvZModders.PvZMod.item.custom.GardenPlotterItem;
import net.PvZModders.PvZMod.item.custom.JetpackItem;
import net.PvZModders.PvZMod.item.custom.MysticalEyeItem;
import net.PvZModders.PvZMod.item.custom.PirateShipItem;
import net.PvZModders.PvZMod.item.custom.SeedPacketItem;
import net.PvZModders.PvZMod.item.custom.SeedHolderItem;
import net.PvZModders.PvZMod.item.custom.SpeedyMinecartItem;
import net.PvZModders.PvZMod.item.custom.TargetingPriorityChangerItem;
import net.PvZModders.PvZMod.item.custom.TideShellItem;
import net.PvZModders.PvZMod.item.custom.TotemShieldItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, PvZ2Mod.MOD_ID);

    public static final RegistryObject<Item> SUNFLOWER_SEED_PACKET = ITEMS.register("sunflower_seed_packet",
            () -> new SeedPacketItem("sunflower", new Item.Properties()));
    public static final RegistryObject<Item> PEASHOOTER_SEED_PACKET = ITEMS.register("peashooter_seed_packet",
            () -> new SeedPacketItem("peashooter", new Item.Properties()));
    public static final RegistryObject<Item> WALL_NUT_SEED_PACKET = ITEMS.register("wall_nut_seed_packet",
            () -> new SeedPacketItem("wall_nut", new Item.Properties()));
    public static final RegistryObject<Item> POTATO_MINE_SEED_PACKET = ITEMS.register("potato_mine_seed_packet",
            () -> new SeedPacketItem("potato_mine", new Item.Properties()));
    public static final RegistryObject<Item> REPEATER_SEED_PACKET = ITEMS.register("repeater_seed_packet",
            () -> new SeedPacketItem("repeater", new Item.Properties()));
    public static final RegistryObject<Item> CHOMPER_SEED_PACKET = ITEMS.register("chomper_seed_packet",
            () -> new SeedPacketItem("chomper", new Item.Properties()));
    public static final RegistryObject<Item> BLOOMERANG_SEED_PACKET = ITEMS.register("bloomerang_seed_packet",
            () -> new SeedPacketItem("bloomerang", new Item.Properties()));
    public static final RegistryObject<Item> ICEBERG_LETTUCE_SEED_PACKET = ITEMS.register("iceberg_lettuce_seed_packet",
            () -> new SeedPacketItem("iceberg_lettuce", new Item.Properties()));
    public static final RegistryObject<Item> GRAVE_BUSTER_SEED_PACKET = ITEMS.register("grave_buster_seed_packet",
            () -> new SeedPacketItem("grave_buster", new Item.Properties()));
    public static final RegistryObject<Item> BONK_CHOY_SEED_PACKET = ITEMS.register("bonk_choy_seed_packet",
            () -> new SeedPacketItem("bonk_choy", new Item.Properties()));
    public static final RegistryObject<Item> TORCHWOOD_SEED_PACKET = ITEMS.register("torchwood_seed_packet",
            () -> new SeedPacketItem("torchwood", new Item.Properties()));
    public static final RegistryObject<Item> TWIN_SUNFLOWER_SEED_PACKET = ITEMS.register("twin_sunflower_seed_packet",
            () -> new SeedPacketItem("twin_sunflower", new Item.Properties()));
    public static final RegistryObject<Item> KERNEL_PULT_SEED_PACKET = ITEMS.register("kernel_pult_seed_packet",
            () -> new SeedPacketItem("kernel_pult", new Item.Properties()));
    public static final RegistryObject<Item> SNAPDRAGON_SEED_PACKET = ITEMS.register("snapdragon_seed_packet",
            () -> new SeedPacketItem("snapdragon", new Item.Properties()));
    public static final RegistryObject<Item> SPIKEWEED_SEED_PACKET = ITEMS.register("spikeweed_seed_packet",
            () -> new SeedPacketItem("spikeweed", new Item.Properties()));
    public static final RegistryObject<Item> SPRING_BEAN_SEED_PACKET = ITEMS.register("spring_bean_seed_packet",
            () -> new SeedPacketItem("spring_bean", new Item.Properties()));
    public static final RegistryObject<Item> COCONUT_CANNON_SEED_PACKET = ITEMS.register("coconut_cannon_seed_packet",
            () -> new SeedPacketItem("coconut_cannon", new Item.Properties()));
    public static final RegistryObject<Item> THREEPEATER_SEED_PACKET = ITEMS.register("threepeater_seed_packet",
            () -> new SeedPacketItem("threepeater", new Item.Properties()));
    public static final RegistryObject<Item> SPIKEROCK_SEED_PACKET = ITEMS.register("spikerock_seed_packet",
            () -> new SeedPacketItem("spikerock", new Item.Properties()));
    public static final RegistryObject<Item> CHERRY_BOMB_SEED_PACKET = ITEMS.register("cherry_bomb_seed_packet",
            () -> new SeedPacketItem("cherry_bomb", new Item.Properties()));
    public static final RegistryObject<Item> SPLIT_PEA_SEED_PACKET = ITEMS.register("split_pea_seed_packet",
            () -> new SeedPacketItem("split_pea", new Item.Properties()));
    public static final RegistryObject<Item> CHILI_BEAN_SEED_PACKET = ITEMS.register("chili_bean_seed_packet",
            () -> new SeedPacketItem("chili_bean", new Item.Properties()));
    public static final RegistryObject<Item> PEA_POD_SEED_PACKET = ITEMS.register("pea_pod_seed_packet",
            () -> new SeedPacketItem("pea_pod", new Item.Properties()));
    public static final RegistryObject<Item> LIGHTNING_REED_SEED_PACKET = ITEMS.register("lightning_reed_seed_packet",
            () -> new SeedPacketItem("lightning_reed", new Item.Properties()));
    public static final RegistryObject<Item> MELON_PULT_SEED_PACKET = ITEMS.register("melon_pult_seed_packet",
            () -> new SeedPacketItem("melon_pult", new Item.Properties()));
    public static final RegistryObject<Item> TALL_NUT_SEED_PACKET = ITEMS.register("tall_nut_seed_packet",
            () -> new SeedPacketItem("tall_nut", new Item.Properties()));
    public static final RegistryObject<Item> WINTER_MELON_SEED_PACKET = ITEMS.register("winter_melon_seed_packet",
            () -> new SeedPacketItem("winter_melon", new Item.Properties()));
    public static final RegistryObject<Item> HOT_POTATO_SEED_PACKET = ITEMS.register("hot_potato_seed_packet",
            () -> new SeedPacketItem("hot_potato", new Item.Properties()));
    public static final RegistryObject<Item> PEPPER_PULT_SEED_PACKET = ITEMS.register("pepper_pult_seed_packet",
            () -> new SeedPacketItem("pepper_pult", new Item.Properties()));
    public static final RegistryObject<Item> CHARD_GUARD_SEED_PACKET = ITEMS.register("chard_guard_seed_packet",
            () -> new SeedPacketItem("chard_guard", new Item.Properties()));
    public static final RegistryObject<Item> STUNION_SEED_PACKET = ITEMS.register("stunion_seed_packet",
            () -> new SeedPacketItem("stunion", new Item.Properties()));
    public static final RegistryObject<Item> ROTOBAGA_SEED_PACKET = ITEMS.register("rotobaga_seed_packet",
            () -> new SeedPacketItem("rotobaga", new Item.Properties()));
    public static final RegistryObject<Item> RED_STINGER_SEED_PACKET = ITEMS.register("red_stinger_seed_packet",
            () -> new SeedPacketItem("red_stinger", new Item.Properties()));
    public static final RegistryObject<Item> AKEE_SEED_PACKET = ITEMS.register("akee_seed_packet",
            () -> new SeedPacketItem("akee", new Item.Properties()));
    public static final RegistryObject<Item> ENDURIAN_SEED_PACKET = ITEMS.register("endurian_seed_packet",
            () -> new SeedPacketItem("endurian", new Item.Properties()));
    public static final RegistryObject<Item> STALLIA_SEED_PACKET = ITEMS.register("stallia_seed_packet",
            () -> new SeedPacketItem("stallia", new Item.Properties()));
    public static final RegistryObject<Item> GOLD_LEAF_SEED_PACKET = ITEMS.register("gold_leaf_seed_packet",
            () -> new SeedPacketItem("gold_leaf", new Item.Properties()));
    public static final RegistryObject<Item> SUN_SHROOM_SEED_PACKET = ITEMS.register("sun_shroom_seed_packet",
            () -> new SeedPacketItem("sun_shroom", new Item.Properties()));
    public static final RegistryObject<Item> PUFF_SHROOM_SEED_PACKET = ITEMS.register("puff_shroom_seed_packet",
            () -> new SeedPacketItem("puff_shroom", new Item.Properties()));
    public static final RegistryObject<Item> FUME_SHROOM_SEED_PACKET = ITEMS.register("fume_shroom_seed_packet",
            () -> new SeedPacketItem("fume_shroom", new Item.Properties()));
    public static final RegistryObject<Item> SUN_BEAN_SEED_PACKET = ITEMS.register("sun_bean_seed_packet",
            () -> new SeedPacketItem("sun_bean", new Item.Properties()));
    public static final RegistryObject<Item> MAGNET_SHROOM_SEED_PACKET = ITEMS.register("magnet_shroom_seed_packet",
            () -> new SeedPacketItem("magnet_shroom", new Item.Properties()));
    public static final RegistryObject<Item> PRIMAL_PEASHOOTER_SEED_PACKET = ITEMS.register("primal_peashooter_seed_packet",
            () -> new SeedPacketItem("primal_peashooter", new Item.Properties()));
    public static final RegistryObject<Item> PRIMAL_WALL_NUT_SEED_PACKET = ITEMS.register("primal_wall_nut_seed_packet",
            () -> new SeedPacketItem("primal_wall_nut", new Item.Properties()));
    public static final RegistryObject<Item> PERFUME_SHROOM_SEED_PACKET = ITEMS.register("perfume_shroom_seed_packet",
            () -> new SeedPacketItem("perfume_shroom", new Item.Properties()));
    public static final RegistryObject<Item> PRIMAL_SUNFLOWER_SEED_PACKET = ITEMS.register("primal_sunflower_seed_packet",
            () -> new SeedPacketItem("primal_sunflower", new Item.Properties()));
    public static final RegistryObject<Item> PRIMAL_POTATO_MINE_SEED_PACKET = ITEMS.register("primal_potato_mine_seed_packet",
            () -> new SeedPacketItem("primal_potato_mine", new Item.Properties()));
    public static final RegistryObject<Item> PHAT_BEET_SEED_PACKET = ITEMS.register("phat_beet_seed_packet",
            () -> new SeedPacketItem("phat_beet", new Item.Properties()));
    public static final RegistryObject<Item> CELERY_STALKER_SEED_PACKET = ITEMS.register("celery_stalker_seed_packet",
            () -> new SeedPacketItem("celery_stalker", new Item.Properties()));
    public static final RegistryObject<Item> THYME_WARP_SEED_PACKET = ITEMS.register("thyme_warp_seed_packet",
            () -> new SeedPacketItem("thyme_warp", new Item.Properties()));
    public static final RegistryObject<Item> GARLIC_SEED_PACKET = ITEMS.register("garlic_seed_packet",
            () -> new SeedPacketItem("garlic", new Item.Properties()));
    public static final RegistryObject<Item> SPORE_SHROOM_SEED_PACKET = ITEMS.register("spore_shroom_seed_packet",
            () -> new SeedPacketItem("spore_shroom", new Item.Properties()));
    public static final RegistryObject<Item> INTENSIVE_CARROT_SEED_PACKET = ITEMS.register("intensive_carrot_seed_packet",
            () -> new SeedPacketItem("intensive_carrot", new Item.Properties()));
    public static final RegistryObject<Item> LASER_BEAN_SEED_PACKET = ITEMS.register("laser_bean_seed_packet",
            () -> new SeedPacketItem("laser_bean", new Item.Properties()));
    public static final RegistryObject<Item> BLOVER_SEED_PACKET = ITEMS.register("blover_seed_packet",
            () -> new SeedPacketItem("blover", new Item.Properties()));
    public static final RegistryObject<Item> CITRON_SEED_PACKET = ITEMS.register("citron_seed_packet",
            () -> new SeedPacketItem("citron", new Item.Properties()));
    public static final RegistryObject<Item> EM_PEACH_SEED_PACKET = ITEMS.register("em_peach_seed_packet",
            () -> new SeedPacketItem("em_peach", new Item.Properties()));
    public static final RegistryObject<Item> INFI_NUT_SEED_PACKET = ITEMS.register("infi_nut_seed_packet",
            () -> new SeedPacketItem("infi_nut", new Item.Properties()));
    public static final RegistryObject<Item> MAGNIFYING_GRASS_SEED_PACKET = ITEMS.register("magnifying_grass_seed_packet",
            () -> new SeedPacketItem("magnifying_grass", new Item.Properties()));
    public static final RegistryObject<Item> TILE_TURNIP_SEED_PACKET = ITEMS.register("tile_turnip_seed_packet",
            () -> new SeedPacketItem("tile_turnip", new Item.Properties()));
    public static final RegistryObject<Item> LILY_PAD_SEED_PACKET = ITEMS.register("lily_pad_seed_packet",
            () -> new SeedPacketItem("lily_pad", new Item.Properties()));
    public static final RegistryObject<Item> TANGLE_KELP_SEED_PACKET = ITEMS.register("tangle_kelp_seed_packet",
            () -> new SeedPacketItem("tangle_kelp", new Item.Properties()));
    public static final RegistryObject<Item> BOWLING_BULB_SEED_PACKET = ITEMS.register("bowling_bulb_seed_packet",
            () -> new SeedPacketItem("bowling_bulb", new Item.Properties()));
    public static final RegistryObject<Item> GUACODILE_SEED_PACKET = ITEMS.register("guacodile_seed_packet",
            () -> new SeedPacketItem("guacodile", new Item.Properties()));
    public static final RegistryObject<Item> BANANA_LAUNCHER_SEED_PACKET = ITEMS.register("banana_launcher_seed_packet",
            () -> new SeedPacketItem("banana_launcher", new Item.Properties()));
    public static final RegistryObject<Item> MOONFLOWER_SEED_PACKET = ITEMS.register("moonflower_seed_packet",
            () -> new SeedPacketItem("moonflower", new Item.Properties()));
    public static final RegistryObject<Item> NIGHTSHADE_SEED_PACKET = ITEMS.register("nightshade_seed_packet",
            () -> new SeedPacketItem("nightshade", new Item.Properties()));
    public static final RegistryObject<Item> SHADOW_SHROOM_SEED_PACKET = ITEMS.register("shadow_shroom_seed_packet",
            () -> new SeedPacketItem("shadow_shroom", new Item.Properties()));
    public static final RegistryObject<Item> DUSK_LOBBER_SEED_PACKET = ITEMS.register("dusk_lobber_seed_packet",
            () -> new SeedPacketItem("dusk_lobber", new Item.Properties()));
    public static final RegistryObject<Item> GRIMROSE_SEED_PACKET = ITEMS.register("grimrose_seed_packet",
            () -> new SeedPacketItem("grimrose", new Item.Properties()));
    public static final RegistryObject<Item> SUNDROP = ITEMS.register("sundrop",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SUN_PILLAR = ITEMS.register("sun_pillar",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> PEA = ITEMS.register("pea",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> GARDEN_PLOTTER = ITEMS.register("garden_plotter",
            () -> new GardenPlotterItem(ModBlocks.GARDEN_PLOTTER.get(), new Item.Properties()));
    public static final RegistryObject<Item> BIOME_DETECTOR = ITEMS.register("biome_detector",
            () -> new BiomeDetectorItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> SEED_HOLDER = ITEMS.register("seed_holder",
            () -> new SeedHolderItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> TARGETING_PRIORITY_CHANGER = ITEMS.register("targeting_priority_changer",
            () -> new TargetingPriorityChangerItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> SPEEDY_MINECART = ITEMS.register("speedy_minecart",
            () -> new SpeedyMinecartItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> FLYING_PLANE = ITEMS.register("flying_plane",
            () -> new FlyingPlaneItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> DINO_WHISTLE = ITEMS.register("dino_whistle",
            () -> new DinoWhistleItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> TOTEM_SHIELD = ITEMS.register("totem_shield",
            () -> new TotemShieldItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> FREEZE_RAY = ITEMS.register("freeze_ray",
            () -> new FreezeRayItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> JETPACK = ITEMS.register("jetpack",
            () -> new JetpackItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> CITRON_HELMET = ITEMS.register("citron_helmet",
            () -> new Item(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> CITRON_CHESTPLATE = ITEMS.register("citron_chestplate",
            () -> new Item(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> CITRON_LEGGINGS = ITEMS.register("citron_leggings",
            () -> new Item(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> CITRON_BOOTS = ITEMS.register("citron_boots",
            () -> new Item(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> TIDE_SHELL = ITEMS.register("tide_shell",
            () -> new TideShellItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> COMMANDERS_BUCKET = ITEMS.register("commanders_bucket",
            () -> new CommandersBucketItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> PIRATE_CANNON = ITEMS.register("pirate_cannon",
            () -> new Item(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> CAPTAINS_HELMET = ITEMS.register("captains_helmet",
            () -> new Item(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> CAPTAINS_CHESTPLATE = ITEMS.register("captains_chestplate",
            () -> new Item(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> CAPTAINS_LEGGINGS = ITEMS.register("captains_leggings",
            () -> new Item(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> CAPTAINS_BOOTS = ITEMS.register("captains_boots",
            () -> new Item(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> PIRATE_SHIP = ITEMS.register("pirate_ship",
            () -> new PirateShipItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> MYSTICAL_EYE = ITEMS.register("mystical_eye",
            () -> new MysticalEyeItem(new Item.Properties().stacksTo(1)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}

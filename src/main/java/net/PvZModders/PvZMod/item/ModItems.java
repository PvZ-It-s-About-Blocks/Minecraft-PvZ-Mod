package net.PvZModders.PvZMod.item;

import net.PvZModders.PvZMod.PvZ2Mod;
import net.PvZModders.PvZMod.block.ModBlocks;
import net.PvZModders.PvZMod.item.custom.BiomeDetectorItem;
import net.PvZModders.PvZMod.item.custom.DinoWhistleItem;
import net.PvZModders.PvZMod.item.custom.FlyingPlaneItem;
import net.PvZModders.PvZMod.item.custom.GardenPlotterItem;
import net.PvZModders.PvZMod.item.custom.SeedPacketItem;
import net.PvZModders.PvZMod.item.custom.SeedHolderItem;
import net.PvZModders.PvZMod.item.custom.SpeedyMinecartItem;
import net.PvZModders.PvZMod.item.custom.TargetingPriorityChangerItem;
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
    public static final RegistryObject<Item> SUNDROP = ITEMS.register("sundrop",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SUN_PILLAR = ITEMS.register("sun_pillar",
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

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}

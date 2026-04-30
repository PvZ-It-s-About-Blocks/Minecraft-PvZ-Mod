package net.PvZModders.PvZMod.item;

import net.PvZModders.PvZMod.PvZ2Mod;
import net.PvZModders.PvZMod.block.ModBlocks;
import net.PvZModders.PvZMod.item.custom.BiomeDetectorItem;
import net.PvZModders.PvZMod.item.custom.GardenPlotterItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, PvZ2Mod.MOD_ID);

    public static final RegistryObject<Item> SUNFLOWER_SEED_PACKET = ITEMS.register("sunflower_seed_packet",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> PEASHOOTER_SEED_PACKET = ITEMS.register("peashooter_seed_packet",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> GARDEN_PLOTTER = ITEMS.register("garden_plotter",
            () -> new GardenPlotterItem(ModBlocks.GARDEN_PLOTTER.get(), new Item.Properties()));
    public static final RegistryObject<Item> BIOME_DETECTOR = ITEMS.register("biome_detector",
            () -> new BiomeDetectorItem(new Item.Properties().stacksTo(1)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}

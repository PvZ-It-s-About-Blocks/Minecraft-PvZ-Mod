package io.github.pvz_coders.pvzmod;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

// Main mod class for PvZ: It’s About Blocks
@Mod(MinecraftPvZMod.MODID)
public class MinecraftPvZMod {
    // Define mod ID in a common place for everything to reference
    public static final String MODID = "pvz_mod";
    
    // Logger for debugging
    private static final Logger LOGGER = LogUtils.getLogger();

    // Deferred Registers for Blocks, Items, and Creative Tabs
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    // Example Block & Item Registrations
    public static final RegistryObject<Block> PEASHOOTER_BLOCK = BLOCKS.register("peashooter_block",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.PLANT)));

    public static final RegistryObject<Item> PEASHOOTER_ITEM = ITEMS.register("peashooter_item",
            () -> new BlockItem(PEASHOOTER_BLOCK.get(), new Item.Properties()));

    public static final RegistryObject<Item> SUNFLOWER_ITEM = ITEMS.register("sunflower_item",
            () -> new Item(new Item.Properties().food(new FoodProperties.Builder()
                    .alwaysEdible().nutrition(2).saturationModifier(2.5f).build())));

    // Creative Tab
    public static final RegistryObject<CreativeModeTab> PVZ_TAB = CREATIVE_MODE_TABS.register("pvz_tab",
            () -> CreativeModeTab.builder()
                    .withTabsBefore(CreativeModeTabs.COMBAT)
                    .icon(() -> PEASHOOTER_ITEM.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(PEASHOOTER_ITEM.get());
                        output.accept(SUNFLOWER_ITEM.get());
                    }).build());

    public MinecraftPvZMod(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();

        // Register setup methods
        modEventBus.addListener(this::commonSetup);

        // Register Deferred Registers to the mod event bus
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);

        // Register the mod for Forge events
        MinecraftForge.EVENT_BUS.register(this);

        // Add items to creative mode
        modEventBus.addListener(this::addCreative);

        // Register configuration settings
        context.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("[PvZ Mod] Common setup initialized.");
    }

    // Add custom items to the creative inventory
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            event.accept(PEASHOOTER_ITEM);
        }
    }

    // Event for when the server starts
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("[PvZ Mod] Server is starting...");
    }

    // Client-side setup
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            LOGGER.info("[PvZ Mod] Client setup initialized.");
            LOGGER.info("Minecraft Player: {}", Minecraft.getInstance().getUser().getName());
        }
    }
}

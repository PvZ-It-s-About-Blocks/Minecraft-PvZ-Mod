package net.PvZModders.PvZMod;

import com.mojang.logging.LogUtils;
import net.PvZModders.PvZMod.block.ModBlocks;
import net.PvZModders.PvZMod.block.entity.ModBlockEntities;
import net.PvZModders.PvZMod.client.SunExperienceOrbRenderer;
import net.PvZModders.PvZMod.client.seed.ClientSeedStorage;
import net.PvZModders.PvZMod.client.screen.BiomeDetectorScreen;
import net.PvZModders.PvZMod.client.screen.GardenTotemScreen;
import net.PvZModders.PvZMod.entity.client.PennyVanRenderer;
import net.PvZModders.PvZMod.entity.client.WildWestMinecartRenderer;
import net.PvZModders.PvZMod.entity.client.pennytest;
import net.PvZModders.PvZMod.entity.ModEntities;
import net.PvZModders.PvZMod.entity.custom.PennyVanEntity;
import net.PvZModders.PvZMod.entity.custom.JurassicDinosaurEntity;
import net.PvZModders.PvZMod.item.ModCreativeModTabs;
import net.PvZModders.PvZMod.item.ModItems;
import net.PvZModders.PvZMod.menu.ModMenuTypes;
import net.PvZModders.PvZMod.network.ModMessages;
import net.PvZModders.PvZMod.progression.GardenDefinitions;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.SnowGolemRenderer;
import net.minecraft.client.renderer.entity.ZombieRenderer;
import net.minecraft.client.renderer.entity.MinecartRenderer;
import net.minecraft.client.renderer.entity.SnifferRenderer;
import net.minecraft.client.renderer.item.CompassItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.animal.sniffer.Sniffer;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.CompassItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(PvZ2Mod.MOD_ID)
public class PvZ2Mod {
    public static final String MOD_ID = "pvz2mod";
    public static final Logger LOGGER = LogUtils.getLogger();

    public PvZ2Mod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModCreativeModTabs.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModEntities.register(modEventBus);
        ModMenuTypes.register(modEventBus);
        ModMessages.register();

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::addCreative);
        modEventBus.addListener(this::registerEntityAttributes);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Loaded {} garden definitions for progression", GardenDefinitions.all().size());
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(ModItems.SUNFLOWER_SEED_PACKET);
            event.accept(ModItems.PEASHOOTER_SEED_PACKET);
            event.accept(ModItems.WALL_NUT_SEED_PACKET);
            event.accept(ModItems.POTATO_MINE_SEED_PACKET);
            event.accept(ModItems.REPEATER_SEED_PACKET);
            event.accept(ModItems.CHOMPER_SEED_PACKET);
            event.accept(ModItems.BLOOMERANG_SEED_PACKET);
            event.accept(ModItems.ICEBERG_LETTUCE_SEED_PACKET);
            event.accept(ModItems.GRAVE_BUSTER_SEED_PACKET);
            event.accept(ModItems.BONK_CHOY_SEED_PACKET);
            event.accept(ModItems.TORCHWOOD_SEED_PACKET);
            event.accept(ModItems.TWIN_SUNFLOWER_SEED_PACKET);
            event.accept(ModItems.KERNEL_PULT_SEED_PACKET);
            event.accept(ModItems.SNAPDRAGON_SEED_PACKET);
            event.accept(ModItems.SPIKEWEED_SEED_PACKET);
            event.accept(ModItems.SPRING_BEAN_SEED_PACKET);
            event.accept(ModItems.COCONUT_CANNON_SEED_PACKET);
            event.accept(ModItems.THREEPEATER_SEED_PACKET);
            event.accept(ModItems.SPIKEROCK_SEED_PACKET);
            event.accept(ModItems.CHERRY_BOMB_SEED_PACKET);
            event.accept(ModItems.SPLIT_PEA_SEED_PACKET);
            event.accept(ModItems.CHILI_BEAN_SEED_PACKET);
            event.accept(ModItems.PEA_POD_SEED_PACKET);
            event.accept(ModItems.LIGHTNING_REED_SEED_PACKET);
            event.accept(ModItems.MELON_PULT_SEED_PACKET);
            event.accept(ModItems.TALL_NUT_SEED_PACKET);
            event.accept(ModItems.WINTER_MELON_SEED_PACKET);
            event.accept(ModItems.HOT_POTATO_SEED_PACKET);
            event.accept(ModItems.PEPPER_PULT_SEED_PACKET);
            event.accept(ModItems.CHARD_GUARD_SEED_PACKET);
            event.accept(ModItems.STUNION_SEED_PACKET);
            event.accept(ModItems.ROTOBAGA_SEED_PACKET);
            event.accept(ModItems.RED_STINGER_SEED_PACKET);
            event.accept(ModItems.AKEE_SEED_PACKET);
            event.accept(ModItems.ENDURIAN_SEED_PACKET);
            event.accept(ModItems.STALLIA_SEED_PACKET);
            event.accept(ModItems.GOLD_LEAF_SEED_PACKET);
            event.accept(ModItems.SUN_SHROOM_SEED_PACKET);
            event.accept(ModItems.PUFF_SHROOM_SEED_PACKET);
            event.accept(ModItems.FUME_SHROOM_SEED_PACKET);
            event.accept(ModItems.SUN_BEAN_SEED_PACKET);
            event.accept(ModItems.MAGNET_SHROOM_SEED_PACKET);
            event.accept(ModItems.PRIMAL_PEASHOOTER_SEED_PACKET);
            event.accept(ModItems.PRIMAL_WALL_NUT_SEED_PACKET);
            event.accept(ModItems.PERFUME_SHROOM_SEED_PACKET);
            event.accept(ModItems.PRIMAL_SUNFLOWER_SEED_PACKET);
            event.accept(ModItems.PRIMAL_POTATO_MINE_SEED_PACKET);
            event.accept(ModItems.PHAT_BEET_SEED_PACKET);
            event.accept(ModItems.CELERY_STALKER_SEED_PACKET);
            event.accept(ModItems.THYME_WARP_SEED_PACKET);
            event.accept(ModItems.GARLIC_SEED_PACKET);
            event.accept(ModItems.SPORE_SHROOM_SEED_PACKET);
            event.accept(ModItems.INTENSIVE_CARROT_SEED_PACKET);
            event.accept(ModItems.LASER_BEAN_SEED_PACKET);
            event.accept(ModItems.BLOVER_SEED_PACKET);
            event.accept(ModItems.CITRON_SEED_PACKET);
            event.accept(ModItems.EM_PEACH_SEED_PACKET);
            event.accept(ModItems.INFI_NUT_SEED_PACKET);
            event.accept(ModItems.MAGNIFYING_GRASS_SEED_PACKET);
            event.accept(ModItems.TILE_TURNIP_SEED_PACKET);
            event.accept(ModItems.LILY_PAD_SEED_PACKET);
            event.accept(ModItems.TANGLE_KELP_SEED_PACKET);
            event.accept(ModItems.BOWLING_BULB_SEED_PACKET);
            event.accept(ModItems.GUACODILE_SEED_PACKET);
            event.accept(ModItems.BANANA_LAUNCHER_SEED_PACKET);
            event.accept(ModItems.GARDEN_PLOTTER);
            event.accept(ModItems.BIOME_DETECTOR);
            event.accept(ModItems.SEED_HOLDER);
            event.accept(ModItems.TARGETING_PRIORITY_CHANGER);
            event.accept(ModItems.SPEEDY_MINECART);
            event.accept(ModItems.FLYING_PLANE);
            event.accept(ModItems.DINO_WHISTLE);
            event.accept(ModItems.TOTEM_SHIELD);
            event.accept(ModItems.FREEZE_RAY);
            event.accept(ModItems.JETPACK);
            event.accept(ModItems.CITRON_HELMET);
            event.accept(ModItems.CITRON_CHESTPLATE);
            event.accept(ModItems.CITRON_LEGGINGS);
            event.accept(ModItems.CITRON_BOOTS);
            event.accept(ModItems.TIDE_SHELL);
            event.accept(ModItems.PIRATE_CANNON);
            event.accept(ModItems.CAPTAINS_HELMET);
            event.accept(ModItems.CAPTAINS_CHESTPLATE);
            event.accept(ModItems.CAPTAINS_LEGGINGS);
            event.accept(ModItems.CAPTAINS_BOOTS);
            event.accept(ModItems.PIRATE_SHIP);
        }
    }

    private void registerEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.PENNY_VAN.get(), PennyVanEntity.createAttributes().build());
        event.put(ModEntities.GARDEN_ZOMBIE.get(), Zombie.createAttributes().build());
        event.put(ModEntities.ALL_PLANTS.get(), SnowGolem.createAttributes().build());
        event.put(ModEntities.JURASSIC_DINOSAUR.get(), Sniffer.createAttributes().build());
        for (var plantEntityType : ModEntities.plantEntityTypes()) {
            event.put(plantEntityType.get(), SnowGolem.createAttributes().build());
        }
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            event.enqueueWork(() -> {
                net.minecraft.client.renderer.entity.EntityRenderers.register(ModEntities.PENNY_VAN.get(), PennyVanRenderer::new);
                net.minecraft.client.renderer.entity.EntityRenderers.register(ModEntities.GARDEN_ZOMBIE.get(), ZombieRenderer::new);
                net.minecraft.client.renderer.entity.EntityRenderers.register(ModEntities.ALL_PLANTS.get(), SnowGolemRenderer::new);
                net.minecraft.client.renderer.entity.EntityRenderers.register(ModEntities.SPEEDY_MINECART.get(), context -> new MinecartRenderer<>(context, ModelLayers.MINECART));
                net.minecraft.client.renderer.entity.EntityRenderers.register(ModEntities.WILD_WEST_MINECART.get(), WildWestMinecartRenderer::new);
                net.minecraft.client.renderer.entity.EntityRenderers.register(ModEntities.FLYING_PLANE.get(), context -> new MinecartRenderer<>(context, ModelLayers.MINECART));
                net.minecraft.client.renderer.entity.EntityRenderers.register(ModEntities.JURASSIC_DINOSAUR.get(), SnifferRenderer::new);
                for (var plantEntityType : ModEntities.plantEntityTypes()) {
                    net.minecraft.client.renderer.entity.EntityRenderers.register(plantEntityType.get(), SnowGolemRenderer::new);
                }
                MenuScreens.register(ModMenuTypes.GARDEN_TOTEM.get(), GardenTotemScreen::new);
                MenuScreens.register(ModMenuTypes.BIOME_DETECTOR.get(), BiomeDetectorScreen::new);
                ItemProperties.register(ModItems.BIOME_DETECTOR.get(), new ResourceLocation("angle"), new CompassItemPropertyFunction((level, stack, entity) ->
                        CompassItem.isLodestoneCompass(stack) ? CompassItem.getLodestonePosition(stack.getOrCreateTag()) : CompassItem.getSpawnPosition(level)
                ));
                ItemProperties.register(ModItems.SEED_HOLDER.get(), new ResourceLocation(PvZ2Mod.MOD_ID, "enabled"), (stack, level, entity, seed) ->
                        ClientSeedStorage.seedModeEnabled() ? 1.0F : 0.0F
                );
            });
        }

        @SubscribeEvent
        public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
            event.registerLayerDefinition(pennytest.LAYER_LOCATION, pennytest::createBodyLayer);
        }

        @SubscribeEvent
        public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(ModEntities.SUN.get(), SunExperienceOrbRenderer::new);
        }
    }
}

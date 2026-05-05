package net.PvZModders.PvZMod;

import com.mojang.logging.LogUtils;
import net.PvZModders.PvZMod.block.ModBlocks;
import net.PvZModders.PvZMod.block.entity.ModBlockEntities;
import net.PvZModders.PvZMod.client.SunExperienceOrbRenderer;
import net.PvZModders.PvZMod.client.seed.ClientSeedStorage;
import net.PvZModders.PvZMod.client.screen.BiomeDetectorScreen;
import net.PvZModders.PvZMod.client.screen.GardenTotemScreen;
import net.PvZModders.PvZMod.entity.client.PennyVanRenderer;
import net.PvZModders.PvZMod.entity.client.pennytest;
import net.PvZModders.PvZMod.entity.ModEntities;
import net.PvZModders.PvZMod.entity.custom.PennyVanEntity;
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
import net.minecraft.client.renderer.item.CompassItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.SnowGolem;
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
            event.accept(ModItems.SPLIT_PEA_SEED_PACKET);
            event.accept(ModItems.CHILI_BEAN_SEED_PACKET);
            event.accept(ModItems.PEA_POD_SEED_PACKET);
            event.accept(ModItems.LIGHTNING_REED_SEED_PACKET);
            event.accept(ModItems.MELON_PULT_SEED_PACKET);
            event.accept(ModItems.TALL_NUT_SEED_PACKET);
            event.accept(ModItems.WINTER_MELON_SEED_PACKET);
            event.accept(ModItems.GARDEN_PLOTTER);
            event.accept(ModItems.BIOME_DETECTOR);
            event.accept(ModItems.SEED_HOLDER);
            event.accept(ModItems.TARGETING_PRIORITY_CHANGER);
            event.accept(ModItems.SPEEDY_MINECART);
        }
    }

    private void registerEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.PENNY_VAN.get(), PennyVanEntity.createAttributes().build());
        event.put(ModEntities.GARDEN_ZOMBIE.get(), Zombie.createAttributes().build());
        event.put(ModEntities.ALL_PLANTS.get(), SnowGolem.createAttributes().build());
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
                net.minecraft.client.renderer.entity.EntityRenderers.register(EntityType.EXPERIENCE_ORB, SunExperienceOrbRenderer::new);
                net.minecraft.client.renderer.entity.EntityRenderers.register(ModEntities.SUN.get(), SunExperienceOrbRenderer::new);
                net.minecraft.client.renderer.entity.EntityRenderers.register(ModEntities.GARDEN_ZOMBIE.get(), ZombieRenderer::new);
                net.minecraft.client.renderer.entity.EntityRenderers.register(ModEntities.ALL_PLANTS.get(), SnowGolemRenderer::new);
                net.minecraft.client.renderer.entity.EntityRenderers.register(ModEntities.SPEEDY_MINECART.get(), context -> new MinecartRenderer<>(context, ModelLayers.MINECART));
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
    }
}

package net.PvZModders.PvZMod.item;

import net.PvZModders.PvZMod.PvZ2Mod;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeModTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, PvZ2Mod.MOD_ID);

    public static final RegistryObject<CreativeModeTab> PVZ_TAB = CREATIVE_MODE_TABS.register("pvz_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.SUNFLOWER_SEED_PACKET.get()))
                    .title(Component.translatable("creativetab.pvz_tab"))
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.SUNFLOWER_SEED_PACKET.get());
                        output.accept(ModItems.PEASHOOTER_SEED_PACKET.get());
                        output.accept(ModItems.WALL_NUT_SEED_PACKET.get());
                        output.accept(ModItems.POTATO_MINE_SEED_PACKET.get());
                        output.accept(ModItems.REPEATER_SEED_PACKET.get());
                        output.accept(ModItems.CHOMPER_SEED_PACKET.get());
                        output.accept(ModItems.BLOOMERANG_SEED_PACKET.get());
                        output.accept(ModItems.ICEBERG_LETTUCE_SEED_PACKET.get());
                        output.accept(ModItems.GRAVE_BUSTER_SEED_PACKET.get());
                        output.accept(ModItems.BONK_CHOY_SEED_PACKET.get());
                        output.accept(ModItems.TORCHWOOD_SEED_PACKET.get());
                        output.accept(ModItems.TWIN_SUNFLOWER_SEED_PACKET.get());
                        output.accept(ModItems.KERNEL_PULT_SEED_PACKET.get());
                        output.accept(ModItems.SNAPDRAGON_SEED_PACKET.get());
                        output.accept(ModItems.SPIKEWEED_SEED_PACKET.get());
                        output.accept(ModItems.SPRING_BEAN_SEED_PACKET.get());
                        output.accept(ModItems.COCONUT_CANNON_SEED_PACKET.get());
                        output.accept(ModItems.THREEPEATER_SEED_PACKET.get());
                        output.accept(ModItems.SPIKEROCK_SEED_PACKET.get());
                        output.accept(ModItems.CHERRY_BOMB_SEED_PACKET.get());
                        output.accept(ModItems.SPLIT_PEA_SEED_PACKET.get());
                        output.accept(ModItems.CHILI_BEAN_SEED_PACKET.get());
                        output.accept(ModItems.PEA_POD_SEED_PACKET.get());
                        output.accept(ModItems.LIGHTNING_REED_SEED_PACKET.get());
                        output.accept(ModItems.MELON_PULT_SEED_PACKET.get());
                        output.accept(ModItems.TALL_NUT_SEED_PACKET.get());
                        output.accept(ModItems.WINTER_MELON_SEED_PACKET.get());
                        output.accept(ModItems.RED_STINGER_SEED_PACKET.get());
                        output.accept(ModItems.AKEE_SEED_PACKET.get());
                        output.accept(ModItems.ENDURIAN_SEED_PACKET.get());
                        output.accept(ModItems.STALLIA_SEED_PACKET.get());
                        output.accept(ModItems.GOLD_LEAF_SEED_PACKET.get());
                        output.accept(ModItems.SUN_SHROOM_SEED_PACKET.get());
                        output.accept(ModItems.PUFF_SHROOM_SEED_PACKET.get());
                        output.accept(ModItems.FUME_SHROOM_SEED_PACKET.get());
                        output.accept(ModItems.SUN_BEAN_SEED_PACKET.get());
                        output.accept(ModItems.MAGNET_SHROOM_SEED_PACKET.get());
                        output.accept(ModItems.PRIMAL_PEASHOOTER_SEED_PACKET.get());
                        output.accept(ModItems.PRIMAL_WALL_NUT_SEED_PACKET.get());
                        output.accept(ModItems.PERFUME_SHROOM_SEED_PACKET.get());
                        output.accept(ModItems.PRIMAL_SUNFLOWER_SEED_PACKET.get());
                        output.accept(ModItems.PRIMAL_POTATO_MINE_SEED_PACKET.get());
                        output.accept(ModItems.PHAT_BEET_SEED_PACKET.get());
                        output.accept(ModItems.CELERY_STALKER_SEED_PACKET.get());
                        output.accept(ModItems.THYME_WARP_SEED_PACKET.get());
                        output.accept(ModItems.GARLIC_SEED_PACKET.get());
                        output.accept(ModItems.SPORE_SHROOM_SEED_PACKET.get());
                        output.accept(ModItems.INTENSIVE_CARROT_SEED_PACKET.get());
                        output.accept(ModItems.LASER_BEAN_SEED_PACKET.get());
                        output.accept(ModItems.BLOVER_SEED_PACKET.get());
                        output.accept(ModItems.CITRON_SEED_PACKET.get());
                        output.accept(ModItems.EM_PEACH_SEED_PACKET.get());
                        output.accept(ModItems.INFI_NUT_SEED_PACKET.get());
                        output.accept(ModItems.MAGNIFYING_GRASS_SEED_PACKET.get());
                        output.accept(ModItems.TILE_TURNIP_SEED_PACKET.get());
                        output.accept(ModItems.LILY_PAD_SEED_PACKET.get());
                        output.accept(ModItems.TANGLE_KELP_SEED_PACKET.get());
                        output.accept(ModItems.BOWLING_BULB_SEED_PACKET.get());
                        output.accept(ModItems.GUACODILE_SEED_PACKET.get());
                        output.accept(ModItems.BANANA_LAUNCHER_SEED_PACKET.get());
                        output.accept(ModItems.GARDEN_PLOTTER.get());
                        output.accept(ModItems.BIOME_DETECTOR.get());
                        output.accept(ModItems.SEED_HOLDER.get());
                        output.accept(ModItems.TARGETING_PRIORITY_CHANGER.get());
                        output.accept(ModItems.SPEEDY_MINECART.get());
                        output.accept(ModItems.FLYING_PLANE.get());
                        output.accept(ModItems.DINO_WHISTLE.get());
                        output.accept(ModItems.TOTEM_SHIELD.get());
                        output.accept(ModItems.FREEZE_RAY.get());
                        output.accept(ModItems.JETPACK.get());
                        output.accept(ModItems.CITRON_HELMET.get());
                        output.accept(ModItems.CITRON_CHESTPLATE.get());
                        output.accept(ModItems.CITRON_LEGGINGS.get());
                        output.accept(ModItems.CITRON_BOOTS.get());
                        output.accept(ModItems.TIDE_SHELL.get());
                        output.accept(ModItems.PIRATE_CANNON.get());
                        output.accept(ModItems.CAPTAINS_HELMET.get());
                        output.accept(ModItems.CAPTAINS_CHESTPLATE.get());
                        output.accept(ModItems.CAPTAINS_LEGGINGS.get());
                        output.accept(ModItems.CAPTAINS_BOOTS.get());
                        output.accept(ModItems.PIRATE_SHIP.get());
                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}

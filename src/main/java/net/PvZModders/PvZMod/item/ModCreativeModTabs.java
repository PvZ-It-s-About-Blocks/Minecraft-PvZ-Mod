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
                        output.accept(ModItems.GARDEN_PLOTTER.get());
                        output.accept(ModItems.BIOME_DETECTOR.get());
                        output.accept(ModItems.SEED_HOLDER.get());
                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}

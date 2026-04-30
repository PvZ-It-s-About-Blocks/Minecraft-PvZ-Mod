package net.PvZModders.PvZMod.menu;

import net.PvZModders.PvZMod.PvZ2Mod;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, PvZ2Mod.MOD_ID);

    public static final RegistryObject<MenuType<GardenTotemMenu>> GARDEN_TOTEM =
            MENU_TYPES.register("garden_totem", () -> new MenuType<>(GardenTotemMenu::new, FeatureFlags.VANILLA_SET));

    public static void register(IEventBus eventBus) {
        MENU_TYPES.register(eventBus);
    }
}

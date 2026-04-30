package net.PvZModders.PvZMod.block.entity;

import net.PvZModders.PvZMod.PvZ2Mod;
import net.PvZModders.PvZMod.block.ModBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, PvZ2Mod.MOD_ID);

    public static final RegistryObject<BlockEntityType<GardenPlotterBlockEntity>> GARDEN_PLOTTER_BE =
            BLOCK_ENTITIES.register("garden_plotter_be", () -> BlockEntityType.Builder
                    .of(GardenPlotterBlockEntity::new, ModBlocks.GARDEN_PLOTTER.get())
                    .build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}

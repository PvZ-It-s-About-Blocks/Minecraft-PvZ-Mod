package net.PvZModders.PvZMod.block;

import net.PvZModders.PvZMod.PvZ2Mod;
import net.PvZModders.PvZMod.block.custom.BiomePortalFrameBlock;
import net.PvZModders.PvZMod.block.custom.GardenTotemBlock;
import net.PvZModders.PvZMod.block.custom.GardenPlotterBlock;
import net.PvZModders.PvZMod.progression.portal.GardenEyeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.LinkedHashMap;
import java.util.Map;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, PvZ2Mod.MOD_ID);

    public static final RegistryObject<Block> GARDEN_PLOTTER = BLOCKS.register("garden_plotter",
            () -> new GardenPlotterBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT)
                    .strength(0.2F)
                    .noCollission()
                    .sound(SoundType.GRASS)));

    public static final RegistryObject<Block> GARDEN_TOTEM = BLOCKS.register("garden_totem",
            () -> new GardenTotemBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .strength(50.0F, 1200.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE)));

    public static final Map<GardenEyeType, RegistryObject<Block>> PORTAL_FRAMES = registerPortalFrames();

    private static Map<GardenEyeType, RegistryObject<Block>> registerPortalFrames() {
        Map<GardenEyeType, RegistryObject<Block>> frames = new LinkedHashMap<>();
        for (GardenEyeType type : GardenEyeType.REQUIRED) {
            frames.put(type, BLOCKS.register(type.frameId(),
                    () -> new BiomePortalFrameBlock(type, BlockBehaviour.Properties.copy(Blocks.END_PORTAL_FRAME))));
        }
        return Map.copyOf(frames);
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    } //tester
}

package net.PvZModders.PvZMod.progression.seed;

import net.PvZModders.PvZMod.PvZ2Mod;
import net.PvZModders.PvZMod.item.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public record PlantSeedDefinition(ResourceLocation seedPacketId, int sunCost, Block placeholderBlock) {
    private static final Map<ResourceLocation, PlantSeedDefinition> DEFINITIONS = new HashMap<>();

    static {
        register(new PlantSeedDefinition(new ResourceLocation(PvZ2Mod.MOD_ID, "sunflower_seed_packet"), 50, Blocks.SUNFLOWER));
        register(new PlantSeedDefinition(new ResourceLocation(PvZ2Mod.MOD_ID, "peashooter_seed_packet"), 100, Blocks.OAK_SAPLING));
    }

    public static Optional<PlantSeedDefinition> get(ResourceLocation seedPacketId) {
        return Optional.ofNullable(DEFINITIONS.get(seedPacketId));
    }

    public static int sunCost(ResourceLocation seedPacketId) {
        return get(seedPacketId).map(PlantSeedDefinition::sunCost).orElse(100);
    }

    public static ResourceLocation sunflowerSeedPacketId() {
        return BuiltInIds.SUNFLOWER;
    }

    public static ResourceLocation peashooterSeedPacketId() {
        return BuiltInIds.PEASHOOTER;
    }

    private static void register(PlantSeedDefinition definition) {
        DEFINITIONS.put(definition.seedPacketId(), definition);
    }

    private static final class BuiltInIds {
        private static final ResourceLocation SUNFLOWER = ModItems.SUNFLOWER_SEED_PACKET.getId();
        private static final ResourceLocation PEASHOOTER = ModItems.PEASHOOTER_SEED_PACKET.getId();
    }
}

package net.PvZModders.PvZMod.progression;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

import java.util.List;

public record GardenDefinition(
        GardenId id,
        String displayName,
        ResourceKey<Biome> biome,
        List<String> plantsUnlocked,
        List<String> rewards,
        int recommendedDifficulty
) {
}

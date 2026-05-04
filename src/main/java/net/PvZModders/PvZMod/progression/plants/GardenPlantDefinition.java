package net.PvZModders.PvZMod.progression.plants;

import net.PvZModders.PvZMod.progression.GardenId;
import net.PvZModders.PvZMod.progression.seed.PlantSeedDefinition;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Optional;

public record GardenPlantDefinition(
        GardenId gardenId,
        String plantId,
        String displayName,
        String description,
        int sunCost,
        int unlockWave,
        ResourceLocation seedPacketId
) {
    public int productionSeconds() {
        return Math.max(10, sunCost * 10);
    }

    public String unlockHint() {
        return unlockWave <= 0 ? "Starter plant." : "Clear wave " + unlockWave + " to unlock.";
    }

    public boolean isUnlockedAtWave(int currentWave) {
        return unlockWave <= 0 || currentWave > unlockWave;
    }

    public int gardenColor() {
        return PlantSeedDefinition.getByPlantId(plantId)
                .map(PlantSeedDefinition::gardenColor)
                .orElse(0x2F9F3F);
    }

    public static List<GardenPlantDefinition> originalGardenPlants() {
        return OriginalGardenPlants.PLANTS;
    }

    public static List<GardenPlantDefinition> ancientEgyptPlants() {
        return AncientEgyptPlants.PLANTS;
    }

    private static GardenPlantDefinition ancient(String plantId, String displayName, String description, int sunCost, int unlockWave) {
        Optional<PlantSeedDefinition> seedDefinition = PlantSeedDefinition.getByPlantId(plantId);
        ResourceLocation seedPacketId = seedDefinition
                .map(PlantSeedDefinition::seedPacketId)
                .orElse(new ResourceLocation("pvz2mod", plantId + "_seed_packet"));
        return new GardenPlantDefinition(GardenId.DESERT, plantId, displayName, description, sunCost, unlockWave, seedPacketId);
    }

    private static GardenPlantDefinition original(String plantId, String displayName, String description, int sunCost, int unlockWave) {
        Optional<PlantSeedDefinition> seedDefinition = PlantSeedDefinition.getByPlantId(plantId);
        ResourceLocation seedPacketId = seedDefinition
                .map(PlantSeedDefinition::seedPacketId)
                .orElse(new ResourceLocation("pvz2mod", plantId + "_seed_packet"));
        return new GardenPlantDefinition(GardenId.INITIAL_PLAINS, plantId, displayName, description, sunCost, unlockWave, seedPacketId);
    }

    private static final class OriginalGardenPlants {
        private static final List<GardenPlantDefinition> PLANTS = List.of(
                original("sunflower", "Sunflower", "Produces 25 sun every few seconds.", 50, 0),
                original("peashooter", "Peashooter", "Shoots snowballs at nearby zombies.", 100, 0),
                original("wall_nut", "Wall-nut", "Tough blocker that buys time for your defense.", 50, 5),
                original("potato_mine", "Potato Mine", "Explodes in a small area when zombies get close.", 25, 7),
                original("repeater", "Repeater", "Fires two shots at a time.", 200, 12),
                original("chomper", "Chomper", "Bites one close zombie, then cools down.", 150, 18)
        );
    }

    private static final class AncientEgyptPlants {
        private static final List<GardenPlantDefinition> PLANTS = List.of(
                ancient("bloomerang", "Bloomerang", "Throws a returning piercing boomerang.", 175, 2),
                ancient("iceberg_lettuce", "Iceberg Lettuce", "Freezes one nearby zombie, then disappears.", 0, 5),
                ancient("grave_buster", "Grave Buster", "Consumes grave obstacles over time.", 0, 9),
                ancient("bonk_choy", "Bonk Choy", "Rapidly punches zombies in front and behind.", 150, 13),
                ancient("torchwood", "Torchwood", "Doubles compatible pea projectile damage.", 175, 19),
                ancient("twin_sunflower", "Twin Sunflower", "Produces 50 sun every few seconds.", 125, 24)
        );
    }
}

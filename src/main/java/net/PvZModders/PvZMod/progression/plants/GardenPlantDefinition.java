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

    public static List<GardenPlantDefinition> wildWestPlants() {
        return WildWestPlants.PLANTS;
    }

    public static List<GardenPlantDefinition> lostCityPlants() {
        return LostCityPlants.PLANTS;
    }

    public static List<GardenPlantDefinition> darkAgesPlants() {
        return DarkAgesPlants.PLANTS;
    }

    public static List<GardenPlantDefinition> frostbitePlants() {
        return FrostbitePlants.PLANTS;
    }

    public static List<GardenPlantDefinition> jurassicMarshPlants() {
        return JurassicMarshPlants.PLANTS;
    }

    public static List<GardenPlantDefinition> neonMixtapePlants() {
        return NeonMixtapePlants.PLANTS;
    }

    public static List<GardenPlantDefinition> farFuturePlants() {
        return FarFuturePlants.PLANTS;
    }

    public static List<GardenPlantDefinition> forGarden(GardenId gardenId) {
        return switch (gardenId) {
            case INITIAL_PLAINS -> originalGardenPlants();
            case DESERT -> ancientEgyptPlants();
            case WILD_WEST -> wildWestPlants();
            case FROSTBITE -> frostbitePlants();
            case LOST_CITY -> lostCityPlants();
            case DARK_AGES -> darkAgesPlants();
            case JURASSIC_MARSH -> jurassicMarshPlants();
            case NEON_MIXTAPE -> neonMixtapePlants();
            case FAR_FUTURE -> farFuturePlants();
            default -> List.of();
        };
    }

    public static int maxKnownGardenPlantCount() {
        return Math.max(
                Math.max(Math.max(originalGardenPlants().size(), ancientEgyptPlants().size()), Math.max(wildWestPlants().size(), lostCityPlants().size())),
                Math.max(Math.max(Math.max(darkAgesPlants().size(), jurassicMarshPlants().size()), Math.max(neonMixtapePlants().size(), farFuturePlants().size())), frostbitePlants().size())
        );
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

    private static GardenPlantDefinition wildWest(String plantId, String displayName, String description, int sunCost, int unlockWave) {
        Optional<PlantSeedDefinition> seedDefinition = PlantSeedDefinition.getByPlantId(plantId);
        ResourceLocation seedPacketId = seedDefinition
                .map(PlantSeedDefinition::seedPacketId)
                .orElse(new ResourceLocation("pvz2mod", plantId + "_seed_packet"));
        return new GardenPlantDefinition(GardenId.WILD_WEST, plantId, displayName, description, sunCost, unlockWave, seedPacketId);
    }

    private static GardenPlantDefinition lostCity(String plantId, String displayName, String description, int sunCost, int unlockWave) {
        Optional<PlantSeedDefinition> seedDefinition = PlantSeedDefinition.getByPlantId(plantId);
        ResourceLocation seedPacketId = seedDefinition
                .map(PlantSeedDefinition::seedPacketId)
                .orElse(new ResourceLocation("pvz2mod", plantId + "_seed_packet"));
        return new GardenPlantDefinition(GardenId.LOST_CITY, plantId, displayName, description, sunCost, unlockWave, seedPacketId);
    }

    private static GardenPlantDefinition frostbite(String plantId, String displayName, String description, int sunCost, int unlockWave) {
        Optional<PlantSeedDefinition> seedDefinition = PlantSeedDefinition.getByPlantId(plantId);
        ResourceLocation seedPacketId = seedDefinition
                .map(PlantSeedDefinition::seedPacketId)
                .orElse(new ResourceLocation("pvz2mod", plantId + "_seed_packet"));
        return new GardenPlantDefinition(GardenId.FROSTBITE, plantId, displayName, description, sunCost, unlockWave, seedPacketId);
    }

    private static GardenPlantDefinition darkAges(String plantId, String displayName, String description, int sunCost, int unlockWave) {
        Optional<PlantSeedDefinition> seedDefinition = PlantSeedDefinition.getByPlantId(plantId);
        ResourceLocation seedPacketId = seedDefinition
                .map(PlantSeedDefinition::seedPacketId)
                .orElse(new ResourceLocation("pvz2mod", plantId + "_seed_packet"));
        return new GardenPlantDefinition(GardenId.DARK_AGES, plantId, displayName, description, sunCost, unlockWave, seedPacketId);
    }

    private static GardenPlantDefinition jurassic(String plantId, String displayName, String description, int sunCost, int unlockWave) {
        Optional<PlantSeedDefinition> seedDefinition = PlantSeedDefinition.getByPlantId(plantId);
        ResourceLocation seedPacketId = seedDefinition
                .map(PlantSeedDefinition::seedPacketId)
                .orElse(new ResourceLocation("pvz2mod", plantId + "_seed_packet"));
        return new GardenPlantDefinition(GardenId.JURASSIC_MARSH, plantId, displayName, description, sunCost, unlockWave, seedPacketId);
    }

    private static GardenPlantDefinition neon(String plantId, String displayName, String description, int sunCost, int unlockWave) {
        Optional<PlantSeedDefinition> seedDefinition = PlantSeedDefinition.getByPlantId(plantId);
        ResourceLocation seedPacketId = seedDefinition
                .map(PlantSeedDefinition::seedPacketId)
                .orElse(new ResourceLocation("pvz2mod", plantId + "_seed_packet"));
        return new GardenPlantDefinition(GardenId.NEON_MIXTAPE, plantId, displayName, description, sunCost, unlockWave, seedPacketId);
    }

    private static GardenPlantDefinition farFuture(String plantId, String displayName, String description, int sunCost, int unlockWave) {
        Optional<PlantSeedDefinition> seedDefinition = PlantSeedDefinition.getByPlantId(plantId);
        ResourceLocation seedPacketId = seedDefinition
                .map(PlantSeedDefinition::seedPacketId)
                .orElse(new ResourceLocation("pvz2mod", plantId + "_seed_packet"));
        return new GardenPlantDefinition(GardenId.FAR_FUTURE, plantId, displayName, description, sunCost, unlockWave, seedPacketId);
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

    private static final class WildWestPlants {
        private static final List<GardenPlantDefinition> PLANTS = List.of(
                wildWest("split_pea", "Split Pea", "Shoots forward and backward.", 125, 1),
                wildWest("chili_bean", "Chili Bean", "Defeats one zombie and stuns nearby zombies.", 50, 4),
                wildWest("pea_pod", "Pea Pod", "Stacks up to five shooters on one tile.", 125, 6),
                wildWest("lightning_reed", "Lightning Reed", "Chains electric damage through nearby zombies.", 125, 9),
                wildWest("melon_pult", "Melon-pult", "Lobs heavy splash-damage melons.", 325, 11),
                wildWest("tall_nut", "Tall-nut", "Very sturdy blocker for tough waves.", 125, 18),
                wildWest("winter_melon", "Winter Melon", "Lobs chilling splash-damage melons.", 500, 24)
        );
    }

    private static final class LostCityPlants {
        private static final List<GardenPlantDefinition> PLANTS = List.of(
                lostCity("red_stinger", "Red Stinger", "Changes offense or defense based on garden position.", 150, 1),
                lostCity("akee", "A.K.E.E.", "Bounces seed shots between nearby zombies.", 175, 6),
                lostCity("endurian", "Endurian", "Sturdy blocker that damages touching zombies.", 100, 10),
                lostCity("stallia", "Stallia", "Releases a slowing perfume cloud, then disappears.", 0, 19),
                lostCity("gold_leaf", "Gold Leaf", "Creates a Gold Tile on a valid garden tile.", 80, 26)
        );
    }

    private static final class FrostbitePlants {
        private static final List<GardenPlantDefinition> PLANTS = List.of(
                frostbite("hot_potato", "Hot Potato", "Thaws a frozen, iced, or frosted plant.", 0, 1),
                frostbite("pepper_pult", "Pepper-pult", "Lobs flaming peppers and warms nearby plants.", 200, 6),
                frostbite("chard_guard", "Chard Guard", "Blocks zombies and shoves them back three times.", 75, 11),
                frostbite("stunion", "Stunion", "Releases stunning gas when zombies get close.", 25, 19),
                frostbite("rotobaga", "Rotobaga", "Shoots diagonally in up to four directions.", 150, 26)
        );
    }

    private static final class DarkAgesPlants {
        private static final List<GardenPlantDefinition> PLANTS = List.of(
                darkAges("sun_shroom", "Sun-shroom", "Grows over time and produces stronger Sun.", 25, 1),
                darkAges("puff_shroom", "Puff-shroom", "Temporary free shooter with short range.", 0, 2),
                darkAges("fume_shroom", "Fume-shroom", "Damages all zombies in a forward fume cloud.", 125, 4),
                darkAges("sun_bean", "Sun Bean", "Infects one zombie so damage creates Sun.", 50, 6),
                darkAges("magnet_shroom", "Magnet-shroom", "Strips armor or metal equipment from nearby zombies.", 100, 15)
        );
    }

    private static final class JurassicMarshPlants {
        private static final List<GardenPlantDefinition> PLANTS = List.of(
                jurassic("primal_peashooter", "Primal Peashooter", "Shoots heavy stunning peas with occasional knockback.", 175, 1),
                jurassic("primal_wall_nut", "Primal Wall-nut", "Durable prehistoric blocker.", 75, 4),
                jurassic("perfume_shroom", "Perfume-shroom", "Charms a nearby dinosaur for your defense.", 150, 8),
                jurassic("primal_sunflower", "Primal Sunflower", "Produces 50 sun every few seconds.", 75, 17),
                jurassic("primal_potato_mine", "Primal Potato Mine", "Fast-arming mine with a larger blast.", 50, 23)
        );
    }

    private static final class NeonMixtapePlants {
        private static final List<GardenPlantDefinition> PLANTS = List.of(
                neon("phat_beet", "Phat Beet", "Thumps nearby zombies with area damage.", 150, 1),
                neon("celery_stalker", "Celery Stalker", "Hides until zombies pass, then ambushes them.", 50, 5),
                neon("thyme_warp", "Thyme Warp", "Warps nearby zombies backward while healing them slightly.", 100, 9),
                neon("garlic", "Garlic", "Diverts zombies sideways and disrupts their path.", 50, 17),
                neon("spore_shroom", "Spore-shroom", "Shoots spores that can sprout new Spore-shrooms.", 150, 21),
                neon("intensive_carrot", "Intensive Carrot", "Revives a recently destroyed plant.", 100, 26)
        );
    }

    private static final class FarFuturePlants {
        private static final List<GardenPlantDefinition> PLANTS = List.of(
                farFuture("laser_bean", "Laser Bean", "Pierces a whole line with a laser beam.", 200, 1),
                farFuture("blover", "Blover", "Blows away airborne hostile mobs, then disappears.", 50, 3),
                farFuture("citron", "Citron", "Charges a powerful plasma shot.", 350, 6),
                farFuture("em_peach", "E.M.Peach", "Releases an EMP burst that stuns hostile mobs.", 25, 9),
                farFuture("infi_nut", "Infi-nut", "Regenerating defensive blocker.", 75, 13),
                farFuture("magnifying_grass", "Magnifying Grass", "Spends Sun to fire a high-damage beam.", 50, 17),
                farFuture("tile_turnip", "Tile Turnip", "Creates a Power Tile on a valid garden tile.", 0, 24)
        );
    }
}

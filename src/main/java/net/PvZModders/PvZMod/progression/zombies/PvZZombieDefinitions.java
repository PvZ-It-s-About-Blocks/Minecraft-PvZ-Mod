package net.PvZModders.PvZMod.progression.zombies;

import net.PvZModders.PvZMod.progression.GardenId;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Collections;

public final class PvZZombieDefinitions {
    public static final String TYPE_TAG = "PvZZombieType";
    public static final String MODEL_KEY_TAG = "PvZZombieModelKey";
    public static final String GARGANTUAR_LIKE_TAG = "PvZGargantuarLike";
    public static final String FLYING_ZOMBIE_TAG = "PvZFlyingZombie";
    public static final String ATTACK_DAMAGE_TAG = "PvZZombieAttackDamage";

    private static final Map<String, PvZZombieDefinition> DEFINITIONS = createDefinitions();
    private static final PvZZombieDefinition BASIC = DEFINITIONS.get("basic_zombie");

    private PvZZombieDefinitions() {
    }

    public static List<PvZZombieDefinition> all() {
        return List.copyOf(DEFINITIONS.values());
    }

    public static Optional<PvZZombieDefinition> byId(String id) {
        return Optional.ofNullable(DEFINITIONS.get(id));
    }

    public static PvZZombieDefinition byEntityType(ResourceLocation entityTypeId) {
        if (entityTypeId == null) {
            return BASIC;
        }
        return byId(entityTypeId.getPath()).orElse(BASIC);
    }

    public static boolean isCustomZombieId(String id) {
        return DEFINITIONS.containsKey(id);
    }

    public static boolean isGargantuarLike(LivingEntity entity) {
        ResourceLocation entityTypeId = net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
        String path = entityTypeId == null ? "" : entityTypeId.getPath();
        return entity.getPersistentData().getBoolean(GARGANTUAR_LIKE_TAG)
                || "gargantuar".equals(path)
                || (entity instanceof net.PvZModders.PvZMod.entity.custom.PvZZombieEntity zombie
                && zombie.definition().has(PvZZombieSpecial.GARGANTUAR));
    }

    public static boolean isFlyingZombie(LivingEntity entity) {
        return entity.getPersistentData().getBoolean(FLYING_ZOMBIE_TAG)
                || (entity instanceof net.PvZModders.PvZMod.entity.custom.PvZZombieEntity zombie
                && zombie.definition().has(PvZZombieSpecial.FLYING));
    }

    public static boolean isLostCityZombie(LivingEntity entity) {
        return entity instanceof net.PvZModders.PvZMod.entity.custom.PvZZombieEntity zombie
                && zombie.definition().gardenSource() == GardenId.LOST_CITY;
    }

    private static Map<String, PvZZombieDefinition> createDefinitions() {
        Map<String, PvZZombieDefinition> definitions = new LinkedHashMap<>();
        register(definitions, "basic_zombie", "Basic Zombie", 20.0D, 1.0D, 4.0D, 0.0D, 1.0F,
                "Baseline garden zombie.", Set.of());
        register(definitions, "conehead_zombie", "Conehead Zombie", 45.0D, 1.0D, 4.0D, 0.05D, 1.0F,
                "Tougher early armor zombie.", Set.of());
        register(definitions, "buckethead_zombie", "Buckethead Zombie", 90.0D, 0.95D, 4.0D, 0.15D, 1.0F,
                "Heavy early armor zombie.", Set.of(PvZZombieSpecial.METAL));
        register(definitions, "flag_zombie", "Flag Zombie", 20.0D, 1.15D, 4.0D, 0.0D, 1.0F,
                "Marks a larger push in the wave.", Set.of(PvZZombieSpecial.FLAG));
        register(definitions, "newspaper_zombie", "Newspaper Zombie", 35.0D, 0.9D, 4.0D, 0.0D, 1.0F,
                "Rages and runs faster after its newspaper breaks.", Set.of(PvZZombieSpecial.NEWSPAPER_RAGE));
        register(definitions, "screen_door_zombie", "Screen Door Zombie", 70.0D, 0.9D, 4.0D, 0.12D, 1.0F,
                "A front-shielded zombie with reduced frontal damage.", Set.of(PvZZombieSpecial.SCREEN_DOOR_SHIELD, PvZZombieSpecial.METAL));
        register(definitions, "pole_vaulting_zombie", "Pole Vaulting Zombie", 35.0D, 1.25D, 4.0D, 0.0D, 1.0F,
                "Vaults over the first blocker once, then slows down.", Set.of(PvZZombieSpecial.POLE_VAULT));
        register(definitions, "football_zombie", "Football Zombie", 110.0D, 1.32D, 5.0D, 0.35D, 1.0F,
                "Fast tank zombie.", Set.of(PvZZombieSpecial.METAL));
        register(definitions, "imp", "Imp", 10.0D, 1.45D, 3.0D, 0.0D, 0.55F,
                "Small, fast, fragile zombie.", Set.of(PvZZombieSpecial.IMP));
        register(definitions, "gargantuar", "Gargantuar", 300.0D, 0.65D, 16.0D, 0.9D, 3.0F,
                "Huge heavy threat with enormous health and knockback resistance.", Set.of(PvZZombieSpecial.GARGANTUAR));
        register(definitions, GardenId.DESERT, "mummy_zombie", "Mummy Zombie", 20.0D, 1.0D, 4.0D, 0.0D, 1.0F,
                "Baseline Ancient Egypt mummy.", Set.of());
        register(definitions, GardenId.DESERT, "conehead_mummy", "Conehead Mummy", 45.0D, 1.0D, 4.0D, 0.05D, 1.0F,
                "Early armored mummy.", Set.of());
        register(definitions, GardenId.DESERT, "buckethead_mummy", "Buckethead Mummy", 90.0D, 0.95D, 4.0D, 0.15D, 1.0F,
                "Heavy armored mummy.", Set.of(PvZZombieSpecial.METAL));
        register(definitions, GardenId.DESERT, "flag_mummy_zombie", "Flag Mummy Zombie", 20.0D, 1.15D, 4.0D, 0.0D, 1.0F,
                "Marks a larger Ancient Egypt push.", Set.of(PvZZombieSpecial.FLAG));
        register(definitions, GardenId.DESERT, "ra_zombie", "Ra Zombie", 35.0D, 0.9D, 4.0D, 0.0D, 1.0F,
                "Drains Sun from nearby defenders while alive.", Set.of(PvZZombieSpecial.RA_DRAIN));
        register(definitions, GardenId.DESERT, "camel_zombie", "Camel Zombie", 70.0D, 0.95D, 4.0D, 0.12D, 1.0F,
                "Shield-line mummy with frontal damage reduction.", Set.of(PvZZombieSpecial.SCREEN_DOOR_SHIELD));
        register(definitions, GardenId.DESERT, "explorer_zombie", "Explorer Zombie", 35.0D, 1.0D, 6.0D, 0.0D, 1.0F,
                "Torch-bearing zombie that pressures plants harder.", Set.of(PvZZombieSpecial.EXPLORER_TORCH));
        register(definitions, GardenId.DESERT, "tomb_raiser_zombie", "Tomb Raiser Zombie", 45.0D, 0.9D, 4.0D, 0.0D, 1.0F,
                "Raises temporary tombstone obstacles inside the garden.", Set.of(PvZZombieSpecial.TOMB_RAISER));
        register(definitions, GardenId.DESERT, "pharaoh_zombie", "Pharaoh Zombie", 120.0D, 0.7D, 6.0D, 0.45D, 1.0F,
                "Slow, coffin-armored heavy mummy.", Set.of(PvZZombieSpecial.METAL));
        register(definitions, GardenId.DESERT, "mummified_gargantuar", "Mummified Gargantuar", 330.0D, 0.62D, 18.0D, 0.95D, 3.0F,
                "Huge Ancient Egypt gargantuar-like threat.", Set.of(PvZZombieSpecial.GARGANTUAR));
        register(definitions, GardenId.WILD_WEST, "cowboy_zombie", "Cowboy Zombie", 20.0D, 1.0D, 4.0D, 0.0D, 1.0F,
                "Baseline Wild West zombie.", Set.of());
        register(definitions, GardenId.WILD_WEST, "conehead_cowboy", "Conehead Cowboy", 45.0D, 1.0D, 4.0D, 0.05D, 1.0F,
                "Early armored Wild West zombie.", Set.of());
        register(definitions, GardenId.WILD_WEST, "buckethead_cowboy", "Buckethead Cowboy", 90.0D, 0.95D, 4.0D, 0.15D, 1.0F,
                "Heavy armored Wild West zombie.", Set.of(PvZZombieSpecial.METAL));
        register(definitions, GardenId.WILD_WEST, "flag_cowboy_zombie", "Flag Cowboy Zombie", 20.0D, 1.15D, 4.0D, 0.0D, 1.0F,
                "Marks a larger Wild West push.", Set.of(PvZZombieSpecial.FLAG));
        register(definitions, GardenId.WILD_WEST, "prospector_zombie", "Prospector Zombie", 35.0D, 1.0D, 4.0D, 0.0D, 1.0F,
                "Leaps past one defensive blocker after entering.", Set.of(PvZZombieSpecial.PROSPECTOR_LEAP));
        register(definitions, GardenId.WILD_WEST, "pianist_zombie", "Pianist Zombie", 60.0D, 0.75D, 4.0D, 0.1D, 1.0F,
                "Supports nearby zombies with music.", Set.of(PvZZombieSpecial.PIANIST_SUPPORT));
        register(definitions, GardenId.WILD_WEST, "poncho_zombie", "Poncho Zombie", 70.0D, 1.0D, 4.0D, 0.12D, 1.0F,
                "Hidden shield zombie that resists projectiles until weakened.", Set.of(PvZZombieSpecial.PONCHO_SHIELD, PvZZombieSpecial.METAL));
        register(definitions, GardenId.WILD_WEST, "chicken_wrangler_zombie", "Chicken Wrangler Zombie", 45.0D, 1.0D, 4.0D, 0.0D, 1.0F,
                "Releases fast Zombie Chickens when damaged.", Set.of(PvZZombieSpecial.CHICKEN_WRANGLER));
        register(definitions, GardenId.WILD_WEST, "zombie_chicken", "Zombie Chicken", 4.0D, 2.0D, 2.0D, 0.0D, 0.4F,
                "Tiny fast swarm zombie.", Set.of(PvZZombieSpecial.IMP));
        register(definitions, GardenId.WILD_WEST, "bull_rider_zombie", "Bull Rider Zombie", 85.0D, 1.25D, 8.0D, 0.3D, 1.25F,
                "Mounted charge pressure enemy.", Set.of(PvZZombieSpecial.BULL_CHARGE));
        register(definitions, GardenId.WILD_WEST, "zombie_bull", "Zombie Bull", 70.0D, 1.2D, 9.0D, 0.35D, 1.35F,
                "Charging bull enemy.", Set.of(PvZZombieSpecial.BULL_CHARGE));
        register(definitions, GardenId.WILD_WEST, "wild_west_gargantuar", "Wild West Gargantuar", 330.0D, 0.62D, 18.0D, 0.95D, 3.0F,
                "Huge Wild West gargantuar-like threat.", Set.of(PvZZombieSpecial.GARGANTUAR));
        register(definitions, GardenId.FROSTBITE, "cave_zombie", "Cave Zombie", 20.0D, 1.0D, 4.0D, 0.0D, 1.0F,
                "Baseline Frostbite Caves zombie.", Set.of());
        register(definitions, GardenId.FROSTBITE, "conehead_cave_zombie", "Conehead Cave Zombie", 45.0D, 1.0D, 4.0D, 0.05D, 1.0F,
                "Early armored Frostbite Caves zombie.", Set.of());
        register(definitions, GardenId.FROSTBITE, "buckethead_cave_zombie", "Buckethead Cave Zombie", 90.0D, 0.95D, 4.0D, 0.15D, 1.0F,
                "Heavy armored Frostbite Caves zombie.", Set.of(PvZZombieSpecial.METAL));
        register(definitions, GardenId.FROSTBITE, "flag_cave_zombie", "Flag Cave Zombie", 20.0D, 1.15D, 4.0D, 0.0D, 1.0F,
                "Marks a larger Frostbite Caves push.", Set.of(PvZZombieSpecial.FLAG));
        register(definitions, GardenId.FROSTBITE, "hunter_zombie", "Hunter Zombie", 45.0D, 1.0D, 4.0D, 0.0D, 1.0F,
                "Throws freezing shots at plants.", Set.of(PvZZombieSpecial.HUNTER_FREEZE));
        register(definitions, GardenId.FROSTBITE, "troglobite", "Troglobite", 50.0D, 0.9D, 4.0D, 0.1D, 1.0F,
                "Pushes or creates Ice Block Zombie pressure.", Set.of(PvZZombieSpecial.TROGLOBITE_PUSH));
        register(definitions, GardenId.FROSTBITE, "ice_block_zombie", "Ice Block Zombie", 70.0D, 0.45D, 3.0D, 0.75D, 1.05F,
                "Slow icy obstacle zombie.", Set.of(PvZZombieSpecial.ICE_BLOCK, PvZZombieSpecial.METAL));
        register(definitions, GardenId.FROSTBITE, "weasel_hoarder", "Weasel Hoarder", 50.0D, 1.0D, 4.0D, 0.0D, 1.0F,
                "Releases fast Zombie Weasels when damaged.", Set.of(PvZZombieSpecial.WEASEL_HOARDER));
        register(definitions, GardenId.FROSTBITE, "zombie_weasel", "Zombie Weasel", 4.0D, 2.05D, 2.0D, 0.0D, 0.4F,
                "Tiny fast Frostbite swarm zombie.", Set.of(PvZZombieSpecial.IMP));
        register(definitions, GardenId.FROSTBITE, "dodo_rider_zombie", "Dodo Rider Zombie", 60.0D, 1.05D, 4.0D, 0.05D, 1.1F,
                "Hopping pressure enemy.", Set.of(PvZZombieSpecial.DODO_HOP));
        register(definitions, GardenId.FROSTBITE, "dodo", "Dodo", 35.0D, 1.15D, 3.0D, 0.0D, 0.9F,
                "Standalone hopping Frostbite creature enemy.", Set.of(PvZZombieSpecial.DODO_HOP));
        register(definitions, GardenId.FROSTBITE, "sloth_gargantuar", "Sloth Gargantuar", 340.0D, 0.52D, 18.0D, 0.95D, 3.0F,
                "Huge slow Frostbite gargantuar-like threat.", Set.of(PvZZombieSpecial.GARGANTUAR));
        register(definitions, GardenId.LOST_CITY, "lost_city_zombie", "Lost City Zombie", 20.0D, 1.0D, 4.0D, 0.0D, 1.0F,
                "Baseline Lost City zombie.", Set.of());
        register(definitions, GardenId.LOST_CITY, "conehead_lost_city_zombie", "Conehead Lost City Zombie", 45.0D, 1.0D, 4.0D, 0.05D, 1.0F,
                "Early armored Lost City zombie.", Set.of());
        register(definitions, GardenId.LOST_CITY, "buckethead_lost_city_zombie", "Buckethead Lost City Zombie", 90.0D, 0.95D, 4.0D, 0.15D, 1.0F,
                "Heavy armored Lost City zombie.", Set.of(PvZZombieSpecial.METAL));
        register(definitions, GardenId.LOST_CITY, "flag_lost_city_zombie", "Flag Lost City Zombie", 20.0D, 1.15D, 4.0D, 0.0D, 1.0F,
                "Marks a larger Lost City push.", Set.of(PvZZombieSpecial.FLAG));
        register(definitions, GardenId.LOST_CITY, "excavator_zombie", "Excavator Zombie", 60.0D, 0.9D, 4.0D, 0.12D, 1.0F,
                "Deflects straight frontal projectile pressure.", Set.of(PvZZombieSpecial.EXCAVATOR_SHIELD, PvZZombieSpecial.METAL));
        register(definitions, GardenId.LOST_CITY, "parasol_zombie", "Parasol Zombie", 45.0D, 1.0D, 4.0D, 0.0D, 1.0F,
                "Resists lobbed and falling projectiles.", Set.of(PvZZombieSpecial.PARASOL_SHIELD));
        register(definitions, GardenId.LOST_CITY, "relic_hunter_zombie", "Relic Hunter Zombie", 50.0D, 1.25D, 4.0D, 0.0D, 1.0F,
                "Aggressively leaps past one blocker.", Set.of(PvZZombieSpecial.RELIC_HUNTER_LEAP));
        register(definitions, GardenId.LOST_CITY, "turquoise_skull_zombie", "Turquoise Skull Zombie", 55.0D, 0.85D, 3.0D, 0.0D, 1.0F,
                "Drains Sun with a visible skull beam.", Set.of(PvZZombieSpecial.TURQUOISE_SKULL_DRAIN));
        register(definitions, GardenId.LOST_CITY, "lost_pilot_zombie", "Lost Pilot Zombie", 30.0D, 1.25D, 4.0D, 0.0D, 1.0F,
                "Flying-tagged aerial pressure zombie.", Set.of(PvZZombieSpecial.FLYING));
        register(definitions, GardenId.LOST_CITY, "bug_zombie", "Bug Zombie", 40.0D, 1.2D, 4.0D, 0.0D, 1.0F,
                "Tougher flying-tagged bug-carried zombie.", Set.of(PvZZombieSpecial.FLYING));
        register(definitions, GardenId.LOST_CITY, "imp_porter", "Imp Porter", 10.0D, 1.45D, 3.0D, 0.0D, 0.55F,
                "Small, fast Lost City support enemy.", Set.of(PvZZombieSpecial.IMP));
        register(definitions, GardenId.LOST_CITY, "porter_gargantuar", "Porter Gargantuar", 340.0D, 0.6D, 18.0D, 0.95D, 3.0F,
                "Huge Lost City gargantuar-like threat.", Set.of(PvZZombieSpecial.GARGANTUAR, PvZZombieSpecial.PORTER_GARGANTUAR));
        return Collections.unmodifiableMap(new LinkedHashMap<>(definitions));
    }

    private static void register(Map<String, PvZZombieDefinition> definitions, String id, String displayName, double maxHealth,
                                 double movementSpeedMultiplier, double attackDamage, double knockbackResistance,
                                 float visualScale, String almanacText, Set<PvZZombieSpecial> specials) {
        register(definitions, GardenId.INITIAL_PLAINS, id, displayName, maxHealth, movementSpeedMultiplier, attackDamage, knockbackResistance, visualScale, almanacText, specials);
    }

    private static void register(Map<String, PvZZombieDefinition> definitions, GardenId gardenId, String id, String displayName, double maxHealth,
                                 double movementSpeedMultiplier, double attackDamage, double knockbackResistance,
                                 float visualScale, String almanacText, Set<PvZZombieSpecial> specials) {
        definitions.put(id, new PvZZombieDefinition(
                id,
                displayName,
                gardenId,
                maxHealth,
                movementSpeedMultiplier,
                attackDamage,
                knockbackResistance,
                visualScale,
                id,
                almanacText,
                specials
        ));
    }
}

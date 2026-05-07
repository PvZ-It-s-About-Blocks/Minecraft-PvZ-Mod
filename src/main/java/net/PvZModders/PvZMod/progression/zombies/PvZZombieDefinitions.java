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

    public static boolean isDarkAgesZombie(LivingEntity entity) {
        return entity instanceof net.PvZModders.PvZMod.entity.custom.PvZZombieEntity zombie
                && zombie.definition().gardenSource() == GardenId.DARK_AGES;
    }

    public static boolean isNeonZombie(LivingEntity entity) {
        return entity instanceof net.PvZModders.PvZMod.entity.custom.PvZZombieEntity zombie
                && zombie.definition().gardenSource() == GardenId.NEON_MIXTAPE;
    }

    public static boolean isJurassicZombie(LivingEntity entity) {
        return entity instanceof net.PvZModders.PvZMod.entity.custom.PvZZombieEntity zombie
                && zombie.definition().gardenSource() == GardenId.JURASSIC_MARSH;
    }

    public static boolean isBigWaveBeachZombie(LivingEntity entity) {
        return entity instanceof net.PvZModders.PvZMod.entity.custom.PvZZombieEntity zombie
                && zombie.definition().gardenSource() == GardenId.BIG_WAVE_BEACH;
    }

    public static boolean isPirateSeasZombie(LivingEntity entity) {
        return entity instanceof net.PvZModders.PvZMod.entity.custom.PvZZombieEntity zombie
                && zombie.definition().gardenSource() == GardenId.PIRATE_SEAS;
    }

    public static boolean isFarFutureZombie(LivingEntity entity) {
        return entity instanceof net.PvZModders.PvZMod.entity.custom.PvZZombieEntity zombie
                && zombie.definition().gardenSource() == GardenId.FAR_FUTURE;
    }

    public static boolean isModernDayZombie(LivingEntity entity) {
        return entity instanceof net.PvZModders.PvZMod.entity.custom.PvZZombieEntity zombie
                && zombie.definition().gardenSource() == GardenId.MODERN_DAY;
    }

    public static boolean isMachineZombie(LivingEntity entity) {
        return entity instanceof net.PvZModders.PvZMod.entity.custom.PvZZombieEntity zombie
                && Set.of("robo_cone_zombie", "mecha_football_zombie", "disco_tron_3000", "gargantuar_prime").contains(zombie.definition().id());
    }

    public static boolean isSummonerZombie(LivingEntity entity) {
        return entity instanceof net.PvZModders.PvZMod.entity.custom.PvZZombieEntity zombie
                && (zombie.definition().has(PvZZombieSpecial.ARCADE_SUMMONER)
                || zombie.definition().has(PvZZombieSpecial.IMP_CANNON)
                || zombie.definition().has(PvZZombieSpecial.CHICKEN_WRANGLER)
                || zombie.definition().has(PvZZombieSpecial.WEASEL_HOARDER));
    }

    public static boolean isControlZombie(LivingEntity entity) {
        return entity instanceof net.PvZModders.PvZMod.entity.custom.PvZZombieEntity zombie
                && (zombie.definition().has(PvZZombieSpecial.WIZARD_DISABLE)
                || zombie.definition().has(PvZZombieSpecial.OCTO_DISABLE)
                || zombie.definition().has(PvZZombieSpecial.FISHERMAN_HOOK)
                || zombie.definition().has(PvZZombieSpecial.HUNTER_FREEZE));
    }

    public static boolean isSupportZombie(LivingEntity entity) {
        return entity instanceof net.PvZModders.PvZMod.entity.custom.PvZZombieEntity zombie
                && (zombie.definition().has(PvZZombieSpecial.KING_SUPPORT)
                || zombie.definition().has(PvZZombieSpecial.GLITTER_AURA)
                || zombie.definition().has(PvZZombieSpecial.MC_MUSIC_SUPPORT)
                || zombie.definition().has(PvZZombieSpecial.BOOMBOX_PULSE)
                || zombie.definition().has(PvZZombieSpecial.PIRATE_CAPTAIN)
                || zombie.definition().has(PvZZombieSpecial.RALLY_SUPPORT));
    }

    public static boolean isDisplacementZombie(LivingEntity entity) {
        return entity instanceof net.PvZModders.PvZMod.entity.custom.PvZZombieEntity zombie
                && (zombie.definition().has(PvZZombieSpecial.POLE_VAULT)
                || zombie.definition().has(PvZZombieSpecial.PROSPECTOR_LEAP)
                || zombie.definition().has(PvZZombieSpecial.RELIC_HUNTER_LEAP)
                || zombie.definition().has(PvZZombieSpecial.PUNK_SHOVE)
                || zombie.definition().has(PvZZombieSpecial.BREAKDANCER_KICK)
                || zombie.definition().has(PvZZombieSpecial.SWASHBUCKLER)
                || zombie.definition().has(PvZZombieSpecial.JURASSIC_BULLY)
                || zombie.definition().has(PvZZombieSpecial.ALL_STAR_TACKLE));
    }

    public static boolean isAquaticZombie(LivingEntity entity) {
        return entity instanceof net.PvZModders.PvZMod.entity.custom.PvZZombieEntity zombie
                && zombie.definition().has(PvZZombieSpecial.AQUATIC);
    }

    public static boolean isSmallZombie(LivingEntity entity) {
        return entity instanceof net.PvZModders.PvZMod.entity.custom.PvZZombieEntity zombie
                && zombie.definition().has(PvZZombieSpecial.IMP);
    }

    public static boolean isStationaryZombie(LivingEntity entity) {
        return entity instanceof net.PvZModders.PvZMod.entity.custom.PvZZombieEntity zombie
                && zombie.definition().has(PvZZombieSpecial.STATIONARY);
    }

    public static boolean isVehicleZombie(LivingEntity entity) {
        return entity instanceof net.PvZModders.PvZMod.entity.custom.PvZZombieEntity zombie
                && zombie.definition().has(PvZZombieSpecial.VEHICLE_MINECART);
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
        register(definitions, GardenId.DARK_AGES, "peasant_zombie", "Peasant Zombie", 20.0D, 1.0D, 4.0D, 0.0D, 1.0F,
                "Baseline Dark Ages peasant zombie.", Set.of());
        register(definitions, GardenId.DARK_AGES, "conehead_peasant", "Conehead Peasant", 45.0D, 1.0D, 4.0D, 0.05D, 1.0F,
                "Early armored Dark Ages peasant.", Set.of());
        register(definitions, GardenId.DARK_AGES, "buckethead_peasant", "Buckethead Peasant", 90.0D, 0.95D, 4.0D, 0.15D, 1.0F,
                "Heavy armored Dark Ages peasant.", Set.of(PvZZombieSpecial.METAL));
        register(definitions, GardenId.DARK_AGES, "flag_peasant_zombie", "Flag Peasant Zombie", 20.0D, 1.15D, 4.0D, 0.0D, 1.0F,
                "Marks a larger Dark Ages push.", Set.of(PvZZombieSpecial.FLAG));
        register(definitions, GardenId.DARK_AGES, "knight_zombie", "Knight Zombie", 120.0D, 0.65D, 6.0D, 0.5D, 1.0F,
                "Slow armored Dark Ages tank.", Set.of(PvZZombieSpecial.METAL));
        register(definitions, GardenId.DARK_AGES, "jester_zombie", "Jester Zombie", 45.0D, 1.15D, 4.0D, 0.0D, 1.0F,
                "Periodically spins to deflect straight projectiles.", Set.of(PvZZombieSpecial.JESTER_SPIN));
        register(definitions, GardenId.DARK_AGES, "wizard_zombie", "Wizard Zombie", 55.0D, 0.9D, 3.0D, 0.0D, 1.0F,
                "Temporarily disables plants with sheep magic.", Set.of(PvZZombieSpecial.WIZARD_DISABLE));
        register(definitions, GardenId.DARK_AGES, "king_zombie", "King Zombie", 70.0D, 0.75D, 4.0D, 0.1D, 1.0F,
                "Buffs nearby Dark Ages zombies with Royal Guard.", Set.of(PvZZombieSpecial.KING_SUPPORT));
        register(definitions, GardenId.DARK_AGES, "dragon_imp", "Dragon Imp", 12.0D, 1.35D, 3.0D, 0.0D, 0.55F,
                "Small, fast fire-themed imp.", Set.of(PvZZombieSpecial.IMP, PvZZombieSpecial.DRAGON_IMP_FIRE));
        register(definitions, GardenId.DARK_AGES, "dark_ages_gargantuar", "Dark Ages Gargantuar", 340.0D, 0.58D, 18.0D, 0.95D, 3.0F,
                "Huge Dark Ages gargantuar-like threat.", Set.of(PvZZombieSpecial.GARGANTUAR, PvZZombieSpecial.DARK_AGES_GARGANTUAR));
        register(definitions, GardenId.NEON_MIXTAPE, "neon_zombie", "Neon Zombie", 20.0D, 1.0D, 4.0D, 0.0D, 1.0F,
                "Baseline Neon Mixtape zombie.", Set.of());
        register(definitions, GardenId.NEON_MIXTAPE, "conehead_neon_zombie", "Conehead Neon Zombie", 45.0D, 1.0D, 4.0D, 0.05D, 1.0F,
                "Early armored Neon Mixtape zombie.", Set.of());
        register(definitions, GardenId.NEON_MIXTAPE, "buckethead_neon_zombie", "Buckethead Neon Zombie", 90.0D, 0.95D, 4.0D, 0.15D, 1.0F,
                "Heavy armored Neon Mixtape zombie.", Set.of(PvZZombieSpecial.METAL));
        register(definitions, GardenId.NEON_MIXTAPE, "flag_neon_zombie", "Flag Neon Zombie", 20.0D, 1.15D, 4.0D, 0.0D, 1.0F,
                "Marks a larger Neon Mixtape push.", Set.of(PvZZombieSpecial.FLAG));
        register(definitions, GardenId.NEON_MIXTAPE, "punk_zombie", "Punk Zombie", 45.0D, 1.15D, 4.0D, 0.0D, 1.0F,
                "Creates short-range shove pressure.", Set.of(PvZZombieSpecial.PUNK_SHOVE));
        register(definitions, GardenId.NEON_MIXTAPE, "glitter_zombie", "Glitter Zombie", 50.0D, 1.0D, 4.0D, 0.0D, 1.0F,
                "Gives nearby zombies a defensive aura.", Set.of(PvZZombieSpecial.GLITTER_AURA));
        register(definitions, GardenId.NEON_MIXTAPE, "mc_zom_b", "MC Zom-B", 55.0D, 0.9D, 3.0D, 0.0D, 1.0F,
                "Boosts nearby zombies with music pulses.", Set.of(PvZZombieSpecial.MC_MUSIC_SUPPORT));
        register(definitions, GardenId.NEON_MIXTAPE, "breakdancer_zombie", "Breakdancer Zombie", 60.0D, 1.0D, 4.0D, 0.05D, 1.0F,
                "Kicks nearby zombies forward.", Set.of(PvZZombieSpecial.BREAKDANCER_KICK));
        register(definitions, GardenId.NEON_MIXTAPE, "arcade_zombie", "Arcade Zombie", 75.0D, 0.75D, 4.0D, 0.1D, 1.0F,
                "Summons small 8-Bit Zombies.", Set.of(PvZZombieSpecial.ARCADE_SUMMONER));
        register(definitions, GardenId.NEON_MIXTAPE, "eight_bit_zombie", "8-Bit Zombie", 8.0D, 1.55D, 2.0D, 0.0D, 0.55F,
                "Small, fast summoned Neon swarm enemy.", Set.of(PvZZombieSpecial.IMP));
        register(definitions, GardenId.NEON_MIXTAPE, "boombox_zombie", "Boombox Zombie", 65.0D, 0.9D, 4.0D, 0.1D, 1.0F,
                "Creates local speaker pulses.", Set.of(PvZZombieSpecial.BOOMBOX_PULSE));
        register(definitions, GardenId.NEON_MIXTAPE, "neon_gargantuar", "Neon Gargantuar", 340.0D, 0.6D, 18.0D, 0.95D, 3.0F,
                "Huge Neon Mixtape gargantuar-like threat.", Set.of(PvZZombieSpecial.GARGANTUAR, PvZZombieSpecial.NEON_GARGANTUAR));
        register(definitions, GardenId.JURASSIC_MARSH, "jurassic_zombie", "Jurassic Zombie", 20.0D, 1.0D, 4.0D, 0.0D, 1.0F,
                "Baseline Jurassic Marsh zombie.", Set.of());
        register(definitions, GardenId.JURASSIC_MARSH, "conehead_jurassic_zombie", "Conehead Jurassic Zombie", 45.0D, 1.0D, 4.0D, 0.05D, 1.0F,
                "Early armored Jurassic Marsh zombie.", Set.of());
        register(definitions, GardenId.JURASSIC_MARSH, "buckethead_jurassic_zombie", "Buckethead Jurassic Zombie", 90.0D, 0.95D, 4.0D, 0.15D, 1.0F,
                "Heavy armored Jurassic Marsh zombie.", Set.of(PvZZombieSpecial.METAL));
        register(definitions, GardenId.JURASSIC_MARSH, "flag_jurassic_zombie", "Flag Jurassic Zombie", 20.0D, 1.15D, 4.0D, 0.0D, 1.0F,
                "Marks a larger Jurassic Marsh push.", Set.of(PvZZombieSpecial.FLAG));
        register(definitions, GardenId.JURASSIC_MARSH, "fossilhead_zombie", "Fossilhead Zombie", 110.0D, 0.85D, 5.0D, 0.38D, 1.0F,
                "Durable fossil-armored tank with strong knockback resistance.", Set.of(PvZZombieSpecial.METAL));
        register(definitions, GardenId.JURASSIC_MARSH, "amberhead_zombie", "Amberhead Zombie", 140.0D, 0.75D, 6.0D, 0.5D, 1.0F,
                "Very slow amber-armored heavy zombie.", Set.of(PvZZombieSpecial.METAL));
        register(definitions, GardenId.JURASSIC_MARSH, "jurassic_imp", "Jurassic Imp", 10.0D, 1.45D, 3.0D, 0.0D, 0.55F,
                "Small, fast Jurassic Marsh imp.", Set.of(PvZZombieSpecial.IMP));
        register(definitions, GardenId.JURASSIC_MARSH, "jurassic_bully", "Jurassic Bully", 80.0D, 1.0D, 6.0D, 0.25D, 1.1F,
                "Strong body-pressure zombie that punishes blockers.", Set.of(PvZZombieSpecial.JURASSIC_BULLY));
        register(definitions, GardenId.JURASSIC_MARSH, "rockpuncher_zombie", "Rockpuncher Zombie", 95.0D, 0.85D, 6.0D, 0.3D, 1.05F,
                "Anti-defense bruiser that deals bonus damage to wall plants.", Set.of(PvZZombieSpecial.ROCKPUNCHER));
        register(definitions, GardenId.JURASSIC_MARSH, "jurassic_gargantuar", "Jurassic Gargantuar", 350.0D, 0.58D, 18.0D, 0.95D, 3.0F,
                "Huge Jurassic Marsh gargantuar-like threat.", Set.of(PvZZombieSpecial.GARGANTUAR, PvZZombieSpecial.JURASSIC_GARGANTUAR));
        register(definitions, GardenId.FAR_FUTURE, "future_zombie", "Future Zombie", 20.0D, 1.0D, 4.0D, 0.0D, 1.0F,
                "Baseline Far Future zombie.", Set.of());
        register(definitions, GardenId.FAR_FUTURE, "conehead_future_zombie", "Conehead Future Zombie", 45.0D, 1.0D, 4.0D, 0.05D, 1.0F,
                "Early armored Far Future zombie.", Set.of());
        register(definitions, GardenId.FAR_FUTURE, "buckethead_future_zombie", "Buckethead Future Zombie", 90.0D, 0.95D, 4.0D, 0.15D, 1.0F,
                "Heavy armored Far Future zombie.", Set.of(PvZZombieSpecial.METAL));
        register(definitions, GardenId.FAR_FUTURE, "flag_future_zombie", "Flag Future Zombie", 20.0D, 1.15D, 4.0D, 0.0D, 1.0F,
                "Marks a larger Far Future push.", Set.of(PvZZombieSpecial.FLAG));
        register(definitions, GardenId.FAR_FUTURE, "jetpack_zombie", "Jetpack Zombie", 30.0D, 1.3D, 4.0D, 0.0D, 1.0F,
                "Flying Far Future bypass zombie.", Set.of(PvZZombieSpecial.FLYING));
        register(definitions, GardenId.FAR_FUTURE, "blastronaut_zombie", "Blastronaut Zombie", 40.0D, 1.15D, 3.0D, 0.0D, 1.0F,
                "Flying ranged pressure zombie.", Set.of(PvZZombieSpecial.FLYING, PvZZombieSpecial.BLASTRONAUT_BLAST));
        register(definitions, GardenId.FAR_FUTURE, "robo_cone_zombie", "Robo-Cone Zombie", 120.0D, 0.85D, 5.0D, 0.45D, 1.0F,
                "Advanced armored tech zombie.", Set.of(PvZZombieSpecial.METAL));
        register(definitions, GardenId.FAR_FUTURE, "mecha_football_zombie", "Mecha-Football Zombie", 140.0D, 1.15D, 9.0D, 0.65D, 1.15F,
                "Charging machine zombie with a minecart vehicle shell.", Set.of(PvZZombieSpecial.METAL, PvZZombieSpecial.MECHA_FOOTBALL_CHARGE, PvZZombieSpecial.VEHICLE_MINECART));
        register(definitions, GardenId.FAR_FUTURE, "disco_tron_3000", "Disco-tron 3000", 110.0D, 0.75D, 4.0D, 0.55D, 1.2F,
                "Machine summoner that creates Bug Bot Imps from a minecart shell.", Set.of(PvZZombieSpecial.DISCO_TRON_SUMMONER, PvZZombieSpecial.VEHICLE_MINECART, PvZZombieSpecial.METAL));
        register(definitions, GardenId.FAR_FUTURE, "bug_bot_imp", "Bug Bot Imp", 10.0D, 1.55D, 3.0D, 0.0D, 0.55F,
                "Small, fast Far Future summoned imp.", Set.of(PvZZombieSpecial.IMP));
        register(definitions, GardenId.FAR_FUTURE, "gargantuar_prime", "Gargantuar Prime", 360.0D, 0.56D, 18.0D, 0.95D, 3.0F,
                "Huge Far Future gargantuar-like threat.", Set.of(PvZZombieSpecial.GARGANTUAR, PvZZombieSpecial.METAL, PvZZombieSpecial.GARGANTUAR_PRIME));
        register(definitions, GardenId.BIG_WAVE_BEACH, "beach_zombie", "Beach Zombie", 20.0D, 1.0D, 4.0D, 0.0D, 1.0F,
                "Baseline Big Wave Beach zombie.", Set.of());
        register(definitions, GardenId.BIG_WAVE_BEACH, "conehead_beach_zombie", "Conehead Beach Zombie", 45.0D, 1.0D, 4.0D, 0.05D, 1.0F,
                "Early armored Big Wave Beach zombie.", Set.of());
        register(definitions, GardenId.BIG_WAVE_BEACH, "buckethead_beach_zombie", "Buckethead Beach Zombie", 90.0D, 0.95D, 4.0D, 0.15D, 1.0F,
                "Heavy armored Big Wave Beach zombie.", Set.of(PvZZombieSpecial.METAL));
        register(definitions, GardenId.BIG_WAVE_BEACH, "flag_beach_zombie", "Flag Beach Zombie", 20.0D, 1.15D, 4.0D, 0.0D, 1.0F,
                "Marks a larger Big Wave Beach push.", Set.of(PvZZombieSpecial.FLAG));
        register(definitions, GardenId.BIG_WAVE_BEACH, "snorkel_zombie", "Snorkel Zombie", 35.0D, 1.0D, 4.0D, 0.0D, 1.0F,
                "Aquatic sneak zombie that benefits from flooded tiles.", Set.of(PvZZombieSpecial.AQUATIC));
        register(definitions, GardenId.BIG_WAVE_BEACH, "surfer_zombie", "Surfer Zombie", 45.0D, 1.35D, 4.0D, 0.12D, 1.0F,
                "Fast surfboard pressure zombie that slows after the board breaks.", Set.of(PvZZombieSpecial.SURFER));
        register(definitions, GardenId.BIG_WAVE_BEACH, "fisherman_zombie", "Fisherman Zombie", 50.0D, 0.9D, 4.0D, 0.0D, 1.0F,
                "Hooks land plants into flooded tiles so they begin drowning.", Set.of(PvZZombieSpecial.FISHERMAN_HOOK));
        register(definitions, GardenId.BIG_WAVE_BEACH, "octo_zombie", "Octo Zombie", 60.0D, 0.9D, 4.0D, 0.05D, 1.0F,
                "Temporarily disables plants with octopus ink.", Set.of(PvZZombieSpecial.OCTO_DISABLE));
        register(definitions, GardenId.BIG_WAVE_BEACH, "mermaid_imp", "Mermaid Imp", 10.0D, 1.45D, 3.0D, 0.0D, 0.55F,
                "Small, fast aquatic imp.", Set.of(PvZZombieSpecial.IMP, PvZZombieSpecial.AQUATIC));
        register(definitions, GardenId.BIG_WAVE_BEACH, "deep_sea_gargantuar", "Deep Sea Gargantuar", 350.0D, 0.58D, 18.0D, 0.95D, 3.0F,
                "Huge Big Wave Beach gargantuar-like threat.", Set.of(PvZZombieSpecial.GARGANTUAR, PvZZombieSpecial.AQUATIC, PvZZombieSpecial.DEEP_SEA_GARGANTUAR));
        register(definitions, GardenId.PIRATE_SEAS, "pirate_zombie", "Pirate Zombie", 20.0D, 1.0D, 4.0D, 0.0D, 1.0F,
                "Baseline Pirate Seas zombie.", Set.of());
        register(definitions, GardenId.PIRATE_SEAS, "conehead_pirate_zombie", "Conehead Pirate Zombie", 45.0D, 1.0D, 4.0D, 0.05D, 1.0F,
                "Early armored Pirate Seas zombie.", Set.of());
        register(definitions, GardenId.PIRATE_SEAS, "buckethead_pirate_zombie", "Buckethead Pirate Zombie", 90.0D, 0.95D, 4.0D, 0.15D, 1.0F,
                "Heavy armored Pirate Seas zombie.", Set.of(PvZZombieSpecial.METAL));
        register(definitions, GardenId.PIRATE_SEAS, "flag_pirate_zombie", "Flag Pirate Zombie", 20.0D, 1.15D, 4.0D, 0.0D, 1.0F,
                "Marks a larger Pirate Seas push.", Set.of(PvZZombieSpecial.FLAG));
        register(definitions, GardenId.PIRATE_SEAS, "barrel_roller_zombie", "Barrel Roller Zombie", 45.0D, 1.0D, 4.0D, 0.05D, 1.0F,
                "Rolls barrel obstacles down plank paths.", Set.of(PvZZombieSpecial.BARREL_ROLLER));
        register(definitions, GardenId.PIRATE_SEAS, "barrel_obstacle", "Barrel", 40.0D, 0.65D, 8.0D, 0.65D, 1.0F,
                "Moving barrel obstacle that breaks in turbulent water.", Set.of(PvZZombieSpecial.BARREL_OBSTACLE));
        register(definitions, GardenId.PIRATE_SEAS, "swashbuckler_zombie", "Swashbuckler Zombie", 35.0D, 1.15D, 4.0D, 0.0D, 1.0F,
                "Rope-swings once onto a valid Pirate Seas plank.", Set.of(PvZZombieSpecial.SWASHBUCKLER));
        register(definitions, GardenId.PIRATE_SEAS, "seagull_zombie", "Seagull Zombie", 25.0D, 1.35D, 4.0D, 0.0D, 1.0F,
                "Flying Pirate Seas pressure zombie.", Set.of(PvZZombieSpecial.FLYING));
        register(definitions, GardenId.PIRATE_SEAS, "pelican_zombie", "Pelican Zombie", 35.0D, 1.25D, 4.0D, 0.0D, 1.0F,
                "Flying carrier that can drop a Pirate Imp.", Set.of(PvZZombieSpecial.FLYING, PvZZombieSpecial.PELICAN_DROPPER));
        register(definitions, GardenId.PIRATE_SEAS, "imp_cannon", "Imp Cannon", 80.0D, 0.0D, 0.0D, 0.85D, 1.2F,
                "Stationary cannon that launches Pirate Imps onto planks.", Set.of(PvZZombieSpecial.IMP_CANNON, PvZZombieSpecial.STATIONARY));
        register(definitions, GardenId.PIRATE_SEAS, "pirate_imp", "Pirate Imp", 10.0D, 1.45D, 3.0D, 0.0D, 0.55F,
                "Small, fast Pirate Seas imp.", Set.of(PvZZombieSpecial.IMP));
        register(definitions, GardenId.PIRATE_SEAS, "pirate_captain_zombie", "Pirate Captain Zombie", 70.0D, 0.9D, 4.0D, 0.1D, 1.0F,
                "Supports nearby Pirate Seas zombies.", Set.of(PvZZombieSpecial.PIRATE_CAPTAIN));
        register(definitions, GardenId.PIRATE_SEAS, "pirate_gargantuar", "Pirate Gargantuar", 340.0D, 0.58D, 18.0D, 0.95D, 3.0F,
                "Huge Pirate Seas gargantuar-like threat.", Set.of(PvZZombieSpecial.GARGANTUAR, PvZZombieSpecial.PIRATE_GARGANTUAR));
        register(definitions, GardenId.MODERN_DAY, "modern_zombie", "Modern Zombie", 20.0D, 1.0D, 4.0D, 0.0D, 1.0F,
                "Baseline Modern Day zombie.", Set.of());
        register(definitions, GardenId.MODERN_DAY, "conehead_modern_zombie", "Conehead Modern Zombie", 45.0D, 1.0D, 4.0D, 0.05D, 1.0F,
                "Armored Modern Day zombie.", Set.of());
        register(definitions, GardenId.MODERN_DAY, "buckethead_modern_zombie", "Buckethead Modern Zombie", 90.0D, 0.95D, 4.0D, 0.15D, 1.0F,
                "Heavy armored Modern Day zombie.", Set.of(PvZZombieSpecial.METAL));
        register(definitions, GardenId.MODERN_DAY, "flag_modern_zombie", "Flag Modern Zombie", 20.0D, 1.15D, 4.0D, 0.0D, 1.0F,
                "Marks a larger Modern Day push.", Set.of(PvZZombieSpecial.FLAG));
        register(definitions, GardenId.MODERN_DAY, "sunday_edition_zombie", "Sunday Edition Zombie", 120.0D, 0.8D, 5.0D, 0.2D, 1.0F,
                "Durable newspaper variant that rages after its Sunday edition breaks.", Set.of(PvZZombieSpecial.NEWSPAPER_RAGE, PvZZombieSpecial.SUNDAY_EDITION));
        register(definitions, GardenId.MODERN_DAY, "balloon_zombie", "Balloon Zombie", 40.0D, 1.25D, 4.0D, 0.0D, 1.0F,
                "Flying Modern Day bypass zombie.", Set.of(PvZZombieSpecial.FLYING));
        register(definitions, GardenId.MODERN_DAY, "all_star_zombie", "All-Star Zombie", 110.0D, 1.3D, 8.0D, 0.35D, 1.0F,
                "Fast tackle tank that can kick Super-Fan Imps.", Set.of(PvZZombieSpecial.ALL_STAR_TACKLE, PvZZombieSpecial.METAL));
        register(definitions, GardenId.MODERN_DAY, "super_fan_imp", "Super-Fan Imp", 12.0D, 1.45D, 3.0D, 0.0D, 0.55F,
                "Small fast imp that explodes near plants.", Set.of(PvZZombieSpecial.IMP, PvZZombieSpecial.SUPER_FAN_EXPLODE));
        register(definitions, GardenId.MODERN_DAY, "rally_zombie", "Rally Zombie", 45.0D, 1.2D, 4.0D, 0.0D, 1.0F,
                "Large-wave support zombie that briefly rallies nearby zombies.", Set.of(PvZZombieSpecial.RALLY_SUPPORT));
        register(definitions, GardenId.MODERN_DAY, "brickhead_zombie", "Brickhead Zombie", 140.0D, 0.9D, 5.0D, 0.35D, 1.0F,
                "Extremely durable brick-armored zombie.", Set.of(PvZZombieSpecial.METAL));
        register(definitions, GardenId.MODERN_DAY, "modern_gargantuar", "Modern Gargantuar", 360.0D, 0.58D, 18.0D, 0.95D, 3.0F,
                "Huge final easy-mode gargantuar-like threat.", Set.of(PvZZombieSpecial.GARGANTUAR, PvZZombieSpecial.MODERN_GARGANTUAR));
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

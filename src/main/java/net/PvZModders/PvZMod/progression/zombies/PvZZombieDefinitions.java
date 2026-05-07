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
        return Collections.unmodifiableMap(new LinkedHashMap<>(definitions));
    }

    private static void register(Map<String, PvZZombieDefinition> definitions, String id, String displayName, double maxHealth,
                                 double movementSpeedMultiplier, double attackDamage, double knockbackResistance,
                                 float visualScale, String almanacText, Set<PvZZombieSpecial> specials) {
        definitions.put(id, new PvZZombieDefinition(
                id,
                displayName,
                GardenId.INITIAL_PLAINS,
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

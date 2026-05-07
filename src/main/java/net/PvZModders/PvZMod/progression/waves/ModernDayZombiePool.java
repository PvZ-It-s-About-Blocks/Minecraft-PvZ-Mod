package net.PvZModders.PvZMod.progression.waves;

import net.PvZModders.PvZMod.progression.zombies.PvZZombieDefinitions;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ModernDayZombiePool {
    private static final int DEFAULT_DIRECTION_COUNT_EARLY = 1;
    private static final int DEFAULT_DIRECTION_COUNT_MID = 2;
    private static final int DEFAULT_DIRECTION_COUNT_LATE = 3;
    private static final int DEFAULT_DIRECTION_COUNT_FINAL = 4;

    private ModernDayZombiePool() {
    }

    public static List<WaveSpawnGroup> groupsForWave(int wave) {
        ModernDayWaveMix mix = mixForWave(wave);
        List<WeightedZombie> pool = new ArrayList<>();
        for (ModernDayZombieGroup group : mix.groups()) {
            pool.addAll(entriesForGroup(group, wave));
        }

        List<WaveSpawnGroup> groups = new ArrayList<>();
        addWeighted(groups, mix.zombieCount(), mix.directionCount(), mix, pool);
        if (wave >= 27 && wave < 30) {
            addGroup(groups, "modern_gargantuar", 1, mix.directionCount());
        } else if (wave >= 30) {
            addGroup(groups, "modern_gargantuar", 2, mix.directionCount());
        }
        return List.copyOf(groups);
    }

    public static List<ModernDayZombieGroup> groupsForWaveThemes(int wave) {
        return mixForWave(wave).groups();
    }

    public static List<String> getZombieTypesForGardenGroup(ModernDayZombieGroup group) {
        return entriesForGroup(group, 30).stream().map(WeightedZombie::id).toList();
    }

    public static String resolveZombieEntityTypeId(String requestedId, ModernDayZombieGroup group) {
        String resolved = resolveZombieId(requestedId, fallbackForGroup(group));
        return resolved.contains(":") ? resolved : "pvz2mod:" + resolved;
    }

    private static ModernDayWaveMix mixForWave(int wave) {
        int zombieCount = Math.min(72, 4 + wave + (wave / 5) * 4);
        int directionCount = wave >= 30 ? DEFAULT_DIRECTION_COUNT_FINAL : wave >= 22 ? DEFAULT_DIRECTION_COUNT_LATE : wave >= 10 ? DEFAULT_DIRECTION_COUNT_MID : DEFAULT_DIRECTION_COUNT_EARLY;
        if (wave <= 3) {
            return new ModernDayWaveMix(zombieCount, directionCount, 0, 0, 1, 1, 1,
                    List.of(ModernDayZombieGroup.MODERN_DAY, ModernDayZombieGroup.ORIGINAL));
        }
        if (wave <= 6) {
            return new ModernDayWaveMix(zombieCount, directionCount, 0, 1, 2, 2, 2,
                    List.of(ModernDayZombieGroup.MODERN_DAY, ModernDayZombieGroup.ORIGINAL, ModernDayZombieGroup.ANCIENT_EGYPT, ModernDayZombieGroup.PIRATE_SEAS));
        }
        if (wave <= 9) {
            return new ModernDayWaveMix(zombieCount, directionCount, 0, 2, 2, 2, 3,
                    List.of(ModernDayZombieGroup.MODERN_DAY, ModernDayZombieGroup.ORIGINAL, ModernDayZombieGroup.ANCIENT_EGYPT, ModernDayZombieGroup.PIRATE_SEAS, ModernDayZombieGroup.WILD_WEST, ModernDayZombieGroup.FROSTBITE));
        }
        if (wave <= 12) {
            return new ModernDayWaveMix(zombieCount, directionCount, 0, 2, 3, 3, 3,
                    List.of(ModernDayZombieGroup.MODERN_DAY, ModernDayZombieGroup.ORIGINAL, ModernDayZombieGroup.ANCIENT_EGYPT, ModernDayZombieGroup.PIRATE_SEAS, ModernDayZombieGroup.WILD_WEST, ModernDayZombieGroup.FROSTBITE, ModernDayZombieGroup.LOST_CITY, ModernDayZombieGroup.DARK_AGES));
        }
        if (wave <= 15) {
            return new ModernDayWaveMix(zombieCount, directionCount, 0, 3, 3, 3, 4,
                    List.of(ModernDayZombieGroup.MODERN_DAY, ModernDayZombieGroup.ORIGINAL, ModernDayZombieGroup.LOST_CITY, ModernDayZombieGroup.DARK_AGES, ModernDayZombieGroup.NEON_MIXTAPE, ModernDayZombieGroup.JURASSIC_MARSH));
        }
        if (wave <= 18) {
            return new ModernDayWaveMix(zombieCount, directionCount, 0, 3, 4, 4, 4,
                    List.of(ModernDayZombieGroup.MODERN_DAY, ModernDayZombieGroup.ORIGINAL, ModernDayZombieGroup.FAR_FUTURE, ModernDayZombieGroup.BIG_WAVE_BEACH, ModernDayZombieGroup.NEON_MIXTAPE, ModernDayZombieGroup.JURASSIC_MARSH));
        }
        if (wave <= 24) {
            return new ModernDayWaveMix(zombieCount, directionCount, 0, 4, 4, 5, 5,
                    List.of(ModernDayZombieGroup.MODERN_DAY, ModernDayZombieGroup.ORIGINAL, ModernDayZombieGroup.ANCIENT_EGYPT, ModernDayZombieGroup.PIRATE_SEAS, ModernDayZombieGroup.WILD_WEST, ModernDayZombieGroup.FROSTBITE, ModernDayZombieGroup.LOST_CITY, ModernDayZombieGroup.FAR_FUTURE, ModernDayZombieGroup.DARK_AGES, ModernDayZombieGroup.NEON_MIXTAPE, ModernDayZombieGroup.JURASSIC_MARSH, ModernDayZombieGroup.BIG_WAVE_BEACH));
        }
        return new ModernDayWaveMix(zombieCount, directionCount, wave >= 30 ? 2 : 1, 5, 5, 6, 6,
                List.of(ModernDayZombieGroup.MODERN_DAY, ModernDayZombieGroup.ORIGINAL, ModernDayZombieGroup.ANCIENT_EGYPT, ModernDayZombieGroup.PIRATE_SEAS, ModernDayZombieGroup.WILD_WEST, ModernDayZombieGroup.FROSTBITE, ModernDayZombieGroup.LOST_CITY, ModernDayZombieGroup.FAR_FUTURE, ModernDayZombieGroup.DARK_AGES, ModernDayZombieGroup.NEON_MIXTAPE, ModernDayZombieGroup.JURASSIC_MARSH, ModernDayZombieGroup.BIG_WAVE_BEACH));
    }

    private static List<WeightedZombie> entriesForGroup(ModernDayZombieGroup group, int wave) {
        return switch (group) {
            case MODERN_DAY -> modernDayEntries(wave);
            case ORIGINAL -> entries(
                    entry("basic_zombie", 28), entry("conehead_zombie", 12), entry("buckethead_zombie", 8),
                    entry("flag_zombie", 2, 1), entry("newspaper_zombie", 8), entry("screen_door_zombie", 6),
                    entry("pole_vaulting_zombie", 4), entry("football_zombie", 4, 2), entry("imp", 4),
                    entry("gargantuar", 1, wave >= 25 ? 1 : 0));
            case ANCIENT_EGYPT -> entries(
                    entry("mummy_zombie", 28), entry("conehead_mummy", 12), entry("buckethead_mummy", 8),
                    entry("flag_mummy_zombie", 2, 1), entry("ra_zombie", 5, 2), entry("camel_zombie", 6),
                    entry("explorer_zombie", 5), entry("tomb_raiser_zombie", 4, 2), entry("pharaoh_zombie", 4, 2),
                    entry("mummified_gargantuar", 1, wave >= 25 ? 1 : 0));
            case PIRATE_SEAS -> entries(
                    entry("pirate_zombie", 28), entry("conehead_pirate_zombie", 12), entry("buckethead_pirate_zombie", 8),
                    entry("flag_pirate_zombie", 2, 1), entry("barrel_roller_zombie", 5), entry("swashbuckler_zombie", 6),
                    entry("seagull_zombie", 5, 2), entry("pelican_zombie", 4, 2), entry("imp_cannon", 2, 1),
                    entry("pirate_imp", 4), entry("pirate_captain_zombie", 3, 1), entry("pirate_gargantuar", 1, wave >= 25 ? 1 : 0));
            case WILD_WEST -> entries(
                    entry("cowboy_zombie", 28), entry("conehead_cowboy", 12), entry("buckethead_cowboy", 8),
                    entry("flag_cowboy_zombie", 2, 1), entry("prospector_zombie", 6), entry("pianist_zombie", 3, 1),
                    entry("poncho_zombie", 6), entry("chicken_wrangler_zombie", 4, 2), entry("zombie_chicken", 4),
                    entry("bull_rider_zombie", 4, 2), entry("zombie_bull", 4, 2), entry("wild_west_gargantuar", 1, wave >= 25 ? 1 : 0));
            case FROSTBITE -> entries(
                    entry("cave_zombie", 28), entry("conehead_cave_zombie", 12), entry("buckethead_cave_zombie", 8),
                    entry("flag_cave_zombie", 2, 1), entry("hunter_zombie", 5, 2), entry("troglobite", 4, 2),
                    entry("ice_block_zombie", 6), entry("weasel_hoarder", 4, 2), entry("zombie_weasel", 4),
                    entry("dodo_rider_zombie", 4, 2), entry("dodo", 3), entry("sloth_gargantuar", 1, wave >= 25 ? 1 : 0));
            case LOST_CITY -> entries(
                    entry("lost_city_zombie", 28), entry("conehead_lost_city_zombie", 12), entry("buckethead_lost_city_zombie", 8),
                    entry("flag_lost_city_zombie", 2, 1), entry("excavator_zombie", 5), entry("parasol_zombie", 5),
                    entry("relic_hunter_zombie", 5), entry("turquoise_skull_zombie", 4, 2), entry("lost_pilot_zombie", 4, 2),
                    entry("bug_zombie", 4, 2), entry("imp_porter", 4), entry("porter_gargantuar", 1, wave >= 25 ? 1 : 0));
            case FAR_FUTURE -> entries(
                    entry("future_zombie", 28), entry("conehead_future_zombie", 12), entry("buckethead_future_zombie", 8),
                    entry("flag_future_zombie", 2, 1), entry("jetpack_zombie", 5, 2), entry("blastronaut_zombie", 4, 2),
                    entry("robo_cone_zombie", 5), entry("mecha_football_zombie", 4, 2), entry("disco_tron_3000", 2, 1),
                    entry("bug_bot_imp", 4), entry("gargantuar_prime", 1, wave >= 25 ? 1 : 0));
            case DARK_AGES -> entries(
                    entry("peasant_zombie", 28), entry("conehead_peasant", 12), entry("buckethead_peasant", 8),
                    entry("flag_peasant_zombie", 2, 1), entry("knight_zombie", 6), entry("jester_zombie", 4, 2),
                    entry("wizard_zombie", 4, 2), entry("king_zombie", 3, 1), entry("dragon_imp", 4),
                    entry("dark_ages_gargantuar", 1, wave >= 25 ? 1 : 0));
            case NEON_MIXTAPE -> entries(
                    entry("neon_zombie", 28), entry("conehead_neon_zombie", 12), entry("buckethead_neon_zombie", 8),
                    entry("flag_neon_zombie", 2, 1), entry("punk_zombie", 5), entry("glitter_zombie", 3, 1),
                    entry("mc_zom_b", 3, 1), entry("breakdancer_zombie", 4, 2), entry("arcade_zombie", 2, 1),
                    entry("eight_bit_zombie", 4), entry("boombox_zombie", 2, 1), entry("neon_gargantuar", 1, wave >= 25 ? 1 : 0));
            case JURASSIC_MARSH -> entries(
                    entry("jurassic_zombie", 28), entry("conehead_jurassic_zombie", 12), entry("buckethead_jurassic_zombie", 8),
                    entry("flag_jurassic_zombie", 2, 1), entry("fossilhead_zombie", 6), entry("amberhead_zombie", 4, 2),
                    entry("jurassic_imp", 4), entry("jurassic_bully", 4), entry("rockpuncher_zombie", 4),
                    entry("jurassic_gargantuar", 1, wave >= 25 ? 1 : 0));
            case BIG_WAVE_BEACH -> entries(
                    entry("beach_zombie", 28), entry("conehead_beach_zombie", 12), entry("buckethead_beach_zombie", 8),
                    entry("flag_beach_zombie", 2, 1), entry("snorkel_zombie", 5), entry("surfer_zombie", 5),
                    entry("fisherman_zombie", 4, 2), entry("octo_zombie", 3, 1), entry("mermaid_imp", 4),
                    entry("deep_sea_gargantuar", 1, wave >= 25 ? 1 : 0));
        };
    }

    private static List<WeightedZombie> modernDayEntries(int wave) {
        List<WeightedZombie> entries = new ArrayList<>();
        entries.add(entry("modern_zombie", 34));
        entries.add(entry("conehead_modern_zombie", 14));
        entries.add(entry("buckethead_modern_zombie", wave >= 5 ? 8 : 0));
        entries.add(entry("flag_modern_zombie", wave >= 3 ? 2 : 0, 1));
        if (wave >= 19) {
            entries.add(entry("balloon_zombie", 5, 2));
            entries.add(entry("all_star_zombie", 5, 2));
            entries.add(entry("super_fan_imp", 4));
            entries.add(entry("rally_zombie", 3, 1));
        }
        if (wave >= 22) {
            entries.add(entry("sunday_edition_zombie", 5, 2));
            entries.add(entry("brickhead_zombie", 5, 2));
        }
        return entries;
    }

    private static void addWeighted(List<WaveSpawnGroup> groups, int totalCount, int directionCount, ModernDayWaveMix mix, List<WeightedZombie> entries) {
        List<WeightedZombie> usableEntries = entries.stream()
                .filter(entry -> entry.weight() > 0)
                .filter(entry -> entry.cap() != 0)
                .toList();
        int totalWeight = usableEntries.stream().mapToInt(WeightedZombie::weight).sum();
        int remaining = Math.max(0, totalCount);
        Map<String, Integer> cappedCounts = new LinkedHashMap<>();
        EnumMap<ModernCap, Integer> categoryCounts = new EnumMap<>(ModernCap.class);
        for (int i = 0; i < usableEntries.size(); i++) {
            WeightedZombie entry = usableEntries.get(i);
            int count = i == usableEntries.size() - 1 ? remaining : (int) Math.floor(totalCount * (entry.weight() / (double) totalWeight));
            count = Math.min(remaining, count);
            count = Math.min(count, allowedByCaps(entry, mix, categoryCounts));
            if (count <= 0) {
                continue;
            }
            cappedCounts.merge(resolveZombieId(entry.id(), fallbackFor(entry.id())), count, Integer::sum);
            incrementCaps(entry, categoryCounts, count);
            remaining -= count;
        }
        if (remaining > 0) {
            cappedCounts.merge(resolveZombieId("modern_zombie", "basic_zombie"), remaining, Integer::sum);
        }
        for (Map.Entry<String, Integer> entry : cappedCounts.entrySet()) {
            addResolvedGroup(groups, entry.getKey(), entry.getValue(), directionCount, List.of());
        }
    }

    private static int allowedByCaps(WeightedZombie entry, ModernDayWaveMix mix, EnumMap<ModernCap, Integer> counts) {
        int max = entry.cap() > 0 ? entry.cap() : Integer.MAX_VALUE;
        if (entry.gargantuar()) {
            max = Math.min(max, mix.maxGargantuars() - counts.getOrDefault(ModernCap.GARGANTUAR, 0));
        }
        if (entry.flying()) {
            max = Math.min(max, mix.maxFlying() - counts.getOrDefault(ModernCap.FLYING, 0));
        }
        if (entry.summoner()) {
            max = Math.min(max, mix.maxSummoners() - counts.getOrDefault(ModernCap.SUMMONER, 0));
        }
        if (entry.control()) {
            max = Math.min(max, mix.maxControl() - counts.getOrDefault(ModernCap.CONTROL, 0));
        }
        if (entry.support()) {
            max = Math.min(max, mix.maxSupport() - counts.getOrDefault(ModernCap.SUPPORT, 0));
        }
        return Math.max(0, max);
    }

    private static void incrementCaps(WeightedZombie entry, EnumMap<ModernCap, Integer> counts, int count) {
        if (entry.gargantuar()) {
            counts.merge(ModernCap.GARGANTUAR, count, Integer::sum);
        }
        if (entry.flying()) {
            counts.merge(ModernCap.FLYING, count, Integer::sum);
        }
        if (entry.summoner()) {
            counts.merge(ModernCap.SUMMONER, count, Integer::sum);
        }
        if (entry.control()) {
            counts.merge(ModernCap.CONTROL, count, Integer::sum);
        }
        if (entry.support()) {
            counts.merge(ModernCap.SUPPORT, count, Integer::sum);
        }
    }

    private static void addGroup(List<WaveSpawnGroup> groups, String zombieId, int count, int directionCount) {
        addResolvedGroup(groups, resolveZombieId(zombieId, "basic_zombie"), count, directionCount, List.of());
    }

    private static void addResolvedGroup(List<WaveSpawnGroup> groups, String resolvedZombieId, int count, int directionCount, List<WaveSpawnDirection> fixedDirections) {
        if (count <= 0) {
            return;
        }
        if (resolvedZombieId.contains(":")) {
            groups.add(new WaveSpawnGroup(resolvedZombieId, count, directionCount, fixedDirections));
        } else {
            groups.add(new WaveSpawnGroup("pvz2mod:" + resolvedZombieId, count, directionCount, fixedDirections));
        }
    }

    private static String resolveZombieId(String requestedId, String fallbackId) {
        if (PvZZombieDefinitions.isCustomZombieId(requestedId)) {
            return requestedId;
        }
        if (PvZZombieDefinitions.isCustomZombieId(fallbackId)) {
            return fallbackId;
        }
        return "minecraft:zombie";
    }

    private static String fallbackFor(String requestedId) {
        if (requestedId.contains("future") || requestedId.contains("jetpack") || requestedId.contains("robo") || requestedId.contains("mecha") || requestedId.contains("disco") || requestedId.contains("bot") || requestedId.contains("prime")) {
            return "basic_zombie";
        }
        return "basic_zombie";
    }

    private static String fallbackForGroup(ModernDayZombieGroup group) {
        return switch (group) {
            case MODERN_DAY -> "modern_zombie";
            case ORIGINAL -> "basic_zombie";
            case ANCIENT_EGYPT -> "mummy_zombie";
            case PIRATE_SEAS -> "pirate_zombie";
            case WILD_WEST -> "cowboy_zombie";
            case FROSTBITE -> "cave_zombie";
            case LOST_CITY -> "lost_city_zombie";
            case FAR_FUTURE -> "basic_zombie";
            case DARK_AGES -> "peasant_zombie";
            case NEON_MIXTAPE -> "neon_zombie";
            case JURASSIC_MARSH -> "jurassic_zombie";
            case BIG_WAVE_BEACH -> "beach_zombie";
        };
    }

    private static List<WeightedZombie> entries(WeightedZombie... entries) {
        return List.of(entries);
    }

    private static WeightedZombie entry(String id, int weight) {
        return entry(id, weight, -1);
    }

    private static WeightedZombie entry(String id, int weight, int cap) {
        return new WeightedZombie(id, weight, cap);
    }

    private enum ModernCap {
        GARGANTUAR,
        FLYING,
        SUMMONER,
        CONTROL,
        SUPPORT
    }

    private record ModernDayWaveMix(int zombieCount, int directionCount, int maxGargantuars, int maxFlying,
                                    int maxSummoners, int maxControl, int maxSupport,
                                    List<ModernDayZombieGroup> groups) {
    }

    private record WeightedZombie(String id, int weight, int cap) {
        private boolean gargantuar() {
            return id.contains("gargantuar") || id.contains("prime");
        }

        private boolean flying() {
            return id.contains("balloon") || id.contains("jetpack") || id.contains("blastronaut")
                    || id.contains("seagull") || id.contains("pelican") || id.contains("pilot") || id.equals("bug_zombie");
        }

        private boolean summoner() {
            return id.contains("arcade") || id.contains("cannon") || id.contains("wrangler") || id.contains("hoarder") || id.contains("disco_tron");
        }

        private boolean control() {
            return id.contains("wizard") || id.contains("fisherman") || id.contains("octo") || id.contains("hunter") || id.contains("jester");
        }

        private boolean support() {
            return id.contains("king") || id.contains("glitter") || id.contains("mc_zom_b") || id.contains("boombox")
                    || id.contains("captain") || id.contains("rally") || id.contains("pianist");
        }
    }
}

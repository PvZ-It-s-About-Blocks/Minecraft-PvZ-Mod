package net.PvZModders.PvZMod.progression.waves;

import java.util.ArrayList;
import java.util.List;

public final class ModernDayZombiePool {
    private ModernDayZombiePool() {
    }

    public enum Theme {
        ORIGINAL,
        ANCIENT_EGYPT,
        PIRATE_SEAS,
        WILD_WEST,
        FROSTBITE,
        LOST_CITY,
        FAR_FUTURE,
        DARK_AGES,
        NEON_MIXTAPE,
        JURASSIC_MARSH,
        BIG_WAVE_BEACH,
        GREENHOUSE
    }

    public static List<WaveSpawnGroup> groupsForWave(int wave) {
        List<Theme> themes = themesForWave(wave);
        int totalZombies = Math.min(68, 3 + wave + (wave / 5) * 4);
        int directionCount = wave >= 30 ? 4 : wave >= 22 ? 3 : wave >= 10 ? 2 : 1;
        List<WaveSpawnGroup> groups = new ArrayList<>();
        int baseCount = Math.max(1, totalZombies / themes.size());
        int remainder = totalZombies % themes.size();
        for (int i = 0; i < themes.size(); i++) {
            int count = baseCount + (i < remainder ? 1 : 0);
            groups.add(new WaveSpawnGroup(entityTypeFor(themes.get(i)), count, directionCount, List.of()));
        }
        return List.copyOf(groups);
    }

    public static List<Theme> themesForWave(int wave) {
        if (wave >= 30) {
            return List.of(Theme.ORIGINAL, Theme.ANCIENT_EGYPT, Theme.PIRATE_SEAS, Theme.WILD_WEST, Theme.FROSTBITE, Theme.LOST_CITY, Theme.FAR_FUTURE, Theme.DARK_AGES, Theme.NEON_MIXTAPE, Theme.JURASSIC_MARSH, Theme.BIG_WAVE_BEACH);
        }
        if (wave >= 24) {
            return List.of(Theme.ORIGINAL, Theme.PIRATE_SEAS, Theme.WILD_WEST, Theme.FAR_FUTURE, Theme.NEON_MIXTAPE, Theme.JURASSIC_MARSH, Theme.BIG_WAVE_BEACH);
        }
        if (wave >= 16) {
            return List.of(Theme.ORIGINAL, Theme.ANCIENT_EGYPT, Theme.FROSTBITE, Theme.LOST_CITY, Theme.DARK_AGES);
        }
        if (wave >= 10) {
            return List.of(Theme.ORIGINAL, Theme.ANCIENT_EGYPT, Theme.PIRATE_SEAS, Theme.WILD_WEST);
        }
        return List.of(Theme.ORIGINAL);
    }

    public static String entityTypeFor(Theme theme) {
        // TODO: Replace each theme with its future garden-specific zombie entity type.
        return "minecraft:zombie";
    }
}

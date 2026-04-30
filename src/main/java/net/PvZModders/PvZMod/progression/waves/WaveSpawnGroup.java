package net.PvZModders.PvZMod.progression.waves;

import java.util.List;

public record WaveSpawnGroup(
        String entityTypeId,
        int count,
        int directionCount,
        List<WaveSpawnDirection> fixedDirections
) {
    public boolean usesRandomDirections() {
        return fixedDirections.isEmpty();
    }
}

package net.PvZModders.PvZMod.progression.waves;

import java.util.List;

public record GardenWaveDefinition(
        int wave,
        String scanText,
        List<WaveReward> rewards
) {
}

package net.PvZModders.PvZMod.progression.waves;

import net.PvZModders.PvZMod.progression.GardenId;

import java.util.List;

public final class GardenWaves {
    private GardenWaves() {
    }

    public static List<GardenWaveDefinition> all(GardenId gardenId) {
        return gardenId == GardenId.DESERT ? AncientEgyptWaves.all() : OriginalGardenWaves.all();
    }

    public static GardenWaveDefinition get(GardenId gardenId, int wave) {
        return gardenId == GardenId.DESERT ? AncientEgyptWaves.get(wave) : OriginalGardenWaves.get(wave);
    }
}

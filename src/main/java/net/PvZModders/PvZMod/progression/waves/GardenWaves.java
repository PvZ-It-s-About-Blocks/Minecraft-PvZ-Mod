package net.PvZModders.PvZMod.progression.waves;

import net.PvZModders.PvZMod.progression.GardenId;

import java.util.List;

public final class GardenWaves {
    private GardenWaves() {
    }

    public static List<GardenWaveDefinition> all(GardenId gardenId) {
        return switch (gardenId) {
            case DESERT -> AncientEgyptWaves.all();
            case WILD_WEST -> WildWestWaves.all();
            case LOST_CITY -> LostCityWaves.all();
            default -> OriginalGardenWaves.all();
        };
    }

    public static GardenWaveDefinition get(GardenId gardenId, int wave) {
        return switch (gardenId) {
            case DESERT -> AncientEgyptWaves.get(wave);
            case WILD_WEST -> WildWestWaves.get(wave);
            case LOST_CITY -> LostCityWaves.get(wave);
            default -> OriginalGardenWaves.get(wave);
        };
    }
}

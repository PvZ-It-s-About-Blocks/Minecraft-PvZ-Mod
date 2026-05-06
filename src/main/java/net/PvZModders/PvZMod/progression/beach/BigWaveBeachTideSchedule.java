package net.PvZModders.PvZMod.progression.beach;

import java.util.List;

public final class BigWaveBeachTideSchedule {
    private BigWaveBeachTideSchedule() {
    }

    public static boolean isHighTideActive(int wave, long elapsedTicks) {
        for (TideEvent event : eventsForWave(wave)) {
            if (elapsedTicks >= event.startTick() && elapsedTicks < event.startTick() + event.durationTicks()) {
                return true;
            }
        }
        return false;
    }

    public static List<TideEvent> eventsForWave(int wave) {
        return switch (wave) {
            case 3 -> List.of(seconds(25, 20));
            case 6 -> List.of(seconds(15, 20));
            case 10 -> List.of(seconds(20, 35));
            case 15 -> List.of(seconds(10, 20), seconds(45, 20));
            case 20 -> List.of(seconds(10, 60));
            case 25 -> List.of(seconds(5, 35), seconds(55, 30));
            case 30 -> List.of(seconds(5, 90));
            default -> wave > 10 && wave % 4 == 0 ? List.of(seconds(20, 25)) : List.of();
        };
    }

    private static TideEvent seconds(int startSeconds, int durationSeconds) {
        return new TideEvent(startSeconds * 20L, durationSeconds * 20L);
    }

    public record TideEvent(long startTick, long durationTicks) {
    }
}

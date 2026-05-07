package net.PvZModders.PvZMod.progression.waves;

import java.util.List;
import java.util.Map;

public final class AncientEgyptSandstormSchedule {
    private static final Map<Integer, List<SandstormEvent>> EVENTS = Map.ofEntries(
            Map.entry(5, List.of(seconds(25, 5))),
            Map.entry(8, List.of(seconds(20, 6))),
            Map.entry(12, List.of(seconds(15, 6), seconds(45, 5))),
            Map.entry(18, List.of(seconds(20, 8))),
            Map.entry(24, List.of(seconds(10, 6), seconds(50, 8))),
            Map.entry(30, List.of(seconds(10, 8), seconds(45, 10)))
    );

    private AncientEgyptSandstormSchedule() {
    }

    public static List<SandstormEvent> eventsForWave(int wave) {
        return EVENTS.getOrDefault(wave, List.of());
    }

    public static boolean isActive(int wave, long elapsedTicks) {
        for (SandstormEvent event : eventsForWave(wave)) {
            if (event.isActive(elapsedTicks)) {
                return true;
            }
        }
        return false;
    }

    private static SandstormEvent seconds(int startSeconds, int durationSeconds) {
        return new SandstormEvent(startSeconds * 20L, durationSeconds * 20L);
    }

    public record SandstormEvent(long startTick, long durationTicks) {
        public boolean isActive(long elapsedTicks) {
            return elapsedTicks >= startTick && elapsedTicks < startTick + durationTicks;
        }

        public boolean startsNow(long elapsedTicks) {
            return elapsedTicks == startTick;
        }
    }
}

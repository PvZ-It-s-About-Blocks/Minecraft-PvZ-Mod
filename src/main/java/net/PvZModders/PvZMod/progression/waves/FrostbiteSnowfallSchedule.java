package net.PvZModders.PvZMod.progression.waves;

import java.util.List;

public final class FrostbiteSnowfallSchedule {
    private FrostbiteSnowfallSchedule() {
    }

    public static boolean isHeavySnowfallActive(int wave, long elapsedTicks) {
        for (SnowfallWindow window : windowsForWave(wave)) {
            if (elapsedTicks >= window.startTick() && elapsedTicks < window.endTick()) {
                return true;
            }
        }
        return false;
    }

    public static List<SnowfallWindow> windowsForWave(int wave) {
        return switch (wave) {
            case 3 -> List.of(seconds(20, 35));
            case 5 -> List.of(seconds(25, 40));
            case 7 -> List.of(seconds(15, 30), seconds(45, 55));
            case 10 -> List.of(seconds(20, 50));
            case 15 -> List.of(seconds(10, 30), seconds(45, 65));
            case 20 -> List.of(seconds(10, 70));
            case 25 -> List.of(seconds(5, 35), seconds(50, 80));
            case 30 -> List.of(seconds(5, 90));
            default -> List.of();
        };
    }

    private static SnowfallWindow seconds(int startSecond, int endSecond) {
        return new SnowfallWindow(startSecond * 20L, endSecond * 20L, 1);
    }

    public record SnowfallWindow(long startTick, long endTick, int intensity) {
    }
}

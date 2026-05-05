package net.PvZModders.PvZMod.progression.waves;

import java.util.List;

public final class NeonSpeakerSchedule {
    private NeonSpeakerSchedule() {
    }

    public static List<NeonSpeakerPulse> pulsesForWave(int wave) {
        return switch (wave) {
            case 2 -> pulse(20 * 30, 1, 20 * 4);
            case 4 -> pulse(20 * 22, 2, 20 * 7);
            case 8 -> pulse(20 * 15, 3, 20 * 8);
            case 10, 12 -> pulse(20 * 18, 2, 20 * 10);
            case 15 -> pulse(20 * 12, 4, 20 * 8);
            case 18 -> pulse(20 * 20, 3, 20 * 7);
            case 20 -> pulse(20 * 10, 5, 20 * 7);
            case 22, 24 -> pulse(20 * 14, 4, 20 * 8);
            case 25 -> pulse(20 * 8, 6, 20 * 6);
            case 27 -> pulse(20 * 10, 5, 20 * 6);
            case 30 -> pulse(20 * 6, 8, 20 * 5);
            default -> List.of();
        };
    }

    private static List<NeonSpeakerPulse> pulse(int firstTick, int count, int intervalTicks) {
        return List.of(new NeonSpeakerPulse(firstTick, count, intervalTicks, "ENERGIZE_ZOMBIES"));
    }

    public record NeonSpeakerPulse(int firstTick, int count, int intervalTicks, String effectType) {
        public int activationTick(int index) {
            return firstTick + index * intervalTicks;
        }
    }
}

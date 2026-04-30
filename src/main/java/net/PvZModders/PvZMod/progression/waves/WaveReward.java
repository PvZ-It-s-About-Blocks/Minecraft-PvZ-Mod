package net.PvZModders.PvZMod.progression.waves;

public record WaveReward(
        WaveRewardType type,
        String id,
        String displayName,
        String iconItemId,
        String note
) {
}

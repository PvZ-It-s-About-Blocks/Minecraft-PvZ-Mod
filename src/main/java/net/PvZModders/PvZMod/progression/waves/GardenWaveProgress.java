package net.PvZModders.PvZMod.progression.waves;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class GardenWaveProgress {
    private int currentWave = 1;
    private boolean waveActive;
    private final Set<Integer> claimedRewardWaves = new HashSet<>();

    public int currentWave() {
        return currentWave;
    }

    public boolean waveActive() {
        return waveActive;
    }

    public Set<Integer> claimedRewardWaves() {
        return Collections.unmodifiableSet(claimedRewardWaves);
    }

    public void startWave() {
        waveActive = true;
    }

    public void completeCurrentWave() {
        waveActive = false;
        if (currentWave < OriginalGardenWaves.MAX_WAVE) {
            currentWave++;
        }
    }

    public void failCurrentWave() {
        waveActive = false;
    }

    public boolean isDefaultProgress() {
        return currentWave == 1 && !waveActive && claimedRewardWaves.isEmpty();
    }

    public void copyFrom(GardenWaveProgress other) {
        currentWave = other.currentWave;
        waveActive = other.waveActive;
        claimedRewardWaves.clear();
        claimedRewardWaves.addAll(other.claimedRewardWaves);
    }

    public boolean markRewardClaimed(int wave) {
        return claimedRewardWaves.add(wave);
    }

    public boolean isRewardClaimed(int wave) {
        return claimedRewardWaves.contains(wave);
    }

    public void load(CompoundTag tag) {
        currentWave = Math.max(1, Math.min(OriginalGardenWaves.MAX_WAVE, tag.getInt("CurrentWave")));
        waveActive = tag.getBoolean("WaveActive");
        claimedRewardWaves.clear();

        ListTag claimed = tag.getList("ClaimedRewardWaves", Tag.TAG_INT);
        for (int i = 0; i < claimed.size(); i++) {
            claimedRewardWaves.add(claimed.getInt(i));
        }
    }

    public void save(CompoundTag tag) {
        tag.putInt("CurrentWave", currentWave);
        tag.putBoolean("WaveActive", waveActive);

        ListTag claimed = new ListTag();
        for (int wave : claimedRewardWaves) {
            claimed.add(net.minecraft.nbt.IntTag.valueOf(wave));
        }
        tag.put("ClaimedRewardWaves", claimed);
    }
}

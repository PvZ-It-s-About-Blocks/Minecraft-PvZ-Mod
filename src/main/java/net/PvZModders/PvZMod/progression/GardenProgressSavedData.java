package net.PvZModders.PvZMod.progression;

import net.PvZModders.PvZMod.progression.waves.GardenWaveProgress;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.EnumMap;
import java.util.Map;

public class GardenProgressSavedData extends SavedData {
    private static final String DATA_NAME = "pvz2mod_garden_progress";
    private final Map<GardenId, GardenWaveProgress> waveProgress = new EnumMap<>(GardenId.class);

    public static GardenProgressSavedData get(ServerLevel level) {
        return level.getServer().overworld().getDataStorage().computeIfAbsent(GardenProgressSavedData::load, GardenProgressSavedData::new, DATA_NAME);
    }

    public static GardenProgressSavedData load(CompoundTag tag) {
        GardenProgressSavedData data = new GardenProgressSavedData();
        ListTag gardens = tag.getList("Gardens", Tag.TAG_COMPOUND);
        for (int i = 0; i < gardens.size(); i++) {
            CompoundTag gardenTag = gardens.getCompound(i);
            try {
                GardenId gardenId = GardenId.valueOf(gardenTag.getString("GardenId"));
                GardenWaveProgress progress = new GardenWaveProgress();
                progress.load(gardenTag.getCompound("WaveProgress"));
                data.waveProgress.put(gardenId, progress);
            } catch (IllegalArgumentException ignored) {
                // Ignore old or malformed progression entries.
            }
        }
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag gardens = new ListTag();
        for (Map.Entry<GardenId, GardenWaveProgress> entry : waveProgress.entrySet()) {
            CompoundTag gardenTag = new CompoundTag();
            CompoundTag progressTag = new CompoundTag();
            entry.getValue().save(progressTag);
            gardenTag.putString("GardenId", entry.getKey().name());
            gardenTag.put("WaveProgress", progressTag);
            gardens.add(gardenTag);
        }
        tag.put("Gardens", gardens);
        return tag;
    }

    public GardenWaveProgress getWaveProgress(GardenId gardenId) {
        return waveProgress.computeIfAbsent(gardenId, id -> new GardenWaveProgress());
    }

    public void adoptLegacyProgressIfUnset(GardenId gardenId, GardenWaveProgress legacyProgress) {
        GardenWaveProgress sharedProgress = getWaveProgress(gardenId);
        if (sharedProgress.isDefaultProgress() && !legacyProgress.isDefaultProgress()) {
            sharedProgress.copyFrom(legacyProgress);
            setDirty();
        }
    }
}

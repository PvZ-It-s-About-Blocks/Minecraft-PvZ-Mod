package net.PvZModders.PvZMod.progression.sun;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

public class SunSavedData extends SavedData {
    private static final String DATA_NAME = "pvz2mod_sun";
    private boolean sunUnlocked;

    public static SunSavedData get(ServerLevel level) {
        return level.getServer().overworld().getDataStorage().computeIfAbsent(SunSavedData::load, SunSavedData::new, DATA_NAME);
    }

    public static SunSavedData load(CompoundTag tag) {
        SunSavedData data = new SunSavedData();
        data.sunUnlocked = tag.getBoolean("SunUnlocked");
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        tag.putBoolean("SunUnlocked", sunUnlocked);
        return tag;
    }

    public boolean sunUnlocked() {
        return sunUnlocked;
    }

    public void unlockSun() {
        if (!sunUnlocked) {
            sunUnlocked = true;
            setDirty();
        }
    }
}

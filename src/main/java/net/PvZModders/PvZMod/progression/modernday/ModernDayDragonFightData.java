package net.PvZModders.PvZMod.progression.modernday;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

public class ModernDayDragonFightData extends SavedData {
    private static final String DATA_NAME = "pvz2mod_modern_day_dragon_fight";
    private boolean dragonFightUnlocked;

    public static ModernDayDragonFightData get(ServerLevel level) {
        return level.getServer().overworld().getDataStorage().computeIfAbsent(ModernDayDragonFightData::load, ModernDayDragonFightData::new, DATA_NAME);
    }

    public static ModernDayDragonFightData load(CompoundTag tag) {
        ModernDayDragonFightData data = new ModernDayDragonFightData();
        data.dragonFightUnlocked = tag.getBoolean("DragonFightUnlocked");
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        tag.putBoolean("DragonFightUnlocked", dragonFightUnlocked);
        return tag;
    }

    public boolean dragonFightUnlocked() {
        return dragonFightUnlocked;
    }

    public void unlockDragonFight() {
        if (!dragonFightUnlocked) {
            dragonFightUnlocked = true;
            setDirty();
        }
    }
}

package net.PvZModders.PvZMod.progression.shop;

import net.PvZModders.PvZMod.PvZ2Mod;
import net.PvZModders.PvZMod.progression.GardenId;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashSet;
import java.util.Set;

public class DaveShopSavedData extends SavedData {
    private static final String DATA_NAME = PvZ2Mod.MOD_ID + "_dave_shop";
    private final Set<String> purchasedEntries = new HashSet<>();
    private final Set<String> unlockedPlants = new HashSet<>();

    public static DaveShopSavedData get(ServerLevel level) {
        return level.getServer().overworld().getDataStorage().computeIfAbsent(
                DaveShopSavedData::load,
                DaveShopSavedData::new,
                DATA_NAME
        );
    }

    public static DaveShopSavedData load(CompoundTag tag) {
        DaveShopSavedData data = new DaveShopSavedData();
        readStrings(tag.getList("PurchasedEntries", Tag.TAG_STRING), data.purchasedEntries);
        readStrings(tag.getList("UnlockedPlants", Tag.TAG_STRING), data.unlockedPlants);
        return data;
    }

    public boolean isPurchased(GardenId gardenId, String entryId) {
        return purchasedEntries.contains(key(gardenId, entryId));
    }

    public void markPurchased(GardenId gardenId, String entryId) {
        if (purchasedEntries.add(key(gardenId, entryId))) {
            setDirty();
        }
    }

    public boolean isPlantUnlocked(GardenId gardenId, String plantId) {
        return unlockedPlants.contains(key(gardenId, plantId));
    }

    public void unlockPlant(GardenId gardenId, String plantId) {
        if (unlockedPlants.add(key(gardenId, plantId))) {
            setDirty();
        }
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        tag.put("PurchasedEntries", writeStrings(purchasedEntries));
        tag.put("UnlockedPlants", writeStrings(unlockedPlants));
        return tag;
    }

    private static String key(GardenId gardenId, String id) {
        return gardenId.name() + ":" + id;
    }

    private static void readStrings(ListTag list, Set<String> target) {
        for (int i = 0; i < list.size(); i++) {
            target.add(list.getString(i));
        }
    }

    private static ListTag writeStrings(Set<String> values) {
        ListTag list = new ListTag();
        values.stream().sorted().map(StringTag::valueOf).forEach(list::add);
        return list;
    }
}

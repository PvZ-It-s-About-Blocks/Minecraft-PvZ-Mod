package net.PvZModders.PvZMod.progression.plants;

import net.PvZModders.PvZMod.PvZ2Mod;
import net.PvZModders.PvZMod.progression.GardenId;
import net.PvZModders.PvZMod.progression.shop.DaveShopSavedData;
import net.PvZModders.PvZMod.progression.upgrades.PvZUpgradeSavedData;
import net.PvZModders.PvZMod.progression.upgrades.PvZUpgradeValues;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.List;

public class GardenPlantProductionSavedData extends SavedData {
    private static final String DATA_NAME = PvZ2Mod.MOD_ID + "_garden_plant_production";
    public static final int GARDEN_PACKET_CAP = PvZUpgradeValues.BASE_GARDEN_PACKET_CAP;

    private final CompoundTag plants = new CompoundTag();

    public static GardenPlantProductionSavedData get(ServerLevel level) {
        return level.getServer().overworld().getDataStorage().computeIfAbsent(
                GardenPlantProductionSavedData::load,
                GardenPlantProductionSavedData::new,
                DATA_NAME
        );
    }

    public static GardenPlantProductionSavedData load(CompoundTag tag) {
        GardenPlantProductionSavedData data = new GardenPlantProductionSavedData();
        data.plants.merge(tag.getCompound("Plants"));
        return data;
    }

    public void tick(ServerLevel level, GardenId gardenId, int currentWave, List<GardenPlantDefinition> definitions) {
        long gameTime = level.getGameTime();
        PvZUpgradeSavedData upgrades = PvZUpgradeSavedData.get(level);
        DaveShopSavedData shopData = DaveShopSavedData.get(level);
        int packetCap = PvZUpgradeValues.gardenPacketCap(upgrades);
        for (GardenPlantDefinition definition : definitions) {
            if (!definition.isUnlockedAtWave(currentWave) && !shopData.isPlantUnlocked(gardenId, definition.plantId())) {
                continue;
            }

            CompoundTag plantTag = plantTag(gardenId, definition.plantId());
            int count = plantTag.getInt("Count");
            if (count >= packetCap) {
                plantTag.putLong("NextReadyTick", 0L);
                continue;
            }

            long interval = PvZUpgradeValues.getSeedRefillTimeTicks(definition, upgrades);
            long nextReadyTick = plantTag.getLong("NextReadyTick");
            if (nextReadyTick <= 0L) {
                plantTag.putLong("NextReadyTick", gameTime + interval);
                setDirty();
                continue;
            }

            while (gameTime >= nextReadyTick && count < packetCap) {
                count++;
                nextReadyTick += interval;
                setDirty();
            }

            plantTag.putInt("Count", count);
            plantTag.putLong("NextReadyTick", count >= packetCap ? 0L : nextReadyTick);
        }
    }

    public int count(GardenId gardenId, String plantId) {
        return plantTag(gardenId, plantId).getInt("Count");
    }

    public int remainingSeconds(ServerLevel level, GardenId gardenId, GardenPlantDefinition definition) {
        CompoundTag plantTag = plantTag(gardenId, definition.plantId());
        PvZUpgradeSavedData upgrades = PvZUpgradeSavedData.get(level);
        if (plantTag.getInt("Count") >= PvZUpgradeValues.gardenPacketCap(upgrades)) {
            return 0;
        }

        long nextReadyTick = plantTag.getLong("NextReadyTick");
        if (nextReadyTick <= 0L) {
            return refillSeconds(level, definition);
        }
        return Math.max(0, (int) Math.ceil((nextReadyTick - level.getGameTime()) / 20.0D));
    }

    public int refillSeconds(ServerLevel level, GardenPlantDefinition definition) {
        return Math.max(1, (int) Math.ceil(PvZUpgradeValues.getSeedRefillTimeTicks(definition, PvZUpgradeSavedData.get(level)) / 20.0D));
    }

    public int packetCap(ServerLevel level) {
        return PvZUpgradeValues.gardenPacketCap(PvZUpgradeSavedData.get(level));
    }

    public boolean takePacket(GardenId gardenId, String plantId) {
        return takePackets(gardenId, plantId, 1) > 0;
    }

    public int takePackets(GardenId gardenId, String plantId, int amount) {
        CompoundTag plantTag = plantTag(gardenId, plantId);
        int count = plantTag.getInt("Count");
        int taken = Math.min(Math.max(0, amount), count);
        if (taken <= 0) {
            return 0;
        }
        plantTag.putInt("Count", count - taken);
        setDirty();
        return taken;
    }

    public void addPacket(GardenId gardenId, String plantId) {
        addPacket(gardenId, plantId, GARDEN_PACKET_CAP);
    }

    public void addPacket(ServerLevel level, GardenId gardenId, String plantId) {
        addPacket(gardenId, plantId, packetCap(level));
    }

    private void addPacket(GardenId gardenId, String plantId, int packetCap) {
        CompoundTag plantTag = plantTag(gardenId, plantId);
        plantTag.putInt("Count", Math.min(packetCap, plantTag.getInt("Count") + 1));
        setDirty();
    }

    private CompoundTag plantTag(GardenId gardenId, String plantId) {
        String key = gardenId.name() + ":" + plantId;
        if (!plants.contains(key)) {
            plants.put(key, new CompoundTag());
        }
        return plants.getCompound(key);
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        tag.put("Plants", plants.copy());
        return tag;
    }
}

package net.PvZModders.PvZMod.progression;

import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

public class GardenPortalSavedData extends SavedData {
    private static final String DATA_NAME = "pvz2mod_garden_portals";
    private final Map<GardenId, GlobalPos> portals = new EnumMap<>(GardenId.class);

    public static GardenPortalSavedData get(ServerLevel level) {
        return level.getServer().overworld().getDataStorage().computeIfAbsent(GardenPortalSavedData::load, GardenPortalSavedData::new, DATA_NAME);
    }

    public static GardenPortalSavedData load(CompoundTag tag) {
        GardenPortalSavedData data = new GardenPortalSavedData();
        ListTag portalTags = tag.getList("Portals", Tag.TAG_COMPOUND);
        for (int i = 0; i < portalTags.size(); i++) {
            CompoundTag portalTag = portalTags.getCompound(i);
            try {
                GardenId gardenId = GardenId.valueOf(portalTag.getString("GardenId"));
                ResourceKey<Level> dimension = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(portalTag.getString("Dimension")));
                BlockPos pos = new BlockPos(portalTag.getInt("X"), portalTag.getInt("Y"), portalTag.getInt("Z"));
                data.portals.put(gardenId, GlobalPos.of(dimension, pos));
            } catch (IllegalArgumentException ignored) {
                // Ignore old or malformed portal entries.
            }
        }
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag portalTags = new ListTag();
        for (Map.Entry<GardenId, GlobalPos> entry : portals.entrySet()) {
            CompoundTag portalTag = new CompoundTag();
            portalTag.putString("GardenId", entry.getKey().name());
            portalTag.putString("Dimension", entry.getValue().dimension().location().toString());
            portalTag.putInt("X", entry.getValue().pos().getX());
            portalTag.putInt("Y", entry.getValue().pos().getY());
            portalTag.putInt("Z", entry.getValue().pos().getZ());
            portalTags.add(portalTag);
        }
        tag.put("Portals", portalTags);
        return tag;
    }

    public Optional<GlobalPos> getPortal(GardenId gardenId) {
        return Optional.ofNullable(portals.get(gardenId));
    }

    public void setPortal(GardenId gardenId, ServerLevel level, BlockPos pos) {
        GlobalPos newPortal = GlobalPos.of(level.dimension(), pos);
        if (!newPortal.equals(portals.get(gardenId))) {
            portals.put(gardenId, newPortal);
            setDirty();
        }
    }

    public void removePortal(GardenId gardenId, GlobalPos pos) {
        if (pos.equals(portals.get(gardenId))) {
            portals.remove(gardenId);
            setDirty();
        }
    }

    public int discoveredMask() {
        int mask = 0;
        for (GardenPortalOption option : GardenPortalOption.values()) {
            if (portals.containsKey(option.gardenId())) {
                mask |= option.mask();
            }
        }
        return mask;
    }
}

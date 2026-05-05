package net.PvZModders.PvZMod.progression.farfuture;

import net.PvZModders.PvZMod.progression.seed.PlantEntityManager;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class FarFuturePowerTileManager {
    private static final Map<TileKey, PowerTileType> ACTIVE_TILES = new HashMap<>();
    private static final Map<TileKey, UUID> DISPLAY_IDS = new HashMap<>();

    private FarFuturePowerTileManager() {
    }

    public static void generatePowerTilesForWave(ServerLevel level, BlockPos totemPos, int wave) {
        clearPowerTiles(level, totemPos);
        for (FarFuturePowerTileSchedule.PowerTilePlacement placement : FarFuturePowerTileSchedule.placementsForWave(wave)) {
            BlockPos tilePos = totemPos.offset(placement.xOffset(), -1, placement.zOffset());
            addPowerTile(level, tilePos, placement.type());
        }
    }

    public static boolean addPowerTile(ServerLevel level, BlockPos tilePos, PowerTileType type) {
        TileKey key = TileKey.of(level, tilePos);
        ACTIVE_TILES.put(key, type);
        syncPowerTileOverlay(level, key, tilePos, type);
        return true;
    }

    public static boolean addPowerTile(ServerLevel level, BlockPos tilePos) {
        PowerTileType type = PowerTileType.values()[Math.floorMod(tilePos.getX() * 31 + tilePos.getZ(), PowerTileType.values().length)];
        return addPowerTile(level, tilePos, type);
    }

    public static void clearPowerTiles(ServerLevel level, BlockPos totemPos) {
        AABB gardenArea = new AABB(totemPos.offset(-7, -2, -7), totemPos.offset(7, 2, 7));
        List<TileKey> toClear = ACTIVE_TILES.keySet()
                .stream()
                .filter(key -> key.dimension.equals(level.dimension().location().toString()))
                .filter(key -> gardenArea.contains(key.x + 0.5D, key.y + 0.5D, key.z + 0.5D))
                .toList();
        for (TileKey key : toClear) {
            ACTIVE_TILES.remove(key);
            discardDisplay(level, DISPLAY_IDS.remove(key));
        }
    }

    public static Optional<PowerTileType> getPowerTileType(ServerLevel level, BlockPos tilePos) {
        return Optional.ofNullable(ACTIVE_TILES.get(TileKey.of(level, tilePos)));
    }

    public static float getDamageMultiplier(ServerLevel level, SnowGolem plant) {
        BlockPos tilePos = plant.blockPosition().below();
        PowerTileType type = ACTIVE_TILES.get(TileKey.of(level, tilePos));
        if (type == null) {
            return 1.0F;
        }

        String plantId = plant.getPersistentData().getString(PlantEntityManager.PLANT_ID_TAG);
        if (plantId.isEmpty()) {
            return 1.0F;
        }

        int matchingPlants = 0;
        AABB searchArea = new AABB(plant.blockPosition()).inflate(16.0D, 4.0D, 16.0D);
        for (SnowGolem otherPlant : level.getEntitiesOfClass(SnowGolem.class, searchArea, PlantEntityManager::isPlant)) {
            if (!plantId.equals(otherPlant.getPersistentData().getString(PlantEntityManager.PLANT_ID_TAG))) {
                continue;
            }
            if (ACTIVE_TILES.get(TileKey.of(level, otherPlant.blockPosition().below())) == type) {
                matchingPlants++;
            }
        }
        return 1.0F + Math.max(0, matchingPlants - 1) * 0.5F;
    }

    public static boolean isOnPowerTile(ServerLevel level, SnowGolem plant) {
        return ACTIVE_TILES.containsKey(TileKey.of(level, plant.blockPosition().below()));
    }

    private static void syncPowerTileOverlay(ServerLevel level, TileKey key, BlockPos tilePos, PowerTileType type) {
        UUID existingId = DISPLAY_IDS.get(key);
        Entity existing = existingId == null ? null : level.getEntity(existingId);
        if (existing != null) {
            return;
        }

        Display.BlockDisplay display = EntityType.BLOCK_DISPLAY.create(level);
        if (display == null) {
            return;
        }
        display.load(createBlockDisplayTag(type.displayBlock().defaultBlockState()));
        display.setNoGravity(true);
        display.setPos(tilePos.getX(), tilePos.getY() + 1.0D, tilePos.getZ());
        level.addFreshEntity(display);
        DISPLAY_IDS.put(key, display.getUUID());
    }

    private static void discardDisplay(ServerLevel level, UUID displayId) {
        if (displayId == null) {
            return;
        }
        Entity display = level.getEntity(displayId);
        if (display != null) {
            display.discard();
        }
    }

    private static CompoundTag createBlockDisplayTag(BlockState state) {
        CompoundTag tag = new CompoundTag();
        tag.put("block_state", NbtUtils.writeBlockState(state));

        CompoundTag transformation = new CompoundTag();
        transformation.put("translation", floatList(0.0F, 0.01F, 0.0F));
        transformation.put("scale", floatList(1.0F, 0.04F, 1.0F));
        transformation.put("left_rotation", floatList(0.0F, 0.0F, 0.0F, 1.0F));
        transformation.put("right_rotation", floatList(0.0F, 0.0F, 0.0F, 1.0F));
        tag.put("transformation", transformation);
        tag.putFloat("view_range", 64.0F);
        tag.putFloat("shadow_radius", 0.0F);
        tag.putFloat("shadow_strength", 0.0F);
        return tag;
    }

    private static ListTag floatList(float... values) {
        ListTag list = new ListTag();
        for (float value : values) {
            list.add(FloatTag.valueOf(value));
        }
        return list;
    }

    private record TileKey(String dimension, int x, int y, int z) {
        private static TileKey of(ServerLevel level, BlockPos pos) {
            return new TileKey(level.dimension().location().toString(), pos.getX(), pos.getY(), pos.getZ());
        }
    }
}

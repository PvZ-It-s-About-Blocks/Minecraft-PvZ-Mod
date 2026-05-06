package net.PvZModders.PvZMod.progression.beach;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class BigWaveBeachTideManager {
    private static final Map<TileKey, UUID> FLOOD_OVERLAYS = new HashMap<>();

    private BigWaveBeachTideManager() {
    }

    public static TideTileType tileType(BlockPos totemPos, BlockPos tilePos) {
        int dx = tilePos.getX() - totemPos.getX();
        int dz = tilePos.getZ() - totemPos.getZ();
        if (Math.abs(dx) > 7 || Math.abs(dz) > 7) {
            return TideTileType.ALWAYS_LAND;
        }
        if (dz <= -5 || dz >= 6) {
            return TideTileType.ALWAYS_WATER;
        }
        if (dz >= -2 && dz <= 2) {
            return TideTileType.TIDE_TILE;
        }
        return TideTileType.ALWAYS_LAND;
    }

    public static void tickTide(ServerLevel level, BlockPos totemPos, int wave, long elapsedTicks, boolean announce) {
        boolean highTide = BigWaveBeachTideSchedule.isHighTideActive(wave, elapsedTicks);
        if (highTide) {
            showHighTide(level, totemPos);
            if (announce && level.getGameTime() % 60L == 0L) {
                level.players().forEach(player -> {
                    if (player.distanceToSqr(totemPos.getX() + 0.5D, totemPos.getY() + 0.5D, totemPos.getZ() + 0.5D) <= 4096.0D) {
                        player.displayClientMessage(net.minecraft.network.chat.Component.literal("The tide is coming in!").withStyle(net.minecraft.ChatFormatting.AQUA), true);
                    }
                });
            }
        } else {
            clearTide(level, totemPos);
        }
    }

    public static boolean isTileFlooded(ServerLevel level, BlockPos pos) {
        if (level.getBlockState(pos).is(Blocks.WATER)) {
            return true;
        }
        return FLOOD_OVERLAYS.containsKey(TileKey.of(level, pos));
    }

    public static void clearTide(ServerLevel level, BlockPos totemPos) {
        AABB gardenArea = new AABB(totemPos.offset(-7, -1, -7), totemPos.offset(7, 2, 7));
        List<TileKey> toClear = FLOOD_OVERLAYS.keySet().stream()
                .filter(key -> key.dimension.equals(level.dimension().location().toString()))
                .filter(key -> gardenArea.contains(key.x + 0.5D, key.y + 0.5D, key.z + 0.5D))
                .toList();
        for (TileKey key : toClear) {
            discardDisplay(level, FLOOD_OVERLAYS.remove(key));
        }
    }

    private static void showHighTide(ServerLevel level, BlockPos totemPos) {
        for (int x = -7; x <= 7; x++) {
            for (int z = -7; z <= 7; z++) {
                BlockPos tilePos = totemPos.offset(x, 0, z);
                TideTileType type = tileType(totemPos, tilePos);
                if (type == TideTileType.TIDE_TILE || type == TideTileType.ALWAYS_WATER) {
                    syncFloodOverlay(level, tilePos);
                }
            }
        }
    }

    private static void syncFloodOverlay(ServerLevel level, BlockPos tilePos) {
        TileKey key = TileKey.of(level, tilePos);
        UUID existingId = FLOOD_OVERLAYS.get(key);
        Entity existing = existingId == null ? null : level.getEntity(existingId);
        if (existing != null) {
            return;
        }
        Display.BlockDisplay display = EntityType.BLOCK_DISPLAY.create(level);
        if (display == null) {
            return;
        }
        display.load(createWaterOverlayTag(Blocks.WATER.defaultBlockState()));
        display.setNoGravity(true);
        display.setPos(tilePos.getX(), tilePos.getY(), tilePos.getZ());
        level.addFreshEntity(display);
        FLOOD_OVERLAYS.put(key, display.getUUID());
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

    private static CompoundTag createWaterOverlayTag(BlockState state) {
        CompoundTag tag = new CompoundTag();
        tag.put("block_state", NbtUtils.writeBlockState(state));
        CompoundTag transformation = new CompoundTag();
        transformation.put("translation", floatList(0.0F, 0.02F, 0.0F));
        transformation.put("scale", floatList(1.0F, 0.08F, 1.0F));
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

package net.PvZModders.PvZMod.progression.pirate;

import net.PvZModders.PvZMod.block.ModBlocks;
import net.PvZModders.PvZMod.block.entity.GardenTotemBlockEntity;
import net.PvZModders.PvZMod.progression.GardenId;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public final class PirateSeasPlankManager {
    private static final int RADIUS = 7;
    private static final Map<String, Map<BlockPos, BlockState>> ORIGINAL_TEMP_STATES = new HashMap<>();
    private static final Map<String, Set<BlockPos>> TEMP_PLANKS = new HashMap<>();

    private PirateSeasPlankManager() {
    }

    public static void createPermanentTotemPlatform(ServerLevel level, BlockPos totemPos) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                BlockPos plankPos = totemPos.offset(dx, -1, dz);
                BlockState current = level.getBlockState(plankPos);
                if (current.is(Blocks.WATER) || current.isAir()) {
                    level.setBlock(plankPos, Blocks.OAK_PLANKS.defaultBlockState(), 3);
                }
            }
        }
    }

    public static void generatePlanksForWave(ServerLevel level, BlockPos totemPos, int wave) {
        clearWavePlanks(level, totemPos);
        for (int[] offset : patternFor(wave)) {
            BlockPos plankPos = totemPos.offset(offset[0], -1, offset[1]);
            if (Math.abs(offset[0]) <= 1 && Math.abs(offset[1]) <= 1) {
                continue;
            }

            BlockState current = level.getBlockState(plankPos);
            if (!current.is(Blocks.WATER) && !current.isAir()) {
                continue;
            }

            keyStates(level, totemPos).putIfAbsent(plankPos.immutable(), current);
            keyPlanks(level, totemPos).add(plankPos.immutable());
            level.setBlock(plankPos, Blocks.OAK_PLANKS.defaultBlockState(), 3);
        }
    }

    public static void clearWavePlanks(ServerLevel level, BlockPos totemPos) {
        String key = key(level, totemPos);
        Map<BlockPos, BlockState> states = ORIGINAL_TEMP_STATES.remove(key);
        if (states == null) {
            TEMP_PLANKS.remove(key);
            return;
        }

        for (Map.Entry<BlockPos, BlockState> entry : states.entrySet()) {
            if (level.getBlockState(entry.getKey()).is(Blocks.OAK_PLANKS)) {
                level.setBlock(entry.getKey(), entry.getValue(), 3);
            }
        }
        TEMP_PLANKS.remove(key);
    }

    public static boolean isPirateSeasPlankTile(ServerLevel level, BlockPos pos) {
        Optional<BlockPos> totem = nearestPirateTotem(level, pos);
        if (totem.isEmpty()) {
            return false;
        }
        BlockPos floor = floorPosNear(pos, totem.get());
        return Math.abs(floor.getX() - totem.get().getX()) <= RADIUS
                && Math.abs(floor.getZ() - totem.get().getZ()) <= RADIUS
                && level.getBlockState(floor).is(Blocks.OAK_PLANKS);
    }

    public static boolean isChurningWaterHole(ServerLevel level, BlockPos pos) {
        Optional<BlockPos> totem = nearestPirateTotem(level, pos);
        if (totem.isEmpty()) {
            return false;
        }
        BlockPos floor = floorPosNear(pos, totem.get());
        if (Math.abs(floor.getX() - totem.get().getX()) > RADIUS || Math.abs(floor.getZ() - totem.get().getZ()) > RADIUS) {
            return false;
        }
        return !level.getBlockState(floor).is(Blocks.OAK_PLANKS);
    }

    private static BlockPos floorPosNear(BlockPos pos, BlockPos totem) {
        if (pos.getY() == totem.getY() - 1) {
            return pos;
        }
        if (pos.getY() == totem.getY()) {
            return pos.below();
        }
        return new BlockPos(pos.getX(), totem.getY() - 1, pos.getZ());
    }

    private static Optional<BlockPos> nearestPirateTotem(ServerLevel level, BlockPos pos) {
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        for (int dx = -RADIUS; dx <= RADIUS; dx++) {
            for (int dz = -RADIUS; dz <= RADIUS; dz++) {
                for (int dy = -2; dy <= 2; dy++) {
                    mutable.set(pos.getX() + dx, pos.getY() + dy, pos.getZ() + dz);
                    if (!level.getBlockState(mutable).is(ModBlocks.GARDEN_TOTEM.get())) {
                        continue;
                    }
                    if (level.getBlockEntity(mutable) instanceof GardenTotemBlockEntity totem && totem.getGardenId() == GardenId.PIRATE_SEAS) {
                        return Optional.of(mutable.immutable());
                    }
                }
            }
        }
        return Optional.empty();
    }

    private static List<int[]> patternFor(int wave) {
        if (wave >= 30) {
            return List.of(new int[]{0, -6}, new int[]{0, -5}, new int[]{-2, -4}, new int[]{0, -4}, new int[]{2, -4}, new int[]{-4, -2}, new int[]{-2, -2}, new int[]{0, -2}, new int[]{2, -2}, new int[]{4, -2}, new int[]{-5, 0}, new int[]{-3, 0}, new int[]{3, 0}, new int[]{5, 0}, new int[]{-4, 2}, new int[]{-2, 2}, new int[]{0, 2}, new int[]{2, 2}, new int[]{4, 2}, new int[]{-2, 4}, new int[]{0, 4}, new int[]{2, 4}, new int[]{0, 5}, new int[]{0, 6});
        }
        if (wave >= 25) {
            return List.of(new int[]{0, -6}, new int[]{0, -5}, new int[]{-2, -4}, new int[]{2, -4}, new int[]{-4, -2}, new int[]{0, -2}, new int[]{4, -2}, new int[]{-5, 0}, new int[]{-3, 0}, new int[]{3, 0}, new int[]{5, 0}, new int[]{-4, 2}, new int[]{0, 2}, new int[]{4, 2}, new int[]{-2, 4}, new int[]{2, 4}, new int[]{0, 6});
        }
        if (wave >= 20) {
            return List.of(new int[]{0, -6}, new int[]{0, -5}, new int[]{-2, -4}, new int[]{2, -4}, new int[]{-3, -2}, new int[]{0, -2}, new int[]{3, -2}, new int[]{-4, 0}, new int[]{4, 0}, new int[]{-3, 2}, new int[]{0, 2}, new int[]{3, 2}, new int[]{-2, 4}, new int[]{2, 4});
        }
        if (wave >= 15) {
            return List.of(new int[]{-3, -4}, new int[]{-2, -4}, new int[]{2, -4}, new int[]{3, -4}, new int[]{-4, -2}, new int[]{0, -2}, new int[]{4, -2}, new int[]{-4, 0}, new int[]{4, 0}, new int[]{-4, 2}, new int[]{0, 2}, new int[]{4, 2}, new int[]{-3, 4}, new int[]{3, 4});
        }
        if (wave >= 10) {
            return List.of(new int[]{0, -5}, new int[]{0, -4}, new int[]{-2, -3}, new int[]{2, -3}, new int[]{-2, -2}, new int[]{2, -2}, new int[]{-4, 0}, new int[]{0, 0}, new int[]{4, 0}, new int[]{-2, 2}, new int[]{2, 2});
        }
        if (wave >= 6) {
            return List.of(new int[]{-3, -4}, new int[]{-3, -3}, new int[]{-3, -2}, new int[]{3, -4}, new int[]{3, -3}, new int[]{3, -2}, new int[]{-3, 0}, new int[]{3, 0}, new int[]{-3, 2}, new int[]{3, 2});
        }
        if (wave >= 3) {
            return List.of(new int[]{-2, -4}, new int[]{-1, -4}, new int[]{0, -4}, new int[]{1, -4}, new int[]{2, -4}, new int[]{0, -3}, new int[]{0, -2}, new int[]{-3, 0}, new int[]{3, 0});
        }
        return List.of(new int[]{0, -4}, new int[]{0, -3}, new int[]{0, -2}, new int[]{-1, -2}, new int[]{1, -2});
    }

    private static Map<BlockPos, BlockState> keyStates(ServerLevel level, BlockPos totemPos) {
        return ORIGINAL_TEMP_STATES.computeIfAbsent(key(level, totemPos), ignored -> new HashMap<>());
    }

    private static Set<BlockPos> keyPlanks(ServerLevel level, BlockPos totemPos) {
        return TEMP_PLANKS.computeIfAbsent(key(level, totemPos), ignored -> new HashSet<>());
    }

    private static String key(ServerLevel level, BlockPos totemPos) {
        return level.dimension().location() + ":" + totemPos.asLong();
    }
}

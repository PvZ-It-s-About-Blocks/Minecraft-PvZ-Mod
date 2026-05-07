package net.PvZModders.PvZMod.progression.waves;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public final class AncientEgyptTombManager {
    private static final int GARDEN_RADIUS = 7;
    private static final int MAX_TOMBS_PER_GARDEN = 8;
    private static final Map<BlockPos, BlockState> ORIGINAL_STATES = new HashMap<>();

    private AncientEgyptTombManager() {
    }

    public static boolean tryRaiseTomb(ServerLevel level, BlockPos gardenCenter, BlockPos sourcePos, int alreadyRaisedByZombie) {
        if (alreadyRaisedByZombie >= 3 || activeTombsNear(gardenCenter) >= MAX_TOMBS_PER_GARDEN) {
            return false;
        }

        for (int attempt = 0; attempt < 12; attempt++) {
            int xOffset = level.random.nextInt(GARDEN_RADIUS * 2 + 1) - GARDEN_RADIUS;
            int zOffset = level.random.nextInt(GARDEN_RADIUS * 2 + 1) - GARDEN_RADIUS;
            BlockPos ground = level.getHeightmapPos(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, gardenCenter.offset(xOffset, 0, zOffset)).below();
            if (!isValidTombSpot(level, ground, gardenCenter, sourcePos)) {
                continue;
            }

            ORIGINAL_STATES.putIfAbsent(ground, level.getBlockState(ground));
            level.setBlock(ground, Blocks.SOUL_SAND.defaultBlockState(), 3);
            level.playSound(null, ground, SoundEvents.SOUL_SAND_PLACE, SoundSource.BLOCKS, 0.7F, 0.8F);
            return true;
        }
        return false;
    }

    public static void clearTombs(ServerLevel level, BlockPos gardenCenter) {
        Iterator<Map.Entry<BlockPos, BlockState>> iterator = ORIGINAL_STATES.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<BlockPos, BlockState> entry = iterator.next();
            if (isInsideGarden(entry.getKey(), gardenCenter)) {
                level.setBlock(entry.getKey(), entry.getValue(), 3);
                iterator.remove();
            }
        }
    }

    private static int activeTombsNear(BlockPos gardenCenter) {
        int count = 0;
        for (BlockPos pos : ORIGINAL_STATES.keySet()) {
            if (isInsideGarden(pos, gardenCenter)) {
                count++;
            }
        }
        return count;
    }

    private static boolean isValidTombSpot(ServerLevel level, BlockPos ground, BlockPos gardenCenter, BlockPos sourcePos) {
        return isInsideGarden(ground, gardenCenter)
                && ground.distSqr(sourcePos) >= 9.0D
                && !ORIGINAL_STATES.containsKey(ground)
                && level.getBlockState(ground).isFaceSturdy(level, ground, net.minecraft.core.Direction.UP)
                && level.getBlockState(ground.above()).getCollisionShape(level, ground.above()).isEmpty()
                && level.getBlockState(ground.above(2)).getCollisionShape(level, ground.above(2)).isEmpty();
    }

    private static boolean isInsideGarden(BlockPos pos, BlockPos gardenCenter) {
        return Math.abs(pos.getX() - gardenCenter.getX()) <= GARDEN_RADIUS
                && Math.abs(pos.getZ() - gardenCenter.getZ()) <= GARDEN_RADIUS;
    }
}

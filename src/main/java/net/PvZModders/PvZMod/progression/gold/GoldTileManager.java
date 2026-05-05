package net.PvZModders.PvZMod.progression.gold;

import net.PvZModders.PvZMod.block.ModBlocks;
import net.PvZModders.PvZMod.block.entity.GardenTotemBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;

public final class GoldTileManager {
    private GoldTileManager() {
    }

    public static boolean addGoldTileNear(ServerLevel level, BlockPos tilePos) {
        return findTotemFor(level, tilePos) instanceof GardenTotemBlockEntity totem
                && totem.addGoldTile(level, tilePos);
    }

    public static boolean isGoldTile(ServerLevel level, BlockPos tilePos) {
        return findTotemFor(level, tilePos) instanceof GardenTotemBlockEntity totem
                && totem.isGoldTile(tilePos);
    }

    private static BlockEntity findTotemFor(ServerLevel level, BlockPos tilePos) {
        for (int x = -7; x <= 7; x++) {
            for (int z = -7; z <= 7; z++) {
                for (int y = -1; y <= 4; y++) {
                    BlockPos candidate = tilePos.offset(x, y, z);
                    if (level.getBlockState(candidate).is(ModBlocks.GARDEN_TOTEM.get())
                            && level.getBlockEntity(candidate) instanceof GardenTotemBlockEntity totem) {
                        return totem;
                    }
                }
            }
        }
        return null;
    }
}

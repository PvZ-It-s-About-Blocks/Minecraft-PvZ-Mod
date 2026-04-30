package net.PvZModders.PvZMod.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3f;

public class GardenPlotterBlockEntity extends BlockEntity {
    private static final int WIDTH = 5;
    private static final int LENGTH = 12;

    public GardenPlotterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.GARDEN_PLOTTER_BE.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, GardenPlotterBlockEntity be) {
        if (!(level instanceof ServerLevel serverLevel) || serverLevel.getGameTime() % 10 != 0) {
            return;
        }
        be.showValidationParticles(serverLevel, pos);
    }

    private void showValidationParticles(ServerLevel level, BlockPos origin) {
        int startX = origin.getX() - (WIDTH / 2);
        int startZ = origin.getZ() + 1;
        int y = origin.getY();

        for (int dx = 0; dx < WIDTH; dx++) {
            for (int dz = 0; dz < LENGTH; dz++) {
                BlockPos floorPos = new BlockPos(startX + dx, y - 1, startZ + dz);
                BlockPos airPos = floorPos.above();

                boolean validFloor = level.getBlockState(floorPos).is(BlockTags.DIRT) || level.getBlockState(floorPos).is(BlockTags.SAND);
                boolean validAir = level.getBlockState(airPos).isAir();
                boolean valid = validFloor && validAir;

                Vector3f color = valid ? new Vector3f(0.2F, 1.0F, 0.2F) : new Vector3f(1.0F, 0.2F, 0.2F);
                DustParticleOptions dust = new DustParticleOptions(color, 1.0F);

                level.sendParticles(dust,
                        airPos.getX() + 0.5D,
                        airPos.getY() + 0.15D,
                        airPos.getZ() + 0.5D,
                        1,
                        0.0D,
                        0.0D,
                        0.0D,
                        0.0D);
            }
        }
    }
}

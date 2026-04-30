package net.PvZModders.PvZMod.progression.waves;

import net.minecraft.core.BlockPos;

public enum WaveSpawnDirection {
    NORTH(0, -1, "North"),
    SOUTH(0, 1, "South"),
    EAST(1, 0, "East"),
    WEST(-1, 0, "West");

    private final int xStep;
    private final int zStep;
    private final String displayName;

    WaveSpawnDirection(int xStep, int zStep, String displayName) {
        this.xStep = xStep;
        this.zStep = zStep;
        this.displayName = displayName;
    }

    public BlockPos offsetFrom(BlockPos origin, int distance, int sideOffset) {
        if (xStep != 0) {
            return origin.offset(xStep * distance, 0, sideOffset);
        }
        return origin.offset(sideOffset, 0, zStep * distance);
    }

    public String displayName() {
        return displayName;
    }
}

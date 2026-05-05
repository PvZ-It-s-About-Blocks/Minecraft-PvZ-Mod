package net.PvZModders.PvZMod.progression.farfuture;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public enum PowerTileType {
    RED(Blocks.RED_TERRACOTTA),
    BLUE(Blocks.BLUE_TERRACOTTA),
    YELLOW(Blocks.YELLOW_TERRACOTTA),
    GREEN(Blocks.GREEN_TERRACOTTA),
    PURPLE(Blocks.PURPLE_TERRACOTTA);

    private final Block displayBlock;

    PowerTileType(Block displayBlock) {
        this.displayBlock = displayBlock;
    }

    public Block displayBlock() {
        return displayBlock;
    }
}

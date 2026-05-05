package net.PvZModders.PvZMod.progression.farfuture;

import java.util.List;

public final class FarFuturePowerTileSchedule {
    private FarFuturePowerTileSchedule() {
    }

    public static List<PowerTilePlacement> placementsForWave(int wave) {
        if (wave < 3) {
            return List.of();
        }
        if (wave < 6) {
            return List.of(
                    new PowerTilePlacement(-2, -2, PowerTileType.BLUE),
                    new PowerTilePlacement(2, -2, PowerTileType.BLUE)
            );
        }
        if (wave < 10) {
            return List.of(
                    new PowerTilePlacement(-3, -2, PowerTileType.BLUE),
                    new PowerTilePlacement(0, 0, PowerTileType.RED),
                    new PowerTilePlacement(3, 2, PowerTileType.BLUE)
            );
        }
        if (wave < 15) {
            return List.of(
                    new PowerTilePlacement(-4, -3, PowerTileType.YELLOW),
                    new PowerTilePlacement(-1, -1, PowerTileType.BLUE),
                    new PowerTilePlacement(2, -1, PowerTileType.BLUE),
                    new PowerTilePlacement(4, 3, PowerTileType.GREEN)
            );
        }
        if (wave < 20) {
            return List.of(
                    new PowerTilePlacement(-5, -3, PowerTileType.RED),
                    new PowerTilePlacement(-2, -1, PowerTileType.RED),
                    new PowerTilePlacement(1, 1, PowerTileType.GREEN),
                    new PowerTilePlacement(3, 2, PowerTileType.GREEN),
                    new PowerTilePlacement(5, -2, PowerTileType.PURPLE)
            );
        }
        if (wave < 25) {
            return List.of(
                    new PowerTilePlacement(-5, -4, PowerTileType.BLUE),
                    new PowerTilePlacement(-3, 0, PowerTileType.YELLOW),
                    new PowerTilePlacement(-1, 3, PowerTileType.YELLOW),
                    new PowerTilePlacement(1, -3, PowerTileType.PURPLE),
                    new PowerTilePlacement(3, 0, PowerTileType.PURPLE),
                    new PowerTilePlacement(5, 4, PowerTileType.GREEN)
            );
        }
        if (wave < 30) {
            return List.of(
                    new PowerTilePlacement(-6, -4, PowerTileType.RED),
                    new PowerTilePlacement(-4, 1, PowerTileType.RED),
                    new PowerTilePlacement(-2, -2, PowerTileType.BLUE),
                    new PowerTilePlacement(0, 3, PowerTileType.BLUE),
                    new PowerTilePlacement(2, -4, PowerTileType.YELLOW),
                    new PowerTilePlacement(4, 1, PowerTileType.YELLOW),
                    new PowerTilePlacement(6, 4, PowerTileType.GREEN)
            );
        }
        return List.of(
                new PowerTilePlacement(-6, -5, PowerTileType.RED),
                new PowerTilePlacement(-4, -1, PowerTileType.RED),
                new PowerTilePlacement(-2, 3, PowerTileType.BLUE),
                new PowerTilePlacement(0, -3, PowerTileType.BLUE),
                new PowerTilePlacement(2, 1, PowerTileType.YELLOW),
                new PowerTilePlacement(4, 5, PowerTileType.YELLOW),
                new PowerTilePlacement(5, -2, PowerTileType.PURPLE),
                new PowerTilePlacement(6, 3, PowerTileType.PURPLE)
        );
    }

    public record PowerTilePlacement(int xOffset, int zOffset, PowerTileType type) {
    }
}

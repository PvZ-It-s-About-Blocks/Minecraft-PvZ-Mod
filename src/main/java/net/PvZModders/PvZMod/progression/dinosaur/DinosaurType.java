package net.PvZModders.PvZMod.progression.dinosaur;

import net.minecraft.util.RandomSource;

public enum DinosaurType {
    RAPTOR("Raptor", 5, 20 * 5),
    STEGOSAURUS("Stegosaurus", 3, 20 * 10),
    PTEROSAUR("Pterosaur", 3, 20 * 10),
    T_REX("T. Rex", 5, 20 * 8),
    ANKYLOSAURUS("Ankylosaurus", 5, 20 * 10);

    private final String displayName;
    private final int actionLimit;
    private final int actionCooldownTicks;

    DinosaurType(String displayName, int actionLimit, int actionCooldownTicks) {
        this.displayName = displayName;
        this.actionLimit = actionLimit;
        this.actionCooldownTicks = actionCooldownTicks;
    }

    public String displayName() {
        return displayName;
    }

    public int actionLimit() {
        return actionLimit;
    }

    public int actionCooldownTicks() {
        return actionCooldownTicks;
    }

    public static DinosaurType byName(String name) {
        for (DinosaurType type : values()) {
            if (type.name().equals(name)) {
                return type;
            }
        }
        return RAPTOR;
    }

    public static DinosaurType passive(RandomSource random) {
        return random.nextInt(10) < 7 ? RAPTOR : values()[random.nextInt(Math.min(3, values().length))];
    }

    public static DinosaurType forWave(int wave, RandomSource random) {
        return switch (wave) {
            case 5, 6, 7 -> RAPTOR;
            case 8, 9, 10 -> STEGOSAURUS;
            case 11, 12, 13, 14 -> PTEROSAUR;
            case 15, 16, 17, 18, 19 -> T_REX;
            case 20, 21, 22, 23, 24 -> ANKYLOSAURUS;
            default -> scheduledLateWaveType(wave);
        };
    }

    private static DinosaurType scheduledLateWaveType(int wave) {
        if (wave < 5) {
            return RAPTOR;
        }
        if (wave >= 25) {
            return switch (Math.floorMod(wave, 5)) {
                case 0 -> T_REX;
                case 1 -> RAPTOR;
                case 2 -> STEGOSAURUS;
                case 3 -> PTEROSAUR;
                default -> ANKYLOSAURUS;
            };
        }
        return RAPTOR;
    }
}

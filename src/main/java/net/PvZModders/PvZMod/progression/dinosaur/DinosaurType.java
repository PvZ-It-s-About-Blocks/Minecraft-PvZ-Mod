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
        if (wave >= 25) {
            return values()[random.nextInt(values().length)];
        }
        if (wave >= 18) {
            return switch (random.nextInt(4)) {
                case 0 -> RAPTOR;
                case 1 -> STEGOSAURUS;
                case 2 -> PTEROSAUR;
                default -> ANKYLOSAURUS;
            };
        }
        if (wave >= 10) {
            return switch (random.nextInt(3)) {
                case 0 -> RAPTOR;
                case 1 -> STEGOSAURUS;
                default -> PTEROSAUR;
            };
        }
        return RAPTOR;
    }
}

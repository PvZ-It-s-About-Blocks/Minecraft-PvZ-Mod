package net.PvZModders.PvZMod.progression.targeting;

public enum TargetingPriority {
    FIRST,
    LAST,
    STRONG,
    WEAK,
    RANDOM;

    public TargetingPriority next() {
        TargetingPriority[] values = values();
        return values[(ordinal() + 1) % values.length];
    }
}

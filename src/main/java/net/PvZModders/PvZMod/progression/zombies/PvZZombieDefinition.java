package net.PvZModders.PvZMod.progression.zombies;

import net.PvZModders.PvZMod.progression.GardenId;

import java.util.Set;

public record PvZZombieDefinition(
        String id,
        String displayName,
        GardenId gardenSource,
        double maxHealth,
        double movementSpeedMultiplier,
        double attackDamage,
        double knockbackResistance,
        float visualScale,
        String modelKey,
        String almanacText,
        Set<PvZZombieSpecial> specials
) {
    public boolean has(PvZZombieSpecial special) {
        return specials.contains(special);
    }
}

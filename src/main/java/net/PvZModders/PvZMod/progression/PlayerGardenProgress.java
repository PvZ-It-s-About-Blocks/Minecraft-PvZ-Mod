package net.PvZModders.PvZMod.progression;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public class PlayerGardenProgress {
    private final EnumSet<GardenId> unlockedGardens = EnumSet.noneOf(GardenId.class);

    public boolean isUnlocked(GardenId id) {
        return unlockedGardens.contains(id);
    }

    public boolean unlock(GardenId id) {
        return unlockedGardens.add(id);
    }

    public Set<GardenId> getUnlockedGardens() {
        return Collections.unmodifiableSet(unlockedGardens);
    }
}

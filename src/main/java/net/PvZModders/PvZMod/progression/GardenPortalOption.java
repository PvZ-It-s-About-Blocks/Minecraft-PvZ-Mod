package net.PvZModders.PvZMod.progression;

import java.util.Optional;

public enum GardenPortalOption {
    ORIGINAL(GardenId.INITIAL_PLAINS, "Original Garden", 0x2F9F3F),
    DESERT(GardenId.DESERT, "Ancient Egypt Garden", 0xE6C84A),
    PIRATE_SEAS(GardenId.PIRATE_SEAS, "Pirate Seas Garden", 0x2AB7D6),
    WILD_WEST(GardenId.WILD_WEST, "Wild West Garden", 0xD87925),
    FROSTBITE(GardenId.FROSTBITE, "Frostbite Garden", 0x8FE8FF),
    LOST_CITY(GardenId.LOST_CITY, "Lost City Garden", 0x237C2F),
    FAR_FUTURE(GardenId.FAR_FUTURE, "Far Future Garden", 0x76D7FF),
    DARK_AGES(GardenId.DARK_AGES, "Dark Ages Garden", 0x59407A),
    NEON_MIXTAPE(GardenId.NEON_MIXTAPE, "Neon Mixtape Garden", 0xD44CFF),
    JURASSIC_MARSH(GardenId.JURASSIC_MARSH, "Jurassic Marsh Garden", 0x6B8F3A),
    BIG_WAVE_BEACH(GardenId.BIG_WAVE_BEACH, "Big Wave Beach Garden", 0x2877CC),
    MODERN_DAY(GardenId.MODERN_DAY, "Modern Day Garden", 0x7DE05B),
    GREENHOUSE(GardenId.GREENHOUSE, "Greenhouse Garden", 0xFFFFFF);

    private final GardenId gardenId;
    private final String displayName;
    private final int color;

    GardenPortalOption(GardenId gardenId, String displayName, int color) {
        this.gardenId = gardenId;
        this.displayName = displayName;
        this.color = color;
    }

    public GardenId gardenId() {
        return gardenId;
    }

    public String displayName() {
        return displayName;
    }

    public int color() {
        return color;
    }

    public int mask() {
        return 1 << ordinal();
    }

    public static Optional<GardenPortalOption> byIndex(int index) {
        GardenPortalOption[] options = values();
        return index >= 0 && index < options.length ? Optional.of(options[index]) : Optional.empty();
    }

    public static int indexOf(GardenId gardenId) {
        for (GardenPortalOption option : values()) {
            if (option.gardenId == gardenId) {
                return option.ordinal();
            }
        }
        return 0;
    }
}

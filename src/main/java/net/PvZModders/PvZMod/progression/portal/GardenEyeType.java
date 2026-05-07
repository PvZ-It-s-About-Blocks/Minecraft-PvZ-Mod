package net.PvZModders.PvZMod.progression.portal;

import net.PvZModders.PvZMod.progression.GardenId;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public enum GardenEyeType {
    ORIGINAL(GardenId.INITIAL_PLAINS, "original_garden", "Original Garden", "grass_block"),
    ANCIENT_EGYPT(GardenId.DESERT, "ancient_egypt", "Ancient Egypt", "sand"),
    PIRATE_SEAS(GardenId.PIRATE_SEAS, "pirate_seas", "Pirate Seas", "oak_planks"),
    WILD_WEST(GardenId.WILD_WEST, "wild_west", "Wild West", "rail"),
    FROSTBITE(GardenId.FROSTBITE, "frostbite", "Frostbite Garden", "ice"),
    LOST_CITY(GardenId.LOST_CITY, "lost_city", "Lost City", "mossy_stone_bricks"),
    FAR_FUTURE(GardenId.FAR_FUTURE, "far_future", "Far Future", "purple_glazed_terracotta"),
    DARK_AGES(GardenId.DARK_AGES, "dark_ages", "Dark Ages", "dark_oak_planks"),
    NEON_MIXTAPE(GardenId.NEON_MIXTAPE, "neon_mixtape", "Neon Mixtape", "note_block"),
    JURASSIC_MARSH(GardenId.JURASSIC_MARSH, "jurassic_marsh", "Jurassic Marsh", "bone_block"),
    BIG_WAVE_BEACH(GardenId.BIG_WAVE_BEACH, "big_wave_beach", "Big Wave Beach", "prismarine"),
    GREENHOUSE(GardenId.GREENHOUSE, "greenhouse", "Greenhouse Garden", "glass");

    public static final List<GardenEyeType> REQUIRED = List.of(values());

    private final GardenId gardenId;
    private final String idPrefix;
    private final String gardenName;
    private final String placeholderTexture;

    GardenEyeType(GardenId gardenId, String idPrefix, String gardenName, String placeholderTexture) {
        this.gardenId = gardenId;
        this.idPrefix = idPrefix;
        this.gardenName = gardenName;
        this.placeholderTexture = placeholderTexture;
    }

    public GardenId gardenId() {
        return gardenId;
    }

    public String idPrefix() {
        return idPrefix;
    }

    public String eyeId() {
        return idPrefix + "_eye";
    }

    public String frameId() {
        return idPrefix + "_portal_frame";
    }

    public String gardenName() {
        return gardenName;
    }

    public String eyeDisplayName() {
        return gardenName + " Eye";
    }

    public String frameDisplayName() {
        return gardenName + " Portal Frame";
    }

    public String placeholderTexture() {
        return placeholderTexture;
    }

    public static Optional<GardenEyeType> byEyeId(String eyeId) {
        return Arrays.stream(values()).filter(type -> type.eyeId().equals(eyeId)).findFirst();
    }

    public static Optional<GardenEyeType> byFrameId(String frameId) {
        return Arrays.stream(values()).filter(type -> type.frameId().equals(frameId)).findFirst();
    }
}

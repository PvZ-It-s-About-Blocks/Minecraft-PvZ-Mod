package net.PvZModders.PvZMod.progression.atmosphere;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.biome.Biomes;

public final class DarkAgesBiomeEffects {
    private static final String IN_DARK_AGES_TAG = "PvZInDarkAgesBiome";

    private DarkAgesBiomeEffects() {
    }

    public static boolean isPlayerInDarkAgesBiome(Player player) {
        return player.level().getBiome(BlockPos.containing(player.position())).is(Biomes.DARK_FOREST);
    }

    public static boolean shouldSuppressPassiveSunDrops(ServerPlayer player) {
        return isPlayerInDarkAgesBiome(player);
    }

    public static boolean wasInDarkAgesBiome(Player player) {
        return player.getPersistentData().getBoolean(IN_DARK_AGES_TAG);
    }

    public static void setInDarkAgesBiome(Player player, boolean inDarkAges) {
        player.getPersistentData().putBoolean(IN_DARK_AGES_TAG, inDarkAges);
    }
}

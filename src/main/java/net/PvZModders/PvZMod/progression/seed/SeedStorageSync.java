package net.PvZModders.PvZMod.progression.seed;

import net.PvZModders.PvZMod.network.ModMessages;
import net.minecraft.server.level.ServerPlayer;

public final class SeedStorageSync {
    private SeedStorageSync() {
    }

    public static void syncToClient(ServerPlayer player) {
        ModMessages.sendSeedStorage(player);
    }
}

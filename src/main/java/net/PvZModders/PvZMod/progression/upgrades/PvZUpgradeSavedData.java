package net.PvZModders.PvZMod.progression.upgrades;

import net.PvZModders.PvZMod.PvZ2Mod;
import net.PvZModders.PvZMod.progression.GardenId;
import net.PvZModders.PvZMod.progression.GardenProgressSavedData;
import net.PvZModders.PvZMod.progression.seed.SeedStorage;
import net.PvZModders.PvZMod.progression.sun.SunManager;
import net.PvZModders.PvZMod.progression.waves.GardenWaves;
import net.PvZModders.PvZMod.progression.waves.WaveReward;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.EnumSet;

@Mod.EventBusSubscriber(modid = PvZ2Mod.MOD_ID)
public class PvZUpgradeSavedData extends SavedData {
    private static final String DATA_NAME = PvZ2Mod.MOD_ID + "_garden_upgrades";
    private final EnumSet<GardenUpgrade> unlocked = EnumSet.noneOf(GardenUpgrade.class);

    public static PvZUpgradeSavedData get(ServerLevel level) {
        return level.getServer().overworld().getDataStorage().computeIfAbsent(
                PvZUpgradeSavedData::load,
                PvZUpgradeSavedData::new,
                DATA_NAME
        );
    }

    public static PvZUpgradeSavedData load(CompoundTag tag) {
        PvZUpgradeSavedData data = new PvZUpgradeSavedData();
        ListTag list = tag.getList("Unlocked", Tag.TAG_STRING);
        for (int i = 0; i < list.size(); i++) {
            GardenUpgrade.byId(list.getString(i)).ifPresent(data.unlocked::add);
        }
        return data;
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            PvZUpgradeSavedData data = get(player.serverLevel());
            data.backfillFromClaimedRewards(player.serverLevel());
            data.applyToPlayer(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            get(player.serverLevel()).applyToPlayer(player);
        }
    }

    public boolean unlockByRewardId(String rewardId) {
        return GardenUpgrade.byId(rewardId).map(this::unlock).orElse(false);
    }

    public boolean unlock(GardenUpgrade upgrade) {
        boolean added = unlocked.add(upgrade);
        if (added) {
            setDirty();
        }
        return added;
    }

    public boolean isUnlocked(GardenUpgrade upgrade) {
        return unlocked.contains(upgrade);
    }

    public void applyToPlayer(ServerPlayer player) {
        SunManager.setSunCap(player, PvZUpgradeValues.playerSunCap(this));
        SunManager.syncSunBar(player);
        SeedStorage.applyUpgrades(player, this);
    }

    public void applyToAllPlayers(ServerLevel level) {
        for (ServerPlayer player : level.getServer().getPlayerList().getPlayers()) {
            applyToPlayer(player);
            player.sendSystemMessage(Component.literal("Garden upgrade applied.").withStyle(ChatFormatting.GREEN));
        }
    }

    public static int playerSeedPacketCap(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            return PvZUpgradeValues.playerSeedPacketCap(get(serverPlayer.serverLevel()));
        }
        return SeedStorage.BASE_PLAYER_PACKET_CAP;
    }

    private void backfillFromClaimedRewards(ServerLevel level) {
        GardenProgressSavedData progressData = GardenProgressSavedData.get(level.getServer().overworld());
        boolean changed = false;
        for (GardenId gardenId : GardenId.values()) {
            for (int claimedWave : progressData.getWaveProgress(gardenId).claimedRewardWaves()) {
                for (WaveReward reward : GardenWaves.get(gardenId, claimedWave).rewards()) {
                    if (reward.type() == net.PvZModders.PvZMod.progression.waves.WaveRewardType.GARDEN_UPGRADE
                            || reward.type() == net.PvZModders.PvZMod.progression.waves.WaveRewardType.PLAYER_UPGRADE) {
                        changed |= unlockByRewardId(reward.id());
                    }
                }
            }
        }
        if (changed) {
            setDirty();
        }
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag list = new ListTag();
        for (GardenUpgrade upgrade : unlocked) {
            list.add(StringTag.valueOf(upgrade.id()));
        }
        tag.put("Unlocked", list);
        return tag;
    }
}

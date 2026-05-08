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
import java.util.EnumMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = PvZ2Mod.MOD_ID)
public class PvZUpgradeSavedData extends SavedData {
    private static final String DATA_NAME = PvZ2Mod.MOD_ID + "_garden_upgrades";
    private final EnumSet<GardenUpgrade> unlocked = EnumSet.noneOf(GardenUpgrade.class);
    private final EnumMap<GardenUpgradeCategory, Integer> categoryTiers = new EnumMap<>(GardenUpgradeCategory.class);

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
        CompoundTag categoryTag = tag.getCompound("CategoryTiers");
        for (GardenUpgradeCategory category : GardenUpgradeCategory.values()) {
            if (categoryTag.contains(category.id())) {
                data.categoryTiers.put(category, Math.max(0, Math.min(category.maxTier(), categoryTag.getInt(category.id()))));
            }
        }
        data.migrateFlagsToCategoryTiers();
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

    @SubscribeEvent
    public static void onPlayerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            get(player.serverLevel()).applyToPlayer(player);
        }
    }

    public static boolean unlockWorldUpgrade(ServerLevel level, GardenUpgrade upgrade) {
        PvZUpgradeSavedData data = get(level);
        boolean unlockedNow = data.unlock(upgrade);
        if (unlockedNow) {
            data.applyToAllPlayers(level);
        }
        return unlockedNow;
    }

    public static boolean isWorldUpgradeUnlocked(ServerLevel level, GardenUpgrade upgrade) {
        return get(level).isUnlocked(upgrade);
    }

    public static java.util.Set<GardenUpgrade> getUnlockedWorldUpgrades(ServerLevel level) {
        return java.util.Collections.unmodifiableSet(get(level).unlocked);
    }

    public static void applyWorldUpgradeBenefitsToPlayer(ServerPlayer player) {
        get(player.serverLevel()).applyToPlayer(player);
    }

    public static void applyWorldUpgradeBenefitsToAllOnlinePlayers(ServerLevel level) {
        get(level).applyToAllPlayers(level);
    }

    public static int recalculatePlayerSunCapFromWorldUpgrades(ServerPlayer player) {
        return PvZUpgradeValues.playerSunCap(get(player.serverLevel()));
    }

    public static int recalculateSeedHolderSlotsFromWorldUpgrades(ServerPlayer player) {
        return PvZUpgradeValues.pageOneUnlockedSlots(get(player.serverLevel()));
    }

    public static int recalculateSeedStorageCapacityFromWorldUpgrades(ServerPlayer player) {
        return PvZUpgradeValues.playerSeedPacketCap(get(player.serverLevel()));
    }

    public static int recalculateTotemSeedStorageFromWorldUpgrades(ServerLevel level) {
        return PvZUpgradeValues.gardenPacketCap(get(level));
    }

    public static float recalculateSeedReplenishmentSpeedFromWorldUpgrades(ServerLevel level) {
        return PvZUpgradeValues.seedRefillMultiplier(get(level));
    }

    public static int getMinimumWaveStartSun(ServerPlayer player, GardenId gardenId) {
        return Math.min(SunManager.getSunCap(player), PvZUpgradeValues.minimumWaveStartSun(get(player.serverLevel())));
    }

    public static void ensureMinimumSunOnWaveStart(ServerPlayer player, GardenId gardenId) {
        int targetSun = getMinimumWaveStartSun(player, gardenId);
        if (SunManager.getSun(player) < targetSun) {
            SunManager.setSun(player, targetSun);
            SunManager.syncSunBar(player);
        }
    }

    public boolean unlockByRewardId(String rewardId) {
        return GardenUpgrade.byId(rewardId).map(this::unlock).orElse(false);
    }

    public boolean unlock(GardenUpgrade upgrade) {
        int previousCategoryTier = GardenUpgradeCategory.forUpgrade(upgrade)
                .map(this::getUpgradeTier)
                .orElse(0);
        boolean added = unlocked.add(upgrade);
        GardenUpgradeCategory.forUpgrade(upgrade).ifPresent(category -> {
            int migratedTier = Math.max(previousCategoryTier, category.tierOf(upgrade));
            if (migratedTier != previousCategoryTier) {
                categoryTiers.put(category, migratedTier);
                setDirty();
            }
        });
        if (added) {
            setDirty();
        }
        return added;
    }

    public boolean isUnlocked(GardenUpgrade upgrade) {
        return unlocked.contains(upgrade)
                || GardenUpgradeCategory.forUpgrade(upgrade)
                .map(category -> getUpgradeTier(category) >= category.tierOf(upgrade))
                .orElse(false);
    }

    public int getUpgradeTier(GardenUpgradeCategory category) {
        return Math.max(categoryTierFromUnlockedFlags(category), categoryTiers.getOrDefault(category, 0));
    }

    public boolean canUpgradeCategory(GardenUpgradeCategory category) {
        return getUpgradeTier(category) < category.maxTier();
    }

    public boolean upgradeNextTier(GardenUpgradeCategory category) {
        int current = getUpgradeTier(category);
        if (current >= category.maxTier()) {
            return false;
        }
        int nextTier = current + 1;
        categoryTiers.put(category, nextTier);
        category.upgradeForTier(nextTier).ifPresent(unlocked::add);
        setDirty();
        return true;
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
            migrateFlagsToCategoryTiers();
            setDirty();
        }
    }

    private void migrateFlagsToCategoryTiers() {
        for (GardenUpgradeCategory category : GardenUpgradeCategory.values()) {
            int flagTier = categoryTierFromUnlockedFlags(category);
            if (flagTier > categoryTiers.getOrDefault(category, 0)) {
                categoryTiers.put(category, flagTier);
            }
        }
    }

    private int categoryTierFromUnlockedFlags(GardenUpgradeCategory category) {
        int tier = 0;
        for (GardenUpgrade upgrade : unlocked) {
            tier = Math.max(tier, category.tierOf(upgrade));
        }
        return tier;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag list = new ListTag();
        for (GardenUpgrade upgrade : unlocked) {
            list.add(StringTag.valueOf(upgrade.id()));
        }
        tag.put("Unlocked", list);
        CompoundTag categoryTag = new CompoundTag();
        for (Map.Entry<GardenUpgradeCategory, Integer> entry : categoryTiers.entrySet()) {
            categoryTag.putInt(entry.getKey().id(), entry.getValue());
        }
        tag.put("CategoryTiers", categoryTag);
        return tag;
    }
}

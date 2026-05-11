package net.PvZModders.PvZMod.progression.shop;

import net.PvZModders.PvZMod.progression.GardenId;
import net.PvZModders.PvZMod.progression.greenhouse.GreenhouseCoinManager;
import net.PvZModders.PvZMod.progression.upgrades.GardenUpgradeCategory;
import net.PvZModders.PvZMod.progression.upgrades.PvZUpgradeSavedData;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.player.Player;

public final class DaveShopPurchaseManager {
    private static final String PLAYER_SHOP_PURCHASES_TAG = "PvZDaveShopPurchases";

    private DaveShopPurchaseManager() {
    }

    public static boolean purchaseShopEntry(ServerPlayer player, GardenId gardenId, String entryId) {
        return DaveShopRegistry.getEntry(gardenId, entryId)
                .map(entry -> purchaseShopEntry(player, entry))
                .orElseGet(() -> {
                    player.displayClientMessage(Component.literal("Crazy Dave cannot find that stock entry.").withStyle(ChatFormatting.RED), true);
                    return false;
                });
    }

    public static boolean purchaseShopEntry(ServerPlayer player, DaveShopEntry entry) {
        DaveShopSavedData data = DaveShopSavedData.get(player.serverLevel());
        if (!entry.repeatable() && usesWorldPurchaseLock(entry) && data.isPurchased(entry.gardenId(), entry.id())) {
            player.displayClientMessage(Component.literal("Already purchased.").withStyle(ChatFormatting.YELLOW), true);
            return false;
        }
        if (!entry.repeatable() && usesPlayerPurchaseLock(entry) && hasPlayerPurchased(player, entry)) {
            player.displayClientMessage(Component.literal("Already purchased.").withStyle(ChatFormatting.YELLOW), true);
            return false;
        }
        if (entry.purchaseType() == DaveShopPurchaseType.PLANT_UNLOCK
                && data.isPlantUnlocked(entry.gardenId(), entry.plantId())) {
            player.displayClientMessage(Component.literal("Plant already unlocked.").withStyle(ChatFormatting.YELLOW), true);
            return false;
        }
        if (entry.purchaseType() == DaveShopPurchaseType.UPGRADE
                && GardenUpgradeCategory.byId(entry.upgradeCategoryId())
                .map(category -> !PvZUpgradeSavedData.get(player.serverLevel()).canUpgradeCategory(category))
                .orElse(true)) {
            player.displayClientMessage(Component.literal("That upgrade is already at max tier.").withStyle(ChatFormatting.YELLOW), true);
            return false;
        }
        if (!GreenhouseCoinManager.hasCoins(player, entry.coinPrice())) {
            player.displayClientMessage(Component.literal("Not enough coins. Need " + entry.coinPrice() + ".").withStyle(ChatFormatting.RED), true);
            return false;
        }
        if (!GreenhouseCoinManager.removeCoins(player, entry.coinPrice())) {
            player.displayClientMessage(Component.literal("Coin payment failed.").withStyle(ChatFormatting.RED), true);
            return false;
        }

        boolean granted = switch (entry.purchaseType()) {
            case ITEM, ARMOR, RECIPE, SPECIAL -> grantItem(player, entry);
            case PLANT_UNLOCK -> unlockShopPlantForWorld(player, entry);
            case UPGRADE -> upgradeNextTier(player, entry);
        };

        if (!granted) {
            GreenhouseCoinManager.giveCoins(player, entry.coinPrice());
            player.displayClientMessage(Component.literal("Purchase failed; coins refunded.").withStyle(ChatFormatting.RED), true);
            return false;
        }

        if (!entry.repeatable() && usesWorldPurchaseLock(entry)) {
            data.markPurchased(entry.gardenId(), entry.id());
        } else if (!entry.repeatable() && usesPlayerPurchaseLock(entry)) {
            markPlayerPurchased(player, entry);
        }
        player.displayClientMessage(Component.literal(entry.displayName() + " purchased.").withStyle(ChatFormatting.GREEN), true);
        return true;
    }

    public static boolean isEntryAvailableForPlayer(ServerLevel level, Player player, DaveShopEntry entry) {
        DaveShopSavedData shopData = DaveShopSavedData.get(level);
        PvZUpgradeSavedData upgradeData = PvZUpgradeSavedData.get(level);
        return switch (entry.purchaseType()) {
            case PLANT_UNLOCK -> !shopData.isPlantUnlocked(entry.gardenId(), entry.plantId());
            case UPGRADE -> GardenUpgradeCategory.byId(entry.upgradeCategoryId())
                    .map(upgradeData::canUpgradeCategory)
                    .orElse(false);
            case ITEM, ARMOR, RECIPE, SPECIAL -> entry.repeatable()
                    || player == null
                    || !hasPlayerPurchased(player, entry);
        };
    }

    private static boolean usesWorldPurchaseLock(DaveShopEntry entry) {
        return entry.purchaseType() == DaveShopPurchaseType.PLANT_UNLOCK;
    }

    private static boolean usesPlayerPurchaseLock(DaveShopEntry entry) {
        return switch (entry.purchaseType()) {
            case ITEM, ARMOR, RECIPE, SPECIAL -> true;
            default -> false;
        };
    }

    private static boolean hasPlayerPurchased(Player player, DaveShopEntry entry) {
        return player != null
                && player.getPersistentData()
                .getCompound(PLAYER_SHOP_PURCHASES_TAG)
                .getBoolean(playerPurchaseKey(entry));
    }

    private static void markPlayerPurchased(Player player, DaveShopEntry entry) {
        CompoundTag purchases = player.getPersistentData().getCompound(PLAYER_SHOP_PURCHASES_TAG);
        purchases.putBoolean(playerPurchaseKey(entry), true);
        player.getPersistentData().put(PLAYER_SHOP_PURCHASES_TAG, purchases);
    }

    private static String playerPurchaseKey(DaveShopEntry entry) {
        return entry.gardenId().name() + ":" + entry.id();
    }

    private static boolean grantItem(ServerPlayer player, DaveShopEntry entry) {
        Item item = BuiltInRegistries.ITEM.get(entry.itemId());
        if (item == Items.AIR || entry.itemCount() <= 0) {
            return false;
        }

        ItemStack stack = new ItemStack(item, entry.itemCount());
        if (!player.getInventory().add(stack) && !stack.isEmpty()) {
            player.drop(stack, false);
        }
        return true;
    }

    private static boolean unlockShopPlantForWorld(ServerPlayer player, DaveShopEntry entry) {
        if (entry.plantId().isBlank()) {
            return false;
        }
        DaveShopSavedData.get(player.serverLevel()).unlockPlant(entry.gardenId(), entry.plantId());
        player.displayClientMessage(Component.literal("Plant unlocked: " + entry.displayName()).withStyle(ChatFormatting.GREEN), true);
        return true;
    }

    private static boolean upgradeNextTier(ServerPlayer player, DaveShopEntry entry) {
        return GardenUpgradeCategory.byId(entry.upgradeCategoryId())
                .map(category -> {
                    PvZUpgradeSavedData upgrades = PvZUpgradeSavedData.get(player.serverLevel());
                    if (!upgrades.upgradeNextTier(category)) {
                        return false;
                    }
                    upgrades.applyToAllPlayers(player.serverLevel());
                    player.displayClientMessage(Component.literal(category.displayName() + " upgraded to Tier " + upgrades.getUpgradeTier(category) + ".").withStyle(ChatFormatting.GREEN), true);
                    return true;
                })
                .orElse(false);
    }

}

package net.PvZModders.PvZMod.progression.shop;

import net.PvZModders.PvZMod.progression.GardenId;
import net.PvZModders.PvZMod.progression.greenhouse.GreenhouseCoinManager;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public final class DaveShopPurchaseManager {
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
        if (!entry.repeatable() && data.isPurchased(entry.gardenId(), entry.id())) {
            player.displayClientMessage(Component.literal("Already purchased.").withStyle(ChatFormatting.YELLOW), true);
            return false;
        }
        if (entry.purchaseType() == DaveShopPurchaseType.PLANT_UNLOCK
                && data.isPlantUnlocked(entry.gardenId(), entry.plantId())) {
            player.displayClientMessage(Component.literal("Plant already unlocked.").withStyle(ChatFormatting.YELLOW), true);
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
            case UPGRADE -> false;
        };

        if (!granted) {
            GreenhouseCoinManager.giveCoins(player, entry.coinPrice());
            player.displayClientMessage(Component.literal("Purchase failed; coins refunded.").withStyle(ChatFormatting.RED), true);
            return false;
        }

        if (!entry.repeatable()) {
            data.markPurchased(entry.gardenId(), entry.id());
        }
        player.displayClientMessage(Component.literal(entry.displayName() + " purchased.").withStyle(ChatFormatting.GREEN), true);
        return true;
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

}

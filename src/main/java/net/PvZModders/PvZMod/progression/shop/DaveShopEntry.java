package net.PvZModders.PvZMod.progression.shop;

import net.PvZModders.PvZMod.progression.GardenId;
import net.minecraft.resources.ResourceLocation;

public record DaveShopEntry(
        String id,
        String displayName,
        String description,
        GardenId gardenId,
        DaveShopCategory category,
        DaveShopPurchaseType purchaseType,
        int coinPrice,
        ResourceLocation iconItemId,
        ResourceLocation itemId,
        int itemCount,
        String plantId,
        String upgradeCategoryId,
        boolean repeatable,
        int prerequisiteWave
) {
    public static DaveShopEntry item(GardenId gardenId, String id, String displayName, String description, int coinPrice,
                                     ResourceLocation itemId, int itemCount, boolean repeatable) {
        return new DaveShopEntry(id, displayName, description, gardenId, DaveShopCategory.ITEMS, DaveShopPurchaseType.ITEM,
                coinPrice, itemId, itemId, itemCount, "", "", repeatable, 0);
    }

    public static DaveShopEntry plantUnlock(GardenId gardenId, String id, String displayName, String description, int coinPrice,
                                            ResourceLocation iconItemId, String plantId, int prerequisiteWave) {
        return new DaveShopEntry(id, displayName, description, gardenId, DaveShopCategory.PLANTS, DaveShopPurchaseType.PLANT_UNLOCK,
                coinPrice, iconItemId, iconItemId, 0, plantId, "", false, prerequisiteWave);
    }

    public static DaveShopEntry upgrade(GardenId gardenId, String id, String displayName, String description, int coinPrice,
                                        ResourceLocation iconItemId, String upgradeCategoryId) {
        return new DaveShopEntry(id, displayName, description, gardenId, DaveShopCategory.UPGRADES, DaveShopPurchaseType.UPGRADE,
                coinPrice, iconItemId, iconItemId, 0, "", upgradeCategoryId, true, 0);
    }
}

package net.PvZModders.PvZMod.client.seed;

import net.PvZModders.PvZMod.progression.seed.PlantSeedDefinition;
import net.PvZModders.PvZMod.progression.seed.PlantSlotData;
import net.PvZModders.PvZMod.progression.seed.SeedStorage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public final class ClientSeedStorage {
    private static CompoundTag seedStorageTag = new CompoundTag();

    private ClientSeedStorage() {
    }

    public static void update(CompoundTag tag) {
        seedStorageTag = tag.copy();
    }

    public static boolean seedModeEnabled() {
        return seedStorageTag.getBoolean("SeedModeEnabled");
    }

    public static int selectedPlantSlot() {
        return Math.max(0, Math.min(SeedStorage.PLANT_SLOTS_PER_PAGE - 1, seedStorageTag.getInt("SelectedPlantSlot")));
    }

    public static int currentPage() {
        return seedStorageTag.getInt("CurrentPlantHotbarPage") == SeedStorage.PAGE_TWO ? SeedStorage.PAGE_TWO : SeedStorage.PAGE_ONE;
    }

    public static boolean secondPageUnlocked() {
        return seedStorageTag.getBoolean("SecondPlantHotbarPageUnlocked");
    }

    public static int unlockedSlots(int page) {
        return Math.max(0, Math.min(SeedStorage.PLANT_SLOTS_PER_PAGE, seedStorageTag.getInt(page == SeedStorage.PAGE_TWO ? "UnlockedPlantSlotsPage2" : "UnlockedPlantSlotsPage1")));
    }

    public static boolean isSlotUnlocked(int page, int slot) {
        return slot >= 0 && slot < unlockedSlots(page);
    }

    public static PlantSlotData slot(int page, int slot) {
        ListTag slots = seedStorageTag.getList(page == SeedStorage.PAGE_TWO ? "Page2PlantSlots" : "Page1PlantSlots", Tag.TAG_COMPOUND);
        if (slot < 0 || slot >= slots.size()) {
            return PlantSlotData.empty();
        }
        return PlantSlotData.load(slots.getCompound(slot));
    }

    public static ItemStack slotStack(int page, int slot) {
        return slot(page, slot).toItemStack();
    }

    public static int sunCost(ResourceLocation itemId) {
        return itemId == null ? 0 : PlantSeedDefinition.sunCost(itemId);
    }
}

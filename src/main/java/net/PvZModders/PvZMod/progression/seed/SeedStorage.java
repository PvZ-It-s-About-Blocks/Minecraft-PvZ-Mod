package net.PvZModders.PvZMod.progression.seed;

import net.PvZModders.PvZMod.PvZ2Mod;
import net.PvZModders.PvZMod.progression.sun.SunManager;
import net.PvZModders.PvZMod.progression.upgrades.PvZUpgradeSavedData;
import net.PvZModders.PvZMod.progression.upgrades.PvZUpgradeValues;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Optional;

@Mod.EventBusSubscriber(modid = PvZ2Mod.MOD_ID)
public final class SeedStorage {
    public static final int PLANT_SLOTS_PER_PAGE = 8;
    public static final int PAGE_ONE = 0;
    public static final int PAGE_TWO = 1;
    public static final int BASE_PLAYER_PACKET_CAP = 25;

    private static final String ROOT_TAG = "PvZSeedStorage";
    private static final String SEED_MODE_TAG = "SeedModeEnabled";
    private static final String SELECTED_SLOT_TAG = "SelectedPlantSlot";
    private static final String CURRENT_PAGE_TAG = "CurrentPlantHotbarPage";
    private static final String SECOND_PAGE_UNLOCKED_TAG = "SecondPlantHotbarPageUnlocked";
    private static final String UNLOCKED_PAGE_ONE_TAG = "UnlockedPlantSlotsPage1";
    private static final String UNLOCKED_PAGE_TWO_TAG = "UnlockedPlantSlotsPage2";
    private static final String PAGE_ONE_SLOTS_TAG = "Page1PlantSlots";
    private static final String PAGE_TWO_SLOTS_TAG = "Page2PlantSlots";
    private static final String STARTER_LOADOUT_ADDED_TAG = "StarterLoadoutAdded";
    private static final String PLAYER_PACKET_CAP_TAG = "PlayerPacketCap";

    private SeedStorage() {
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        ensureInitialized(event.getEntity());
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            PvZUpgradeSavedData.get(serverPlayer.serverLevel()).applyToPlayer(serverPlayer);
            SeedStorageSync.syncToClient(serverPlayer);
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        event.getEntity().getPersistentData().put(ROOT_TAG, event.getOriginal().getPersistentData().getCompound(ROOT_TAG).copy());
        ensureInitialized(event.getEntity());
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            PvZUpgradeSavedData.get(serverPlayer.serverLevel()).applyToPlayer(serverPlayer);
            SeedStorageSync.syncToClient(serverPlayer);
        }
    }

    public static void toggleSeedMode(Player player) {
        setSeedModeEnabled(player, !isSeedModeEnabled(player));
        player.displayClientMessage(Component.literal(isSeedModeEnabled(player) ? "Seed Mode enabled" : "Seed Mode disabled")
                .withStyle(isSeedModeEnabled(player) ? ChatFormatting.GREEN : ChatFormatting.RED), true);
        sync(player);
    }

    public static boolean isSeedModeEnabled(Player player) {
        return root(player).getBoolean(SEED_MODE_TAG);
    }

    public static void setSeedModeEnabled(Player player, boolean enabled) {
        root(player).putBoolean(SEED_MODE_TAG, enabled);
    }

    public static int getSelectedPlantSlot(Player player) {
        return clampSlot(root(player).getInt(SELECTED_SLOT_TAG));
    }

    public static void setSelectedPlantSlot(Player player, int slot) {
        int clamped = clampSlot(slot);
        if (!isPlantSlotUnlocked(player, getCurrentPlantHotbarPage(player), clamped)) {
            player.displayClientMessage(Component.literal("This plant slot is locked.").withStyle(ChatFormatting.RED), true);
            sync(player);
            return;
        }
        root(player).putInt(SELECTED_SLOT_TAG, clamped);
        sync(player);
    }

    public static void cycleSelectedPlantSlot(Player player, int direction) {
        int page = getCurrentPlantHotbarPage(player);
        int unlocked = getUnlockedPlantSlots(player, page);
        if (unlocked <= 0) {
            return;
        }

        int selected = getSelectedPlantSlot(player);
        int normalizedDirection = direction >= 0 ? 1 : -1;
        for (int step = 0; step < PLANT_SLOTS_PER_PAGE; step++) {
            selected = Math.floorMod(selected + normalizedDirection, PLANT_SLOTS_PER_PAGE);
            if (isPlantSlotUnlocked(player, page, selected)) {
                root(player).putInt(SELECTED_SLOT_TAG, selected);
                sync(player);
                return;
            }
        }
    }

    public static int getCurrentPlantHotbarPage(Player player) {
        return clampPage(root(player).getInt(CURRENT_PAGE_TAG));
    }

    public static void switchPlantHotbarPage(Player player) {
        if (!isSecondPlantHotbarPageUnlocked(player)) {
            player.displayClientMessage(Component.literal("Second plant page locked.").withStyle(ChatFormatting.RED), true);
            sync(player);
            return;
        }

        root(player).putInt(CURRENT_PAGE_TAG, getCurrentPlantHotbarPage(player) == PAGE_ONE ? PAGE_TWO : PAGE_ONE);
        if (!isPlantSlotUnlocked(player, getCurrentPlantHotbarPage(player), getSelectedPlantSlot(player))) {
            root(player).putInt(SELECTED_SLOT_TAG, 0);
        }
        sync(player);
    }

    public static boolean isSecondPlantHotbarPageUnlocked(Player player) {
        return root(player).getBoolean(SECOND_PAGE_UNLOCKED_TAG);
    }

    public static int getUnlockedPlantSlots(Player player, int page) {
        CompoundTag root = root(player);
        return Math.max(0, Math.min(PLANT_SLOTS_PER_PAGE, page == PAGE_ONE ? root.getInt(UNLOCKED_PAGE_ONE_TAG) : root.getInt(UNLOCKED_PAGE_TWO_TAG)));
    }

    public static boolean isPlantSlotUnlocked(Player player, int page, int slot) {
        return clampSlot(slot) < getUnlockedPlantSlots(player, clampPage(page));
    }

    public static PlantSlotData getPlantSlot(Player player, int page, int slot) {
        ListTag slots = slotsForPage(root(player), clampPage(page));
        return PlantSlotData.load(slots.getCompound(clampSlot(slot)));
    }

    public static PlantSlotData getPlantSlotByStorageIndex(Player player, int storageIndex) {
        return getPlantSlot(player, storageIndex / PLANT_SLOTS_PER_PAGE, storageIndex % PLANT_SLOTS_PER_PAGE);
    }

    public static ItemStack getPlantSlotStackByStorageIndex(Player player, int storageIndex) {
        return getPlantSlotByStorageIndex(player, storageIndex).toItemStack();
    }

    public static PlantSlotData getSelectedPlantSlotData(Player player) {
        return getPlantSlot(player, getCurrentPlantHotbarPage(player), getSelectedPlantSlot(player));
    }

    public static boolean canAffordSelectedPlant(Player player) {
        PlantSlotData slot = getSelectedPlantSlotData(player);
        return !slot.isEmpty() && SunManager.getSun(player) >= PlantSeedDefinition.sunCost(slot.itemId());
    }

    public static boolean consumeSelectedPlantPacket(Player player) {
        int page = getCurrentPlantHotbarPage(player);
        int slotIndex = getSelectedPlantSlot(player);
        PlantSlotData slot = getPlantSlot(player, page, slotIndex);
        if (slot.packetCount() <= 0) {
            return false;
        }
        slot.decrementPacket();
        setPlantSlot(player, page, slotIndex, slot);
        sync(player);
        return true;
    }

    public static void placeSelectedPlant(Player player, BlockHitResult target) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        if (!isSeedModeEnabled(player)) {
            return;
        }

        int page = getCurrentPlantHotbarPage(player);
        int selectedSlot = getSelectedPlantSlot(player);
        if (!isPlantSlotUnlocked(player, page, selectedSlot)) {
            player.displayClientMessage(Component.literal("This plant slot is locked.").withStyle(ChatFormatting.RED), true);
            sync(player);
            return;
        }

        PlantSlotData slot = getSelectedPlantSlotData(player);
        if (slot.isEmpty()) {
            player.displayClientMessage(Component.literal("No plant selected.").withStyle(ChatFormatting.RED), true);
            sync(player);
            return;
        }

        if (slot.packetCount() <= 0) {
            player.displayClientMessage(Component.literal("No seed packets left.").withStyle(ChatFormatting.RED), true);
            sync(player);
            return;
        }

        Optional<PlantSeedDefinition> definition = PlantSeedDefinition.get(slot.itemId());
        int sunCost = definition.map(PlantSeedDefinition::sunCost).orElse(100);
        if (SunManager.getSun(player) < sunCost) {
            player.displayClientMessage(Component.literal("Not enough Sun.").withStyle(ChatFormatting.RED), true);
            sync(player);
            return;
        }

        if (definition.isEmpty()
                || (!PlantEntityManager.placePlantInTargetMinecart(serverPlayer, definition.get())
                && (target == null || !PlantEntityManager.placePlant(serverPlayer, target, definition.get())))) {
            player.displayClientMessage(Component.literal("Cannot plant there.").withStyle(ChatFormatting.RED), true);
            sync(player);
            return;
        }

        SunManager.spendSun(player, sunCost);
        consumeSelectedPlantPacket(player);
    }

    public static void unlockPlantSlot(Player player, int page) {
        CompoundTag root = root(player);
        if (clampPage(page) == PAGE_ONE) {
            root.putInt(UNLOCKED_PAGE_ONE_TAG, Math.min(PLANT_SLOTS_PER_PAGE, root.getInt(UNLOCKED_PAGE_ONE_TAG) + 1));
        } else {
            root.putInt(UNLOCKED_PAGE_TWO_TAG, Math.min(PLANT_SLOTS_PER_PAGE, root.getInt(UNLOCKED_PAGE_TWO_TAG) + 1));
        }
        sync(player);
    }

    public static void unlockSecondPlantHotbarPage(Player player) {
        CompoundTag root = root(player);
        root.putBoolean(SECOND_PAGE_UNLOCKED_TAG, true);
        if (root.getInt(UNLOCKED_PAGE_TWO_TAG) <= 0) {
            root.putInt(UNLOCKED_PAGE_TWO_TAG, 6);
        }
        sync(player);
    }

    public static void applyUpgrades(Player player, PvZUpgradeSavedData upgrades) {
        CompoundTag root = root(player);
        root.putInt(UNLOCKED_PAGE_ONE_TAG, Math.max(root.getInt(UNLOCKED_PAGE_ONE_TAG), PvZUpgradeValues.pageOneUnlockedSlots(upgrades)));
        if (PvZUpgradeValues.secondPageUnlocked(upgrades)) {
            root.putBoolean(SECOND_PAGE_UNLOCKED_TAG, true);
            root.putInt(UNLOCKED_PAGE_TWO_TAG, Math.max(root.getInt(UNLOCKED_PAGE_TWO_TAG), PvZUpgradeValues.pageTwoUnlockedSlots(upgrades)));
        }
        root.putInt(PLAYER_PACKET_CAP_TAG, PvZUpgradeValues.playerSeedPacketCap(upgrades));
        sync(player);
    }

    public static int getPlayerPacketCap(Player player) {
        CompoundTag root = root(player);
        if (!root.contains(PLAYER_PACKET_CAP_TAG)) {
            root.putInt(PLAYER_PACKET_CAP_TAG, BASE_PLAYER_PACKET_CAP);
        }
        return Math.max(BASE_PLAYER_PACKET_CAP, root.getInt(PLAYER_PACKET_CAP_TAG));
    }

    public static void setPlantSlot(Player player, int page, int slot, PlantSlotData slotData) {
        ListTag slots = slotsForPage(root(player), clampPage(page));
        slots.set(clampSlot(slot), slotData.save());
    }

    public static void setPlantSlotByStorageIndex(Player player, int storageIndex, ItemStack stack) {
        int page = storageIndex / PLANT_SLOTS_PER_PAGE;
        int slot = storageIndex % PLANT_SLOTS_PER_PAGE;
        setPlantSlot(player, page, slot, capped(slotDataFromStack(stack), getPlayerPacketCap(player)));
        sync(player);
    }

    public static boolean isPlantSeedPacket(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        return PlantSeedDefinition.get(BuiltInRegistries.ITEM.getKey(stack.getItem())).isPresent();
    }

    public static void setPlantSlotLoadoutPlaceholder(Player player, int page, int slot, ResourceLocation seedPacketId, int packetCount) {
        setPlantSlot(player, page, slot, new PlantSlotData(seedPacketId, Math.min(getPlayerPacketCap(player), packetCount)));
        sync(player);
    }

    public static boolean addPlantPacketsToLoadout(Player player, ResourceLocation seedPacketId, int packetCount) {
        CompoundTag root = root(player);
        int packetCap = getPlayerPacketCap(player);
        for (int page = PAGE_ONE; page <= PAGE_TWO; page++) {
            int unlocked = getUnlockedPlantSlots(player, page);
            ListTag slots = slotsForPage(root, page);
            for (int slot = 0; slot < unlocked; slot++) {
                PlantSlotData slotData = PlantSlotData.load(slots.getCompound(slot));
                if (!slotData.isEmpty() && slotData.itemId().equals(seedPacketId)) {
                    slotData.set(seedPacketId, Math.min(packetCap, slotData.packetCount() + packetCount));
                    slots.set(slot, slotData.save());
                    sync(player);
                    return true;
                }
            }
        }

        for (int page = PAGE_ONE; page <= PAGE_TWO; page++) {
            int unlocked = getUnlockedPlantSlots(player, page);
            ListTag slots = slotsForPage(root, page);
            for (int slot = 0; slot < unlocked; slot++) {
                PlantSlotData slotData = PlantSlotData.load(slots.getCompound(slot));
                if (slotData.isEmpty()) {
                    slots.set(slot, new PlantSlotData(seedPacketId, Math.min(packetCap, packetCount)).save());
                    sync(player);
                    return true;
                }
            }
        }

        sync(player);
        return false;
    }

    public static CompoundTag copyForSync(Player player) {
        return root(player).copy();
    }

    public static void ensureInitialized(Player player) {
        CompoundTag root = root(player);
        if (!root.contains(UNLOCKED_PAGE_ONE_TAG)) {
            root.putBoolean(SEED_MODE_TAG, false);
            root.putInt(SELECTED_SLOT_TAG, 0);
            root.putInt(CURRENT_PAGE_TAG, PAGE_ONE);
            root.putBoolean(SECOND_PAGE_UNLOCKED_TAG, false);
            root.putInt(UNLOCKED_PAGE_ONE_TAG, 6);
            root.putInt(UNLOCKED_PAGE_TWO_TAG, 0);
            root.putInt(PLAYER_PACKET_CAP_TAG, BASE_PLAYER_PACKET_CAP);
            root.put(PAGE_ONE_SLOTS_TAG, emptySlots());
            root.put(PAGE_TWO_SLOTS_TAG, emptySlots());
        } else {
            ensureSlotsList(root, PAGE_ONE_SLOTS_TAG);
            ensureSlotsList(root, PAGE_TWO_SLOTS_TAG);
        }
        if (!root.getBoolean(STARTER_LOADOUT_ADDED_TAG)) {
            addStarterLoadout(root);
            root.putBoolean(STARTER_LOADOUT_ADDED_TAG, true);
        }
    }

    private static void addStarterLoadout(CompoundTag root) {
        ListTag pageOneSlots = slotsForPage(root, PAGE_ONE);
        pageOneSlots.set(0, new PlantSlotData(PlantSeedDefinition.sunflowerSeedPacketId(), 10).save());
        pageOneSlots.set(1, new PlantSlotData(PlantSeedDefinition.peashooterSeedPacketId(), 10).save());
    }

    private static CompoundTag root(Player player) {
        CompoundTag persistentData = player.getPersistentData();
        if (!persistentData.contains(ROOT_TAG)) {
            persistentData.put(ROOT_TAG, new CompoundTag());
        }
        CompoundTag root = persistentData.getCompound(ROOT_TAG);
        if (!root.contains(UNLOCKED_PAGE_ONE_TAG)) {
            ensureInitializedFromRoot(root);
        }
        if (!root.getBoolean(STARTER_LOADOUT_ADDED_TAG)) {
            addStarterLoadout(root);
            root.putBoolean(STARTER_LOADOUT_ADDED_TAG, true);
        }
        return root;
    }

    private static void ensureInitializedFromRoot(CompoundTag root) {
        root.putBoolean(SEED_MODE_TAG, false);
        root.putInt(SELECTED_SLOT_TAG, 0);
        root.putInt(CURRENT_PAGE_TAG, PAGE_ONE);
        root.putBoolean(SECOND_PAGE_UNLOCKED_TAG, false);
        root.putInt(UNLOCKED_PAGE_ONE_TAG, 6);
        root.putInt(UNLOCKED_PAGE_TWO_TAG, 0);
        root.putInt(PLAYER_PACKET_CAP_TAG, BASE_PLAYER_PACKET_CAP);
        root.put(PAGE_ONE_SLOTS_TAG, emptySlots());
        root.put(PAGE_TWO_SLOTS_TAG, emptySlots());
    }

    private static ListTag slotsForPage(CompoundTag root, int page) {
        return root.getList(page == PAGE_ONE ? PAGE_ONE_SLOTS_TAG : PAGE_TWO_SLOTS_TAG, Tag.TAG_COMPOUND);
    }

    private static ListTag emptySlots() {
        ListTag slots = new ListTag();
        for (int i = 0; i < PLANT_SLOTS_PER_PAGE; i++) {
            slots.add(PlantSlotData.empty().save());
        }
        return slots;
    }

    private static PlantSlotData capped(PlantSlotData slotData, int packetCap) {
        if (slotData.isEmpty()) {
            return slotData;
        }
        return new PlantSlotData(slotData.itemId(), Math.min(packetCap, slotData.packetCount()));
    }

    private static PlantSlotData slotDataFromStack(ItemStack stack) {
        if (!isPlantSeedPacket(stack)) {
            return PlantSlotData.empty();
        }
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        return new PlantSlotData(itemId, stack.getCount());
    }

    private static void ensureSlotsList(CompoundTag root, String key) {
        ListTag slots = root.getList(key, Tag.TAG_COMPOUND);
        while (slots.size() < PLANT_SLOTS_PER_PAGE) {
            slots.add(PlantSlotData.empty().save());
        }
        root.put(key, slots);
    }

    private static int clampSlot(int slot) {
        return Math.max(0, Math.min(PLANT_SLOTS_PER_PAGE - 1, slot));
    }

    private static int clampPage(int page) {
        return page == PAGE_TWO ? PAGE_TWO : PAGE_ONE;
    }

    private static void sync(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            SeedStorageSync.syncToClient(serverPlayer);
        }
    }
}

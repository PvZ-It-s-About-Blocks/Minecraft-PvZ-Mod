package net.PvZModders.PvZMod.menu;

import net.PvZModders.PvZMod.block.entity.GardenTotemBlockEntity;
import net.PvZModders.PvZMod.progression.greenhouse.GreenhouseCoinManager;
import net.PvZModders.PvZMod.progression.plants.GardenPlantDefinition;
import net.PvZModders.PvZMod.progression.seed.SeedStorage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

public class GardenTotemMenu extends AbstractContainerMenu {
    public static final int START_WAVE_BUTTON = 0;
    public static final int PORTAL_BUTTON_OFFSET = 100;
    public static final int PLANT_BUTTON_OFFSET = 200;
    public static final int SHOP_BUTTON_OFFSET = 300;
    public static final int WATERING_CAN_BUTTON_OFFSET = 400;
    public static final int SEED_POLISH_BUTTON_OFFSET = 500;
    public static final int PLANT_COUNT = GardenPlantDefinition.maxKnownGardenPlantCount();
    public static final int SEED_STORAGE_SLOT_START = 0;
    public static final int SEED_STORAGE_SLOT_COUNT = 6;
    public static final int GARDEN_SOURCE_SLOT_START = SEED_STORAGE_SLOT_START + SEED_STORAGE_SLOT_COUNT;
    public static final int GARDEN_SOURCE_SLOT_COUNT = PLANT_COUNT;
    public static final int TRASH_SLOT_INDEX = GARDEN_SOURCE_SLOT_START + GARDEN_SOURCE_SLOT_COUNT;

    private final GardenTotemBlockEntity gardenTotem;
    private final Player player;
    private int currentWave;
    private int waveActive;
    private int portalDiscoveryMask;
    private int currentPortalIndex;
    private int gardenPortalIndex;
    private final int[] plantCounts = new int[PLANT_COUNT];
    private final int[] plantRemainingSeconds = new int[PLANT_COUNT];
    private final int[] plantRefillSeconds = new int[PLANT_COUNT];
    private final ItemStack[] seedStorageStacks = new ItemStack[SEED_STORAGE_SLOT_COUNT];
    private boolean planterSlotsVisible;
    private int gardenPacketCap = net.PvZModders.PvZMod.progression.plants.GardenPlantProductionSavedData.GARDEN_PACKET_CAP;
    private int shopPlantUnlockMask;
    private int shopEntryAvailableMask;
    private int coinCount;

    public GardenTotemMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, 1, false);
    }

    public GardenTotemMenu(int containerId, Inventory playerInventory, int currentWave, boolean waveActive) {
        super(ModMenuTypes.GARDEN_TOTEM.get(), containerId);
        this.gardenTotem = null;
        this.player = playerInventory.player;
        this.currentWave = currentWave;
        this.waveActive = waveActive ? 1 : 0;
        this.portalDiscoveryMask = 1;
        this.currentPortalIndex = 0;
        this.gardenPortalIndex = 0;
        this.shopPlantUnlockMask = 0;
        this.shopEntryAvailableMask = 0;
        this.coinCount = 0;
        addContainerSlots(playerInventory);
        addDataSlots();
    }

    public GardenTotemMenu(int containerId, Inventory playerInventory, GardenTotemBlockEntity gardenTotem) {
        super(ModMenuTypes.GARDEN_TOTEM.get(), containerId);
        this.gardenTotem = gardenTotem;
        this.player = playerInventory.player;
        this.currentWave = gardenTotem.getCurrentWave();
        this.waveActive = gardenTotem.isWaveActive() ? 1 : 0;
        this.portalDiscoveryMask = gardenTotem.getPortalDiscoveryMask();
        this.currentPortalIndex = gardenTotem.getCurrentPortalIndex();
        this.gardenPortalIndex = gardenTotem.getGardenPortalIndex();
        this.shopPlantUnlockMask = gardenTotem.getShopPlantUnlockMask();
        this.shopEntryAvailableMask = gardenTotem.getShopEntryAvailableMask(playerInventory.player);
        this.coinCount = GreenhouseCoinManager.countCoins(playerInventory.player);
        addContainerSlots(playerInventory);
        addDataSlots();
    }

    private void addContainerSlots(Inventory playerInventory) {
        for (int index = 0; index < SEED_STORAGE_SLOT_COUNT; index++) {
            seedStorageStacks[index] = SeedStorage.getPlantSlotStackByStorageIndex(playerInventory.player, index);
            int x = 286 + (index % 2) * 18;
            int y = 78 + (index / 2) * 18;
            addSlot(new SeedStorageSlot(playerInventory.player, index, x, y));
        }

        java.util.List<GardenPlantDefinition> plants = gardenTotem == null
                ? GardenPlantDefinition.originalGardenPlants()
                : gardenTotem.getGardenPlants();
        for (int index = 0; index < GARDEN_SOURCE_SLOT_COUNT; index++) {
            GardenPlantDefinition plant = index < plants.size() ? plants.get(index) : null;
            int cardX = 24;
            int cardY = 60 + index * 25;
            addSlot(new GardenPlantSourceSlot(gardenTotem, index, plant, cardX + 7, cardY + 6));
        }

        addSlot(new TrashSlot(334, 206));
        setPlanterSlotsVisible(false);
    }

    public void setPlanterSlotsVisible(boolean visible) {
        planterSlotsVisible = visible;
    }

    private void addDataSlots() {
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return gardenTotem == null ? GardenTotemMenu.this.currentWave : gardenTotem.getCurrentWave();
            }

            @Override
            public void set(int value) {
                GardenTotemMenu.this.currentWave = value;
            }
        });
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return gardenTotem == null ? GardenTotemMenu.this.waveActive : gardenTotem.isWaveActive() ? 1 : 0;
            }

            @Override
            public void set(int value) {
                GardenTotemMenu.this.waveActive = value;
            }
        });
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return gardenTotem == null ? GardenTotemMenu.this.portalDiscoveryMask : gardenTotem.getPortalDiscoveryMask();
            }

            @Override
            public void set(int value) {
                GardenTotemMenu.this.portalDiscoveryMask = value;
            }
        });
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return gardenTotem == null ? GardenTotemMenu.this.currentPortalIndex : gardenTotem.getCurrentPortalIndex();
            }

            @Override
            public void set(int value) {
                GardenTotemMenu.this.currentPortalIndex = value;
            }
        });
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return gardenTotem == null ? GardenTotemMenu.this.gardenPortalIndex : gardenTotem.getGardenPortalIndex();
            }

            @Override
            public void set(int value) {
                GardenTotemMenu.this.gardenPortalIndex = value;
            }
        });
        for (int plantIndex = 0; plantIndex < PLANT_COUNT; plantIndex++) {
            final int index = plantIndex;
            addDataSlot(new DataSlot() {
                @Override
                public int get() {
                    return gardenTotem == null ? GardenTotemMenu.this.plantCounts[index] : gardenTotem.getGardenPlantCount(index);
                }

                @Override
                public void set(int value) {
                    GardenTotemMenu.this.plantCounts[index] = value;
                }
            });
            addDataSlot(new DataSlot() {
                @Override
                public int get() {
                    return gardenTotem == null ? GardenTotemMenu.this.plantRemainingSeconds[index] : gardenTotem.getGardenPlantRemainingSeconds(index);
                }

                @Override
                public void set(int value) {
                    GardenTotemMenu.this.plantRemainingSeconds[index] = value;
                }
            });
            addDataSlot(new DataSlot() {
                @Override
                public int get() {
                    return gardenTotem == null ? GardenTotemMenu.this.plantRefillSeconds[index] : gardenTotem.getGardenPlantRefillSeconds(index);
                }

                @Override
                public void set(int value) {
                    GardenTotemMenu.this.plantRefillSeconds[index] = value;
                }
            });
        }
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return gardenTotem == null ? GardenTotemMenu.this.gardenPacketCap : gardenTotem.getGardenPacketCap();
            }

            @Override
            public void set(int value) {
                GardenTotemMenu.this.gardenPacketCap = value;
            }
        });
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return gardenTotem == null ? GardenTotemMenu.this.shopPlantUnlockMask : gardenTotem.getShopPlantUnlockMask();
            }

            @Override
            public void set(int value) {
                GardenTotemMenu.this.shopPlantUnlockMask = value;
            }
        });
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return gardenTotem == null ? GardenTotemMenu.this.shopEntryAvailableMask : gardenTotem.getShopEntryAvailableMask(player);
            }

            @Override
            public void set(int value) {
                GardenTotemMenu.this.shopEntryAvailableMask = value;
            }
        });
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return gardenTotem == null ? GardenTotemMenu.this.coinCount : GreenhouseCoinManager.countCoins(player);
            }

            @Override
            public void set(int value) {
                GardenTotemMenu.this.coinCount = value;
            }
        });
    }

    public int currentWave() {
        return currentWave;
    }

    public boolean waveActive() {
        return waveActive != 0;
    }

    public boolean isPortalDiscovered(int index) {
        return (portalDiscoveryMask & (1 << index)) != 0;
    }

    public int currentPortalIndex() {
        return currentPortalIndex;
    }

    public net.PvZModders.PvZMod.progression.GardenId gardenId() {
        net.PvZModders.PvZMod.progression.GardenPortalOption[] options = net.PvZModders.PvZMod.progression.GardenPortalOption.values();
        int index = Math.max(0, Math.min(options.length - 1, gardenPortalIndex));
        return options[index].gardenId();
    }

    public int plantCount(int plantIndex) {
        return plantIndex >= 0 && plantIndex < plantCounts.length ? plantCounts[plantIndex] : 0;
    }

    public int plantRemainingSeconds(int plantIndex) {
        return plantIndex >= 0 && plantIndex < plantRemainingSeconds.length ? plantRemainingSeconds[plantIndex] : 0;
    }

    public int plantRefillSeconds(int plantIndex) {
        return plantIndex >= 0 && plantIndex < plantRefillSeconds.length ? Math.max(1, plantRefillSeconds[plantIndex]) : 1;
    }

    public int gardenPacketCap() {
        return Math.max(1, gardenPacketCap);
    }

    public boolean isPlantUnlockedFromShop(int plantIndex) {
        return plantIndex >= 0 && plantIndex < 31 && (shopPlantUnlockMask & (1 << plantIndex)) != 0;
    }

    public boolean isShopEntryAvailable(int entryIndex) {
        return entryIndex >= 0 && entryIndex < 31 && (shopEntryAvailableMask & (1 << entryIndex)) != 0;
    }

    public int coinCount() {
        return Math.max(0, coinCount);
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id == START_WAVE_BUTTON && gardenTotem != null && player instanceof ServerPlayer serverPlayer) {
            gardenTotem.startTotemDefense(serverPlayer);
            return true;
        }

        if (id >= PORTAL_BUTTON_OFFSET && id < PLANT_BUTTON_OFFSET && gardenTotem != null && player instanceof ServerPlayer serverPlayer) {
            gardenTotem.teleportToGarden(serverPlayer, id - PORTAL_BUTTON_OFFSET);
            return true;
        }

        if (id >= SEED_POLISH_BUTTON_OFFSET && gardenTotem != null && player instanceof ServerPlayer serverPlayer) {
            gardenTotem.useSeedPolishOnPlant(serverPlayer, id - SEED_POLISH_BUTTON_OFFSET);
            return true;
        }

        if (id >= WATERING_CAN_BUTTON_OFFSET && id < SEED_POLISH_BUTTON_OFFSET && gardenTotem != null && player instanceof ServerPlayer serverPlayer) {
            gardenTotem.useWateringCanOnPlant(serverPlayer, id - WATERING_CAN_BUTTON_OFFSET);
            return true;
        }

        if (id >= PLANT_BUTTON_OFFSET && id < SHOP_BUTTON_OFFSET && gardenTotem != null && player instanceof ServerPlayer serverPlayer) {
            gardenTotem.withdrawGardenPlantPacket(serverPlayer, id - PLANT_BUTTON_OFFSET);
            return true;
        }

        if (id >= SHOP_BUTTON_OFFSET && id < WATERING_CAN_BUTTON_OFFSET && gardenTotem != null && player instanceof ServerPlayer serverPlayer) {
            gardenTotem.purchaseDaveShopEntry(serverPlayer, id - SHOP_BUTTON_OFFSET);
            return true;
        }

        return false;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        if (index < 0 || index >= slots.size()) {
            return ItemStack.EMPTY;
        }

        Slot slot = slots.get(index);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();
        if (index >= GARDEN_SOURCE_SLOT_START && index < GARDEN_SOURCE_SLOT_START + GARDEN_SOURCE_SLOT_COUNT) {
            if (!moveItemStackTo(stack, SEED_STORAGE_SLOT_START, SEED_STORAGE_SLOT_START + SEED_STORAGE_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            return ItemStack.EMPTY;
        }

        if (stack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        return original;
    }

    @Override
    public void clicked(int slotId, int button, ClickType clickType, Player player) {
        if (slotId == -999 && SeedStorage.isPlantSeedPacket(getCarried())) {
            return;
        }
        if (clickType == ClickType.THROW
                && slotId >= SEED_STORAGE_SLOT_START
                && slotId < SEED_STORAGE_SLOT_START + SEED_STORAGE_SLOT_COUNT) {
            return;
        }
        super.clicked(slotId, button, clickType, player);
    }

    @Override
    public void removed(Player player) {
        ItemStack carried = getCarried();
        if (!player.level().isClientSide && SeedStorage.isPlantSeedPacket(carried)) {
            ItemStack leftover = returnSeedPacketStackToStorage(player, carried);
            if (!leftover.isEmpty()) {
                player.getInventory().placeItemBackInInventory(leftover);
            }
            setCarried(ItemStack.EMPTY);
        }
        super.removed(player);
    }

    private ItemStack returnSeedPacketStackToStorage(Player player, ItemStack stack) {
        ItemStack remaining = stack.copy();
        for (int index = 0; index < SEED_STORAGE_SLOT_COUNT && !remaining.isEmpty(); index++) {
            ItemStack current = seedStorageStacks[index];
            if (current.isEmpty()) {
                int moved = Math.min(remaining.getCount(), SeedStorage.getPlayerPacketCap(player));
                seedStorageStacks[index] = remaining.copyWithCount(moved);
                remaining.shrink(moved);
                SeedStorage.setPlantSlotByStorageIndex(player, index, seedStorageStacks[index]);
            } else if (ItemStack.isSameItemSameTags(current, remaining)) {
                int room = SeedStorage.getPlayerPacketCap(player) - current.getCount();
                if (room <= 0) {
                    continue;
                }
                int moved = Math.min(remaining.getCount(), room);
                current.grow(moved);
                remaining.shrink(moved);
                SeedStorage.setPlantSlotByStorageIndex(player, index, current);
            }
        }
        return remaining;
    }

    private final class SeedStorageSlot extends Slot {
        private static final Container EMPTY_CONTAINER = new SimpleContainer(1);
        private final Player player;
        private final int storageIndex;

        private SeedStorageSlot(Player player, int storageIndex, int x, int y) {
            super(EMPTY_CONTAINER, 0, x, y);
            this.player = player;
            this.storageIndex = storageIndex;
        }

        @Override
        public ItemStack getItem() {
            return seedStorageStacks[storageIndex];
        }

        @Override
        public void set(ItemStack stack) {
            seedStorageStacks[storageIndex] = stack.isEmpty() ? ItemStack.EMPTY : stack.copyWithCount(Math.min(stack.getCount(), SeedStorage.getPlayerPacketCap(player)));
            setChanged();
        }

        @Override
        public void setChanged() {
            SeedStorage.setPlantSlotByStorageIndex(player, storageIndex, seedStorageStacks[storageIndex]);
        }

        @Override
        public ItemStack remove(int amount) {
            ItemStack current = getItem();
            if (current.isEmpty()) {
                return ItemStack.EMPTY;
            }
            ItemStack removed = current.split(amount);
            set(current);
            return removed;
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return stack.isEmpty() || SeedStorage.isPlantSeedPacket(stack);
        }

        @Override
        public int getMaxStackSize() {
            return SeedStorage.getPlayerPacketCap(player);
        }

        @Override
        public boolean isActive() {
            return planterSlotsVisible;
        }
    }

    private final class GardenPlantSourceSlot extends Slot {
        private static final Container EMPTY_CONTAINER = new SimpleContainer(1);
        private final GardenTotemBlockEntity gardenTotem;
        private final int plantIndex;
        private final GardenPlantDefinition plant;

        private GardenPlantSourceSlot(GardenTotemBlockEntity gardenTotem, int plantIndex, GardenPlantDefinition plant, int x, int y) {
            super(EMPTY_CONTAINER, 0, x, y);
            this.gardenTotem = gardenTotem;
            this.plantIndex = plantIndex;
            this.plant = plant;
        }

        @Override
        public ItemStack getItem() {
            if (plant == null || gardenTotem == null || !gardenTotem.isGardenPlantUnlocked(plantIndex) || gardenTotem.getGardenPlantCount(plantIndex) <= 0) {
                return ItemStack.EMPTY;
            }
            return new ItemStack(BuiltInRegistries.ITEM.get(plant.seedPacketId()), gardenTotem.getGardenPlantCount(plantIndex));
        }

        @Override
        public ItemStack remove(int amount) {
            if (gardenTotem == null) {
                return ItemStack.EMPTY;
            }
            return gardenTotem.removeGardenPlantPackets(plantIndex, amount);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return false;
        }

        @Override
        public boolean mayPickup(Player player) {
            return !getItem().isEmpty();
        }

        @Override
        public boolean isActive() {
            return false;
        }
    }

    private final class TrashSlot extends Slot {
        private static final Container EMPTY_CONTAINER = new SimpleContainer(1);

        private TrashSlot(int x, int y) {
            super(EMPTY_CONTAINER, 0, x, y);
        }

        @Override
        public ItemStack getItem() {
            return ItemStack.EMPTY;
        }

        @Override
        public void set(ItemStack stack) {
            setChanged();
        }

        @Override
        public ItemStack remove(int amount) {
            return ItemStack.EMPTY;
        }

        @Override
        public boolean mayPickup(Player player) {
            return false;
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return SeedStorage.isPlantSeedPacket(stack);
        }

        @Override
        public boolean isActive() {
            return planterSlotsVisible;
        }
    }
}

package net.PvZModders.PvZMod.menu;

import net.PvZModders.PvZMod.block.entity.GardenTotemBlockEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;

public class GardenTotemMenu extends AbstractContainerMenu {
    public static final int START_WAVE_BUTTON = 0;
    public static final int PORTAL_BUTTON_OFFSET = 100;

    private final GardenTotemBlockEntity gardenTotem;
    private int currentWave;
    private int waveActive;
    private int portalDiscoveryMask;
    private int currentPortalIndex;

    public GardenTotemMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, 1, false);
    }

    public GardenTotemMenu(int containerId, Inventory playerInventory, int currentWave, boolean waveActive) {
        super(ModMenuTypes.GARDEN_TOTEM.get(), containerId);
        this.gardenTotem = null;
        this.currentWave = currentWave;
        this.waveActive = waveActive ? 1 : 0;
        this.portalDiscoveryMask = 1;
        this.currentPortalIndex = 0;
        addDataSlots();
    }

    public GardenTotemMenu(int containerId, Inventory playerInventory, GardenTotemBlockEntity gardenTotem) {
        super(ModMenuTypes.GARDEN_TOTEM.get(), containerId);
        this.gardenTotem = gardenTotem;
        this.currentWave = gardenTotem.getCurrentWave();
        this.waveActive = gardenTotem.isWaveActive() ? 1 : 0;
        this.portalDiscoveryMask = gardenTotem.getPortalDiscoveryMask();
        this.currentPortalIndex = gardenTotem.getCurrentPortalIndex();
        addDataSlots();
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

        if (id >= PORTAL_BUTTON_OFFSET && gardenTotem != null && player instanceof ServerPlayer serverPlayer) {
            gardenTotem.teleportToGarden(serverPlayer, id - PORTAL_BUTTON_OFFSET);
            return true;
        }

        return false;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }
}

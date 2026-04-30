package net.PvZModders.PvZMod.menu;

import net.PvZModders.PvZMod.block.entity.GardenTotemBlockEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;

public class GardenTotemMenu extends AbstractContainerMenu {
    private static final int START_WAVE_BUTTON = 0;

    private final GardenTotemBlockEntity gardenTotem;
    private int currentWave;
    private int waveActive;

    public GardenTotemMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, 1, false);
    }

    public GardenTotemMenu(int containerId, Inventory playerInventory, int currentWave, boolean waveActive) {
        super(ModMenuTypes.GARDEN_TOTEM.get(), containerId);
        this.gardenTotem = null;
        this.currentWave = currentWave;
        this.waveActive = waveActive ? 1 : 0;
        addWaveDataSlots();
    }

    public GardenTotemMenu(int containerId, Inventory playerInventory, GardenTotemBlockEntity gardenTotem) {
        super(ModMenuTypes.GARDEN_TOTEM.get(), containerId);
        this.gardenTotem = gardenTotem;
        this.currentWave = gardenTotem.getCurrentWave();
        this.waveActive = gardenTotem.isWaveActive() ? 1 : 0;
        addWaveDataSlots();
    }

    private void addWaveDataSlots() {
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
    }

    public int currentWave() {
        return currentWave;
    }

    public boolean waveActive() {
        return waveActive != 0;
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

        return false;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }
}

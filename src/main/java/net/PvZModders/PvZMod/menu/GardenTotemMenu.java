package net.PvZModders.PvZMod.menu;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;

public class GardenTotemMenu extends AbstractContainerMenu {
    private int currentWave;
    private int waveActive;

    public GardenTotemMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, 1, false);
    }

    public GardenTotemMenu(int containerId, Inventory playerInventory, int currentWave, boolean waveActive) {
        super(ModMenuTypes.GARDEN_TOTEM.get(), containerId);
        this.currentWave = currentWave;
        this.waveActive = waveActive ? 1 : 0;
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return GardenTotemMenu.this.currentWave;
            }

            @Override
            public void set(int value) {
                GardenTotemMenu.this.currentWave = value;
            }
        });
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return GardenTotemMenu.this.waveActive;
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
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }
}

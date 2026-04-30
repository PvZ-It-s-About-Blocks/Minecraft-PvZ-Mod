package net.PvZModders.PvZMod.menu;

import net.PvZModders.PvZMod.item.custom.BiomeDetectorItem;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public class BiomeDetectorMenu extends AbstractContainerMenu {
    private final ItemStack detectorStack;

    public BiomeDetectorMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, ItemStack.EMPTY);
    }

    public BiomeDetectorMenu(int containerId, Inventory playerInventory, ItemStack detectorStack) {
        super(ModMenuTypes.BIOME_DETECTOR.get(), containerId);
        this.detectorStack = detectorStack;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (player instanceof ServerPlayer serverPlayer && BiomeDetectorItem.selectTarget(detectorStack, serverPlayer, id)) {
            serverPlayer.closeContainer();
            return true;
        }
        return false;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }
}

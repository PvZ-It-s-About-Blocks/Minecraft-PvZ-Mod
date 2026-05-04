package net.PvZModders.PvZMod.client.screen;

import net.PvZModders.PvZMod.menu.BiomeDetectorMenu;
import net.PvZModders.PvZMod.progression.GardenBiomeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class BiomeDetectorScreen extends AbstractContainerScreen<BiomeDetectorMenu> {
    private static final int OPTION_WIDTH = 112;
    private static final int OPTION_HEIGHT = 20;

    public BiomeDetectorScreen(BiomeDetectorMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        imageWidth = 260;
        imageHeight = 178;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = leftPos;
        int y = topPos;
        guiGraphics.fill(x, y, x + imageWidth, y + imageHeight, 0xFF1F1F1F);
        guiGraphics.fill(x + 4, y + 4, x + imageWidth - 4, y + imageHeight - 4, 0xFFC6C6C6);
        guiGraphics.fill(x + 12, y + 30, x + imageWidth - 12, y + imageHeight - 12, 0xFF8F8F8F);

        GardenBiomeCategory[] categories = GardenBiomeCategory.values();
        for (int i = 0; i < categories.length; i++) {
            renderOption(guiGraphics, categories[i], i, mouseX, mouseY);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(font, "Biome Detector", 14, 12, 0x2F2F2F, false);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            GardenBiomeCategory[] categories = GardenBiomeCategory.values();
            for (int i = 0; i < categories.length; i++) {
                if (isMouseOverOption(i, mouseX, mouseY)) {
                    Minecraft minecraft = Minecraft.getInstance();
                    if (minecraft.gameMode != null) {
                        minecraft.gameMode.handleInventoryButtonClick(menu.containerId, i);
                    }
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void renderOption(GuiGraphics guiGraphics, GardenBiomeCategory category, int index, int mouseX, int mouseY) {
        int optionX = getOptionX(index);
        int optionY = getOptionY(index);
        boolean hovered = isMouseOverOption(index, mouseX, mouseY);
        int fill = hovered ? lighten(category.color()) : category.color();

        guiGraphics.fill(optionX, optionY, optionX + OPTION_WIDTH, optionY + OPTION_HEIGHT, 0xFF1F1F1F);
        guiGraphics.fill(optionX + 2, optionY + 2, optionX + OPTION_WIDTH - 2, optionY + OPTION_HEIGHT - 2, 0xFF000000 | fill);
        guiGraphics.drawString(font, category.displayName(), optionX + 6, optionY + 6, 0xFFFFFF, true);
    }

    private int getOptionX(int index) {
        return leftPos + 14 + (index % 2) * (OPTION_WIDTH + 8);
    }

    private int getOptionY(int index) {
        return topPos + 38 + (index / 2) * (OPTION_HEIGHT + 4);
    }

    private boolean isMouseOverOption(int index, double mouseX, double mouseY) {
        int optionX = getOptionX(index);
        int optionY = getOptionY(index);
        return mouseX >= optionX && mouseX < optionX + OPTION_WIDTH && mouseY >= optionY && mouseY < optionY + OPTION_HEIGHT;
    }

    private int lighten(int color) {
        int r = Math.min(255, ((color >> 16) & 255) + 32);
        int g = Math.min(255, ((color >> 8) & 255) + 32);
        int b = Math.min(255, (color & 255) + 32);
        return (r << 16) | (g << 8) | b;
    }
}

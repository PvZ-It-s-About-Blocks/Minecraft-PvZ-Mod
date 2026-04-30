package net.PvZModders.PvZMod.client.screen;

import net.PvZModders.PvZMod.menu.GardenTotemMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class GardenTotemScreen extends AbstractContainerScreen<GardenTotemMenu> {
    private static final int TAB_PROGRESS = 0;
    private static final int TAB_PORTAL = 1;
    private static final int TAB_PLANTER = 2;
    private int selectedTab = TAB_PROGRESS;

    public GardenTotemScreen(GardenTotemMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        imageWidth = 300;
        imageHeight = 180;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = leftPos;
        int y = topPos;
        renderFrame(guiGraphics, x, y);
        renderTabs(guiGraphics, x, y, mouseX, mouseY);
        renderSelectedTab(guiGraphics, x, y);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(font, title, 14, 10, 0x3F3F3F, false);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            for (int tab = 0; tab < 3; tab++) {
                if (isMouseOverTab(tab, mouseX, mouseY)) {
                    selectedTab = tab;
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void renderFrame(GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.fill(x, y, x + imageWidth, y + imageHeight, 0xFF1F1F1F);
        guiGraphics.fill(x + 4, y + 4, x + imageWidth - 4, y + imageHeight - 4, 0xFFC6C6C6);
        guiGraphics.fill(x + 10, y + 34, x + imageWidth - 10, y + imageHeight - 12, 0xFFEFE4B0);
        guiGraphics.fill(x + 12, y + 36, x + imageWidth - 12, y + imageHeight - 14, 0xFFF6EDC8);
        drawPanelNoise(guiGraphics, x + 12, y + 36, imageWidth - 24, imageHeight - 50);
    }

    private void drawPanelNoise(GuiGraphics guiGraphics, int x, int y, int width, int height) {
        for (int ix = 0; ix < width; ix += 8) {
            for (int iy = 0; iy < height; iy += 8) {
                int shade = ((ix + iy) / 8) % 2 == 0 ? 0x22D6C57D : 0x229C8B55;
                guiGraphics.fill(x + ix, y + iy, Math.min(x + ix + 4, x + width), Math.min(y + iy + 4, y + height), shade);
            }
        }
    }

    private void renderTabs(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY) {
        renderTab(guiGraphics, x, y, TAB_PROGRESS, new ItemStack(Items.EXPERIENCE_BOTTLE), "Progress", mouseX, mouseY);
        renderTab(guiGraphics, x, y, TAB_PORTAL, new ItemStack(Items.ENDER_PEARL), "Portal", mouseX, mouseY);
        renderTab(guiGraphics, x, y, TAB_PLANTER, new ItemStack(Items.GRASS_BLOCK), "Planter", mouseX, mouseY);
    }

    private void renderTab(GuiGraphics guiGraphics, int x, int y, int tab, ItemStack icon, String tooltip, int mouseX, int mouseY) {
        int tabX = x + 20 + tab * 34;
        int tabY = y - 24;
        boolean selected = selectedTab == tab;
        boolean hovered = isMouseOverTab(tab, mouseX, mouseY);
        int border = selected ? 0xFF1F1F1F : 0xFF555555;
        int fill = selected ? 0xFFC6C6C6 : hovered ? 0xFFB0B0B0 : 0xFF8F8F8F;

        guiGraphics.fill(tabX, tabY, tabX + 30, tabY + 28, border);
        guiGraphics.fill(tabX + 3, tabY + 3, tabX + 27, tabY + 27, fill);
        guiGraphics.renderItem(icon, tabX + 7, tabY + 7);

        if (hovered) {
            guiGraphics.renderTooltip(font, Component.literal(tooltip), mouseX, mouseY);
        }
    }

    private void renderSelectedTab(GuiGraphics guiGraphics, int x, int y) {
        if (selectedTab == TAB_PROGRESS) {
            renderProgressTab(guiGraphics, x, y);
        } else if (selectedTab == TAB_PORTAL) {
            renderPortalTab(guiGraphics, x, y);
        } else {
            renderPlanterTab(guiGraphics, x, y);
        }
    }

    private void renderProgressTab(GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.drawString(font, Component.literal("Progress").withStyle(ChatFormatting.DARK_GRAY), x + 24, y + 48, 0x3F3F3F, false);
        int startX = x + 64;
        int centerY = y + 105;
        drawNode(guiGraphics, startX, centerY, new ItemStack(Items.GRASS_BLOCK), true);
        drawLine(guiGraphics, startX + 26, centerY + 12, startX + 86, centerY + 12);
        drawNode(guiGraphics, startX + 90, centerY, new ItemStack(Items.WHEAT_SEEDS), false);
        drawLine(guiGraphics, startX + 116, centerY + 12, startX + 176, centerY + 12);
        drawNode(guiGraphics, startX + 180, centerY, new ItemStack(Items.SUNFLOWER), false);
    }

    private void renderPortalTab(GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.drawString(font, Component.literal("Portal").withStyle(ChatFormatting.DARK_GRAY), x + 24, y + 48, 0x3F3F3F, false);
        guiGraphics.drawString(font, Component.literal("Current garden").withStyle(ChatFormatting.DARK_GREEN), x + 70, y + 96, 0x2F6F2F, false);
        drawNode(guiGraphics, x + 34, y + 84, new ItemStack(Items.ENDER_PEARL), true);
    }

    private void renderPlanterTab(GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.drawString(font, Component.literal("Planter").withStyle(ChatFormatting.DARK_GRAY), x + 24, y + 48, 0x3F3F3F, false);
        guiGraphics.drawString(font, Component.literal("Plant growth location placeholder").withStyle(ChatFormatting.DARK_GREEN), x + 54, y + 96, 0x2F6F2F, false);
        drawNode(guiGraphics, x + 34, y + 84, new ItemStack(Items.GRASS_BLOCK), true);
    }

    private void drawNode(GuiGraphics guiGraphics, int x, int y, ItemStack icon, boolean unlocked) {
        int border = unlocked ? 0xFF6B5100 : 0xFF1F1F1F;
        int fill = unlocked ? 0xFFC99A00 : 0xFFC6C6C6;
        guiGraphics.fill(x, y, x + 28, y + 28, border);
        guiGraphics.fill(x + 3, y + 3, x + 25, y + 25, fill);
        guiGraphics.renderItem(icon, x + 6, y + 6);
    }

    private void drawLine(GuiGraphics guiGraphics, int x1, int y1, int x2, int y2) {
        guiGraphics.fill(x1, y1 - 1, x2, y2 + 1, 0xFF1F1F1F);
    }

    private boolean isMouseOverTab(int tab, double mouseX, double mouseY) {
        int tabX = leftPos + 20 + tab * 34;
        int tabY = topPos - 24;
        return mouseX >= tabX && mouseX < tabX + 30 && mouseY >= tabY && mouseY < tabY + 28;
    }
}

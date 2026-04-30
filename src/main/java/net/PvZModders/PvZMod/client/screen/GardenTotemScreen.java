package net.PvZModders.PvZMod.client.screen;

import net.PvZModders.PvZMod.menu.GardenTotemMenu;
import net.PvZModders.PvZMod.progression.waves.GardenWaveDefinition;
import net.PvZModders.PvZMod.progression.waves.OriginalGardenWaves;
import net.PvZModders.PvZMod.progression.waves.WaveReward;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class GardenTotemScreen extends AbstractContainerScreen<GardenTotemMenu> {
    private static final int TAB_PROGRESS = 0;
    private static final int TAB_PORTAL = 1;
    private static final int TAB_PLANTER = 2;
    private static final int TAB_SIZE = 30;
    private static final int TAB_SPACING = 4;
    private static final int TAB_COUNT = 3;
    private static final int START_WAVE_BUTTON = 0;
    private static final int WAVE_CANVAS_WIDTH = 940;
    private static final int WAVE_CANVAS_HEIGHT = 330;
    private static final int WAVE_NODE_SIZE = 24;
    private int selectedTab = TAB_PROGRESS;
    private int selectedWave = 1;
    private double waveCanvasX;
    private double waveCanvasY;
    private boolean draggingWaveCanvas;

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
        guiGraphics.drawString(font, title, 14, 10, 0x1F8F2F, false);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            for (int tab = 0; tab < 3; tab++) {
                if (isMouseOverTab(tab, mouseX, mouseY)) {
                    selectedTab = tab;
                    draggingWaveCanvas = false;
                    return true;
                }
            }

            if (selectedTab == TAB_PROGRESS) {
                if (isMouseOverStartWave(mouseX, mouseY)) {
                    Minecraft minecraft = Minecraft.getInstance();
                    if (minecraft.gameMode != null) {
                        minecraft.gameMode.handleInventoryButtonClick(menu.containerId, START_WAVE_BUTTON);
                    }
                    return true;
                }

                int hoveredWave = getHoveredWave(mouseX, mouseY);
                if (hoveredWave > 0) {
                    selectedWave = hoveredWave;
                    return true;
                }

                if (isMouseOverWaveCanvas(mouseX, mouseY)) {
                    draggingWaveCanvas = true;
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (button == 0 && selectedTab == TAB_PROGRESS && draggingWaveCanvas) {
            waveCanvasX = clampWaveCanvasX(waveCanvasX + dragX);
            waveCanvasY = clampWaveCanvasY(waveCanvasY + dragY);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            draggingWaveCanvas = false;
        }
        return super.mouseReleased(mouseX, mouseY, button);
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
        renderTab(guiGraphics, x, y, TAB_PROGRESS, new ItemStack(Items.EXPERIENCE_BOTTLE), "Waves", mouseX, mouseY);
        renderTab(guiGraphics, x, y, TAB_PORTAL, new ItemStack(Items.ENDER_PEARL), "Portal", mouseX, mouseY);
        renderTab(guiGraphics, x, y, TAB_PLANTER, new ItemStack(Items.GRASS_BLOCK), "Planter", mouseX, mouseY);
    }

    private void renderTab(GuiGraphics guiGraphics, int x, int y, int tab, ItemStack icon, String tooltip, int mouseX, int mouseY) {
        int tabX = getTabX(x, tab);
        int tabY = y - 24;
        boolean selected = selectedTab == tab;
        boolean hovered = isMouseOverTab(tab, mouseX, mouseY);
        int border = selected ? 0xFF1F1F1F : 0xFF555555;
        int fill = selected ? 0xFFC6C6C6 : hovered ? 0xFFB0B0B0 : 0xFF8F8F8F;

        guiGraphics.fill(tabX, tabY, tabX + TAB_SIZE, tabY + 28, border);
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
        guiGraphics.drawString(font, Component.literal("Waves").withStyle(ChatFormatting.DARK_GRAY), x + 24, y + 48, 0x3F3F3F, false);
        guiGraphics.drawString(font, Component.literal("Current: " + menu.currentWave() + "/30" + (menu.waveActive() ? " Active" : " Ready")).withStyle(ChatFormatting.DARK_GREEN), x + 176, y + 48, 0x2F6F2F, false);

        int canvasX = getWaveCanvasScreenX();
        int canvasY = getWaveCanvasScreenY();
        int canvasW = getWaveCanvasScreenWidth();
        int canvasH = getWaveCanvasScreenHeight();

        guiGraphics.enableScissor(canvasX, canvasY, canvasX + canvasW, canvasY + canvasH);
        drawPanelNoise(guiGraphics, canvasX + (int) waveCanvasX, canvasY + (int) waveCanvasY, WAVE_CANVAS_WIDTH, WAVE_CANVAS_HEIGHT);

        for (GardenWaveDefinition wave : OriginalGardenWaves.all()) {
            int nodeX = canvasX + getWaveVirtualX(wave.wave()) + (int) waveCanvasX;
            int nodeY = canvasY + getWaveVirtualY(wave.wave()) + (int) waveCanvasY;
            if (wave.wave() > 1) {
                int previousX = canvasX + getWaveVirtualX(wave.wave() - 1) + (int) waveCanvasX;
                int previousY = canvasY + getWaveVirtualY(wave.wave() - 1) + (int) waveCanvasY;
                drawWaveLine(guiGraphics, previousX + WAVE_NODE_SIZE / 2, previousY + WAVE_NODE_SIZE / 2, nodeX + WAVE_NODE_SIZE / 2, nodeY + WAVE_NODE_SIZE / 2, wave.wave());
            }

            guiGraphics.drawString(font, String.valueOf(wave.wave()), nodeX + 8, nodeY - 12, 0x3F3F3F, false);
            if (!wave.rewards().isEmpty()) {
                renderRewardIcons(guiGraphics, wave, nodeX + 4, nodeY - 32);
            }
            drawWaveNode(guiGraphics, nodeX, nodeY, wave.wave());
        }
        guiGraphics.disableScissor();

        GardenWaveDefinition selected = OriginalGardenWaves.get(selectedWave);
        renderSelectedWaveBox(guiGraphics, x, y, selected);
    }

    private void renderRewardIcons(GuiGraphics guiGraphics, GardenWaveDefinition wave, int x, int y) {
        int iconX = x;
        for (WaveReward reward : wave.rewards()) {
            guiGraphics.renderItem(itemFromId(reward.iconItemId()), iconX, y);
            iconX += 16;
        }
    }

    private void drawWaveNode(GuiGraphics guiGraphics, int x, int y, int wave) {
        boolean completed = wave < menu.currentWave();
        boolean current = wave == menu.currentWave();
        boolean selected = wave == selectedWave;
        int border = selected ? 0xFFFFFFFF : 0xFF1F1F1F;
        int fill = completed ? 0xFF3F9F3F : current ? 0xFF3366CC : 0xFF363636;
        guiGraphics.fill(x, y, x + WAVE_NODE_SIZE, y + WAVE_NODE_SIZE, border);
        guiGraphics.fill(x + 4, y + 4, x + WAVE_NODE_SIZE - 4, y + WAVE_NODE_SIZE - 4, fill);
    }

    private void drawWaveLine(GuiGraphics guiGraphics, int x1, int y1, int x2, int y2, int wave) {
        int color = wave <= menu.currentWave() ? 0xFFBFBFBF : 0xFF050505;
        if (x1 == x2 || y1 == y2) {
            guiGraphics.fill(Math.min(x1, x2), Math.min(y1, y2) - 1, Math.max(x1, x2) + 1, Math.max(y1, y2) + 1, color);
            return;
        }

        guiGraphics.fill(Math.min(x1, x2), y1 - 1, Math.max(x1, x2) + 1, y1 + 1, color);
        guiGraphics.fill(x2 - 1, Math.min(y1, y2), x2 + 1, Math.max(y1, y2) + 1, color);
    }

    private void renderSelectedWaveBox(GuiGraphics guiGraphics, int x, int y, GardenWaveDefinition selected) {
        int boxX = x + 20;
        int boxY = y + 142;
        int boxW = imageWidth - 40;
        int boxH = 30;
        boolean completed = selected.wave() < menu.currentWave();
        boolean current = selected.wave() == menu.currentWave();
        int fill = completed ? 0x883F9F3F : current ? 0x883366CC : 0x88363636;
        int border = completed ? 0xFF3F9F3F : current ? 0xFF3366CC : 0xFF111111;

        guiGraphics.fill(boxX, boxY, boxX + boxW, boxY + boxH, border);
        guiGraphics.fill(boxX + 2, boxY + 2, boxX + boxW - 2, boxY + boxH - 2, fill);
        guiGraphics.drawString(font, Component.literal("Scans: " + selected.scanText()).withStyle(ChatFormatting.DARK_GRAY), boxX + 6, boxY + 6, 0x3F3F3F, false);

        if (current && !menu.waveActive()) {
            int buttonX = boxX + boxW - 82;
            int buttonY = boxY + 8;
            guiGraphics.fill(buttonX, buttonY, buttonX + 76, buttonY + 16, 0xFF1F1F1F);
            guiGraphics.fill(buttonX + 2, buttonY + 2, buttonX + 74, buttonY + 14, 0xFF3366CC);
            guiGraphics.drawString(font, "Start Wave?", buttonX + 7, buttonY + 5, 0xFFFFFF, false);
        }
    }

    private int getHoveredWave(double mouseX, double mouseY) {
        if (!isMouseOverWaveCanvas(mouseX, mouseY)) {
            return -1;
        }

        for (int wave = 1; wave <= OriginalGardenWaves.MAX_WAVE; wave++) {
            int nodeX = getWaveCanvasScreenX() + getWaveVirtualX(wave) + (int) waveCanvasX;
            int nodeY = getWaveCanvasScreenY() + getWaveVirtualY(wave) + (int) waveCanvasY;
            if (mouseX >= nodeX && mouseX < nodeX + WAVE_NODE_SIZE && mouseY >= nodeY && mouseY < nodeY + WAVE_NODE_SIZE) {
                return wave;
            }
        }

        return -1;
    }

    private ItemStack itemFromId(String itemId) {
        return new ItemStack(BuiltInRegistries.ITEM.get(new ResourceLocation(itemId)));
    }

    private boolean isMouseOverStartWave(double mouseX, double mouseY) {
        if (selectedWave != menu.currentWave() || menu.waveActive()) {
            return false;
        }

        int boxX = leftPos + 20;
        int boxY = topPos + 142;
        int buttonX = boxX + imageWidth - 40 - 82;
        int buttonY = boxY + 8;
        return mouseX >= buttonX && mouseX < buttonX + 76 && mouseY >= buttonY && mouseY < buttonY + 16;
    }

    private boolean isMouseOverWaveCanvas(double mouseX, double mouseY) {
        int canvasX = getWaveCanvasScreenX();
        int canvasY = getWaveCanvasScreenY();
        return mouseX >= canvasX && mouseX < canvasX + getWaveCanvasScreenWidth()
                && mouseY >= canvasY && mouseY < canvasY + getWaveCanvasScreenHeight();
    }

    private int getWaveCanvasScreenX() {
        return leftPos + 14;
    }

    private int getWaveCanvasScreenY() {
        return topPos + 62;
    }

    private int getWaveCanvasScreenWidth() {
        return imageWidth - 28;
    }

    private int getWaveCanvasScreenHeight() {
        return 74;
    }

    private int getWaveVirtualX(int wave) {
        int index = wave - 1;
        int row = index / 10;
        int column = index % 10;
        if (row % 2 == 1) {
            column = 9 - column;
        }
        return 34 + column * 88;
    }

    private int getWaveVirtualY(int wave) {
        int row = (wave - 1) / 10;
        return 50 + row * 92;
    }

    private double clampWaveCanvasX(double value) {
        return Math.max(getWaveCanvasScreenWidth() - WAVE_CANVAS_WIDTH, Math.min(0.0D, value));
    }

    private double clampWaveCanvasY(double value) {
        return Math.max(getWaveCanvasScreenHeight() - WAVE_CANVAS_HEIGHT, Math.min(0.0D, value));
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

    private boolean isMouseOverTab(int tab, double mouseX, double mouseY) {
        int tabX = getTabX(leftPos, tab);
        int tabY = topPos - 24;
        return mouseX >= tabX && mouseX < tabX + TAB_SIZE && mouseY >= tabY && mouseY < tabY + 28;
    }

    private int getTabX(int x, int tab) {
        int totalWidth = TAB_COUNT * TAB_SIZE + (TAB_COUNT - 1) * TAB_SPACING;
        return x + (imageWidth - totalWidth) / 2 + tab * (TAB_SIZE + TAB_SPACING);
    }
}

package net.PvZModders.PvZMod.client.screen;

import net.PvZModders.PvZMod.menu.GardenTotemMenu;
import net.PvZModders.PvZMod.progression.GardenPortalOption;
import net.PvZModders.PvZMod.progression.plants.GardenPlantDefinition;
import net.PvZModders.PvZMod.progression.plants.GardenPlantProductionSavedData;
import net.PvZModders.PvZMod.progression.waves.GardenWaveDefinition;
import net.PvZModders.PvZMod.progression.waves.OriginalGardenWaves;
import net.PvZModders.PvZMod.progression.waves.WaveReward;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import java.util.List;

public class GardenTotemScreen extends AbstractContainerScreen<GardenTotemMenu> {
    private static final int TAB_PROGRESS = 0;
    private static final int TAB_PORTAL = 1;
    private static final int TAB_PLANTER = 2;
    private static final int TAB_SHOP = 3;
    private static final int TAB_SIZE = 30;
    private static final int TAB_SPACING = 4;
    private static final int TAB_COUNT = 4;
    private static final int START_WAVE_BUTTON = 0;
    private static final int WAVE_SPACING = 72;
    private static final int WAVE_CANVAS_WIDTH = 34 + (OriginalGardenWaves.MAX_WAVE - 1) * WAVE_SPACING + 80;
    private static final int WAVE_CANVAS_HEIGHT = 74;
    private static final int WAVE_NODE_SIZE = 24;
    private int selectedTab = TAB_PROGRESS;
    private int selectedWave = 1;
    private double waveCanvasX;
    private boolean draggingWaveCanvas;
    private boolean waveCanvasFocused;
    private int lastFocusedWave = -1;
    private Component hoveredRewardTooltip;
    private List<Component> hoveredPlantTooltip;

    public GardenTotemScreen(GardenTotemMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        imageWidth = 360;
        imageHeight = 230;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = leftPos;
        int y = topPos;
        renderFrame(guiGraphics, x, y);
        renderTabs(guiGraphics, x, y, mouseX, mouseY);
        renderSelectedTab(guiGraphics, x, y, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(font, title, 14, 10, gardenColor(), false);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            for (int tab = 0; tab < TAB_COUNT; tab++) {
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
                    waveCanvasFocused = true;
                    return true;
                }

                if (isMouseOverWaveCanvas(mouseX, mouseY)) {
                    draggingWaveCanvas = true;
                    waveCanvasFocused = true;
                    return true;
                }
            } else if (selectedTab == TAB_PORTAL) {
                int hoveredPortal = getHoveredPortal(mouseX, mouseY);
                if (hoveredPortal >= 0) {
                    Minecraft minecraft = Minecraft.getInstance();
                    if (minecraft.gameMode != null) {
                        minecraft.gameMode.handleInventoryButtonClick(menu.containerId, GardenTotemMenu.PORTAL_BUTTON_OFFSET + hoveredPortal);
                    }
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
        renderTab(guiGraphics, x, y, TAB_SHOP, new ItemStack(Items.VILLAGER_SPAWN_EGG), "Crazy Dave's Shop", mouseX, mouseY);
    }

    private void renderTab(GuiGraphics guiGraphics, int x, int y, int tab, ItemStack icon, String tooltip, int mouseX, int mouseY) {
        int tabX = getTabX(x, tab);
        int tabY = y - 10;
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

    private void renderSelectedTab(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY) {
        menu.setPlanterSlotsVisible(selectedTab == TAB_PLANTER);
        if (selectedTab == TAB_PROGRESS) {
            renderProgressTab(guiGraphics, x, y, mouseX, mouseY);
        } else if (selectedTab == TAB_PORTAL) {
            renderPortalTab(guiGraphics, x, y);
        } else if (selectedTab == TAB_PLANTER) {
            renderPlanterTab(guiGraphics, x, y, mouseX, mouseY);
        } else {
            renderShopTab(guiGraphics, x, y);
        }
    }

    private void renderProgressTab(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY) {
        focusCurrentWaveIfNeeded();
        guiGraphics.drawString(font, Component.literal("Waves").withStyle(ChatFormatting.DARK_GRAY), x + 24, y + 48, 0x3F3F3F, false);

        int canvasX = getWaveCanvasScreenX();
        int canvasY = getWaveCanvasScreenY();
        int canvasW = getWaveCanvasScreenWidth();
        int canvasH = getWaveCanvasScreenHeight();

        hoveredRewardTooltip = null;
        guiGraphics.enableScissor(canvasX, canvasY, canvasX + canvasW, canvasY + canvasH);
        drawPanelNoise(guiGraphics, canvasX + (int) waveCanvasX, canvasY, WAVE_CANVAS_WIDTH, WAVE_CANVAS_HEIGHT);

        for (GardenWaveDefinition wave : OriginalGardenWaves.all()) {
            int nodeX = canvasX + getWaveVirtualX(wave.wave()) + (int) waveCanvasX;
            int nodeY = canvasY + getWaveVirtualY();
            if (wave.wave() > 1) {
                int previousX = canvasX + getWaveVirtualX(wave.wave() - 1) + (int) waveCanvasX;
                int previousY = canvasY + getWaveVirtualY();
                drawWaveLine(guiGraphics, previousX + WAVE_NODE_SIZE, previousY + WAVE_NODE_SIZE / 2, nodeX, nodeY + WAVE_NODE_SIZE / 2, wave.wave());
            }

            guiGraphics.drawString(font, String.valueOf(wave.wave()), nodeX + 8, nodeY - 12, 0x3F3F3F, false);
            if (!wave.rewards().isEmpty()) {
                renderRewardIcons(guiGraphics, wave, nodeX + 4, nodeY - 32, mouseX, mouseY);
            }
            drawWaveNode(guiGraphics, nodeX, nodeY, wave.wave());
        }
        guiGraphics.disableScissor();
        if (hoveredRewardTooltip != null) {
            guiGraphics.renderTooltip(font, hoveredRewardTooltip, mouseX, mouseY);
        }

        GardenWaveDefinition selected = OriginalGardenWaves.get(selectedWave);
        renderSelectedWaveBox(guiGraphics, x, y, selected);
    }

    private void renderRewardIcons(GuiGraphics guiGraphics, GardenWaveDefinition wave, int x, int y, int mouseX, int mouseY) {
        int iconX = x;
        for (WaveReward reward : wave.rewards()) {
            guiGraphics.renderItem(itemFromId(reward.iconItemId()), iconX, y);
            if (mouseX >= iconX && mouseX < iconX + 16 && mouseY >= y && mouseY < y + 16) {
                hoveredRewardTooltip = Component.literal("Unlock: " + reward.displayName());
            }
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
        int buttonW = 86;
        int scanW = current && !menu.waveActive() ? boxW - buttonW - 8 : boxW;

        guiGraphics.fill(boxX, boxY, boxX + scanW, boxY + boxH, border);
        guiGraphics.fill(boxX + 2, boxY + 2, boxX + scanW - 2, boxY + boxH - 2, fill);
        renderWrappedScanText(guiGraphics, "Scans: " + selected.scanText(), boxX + 6, boxY + 5, scanW - 12);

        if (current && !menu.waveActive()) {
            int buttonX = boxX + boxW - buttonW;
            int buttonY = boxY;
            guiGraphics.fill(buttonX, buttonY, buttonX + buttonW, buttonY + boxH, 0xFF1F1F1F);
            guiGraphics.fill(buttonX + 3, buttonY + 3, buttonX + buttonW - 3, buttonY + boxH - 3, 0xFF3366CC);
            guiGraphics.drawString(font, "Start Wave?", buttonX + 10, buttonY + 11, 0xFFFFFF, false);
        }
    }

    private void renderWrappedScanText(GuiGraphics guiGraphics, String text, int x, int y, int width) {
        List<FormattedCharSequence> lines = font.split(FormattedText.of(text), width);
        for (int i = 0; i < Math.min(2, lines.size()); i++) {
            guiGraphics.drawString(font, lines.get(i), x, y + i * 10, 0xFFFFFF, false);
        }
    }

    private int getHoveredWave(double mouseX, double mouseY) {
        if (!isMouseOverWaveCanvas(mouseX, mouseY)) {
            return -1;
        }

        for (int wave = 1; wave <= OriginalGardenWaves.MAX_WAVE; wave++) {
            int nodeX = getWaveCanvasScreenX() + getWaveVirtualX(wave) + (int) waveCanvasX;
            int nodeY = getWaveCanvasScreenY() + getWaveVirtualY();
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
        int buttonX = boxX + imageWidth - 40 - 86;
        int buttonY = boxY;
        return mouseX >= buttonX && mouseX < buttonX + 86 && mouseY >= buttonY && mouseY < buttonY + 30;
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
        return 34 + (wave - 1) * WAVE_SPACING;
    }

    private int getWaveVirtualY() {
        return 48;
    }

    private double clampWaveCanvasX(double value) {
        return Math.max(getWaveCanvasScreenWidth() - WAVE_CANVAS_WIDTH, Math.min(0.0D, value));
    }

    private void focusCurrentWaveIfNeeded() {
        int currentWave = menu.currentWave();
        if (waveCanvasFocused && lastFocusedWave == currentWave) {
            return;
        }

        if (!waveCanvasFocused || selectedWave == lastFocusedWave) {
            selectedWave = currentWave;
            int canvasCenter = getWaveCanvasScreenWidth() / 2;
            waveCanvasX = clampWaveCanvasX(canvasCenter - getWaveVirtualX(currentWave) - WAVE_NODE_SIZE / 2.0D);
            waveCanvasFocused = true;
            lastFocusedWave = currentWave;
        }
    }

    private int gardenColor() {
        GardenPortalOption[] options = GardenPortalOption.values();
        int index = Math.max(0, Math.min(options.length - 1, menu.currentPortalIndex()));
        return options[index].color();
    }

    private void renderPortalTab(GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.drawString(font, Component.literal("Portal").withStyle(ChatFormatting.DARK_GRAY), x + 24, y + 48, 0x3F3F3F, false);
        GardenPortalOption current = GardenPortalOption.values()[menu.currentPortalIndex()];
        guiGraphics.drawString(font, current.displayName() + " (Current Garden)", x + 90, y + 48, 0x2F6F2F, false);
        GardenPortalOption[] options = GardenPortalOption.values();
        for (int i = 0; i < options.length; i++) {
            renderPortalOption(guiGraphics, options[i], i, x, y);
        }
    }

    private void renderPortalOption(GuiGraphics guiGraphics, GardenPortalOption option, int index, int x, int y) {
        int optionX = getPortalOptionX(x, index);
        int optionY = getPortalOptionY(y, index);
        boolean discovered = menu.isPortalDiscovered(index);
        boolean current = menu.currentPortalIndex() == index;
        int color = option.color();
        int border = current ? 0xFFFFFFFF : 0xFF1F1F1F;
        int fill = discovered ? color : darken(color);
        String label = discovered ? option.displayName() : "??????";
        label = font.plainSubstrByWidth(label, 104);

        guiGraphics.fill(optionX, optionY, optionX + 14, optionY + 14, border);
        guiGraphics.fill(optionX + 3, optionY + 3, optionX + 11, optionY + 11, 0xFF000000 | fill);
        guiGraphics.drawString(font, label, optionX + 20, optionY + 3, discovered ? (0xFF000000 | color) : 0x5F5F5F, false);
    }

    private int getHoveredPortal(double mouseX, double mouseY) {
        for (int i = 0; i < GardenPortalOption.values().length; i++) {
            int optionX = getPortalOptionX(leftPos, i);
            int optionY = getPortalOptionY(topPos, i);
            if (mouseX >= optionX && mouseX < optionX + 128 && mouseY >= optionY && mouseY < optionY + 14) {
                return i;
            }
        }
        return -1;
    }

    private int getPortalOptionX(int x, int index) {
        return x + 20 + (index % 2) * 132;
    }

    private int getPortalOptionY(int y, int index) {
        return y + 56 + (index / 2) * 16;
    }

    private int darken(int color) {
        int r = ((color >> 16) & 255) / 3;
        int g = ((color >> 8) & 255) / 3;
        int b = (color & 255) / 3;
        return (r << 16) | (g << 8) | b;
    }

    private void renderPlanterTab(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY) {
        hoveredPlantTooltip = null;
        guiGraphics.drawString(font, Component.literal("Planter").withStyle(ChatFormatting.DARK_GRAY), x + 24, y + 48, 0x3F3F3F, false);
        guiGraphics.drawString(font, Component.literal("Garden Packets").withStyle(ChatFormatting.DARK_GREEN), x + 116, y + 48, 0x2F6F2F, false);
        List<GardenPlantDefinition> plants = GardenPlantDefinition.originalGardenPlants();
        for (int i = 0; i < plants.size(); i++) {
            renderPlantCard(guiGraphics, plants.get(i), i, x, y, mouseX, mouseY);
        }
        renderSeedStorageArea(guiGraphics, x, y);
        renderTrashArea(guiGraphics, x, y);
        if (hoveredPlantTooltip != null) {
            guiGraphics.renderTooltip(font, hoveredPlantTooltip.stream().map(Component::getVisualOrderText).toList(), mouseX, mouseY);
        }
    }

    private void renderPlantCard(GuiGraphics guiGraphics, GardenPlantDefinition plant, int index, int x, int y, int mouseX, int mouseY) {
        int cardX = getPlantCardX(x, index);
        int cardY = getPlantCardY(y, index);
        int cardW = 238;
        int cardH = 23;
        boolean unlocked = plant.isUnlockedAtWave(menu.currentWave());
        int count = menu.plantCount(index);
        int remaining = menu.plantRemainingSeconds(index);
        int border = unlocked ? 0xFF2F6F2F : 0xFF050505;
        int fill = unlocked ? 0x66FFFFFF : 0x66000000;

        guiGraphics.fill(cardX, cardY, cardX + cardW, cardY + cardH, border);
        guiGraphics.fill(cardX + 2, cardY + 2, cardX + cardW - 2, cardY + cardH - 2, fill);
        guiGraphics.fill(cardX + 6, cardY + 5, cardX + 24, cardY + 23, unlocked ? 0xFFB6D7A8 : 0xFF000000);
        if (unlocked && count <= 0) {
            guiGraphics.renderItem(itemFromId(plant.seedPacketId().toString()), cardX + 7, cardY + 6);
        }

        String name = font.plainSubstrByWidth(plant.displayName(), 70);
        guiGraphics.drawString(font, name, cardX + 30, cardY + 4, unlocked ? 0x2F6F2F : 0x202020, false);
        String cost = unlocked ? plant.sunCost() + " sun" : "?";
        guiGraphics.drawString(font, cost, cardX + 104, cardY + 4, unlocked ? 0x9E7E00 : 0x202020, false);
        String description = unlocked ? font.plainSubstrByWidth(plant.description(), 96) : "?";
        guiGraphics.drawString(font, description, cardX + 30, cardY + 14, unlocked ? 0x3F3F3F : 0x202020, false);

        int barX = cardX + 150;
        int barY = cardY + 15;
        int barW = cardW - 156;
        guiGraphics.fill(barX, barY, barX + barW, barY + 5, 0xFF1F1F1F);
        if (unlocked) {
            int fillW = count >= GardenPlantProductionSavedData.GARDEN_PACKET_CAP
                    ? barW
                    : (int) (barW * (1.0F - Math.min(1.0F, remaining / (float) plant.productionSeconds())));
            guiGraphics.fill(barX + 1, barY + 1, barX + 1 + Math.max(0, fillW - 2), barY + 4, 0xFF3F9F3F);
            guiGraphics.drawString(font, count + "/40", cardX + 190, cardY + 4, count > 0 ? 0x2F6F2F : 0x5F5F5F, false);
        }

        if (mouseX >= cardX && mouseX < cardX + cardW && mouseY >= cardY && mouseY < cardY + cardH) {
            hoveredPlantTooltip = plantTooltip(plant, unlocked, count, remaining);
        }
    }

    private List<Component> plantTooltip(GardenPlantDefinition plant, boolean unlocked, int count, int remaining) {
        if (!unlocked) {
            return List.of(
                    Component.literal(plant.displayName()).withStyle(ChatFormatting.DARK_GRAY),
                    Component.literal("Cost: ?").withStyle(ChatFormatting.GRAY),
                    Component.literal("Description: ?").withStyle(ChatFormatting.GRAY),
                    Component.literal("Unlock: " + plant.unlockHint()).withStyle(ChatFormatting.YELLOW)
            );
        }

        String timer = count >= GardenPlantProductionSavedData.GARDEN_PACKET_CAP ? "Full" : remaining + "s";
        return List.of(
                Component.literal(plant.displayName()).withStyle(ChatFormatting.GREEN),
                Component.literal("Cost: " + plant.sunCost() + " sun").withStyle(ChatFormatting.GOLD),
                Component.literal(plant.description()).withStyle(ChatFormatting.GRAY),
                Component.literal("Totem packets: " + count + "/40").withStyle(ChatFormatting.DARK_GREEN),
                Component.literal("Next packet: " + timer).withStyle(ChatFormatting.YELLOW),
                Component.literal("Drag packets into Seed Storage or inventory.").withStyle(ChatFormatting.AQUA)
        );
    }

    private void renderSeedStorageArea(GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.drawString(font, Component.literal("Seed Storage").withStyle(ChatFormatting.DARK_GRAY), x + 276, y + 58, 0x3F3F3F, false);
        for (int index = 0; index < GardenTotemMenu.SEED_STORAGE_SLOT_COUNT; index++) {
            drawSlotBackground(guiGraphics, x + 286 + (index % 2) * 18, y + 78 + (index / 2) * 18, 0xFF2F6F2F);
        }
    }

    private void renderTrashArea(GuiGraphics guiGraphics, int x, int y) {
        drawSlotBackground(guiGraphics, x + 334, y + 206, 0xFF7F1F1F);
        guiGraphics.renderItem(new ItemStack(Items.BARRIER), x + 335, y + 207);
    }

    private void drawSlotBackground(GuiGraphics guiGraphics, int x, int y, int border) {
        guiGraphics.fill(x - 1, y - 1, x + 17, y + 17, border);
        guiGraphics.fill(x, y, x + 16, y + 16, 0xFF8F8F8F);
    }

    private int getHoveredPlant(double mouseX, double mouseY) {
        for (int i = 0; i < GardenPlantDefinition.originalGardenPlants().size(); i++) {
            int cardX = getPlantCardX(leftPos, i);
            int cardY = getPlantCardY(topPos, i);
            if (mouseX >= cardX && mouseX < cardX + 238 && mouseY >= cardY && mouseY < cardY + 23) {
                return i;
            }
        }
        return -1;
    }

    private int getPlantCardX(int x, int index) {
        return x + 24;
    }

    private int getPlantCardY(int y, int index) {
        return y + 60 + index * 25;
    }

    private void renderShopTab(GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.drawString(font, Component.literal("Crazy Dave's Shop").withStyle(ChatFormatting.DARK_GRAY), x + 24, y + 48, 0x3F3F3F, false);
        guiGraphics.drawString(font, Component.literal("Shop inventory placeholder").withStyle(ChatFormatting.DARK_GREEN), x + 70, y + 96, 0x2F6F2F, false);
        drawNode(guiGraphics, x + 34, y + 84, new ItemStack(Items.VILLAGER_SPAWN_EGG), true);
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
        int tabY = topPos - 10;
        return mouseX >= tabX && mouseX < tabX + TAB_SIZE && mouseY >= tabY && mouseY < tabY + 28;
    }

    private int getTabX(int x, int tab) {
        int totalWidth = TAB_COUNT * TAB_SIZE + (TAB_COUNT - 1) * TAB_SPACING;
        return x + (imageWidth - totalWidth) / 2 + tab * (TAB_SIZE + TAB_SPACING);
    }
}

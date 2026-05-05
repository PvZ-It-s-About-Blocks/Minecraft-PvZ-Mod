package net.PvZModders.PvZMod.client.seed;

import net.PvZModders.PvZMod.PvZ2Mod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = PvZ2Mod.MOD_ID, value = Dist.CLIENT)
public final class PlantHotbarHudOverlay {
    private static final int SLOT_SIZE = 20;
    private static final int SLOT_GAP = 2;
    private static final int SLOT_COUNT = 9;

    private PlantHotbarHudOverlay() {
    }

    @SubscribeEvent
    public static void render(RenderGuiOverlayEvent.Post event) {
        if (!event.getOverlay().id().equals(VanillaGuiOverlay.HOTBAR.id()) || !ClientSeedStorage.seedModeEnabled()) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null || minecraft.options.hideGui) {
            return;
        }

        renderPlantHotbar(event.getGuiGraphics(), minecraft, player, event.getWindow().getGuiScaledWidth(), event.getWindow().getGuiScaledHeight());
    }

    private static void renderPlantHotbar(GuiGraphics guiGraphics, Minecraft minecraft, LocalPlayer player, int screenWidth, int screenHeight) {
        int totalWidth = SLOT_COUNT * SLOT_SIZE + (SLOT_COUNT - 1) * SLOT_GAP;
        int startX = screenWidth / 2 - totalWidth / 2;
        int y = screenHeight - 58;
        int page = ClientSeedStorage.currentPage();
        int selected = ClientSeedStorage.selectedPlantSlot();

        for (int slot = 0; slot < 8; slot++) {
            int x = startX + slot * (SLOT_SIZE + SLOT_GAP);
            boolean unlocked = ClientSeedStorage.isSlotUnlocked(page, slot);
            boolean selectedSlot = slot == selected;
            ItemStack stack = ClientSeedStorage.slotStack(page, slot);
            boolean affordable = stack.isEmpty() || (ClientSeedStorage.slot(page, slot).packetCount() > 0
                    && player.experienceLevel >= ClientSeedStorage.sunCost(ClientSeedStorage.slot(page, slot).itemId()));
            renderPlantSlot(guiGraphics, minecraft.font, x, y, stack, unlocked, selectedSlot, affordable);
        }

        int swapX = startX + 8 * (SLOT_SIZE + SLOT_GAP);
        renderSwapSlot(guiGraphics, minecraft.font, swapX, y);
        renderSelectedPlantName(guiGraphics, minecraft.font, screenWidth, y - 12, page, selected);
    }

    private static void renderSelectedPlantName(GuiGraphics guiGraphics, Font font, int screenWidth, int y, int page, int selected) {
        String label = selectedPlantLabel(page, selected);
        if (label.isEmpty()) {
            return;
        }

        int textWidth = font.width(label);
        int x = screenWidth / 2 - textWidth / 2;
        guiGraphics.fill(x - 4, y - 2, x + textWidth + 4, y + 10, 0x99000000);
        guiGraphics.drawString(font, label, x, y, 0xFFE9FFE9, false);
    }

    private static String selectedPlantLabel(int page, int selected) {
        if (selected < 0 || selected >= 8) {
            return ClientSeedStorage.secondPageUnlocked() ? "Swap Page" : "Second plant page locked.";
        }
        if (!ClientSeedStorage.isSlotUnlocked(page, selected)) {
            return "Locked";
        }

        ItemStack stack = ClientSeedStorage.slotStack(page, selected);
        return stack.isEmpty() ? "Empty" : stack.getHoverName().getString();
    }

    private static void renderPlantSlot(GuiGraphics guiGraphics, Font font, int x, int y, ItemStack stack, boolean unlocked, boolean selected, boolean affordable) {
        int border = selected ? 0xFFFFFF88 : 0xFF103F1F;
        int fill = unlocked ? 0xCC1F7A33 : 0xAA222222;
        guiGraphics.fill(x, y, x + SLOT_SIZE, y + SLOT_SIZE, border);
        guiGraphics.fill(x + 2, y + 2, x + SLOT_SIZE - 2, y + SLOT_SIZE - 2, fill);

        if (!unlocked) {
            guiGraphics.drawString(font, "X", x + 7, y + 6, 0xFF5555, false);
            return;
        }

        if (!stack.isEmpty()) {
            guiGraphics.renderItem(stack, x + 2, y + 2);
            if (stack.getCount() > 1) {
                guiGraphics.renderItemDecorations(font, stack, x + 2, y + 2);
            }
            if (!affordable) {
                guiGraphics.fill(x + 2, y + 2, x + SLOT_SIZE - 2, y + SLOT_SIZE - 2, 0x88AA1111);
            }
        }
    }

    private static void renderSwapSlot(GuiGraphics guiGraphics, Font font, int x, int y) {
        boolean unlocked = ClientSeedStorage.secondPageUnlocked();
        int fill = unlocked ? 0xCC2D8F48 : 0xAA222222;
        guiGraphics.fill(x, y, x + SLOT_SIZE, y + SLOT_SIZE, 0xFF103F1F);
        guiGraphics.fill(x + 2, y + 2, x + SLOT_SIZE - 2, y + SLOT_SIZE - 2, fill);
        guiGraphics.drawString(font, unlocked ? ">" : "X", x + 7, y + 6, unlocked ? 0xFFFFFF : 0xFF5555, false);
    }
}

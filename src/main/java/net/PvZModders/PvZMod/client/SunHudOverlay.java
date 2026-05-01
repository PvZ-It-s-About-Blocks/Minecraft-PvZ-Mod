package net.PvZModders.PvZMod.client;

import net.PvZModders.PvZMod.PvZ2Mod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = PvZ2Mod.MOD_ID, value = Dist.CLIENT)
public final class SunHudOverlay {
    private SunHudOverlay() {
    }

    @SubscribeEvent
    public static void renderSunBar(RenderGuiOverlayEvent.Pre event) {
        if (!event.getOverlay().id().equals(VanillaGuiOverlay.EXPERIENCE_BAR.id())) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null || minecraft.options.hideGui) {
            return;
        }

        event.setCanceled(true);
        renderYellowExperienceBar(event.getGuiGraphics(), minecraft, event.getWindow().getGuiScaledWidth(), event.getWindow().getGuiScaledHeight(), player);
    }

    private static void renderYellowExperienceBar(GuiGraphics guiGraphics, Minecraft minecraft, int screenWidth, int screenHeight, LocalPlayer player) {
        int x = screenWidth / 2 - 91;
        int y = screenHeight - 32 + 3;
        int fillWidth = Math.min(182, Math.max(0, (int) (player.experienceProgress * 182.0F)));

        guiGraphics.fill(x - 1, y - 1, x + 183, y + 6, 0xFF1F1A00);
        guiGraphics.fill(x, y, x + 182, y + 5, 0xFF5A4500);
        if (fillWidth > 0) {
            guiGraphics.fill(x, y, x + fillWidth, y + 5, 0xFFFFD447);
            guiGraphics.fill(x, y, x + fillWidth, y + 1, 0xFFFFFF99);
        }

        Font font = minecraft.font;
        String sunText = String.valueOf(player.experienceLevel);
        int textX = (screenWidth - font.width(sunText)) / 2;
        int textY = screenHeight - 31 - 4;
        guiGraphics.drawString(font, sunText, textX + 1, textY, 0, false);
        guiGraphics.drawString(font, sunText, textX - 1, textY, 0, false);
        guiGraphics.drawString(font, sunText, textX, textY + 1, 0, false);
        guiGraphics.drawString(font, sunText, textX, textY - 1, 0, false);
        guiGraphics.drawString(font, sunText, textX, textY, 0xFFFFD447, false);
    }
}

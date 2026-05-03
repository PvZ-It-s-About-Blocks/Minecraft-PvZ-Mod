package net.PvZModders.PvZMod.client;

import net.PvZModders.PvZMod.PvZ2Mod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = PvZ2Mod.MOD_ID, value = Dist.CLIENT)
public final class GardenTeleportOverlay {
    private static final long DURATION_MILLIS = 3000L;
    private static final long FADE_IN_MILLIS = 650L;
    private static final long FADE_OUT_MILLIS = 900L;
    private static long animationStartMillis = -1L;

    private GardenTeleportOverlay() {
    }

    public static void start() {
        animationStartMillis = System.currentTimeMillis();
    }

    @SubscribeEvent
    public static void render(RenderGuiOverlayEvent.Post event) {
        if (!event.getOverlay().id().equals(VanillaGuiOverlay.HOTBAR.id())) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || animationStartMillis < 0L) {
            return;
        }

        long age = System.currentTimeMillis() - animationStartMillis;
        if (age >= DURATION_MILLIS) {
            animationStartMillis = -1L;
            return;
        }

        float alpha = alphaForAge(age);
        if (alpha <= 0.0F) {
            return;
        }

        GuiGraphics guiGraphics = event.getGuiGraphics();
        int width = event.getWindow().getGuiScaledWidth();
        int height = event.getWindow().getGuiScaledHeight();
        int opacity = Math.min(220, Math.max(0, (int) (alpha * 220.0F)));
        int color = (opacity << 24) | 0x102D1C;
        guiGraphics.fill(0, 0, width, height, color);

        int glowOpacity = Math.min(115, Math.max(0, (int) (alpha * 115.0F)));
        int centerX = width / 2;
        int centerY = height / 2;
        guiGraphics.fill(centerX - 48, centerY - 1, centerX + 48, centerY + 1, (glowOpacity << 24) | 0x70FF80);
        guiGraphics.fill(centerX - 1, centerY - 34, centerX + 1, centerY + 34, (glowOpacity << 24) | 0x70FF80);
    }

    private static float alphaForAge(long age) {
        if (age < FADE_IN_MILLIS) {
            return age / (float) FADE_IN_MILLIS;
        }

        long fadeOutStart = DURATION_MILLIS - FADE_OUT_MILLIS;
        if (age >= fadeOutStart) {
            return 1.0F - ((age - fadeOutStart) / (float) FADE_OUT_MILLIS);
        }

        return 1.0F;
    }
}

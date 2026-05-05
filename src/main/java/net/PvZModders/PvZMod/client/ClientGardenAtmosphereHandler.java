package net.PvZModders.PvZMod.client;

import net.PvZModders.PvZMod.PvZ2Mod;
import net.PvZModders.PvZMod.progression.atmosphere.DarkAgesBiomeEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = PvZ2Mod.MOD_ID, value = Dist.CLIENT)
public final class ClientGardenAtmosphereHandler {
    private static float darkAgesDarkness;

    private ClientGardenAtmosphereHandler() {
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        boolean inDarkAges = player != null && DarkAgesBiomeEffects.isPlayerInDarkAgesBiome(player);
        float target = inDarkAges ? 0.72F : 0.0F;
        darkAgesDarkness += (target - darkAgesDarkness) * 0.08F;
        if (Math.abs(darkAgesDarkness - target) < 0.01F) {
            darkAgesDarkness = target;
        }
    }

    @SubscribeEvent
    public static void renderDarkAgesOverlay(RenderGuiOverlayEvent.Post event) {
        if (!event.getOverlay().id().equals(VanillaGuiOverlay.HOTBAR.id()) || darkAgesDarkness <= 0.01F) {
            return;
        }

        GuiGraphics guiGraphics = event.getGuiGraphics();
        int width = event.getWindow().getGuiScaledWidth();
        int height = event.getWindow().getGuiScaledHeight();
        int opacity = Math.min(185, Math.max(0, (int) (darkAgesDarkness * 185.0F)));
        guiGraphics.fill(0, 0, width, height, (opacity << 24) | 0x070611);
    }
}

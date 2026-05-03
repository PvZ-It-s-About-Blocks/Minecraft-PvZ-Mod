package net.PvZModders.PvZMod.client.seed;

import net.PvZModders.PvZMod.PvZ2Mod;
import net.PvZModders.PvZMod.network.ModMessages;
import net.PvZModders.PvZMod.network.SeedHotbarActionC2SPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = PvZ2Mod.MOD_ID, value = Dist.CLIENT)
public final class SeedHotbarInputHandler {
    private SeedHotbarInputHandler() {
    }

    @SubscribeEvent
    public static void onKey(InputEvent.Key event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.screen != null || !ClientSeedStorage.seedModeEnabled() || !Screen.hasShiftDown()) {
            return;
        }

        if (event.getAction() != GLFW.GLFW_PRESS) {
            return;
        }

        int slot = keyToSlot(event.getKey());
        if (slot < 0) {
            return;
        }

        minecraft.options.keyHotbarSlots[slot].consumeClick();
        if (slot == 8) {
            ModMessages.sendSeedActionToServer(SeedHotbarActionC2SPacket.SWITCH_PAGE, 0);
        } else {
            ModMessages.sendSeedActionToServer(SeedHotbarActionC2SPacket.SELECT_SLOT, slot);
        }
    }

    @SubscribeEvent
    public static void onScroll(InputEvent.MouseScrollingEvent event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.screen != null || !ClientSeedStorage.seedModeEnabled() || !Screen.hasShiftDown()) {
            return;
        }

        ModMessages.sendSeedActionToServer(SeedHotbarActionC2SPacket.CYCLE_SLOT, event.getScrollDelta() < 0.0D ? 1 : -1);
        if (event.isCancelable()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onUse(InputEvent.InteractionKeyMappingTriggered event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.screen != null || !ClientSeedStorage.seedModeEnabled() || !Screen.hasShiftDown() || !event.isUseItem()) {
            return;
        }

        ModMessages.sendSeedActionToServer(SeedHotbarActionC2SPacket.PLACE_SELECTED, 0);
        if (event.isCancelable()) {
            event.setCanceled(true);
        }
        event.setSwingHand(false);
    }

    private static int keyToSlot(int key) {
        return switch (key) {
            case GLFW.GLFW_KEY_1 -> 0;
            case GLFW.GLFW_KEY_2 -> 1;
            case GLFW.GLFW_KEY_3 -> 2;
            case GLFW.GLFW_KEY_4 -> 3;
            case GLFW.GLFW_KEY_5 -> 4;
            case GLFW.GLFW_KEY_6 -> 5;
            case GLFW.GLFW_KEY_7 -> 6;
            case GLFW.GLFW_KEY_8 -> 7;
            case GLFW.GLFW_KEY_9 -> 8;
            default -> -1;
        };
    }
}

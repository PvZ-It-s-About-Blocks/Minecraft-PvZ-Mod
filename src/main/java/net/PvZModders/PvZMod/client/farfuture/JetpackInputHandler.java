package net.PvZModders.PvZMod.client.farfuture;

import net.PvZModders.PvZMod.PvZ2Mod;
import net.PvZModders.PvZMod.item.ModItems;
import net.PvZModders.PvZMod.network.ModMessages;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = PvZ2Mod.MOD_ID, value = Dist.CLIENT)
public final class JetpackInputHandler {
    private static boolean lastSentThrusting;
    private static int resendTicks;

    private JetpackInputHandler() {
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        boolean thrusting = minecraft.player != null
                && minecraft.screen == null
                && minecraft.options.keyJump.isDown()
                && hasJetpack(minecraft);
        if (thrusting != lastSentThrusting || (thrusting && resendTicks-- <= 0)) {
            ModMessages.sendJetpackThrustToServer(thrusting);
            lastSentThrusting = thrusting;
            resendTicks = 4;
        }
    }

    private static boolean hasJetpack(Minecraft minecraft) {
        if (minecraft.player == null) {
            return false;
        }
        for (ItemStack stack : minecraft.player.getInventory().items) {
            if (stack.is(ModItems.JETPACK.get())) {
                return true;
            }
        }
        return minecraft.player.getMainHandItem().is(ModItems.JETPACK.get())
                || minecraft.player.getOffhandItem().is(ModItems.JETPACK.get());
    }
}

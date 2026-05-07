package net.PvZModders.PvZMod.progression.portal;

import net.PvZModders.PvZMod.PvZ2Mod;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = PvZ2Mod.MOD_ID)
public final class GardenPortalStrongholdConverter {
    private GardenPortalStrongholdConverter() {
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.player instanceof ServerPlayer player)) {
            return;
        }
        if (player.tickCount % 80 != 0 || !(player.level() instanceof ServerLevel level)) {
            return;
        }
        GardenPortalActivationHandler.convertNearestVanillaPortal(level, player.blockPosition()).ifPresent(center ->
                player.sendSystemMessage(Component.literal("The stronghold portal room reshapes into a Garden Portal.").withStyle(ChatFormatting.LIGHT_PURPLE)));
    }
}

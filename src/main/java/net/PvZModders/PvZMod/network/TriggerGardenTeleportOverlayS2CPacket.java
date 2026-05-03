package net.PvZModders.PvZMod.network;

import net.PvZModders.PvZMod.client.GardenTeleportOverlay;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class TriggerGardenTeleportOverlayS2CPacket {
    public static void encode(TriggerGardenTeleportOverlayS2CPacket packet, FriendlyByteBuf buffer) {
    }

    public static TriggerGardenTeleportOverlayS2CPacket decode(FriendlyByteBuf buffer) {
        return new TriggerGardenTeleportOverlayS2CPacket();
    }

    public static void handle(TriggerGardenTeleportOverlayS2CPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> GardenTeleportOverlay::start));
        context.setPacketHandled(true);
    }
}

package net.PvZModders.PvZMod.network;

import net.PvZModders.PvZMod.progression.farfuture.JetpackManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class JetpackThrustC2SPacket {
    private final boolean thrusting;

    public JetpackThrustC2SPacket(boolean thrusting) {
        this.thrusting = thrusting;
    }

    public static void encode(JetpackThrustC2SPacket packet, FriendlyByteBuf buffer) {
        buffer.writeBoolean(packet.thrusting);
    }

    public static JetpackThrustC2SPacket decode(FriendlyByteBuf buffer) {
        return new JetpackThrustC2SPacket(buffer.readBoolean());
    }

    public static void handle(JetpackThrustC2SPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                JetpackManager.setThrusting(player, packet.thrusting);
            }
        });
        context.setPacketHandled(true);
    }
}

package net.PvZModders.PvZMod.network;

import net.PvZModders.PvZMod.PvZ2Mod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;

public final class ModMessages {
    private static final String PROTOCOL_VERSION = "1";
    private static int packetId;

    private static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(PvZ2Mod.MOD_ID, "main"))
            .networkProtocolVersion(() -> PROTOCOL_VERSION)
            .clientAcceptedVersions(PROTOCOL_VERSION::equals)
            .serverAcceptedVersions(PROTOCOL_VERSION::equals)
            .simpleChannel();

    private ModMessages() {
    }

    public static void register() {
        CHANNEL.registerMessage(
                nextPacketId(),
                TriggerGardenTeleportOverlayS2CPacket.class,
                TriggerGardenTeleportOverlayS2CPacket::encode,
                TriggerGardenTeleportOverlayS2CPacket::decode,
                TriggerGardenTeleportOverlayS2CPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT)
        );
    }

    public static void sendGardenTeleportOverlay(ServerPlayer player) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new TriggerGardenTeleportOverlayS2CPacket());
    }

    private static int nextPacketId() {
        return packetId++;
    }
}

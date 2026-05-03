package net.PvZModders.PvZMod.network;

import net.PvZModders.PvZMod.progression.seed.SeedStorage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SeedHotbarActionC2SPacket {
    public static final int SELECT_SLOT = 0;
    public static final int CYCLE_SLOT = 1;
    public static final int SWITCH_PAGE = 2;
    public static final int PLACE_SELECTED = 3;

    private final int action;
    private final int value;

    public SeedHotbarActionC2SPacket(int action, int value) {
        this.action = action;
        this.value = value;
    }

    public static void encode(SeedHotbarActionC2SPacket packet, FriendlyByteBuf buffer) {
        buffer.writeInt(packet.action);
        buffer.writeInt(packet.value);
    }

    public static SeedHotbarActionC2SPacket decode(FriendlyByteBuf buffer) {
        return new SeedHotbarActionC2SPacket(buffer.readInt(), buffer.readInt());
    }

    public static void handle(SeedHotbarActionC2SPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) {
                return;
            }

            switch (packet.action) {
                case SELECT_SLOT -> SeedStorage.setSelectedPlantSlot(player, packet.value);
                case CYCLE_SLOT -> SeedStorage.cycleSelectedPlantSlot(player, packet.value);
                case SWITCH_PAGE -> SeedStorage.switchPlantHotbarPage(player);
                case PLACE_SELECTED -> SeedStorage.placeSelectedPlant(player, tracePlantTarget(player));
                default -> {
                }
            }
        });
        context.setPacketHandled(true);
    }

    private static BlockHitResult tracePlantTarget(ServerPlayer player) {
        double reach = player.getBlockReach();
        Vec3 eye = player.getEyePosition();
        Vec3 look = player.getViewVector(1.0F);
        Vec3 end = eye.add(look.x * reach, look.y * reach, look.z * reach);
        return player.level().clip(new ClipContext(eye, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
    }
}

package net.PvZModders.PvZMod.network;

import net.PvZModders.PvZMod.client.seed.ClientSeedStorage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SeedStorageSyncS2CPacket {
    private final CompoundTag seedStorageTag;

    public SeedStorageSyncS2CPacket(CompoundTag seedStorageTag) {
        this.seedStorageTag = seedStorageTag;
    }

    public static void encode(SeedStorageSyncS2CPacket packet, FriendlyByteBuf buffer) {
        buffer.writeNbt(packet.seedStorageTag);
    }

    public static SeedStorageSyncS2CPacket decode(FriendlyByteBuf buffer) {
        CompoundTag tag = buffer.readNbt();
        return new SeedStorageSyncS2CPacket(tag == null ? new CompoundTag() : tag);
    }

    public static void handle(SeedStorageSyncS2CPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientSeedStorage.update(packet.seedStorageTag)));
        context.setPacketHandled(true);
    }
}

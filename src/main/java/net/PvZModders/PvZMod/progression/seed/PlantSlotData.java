package net.PvZModders.PvZMod.progression.seed;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.registries.BuiltInRegistries;

public class PlantSlotData {
    private ResourceLocation itemId;
    private int packetCount;

    public PlantSlotData(ResourceLocation itemId, int packetCount) {
        this.itemId = itemId;
        this.packetCount = Math.max(0, packetCount);
    }

    public static PlantSlotData empty() {
        return new PlantSlotData(null, 0);
    }

    public static PlantSlotData load(CompoundTag tag) {
        if (!tag.contains("Item")) {
            return empty();
        }
        ResourceLocation id = ResourceLocation.tryParse(tag.getString("Item"));
        if (id == null || !BuiltInRegistries.ITEM.containsKey(id)) {
            return empty();
        }
        return new PlantSlotData(id, tag.getInt("Count"));
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        if (itemId != null) {
            tag.putString("Item", itemId.toString());
        }
        tag.putInt("Count", packetCount);
        return tag;
    }

    public boolean isEmpty() {
        return itemId == null;
    }

    public ResourceLocation itemId() {
        return itemId;
    }

    public int packetCount() {
        return packetCount;
    }

    public void set(ResourceLocation itemId, int packetCount) {
        this.itemId = itemId;
        this.packetCount = Math.max(0, packetCount);
    }

    public void decrementPacket() {
        packetCount = Math.max(0, packetCount - 1);
    }

    public ItemStack toItemStack() {
        if (itemId == null) {
            return ItemStack.EMPTY;
        }
        Item item = BuiltInRegistries.ITEM.get(itemId);
        if (item == null) {
            return ItemStack.EMPTY;
        }
        return new ItemStack(item, Math.max(1, packetCount));
    }
}

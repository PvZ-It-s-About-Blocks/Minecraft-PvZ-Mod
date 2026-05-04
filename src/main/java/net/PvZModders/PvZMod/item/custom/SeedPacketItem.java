package net.PvZModders.PvZMod.item.custom;

import net.PvZModders.PvZMod.progression.seed.PlantSeedDefinition;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class SeedPacketItem extends Item {
    private final String plantId;

    public SeedPacketItem(String plantId, Properties properties) {
        super(properties);
        this.plantId = plantId;
    }

    @Override
    public Component getName(ItemStack stack) {
        Component name = super.getName(stack);
        var definition = PlantSeedDefinition.getByPlantId(plantId);
        if (definition.isEmpty()) {
            return name;
        }
        return name.copy().withStyle(style -> style.withColor(TextColor.fromRgb(definition.get().gardenColor())));
    }
}

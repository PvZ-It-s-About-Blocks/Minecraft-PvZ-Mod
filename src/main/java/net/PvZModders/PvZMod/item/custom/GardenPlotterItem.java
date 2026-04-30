package net.PvZModders.PvZMod.item.custom;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class GardenPlotterItem extends BlockItem {
    public GardenPlotterItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.pvz2mod.garden_plotter.line1"));
        tooltip.add(Component.translatable("tooltip.pvz2mod.garden_plotter.line2"));
        super.appendHoverText(stack, level, tooltip, flag);
    }
}

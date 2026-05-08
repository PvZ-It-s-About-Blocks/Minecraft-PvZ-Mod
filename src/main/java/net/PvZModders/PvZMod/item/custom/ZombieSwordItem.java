package net.PvZModders.PvZMod.item.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class ZombieSwordItem extends SwordItem {
    public ZombieSwordItem(Tier tier, int attackDamageModifier, float attackSpeedModifier, Properties properties) {
        super(tier, attackDamageModifier, attackSpeedModifier, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("Deals bonus damage to PvZ wave zombies.").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.literal("Bonus is reduced against Gargantuars.").withStyle(ChatFormatting.DARK_GRAY));
    }
}

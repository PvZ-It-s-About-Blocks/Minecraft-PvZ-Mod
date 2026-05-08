package net.PvZModders.PvZMod.item.custom;

import net.PvZModders.PvZMod.block.entity.GardenTotemBlockEntity;
import net.PvZModders.PvZMod.progression.coins.CoinEconomyValues;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import java.util.List;

public class TotemRepairKitItem extends Item {
    public TotemRepairKitItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        if (!(context.getPlayer() instanceof ServerPlayer player)
                || !(level.getBlockEntity(context.getClickedPos()) instanceof GardenTotemBlockEntity totem)) {
            return InteractionResult.PASS;
        }

        ItemStack stack = context.getItemInHand();
        if (!totem.repairTotem(player, CoinEconomyValues.TOTEM_REPAIR_KIT_REPAIR_AMOUNT)) {
            return InteractionResult.FAIL;
        }
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        return InteractionResult.CONSUME;
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("Repairs a damaged garden totem.").withStyle(ChatFormatting.GRAY));
    }
}

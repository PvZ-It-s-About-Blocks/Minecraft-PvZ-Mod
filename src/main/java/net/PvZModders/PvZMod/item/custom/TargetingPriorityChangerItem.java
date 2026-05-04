package net.PvZModders.PvZMod.item.custom;

import net.PvZModders.PvZMod.progression.targeting.TargetingPriority;
import net.PvZModders.PvZMod.progression.targeting.TargetingPriorityManager;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class TargetingPriorityChangerItem extends Item {
    public TargetingPriorityChangerItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide()) {
            TargetingPriority priority = TargetingPriorityManager.cyclePriority(player);
            player.displayClientMessage(Component.literal("Plant targeting priority set to: " + priority.name()).withStyle(ChatFormatting.GREEN), true);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}

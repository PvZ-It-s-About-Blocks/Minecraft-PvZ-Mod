package net.PvZModders.PvZMod.item.custom;

import net.PvZModders.PvZMod.progression.targeting.TargetingPriorityManager;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class CommandersBucketItem extends Item {
    public CommandersBucketItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        if (!(target instanceof Zombie)) {
            if (!player.level().isClientSide) {
                player.displayClientMessage(Component.literal("Commander target must be a zombie.").withStyle(ChatFormatting.RED), true);
            }
            return InteractionResult.FAIL;
        }

        if (!player.level().isClientSide) {
            TargetingPriorityManager.setFocusTarget(player, target);
            player.displayClientMessage(Component.literal("Plants focusing fire on: " + target.getDisplayName().getString()).withStyle(ChatFormatting.AQUA), true);
        }
        return InteractionResult.sidedSuccess(player.level().isClientSide);
    }
}

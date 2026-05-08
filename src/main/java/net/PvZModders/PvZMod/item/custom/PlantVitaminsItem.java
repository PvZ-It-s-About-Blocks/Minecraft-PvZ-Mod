package net.PvZModders.PvZMod.item.custom;

import net.PvZModders.PvZMod.progression.seed.PlantEntityManager;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class PlantVitaminsItem extends Item {
    public PlantVitaminsItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        if (!PlantEntityManager.isPlant(target)) {
            return InteractionResult.PASS;
        }
        if (!PlantEntityManager.canApplyPlantVitamins(target)) {
            if (!player.level().isClientSide) {
                player.displayClientMessage(Component.literal("This plant cannot use Plant Vitamins.").withStyle(ChatFormatting.RED), true);
            }
            return InteractionResult.FAIL;
        }
        if (!player.level().isClientSide && PlantEntityManager.applyPlantVitamins(player, target)) {
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
        }
        return InteractionResult.sidedSuccess(player.level().isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack stack, net.minecraft.world.level.Level level, List<Component> tooltip, net.minecraft.world.item.TooltipFlag flag) {
        tooltip.add(Component.literal("Increases one plant's attack speed for 10 seconds.").withStyle(ChatFormatting.GRAY));
    }
}

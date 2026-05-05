package net.PvZModders.PvZMod.item.custom;

import net.PvZModders.PvZMod.block.entity.GardenTotemBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.entity.BlockEntity;

public class TotemShieldItem extends Item {
    public TotemShieldItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getLevel().isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        BlockEntity blockEntity = context.getLevel().getBlockEntity(context.getClickedPos());
        if (!(blockEntity instanceof GardenTotemBlockEntity totem) || !(context.getPlayer() instanceof ServerPlayer player)) {
            return InteractionResult.PASS;
        }

        totem.activateTotemShield(player);
        if (!player.getAbilities().instabuild) {
            context.getItemInHand().shrink(1);
        }
        player.displayClientMessage(Component.literal("Totem Shield activated.").withStyle(ChatFormatting.AQUA), true);
        return InteractionResult.CONSUME;
    }
}

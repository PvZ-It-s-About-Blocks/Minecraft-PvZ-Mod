package net.PvZModders.PvZMod.item.custom;

import net.PvZModders.PvZMod.progression.seed.SeedStorage;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class SeedHolderItem extends Item {
    public SeedHolderItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            SeedStorage.toggleSeedMode(player);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
}

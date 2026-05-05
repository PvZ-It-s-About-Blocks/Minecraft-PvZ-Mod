package net.PvZModders.PvZMod.item.custom;

import net.PvZModders.PvZMod.entity.custom.FlyingPlaneEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class FlyingPlaneItem extends Item {
    public FlyingPlaneItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResultHolder.success(stack);
        }

        Vec3 spawnPos = player.getEyePosition().add(player.getLookAngle().normalize().scale(2.0D));
        FlyingPlaneEntity plane = new FlyingPlaneEntity(serverLevel, spawnPos.x, spawnPos.y, spawnPos.z);
        plane.setYRot(player.getYRot());
        plane.setXRot(player.getXRot());
        if (!serverLevel.addFreshEntity(plane)) {
            return InteractionResultHolder.fail(stack);
        }

        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        return InteractionResultHolder.consume(stack);
    }
}

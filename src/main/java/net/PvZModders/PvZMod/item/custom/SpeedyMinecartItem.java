package net.PvZModders.PvZMod.item.custom;

import net.PvZModders.PvZMod.entity.custom.SpeedyMinecartEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.Vec3;

public class SpeedyMinecartItem extends Item {
    public SpeedyMinecartItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (!(context.getLevel() instanceof ServerLevel level)) {
            return InteractionResult.SUCCESS;
        }

        BlockPos pos = context.getClickedPos().relative(context.getClickedFace());
        Vec3 spawnPos = Vec3.atBottomCenterOf(pos).add(0.0D, 0.1D, 0.0D);
        SpeedyMinecartEntity minecart = new SpeedyMinecartEntity(level, spawnPos.x, spawnPos.y, spawnPos.z);
        minecart.setYRot(context.getRotation());
        if (!level.addFreshEntity(minecart)) {
            return InteractionResult.FAIL;
        }

        if (context.getPlayer() == null || !context.getPlayer().getAbilities().instabuild) {
            context.getItemInHand().shrink(1);
        }
        return InteractionResult.CONSUME;
    }
}

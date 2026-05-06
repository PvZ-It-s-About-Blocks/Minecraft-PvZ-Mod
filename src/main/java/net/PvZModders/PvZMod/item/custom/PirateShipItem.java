package net.PvZModders.PvZMod.item.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

public class PirateShipItem extends Item {
    public PirateShipItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (!(context.getLevel() instanceof ServerLevel level)) {
            return InteractionResult.SUCCESS;
        }

        BlockPos clicked = context.getClickedPos();
        BlockPos spawnBlock = level.getBlockState(clicked).is(Blocks.WATER) ? clicked : clicked.relative(context.getClickedFace());
        Vec3 spawnPos = Vec3.atBottomCenterOf(spawnBlock).add(0.0D, 0.15D, 0.0D);
        Boat boat = EntityType.BOAT.create(level);
        if (boat == null) {
            return InteractionResult.FAIL;
        }

        boat.setVariant(Boat.Type.SPRUCE);
        boat.moveTo(spawnPos.x, spawnPos.y, spawnPos.z, context.getRotation(), 0.0F);
        boat.getPersistentData().putBoolean("PvZPirateShip", true);
        boat.setCustomName(net.minecraft.network.chat.Component.literal("Pirate Ship"));
        if (!level.addFreshEntity(boat)) {
            return InteractionResult.FAIL;
        }

        level.playSound(null, spawnBlock, SoundEvents.BOAT_PADDLE_WATER, SoundSource.NEUTRAL, 0.8F, 0.7F);
        if (context.getPlayer() == null || !context.getPlayer().getAbilities().instabuild) {
            context.getItemInHand().shrink(1);
        }
        return InteractionResult.CONSUME;
    }
}

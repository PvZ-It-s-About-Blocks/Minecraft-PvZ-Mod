package net.PvZModders.PvZMod.item.custom;

import net.PvZModders.PvZMod.progression.portal.GardenEyeType;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.StructureTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.projectile.EyeOfEnder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import javax.annotation.Nullable;
import java.util.List;

public class GardenEyeItem extends Item {
    private final GardenEyeType type;

    public GardenEyeItem(GardenEyeType type, Properties properties) {
        super(properties);
        this.type = type;
    }

    public GardenEyeType gardenEyeType() {
        return type;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, net.minecraft.world.entity.player.Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        BlockHitResult hit = getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE);
        if (hit.getType() == HitResult.Type.BLOCK && level.getBlockState(hit.getBlockPos()).is(Blocks.END_PORTAL_FRAME)) {
            return InteractionResultHolder.pass(stack);
        }

        player.startUsingItem(hand);
        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResultHolder.success(stack);
        }

        BlockPos stronghold = serverLevel.findNearestMapStructure(StructureTags.EYE_OF_ENDER_LOCATED, player.blockPosition(), 100, false);
        if (stronghold == null) {
            return InteractionResultHolder.consume(stack);
        }

        EyeOfEnder eye = new NonBreakingGardenEyeOfEnder(level, player.getX(), player.getY(0.5D), player.getZ());
        ItemStack visualStack = stack.copy();
        visualStack.setCount(1);
        eye.setItem(visualStack);
        eye.signalTo(stronghold);
        level.gameEvent(net.minecraft.world.level.gameevent.GameEvent.PROJECTILE_SHOOT, eye.position(), net.minecraft.world.level.gameevent.GameEvent.Context.of(player));
        level.addFreshEntity(eye);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENDER_EYE_LAUNCH, SoundSource.NEUTRAL, 0.5F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
        player.swing(hand, true);
        return InteractionResultHolder.consume(stack);
    }

    private static class NonBreakingGardenEyeOfEnder extends EyeOfEnder {
        NonBreakingGardenEyeOfEnder(Level level, double x, double y, double z) {
            super(level, x, y, z);
        }

        @Override
        public void tick() {
            if (this.tickCount >= 78) {
                discard();
                return;
            }
            super.tick();
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("Points toward the stronghold.").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.literal("Awakens the " + type.gardenName() + " Portal Frame.").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.literal("One of twelve eyes needed to awaken the Garden Portal.").withStyle(ChatFormatting.DARK_PURPLE));
    }
}

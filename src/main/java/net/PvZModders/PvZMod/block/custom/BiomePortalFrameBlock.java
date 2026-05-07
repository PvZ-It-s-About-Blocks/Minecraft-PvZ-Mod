package net.PvZModders.PvZMod.block.custom;

import net.PvZModders.PvZMod.item.custom.GardenEyeItem;
import net.PvZModders.PvZMod.progression.portal.GardenEyeType;
import net.PvZModders.PvZMod.progression.portal.GardenPortalActivationHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EndPortalFrameBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class BiomePortalFrameBlock extends EndPortalFrameBlock {
    private final GardenEyeType requiredEyeType;

    public BiomePortalFrameBlock(GardenEyeType requiredEyeType, BlockBehaviour.Properties properties) {
        super(properties);
        this.requiredEyeType = requiredEyeType;
    }

    public GardenEyeType requiredEyeType() {
        return requiredEyeType;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (state.getValue(HAS_EYE)) {
            if (!level.isClientSide) {
                player.displayClientMessage(Component.literal("This frame is already awakened.").withStyle(ChatFormatting.GRAY), true);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        ItemStack stack = player.getItemInHand(hand);
        if (!(stack.getItem() instanceof GardenEyeItem gardenEye)) {
            if (!level.isClientSide) {
                player.displayClientMessage(Component.literal("You need the eye from this biome: " + requiredEyeType.gardenName() + ".").withStyle(ChatFormatting.YELLOW), true);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        if (gardenEye.gardenEyeType() != requiredEyeType) {
            if (!level.isClientSide) {
                player.displayClientMessage(Component.literal("This frame does not accept that eye. It needs the " + requiredEyeType.eyeDisplayName() + ".").withStyle(ChatFormatting.RED), true);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        if (!level.isClientSide) {
            level.setBlock(pos, state.setValue(HAS_EYE, true), 3);
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            level.playSound(null, pos, SoundEvents.END_PORTAL_FRAME_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
            player.displayClientMessage(Component.literal("The " + requiredEyeType.gardenName() + " Eye awakens this frame.").withStyle(ChatFormatting.LIGHT_PURPLE), true);
            GardenPortalActivationHandler.onFrameFilled((ServerLevel) level, pos);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}

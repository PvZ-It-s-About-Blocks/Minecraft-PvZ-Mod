package net.PvZModders.PvZMod.item.custom;

import net.PvZModders.PvZMod.block.ModBlocks;
import net.PvZModders.PvZMod.block.entity.GardenTotemBlockEntity;
import net.PvZModders.PvZMod.network.ModMessages;
import net.PvZModders.PvZMod.progression.GardenPortalOption;
import net.PvZModders.PvZMod.progression.GardenPortalSavedData;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Optional;
import java.util.Set;

public class MysticalEyeItem extends Item {
    private static final String NEXT_PORTAL_INDEX_TAG = "PvZMysticalEyeNextPortalIndex";

    public MysticalEyeItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResultHolder.success(stack);
        }

        GardenPortalSavedData portalData = GardenPortalSavedData.get(serverPlayer.serverLevel());
        GardenPortalOption[] options = GardenPortalOption.values();
        CompoundTag tag = stack.getOrCreateTag();
        int start = Math.floorMod(tag.getInt(NEXT_PORTAL_INDEX_TAG), options.length);
        for (int offset = 0; offset < options.length; offset++) {
            int index = (start + offset) % options.length;
            GardenPortalOption option = options[index];
            Optional<GlobalPos> target = portalData.getPortal(option.gardenId());
            if (target.isEmpty() || !canTeleportToGarden(serverPlayer, target.get(), portalData, option)) {
                continue;
            }

            tag.putInt(NEXT_PORTAL_INDEX_TAG, (index + 1) % options.length);
            teleportPlayerToGarden(serverPlayer, target.get(), option);
            return InteractionResultHolder.consume(stack);
        }

        serverPlayer.displayClientMessage(Component.literal("No valid garden totems are available.").withStyle(ChatFormatting.RED), true);
        return InteractionResultHolder.fail(stack);
    }

    private boolean canTeleportToGarden(ServerPlayer player, GlobalPos target, GardenPortalSavedData portalData, GardenPortalOption option) {
        ServerLevel targetLevel = player.server.getLevel(target.dimension());
        if (targetLevel == null || !targetLevel.getBlockState(target.pos()).is(ModBlocks.GARDEN_TOTEM.get())) {
            portalData.removePortal(option.gardenId(), target);
            return false;
        }
        if (targetLevel.getBlockEntity(target.pos()) instanceof GardenTotemBlockEntity totem) {
            return !totem.isWaveActive();
        }
        return false;
    }

    private void teleportPlayerToGarden(ServerPlayer player, GlobalPos target, GardenPortalOption option) {
        ServerLevel targetLevel = player.server.getLevel(target.dimension());
        if (targetLevel == null) {
            return;
        }
        BlockPos arrival = findTeleportArrival(targetLevel, target.pos());
        ModMessages.sendGardenTeleportOverlay(player);
        player.teleportTo(targetLevel, arrival.getX() + 0.5D, arrival.getY(), arrival.getZ() + 0.5D, Set.of(), player.getYRot(), player.getXRot());
        player.displayClientMessage(Component.literal("Mystical Eye: " + option.displayName()).withStyle(ChatFormatting.LIGHT_PURPLE), true);
    }

    private BlockPos findTeleportArrival(ServerLevel level, BlockPos totemPos) {
        for (BlockPos candidate : new BlockPos[]{
                totemPos.north(2), totemPos.south(2), totemPos.east(2), totemPos.west(2),
                totemPos.north(3), totemPos.south(3), totemPos.east(3), totemPos.west(3)
        }) {
            if (level.getBlockState(candidate).isAir() && level.getBlockState(candidate.above()).isAir()) {
                return candidate;
            }
        }
        return totemPos.above();
    }
}

package net.PvZModders.PvZMod.progression.portal;

import net.PvZModders.PvZMod.block.ModBlocks;
import net.PvZModders.PvZMod.block.custom.BiomePortalFrameBlock;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EndPortalFrameBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class GardenPortalActivationHandler {
    public static final List<FramePlacement> FRAME_PLACEMENTS = createPlacements();

    private GardenPortalActivationHandler() {
    }

    public static void onFrameFilled(ServerLevel level, BlockPos framePos) {
        Optional<BlockPos> center = findPortalCenter(level, framePos, 3);
        if (center.isEmpty()) {
            return;
        }
        PortalStatus status = getStatus(level, center.get());
        messageNearby(level, center.get(), Component.literal("Garden Eyes placed: " + status.filled() + "/12").withStyle(ChatFormatting.DARK_PURPLE));
        if (status.ready()) {
            activatePortal(level, center.get());
        }
    }

    public static void createPortal(ServerLevel level, BlockPos center, boolean filled) {
        clearInterior(level, center);
        for (FramePlacement placement : FRAME_PLACEMENTS) {
            BlockState state = blockFor(placement.type()).defaultBlockState()
                    .setValue(EndPortalFrameBlock.FACING, placement.facing())
                    .setValue(EndPortalFrameBlock.HAS_EYE, filled);
            level.setBlock(center.offset(placement.dx(), 0, placement.dz()), state, 3);
        }
        if (filled) {
            activatePortal(level, center);
        }
    }

    public static boolean resetNearestPortal(ServerLevel level, BlockPos near) {
        Optional<BlockPos> center = findPortalCenter(level, near, 8);
        if (center.isEmpty()) {
            return false;
        }
        clearInterior(level, center.get());
        for (FramePlacement placement : FRAME_PLACEMENTS) {
            BlockPos pos = center.get().offset(placement.dx(), 0, placement.dz());
            BlockState state = level.getBlockState(pos);
            if (isGardenFrame(state)) {
                level.setBlock(pos, state.setValue(EndPortalFrameBlock.HAS_EYE, false), 3);
            }
        }
        return true;
    }

    public static boolean fillNearestPortal(ServerLevel level, BlockPos near) {
        Optional<BlockPos> center = findPortalCenter(level, near, 8);
        if (center.isEmpty()) {
            return false;
        }
        for (FramePlacement placement : FRAME_PLACEMENTS) {
            BlockPos pos = center.get().offset(placement.dx(), 0, placement.dz());
            BlockState state = level.getBlockState(pos);
            if (isGardenFrame(state)) {
                level.setBlock(pos, state.setValue(EndPortalFrameBlock.HAS_EYE, true), 3);
            }
        }
        if (getStatus(level, center.get()).ready()) {
            activatePortal(level, center.get());
        }
        return true;
    }

    public static Optional<BlockPos> convertNearestVanillaPortal(ServerLevel level, BlockPos near) {
        Optional<BlockPos> center = findVanillaPortalCenter(level, near, 8);
        center.ifPresent(pos -> createPortal(level, pos, false));
        return center;
    }

    public static Optional<PortalStatus> getNearestStatus(ServerLevel level, BlockPos near) {
        return findPortalCenter(level, near, 8).map(center -> getStatus(level, center));
    }

    public static Optional<BlockPos> findPortalCenter(ServerLevel level, BlockPos near, int radius) {
        for (BlockPos candidate : BlockPos.betweenClosed(near.offset(-radius, -2, -radius), near.offset(radius, 2, radius))) {
            if (isPortalRing(level, candidate)) {
                return Optional.of(candidate.immutable());
            }
        }
        return Optional.empty();
    }

    private static Optional<BlockPos> findVanillaPortalCenter(ServerLevel level, BlockPos near, int radius) {
        for (BlockPos candidate : BlockPos.betweenClosed(near.offset(-radius, -2, -radius), near.offset(radius, 2, radius))) {
            boolean vanillaRing = true;
            for (FramePlacement placement : FRAME_PLACEMENTS) {
                if (!level.getBlockState(candidate.offset(placement.dx(), 0, placement.dz())).is(Blocks.END_PORTAL_FRAME)) {
                    vanillaRing = false;
                    break;
                }
            }
            if (vanillaRing) {
                return Optional.of(candidate.immutable());
            }
        }
        return Optional.empty();
    }

    private static boolean isPortalRing(ServerLevel level, BlockPos center) {
        for (FramePlacement placement : FRAME_PLACEMENTS) {
            BlockState state = level.getBlockState(center.offset(placement.dx(), 0, placement.dz()));
            if (!(state.getBlock() instanceof BiomePortalFrameBlock frame) || frame.requiredEyeType() != placement.type()) {
                return false;
            }
        }
        return true;
    }

    public static PortalStatus getStatus(ServerLevel level, BlockPos center) {
        int filled = 0;
        boolean complete = true;
        for (FramePlacement placement : FRAME_PLACEMENTS) {
            BlockState state = level.getBlockState(center.offset(placement.dx(), 0, placement.dz()));
            if (!(state.getBlock() instanceof BiomePortalFrameBlock frame) || frame.requiredEyeType() != placement.type()) {
                complete = false;
                continue;
            }
            if (state.getValue(EndPortalFrameBlock.HAS_EYE)) {
                filled++;
            } else {
                complete = false;
            }
        }
        return new PortalStatus(filled, complete && filled == GardenEyeType.REQUIRED.size());
    }

    private static void activatePortal(ServerLevel level, BlockPos center) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                level.setBlock(center.offset(dx, 0, dz), Blocks.END_PORTAL.defaultBlockState(), 3);
            }
        }
        level.playSound(null, center, SoundEvents.END_PORTAL_SPAWN, SoundSource.BLOCKS, 1.0F, 1.0F);
        messageNearby(level, center, Component.literal("The Garden Portal awakens.").withStyle(ChatFormatting.LIGHT_PURPLE));
    }

    private static void clearInterior(ServerLevel level, BlockPos center) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                level.setBlock(center.offset(dx, 0, dz), Blocks.AIR.defaultBlockState(), 3);
            }
        }
    }

    private static void messageNearby(ServerLevel level, BlockPos center, Component message) {
        for (ServerPlayer player : level.getPlayers(player -> player.blockPosition().closerThan(center, 24.0D))) {
            player.sendSystemMessage(message);
        }
    }

    private static Block blockFor(GardenEyeType type) {
        return ModBlocks.PORTAL_FRAMES.get(type).get();
    }

    private static boolean isGardenFrame(BlockState state) {
        return state.getBlock() instanceof BiomePortalFrameBlock;
    }

    private static List<FramePlacement> createPlacements() {
        List<FramePlacement> placements = new ArrayList<>();
        GardenEyeType[] types = GardenEyeType.REQUIRED.toArray(GardenEyeType[]::new);
        int i = 0;
        for (int dx = -1; dx <= 1; dx++) {
            placements.add(new FramePlacement(types[i++], dx, -2, Direction.SOUTH));
        }
        for (int dz = -1; dz <= 1; dz++) {
            placements.add(new FramePlacement(types[i++], 2, dz, Direction.WEST));
        }
        for (int dx = 1; dx >= -1; dx--) {
            placements.add(new FramePlacement(types[i++], dx, 2, Direction.NORTH));
        }
        for (int dz = 1; dz >= -1; dz--) {
            placements.add(new FramePlacement(types[i++], -2, dz, Direction.EAST));
        }
        return List.copyOf(placements);
    }

    public record FramePlacement(GardenEyeType type, int dx, int dz, Direction facing) {
    }

    public record PortalStatus(int filled, boolean ready) {
    }
}

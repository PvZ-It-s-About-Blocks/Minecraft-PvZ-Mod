package net.PvZModders.PvZMod.progression.waves;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class WaveZombieSpawnManager {
    public static final int GARDEN_COMBAT_RADIUS = 15;
    public static final int ZOMBIE_SPAWN_MIN_RADIUS = 18;
    public static final int ZOMBIE_SPAWN_MAX_RADIUS = 28;
    public static final int SPAWN_POSITION_ATTEMPTS = 24;
    public static final int SPAWN_GROUP_OFFSET_RADIUS = 4;
    public static final int PORTAL_VISUAL_DURATION_TICKS = 20 * 6;

    private static final int SIDE_RANDOM_EXTRA = 6;
    private static final int FALLBACK_MIN_RADIUS = 5;
    private static final int FALLBACK_MAX_RADIUS = GARDEN_COMBAT_RADIUS;

    private WaveZombieSpawnManager() {
    }

    public static BlockPos findWaveSpawnPosition(ServerLevel level, BlockPos gardenCenter, WaveSpawnDirection direction, int seed) {
        BlockPos outside = findSpawnPositionForDirection(level, gardenCenter, direction, seed, ZOMBIE_SPAWN_MIN_RADIUS, ZOMBIE_SPAWN_MAX_RADIUS);
        if (outside != null) {
            return outside;
        }

        /*
         * If the outer approach terrain is made invalid with lava, holes, walls, or void gaps,
         * waves intentionally fall back inside the combat radius so terrain cheese cannot softlock
         * the defense or stop zombies from spawning.
         */
        BlockPos fallback = findSpawnPositionForDirection(level, gardenCenter, direction, seed + 7919, FALLBACK_MIN_RADIUS, FALLBACK_MAX_RADIUS);
        if (fallback != null) {
            return fallback;
        }

        BlockPos edgeFallback = findGroundAtOrNear(level, direction.borderPosition(gardenCenter, Math.min(GARDEN_COMBAT_RADIUS, 7), 0));
        if (edgeFallback != null && hasEnoughSpawnSpace(level, edgeFallback)) {
            return edgeFallback;
        }

        return gardenCenter.above();
    }

    public static BlockPos findNearbySpawnPosition(ServerLevel level, BlockPos gardenCenter, BlockPos anchor, int seed) {
        RandomSource random = RandomSource.create(level.getSeed() ^ seed ^ anchor.asLong());
        for (int attempt = 0; attempt < SPAWN_POSITION_ATTEMPTS; attempt++) {
            int xOffset = random.nextInt(SPAWN_GROUP_OFFSET_RADIUS * 2 + 1) - SPAWN_GROUP_OFFSET_RADIUS;
            int zOffset = random.nextInt(SPAWN_GROUP_OFFSET_RADIUS * 2 + 1) - SPAWN_GROUP_OFFSET_RADIUS;
            BlockPos candidate = findGroundAtOrNear(level, anchor.offset(xOffset, 0, zOffset));
            if (candidate != null && isValidWaveZombieSpawn(level, candidate, gardenCenter)) {
                return candidate;
            }
        }
        return anchor;
    }

    public static Map<UUID, Long> spawnPortalVisual(ServerLevel level, BlockPos anchor) {
        Map<UUID, Long> visuals = new HashMap<>();
        long expireAt = level.getGameTime() + PORTAL_VISUAL_DURATION_TICKS;
        BlockPos base = anchor.above();

        for (int x = -1; x <= 0; x++) {
            for (int y = 0; y < 3; y++) {
                Display.BlockDisplay display = EntityType.BLOCK_DISPLAY.create(level);
                if (display == null) {
                    continue;
                }
                display.load(createBlockDisplayTag(Blocks.NETHER_PORTAL.defaultBlockState(), 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.08F));
                display.setNoGravity(true);
                display.setPos(base.getX() + x + 0.5D, base.getY() + y, base.getZ() + 0.46D);
                level.addFreshEntity(display);
                visuals.put(display.getUUID(), expireAt);
            }
        }

        level.sendParticles(ParticleTypes.PORTAL, anchor.getX() + 0.5D, anchor.getY() + 1.5D, anchor.getZ() + 0.5D, 80, 1.2D, 1.4D, 0.25D, 0.08D);
        level.playSound(null, anchor, SoundEvents.PORTAL_TRIGGER, SoundSource.HOSTILE, 0.8F, 1.0F);
        return visuals;
    }

    public static void tickPortalVisuals(ServerLevel level, Map<UUID, Long> visuals) {
        if (visuals.isEmpty()) {
            return;
        }

        long gameTime = level.getGameTime();
        visuals.entrySet().removeIf(entry -> {
            Entity entity = level.getEntity(entry.getKey());
            if (entity == null) {
                return true;
            }
            if (gameTime >= entry.getValue()) {
                entity.discard();
                return true;
            }
            if (gameTime % 10L == 0L) {
                level.sendParticles(ParticleTypes.PORTAL, entity.getX() + 0.5D, entity.getY() + 0.5D, entity.getZ() + 0.5D, 3, 0.2D, 0.4D, 0.1D, 0.02D);
            }
            return false;
        });
    }

    public static void cleanupPortalVisuals(ServerLevel level, Map<UUID, Long> visuals) {
        for (UUID visualId : visuals.keySet()) {
            Entity entity = level.getEntity(visualId);
            if (entity != null) {
                entity.discard();
            }
        }
        visuals.clear();
    }

    private static BlockPos findSpawnPositionForDirection(ServerLevel level, BlockPos gardenCenter, WaveSpawnDirection direction, int seed, int minRadius, int maxRadius) {
        RandomSource random = RandomSource.create(level.getSeed() ^ seed ^ direction.ordinal() * 341873128712L);
        int sideRange = GARDEN_COMBAT_RADIUS + SIDE_RANDOM_EXTRA;

        for (int attempt = 0; attempt < SPAWN_POSITION_ATTEMPTS; attempt++) {
            int distance = minRadius + random.nextInt(Math.max(1, maxRadius - minRadius + 1));
            int sideOffset = random.nextInt(sideRange * 2 + 1) - sideRange;
            BlockPos candidate = findGroundAtOrNear(level, direction.offsetFrom(gardenCenter, distance, sideOffset));
            if (candidate != null && isValidWaveZombieSpawn(level, candidate, gardenCenter)) {
                return candidate;
            }
        }

        return null;
    }

    private static boolean isValidWaveZombieSpawn(ServerLevel level, BlockPos pos, BlockPos gardenCenter) {
        return level.hasChunkAt(pos)
                && hasEnoughSpawnSpace(level, pos)
                && !isDangerousSpawnBlock(level.getBlockState(pos))
                && !isDangerousSpawnBlock(level.getBlockState(pos.below()))
                && level.getBlockState(pos.below()).isFaceSturdy(level, pos.below(), Direction.UP)
                && canPathTowardGarden(level, pos, gardenCenter);
    }

    private static BlockPos findGroundAtOrNear(ServerLevel level, BlockPos candidate) {
        if (!level.hasChunkAt(candidate)) {
            return null;
        }

        BlockPos heightPos = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, candidate);
        if (heightPos.getY() <= level.getMinBuildHeight() + 1) {
            return null;
        }

        for (int yOffset = 0; yOffset >= -4; yOffset--) {
            BlockPos pos = heightPos.offset(0, yOffset, 0);
            if (hasEnoughSpawnSpace(level, pos) && level.getBlockState(pos.below()).isFaceSturdy(level, pos.below(), Direction.UP)) {
                return pos;
            }
        }
        return null;
    }

    private static boolean hasEnoughSpawnSpace(ServerLevel level, BlockPos pos) {
        BlockState feet = level.getBlockState(pos);
        BlockState head = level.getBlockState(pos.above());
        return feet.getCollisionShape(level, pos).isEmpty()
                && head.getCollisionShape(level, pos.above()).isEmpty()
                && feet.getFluidState().isEmpty()
                && head.getFluidState().isEmpty();
    }

    private static boolean isDangerousSpawnBlock(BlockState state) {
        return state.is(Blocks.LAVA)
                || state.getFluidState().is(FluidTags.LAVA)
                || state.getFluidState().is(FluidTags.WATER)
                || state.is(Blocks.FIRE)
                || state.is(Blocks.SOUL_FIRE)
                || state.is(Blocks.MAGMA_BLOCK)
                || state.is(Blocks.CACTUS)
                || state.is(Blocks.CAMPFIRE)
                || state.is(Blocks.SOUL_CAMPFIRE)
                || state.is(Blocks.POWDER_SNOW);
    }

    private static boolean canPathTowardGarden(ServerLevel level, BlockPos pos, BlockPos gardenCenter) {
        return level.hasChunkAt(pos) && level.hasChunkAt(gardenCenter);
    }

    private static CompoundTag createBlockDisplayTag(BlockState state, float translateX, float translateY, float translateZ, float scaleX, float scaleY, float scaleZ) {
        CompoundTag tag = new CompoundTag();
        tag.put("block_state", NbtUtils.writeBlockState(state));

        CompoundTag transformation = new CompoundTag();
        transformation.put("translation", floatList(translateX, translateY, translateZ));
        transformation.put("scale", floatList(scaleX, scaleY, scaleZ));
        transformation.put("left_rotation", floatList(0.0F, 0.0F, 0.0F, 1.0F));
        transformation.put("right_rotation", floatList(0.0F, 0.0F, 0.0F, 1.0F));
        tag.put("transformation", transformation);

        tag.putFloat("view_range", 64.0F);
        tag.putFloat("shadow_radius", 0.0F);
        tag.putFloat("shadow_strength", 0.0F);
        return tag;
    }

    private static ListTag floatList(float... values) {
        ListTag list = new ListTag();
        for (float value : values) {
            list.add(FloatTag.valueOf(value));
        }
        return list;
    }
}

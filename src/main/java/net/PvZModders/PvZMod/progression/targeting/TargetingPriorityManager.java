package net.PvZModders.PvZMod.progression.targeting;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class TargetingPriorityManager {
    private static final String TARGETING_PRIORITY_TAG = "PvZTargetingPriority";
    private static final String FOCUS_TARGET_UUID_TAG = "PvZFocusTargetUuid";
    private static final String FOCUS_TARGET_SET_TICK_TAG = "PvZFocusTargetSetTick";
    private static final String FOCUS_TARGET_EXPIRES_TICK_TAG = "PvZFocusTargetExpiresTick";
    private static final int FOCUS_TARGET_DURATION_TICKS = 20 * 20;

    private TargetingPriorityManager() {
    }

    public static TargetingPriority getPriority(Player player) {
        CompoundTag tag = player.getPersistentData();
        if (!tag.contains(TARGETING_PRIORITY_TAG)) {
            return TargetingPriority.FIRST;
        }
        try {
            return TargetingPriority.valueOf(tag.getString(TARGETING_PRIORITY_TAG));
        } catch (IllegalArgumentException ignored) {
            return TargetingPriority.FIRST;
        }
    }

    public static TargetingPriority cyclePriority(Player player) {
        TargetingPriority next = getPriority(player).next();
        player.getPersistentData().putString(TARGETING_PRIORITY_TAG, next.name());
        return next;
    }

    public static void setFocusTarget(Player player, LivingEntity target) {
        CompoundTag tag = player.getPersistentData();
        long gameTime = player.level().getGameTime();
        tag.putUUID(FOCUS_TARGET_UUID_TAG, target.getUUID());
        tag.putLong(FOCUS_TARGET_SET_TICK_TAG, gameTime);
        tag.putLong(FOCUS_TARGET_EXPIRES_TICK_TAG, gameTime + FOCUS_TARGET_DURATION_TICKS);
    }

    public static <T extends LivingEntity> Optional<T> selectFocusedTarget(ServerLevel level, List<T> targets) {
        if (targets.isEmpty()) {
            return Optional.empty();
        }

        long newestFocusTick = Long.MIN_VALUE;
        T focusedTarget = null;
        for (ServerPlayer player : level.players()) {
            CompoundTag tag = player.getPersistentData();
            if (!tag.hasUUID(FOCUS_TARGET_UUID_TAG) || level.getGameTime() > tag.getLong(FOCUS_TARGET_EXPIRES_TICK_TAG)) {
                continue;
            }

            UUID targetId = tag.getUUID(FOCUS_TARGET_UUID_TAG);
            long focusTick = tag.getLong(FOCUS_TARGET_SET_TICK_TAG);
            for (T target : targets) {
                if (target.isAlive() && target.getUUID().equals(targetId) && focusTick > newestFocusTick) {
                    newestFocusTick = focusTick;
                    focusedTarget = target;
                }
            }
        }
        return Optional.ofNullable(focusedTarget);
    }

    public static <T extends LivingEntity> Optional<T> selectTarget(List<T> targets, LivingEntity plant, TargetingPriority priority) {
        if (targets.isEmpty()) {
            return Optional.empty();
        }
        return switch (priority) {
            case FIRST -> targets.stream().min(Comparator.comparingDouble(plant::distanceToSqr));
            case LAST -> targets.stream().max(Comparator.comparingDouble(plant::distanceToSqr));
            case STRONG -> targets.stream().max(Comparator.comparingDouble(LivingEntity::getHealth));
            case WEAK -> targets.stream().min(Comparator.comparingDouble(LivingEntity::getHealth));
            case RANDOM -> Optional.of(targets.get(plant.getRandom().nextInt(targets.size())));
        };
    }
}

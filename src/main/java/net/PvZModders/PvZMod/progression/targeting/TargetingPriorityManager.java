package net.PvZModders.PvZMod.progression.targeting;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public final class TargetingPriorityManager {
    private static final String TARGETING_PRIORITY_TAG = "PvZTargetingPriority";

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

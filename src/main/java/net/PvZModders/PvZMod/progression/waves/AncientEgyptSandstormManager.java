package net.PvZModders.PvZMod.progression.waves;

import net.PvZModders.PvZMod.entity.custom.PvZZombieEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public final class AncientEgyptSandstormManager {
    public static final String BOOSTED_TAG = "PvZSandstormBoosted";
    public static final String END_TICK_TAG = "PvZSandstormEndTick";
    public static final String DAMAGE_REDUCTION_TAG = "PvZSandstormDamageReduction";

    private static final double SPEED_MULTIPLIER = 2.75D;
    private static final float DAMAGE_REDUCTION = 0.4F;

    private AncientEgyptSandstormManager() {
    }

    public static boolean tick(ServerLevel level, BlockPos gardenCenter, int wave, long elapsedTicks, Set<UUID> activeWaveEntityIds) {
        boolean active = AncientEgyptSandstormSchedule.isActive(wave, elapsedTicks);
        long gameTime = level.getGameTime();
        long maxRemaining = active ? maxRemainingTicks(wave, elapsedTicks) : 0L;

        for (UUID entityId : List.copyOf(activeWaveEntityIds)) {
            Entity entity = level.getEntity(entityId);
            if (!(entity instanceof Mob mob) || !mob.isAlive()) {
                continue;
            }
            if (active) {
                applySandstormBoost(level, mob, gardenCenter, gameTime + maxRemaining);
            } else {
                clearExpiredBoost(mob, gameTime);
            }
        }

        if (active && gameTime % 5L == 0L) {
            level.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, gardenCenter.getX() + 0.5D, gardenCenter.getY() + 2.0D, gardenCenter.getZ() + 0.5D, 70, 8.0D, 1.5D, 8.0D, 0.08D);
            level.sendParticles(ParticleTypes.POOF, gardenCenter.getX() + 0.5D, gardenCenter.getY() + 1.0D, gardenCenter.getZ() + 0.5D, 45, 7.0D, 0.7D, 7.0D, 0.05D);
        }
        return active;
    }

    public static void clearBoosts(ServerLevel level, Set<UUID> activeWaveEntityIds) {
        for (UUID entityId : activeWaveEntityIds) {
            Entity entity = level.getEntity(entityId);
            if (entity instanceof Mob mob) {
                clearSandstormBoost(mob);
            }
        }
    }

    private static void applySandstormBoost(ServerLevel level, Mob mob, BlockPos gardenCenter, long endTick) {
        CompoundTag tag = mob.getPersistentData();
        AttributeInstance speed = mob.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed != null) {
            speed.setBaseValue(baseSpeed(mob) * SPEED_MULTIPLIER);
        }
        if (!tag.getBoolean(BOOSTED_TAG)) {
            level.sendParticles(ParticleTypes.POOF, mob.getX(), mob.getY() + 0.7D, mob.getZ(), 8, 0.35D, 0.35D, 0.35D, 0.04D);
        }
        tag.putBoolean(BOOSTED_TAG, true);
        tag.putLong(END_TICK_TAG, endTick);
        tag.putFloat(DAMAGE_REDUCTION_TAG, DAMAGE_REDUCTION);

        Vec3 towardGarden = Vec3.atCenterOf(gardenCenter).subtract(mob.position()).multiply(1.0D, 0.0D, 1.0D);
        if (towardGarden.lengthSqr() > 1.0E-4D) {
            Vec3 push = towardGarden.normalize().scale(0.08D);
            mob.setDeltaMovement(mob.getDeltaMovement().add(push.x, 0.0D, push.z));
            mob.getNavigation().moveTo(gardenCenter.getX() + 0.5D, gardenCenter.getY(), gardenCenter.getZ() + 0.5D, 1.15D);
        }
    }

    private static void clearExpiredBoost(Mob mob, long gameTime) {
        if (mob.getPersistentData().getBoolean(BOOSTED_TAG) && gameTime >= mob.getPersistentData().getLong(END_TICK_TAG)) {
            clearSandstormBoost(mob);
        }
    }

    private static void clearSandstormBoost(Mob mob) {
        CompoundTag tag = mob.getPersistentData();
        if (!tag.getBoolean(BOOSTED_TAG)) {
            return;
        }

        AttributeInstance speed = mob.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed != null) {
            speed.setBaseValue(baseSpeed(mob));
        }
        tag.remove(BOOSTED_TAG);
        tag.remove(END_TICK_TAG);
        tag.remove(DAMAGE_REDUCTION_TAG);
    }

    private static double baseSpeed(Mob mob) {
        if (mob instanceof PvZZombieEntity zombie) {
            return zombie.configuredMovementSpeed();
        }
        AttributeInstance speed = mob.getAttribute(Attributes.MOVEMENT_SPEED);
        return speed == null ? 0.13D : Math.max(0.01D, speed.getBaseValue());
    }

    private static long maxRemainingTicks(int wave, long elapsedTicks) {
        long remaining = 20L;
        for (AncientEgyptSandstormSchedule.SandstormEvent event : AncientEgyptSandstormSchedule.eventsForWave(wave)) {
            if (event.isActive(elapsedTicks)) {
                remaining = Math.max(remaining, event.startTick() + event.durationTicks() - elapsedTicks);
            }
        }
        return remaining;
    }
}

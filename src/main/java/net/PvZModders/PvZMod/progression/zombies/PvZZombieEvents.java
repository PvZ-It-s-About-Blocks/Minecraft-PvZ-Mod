package net.PvZModders.PvZMod.progression.zombies;

import net.PvZModders.PvZMod.PvZ2Mod;
import net.PvZModders.PvZMod.block.entity.GardenTotemBlockEntity;
import net.PvZModders.PvZMod.entity.ModEntities;
import net.PvZModders.PvZMod.entity.custom.PvZZombieEntity;
import net.PvZModders.PvZMod.progression.waves.AncientEgyptSandstormManager;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Optional;

@Mod.EventBusSubscriber(modid = PvZ2Mod.MOD_ID)
public final class PvZZombieEvents {
    private PvZZombieEvents() {
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.getEntity().getPersistentData().getBoolean(AncientEgyptSandstormManager.BOOSTED_TAG)
                && event.getEntity().getPersistentData().contains(AncientEgyptSandstormManager.DAMAGE_REDUCTION_TAG)
                && event.getEntity().getPersistentData().getBoolean("PvZWaveZombie")) {
            float reduction = event.getEntity().getPersistentData().getFloat(AncientEgyptSandstormManager.DAMAGE_REDUCTION_TAG);
            event.setAmount(event.getAmount() * Math.max(0.0F, 1.0F - reduction));
        }

        if (!(event.getEntity() instanceof PvZZombieEntity zombie)
                || !zombie.isAlive()) {
            return;
        }

        if (zombie.definition().has(PvZZombieSpecial.PONCHO_SHIELD)) {
            tickPonchoShield(zombie, event);
        }

        if (zombie.definition().has(PvZZombieSpecial.CHICKEN_WRANGLER)
                && !zombie.getPersistentData().getBoolean(PvZZombieEntity.CHICKEN_WRANGLER_RELEASED_TAG)
                && zombie.getHealth() - event.getAmount() <= zombie.getMaxHealth() * 0.5F) {
            releaseZombieChickens(zombie);
        }

        if (!zombie.definition().has(PvZZombieSpecial.SCREEN_DOOR_SHIELD)) {
            return;
        }

        Entity damageSource = event.getSource().getDirectEntity();
        if (damageSource == null || !isDamageFromFront(zombie, damageSource.position())) {
            return;
        }

        event.setAmount(event.getAmount() * 0.5F);
        if (zombie.level() instanceof ServerLevel level) {
            level.sendParticles(ParticleTypes.CRIT, zombie.getX(), zombie.getY() + 1.0D, zombie.getZ(), 5, 0.25D, 0.25D, 0.25D, 0.02D);
        }
    }

    private static void tickPonchoShield(PvZZombieEntity zombie, LivingHurtEvent event) {
        if (zombie.getHealth() <= zombie.getMaxHealth() * 0.5F) {
            zombie.getPersistentData().putBoolean(PvZZombieEntity.PONCHO_SHIELD_ACTIVE_TAG, false);
        }
        if (!zombie.getPersistentData().getBoolean(PvZZombieEntity.PONCHO_SHIELD_ACTIVE_TAG)) {
            return;
        }

        Entity direct = event.getSource().getDirectEntity();
        if (direct instanceof Projectile) {
            event.setAmount(event.getAmount() * 0.65F);
            if (zombie.level() instanceof ServerLevel level) {
                level.sendParticles(ParticleTypes.CRIT, zombie.getX(), zombie.getY() + 1.0D, zombie.getZ(), 4, 0.25D, 0.25D, 0.25D, 0.02D);
            }
        }
    }

    private static void releaseZombieChickens(PvZZombieEntity wrangler) {
        if (!(wrangler.level() instanceof ServerLevel level)) {
            return;
        }

        wrangler.getPersistentData().putBoolean(PvZZombieEntity.CHICKEN_WRANGLER_RELEASED_TAG, true);
        Optional.ofNullable(ModEntities.ZOMBIES.get("zombie_chicken"))
                .map(registryObject -> registryObject.get())
                .ifPresent(chickenType -> spawnChickens(level, wrangler, chickenType));
    }

    private static void spawnChickens(ServerLevel level, PvZZombieEntity wrangler, EntityType<PvZZombieEntity> chickenType) {
        int count = 4 + level.random.nextInt(3);
        for (int i = 0; i < count; i++) {
            PvZZombieEntity chicken = chickenType.create(level);
            if (chicken == null) {
                continue;
            }
            double x = wrangler.getX() + (level.random.nextDouble() - 0.5D) * 1.6D;
            double z = wrangler.getZ() + (level.random.nextDouble() - 0.5D) * 1.6D;
            chicken.moveTo(x, wrangler.getY(), z, wrangler.getYRot(), 0.0F);
            chicken.getPersistentData().putBoolean(GardenTotemBlockEntity.WAVE_ZOMBIE_TAG, wrangler.getPersistentData().getBoolean(GardenTotemBlockEntity.WAVE_ZOMBIE_TAG));
            copyGardenCenter(wrangler, chicken);
            chicken.finalizeSpawn(level, level.getCurrentDifficultyAt(chicken.blockPosition()), MobSpawnType.EVENT, null, null);
            level.addFreshEntity(chicken);
            chicken.configureForWave(0.13D);
        }
        level.sendParticles(ParticleTypes.CLOUD, wrangler.getX(), wrangler.getY() + 0.8D, wrangler.getZ(), 30, 0.6D, 0.3D, 0.6D, 0.05D);
        level.playSound(null, wrangler.blockPosition(), SoundEvents.CHICKEN_AMBIENT, SoundSource.HOSTILE, 0.9F, 0.65F);
    }

    private static void copyGardenCenter(PvZZombieEntity from, PvZZombieEntity to) {
        if (!from.getPersistentData().contains(PvZZombieEntity.GARDEN_CENTER_X_TAG)) {
            return;
        }
        to.getPersistentData().putInt(PvZZombieEntity.GARDEN_CENTER_X_TAG, from.getPersistentData().getInt(PvZZombieEntity.GARDEN_CENTER_X_TAG));
        to.getPersistentData().putInt(PvZZombieEntity.GARDEN_CENTER_Y_TAG, from.getPersistentData().getInt(PvZZombieEntity.GARDEN_CENTER_Y_TAG));
        to.getPersistentData().putInt(PvZZombieEntity.GARDEN_CENTER_Z_TAG, from.getPersistentData().getInt(PvZZombieEntity.GARDEN_CENTER_Z_TAG));
    }

    private static boolean isDamageFromFront(PvZZombieEntity zombie, Vec3 sourcePosition) {
        Vec3 facing = zombie.getLookAngle().multiply(1.0D, 0.0D, 1.0D);
        Vec3 incoming = sourcePosition.subtract(zombie.position()).multiply(1.0D, 0.0D, 1.0D);
        if (facing.lengthSqr() < 1.0E-4D || incoming.lengthSqr() < 1.0E-4D) {
            return false;
        }
        return facing.normalize().dot(incoming.normalize()) > 0.35D;
    }
}

package net.PvZModders.PvZMod.progression.zombies;

import net.PvZModders.PvZMod.PvZ2Mod;
import net.PvZModders.PvZMod.block.entity.GardenTotemBlockEntity;
import net.PvZModders.PvZMod.entity.ModEntities;
import net.PvZModders.PvZMod.entity.custom.PvZZombieEntity;
import net.PvZModders.PvZMod.progression.seed.PlantEntityManager;
import net.PvZModders.PvZMod.progression.waves.AncientEgyptSandstormManager;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
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
        if (zombie.definition().has(PvZZombieSpecial.ICE_BLOCK) && isHotDamage(event)) {
            event.setAmount(event.getAmount() * 1.25F);
            if (zombie.level() instanceof ServerLevel level) {
                level.sendParticles(ParticleTypes.FLAME, zombie.getX(), zombie.getY() + 1.0D, zombie.getZ(), 6, 0.25D, 0.25D, 0.25D, 0.02D);
            }
        }

        if (zombie.definition().has(PvZZombieSpecial.CHICKEN_WRANGLER)
                && !zombie.getPersistentData().getBoolean(PvZZombieEntity.CHICKEN_WRANGLER_RELEASED_TAG)
                && zombie.getHealth() - event.getAmount() <= zombie.getMaxHealth() * 0.5F) {
            releaseZombieChickens(zombie);
        }
        if (zombie.definition().has(PvZZombieSpecial.WEASEL_HOARDER)
                && !zombie.getPersistentData().getBoolean(PvZZombieEntity.WEASEL_HOARDER_RELEASED_TAG)
                && zombie.getHealth() - event.getAmount() <= zombie.getMaxHealth() * 0.5F) {
            releaseZombieWeasels(zombie);
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

    @SubscribeEvent
    public static void onProjectileImpact(ProjectileImpactEvent event) {
        if (!(event.getProjectile() instanceof Snowball snowball)
                || !snowball.getPersistentData().getBoolean(PvZZombieEntity.HUNTER_FREEZE_SHOT_TAG)
                || !(snowball.level() instanceof ServerLevel level)
                || !(event.getRayTraceResult() instanceof EntityHitResult hit)
                || !(hit.getEntity() instanceof SnowGolem plant)
                || !PlantEntityManager.isPlant(plant)) {
            return;
        }

        PlantEntityManager.addHunterFreezeStage(level, plant);
        snowball.discard();
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
                .ifPresent(chickenType -> spawnSwarm(level, wrangler, chickenType, 4 + level.random.nextInt(3), SoundEvents.CHICKEN_AMBIENT, ParticleTypes.CLOUD));
    }

    private static void releaseZombieWeasels(PvZZombieEntity hoarder) {
        if (!(hoarder.level() instanceof ServerLevel level)) {
            return;
        }

        hoarder.getPersistentData().putBoolean(PvZZombieEntity.WEASEL_HOARDER_RELEASED_TAG, true);
        Optional.ofNullable(ModEntities.ZOMBIES.get("zombie_weasel"))
                .map(registryObject -> registryObject.get())
                .ifPresent(weaselType -> spawnSwarm(level, hoarder, weaselType, 4 + level.random.nextInt(3), SoundEvents.FOX_SCREECH, ParticleTypes.SNOWFLAKE));
    }

    private static void spawnSwarm(ServerLevel level, PvZZombieEntity source, EntityType<PvZZombieEntity> swarmType, int count, net.minecraft.sounds.SoundEvent sound, net.minecraft.core.particles.ParticleOptions particle) {
        for (int i = 0; i < count; i++) {
            PvZZombieEntity swarm = swarmType.create(level);
            if (swarm == null) {
                continue;
            }
            double x = source.getX() + (level.random.nextDouble() - 0.5D) * 1.6D;
            double z = source.getZ() + (level.random.nextDouble() - 0.5D) * 1.6D;
            swarm.moveTo(x, source.getY(), z, source.getYRot(), 0.0F);
            swarm.getPersistentData().putBoolean(GardenTotemBlockEntity.WAVE_ZOMBIE_TAG, source.getPersistentData().getBoolean(GardenTotemBlockEntity.WAVE_ZOMBIE_TAG));
            copyGardenCenter(source, swarm);
            swarm.finalizeSpawn(level, level.getCurrentDifficultyAt(swarm.blockPosition()), MobSpawnType.EVENT, null, null);
            level.addFreshEntity(swarm);
            swarm.configureForWave(0.13D);
        }
        level.sendParticles(particle, source.getX(), source.getY() + 0.8D, source.getZ(), 30, 0.6D, 0.3D, 0.6D, 0.05D);
        level.playSound(null, source.blockPosition(), sound, SoundSource.HOSTILE, 0.9F, 0.65F);
    }

    private static boolean isHotDamage(LivingHurtEvent event) {
        Entity source = event.getSource().getEntity();
        Entity direct = event.getSource().getDirectEntity();
        return event.getSource().is(net.minecraft.tags.DamageTypeTags.IS_FIRE)
                || PlantEntityManager.isHotPlantEntity(source)
                || PlantEntityManager.isHotPlantEntity(direct)
                || direct instanceof Projectile projectile && projectile.isOnFire();
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

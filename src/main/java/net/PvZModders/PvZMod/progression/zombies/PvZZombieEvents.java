package net.PvZModders.PvZMod.progression.zombies;

import net.PvZModders.PvZMod.PvZ2Mod;
import net.PvZModders.PvZMod.block.entity.GardenTotemBlockEntity;
import net.PvZModders.PvZMod.entity.ModEntities;
import net.PvZModders.PvZMod.entity.custom.PvZZombieEntity;
import net.PvZModders.PvZMod.progression.coins.ZombieCoinDropManager;
import net.PvZModders.PvZMod.progression.beach.BigWaveBeachTideManager;
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
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Optional;

@Mod.EventBusSubscriber(modid = PvZ2Mod.MOD_ID)
public final class PvZZombieEvents {
    private static final String PROJECTILE_KIND_TAG = "PvZProjectileKind";

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
        if (zombie.getPersistentData().getLong(PvZZombieEntity.ROYAL_GUARD_END_TICK_TAG) > zombie.level().getGameTime()) {
            event.setAmount(event.getAmount() * 0.7F);
            if (zombie.level() instanceof ServerLevel level && level.getGameTime() % 5L == 0L) {
                level.sendParticles(ParticleTypes.ENCHANT, zombie.getX(), zombie.getY() + 1.1D, zombie.getZ(), 3, 0.25D, 0.35D, 0.25D, 0.01D);
            }
        }
        if (zombie.definition().has(PvZZombieSpecial.JESTER_SPIN)
                && zombie.getPersistentData().getBoolean(PvZZombieEntity.JESTER_SPINNING_TAG)
                && isStraightProjectileDamage(event)) {
            event.setAmount(event.getAmount() * 0.2F);
            if (zombie.level() instanceof ServerLevel level) {
                level.sendParticles(ParticleTypes.CRIT, zombie.getX(), zombie.getY() + 1.0D, zombie.getZ(), 10, 0.35D, 0.45D, 0.35D, 0.03D);
                level.playSound(null, zombie.blockPosition(), SoundEvents.SHIELD_BLOCK, SoundSource.HOSTILE, 0.6F, 1.4F);
            }
        }
        if (zombie.definition().has(PvZZombieSpecial.EXCAVATOR_SHIELD) && isStraightProjectileDamage(event)) {
            Entity direct = event.getSource().getDirectEntity();
            Entity source = event.getSource().getEntity();
            Vec3 sourcePosition = direct != null ? direct.position() : source != null ? source.position() : null;
            if (sourcePosition == null || isDamageFromFront(zombie, sourcePosition)) {
                event.setAmount(event.getAmount() * 0.4F);
                if (zombie.level() instanceof ServerLevel level) {
                    level.sendParticles(ParticleTypes.CRIT, zombie.getX(), zombie.getY() + 1.0D, zombie.getZ(), 8, 0.3D, 0.3D, 0.3D, 0.02D);
                }
            }
        }
        if (zombie.definition().has(PvZZombieSpecial.PARASOL_SHIELD) && isLobbedProjectileDamage(event)) {
            event.setAmount(event.getAmount() * 0.5F);
            if (zombie.level() instanceof ServerLevel level) {
                level.sendParticles(ParticleTypes.CLOUD, zombie.getX(), zombie.getY() + 1.4D, zombie.getZ(), 8, 0.35D, 0.2D, 0.35D, 0.02D);
            }
        }
        if (zombie.definition().has(PvZZombieSpecial.ICE_BLOCK) && isHotDamage(event)) {
            event.setAmount(event.getAmount() * 1.25F);
            if (zombie.level() instanceof ServerLevel level) {
                level.sendParticles(ParticleTypes.FLAME, zombie.getX(), zombie.getY() + 1.0D, zombie.getZ(), 6, 0.25D, 0.25D, 0.25D, 0.02D);
            }
        }
        if (zombie.definition().id().equals("snorkel_zombie") && isBeachWaterTile(zombie) && isStraightProjectileDamage(event)) {
            event.setAmount(event.getAmount() * 0.75F);
            if (zombie.level() instanceof ServerLevel level) {
                level.sendParticles(ParticleTypes.BUBBLE, zombie.getX(), zombie.getY() + 0.7D, zombie.getZ(), 6, 0.25D, 0.25D, 0.25D, 0.02D);
            }
        }
        if (zombie.definition().has(PvZZombieSpecial.SURFER)
                && zombie.getPersistentData().getBoolean("PvZSurferBoardActive")) {
            double taken = zombie.getPersistentData().getDouble("PvZSurferDamageTaken") + event.getAmount();
            zombie.getPersistentData().putDouble("PvZSurferDamageTaken", taken);
            if (taken >= 25.0D) {
                breakSurferBoard(zombie);
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
        if (zombie.definition().has(PvZZombieSpecial.PORTER_GARGANTUAR)
                && !zombie.getPersistentData().getBoolean(PvZZombieEntity.PORTER_GARGANTUAR_THROWN_IMP_TAG)
                && zombie.getHealth() - event.getAmount() <= zombie.getMaxHealth() * 0.5F) {
            releaseImpPorter(zombie);
        }
        if (zombie.definition().has(PvZZombieSpecial.DARK_AGES_GARGANTUAR)
                && !zombie.getPersistentData().getBoolean(PvZZombieEntity.DARK_AGES_GARGANTUAR_THROWN_IMP_TAG)
                && zombie.getHealth() - event.getAmount() <= zombie.getMaxHealth() * 0.5F) {
            releaseDragonImp(zombie);
        }
        if (zombie.definition().has(PvZZombieSpecial.NEON_GARGANTUAR)
                && !zombie.getPersistentData().getBoolean(PvZZombieEntity.NEON_GARGANTUAR_THROWN_IMP_TAG)
                && zombie.getHealth() - event.getAmount() <= zombie.getMaxHealth() * 0.5F) {
            releaseEightBitZombie(zombie);
        }
        if (zombie.definition().has(PvZZombieSpecial.JURASSIC_GARGANTUAR)
                && !zombie.getPersistentData().getBoolean(PvZZombieEntity.JURASSIC_GARGANTUAR_THROWN_IMP_TAG)
                && zombie.getHealth() - event.getAmount() <= zombie.getMaxHealth() * 0.5F) {
            releaseJurassicImp(zombie);
        }
        if (zombie.definition().has(PvZZombieSpecial.DEEP_SEA_GARGANTUAR)
                && !zombie.getPersistentData().getBoolean(PvZZombieEntity.DEEP_SEA_GARGANTUAR_THROWN_IMP_TAG)
                && zombie.getHealth() - event.getAmount() <= zombie.getMaxHealth() * 0.5F) {
            releaseMermaidImp(zombie);
        }
        if (zombie.definition().has(PvZZombieSpecial.PIRATE_GARGANTUAR)
                && !zombie.getPersistentData().getBoolean(PvZZombieEntity.PIRATE_GARGANTUAR_THROWN_IMP_TAG)
                && zombie.getHealth() - event.getAmount() <= zombie.getMaxHealth() * 0.5F) {
            releasePirateImp(zombie);
        }
        if (zombie.definition().has(PvZZombieSpecial.MODERN_GARGANTUAR)
                && !zombie.getPersistentData().getBoolean(PvZZombieEntity.MODERN_GARGANTUAR_THROWN_IMP_TAG)
                && zombie.getHealth() - event.getAmount() <= zombie.getMaxHealth() * 0.5F) {
            releaseSuperFanImp(zombie);
        }
        if (zombie.definition().has(PvZZombieSpecial.GARGANTUAR_PRIME)
                && !zombie.getPersistentData().getBoolean(PvZZombieEntity.GARGANTUAR_PRIME_THROWN_IMP_TAG)
                && zombie.getHealth() - event.getAmount() <= zombie.getMaxHealth() * 0.5F) {
            releaseBugBotImp(zombie);
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

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof PvZZombieEntity zombie)
                || !zombie.getPersistentData().getBoolean(GardenTotemBlockEntity.WAVE_ZOMBIE_TAG)
                || !(zombie.level() instanceof ServerLevel level)) {
            return;
        }

        ZombieCoinDropManager.dropZombieCoins(zombie);
        if ("yeti_zombie".equals(zombie.definition().id()) || zombie.definition().has(PvZZombieSpecial.YETI_FLEE)) {
            level.sendParticles(ParticleTypes.HAPPY_VILLAGER, zombie.getX(), zombie.getY() + 1.0D, zombie.getZ(), 24, 0.5D, 0.45D, 0.5D, 0.04D);
            level.playSound(null, zombie.blockPosition(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.7F, 1.2F);
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

    private static void releaseImpPorter(PvZZombieEntity gargantuar) {
        if (!(gargantuar.level() instanceof ServerLevel level)) {
            return;
        }

        gargantuar.getPersistentData().putBoolean(PvZZombieEntity.PORTER_GARGANTUAR_THROWN_IMP_TAG, true);
        Optional.ofNullable(ModEntities.ZOMBIES.get("imp_porter"))
                .map(registryObject -> registryObject.get())
                .ifPresent(impType -> spawnSwarm(level, gargantuar, impType, 1, SoundEvents.ZOMBIE_INFECT, ParticleTypes.CRIT));
    }

    private static void releaseDragonImp(PvZZombieEntity gargantuar) {
        if (!(gargantuar.level() instanceof ServerLevel level)) {
            return;
        }

        gargantuar.getPersistentData().putBoolean(PvZZombieEntity.DARK_AGES_GARGANTUAR_THROWN_IMP_TAG, true);
        Optional.ofNullable(ModEntities.ZOMBIES.get("dragon_imp"))
                .map(registryObject -> registryObject.get())
                .ifPresent(impType -> spawnSwarm(level, gargantuar, impType, 1, SoundEvents.BLAZE_SHOOT, ParticleTypes.FLAME));
    }

    private static void releaseEightBitZombie(PvZZombieEntity gargantuar) {
        if (!(gargantuar.level() instanceof ServerLevel level)) {
            return;
        }

        gargantuar.getPersistentData().putBoolean(PvZZombieEntity.NEON_GARGANTUAR_THROWN_IMP_TAG, true);
        Optional.ofNullable(ModEntities.ZOMBIES.get("eight_bit_zombie"))
                .map(registryObject -> registryObject.get())
                .ifPresent(eightBitType -> spawnSwarm(level, gargantuar, eightBitType, 1, SoundEvents.NOTE_BLOCK_PLING.get(), ParticleTypes.NOTE));
    }

    private static void releaseJurassicImp(PvZZombieEntity gargantuar) {
        if (!(gargantuar.level() instanceof ServerLevel level)) {
            return;
        }

        gargantuar.getPersistentData().putBoolean(PvZZombieEntity.JURASSIC_GARGANTUAR_THROWN_IMP_TAG, true);
        Optional.ofNullable(ModEntities.ZOMBIES.get("jurassic_imp"))
                .map(registryObject -> registryObject.get())
                .ifPresent(impType -> spawnSwarm(level, gargantuar, impType, 1, SoundEvents.SNIFFER_DIGGING, ParticleTypes.CAMPFIRE_COSY_SMOKE));
    }

    private static void releaseMermaidImp(PvZZombieEntity gargantuar) {
        if (!(gargantuar.level() instanceof ServerLevel level)) {
            return;
        }

        gargantuar.getPersistentData().putBoolean(PvZZombieEntity.DEEP_SEA_GARGANTUAR_THROWN_IMP_TAG, true);
        Optional.ofNullable(ModEntities.ZOMBIES.get("mermaid_imp"))
                .map(registryObject -> registryObject.get())
                .ifPresent(impType -> spawnSwarm(level, gargantuar, impType, 1, SoundEvents.DOLPHIN_SPLASH, ParticleTypes.BUBBLE));
    }

    private static void releasePirateImp(PvZZombieEntity gargantuar) {
        if (!(gargantuar.level() instanceof ServerLevel level)) {
            return;
        }

        gargantuar.getPersistentData().putBoolean(PvZZombieEntity.PIRATE_GARGANTUAR_THROWN_IMP_TAG, true);
        Optional.ofNullable(ModEntities.ZOMBIES.get("pirate_imp"))
                .map(registryObject -> registryObject.get())
                .ifPresent(impType -> spawnSwarm(level, gargantuar, impType, 1, SoundEvents.CROSSBOW_SHOOT, ParticleTypes.CLOUD));
    }

    private static void releaseSuperFanImp(PvZZombieEntity gargantuar) {
        if (!(gargantuar.level() instanceof ServerLevel level)) {
            return;
        }

        gargantuar.getPersistentData().putBoolean(PvZZombieEntity.MODERN_GARGANTUAR_THROWN_IMP_TAG, true);
        Optional.ofNullable(ModEntities.ZOMBIES.get("super_fan_imp"))
                .map(registryObject -> registryObject.get())
                .ifPresent(impType -> spawnSwarm(level, gargantuar, impType, 1, SoundEvents.ARMOR_EQUIP_LEATHER, ParticleTypes.CLOUD));
    }

    private static void releaseBugBotImp(PvZZombieEntity gargantuar) {
        if (!(gargantuar.level() instanceof ServerLevel level)) {
            return;
        }

        gargantuar.getPersistentData().putBoolean(PvZZombieEntity.GARGANTUAR_PRIME_THROWN_IMP_TAG, true);
        Optional.ofNullable(ModEntities.ZOMBIES.get("bug_bot_imp"))
                .map(registryObject -> registryObject.get())
                .ifPresent(impType -> spawnSwarm(level, gargantuar, impType, 1, SoundEvents.NOTE_BLOCK_BIT.get(), ParticleTypes.ELECTRIC_SPARK));
    }

    private static void breakSurferBoard(PvZZombieEntity surfer) {
        surfer.getPersistentData().putBoolean("PvZSurferBoardActive", false);
        surfer.configureForWave(0.13D);
        if (surfer.level() instanceof ServerLevel level) {
            level.sendParticles(ParticleTypes.SPLASH, surfer.getX(), surfer.getY() + 0.5D, surfer.getZ(), 16, 0.35D, 0.25D, 0.35D, 0.04D);
            level.playSound(null, surfer.blockPosition(), SoundEvents.WOOD_BREAK, SoundSource.HOSTILE, 0.75F, 1.1F);
        }
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

    private static boolean isBeachWaterTile(PvZZombieEntity zombie) {
        if (!(zombie.level() instanceof ServerLevel level)) {
            return zombie.isInWater();
        }
        net.minecraft.core.BlockPos pos = zombie.blockPosition();
        return zombie.isInWater()
                || level.getBlockState(pos).is(net.minecraft.world.level.block.Blocks.WATER)
                || BigWaveBeachTideManager.isTileFlooded(level, pos)
                || BigWaveBeachTideManager.isTileFlooded(level, pos.below());
    }

    private static boolean isStraightProjectileDamage(LivingHurtEvent event) {
        Entity direct = event.getSource().getDirectEntity();
        Entity source = event.getSource().getEntity();
        if (direct instanceof Projectile projectile) {
            return !isLobbedProjectileKind(projectile.getPersistentData().getString(PROJECTILE_KIND_TAG));
        }
        String behavior = plantBehavior(source);
        return "PEASHOOTER".equals(behavior)
                || "REPEATER".equals(behavior)
                || "SPLIT_PEA".equals(behavior)
                || "PEA_POD".equals(behavior)
                || "RED_STINGER".equals(behavior)
                || "PUFF_SHROOM".equals(behavior)
                || "SPORE_SHROOM".equals(behavior)
                || "ROTOBAGA".equals(behavior)
                || "GUACODILE".equals(behavior);
    }

    private static boolean isLobbedProjectileDamage(LivingHurtEvent event) {
        Entity direct = event.getSource().getDirectEntity();
        Entity source = event.getSource().getEntity();
        if (direct instanceof Projectile projectile && isLobbedProjectileKind(projectile.getPersistentData().getString(PROJECTILE_KIND_TAG))) {
            return true;
        }
        String behavior = plantBehavior(source);
        return "KERNEL_PULT".equals(behavior)
                || "MELON_PULT".equals(behavior)
                || "WINTER_MELON".equals(behavior)
                || "PEPPER_PULT".equals(behavior)
                || "AKEE".equals(behavior)
                || "COCONUT_CANNON".equals(behavior)
                || "BANANA_LAUNCHER".equals(behavior)
                || "DUSK_LOBBER".equals(behavior);
    }

    private static boolean isLobbedProjectileKind(String kind) {
        return "kernel".equals(kind)
                || "butter".equals(kind)
                || "melon".equals(kind)
                || "winter_melon".equals(kind)
                || "pepper_pult".equals(kind)
                || "akee_seed".equals(kind)
                || "coconut_cannon".equals(kind)
                || "banana".equals(kind)
                || "shadow_lob".equals(kind)
                || "powered_shadow_lob".equals(kind);
    }

    private static String plantBehavior(Entity entity) {
        if (entity == null || !entity.getPersistentData().contains(PlantEntityManager.PLANT_BEHAVIOR_TAG)) {
            return "";
        }
        return entity.getPersistentData().getString(PlantEntityManager.PLANT_BEHAVIOR_TAG);
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

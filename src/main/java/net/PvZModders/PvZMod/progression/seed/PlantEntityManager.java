package net.PvZModders.PvZMod.progression.seed;

import net.PvZModders.PvZMod.PvZ2Mod;
import net.PvZModders.PvZMod.block.ModBlocks;
import net.PvZModders.PvZMod.entity.ModEntities;
import net.PvZModders.PvZMod.entity.custom.JurassicDinosaurEntity;
import net.PvZModders.PvZMod.entity.custom.WildWestMinecartEntity;
import net.PvZModders.PvZMod.progression.gold.GoldTileManager;
import net.PvZModders.PvZMod.progression.sun.SunManager;
import net.PvZModders.PvZMod.progression.targeting.TargetingPriority;
import net.PvZModders.PvZMod.progression.targeting.TargetingPriorityManager;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.util.Mth;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = PvZ2Mod.MOD_ID)
public final class PlantEntityManager {
    public static final String PLANT_TAG = "PvZPlant";
    public static final String PLANT_ID_TAG = "PvZPlantId";
    public static final String PLANT_BEHAVIOR_TAG = "PvZPlantBehavior";
    public static final String NEXT_ACTION_TICK_TAG = "PvZPlantNextActionTick";
    public static final String CHOMPER_COOLDOWN_TICK_TAG = "PvZChomperCooldownTick";
    private static final String GRAVE_X_TAG = "PvZGraveX";
    private static final String GRAVE_Y_TAG = "PvZGraveY";
    private static final String GRAVE_Z_TAG = "PvZGraveZ";
    private static final String TORCHWOOD_BUFFED_TAG = "PvZTorchwoodBuffed";
    private static final String PEA_POD_STACK_TAG = "PvZPeaPodStack";
    private static final String PROJECTILE_KIND_TAG = "PvZProjectileKind";
    private static final String RED_STINGER_MODE_TAG = "PvZRedStingerMode";
    private static final String PLANT_PLACED_TICK_TAG = "PvZPlantPlacedTick";
    private static final String SUN_SHROOM_STAGE_TAG = "PvZSunShroomStage";
    private static final String SUN_BEAN_INFECTED_TAG = "PvZSunBeanInfected";
    private static final String SUN_BEAN_EXPIRES_TICK_TAG = "PvZSunBeanExpiresTick";
    private static final String SUN_BEAN_NEXT_SUN_TICK_TAG = "PvZSunBeanNextSunTick";
    private static final String ARMOR_STRIPPED_TAG = "PvZArmorStripped";
    private static final String METAL_ZOMBIE_TAG = "PvZMetalZombie";
    private static final String CELERY_ACTIVATED_TAG = "PvZCeleryActivated";
    private static final String SPORE_SHROOM_SOURCE_TAG = "PvZSporeShroomSource";
    private static final String PLANT_PROJECTILE_TAG = "PvZPlantProjectile";
    private static final String REPEATER_PENDING_SECOND_SHOT_TAG = "PvZRepeaterPendingSecondShot";
    private static final String REPEATER_TARGET_UUID_TAG = "PvZRepeaterTargetUuid";

    private static final double PLANT_SCAN_RADIUS = 128.0D;
    private static final double SHOOTER_RANGE = 14.0D;
    private static final double PUFF_SHROOM_RANGE = 4.0D;
    private static final double FUME_SHROOM_RANGE = 6.0D;
    private static final double MAGNET_SHROOM_RANGE = 8.0D;
    private static final double PERFUME_SHROOM_RANGE = 8.0D;
    private static final double PHAT_BEET_RADIUS = 3.0D;
    private static final double SPORE_SHROOM_RANGE = 12.0D;
    private static final int SHOOTER_INTERVAL_TICKS = 30;
    private static final int REPEATER_SECOND_SHOT_DELAY_TICKS = 6;
    private static final int PHAT_BEET_INTERVAL_TICKS = 40;
    private static final int CELERY_STALKER_INTERVAL_TICKS = 12;
    private static final int SUNFLOWER_INTERVAL_TICKS = 60;
    private static final int SUN_SHROOM_INTERVAL_TICKS = 80;
    private static final int SUN_SHROOM_STAGE_TWO_TICKS = 20 * 60;
    private static final int SUN_SHROOM_STAGE_THREE_TICKS = 20 * 120;
    private static final int PUFF_SHROOM_LIFETIME_TICKS = 20 * 60;
    private static final int FUME_SHROOM_INTERVAL_TICKS = 35;
    private static final int BONK_CHOY_INTERVAL_TICKS = 10;
    private static final int GRAVE_BUSTER_EAT_TICKS = 60;
    private static final int CHOMPER_COOLDOWN_TICKS = 100;
    private static final int LIGHTNING_REED_INTERVAL_TICKS = 25;
    private static final int MELON_PULT_INTERVAL_TICKS = 45;
    private static final int PRIMAL_POTATO_ARM_TICKS = 20;
    private static final int SUN_BEAN_INFECTED_TICKS = 20 * 15;
    private static final int SUN_BEAN_SUN_COOLDOWN_TICKS = 10;
    private static final int MAGNET_SHROOM_COOLDOWN_TICKS = 20 * 10;
    private static final int RECENT_PLANT_DEATH_WINDOW_TICKS = 20 * 60;
    private static final int MAX_SPORE_SHROOM_CLONES_NEARBY = 12;
    private static final float PEA_DAMAGE = 4.0F;
    private static final float PRIMAL_PEA_DAMAGE = 7.0F;
    private static final float PUFF_SHROOM_DAMAGE = 2.0F;
    private static final float FUME_SHROOM_DAMAGE = 4.0F;
    private static final float POTATO_MINE_DAMAGE = 24.0F;
    private static final float PRIMAL_POTATO_MINE_DAMAGE = 36.0F;
    private static final float CHOMPER_DAMAGE = 40.0F;
    private static final float BLOOMERANG_DAMAGE = 5.0F;
    private static final float BONK_CHOY_DAMAGE = 3.0F;
    private static final float CHILI_BEAN_DAMAGE = 40.0F;
    private static final float LIGHTNING_REED_DAMAGE = 5.0F;
    private static final float MELON_DIRECT_DAMAGE = 10.0F;
    private static final float MELON_SPLASH_DAMAGE = 5.0F;
    private static final float WINTER_MELON_DIRECT_DAMAGE = 12.0F;
    private static final float WINTER_MELON_SPLASH_DAMAGE = 7.0F;
    private static final float RED_STINGER_STRONG_DAMAGE = 7.0F;
    private static final float RED_STINGER_NORMAL_DAMAGE = 4.0F;
    private static final float AKEE_DAMAGE = 6.0F;
    private static final float ENDURIAN_THORN_DAMAGE = 3.0F;
    private static final float PHAT_BEET_DAMAGE = 4.0F;
    private static final float CELERY_STALKER_DAMAGE = 8.0F;
    private static final float SPORE_SHROOM_DAMAGE = 5.0F;
    private static final int SUN_BEAN_SUN_VALUE = 5;
    private static final float DEFAULT_PLANT_HEALTH = 20.0F;
    private static final float WALL_NUT_HEALTH = 80.0F;
    private static final float PRIMAL_WALL_NUT_HEALTH = 120.0F;
    private static final float TALL_NUT_HEALTH = 150.0F;
    private static final float ENDURIAN_HEALTH = 100.0F;
    private static final float RED_STINGER_DEFENSIVE_HEALTH = 55.0F;
    private static final List<RecentPlantDeath> RECENT_PLANT_DEATHS = new ArrayList<>();

    private PlantEntityManager() {
    }

    public static boolean placePlant(ServerPlayer player, BlockHitResult target, PlantSeedDefinition definition) {
        if (target == null) {
            return false;
        }

        ServerLevel level = player.serverLevel();
        boolean graveBuster = definition.behavior() == PlantSeedDefinition.PlantBehavior.GRAVE_BUSTER;
        BlockPos graveTargetPos = target.getBlockPos();
        if (graveBuster && !isGraveTarget(level.getBlockState(graveTargetPos))) {
            return false;
        }

        BlockPos placePos = graveBuster ? graveTargetPos.above() : target.getBlockPos().relative(target.getDirection());
        if (definition.behavior() == PlantSeedDefinition.PlantBehavior.GOLD_LEAF) {
            return GoldTileManager.addGoldTileNear(level, target.getBlockPos());
        }

        if (definition.behavior() == PlantSeedDefinition.PlantBehavior.INTENSIVE_CARROT) {
            return reviveRecentPlant(player, target.getBlockPos());
        }

        if (definition.behavior() == PlantSeedDefinition.PlantBehavior.PEA_POD) {
            Optional<SnowGolem> existingPeaPod = findPlantAt(level, placePos, PlantSeedDefinition.PlantBehavior.PEA_POD)
                    .or(() -> findPlantAt(level, target.getBlockPos(), PlantSeedDefinition.PlantBehavior.PEA_POD));
            if (existingPeaPod.isPresent()) {
                return upgradePeaPod(player, existingPeaPod.get());
            }
        }

        if (!level.getBlockState(placePos).isAir() || level.getBlockState(placePos.below()).isAir()) {
            return false;
        }

        EntityType<? extends SnowGolem> plantType = ModEntities.PLANTS.containsKey(definition.plantId())
                ? ModEntities.PLANTS.get(definition.plantId()).get()
                : EntityType.SNOW_GOLEM;
        SnowGolem plant = plantType.create(level);
        if (plant == null) {
            return false;
        }

        plant.moveTo(placePos.getX() + 0.5D, placePos.getY(), placePos.getZ() + 0.5D, player.getYRot(), 0.0F);
        initializePlantEntity(plant, definition, level.getGameTime());
        if (definition.behavior() == PlantSeedDefinition.PlantBehavior.RED_STINGER) {
            updateRedStingerMode(level, plant);
        }
        if (graveBuster) {
            CompoundTag tag = plant.getPersistentData();
            tag.putInt(GRAVE_X_TAG, graveTargetPos.getX());
            tag.putInt(GRAVE_Y_TAG, graveTargetPos.getY());
            tag.putInt(GRAVE_Z_TAG, graveTargetPos.getZ());
            tag.putLong(NEXT_ACTION_TICK_TAG, level.getGameTime() + GRAVE_BUSTER_EAT_TICKS);
        }

        return level.addFreshEntity(plant);
    }

    public static boolean placePlantInTargetMinecart(ServerPlayer player, PlantSeedDefinition definition) {
        Optional<WildWestMinecartEntity> target = findTargetMinecart(player);
        if (target.isEmpty()) {
            return false;
        }

        return placePlantInMinecart(player, target.get(), definition);
    }

    public static void initializeSummonedPlant(SnowGolem plant) {
        if (isPlant(plant)) {
            return;
        }

        String plantId = BuiltInRegistries.ENTITY_TYPE.getKey(plant.getType()).getPath();
        PlantSeedDefinition.getByPlantId(plantId).ifPresent(definition -> initializePlantEntity(plant, definition, plant.level().getGameTime()));
    }

    public static void initializePlantEntity(SnowGolem plant, PlantSeedDefinition definition, long gameTime) {
        plant.setNoAi(true);
        plant.setSilent(true);
        plant.setPersistenceRequired();
        plant.setCustomName(Component.literal(definition.displayName()).withStyle(style -> style.withColor(TextColor.fromRgb(definition.gardenColor()))));
        plant.setCustomNameVisible(true);
        setPlantHealth(plant, maxHealthFor(definition.behavior()));

        CompoundTag tag = plant.getPersistentData();
        tag.putBoolean(PLANT_TAG, true);
        tag.putString(PLANT_ID_TAG, definition.plantId());
        tag.putString(PLANT_BEHAVIOR_TAG, definition.behavior().name());
        tag.putLong(PLANT_PLACED_TICK_TAG, gameTime);
        tag.putLong(NEXT_ACTION_TICK_TAG, gameTime + 20L);
        if (definition.behavior() == PlantSeedDefinition.PlantBehavior.PEA_POD) {
            tag.putInt(PEA_POD_STACK_TAG, 1);
        }
        if (definition.behavior() == PlantSeedDefinition.PlantBehavior.RED_STINGER) {
            tag.putString(RED_STINGER_MODE_TAG, "NORMAL");
        }
        if (definition.behavior() == PlantSeedDefinition.PlantBehavior.CELERY_STALKER) {
            tag.putBoolean(CELERY_ACTIVATED_TAG, false);
            plant.setInvisible(true);
        }
    }

    public static boolean attackNearbyPlant(ServerLevel level, Mob mob, float damage) {
        Optional<LivingEntity> plant = findPlantInAttackRange(level, mob);
        if (plant.isEmpty()) {
            return false;
        }

        LivingEntity target = plant.get();
        mob.getNavigation().stop();
        mob.getLookControl().setLookAt(target);
        if ((level.getGameTime() + mob.getId()) % 20 == 0) {
            mob.swing(InteractionHand.MAIN_HAND);
            target.hurt(level.damageSources().mobAttack(mob), damage);
            if (behaviorFor(target) == PlantSeedDefinition.PlantBehavior.ENDURIAN) {
                mob.hurt(level.damageSources().thorns(target), ENDURIAN_THORN_DAMAGE);
            }
            if (behaviorFor(target) == PlantSeedDefinition.PlantBehavior.GARLIC) {
                divertGarlicZombie(level, mob, target);
            }
        }
        return true;
    }

    public static boolean isPlant(Entity entity) {
        return entity != null && entity.getPersistentData().getBoolean(PLANT_TAG);
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        Set<UUID> tickedPlants = new HashSet<>();
        for (ServerPlayer player : event.getServer().getPlayerList().getPlayers()) {
            ServerLevel level = player.serverLevel();
            AABB scanArea = player.getBoundingBox().inflate(PLANT_SCAN_RADIUS);
            List<SnowGolem> plants = level.getEntitiesOfClass(SnowGolem.class, scanArea, PlantEntityManager::isPlant);
            for (SnowGolem plant : plants) {
                if (tickedPlants.add(plant.getUUID())) {
                    tickPlant(level, plant);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (isFriendlyPlantDamage(event.getEntity(), event.getSource())) {
            event.setCanceled(true);
            return;
        }

        if (isPlant(event.getEntity()) && event.getEntity().level() instanceof ServerLevel level
                && event.getEntity().getHealth() - event.getAmount() <= 0.0F) {
            recordPlantDeath(level, event.getEntity());
        }

        if (!(event.getEntity() instanceof Zombie zombie) || !(zombie.level() instanceof ServerLevel level)) {
            return;
        }

        CompoundTag tag = zombie.getPersistentData();
        if (!tag.getBoolean(SUN_BEAN_INFECTED_TAG)) {
            return;
        }

        long gameTime = level.getGameTime();
        if (gameTime > tag.getLong(SUN_BEAN_EXPIRES_TICK_TAG)) {
            tag.remove(SUN_BEAN_INFECTED_TAG);
            return;
        }

        if (gameTime < tag.getLong(SUN_BEAN_NEXT_SUN_TICK_TAG)) {
            return;
        }

        Player nearestPlayer = level.getNearestPlayer(zombie, 64.0D);
        if (nearestPlayer != null) {
            SunManager.addSun(nearestPlayer, SUN_BEAN_SUN_VALUE);
            level.sendParticles(ParticleTypes.HAPPY_VILLAGER, zombie.getX(), zombie.getY() + 1.0D, zombie.getZ(), 4, 0.25D, 0.25D, 0.25D, 0.02D);
            tag.putLong(SUN_BEAN_NEXT_SUN_TICK_TAG, gameTime + SUN_BEAN_SUN_COOLDOWN_TICKS);
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (!(event.getEntity().level() instanceof ServerLevel level)) {
            return;
        }

        if (isPlant(event.getEntity())) {
            recordPlantDeath(level, event.getEntity());
            return;
        }

        if (event.getEntity() instanceof Zombie zombie && zombie.getPersistentData().contains(SPORE_SHROOM_SOURCE_TAG)) {
            UUID sourceId = zombie.getPersistentData().getUUID(SPORE_SHROOM_SOURCE_TAG);
            Entity source = level.getEntity(sourceId);
            if (source instanceof SnowGolem sporeShroom && isPlant(sporeShroom)) {
                spawnSporeShroomClone(level, sporeShroom, zombie.blockPosition());
            }
        }
    }

    @SubscribeEvent
    public static void onProjectileImpact(ProjectileImpactEvent event) {
        Projectile projectile = event.getProjectile();
        if (!projectile.getPersistentData().getBoolean(PLANT_PROJECTILE_TAG)) {
            return;
        }

        if (event.getRayTraceResult() instanceof net.minecraft.world.phys.EntityHitResult hitResult && isPlant(hitResult.getEntity())) {
            event.setCanceled(true);
        }
    }

    private static void tickPlant(ServerLevel level, SnowGolem plant) {
        ensurePlantNameVisible(plant);
        lookAtNearestHostile(level, plant);
        PlantSeedDefinition.PlantBehavior behavior = behaviorFor(plant);
        switch (behavior) {
            case PEASHOOTER -> tickShooter(level, plant, 1);
            case REPEATER -> tickRepeater(level, plant);
            case SUNFLOWER -> tickSunflower(level, plant);
            case TWIN_SUNFLOWER -> tickTwinSunflower(level, plant);
            case POTATO_MINE -> tickPotatoMine(level, plant);
            case CHOMPER -> tickChomper(level, plant);
            case BLOOMERANG -> tickBloomerang(level, plant);
            case ICEBERG_LETTUCE -> tickIcebergLettuce(level, plant);
            case GRAVE_BUSTER -> tickGraveBuster(level, plant);
            case BONK_CHOY -> tickBonkChoy(level, plant);
            case SPLIT_PEA -> tickSplitPea(level, plant);
            case CHILI_BEAN -> tickChiliBean(level, plant);
            case PEA_POD -> tickPeaPod(level, plant);
            case LIGHTNING_REED -> tickLightningReed(level, plant);
            case MELON_PULT -> tickMelonPult(level, plant, false);
            case WINTER_MELON -> tickMelonPult(level, plant, true);
            case RED_STINGER -> tickRedStinger(level, plant);
            case AKEE -> tickAkee(level, plant);
            case ENDURIAN -> tickEndurian(level, plant);
            case STALLIA -> tickStallia(level, plant);
            case GOLD_LEAF -> tickGoldLeaf(level, plant);
            case SUN_SHROOM -> tickSunShroom(level, plant);
            case PUFF_SHROOM -> tickPuffShroom(level, plant);
            case FUME_SHROOM -> tickFumeShroom(level, plant);
            case SUN_BEAN -> tickSunBean(level, plant);
            case MAGNET_SHROOM -> tickMagnetShroom(level, plant);
            case PRIMAL_PEASHOOTER -> tickPrimalPeashooter(level, plant);
            case PERFUME_SHROOM -> tickPerfumeShroom(level, plant);
            case PRIMAL_SUNFLOWER -> tickPrimalSunflower(level, plant);
            case PRIMAL_POTATO_MINE -> tickPrimalPotatoMine(level, plant);
            case PHAT_BEET -> tickPhatBeet(level, plant);
            case CELERY_STALKER -> tickCeleryStalker(level, plant);
            case THYME_WARP -> tickThymeWarp(level, plant);
            case SPORE_SHROOM -> tickSporeShroom(level, plant);
            case INTENSIVE_CARROT -> tickIntensiveCarrot(level, plant);
            case WALL_NUT, PRIMAL_WALL_NUT, TALL_NUT, TORCHWOOD, GARLIC, PLACEHOLDER -> {
            }
        }
    }

    private static void tickShooter(ServerLevel level, SnowGolem plant, int shots) {
        long gameTime = level.getGameTime();
        CompoundTag tag = plant.getPersistentData();
        if (gameTime < tag.getLong(NEXT_ACTION_TICK_TAG)) {
            return;
        }

        Optional<LivingEntity> target = selectHostile(level, plant, SHOOTER_RANGE);
        if (target.isEmpty()) {
            tag.putLong(NEXT_ACTION_TICK_TAG, gameTime + 10L);
            return;
        }

        for (int shot = 0; shot < shots; shot++) {
            shootSnowball(level, plant, target.get(), shot * 0.18D);
        }
        tag.putLong(NEXT_ACTION_TICK_TAG, gameTime + SHOOTER_INTERVAL_TICKS);
    }

    private static void tickRepeater(ServerLevel level, SnowGolem plant) {
        long gameTime = level.getGameTime();
        CompoundTag tag = plant.getPersistentData();
        if (gameTime < tag.getLong(NEXT_ACTION_TICK_TAG)) {
            return;
        }

        if (tag.getBoolean(REPEATER_PENDING_SECOND_SHOT_TAG)) {
            LivingEntity secondTarget = null;
            if (tag.hasUUID(REPEATER_TARGET_UUID_TAG)) {
                Entity stored = level.getEntity(tag.getUUID(REPEATER_TARGET_UUID_TAG));
                if (stored instanceof LivingEntity living && isHostileTarget(living) && living.isAlive() && plant.distanceToSqr(living) <= SHOOTER_RANGE * SHOOTER_RANGE) {
                    secondTarget = living;
                }
            }
            if (secondTarget == null) {
                secondTarget = selectHostile(level, plant, SHOOTER_RANGE).orElse(null);
            }

            if (secondTarget != null) {
                shootSnowball(level, plant, secondTarget, 0.08D);
            }
            tag.putBoolean(REPEATER_PENDING_SECOND_SHOT_TAG, false);
            tag.remove(REPEATER_TARGET_UUID_TAG);
            tag.putLong(NEXT_ACTION_TICK_TAG, gameTime + SHOOTER_INTERVAL_TICKS);
            return;
        }

        Optional<LivingEntity> target = selectHostile(level, plant, SHOOTER_RANGE);
        if (target.isEmpty()) {
            tag.putLong(NEXT_ACTION_TICK_TAG, gameTime + 10L);
            return;
        }

        shootSnowball(level, plant, target.get(), -0.08D);
        tag.putBoolean(REPEATER_PENDING_SECOND_SHOT_TAG, true);
        tag.putUUID(REPEATER_TARGET_UUID_TAG, target.get().getUUID());
        tag.putLong(NEXT_ACTION_TICK_TAG, gameTime + REPEATER_SECOND_SHOT_DELAY_TICKS);
    }

    private static void tickSunflower(ServerLevel level, SnowGolem plant) {
        long gameTime = level.getGameTime();
        CompoundTag tag = plant.getPersistentData();
        if (gameTime < tag.getLong(NEXT_ACTION_TICK_TAG)) {
            return;
        }

        SunManager.spawnSunAt(level, plant.blockPosition().above(3));
        tag.putLong(NEXT_ACTION_TICK_TAG, gameTime + SUNFLOWER_INTERVAL_TICKS);
    }

    private static void tickTwinSunflower(ServerLevel level, SnowGolem plant) {
        long gameTime = level.getGameTime();
        CompoundTag tag = plant.getPersistentData();
        if (gameTime < tag.getLong(NEXT_ACTION_TICK_TAG)) {
            return;
        }

        SunManager.spawnSunAt(level, plant.blockPosition().above(3));
        SunManager.spawnSunAt(level, plant.blockPosition().above(3).east());
        tag.putLong(NEXT_ACTION_TICK_TAG, gameTime + SUNFLOWER_INTERVAL_TICKS);
    }

    private static void tickSunShroom(ServerLevel level, SnowGolem plant) {
        long gameTime = level.getGameTime();
        CompoundTag tag = plant.getPersistentData();
        updateSunShroomStage(plant, gameTime);
        if (gameTime < tag.getLong(NEXT_ACTION_TICK_TAG)) {
            return;
        }

        int stage = Math.max(1, tag.getInt(SUN_SHROOM_STAGE_TAG));
        int sunValue = switch (stage) {
            case 1 -> 15;
            case 2 -> 25;
            default -> 50;
        };
        SunManager.spawnSunAt(level, plant.blockPosition().above(3), sunValue);
        tag.putLong(NEXT_ACTION_TICK_TAG, gameTime + SUN_SHROOM_INTERVAL_TICKS);
    }

    private static void tickPuffShroom(ServerLevel level, SnowGolem plant) {
        long gameTime = level.getGameTime();
        CompoundTag tag = plant.getPersistentData();
        if (gameTime - tag.getLong(PLANT_PLACED_TICK_TAG) >= PUFF_SHROOM_LIFETIME_TICKS) {
            level.sendParticles(ParticleTypes.POOF, plant.getX(), plant.getY() + 0.8D, plant.getZ(), 8, 0.25D, 0.25D, 0.25D, 0.02D);
            plant.discard();
            return;
        }

        if (gameTime < tag.getLong(NEXT_ACTION_TICK_TAG)) {
            return;
        }

        Optional<Zombie> target = selectZombie(level, plant, PUFF_SHROOM_RANGE);
        if (target.isEmpty()) {
            tag.putLong(NEXT_ACTION_TICK_TAG, gameTime + 10L);
            return;
        }

        shootSnowballVisual(level, plant, target.get(), false, "spore");
        target.get().hurt(level.damageSources().mobAttack(plant), PUFF_SHROOM_DAMAGE);
        tag.putLong(NEXT_ACTION_TICK_TAG, gameTime + SHOOTER_INTERVAL_TICKS);
    }

    private static void tickFumeShroom(ServerLevel level, SnowGolem plant) {
        long gameTime = level.getGameTime();
        CompoundTag tag = plant.getPersistentData();
        if (gameTime < tag.getLong(NEXT_ACTION_TICK_TAG)) {
            return;
        }

        Vec3 facing = facingVector(plant);
        List<Zombie> targets = level.getEntitiesOfClass(Zombie.class, plant.getBoundingBox().inflate(FUME_SHROOM_RANGE, 2.0D, FUME_SHROOM_RANGE), Zombie::isAlive)
                .stream()
                .filter(zombie -> isInFrontCone(plant, zombie, facing, FUME_SHROOM_RANGE))
                .toList();
        if (targets.isEmpty()) {
            tag.putLong(NEXT_ACTION_TICK_TAG, gameTime + 10L);
            return;
        }

        for (Zombie zombie : targets) {
            zombie.hurt(level.damageSources().mobAttack(plant), FUME_SHROOM_DAMAGE);
        }
        Vec3 center = plant.position().add(facing.scale(2.5D)).add(0.0D, 0.8D, 0.0D);
        level.sendParticles(ParticleTypes.CLOUD, center.x, center.y, center.z, 32, 1.2D, 0.35D, 1.2D, 0.04D);
        tag.putLong(NEXT_ACTION_TICK_TAG, gameTime + FUME_SHROOM_INTERVAL_TICKS);
    }

    private static void tickSunBean(ServerLevel level, SnowGolem plant) {
        Optional<Zombie> trigger = selectZombie(level, plant, 1.35D);
        if (trigger.isEmpty()) {
            return;
        }

        Zombie zombie = trigger.get();
        long gameTime = level.getGameTime();
        CompoundTag tag = zombie.getPersistentData();
        tag.putBoolean(SUN_BEAN_INFECTED_TAG, true);
        tag.putLong(SUN_BEAN_EXPIRES_TICK_TAG, gameTime + SUN_BEAN_INFECTED_TICKS);
        tag.putLong(SUN_BEAN_NEXT_SUN_TICK_TAG, gameTime);
        zombie.addEffect(new MobEffectInstance(MobEffects.GLOWING, SUN_BEAN_INFECTED_TICKS, 0));
        level.sendParticles(ParticleTypes.HAPPY_VILLAGER, zombie.getX(), zombie.getY() + 1.0D, zombie.getZ(), 20, 0.5D, 0.45D, 0.5D, 0.03D);
        plant.discard();
    }

    private static void tickMagnetShroom(ServerLevel level, SnowGolem plant) {
        long gameTime = level.getGameTime();
        CompoundTag tag = plant.getPersistentData();
        if (gameTime < tag.getLong(NEXT_ACTION_TICK_TAG)) {
            return;
        }

        Optional<Zombie> target = level.getEntitiesOfClass(Zombie.class, plant.getBoundingBox().inflate(MAGNET_SHROOM_RANGE, 3.0D, MAGNET_SHROOM_RANGE), Zombie::isAlive)
                .stream()
                .filter(PlantEntityManager::hasMetalOrArmor)
                .min((first, second) -> Double.compare(plant.distanceToSqr(first), plant.distanceToSqr(second)));
        if (target.isEmpty()) {
            tag.putLong(NEXT_ACTION_TICK_TAG, gameTime + 20L);
            return;
        }

        if (stripOneMetalOrArmorItem(target.get())) {
            level.sendParticles(ParticleTypes.CRIT, target.get().getX(), target.get().getY() + 1.0D, target.get().getZ(), 24, 0.35D, 0.45D, 0.35D, 0.05D);
            level.sendParticles(ParticleTypes.ENCHANT, plant.getX(), plant.getY() + 1.0D, plant.getZ(), 16, 0.35D, 0.45D, 0.35D, 0.05D);
            tag.putLong(NEXT_ACTION_TICK_TAG, gameTime + MAGNET_SHROOM_COOLDOWN_TICKS);
        } else {
            tag.putLong(NEXT_ACTION_TICK_TAG, gameTime + 20L);
        }
    }

    private static void tickPrimalPeashooter(ServerLevel level, SnowGolem plant) {
        long gameTime = level.getGameTime();
        CompoundTag tag = plant.getPersistentData();
        if (gameTime < tag.getLong(NEXT_ACTION_TICK_TAG)) {
            return;
        }

        Optional<Zombie> target = selectZombie(level, plant, SHOOTER_RANGE);
        if (target.isEmpty()) {
            tag.putLong(NEXT_ACTION_TICK_TAG, gameTime + 10L);
            return;
        }

        Zombie zombie = target.get();
        shootSnowballVisual(level, plant, zombie, hasTorchwoodBetween(level, plant.position(), zombie.position()), "primal_pea");
        zombie.hurt(level.damageSources().mobAttack(plant), PRIMAL_PEA_DAMAGE);
        zombie.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 8));
        if (level.random.nextFloat() < 0.25F) {
            Vec3 knock = zombie.position().subtract(plant.position()).multiply(1.0D, 0.0D, 1.0D);
            if (knock.lengthSqr() > 1.0E-4D) {
                zombie.setDeltaMovement(zombie.getDeltaMovement().add(knock.normalize().scale(0.55D)).add(0.0D, 0.18D, 0.0D));
            }
        }
        tag.putLong(NEXT_ACTION_TICK_TAG, gameTime + SHOOTER_INTERVAL_TICKS);
    }

    private static void tickPerfumeShroom(ServerLevel level, SnowGolem plant) {
        Optional<JurassicDinosaurEntity> dinosaur = level.getEntitiesOfClass(JurassicDinosaurEntity.class, plant.getBoundingBox().inflate(PERFUME_SHROOM_RANGE), JurassicDinosaurEntity::isAlive)
                .stream()
                .filter(dino -> !dino.isCharmed())
                .min((first, second) -> Double.compare(plant.distanceToSqr(first), plant.distanceToSqr(second)));
        if (dinosaur.isEmpty()) {
            return;
        }

        Player owner = level.getNearestPlayer(plant, 64.0D);
        dinosaur.get().charmFor(owner == null ? plant.getUUID() : owner.getUUID(), 20 * 60);
        level.sendParticles(ParticleTypes.HEART, dinosaur.get().getX(), dinosaur.get().getY() + 1.3D, dinosaur.get().getZ(), 12, 0.5D, 0.45D, 0.5D, 0.02D);
        plant.discard();
    }

    private static void tickPrimalSunflower(ServerLevel level, SnowGolem plant) {
        long gameTime = level.getGameTime();
        CompoundTag tag = plant.getPersistentData();
        if (gameTime < tag.getLong(NEXT_ACTION_TICK_TAG)) {
            return;
        }

        SunManager.spawnSunAt(level, plant.blockPosition().above(3), 50);
        tag.putLong(NEXT_ACTION_TICK_TAG, gameTime + SUNFLOWER_INTERVAL_TICKS);
    }

    private static void tickPrimalPotatoMine(ServerLevel level, SnowGolem plant) {
        if (level.getGameTime() - plant.getPersistentData().getLong(PLANT_PLACED_TICK_TAG) < PRIMAL_POTATO_ARM_TICKS) {
            return;
        }

        List<Zombie> zombies = level.getEntitiesOfClass(Zombie.class, plant.getBoundingBox().inflate(1.75D, 0.75D, 1.75D), Zombie::isAlive);
        if (zombies.isEmpty()) {
            return;
        }

        AABB blastArea = new AABB(plant.blockPosition()).inflate(2.5D, 1.0D, 2.5D);
        for (Zombie zombie : level.getEntitiesOfClass(Zombie.class, blastArea, Zombie::isAlive)) {
            zombie.hurt(level.damageSources().generic(), PRIMAL_POTATO_MINE_DAMAGE);
        }
        level.sendParticles(ParticleTypes.EXPLOSION, plant.getX(), plant.getY() + 0.5D, plant.getZ(), 6, 1.2D, 0.4D, 1.2D, 0.0D);
        level.levelEvent(2001, plant.blockPosition(), 0);
        plant.discard();
    }

    private static void tickPhatBeet(ServerLevel level, SnowGolem plant) {
        long gameTime = level.getGameTime();
        CompoundTag tag = plant.getPersistentData();
        if (gameTime < tag.getLong(NEXT_ACTION_TICK_TAG)) {
            return;
        }

        List<Zombie> zombies = level.getEntitiesOfClass(Zombie.class, plant.getBoundingBox().inflate(PHAT_BEET_RADIUS, 1.5D, PHAT_BEET_RADIUS), Zombie::isAlive);
        if (zombies.isEmpty()) {
            tag.putLong(NEXT_ACTION_TICK_TAG, gameTime + 10L);
            return;
        }

        for (Zombie zombie : zombies) {
            zombie.hurt(level.damageSources().mobAttack(plant), PHAT_BEET_DAMAGE);
        }
        level.sendParticles(ParticleTypes.NOTE, plant.getX(), plant.getY() + 1.1D, plant.getZ(), 8, 0.8D, 0.2D, 0.8D, 0.0D);
        level.sendParticles(ParticleTypes.SONIC_BOOM, plant.getX(), plant.getY() + 0.3D, plant.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
        tag.putLong(NEXT_ACTION_TICK_TAG, gameTime + PHAT_BEET_INTERVAL_TICKS);
    }

    private static void tickCeleryStalker(ServerLevel level, SnowGolem plant) {
        CompoundTag tag = plant.getPersistentData();
        boolean activated = tag.getBoolean(CELERY_ACTIVATED_TAG);
        if (!activated) {
            Optional<Zombie> passedZombie = level.getEntitiesOfClass(Zombie.class, plant.getBoundingBox().inflate(1.6D, 1.0D, 1.6D), Zombie::isAlive)
                    .stream()
                    .filter(zombie -> isBehindPlant(plant, zombie))
                    .findFirst();
            if (passedZombie.isEmpty()) {
                return;
            }

            tag.putBoolean(CELERY_ACTIVATED_TAG, true);
            plant.setInvisible(false);
            level.sendParticles(ParticleTypes.POOF, plant.getX(), plant.getY() + 0.8D, plant.getZ(), 12, 0.3D, 0.3D, 0.3D, 0.02D);
        }

        long gameTime = level.getGameTime();
        if (gameTime < tag.getLong(NEXT_ACTION_TICK_TAG)) {
            return;
        }

        List<Zombie> targets = level.getEntitiesOfClass(Zombie.class, plant.getBoundingBox().inflate(1.6D, 0.75D, 1.6D), Zombie::isAlive);
        for (Zombie zombie : targets) {
            zombie.hurt(level.damageSources().mobAttack(plant), CELERY_STALKER_DAMAGE);
        }
        if (!targets.isEmpty()) {
            level.sendParticles(ParticleTypes.CRIT, plant.getX(), plant.getY() + 1.0D, plant.getZ(), 10, 0.5D, 0.3D, 0.5D, 0.04D);
        }
        tag.putLong(NEXT_ACTION_TICK_TAG, gameTime + CELERY_STALKER_INTERVAL_TICKS);
    }

    private static void tickThymeWarp(ServerLevel level, SnowGolem plant) {
        AABB area = plant.getBoundingBox().inflate(8.0D, 3.0D, 8.0D);
        List<Zombie> zombies = level.getEntitiesOfClass(Zombie.class, area, Zombie::isAlive);
        if (zombies.isEmpty()) {
            return;
        }

        for (Zombie zombie : zombies) {
            Vec3 away = zombie.position().subtract(plant.position()).multiply(1.0D, 0.0D, 1.0D);
            if (away.lengthSqr() < 1.0E-4D) {
                away = facingVector(plant).scale(-1.0D);
            }
            moveZombieSafely(level, zombie, away.normalize(), 7.0D);
            zombie.heal(4.0F);
        }
        level.sendParticles(ParticleTypes.PORTAL, plant.getX(), plant.getY() + 0.8D, plant.getZ(), 36, 1.5D, 0.6D, 1.5D, 0.05D);
        plant.discard();
    }

    private static void tickSporeShroom(ServerLevel level, SnowGolem plant) {
        long gameTime = level.getGameTime();
        CompoundTag tag = plant.getPersistentData();
        if (gameTime < tag.getLong(NEXT_ACTION_TICK_TAG)) {
            return;
        }

        Optional<Zombie> target = selectZombie(level, plant, SPORE_SHROOM_RANGE);
        if (target.isEmpty()) {
            tag.putLong(NEXT_ACTION_TICK_TAG, gameTime + 10L);
            return;
        }

        Zombie zombie = target.get();
        zombie.getPersistentData().putUUID(SPORE_SHROOM_SOURCE_TAG, plant.getUUID());
        shootSnowballVisual(level, plant, zombie, false, "spore_shroom");
        zombie.hurt(level.damageSources().mobAttack(plant), SPORE_SHROOM_DAMAGE);
        tag.putLong(NEXT_ACTION_TICK_TAG, gameTime + SHOOTER_INTERVAL_TICKS);
    }

    private static void tickIntensiveCarrot(ServerLevel level, SnowGolem plant) {
        if (reviveRecentPlantAt(level, plant.blockPosition().below())) {
            level.sendParticles(ParticleTypes.HAPPY_VILLAGER, plant.getX(), plant.getY() + 0.8D, plant.getZ(), 16, 0.4D, 0.4D, 0.4D, 0.02D);
            plant.discard();
        }
    }

    private static void tickPotatoMine(ServerLevel level, SnowGolem plant) {
        List<Zombie> zombies = level.getEntitiesOfClass(Zombie.class, plant.getBoundingBox().inflate(1.5D, 0.75D, 1.5D), Zombie::isAlive);
        if (zombies.isEmpty()) {
            return;
        }

        AABB blastArea = new AABB(plant.blockPosition()).inflate(1.5D, 1.0D, 1.5D);
        for (Zombie zombie : level.getEntitiesOfClass(Zombie.class, blastArea, Zombie::isAlive)) {
            zombie.hurt(level.damageSources().generic(), POTATO_MINE_DAMAGE);
        }
        level.levelEvent(2001, plant.blockPosition(), 0);
        plant.discard();
    }

    private static void tickChomper(ServerLevel level, SnowGolem plant) {
        long gameTime = level.getGameTime();
        CompoundTag tag = plant.getPersistentData();
        if (gameTime < tag.getLong(CHOMPER_COOLDOWN_TICK_TAG)) {
            return;
        }

        Optional<Zombie> target = selectZombie(level, plant, 1.5D);
        if (target.isEmpty()) {
            return;
        }

        target.get().hurt(level.damageSources().mobAttack(plant), CHOMPER_DAMAGE);
        tag.putLong(CHOMPER_COOLDOWN_TICK_TAG, gameTime + CHOMPER_COOLDOWN_TICKS);
    }

    private static void tickBloomerang(ServerLevel level, SnowGolem plant) {
        long gameTime = level.getGameTime();
        CompoundTag tag = plant.getPersistentData();
        if (gameTime < tag.getLong(NEXT_ACTION_TICK_TAG)) {
            return;
        }

        List<Zombie> targets = level.getEntitiesOfClass(Zombie.class, plant.getBoundingBox().inflate(SHOOTER_RANGE, 2.0D, SHOOTER_RANGE), Zombie::isAlive)
                .stream()
                .sorted((first, second) -> Double.compare(plant.distanceToSqr(first), plant.distanceToSqr(second)))
                .limit(3)
                .toList();
        if (targets.isEmpty()) {
            tag.putLong(NEXT_ACTION_TICK_TAG, gameTime + 10L);
            return;
        }

        boolean buffed = hasTorchwoodBetween(level, plant.position(), targets.get(0).position());
        float damage = buffed ? BLOOMERANG_DAMAGE * 2.0F : BLOOMERANG_DAMAGE;
        for (Zombie zombie : targets) {
            zombie.hurt(level.damageSources().mobAttack(plant), damage);
            zombie.hurt(level.damageSources().mobAttack(plant), damage);
        }
        shootSnowballVisual(level, plant, targets.get(0), buffed);
        tag.putLong(NEXT_ACTION_TICK_TAG, gameTime + 40L);
    }

    private static void tickIcebergLettuce(ServerLevel level, SnowGolem plant) {
        Optional<Zombie> target = selectZombie(level, plant, 1.5D);
        if (target.isEmpty()) {
            return;
        }

        target.get().addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20 * 8, 8));
        plant.discard();
    }

    private static void tickGraveBuster(ServerLevel level, SnowGolem plant) {
        CompoundTag tag = plant.getPersistentData();
        if (!tag.contains(GRAVE_X_TAG)) {
            plant.discard();
            return;
        }

        if (level.getGameTime() < tag.getLong(NEXT_ACTION_TICK_TAG)) {
            return;
        }

        BlockPos gravePos = new BlockPos(tag.getInt(GRAVE_X_TAG), tag.getInt(GRAVE_Y_TAG), tag.getInt(GRAVE_Z_TAG));
        if (isGraveTarget(level.getBlockState(gravePos))) {
            level.setBlock(gravePos, Blocks.AIR.defaultBlockState(), 3);
        }
        plant.discard();
    }

    private static void tickBonkChoy(ServerLevel level, SnowGolem plant) {
        long gameTime = level.getGameTime();
        CompoundTag tag = plant.getPersistentData();
        if (gameTime < tag.getLong(NEXT_ACTION_TICK_TAG)) {
            return;
        }

        Optional<Zombie> target = selectZombie(level, plant, 1.5D);
        target.ifPresent(zombie -> zombie.hurt(level.damageSources().mobAttack(plant), BONK_CHOY_DAMAGE));
        tag.putLong(NEXT_ACTION_TICK_TAG, gameTime + BONK_CHOY_INTERVAL_TICKS);
    }

    private static void tickSplitPea(ServerLevel level, SnowGolem plant) {
        long gameTime = level.getGameTime();
        CompoundTag tag = plant.getPersistentData();
        if (gameTime < tag.getLong(NEXT_ACTION_TICK_TAG)) {
            return;
        }

        Optional<Zombie> forwardTarget = selectDirectionalZombie(level, plant, SHOOTER_RANGE, true);
        Optional<Zombie> backwardTarget = selectDirectionalZombie(level, plant, SHOOTER_RANGE, false);
        if (forwardTarget.isEmpty() && backwardTarget.isEmpty()) {
            tag.putLong(NEXT_ACTION_TICK_TAG, gameTime + 10L);
            return;
        }

        forwardTarget.ifPresent(zombie -> shootSnowball(level, plant, zombie, -0.08D));
        backwardTarget.ifPresent(zombie -> shootSnowball(level, plant, zombie, 0.08D));
        tag.putLong(NEXT_ACTION_TICK_TAG, gameTime + SHOOTER_INTERVAL_TICKS);
    }

    private static void tickChiliBean(ServerLevel level, SnowGolem plant) {
        Optional<Zombie> trigger = selectZombie(level, plant, 1.35D);
        if (trigger.isEmpty()) {
            return;
        }

        Zombie triggeringZombie = trigger.get();
        triggeringZombie.hurt(level.damageSources().mobAttack(plant), CHILI_BEAN_DAMAGE);
        AABB gasArea = triggeringZombie.getBoundingBox().inflate(3.0D, 1.0D, 3.0D);
        for (Zombie zombie : level.getEntitiesOfClass(Zombie.class, gasArea, Zombie::isAlive)) {
            zombie.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20 * 5, 6));
        }
        level.sendParticles(ParticleTypes.CLOUD, triggeringZombie.getX(), triggeringZombie.getY() + 0.6D, triggeringZombie.getZ(), 24, 1.2D, 0.4D, 1.2D, 0.03D);
        plant.discard();
    }

    private static void tickPeaPod(ServerLevel level, SnowGolem plant) {
        int stackLevel = Math.max(1, Math.min(5, plant.getPersistentData().getInt(PEA_POD_STACK_TAG)));
        tickShooter(level, plant, stackLevel);
    }

    private static void tickLightningReed(ServerLevel level, SnowGolem plant) {
        long gameTime = level.getGameTime();
        CompoundTag tag = plant.getPersistentData();
        if (gameTime < tag.getLong(NEXT_ACTION_TICK_TAG)) {
            return;
        }

        Optional<Zombie> target = selectZombie(level, plant, 12.0D);
        if (target.isEmpty()) {
            tag.putLong(NEXT_ACTION_TICK_TAG, gameTime + 10L);
            return;
        }

        Zombie primary = target.get();
        primary.hurt(level.damageSources().mobAttack(plant), LIGHTNING_REED_DAMAGE);
        level.sendParticles(ParticleTypes.ELECTRIC_SPARK, primary.getX(), primary.getY() + 1.0D, primary.getZ(), 16, 0.35D, 0.5D, 0.35D, 0.02D);

        List<Zombie> chainedTargets = level.getEntitiesOfClass(Zombie.class, primary.getBoundingBox().inflate(4.0D), Zombie::isAlive)
                .stream()
                .filter(zombie -> zombie != primary)
                .sorted((first, second) -> Double.compare(primary.distanceToSqr(first), primary.distanceToSqr(second)))
                .limit(2)
                .toList();
        float chainDamage = LIGHTNING_REED_DAMAGE * 0.7F;
        for (Zombie zombie : chainedTargets) {
            zombie.hurt(level.damageSources().mobAttack(plant), chainDamage);
            level.sendParticles(ParticleTypes.ELECTRIC_SPARK, zombie.getX(), zombie.getY() + 1.0D, zombie.getZ(), 10, 0.3D, 0.45D, 0.3D, 0.02D);
            chainDamage *= 0.7F;
        }
        tag.putLong(NEXT_ACTION_TICK_TAG, gameTime + LIGHTNING_REED_INTERVAL_TICKS);
    }

    private static void tickMelonPult(ServerLevel level, SnowGolem plant, boolean winter) {
        long gameTime = level.getGameTime();
        CompoundTag tag = plant.getPersistentData();
        if (gameTime < tag.getLong(NEXT_ACTION_TICK_TAG)) {
            return;
        }

        Optional<Zombie> target = selectZombie(level, plant, SHOOTER_RANGE);
        if (target.isEmpty()) {
            tag.putLong(NEXT_ACTION_TICK_TAG, gameTime + 10L);
            return;
        }

        Zombie directTarget = target.get();
        float directDamage = winter ? WINTER_MELON_DIRECT_DAMAGE : MELON_DIRECT_DAMAGE;
        float splashDamage = winter ? WINTER_MELON_SPLASH_DAMAGE : MELON_SPLASH_DAMAGE;
        directTarget.hurt(level.damageSources().mobAttack(plant), directDamage);
        for (Zombie zombie : level.getEntitiesOfClass(Zombie.class, directTarget.getBoundingBox().inflate(2.25D, 1.0D, 2.25D), Zombie::isAlive)) {
            if (zombie != directTarget) {
                zombie.hurt(level.damageSources().mobAttack(plant), splashDamage);
            }
            if (winter) {
                zombie.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20 * 6, 2));
            }
        }
        shootSnowballVisual(level, plant, directTarget, false, winter ? "winter_melon" : "melon");
        level.levelEvent(2001, directTarget.blockPosition(), 0);
        tag.putLong(NEXT_ACTION_TICK_TAG, gameTime + MELON_PULT_INTERVAL_TICKS);
    }

    private static void tickRedStinger(ServerLevel level, SnowGolem plant) {
        long gameTime = level.getGameTime();
        CompoundTag tag = plant.getPersistentData();
        if (gameTime < tag.getLong(NEXT_ACTION_TICK_TAG)) {
            return;
        }

        updateRedStingerMode(level, plant);
        String mode = tag.getString(RED_STINGER_MODE_TAG);
        if ("DEFENSIVE".equals(mode)) {
            tag.putLong(NEXT_ACTION_TICK_TAG, gameTime + SHOOTER_INTERVAL_TICKS);
            return;
        }

        Optional<Zombie> target = selectZombie(level, plant, SHOOTER_RANGE);
        if (target.isEmpty()) {
            tag.putLong(NEXT_ACTION_TICK_TAG, gameTime + 10L);
            return;
        }

        float damage = "STRONG".equals(mode) ? RED_STINGER_STRONG_DAMAGE : RED_STINGER_NORMAL_DAMAGE;
        shootSnowballVisual(level, plant, target.get(), hasTorchwoodBetween(level, plant.position(), target.get().position()), "red_stinger");
        target.get().hurt(level.damageSources().mobAttack(plant), damage);
        tag.putLong(NEXT_ACTION_TICK_TAG, gameTime + SHOOTER_INTERVAL_TICKS);
    }

    private static void tickAkee(ServerLevel level, SnowGolem plant) {
        long gameTime = level.getGameTime();
        CompoundTag tag = plant.getPersistentData();
        if (gameTime < tag.getLong(NEXT_ACTION_TICK_TAG)) {
            return;
        }

        Optional<Zombie> firstTarget = selectZombie(level, plant, SHOOTER_RANGE);
        if (firstTarget.isEmpty()) {
            tag.putLong(NEXT_ACTION_TICK_TAG, gameTime + 10L);
            return;
        }

        Set<UUID> hitTargets = new HashSet<>();
        Zombie current = firstTarget.get();
        for (int bounce = 0; bounce < 4 && current != null; bounce++) {
            current.hurt(level.damageSources().mobAttack(plant), AKEE_DAMAGE);
            shootSnowballVisual(level, plant, current, false, "akee_seed");
            hitTargets.add(current.getUUID());
            Zombie previous = current;
            current = level.getEntitiesOfClass(Zombie.class, previous.getBoundingBox().inflate(5.0D), Zombie::isAlive)
                    .stream()
                    .filter(zombie -> !hitTargets.contains(zombie.getUUID()))
                    .min((first, second) -> Double.compare(previous.distanceToSqr(first), previous.distanceToSqr(second)))
                    .orElse(null);
        }
        tag.putLong(NEXT_ACTION_TICK_TAG, gameTime + SHOOTER_INTERVAL_TICKS);
    }

    private static void tickEndurian(ServerLevel level, SnowGolem plant) {
        long gameTime = level.getGameTime();
        CompoundTag tag = plant.getPersistentData();
        if (gameTime < tag.getLong(NEXT_ACTION_TICK_TAG)) {
            return;
        }

        for (Zombie zombie : level.getEntitiesOfClass(Zombie.class, plant.getBoundingBox().inflate(0.8D, 0.5D, 0.8D), Zombie::isAlive)) {
            zombie.hurt(level.damageSources().thorns(plant), ENDURIAN_THORN_DAMAGE);
        }
        tag.putLong(NEXT_ACTION_TICK_TAG, gameTime + 20L);
    }

    private static void tickStallia(ServerLevel level, SnowGolem plant) {
        List<Zombie> zombies = level.getEntitiesOfClass(Zombie.class, plant.getBoundingBox().inflate(3.0D, 1.0D, 3.0D), Zombie::isAlive);
        if (zombies.isEmpty()) {
            return;
        }

        for (Zombie zombie : zombies) {
            zombie.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20 * 8, 5));
        }
        level.sendParticles(ParticleTypes.SPORE_BLOSSOM_AIR, plant.getX(), plant.getY() + 0.6D, plant.getZ(), 28, 1.5D, 0.5D, 1.5D, 0.02D);
        plant.discard();
    }

    private static void tickGoldLeaf(ServerLevel level, SnowGolem plant) {
        if (GoldTileManager.addGoldTileNear(level, plant.blockPosition().below())) {
            plant.discard();
        }
    }

    private static Optional<LivingEntity> findPlantInAttackRange(ServerLevel level, Mob mob) {
        AABB attackArea = mob.getBoundingBox().inflate(0.65D, 0.5D, 0.65D);
        return level.getEntitiesOfClass(LivingEntity.class, attackArea, entity -> entity.isAlive() && isPlant(entity))
                .stream()
                .min((first, second) -> Double.compare(mob.distanceToSqr(first), mob.distanceToSqr(second)));
    }

    private static Optional<Zombie> selectZombie(ServerLevel level, SnowGolem plant, double range) {
        AABB area = plant.getBoundingBox().inflate(range, 3.0D, range);
        List<Zombie> zombies = level.getEntitiesOfClass(Zombie.class, area, Zombie::isAlive);
        return TargetingPriorityManager.selectTarget(zombies, plant, priorityFor(level, plant));
    }

    private static void ensurePlantNameVisible(SnowGolem plant) {
        plant.setCustomNameVisible(true);
        if (plant.getCustomName() != null) {
            return;
        }

        String plantId = plant.getPersistentData().getString(PLANT_ID_TAG);
        PlantSeedDefinition.getByPlantId(plantId).ifPresent(definition ->
                plant.setCustomName(Component.literal(definition.displayName()).withStyle(style -> style.withColor(TextColor.fromRgb(definition.gardenColor())))));
    }

    private static Optional<LivingEntity> selectHostile(ServerLevel level, SnowGolem plant, double range) {
        AABB area = plant.getBoundingBox().inflate(range, 3.0D, range);
        List<LivingEntity> hostiles = level.getEntitiesOfClass(LivingEntity.class, area, entity -> entity.isAlive() && isHostileTarget(entity));
        return hostiles.stream()
                .min((first, second) -> Double.compare(plant.distanceToSqr(first), plant.distanceToSqr(second)));
    }

    private static boolean isHostileTarget(LivingEntity entity) {
        return entity instanceof Monster && !isPlant(entity);
    }

    private static void lookAtNearestHostile(ServerLevel level, SnowGolem plant) {
        Optional<LivingEntity> target = selectHostile(level, plant, SHOOTER_RANGE);
        if (target.isEmpty()) {
            return;
        }

        Vec3 toTarget = target.get().position().subtract(plant.position());
        float yaw = (float) (Mth.atan2(toTarget.z, toTarget.x) * Mth.RAD_TO_DEG) - 90.0F;
        double horizontal = Math.sqrt(toTarget.x * toTarget.x + toTarget.z * toTarget.z);
        float pitch = (float) (-(Mth.atan2(toTarget.y + target.get().getBbHeight() * 0.5D - 1.0D, horizontal) * Mth.RAD_TO_DEG));
        plant.setYRot(yaw);
        plant.setYHeadRot(yaw);
        plant.yBodyRot = yaw;
        plant.setXRot(pitch);
    }

    private static boolean placePlantInMinecart(ServerPlayer player, WildWestMinecartEntity cart, PlantSeedDefinition definition) {
        Optional<SnowGolem> existingPlant = cart.getPassengers()
                .stream()
                .filter(entity -> entity instanceof SnowGolem && isPlant(entity))
                .map(entity -> (SnowGolem) entity)
                .findFirst();

        if (existingPlant.isPresent()) {
            if (definition.behavior() == PlantSeedDefinition.PlantBehavior.PEA_POD
                    && behaviorFor(existingPlant.get()) == PlantSeedDefinition.PlantBehavior.PEA_POD) {
                return upgradePeaPod(player, existingPlant.get());
            }
            player.displayClientMessage(Component.literal("Minecart already has a plant.").withStyle(ChatFormatting.RED), true);
            return false;
        }

        EntityType<? extends SnowGolem> plantType = ModEntities.PLANTS.containsKey(definition.plantId())
                ? ModEntities.PLANTS.get(definition.plantId()).get()
                : EntityType.SNOW_GOLEM;
        SnowGolem plant = plantType.create(player.serverLevel());
        if (plant == null) {
            return false;
        }

        Vec3 plantPos = cart.plantPosition();
        plant.moveTo(plantPos.x, plantPos.y, plantPos.z, player.getYRot(), 0.0F);
        initializePlantEntity(plant, definition, player.serverLevel().getGameTime());
        if (!player.serverLevel().addFreshEntity(plant)) {
            return false;
        }
        plant.startRiding(cart, true);
        return true;
    }

    private static Optional<WildWestMinecartEntity> findTargetMinecart(ServerPlayer player) {
        double reach = player.getBlockReach();
        Vec3 eye = player.getEyePosition();
        Vec3 look = player.getViewVector(1.0F);
        Vec3 end = eye.add(look.scale(reach));
        AABB searchArea = new AABB(eye, end).inflate(1.0D);

        WildWestMinecartEntity nearest = null;
        double nearestDistance = Double.MAX_VALUE;
        for (WildWestMinecartEntity cart : player.serverLevel().getEntitiesOfClass(WildWestMinecartEntity.class, searchArea, Entity::isAlive)) {
            if (cart.getBoundingBox().inflate(0.35D).clip(eye, end).isEmpty()) {
                continue;
            }
            double distance = eye.distanceToSqr(cart.position());
            if (distance < nearestDistance) {
                nearest = cart;
                nearestDistance = distance;
            }
        }

        return Optional.ofNullable(nearest);
    }

    private static Optional<Zombie> selectDirectionalZombie(ServerLevel level, SnowGolem plant, double range, boolean forward) {
        Vec3 facing = facingVector(plant);
        AABB area = plant.getBoundingBox().inflate(range, 3.0D, range);
        List<Zombie> zombies = level.getEntitiesOfClass(Zombie.class, area, Zombie::isAlive)
                .stream()
                .filter(zombie -> {
                    Vec3 toZombie = zombie.position().subtract(plant.position()).multiply(1.0D, 0.0D, 1.0D);
                    if (toZombie.lengthSqr() < 1.0E-4D) {
                        return true;
                    }
                    double dot = toZombie.normalize().dot(facing);
                    return forward ? dot >= 0.05D : dot <= -0.05D;
                })
                .toList();
        return TargetingPriorityManager.selectTarget(zombies, plant, priorityFor(level, plant));
    }

    private static void updateSunShroomStage(SnowGolem plant, long gameTime) {
        CompoundTag tag = plant.getPersistentData();
        long age = gameTime - tag.getLong(PLANT_PLACED_TICK_TAG);
        int stage = age >= SUN_SHROOM_STAGE_THREE_TICKS ? 3 : age >= SUN_SHROOM_STAGE_TWO_TICKS ? 2 : 1;
        if (tag.getInt(SUN_SHROOM_STAGE_TAG) == stage) {
            return;
        }

        tag.putInt(SUN_SHROOM_STAGE_TAG, stage);
        plant.setCustomName(Component.literal("Sun-shroom " + stage).withStyle(style -> style.withColor(TextColor.fromRgb(
                PlantSeedDefinition.getByPlantId("sun_shroom").map(PlantSeedDefinition::gardenColor).orElse(0x59407A)
        ))));
    }

    private static boolean isBehindPlant(SnowGolem plant, Zombie zombie) {
        Vec3 toZombie = zombie.position().subtract(plant.position()).multiply(1.0D, 0.0D, 1.0D);
        if (toZombie.lengthSqr() < 1.0E-4D) {
            return false;
        }
        return toZombie.normalize().dot(facingVector(plant)) <= -0.1D;
    }

    private static void moveZombieSafely(ServerLevel level, Zombie zombie, Vec3 direction, double distance) {
        Vec3 safeDirection = direction.lengthSqr() < 1.0E-4D ? new Vec3(0.0D, 0.0D, 1.0D) : direction.normalize();
        BlockPos raw = BlockPos.containing(zombie.position().add(safeDirection.scale(distance)));
        BlockPos surface = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, raw);
        if (level.getBlockState(surface).isAir() && level.getBlockState(surface.above()).isAir()) {
            zombie.teleportTo(surface.getX() + 0.5D, surface.getY(), surface.getZ() + 0.5D);
        }
    }

    private static void divertGarlicZombie(ServerLevel level, Mob mob, LivingEntity garlic) {
        Vec3 toMob = mob.position().subtract(garlic.position()).multiply(1.0D, 0.0D, 1.0D);
        Vec3 side = toMob.lengthSqr() < 1.0E-4D ? new Vec3(1.0D, 0.0D, 0.0D) : new Vec3(-toMob.z, 0.0D, toMob.x).normalize();
        if (level.random.nextBoolean()) {
            side = side.scale(-1.0D);
        }
        mob.setDeltaMovement(mob.getDeltaMovement().add(side.scale(0.65D)).add(0.0D, 0.12D, 0.0D));
        mob.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 20 * 2, 0));
        mob.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20 * 2, 1));
        mob.getNavigation().stop();
        level.sendParticles(ParticleTypes.SNEEZE, mob.getX(), mob.getY() + 1.0D, mob.getZ(), 6, 0.35D, 0.25D, 0.35D, 0.02D);
    }

    private static boolean reviveRecentPlant(ServerPlayer player, BlockPos targetPos) {
        cleanupRecentPlantDeaths(player.serverLevel());
        if (!reviveRecentPlantAt(player.serverLevel(), targetPos) && !reviveRecentPlantAt(player.serverLevel(), targetPos.above())) {
            player.displayClientMessage(Component.literal("No plant to revive.").withStyle(ChatFormatting.RED), true);
            return false;
        }
        return true;
    }

    private static boolean reviveRecentPlantAt(ServerLevel level, BlockPos targetPos) {
        cleanupRecentPlantDeaths(level);
        Optional<RecentPlantDeath> death = RECENT_PLANT_DEATHS.stream()
                .filter(entry -> entry.dimension().equals(level.dimension().location().toString()))
                .filter(entry -> entry.position().distSqr(targetPos) <= 2.0D)
                .findFirst();
        if (death.isEmpty()) {
            return false;
        }

        Optional<PlantSeedDefinition> definition = PlantSeedDefinition.getByPlantId(death.get().plantId());
        if (definition.isEmpty() || !canSpawnPlantAt(level, death.get().position())) {
            return false;
        }

        EntityType<? extends SnowGolem> plantType = ModEntities.PLANTS.containsKey(definition.get().plantId())
                ? ModEntities.PLANTS.get(definition.get().plantId()).get()
                : EntityType.SNOW_GOLEM;
        SnowGolem plant = plantType.create(level);
        if (plant == null) {
            return false;
        }

        BlockPos pos = death.get().position();
        plant.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 0.0F, 0.0F);
        initializePlantEntity(plant, definition.get(), level.getGameTime());
        if (level.addFreshEntity(plant)) {
            RECENT_PLANT_DEATHS.remove(death.get());
            return true;
        }
        return false;
    }

    private static void recordPlantDeath(ServerLevel level, Entity plant) {
        String plantId = plant.getPersistentData().getString(PLANT_ID_TAG);
        if (plantId.isEmpty()) {
            return;
        }

        RECENT_PLANT_DEATHS.add(new RecentPlantDeath(level.dimension().location().toString(), plantId, plant.blockPosition(), level.getGameTime()));
        cleanupRecentPlantDeaths(level);
        while (RECENT_PLANT_DEATHS.size() > 128) {
            RECENT_PLANT_DEATHS.remove(0);
        }
    }

    private static void cleanupRecentPlantDeaths(ServerLevel level) {
        long oldestAllowed = level.getGameTime() - RECENT_PLANT_DEATH_WINDOW_TICKS;
        RECENT_PLANT_DEATHS.removeIf(entry -> entry.gameTime() < oldestAllowed);
    }

    private static void spawnSporeShroomClone(ServerLevel level, SnowGolem source, BlockPos rawPos) {
        PlantSeedDefinition definition = PlantSeedDefinition.getByPlantId("spore_shroom").orElse(null);
        if (definition == null) {
            return;
        }

        int nearbySpores = level.getEntitiesOfClass(SnowGolem.class, source.getBoundingBox().inflate(32.0D), plant -> isPlant(plant) && behaviorFor(plant) == PlantSeedDefinition.PlantBehavior.SPORE_SHROOM).size();
        if (nearbySpores >= MAX_SPORE_SHROOM_CLONES_NEARBY) {
            return;
        }

        BlockPos surface = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, rawPos);
        if (!canSpawnPlantAt(level, surface)) {
            return;
        }

        EntityType<? extends SnowGolem> plantType = ModEntities.PLANTS.containsKey("spore_shroom")
                ? ModEntities.PLANTS.get("spore_shroom").get()
                : EntityType.SNOW_GOLEM;
        SnowGolem clone = plantType.create(level);
        if (clone == null) {
            return;
        }

        clone.moveTo(surface.getX() + 0.5D, surface.getY(), surface.getZ() + 0.5D, source.getYRot(), 0.0F);
        initializePlantEntity(clone, definition, level.getGameTime());
        level.addFreshEntity(clone);
    }

    private static boolean canSpawnPlantAt(ServerLevel level, BlockPos pos) {
        return level.getBlockState(pos).isAir()
                && level.getBlockState(pos.above()).isAir()
                && !level.getBlockState(pos.below()).isAir();
    }

    private static boolean isInFrontCone(SnowGolem plant, Zombie zombie, Vec3 facing, double range) {
        Vec3 toZombie = zombie.position().subtract(plant.position()).multiply(1.0D, 0.0D, 1.0D);
        double distanceSqr = toZombie.lengthSqr();
        if (distanceSqr < 1.0E-4D || distanceSqr > range * range) {
            return false;
        }

        double dot = toZombie.normalize().dot(facing);
        return dot >= 0.35D;
    }

    private static boolean hasMetalOrArmor(Zombie zombie) {
        if (zombie.getPersistentData().getBoolean(METAL_ZOMBIE_TAG)) {
            return true;
        }

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (!zombie.getItemBySlot(slot).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private static boolean stripOneMetalOrArmorItem(Zombie zombie) {
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack equipped = zombie.getItemBySlot(slot);
            if (!equipped.isEmpty()) {
                zombie.setItemSlot(slot, ItemStack.EMPTY);
                zombie.getPersistentData().putBoolean(ARMOR_STRIPPED_TAG, true);
                return true;
            }
        }

        if (zombie.getPersistentData().getBoolean(METAL_ZOMBIE_TAG)) {
            zombie.getPersistentData().putBoolean(METAL_ZOMBIE_TAG, false);
            zombie.getPersistentData().putBoolean(ARMOR_STRIPPED_TAG, true);
            return true;
        }
        return false;
    }

    private static boolean isFriendlyPlantDamage(LivingEntity target, DamageSource source) {
        if (!isPlant(target)) {
            return false;
        }

        Entity direct = source.getDirectEntity();
        Entity attacker = source.getEntity();
        if (direct != null && direct.getPersistentData().getBoolean(PLANT_PROJECTILE_TAG)) {
            return true;
        }
        return isPlant(attacker);
    }

    private static void shootSnowball(ServerLevel level, SnowGolem plant, LivingEntity target, double sideOffset) {
        boolean buffed = hasTorchwoodBetween(level, plant.position(), target.position());
        Snowball snowball = new Snowball(level, plant);
        Vec3 start = plant.position().add(0.0D, 1.25D, 0.0D);
        Vec3 targetPos = target.position().add(0.0D, target.getBbHeight() * 0.55D, 0.0D);
        Vec3 direction = targetPos.subtract(start).normalize();
        Vec3 side = new Vec3(-direction.z, 0.0D, direction.x).normalize().scale(sideOffset);
        snowball.setPos(start.x + side.x, start.y, start.z + side.z);
        snowball.shoot(direction.x, direction.y + 0.05D, direction.z, 1.35F, 0.0F);
        if (buffed) {
            snowball.getPersistentData().putBoolean(TORCHWOOD_BUFFED_TAG, true);
            snowball.setSecondsOnFire(2);
        }
        snowball.getPersistentData().putBoolean(PLANT_PROJECTILE_TAG, true);
        snowball.getPersistentData().putString(PROJECTILE_KIND_TAG, "pea");
        level.addFreshEntity(snowball);

        DamageSource source = level.damageSources().mobProjectile(snowball, plant);
        target.hurt(source, buffed ? PEA_DAMAGE * 2.0F : PEA_DAMAGE);
    }

    private static void shootSnowballVisual(ServerLevel level, SnowGolem plant, LivingEntity target, boolean buffed) {
        shootSnowballVisual(level, plant, target, buffed, "snowball");
    }

    private static void shootSnowballVisual(ServerLevel level, SnowGolem plant, LivingEntity target, boolean buffed, String projectileKind) {
        Snowball snowball = new Snowball(level, plant);
        Vec3 start = plant.position().add(0.0D, 1.25D, 0.0D);
        Vec3 targetPos = target.position().add(0.0D, target.getBbHeight() * 0.55D, 0.0D);
        Vec3 direction = targetPos.subtract(start).normalize();
        snowball.setPos(start.x, start.y, start.z);
        snowball.shoot(direction.x, direction.y + 0.18D, direction.z, 1.1F, 0.0F);
        snowball.getPersistentData().putBoolean(PLANT_PROJECTILE_TAG, true);
        snowball.getPersistentData().putString(PROJECTILE_KIND_TAG, projectileKind);
        if (buffed) {
            snowball.getPersistentData().putBoolean(TORCHWOOD_BUFFED_TAG, true);
            snowball.setSecondsOnFire(2);
        }
        level.addFreshEntity(snowball);
    }

    private static boolean hasTorchwoodBetween(ServerLevel level, Vec3 start, Vec3 end) {
        AABB area = new AABB(start, end).inflate(0.75D);
        for (SnowGolem plant : level.getEntitiesOfClass(SnowGolem.class, area, PlantEntityManager::isPlant)) {
            if (behaviorFor(plant) == PlantSeedDefinition.PlantBehavior.TORCHWOOD) {
                return true;
            }
        }
        return false;
    }

    private static TargetingPriority priorityFor(ServerLevel level, SnowGolem plant) {
        Player nearestPlayer = level.getNearestPlayer(plant, 64.0D);
        return nearestPlayer == null ? TargetingPriority.FIRST : TargetingPriorityManager.getPriority(nearestPlayer);
    }

    private static boolean isGraveTarget(BlockState state) {
        return state.is(Blocks.SKELETON_SKULL)
                || state.is(Blocks.WITHER_SKELETON_SKULL)
                || state.is(Blocks.SOUL_SAND)
                || state.is(Blocks.SOUL_SOIL);
    }

    private static Optional<SnowGolem> findPlantAt(ServerLevel level, BlockPos pos, PlantSeedDefinition.PlantBehavior behavior) {
        AABB area = new AABB(pos).inflate(0.35D, 0.75D, 0.35D);
        return level.getEntitiesOfClass(SnowGolem.class, area, plant -> isPlant(plant) && behaviorFor(plant) == behavior)
                .stream()
                .findFirst();
    }

    private static boolean upgradePeaPod(ServerPlayer player, SnowGolem peaPod) {
        CompoundTag tag = peaPod.getPersistentData();
        int stackLevel = Math.max(1, tag.getInt(PEA_POD_STACK_TAG));
        if (stackLevel >= 5) {
            player.displayClientMessage(Component.literal("Pea Pod is already fully stacked.").withStyle(ChatFormatting.RED), true);
            return false;
        }

        int nextLevel = stackLevel + 1;
        tag.putInt(PEA_POD_STACK_TAG, nextLevel);
        peaPod.setCustomName(Component.literal("Pea Pod x" + nextLevel).withStyle(style -> style.withColor(TextColor.fromRgb(
                PlantSeedDefinition.getByPlantId("pea_pod").map(PlantSeedDefinition::gardenColor).orElse(0xD87925)
        ))));
        return true;
    }

    private static void updateRedStingerMode(ServerLevel level, SnowGolem plant) {
        double distanceToTotem = distanceToNearestTotem(level, plant.blockPosition());
        String mode = distanceToTotem <= 3.5D ? "STRONG" : distanceToTotem <= 5.5D ? "NORMAL" : "DEFENSIVE";
        CompoundTag tag = plant.getPersistentData();
        if (mode.equals(tag.getString(RED_STINGER_MODE_TAG))) {
            return;
        }

        tag.putString(RED_STINGER_MODE_TAG, mode);
        if ("DEFENSIVE".equals(mode)) {
            setPlantHealth(plant, RED_STINGER_DEFENSIVE_HEALTH);
        } else {
            setPlantHealth(plant, DEFAULT_PLANT_HEALTH);
        }
        plant.setCustomName(Component.literal("Red Stinger (" + mode + ")").withStyle(style -> style.withColor(TextColor.fromRgb(
                PlantSeedDefinition.getByPlantId("red_stinger").map(PlantSeedDefinition::gardenColor).orElse(0x237C2F)
        ))));
    }

    private static double distanceToNearestTotem(ServerLevel level, BlockPos pos) {
        double nearest = Double.MAX_VALUE;
        for (int x = -7; x <= 7; x++) {
            for (int z = -7; z <= 7; z++) {
                BlockPos candidate = pos.offset(x, 0, z);
                if (level.getBlockState(candidate).is(ModBlocks.GARDEN_TOTEM.get())) {
                    nearest = Math.min(nearest, Math.sqrt(candidate.distSqr(pos)));
                }
            }
        }
        return nearest == Double.MAX_VALUE ? 4.5D : nearest;
    }

    private static Vec3 facingVector(SnowGolem plant) {
        float yaw = plant.getYRot() * Mth.DEG_TO_RAD;
        return new Vec3(-Mth.sin(yaw), 0.0D, Mth.cos(yaw)).normalize();
    }

    private static float maxHealthFor(PlantSeedDefinition.PlantBehavior behavior) {
        return switch (behavior) {
            case WALL_NUT -> WALL_NUT_HEALTH;
            case PRIMAL_WALL_NUT -> PRIMAL_WALL_NUT_HEALTH;
            case TALL_NUT -> TALL_NUT_HEALTH;
            case ENDURIAN -> ENDURIAN_HEALTH;
            default -> DEFAULT_PLANT_HEALTH;
        };
    }

    private static PlantSeedDefinition.PlantBehavior behaviorFor(Entity plant) {
        try {
            return PlantSeedDefinition.PlantBehavior.valueOf(plant.getPersistentData().getString(PLANT_BEHAVIOR_TAG));
        } catch (IllegalArgumentException ignored) {
            return PlantSeedDefinition.PlantBehavior.PLACEHOLDER;
        }
    }

    private static void setPlantHealth(SnowGolem plant, float maxHealth) {
        if (plant.getAttribute(Attributes.MAX_HEALTH) != null) {
            plant.getAttribute(Attributes.MAX_HEALTH).setBaseValue(maxHealth);
        }
        plant.setHealth(maxHealth);
    }

    private record RecentPlantDeath(String dimension, String plantId, BlockPos position, long gameTime) {
    }
}

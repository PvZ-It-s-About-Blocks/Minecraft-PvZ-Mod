package net.PvZModders.PvZMod.progression.seed;

import net.PvZModders.PvZMod.PvZ2Mod;
import net.PvZModders.PvZMod.entity.ModEntities;
import net.PvZModders.PvZMod.progression.sun.SunManager;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

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

    private static final double PLANT_SCAN_RADIUS = 128.0D;
    private static final double SHOOTER_RANGE = 14.0D;
    private static final int SHOOTER_INTERVAL_TICKS = 30;
    private static final int SUNFLOWER_INTERVAL_TICKS = 60;
    private static final int CHOMPER_COOLDOWN_TICKS = 100;
    private static final float PEA_DAMAGE = 4.0F;
    private static final float POTATO_MINE_DAMAGE = 24.0F;
    private static final float CHOMPER_DAMAGE = 40.0F;
    private static final float DEFAULT_PLANT_HEALTH = 20.0F;
    private static final float WALL_NUT_HEALTH = 80.0F;

    private PlantEntityManager() {
    }

    public static boolean placePlant(ServerPlayer player, BlockHitResult target, PlantSeedDefinition definition) {
        if (target == null) {
            return false;
        }

        ServerLevel level = player.serverLevel();
        BlockPos placePos = target.getBlockPos().relative(target.getDirection());
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

        return level.addFreshEntity(plant);
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
        plant.setCustomName(Component.literal(definition.displayName()).withStyle(ChatFormatting.GREEN));
        plant.setCustomNameVisible(true);
        setPlantHealth(plant, definition.behavior() == PlantSeedDefinition.PlantBehavior.WALL_NUT ? WALL_NUT_HEALTH : DEFAULT_PLANT_HEALTH);

        CompoundTag tag = plant.getPersistentData();
        tag.putBoolean(PLANT_TAG, true);
        tag.putString(PLANT_ID_TAG, definition.plantId());
        tag.putString(PLANT_BEHAVIOR_TAG, definition.behavior().name());
        tag.putLong(NEXT_ACTION_TICK_TAG, gameTime + 20L);
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

    private static void tickPlant(ServerLevel level, SnowGolem plant) {
        PlantSeedDefinition.PlantBehavior behavior = behaviorFor(plant);
        switch (behavior) {
            case PEASHOOTER -> tickShooter(level, plant, 1);
            case REPEATER -> tickShooter(level, plant, 2);
            case SUNFLOWER -> tickSunflower(level, plant);
            case POTATO_MINE -> tickPotatoMine(level, plant);
            case CHOMPER -> tickChomper(level, plant);
            case WALL_NUT, PLACEHOLDER -> {
            }
        }
    }

    private static void tickShooter(ServerLevel level, SnowGolem plant, int shots) {
        long gameTime = level.getGameTime();
        CompoundTag tag = plant.getPersistentData();
        if (gameTime < tag.getLong(NEXT_ACTION_TICK_TAG)) {
            return;
        }

        Optional<Zombie> target = nearestZombie(level, plant, SHOOTER_RANGE);
        if (target.isEmpty()) {
            tag.putLong(NEXT_ACTION_TICK_TAG, gameTime + 10L);
            return;
        }

        for (int shot = 0; shot < shots; shot++) {
            shootSnowball(level, plant, target.get(), shot * 0.18D);
        }
        tag.putLong(NEXT_ACTION_TICK_TAG, gameTime + SHOOTER_INTERVAL_TICKS);
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

        Optional<Zombie> target = nearestZombie(level, plant, 1.5D);
        if (target.isEmpty()) {
            return;
        }

        target.get().hurt(level.damageSources().mobAttack(plant), CHOMPER_DAMAGE);
        tag.putLong(CHOMPER_COOLDOWN_TICK_TAG, gameTime + CHOMPER_COOLDOWN_TICKS);
    }

    private static Optional<LivingEntity> findPlantInAttackRange(ServerLevel level, Mob mob) {
        AABB attackArea = mob.getBoundingBox().inflate(0.65D, 0.5D, 0.65D);
        return level.getEntitiesOfClass(LivingEntity.class, attackArea, entity -> entity.isAlive() && isPlant(entity))
                .stream()
                .min((first, second) -> Double.compare(mob.distanceToSqr(first), mob.distanceToSqr(second)));
    }

    private static Optional<Zombie> nearestZombie(ServerLevel level, SnowGolem plant, double range) {
        AABB area = plant.getBoundingBox().inflate(range, 3.0D, range);
        return level.getEntitiesOfClass(Zombie.class, area, Zombie::isAlive)
                .stream()
                .min((first, second) -> Double.compare(plant.distanceToSqr(first), plant.distanceToSqr(second)));
    }

    private static void shootSnowball(ServerLevel level, SnowGolem plant, LivingEntity target, double sideOffset) {
        Snowball snowball = new Snowball(level, plant);
        Vec3 start = plant.position().add(0.0D, 1.25D, 0.0D);
        Vec3 targetPos = target.position().add(0.0D, target.getBbHeight() * 0.55D, 0.0D);
        Vec3 direction = targetPos.subtract(start).normalize();
        Vec3 side = new Vec3(-direction.z, 0.0D, direction.x).normalize().scale(sideOffset);
        snowball.setPos(start.x + side.x, start.y, start.z + side.z);
        snowball.shoot(direction.x, direction.y + 0.05D, direction.z, 1.35F, 0.0F);
        level.addFreshEntity(snowball);

        DamageSource source = level.damageSources().mobProjectile(snowball, plant);
        target.hurt(source, PEA_DAMAGE);
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
}

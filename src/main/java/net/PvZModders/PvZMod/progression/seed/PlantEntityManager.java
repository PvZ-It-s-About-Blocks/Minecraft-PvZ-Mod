package net.PvZModders.PvZMod.progression.seed;

import net.PvZModders.PvZMod.PvZ2Mod;
import net.PvZModders.PvZMod.block.ModBlocks;
import net.PvZModders.PvZMod.entity.ModEntities;
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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.util.Mth;
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
    private static final String GRAVE_X_TAG = "PvZGraveX";
    private static final String GRAVE_Y_TAG = "PvZGraveY";
    private static final String GRAVE_Z_TAG = "PvZGraveZ";
    private static final String TORCHWOOD_BUFFED_TAG = "PvZTorchwoodBuffed";
    private static final String PEA_POD_STACK_TAG = "PvZPeaPodStack";
    private static final String PROJECTILE_KIND_TAG = "PvZProjectileKind";
    private static final String RED_STINGER_MODE_TAG = "PvZRedStingerMode";

    private static final double PLANT_SCAN_RADIUS = 128.0D;
    private static final double SHOOTER_RANGE = 14.0D;
    private static final int SHOOTER_INTERVAL_TICKS = 30;
    private static final int SUNFLOWER_INTERVAL_TICKS = 60;
    private static final int BONK_CHOY_INTERVAL_TICKS = 10;
    private static final int GRAVE_BUSTER_EAT_TICKS = 60;
    private static final int CHOMPER_COOLDOWN_TICKS = 100;
    private static final int LIGHTNING_REED_INTERVAL_TICKS = 25;
    private static final int MELON_PULT_INTERVAL_TICKS = 45;
    private static final float PEA_DAMAGE = 4.0F;
    private static final float POTATO_MINE_DAMAGE = 24.0F;
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
    private static final float DEFAULT_PLANT_HEALTH = 20.0F;
    private static final float WALL_NUT_HEALTH = 80.0F;
    private static final float TALL_NUT_HEALTH = 150.0F;
    private static final float ENDURIAN_HEALTH = 100.0F;
    private static final float RED_STINGER_DEFENSIVE_HEALTH = 55.0F;

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
        tag.putLong(NEXT_ACTION_TICK_TAG, gameTime + 20L);
        if (definition.behavior() == PlantSeedDefinition.PlantBehavior.PEA_POD) {
            tag.putInt(PEA_POD_STACK_TAG, 1);
        }
        if (definition.behavior() == PlantSeedDefinition.PlantBehavior.RED_STINGER) {
            tag.putString(RED_STINGER_MODE_TAG, "NORMAL");
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
            case WALL_NUT, TALL_NUT, TORCHWOOD, PLACEHOLDER -> {
            }
        }
    }

    private static void tickShooter(ServerLevel level, SnowGolem plant, int shots) {
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
}

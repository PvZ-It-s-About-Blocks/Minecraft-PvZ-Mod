package net.PvZModders.PvZMod.progression.seed;

import net.PvZModders.PvZMod.PvZ2Mod;
import net.PvZModders.PvZMod.block.ModBlocks;
import net.PvZModders.PvZMod.entity.ModEntities;
import net.PvZModders.PvZMod.entity.custom.JurassicDinosaurEntity;
import net.PvZModders.PvZMod.entity.custom.PeaProjectileEntity;
import net.PvZModders.PvZMod.entity.custom.PvZZombieEntity;
import net.PvZModders.PvZMod.entity.custom.WildWestMinecartEntity;
import net.PvZModders.PvZMod.progression.beach.BigWaveBeachTideManager;
import net.PvZModders.PvZMod.progression.farfuture.FarFuturePowerTileManager;
import net.PvZModders.PvZMod.progression.greenhouse.GreenhouseCoinManager;
import net.PvZModders.PvZMod.progression.gold.GoldTileManager;
import net.PvZModders.PvZMod.progression.pirate.PirateSeasPlankManager;
import net.PvZModders.PvZMod.progression.sun.SunManager;
import net.PvZModders.PvZMod.progression.targeting.TargetingPriority;
import net.PvZModders.PvZMod.progression.targeting.TargetingPriorityManager;
import net.PvZModders.PvZMod.progression.upgrades.GardenUpgrade;
import net.PvZModders.PvZMod.progression.upgrades.PvZUpgradeSavedData;
import net.PvZModders.PvZMod.progression.zombies.PvZZombieDefinitions;
import net.PvZModders.PvZMod.progression.zombies.PvZZombieSpecial;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShovelItem;
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
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
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
    private static final String FREEZE_STAGE_TAG = "PvZFreezeStage";
    private static final String FREEZE_NEXT_STAGE_TICK_TAG = "PvZFreezeNextStageTick";
    private static final String FREEZE_OVERLAY_UUID_TAG = "PvZFreezeOverlayUuid";
    public static final String WIZARD_DISABLED_TAG = "PvZWizardDisabled";
    public static final String WIZARD_DISABLED_END_TICK_TAG = "PvZWizardDisabledEndTick";
    public static final String OCTO_DISABLED_TAG = "PvZOctoDisabled";
    public static final String OCTO_DISABLED_END_TICK_TAG = "PvZOctoDisabledEndTick";
    public static final String PLANT_VITAMINS_BUFF_END_TICK_TAG = "PvZPlantVitaminsBuffEndTick";
    public static final String PLANT_VITAMINS_ATTACK_SPEED_MULTIPLIER_TAG = "PvZPlantVitaminsAttackSpeedMultiplier";
    private static final String CHARD_GUARD_CHARGES_TAG = "PvZChardGuardCharges";
    private static final String INFI_NUT_LAST_HURT_TICK_TAG = "PvZInfiNutLastHurtTick";
    private static final String MECHANICAL_ZOMBIE_TAG = "PvZMechanicalZombie";
    private static final String FLYING_ZOMBIE_TAG = "PvZFlyingZombie";
    private static final String SUBMERGED_TAG = "PvZSubmerged";
    private static final String SUBMERGED_START_TICK_TAG = "PvZSubmergedStartTick";
    private static final String NEXT_DROWNING_DAMAGE_TICK_TAG = "PvZNextDrowningDamageTick";
    private static final String ALLOW_DROWNING_DAMAGE_TAG = "PvZAllowDrowningDamage";
    private static final String RECENTLY_HOOKED_END_TICK_TAG = "PvZRecentlyHookedEndTick";
    private static final String GUACODILE_RUSHING_TAG = "PvZGuacodileRushing";
    private static final String GUACODILE_RUSH_END_TICK_TAG = "PvZGuacodileRushEndTick";
    public static final String LADDERED_TAG = "PvZLaddered";

    private static final double PLANT_SCAN_RADIUS = 128.0D;
    private static final double SHOOTER_RANGE = 14.0D;
    private static final double PUFF_SHROOM_RANGE = 4.0D;
    private static final double FUME_SHROOM_RANGE = 6.0D;
    private static final double MAGNET_SHROOM_RANGE = 8.0D;
    private static final double PERFUME_SHROOM_RANGE = 8.0D;
    private static final double PHAT_BEET_RADIUS = 3.0D;
    private static final double SPORE_SHROOM_RANGE = 12.0D;
    private static final float PEA_PROJECTILE_SPEED = 0.62F;
    private static final float BASIC_PROJECTILE_SPEED = 0.52F;
    private static final double LOBBED_PROJECTILE_DISTANCE_DIVISOR = 22.0D;
    private static final double LOBBED_PROJECTILE_MIN_SPEED = 0.32D;
    private static final double LOBBED_PROJECTILE_MAX_SPEED = 0.72D;
    private static final int SHOOTER_INTERVAL_TICKS = 30;
    private static final int REPEATER_SECOND_SHOT_DELAY_TICKS = 6;
    private static final int PHAT_BEET_INTERVAL_TICKS = 40;
    private static final int CELERY_STALKER_INTERVAL_TICKS = 12;
    private static final int SUNFLOWER_INTERVAL_TICKS = 20 * 8;
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
    private static final int CITRON_INTERVAL_TICKS = 20 * 5;
    private static final int INFI_NUT_REGEN_INTERVAL_TICKS = 20 * 4;
    private static final int INFI_NUT_RECENT_DAMAGE_TICKS = 20 * 3;
    private static final int MAGNIFYING_GRASS_SUN_COST = 50;
    private static final int SUBMERGED_GRACE_TICKS = 20 * 5;
    private static final int DROWNING_DAMAGE_INTERVAL_TICKS = 20 * 2;
    private static final int BANANA_LAUNCHER_INTERVAL_TICKS = 20 * 6;
    private static final int KERNEL_PULT_INTERVAL_TICKS = 40;
    private static final int COCONUT_CANNON_INTERVAL_TICKS = 20 * 6;
    private static final int SPRING_BEAN_COOLDOWN_TICKS = 20 * 10;
    private static final int CHERRY_BOMB_FUSE_TICKS = 20;
    private static final int RECENT_PLANT_DEATH_WINDOW_TICKS = 20 * 60;
    private static final int MOONFLOWER_INTERVAL_TICKS = 20 * 4;
    private static final int NIGHTSHADE_INTERVAL_TICKS = 16;
    private static final int SHADOW_SHROOM_INTERVAL_TICKS = 20 * 6;
    private static final int SHADOW_SHROOM_CURSE_TICKS = 20 * 8;
    private static final int DUSK_LOBBER_INTERVAL_TICKS = 45;
    private static final int GRIMROSE_COOLDOWN_TICKS = 20 * 9;
    private static final int MAX_SPORE_SHROOM_CLONES_NEARBY = 12;
    private static final int FREEZE_STAGE_INTERVAL_TICKS = 20 * 5;
    private static final int FREEZE_DECAY_INTERVAL_TICKS = 20 * 9;
    private static final int WARMED_THAW_INTERVAL_TICKS = 20 * 2;
    private static final double PEPPER_WARM_RADIUS = 3.0D;
    private static final double ROTOBAGA_RANGE = 14.0D;
    private static final int MARIGOLD_COIN_INTERVAL_TICKS = 20 * 25;
    private static final int GOLD_MAGNET_INTERVAL_TICKS = 20;
    private static final double GOLD_MAGNET_RANGE = 8.0D;
    private static final int ALOE_HEAL_INTERVAL_TICKS = 20 * 5;
    private static final double ALOE_HEAL_RANGE = 4.0D;
    public static final int PLANT_VITAMINS_DURATION_TICKS = 200;
    public static final double PLANT_VITAMINS_ATTACK_SPEED_MULTIPLIER = 1.25D;
    private static final double PLANT_SHOVEL_TWO_REFUND_MULTIPLIER = 0.20D;
    private static final int JALAPENO_FUSE_TICKS = 12;
    private static final int SOLAR_TOMATO_FUSE_TICKS = 12;
    private static final double SOLAR_TOMATO_RADIUS = 3.0D;
    private static final int SOLAR_TOMATO_SUN_PER_ZOMBIE = 50;
    private static final double HOT_DATE_LURE_RANGE = 6.0D;
    private static final double HOT_DATE_FIRE_RADIUS = 3.0D;
    private static final double JALAPENO_RANGE = 16.0D;
    private static final double CACTUS_RANGE = 15.0D;
    private static final int CACTUS_PIERCE_LIMIT = 3;
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
    private static final float LASER_BEAN_DAMAGE = 6.0F;
    private static final float MAGNIFYING_GRASS_DAMAGE = 12.0F;
    private static final float CITRON_DAMAGE = 32.0F;
    private static final float BOWLING_BULB_DAMAGE = 7.0F;
    private static final float GUACODILE_SEED_DAMAGE = 4.0F;
    private static final float GUACODILE_RUSH_DAMAGE = 10.0F;
    private static final float BANANA_DIRECT_DAMAGE = 24.0F;
    private static final float BANANA_SPLASH_DAMAGE = 12.0F;
    private static final float KERNEL_DAMAGE = 4.0F;
    private static final float BUTTER_DAMAGE = 3.0F;
    private static final float SNAPDRAGON_DAMAGE = 5.0F;
    private static final float SPIKEWEED_DAMAGE = 3.0F;
    private static final float SPIKEROCK_DAMAGE = 6.0F;
    private static final float COCONUT_DIRECT_DAMAGE = 30.0F;
    private static final float COCONUT_SPLASH_DAMAGE = 16.0F;
    private static final float CHERRY_BOMB_DAMAGE = 80.0F;
    private static final float PEPPER_PULT_DIRECT_DAMAGE = 9.0F;
    private static final float PEPPER_PULT_SPLASH_DAMAGE = 4.5F;
    private static final float ROTOBAGA_DAMAGE = 4.0F;
    private static final float NIGHTSHADE_DAMAGE = 5.0F;
    private static final float POWERED_NIGHTSHADE_DAMAGE = 8.0F;
    private static final float SHADOW_SHROOM_DAMAGE = 2.5F;
    private static final float DUSK_LOBBER_DIRECT_DAMAGE = 8.0F;
    private static final float DUSK_LOBBER_SPLASH_DAMAGE = 4.0F;
    private static final float SQUASH_DAMAGE = 999.0F;
    private static final float SQUASH_GARGANTUAR_DAMAGE = 90.0F;
    private static final float JALAPENO_DAMAGE = 120.0F;
    private static final float JALAPENO_GARGANTUAR_DAMAGE = 100.0F;
    private static final float CACTUS_DAMAGE = 6.0F;
    private static final float POWERED_DUSK_LOBBER_SPLASH_DAMAGE = 6.0F;
    private static final float GRIMROSE_DAMAGE = 60.0F;
    private static final float FIRE_PEA_DAMAGE = 6.0F;
    private static final float PARSNIP_DAMAGE = 7.0F;
    private static final float WASABI_WHIP_DAMAGE = 6.0F;
    private static final float HOT_DATE_FIRE_DAMAGE = 45.0F;
    private static final int SUN_BEAN_SUN_VALUE = 5;
    private static final float DEFAULT_PLANT_HEALTH = 20.0F;
    private static final float WALL_NUT_HEALTH = 80.0F;
    private static final float PRIMAL_WALL_NUT_HEALTH = 120.0F;
    private static final float TALL_NUT_HEALTH = 150.0F;
    private static final float ENDURIAN_HEALTH = 100.0F;
    private static final float RED_STINGER_DEFENSIVE_HEALTH = 55.0F;
    private static final float INFI_NUT_HEALTH = 55.0F;
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

        if (definition.behavior() == PlantSeedDefinition.PlantBehavior.HOT_POTATO) {
            return thawPlantNear(player, target.getBlockPos());
        }

        BlockPos targetPos = target.getBlockPos();
        BlockPos placePos = graveBuster ? graveTargetPos.above() : targetPos.relative(target.getDirection());
        PlantSeedDefinition.PlantBehavior behavior = definition.behavior();
        boolean aquaticPlant = isAquaticPlant(behavior);
        boolean amphibiousPlant = isAmphibiousPlant(behavior);
        if (aquaticPlant) {
            if (!canPlaceAquaticPlant(level, targetPos)) {
                return false;
            }
            placePos = targetPos;
        } else if (amphibiousPlant && isWaterOrFlooded(level, targetPos)) {
            placePos = targetPos;
        } else if (!amphibiousPlant && isWaterOrFlooded(level, targetPos)) {
            if (!hasLilyPadSupport(level, targetPos)) {
                return false;
            }
            placePos = targetPos.above();
        }
        if (behavior == PlantSeedDefinition.PlantBehavior.LILY_PAD && PirateSeasPlankManager.isChurningWaterHole(level, targetPos)) {
            player.displayClientMessage(Component.literal("Lily Pads cannot root in churning water.").withStyle(ChatFormatting.RED), true);
            return false;
        }
        if (definition.behavior() == PlantSeedDefinition.PlantBehavior.GOLD_LEAF) {
            return GoldTileManager.addGoldTileNear(level, target.getBlockPos());
        }

        if (definition.behavior() == PlantSeedDefinition.PlantBehavior.TILE_TURNIP) {
            boolean added = FarFuturePowerTileManager.addPowerTile(level, target.getBlockPos());
            if (added) {
                level.sendParticles(ParticleTypes.END_ROD, target.getBlockPos().getX() + 0.5D, target.getBlockPos().getY() + 1.1D, target.getBlockPos().getZ() + 0.5D, 18, 0.35D, 0.2D, 0.35D, 0.02D);
            }
            return added;
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

        boolean waterPlacement = isWaterOrFlooded(level, placePos) && (aquaticPlant || amphibiousPlant);
        if ((!level.getBlockState(placePos).isAir() && !waterPlacement)
                || (level.getBlockState(placePos.below()).isAir() && !waterPlacement && !hasLilyPadSupport(level, placePos.below()))) {
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
        if (definition.behavior() == PlantSeedDefinition.PlantBehavior.CHARD_GUARD) {
            tag.putInt(CHARD_GUARD_CHARGES_TAG, 3);
        }
        if (definition.behavior() == PlantSeedDefinition.PlantBehavior.INFI_NUT) {
            tag.putLong(INFI_NUT_LAST_HURT_TICK_TAG, gameTime);
        }
        if (definition.behavior() == PlantSeedDefinition.PlantBehavior.CHERRY_BOMB) {
            tag.putLong(NEXT_ACTION_TICK_TAG, gameTime + CHERRY_BOMB_FUSE_TICKS);
        }
        if (definition.behavior() == PlantSeedDefinition.PlantBehavior.JALAPENO) {
            tag.putLong(NEXT_ACTION_TICK_TAG, gameTime + JALAPENO_FUSE_TICKS);
        }
        if (definition.behavior() == PlantSeedDefinition.PlantBehavior.SOLAR_TOMATO) {
            tag.putLong(NEXT_ACTION_TICK_TAG, gameTime + SOLAR_TOMATO_FUSE_TICKS);
        }
    }

    @SubscribeEvent
    public static void onPlantShovel(PlayerInteractEvent.EntityInteract event) {
        if (!(event.getTarget() instanceof SnowGolem plant) || !isPlant(plant)) {
            return;
        }
        if (!(event.getEntity() instanceof ServerPlayer player) || !(player.level() instanceof ServerLevel level)) {
            return;
        }
        ItemStack held = event.getItemStack();
        if (!(held.getItem() instanceof ShovelItem)) {
            return;
        }

        int refund = calculateShovelCoinRefund(level, plant);
        plant.discard();
        if (refund > 0) {
            GreenhouseCoinManager.giveCoins(player, refund);
            player.displayClientMessage(Component.literal("Plant shoveled: +" + refund + " coins.").withStyle(ChatFormatting.GOLD), true);
        } else {
            player.displayClientMessage(Component.literal("Plant removed.").withStyle(ChatFormatting.YELLOW), true);
        }
        level.sendParticles(ParticleTypes.POOF, plant.getX(), plant.getY() + 0.7D, plant.getZ(), 10, 0.25D, 0.25D, 0.25D, 0.02D);
        level.playSound(null, plant.blockPosition(), SoundEvents.GRASS_BREAK, SoundSource.PLAYERS, 0.75F, 1.25F);
        event.setCancellationResult(InteractionResult.CONSUME);
        event.setCanceled(true);
    }

    private static int calculateShovelCoinRefund(ServerLevel level, SnowGolem plant) {
        if (!PvZUpgradeSavedData.get(level).isUnlocked(GardenUpgrade.PLANT_SHOVEL_II)) {
            return 0;
        }
        return PlantSeedDefinition.getByPlantId(plantId(plant))
                .map(definition -> Math.max(1, Mth.floor(definition.sunCost() * PLANT_SHOVEL_TWO_REFUND_MULTIPLIER)))
                .orElse(0);
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
            float adjustedDamage = adjustedZombiePlantDamage(level, mob, target, damage);
            target.hurt(level.damageSources().mobAttack(mob), adjustedDamage);
            if (mob instanceof PvZZombieEntity zombie
                    && zombie.definition().has(PvZZombieSpecial.SURFER)
                    && zombie.getPersistentData().getBoolean("PvZSurferBoardActive")) {
                zombie.getPersistentData().putBoolean("PvZSurferBoardActive", false);
                zombie.configureForWave(0.23D);
                level.sendParticles(ParticleTypes.SPLASH, zombie.getX(), zombie.getY() + 0.5D, zombie.getZ(), 16, 0.35D, 0.25D, 0.35D, 0.04D);
                level.playSound(null, zombie.blockPosition(), SoundEvents.WOOD_BREAK, SoundSource.HOSTILE, 0.75F, 1.1F);
            }
            if (behaviorFor(target) == PlantSeedDefinition.PlantBehavior.ENDURIAN) {
                mob.hurt(level.damageSources().thorns(target), ENDURIAN_THORN_DAMAGE);
            }
            if (behaviorFor(target) == PlantSeedDefinition.PlantBehavior.GARLIC) {
                divertGarlicZombie(level, mob, target);
            }
        }
        return true;
    }

    private static float adjustedZombiePlantDamage(ServerLevel level, Mob mob, LivingEntity target, float baseDamage) {
        if (!(mob instanceof PvZZombieEntity zombie)) {
            return baseDamage;
        }

        float damage = baseDamage;
        if (zombie.definition().has(PvZZombieSpecial.ROCKPUNCHER) && isDefensivePlant(target)) {
            damage *= 2.0F;
            if (target instanceof SnowGolem plant) {
                PlantEntityManager.applyWizardDisable(level, plant, zombie, 20);
            }
            level.sendParticles(ParticleTypes.CRIT, target.getX(), target.getY() + 0.8D, target.getZ(), 10, 0.25D, 0.25D, 0.25D, 0.03D);
            level.playSound(null, target.blockPosition(), SoundEvents.STONE_BREAK, SoundSource.HOSTILE, 0.75F, 0.85F);
        }
        if (zombie.definition().has(PvZZombieSpecial.JURASSIC_BULLY)) {
            damage *= isDefensivePlant(target) ? 1.5F : 1.25F;
            if (level.getGameTime() % (20L * 6L) < 20L) {
                level.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, target.getX(), target.getY() + 0.9D, target.getZ(), 8, 0.3D, 0.25D, 0.3D, 0.02D);
                level.playSound(null, target.blockPosition(), SoundEvents.HOGLIN_ATTACK, SoundSource.HOSTILE, 0.65F, 0.8F);
            }
        }
        if (isLadderedPlant(target)) {
            damage *= 1.5F;
        }
        return damage;
    }

    public static boolean isDefensivePlant(Entity plant) {
        PlantSeedDefinition.PlantBehavior behavior = behaviorFor(plant);
        return behavior == PlantSeedDefinition.PlantBehavior.WALL_NUT
                || behavior == PlantSeedDefinition.PlantBehavior.PEA_NUT
                || behavior == PlantSeedDefinition.PlantBehavior.TALL_NUT
                || behavior == PlantSeedDefinition.PlantBehavior.PRIMAL_WALL_NUT
                || behavior == PlantSeedDefinition.PlantBehavior.CHARD_GUARD
                || behavior == PlantSeedDefinition.PlantBehavior.INFI_NUT
                || behavior == PlantSeedDefinition.PlantBehavior.ENDURIAN
                || behavior == PlantSeedDefinition.PlantBehavior.SPIKEROCK;
    }

    public static void applyLadderedState(ServerLevel level, SnowGolem plant) {
        if (!isPlant(plant)) {
            return;
        }
        plant.getPersistentData().putBoolean(LADDERED_TAG, true);
        plant.setCustomName(Component.literal(plant.getDisplayName().getString().replace(" (Laddered)", "") + " (Laddered)").withStyle(ChatFormatting.GRAY));
        level.sendParticles(ParticleTypes.CRIT, plant.getX(), plant.getY() + 0.9D, plant.getZ(), 14, 0.3D, 0.35D, 0.3D, 0.03D);
        level.playSound(null, plant.blockPosition(), SoundEvents.LADDER_PLACE, SoundSource.HOSTILE, 0.8F, 0.8F);
    }

    public static boolean isLadderedPlant(Entity plant) {
        return isPlant(plant) && plant.getPersistentData().getBoolean(LADDERED_TAG);
    }

    public static boolean isPlant(Entity entity) {
        return entity != null && entity.getPersistentData().getBoolean(PLANT_TAG);
    }

    public static String plantId(Entity entity) {
        return isPlant(entity) ? entity.getPersistentData().getString(PLANT_ID_TAG) : "";
    }

    public static boolean canApplyPlantVitamins(Entity plant) {
        if (!isPlant(plant)) {
            return false;
        }
        return switch (behaviorFor(plant)) {
            case PEASHOOTER, REPEATER, FIRE_PEASHOOTER, PEA_NUT, BLOOMERANG, BONK_CHOY, SPLIT_PEA, PEA_POD,
                    LIGHTNING_REED, MELON_PULT, WINTER_MELON, RED_STINGER, AKEE, PUFF_SHROOM, FUME_SHROOM,
                    PRIMAL_PEASHOOTER, PHAT_BEET, CELERY_STALKER, SPORE_SHROOM, LASER_BEAN, CITRON,
                    MAGNIFYING_GRASS, PEPPER_PULT, ROTOBAGA, KERNEL_PULT, SNAPDRAGON, SPIKEWEED,
                    COCONUT_CANNON, THREEPEATER, SPIKEROCK, BOWLING_BULB, GUACODILE, BANANA_LAUNCHER,
                    NIGHTSHADE, SHADOW_SHROOM, DUSK_LOBBER, CACTUS, PARSNIP, WASABI_WHIP -> true;
            default -> false;
        };
    }

    public static boolean applyPlantVitamins(Player player, LivingEntity plant) {
        if (!canApplyPlantVitamins(plant) || !(plant.level() instanceof ServerLevel level)) {
            return false;
        }
        CompoundTag tag = plant.getPersistentData();
        tag.putLong(PLANT_VITAMINS_BUFF_END_TICK_TAG, level.getGameTime() + PLANT_VITAMINS_DURATION_TICKS);
        tag.putDouble(PLANT_VITAMINS_ATTACK_SPEED_MULTIPLIER_TAG, PLANT_VITAMINS_ATTACK_SPEED_MULTIPLIER);
        level.sendParticles(ParticleTypes.HEART, plant.getX(), plant.getY() + 1.0D, plant.getZ(), 8, 0.25D, 0.35D, 0.25D, 0.02D);
        level.playSound(null, plant.blockPosition(), SoundEvents.BREWING_STAND_BREW, SoundSource.PLAYERS, 0.65F, 1.35F);
        player.displayClientMessage(Component.literal("Plant Vitamins applied.").withStyle(ChatFormatting.GREEN), true);
        return true;
    }

    public static boolean isPlantVitaminsBuffed(Entity plant) {
        return isPlant(plant)
                && plant.level() instanceof ServerLevel level
                && plant.getPersistentData().getLong(PLANT_VITAMINS_BUFF_END_TICK_TAG) > level.getGameTime();
    }

    public static double getPlantAttackSpeedMultiplier(Entity plant) {
        if (!isPlantVitaminsBuffed(plant)) {
            return 1.0D;
        }
        return Math.max(1.0D, plant.getPersistentData().getDouble(PLANT_VITAMINS_ATTACK_SPEED_MULTIPLIER_TAG));
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

        if (isPlant(event.getEntity()) && event.getEntity().getPersistentData().getBoolean(ALLOW_DROWNING_DAMAGE_TAG)) {
            event.getEntity().getPersistentData().remove(ALLOW_DROWNING_DAMAGE_TAG);
        } else
        if (isPlant(event.getEntity()) && isEnvironmentalPlaceholderDamage(event.getSource())) {
            event.setCanceled(true);
            return;
        }

        if (isPlant(event.getEntity()) && event.getEntity().level() instanceof ServerLevel level
                && event.getEntity().getHealth() - event.getAmount() <= 0.0F) {
            if (event.getEntity() instanceof SnowGolem plant
                    && behaviorFor(plant) == PlantSeedDefinition.PlantBehavior.HOT_DATE) {
                triggerHotDateFire(level, plant);
            }
            recordPlantDeath(level, event.getEntity());
        }

        if (isPlant(event.getEntity()) && event.getEntity().level() instanceof ServerLevel level
                && behaviorFor(event.getEntity()) == PlantSeedDefinition.PlantBehavior.INFI_NUT) {
            event.getEntity().getPersistentData().putLong(INFI_NUT_LAST_HURT_TICK_TAG, level.getGameTime());
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
            cleanupFreezeOverlay(level, event.getEntity());
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

        event.setCanceled(true);
        projectile.discard();
        if (event.getRayTraceResult() instanceof net.minecraft.world.phys.EntityHitResult hitResult && isPlant(hitResult.getEntity())) {
            event.setCanceled(true);
        }
    }

    private static void tickPlant(ServerLevel level, SnowGolem plant) {
        ensurePlantNameVisible(plant);
        lookAtNearestHostile(level, plant);
        if (tickSubmergedPlantDrowning(level, plant)) {
            return;
        }
        if (isOctoDisabled(level, plant)) {
            if (level.getGameTime() % 20L == 0L) {
                level.sendParticles(ParticleTypes.SQUID_INK, plant.getX(), plant.getY() + 1.0D, plant.getZ(), 5, 0.25D, 0.35D, 0.25D, 0.01D);
            }
            return;
        }
        if (isWizardDisabled(level, plant)) {
            if (level.getGameTime() % 20L == 0L) {
                level.sendParticles(ParticleTypes.ENCHANT, plant.getX(), plant.getY() + 1.0D, plant.getZ(), 5, 0.25D, 0.35D, 0.25D, 0.01D);
            }
            return;
        }
        if (isPlantFrozen(plant)) {
            syncFreezeOverlay(level, plant);
            if (level.getGameTime() % 20L == 0L) {
                level.sendParticles(ParticleTypes.SNOWFLAKE, plant.getX(), plant.getY() + 1.0D, plant.getZ(), 4, 0.25D, 0.35D, 0.25D, 0.01D);
            }
            return;
        }

        PlantSeedDefinition.PlantBehavior behavior = behaviorFor(plant);
        switch (behavior) {
            case PEASHOOTER -> tickShooter(level, plant, 1);
            case FIRE_PEASHOOTER -> tickFirePeashooter(level, plant);
            case SOLAR_TOMATO -> tickSolarTomato(level, plant);
            case PEA_NUT -> tickPeaNut(level, plant);
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
            case LASER_BEAN -> tickLaserBean(level, plant);
            case BLOVER -> tickBlover(level, plant);
            case CITRON -> tickCitron(level, plant);
            case EM_PEACH -> tickEmPeach(level, plant);
            case INFI_NUT -> tickInfiNut(level, plant);
            case MAGNIFYING_GRASS -> tickMagnifyingGrass(level, plant);
            case TILE_TURNIP -> tickTileTurnip(level, plant);
            case HOT_POTATO -> plant.discard();
            case PEPPER_PULT -> tickPepperPult(level, plant);
            case CHARD_GUARD -> tickChardGuard(level, plant);
            case STUNION -> tickStunion(level, plant);
            case ROTOBAGA -> tickRotobaga(level, plant);
            case KERNEL_PULT -> tickKernelPult(level, plant);
            case SNAPDRAGON -> tickSnapdragon(level, plant);
            case SPIKEWEED -> tickSpikeweed(level, plant, false);
            case SPRING_BEAN -> tickSpringBean(level, plant);
            case COCONUT_CANNON -> tickCoconutCannon(level, plant);
            case THREEPEATER -> tickThreepeater(level, plant);
            case SPIKEROCK -> tickSpikeweed(level, plant, true);
            case CHERRY_BOMB -> tickCherryBomb(level, plant);
            case TANGLE_KELP -> tickTangleKelp(level, plant);
            case BOWLING_BULB -> tickBowlingBulb(level, plant);
            case GUACODILE -> tickGuacodile(level, plant);
            case BANANA_LAUNCHER -> tickBananaLauncher(level, plant);
            case MOONFLOWER -> tickMoonflower(level, plant);
            case NIGHTSHADE -> tickNightshade(level, plant);
            case SHADOW_SHROOM -> tickShadowShroom(level, plant);
            case DUSK_LOBBER -> tickDuskLobber(level, plant);
            case GRIMROSE -> tickGrimrose(level, plant);
            case SQUASH -> tickSquash(level, plant);
            case JALAPENO -> tickJalapeno(level, plant);
            case MARIGOLD -> tickMarigold(level, plant);
            case GOLD_MAGNET -> tickGoldMagnet(level, plant);
            case CACTUS -> tickCactus(level, plant);
            case ALOE -> tickAloe(level, plant);
            case PARSNIP -> tickParsnip(level, plant);
            case HOT_DATE -> tickHotDate(level, plant);
            case WASABI_WHIP -> tickWasabiWhip(level, plant);
            case WALL_NUT, PRIMAL_WALL_NUT, TALL_NUT, TORCHWOOD, GARLIC, LILY_PAD, PLACEHOLDER -> {
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
        level.sendParticles(ParticleTypes.CRIT, plant.getX(), plant.getY() + 1.15D, plant.getZ(), 4, 0.15D, 0.08D, 0.15D, 0.01D);
        tag.putLong(NEXT_ACTION_TICK_TAG, gameTime + adjustedCooldown(plant, SHOOTER_INTERVAL_TICKS));
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
            tag.putLong(NEXT_ACTION_TICK_TAG, gameTime + adjustedCooldown(plant, SHOOTER_INTERVAL_TICKS));
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

    private static void tickFirePeashooter(ServerLevel level, SnowGolem plant) {
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

        Snowball firePea = createProjectileVisual(level, plant, "fire_pea");
        Vec3 start = plant.position().add(0.0D, 1.25D, 0.0D);
        Vec3 direction = horizontalDirectionTo(plant, target.get());
        firePea.setPos(start.x, start.y, start.z);
        firePea.shoot(direction.x, 0.0D, direction.z, PEA_PROJECTILE_SPEED, 0.0F);
        firePea.setSecondsOnFire(2);
        firePea.getPersistentData().putBoolean(PLANT_PROJECTILE_TAG, true);
        firePea.getPersistentData().putString(PROJECTILE_KIND_TAG, "fire_pea");
        level.addFreshEntity(firePea);
        plant.swing(InteractionHand.MAIN_HAND, true);
        hurtWithoutKnockback(target.get(), level.damageSources().mobProjectile(firePea, plant), scaledPlantDamage(level, plant, FIRE_PEA_DAMAGE));
        level.sendParticles(ParticleTypes.FLAME, plant.getX(), plant.getY() + 1.15D, plant.getZ(), 6, 0.15D, 0.08D, 0.15D, 0.01D);
        tag.putLong(NEXT_ACTION_TICK_TAG, gameTime + adjustedCooldown(plant, SHOOTER_INTERVAL_TICKS));
    }

    private static void tickPeaNut(ServerLevel level, SnowGolem plant) {
        tickShooter(level, plant, 1);
    }

    private static void tickSolarTomato(ServerLevel level, SnowGolem plant) {
        long gameTime = level.getGameTime();
        CompoundTag tag = plant.getPersistentData();
        if (gameTime < tag.getLong(NEXT_ACTION_TICK_TAG)) {
            return;
        }

        AABB area = plant.getBoundingBox().inflate(SOLAR_TOMATO_RADIUS, 1.5D, SOLAR_TOMATO_RADIUS);
        List<PvZZombieEntity> targets = level.getEntitiesOfClass(PvZZombieEntity.class, area,
                zombie -> zombie.isAlive() && zombie.getPersistentData().getBoolean("PvZWaveZombie"))
                .stream()
                .limit(12)
                .toList();
        for (PvZZombieEntity zombie : targets) {
            int duration = PvZZombieDefinitions.isGargantuarLike(zombie) ? 30 : 60;
            zombie.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, duration, 9));
            zombie.getNavigation().stop();
            SunManager.spawnSunAt(level, zombie.blockPosition().above(2), SOLAR_TOMATO_SUN_PER_ZOMBIE);
        }
        level.sendParticles(ParticleTypes.HAPPY_VILLAGER, plant.getX(), plant.getY() + 0.8D, plant.getZ(), 30, 1.3D, 0.5D, 1.3D, 0.04D);
        level.playSound(null, plant.blockPosition(), SoundEvents.BEEHIVE_DRIP, SoundSource.BLOCKS, 0.8F, 1.2F);
        plant.discard();
    }

    private static void tickSquash(ServerLevel level, SnowGolem plant) {
        Optional<Zombie> target = selectZombie(level, plant, 2.0D);
        if (target.isEmpty()) {
            return;
        }

        Zombie zombie = target.get();
        float damage = isGargantuarLike(zombie) ? SQUASH_GARGANTUAR_DAMAGE : SQUASH_DAMAGE;
        hurtWithoutKnockback(zombie, level.damageSources().mobAttack(plant), scaledPlantDamage(level, plant, damage));
        plant.setDeltaMovement(new Vec3(0.0D, 0.45D, 0.0D));
        level.sendParticles(ParticleTypes.EXPLOSION, zombie.getX(), zombie.getY() + 0.5D, zombie.getZ(), 3, 0.4D, 0.2D, 0.4D, 0.0D);
        level.playSound(null, plant.blockPosition(), SoundEvents.ANVIL_LAND, SoundSource.HOSTILE, 0.75F, 1.4F);
        plant.discard();
    }

    private static void tickJalapeno(ServerLevel level, SnowGolem plant) {
        long gameTime = level.getGameTime();
        CompoundTag tag = plant.getPersistentData();
        if (gameTime < tag.getLong(NEXT_ACTION_TICK_TAG)) {
            level.sendParticles(ParticleTypes.SMOKE, plant.getX(), plant.getY() + 0.7D, plant.getZ(), 2, 0.15D, 0.15D, 0.15D, 0.01D);
            return;
        }

        Vec3 facing = facingVector(plant);
        Vec3 start = plant.position().add(0.0D, 0.9D, 0.0D);
        Vec3 end = start.add(facing.scale(JALAPENO_RANGE));
        for (Zombie zombie : level.getEntitiesOfClass(Zombie.class, plant.getBoundingBox().inflate(JALAPENO_RANGE, 2.0D, JALAPENO_RANGE), Zombie::isAlive)) {
            Vec3 toZombie = zombie.position().subtract(plant.position()).multiply(1.0D, 0.0D, 1.0D);
            if (toZombie.lengthSqr() < 1.0E-4D || toZombie.length() > JALAPENO_RANGE || toZombie.normalize().dot(facing) < 0.92D) {
                continue;
            }
            float damage = isGargantuarLike(zombie) ? JALAPENO_GARGANTUAR_DAMAGE : JALAPENO_DAMAGE;
            hurtWithoutKnockback(zombie, level.damageSources().mobAttack(plant), scaledPlantDamage(level, plant, damage));
            zombie.setSecondsOnFire(2);
        }
        renderBeam(level, start, end, ParticleTypes.FLAME);
        level.sendParticles(ParticleTypes.FLAME, plant.getX(), plant.getY() + 0.6D, plant.getZ(), 36, 1.0D, 0.3D, 1.0D, 0.08D);
        level.playSound(null, plant.blockPosition(), SoundEvents.BLAZE_SHOOT, SoundSource.HOSTILE, 0.95F, 0.7F);
        plant.discard();
    }

    private static void tickParsnip(ServerLevel level, SnowGolem plant) {
        long gameTime = level.getGameTime();
        CompoundTag tag = plant.getPersistentData();
        if (gameTime < tag.getLong(NEXT_ACTION_TICK_TAG)) {
            return;
        }

        Optional<Zombie> target = selectZombie(level, plant, 2.0D);
        if (target.isPresent()) {
            Zombie zombie = target.get();
            hurtWithoutKnockback(zombie, level.damageSources().mobAttack(plant), scaledPlantDamage(level, plant, PARSNIP_DAMAGE));
            plant.swing(InteractionHand.MAIN_HAND, true);
            level.sendParticles(ParticleTypes.CRIT, zombie.getX(), zombie.getY() + 0.7D, zombie.getZ(), 8, 0.22D, 0.25D, 0.22D, 0.04D);
            tag.putLong(NEXT_ACTION_TICK_TAG, gameTime + adjustedCooldown(plant, 16));
            return;
        }

        selectZombie(level, plant, 5.0D).ifPresent(zombie -> {
            Vec3 direction = zombie.position().subtract(plant.position()).multiply(1.0D, 0.0D, 1.0D);
            if (direction.lengthSqr() > 1.0E-4D) {
                plant.setDeltaMovement(direction.normalize().scale(0.35D).add(0.0D, 0.12D, 0.0D));
                plant.hurtMarked = true;
                level.sendParticles(ParticleTypes.CLOUD, plant.getX(), plant.getY() + 0.2D, plant.getZ(), 6, 0.2D, 0.1D, 0.2D, 0.02D);
            }
        });
        tag.putLong(NEXT_ACTION_TICK_TAG, gameTime + adjustedCooldown(plant, 24));
    }

    private static void tickHotDate(ServerLevel level, SnowGolem plant) {
        long gameTime = level.getGameTime();
        CompoundTag tag = plant.getPersistentData();
        if (gameTime < tag.getLong(NEXT_ACTION_TICK_TAG)) {
            return;
        }

        AABB area = plant.getBoundingBox().inflate(HOT_DATE_LURE_RANGE, 2.0D, HOT_DATE_LURE_RANGE);
        for (Zombie zombie : level.getEntitiesOfClass(Zombie.class, area, Zombie::isAlive)) {
            if (isGargantuarLike(zombie)) {
                continue;
            }
            zombie.getNavigation().moveTo(plant, 0.85D);
            zombie.getLookControl().setLookAt(plant);
        }
        level.sendParticles(ParticleTypes.HEART, plant.getX(), plant.getY() + 1.0D, plant.getZ(), 5, 0.25D, 0.25D, 0.25D, 0.01D);
        tag.putLong(NEXT_ACTION_TICK_TAG, gameTime + 20L);
    }

    private static void triggerHotDateFire(ServerLevel level, SnowGolem plant) {
        AABB area = plant.getBoundingBox().inflate(HOT_DATE_FIRE_RADIUS, 1.5D, HOT_DATE_FIRE_RADIUS);
        for (Zombie zombie : level.getEntitiesOfClass(Zombie.class, area, Zombie::isAlive)) {
            float damage = isGargantuarLike(zombie) ? HOT_DATE_FIRE_DAMAGE * 0.65F : HOT_DATE_FIRE_DAMAGE;
            hurtWithoutKnockback(zombie, level.damageSources().mobAttack(plant), scaledPlantDamage(level, plant, damage));
            zombie.setSecondsOnFire(2);
        }
        level.sendParticles(ParticleTypes.FLAME, plant.getX(), plant.getY() + 0.6D, plant.getZ(), 42, 1.1D, 0.35D, 1.1D, 0.08D);
        level.playSound(null, plant.blockPosition(), SoundEvents.FIRECHARGE_USE, SoundSource.HOSTILE, 0.95F, 0.8F);
    }

    private static void tickWasabiWhip(ServerLevel level, SnowGolem plant) {
        long gameTime = level.getGameTime();
        CompoundTag tag = plant.getPersistentData();
        if (gameTime < tag.getLong(NEXT_ACTION_TICK_TAG)) {
            return;
        }

        Vec3 facing = facingVector(plant);
        List<Zombie> targets = level.getEntitiesOfClass(Zombie.class, plant.getBoundingBox().inflate(2.1D, 1.5D, 2.1D), Zombie::isAlive)
                .stream()
                .filter(zombie -> {
                    Vec3 toZombie = zombie.position().subtract(plant.position()).multiply(1.0D, 0.0D, 1.0D);
                    return toZombie.lengthSqr() <= 4.5D
                            && toZombie.lengthSqr() > 1.0E-4D
                            && Math.abs(toZombie.normalize().dot(facing)) > 0.55D;
                })
                .toList();
        if (targets.isEmpty()) {
            tag.putLong(NEXT_ACTION_TICK_TAG, gameTime + 8L);
            return;
        }

        for (Zombie zombie : targets) {
            hurtWithoutKnockback(zombie, level.damageSources().mobAttack(plant), scaledPlantDamage(level, plant, WASABI_WHIP_DAMAGE));
            zombie.setSecondsOnFire(1);
        }
        plant.swing(InteractionHand.MAIN_HAND, true);
        level.sendParticles(ParticleTypes.FLAME, plant.getX(), plant.getY() + 0.9D, plant.getZ(), 14, 0.45D, 0.2D, 0.45D, 0.04D);
        tag.putLong(NEXT_ACTION_TICK_TAG, gameTime + adjustedCooldown(plant, 18));
    }

    private static void tickMarigold(ServerLevel level, SnowGolem plant) {
        long gameTime = level.getGameTime();
        CompoundTag tag = plant.getPersistentData();
        if (gameTime < tag.getLong(NEXT_ACTION_TICK_TAG)) {
            return;
        }

        GreenhouseCoinManager.addCoinsToNearestPlayer(level, plant.position().add(0.0D, 1.0D, 0.0D), 1);
        level.sendParticles(ParticleTypes.HAPPY_VILLAGER, plant.getX(), plant.getY() + 1.0D, plant.getZ(), 8, 0.25D, 0.25D, 0.25D, 0.02D);
        level.playSound(null, plant.blockPosition(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.35F, 1.7F);
        tag.putLong(NEXT_ACTION_TICK_TAG, gameTime + MARIGOLD_COIN_INTERVAL_TICKS);
    }

    private static void tickGoldMagnet(ServerLevel level, SnowGolem plant) {
        long gameTime = level.getGameTime();
        CompoundTag tag = plant.getPersistentData();
        if (gameTime < tag.getLong(NEXT_ACTION_TICK_TAG)) {
            return;
        }

        int collected = GreenhouseCoinManager.collectCoinsNearby(level, plant.position().add(0.0D, 0.8D, 0.0D), GOLD_MAGNET_RANGE);
        if (collected > 0) {
            level.sendParticles(ParticleTypes.ENCHANT, plant.getX(), plant.getY() + 1.0D, plant.getZ(), 18, 0.4D, 0.35D, 0.4D, 0.04D);
            level.playSound(null, plant.blockPosition(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.45F, 1.2F);
        }
        tag.putLong(NEXT_ACTION_TICK_TAG, gameTime + GOLD_MAGNET_INTERVAL_TICKS);
    }

    private static void tickCactus(ServerLevel level, SnowGolem plant) {
        long gameTime = level.getGameTime();
        CompoundTag tag = plant.getPersistentData();
        if (gameTime < tag.getLong(NEXT_ACTION_TICK_TAG)) {
            return;
        }

        Vec3 facing = facingVector(plant);
        List<Zombie> candidates = level.getEntitiesOfClass(Zombie.class, plant.getBoundingBox().inflate(CACTUS_RANGE, 4.0D, CACTUS_RANGE), Zombie::isAlive)
                .stream()
                .filter(zombie -> isFlyingZombie(zombie) || isInFrontCone(plant, zombie, facing, CACTUS_RANGE))
                .sorted((first, second) -> {
                    boolean firstFlying = isFlyingZombie(first);
                    boolean secondFlying = isFlyingZombie(second);
                    if (firstFlying != secondFlying) {
                        return firstFlying ? -1 : 1;
                    }
                    return Double.compare(plant.distanceToSqr(first), plant.distanceToSqr(second));
                })
                .limit(CACTUS_PIERCE_LIMIT)
                .toList();
        if (candidates.isEmpty()) {
            tag.putLong(NEXT_ACTION_TICK_TAG, gameTime + 10L);
            return;
        }

        for (Zombie zombie : candidates) {
            hurtWithoutKnockback(zombie, level.damageSources().mobAttack(plant), scaledPlantDamage(level, plant, CACTUS_DAMAGE));
        }
        shootSnowballVisual(level, plant, candidates.get(0), false, "cactus_thorn");
        level.sendParticles(ParticleTypes.CRIT, plant.getX(), plant.getY() + 1.1D, plant.getZ(), 8, 0.2D, 0.12D, 0.2D, 0.02D);
        tag.putLong(NEXT_ACTION_TICK_TAG, gameTime + SHOOTER_INTERVAL_TICKS);
    }

    private static void tickAloe(ServerLevel level, SnowGolem plant) {
        long gameTime = level.getGameTime();
        CompoundTag tag = plant.getPersistentData();
        if (gameTime < tag.getLong(NEXT_ACTION_TICK_TAG)) {
            return;
        }

        Optional<SnowGolem> target = level.getEntitiesOfClass(SnowGolem.class, plant.getBoundingBox().inflate(ALOE_HEAL_RANGE), other ->
                        other != plant && other.isAlive() && isPlant(other) && other.getHealth() < other.getMaxHealth())
                .stream()
                .min((first, second) -> Float.compare(first.getHealth() / first.getMaxHealth(), second.getHealth() / second.getMaxHealth()));
        target.ifPresent(healed -> {
            healed.heal(6.0F);
            level.sendParticles(ParticleTypes.HAPPY_VILLAGER, healed.getX(), healed.getY() + 1.0D, healed.getZ(), 10, 0.25D, 0.25D, 0.25D, 0.02D);
            renderBeam(level, plant.position().add(0.0D, 1.0D, 0.0D), healed.position().add(0.0D, 1.0D, 0.0D), ParticleTypes.HAPPY_VILLAGER);
            level.playSound(null, healed.blockPosition(), SoundEvents.BREWING_STAND_BREW, SoundSource.PLAYERS, 0.45F, 1.4F);
        });
        tag.putLong(NEXT_ACTION_TICK_TAG, gameTime + ALOE_HEAL_INTERVAL_TICKS);
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
        hurtWithoutKnockback(target.get(), level.damageSources().mobAttack(plant), PUFF_SHROOM_DAMAGE);
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
            hurtWithoutKnockback(zombie, level.damageSources().mobAttack(plant), FUME_SHROOM_DAMAGE);
        }
        Vec3 center = plant.position().add(facing.scale(2.5D)).add(0.0D, 0.8D, 0.0D);
        level.sendParticles(ParticleTypes.CLOUD, center.x, center.y, center.z, 32, 1.2D, 0.35D, 1.2D, 0.04D);
        level.playSound(null, plant.blockPosition(), SoundEvents.FIRE_EXTINGUISH, SoundSource.HOSTILE, 0.35F, 1.8F);
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
            renderBeam(level, plant.position().add(0.0D, 1.1D, 0.0D), target.get().position().add(0.0D, 1.0D, 0.0D), ParticleTypes.ENCHANT);
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
        hurtWithoutKnockback(zombie, level.damageSources().mobAttack(plant), PRIMAL_PEA_DAMAGE);
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
            hurtWithoutKnockback(zombie, level.damageSources().generic(), PRIMAL_POTATO_MINE_DAMAGE);
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
            hurtWithoutKnockback(zombie, level.damageSources().mobAttack(plant), PHAT_BEET_DAMAGE);
        }
        level.sendParticles(ParticleTypes.NOTE, plant.getX(), plant.getY() + 1.1D, plant.getZ(), 8, 0.8D, 0.2D, 0.8D, 0.0D);
        level.sendParticles(ParticleTypes.SONIC_BOOM, plant.getX(), plant.getY() + 0.3D, plant.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
        level.playSound(null, plant.blockPosition(), SoundEvents.NOTE_BLOCK_BASEDRUM.get(), SoundSource.HOSTILE, 0.7F, 0.6F);
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
            hurtWithoutKnockback(zombie, level.damageSources().mobAttack(plant), CELERY_STALKER_DAMAGE);
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
        hurtWithoutKnockback(zombie, level.damageSources().mobAttack(plant), SPORE_SHROOM_DAMAGE);
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
            hurtWithoutKnockback(zombie, level.damageSources().generic(), POTATO_MINE_DAMAGE);
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

        hurtWithoutKnockback(target.get(), level.damageSources().mobAttack(plant), CHOMPER_DAMAGE);
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
            hurtWithoutKnockback(zombie, level.damageSources().mobAttack(plant), damage);
            hurtWithoutKnockback(zombie, level.damageSources().mobAttack(plant), damage);
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
        target.ifPresent(zombie -> hurtWithoutKnockback(zombie, level.damageSources().mobAttack(plant), BONK_CHOY_DAMAGE));
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
        hurtWithoutKnockback(triggeringZombie, level.damageSources().mobAttack(plant), CHILI_BEAN_DAMAGE);
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
        hurtWithoutKnockback(primary, level.damageSources().mobAttack(plant), LIGHTNING_REED_DAMAGE);
        renderBeam(level, plant.position().add(0.0D, 1.1D, 0.0D), primary.position().add(0.0D, primary.getBbHeight() * 0.55D, 0.0D), ParticleTypes.ELECTRIC_SPARK);
        level.sendParticles(ParticleTypes.ELECTRIC_SPARK, primary.getX(), primary.getY() + 1.0D, primary.getZ(), 16, 0.35D, 0.5D, 0.35D, 0.02D);

        List<Zombie> chainedTargets = level.getEntitiesOfClass(Zombie.class, primary.getBoundingBox().inflate(4.0D), Zombie::isAlive)
                .stream()
                .filter(zombie -> zombie != primary)
                .sorted((first, second) -> Double.compare(primary.distanceToSqr(first), primary.distanceToSqr(second)))
                .limit(2)
                .toList();
        float chainDamage = LIGHTNING_REED_DAMAGE * 0.7F;
        Zombie previousTarget = primary;
        for (Zombie zombie : chainedTargets) {
            hurtWithoutKnockback(zombie, level.damageSources().mobAttack(plant), chainDamage);
            renderBeam(level, previousTarget.position().add(0.0D, previousTarget.getBbHeight() * 0.55D, 0.0D), zombie.position().add(0.0D, zombie.getBbHeight() * 0.55D, 0.0D), ParticleTypes.ELECTRIC_SPARK);
            level.sendParticles(ParticleTypes.ELECTRIC_SPARK, zombie.getX(), zombie.getY() + 1.0D, zombie.getZ(), 10, 0.3D, 0.45D, 0.3D, 0.02D);
            chainDamage *= 0.7F;
            previousTarget = zombie;
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
        hurtWithoutKnockback(directTarget, level.damageSources().mobAttack(plant), directDamage);
        for (Zombie zombie : level.getEntitiesOfClass(Zombie.class, directTarget.getBoundingBox().inflate(2.25D, 1.0D, 2.25D), Zombie::isAlive)) {
            if (zombie != directTarget) {
                hurtWithoutKnockback(zombie, level.damageSources().mobAttack(plant), splashDamage);
            }
            if (winter) {
                zombie.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20 * 6, 2));
            }
        }
        shootLobbedSnowballVisual(level, plant, directTarget, winter ? "winter_melon" : "melon");
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
        hurtWithoutKnockback(target.get(), level.damageSources().mobAttack(plant), damage);
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
            hurtWithoutKnockback(current, level.damageSources().mobAttack(plant), AKEE_DAMAGE);
            shootLobbedSnowballVisual(level, plant, current, "akee_seed");
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

    private static void tickLaserBean(ServerLevel level, SnowGolem plant) {
        long gameTime = level.getGameTime();
        CompoundTag tag = plant.getPersistentData();
        if (gameTime < tag.getLong(NEXT_ACTION_TICK_TAG)) {
            return;
        }

        Vec3 facing = facingVector(plant);
        List<Zombie> targets = level.getEntitiesOfClass(Zombie.class, plant.getBoundingBox().inflate(SHOOTER_RANGE, 2.0D, SHOOTER_RANGE), Zombie::isAlive)
                .stream()
                .filter(zombie -> isInFrontCone(plant, zombie, facing, SHOOTER_RANGE))
                .toList();
        if (targets.isEmpty()) {
            tag.putLong(NEXT_ACTION_TICK_TAG, gameTime + 10L);
            return;
        }

        Vec3 start = plant.position().add(0.0D, 1.15D, 0.0D);
        Vec3 end = start.add(facing.scale(SHOOTER_RANGE));
        renderGuardianStyleLaser(level, start, end);
        for (Zombie zombie : targets) {
            hurtWithoutKnockback(zombie, level.damageSources().mobAttack(plant), scaledPlantDamage(level, plant, LASER_BEAN_DAMAGE));
        }
        tag.putLong(NEXT_ACTION_TICK_TAG, gameTime + SHOOTER_INTERVAL_TICKS);
    }

    private static void tickBlover(ServerLevel level, SnowGolem plant) {
        AABB gustArea = plant.getBoundingBox().inflate(7.0D, 5.0D, 7.0D);
        for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, gustArea, entity -> entity.isAlive() && isHostileTarget(entity) && isFlyingZombie(entity))) {
            Vec3 away = target.position().subtract(plant.position()).multiply(1.0D, 0.0D, 1.0D);
            if (away.lengthSqr() < 1.0E-4D) {
                away = facingVector(plant);
            }
            target.setDeltaMovement(target.getDeltaMovement().add(away.normalize().scale(1.2D)).add(0.0D, 0.45D, 0.0D));
            hurtWithoutKnockback(target, level.damageSources().mobAttack(plant), 12.0F);
        }
        level.sendParticles(ParticleTypes.CLOUD, plant.getX(), plant.getY() + 0.8D, plant.getZ(), 42, 2.0D, 0.6D, 2.0D, 0.08D);
        level.playSound(null, plant.blockPosition(), SoundEvents.PHANTOM_FLAP, SoundSource.HOSTILE, 0.7F, 1.5F);
        plant.discard();
    }

    private static void tickCitron(ServerLevel level, SnowGolem plant) {
        long gameTime = level.getGameTime();
        CompoundTag tag = plant.getPersistentData();
        if (gameTime < tag.getLong(NEXT_ACTION_TICK_TAG)) {
            if (gameTime % 10L == 0L) {
                level.sendParticles(ParticleTypes.ELECTRIC_SPARK, plant.getX(), plant.getY() + 1.15D, plant.getZ(), 3, 0.25D, 0.15D, 0.25D, 0.01D);
            }
            return;
        }

        Optional<Zombie> target = selectZombie(level, plant, SHOOTER_RANGE);
        if (target.isEmpty()) {
            tag.putLong(NEXT_ACTION_TICK_TAG, gameTime + 10L);
            return;
        }

        Zombie zombie = target.get();
        shootSnowballVisual(level, plant, zombie, false, "citron_plasma");
        level.sendParticles(ParticleTypes.END_ROD, zombie.getX(), zombie.getY() + 0.9D, zombie.getZ(), 24, 0.35D, 0.35D, 0.35D, 0.04D);
        level.playSound(null, plant.blockPosition(), SoundEvents.BEACON_POWER_SELECT, SoundSource.HOSTILE, 0.65F, 1.2F);
        hurtWithoutKnockback(zombie, level.damageSources().mobAttack(plant), scaledPlantDamage(level, plant, CITRON_DAMAGE));
        tag.putLong(NEXT_ACTION_TICK_TAG, gameTime + CITRON_INTERVAL_TICKS);
    }

    private static void tickEmPeach(ServerLevel level, SnowGolem plant) {
        AABB empArea = plant.getBoundingBox().inflate(4.0D, 2.0D, 4.0D);
        for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, empArea, entity -> entity.isAlive() && isHostileTarget(entity))) {
            int duration = isMechanicalZombie(target) ? 20 * 5 : 30;
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, duration, 8));
            target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, duration, 1));
            if (target instanceof Mob mob) {
                mob.getNavigation().stop();
            }
        }
        level.sendParticles(ParticleTypes.ELECTRIC_SPARK, plant.getX(), plant.getY() + 0.8D, plant.getZ(), 60, 2.5D, 0.8D, 2.5D, 0.08D);
        level.sendParticles(ParticleTypes.SONIC_BOOM, plant.getX(), plant.getY() + 0.8D, plant.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
        level.playSound(null, plant.blockPosition(), SoundEvents.GENERIC_EXPLODE, SoundSource.HOSTILE, 0.55F, 1.8F);
        plant.discard();
    }

    private static void tickInfiNut(ServerLevel level, SnowGolem plant) {
        long gameTime = level.getGameTime();
        CompoundTag tag = plant.getPersistentData();
        if (gameTime < tag.getLong(NEXT_ACTION_TICK_TAG)) {
            return;
        }

        tag.putLong(NEXT_ACTION_TICK_TAG, gameTime + INFI_NUT_REGEN_INTERVAL_TICKS);
        if (gameTime - tag.getLong(INFI_NUT_LAST_HURT_TICK_TAG) < INFI_NUT_RECENT_DAMAGE_TICKS) {
            return;
        }

        float maxHealth = plant.getMaxHealth();
        if (plant.getHealth() < maxHealth) {
            plant.heal(6.0F);
            level.sendParticles(ParticleTypes.HAPPY_VILLAGER, plant.getX(), plant.getY() + 1.0D, plant.getZ(), 8, 0.25D, 0.25D, 0.25D, 0.02D);
        }
    }

    private static void tickMagnifyingGrass(ServerLevel level, SnowGolem plant) {
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

        Vec3 start = plant.position().add(0.0D, 1.15D, 0.0D);
        Vec3 end = target.get().position().add(0.0D, target.get().getBbHeight() * 0.55D, 0.0D);
        Optional<Player> sunSource = nearestPlayerWithSun(level, plant, MAGNIFYING_GRASS_SUN_COST);
        if (sunSource.isEmpty() || !SunManager.spendSun(sunSource.get(), MAGNIFYING_GRASS_SUN_COST)) {
            tag.putLong(NEXT_ACTION_TICK_TAG, gameTime + 20L);
            return;
        }
        renderGuardianStyleLaser(level, start, end);
        level.sendParticles(ParticleTypes.END_ROD, end.x, end.y, end.z, 10, 0.15D, 0.15D, 0.15D, 0.03D);
        hurtWithoutKnockback(target.get(), level.damageSources().mobAttack(plant), scaledPlantDamage(level, plant, MAGNIFYING_GRASS_DAMAGE));
        tag.putLong(NEXT_ACTION_TICK_TAG, gameTime + SHOOTER_INTERVAL_TICKS);
    }

    private static void tickTileTurnip(ServerLevel level, SnowGolem plant) {
        if (FarFuturePowerTileManager.addPowerTile(level, plant.blockPosition().below())) {
            level.sendParticles(ParticleTypes.END_ROD, plant.getX(), plant.getY() + 0.5D, plant.getZ(), 18, 0.35D, 0.2D, 0.35D, 0.02D);
            plant.discard();
        }
    }

    private static void tickKernelPult(ServerLevel level, SnowGolem plant) {
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

        boolean butter = level.random.nextFloat() < 0.25F;
        Zombie zombie = target.get();
        shootLobbedSnowballVisual(level, plant, zombie, butter ? "butter" : "kernel");
        hurtWithoutKnockback(zombie, level.damageSources().mobAttack(plant), scaledPlantDamage(level, plant, butter ? BUTTER_DAMAGE : KERNEL_DAMAGE));
        if (butter) {
            zombie.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20 * 2, 8));
            zombie.getNavigation().stop();
            level.sendParticles(ParticleTypes.HAPPY_VILLAGER, zombie.getX(), zombie.getY() + 1.0D, zombie.getZ(), 10, 0.25D, 0.25D, 0.25D, 0.02D);
        }
        tag.putLong(NEXT_ACTION_TICK_TAG, gameTime + KERNEL_PULT_INTERVAL_TICKS);
    }

    private static void tickSnapdragon(ServerLevel level, SnowGolem plant) {
        long gameTime = level.getGameTime();
        CompoundTag tag = plant.getPersistentData();
        if (gameTime < tag.getLong(NEXT_ACTION_TICK_TAG)) {
            return;
        }

        Vec3 facing = facingVector(plant);
        int hits = 0;
        for (Zombie zombie : level.getEntitiesOfClass(Zombie.class, plant.getBoundingBox().inflate(4.0D, 1.0D, 4.0D), Zombie::isAlive)) {
            if (!isInFrontCone(plant, zombie, facing, 4.0D)) {
                continue;
            }
            hurtWithoutKnockback(zombie, level.damageSources().mobAttack(plant), scaledPlantDamage(level, plant, SNAPDRAGON_DAMAGE));
            hits++;
        }

        if (hits == 0) {
            tag.putLong(NEXT_ACTION_TICK_TAG, gameTime + 10L);
            return;
        }

        Vec3 start = plant.position().add(0.0D, 1.0D, 0.0D);
        for (int i = 1; i <= 8; i++) {
            Vec3 pos = start.add(facing.scale(i * 0.45D));
            level.sendParticles(ParticleTypes.FLAME, pos.x, pos.y, pos.z, 3, 0.2D, 0.12D, 0.2D, 0.015D);
        }
        level.playSound(null, plant.blockPosition(), SoundEvents.BLAZE_SHOOT, SoundSource.HOSTILE, 0.45F, 1.5F);
        tag.putLong(NEXT_ACTION_TICK_TAG, gameTime + SHOOTER_INTERVAL_TICKS);
    }

    private static void tickSpikeweed(ServerLevel level, SnowGolem plant, boolean spikerock) {
        long gameTime = level.getGameTime();
        CompoundTag tag = plant.getPersistentData();
        if (gameTime < tag.getLong(NEXT_ACTION_TICK_TAG)) {
            return;
        }

        float damage = spikerock ? SPIKEROCK_DAMAGE : SPIKEWEED_DAMAGE;
        int hits = 0;
        for (Zombie zombie : level.getEntitiesOfClass(Zombie.class, plant.getBoundingBox().inflate(0.8D, 0.35D, 0.8D), Zombie::isAlive)) {
            hurtWithoutKnockback(zombie, level.damageSources().mobAttack(plant), scaledPlantDamage(level, plant, damage));
            hits++;
        }
        if (hits > 0) {
            level.sendParticles(ParticleTypes.CRIT, plant.getX(), plant.getY() + 0.15D, plant.getZ(), 8, 0.4D, 0.08D, 0.4D, 0.01D);
        }
        tag.putLong(NEXT_ACTION_TICK_TAG, gameTime + 20L);
    }

    private static void tickSpringBean(ServerLevel level, SnowGolem plant) {
        long gameTime = level.getGameTime();
        CompoundTag tag = plant.getPersistentData();
        if (gameTime < tag.getLong(NEXT_ACTION_TICK_TAG)) {
            return;
        }

        Optional<Zombie> target = selectZombie(level, plant, 1.8D);
        if (target.isEmpty()) {
            return;
        }

        Zombie zombie = target.get();
        Vec3 push = zombie.position().subtract(plant.position()).multiply(1.0D, 0.0D, 1.0D);
        if (push.lengthSqr() < 1.0E-4D) {
            push = facingVector(plant);
        }
        push = push.normalize();
        BlockPos landing = BlockPos.containing(zombie.position().add(push.scale(3.0D)));
        zombie.setDeltaMovement(zombie.getDeltaMovement().add(push.scale(1.35D)).add(0.0D, 0.25D, 0.0D));
        zombie.getNavigation().stop();
        if (PirateSeasPlankManager.isChurningWaterHole(level, landing) || PirateSeasPlankManager.isChurningWaterHole(level, landing.below())) {
            if (isGargantuarLike(zombie)) {
                hurtWithoutKnockback(zombie, level.damageSources().mobAttack(plant), 40.0F);
            } else {
                zombie.hurt(level.damageSources().mobAttack(plant), 999.0F);
            }
            level.sendParticles(ParticleTypes.SPLASH, zombie.getX(), zombie.getY() + 0.5D, zombie.getZ(), 24, 0.6D, 0.4D, 0.6D, 0.05D);
        }
        level.playSound(null, plant.blockPosition(), SoundEvents.SLIME_JUMP, SoundSource.HOSTILE, 0.6F, 1.1F);
        tag.putLong(NEXT_ACTION_TICK_TAG, gameTime + SPRING_BEAN_COOLDOWN_TICKS);
    }

    private static void tickCoconutCannon(ServerLevel level, SnowGolem plant) {
        long gameTime = level.getGameTime();
        CompoundTag tag = plant.getPersistentData();
        if (gameTime < tag.getLong(NEXT_ACTION_TICK_TAG)) {
            return;
        }

        Optional<Zombie> target = selectZombie(level, plant, 18.0D);
        if (target.isEmpty()) {
            tag.putLong(NEXT_ACTION_TICK_TAG, gameTime + 20L);
            return;
        }

        Zombie directTarget = target.get();
        hurtWithoutKnockback(directTarget, level.damageSources().mobAttack(plant), scaledPlantDamage(level, plant, COCONUT_DIRECT_DAMAGE));
        for (Zombie zombie : level.getEntitiesOfClass(Zombie.class, directTarget.getBoundingBox().inflate(3.0D, 1.5D, 3.0D), Zombie::isAlive)) {
            if (zombie != directTarget) {
                hurtWithoutKnockback(zombie, level.damageSources().mobAttack(plant), scaledPlantDamage(level, plant, COCONUT_SPLASH_DAMAGE));
            }
        }
        shootLobbedSnowballVisual(level, plant, directTarget, "coconut_cannon");
        level.sendParticles(ParticleTypes.EXPLOSION, directTarget.getX(), directTarget.getY() + 0.7D, directTarget.getZ(), 2, 0.35D, 0.2D, 0.35D, 0.0D);
        level.playSound(null, directTarget.blockPosition(), SoundEvents.GENERIC_EXPLODE, SoundSource.HOSTILE, 0.65F, 1.0F);
        tag.putLong(NEXT_ACTION_TICK_TAG, gameTime + COCONUT_CANNON_INTERVAL_TICKS);
    }

    private static void tickThreepeater(ServerLevel level, SnowGolem plant) {
        long gameTime = level.getGameTime();
        CompoundTag tag = plant.getPersistentData();
        if (gameTime < tag.getLong(NEXT_ACTION_TICK_TAG)) {
            return;
        }

        List<Zombie> targets = level.getEntitiesOfClass(Zombie.class, plant.getBoundingBox().inflate(SHOOTER_RANGE, 3.0D, SHOOTER_RANGE), Zombie::isAlive)
                .stream()
                .sorted((first, second) -> Double.compare(plant.distanceToSqr(first), plant.distanceToSqr(second)))
                .limit(3)
                .toList();
        if (targets.isEmpty()) {
            tag.putLong(NEXT_ACTION_TICK_TAG, gameTime + 10L);
            return;
        }

        double[] offsets = {-0.2D, 0.0D, 0.2D};
        for (int i = 0; i < targets.size(); i++) {
            shootSnowball(level, plant, targets.get(i), offsets[Math.min(i, offsets.length - 1)]);
        }
        level.sendParticles(ParticleTypes.CRIT, plant.getX(), plant.getY() + 1.15D, plant.getZ(), 6, 0.18D, 0.1D, 0.18D, 0.01D);
        tag.putLong(NEXT_ACTION_TICK_TAG, gameTime + SHOOTER_INTERVAL_TICKS);
    }

    private static void tickCherryBomb(ServerLevel level, SnowGolem plant) {
        long gameTime = level.getGameTime();
        CompoundTag tag = plant.getPersistentData();
        if (gameTime < tag.getLong(NEXT_ACTION_TICK_TAG)) {
            level.sendParticles(ParticleTypes.SMOKE, plant.getX(), plant.getY() + 0.8D, plant.getZ(), 2, 0.15D, 0.15D, 0.15D, 0.01D);
            return;
        }

        AABB blast = plant.getBoundingBox().inflate(3.0D, 1.5D, 3.0D);
        for (Zombie zombie : level.getEntitiesOfClass(Zombie.class, blast, Zombie::isAlive)) {
            hurtWithoutKnockback(zombie, level.damageSources().mobAttack(plant), scaledPlantDamage(level, plant, CHERRY_BOMB_DAMAGE));
        }
        level.sendParticles(ParticleTypes.EXPLOSION, plant.getX(), plant.getY() + 0.7D, plant.getZ(), 4, 1.0D, 0.4D, 1.0D, 0.0D);
        level.playSound(null, plant.blockPosition(), SoundEvents.GENERIC_EXPLODE, SoundSource.HOSTILE, 0.8F, 1.2F);
        plant.discard();
    }

    private static void tickTangleKelp(ServerLevel level, SnowGolem plant) {
        Optional<Zombie> target = selectZombie(level, plant, 1.6D);
        if (target.isEmpty()) {
            return;
        }

        Zombie zombie = target.get();
        if (isGargantuarLike(zombie)) {
            hurtWithoutKnockback(zombie, level.damageSources().mobAttack(plant), 40.0F);
        } else {
            zombie.hurt(level.damageSources().mobAttack(plant), 999.0F);
        }
        level.sendParticles(ParticleTypes.BUBBLE, zombie.getX(), zombie.getY() + 0.4D, zombie.getZ(), 36, 0.7D, 0.5D, 0.7D, 0.04D);
        level.playSound(null, plant.blockPosition(), SoundEvents.PLAYER_SPLASH_HIGH_SPEED, SoundSource.HOSTILE, 0.7F, 0.8F);
        plant.discard();
    }

    private static void tickBowlingBulb(ServerLevel level, SnowGolem plant) {
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
        for (int bounce = 0; bounce < 3 && current != null; bounce++) {
            hurtWithoutKnockback(current, level.damageSources().mobAttack(plant), BOWLING_BULB_DAMAGE);
            shootSnowballVisual(level, plant, current, false, "bowling_bulb");
            hitTargets.add(current.getUUID());
            Zombie previous = current;
            current = level.getEntitiesOfClass(Zombie.class, previous.getBoundingBox().inflate(5.0D), Zombie::isAlive)
                    .stream()
                    .filter(zombie -> !hitTargets.contains(zombie.getUUID()))
                    .min((first, second) -> Double.compare(previous.distanceToSqr(first), previous.distanceToSqr(second)))
                    .orElse(null);
        }
        tag.putLong(NEXT_ACTION_TICK_TAG, gameTime + MELON_PULT_INTERVAL_TICKS);
    }

    private static void tickGuacodile(ServerLevel level, SnowGolem plant) {
        CompoundTag tag = plant.getPersistentData();
        long gameTime = level.getGameTime();
        if (tag.getBoolean(GUACODILE_RUSHING_TAG)) {
            tickGuacodileRush(level, plant);
            if (gameTime >= tag.getLong(GUACODILE_RUSH_END_TICK_TAG)) {
                plant.discard();
            }
            return;
        }

        Optional<Zombie> closeTarget = selectZombie(level, plant, 1.7D);
        if (closeTarget.isPresent()) {
            tag.putBoolean(GUACODILE_RUSHING_TAG, true);
            tag.putLong(GUACODILE_RUSH_END_TICK_TAG, gameTime + 20L);
            return;
        }

        if (gameTime < tag.getLong(NEXT_ACTION_TICK_TAG)) {
            return;
        }
        Optional<Zombie> target = selectZombie(level, plant, SHOOTER_RANGE);
        if (target.isEmpty()) {
            tag.putLong(NEXT_ACTION_TICK_TAG, gameTime + 10L);
            return;
        }

        shootSnowballVisual(level, plant, target.get(), false, "guacodile_seed");
        hurtWithoutKnockback(target.get(), level.damageSources().mobAttack(plant), GUACODILE_SEED_DAMAGE);
        tag.putLong(NEXT_ACTION_TICK_TAG, gameTime + SHOOTER_INTERVAL_TICKS);
    }

    private static void tickGuacodileRush(ServerLevel level, SnowGolem plant) {
        Vec3 facing = facingVector(plant);
        plant.setDeltaMovement(facing.scale(0.45D));
        for (Zombie zombie : level.getEntitiesOfClass(Zombie.class, plant.getBoundingBox().inflate(0.8D), Zombie::isAlive)) {
            hurtWithoutKnockback(zombie, level.damageSources().mobAttack(plant), GUACODILE_RUSH_DAMAGE);
        }
        level.sendParticles(ParticleTypes.SPLASH, plant.getX(), plant.getY() + 0.4D, plant.getZ(), 8, 0.35D, 0.2D, 0.35D, 0.03D);
    }

    private static void tickBananaLauncher(ServerLevel level, SnowGolem plant) {
        long gameTime = level.getGameTime();
        CompoundTag tag = plant.getPersistentData();
        if (gameTime < tag.getLong(NEXT_ACTION_TICK_TAG)) {
            return;
        }

        Optional<Zombie> target = selectZombie(level, plant, 18.0D);
        if (target.isEmpty()) {
            tag.putLong(NEXT_ACTION_TICK_TAG, gameTime + 20L);
            return;
        }

        Zombie directTarget = target.get();
        hurtWithoutKnockback(directTarget, level.damageSources().mobAttack(plant), BANANA_DIRECT_DAMAGE);
        for (Zombie zombie : level.getEntitiesOfClass(Zombie.class, directTarget.getBoundingBox().inflate(3.0D, 1.5D, 3.0D), Zombie::isAlive)) {
            if (zombie != directTarget) {
                hurtWithoutKnockback(zombie, level.damageSources().mobAttack(plant), BANANA_SPLASH_DAMAGE);
            }
        }
        shootLobbedSnowballVisual(level, plant, directTarget, "banana");
        level.sendParticles(ParticleTypes.EXPLOSION, directTarget.getX(), directTarget.getY() + 0.7D, directTarget.getZ(), 2, 0.3D, 0.2D, 0.3D, 0.0D);
        level.playSound(null, directTarget.blockPosition(), SoundEvents.GENERIC_EXPLODE, SoundSource.HOSTILE, 0.55F, 1.4F);
        tag.putLong(NEXT_ACTION_TICK_TAG, gameTime + BANANA_LAUNCHER_INTERVAL_TICKS);
    }

    private static void tickPepperPult(ServerLevel level, SnowGolem plant) {
        thawNearbyPlants(level, plant);
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
        hurtWithoutKnockback(directTarget, level.damageSources().mobAttack(plant), PEPPER_PULT_DIRECT_DAMAGE);
        for (Zombie zombie : level.getEntitiesOfClass(Zombie.class, directTarget.getBoundingBox().inflate(2.0D, 1.0D, 2.0D), Zombie::isAlive)) {
            if (zombie != directTarget) {
                hurtWithoutKnockback(zombie, level.damageSources().mobAttack(plant), PEPPER_PULT_SPLASH_DAMAGE);
            }
        }
        shootLobbedSnowballVisual(level, plant, directTarget, "pepper_pult");
        level.sendParticles(ParticleTypes.FLAME, directTarget.getX(), directTarget.getY() + 0.6D, directTarget.getZ(), 16, 0.45D, 0.25D, 0.45D, 0.02D);
        level.playSound(null, plant.blockPosition(), SoundEvents.BLAZE_SHOOT, SoundSource.HOSTILE, 0.45F, 1.3F);
        tag.putLong(NEXT_ACTION_TICK_TAG, gameTime + MELON_PULT_INTERVAL_TICKS);
    }

    private static void tickChardGuard(ServerLevel level, SnowGolem plant) {
        CompoundTag tag = plant.getPersistentData();
        int charges = Math.max(0, tag.getInt(CHARD_GUARD_CHARGES_TAG));
        if (charges <= 0 || level.getGameTime() < tag.getLong(NEXT_ACTION_TICK_TAG)) {
            return;
        }

        Vec3 facing = facingVector(plant);
        Optional<Zombie> target = level.getEntitiesOfClass(Zombie.class, plant.getBoundingBox().inflate(1.75D, 0.75D, 1.75D), Zombie::isAlive)
                .stream()
                .filter(zombie -> isInFrontCone(plant, zombie, facing, 1.75D))
                .findFirst();
        if (target.isEmpty()) {
            return;
        }

        Zombie zombie = target.get();
        Vec3 shove = zombie.position().subtract(plant.position()).multiply(1.0D, 0.0D, 1.0D);
        if (shove.lengthSqr() < 1.0E-4D) {
            shove = facing;
        }
        zombie.setDeltaMovement(zombie.getDeltaMovement().add(shove.normalize().scale(1.15D)).add(0.0D, 0.22D, 0.0D));
        zombie.getNavigation().stop();
        tag.putInt(CHARD_GUARD_CHARGES_TAG, charges - 1);
        tag.putLong(NEXT_ACTION_TICK_TAG, level.getGameTime() + 20L);
        level.sendParticles(ParticleTypes.CRIT, zombie.getX(), zombie.getY() + 0.8D, zombie.getZ(), 12, 0.35D, 0.25D, 0.35D, 0.03D);
    }

    private static void tickStunion(ServerLevel level, SnowGolem plant) {
        Optional<Zombie> trigger = selectZombie(level, plant, 1.5D);
        if (trigger.isEmpty()) {
            return;
        }

        AABB gasArea = plant.getBoundingBox().inflate(3.0D, 1.0D, 3.0D);
        for (Zombie zombie : level.getEntitiesOfClass(Zombie.class, gasArea, Zombie::isAlive)) {
            int duration = isGargantuarLike(zombie) ? 30 : 60;
            zombie.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, duration, 8));
            zombie.getNavigation().stop();
        }
        level.sendParticles(ParticleTypes.SNEEZE, plant.getX(), plant.getY() + 0.7D, plant.getZ(), 32, 1.2D, 0.45D, 1.2D, 0.04D);
        plant.discard();
    }

    private static void tickRotobaga(ServerLevel level, SnowGolem plant) {
        long gameTime = level.getGameTime();
        CompoundTag tag = plant.getPersistentData();
        if (gameTime < tag.getLong(NEXT_ACTION_TICK_TAG)) {
            return;
        }

        Vec3[] diagonals = {
                new Vec3(1.0D, 0.0D, 1.0D).normalize(),
                new Vec3(1.0D, 0.0D, -1.0D).normalize(),
                new Vec3(-1.0D, 0.0D, 1.0D).normalize(),
                new Vec3(-1.0D, 0.0D, -1.0D).normalize()
        };
        int shots = 0;
        for (Vec3 diagonal : diagonals) {
            Optional<Zombie> target = selectDiagonalZombie(level, plant, diagonal);
            if (target.isPresent()) {
                shootSnowballVisual(level, plant, target.get(), false, "rotobaga");
                hurtWithoutKnockback(target.get(), level.damageSources().mobAttack(plant), ROTOBAGA_DAMAGE);
                shots++;
            }
        }

        tag.putLong(NEXT_ACTION_TICK_TAG, gameTime + (shots == 0 ? 10L : SHOOTER_INTERVAL_TICKS));
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

    private static void tickMoonflower(ServerLevel level, SnowGolem plant) {
        long gameTime = level.getGameTime();
        CompoundTag tag = plant.getPersistentData();
        if (gameTime % 20L == 0L) {
            level.sendParticles(ParticleTypes.PORTAL, plant.getX(), plant.getY() + 0.9D, plant.getZ(), 8, 0.35D, 0.2D, 0.35D, 0.02D);
        }
        if (gameTime < tag.getLong(NEXT_ACTION_TICK_TAG)) {
            return;
        }

        SunManager.spawnSunAt(level, plant.blockPosition().above(3), 25);
        tag.putLong(NEXT_ACTION_TICK_TAG, gameTime + MOONFLOWER_INTERVAL_TICKS);
    }

    private static void tickNightshade(ServerLevel level, SnowGolem plant) {
        long gameTime = level.getGameTime();
        CompoundTag tag = plant.getPersistentData();
        boolean powered = isPoweredByMoonflower(level, plant);
        long interval = powered ? Math.max(8, NIGHTSHADE_INTERVAL_TICKS - 5) : NIGHTSHADE_INTERVAL_TICKS;
        if (gameTime < tag.getLong(NEXT_ACTION_TICK_TAG)) {
            return;
        }

        double range = powered ? 3.0D : 2.0D;
        Optional<Zombie> target = selectZombie(level, plant, range);
        if (target.isEmpty()) {
            tag.putLong(NEXT_ACTION_TICK_TAG, gameTime + 10L);
            return;
        }

        hurtWithoutKnockback(target.get(), level.damageSources().mobAttack(plant), scaledPlantDamage(level, plant, powered ? POWERED_NIGHTSHADE_DAMAGE : NIGHTSHADE_DAMAGE));
        level.sendParticles(powered ? ParticleTypes.REVERSE_PORTAL : ParticleTypes.PORTAL, target.get().getX(), target.get().getY() + 0.8D, target.get().getZ(), powered ? 18 : 9, 0.25D, 0.4D, 0.25D, 0.02D);
        tag.putLong(NEXT_ACTION_TICK_TAG, gameTime + interval);
    }

    private static void tickShadowShroom(ServerLevel level, SnowGolem plant) {
        long gameTime = level.getGameTime();
        CompoundTag tag = plant.getPersistentData();
        if (gameTime < tag.getLong(NEXT_ACTION_TICK_TAG)) {
            return;
        }

        boolean powered = isPoweredByMoonflower(level, plant);
        Optional<Zombie> target = selectZombie(level, plant, powered ? 7.0D : 5.0D);
        if (target.isEmpty()) {
            tag.putLong(NEXT_ACTION_TICK_TAG, gameTime + 15L);
            return;
        }

        applyShadowCurse(level, plant, target.get(), powered);
        if (powered) {
            AABB spreadArea = target.get().getBoundingBox().inflate(2.0D, 1.0D, 2.0D);
            for (Zombie zombie : level.getEntitiesOfClass(Zombie.class, spreadArea, Zombie::isAlive)) {
                if (zombie != target.get()) {
                    applyShadowCurse(level, plant, zombie, false);
                }
            }
        }
        level.sendParticles(ParticleTypes.WITCH, plant.getX(), plant.getY() + 0.8D, plant.getZ(), powered ? 24 : 12, 0.35D, 0.25D, 0.35D, 0.03D);
        tag.putLong(NEXT_ACTION_TICK_TAG, gameTime + SHADOW_SHROOM_INTERVAL_TICKS);
    }

    private static void applyShadowCurse(ServerLevel level, SnowGolem plant, Zombie zombie, boolean powered) {
        zombie.addEffect(new MobEffectInstance(MobEffects.WITHER, SHADOW_SHROOM_CURSE_TICKS, powered ? 1 : 0));
        hurtWithoutKnockback(zombie, level.damageSources().mobAttack(plant), scaledPlantDamage(level, plant, powered ? SHADOW_SHROOM_DAMAGE * 1.5F : SHADOW_SHROOM_DAMAGE));
    }

    private static void tickDuskLobber(ServerLevel level, SnowGolem plant) {
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

        boolean powered = isPoweredByMoonflower(level, plant);
        Zombie directTarget = target.get();
        double splashRadius = powered ? 3.0D : 2.0D;
        hurtWithoutKnockback(directTarget, level.damageSources().mobAttack(plant), scaledPlantDamage(level, plant, DUSK_LOBBER_DIRECT_DAMAGE));
        for (Zombie zombie : level.getEntitiesOfClass(Zombie.class, directTarget.getBoundingBox().inflate(splashRadius, 1.0D, splashRadius), Zombie::isAlive)) {
            if (zombie != directTarget) {
                hurtWithoutKnockback(zombie, level.damageSources().mobAttack(plant), scaledPlantDamage(level, plant, powered ? POWERED_DUSK_LOBBER_SPLASH_DAMAGE : DUSK_LOBBER_SPLASH_DAMAGE));
            }
        }
        shootLobbedSnowballVisual(level, plant, directTarget, powered ? "powered_shadow_lob" : "shadow_lob");
        level.sendParticles(ParticleTypes.DRAGON_BREATH, directTarget.getX(), directTarget.getY() + 0.8D, directTarget.getZ(), powered ? 30 : 16, splashRadius * 0.25D, 0.35D, splashRadius * 0.25D, 0.03D);
        tag.putLong(NEXT_ACTION_TICK_TAG, gameTime + DUSK_LOBBER_INTERVAL_TICKS);
    }

    private static void tickGrimrose(ServerLevel level, SnowGolem plant) {
        long gameTime = level.getGameTime();
        CompoundTag tag = plant.getPersistentData();
        if (gameTime < tag.getLong(NEXT_ACTION_TICK_TAG)) {
            return;
        }

        boolean powered = isPoweredByMoonflower(level, plant);
        List<Zombie> targets = level.getEntitiesOfClass(Zombie.class, plant.getBoundingBox().inflate(powered ? 4.0D : 2.5D, 1.0D, powered ? 4.0D : 2.5D), Zombie::isAlive)
                .stream()
                .sorted((first, second) -> Double.compare(plant.distanceToSqr(first), plant.distanceToSqr(second)))
                .limit(powered ? 2 : 1)
                .toList();
        if (targets.isEmpty()) {
            tag.putLong(NEXT_ACTION_TICK_TAG, gameTime + 10L);
            return;
        }

        for (Zombie zombie : targets) {
            hurtWithoutKnockback(zombie, level.damageSources().mobAttack(plant), GRIMROSE_DAMAGE);
            level.sendParticles(ParticleTypes.REVERSE_PORTAL, zombie.getX(), zombie.getY() + 0.6D, zombie.getZ(), 28, 0.35D, 0.5D, 0.35D, 0.05D);
        }
        level.playSound(null, plant.blockPosition(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.HOSTILE, 0.7F, 0.7F);
        plant.discard();
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
        Optional<Zombie> focusedTarget = TargetingPriorityManager.selectFocusedTarget(level, zombies);
        if (focusedTarget.isPresent()) {
            return focusedTarget;
        }
        return TargetingPriorityManager.selectTarget(zombies, plant, priorityFor(level, plant));
    }

    public static void tickFrostbiteFreeze(ServerLevel level, BlockPos totemPos, int radius, boolean heavySnowfallActive) {
        AABB area = new AABB(totemPos).inflate(radius + 1.0D, 4.0D, radius + 1.0D);
        for (SnowGolem plant : level.getEntitiesOfClass(SnowGolem.class, area, plant -> plant.isAlive() && isPlant(plant))) {
            tickPlantFreezeState(level, plant, heavySnowfallActive);
        }
    }

    private static void tickPlantFreezeState(ServerLevel level, SnowGolem plant, boolean heavySnowfallActive) {
        PlantSeedDefinition.PlantBehavior behavior = behaviorFor(plant);
        if (isHotPlant(behavior)) {
            if (getFreezeStage(plant) > 0) {
                thawPlant(level, plant);
            }
            return;
        }

        boolean warmed = isPlantWarmedByHotPlant(level, plant);
        CompoundTag tag = plant.getPersistentData();
        long gameTime = level.getGameTime();
        long nextChange = tag.getLong(FREEZE_NEXT_STAGE_TICK_TAG);
        if (gameTime < nextChange) {
            syncFreezeOverlay(level, plant);
            return;
        }

        int stage = getFreezeStage(plant);
        if (heavySnowfallActive && !warmed) {
            if (stage < 3) {
                setFreezeStage(level, plant, stage + 1);
                level.sendParticles(ParticleTypes.SNOWFLAKE, plant.getX(), plant.getY() + 1.0D, plant.getZ(), 12, 0.35D, 0.45D, 0.35D, 0.02D);
            }
            tag.putLong(FREEZE_NEXT_STAGE_TICK_TAG, gameTime + FREEZE_STAGE_INTERVAL_TICKS);
        } else if (stage > 0) {
            setFreezeStage(level, plant, stage - 1);
            tag.putLong(FREEZE_NEXT_STAGE_TICK_TAG, gameTime + (warmed ? WARMED_THAW_INTERVAL_TICKS : FREEZE_DECAY_INTERVAL_TICKS));
        } else {
            cleanupFreezeOverlay(level, plant);
            tag.putLong(FREEZE_NEXT_STAGE_TICK_TAG, gameTime + 20L);
        }
    }

    private static boolean thawPlantNear(ServerPlayer player, BlockPos pos) {
        Optional<SnowGolem> plant = findAnyPlantAt(player.serverLevel(), pos)
                .or(() -> findAnyPlantAt(player.serverLevel(), pos.above()))
                .filter(candidate -> getFreezeStage(candidate) > 0);
        if (plant.isEmpty() || getFreezeStage(plant.get()) <= 0) {
            player.displayClientMessage(Component.literal("No frozen plant to thaw.").withStyle(ChatFormatting.AQUA), true);
            return false;
        }

        thawPlant(player.serverLevel(), plant.get());
        player.serverLevel().sendParticles(ParticleTypes.FLAME, plant.get().getX(), plant.get().getY() + 0.8D, plant.get().getZ(), 12, 0.35D, 0.35D, 0.35D, 0.02D);
        player.displayClientMessage(Component.literal("Plant thawed.").withStyle(ChatFormatting.AQUA), true);
        return true;
    }

    private static Optional<SnowGolem> findAnyPlantAt(ServerLevel level, BlockPos pos) {
        AABB area = new AABB(pos).inflate(0.45D, 0.9D, 0.45D);
        return level.getEntitiesOfClass(SnowGolem.class, area, plant -> isPlant(plant) && plant.isAlive())
                .stream()
                .findFirst();
    }

    private static void thawNearbyPlants(ServerLevel level, SnowGolem hotPlant) {
        if (level.getGameTime() % WARMED_THAW_INTERVAL_TICKS != 0L) {
            return;
        }

        for (SnowGolem plant : level.getEntitiesOfClass(SnowGolem.class, hotPlant.getBoundingBox().inflate(PEPPER_WARM_RADIUS), PlantEntityManager::isPlant)) {
            int stage = getFreezeStage(plant);
            if (stage > 0) {
                setFreezeStage(level, plant, stage - 1);
            }
        }
    }

    private static boolean isPlantFrozen(SnowGolem plant) {
        return getFreezeStage(plant) >= 3;
    }

    private static int getFreezeStage(Entity plant) {
        return Math.max(0, Math.min(3, plant.getPersistentData().getInt(FREEZE_STAGE_TAG)));
    }

    private static void setFreezeStage(ServerLevel level, SnowGolem plant, int stage) {
        int clamped = Math.max(0, Math.min(3, stage));
        plant.getPersistentData().putInt(FREEZE_STAGE_TAG, clamped);
        syncFreezeOverlay(level, plant);
    }

    private static void thawPlant(ServerLevel level, SnowGolem plant) {
        plant.getPersistentData().putInt(FREEZE_STAGE_TAG, 0);
        plant.getPersistentData().putLong(FREEZE_NEXT_STAGE_TICK_TAG, level.getGameTime() + 20L);
        cleanupFreezeOverlay(level, plant);
    }

    public static boolean addHunterFreezeStage(ServerLevel level, SnowGolem plant) {
        if (!isPlant(plant) || isHotPlant(behaviorFor(plant))) {
            level.sendParticles(ParticleTypes.SMOKE, plant.getX(), plant.getY() + 0.9D, plant.getZ(), 6, 0.25D, 0.25D, 0.25D, 0.01D);
            return false;
        }

        int stage = getFreezeStage(plant);
        if (stage >= 3) {
            syncFreezeOverlay(level, plant);
            return false;
        }

        setFreezeStage(level, plant, stage + 1);
        plant.getPersistentData().putLong(FREEZE_NEXT_STAGE_TICK_TAG, level.getGameTime() + FREEZE_STAGE_INTERVAL_TICKS);
        level.sendParticles(ParticleTypes.SNOWFLAKE, plant.getX(), plant.getY() + 1.0D, plant.getZ(), 14, 0.35D, 0.45D, 0.35D, 0.02D);
        return true;
    }

    public static boolean isHotPlantEntity(Entity entity) {
        return entity instanceof SnowGolem plant && isPlant(plant) && isHotPlant(behaviorFor(plant));
    }

    public static void applyWizardDisable(ServerLevel level, SnowGolem plant, Entity wizard, int durationTicks) {
        if (!isPlant(plant)) {
            return;
        }
        plant.getPersistentData().putBoolean(WIZARD_DISABLED_TAG, true);
        plant.getPersistentData().putLong(WIZARD_DISABLED_END_TICK_TAG, level.getGameTime() + durationTicks);
        level.sendParticles(ParticleTypes.ENCHANT, plant.getX(), plant.getY() + 1.0D, plant.getZ(), 18, 0.35D, 0.45D, 0.35D, 0.04D);
        level.playSound(null, plant.blockPosition(), SoundEvents.SHEEP_AMBIENT, SoundSource.HOSTILE, 0.7F, 1.25F);
    }

    public static void clearWizardDisable(Entity plant) {
        plant.getPersistentData().remove(WIZARD_DISABLED_TAG);
        plant.getPersistentData().remove(WIZARD_DISABLED_END_TICK_TAG);
    }

    public static boolean isWizardDisabled(ServerLevel level, Entity plant) {
        CompoundTag tag = plant.getPersistentData();
        if (!tag.getBoolean(WIZARD_DISABLED_TAG)) {
            return false;
        }
        if (level.getGameTime() >= tag.getLong(WIZARD_DISABLED_END_TICK_TAG)) {
            clearWizardDisable(plant);
            return false;
        }
        return true;
    }

    public static void applyOctoDisable(ServerLevel level, SnowGolem plant, Entity octoZombie, int durationTicks) {
        if (!isPlant(plant)) {
            return;
        }
        plant.getPersistentData().putBoolean(OCTO_DISABLED_TAG, true);
        plant.getPersistentData().putLong(OCTO_DISABLED_END_TICK_TAG, level.getGameTime() + durationTicks);
        level.sendParticles(ParticleTypes.SQUID_INK, plant.getX(), plant.getY() + 1.0D, plant.getZ(), 22, 0.35D, 0.45D, 0.35D, 0.04D);
        level.playSound(null, plant.blockPosition(), SoundEvents.SQUID_SQUIRT, SoundSource.HOSTILE, 0.7F, 1.0F);
    }

    public static void clearOctoDisable(Entity plant) {
        plant.getPersistentData().remove(OCTO_DISABLED_TAG);
        plant.getPersistentData().remove(OCTO_DISABLED_END_TICK_TAG);
    }

    public static boolean isOctoDisabled(ServerLevel level, Entity plant) {
        CompoundTag tag = plant.getPersistentData();
        if (!tag.getBoolean(OCTO_DISABLED_TAG)) {
            return false;
        }
        if (level.getGameTime() >= tag.getLong(OCTO_DISABLED_END_TICK_TAG)) {
            clearOctoDisable(plant);
            return false;
        }
        return true;
    }

    private static boolean isPlantWarmedByHotPlant(ServerLevel level, SnowGolem plant) {
        return level.getEntitiesOfClass(SnowGolem.class, plant.getBoundingBox().inflate(PEPPER_WARM_RADIUS), other -> other != plant && isPlant(other) && isHotPlant(behaviorFor(other)))
                .stream()
                .findAny()
                .isPresent();
    }

    private static boolean isHotPlant(PlantSeedDefinition.PlantBehavior behavior) {
        return behavior == PlantSeedDefinition.PlantBehavior.HOT_POTATO
                || behavior == PlantSeedDefinition.PlantBehavior.FIRE_PEASHOOTER
                || behavior == PlantSeedDefinition.PlantBehavior.PEPPER_PULT
                || behavior == PlantSeedDefinition.PlantBehavior.WASABI_WHIP
                || behavior == PlantSeedDefinition.PlantBehavior.TORCHWOOD;
    }

    private static boolean isShadowPlant(PlantSeedDefinition.PlantBehavior behavior) {
        return behavior == PlantSeedDefinition.PlantBehavior.MOONFLOWER
                || behavior == PlantSeedDefinition.PlantBehavior.NIGHTSHADE
                || behavior == PlantSeedDefinition.PlantBehavior.SHADOW_SHROOM
                || behavior == PlantSeedDefinition.PlantBehavior.DUSK_LOBBER
                || behavior == PlantSeedDefinition.PlantBehavior.GRIMROSE;
    }

    private static boolean isPoweredByMoonflower(ServerLevel level, SnowGolem plant) {
        if (!isShadowPlant(behaviorFor(plant))) {
            return false;
        }
        return level.getEntitiesOfClass(SnowGolem.class, plant.getBoundingBox().inflate(3.0D), other ->
                        other != plant
                                && other.isAlive()
                                && isPlant(other)
                                && behaviorFor(other) == PlantSeedDefinition.PlantBehavior.MOONFLOWER)
                .stream()
                .findAny()
                .isPresent();
    }

    private static void syncFreezeOverlay(ServerLevel level, SnowGolem plant) {
        if (getFreezeStage(plant) < 3) {
            cleanupFreezeOverlay(level, plant);
            return;
        }

        CompoundTag tag = plant.getPersistentData();
        Entity existing = tag.hasUUID(FREEZE_OVERLAY_UUID_TAG) ? level.getEntity(tag.getUUID(FREEZE_OVERLAY_UUID_TAG)) : null;
        Display.BlockDisplay overlay = existing instanceof Display.BlockDisplay blockDisplay ? blockDisplay : null;
        if (overlay == null) {
            overlay = EntityType.BLOCK_DISPLAY.create(level);
            if (overlay == null) {
                return;
            }
            overlay.load(createIceOverlayTag());
            overlay.setNoGravity(true);
            level.addFreshEntity(overlay);
            tag.putUUID(FREEZE_OVERLAY_UUID_TAG, overlay.getUUID());
        }
        overlay.setPos(plant.getX() - 0.45D, plant.getY(), plant.getZ() - 0.45D);
    }

    private static void cleanupFreezeOverlay(ServerLevel level, Entity plant) {
        CompoundTag tag = plant.getPersistentData();
        if (!tag.hasUUID(FREEZE_OVERLAY_UUID_TAG)) {
            return;
        }

        Entity overlay = level.getEntity(tag.getUUID(FREEZE_OVERLAY_UUID_TAG));
        if (overlay != null) {
            overlay.discard();
        }
        tag.remove(FREEZE_OVERLAY_UUID_TAG);
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

    private static boolean isFlyingZombie(LivingEntity entity) {
        return entity.getPersistentData().getBoolean(FLYING_ZOMBIE_TAG) || !entity.onGround();
    }

    private static boolean isMechanicalZombie(LivingEntity entity) {
        return entity.getPersistentData().getBoolean(MECHANICAL_ZOMBIE_TAG);
    }

    private static boolean canPlaceAquaticPlant(ServerLevel level, BlockPos targetPos) {
        return isWaterOrFlooded(level, targetPos);
    }

    public static boolean isWaterOrFlooded(ServerLevel level, BlockPos pos) {
        return level.getBlockState(pos).is(Blocks.WATER)
                || BigWaveBeachTideManager.isTileFlooded(level, pos)
                || BigWaveBeachTideManager.isTileFlooded(level, pos.below());
    }

    public static boolean hasLilyPadSupport(ServerLevel level, BlockPos pos) {
        return findPlantAt(level, pos, PlantSeedDefinition.PlantBehavior.LILY_PAD).isPresent()
                || findPlantAt(level, pos.below(), PlantSeedDefinition.PlantBehavior.LILY_PAD).isPresent();
    }

    public static boolean isAquaticPlant(PlantSeedDefinition.PlantBehavior behavior) {
        return behavior == PlantSeedDefinition.PlantBehavior.LILY_PAD
                || behavior == PlantSeedDefinition.PlantBehavior.TANGLE_KELP;
    }

    public static boolean isAquaticPlant(Entity plant) {
        return isPlant(plant) && isAquaticPlant(behaviorFor(plant));
    }

    public static boolean isAmphibiousPlant(PlantSeedDefinition.PlantBehavior behavior) {
        return behavior == PlantSeedDefinition.PlantBehavior.GUACODILE;
    }

    public static boolean isAmphibiousPlant(Entity plant) {
        return isPlant(plant) && isAmphibiousPlant(behaviorFor(plant));
    }

    public static boolean canFishermanHookPlant(Entity plant) {
        return plant instanceof SnowGolem
                && isPlant(plant)
                && !isAquaticPlant(plant)
                && !isAmphibiousPlant(plant)
                && behaviorFor(plant) != PlantSeedDefinition.PlantBehavior.LILY_PAD;
    }

    public static boolean isRecentlyHooked(ServerLevel level, Entity plant) {
        return plant.getPersistentData().getLong(RECENTLY_HOOKED_END_TICK_TAG) > level.getGameTime();
    }

    public static void setRecentlyHooked(ServerLevel level, Entity plant, int durationTicks) {
        plant.getPersistentData().putLong(RECENTLY_HOOKED_END_TICK_TAG, level.getGameTime() + durationTicks);
    }

    public static Optional<BlockPos> findNearestHookWaterDestination(ServerLevel level, Entity plant, Entity fisherman) {
        BlockPos origin = plant.blockPosition();
        Vec3 pullDirection = fisherman.position().subtract(plant.position()).multiply(1.0D, 0.0D, 1.0D);
        double bestScore = Double.MAX_VALUE;
        BlockPos best = null;
        for (int radius = 1; radius <= 6; radius++) {
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (Math.abs(dx) != radius && Math.abs(dz) != radius) {
                        continue;
                    }
                    BlockPos candidate = origin.offset(dx, 0, dz);
                    if (!isWaterOrFlooded(level, candidate) || hasLilyPadSupport(level, candidate)) {
                        continue;
                    }
                    if (!level.getBlockState(candidate).getCollisionShape(level, candidate).isEmpty()
                            || !level.getBlockState(candidate.above()).getCollisionShape(level, candidate.above()).isEmpty()) {
                        continue;
                    }
                    Vec3 toCandidate = Vec3.atCenterOf(candidate).subtract(plant.position()).multiply(1.0D, 0.0D, 1.0D);
                    if (pullDirection.lengthSqr() > 1.0E-4D && toCandidate.lengthSqr() > 1.0E-4D
                            && pullDirection.normalize().dot(toCandidate.normalize()) < -0.35D) {
                        continue;
                    }
                    double score = candidate.distSqr(origin) + candidate.distSqr(fisherman.blockPosition()) * 0.15D;
                    if (score < bestScore) {
                        bestScore = score;
                        best = candidate;
                    }
                }
            }
            if (best != null) {
                return Optional.of(best);
            }
        }
        return Optional.empty();
    }

    public static boolean pullPlantTowardWater(ServerLevel level, SnowGolem plant, BlockPos destination) {
        if (!canFishermanHookPlant(plant) || !isWaterOrFlooded(level, destination)) {
            return false;
        }
        if (!level.getBlockState(destination).getCollisionShape(level, destination).isEmpty()
                || !level.getBlockState(destination.above()).getCollisionShape(level, destination.above()).isEmpty()) {
            return false;
        }
        plant.moveTo(destination.getX() + 0.5D, destination.getY(), destination.getZ() + 0.5D, plant.getYRot(), plant.getXRot());
        applyHookedSubmergedState(level, plant);
        return true;
    }

    public static void applyHookedSubmergedState(ServerLevel level, SnowGolem plant) {
        if (!canFishermanHookPlant(plant)) {
            return;
        }
        CompoundTag tag = plant.getPersistentData();
        tag.putBoolean(SUBMERGED_TAG, true);
        tag.putLong(SUBMERGED_START_TICK_TAG, level.getGameTime());
        tag.putLong(NEXT_DROWNING_DAMAGE_TICK_TAG, level.getGameTime() + SUBMERGED_GRACE_TICKS);
        setRecentlyHooked(level, plant, 20 * 5);
        plant.setCustomName(Component.literal(plant.getDisplayName().getString().replace(" (Submerged)", "") + " (Submerged)").withStyle(ChatFormatting.AQUA));
        level.sendParticles(ParticleTypes.FISHING, plant.getX(), plant.getY() + 0.8D, plant.getZ(), 18, 0.35D, 0.35D, 0.35D, 0.03D);
    }

    public static boolean isPlantSubmerged(Entity plant) {
        return isPlant(plant) && plant.getPersistentData().getBoolean(SUBMERGED_TAG);
    }

    private static boolean tickSubmergedPlantDrowning(ServerLevel level, SnowGolem plant) {
        PlantSeedDefinition.PlantBehavior behavior = behaviorFor(plant);
        if (isAquaticPlant(behavior) || isAmphibiousPlant(behavior)) {
            clearSubmergedState(plant);
            return false;
        }

        BlockPos pos = plant.blockPosition();
        boolean flooded = isWaterOrFlooded(level, pos) || isWaterOrFlooded(level, pos.below());
        boolean supported = hasLilyPadSupport(level, pos) || hasLilyPadSupport(level, pos.below());
        CompoundTag tag = plant.getPersistentData();
        if (!flooded || supported) {
            clearSubmergedState(plant);
            return false;
        }

        long gameTime = level.getGameTime();
        if (!tag.getBoolean(SUBMERGED_TAG)) {
            tag.putBoolean(SUBMERGED_TAG, true);
            tag.putLong(SUBMERGED_START_TICK_TAG, gameTime);
            tag.putLong(NEXT_DROWNING_DAMAGE_TICK_TAG, gameTime + SUBMERGED_GRACE_TICKS);
            plant.setCustomName(Component.literal(plant.getDisplayName().getString() + " (Submerged)").withStyle(ChatFormatting.AQUA));
        }

        if (gameTime >= tag.getLong(NEXT_DROWNING_DAMAGE_TICK_TAG)) {
            tag.putBoolean(ALLOW_DROWNING_DAMAGE_TAG, true);
            plant.hurt(level.damageSources().drown(), 2.0F);
            tag.putLong(NEXT_DROWNING_DAMAGE_TICK_TAG, gameTime + DROWNING_DAMAGE_INTERVAL_TICKS);
        }
        if (gameTime % 20L == 0L) {
            level.sendParticles(ParticleTypes.BUBBLE, plant.getX(), plant.getY() + 0.8D, plant.getZ(), 8, 0.25D, 0.25D, 0.25D, 0.02D);
        }
        return true;
    }

    private static void clearSubmergedState(SnowGolem plant) {
        CompoundTag tag = plant.getPersistentData();
        if (!tag.getBoolean(SUBMERGED_TAG)) {
            return;
        }
        tag.remove(SUBMERGED_TAG);
        tag.remove(SUBMERGED_START_TICK_TAG);
        tag.remove(NEXT_DROWNING_DAMAGE_TICK_TAG);
        String plantId = tag.getString(PLANT_ID_TAG);
        PlantSeedDefinition.getByPlantId(plantId).ifPresent(definition ->
                plant.setCustomName(Component.literal(definition.displayName()).withStyle(style -> style.withColor(TextColor.fromRgb(definition.gardenColor())))));
    }

    private static Optional<Player> nearestPlayerWithSun(ServerLevel level, SnowGolem plant, int sunCost) {
        AABB area = plant.getBoundingBox().inflate(64.0D);
        return level.getEntitiesOfClass(Player.class, area, player -> player.isAlive() && SunManager.getSun(player) >= sunCost)
                .stream()
                .min((first, second) -> Double.compare(plant.distanceToSqr(first), plant.distanceToSqr(second)));
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

    private static Optional<Zombie> selectDiagonalZombie(ServerLevel level, SnowGolem plant, Vec3 diagonal) {
        AABB area = plant.getBoundingBox().inflate(ROTOBAGA_RANGE, 3.0D, ROTOBAGA_RANGE);
        List<Zombie> zombies = level.getEntitiesOfClass(Zombie.class, area, Zombie::isAlive)
                .stream()
                .filter(zombie -> {
                    Vec3 toZombie = zombie.position().subtract(plant.position()).multiply(1.0D, 0.0D, 1.0D);
                    if (toZombie.lengthSqr() < 1.0E-4D || toZombie.length() > ROTOBAGA_RANGE) {
                        return false;
                    }
                    return toZombie.normalize().dot(diagonal) >= 0.9D;
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

    private static boolean isGargantuarLike(Mob mob) {
        return PvZZombieDefinitions.isGargantuarLike(mob);
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

    private static boolean isEnvironmentalPlaceholderDamage(DamageSource source) {
        return source.getEntity() == null && source.getDirectEntity() == null;
    }

    private static boolean hurtWithoutKnockback(LivingEntity target, DamageSource source, float amount) {
        Vec3 movement = target.getDeltaMovement();
        boolean hurt = target.hurt(source, amount);
        target.setDeltaMovement(movement);
        return hurt;
    }

    private static float scaledPlantDamage(ServerLevel level, SnowGolem plant, float amount) {
        return amount * FarFuturePowerTileManager.getDamageMultiplier(level, plant);
    }

    private static long adjustedCooldown(SnowGolem plant, long baseCooldownTicks) {
        return Math.max(1L, Math.round(baseCooldownTicks / getPlantAttackSpeedMultiplier(plant)));
    }

    private static CompoundTag createIceOverlayTag() {
        CompoundTag tag = new CompoundTag();
        tag.put("block_state", NbtUtils.writeBlockState(Blocks.ICE.defaultBlockState()));

        CompoundTag transformation = new CompoundTag();
        transformation.put("translation", floatList(0.0F, 0.0F, 0.0F));
        transformation.put("scale", floatList(0.9F, 1.7F, 0.9F));
        transformation.put("left_rotation", floatList(0.0F, 0.0F, 0.0F, 1.0F));
        transformation.put("right_rotation", floatList(0.0F, 0.0F, 0.0F, 1.0F));
        tag.put("transformation", transformation);

        tag.putFloat("view_range", 32.0F);
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

    private static void shootSnowball(ServerLevel level, SnowGolem plant, LivingEntity target, double sideOffset) {
        boolean buffed = hasTorchwoodBetween(level, plant.position(), target.position());
        Snowball snowball = createProjectileVisual(level, plant, "pea");
        Vec3 start = plant.position().add(0.0D, 1.25D, 0.0D);
        Vec3 direction = horizontalDirectionTo(plant, target);
        Vec3 side = new Vec3(-direction.z, 0.0D, direction.x).normalize().scale(sideOffset);
        snowball.setPos(start.x + side.x, start.y, start.z + side.z);
        snowball.shoot(direction.x, 0.0D, direction.z, PEA_PROJECTILE_SPEED, 0.0F);
        if (buffed) {
            snowball.getPersistentData().putBoolean(TORCHWOOD_BUFFED_TAG, true);
            snowball.setSecondsOnFire(2);
        }
        snowball.getPersistentData().putBoolean(PLANT_PROJECTILE_TAG, true);
        snowball.getPersistentData().putString(PROJECTILE_KIND_TAG, "pea");
        level.addFreshEntity(snowball);
        plant.swing(InteractionHand.MAIN_HAND, true);

        DamageSource source = level.damageSources().mobProjectile(snowball, plant);
        hurtWithoutKnockback(target, source, scaledPlantDamage(level, plant, buffed ? PEA_DAMAGE * 2.0F : PEA_DAMAGE));
    }

    private static void shootSnowballVisual(ServerLevel level, SnowGolem plant, LivingEntity target, boolean buffed) {
        shootSnowballVisual(level, plant, target, buffed, "snowball");
    }

    private static void shootSnowballVisual(ServerLevel level, SnowGolem plant, LivingEntity target, boolean buffed, String projectileKind) {
        Snowball snowball = createProjectileVisual(level, plant, projectileKind);
        Vec3 start = plant.position().add(0.0D, 1.25D, 0.0D);
        Vec3 direction = horizontalDirectionTo(plant, target);
        snowball.setPos(start.x, start.y, start.z);
        snowball.shoot(direction.x, 0.0D, direction.z, BASIC_PROJECTILE_SPEED, 0.0F);
        snowball.getPersistentData().putBoolean(PLANT_PROJECTILE_TAG, true);
        snowball.getPersistentData().putString(PROJECTILE_KIND_TAG, projectileKind);
        if (buffed) {
            snowball.getPersistentData().putBoolean(TORCHWOOD_BUFFED_TAG, true);
            snowball.setSecondsOnFire(2);
        }
        level.addFreshEntity(snowball);
        if (usesPeaProjectileVisual(projectileKind)) {
            plant.swing(InteractionHand.MAIN_HAND, true);
        }
    }

    private static void shootLobbedSnowballVisual(ServerLevel level, SnowGolem plant, LivingEntity target, String projectileKind) {
        Snowball snowball = new Snowball(level, plant);
        Vec3 start = plant.position().add(0.0D, 1.45D, 0.0D);
        Vec3 targetPos = target.position().add(0.0D, target.getBbHeight() * 0.45D, 0.0D);
        Vec3 delta = targetPos.subtract(start);
        Vec3 horizontal = new Vec3(delta.x, 0.0D, delta.z);
        double distance = Math.max(1.0D, horizontal.length());
        Vec3 direction = horizontal.normalize();
        double speed = Mth.clamp(distance / LOBBED_PROJECTILE_DISTANCE_DIVISOR, LOBBED_PROJECTILE_MIN_SPEED, LOBBED_PROJECTILE_MAX_SPEED);
        double verticalLift = Mth.clamp(0.35D + distance * 0.035D, 0.45D, 0.95D);
        snowball.setPos(start.x, start.y, start.z);
        snowball.shoot(direction.x, verticalLift, direction.z, (float) speed, 0.0F);
        snowball.getPersistentData().putBoolean(PLANT_PROJECTILE_TAG, true);
        snowball.getPersistentData().putString(PROJECTILE_KIND_TAG, projectileKind);
        level.addFreshEntity(snowball);
        level.sendParticles(ParticleTypes.POOF, start.x, start.y, start.z, 4, 0.12D, 0.12D, 0.12D, 0.01D);
    }

    private static Snowball createProjectileVisual(ServerLevel level, SnowGolem plant, String projectileKind) {
        if (usesPeaProjectileVisual(projectileKind)) {
            return new PeaProjectileEntity(level, plant);
        }
        return new Snowball(level, plant);
    }

    private static boolean usesPeaProjectileVisual(String projectileKind) {
        return "pea".equals(projectileKind) || "primal_pea".equals(projectileKind) || "fire_pea".equals(projectileKind);
    }

    private static void renderGuardianStyleLaser(ServerLevel level, Vec3 start, Vec3 end) {
        renderBeam(level, start, end, ParticleTypes.END_ROD);
        renderBeam(level, start, end, ParticleTypes.ELECTRIC_SPARK);
        level.playSound(null, BlockPos.containing(start), SoundEvents.GUARDIAN_ATTACK, SoundSource.HOSTILE, 0.65F, 1.6F);
    }

    private static void renderBeam(ServerLevel level, Vec3 start, Vec3 end, ParticleOptions particle) {
        Vec3 delta = end.subtract(start);
        double length = delta.length();
        if (length <= 0.01D) {
            return;
        }

        Vec3 step = delta.normalize().scale(0.35D);
        int points = Math.max(1, Mth.ceil(length / 0.35D));
        for (int index = 0; index <= points; index++) {
            Vec3 pos = start.add(step.scale(index));
            level.sendParticles(particle, pos.x, pos.y, pos.z, 1, 0.01D, 0.01D, 0.01D, 0.0D);
        }
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

    private static Vec3 horizontalDirectionTo(SnowGolem plant, LivingEntity target) {
        Vec3 direction = target.position().subtract(plant.position()).multiply(1.0D, 0.0D, 1.0D);
        return direction.lengthSqr() < 1.0E-4D ? facingVector(plant) : direction.normalize();
    }

    private static float maxHealthFor(PlantSeedDefinition.PlantBehavior behavior) {
        return switch (behavior) {
            case WALL_NUT -> WALL_NUT_HEALTH;
            case PEA_NUT -> 65.0F;
            case HOT_DATE -> 55.0F;
            case PRIMAL_WALL_NUT -> PRIMAL_WALL_NUT_HEALTH;
            case TALL_NUT -> TALL_NUT_HEALTH;
            case ENDURIAN -> ENDURIAN_HEALTH;
            case INFI_NUT -> INFI_NUT_HEALTH;
            case SPIKEROCK -> 80.0F;
            case SPIKEWEED -> 35.0F;
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

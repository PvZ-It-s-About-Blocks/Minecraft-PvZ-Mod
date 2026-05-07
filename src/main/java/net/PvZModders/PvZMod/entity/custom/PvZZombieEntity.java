package net.PvZModders.PvZMod.entity.custom;

import net.PvZModders.PvZMod.entity.ModEntities;
import net.PvZModders.PvZMod.progression.beach.BigWaveBeachTideManager;
import net.PvZModders.PvZMod.progression.pirate.PirateSeasPlankManager;
import net.PvZModders.PvZMod.progression.seed.PlantEntityManager;
import net.PvZModders.PvZMod.progression.sun.SunManager;
import net.PvZModders.PvZMod.progression.waves.AncientEgyptTombManager;
import net.PvZModders.PvZMod.progression.zombies.PvZZombieDefinition;
import net.PvZModders.PvZMod.progression.zombies.PvZZombieDefinitions;
import net.PvZModders.PvZMod.progression.zombies.PvZZombieSpecial;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.Optional;

public class PvZZombieEntity extends Zombie {
    private static final String CONFIGURED_TAG = "PvZZombieConfigured";
    private static final String WAVE_BASE_SPEED_TAG = "PvZWaveBaseSpeed";
    private static final String NEWSPAPER_RAGED_TAG = "PvZNewspaperRaged";
    private static final String POLE_VAULT_READY_TAG = "PvZPoleVaultReady";
    private static final String RA_NEXT_DRAIN_TICK_TAG = "PvZRaNextDrainTick";
    private static final String TOMB_NEXT_RAISE_TICK_TAG = "PvZTombNextRaiseTick";
    private static final String TOMB_RAISED_COUNT_TAG = "PvZTombRaisedCount";
    public static final String PROSPECTOR_LEAPED_TAG = "PvZProspectorLeaped";
    public static final String CHICKEN_WRANGLER_RELEASED_TAG = "PvZChickenWranglerReleased";
    public static final String WEASEL_HOARDER_RELEASED_TAG = "PvZWeaselHoarderReleased";
    public static final String PONCHO_SHIELD_ACTIVE_TAG = "PvZPonchoShieldActive";
    public static final String HUNTER_FREEZE_SHOT_TAG = "PvZHunterFreezeShot";
    public static final String RELIC_HUNTER_LEAPED_TAG = "PvZRelicHunterLeaped";
    public static final String PORTER_GARGANTUAR_THROWN_IMP_TAG = "PvZPorterGargantuarThrownImp";
    public static final String DARK_AGES_GARGANTUAR_THROWN_IMP_TAG = "PvZDarkAgesGargantuarThrownImp";
    public static final String JESTER_SPINNING_TAG = "PvZJesterSpinning";
    public static final String ROYAL_GUARD_END_TICK_TAG = "PvZRoyalGuardEndTick";
    public static final String NEON_GARGANTUAR_THROWN_IMP_TAG = "PvZNeonGargantuarThrownImp";
    public static final String JURASSIC_GARGANTUAR_THROWN_IMP_TAG = "PvZJurassicGargantuarThrownImp";
    public static final String DEEP_SEA_GARGANTUAR_THROWN_IMP_TAG = "PvZDeepSeaGargantuarThrownImp";
    public static final String PIRATE_GARGANTUAR_THROWN_IMP_TAG = "PvZPirateGargantuarThrownImp";
    public static final String MODERN_GARGANTUAR_THROWN_IMP_TAG = "PvZModernGargantuarThrownImp";
    public static final String MUSIC_BOOSTED_TAG = "PvZMusicBoosted";
    public static final String MUSIC_BOOST_END_TICK_TAG = "PvZMusicBoostEndTick";
    public static final String MUSIC_BOOST_STRENGTH_TAG = "PvZMusicBoostStrength";
    private static final String PROSPECTOR_LEAP_TICK_TAG = "PvZProspectorLeapTick";
    private static final String RELIC_HUNTER_LEAP_TICK_TAG = "PvZRelicHunterLeapTick";
    private static final String PIANIST_NEXT_SUPPORT_TICK_TAG = "PvZPianistNextSupportTick";
    private static final String BULL_NEXT_CHARGE_TICK_TAG = "PvZBullNextChargeTick";
    private static final String BULL_CHARGE_END_TICK_TAG = "PvZBullChargeEndTick";
    private static final String HUNTER_NEXT_FREEZE_TICK_TAG = "PvZHunterNextFreezeTick";
    private static final String TROGLOBITE_NEXT_PUSH_TICK_TAG = "PvZTroglobiteNextPushTick";
    private static final String TROGLOBITE_ICE_CREATED_TAG = "PvZTroglobiteIceCreated";
    private static final String DODO_NEXT_HOP_TICK_TAG = "PvZDodoNextHopTick";
    private static final String TURQUOISE_NEXT_DRAIN_TICK_TAG = "PvZTurquoiseNextDrainTick";
    private static final String JESTER_NEXT_SPIN_TICK_TAG = "PvZJesterNextSpinTick";
    private static final String JESTER_SPIN_END_TICK_TAG = "PvZJesterSpinEndTick";
    private static final String WIZARD_NEXT_CAST_TICK_TAG = "PvZWizardNextCastTick";
    private static final String KING_NEXT_SUPPORT_TICK_TAG = "PvZKingNextSupportTick";
    private static final String DRAGON_IMP_NEXT_FIRE_TICK_TAG = "PvZDragonImpNextFireTick";
    private static final String PUNK_NEXT_SHOVE_TICK_TAG = "PvZPunkNextShoveTick";
    private static final String GLITTER_NEXT_AURA_TICK_TAG = "PvZGlitterNextAuraTick";
    private static final String MC_NEXT_MUSIC_TICK_TAG = "PvZMcNextMusicTick";
    private static final String BREAKDANCER_NEXT_KICK_TICK_TAG = "PvZBreakdancerNextKickTick";
    private static final String ARCADE_NEXT_SUMMON_TICK_TAG = "PvZArcadeNextSummonTick";
    private static final String ARCADE_SUMMON_COUNT_TAG = "PvZArcadeSummonCount";
    private static final String BOOMBOX_NEXT_PULSE_TICK_TAG = "PvZBoomboxNextPulseTick";
    private static final String SURFER_BOARD_ACTIVE_TAG = "PvZSurferBoardActive";
    private static final String SURFER_DAMAGE_TAKEN_TAG = "PvZSurferDamageTaken";
    private static final String FISHERMAN_NEXT_HOOK_TICK_TAG = "PvZFishermanNextHookTick";
    private static final String OCTO_NEXT_DISABLE_TICK_TAG = "PvZOctoNextDisableTick";
    private static final String BARREL_NEXT_ROLL_TICK_TAG = "PvZBarrelNextRollTick";
    private static final String SWASHBUCKLER_SWUNG_TAG = "PvZSwashbucklerSwung";
    private static final String PELICAN_DROPPED_IMP_TAG = "PvZPelicanDroppedImp";
    private static final String IMP_CANNON_NEXT_LAUNCH_TICK_TAG = "PvZImpCannonNextLaunchTick";
    private static final String IMP_CANNON_LAUNCH_COUNT_TAG = "PvZImpCannonLaunchCount";
    private static final String PIRATE_CAPTAIN_NEXT_SUPPORT_TICK_TAG = "PvZPirateCaptainNextSupportTick";
    private static final String PIRATE_CAPTAIN_BUFF_END_TICK_TAG = "PvZPirateCaptainBuffEndTick";
    private static final String TURBULENT_WATER_DAMAGE_TICK_TAG = "PvZTurbulentWaterDamageTick";
    private static final String ALL_STAR_TACKLE_READY_TAG = "PvZAllStarTackleReady";
    private static final String ALL_STAR_NEXT_IMP_KICK_TICK_TAG = "PvZAllStarNextImpKickTick";
    private static final String SUPER_FAN_EXPLODE_TICK_TAG = "PvZSuperFanExplodeTick";
    private static final String RALLY_NEXT_SUPPORT_TICK_TAG = "PvZRallyNextSupportTick";
    private static final String RALLY_BUFF_END_TICK_TAG = "PvZRallyBuffEndTick";
    public static final String GARDEN_CENTER_X_TAG = "PvZGardenCenterX";
    public static final String GARDEN_CENTER_Y_TAG = "PvZGardenCenterY";
    public static final String GARDEN_CENTER_Z_TAG = "PvZGardenCenterZ";
    private static final double DEFAULT_WAVE_BASE_SPEED = 0.13D;
    private static final float NEWSPAPER_RAGE_HEALTH_THRESHOLD = 15.0F;
    private static final double NEWSPAPER_RAGE_SPEED_MULTIPLIER = 1.5D;
    private static final double POLE_VAULT_AFTER_SPEED_MULTIPLIER = 0.9D;
    private static final int RA_DRAIN_AMOUNT = 15;
    private static final int RA_DRAIN_INTERVAL_TICKS = 20 * 3;
    private static final int TOMB_RAISE_INTERVAL_TICKS = 20 * 10;
    private static final int PIANIST_SUPPORT_INTERVAL_TICKS = 20 * 5;
    private static final int BULL_CHARGE_INTERVAL_TICKS = 20 * 7;
    private static final int BULL_CHARGE_DURATION_TICKS = 20 * 3;
    private static final double BULL_CHARGE_SPEED_MULTIPLIER = 2.25D;
    private static final int HUNTER_FREEZE_INTERVAL_TICKS = 20 * 4;
    private static final int TROGLOBITE_PUSH_INTERVAL_TICKS = 20 * 8;
    private static final int DODO_HOP_INTERVAL_TICKS = 20 * 6;
    private static final int TURQUOISE_DRAIN_AMOUNT = 15;
    private static final int TURQUOISE_DRAIN_INTERVAL_TICKS = 20 * 4;
    private static final int JESTER_SPIN_DURATION_TICKS = 20 * 2;
    private static final int JESTER_SPIN_INTERVAL_TICKS = 20 * 6;
    private static final int WIZARD_CAST_INTERVAL_TICKS = 20 * 8;
    private static final int WIZARD_DISABLE_DURATION_TICKS = 20 * 6;
    private static final int KING_SUPPORT_INTERVAL_TICKS = 20 * 5;
    private static final int KING_SUPPORT_DURATION_TICKS = 20 * 5;
    private static final int DRAGON_IMP_FIRE_INTERVAL_TICKS = 20 * 4;
    private static final int PUNK_SHOVE_INTERVAL_TICKS = 20 * 6;
    private static final int GLITTER_AURA_INTERVAL_TICKS = 20 * 3;
    private static final int GLITTER_AURA_DURATION_TICKS = 20 * 3;
    private static final int MC_MUSIC_INTERVAL_TICKS = 20 * 7;
    private static final int BREAKDANCER_KICK_INTERVAL_TICKS = 20 * 7;
    private static final int ARCADE_SUMMON_INTERVAL_TICKS = 20 * 9;
    private static final int ARCADE_MAX_SUMMONS = 6;
    private static final int BOOMBOX_PULSE_INTERVAL_TICKS = 20 * 6;
    private static final int MUSIC_BOOST_DURATION_TICKS = 20 * 3;
    private static final int FISHERMAN_HOOK_INTERVAL_TICKS = 20 * 8;
    private static final int FISHERMAN_HOOK_RANGE = 8;
    private static final int OCTO_DISABLE_INTERVAL_TICKS = 20 * 8;
    private static final int OCTO_DISABLE_DURATION_TICKS = 20 * 6;
    private static final int BARREL_ROLL_INTERVAL_TICKS = 20 * 9;
    private static final int IMP_CANNON_LAUNCH_INTERVAL_TICKS = 20 * 9;
    private static final int IMP_CANNON_MAX_LAUNCHES = 7;
    private static final int PIRATE_CAPTAIN_SUPPORT_INTERVAL_TICKS = 20 * 5;
    private static final int PIRATE_CAPTAIN_SUPPORT_DURATION_TICKS = 20 * 4;
    private static final float SUNDAY_EDITION_RAGE_HEALTH_THRESHOLD = 50.0F;
    private static final double ALL_STAR_AFTER_TACKLE_SPEED_MULTIPLIER = 0.95D;
    private static final int ALL_STAR_IMP_KICK_INTERVAL_TICKS = 20 * 7;
    private static final int SUPER_FAN_FUSE_TICKS = 20;
    private static final float SUPER_FAN_EXPLOSION_RADIUS = 2.0F;
    private static final int RALLY_SUPPORT_INTERVAL_TICKS = 20 * 6;
    private static final int RALLY_SUPPORT_DURATION_TICKS = 20 * 3;

    public PvZZombieEntity(EntityType<? extends Zombie> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void tick() {
        super.tick();
        if (!(level() instanceof ServerLevel level)) {
            return;
        }

        ensureConfigured(DEFAULT_WAVE_BASE_SPEED);
        setCustomNameVisible(true);
        tickNewspaperRage(level);
        tickPoleVault(level);
        tickRaDrain(level);
        tickTombRaiser(level);
        tickProspectorLeap(level);
        tickRelicHunterLeap(level);
        tickPianistSupport(level);
        tickBullCharge(level);
        tickHunterFreeze(level);
        tickTroglobitePush(level);
        tickDodoHop(level);
        tickTurquoiseSkullDrain(level);
        tickJesterSpin(level);
        tickWizardDisable(level);
        tickKingSupport(level);
        tickDragonImpFire(level);
        tickMusicBoostState(level);
        tickPunkShove(level);
        tickGlitterAura(level);
        tickMcMusicSupport(level);
        tickBreakdancerKick(level);
        tickArcadeSummoner(level);
        tickBoomboxPulse(level);
        tickBeachWaterMovement(level);
        tickFishermanHook(level);
        tickOctoDisable(level);
        tickPirateStationaryState(level);
        tickPirateTurbulentWater(level);
        tickBarrelRoller(level);
        tickBarrelObstacle(level);
        tickSwashbucklerSwing(level);
        tickPelicanDrop(level);
        tickImpCannon(level);
        tickPirateCaptainSupport(level);
        tickAllStarTackle(level);
        tickSuperFanImpExplosion(level);
        tickRallySupport(level);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        CompoundTag ownTag = getPersistentData();
        tag.putString(PvZZombieDefinitions.TYPE_TAG, ownTag.getString(PvZZombieDefinitions.TYPE_TAG));
        tag.putBoolean(CONFIGURED_TAG, ownTag.getBoolean(CONFIGURED_TAG));
        tag.putBoolean(NEWSPAPER_RAGED_TAG, ownTag.getBoolean(NEWSPAPER_RAGED_TAG));
        tag.putBoolean(POLE_VAULT_READY_TAG, ownTag.getBoolean(POLE_VAULT_READY_TAG));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        CompoundTag ownTag = getPersistentData();
        if (tag.contains(PvZZombieDefinitions.TYPE_TAG)) {
            ownTag.putString(PvZZombieDefinitions.TYPE_TAG, tag.getString(PvZZombieDefinitions.TYPE_TAG));
        }
        if (tag.contains(CONFIGURED_TAG)) {
            ownTag.putBoolean(CONFIGURED_TAG, tag.getBoolean(CONFIGURED_TAG));
        }
        if (tag.contains(NEWSPAPER_RAGED_TAG)) {
            ownTag.putBoolean(NEWSPAPER_RAGED_TAG, tag.getBoolean(NEWSPAPER_RAGED_TAG));
        }
        if (tag.contains(POLE_VAULT_READY_TAG)) {
            ownTag.putBoolean(POLE_VAULT_READY_TAG, tag.getBoolean(POLE_VAULT_READY_TAG));
        }
    }

    public PvZZombieDefinition definition() {
        CompoundTag tag = getPersistentData();
        if (tag.contains(PvZZombieDefinitions.TYPE_TAG)) {
            return PvZZombieDefinitions.byId(tag.getString(PvZZombieDefinitions.TYPE_TAG))
                    .orElseGet(() -> PvZZombieDefinitions.byEntityType(BuiltInRegistries.ENTITY_TYPE.getKey(getType())));
        }
        return PvZZombieDefinitions.byEntityType(BuiltInRegistries.ENTITY_TYPE.getKey(getType()));
    }

    public void configureForWave(double waveBaseSpeed) {
        ensureConfigured(waveBaseSpeed);
    }

    public double pvzAttackDamage() {
        return getPersistentData().getDouble(PvZZombieDefinitions.ATTACK_DAMAGE_TAG);
    }

    public double configuredMovementSpeed() {
        return movementSpeedFor(definition());
    }

    private void ensureConfigured(double waveBaseSpeed) {
        CompoundTag tag = getPersistentData();
        PvZZombieDefinition definition = definition();
        boolean firstConfigure = !tag.getBoolean(CONFIGURED_TAG);

        tag.putString(PvZZombieDefinitions.TYPE_TAG, definition.id());
        tag.putString(PvZZombieDefinitions.MODEL_KEY_TAG, definition.modelKey());
        tag.putDouble(PvZZombieDefinitions.ATTACK_DAMAGE_TAG, definition.attackDamage());
        tag.putDouble(WAVE_BASE_SPEED_TAG, waveBaseSpeed);
        tag.putBoolean(PvZZombieDefinitions.GARGANTUAR_LIKE_TAG, definition.has(PvZZombieSpecial.GARGANTUAR));
        tag.putBoolean(PvZZombieDefinitions.FLYING_ZOMBIE_TAG, definition.has(PvZZombieSpecial.FLYING));
        tag.putBoolean("PvZMetalZombie", definition.has(PvZZombieSpecial.METAL));
        if (definition.has(PvZZombieSpecial.POLE_VAULT) && !tag.contains(POLE_VAULT_READY_TAG)) {
            tag.putBoolean(POLE_VAULT_READY_TAG, true);
        }
        if (definition.has(PvZZombieSpecial.PONCHO_SHIELD) && !tag.contains(PONCHO_SHIELD_ACTIVE_TAG)) {
            tag.putBoolean(PONCHO_SHIELD_ACTIVE_TAG, true);
        }
        if (definition.has(PvZZombieSpecial.SURFER) && !tag.contains(SURFER_BOARD_ACTIVE_TAG)) {
            tag.putBoolean(SURFER_BOARD_ACTIVE_TAG, true);
        }
        if (definition.has(PvZZombieSpecial.ALL_STAR_TACKLE) && !tag.contains(ALL_STAR_TACKLE_READY_TAG)) {
            tag.putBoolean(ALL_STAR_TACKLE_READY_TAG, true);
        }

        setAttribute(Attributes.MAX_HEALTH, definition.maxHealth());
        setAttribute(Attributes.MOVEMENT_SPEED, movementSpeedFor(definition));
        setAttribute(Attributes.ATTACK_DAMAGE, definition.attackDamage());
        setAttribute(Attributes.KNOCKBACK_RESISTANCE, definition.knockbackResistance());
        setAttribute(Attributes.FOLLOW_RANGE, 48.0D);

        if (firstConfigure || getHealth() > getMaxHealth()) {
            setHealth(getMaxHealth());
        }
        setCustomName(Component.literal(definition.displayName()));
        setCustomNameVisible(true);
        refreshDimensions();
        tag.putBoolean(CONFIGURED_TAG, true);
    }

    private double movementSpeedFor(PvZZombieDefinition definition) {
        double multiplier = definition.movementSpeedMultiplier();
        CompoundTag tag = getPersistentData();
        if (definition.has(PvZZombieSpecial.NEWSPAPER_RAGE) && tag.getBoolean(NEWSPAPER_RAGED_TAG)) {
            multiplier = NEWSPAPER_RAGE_SPEED_MULTIPLIER;
        }
        if (definition.has(PvZZombieSpecial.POLE_VAULT) && tag.contains(POLE_VAULT_READY_TAG) && !tag.getBoolean(POLE_VAULT_READY_TAG)) {
            multiplier = POLE_VAULT_AFTER_SPEED_MULTIPLIER;
        }
        if (definition.has(PvZZombieSpecial.ALL_STAR_TACKLE) && tag.contains(ALL_STAR_TACKLE_READY_TAG) && !tag.getBoolean(ALL_STAR_TACKLE_READY_TAG)) {
            multiplier = ALL_STAR_AFTER_TACKLE_SPEED_MULTIPLIER;
        }
        if (definition.has(PvZZombieSpecial.BULL_CHARGE) && tag.getLong(BULL_CHARGE_END_TICK_TAG) > level().getGameTime()) {
            multiplier = BULL_CHARGE_SPEED_MULTIPLIER;
        }
        if (definition.has(PvZZombieSpecial.SURFER) && !tag.getBoolean(SURFER_BOARD_ACTIVE_TAG)) {
            multiplier = 1.0D;
        }
        if (definition.has(PvZZombieSpecial.AQUATIC) && isOnBeachWaterTile()) {
            multiplier *= definition.has(PvZZombieSpecial.GARGANTUAR) ? 1.15D : 1.25D;
        }
        if (tag.getLong(PIRATE_CAPTAIN_BUFF_END_TICK_TAG) > level().getGameTime()) {
            multiplier *= 1.15D;
        }
        if (tag.getLong(RALLY_BUFF_END_TICK_TAG) > level().getGameTime()) {
            multiplier *= 1.15D;
        }
        return tag.getDouble(WAVE_BASE_SPEED_TAG) * multiplier;
    }

    public static void applyNeonMusicBoost(ServerLevel level, PvZZombieEntity zombie, int durationTicks, float strength) {
        if (!PvZZombieDefinitions.isNeonZombie(zombie) || !zombie.isAlive()) {
            return;
        }

        boolean gargantuar = PvZZombieDefinitions.isGargantuarLike(zombie);
        int adjustedDuration = gargantuar ? Math.max(20, durationTicks / 2) : durationTicks;
        float adjustedStrength = gargantuar ? strength * 0.5F : strength;
        CompoundTag tag = zombie.getPersistentData();
        long endTick = level.getGameTime() + adjustedDuration;
        if (tag.getBoolean(MUSIC_BOOSTED_TAG)
                && tag.getLong(MUSIC_BOOST_END_TICK_TAG) > level.getGameTime()
                && tag.getFloat(MUSIC_BOOST_STRENGTH_TAG) > adjustedStrength) {
            tag.putLong(MUSIC_BOOST_END_TICK_TAG, Math.max(tag.getLong(MUSIC_BOOST_END_TICK_TAG), endTick));
            return;
        }

        tag.putBoolean(MUSIC_BOOSTED_TAG, true);
        tag.putLong(MUSIC_BOOST_END_TICK_TAG, endTick);
        tag.putFloat(MUSIC_BOOST_STRENGTH_TAG, adjustedStrength);
        zombie.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.MOVEMENT_SPEED, adjustedDuration, 0, false, true));
        level.sendParticles(ParticleTypes.NOTE, zombie.getX(), zombie.getY() + 1.4D, zombie.getZ(), 5, 0.35D, 0.35D, 0.35D, 0.0D);
    }

    private void setAttribute(net.minecraft.world.entity.ai.attributes.Attribute attribute, double value) {
        AttributeInstance instance = getAttribute(attribute);
        if (instance != null) {
            instance.setBaseValue(value);
        }
    }

    private void tickNewspaperRage(ServerLevel level) {
        PvZZombieDefinition definition = definition();
        CompoundTag tag = getPersistentData();
        float rageThreshold = definition.has(PvZZombieSpecial.SUNDAY_EDITION) ? SUNDAY_EDITION_RAGE_HEALTH_THRESHOLD : NEWSPAPER_RAGE_HEALTH_THRESHOLD;
        if (!definition.has(PvZZombieSpecial.NEWSPAPER_RAGE)
                || tag.getBoolean(NEWSPAPER_RAGED_TAG)
                || getHealth() > rageThreshold) {
            return;
        }

        tag.putBoolean(NEWSPAPER_RAGED_TAG, true);
        setAttribute(Attributes.MOVEMENT_SPEED, movementSpeedFor(definition));
        level.sendParticles(ParticleTypes.ANGRY_VILLAGER, getX(), getY() + 1.2D, getZ(), 8, 0.3D, 0.35D, 0.3D, 0.02D);
        level.playSound(null, blockPosition(), SoundEvents.ZOMBIE_BREAK_WOODEN_DOOR, SoundSource.HOSTILE, 0.6F, 1.35F);
    }

    private void tickPoleVault(ServerLevel level) {
        PvZZombieDefinition definition = definition();
        CompoundTag tag = getPersistentData();
        if (!definition.has(PvZZombieSpecial.POLE_VAULT) || !tag.getBoolean(POLE_VAULT_READY_TAG) || tickCount % 5 != 0) {
            return;
        }

        Vec3 forward = horizontalForward();
        Optional<SnowGolem> blocker = level.getEntitiesOfClass(SnowGolem.class, getBoundingBox().inflate(1.6D, 0.5D, 1.6D), PlantEntityManager::isPlant)
                .stream()
                .filter(plant -> isInFront(plant.position(), forward))
                .min(Comparator.comparingDouble(this::distanceToSqr));
        if (blocker.isEmpty()) {
            return;
        }

        BlockPos landing = BlockPos.containing(blocker.get().position().add(forward.scale(2.1D)));
        landing = level.getHeightmapPos(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, landing);
        if (!isSafeLanding(level, landing)) {
            return;
        }

        moveTo(landing.getX() + 0.5D, landing.getY(), landing.getZ() + 0.5D, getYRot(), getXRot());
        setDeltaMovement(forward.scale(0.45D).add(0.0D, 0.25D, 0.0D));
        tag.putBoolean(POLE_VAULT_READY_TAG, false);
        setAttribute(Attributes.MOVEMENT_SPEED, movementSpeedFor(definition));
        level.sendParticles(ParticleTypes.CLOUD, getX(), getY() + 0.4D, getZ(), 16, 0.35D, 0.2D, 0.35D, 0.03D);
        level.playSound(null, blockPosition(), SoundEvents.SLIME_JUMP, SoundSource.HOSTILE, 0.8F, 0.8F);
    }

    private void tickRaDrain(ServerLevel level) {
        if (!definition().has(PvZZombieSpecial.RA_DRAIN)) {
            return;
        }

        CompoundTag tag = getPersistentData();
        long gameTime = level.getGameTime();
        if (gameTime < tag.getLong(RA_NEXT_DRAIN_TICK_TAG)) {
            return;
        }

        boolean drainedAny = false;
        for (Player player : level.players()) {
            if (player.distanceToSqr(this) > 24.0D * 24.0D || SunManager.getSun(player) <= 0) {
                continue;
            }
            SunManager.setSun(player, Math.max(0, SunManager.getSun(player) - RA_DRAIN_AMOUNT));
            drainedAny = true;
        }

        if (drainedAny) {
            level.sendParticles(ParticleTypes.SMOKE, getX(), getY() + 1.3D, getZ(), 12, 0.35D, 0.45D, 0.35D, 0.03D);
            level.playSound(null, blockPosition(), SoundEvents.SAND_BREAK, SoundSource.HOSTILE, 0.65F, 0.6F);
        }
        tag.putLong(RA_NEXT_DRAIN_TICK_TAG, gameTime + RA_DRAIN_INTERVAL_TICKS);
    }

    private void tickTombRaiser(ServerLevel level) {
        if (!definition().has(PvZZombieSpecial.TOMB_RAISER)) {
            return;
        }

        CompoundTag tag = getPersistentData();
        long gameTime = level.getGameTime();
        if (gameTime < tag.getLong(TOMB_NEXT_RAISE_TICK_TAG) || !tag.contains(GARDEN_CENTER_X_TAG)) {
            return;
        }

        BlockPos gardenCenter = new BlockPos(tag.getInt(GARDEN_CENTER_X_TAG), tag.getInt(GARDEN_CENTER_Y_TAG), tag.getInt(GARDEN_CENTER_Z_TAG));
        if (AncientEgyptTombManager.tryRaiseTomb(level, gardenCenter, blockPosition(), tag.getInt(TOMB_RAISED_COUNT_TAG))) {
            tag.putInt(TOMB_RAISED_COUNT_TAG, tag.getInt(TOMB_RAISED_COUNT_TAG) + 1);
            level.sendParticles(ParticleTypes.SOUL, getX(), getY() + 1.0D, getZ(), 10, 0.4D, 0.5D, 0.4D, 0.02D);
        }
        tag.putLong(TOMB_NEXT_RAISE_TICK_TAG, gameTime + TOMB_RAISE_INTERVAL_TICKS + level.random.nextInt(20 * 3));
    }

    private void tickProspectorLeap(ServerLevel level) {
        if (!definition().has(PvZZombieSpecial.PROSPECTOR_LEAP)) {
            return;
        }

        CompoundTag tag = getPersistentData();
        long gameTime = level.getGameTime();
        if (tag.getBoolean(PROSPECTOR_LEAPED_TAG)) {
            return;
        }
        if (!tag.contains(PROSPECTOR_LEAP_TICK_TAG)) {
            tag.putLong(PROSPECTOR_LEAP_TICK_TAG, gameTime + 20L * 3);
            return;
        }
        if (gameTime < tag.getLong(PROSPECTOR_LEAP_TICK_TAG)) {
            return;
        }

        Vec3 forward = vectorTowardGarden().orElse(horizontalForward());
        Optional<SnowGolem> blocker = level.getEntitiesOfClass(SnowGolem.class, getBoundingBox().inflate(5.0D, 1.0D, 5.0D), PlantEntityManager::isPlant)
                .stream()
                .filter(plant -> isInFront(plant.position(), forward))
                .min(Comparator.comparingDouble(this::distanceToSqr));
        if (blocker.isEmpty()) {
            tag.putBoolean(PROSPECTOR_LEAPED_TAG, true);
            return;
        }

        BlockPos landing = BlockPos.containing(blocker.get().position().add(forward.scale(2.8D)));
        landing = level.getHeightmapPos(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, landing);
        if (!isSafeLanding(level, landing)) {
            tag.putBoolean(PROSPECTOR_LEAPED_TAG, true);
            return;
        }

        moveTo(landing.getX() + 0.5D, landing.getY(), landing.getZ() + 0.5D, getYRot(), getXRot());
        setDeltaMovement(forward.scale(0.65D).add(0.0D, 0.35D, 0.0D));
        tag.putBoolean(PROSPECTOR_LEAPED_TAG, true);
        level.sendParticles(ParticleTypes.SMOKE, getX(), getY() + 0.6D, getZ(), 24, 0.45D, 0.35D, 0.45D, 0.06D);
        level.playSound(null, blockPosition(), SoundEvents.FIREWORK_ROCKET_LAUNCH, SoundSource.HOSTILE, 0.7F, 1.4F);
    }

    private void tickRelicHunterLeap(ServerLevel level) {
        if (!definition().has(PvZZombieSpecial.RELIC_HUNTER_LEAP)) {
            return;
        }

        CompoundTag tag = getPersistentData();
        long gameTime = level.getGameTime();
        if (tag.getBoolean(RELIC_HUNTER_LEAPED_TAG)) {
            return;
        }
        if (!tag.contains(RELIC_HUNTER_LEAP_TICK_TAG)) {
            tag.putLong(RELIC_HUNTER_LEAP_TICK_TAG, gameTime + 20L * 2);
            return;
        }
        if (gameTime < tag.getLong(RELIC_HUNTER_LEAP_TICK_TAG)) {
            return;
        }

        Vec3 forward = vectorTowardGarden().orElse(horizontalForward());
        Optional<SnowGolem> blocker = level.getEntitiesOfClass(SnowGolem.class, getBoundingBox().inflate(4.5D, 1.0D, 4.5D), PlantEntityManager::isPlant)
                .stream()
                .filter(plant -> isInFront(plant.position(), forward))
                .min(Comparator.comparingDouble(this::distanceToSqr));
        if (blocker.isEmpty()) {
            tag.putBoolean(RELIC_HUNTER_LEAPED_TAG, true);
            return;
        }

        BlockPos landing = BlockPos.containing(blocker.get().position().add(forward.scale(3.2D)));
        landing = level.getHeightmapPos(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, landing);
        if (!isSafeLanding(level, landing)) {
            tag.putBoolean(RELIC_HUNTER_LEAPED_TAG, true);
            return;
        }

        moveTo(landing.getX() + 0.5D, landing.getY(), landing.getZ() + 0.5D, getYRot(), getXRot());
        setDeltaMovement(forward.scale(0.75D).add(0.0D, 0.45D, 0.0D));
        tag.putBoolean(RELIC_HUNTER_LEAPED_TAG, true);
        level.sendParticles(ParticleTypes.CRIT, getX(), getY() + 0.8D, getZ(), 18, 0.45D, 0.35D, 0.45D, 0.04D);
        level.playSound(null, blockPosition(), SoundEvents.SLIME_JUMP, SoundSource.HOSTILE, 0.8F, 1.25F);
    }

    private void tickPianistSupport(ServerLevel level) {
        if (!definition().has(PvZZombieSpecial.PIANIST_SUPPORT)) {
            return;
        }

        CompoundTag tag = getPersistentData();
        long gameTime = level.getGameTime();
        if (gameTime < tag.getLong(PIANIST_NEXT_SUPPORT_TICK_TAG)) {
            return;
        }

        AABB area = getBoundingBox().inflate(6.0D, 2.0D, 6.0D);
        for (PvZZombieEntity zombie : level.getEntitiesOfClass(PvZZombieEntity.class, area, zombie -> zombie.isAlive() && zombie != this)) {
            zombie.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.MOVEMENT_SPEED, 20 * 3, 0, false, true));
        }
        level.sendParticles(ParticleTypes.NOTE, getX(), getY() + 1.6D, getZ(), 12, 0.7D, 0.4D, 0.7D, 0.0D);
        level.playSound(null, blockPosition(), SoundEvents.NOTE_BLOCK_BANJO.get(), SoundSource.HOSTILE, 0.8F, 0.7F);
        tag.putLong(PIANIST_NEXT_SUPPORT_TICK_TAG, gameTime + PIANIST_SUPPORT_INTERVAL_TICKS);
    }

    private void tickBullCharge(ServerLevel level) {
        if (!definition().has(PvZZombieSpecial.BULL_CHARGE) || !getPersistentData().contains(GARDEN_CENTER_X_TAG)) {
            return;
        }

        CompoundTag tag = getPersistentData();
        long gameTime = level.getGameTime();
        if (gameTime >= tag.getLong(BULL_CHARGE_END_TICK_TAG)) {
            if (gameTime >= tag.getLong(BULL_NEXT_CHARGE_TICK_TAG)) {
                tag.putLong(BULL_CHARGE_END_TICK_TAG, gameTime + BULL_CHARGE_DURATION_TICKS);
                tag.putLong(BULL_NEXT_CHARGE_TICK_TAG, gameTime + BULL_CHARGE_INTERVAL_TICKS + level.random.nextInt(20 * 2));
                level.playSound(null, blockPosition(), SoundEvents.GOAT_SCREAMING_RAM_IMPACT, SoundSource.HOSTILE, 0.85F, 0.7F);
            } else {
                setAttribute(Attributes.MOVEMENT_SPEED, movementSpeedFor(definition()));
                return;
            }
        }

        setAttribute(Attributes.MOVEMENT_SPEED, movementSpeedFor(definition()));
        Vec3 towardGarden = vectorTowardGarden().orElse(horizontalForward());
        setDeltaMovement(getDeltaMovement().add(towardGarden.scale(0.12D)));
        level.sendParticles(ParticleTypes.CLOUD, getX(), getY() + 0.25D, getZ(), 4, 0.25D, 0.1D, 0.25D, 0.03D);
    }

    private void tickHunterFreeze(ServerLevel level) {
        if (!definition().has(PvZZombieSpecial.HUNTER_FREEZE)) {
            return;
        }

        CompoundTag tag = getPersistentData();
        long gameTime = level.getGameTime();
        if (gameTime < tag.getLong(HUNTER_NEXT_FREEZE_TICK_TAG)) {
            return;
        }

        Optional<SnowGolem> target = level.getEntitiesOfClass(SnowGolem.class, getBoundingBox().inflate(10.0D, 4.0D, 10.0D), plant -> plant.isAlive() && PlantEntityManager.isPlant(plant))
                .stream()
                .filter(this::hasLineOfSight)
                .min(Comparator.comparingDouble(this::distanceToSqr));
        if (target.isPresent()) {
            SnowGolem plant = target.get();
            Snowball snowball = new Snowball(level, this);
            snowball.getPersistentData().putBoolean(HUNTER_FREEZE_SHOT_TAG, true);
            snowball.setPos(getX(), getEyeY() - 0.15D, getZ());
            Vec3 velocity = plant.position().add(0.0D, 0.9D, 0.0D).subtract(snowball.position()).normalize().scale(0.9D);
            snowball.setDeltaMovement(velocity);
            level.addFreshEntity(snowball);
            level.sendParticles(ParticleTypes.SNOWFLAKE, getX(), getEyeY(), getZ(), 6, 0.2D, 0.2D, 0.2D, 0.02D);
            level.playSound(null, blockPosition(), SoundEvents.SNOWBALL_THROW, SoundSource.HOSTILE, 0.8F, 0.8F);
        }
        tag.putLong(HUNTER_NEXT_FREEZE_TICK_TAG, gameTime + HUNTER_FREEZE_INTERVAL_TICKS);
    }

    private void tickTroglobitePush(ServerLevel level) {
        if (!definition().has(PvZZombieSpecial.TROGLOBITE_PUSH) || !getPersistentData().contains(GARDEN_CENTER_X_TAG)) {
            return;
        }

        CompoundTag tag = getPersistentData();
        long gameTime = level.getGameTime();
        if (gameTime < tag.getLong(TROGLOBITE_NEXT_PUSH_TICK_TAG)) {
            return;
        }

        Vec3 towardGarden = vectorTowardGarden().orElse(horizontalForward());
        boolean pushed = false;
        AABB area = getBoundingBox().inflate(7.0D, 2.0D, 7.0D);
        for (PvZZombieEntity iceBlock : level.getEntitiesOfClass(PvZZombieEntity.class, area, zombie -> zombie.isAlive() && "ice_block_zombie".equals(zombie.definition().id()))) {
            iceBlock.setDeltaMovement(iceBlock.getDeltaMovement().add(towardGarden.scale(0.55D)));
            iceBlock.setAttribute(Attributes.MOVEMENT_SPEED, Math.max(iceBlock.configuredMovementSpeed(), 0.18D));
            pushed = true;
        }

        if (!pushed && tag.getInt(TROGLOBITE_ICE_CREATED_TAG) < 2) {
            spawnIceBlockZombie(level, towardGarden);
            tag.putInt(TROGLOBITE_ICE_CREATED_TAG, tag.getInt(TROGLOBITE_ICE_CREATED_TAG) + 1);
            pushed = true;
        }

        if (pushed) {
            level.sendParticles(ParticleTypes.SNOWFLAKE, getX(), getY() + 0.9D, getZ(), 18, 0.6D, 0.25D, 0.6D, 0.03D);
            level.playSound(null, blockPosition(), SoundEvents.PACKED_MUD_PLACE, SoundSource.HOSTILE, 0.75F, 0.7F);
        }
        tag.putLong(TROGLOBITE_NEXT_PUSH_TICK_TAG, gameTime + TROGLOBITE_PUSH_INTERVAL_TICKS + level.random.nextInt(20 * 3));
    }

    private void spawnIceBlockZombie(ServerLevel level, Vec3 towardGarden) {
        Optional.ofNullable(ModEntities.ZOMBIES.get("ice_block_zombie"))
                .map(registryObject -> registryObject.get())
                .map(entityType -> entityType.create(level))
                .ifPresent(iceBlock -> {
                    BlockPos spawnPos = BlockPos.containing(position().add(towardGarden.scale(1.8D)));
                    spawnPos = level.getHeightmapPos(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, spawnPos);
                    if (!isSafeLanding(level, spawnPos)) {
                        return;
                    }
                    iceBlock.moveTo(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D, getYRot(), 0.0F);
                    iceBlock.getPersistentData().putBoolean("PvZWaveZombie", getPersistentData().getBoolean("PvZWaveZombie"));
                    copyGardenCenterTo(iceBlock);
                    iceBlock.finalizeSpawn(level, level.getCurrentDifficultyAt(iceBlock.blockPosition()), MobSpawnType.EVENT, null, null);
                    level.addFreshEntity(iceBlock);
                    iceBlock.configureForWave(0.13D);
                });
    }

    private void tickDodoHop(ServerLevel level) {
        if (!definition().has(PvZZombieSpecial.DODO_HOP) || !getPersistentData().contains(GARDEN_CENTER_X_TAG)) {
            return;
        }

        CompoundTag tag = getPersistentData();
        long gameTime = level.getGameTime();
        if (gameTime < tag.getLong(DODO_NEXT_HOP_TICK_TAG)) {
            return;
        }

        Vec3 towardGarden = vectorTowardGarden().orElse(horizontalForward());
        BlockPos landing = BlockPos.containing(position().add(towardGarden.scale(2.4D)));
        landing = level.getHeightmapPos(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, landing);
        if (isSafeLanding(level, landing)) {
            setDeltaMovement(towardGarden.scale(0.55D).add(0.0D, 0.65D, 0.0D));
            moveTo(landing.getX() + 0.5D, landing.getY(), landing.getZ() + 0.5D, getYRot(), getXRot());
            level.sendParticles(ParticleTypes.CLOUD, getX(), getY() + 0.35D, getZ(), 10, 0.35D, 0.15D, 0.35D, 0.03D);
            level.playSound(null, blockPosition(), SoundEvents.RABBIT_JUMP, SoundSource.HOSTILE, 0.8F, 0.7F);
        }
        tag.putLong(DODO_NEXT_HOP_TICK_TAG, gameTime + DODO_HOP_INTERVAL_TICKS + level.random.nextInt(20 * 2));
    }

    private void tickTurquoiseSkullDrain(ServerLevel level) {
        if (!definition().has(PvZZombieSpecial.TURQUOISE_SKULL_DRAIN)) {
            return;
        }

        CompoundTag tag = getPersistentData();
        long gameTime = level.getGameTime();
        if (gameTime < tag.getLong(TURQUOISE_NEXT_DRAIN_TICK_TAG)) {
            return;
        }

        boolean drainedAny = false;
        for (Player player : level.players()) {
            if (player.distanceToSqr(this) > 24.0D * 24.0D || SunManager.getSun(player) <= 0) {
                continue;
            }
            SunManager.setSun(player, Math.max(0, SunManager.getSun(player) - TURQUOISE_DRAIN_AMOUNT));
            drainedAny = true;
            Vec3 toPlayer = player.position().add(0.0D, 1.0D, 0.0D).subtract(position().add(0.0D, 1.4D, 0.0D));
            for (int i = 0; i < 10; i++) {
                Vec3 point = position().add(0.0D, 1.4D, 0.0D).add(toPlayer.scale(i / 10.0D));
                level.sendParticles(ParticleTypes.ENCHANT, point.x, point.y, point.z, 1, 0.03D, 0.03D, 0.03D, 0.0D);
            }
        }

        if (drainedAny) {
            level.sendParticles(ParticleTypes.WAX_OFF, getX(), getY() + 1.3D, getZ(), 10, 0.35D, 0.45D, 0.35D, 0.02D);
            level.playSound(null, blockPosition(), SoundEvents.BEACON_AMBIENT, SoundSource.HOSTILE, 0.45F, 1.55F);
        }
        tag.putLong(TURQUOISE_NEXT_DRAIN_TICK_TAG, gameTime + TURQUOISE_DRAIN_INTERVAL_TICKS);
    }

    private void tickJesterSpin(ServerLevel level) {
        if (!definition().has(PvZZombieSpecial.JESTER_SPIN)) {
            return;
        }

        CompoundTag tag = getPersistentData();
        long gameTime = level.getGameTime();
        if (tag.getBoolean(JESTER_SPINNING_TAG)) {
            if (gameTime >= tag.getLong(JESTER_SPIN_END_TICK_TAG)) {
                tag.putBoolean(JESTER_SPINNING_TAG, false);
                tag.putLong(JESTER_NEXT_SPIN_TICK_TAG, gameTime + JESTER_SPIN_INTERVAL_TICKS + level.random.nextInt(20 * 2));
            } else if (gameTime % 5L == 0L) {
                level.sendParticles(ParticleTypes.CRIT, getX(), getY() + 1.0D, getZ(), 4, 0.35D, 0.45D, 0.35D, 0.02D);
            }
            return;
        }

        if (gameTime >= tag.getLong(JESTER_NEXT_SPIN_TICK_TAG)) {
            tag.putBoolean(JESTER_SPINNING_TAG, true);
            tag.putLong(JESTER_SPIN_END_TICK_TAG, gameTime + JESTER_SPIN_DURATION_TICKS);
            level.playSound(null, blockPosition(), SoundEvents.ARMOR_EQUIP_LEATHER, SoundSource.HOSTILE, 0.8F, 1.6F);
        }
    }

    private void tickWizardDisable(ServerLevel level) {
        if (!definition().has(PvZZombieSpecial.WIZARD_DISABLE)) {
            return;
        }

        CompoundTag tag = getPersistentData();
        long gameTime = level.getGameTime();
        if (gameTime < tag.getLong(WIZARD_NEXT_CAST_TICK_TAG)) {
            return;
        }

        Optional<SnowGolem> target = level.getEntitiesOfClass(SnowGolem.class, getBoundingBox().inflate(9.0D, 4.0D, 9.0D), plant -> plant.isAlive() && PlantEntityManager.isPlant(plant))
                .stream()
                .filter(this::hasLineOfSight)
                .min(Comparator.comparingDouble(this::distanceToSqr));
        target.ifPresent(plant -> {
            PlantEntityManager.applyWizardDisable(level, plant, this, WIZARD_DISABLE_DURATION_TICKS);
            renderMagicLine(level, position().add(0.0D, 1.3D, 0.0D), plant.position().add(0.0D, 1.0D, 0.0D), ParticleTypes.ENCHANT);
        });
        tag.putLong(WIZARD_NEXT_CAST_TICK_TAG, gameTime + WIZARD_CAST_INTERVAL_TICKS + level.random.nextInt(20 * 2));
    }

    private void tickKingSupport(ServerLevel level) {
        if (!definition().has(PvZZombieSpecial.KING_SUPPORT)) {
            return;
        }

        CompoundTag tag = getPersistentData();
        long gameTime = level.getGameTime();
        if (gameTime < tag.getLong(KING_NEXT_SUPPORT_TICK_TAG)) {
            return;
        }

        AABB area = getBoundingBox().inflate(6.0D, 2.0D, 6.0D);
        for (PvZZombieEntity zombie : level.getEntitiesOfClass(PvZZombieEntity.class, area, zombie -> zombie.isAlive() && zombie != this && isDarkAgesSupportTarget(zombie))) {
            zombie.getPersistentData().putLong(ROYAL_GUARD_END_TICK_TAG, gameTime + KING_SUPPORT_DURATION_TICKS);
            zombie.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.MOVEMENT_SPEED, KING_SUPPORT_DURATION_TICKS, 0, false, true));
            level.sendParticles(ParticleTypes.ENCHANT, zombie.getX(), zombie.getY() + 1.1D, zombie.getZ(), 5, 0.25D, 0.35D, 0.25D, 0.02D);
        }
        level.playSound(null, blockPosition(), SoundEvents.NOTE_BLOCK_BELL.get(), SoundSource.HOSTILE, 0.8F, 0.8F);
        tag.putLong(KING_NEXT_SUPPORT_TICK_TAG, gameTime + KING_SUPPORT_INTERVAL_TICKS);
    }

    private void tickDragonImpFire(ServerLevel level) {
        if (!definition().has(PvZZombieSpecial.DRAGON_IMP_FIRE)) {
            return;
        }

        CompoundTag tag = getPersistentData();
        long gameTime = level.getGameTime();
        if (gameTime < tag.getLong(DRAGON_IMP_NEXT_FIRE_TICK_TAG)) {
            return;
        }

        Optional<SnowGolem> target = level.getEntitiesOfClass(SnowGolem.class, getBoundingBox().inflate(6.0D, 3.0D, 6.0D), plant -> plant.isAlive() && PlantEntityManager.isPlant(plant))
                .stream()
                .filter(this::hasLineOfSight)
                .min(Comparator.comparingDouble(this::distanceToSqr));
        target.ifPresent(plant -> {
            plant.hurt(level.damageSources().mobAttack(this), 2.0F);
            level.sendParticles(ParticleTypes.FLAME, plant.getX(), plant.getY() + 0.9D, plant.getZ(), 8, 0.25D, 0.25D, 0.25D, 0.02D);
            renderMagicLine(level, position().add(0.0D, 0.8D, 0.0D), plant.position().add(0.0D, 0.9D, 0.0D), ParticleTypes.FLAME);
            level.playSound(null, blockPosition(), SoundEvents.BLAZE_SHOOT, SoundSource.HOSTILE, 0.55F, 1.5F);
        });
        tag.putLong(DRAGON_IMP_NEXT_FIRE_TICK_TAG, gameTime + DRAGON_IMP_FIRE_INTERVAL_TICKS);
    }

    private void tickMusicBoostState(ServerLevel level) {
        CompoundTag tag = getPersistentData();
        if (!tag.getBoolean(MUSIC_BOOSTED_TAG)) {
            return;
        }
        if (level.getGameTime() >= tag.getLong(MUSIC_BOOST_END_TICK_TAG)) {
            tag.remove(MUSIC_BOOSTED_TAG);
            tag.remove(MUSIC_BOOST_END_TICK_TAG);
            tag.remove(MUSIC_BOOST_STRENGTH_TAG);
        } else if (level.getGameTime() % 10L == 0L) {
            level.sendParticles(ParticleTypes.NOTE, getX(), getY() + 1.4D, getZ(), 2, 0.25D, 0.25D, 0.25D, 0.0D);
        }
    }

    private void tickPunkShove(ServerLevel level) {
        if (!definition().has(PvZZombieSpecial.PUNK_SHOVE) || !getPersistentData().contains(GARDEN_CENTER_X_TAG)) {
            return;
        }

        CompoundTag tag = getPersistentData();
        long gameTime = level.getGameTime();
        if (gameTime < tag.getLong(PUNK_NEXT_SHOVE_TICK_TAG)) {
            return;
        }

        Vec3 towardGarden = vectorTowardGarden().orElse(horizontalForward());
        Optional<SnowGolem> plantTarget = level.getEntitiesOfClass(SnowGolem.class, getBoundingBox().inflate(2.0D, 1.0D, 2.0D), plant -> plant.isAlive() && PlantEntityManager.isPlant(plant))
                .stream()
                .filter(plant -> isInFront(plant.position(), towardGarden))
                .min(Comparator.comparingDouble(this::distanceToSqr));
        plantTarget.ifPresent(plant -> {
            plant.hurt(level.damageSources().mobAttack(this), 6.0F);
            PlantEntityManager.applyWizardDisable(level, plant, this, 20 * 2);
            level.sendParticles(ParticleTypes.SONIC_BOOM, plant.getX(), plant.getY() + 0.8D, plant.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
        });

        AABB area = getBoundingBox().inflate(2.5D, 1.0D, 2.5D);
        for (PvZZombieEntity zombie : level.getEntitiesOfClass(PvZZombieEntity.class, area, zombie -> zombie.isAlive() && zombie != this && !PvZZombieDefinitions.isGargantuarLike(zombie))) {
            zombie.setDeltaMovement(zombie.getDeltaMovement().add(towardGarden.scale(0.45D)));
        }
        level.playSound(null, blockPosition(), SoundEvents.NOTE_BLOCK_BASEDRUM.get(), SoundSource.HOSTILE, 0.9F, 0.7F);
        tag.putLong(PUNK_NEXT_SHOVE_TICK_TAG, gameTime + PUNK_SHOVE_INTERVAL_TICKS);
    }

    private void tickGlitterAura(ServerLevel level) {
        if (!definition().has(PvZZombieSpecial.GLITTER_AURA)) {
            return;
        }

        CompoundTag tag = getPersistentData();
        long gameTime = level.getGameTime();
        if (gameTime < tag.getLong(GLITTER_NEXT_AURA_TICK_TAG)) {
            return;
        }

        for (PvZZombieEntity zombie : level.getEntitiesOfClass(PvZZombieEntity.class, getBoundingBox().inflate(5.0D, 2.0D, 5.0D), zombie -> zombie.isAlive() && zombie != this)) {
            zombie.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.DAMAGE_RESISTANCE, GLITTER_AURA_DURATION_TICKS, 0, false, true));
            level.sendParticles(ParticleTypes.FIREWORK, zombie.getX(), zombie.getY() + 1.0D, zombie.getZ(), 3, 0.25D, 0.35D, 0.25D, 0.01D);
            if (zombie.getPersistentData().getBoolean(MUSIC_BOOSTED_TAG)) {
                applyNeonMusicBoost(level, zombie, MUSIC_BOOST_DURATION_TICKS, 0.2F);
            }
        }
        level.sendParticles(ParticleTypes.FIREWORK, getX(), getY() + 1.3D, getZ(), 10, 0.6D, 0.45D, 0.6D, 0.01D);
        tag.putLong(GLITTER_NEXT_AURA_TICK_TAG, gameTime + GLITTER_AURA_INTERVAL_TICKS);
    }

    private void tickMcMusicSupport(ServerLevel level) {
        if (!definition().has(PvZZombieSpecial.MC_MUSIC_SUPPORT)) {
            return;
        }

        CompoundTag tag = getPersistentData();
        long gameTime = level.getGameTime();
        if (gameTime < tag.getLong(MC_NEXT_MUSIC_TICK_TAG)) {
            return;
        }

        pulseNearbyNeonZombies(level, 6.0D, true);
        level.playSound(null, blockPosition(), SoundEvents.NOTE_BLOCK_BANJO.get(), SoundSource.HOSTILE, 0.9F, 1.25F);
        tag.putLong(MC_NEXT_MUSIC_TICK_TAG, gameTime + MC_MUSIC_INTERVAL_TICKS);
    }

    private void tickBreakdancerKick(ServerLevel level) {
        if (!definition().has(PvZZombieSpecial.BREAKDANCER_KICK) || !getPersistentData().contains(GARDEN_CENTER_X_TAG)) {
            return;
        }

        CompoundTag tag = getPersistentData();
        long gameTime = level.getGameTime();
        if (gameTime < tag.getLong(BREAKDANCER_NEXT_KICK_TICK_TAG)) {
            return;
        }

        Vec3 towardGarden = vectorTowardGarden().orElse(horizontalForward());
        int kicked = 0;
        for (PvZZombieEntity zombie : level.getEntitiesOfClass(PvZZombieEntity.class, getBoundingBox().inflate(4.0D, 2.0D, 4.0D), zombie -> zombie.isAlive() && zombie != this && !PvZZombieDefinitions.isGargantuarLike(zombie))) {
            zombie.setDeltaMovement(zombie.getDeltaMovement().add(towardGarden.scale(0.7D).add(0.0D, 0.12D, 0.0D)));
            level.sendParticles(ParticleTypes.CLOUD, zombie.getX(), zombie.getY() + 0.3D, zombie.getZ(), 6, 0.25D, 0.1D, 0.25D, 0.03D);
            if (++kicked >= 3) {
                break;
            }
        }
        level.sendParticles(ParticleTypes.NOTE, getX(), getY() + 1.2D, getZ(), 14, 0.7D, 0.3D, 0.7D, 0.0D);
        level.playSound(null, blockPosition(), SoundEvents.NOTE_BLOCK_SNARE.get(), SoundSource.HOSTILE, 0.8F, 1.1F);
        tag.putLong(BREAKDANCER_NEXT_KICK_TICK_TAG, gameTime + BREAKDANCER_KICK_INTERVAL_TICKS);
    }

    private void tickArcadeSummoner(ServerLevel level) {
        if (!definition().has(PvZZombieSpecial.ARCADE_SUMMONER)) {
            return;
        }

        CompoundTag tag = getPersistentData();
        long gameTime = level.getGameTime();
        if (gameTime < tag.getLong(ARCADE_NEXT_SUMMON_TICK_TAG) || tag.getInt(ARCADE_SUMMON_COUNT_TAG) >= ARCADE_MAX_SUMMONS) {
            return;
        }

        int count = Math.min(ARCADE_MAX_SUMMONS - tag.getInt(ARCADE_SUMMON_COUNT_TAG), 1 + level.random.nextInt(2));
        spawnSummonedZombies(level, "eight_bit_zombie", count, ParticleTypes.NOTE, SoundEvents.NOTE_BLOCK_PLING.get());
        tag.putInt(ARCADE_SUMMON_COUNT_TAG, tag.getInt(ARCADE_SUMMON_COUNT_TAG) + count);
        tag.putLong(ARCADE_NEXT_SUMMON_TICK_TAG, gameTime + ARCADE_SUMMON_INTERVAL_TICKS + level.random.nextInt(20 * 2));
    }

    private void tickBoomboxPulse(ServerLevel level) {
        if (!definition().has(PvZZombieSpecial.BOOMBOX_PULSE)) {
            return;
        }

        CompoundTag tag = getPersistentData();
        long gameTime = level.getGameTime();
        if (gameTime < tag.getLong(BOOMBOX_NEXT_PULSE_TICK_TAG)) {
            return;
        }

        pulseNearbyNeonZombies(level, 6.0D, false);
        level.sendParticles(ParticleTypes.SONIC_BOOM, getX(), getY() + 1.0D, getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
        level.playSound(null, blockPosition(), SoundEvents.NOTE_BLOCK_BASS.get(), SoundSource.HOSTILE, 1.0F, 0.6F);
        tag.putLong(BOOMBOX_NEXT_PULSE_TICK_TAG, gameTime + BOOMBOX_PULSE_INTERVAL_TICKS);
    }

    private void tickBeachWaterMovement(ServerLevel level) {
        PvZZombieDefinition definition = definition();
        if (!definition.has(PvZZombieSpecial.AQUATIC) && !definition.has(PvZZombieSpecial.SURFER)) {
            return;
        }
        if (tickCount % 20 != 0) {
            return;
        }
        setAttribute(Attributes.MOVEMENT_SPEED, movementSpeedFor(definition));
        if (definition.has(PvZZombieSpecial.AQUATIC) && isOnBeachWaterTile()) {
            level.sendParticles(ParticleTypes.BUBBLE, getX(), getY() + 0.4D, getZ(), 4, 0.25D, 0.2D, 0.25D, 0.01D);
        }
    }

    private void tickFishermanHook(ServerLevel level) {
        if (!definition().has(PvZZombieSpecial.FISHERMAN_HOOK)) {
            return;
        }

        CompoundTag tag = getPersistentData();
        long gameTime = level.getGameTime();
        if (gameTime < tag.getLong(FISHERMAN_NEXT_HOOK_TICK_TAG)) {
            return;
        }

        Optional<SnowGolem> target = level.getEntitiesOfClass(SnowGolem.class, getBoundingBox().inflate(FISHERMAN_HOOK_RANGE, 3.0D, FISHERMAN_HOOK_RANGE),
                        plant -> plant.isAlive()
                                && PlantEntityManager.canFishermanHookPlant(plant)
                                && !PlantEntityManager.isRecentlyHooked(level, plant)
                                && hasLineOfSight(plant))
                .stream()
                .min(Comparator.comparingDouble(this::distanceToSqr));
        target.ifPresent(plant -> {
            Optional<BlockPos> destination = PlantEntityManager.findNearestHookWaterDestination(level, plant, this);
            if (destination.isPresent() && PlantEntityManager.pullPlantTowardWater(level, plant, destination.get())) {
                renderMagicLine(level, position().add(0.0D, 1.2D, 0.0D), plant.position().add(0.0D, 0.8D, 0.0D), ParticleTypes.FISHING);
                level.playSound(null, blockPosition(), SoundEvents.FISHING_BOBBER_RETRIEVE, SoundSource.HOSTILE, 0.8F, 0.8F);
            }
        });
        tag.putLong(FISHERMAN_NEXT_HOOK_TICK_TAG, gameTime + FISHERMAN_HOOK_INTERVAL_TICKS + level.random.nextInt(20 * 2));
    }

    private void tickOctoDisable(ServerLevel level) {
        if (!definition().has(PvZZombieSpecial.OCTO_DISABLE)) {
            return;
        }

        CompoundTag tag = getPersistentData();
        long gameTime = level.getGameTime();
        if (gameTime < tag.getLong(OCTO_NEXT_DISABLE_TICK_TAG)) {
            return;
        }

        Optional<SnowGolem> target = level.getEntitiesOfClass(SnowGolem.class, getBoundingBox().inflate(9.0D, 4.0D, 9.0D),
                        plant -> plant.isAlive() && PlantEntityManager.isPlant(plant) && !PlantEntityManager.isOctoDisabled(level, plant) && hasLineOfSight(plant))
                .stream()
                .min(Comparator.comparingDouble(this::distanceToSqr));
        target.ifPresent(plant -> {
            PlantEntityManager.applyOctoDisable(level, plant, this, OCTO_DISABLE_DURATION_TICKS);
            renderMagicLine(level, position().add(0.0D, 1.2D, 0.0D), plant.position().add(0.0D, 0.9D, 0.0D), ParticleTypes.SQUID_INK);
        });
        tag.putLong(OCTO_NEXT_DISABLE_TICK_TAG, gameTime + OCTO_DISABLE_INTERVAL_TICKS + level.random.nextInt(20 * 2));
    }

    private boolean isOnBeachWaterTile() {
        if (!(level() instanceof ServerLevel level)) {
            return isInWater();
        }
        BlockPos pos = blockPosition();
        return isInWater()
                || level.getBlockState(pos).is(net.minecraft.world.level.block.Blocks.WATER)
                || BigWaveBeachTideManager.isTileFlooded(level, pos)
                || BigWaveBeachTideManager.isTileFlooded(level, pos.below());
    }

    private void tickPirateStationaryState(ServerLevel level) {
        if (!definition().has(PvZZombieSpecial.STATIONARY)) {
            return;
        }
        getNavigation().stop();
        setDeltaMovement(Vec3.ZERO);
        setNoAi(false);
        if (getPersistentData().contains(GARDEN_CENTER_X_TAG)) {
            getLookControl().setLookAt(getPersistentData().getInt(GARDEN_CENTER_X_TAG) + 0.5D,
                    getPersistentData().getInt(GARDEN_CENTER_Y_TAG) + 1.0D,
                    getPersistentData().getInt(GARDEN_CENTER_Z_TAG) + 0.5D);
        }
    }

    private void tickPirateTurbulentWater(ServerLevel level) {
        if (!PvZZombieDefinitions.isPirateSeasZombie(this)
                || definition().has(PvZZombieSpecial.FLYING)
                || definition().has(PvZZombieSpecial.STATIONARY)
                || !PirateSeasPlankManager.isTurbulentWater(level, blockPosition())) {
            return;
        }

        CompoundTag tag = getPersistentData();
        long gameTime = level.getGameTime();
        if (gameTime < tag.getLong(TURBULENT_WATER_DAMAGE_TICK_TAG)) {
            return;
        }
        tag.putLong(TURBULENT_WATER_DAMAGE_TICK_TAG, gameTime + 20L);
        if (PvZZombieDefinitions.isGargantuarLike(this)) {
            hurt(level.damageSources().drown(), 50.0F);
            addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20 * 2, 1));
        } else {
            hurt(level.damageSources().drown(), 999.0F);
        }
        level.sendParticles(ParticleTypes.SPLASH, getX(), getY() + 0.4D, getZ(), 24, 0.45D, 0.25D, 0.45D, 0.08D);
        level.playSound(null, blockPosition(), SoundEvents.PLAYER_SPLASH_HIGH_SPEED, SoundSource.HOSTILE, 0.75F, 0.7F);
    }

    private void tickBarrelRoller(ServerLevel level) {
        if (!definition().has(PvZZombieSpecial.BARREL_ROLLER) || !getPersistentData().contains(GARDEN_CENTER_X_TAG)) {
            return;
        }

        CompoundTag tag = getPersistentData();
        long gameTime = level.getGameTime();
        if (gameTime < tag.getLong(BARREL_NEXT_ROLL_TICK_TAG)) {
            return;
        }

        spawnSummonedZombies(level, "barrel_obstacle", 1, ParticleTypes.CAMPFIRE_COSY_SMOKE, SoundEvents.BARREL_OPEN);
        tag.putLong(BARREL_NEXT_ROLL_TICK_TAG, gameTime + BARREL_ROLL_INTERVAL_TICKS + level.random.nextInt(20 * 3));
    }

    private void tickBarrelObstacle(ServerLevel level) {
        if (!definition().has(PvZZombieSpecial.BARREL_OBSTACLE) || !getPersistentData().contains(GARDEN_CENTER_X_TAG)) {
            return;
        }
        if (PirateSeasPlankManager.isTurbulentWater(level, blockPosition())) {
            discard();
            level.sendParticles(ParticleTypes.SPLASH, getX(), getY() + 0.5D, getZ(), 18, 0.35D, 0.2D, 0.35D, 0.05D);
            return;
        }
        Vec3 towardGarden = vectorTowardGarden().orElse(horizontalForward());
        if (tickCount % 5 == 0 && PirateSeasPlankManager.canPirateZombieTraverseTile(level, BlockPos.containing(position().add(towardGarden.scale(0.8D))), false)) {
            setDeltaMovement(getDeltaMovement().add(towardGarden.scale(0.08D)));
        }
    }

    private void tickSwashbucklerSwing(ServerLevel level) {
        CompoundTag tag = getPersistentData();
        if (!definition().has(PvZZombieSpecial.SWASHBUCKLER)
                || tag.getBoolean(SWASHBUCKLER_SWUNG_TAG)
                || !tag.contains(GARDEN_CENTER_X_TAG)
                || tickCount < 20) {
            return;
        }

        findSwashbucklerLandingTile(level).ifPresent(landing -> {
            tag.putBoolean(SWASHBUCKLER_SWUNG_TAG, true);
            Vec3 start = position().add(0.0D, 1.0D, 0.0D);
            teleportTo(landing.getX() + 0.5D, landing.getY(), landing.getZ() + 0.5D);
            setDeltaMovement(Vec3.ZERO);
            renderMagicLine(level, start, position().add(0.0D, 1.0D, 0.0D), ParticleTypes.CLOUD);
            level.sendParticles(ParticleTypes.CLOUD, getX(), getY() + 0.4D, getZ(), 18, 0.35D, 0.15D, 0.35D, 0.04D);
            level.playSound(null, blockPosition(), SoundEvents.LEASH_KNOT_PLACE, SoundSource.HOSTILE, 0.8F, 1.2F);
        });
    }

    private Optional<BlockPos> findSwashbucklerLandingTile(ServerLevel level) {
        BlockPos center = new BlockPos(getPersistentData().getInt(GARDEN_CENTER_X_TAG), getPersistentData().getInt(GARDEN_CENTER_Y_TAG), getPersistentData().getInt(GARDEN_CENTER_Z_TAG));
        BlockPos best = null;
        double currentDistance = blockPosition().distSqr(center);
        double bestDistance = currentDistance;
        for (int dx = -8; dx <= 8; dx++) {
            for (int dz = -8; dz <= 8; dz++) {
                BlockPos candidate = center.offset(dx, 0, dz);
                double distance = candidate.distSqr(center);
                if (distance >= bestDistance || distance > currentDistance || !PirateSeasPlankManager.isPirateSeasPlankTile(level, candidate)) {
                    continue;
                }
                if (!canSwashbucklerLandAt(level, candidate)) {
                    continue;
                }
                best = candidate;
                bestDistance = distance;
            }
        }
        return Optional.ofNullable(best);
    }

    private boolean canSwashbucklerLandAt(ServerLevel level, BlockPos pos) {
        return PirateSeasPlankManager.isPirateSeasPlankTile(level, pos)
                && level.getBlockState(pos).getCollisionShape(level, pos).isEmpty()
                && level.getBlockState(pos.above()).getCollisionShape(level, pos.above()).isEmpty()
                && level.getEntities(this, new AABB(pos).inflate(0.2D, 0.0D, 0.2D)).isEmpty();
    }

    private void tickPelicanDrop(ServerLevel level) {
        CompoundTag tag = getPersistentData();
        if (!definition().has(PvZZombieSpecial.PELICAN_DROPPER)
                || tag.getBoolean(PELICAN_DROPPED_IMP_TAG)
                || !tag.contains(GARDEN_CENTER_X_TAG)
                || tickCount < 40) {
            return;
        }

        findPirateImpLanding(level, 7).ifPresent(landing -> {
            tag.putBoolean(PELICAN_DROPPED_IMP_TAG, true);
            spawnPirateImpAt(level, landing, ParticleTypes.CLOUD, SoundEvents.PARROT_FLY);
        });
    }

    private void tickImpCannon(ServerLevel level) {
        if (!definition().has(PvZZombieSpecial.IMP_CANNON) || !getPersistentData().contains(GARDEN_CENTER_X_TAG)) {
            return;
        }

        CompoundTag tag = getPersistentData();
        long gameTime = level.getGameTime();
        if (gameTime < tag.getLong(IMP_CANNON_NEXT_LAUNCH_TICK_TAG) || tag.getInt(IMP_CANNON_LAUNCH_COUNT_TAG) >= IMP_CANNON_MAX_LAUNCHES) {
            return;
        }

        findPirateImpLanding(level, 7).ifPresent(landing -> {
            spawnPirateImpAt(level, landing, ParticleTypes.EXPLOSION, SoundEvents.GENERIC_EXPLODE);
            tag.putInt(IMP_CANNON_LAUNCH_COUNT_TAG, tag.getInt(IMP_CANNON_LAUNCH_COUNT_TAG) + 1);
        });
        tag.putLong(IMP_CANNON_NEXT_LAUNCH_TICK_TAG, gameTime + IMP_CANNON_LAUNCH_INTERVAL_TICKS + level.random.nextInt(20 * 2));
    }

    private Optional<BlockPos> findPirateImpLanding(ServerLevel level, int radius) {
        if (!getPersistentData().contains(GARDEN_CENTER_X_TAG)) {
            return Optional.empty();
        }
        BlockPos center = new BlockPos(getPersistentData().getInt(GARDEN_CENTER_X_TAG), getPersistentData().getInt(GARDEN_CENTER_Y_TAG), getPersistentData().getInt(GARDEN_CENTER_Z_TAG));
        for (int attempt = 0; attempt < 36; attempt++) {
            BlockPos candidate = center.offset(level.random.nextInt(radius * 2 + 1) - radius, 0, level.random.nextInt(radius * 2 + 1) - radius);
            if (PirateSeasPlankManager.canLaunchPirateImpTo(level, candidate)) {
                return Optional.of(candidate);
            }
        }
        return Optional.empty();
    }

    private void spawnPirateImpAt(ServerLevel level, BlockPos landing, net.minecraft.core.particles.ParticleOptions particle, net.minecraft.sounds.SoundEvent sound) {
        Optional.ofNullable(ModEntities.ZOMBIES.get("pirate_imp"))
                .map(registryObject -> registryObject.get())
                .ifPresent(impType -> {
                    PvZZombieEntity imp = impType.create(level);
                    if (imp == null) {
                        return;
                    }
                    imp.moveTo(landing.getX() + 0.5D, landing.getY(), landing.getZ() + 0.5D, getYRot(), 0.0F);
                    imp.getPersistentData().putBoolean("PvZWaveZombie", getPersistentData().getBoolean("PvZWaveZombie"));
                    copyGardenCenterTo(imp);
                    imp.finalizeSpawn(level, level.getCurrentDifficultyAt(landing), MobSpawnType.EVENT, null, null);
                    level.addFreshEntity(imp);
                    imp.configureForWave(0.13D);
                });
        level.sendParticles(particle, landing.getX() + 0.5D, landing.getY() + 0.5D, landing.getZ() + 0.5D, 18, 0.4D, 0.3D, 0.4D, 0.04D);
        level.playSound(null, landing, sound, SoundSource.HOSTILE, 0.75F, 1.0F);
    }

    private void tickPirateCaptainSupport(ServerLevel level) {
        if (!definition().has(PvZZombieSpecial.PIRATE_CAPTAIN)) {
            return;
        }

        CompoundTag tag = getPersistentData();
        long gameTime = level.getGameTime();
        if (gameTime < tag.getLong(PIRATE_CAPTAIN_NEXT_SUPPORT_TICK_TAG)) {
            return;
        }

        for (PvZZombieEntity zombie : level.getEntitiesOfClass(PvZZombieEntity.class, getBoundingBox().inflate(6.0D, 2.0D, 6.0D),
                zombie -> zombie.isAlive() && zombie != this && PvZZombieDefinitions.isPirateSeasZombie(zombie) && !PvZZombieDefinitions.isGargantuarLike(zombie))) {
            zombie.getPersistentData().putLong(PIRATE_CAPTAIN_BUFF_END_TICK_TAG, gameTime + PIRATE_CAPTAIN_SUPPORT_DURATION_TICKS);
            zombie.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, PIRATE_CAPTAIN_SUPPORT_DURATION_TICKS, 0, false, true));
            zombie.setAttribute(Attributes.MOVEMENT_SPEED, zombie.movementSpeedFor(zombie.definition()));
            level.sendParticles(ParticleTypes.ENCHANT, zombie.getX(), zombie.getY() + 1.1D, zombie.getZ(), 4, 0.25D, 0.35D, 0.25D, 0.01D);
        }
        level.playSound(null, blockPosition(), SoundEvents.GOAT_HORN_SOUND_VARIANTS.get(0).get(), SoundSource.HOSTILE, 0.8F, 1.15F);
        tag.putLong(PIRATE_CAPTAIN_NEXT_SUPPORT_TICK_TAG, gameTime + PIRATE_CAPTAIN_SUPPORT_INTERVAL_TICKS);
    }

    private void tickAllStarTackle(ServerLevel level) {
        if (!definition().has(PvZZombieSpecial.ALL_STAR_TACKLE) || !getPersistentData().contains(GARDEN_CENTER_X_TAG)) {
            return;
        }

        CompoundTag tag = getPersistentData();
        Vec3 towardGarden = vectorTowardGarden().orElse(horizontalForward());
        if (tag.getBoolean(ALL_STAR_TACKLE_READY_TAG)) {
            Optional<SnowGolem> plantTarget = level.getEntitiesOfClass(SnowGolem.class, getBoundingBox().inflate(1.8D, 0.8D, 1.8D), plant -> plant.isAlive() && PlantEntityManager.isPlant(plant))
                    .stream()
                    .filter(plant -> isInFront(plant.position(), towardGarden))
                    .min(Comparator.comparingDouble(this::distanceToSqr));
            plantTarget.ifPresent(plant -> {
                plant.hurt(level.damageSources().mobAttack(this), 18.0F);
                tag.putBoolean(ALL_STAR_TACKLE_READY_TAG, false);
                setAttribute(Attributes.MOVEMENT_SPEED, movementSpeedFor(definition()));
                setDeltaMovement(getDeltaMovement().add(towardGarden.scale(0.45D)));
                level.sendParticles(ParticleTypes.CRIT, plant.getX(), plant.getY() + 0.8D, plant.getZ(), 12, 0.3D, 0.35D, 0.3D, 0.04D);
                level.playSound(null, blockPosition(), SoundEvents.IRON_GOLEM_ATTACK, SoundSource.HOSTILE, 0.9F, 0.8F);
            });
        }

        long gameTime = level.getGameTime();
        if (gameTime < tag.getLong(ALL_STAR_NEXT_IMP_KICK_TICK_TAG)) {
            return;
        }
        Optional<PvZZombieEntity> imp = level.getEntitiesOfClass(PvZZombieEntity.class, getBoundingBox().inflate(3.0D, 1.5D, 3.0D),
                        zombie -> zombie.isAlive() && "super_fan_imp".equals(zombie.definition().id()))
                .stream()
                .min(Comparator.comparingDouble(this::distanceToSqr));
        imp.ifPresent(superFan -> {
            superFan.setDeltaMovement(superFan.getDeltaMovement().add(towardGarden.scale(1.0D).add(0.0D, 0.2D, 0.0D)));
            superFan.getPersistentData().putLong(SUPER_FAN_EXPLODE_TICK_TAG, gameTime + SUPER_FAN_FUSE_TICKS);
            level.sendParticles(ParticleTypes.CLOUD, superFan.getX(), superFan.getY() + 0.5D, superFan.getZ(), 16, 0.25D, 0.15D, 0.25D, 0.05D);
            level.playSound(null, superFan.blockPosition(), SoundEvents.ARMOR_EQUIP_LEATHER, SoundSource.HOSTILE, 0.9F, 0.65F);
        });
        tag.putLong(ALL_STAR_NEXT_IMP_KICK_TICK_TAG, gameTime + ALL_STAR_IMP_KICK_INTERVAL_TICKS);
    }

    private void tickSuperFanImpExplosion(ServerLevel level) {
        if (!definition().has(PvZZombieSpecial.SUPER_FAN_EXPLODE)) {
            return;
        }

        CompoundTag tag = getPersistentData();
        long gameTime = level.getGameTime();
        boolean nearPlant = level.getEntitiesOfClass(SnowGolem.class, getBoundingBox().inflate(1.5D, 0.8D, 1.5D), plant -> plant.isAlive() && PlantEntityManager.isPlant(plant))
                .stream()
                .findAny()
                .isPresent();
        boolean nearTotem = tag.contains(GARDEN_CENTER_X_TAG)
                && distanceToSqr(tag.getInt(GARDEN_CENTER_X_TAG) + 0.5D, tag.getInt(GARDEN_CENTER_Y_TAG) + 0.5D, tag.getInt(GARDEN_CENTER_Z_TAG) + 0.5D) <= 6.25D;
        if ((nearPlant || nearTotem) && !tag.contains(SUPER_FAN_EXPLODE_TICK_TAG)) {
            tag.putLong(SUPER_FAN_EXPLODE_TICK_TAG, gameTime + SUPER_FAN_FUSE_TICKS);
        }
        if (!tag.contains(SUPER_FAN_EXPLODE_TICK_TAG) || gameTime < tag.getLong(SUPER_FAN_EXPLODE_TICK_TAG)) {
            return;
        }

        for (SnowGolem plant : level.getEntitiesOfClass(SnowGolem.class, getBoundingBox().inflate(SUPER_FAN_EXPLOSION_RADIUS, 1.2D, SUPER_FAN_EXPLOSION_RADIUS),
                plant -> plant.isAlive() && PlantEntityManager.isPlant(plant))) {
            plant.hurt(level.damageSources().explosion(null, this), 18.0F);
        }
        level.sendParticles(ParticleTypes.EXPLOSION, getX(), getY() + 0.4D, getZ(), 2, 0.3D, 0.2D, 0.3D, 0.0D);
        level.sendParticles(ParticleTypes.FLAME, getX(), getY() + 0.5D, getZ(), 18, 0.55D, 0.35D, 0.55D, 0.04D);
        level.playSound(null, blockPosition(), SoundEvents.GENERIC_EXPLODE, SoundSource.HOSTILE, 0.8F, 1.25F);
        discard();
    }

    private void tickRallySupport(ServerLevel level) {
        if (!definition().has(PvZZombieSpecial.RALLY_SUPPORT)) {
            return;
        }

        CompoundTag tag = getPersistentData();
        long gameTime = level.getGameTime();
        if (gameTime < tag.getLong(RALLY_NEXT_SUPPORT_TICK_TAG)) {
            return;
        }

        for (PvZZombieEntity zombie : level.getEntitiesOfClass(PvZZombieEntity.class, getBoundingBox().inflate(6.0D, 2.0D, 6.0D),
                zombie -> zombie.isAlive() && zombie != this && !PvZZombieDefinitions.isGargantuarLike(zombie))) {
            zombie.getPersistentData().putLong(RALLY_BUFF_END_TICK_TAG, gameTime + RALLY_SUPPORT_DURATION_TICKS);
            zombie.setAttribute(Attributes.MOVEMENT_SPEED, zombie.movementSpeedFor(zombie.definition()));
            level.sendParticles(ParticleTypes.HAPPY_VILLAGER, zombie.getX(), zombie.getY() + 1.1D, zombie.getZ(), 3, 0.25D, 0.35D, 0.25D, 0.01D);
        }
        level.sendParticles(ParticleTypes.HAPPY_VILLAGER, getX(), getY() + 1.2D, getZ(), 14, 0.6D, 0.4D, 0.6D, 0.02D);
        level.playSound(null, blockPosition(), SoundEvents.RAID_HORN.value(), SoundSource.HOSTILE, 0.65F, 1.55F);
        tag.putLong(RALLY_NEXT_SUPPORT_TICK_TAG, gameTime + RALLY_SUPPORT_INTERVAL_TICKS);
    }

    private void pulseNearbyNeonZombies(ServerLevel level, double radius, boolean includeSelf) {
        for (PvZZombieEntity zombie : level.getEntitiesOfClass(PvZZombieEntity.class, getBoundingBox().inflate(radius, 2.0D, radius), zombie -> zombie.isAlive() && (includeSelf || zombie != this) && PvZZombieDefinitions.isNeonZombie(zombie))) {
            applyNeonMusicBoost(level, zombie, MUSIC_BOOST_DURATION_TICKS, 0.2F);
        }
        level.sendParticles(ParticleTypes.NOTE, getX(), getY() + 1.4D, getZ(), 16, radius * 0.25D, 0.5D, radius * 0.25D, 0.0D);
    }

    private void spawnSummonedZombies(ServerLevel level, String zombieId, int count, net.minecraft.core.particles.ParticleOptions particle, net.minecraft.sounds.SoundEvent sound) {
        Optional.ofNullable(ModEntities.ZOMBIES.get(zombieId))
                .map(registryObject -> registryObject.get())
                .ifPresent(entityType -> {
                    for (int i = 0; i < count; i++) {
                        PvZZombieEntity summoned = entityType.create(level);
                        if (summoned == null) {
                            continue;
                        }
                        double x = getX() + (level.random.nextDouble() - 0.5D) * 2.0D;
                        double z = getZ() + (level.random.nextDouble() - 0.5D) * 2.0D;
                        summoned.moveTo(x, getY(), z, getYRot(), 0.0F);
                        summoned.getPersistentData().putBoolean("PvZWaveZombie", getPersistentData().getBoolean("PvZWaveZombie"));
                        copyGardenCenterTo(summoned);
                        summoned.finalizeSpawn(level, level.getCurrentDifficultyAt(summoned.blockPosition()), MobSpawnType.EVENT, null, null);
                        level.addFreshEntity(summoned);
                        summoned.configureForWave(0.13D);
                    }
                });
        level.sendParticles(particle, getX(), getY() + 1.0D, getZ(), 18, 0.5D, 0.4D, 0.5D, 0.02D);
        level.playSound(null, blockPosition(), sound, SoundSource.HOSTILE, 0.9F, 0.9F);
    }

    private static boolean isDarkAgesSupportTarget(PvZZombieEntity zombie) {
        String id = zombie.definition().id();
        return "peasant_zombie".equals(id)
                || "conehead_peasant".equals(id)
                || "buckethead_peasant".equals(id)
                || "jester_zombie".equals(id)
                || "wizard_zombie".equals(id)
                || "dragon_imp".equals(id)
                || "knight_zombie".equals(id);
    }

    private static void renderMagicLine(ServerLevel level, Vec3 start, Vec3 end, net.minecraft.core.particles.ParticleOptions particle) {
        Vec3 delta = end.subtract(start);
        int steps = Math.max(2, (int) (delta.length() * 4.0D));
        for (int i = 0; i <= steps; i++) {
            Vec3 point = start.add(delta.scale(i / (double) steps));
            level.sendParticles(particle, point.x, point.y, point.z, 1, 0.02D, 0.02D, 0.02D, 0.0D);
        }
    }

    private void copyGardenCenterTo(PvZZombieEntity other) {
        CompoundTag tag = getPersistentData();
        if (!tag.contains(GARDEN_CENTER_X_TAG)) {
            return;
        }
        other.getPersistentData().putInt(GARDEN_CENTER_X_TAG, tag.getInt(GARDEN_CENTER_X_TAG));
        other.getPersistentData().putInt(GARDEN_CENTER_Y_TAG, tag.getInt(GARDEN_CENTER_Y_TAG));
        other.getPersistentData().putInt(GARDEN_CENTER_Z_TAG, tag.getInt(GARDEN_CENTER_Z_TAG));
    }

    private Optional<Vec3> vectorTowardGarden() {
        CompoundTag tag = getPersistentData();
        if (!tag.contains(GARDEN_CENTER_X_TAG)) {
            return Optional.empty();
        }
        Vec3 towardGarden = new Vec3(tag.getInt(GARDEN_CENTER_X_TAG) + 0.5D - getX(), 0.0D, tag.getInt(GARDEN_CENTER_Z_TAG) + 0.5D - getZ());
        if (towardGarden.lengthSqr() < 1.0E-4D) {
            return Optional.empty();
        }
        return Optional.of(towardGarden.normalize());
    }

    private Vec3 horizontalForward() {
        Vec3 look = getLookAngle().multiply(1.0D, 0.0D, 1.0D);
        if (look.lengthSqr() < 1.0E-4D && getTarget() != null) {
            look = getTarget().position().subtract(position()).multiply(1.0D, 0.0D, 1.0D);
        }
        if (look.lengthSqr() < 1.0E-4D) {
            look = Vec3.directionFromRotation(0.0F, getYRot()).multiply(1.0D, 0.0D, 1.0D);
        }
        return look.normalize();
    }

    private boolean isInFront(Vec3 targetPosition, Vec3 forward) {
        Vec3 toTarget = targetPosition.subtract(position()).multiply(1.0D, 0.0D, 1.0D);
        return toTarget.lengthSqr() > 1.0E-4D && toTarget.normalize().dot(forward) > 0.35D;
    }

    private boolean isSafeLanding(ServerLevel level, BlockPos pos) {
        BlockState below = level.getBlockState(pos.below());
        return below.isFaceSturdy(level, pos.below(), net.minecraft.core.Direction.UP)
                && level.getBlockState(pos).getCollisionShape(level, pos).isEmpty()
                && level.getBlockState(pos.above()).getCollisionShape(level, pos.above()).isEmpty()
                && level.getEntities(this, new AABB(pos).inflate(0.2D, 0.0D, 0.2D)).isEmpty();
    }
}

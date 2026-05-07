package net.PvZModders.PvZMod.entity.custom;

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
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
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
        if (definition.has(PvZZombieSpecial.POLE_VAULT) && !tag.contains(POLE_VAULT_READY_TAG)) {
            tag.putBoolean(POLE_VAULT_READY_TAG, true);
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
        return tag.getDouble(WAVE_BASE_SPEED_TAG) * multiplier;
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
        if (!definition.has(PvZZombieSpecial.NEWSPAPER_RAGE)
                || tag.getBoolean(NEWSPAPER_RAGED_TAG)
                || getHealth() > NEWSPAPER_RAGE_HEALTH_THRESHOLD) {
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

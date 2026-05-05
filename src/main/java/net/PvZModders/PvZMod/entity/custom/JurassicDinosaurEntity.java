package net.PvZModders.PvZMod.entity.custom;

import net.PvZModders.PvZMod.entity.ModEntities;
import net.PvZModders.PvZMod.progression.dinosaur.DinosaurType;
import net.PvZModders.PvZMod.progression.seed.PlantEntityManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.sniffer.Sniffer;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JurassicDinosaurEntity extends Sniffer {
    private static final String DINOSAUR_TYPE_TAG = "PvZDinosaurType";
    private static final String CHARMED_TAG = "PvZDinosaurCharmed";
    private static final String CHARMED_UNTIL_TAG = "PvZDinosaurCharmedUntil";
    private static final String OWNER_UUID_TAG = "PvZDinosaurOwner";
    private static final String ACTIONS_LEFT_TAG = "PvZDinosaurActionsLeft";
    private static final String NEXT_ACTION_TICK_TAG = "PvZDinosaurNextActionTick";
    private static final String PET_TAG = "PvZDinosaurPet";
    private static final String TOTEM_X_TAG = "PvZDinosaurTotemX";
    private static final String TOTEM_Y_TAG = "PvZDinosaurTotemY";
    private static final String TOTEM_Z_TAG = "PvZDinosaurTotemZ";
    private static final int PERFUME_CHARM_TICKS = 20 * 60;
    private static final int PET_CHARM_TICKS = 20 * 60 * 5;

    public JurassicDinosaurEntity(EntityType<? extends Sniffer> entityType, Level level) {
        super(entityType, level);
    }

    public JurassicDinosaurEntity(Level level, double x, double y, double z, DinosaurType type) {
        super(ModEntities.JURASSIC_DINOSAUR.get(), level);
        setPos(x, y, z);
        initialize(type);
    }

    public void initialize(DinosaurType type) {
        CompoundTag tag = getPersistentData();
        tag.putString(DINOSAUR_TYPE_TAG, type.name());
        tag.putInt(ACTIONS_LEFT_TAG, type.actionLimit());
        tag.putLong(NEXT_ACTION_TICK_TAG, level().getGameTime() + 20L);
        setPersistenceRequired();
        updateDisplayName();
        if (getAttribute(Attributes.MAX_HEALTH) != null) {
            getAttribute(Attributes.MAX_HEALTH).setBaseValue(40.0D);
            setHealth(getMaxHealth());
        }
    }

    public void initializeForWave(int wave, BlockPos totemPos) {
        initialize(DinosaurType.forWave(wave, random));
        setTotemPos(totemPos);
    }

    public void initializePassive() {
        initialize(DinosaurType.passive(random));
    }

    public void charmFor(UUID owner, int ticks) {
        CompoundTag tag = getPersistentData();
        tag.putBoolean(CHARMED_TAG, true);
        tag.putLong(CHARMED_UNTIL_TAG, level().getGameTime() + ticks);
        tag.putUUID(OWNER_UUID_TAG, owner);
        tag.putInt(ACTIONS_LEFT_TAG, Math.max(tag.getInt(ACTIONS_LEFT_TAG), type().actionLimit()));
        updateDisplayName();
    }

    public void makePet(Player owner) {
        CompoundTag tag = getPersistentData();
        tag.putBoolean(PET_TAG, true);
        tag.putInt(ACTIONS_LEFT_TAG, 999);
        charmFor(owner.getUUID(), PET_CHARM_TICKS);
    }

    public boolean isCharmed() {
        CompoundTag tag = getPersistentData();
        if (!tag.getBoolean(CHARMED_TAG)) {
            return false;
        }
        if (tag.getBoolean(PET_TAG)) {
            return true;
        }
        if (level().getGameTime() <= tag.getLong(CHARMED_UNTIL_TAG)) {
            return true;
        }
        tag.putBoolean(CHARMED_TAG, false);
        updateDisplayName();
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        if (!(level() instanceof ServerLevel level)) {
            return;
        }

        if (getPersistentData().getString(DINOSAUR_TYPE_TAG).isEmpty()) {
            initializePassive();
        }
        tickDinosaurAction(level);
    }

    private void tickDinosaurAction(ServerLevel level) {
        CompoundTag tag = getPersistentData();
        if (level.getGameTime() < tag.getLong(NEXT_ACTION_TICK_TAG)) {
            return;
        }

        DinosaurType type = type();
        boolean acted = switch (type) {
            case RAPTOR -> tickRaptor(level);
            case STEGOSAURUS -> tickStegosaurus(level);
            case PTEROSAUR -> tickPterosaur(level);
            case T_REX -> tickTRex(level);
            case ANKYLOSAURUS -> tickAnkylosaurus(level);
        };

        tag.putLong(NEXT_ACTION_TICK_TAG, level.getGameTime() + type.actionCooldownTicks());
        if (acted && !tag.getBoolean(PET_TAG)) {
            int actionsLeft = Math.max(0, tag.getInt(ACTIONS_LEFT_TAG) - 1);
            tag.putInt(ACTIONS_LEFT_TAG, actionsLeft);
            if (actionsLeft <= 0) {
                level.sendParticles(ParticleTypes.POOF, getX(), getY() + 1.0D, getZ(), 18, 0.7D, 0.5D, 0.7D, 0.03D);
                discard();
            }
        }
    }

    private boolean tickRaptor(ServerLevel level) {
        Optional<Zombie> target = nearestZombie(8.0D);
        if (target.isEmpty()) {
            wanderNearTotem();
            return false;
        }

        Zombie zombie = target.get();
        Vec3 push = directionRelativeToGarden(zombie.position(), isCharmed()).scale(isCharmed() ? 4.0D : 3.5D);
        zombie.setDeltaMovement(zombie.getDeltaMovement().add(push.x * 0.18D, 0.28D, push.z * 0.18D));
        zombie.hurt(level.damageSources().mobAttack(this), 1.0F);
        level.sendParticles(ParticleTypes.CRIT, zombie.getX(), zombie.getY() + 1.0D, zombie.getZ(), 12, 0.35D, 0.35D, 0.35D, 0.05D);
        return true;
    }

    private boolean tickStegosaurus(ServerLevel level) {
        List<Zombie> targets = nearbyZombies(9.0D).stream().limit(3).toList();
        if (targets.isEmpty()) {
            wanderNearTotem();
            return false;
        }

        for (Zombie zombie : targets) {
            if (isCharmed()) {
                zombie.hurt(level.damageSources().mobAttack(this), 18.0F);
                for (Zombie splash : level.getEntitiesOfClass(Zombie.class, zombie.getBoundingBox().inflate(2.0D), Zombie::isAlive)) {
                    if (splash != zombie) {
                        splash.hurt(level.damageSources().mobAttack(this), 6.0F);
                    }
                }
            } else {
                moveZombieSafely(level, zombie, directionRelativeToGarden(zombie.position(), false), 4.0D);
            }
            level.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, zombie.getX(), zombie.getY() + 0.6D, zombie.getZ(), 8, 0.35D, 0.2D, 0.35D, 0.02D);
        }
        return true;
    }

    private boolean tickPterosaur(ServerLevel level) {
        Optional<Zombie> target = nearestZombie(10.0D);
        if (target.isEmpty()) {
            wanderNearTotem();
            return false;
        }

        Zombie zombie = target.get();
        if (isCharmed()) {
            zombie.hurt(level.damageSources().mobAttack(this), 35.0F);
            moveZombieSafely(level, zombie, directionRelativeToGarden(zombie.position(), true), 7.0D);
        } else {
            moveZombieSafely(level, zombie, directionRelativeToGarden(zombie.position(), false), 5.0D);
        }
        level.sendParticles(ParticleTypes.CLOUD, zombie.getX(), zombie.getY() + 1.0D, zombie.getZ(), 18, 0.45D, 0.45D, 0.45D, 0.04D);
        return true;
    }

    private boolean tickTRex(ServerLevel level) {
        List<Zombie> targets = nearbyZombies(10.0D);
        if (targets.isEmpty()) {
            wanderNearTotem();
            return false;
        }

        if (isCharmed()) {
            Zombie target = targets.get(0);
            target.hurt(level.damageSources().mobAttack(this), 40.0F);
            level.sendParticles(ParticleTypes.DAMAGE_INDICATOR, target.getX(), target.getY() + 1.0D, target.getZ(), 12, 0.4D, 0.4D, 0.4D, 0.05D);
        } else {
            for (Zombie zombie : targets) {
                zombie.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20 * 7, 1));
            }
            level.sendParticles(ParticleTypes.ANGRY_VILLAGER, getX(), getY() + 1.5D, getZ(), 24, 1.5D, 0.6D, 1.5D, 0.02D);
        }
        return true;
    }

    private boolean tickAnkylosaurus(ServerLevel level) {
        Optional<Zombie> target = nearestZombie(8.0D);
        if (target.isEmpty()) {
            wanderNearTotem();
            return false;
        }

        Zombie zombie = target.get();
        Vec3 direction = directionRelativeToGarden(zombie.position(), isCharmed());
        zombie.setDeltaMovement(zombie.getDeltaMovement().add(direction.x * 0.28D, 0.25D, direction.z * 0.28D));
        zombie.hurt(level.damageSources().mobAttack(this), isCharmed() ? 30.0F : 4.0F);
        if (!isCharmed()) {
            damageNearbyPlant(level, zombie);
        }
        level.sendParticles(ParticleTypes.EXPLOSION, zombie.getX(), zombie.getY() + 0.5D, zombie.getZ(), 2, 0.2D, 0.2D, 0.2D, 0.0D);
        return true;
    }

    private Optional<Zombie> nearestZombie(double range) {
        return nearbyZombies(range).stream().findFirst();
    }

    private List<Zombie> nearbyZombies(double range) {
        AABB area = getBoundingBox().inflate(range, 3.0D, range);
        return level().getEntitiesOfClass(Zombie.class, area, Zombie::isAlive)
                .stream()
                .sorted(Comparator.comparingDouble(this::distanceToSqr))
                .toList();
    }

    private Vec3 directionRelativeToGarden(Vec3 zombiePos, boolean awayFromGarden) {
        Vec3 totem = totemCenter().orElse(position());
        Vec3 towardGarden = totem.subtract(zombiePos).multiply(1.0D, 0.0D, 1.0D);
        if (towardGarden.lengthSqr() < 1.0E-4D) {
            towardGarden = getLookAngle().multiply(1.0D, 0.0D, 1.0D);
        }
        Vec3 normalized = towardGarden.lengthSqr() < 1.0E-4D ? new Vec3(0.0D, 0.0D, 1.0D) : towardGarden.normalize();
        return awayFromGarden ? normalized.scale(-1.0D) : normalized;
    }

    private Optional<Vec3> totemCenter() {
        CompoundTag tag = getPersistentData();
        if (!tag.contains(TOTEM_X_TAG)) {
            return Optional.empty();
        }
        return Optional.of(new Vec3(tag.getInt(TOTEM_X_TAG) + 0.5D, tag.getInt(TOTEM_Y_TAG) + 0.5D, tag.getInt(TOTEM_Z_TAG) + 0.5D));
    }

    private void moveZombieSafely(ServerLevel level, Zombie zombie, Vec3 direction, double distance) {
        Vec3 movement = direction.normalize().scale(distance);
        BlockPos raw = BlockPos.containing(zombie.position().add(movement));
        BlockPos surface = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, raw);
        if (level.getBlockState(surface).isAir() && level.getBlockState(surface.above()).isAir()) {
            zombie.teleportTo(surface.getX() + 0.5D, surface.getY(), surface.getZ() + 0.5D);
        }
    }

    private void damageNearbyPlant(ServerLevel level, Zombie zombie) {
        for (LivingEntity plant : level.getEntitiesOfClass(LivingEntity.class, zombie.getBoundingBox().inflate(0.8D), PlantEntityManager::isPlant)) {
            plant.hurt(level.damageSources().mobAttack(this), 6.0F);
            return;
        }
    }

    private void wanderNearTotem() {
        if (level().getGameTime() % 40L != 0L) {
            return;
        }
        Vec3 target = totemCenter().orElse(position()).add(random.nextInt(9) - 4, 0.0D, random.nextInt(9) - 4);
        getNavigation().moveTo(target.x, target.y, target.z, 0.85D);
    }

    private void setTotemPos(BlockPos pos) {
        CompoundTag tag = getPersistentData();
        tag.putInt(TOTEM_X_TAG, pos.getX());
        tag.putInt(TOTEM_Y_TAG, pos.getY());
        tag.putInt(TOTEM_Z_TAG, pos.getZ());
    }

    private DinosaurType type() {
        return DinosaurType.byName(getPersistentData().getString(DINOSAUR_TYPE_TAG));
    }

    private void updateDisplayName() {
        CompoundTag tag = getPersistentData();
        boolean charmed = tag.getBoolean(CHARMED_TAG)
                && (tag.getBoolean(PET_TAG) || level().getGameTime() <= tag.getLong(CHARMED_UNTIL_TAG));
        String prefix = charmed ? "Charmed " : "";
        setCustomName(Component.literal(prefix + type().displayName()));
        setCustomNameVisible(true);
    }
}

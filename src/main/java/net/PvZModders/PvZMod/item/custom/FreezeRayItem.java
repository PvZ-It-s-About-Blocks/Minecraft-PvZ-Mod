package net.PvZModders.PvZMod.item.custom;

import net.PvZModders.PvZMod.progression.seed.PlantEntityManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;

public class FreezeRayItem extends Item {
    private static final int COOLDOWN_TICKS = 20 * 5;
    private static final int SLOW_DURATION_TICKS = 20 * 5;
    private static final int MAX_TARGETS = 3;
    private static final double RANGE = 24.0D;
    private static final double RAY_WIDTH = 1.35D;
    private static final String ACTION_SLOW_EXPIRES_TAG = "PvZFreezeRayActionSlowExpires";

    public FreezeRayItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResultHolder.sidedSuccess(stack, true);
        }

        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResultHolder.fail(stack);
        }

        Vec3 start = player.getEyePosition();
        Vec3 look = player.getLookAngle().normalize();
        Vec3 end = start.add(look.scale(RANGE));
        List<LivingEntity> targets = getFreezeRayTargets(serverLevel, player, start, look, end);
        renderBeam(serverLevel, start, targets.isEmpty() ? end : targets.get(targets.size() - 1).position().add(0.0D, 0.8D, 0.0D), ParticleTypes.END_ROD);
        renderBeam(serverLevel, start, targets.isEmpty() ? end : targets.get(targets.size() - 1).position().add(0.0D, 0.8D, 0.0D), ParticleTypes.SNOWFLAKE);
        serverLevel.playSound(null, BlockPos.containing(player.position()), SoundEvents.GUARDIAN_ATTACK, SoundSource.PLAYERS, 0.75F, 1.35F);

        for (LivingEntity target : targets) {
            applyFreezeRaySlow(serverLevel, target);
        }
        player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
        return InteractionResultHolder.success(stack);
    }

    private static List<LivingEntity> getFreezeRayTargets(ServerLevel level, Player player, Vec3 start, Vec3 look, Vec3 end) {
        AABB search = new AABB(start, end).inflate(RAY_WIDTH);
        return level.getEntitiesOfClass(LivingEntity.class, search, entity -> canFreezeRayPierce(entity) && entity != player)
                .stream()
                .filter(entity -> isOnRay(start, look, entity))
                .sorted(Comparator.comparingDouble(entity -> start.distanceToSqr(entity.position())))
                .limit(MAX_TARGETS)
                .toList();
    }

    private static boolean canFreezeRayPierce(LivingEntity entity) {
        return entity.isAlive() && entity instanceof Monster && !PlantEntityManager.isPlant(entity);
    }

    private static boolean isOnRay(Vec3 start, Vec3 look, LivingEntity entity) {
        Vec3 toEntity = entity.position().add(0.0D, entity.getBbHeight() * 0.5D, 0.0D).subtract(start);
        double along = toEntity.dot(look);
        if (along < 0.0D || along > RANGE) {
            return false;
        }
        Vec3 closest = start.add(look.scale(along));
        return closest.distanceToSqr(entity.position().add(0.0D, entity.getBbHeight() * 0.5D, 0.0D)) <= RAY_WIDTH * RAY_WIDTH;
    }

    private static void applyFreezeRaySlow(ServerLevel level, LivingEntity entity) {
        entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, SLOW_DURATION_TICKS, 2));
        CompoundTag tag = entity.getPersistentData();
        tag.putLong(ACTION_SLOW_EXPIRES_TAG, level.getGameTime() + SLOW_DURATION_TICKS);
        level.sendParticles(ParticleTypes.SNOWFLAKE, entity.getX(), entity.getY() + 0.8D, entity.getZ(), 18, 0.35D, 0.45D, 0.35D, 0.03D);
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

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("Pierces up to 3 hostile mobs and slows them for 5 seconds."));
    }
}

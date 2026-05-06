package net.PvZModders.PvZMod.item.custom;

import net.PvZModders.PvZMod.PvZ2Mod;
import net.PvZModders.PvZMod.item.ModItems;
import net.PvZModders.PvZMod.progression.seed.PlantEntityManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
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
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Mod.EventBusSubscriber(modid = PvZ2Mod.MOD_ID)
public class TideShellItem extends Item {
    private static final int COOLDOWN_TICKS = 20 * 10;
    private static final double PULSE_RADIUS = 7.0D;

    public TideShellItem(Properties properties) {
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

        emitTideShellWave(serverLevel, player);
        player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
        return InteractionResultHolder.success(stack);
    }

    public static boolean hasTideShell(Player player) {
        for (ItemStack stack : player.getInventory().items) {
            if (stack.is(ModItems.TIDE_SHELL.get())) {
                return true;
            }
        }
        return player.getMainHandItem().is(ModItems.TIDE_SHELL.get()) || player.getOffhandItem().is(ModItems.TIDE_SHELL.get());
    }

    private static void emitTideShellWave(ServerLevel level, Player player) {
        AABB area = player.getBoundingBox().inflate(PULSE_RADIUS, 3.0D, PULSE_RADIUS);
        for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, area, entity -> entity.isAlive() && entity instanceof Monster && !PlantEntityManager.isPlant(entity))) {
            Vec3 push = entity.position().subtract(player.position()).multiply(1.0D, 0.0D, 1.0D);
            if (push.lengthSqr() < 1.0E-4D) {
                push = player.getLookAngle().multiply(1.0D, 0.0D, 1.0D);
            }
            double strength = isPushResistantEnemy(entity) ? 0.45D : 1.25D;
            entity.setDeltaMovement(entity.getDeltaMovement().add(push.normalize().scale(strength)).add(0.0D, 0.2D, 0.0D));
        }
        level.sendParticles(ParticleTypes.SPLASH, player.getX(), player.getY() + 0.8D, player.getZ(), 80, 2.5D, 0.4D, 2.5D, 0.12D);
        level.playSound(null, BlockPos.containing(player.position()), SoundEvents.PLAYER_SPLASH_HIGH_SPEED, SoundSource.PLAYERS, 1.0F, 1.0F);
    }

    private static boolean isPushResistantEnemy(LivingEntity entity) {
        return entity.getMaxHealth() >= 80.0F || entity.getPersistentData().getBoolean("PvZGargantuarLike");
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        if (event.phase != TickEvent.Phase.END || !hasTideShell(player)) {
            return;
        }

        if (player.isInWater()) {
            player.addEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 40, 0, true, false));
            player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 40, 0, true, false));
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("Improves water movement while carried."));
        tooltip.add(Component.literal("Right-click to push nearby hostile mobs away."));
    }
}

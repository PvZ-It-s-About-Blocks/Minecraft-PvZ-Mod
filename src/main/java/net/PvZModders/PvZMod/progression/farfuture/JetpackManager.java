package net.PvZModders.PvZMod.progression.farfuture;

import net.PvZModders.PvZMod.PvZ2Mod;
import net.PvZModders.PvZMod.item.ModItems;
import net.PvZModders.PvZMod.progression.sun.SunManager;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = PvZ2Mod.MOD_ID)
public final class JetpackManager {
    private static final String THRUSTING_TAG = "PvZJetpackThrusting";
    private static final String LAST_INPUT_TICK_TAG = "PvZJetpackLastInputTick";
    private static final String NEXT_SUN_TICK_TAG = "PvZJetpackNextSunTick";
    private static final int SUN_COST = 5;
    private static final int SUN_COST_INTERVAL_TICKS = 5;
    private static final int INPUT_TIMEOUT_TICKS = 8;
    private static final double THRUST = 0.18D;
    private static final double MAX_UPWARD_VELOCITY = 0.85D;

    private JetpackManager() {
    }

    public static void setThrusting(ServerPlayer player, boolean thrusting) {
        player.getPersistentData().putBoolean(THRUSTING_TAG, thrusting);
        player.getPersistentData().putLong(LAST_INPUT_TICK_TAG, player.serverLevel().getGameTime());
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.player instanceof ServerPlayer player)) {
            return;
        }

        if (!player.getPersistentData().getBoolean(THRUSTING_TAG)) {
            return;
        }

        long gameTime = player.serverLevel().getGameTime();
        if (gameTime - player.getPersistentData().getLong(LAST_INPUT_TICK_TAG) > INPUT_TIMEOUT_TICKS || !hasJetpack(player)) {
            player.getPersistentData().putBoolean(THRUSTING_TAG, false);
            return;
        }

        if (gameTime >= player.getPersistentData().getLong(NEXT_SUN_TICK_TAG)) {
            if (!SunManager.spendSun(player, SUN_COST)) {
                player.getPersistentData().putBoolean(THRUSTING_TAG, false);
                return;
            }
            player.getPersistentData().putLong(NEXT_SUN_TICK_TAG, gameTime + SUN_COST_INTERVAL_TICKS);
        }

        Vec3 movement = player.getDeltaMovement();
        player.setDeltaMovement(movement.x, Math.min(MAX_UPWARD_VELOCITY, movement.y + THRUST), movement.z);
        player.hurtMarked = true;
        player.fallDistance = 0.0F;
        player.serverLevel().sendParticles(ParticleTypes.FLAME, player.getX(), player.getY() + 0.15D, player.getZ(), 5, 0.2D, 0.05D, 0.2D, 0.02D);
        if (gameTime % 8L == 0L) {
            player.serverLevel().playSound(null, player.blockPosition(), SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, 0.25F, 1.5F);
        }
    }

    private static boolean hasJetpack(Player player) {
        for (ItemStack stack : player.getInventory().items) {
            if (stack.is(ModItems.JETPACK.get())) {
                return true;
            }
        }
        for (ItemStack stack : player.getInventory().armor) {
            if (stack.is(ModItems.JETPACK.get())) {
                return true;
            }
        }
        return player.getMainHandItem().is(ModItems.JETPACK.get()) || player.getOffhandItem().is(ModItems.JETPACK.get());
    }
}

package net.PvZModders.PvZMod.progression.greenhouse;

import net.PvZModders.PvZMod.item.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.Optional;

public final class GreenhouseCoinManager {
    public static final String PLAYER_COINS_TAG = "PvZCoins";
    public static final String COIN_DROP_TAG = "PvZCoinDrop";
    public static final String COIN_VALUE_TAG = "PvZCoinValue";

    private GreenhouseCoinManager() {
    }

    public static int getCoins(Player player) {
        return player.getPersistentData().getInt(PLAYER_COINS_TAG);
    }

    public static void addCoins(Player player, int amount) {
        if (amount <= 0) {
            return;
        }
        CompoundTag tag = player.getPersistentData();
        tag.putInt(PLAYER_COINS_TAG, Math.max(0, tag.getInt(PLAYER_COINS_TAG) + amount));
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.displayClientMessage(Component.literal("Coins: " + tag.getInt(PLAYER_COINS_TAG)).withStyle(ChatFormatting.GOLD), true);
        }
    }

    public static void addCoinsToNearestPlayer(ServerLevel level, Vec3 origin, int amount) {
        nearestPlayer(level, origin, 64.0D).ifPresentOrElse(
                player -> addCoins(player, amount),
                () -> dropCoin(level, origin, amount)
        );
    }

    public static void dropCoin(ServerLevel level, Vec3 origin, int amount) {
        if (amount <= 0) {
            return;
        }
        ItemStack stack = new ItemStack(ModItems.COIN.get(), 1);
        stack.getOrCreateTag().putBoolean(COIN_DROP_TAG, true);
        stack.getOrCreateTag().putInt(COIN_VALUE_TAG, amount);
        ItemEntity item = new ItemEntity(level, origin.x, origin.y, origin.z, stack);
        item.getPersistentData().putBoolean(COIN_DROP_TAG, true);
        item.getPersistentData().putInt(COIN_VALUE_TAG, amount);
        item.setDefaultPickUpDelay();
        level.addFreshEntity(item);
    }

    public static boolean isCoinDrop(ItemEntity item) {
        return item.getItem().is(ModItems.COIN.get())
                || item.getPersistentData().getBoolean(COIN_DROP_TAG)
                || item.getItem().getOrCreateTag().getBoolean(COIN_DROP_TAG);
    }

    public static int coinValue(ItemEntity item) {
        int entityValue = item.getPersistentData().getInt(COIN_VALUE_TAG);
        int stackValue = item.getItem().getOrCreateTag().getInt(COIN_VALUE_TAG);
        return Math.max(1, Math.max(entityValue, stackValue));
    }

    public static int collectCoinsNearby(ServerLevel level, Vec3 origin, double range) {
        Optional<ServerPlayer> collector = nearestPlayer(level, origin, 64.0D);
        if (collector.isEmpty()) {
            return 0;
        }

        int collected = 0;
        AABB area = AABB.ofSize(origin, range * 2.0D, range * 2.0D, range * 2.0D);
        for (ItemEntity item : level.getEntitiesOfClass(ItemEntity.class, area, GreenhouseCoinManager::isCoinDrop)) {
            int value = coinValue(item);
            item.discard();
            addCoins(collector.get(), value);
            collected += value;
        }
        return collected;
    }

    private static Optional<ServerPlayer> nearestPlayer(ServerLevel level, Vec3 origin, double range) {
        AABB area = AABB.ofSize(origin, range * 2.0D, range * 2.0D, range * 2.0D);
        return level.getEntitiesOfClass(ServerPlayer.class, area, Player::isAlive)
                .stream()
                .min(Comparator.comparingDouble(player -> player.distanceToSqr(origin)));
    }
}

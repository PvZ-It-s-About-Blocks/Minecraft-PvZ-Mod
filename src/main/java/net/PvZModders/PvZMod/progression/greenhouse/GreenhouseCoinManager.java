package net.PvZModders.PvZMod.progression.greenhouse;

import net.PvZModders.PvZMod.PvZ2Mod;
import net.PvZModders.PvZMod.item.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Comparator;
import java.util.Optional;

@Mod.EventBusSubscriber(modid = PvZ2Mod.MOD_ID)
public final class GreenhouseCoinManager {
    public static final String PLAYER_COINS_TAG = "PvZCoins";
    public static final String COIN_DROP_TAG = "PvZCoinDrop";
    public static final String COIN_VALUE_TAG = "PvZCoinValue";
    private static final int COIN_STACK_SIZE = 9999;

    private GreenhouseCoinManager() {
    }

    public static int getCoins(Player player) {
        return countCoins(player);
    }

    public static int countCoins(Player player) {
        if (player instanceof ServerPlayer) {
            normalizeCoinStacks(player);
        }
        int total = 0;
        Inventory inventory = player.getInventory();
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            ItemStack stack = inventory.getItem(slot);
            if (isCoinItem(stack)) {
                total += stack.getCount();
            }
        }
        return total;
    }

    public static boolean hasCoins(Player player, int amount) {
        return amount <= 0 || countCoins(player) >= amount;
    }

    public static boolean removeCoins(Player player, int amount) {
        if (amount <= 0) {
            return true;
        }
        if (!hasCoins(player, amount)) {
            return false;
        }

        int remaining = amount;
        Inventory inventory = player.getInventory();
        for (int slot = 0; slot < inventory.getContainerSize() && remaining > 0; slot++) {
            ItemStack stack = inventory.getItem(slot);
            if (!isCoinItem(stack)) {
                continue;
            }
            int removed = Math.min(remaining, stack.getCount());
            stack.shrink(removed);
            remaining -= removed;
        }
        inventory.setChanged();
        normalizeCoinStacks(player);
        return remaining <= 0;
    }

    public static boolean isCoinItem(ItemStack stack) {
        return !stack.isEmpty() && stack.is(ModItems.COIN.get());
    }

    public static void addCoins(Player player, int amount) {
        giveCoins(player, amount);
    }

    public static void giveCoins(Player player, int amount) {
        if (amount <= 0) {
            return;
        }

        int remaining = addCoinsToInventory(player, amount);
        while (remaining > 0) {
            int count = Math.min(remaining, COIN_STACK_SIZE);
            player.drop(new ItemStack(ModItems.COIN.get(), count), false);
            remaining -= count;
        }
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.displayClientMessage(Component.literal("Coins: " + countCoins(player)).withStyle(ChatFormatting.GOLD), true);
        }
    }

    public static int addCoinsToInventory(Player player, int amount) {
        if (amount <= 0) {
            return 0;
        }
        Inventory inventory = player.getInventory();
        int remaining = amount;
        for (int slot = 0; slot < inventory.getContainerSize() && remaining > 0; slot++) {
            ItemStack stack = inventory.getItem(slot);
            if (!isCoinItem(stack) || stack.getCount() >= COIN_STACK_SIZE) {
                continue;
            }
            int moved = Math.min(remaining, COIN_STACK_SIZE - stack.getCount());
            stack.setCount(stack.getCount() + moved);
            remaining -= moved;
        }
        for (int slot = 0; slot < inventory.getContainerSize() && remaining > 0; slot++) {
            ItemStack stack = inventory.getItem(slot);
            if (!stack.isEmpty()) {
                continue;
            }
            int moved = Math.min(remaining, COIN_STACK_SIZE);
            inventory.setItem(slot, new ItemStack(ModItems.COIN.get(), moved));
            remaining -= moved;
        }
        inventory.setChanged();
        return remaining;
    }

    public static void normalizeCoinStacks(Player player) {
        Inventory inventory = player.getInventory();
        int total = 0;
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            ItemStack stack = inventory.getItem(slot);
            if (!isCoinItem(stack)) {
                continue;
            }
            total += stack.getCount();
            inventory.setItem(slot, ItemStack.EMPTY);
        }
        int remaining = total;
        for (int slot = 0; slot < inventory.getContainerSize() && remaining > 0; slot++) {
            if (!inventory.getItem(slot).isEmpty()) {
                continue;
            }
            int moved = Math.min(remaining, COIN_STACK_SIZE);
            inventory.setItem(slot, new ItemStack(ModItems.COIN.get(), moved));
            remaining -= moved;
        }
        if (remaining > 0) {
            Vec3 pos = player.position();
            dropCoins(player.level(), pos, remaining);
        }
        inventory.setChanged();
    }

    public static void addCoinsToNearestPlayer(ServerLevel level, Vec3 origin, int amount) {
        nearestPlayer(level, origin, 64.0D).ifPresentOrElse(
                player -> giveCoins(player, amount),
                () -> dropCoins(level, origin, amount)
        );
    }

    public static void dropCoin(ServerLevel level, Vec3 origin, int amount) {
        dropCoins(level, origin, amount);
    }

    public static void dropCoins(Level level, BlockPos pos, int amount) {
        dropCoins(level, Vec3.atCenterOf(pos), amount);
    }

    public static void dropCoins(Level level, Vec3 origin, int amount) {
        if (amount <= 0) {
            return;
        }
        int remaining = amount;
        while (remaining > 0) {
            int count = Math.min(remaining, COIN_STACK_SIZE);
            ItemStack stack = new ItemStack(ModItems.COIN.get(), count);
            ItemEntity item = new ItemEntity(level, origin.x, origin.y, origin.z, stack);
            item.setDefaultPickUpDelay();
            level.addFreshEntity(item);
            remaining -= count;
        }
    }

    public static boolean isCoinDrop(ItemEntity item) {
        return isCoinItem(item.getItem())
                || item.getPersistentData().getBoolean(COIN_DROP_TAG)
                || (item.getItem().hasTag() && item.getItem().getOrCreateTag().getBoolean(COIN_DROP_TAG));
    }

    public static int coinValue(ItemEntity item) {
        int entityValue = item.getPersistentData().getInt(COIN_VALUE_TAG);
        int stackValue = item.getItem().hasTag() ? item.getItem().getOrCreateTag().getInt(COIN_VALUE_TAG) : 0;
        int legacyValue = Math.max(entityValue, stackValue);
        return legacyValue > 0 ? legacyValue : item.getItem().getCount();
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
            giveCoins(collector.get(), value);
            collected += value;
        }
        return collected;
    }

    @SubscribeEvent
    public static void onCoinPickup(EntityItemPickupEvent event) {
        ItemEntity item = event.getItem();
        if (!isCoinDrop(item)) {
            return;
        }
        Player player = event.getEntity();
        int value = coinValue(item);
        if (value <= 0) {
            return;
        }
        int leftover = addCoinsToInventory(player, value);
        if (leftover <= 0) {
            item.discard();
        } else {
            item.setItem(new ItemStack(ModItems.COIN.get(), leftover));
        }
        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END
                || event.player.level().isClientSide
                || event.player.tickCount % 20 != 0) {
            return;
        }
        normalizeCoinStacks(event.player);
    }

    private static Optional<ServerPlayer> nearestPlayer(ServerLevel level, Vec3 origin, double range) {
        AABB area = AABB.ofSize(origin, range * 2.0D, range * 2.0D, range * 2.0D);
        return level.getEntitiesOfClass(ServerPlayer.class, area, Player::isAlive)
                .stream()
                .min(Comparator.comparingDouble(player -> player.distanceToSqr(origin)));
    }
}

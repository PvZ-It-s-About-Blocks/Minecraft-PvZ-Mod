package net.PvZModders.PvZMod.progression.coins;

import net.PvZModders.PvZMod.entity.custom.PvZZombieEntity;
import net.PvZModders.PvZMod.progression.greenhouse.GreenhouseCoinManager;
import net.PvZModders.PvZMod.progression.zombies.PvZZombieDefinition;
import net.PvZModders.PvZMod.progression.zombies.PvZZombieDefinitions;
import net.PvZModders.PvZMod.progression.zombies.PvZZombieSpecial;
import net.minecraft.server.level.ServerLevel;

public final class ZombieCoinDropManager {
    private ZombieCoinDropManager() {
    }

    public static int rollZombieCoinDrop(PvZZombieEntity zombie) {
        if (!(zombie.level() instanceof ServerLevel level)) {
            return 0;
        }

        PvZZombieDefinition definition = zombie.definition();
        if ("yeti_zombie".equals(definition.id()) || definition.has(PvZZombieSpecial.YETI_FLEE)) {
            return randomRange(level, CoinEconomyValues.YETI_MIN_COINS, CoinEconomyValues.YETI_MAX_COINS);
        }
        if (PvZZombieDefinitions.isGargantuarLike(zombie)) {
            return level.random.nextDouble() <= CoinEconomyValues.GARGANTUAR_COIN_DROP_CHANCE
                    ? randomRange(level, 10, 20)
                    : 0;
        }
        if (isStrongSpecial(definition)) {
            return level.random.nextDouble() <= CoinEconomyValues.SPECIAL_ZOMBIE_COIN_DROP_CHANCE
                    ? randomRange(level, 2, 4)
                    : 0;
        }
        if (isArmored(definition)) {
            return level.random.nextDouble() <= CoinEconomyValues.ARMORED_ZOMBIE_COIN_DROP_CHANCE
                    ? randomRange(level, 1, 2)
                    : 0;
        }
        return level.random.nextDouble() <= CoinEconomyValues.COMMON_ZOMBIE_COIN_DROP_CHANCE ? 1 : 0;
    }

    public static void dropZombieCoins(PvZZombieEntity zombie) {
        if (!(zombie.level() instanceof ServerLevel level)) {
            return;
        }

        int coins = rollZombieCoinDrop(zombie);
        if (coins <= 0) {
            return;
        }
        GreenhouseCoinManager.dropCoins(level, zombie.position(), coins);
    }

    private static boolean isArmored(PvZZombieDefinition definition) {
        return definition.has(PvZZombieSpecial.METAL)
                || definition.maxHealth() >= 45.0D
                || definition.id().contains("conehead")
                || definition.id().contains("buckethead")
                || definition.id().contains("brickhead");
    }

    private static boolean isStrongSpecial(PvZZombieDefinition definition) {
        return definition.specials().stream().anyMatch(special -> special != PvZZombieSpecial.FLAG
                && special != PvZZombieSpecial.METAL
                && special != PvZZombieSpecial.IMP)
                || definition.maxHealth() >= 50.0D;
    }

    private static int randomRange(ServerLevel level, int min, int max) {
        return min + level.random.nextInt(Math.max(1, max - min + 1));
    }
}

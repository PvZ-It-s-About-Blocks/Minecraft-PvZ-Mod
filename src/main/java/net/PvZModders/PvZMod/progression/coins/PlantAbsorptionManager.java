package net.PvZModders.PvZMod.progression.coins;

import net.PvZModders.PvZMod.block.entity.GardenTotemBlockEntity;
import net.PvZModders.PvZMod.progression.greenhouse.GreenhouseCoinManager;
import net.PvZModders.PvZMod.progression.seed.PlantEntityManager;
import net.PvZModders.PvZMod.progression.seed.PlantSeedDefinition;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public final class PlantAbsorptionManager {
    private PlantAbsorptionManager() {
    }

    public static int absorbPlantsAfterWaveWin(ServerLevel level, GardenTotemBlockEntity totem) {
        BlockPos center = totem.getBlockPos();
        List<SnowGolem> plants = collectAbsorbablePlantsForGarden(level, center);
        if (plants.isEmpty()) {
            return 0;
        }

        int totalCoins = 0;
        for (SnowGolem plant : plants) {
            totalCoins += calculatePlantAbsorbCoins(plant);
            playPlantAbsorbEffects(level, plant);
            removePlantAfterAbsorption(plant);
        }

        if (totalCoins > 0) {
            GreenhouseCoinManager.dropCoins(level, center.above(), totalCoins);
            level.playSound(null, center, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.8F, 1.1F);
            messageNearbyPlayers(level, center, totalCoins);
        }
        return totalCoins;
    }

    public static int absorbPlantsBeforeWaveStart(ServerLevel level, GardenTotemBlockEntity totem) {
        BlockPos center = totem.getBlockPos();
        List<SnowGolem> plants = collectAbsorbablePlantsForGarden(level, center);
        if (plants.isEmpty()) {
            return 0;
        }

        for (SnowGolem plant : plants) {
            playPlantAbsorbEffects(level, plant);
            removePlantAfterAbsorption(plant);
        }
        level.playSound(null, center, SoundEvents.GRASS_BREAK, SoundSource.PLAYERS, 0.7F, 1.1F);
        return plants.size();
    }

    public static List<SnowGolem> collectAbsorbablePlantsForGarden(ServerLevel level, BlockPos center) {
        double radius = CoinEconomyValues.PLANT_ABSORB_RADIUS;
        AABB area = AABB.ofSize(Vec3.atCenterOf(center), radius * 2.0D, 8.0D, radius * 2.0D);
        return level.getEntitiesOfClass(SnowGolem.class, area, PlantAbsorptionManager::isAbsorbableCombatPlant);
    }

    public static boolean isAbsorbableCombatPlant(SnowGolem plant) {
        return plant.isAlive() && PlantEntityManager.isPlant(plant);
    }

    public static int calculatePlantAbsorbCoins(SnowGolem plant) {
        String plantId = PlantEntityManager.plantId(plant);
        int sunCost = PlantSeedDefinition.getByPlantId(plantId)
                .map(PlantSeedDefinition::sunCost)
                .orElse(0);
        return Math.max(CoinEconomyValues.MIN_PLANT_ABSORB_COINS,
                (int) Math.floor(sunCost * CoinEconomyValues.PLANT_ABSORB_COIN_REFUND_MULTIPLIER));
    }

    public static void removePlantAfterAbsorption(SnowGolem plant) {
        plant.discard();
    }

    public static void playPlantAbsorbEffects(ServerLevel level, SnowGolem plant) {
        level.sendParticles(ParticleTypes.HAPPY_VILLAGER, plant.getX(), plant.getY() + 0.8D, plant.getZ(), 8, 0.25D, 0.25D, 0.25D, 0.02D);
    }

    private static void messageNearbyPlayers(ServerLevel level, BlockPos center, int coins) {
        Vec3 origin = Vec3.atCenterOf(center);
        Component message = Component.literal("Your plants were absorbed and dropped coins!").withStyle(ChatFormatting.GOLD);
        for (ServerPlayer player : level.players()) {
            if (player.distanceToSqr(origin) <= 4096.0D) {
                player.displayClientMessage(message, true);
            }
        }
    }
}

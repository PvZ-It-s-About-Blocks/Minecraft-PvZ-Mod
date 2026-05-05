package net.PvZModders.PvZMod.progression.dinosaur;

import net.PvZModders.PvZMod.PvZ2Mod;
import net.PvZModders.PvZMod.entity.custom.JurassicDinosaurEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = PvZ2Mod.MOD_ID)
public final class JurassicDinosaurSpawner {
    private static final String NEXT_PASSIVE_DINO_TICK_TAG = "PvZNextPassiveDinosaurTick";
    private static final int PASSIVE_SPAWN_RADIUS_MIN = 20;
    private static final int PASSIVE_SPAWN_RADIUS_RANDOM = 18;
    private static final int PASSIVE_DINO_CAP = 3;
    private static final int MIN_PASSIVE_DELAY_TICKS = 20 * 45;
    private static final int RANDOM_PASSIVE_DELAY_TICKS = 20 * 45;

    private JurassicDinosaurSpawner() {
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        for (ServerPlayer player : event.getServer().getPlayerList().getPlayers()) {
            if (!(player.level() instanceof ServerLevel level) || !isJurassicBiome(level, player.blockPosition())) {
                continue;
            }

            long gameTime = level.getGameTime();
            long nextTick = player.getPersistentData().getLong(NEXT_PASSIVE_DINO_TICK_TAG);
            if (nextTick <= 0L) {
                scheduleNext(player, gameTime);
                continue;
            }
            if (gameTime < nextTick) {
                continue;
            }

            scheduleNext(player, gameTime);
            if (nearbyDinosaurCount(level, player.blockPosition()) >= PASSIVE_DINO_CAP) {
                continue;
            }
            spawnPassiveDinosaur(level, player);
        }
    }

    public static boolean isJurassicBiome(ServerLevel level, BlockPos pos) {
        return level.getBiome(pos).is(Biomes.BADLANDS)
                || level.getBiome(pos).is(Biomes.ERODED_BADLANDS)
                || level.getBiome(pos).is(Biomes.WOODED_BADLANDS);
    }

    private static void scheduleNext(ServerPlayer player, long gameTime) {
        player.getPersistentData().putLong(NEXT_PASSIVE_DINO_TICK_TAG,
                gameTime + MIN_PASSIVE_DELAY_TICKS + player.getRandom().nextInt(RANDOM_PASSIVE_DELAY_TICKS + 1));
    }

    private static int nearbyDinosaurCount(ServerLevel level, BlockPos center) {
        AABB area = new AABB(center).inflate(64.0D, 24.0D, 64.0D);
        return level.getEntitiesOfClass(JurassicDinosaurEntity.class, area, JurassicDinosaurEntity::isAlive).size();
    }

    private static void spawnPassiveDinosaur(ServerLevel level, ServerPlayer player) {
        int distance = PASSIVE_SPAWN_RADIUS_MIN + level.random.nextInt(PASSIVE_SPAWN_RADIUS_RANDOM + 1);
        double angle = level.random.nextDouble() * Math.PI * 2.0D;
        BlockPos raw = player.blockPosition().offset((int) Math.round(Math.cos(angle) * distance), 0, (int) Math.round(Math.sin(angle) * distance));
        BlockPos spawnPos = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, raw);
        if (!isJurassicBiome(level, spawnPos) || !level.getBlockState(spawnPos).isAir() || !level.getBlockState(spawnPos.above()).isAir()) {
            return;
        }

        JurassicDinosaurEntity dinosaur = new JurassicDinosaurEntity(level, spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D, DinosaurType.passive(level.random));
        dinosaur.finalizeSpawn(level, level.getCurrentDifficultyAt(spawnPos), MobSpawnType.NATURAL, null, null);
        level.addFreshEntity(dinosaur);
    }
}

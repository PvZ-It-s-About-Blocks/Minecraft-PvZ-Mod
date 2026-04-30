package net.PvZModders.PvZMod.world;

import net.PvZModders.PvZMod.PvZ2Mod;
import net.PvZModders.PvZMod.entity.ModEntities;
import net.PvZModders.PvZMod.entity.custom.PennyVanEntity;
import net.PvZModders.PvZMod.item.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Vector3f;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = PvZ2Mod.MOD_ID)
public final class PennyVanSpawner {
    private static final int PENNY_VAN_SPAWN_DELAY_TICKS = 20 * 10;
    private static final int PENNY_VAN_SPAWN_HEIGHT = 20;
    private static final double PENNY_VAN_DESCENT_SPEED = 0.35D;

    private static UUID firstPlayerId;
    private static boolean pennyVanSpawned;
    private static PennyVanEntity activePennyVan;
    private static BlockPos activePennyVanLandingPos;
    private static final Set<UUID> REWARDED_PLAYERS = new HashSet<>();

    private PennyVanSpawner() {
    }

    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        resetSpawnerState();
    }

    @SubscribeEvent
    public static void onPlayerJoin(EntityJoinLevelEvent event) {
        if (firstPlayerId != null || event.getLevel().isClientSide()) {
            return;
        }
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            firstPlayerId = serverPlayer.getUUID();
        }
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        ServerLevel overworld = event.getServer().overworld();

        if (!pennyVanSpawned && firstPlayerId != null && overworld.getGameTime() >= PENNY_VAN_SPAWN_DELAY_TICKS) {
            ServerPlayer targetPlayer = event.getServer().getPlayerList().getPlayer(firstPlayerId);
            if (targetPlayer != null && targetPlayer.level() == overworld) {
                spawnPennyVan(overworld, targetPlayer);
                pennyVanSpawned = true;
            }
        }

        if (activePennyVan != null && activePennyVan.isAlive() && activePennyVan.level() == overworld) {
            movePennyVanTowardGround();
            spawnVanMarkerParticles(overworld, activePennyVan.position());
        }
    }


    @SubscribeEvent
    public static void onVanRightClick(PlayerInteractEvent.EntityInteract event) {
        if (event.getLevel().isClientSide()) {
            return;
        }
        if (!(event.getTarget() instanceof PennyVanEntity pennyVan) || activePennyVan == null || pennyVan.getId() != activePennyVan.getId()) {
            return;
        }

        UUID playerId = event.getEntity().getUUID();
        if (REWARDED_PLAYERS.contains(playerId)) {
            return;
        }

        ItemStack reward = new ItemStack(ModItems.GARDEN_PLOTTER.get());
        event.getEntity().addItem(reward);
        REWARDED_PLAYERS.add(playerId);

        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            ResourceLocation recipeId = new ResourceLocation(PvZ2Mod.MOD_ID, "garden_plotter");
            event.getEntity().level().getServer().getRecipeManager().byKey(recipeId)
                    .ifPresent(recipe -> serverPlayer.awardRecipes(java.util.List.of(recipe)));
        }

        event.setCancellationResult(InteractionResult.SUCCESS);
        event.setCanceled(true);
    }

    private static void spawnPennyVan(ServerLevel level, ServerPlayer player) {
        BlockPos landingPos = findGroundSpawnPos(level, player);
        BlockPos spawnPos = landingPos.above(PENNY_VAN_SPAWN_HEIGHT);

        PennyVanEntity pennyVan = ModEntities.PENNY_VAN.get().create(level);
        if (pennyVan == null) {
            return;
        }

        pennyVan.setPos(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D);
        pennyVan.setCustomNameVisible(true);
        pennyVan.setCustomName(net.minecraft.network.chat.Component.literal("Penny Van"));
        pennyVan.setPersistenceRequired();
        pennyVan.setInvulnerable(true);
        pennyVan.setNoAi(true);
        pennyVan.setNoGravity(true);
        pennyVan.setDeltaMovement(Vec3.ZERO);
        level.addFreshEntity(pennyVan);
        player.sendSystemMessage(Component.literal("penny spawned"));

        activePennyVan = pennyVan;
        activePennyVanLandingPos = landingPos;
    }

    private static void movePennyVanTowardGround() {
        if (activePennyVanLandingPos == null) {
            return;
        }

        double landingY = activePennyVanLandingPos.getY();
        Vec3 position = activePennyVan.position();
        if (position.y <= landingY) {
            activePennyVan.setPos(position.x, landingY, position.z);
            activePennyVan.setDeltaMovement(Vec3.ZERO);
            activePennyVan.setNoGravity(false);
            activePennyVanLandingPos = null;
            return;
        }

        activePennyVan.setDeltaMovement(0.0D, -PENNY_VAN_DESCENT_SPEED, 0.0D);
        activePennyVan.setPos(position.x, Math.max(landingY, position.y - PENNY_VAN_DESCENT_SPEED), position.z);
    }

    private static BlockPos findGroundSpawnPos(ServerLevel level, ServerPlayer player) {
        BlockPos playerPos = player.blockPosition();
        Direction direction = player.getDirection();
        BlockPos preferredPos = playerPos.relative(direction, 6);

        BlockPos groundPos = findStandablePos(level, preferredPos, playerPos.getY());
        if (groundPos != null) {
            return groundPos;
        }

        for (int radius = 3; radius <= 8; radius++) {
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (Math.abs(dx) != radius && Math.abs(dz) != radius) {
                        continue;
                    }

                    groundPos = findStandablePos(level, playerPos.offset(dx, 0, dz), playerPos.getY());
                    if (groundPos != null) {
                        return groundPos;
                    }
                }
            }
        }

        return playerPos.above();
    }

    private static BlockPos findStandablePos(ServerLevel level, BlockPos xzPos, int centerY) {
        int minY = Math.max(level.getMinBuildHeight() + 1, centerY - 8);
        int maxY = Math.min(level.getMaxBuildHeight() - 2, centerY + 8);

        for (int y = maxY; y >= minY; y--) {
            BlockPos floorPos = new BlockPos(xzPos.getX(), y - 1, xzPos.getZ());
            BlockPos feetPos = floorPos.above();
            BlockPos headPos = feetPos.above();
            BlockState floorState = level.getBlockState(floorPos);

            if (floorState.isFaceSturdy(level, floorPos, Direction.UP)
                    && level.getBlockState(feetPos).isAir()
                    && level.getBlockState(headPos).isAir()) {
                return feetPos;
            }
        }

        return null;
    }

    private static void spawnVanMarkerParticles(ServerLevel level, Vec3 vanPos) {
        double x = vanPos.x;
        double y = vanPos.y + 1.2D;
        double z = vanPos.z;

        DustParticleOptions greenDust = new DustParticleOptions(new Vector3f(0.2F, 1.0F, 0.2F), 1.5F);
        for (int i = 0; i < 10; i++) {
            level.sendParticles(greenDust, x, y + i, z, 1, 0.0D, 0.02D, 0.0D, 0.0D);
        }
    }

    private static void resetSpawnerState() {
        firstPlayerId = null;
        pennyVanSpawned = false;
        activePennyVan = null;
        activePennyVanLandingPos = null;
        REWARDED_PLAYERS.clear();
    }
}

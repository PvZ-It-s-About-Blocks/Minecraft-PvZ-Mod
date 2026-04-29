package net.PvZModders.PvZMod.world;

import net.PvZModders.PvZMod.PvZ2Mod;
import net.PvZModders.PvZMod.item.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.sniffer.Sniffer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Vector3f;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Spawns the first Penny Van event using a Sniffer placeholder.
 * Replace EntityType.SNIFFER with a custom Penny Van entity when the model/renderer is ready.
 */
@Mod.EventBusSubscriber(modid = PvZ2Mod.MOD_ID)
public final class PennyVanSpawner {
    private static final int PENNY_VAN_SPAWN_DELAY_TICKS = 20 * 120;
    private static final int PENNY_VAN_SPAWN_HEIGHT = 20;

    private static UUID firstPlayerId;
    private static boolean pennyVanSpawned;
    private static Sniffer activePennyVan;
    private static final Set<UUID> REWARDED_PLAYERS = new HashSet<>();

    private PennyVanSpawner() {
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
            spawnVanMarkerParticles(overworld, activePennyVan.position());
        }
    }


    @SubscribeEvent
    public static void onVanRightClick(PlayerInteractEvent.EntityInteract event) {
        if (event.getLevel().isClientSide()) {
            return;
        }
        if (!(event.getTarget() instanceof Sniffer sniffer) || activePennyVan == null || sniffer.getId() != activePennyVan.getId()) {
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
        BlockPos landingPos = player.blockPosition().relative(player.getDirection(), 6);
        BlockPos spawnPos = landingPos.above(PENNY_VAN_SPAWN_HEIGHT);

        Sniffer pennyVan = EntityType.SNIFFER.create(level);
        if (pennyVan == null) {
            return;
        }

        pennyVan.setPos(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D);
        pennyVan.setCustomNameVisible(true);
        pennyVan.setCustomName(net.minecraft.network.chat.Component.literal("Penny Van (Placeholder)"));
        pennyVan.setPersistenceRequired();
        pennyVan.setInvulnerable(true);
        pennyVan.setNoAi(true);
        pennyVan.setDeltaMovement(0.0D, -2.0D, 0.0D);
        level.addFreshEntity(pennyVan);

        activePennyVan = pennyVan;
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
}

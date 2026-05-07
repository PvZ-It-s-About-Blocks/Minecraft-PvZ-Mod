package net.PvZModders.PvZMod.progression.modernday;

import net.PvZModders.PvZMod.PvZ2Mod;
import net.PvZModders.PvZMod.progression.GardenId;
import net.PvZModders.PvZMod.progression.GardenPortalSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Optional;

@Mod.EventBusSubscriber(modid = PvZ2Mod.MOD_ID)
public final class ModernDayEndSuppressionManager {
    private static final int CHECK_INTERVAL_TICKS = 40;
    private static final double SUPPRESSION_RADIUS = 96.0D;

    private ModernDayEndSuppressionManager() {
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.getServer().getTickCount() % CHECK_INTERVAL_TICKS != 0) {
            return;
        }

        ServerLevel end = event.getServer().getLevel(Level.END);
        if (end == null) {
            return;
        }
        suppressForbiddenEndEntities(end);
    }

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide() || event.getLevel().dimension() != Level.END || !isForbiddenEndEntity(event.getEntity())) {
            return;
        }

        event.setCanceled(true);
        event.getEntity().discard();
    }

    public static boolean isInModernDayGardenArea(ServerLevel level, BlockPos pos) {
        Optional<GlobalPos> portal = GardenPortalSavedData.get(level).getPortal(GardenId.MODERN_DAY);
        return portal.isPresent()
                && portal.get().dimension() == level.dimension()
                && portal.get().pos().distSqr(pos) <= SUPPRESSION_RADIUS * SUPPRESSION_RADIUS;
    }

    public static void suppressEndMobsForModernDay(ServerLevel level, BlockPos center) {
        AABB area = new AABB(center).inflate(SUPPRESSION_RADIUS, 48.0D, SUPPRESSION_RADIUS);
        for (EnderDragon dragon : level.getEntitiesOfClass(EnderDragon.class, area, EnderDragon::isAlive)) {
            dragon.discard();
        }
        for (EnderMan enderman : level.getEntitiesOfClass(EnderMan.class, area, EnderMan::isAlive)) {
            enderman.discard();
        }
        for (EndCrystal crystal : level.getEntitiesOfClass(EndCrystal.class, area, EndCrystal::isAlive)) {
            crystal.discard();
        }
    }

    private static void suppressForbiddenEndEntities(ServerLevel level) {
        for (Entity entity : level.getAllEntities()) {
            if (isForbiddenEndEntity(entity)) {
                entity.discard();
            }
        }
    }

    private static boolean isForbiddenEndEntity(Entity entity) {
        return entity instanceof EnderMan
                || entity instanceof EnderDragon
                || entity instanceof EndCrystal;
    }
}

package net.PvZModders.PvZMod.progression.waves;

import net.PvZModders.PvZMod.PvZ2Mod;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashSet;
import java.util.Set;

@Mod.EventBusSubscriber(modid = PvZ2Mod.MOD_ID)
public final class WildWestRailProtection {
    private static final Set<GlobalPos> PROTECTED_RAILS = new HashSet<>();

    private WildWestRailProtection() {
    }

    public static void protect(ServerLevel level, BlockPos pos) {
        PROTECTED_RAILS.add(GlobalPos.of(level.dimension(), pos.immutable()));
    }

    public static void unprotect(ServerLevel level, BlockPos pos) {
        PROTECTED_RAILS.remove(GlobalPos.of(level.dimension(), pos.immutable()));
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (event.getLevel() instanceof ServerLevel level && isProtected(level, event.getPos())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onExplosionDetonate(ExplosionEvent.Detonate event) {
        if (event.getLevel() instanceof ServerLevel level) {
            event.getAffectedBlocks().removeIf(pos -> isProtected(level, pos));
        }
    }

    private static boolean isProtected(ServerLevel level, BlockPos pos) {
        return PROTECTED_RAILS.contains(GlobalPos.of(level.dimension(), pos.immutable()));
    }
}

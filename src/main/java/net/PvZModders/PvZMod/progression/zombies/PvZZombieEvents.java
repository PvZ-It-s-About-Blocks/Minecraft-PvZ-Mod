package net.PvZModders.PvZMod.progression.zombies;

import net.PvZModders.PvZMod.PvZ2Mod;
import net.PvZModders.PvZMod.entity.custom.PvZZombieEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = PvZ2Mod.MOD_ID)
public final class PvZZombieEvents {
    private PvZZombieEvents() {
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof PvZZombieEntity zombie)
                || !zombie.definition().has(PvZZombieSpecial.SCREEN_DOOR_SHIELD)) {
            return;
        }

        Entity damageSource = event.getSource().getDirectEntity();
        if (damageSource == null || !isDamageFromFront(zombie, damageSource.position())) {
            return;
        }

        event.setAmount(event.getAmount() * 0.5F);
        if (zombie.level() instanceof ServerLevel level) {
            level.sendParticles(ParticleTypes.CRIT, zombie.getX(), zombie.getY() + 1.0D, zombie.getZ(), 5, 0.25D, 0.25D, 0.25D, 0.02D);
        }
    }

    private static boolean isDamageFromFront(PvZZombieEntity zombie, Vec3 sourcePosition) {
        Vec3 facing = zombie.getLookAngle().multiply(1.0D, 0.0D, 1.0D);
        Vec3 incoming = sourcePosition.subtract(zombie.position()).multiply(1.0D, 0.0D, 1.0D);
        if (facing.lengthSqr() < 1.0E-4D || incoming.lengthSqr() < 1.0E-4D) {
            return false;
        }
        return facing.normalize().dot(incoming.normalize()) > 0.35D;
    }
}

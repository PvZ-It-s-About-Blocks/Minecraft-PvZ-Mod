package net.PvZModders.PvZMod.world;

import net.PvZModders.PvZMod.PvZ2Mod;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = PvZ2Mod.MOD_ID)
public final class ZombieWaveDevEvents {
    private ZombieWaveDevEvents() {
    }

    @SubscribeEvent
    public static void stopZombieSunBurn(LivingEvent.LivingTickEvent event) {
        if (event.getEntity() instanceof Zombie zombie && zombie.isOnFire()) {
            zombie.clearFire();
        }
    }
}

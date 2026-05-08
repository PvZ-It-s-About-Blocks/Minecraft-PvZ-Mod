package net.PvZModders.PvZMod.client;

import net.PvZModders.PvZMod.PvZ2Mod;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderNameTagEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = PvZ2Mod.MOD_ID, value = Dist.CLIENT)
public final class MobHealthNameplateRenderer {
    private MobHealthNameplateRenderer() {
    }

    @SubscribeEvent
    public static void renderMobHealth(RenderNameTagEvent event) {
        if (!(event.getEntity() instanceof Mob mob) || !mob.isAlive()) {
            return;
        }
        if (mob.getCustomName() != null) {
            event.setContent(mob.getCustomName().copy());
            event.setResult(Event.Result.ALLOW);
        }
    }
}

package net.PvZModders.PvZMod.client;

import net.PvZModders.PvZMod.PvZ2Mod;
import net.PvZModders.PvZMod.entity.ModEntities;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EntityType;
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
        int currentHealth = Math.max(0, (int) Math.ceil(mob.getHealth()));
        int maxHealth = Math.max(1, (int) Math.ceil(mob.getMaxHealth()));
        if (isPvZPlantEntityType(mob.getType())) {
            MutableComponent plantName = mob.getCustomName() == null
                    ? Component.literal("Plant")
                    : mob.getCustomName().copy();
            event.setContent(plantName.append(Component.literal(" HP " + currentHealth + "/" + maxHealth).withStyle(ChatFormatting.WHITE)));
            event.setResult(Event.Result.ALLOW);
            return;
        }

        event.setContent(Component.literal("HP " + currentHealth + "/" + maxHealth));
        event.setResult(Event.Result.ALLOW);
    }

    private static boolean isPvZPlantEntityType(EntityType<?> entityType) {
        for (var plantType : ModEntities.plantEntityTypes()) {
            if (plantType.isPresent() && plantType.get() == entityType) {
                return true;
            }
        }
        return false;
    }
}

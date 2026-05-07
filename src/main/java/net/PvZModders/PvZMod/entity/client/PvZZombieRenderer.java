package net.PvZModders.PvZMod.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.PvZModders.PvZMod.entity.custom.PvZZombieEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ZombieRenderer;
import net.minecraft.world.entity.monster.Zombie;

public class PvZZombieRenderer extends ZombieRenderer {
    public PvZZombieRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void scale(Zombie zombie, PoseStack poseStack, float partialTickTime) {
        super.scale(zombie, poseStack, partialTickTime);
        if (zombie instanceof PvZZombieEntity pvzZombie) {
            float scale = pvzZombie.definition().visualScale();
            poseStack.scale(scale, scale, scale);
        }
    }
}
